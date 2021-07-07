package uz.pdp.online.m6l6t2appbankomatcascadetypes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.BankNotesBankomat;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.Bankomat;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.enums.MoneyName;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BankNotesBankomatRepository extends JpaRepository<BankNotesBankomat, UUID> {

//    Optinal<BankNotesBankomat>findByBankomat()
    List<BankNotesBankomat> findAllByBankomat(Bankomat bankomat);

    Optional<BankNotesBankomat>findByBankomatAndBankNotes_MoneyName(Bankomat bankomat, MoneyName bankNotes_moneyName);





}
