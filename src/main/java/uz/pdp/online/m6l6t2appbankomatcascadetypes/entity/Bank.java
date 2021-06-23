package uz.pdp.online.m6l6t2appbankomatcascadetypes.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.enums.BankName;


import javax.persistence.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Bank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private BankName bankName;

    @OneToMany(mappedBy = "bank",cascade = CascadeType.ALL)
    private List<Card> card;


}
