package uz.pdp.online.m6l6t2appbankomatcascadetypes.service.innerService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
//import uz.pdp.online.m6l6t2appbankomatcascadetypes.component.ApiResponse;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.BankNotesBankomat;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.Bankomat;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.enums.MoneyName;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.repository.BankNotesBankomatRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MoneyTransferService {
   @Autowired
    BankNotesBankomatRepository bankNotesBankomatRepository;


    public HashMap<String,Integer> getBankNotesQuantity(Bankomat bankomat){

        List<BankNotesBankomat> allByBankomat = bankNotesBankomatRepository.findAllByBankomat(bankomat);
        HashMap<String,Integer>x=new HashMap<>();
        String allSummaStr="all";
        int allSummaInteger=0;

        for (BankNotesBankomat notesBankomat : allByBankomat) {
            x.put(notesBankomat.getBankNotes().getMoneyName().name(),notesBankomat.getQuantity());
        }
        allSummaInteger=x.get(MoneyName.YUZ_MING.name())*100_000+x.get(MoneyName.ELLIK_MING.name())*50_000+x.get(MoneyName.ONG_MING.name())*10_000+
                x.get(MoneyName.BESH_MING.name())*5_000+x.get(MoneyName.BIR_MING.name())*1_000;
        x.put("all",allSummaInteger);
        x.put("min",50_000);
        x.put("max",4_000_000);
        return x;
   }
   public void updateDataBase(HashMap <String,Integer> baza, Bankomat bankomat){
        List<BankNotesBankomat> allByBankomat = bankNotesBankomatRepository.findAllByBankomat(bankomat);

       for (BankNotesBankomat bankNotesBankomat : allByBankomat) {

           if (bankNotesBankomat.getBankNotes().getMoneyName().name().equals(MoneyName.YUZ_MING.name()))
               bankNotesBankomat.setQuantity(baza.get(MoneyName.YUZ_MING.name()));
           if (bankNotesBankomat.getBankNotes().getMoneyName().name().equals(MoneyName.ELLIK_MING.name()))
               bankNotesBankomat.setQuantity(baza.get(MoneyName.ELLIK_MING.name()));
           if (bankNotesBankomat.getBankNotes().getMoneyName().name().equals(MoneyName.ONG_MING.name()))
               bankNotesBankomat.setQuantity(baza.get(MoneyName.ONG_MING.name()));
           if (bankNotesBankomat.getBankNotes().getMoneyName().name().equals(MoneyName.BESH_MING.name()))
               bankNotesBankomat.setQuantity(baza.get(MoneyName.BESH_MING.name()));
           if (bankNotesBankomat.getBankNotes().getMoneyName().name().equals(MoneyName.BIR_MING.name()))
               bankNotesBankomat.setQuantity(baza.get(MoneyName.BIR_MING.name()));
       }
       bankNotesBankomatRepository.saveAll(allByBankomat);
    }


    public HashMap<String,Integer> getBankNotesQuantityDollar(Bankomat bankomat){

        List<BankNotesBankomat> allByBankomat = bankNotesBankomatRepository.findAllByBankomat(bankomat);
        HashMap<String,Integer>x=new HashMap<>();
        int allSummaInteger=0;
        for (BankNotesBankomat notesBankomat : allByBankomat) {
            x.put(notesBankomat.getBankNotes().getMoneyName().name(),notesBankomat.getQuantity());
        }

        allSummaInteger=x.get(MoneyName.ONE_HUNDRED_DOLLAR.name())*100+x.get(MoneyName.FIFTY_DOLLAR.name())*50+x.get(MoneyName.TWENTY_DOLLAR.name())*20+
                x.get(MoneyName.TEN_DOLLAR.name())*10+x.get(MoneyName.FIVE_DOLLAR.name())*5+x.get(MoneyName.ONE_DOLLAR.name());

        x.put("all",allSummaInteger);
        x.put("min",50);
        x.put("max",1000);
        return x;
    }
    public void updateDataBaseDollar(HashMap <String,Integer> baza, Bankomat bankomat){
        List<BankNotesBankomat> allByBankomat = bankNotesBankomatRepository.findAllByBankomat(bankomat);
        for (BankNotesBankomat bankNotesBankomat : allByBankomat) {
            if (bankNotesBankomat.getBankNotes().getMoneyName().name().equals(MoneyName.ONE_HUNDRED_DOLLAR.name()))
                bankNotesBankomat.setQuantity(baza.get(MoneyName.ONE_HUNDRED_DOLLAR.name()));
            if (bankNotesBankomat.getBankNotes().getMoneyName().name().equals(MoneyName.FIFTY_DOLLAR.name()))
                bankNotesBankomat.setQuantity(baza.get(MoneyName.FIFTY_DOLLAR.name()));
            if (bankNotesBankomat.getBankNotes().getMoneyName().name().equals(MoneyName.TWENTY_DOLLAR.name()))
                bankNotesBankomat.setQuantity(baza.get(MoneyName.TWENTY_DOLLAR.name()));
            if (bankNotesBankomat.getBankNotes().getMoneyName().name().equals(MoneyName.TEN_DOLLAR.name()))
                bankNotesBankomat.setQuantity(baza.get(MoneyName.TEN_DOLLAR.name()));
            if (bankNotesBankomat.getBankNotes().getMoneyName().name().equals(MoneyName.FIVE_DOLLAR.name()))
                bankNotesBankomat.setQuantity(baza.get(MoneyName.FIVE_DOLLAR.name()));
            if (bankNotesBankomat.getBankNotes().getMoneyName().name().equals(MoneyName.ONE_DOLLAR.name()))
                bankNotesBankomat.setQuantity(baza.get(MoneyName.ONE_DOLLAR.name()));
        }
        bankNotesBankomatRepository.saveAll(allByBankomat);
    }
}