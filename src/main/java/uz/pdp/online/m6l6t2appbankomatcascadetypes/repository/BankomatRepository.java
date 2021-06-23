package uz.pdp.online.m6l6t2appbankomatcascadetypes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.Address;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.Bank;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.Bankomat;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.CardType;

import java.util.List;

public interface BankomatRepository extends JpaRepository<Bankomat,Integer> {
    boolean existsByCardTypeAndAddress(CardType cardType, Address address);
    boolean existsByCardTypeAndAddressAndIdNot(CardType cardType, Address address, Integer id);
    List<Bankomat> findAllByBank(Bank bank);



}
