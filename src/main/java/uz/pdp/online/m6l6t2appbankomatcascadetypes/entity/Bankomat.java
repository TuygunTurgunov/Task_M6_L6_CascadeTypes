package uz.pdp.online.m6l6t2appbankomatcascadetypes.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Bankomat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private CardType cardType;

    @ManyToOne
    private Bank bank;

    @Column(nullable = false)
    private Integer maxAmountMoney;

    @Column(nullable = false)
    private Integer minAmountMoney;

    private Integer totalAmountMoney;

    @ManyToOne
    private Address address;

    @ManyToOne
    private User user;

}