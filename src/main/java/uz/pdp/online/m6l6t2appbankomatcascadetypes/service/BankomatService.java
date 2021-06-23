package uz.pdp.online.m6l6t2appbankomatcascadetypes.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.*;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.enums.RoleName;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.payload.ApiResponse;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.payload.BankomatDto;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.repository.*;

import java.util.List;
import java.util.Optional;

@Service
public class BankomatService {
    @Autowired
    BankomatRepository bankomatRepository;
    @Autowired
    CardTypeRepository cardTypeRepository;
    @Autowired
    BankRepository bankRepository;
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserType userType;
    @Autowired
    JavaMailSender javaMailSender;//Mail bilan ishlash ga


    public ApiResponse add(BankomatDto bankomatDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        Integer userType = this.userType.getUserType(currentUser);
        if (userType!=2)
            return new ApiResponse("only office employee can add card",false);


        Optional<CardType> optionalCardType = cardTypeRepository.findById(bankomatDto.getCardTypeId());
        if (!optionalCardType.isPresent())
            return new ApiResponse("card type not found", false);


        Optional<Address> optionalAddress = addressRepository.findById(bankomatDto.getAddressId());
        if (!optionalAddress.isPresent())
            return new ApiResponse("address not fund", false);

        boolean existsByCardTypeAndAddress = bankomatRepository.existsByCardTypeAndAddress(optionalCardType.get(), optionalAddress.get());

        if (existsByCardTypeAndAddress)
            return new ApiResponse("in this address already has such bankomat", false);

        Optional<Bank> optionalBank = bankRepository.findById(bankomatDto.getBankId());
        if (!optionalBank.isPresent())
            return new ApiResponse("bank not found by id", false);

        Optional<User> optionalUser = userRepository.findById(bankomatDto.getUserId());
        if (!optionalUser.isPresent())
            return new ApiResponse("Remote employee not found by id ", false);
        User bankomatUser = optionalUser.get();


        Bankomat newBankomat = new Bankomat();
        newBankomat.setAddress(optionalAddress.get());
        newBankomat.setBank(optionalBank.get());
        newBankomat.setCardType(optionalCardType.get());

        Integer userBankomat = this.userType.getUserType(bankomatUser);
        if (userBankomat!=3)
            return new ApiResponse("banlomat user must be remote employee",false);

        newBankomat.setUser(bankomatUser);
        newBankomat.setTotalAmountMoney(bankomatDto.getTotalAmountMoney());
        newBankomat.setMaxAmountMoney(bankomatDto.getMaxAmountMoney());
        newBankomat.setMinAmountMoney(bankomatDto.getMinAmountMoney());
        bankomatRepository.save(newBankomat);
        return new ApiResponse("bankomat saved", true);
    }


    public ApiResponse edit(Integer id, BankomatDto bankomatDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        boolean isOfficeEmployee = false;

        for (GrantedAuthority authority : currentUser.getAuthorities()) {
            if (authority.getAuthority().equals(RoleName.ROLE_OFFICE_EMPLOYEE.name())) {
                isOfficeEmployee = true;
                break;
            }
        }

        if (!isOfficeEmployee)
            return new ApiResponse("only office employee can edit bankomat", false);


        Optional<Bankomat> optionalBankomat = bankomatRepository.findById(id);
        if (!optionalBankomat.isPresent())
            return new ApiResponse("bankomat not found by id", false);


        Optional<CardType> optionalCardType = cardTypeRepository.findById(bankomatDto.getCardTypeId());
        if (!optionalCardType.isPresent())
            return new ApiResponse("card type not found", false);
        CardType cardType = optionalCardType.get();

        Optional<Address> optionalAddress = addressRepository.findById(bankomatDto.getAddressId());
        if (!optionalAddress.isPresent())
            return new ApiResponse("address not found by id", false);
        Address address = optionalAddress.get();


        boolean existsByCardTypeAndAddressAndIdNot = bankomatRepository.existsByCardTypeAndAddressAndIdNot(cardType, address, id);
        if (existsByCardTypeAndAddressAndIdNot)
            return new ApiResponse("such bankomat in this address already exists", false);

        Optional<Bank> optionalBank = bankRepository.findById(bankomatDto.getBankId());
        if (!optionalBank.isPresent())
            return new ApiResponse("bank not found by id", false);
        Bank bank = optionalBank.get();

        Optional<User> optionalUser = userRepository.findById(bankomatDto.getUserId());
        if (!optionalUser.isPresent())
            return new ApiResponse("user not found by id", false);

        User remoteEmployee = optionalUser.get();

        boolean isRemoteEmployee = false;
        for (GrantedAuthority authority : remoteEmployee.getAuthorities()) {
            if (authority.getAuthority().equals(RoleName.ROLE_REMOTE_EMPLOYEE.name())) {
                isRemoteEmployee = true;
                break;
            }
        }
        if (!isRemoteEmployee)
            return new ApiResponse("bankomat employee must be remote", false);

        Bankomat bankomat = optionalBankomat.get();

        bankomat.setUser(remoteEmployee);
        bankomat.setBank(bank);
        bankomat.setAddress(address);
        bankomat.setCardType(cardType);
        bankomat.setTotalAmountMoney(bankomatDto.getTotalAmountMoney());
        bankomat.setMaxAmountMoney(bankomatDto.getMaxAmountMoney());
        bankomat.setMinAmountMoney(bankomatDto.getMinAmountMoney());
        bankomatRepository.save(bankomat);
        return new ApiResponse("bankomat saved", true);

    }

    public ApiResponse delete(Integer id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        Integer userType = this.userType.getUserType(currentUser);
        if (userType != 2)
            return new ApiResponse("only office employe can delete bankomat", false);
        try {
            bankomatRepository.deleteById(id);
            return new ApiResponse("deleted", true);
        } catch (Exception e) {
            return new ApiResponse("error in delete", false);
        }
    }

    public Bankomat getOne(Integer id){
        Optional<Bankomat> optionalBankomat = bankomatRepository.findById(id);
        return optionalBankomat.orElse(null);
    }

    public List<Bankomat>getAllBankomatByBankId(Integer bankId){
        Optional<Bank> optionalBank = bankRepository.findById(bankId);
        return optionalBank.map(bank -> bankomatRepository.findAllByBank(bank)).orElse(null);
    }

    public void sendEmail(String employeeEmail,Integer bankomatId){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("test@pdp.com");
        mailMessage.setTo(employeeEmail);
        mailMessage.setSubject("Bankomat to'ldirilsin");
        mailMessage.setText(bankomatId+" -chi id li bankomatda 50.000 dan kam pul qolipti");
        javaMailSender.send(mailMessage);
    }
}