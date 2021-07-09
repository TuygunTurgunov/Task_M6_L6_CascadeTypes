package uz.pdp.online.m6l6t2appbankomatcascadetypes.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.*;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.enums.MoneyName;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.payload.ApiResponse;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.payload.TransferDto;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.repository.*;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.service.innerService.MoneyTransferService;

import java.util.HashMap;
import java.util.Optional;

@Service
public class TransferIncomeService {
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private BankomatRepository bankomatRepository;
    @Autowired
    private BankomatService bankomatService;
    @Autowired
    private InHistoryRepository inHistoryRepository;

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


        if (requestCardMoney%1000!=0)
            return new ApiResponse("tanga solib bo'lmaydi",false);

        if (bankomat.getTotalAmountMoney()>=requestCardMoney){
            bankomat.setTotalAmountMoney(bankomat.getTotalAmountMoney()-requestCardMoney);
            bankomatRepository.save(bankomat);
            card.setAmountMoney(card.getAmountMoney()+requestCardMoney);
            cardRepository.save(card);
            InHistory inHistory=new InHistory();
            inHistory.setCard(card);
            inHistory.setBankomat(bankomat);
            inHistory.setMoneyAmount(requestCardMoney);
            inHistoryRepository.save(inHistory);
            return new ApiResponse("kartaga pul tushdi",true);
        }return new ApiResponse("bankomatdagi pul miqdori yetarli emas",false);

    }
}