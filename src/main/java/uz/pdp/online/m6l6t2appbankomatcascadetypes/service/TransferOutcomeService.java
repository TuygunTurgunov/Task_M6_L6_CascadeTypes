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
import uz.pdp.online.m6l6t2appbankomatcascadetypes.repository.*;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.service.innerService.MoneyTransferService;


import java.util.HashMap;
import java.util.Optional;

@Service
public class TransferOutcomeService {
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
    public ApiResponse calculate(TransferDto transferDto){

//1  ==> check card
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Card currentCard = (Card) authentication.getPrincipal();


        Optional<Card> optionalCard = cardRepository.findByCardNumber(currentCard.getCardNumber());
        if (!optionalCard.isPresent())
            return new ApiResponse("card not found by card number", false);
        Card card = optionalCard.get();

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
        if (bankomat.getTotalAmountMoney() < 50000) {
            bankomatService.sendEmail(bankomatUserEmail, bankomat.getId());
            return new ApiResponse("Bankomat da pul kam qolgan", false);
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

        HashMap<String, Integer> bankNotesQuantity = moneyTransferService.getBankNotesQuantity(bankomat);
        ApiResponse exchange = exchanges(bankNotesQuantity, requestCardMoney,bankomat);
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

    public ApiResponse exchanges(HashMap<String,Integer> baza, Integer sum,Bankomat bankomat){
        if (sum<baza.get("min")||sum>baza.get("max"))
            return new ApiResponse("error min value or max value",false);
        if (sum%1000!=0)
            return new ApiResponse("tanga yo'q",false);
        if (baza.get("all")<sum)
            return new ApiResponse("bankomatda siz kiritgan summadan kam miqdorda summa bor",false);
        int lastNumberOfThousand = Math.toIntExact(sum % 10_000 / 1_000);//oxirgi mingtalik raqami masalan 17 000 bo'lsa javob 7 qaytadi

        //Oxirgi 4 yoki 7 ming kabi sonlarni berish uchun bazada yetarli pul bormi
        boolean enoughLastNumber = false;
        if (lastNumberOfThousand > 5){//agar beshdan katta bo'lsa bitta beshmingtalik va mingtalik yoki ming taliklar soni yetarli bo'lishi kerak
            if (baza.get(MoneyName.BIR_MING.name()) >= lastNumberOfThousand || (baza.get(MoneyName.BESH_MING.name()) > 0 && baza.get(MoneyName.BIR_MING.name()) >= lastNumberOfThousand - 5))
                enoughLastNumber = true;
        }else if (lastNumberOfThousand == 5){
            if (baza.get(MoneyName.BESH_MING.name()) > 0 || baza.get(MoneyName.BIR_MING.name()) > 4)
                enoughLastNumber = true;
        }else {
            if (baza.get(MoneyName.BIR_MING.name()) >= lastNumberOfThousand)
                enoughLastNumber = true;
        }

        //Agar oxirgi summasini berolmasak pul yechishni imkoni yo'q
        if (!enoughLastNumber)
            return new ApiResponse("error in last number",null);

        if (sum > 100_000)
            return yuzming(baza, sum,bankomat);
        if (sum > 50_000)
            return ellikming(baza, sum,bankomat);
        if (sum > 10_000)
            return onming(baza, sum,bankomat);
        return beshming(baza, sum,bankomat);

    }
    public ApiResponse yuzming(HashMap<String,Integer> baza, Integer summa,Bankomat bankomat){
        int count = summa / 100_000;
        if (baza.get(MoneyName.YUZ_MING.name()) >= count) {
            baza.put(MoneyName.YUZ_MING.name(),  (baza.get(MoneyName.YUZ_MING.name()) - count));
            if (summa % 100_000 == 0){ //bizga kelgan summa 300 200 kabi yaxlit summa bo'lsa method ishi tugaydi
                moneyTransferService.updateDataBase(baza,bankomat);
                return new ApiResponse("yechildi",true);

            }
            summa = summa - count * 100_000;
            return ellikming(baza, summa,bankomat);
        }
        summa = summa - baza.get(MoneyName.YUZ_MING.name()) * 100_000;
        baza.put(MoneyName.YUZ_MING.name(), 0);
        return ellikming(baza,summa,bankomat);
    }

    public ApiResponse ellikming(HashMap<String,Integer> baza, Integer summa,Bankomat bankomat){
        if (summa >= 50_000){
            int count = summa / 50_000;
            if (baza.get(MoneyName.ELLIK_MING.name()) >= count){
                baza.put(MoneyName.ELLIK_MING.name(), baza.get(MoneyName.ELLIK_MING.name()) - count);
                if (summa % 50_000 == 0){
                    moneyTransferService.updateDataBase(baza,bankomat);
                    return new ApiResponse("yechildi",true);

                }
                summa = summa - count * 50_000;
                return onming(baza, summa,bankomat);
            }
            summa = summa - baza.get(MoneyName.ELLIK_MING.name()) * 50_000;
            baza.put(MoneyName.ELLIK_MING.name(), 0);
            return onming(baza,summa,bankomat);
        }else {
            return onming(baza, summa,bankomat);
        }
    }

    public ApiResponse onming(HashMap<String,Integer> baza, Integer sum,Bankomat bankomat){
        if (sum >= 10_000){
            int count = sum / 10_000;
            if (baza.get(MoneyName.ONG_MING.name()) >= count){
                baza.put(MoneyName.ONG_MING.name(),baza.get(MoneyName.ONG_MING.name()) - count);
                if (sum % 10_000 == 0){
                    moneyTransferService.updateDataBase(baza,bankomat);
                    return new ApiResponse("yechildi",true);
                }
                sum = sum - count * 10_000;
                return onming(baza, sum,bankomat);
            }
            sum = sum - baza.get(MoneyName.ONG_MING.name()) * 10_000;
            baza.put(MoneyName.ONG_MING.name(), 0);
            return beshming(baza,sum,bankomat);
        }else {
            return beshming(baza, sum,bankomat);
        }
    }

    public ApiResponse beshming(HashMap<String,Integer> baza, Integer sum,Bankomat bankomat){
        if (sum >= 5_000){
            int count = sum / 5_000;
            if (baza.get(MoneyName.BESH_MING.name()) >= count){
                baza.put(MoneyName.BESH_MING.name(), baza.get(MoneyName.BESH_MING.name()) - count);
                if (sum % 5_000 == 0){
                    moneyTransferService.updateDataBase(baza,bankomat);
                    return new ApiResponse("yechildi",true);
                }
                sum = sum - count * 5_000;
                return onming(baza, sum,bankomat);
            }
            sum = sum - baza.get(MoneyName.BESH_MING.name()) * 5_000;
            baza.put(MoneyName.BESH_MING.name(), 0);
            return ming(baza,sum,bankomat);
        }else {
            return ming(baza, sum,bankomat);
        }
    }
    public ApiResponse ming(HashMap<String,Integer> baza, Integer sum,Bankomat bankomat){
        if (sum >= 1_000){
            int count = sum / 1_000;
            if (baza.get(MoneyName.BIR_MING.name()) >= count){
                baza.put(MoneyName.BIR_MING.name(), baza.get(MoneyName.BIR_MING.name()) - count);
                moneyTransferService.updateDataBase(baza,bankomat);
                return new ApiResponse("yechildi",true);
            }
        }
        return new ApiResponse("error",false);
    }
//    public static HashMap<String,Integer> updateBaza(HashMap<String,Integer> baza){
//        Integer yuz = baza.get("yuz");
//        Integer ellik = baza.get("ellik");
//        Integer on = baza.get("on");
//        Integer besh = baza.get("besh");
//        Integer ming = baza.get("ming");
//        Integer all = yuz * 100_000 + ellik * 50_000 + on * 10_000 + besh * 5_000 + ming * 1_000;
//        baza.put("baza",all);
//        return baza;
//    }
}