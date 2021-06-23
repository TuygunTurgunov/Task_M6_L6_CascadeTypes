package uz.pdp.online.m6l6t2appbankomatcascadetypes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.Card;

import java.util.Optional;
import java.util.UUID;

public interface CardRepository extends JpaRepository<Card, UUID> {
    boolean existsByCardNumber(Long cardNumber);
    Optional<Card> findByCardNumber(Long cardNumber);


}
