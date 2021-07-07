package uz.pdp.online.m6l6t2appbankomatcascadetypes.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.Bankomat;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.Card;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.OutHistory;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.User;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.enums.MoneyName;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.payload.ApiResponse;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.payload.TransferDto;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.repository.BankomatRepository;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.repository.CardRepository;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.repository.OutHistoryRepository;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.repository.UserRepository;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.service.innerService.MoneyTransferService;

import java.util.HashMap;
import java.util.Optional;

@Service
public class TransferOutcomeDollarService {
    @Autowired
    CardRepository cardRepository;
    @Autowired
    BankomatRepository bankomatRepository;
    @Autowired
    BankomatService bankomatService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    OutHistoryRepository outHistoryRepository;
    @Autowired
    MoneyTransferService moneyTransferService;
    @Autowired
    UserType userType;
    public ApiResponse calculateDollar(TransferDto transferDto){

//1  ==> check card
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Card currentCard = (Card) authentication.getPrincipal();


        Optional<Card> optionalCard = cardRepository.findByCardNumber(currentCard.getCardNumber());
        if (!optionalCard.isPresent())
            return new ApiResponse("card not found by card number", false);
        Card card = optionalCard.get();

        if(card.getCvvCode()==null)
                    return new ApiResponse("visa card cvv code not be null",false);

        String cvvCode = card.getCvvCode().toString();
        if (cvvCode.length()!=4)
            return new ApiResponse("cvv code must be four digit",false);


        if (!card.getAccountNonLocked())
            return new ApiResponse("your card is  blocked , you should unblock first", false);


//2     ==> check bankomat and card  (type)

        Bankomat bankomat = bankomatRepository.getById(transferDto.getBankomatId());
        if (!bankomat.getCardType().getCardTypeName().name().equals(card.getCardType().getCardTypeName().name())) {
            return new ApiResponse("bankomat type and card type not the same", false);
        }

//3
        User bankomatEmployee = bankomat.getUser();
        String bankomatUserEmail = bankomatEmployee.getEmail();
        if (bankomat.getTotalAmountMoney() < 50) {
            bankomatService.sendEmail(bankomatUserEmail, bankomat.getId());
            return new ApiResponse("Bankomatdagi dollar miqdori pul kam qolgan", false);
        }

//4

        Integer requestCardMoney = transferDto.getMoneyAmount();

//5
        boolean cardBankEqualsBankomatBank =false;
        String bankomatBankName = bankomat.getBank().getBankName().name();
        String cardBankName = card.getBank().getBankName().name();
        if (bankomatBankName.equals(cardBankName))
            cardBankEqualsBankomatBank = true;

        double percent;
        double totalSumma;

        if (cardBankEqualsBankomatBank) {
            percent = 0.05f;
        } else {
            percent = 0.1f;
        }
        totalSumma = requestCardMoney + requestCardMoney * percent;


//6
        if (card.getAmountMoney() < totalSumma)
            return new ApiResponse("kartada pul foiz bn yechish uchun  yetarli emas", false);

        HashMap<String, Integer> bankNotesQuantity = moneyTransferService.getBankNotesQuantityDollar(bankomat);

        ApiResponse exchange = exchangesDollar(bankNotesQuantity, requestCardMoney,bankomat);

        if(exchange.getIsSuccess()){
            bankomat.setTotalAmountMoney(bankomat.getTotalAmountMoney()-requestCardMoney);
            bankomatRepository.save(bankomat);
            card.setAmountMoney(card.getAmountMoney()-totalSumma);
            cardRepository.save(card);
            OutHistory outHistory=new OutHistory();
            outHistory.setBankomat(bankomat);
            outHistory.setCard(card);
            outHistory.setMoneyAmount(requestCardMoney);
            outHistoryRepository.save(outHistory);
        }

        return exchange;

    }

    public ApiResponse exchangesDollar(HashMap<String,Integer> baza, Integer sum, Bankomat bankomat){
        if (sum<baza.get("min")||sum>baza.get("max"))
            return new ApiResponse("error min value or max value",false);
//        if (sum%1000!=0)
//            return new ApiResponse("tanga yo'q",false);

        if (baza.get("all")<sum)
            return new ApiResponse("bankomatda siz kiritgan summadan kam miqdorda summa bor",false);
        int lastNumberOfThousand = Math.toIntExact(sum % 10);//oxirgi mingtalik raqami masalan 17 000 bo'lsa javob 7 qaytadi

        //Oxirgi 4 yoki 7 ming kabi sonlarni berish uchun bazada yetarli pul bormi
        boolean enoughLastNumber = false;
        if (lastNumberOfThousand > 5){//agar beshdan katta bo'lsa bitta beshmingtalik va mingtalik yoki ming taliklar soni yetarli bo'lishi kerak
            if (baza.get(MoneyName.ONE_DOLLAR.name()) >= lastNumberOfThousand || (baza.get(MoneyName.FIVE_DOLLAR.name()) > 0 && baza.get(MoneyName.BIR_MING.name()) >= lastNumberOfThousand - 5))
                enoughLastNumber = true;
        }else if (lastNumberOfThousand == 5){
            if (baza.get(MoneyName.FIVE_DOLLAR.name()) > 0 || baza.get(MoneyName.ONE_DOLLAR.name()) > 4)
                enoughLastNumber = true;
        }else {
            if (baza.get(MoneyName.ONE_DOLLAR.name()) >= lastNumberOfThousand)
                enoughLastNumber = true;
        }

        //Agar oxirgi summasini berolmasak pul yechishni imkoni yo'q
        if (!enoughLastNumber)
            return new ApiResponse("error in last number",null);

        if (sum > 100)
            return hundred(baza, sum,bankomat);
        if (sum > 50)
            return fifty(baza, sum,bankomat);
        if (sum > 20)
            return twenty(baza, sum,bankomat);
        if (sum > 10)
            return ten(baza, sum,bankomat);
        return five(baza, sum,bankomat);

    }
    public ApiResponse hundred(HashMap<String,Integer> baza, Integer summa, Bankomat bankomat){
        int count = summa / 100_000;
        if (baza.get(MoneyName.ONE_HUNDRED_DOLLAR.name()) >= count) {
            baza.put(MoneyName.ONE_HUNDRED_DOLLAR.name(),  (baza.get(MoneyName.ONE_HUNDRED_DOLLAR.name()) - count));
            if (summa % 100 == 0){ //bizga kelgan summa 300 200 kabi yaxlit summa bo'lsa method ishi tugaydi
                moneyTransferService.updateDataBaseDollar(baza,bankomat);
                return new ApiResponse("yechildi",true);

            }
            summa = summa - count * 100;
            return fifty(baza, summa,bankomat);
        }
        summa = summa - baza.get(MoneyName.ONE_HUNDRED_DOLLAR.name()) * 100;
        baza.put(MoneyName.ONE_HUNDRED_DOLLAR.name(), 0);
        return fifty(baza,summa,bankomat);
    }

    public ApiResponse fifty(HashMap<String,Integer> baza, Integer summa, Bankomat bankomat){
        if (summa >= 50){
            int count = summa / 50;
            if (baza.get(MoneyName.FIFTY_DOLLAR.name()) >= count){
                baza.put(MoneyName.FIFTY_DOLLAR.name(), baza.get(MoneyName.FIFTY_DOLLAR.name()) - count);
                if (summa % 50 == 0){
                    moneyTransferService.updateDataBaseDollar(baza,bankomat);
                    return new ApiResponse("yechildi",true);
                }
                summa = summa - count * 50;
                return ten(baza, summa,bankomat);
            }
            summa = summa - baza.get(MoneyName.FIFTY_DOLLAR.name()) * 50;
            baza.put(MoneyName.FIFTY_DOLLAR.name(), 0);
            return ten(baza,summa,bankomat);
        }else {
            return twenty(baza, summa,bankomat);
        }
    }
    public ApiResponse twenty(HashMap<String,Integer> baza, Integer summa, Bankomat bankomat){
        if (summa >=20){
            int count = summa / 20;
            if (baza.get(MoneyName.TWENTY_DOLLAR.name()) >= count){
                baza.put(MoneyName.TWENTY_DOLLAR.name(), baza.get(MoneyName.TWENTY_DOLLAR.name()) - count);
                if (summa % 20 == 0){
                    moneyTransferService.updateDataBaseDollar(baza,bankomat);
                    return new ApiResponse("yechildi",true);

                }
                summa = summa - count * 20;
                return ten(baza, summa,bankomat);
            }
            summa = summa - baza.get(MoneyName.TWENTY_DOLLAR.name()) * 20;
            baza.put(MoneyName.TWENTY_DOLLAR.name(), 0);
            return ten(baza,summa,bankomat);
        }else {
            return ten(baza, summa,bankomat);
        }
    }

    public ApiResponse ten(HashMap<String,Integer> baza, Integer sum, Bankomat bankomat){
        if (sum >= 10){
            int count = sum / 10;
            if (baza.get(MoneyName.TEN_DOLLAR.name()) >= count){
                baza.put(MoneyName.TEN_DOLLAR.name(),baza.get(MoneyName.TEN_DOLLAR.name()) - count);
                if (sum % 10 == 0){
                    moneyTransferService.updateDataBaseDollar(baza,bankomat);
                    return new ApiResponse("yechildi",true);
                }
                sum = sum - count * 10;
                return ten(baza, sum,bankomat);
            }
            sum = sum - baza.get(MoneyName.TEN_DOLLAR.name()) * 10;
            baza.put(MoneyName.TEN_DOLLAR.name(), 0);
            return five(baza,sum,bankomat);
        }else {
            return five(baza, sum,bankomat);
        }
    }

    public ApiResponse five(HashMap<String,Integer> baza, Integer sum, Bankomat bankomat){
        if (sum >= 5){
            int count = sum / 5;
            if (baza.get(MoneyName.FIVE_DOLLAR.name()) >= count){
                baza.put(MoneyName.FIVE_DOLLAR.name(), baza.get(MoneyName.FIVE_DOLLAR.name()) - count);
                if (sum % 5 == 0){
                    moneyTransferService.updateDataBaseDollar(baza,bankomat);
                    return new ApiResponse("yechildi",true);
                }
                sum = sum - count * 5;
                return ten(baza, sum,bankomat);
            }
            sum = sum - baza.get(MoneyName.TEN_DOLLAR.name()) * 5;
            baza.put(MoneyName.TEN_DOLLAR.name(), 0);
            return one(baza,sum,bankomat);
        }else {
            return one(baza, sum,bankomat);
        }
    }
    public ApiResponse one(HashMap<String,Integer> baza, Integer sum, Bankomat bankomat){
        if (sum >= 1){
            int count = sum ;
            if (baza.get(MoneyName.ONE_DOLLAR.name()) >= count){
                baza.put(MoneyName.ONE_DOLLAR.name(), baza.get(MoneyName.BIR_MING.name()) - count);
                moneyTransferService.updateDataBaseDollar(baza,bankomat);
                return new ApiResponse("yechildi",true);
            }
        }

        return new ApiResponse("error",false);

    }
}