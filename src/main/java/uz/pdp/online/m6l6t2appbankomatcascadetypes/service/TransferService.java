package uz.pdp.online.m6l6t2appbankomatcascadetypes.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.*;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.enums.MoneyName;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.payload.ApiResponse;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.payload.TransferDto;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.repository.*;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.service.innerService.MoneyTransferService;

import java.util.List;
import java.util.Optional;

@Service
public class TransferService {

    @Autowired
    CardRepository cardRepository;
    @Autowired
    BankomatRepository bankomatRepository;
    @Autowired
    BankomatService bankomatService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    SortNumbers sortNumbers;
    @Autowired
    BankNotesBankomatRepository bankNotesBankomatRepository;
    @Autowired
    BankNotesRepository bankNotesRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    MoneyTransferService moneyTransferService;
    @Autowired
    UserType userType;



    @Transactional(rollbackFor =NullPointerException.class)
    public ApiResponse addOutcomeHistory(TransferDto transferDto) {


//1
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Card currentCard = (Card) authentication.getPrincipal();


        Optional<Card> optionalCard = cardRepository.findByCardNumber(currentCard.getCardNumber());
        if (!optionalCard.isPresent())
            return new ApiResponse("card not found by card number", false);
        Card card = optionalCard.get();

//2

        Bankomat bankomat = bankomatRepository.getById(transferDto.getBankomatId());

//3
        if (!bankomat.getCardType().getCardTypeName().name().equals(card.getCardType().getCardTypeName().name())) {
            return new ApiResponse("bankomat type and card type not the same", false);
        }

//4
        if (!card.getAccountNonLocked())
            return new ApiResponse("your card is  blocked , you should unblock first", false);


//        boolean matches = passwordEncoder.matches("kevotganParol", "tizimdagi bor userni paroli");


//5
        boolean cardBankEqualsBankomatBank = false;
        String bankomatBankName = bankomat.getBank().getBankName().name();
        String cardBankName = card.getBank().getBankName().name();
        if (bankomatBankName.equals(cardBankName))
            cardBankEqualsBankomatBank = true;


//6
        User bankomatEmployee = bankomat.getUser();
        String bankomatUserEmail = bankomatEmployee.getEmail();
        if (bankomat.getTotalAmountMoney() < 50000) {
            bankomatService.sendEmail(bankomatUserEmail, bankomat.getId());
            return new ApiResponse("Bankomat da pul kam qolgan", false);
        }

//7
        Integer requestCardMoney = transferDto.getMoneyAmount();
        if (requestCardMoney < 50000 || requestCardMoney > 4000000)
            return new ApiResponse("eng kamida 50.000  min olinsin , eng ko'pi bn 4.000.000 mln", false);

        if (bankomat.getTotalAmountMoney() < requestCardMoney)
            return new ApiResponse("bankomatda kiritilgan puldan kam pul ", false);

        if (requestCardMoney%1000!=0){
            return new ApiResponse("mayda chuyda kupyuralar yo'q",false);
        }

//8
        double percent;
        double totalSumma;

        if (cardBankEqualsBankomatBank) {
            percent = 0.05f;
            totalSumma = requestCardMoney + requestCardMoney * percent;
        } else {
            percent = 0.1f;
            totalSumma = requestCardMoney + requestCardMoney * percent;
        }


//9
        if (card.getAmountMoney() < totalSumma)
            return new ApiResponse("kartada pul foiz bn yechish uchun  yetarli emas", false);

//9
        List<Integer> listRequestNumbers = sortNumbers.sort(transferDto.getMoneyAmount());
        int moneySize = listRequestNumbers.size();


//        Integer bankNotesAndQuantity = moneyTransferService.getBankNoteAndQuantity(bankomat, MoneyName.YUZ_MING);
//        moneyTransferService.setBankNoteQuantity(bankomat,MoneyName.BESH_MING,12);
//
//
//
//
//        List<BankNotesBankomat> allBanknotesInBankomat = bankNotesBankomatRepository.findAllByBankomat(bankomat);
//        int yuzMingtalikSoni = 0, ellikMingtalikSoni=0, onMingtalikSoni=0, beshMingtalikSoni=0, mingtalikSoni=0;
//
//        for (BankNotesBankomat bankNotesBankomat : allBanknotesInBankomat) {
//            if (bankNotesBankomat.getBankNotes().getMoneyName().equals(MoneyName.YUZ_MING)) {
//                yuzMingtalikSoni = bankNotesBankomat.getQuantity();
//
//             }
//            if (bankNotesBankomat.getBankNotes().getMoneyName().equals(MoneyName.ELLIK_MING)) {
//                ellikMingtalikSoni = bankNotesBankomat.getQuantity();
//            }
//
//            if (bankNotesBankomat.getBankNotes().getMoneyName().equals(MoneyName.ONG_MING)) {
//                onMingtalikSoni = bankNotesBankomat.getQuantity();
//            }
//            if (bankNotesBankomat.getBankNotes().getMoneyName().equals(MoneyName.BESH_MING)) {
//                beshMingtalikSoni = bankNotesBankomat.getQuantity();
//            }
//            if (bankNotesBankomat.getBankNotes().getMoneyName().equals(MoneyName.BIR_MING)) {
//                mingtalikSoni = bankNotesBankomat.getQuantity();
//            }
//        }
//

        /////////////////////////////////  7 xonali summa kirganda



        if (listRequestNumbers.size()==4){

            if (listRequestNumbers.get(0)!=0){
                Integer firstNumber = listRequestNumbers.get(0);
                if ( moneyTransferService.getBankNoteAndQuantity(bankomat, MoneyName.YUZ_MING)>=firstNumber*10){
//                    allBanknotesInBankomat.get(0).setQuantity(yuzMingtalikSoni-firstNumber*10);
                    moneyTransferService.setBankNoteQuantity(bankomat,MoneyName.YUZ_MING,firstNumber*10);
                    //                    bankNotesBankomatRepository.saveAll(allBanknotesInBankomat);
                } else if (moneyTransferService.getBankNoteAndQuantity(bankomat, MoneyName.ELLIK_MING)>=firstNumber*20){
//                    allBanknotesInBankomat.get(1).setQuantity(ellikMingtalikSoni-firstNumber*20);
//                    bankNotesBankomatRepository.saveAll(allBanknotesInBankomat);
                    moneyTransferService.setBankNoteQuantity(bankomat,MoneyName.ELLIK_MING,firstNumber*20);
                }else if (moneyTransferService.getBankNoteAndQuantity(bankomat,MoneyName.ONG_MING)>=firstNumber*100){
//                    allBanknotesInBankomat.get(2).setQuantity(onMingtalikSoni-firstNumber*100);
//                    bankNotesBankomatRepository.saveAll(allBanknotesInBankomat);
                    moneyTransferService.setBankNoteQuantity(bankomat,MoneyName.ONG_MING,firstNumber*100);
                }else if (moneyTransferService.getBankNoteAndQuantity(bankomat,MoneyName.BESH_MING)>=firstNumber*200){
//                    allBanknotesInBankomat.get(3).setQuantity(beshMingtalikSoni-firstNumber*200);
//                    bankNotesBankomatRepository.saveAll(allBanknotesInBankomat);
                    moneyTransferService.setBankNoteQuantity(bankomat,MoneyName.BESH_MING,firstNumber*200);
                }else if (moneyTransferService.getBankNoteAndQuantity(bankomat,MoneyName.BIR_MING)>=firstNumber*1000){
//                    allBanknotesInBankomat.get(4).setQuantity(mingtalikSoni-firstNumber*1000);
//                    bankNotesBankomatRepository.saveAll(allBanknotesInBankomat);
                    moneyTransferService.setBankNoteQuantity(bankomat,MoneyName.BIR_MING,firstNumber*1000);
                }
            }else {
                return new ApiResponse("summa 0 dan boshlanmasin",false);
            }

            if (listRequestNumbers.get(1)!=0){
                Integer secondNumber = listRequestNumbers.get(1);
                if (moneyTransferService.getBankNoteAndQuantity(bankomat,MoneyName.YUZ_MING)>=secondNumber){
//                    allBanknotesInBankomat.get(0).setQuantity(yuzMingtalikSoni-secondNumber);
//                    bankNotesBankomatRepository.saveAll(allBanknotesInBankomat);
                    moneyTransferService.setBankNoteQuantity(bankomat,MoneyName.YUZ_MING,secondNumber);
                }else if (moneyTransferService.getBankNoteAndQuantity(bankomat,MoneyName.ELLIK_MING)>=secondNumber*2){
//                    allBanknotesInBankomat.get(1).setQuantity(ellikMingtalikSoni-secondNumber*2);
//                    bankNotesBankomatRepository.saveAll(allBanknotesInBankomat);
                    moneyTransferService.setBankNoteQuantity(bankomat,MoneyName.ELLIK_MING,secondNumber*2);
                }else if (moneyTransferService.getBankNoteAndQuantity(bankomat,MoneyName.ONG_MING)>=secondNumber*10){
//                    allBanknotesInBankomat.get(2).setQuantity(onMingtalikSoni-secondNumber*10);
//                    bankNotesBankomatRepository.saveAll(allBanknotesInBankomat);
                    moneyTransferService.setBankNoteQuantity(bankomat,MoneyName.ONG_MING,secondNumber*10);
                }else if (moneyTransferService.getBankNoteAndQuantity(bankomat,MoneyName.BESH_MING)>=secondNumber*20){
//                    allBanknotesInBankomat.get(3).setQuantity(beshMingtalikSoni-secondNumber*20);
//                    bankNotesBankomatRepository.saveAll(allBanknotesInBankomat);

                }else if (moneyTransferService.getBankNoteAndQuantity(bankomat,MoneyName.BIR_MING)>=secondNumber*100){
//                    allBanknotesInBankomat.get(4).setQuantity(mingtalikSoni-secondNumber*100);
//                    bankNotesBankomatRepository.saveAll(allBanknotesInBankomat);
                    moneyTransferService.setBankNoteQuantity(bankomat,MoneyName.BIR_MING,secondNumber*100);
                }
            }
            if(listRequestNumbers.get(2)!=0){
                Integer thirdNumber = listRequestNumbers.get(2);
                if (moneyTransferService.getBankNoteAndQuantity(bankomat,MoneyName.ELLIK_MING)>=1 && thirdNumber>=5){
//                    allBanknotesInBankomat.get(1).setQuantity(ellikMingtalikSoni-1);
//                    bankNotesBankomatRepository.saveAll(allBanknotesInBankomat);
                    moneyTransferService.setBankNoteQuantity(bankomat,MoneyName.ELLIK_MING,1);

                    thirdNumber=thirdNumber-5;
                    if (thirdNumber!=0){
                        if (moneyTransferService.getBankNoteAndQuantity(bankomat,MoneyName.ONG_MING)>=thirdNumber){
//                            allBanknotesInBankomat.get(2).setQuantity(onMingtalikSoni-thirdNumber);
//                            bankNotesBankomatRepository.saveAll(allBanknotesInBankomat);
                            moneyTransferService.setBankNoteQuantity(bankomat,MoneyName.ONG_MING,thirdNumber);
                        }
                        else if (moneyTransferService.getBankNoteAndQuantity(bankomat,MoneyName.BESH_MING)>=thirdNumber*2){
//                            allBanknotesInBankomat.get(3).setQuantity(beshMingtalikSoni-thirdNumber*2);
//                            bankNotesBankomatRepository.saveAll(allBanknotesInBankomat);
                            moneyTransferService.setBankNoteQuantity(bankomat,MoneyName.BESH_MING,thirdNumber*2);
                        }
                        else if (moneyTransferService.getBankNoteAndQuantity(bankomat,MoneyName.BIR_MING)>=thirdNumber*10){
//                            allBanknotesInBankomat.get(4).setQuantity(mingtalikSoni-thirdNumber*10);
//                            bankNotesBankomatRepository.saveAll(allBanknotesInBankomat);
                            moneyTransferService.setBankNoteQuantity(bankomat,MoneyName.BIR_MING,thirdNumber*10);
                        }
                    }
                }else {
                    if (moneyTransferService.getBankNoteAndQuantity(bankomat,MoneyName.ONG_MING)>=thirdNumber){
//                        allBanknotesInBankomat.get(2).setQuantity(onMingtalikSoni-thirdNumber);
//                        bankNotesBankomatRepository.saveAll(allBanknotesInBankomat);
                        moneyTransferService.setBankNoteQuantity(bankomat,MoneyName.ONG_MING,thirdNumber);
                    }
                    else if (moneyTransferService.getBankNoteAndQuantity(bankomat,MoneyName.BESH_MING)>=thirdNumber*2){
//                        allBanknotesInBankomat.get(3).setQuantity(beshMingtalikSoni-thirdNumber*2);
//                        bankNotesBankomatRepository.saveAll(allBanknotesInBankomat);
                        moneyTransferService.setBankNoteQuantity(bankomat,MoneyName.BESH_MING,thirdNumber*2);
                    }

                    else if (moneyTransferService.getBankNoteAndQuantity(bankomat,MoneyName.BIR_MING)>=thirdNumber*10){
//                        allBanknotesInBankomat.get(4).setQuantity(mingtalikSoni-thirdNumber*10);
//                        bankNotesBankomatRepository.saveAll(allBanknotesInBankomat);
                        moneyTransferService.setBankNoteQuantity(bankomat,MoneyName.BIR_MING,thirdNumber*10);
                    }
                }
            }
            if (listRequestNumbers.get(3)!=0){
                Integer fourthNumber = listRequestNumbers.get(3);
                if (moneyTransferService.getBankNoteAndQuantity(bankomat,MoneyName.BESH_MING)>=1 && fourthNumber>=5){
                    fourthNumber=fourthNumber-5;
//                    allBanknotesInBankomat.get(3).setQuantity(beshMingtalikSoni-1);
//                    bankNotesBankomatRepository.saveAll(allBanknotesInBankomat);
                    moneyTransferService.setBankNoteQuantity(bankomat,MoneyName.BESH_MING,1);
                    if (fourthNumber!=0 && moneyTransferService.getBankNoteAndQuantity(bankomat,MoneyName.BIR_MING)>=fourthNumber){
//                        allBanknotesInBankomat.get(4).setQuantity(mingtalikSoni-fourthNumber);
//                        bankNotesBankomatRepository.saveAll(allBanknotesInBankomat);
                        moneyTransferService.setBankNoteQuantity(bankomat,MoneyName.BIR_MING,fourthNumber);
                    }
                }else {
//                    if (moneyTransferService.getBankNoteAndQuantity(bankomat,MoneyName.BIR_MING)>=fourthNumber){
////                        allBanknotesInBankomat.get(4).setQuantity(mingtalikSoni-fourthNumber);
////                        bankNotesBankomatRepository.saveAll(allBanknotesInBankomat);
//                        moneyTransferService.setBankNoteQuantity(bankomat,MoneyName.BIR_MING,fourthNumber);
//                    }

                        if (moneyTransferService.getBankNoteAndQuantity(bankomat,MoneyName.BIR_MING)>=fourthNumber)
                            moneyTransferService.setBankNoteQuantity(bankomat,MoneyName.BIR_MING,fourthNumber);
                        else {
                            throw new NullPointerException();
                        }

                }
            }

            card.setAmountMoney(card.getAmountMoney()-totalSumma);
            cardRepository.save(card);
            bankomat.setTotalAmountMoney(bankomat.getTotalAmountMoney()-requestCardMoney);
            bankomatRepository.save(bankomat);
            return new ApiResponse("yechildi",true);
        }

 ////////////////////        6 xonali summa kirganida


//        if(listRequestNumbers.size()==3){
//            if(listRequestNumbers.get(0)!=0){
//                Integer firstNumber = listRequestNumbers.get(0);
//                if (yuzMingtalikSoni>=firstNumber){
//                    allBanknotesInBankomat.get(0).setQuantity(yuzMingtalikSoni-firstNumber);
//                    bankNotesBankomatRepository.saveAll(allBanknotesInBankomat);
//                }else if (ellikMingtalikSoni>=firstNumber*2){
//                    allBanknotesInBankomat.get(1).setQuantity(ellikMingtalikSoni-firstNumber*2);
//                    bankNotesBankomatRepository.saveAll(allBanknotesInBankomat);
//                }else if (onMingtalikSoni>=firstNumber*10){
//                    allBanknotesInBankomat.get(2).setQuantity(onMingtalikSoni-firstNumber*10);
//                    bankNotesBankomatRepository.saveAll(allBanknotesInBankomat);
//                }else if (beshMingtalikSoni>=firstNumber*20){
//                    allBanknotesInBankomat.get(3).setQuantity(beshMingtalikSoni-firstNumber*20);
//                    bankNotesBankomatRepository.saveAll(allBanknotesInBankomat);
//                }else if (mingtalikSoni>=firstNumber*100){
//                    allBanknotesInBankomat.get(4).setQuantity(mingtalikSoni-firstNumber*100);
//                    bankNotesBankomatRepository.saveAll(allBanknotesInBankomat);
//                }
//            }else {
//                return new ApiResponse("summa 0 dan boshlanmasin,",false);
//            }
//
//            if (listRequestNumbers.get(1)!=0){
//                Integer secondNumber = listRequestNumbers.get(1);
//                if (ellikMingtalikSoni>=1 && secondNumber>=5){
//                    allBanknotesInBankomat.get(1).setQuantity(ellikMingtalikSoni-1);
//                    bankNotesBankomatRepository.saveAll(allBanknotesInBankomat);
//                    secondNumber=secondNumber-5;
//                    if (secondNumber!=0){
//                        if (onMingtalikSoni>=secondNumber){
//                            allBanknotesInBankomat.get(2).setQuantity(onMingtalikSoni-secondNumber);
//                            bankNotesBankomatRepository.saveAll(allBanknotesInBankomat);
//                        }
//                        else if (beshMingtalikSoni>=secondNumber*2){
//                            allBanknotesInBankomat.get(3).setQuantity(beshMingtalikSoni-secondNumber*2);
//                            bankNotesBankomatRepository.saveAll(allBanknotesInBankomat);
//                        }
//                        else if (mingtalikSoni>=secondNumber*10){
//                            allBanknotesInBankomat.get(4).setQuantity(mingtalikSoni-secondNumber*10);
//                            bankNotesBankomatRepository.saveAll(allBanknotesInBankomat);
//                        }
//                    }
//                }else {
//                    if (onMingtalikSoni>=secondNumber){
//                        allBanknotesInBankomat.get(2).setQuantity(onMingtalikSoni-secondNumber);
//                        bankNotesBankomatRepository.saveAll(allBanknotesInBankomat);
//                    }
//                    else if (beshMingtalikSoni>=secondNumber*2){
//                        allBanknotesInBankomat.get(3).setQuantity(beshMingtalikSoni-secondNumber*2);
//                        bankNotesBankomatRepository.saveAll(allBanknotesInBankomat);
//                    }
//                    else if (mingtalikSoni>=secondNumber*10){
//                        allBanknotesInBankomat.get(4).setQuantity(mingtalikSoni-secondNumber*10);
//                        bankNotesBankomatRepository.saveAll(allBanknotesInBankomat);
//                    }
//                }
//
//            }
//
//            if (listRequestNumbers.get(2)!=0){
//                Integer fourthNumber = listRequestNumbers.get(2);
//                if (beshMingtalikSoni>=1 && fourthNumber>=5){
//                    fourthNumber=fourthNumber-5;
//                    allBanknotesInBankomat.get(3).setQuantity(beshMingtalikSoni-1);
//                    bankNotesBankomatRepository.saveAll(allBanknotesInBankomat);
//                    if (fourthNumber!=0 && mingtalikSoni>=fourthNumber){
//                        allBanknotesInBankomat.get(4).setQuantity(mingtalikSoni-fourthNumber);
//                        bankNotesBankomatRepository.saveAll(allBanknotesInBankomat);
//                    }
//                }else {
//                    if (mingtalikSoni>=fourthNumber){
//                        allBanknotesInBankomat.get(4).setQuantity(mingtalikSoni-fourthNumber);
//                        bankNotesBankomatRepository.saveAll(allBanknotesInBankomat);
//                    }
//                }
//            }
//
//            card.setAmountMoney(card.getAmountMoney()-totalSumma);
//            cardRepository.save(card);
//            bankomat.setTotalAmountMoney(bankomat.getTotalAmountMoney()-requestCardMoney);
//            bankomatRepository.save(bankomat);
//            return new ApiResponse("yechildi",true);
//
//        }
//
//        /////   5 xonali summa
//        if (listRequestNumbers.size()==2){
//            if (listRequestNumbers.get(0)!=0){
//                Integer firstNumber = listRequestNumbers.get(0);
//                if (ellikMingtalikSoni>=1 && firstNumber>=5){
//                    allBanknotesInBankomat.get(1).setQuantity(ellikMingtalikSoni-1);
//                    bankNotesBankomatRepository.saveAll(allBanknotesInBankomat);
//                    firstNumber=firstNumber-5;
//                    if (firstNumber!=0){
//                        if (onMingtalikSoni>=firstNumber){
//                            allBanknotesInBankomat.get(2).setQuantity(onMingtalikSoni-firstNumber);
//                            bankNotesBankomatRepository.saveAll(allBanknotesInBankomat);
//                        }
//                        else if (beshMingtalikSoni>=firstNumber*2){
//                            allBanknotesInBankomat.get(3).setQuantity(beshMingtalikSoni-firstNumber*2);
//                            bankNotesBankomatRepository.saveAll(allBanknotesInBankomat);
//                        }
//                        else if (mingtalikSoni>=firstNumber*10){
//                            allBanknotesInBankomat.get(4).setQuantity(mingtalikSoni-firstNumber*10);
//                            bankNotesBankomatRepository.saveAll(allBanknotesInBankomat);
//                        }
//                    }
//                }else {
//                    if (onMingtalikSoni>=firstNumber){
//                        allBanknotesInBankomat.get(2).setQuantity(onMingtalikSoni-firstNumber);
//                        bankNotesBankomatRepository.saveAll(allBanknotesInBankomat);
//                    }
//                    else if (beshMingtalikSoni>=firstNumber*2){
//                        allBanknotesInBankomat.get(3).setQuantity(beshMingtalikSoni-firstNumber*2);
//                        bankNotesBankomatRepository.saveAll(allBanknotesInBankomat);
//                    }
//                    else if (mingtalikSoni>=firstNumber*10){
//                        allBanknotesInBankomat.get(4).setQuantity(mingtalikSoni-firstNumber*10);
//                        bankNotesBankomatRepository.saveAll(allBanknotesInBankomat);
//                    }
//                }
//            }
//            if (listRequestNumbers.get(1)!=0){
//                Integer secondNumber = listRequestNumbers.get(1);
//                if (beshMingtalikSoni>=1 && secondNumber>=5){
//                    secondNumber=secondNumber-5;
//                    allBanknotesInBankomat.get(3).setQuantity(beshMingtalikSoni-1);
//                    bankNotesBankomatRepository.saveAll(allBanknotesInBankomat);
//                    if (secondNumber!=0 && mingtalikSoni>=secondNumber){
//                        allBanknotesInBankomat.get(4).setQuantity(mingtalikSoni-secondNumber);
//                        bankNotesBankomatRepository.saveAll(allBanknotesInBankomat);
//                    }
//                }else {
//                    if (mingtalikSoni>=secondNumber){
//                        allBanknotesInBankomat.get(4).setQuantity(mingtalikSoni-secondNumber);
//                        bankNotesBankomatRepository.saveAll(allBanknotesInBankomat);
//                    }
//                }
//            }
//
//            card.setAmountMoney(card.getAmountMoney()-totalSumma);
//            cardRepository.save(card);
//            bankomat.setTotalAmountMoney(bankomat.getTotalAmountMoney()-requestCardMoney);
//            bankomatRepository.save(bankomat);
//            return new ApiResponse("yechildi",true);
//
//        }
//








//        boolean yuztalikBorr;
//        if (sortedMoney.size()==4){
//            for (int i = 0; i < sortedMoney.size(); i++) {
//                Integer moneyNum = sortedMoney.get(i);
//                if (i==0){
//                    moneyNum=moneyNum*10;
//                    if (moneyNum<=yuzMingtalikSoni){
//
//                        yuztalikBorr=true;
//                        card.setAmountMoney(card.getAmountMoney()-totalSumma);
//                        yuzMingtalikSoni=yuzMingtalikSoni-moneyNum;
//                        allBanknotesInBanknot.get(0).setQuantity(yuzMingtalikSoni);
//
//
//                    }
//                }
//            }
//        }
//




//        if (moneySize == 4) {
//            for (int i = 0; i < listRequestNumbers.size(); i++) {
//                if (i == 0 || i == 1) {
//                    boolean yuztalikBor = false;
//                    Integer moneyNum = listRequestNumbers.get(i);
//                    for (int j = 0; j < allBanknotesInBankomat.size(); j++) {
//                        if (allBanknotesInBankomat.get(j).getBankNotes().getMoneyName().equals(MoneyName.YUZ_MING)) {
//                            BankNotesBankomat bankNotesBankomat = allBanknotesInBankomat.get(j);
//                            moneyNum = moneyNum * 10;
//                            Integer moneyQuantity = bankNotesBankomat.getQuantity();
//                            if (moneyQuantity >= moneyNum) {
//                                card.setAmountMoney(card.getAmountMoney() - totalSumma);
//                                cardRepository.save(card);
//                                moneyQuantity = moneyQuantity - moneyNum;
//                                bankNotesBankomat.setQuantity(moneyQuantity);
//                                bankNotesBankomatRepository.save(bankNotesBankomat);
//                                yuztalikBor = true;
//                                bankomat.setTotalAmountMoney(bankomat.getTotalAmountMoney() - requestCardMoney);
//                                return new ApiResponse("yechildi", true);
//                            }
//                        }
//                    }
//                }
//            }
//        }


        return null;
    }
}
