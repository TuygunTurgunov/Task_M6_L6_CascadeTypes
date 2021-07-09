package uz.pdp.online.m6l6t2appbankomatcascadetypes.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.*;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.enums.RoleName;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.payload.ApiResponse;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.payload.CardBlockedDto;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.payload.CardDto;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.repository.*;
//import uz.pdp.online.m6l6t2appbankomatcascadetypes.repository.LastCardUniqueNumberRepository;

import java.sql.Timestamp;
import java.util.*;

@Service
public class CardService {
    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private BankRepository bankRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CardTypeRepository cardTypeRepository;

    @Autowired
    private RandomCardNumber randomCardNumber;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private UserType userType;
    @Autowired
    private RoleRepository roleRepository;


    public ApiResponse add(CardDto cardDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        Integer userType = this.userType.getUserType(currentUser);
        if (userType!=2)
            return new ApiResponse("only office employee can add card",false);



        Optional<Bank> optionalBank = bankRepository.findById(cardDto.getBankId());
        if (!optionalBank.isPresent())
            return new ApiResponse("bank not found",false);

        Optional<CardType> optionalCardType = cardTypeRepository.findById(cardDto.getCardTypeId());
        if (!optionalCardType.isPresent())
            return new ApiResponse("card type not found",false);

        Optional<Client> optionalClient = clientRepository.findById(cardDto.getClientId());
        if (!optionalClient.isPresent())
            return new ApiResponse("client not found",false);

        Card newCard=new Card();
        newCard.setCardNumber(randomCardNumber.getNumber());
        newCard.setCardType(optionalCardType.get());
        newCard.setAmountMoney(cardDto.getAmountMoney());
        newCard.setBank(optionalBank.get());
        newCard.setPassword(passwordEncoder.encode(cardDto.getPassword()));
        newCard.setCvvCode(cardDto.getCvvCode());
        newCard.setClient(optionalClient.get());
        newCard.setRoles(Collections.singleton(roleRepository.findByRoleName(RoleName.ROLE_CARD)));
        newCard.setCountChance(0);
        Long expireDate = cardDto.getExpireDate();
        Timestamp timestamp=new Timestamp(expireDate);
        newCard.setExpireDate(timestamp);
        cardRepository.save(newCard);
        return new ApiResponse("card saved",true);
    }

    public ApiResponse edit (UUID id){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User currentUser =(User) authentication.getPrincipal();
        Integer userType = this.userType.getUserType(currentUser);
        if (userType!=2)
            return new ApiResponse("only office employee can add card",false);


        Optional<Card> optionalCard = cardRepository.findById(id);
        if (!optionalCard.isPresent())
            return new ApiResponse("card not found by uuid",false);
        Card card = optionalCard.get();
        card.setAccountNonLocked(true);
        cardRepository.save(card);
        return new ApiResponse("card block edited",true);
    }

    public Card getOneCard(UUID id){
        Optional<Card> optionalCard = cardRepository.findById(id);
        return optionalCard.orElse(null);

    }

    public List<Card> getALlCard(){

        return cardRepository.findAll();
    }


    public ApiResponse delete(UUID id) {

        try {
            cardRepository.deleteById(id);
            return new ApiResponse("deleted",true);
        }catch (Exception e){
            return new ApiResponse("no deleted",false);
        }


    }

    public ApiResponse editBlocked(Long cardNumber) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        Integer userType = this.userType.getUserType(currentUser);
        if (userType!=2)
            return new ApiResponse("only office employee can change block type",false);


        Optional<Card> optionalCard = cardRepository.findByCardNumber(cardNumber);
        if (!optionalCard.isPresent())
            return new ApiResponse("card not found",false);
        Card card = optionalCard.get();
        card.setAccountNonLocked(true);
        card.setCountChance(0);
        cardRepository.save(card);
        return new ApiResponse("card edited AccountNonLocked = true ",true);

    }
}
