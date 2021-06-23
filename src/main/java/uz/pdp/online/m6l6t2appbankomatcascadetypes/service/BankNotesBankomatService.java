package uz.pdp.online.m6l6t2appbankomatcascadetypes.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.BankNotes;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.BankNotesBankomat;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.Bankomat;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.User;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.payload.ApiResponse;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.payload.BNBDto;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.repository.BankNotesBankomatRepository;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.repository.BankNotesRepository;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.repository.BankomatRepository;

import java.util.Optional;
import java.util.UUID;

@Service
public class BankNotesBankomatService {
    @Autowired
    BankomatRepository bankomatRepository;

    @Autowired
    BankNotesRepository bankNotesRepository;

    @Autowired
    UserType userType;

    @Autowired
    BankNotesBankomatRepository bankNotesBankomatRepository;

    public ApiResponse add(BNBDto bnbDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        Integer userType = this.userType.getUserType(currentUser);
        if (userType != 3)
            return new ApiResponse("only remote employee can add bank notes to bankomat", false);

        Optional<Bankomat> optionalBankomat = bankomatRepository.findById(bnbDto.getBankomatId());
        if (!optionalBankomat.isPresent())
            return new ApiResponse("bankomat not found", false);
        Bankomat bankomat = optionalBankomat.get();

        User bankomatUser = bankomat.getUser();

        if (!bankomatUser.getEmail().equals(currentUser.getEmail()))
            return new ApiResponse("only bankomat user can add banknotes", false);



        Optional<BankNotes> optionalBankNotes = bankNotesRepository.findById(bnbDto.getBankNotesId());
        if (!optionalBankNotes.isPresent())
            return new ApiResponse("bankNotes not found by id", false);
        BankNotes bankNotes = optionalBankNotes.get();

        BankNotesBankomat bankNotesBankomat = new BankNotesBankomat();
        bankNotesBankomat.setBankNotes(bankNotes);
        bankNotesBankomat.setBankomat(bankomat);
        bankNotesBankomat.setQuantity(bnbDto.getQuantity());
        bankNotesBankomatRepository.save(bankNotesBankomat);
        return new ApiResponse("bank notes saved in bankomat", true);
    }

    public ApiResponse edit(UUID id,BNBDto bnbDto){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        Integer userType = this.userType.getUserType(currentUser);
        if (userType != 3)
            return new ApiResponse("only remote employee can add bank notes to bankomat", false);


        Optional<BankNotesBankomat> optionalBankNotesBankomat = bankNotesBankomatRepository.findById(id);
        if (!optionalBankNotesBankomat.isPresent())
            return new ApiResponse("BNB not found by id",false);


        Optional<Bankomat> optionalBankomat = bankomatRepository.findById(bnbDto.getBankomatId());
        if (!optionalBankomat.isPresent())
            return new ApiResponse("bankomat not found", false);
        Bankomat bankomat = optionalBankomat.get();


        User bankomatUser = bankomat.getUser();
        if (bankomatUser != currentUser)
            return new ApiResponse("only bankomat user can edit banknotes", false);

        Optional<BankNotes> optionalBankNotes = bankNotesRepository.findById(bnbDto.getBankNotesId());
        if (!optionalBankNotes.isPresent())
            return new ApiResponse("bankNotes not found by id", false);
        BankNotes bankNotes = optionalBankNotes.get();



        BankNotesBankomat bankNotesBankomat = optionalBankNotesBankomat.get();
        bankNotesBankomat.setBankomat(bankomat);
        bankNotesBankomat.setBankNotes(bankNotes);
        bankNotesBankomat.setQuantity(bnbDto.getQuantity());
        bankNotesBankomatRepository.save(bankNotesBankomat);
        return new ApiResponse("bank notes in bankomat edited",true);




    }



}