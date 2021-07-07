package uz.pdp.online.m6l6t2appbankomatcascadetypes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.Bankomat;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.Card;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.OutHistory;

import java.util.List;
import java.util.UUID;

public interface OutHistoryRepository extends JpaRepository<OutHistory, UUID> {
    List<OutHistory>findAllByBankomat( Bankomat bankomat);


}
