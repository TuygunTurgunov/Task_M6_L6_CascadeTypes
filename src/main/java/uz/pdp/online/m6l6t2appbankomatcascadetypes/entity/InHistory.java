package uz.pdp.online.m6l6t2appbankomatcascadetypes.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class InHistory {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    private Bankomat bankomat;

    @ManyToOne
    private Card card;

    private Integer moneyAmount;

    @CreationTimestamp
    private Timestamp createdAt;

}