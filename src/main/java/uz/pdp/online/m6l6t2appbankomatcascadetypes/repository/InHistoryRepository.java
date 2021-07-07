package uz.pdp.online.m6l6t2appbankomatcascadetypes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.Bankomat;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.InHistory;

import java.util.List;
import java.util.UUID;

public interface InHistoryRepository extends JpaRepository<InHistory, UUID> {

    List<InHistory> findAllByBankomat(Bankomat bankomat);
}
