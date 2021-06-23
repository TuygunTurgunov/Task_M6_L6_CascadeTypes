package uz.pdp.online.m6l6t2appbankomatcascadetypes.service.innerService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.BankNotesBankomat;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.Bankomat;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.enums.MoneyName;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.repository.BankNotesBankomatRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MoneyTransferService {
   @Autowired
    BankNotesBankomatRepository bankNotesBankomatRepository;

   public Integer getBankNoteAndQuantity(Bankomat bankomat, MoneyName moneyName){
       Optional<BankNotesBankomat> optionalBankNotesBankomat = bankNotesBankomatRepository.findByBankomatAndBankNotes_MoneyName(bankomat, moneyName);
       if (!optionalBankNotesBankomat.isPresent())
           return null;
       BankNotesBankomat bankNotesBankomat = optionalBankNotesBankomat.get();
       return bankNotesBankomat.getQuantity();


   }
   public void setBankNoteQuantity(Bankomat bankomat , MoneyName moneyName,Integer quantity){
       Optional<BankNotesBankomat> optionalBankNotesBankomat = bankNotesBankomatRepository.findByBankomatAndBankNotes_MoneyName(bankomat, moneyName);
       if (optionalBankNotesBankomat.isPresent()){
           BankNotesBankomat bankNotesBankomat = optionalBankNotesBankomat.get();
           bankNotesBankomat.setQuantity(bankNotesBankomat.getQuantity()-quantity);
           bankNotesBankomatRepository.save(bankNotesBankomat);
       }

   }
}