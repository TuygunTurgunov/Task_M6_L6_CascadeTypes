package uz.pdp.online.m6l6t2appbankomatcascadetypes.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.enums.CardTypeName;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.enums.RoleName;

import javax.persistence.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class CardType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private CardTypeName cardTypeName;

    @OneToMany(mappedBy = "cardType",cascade = CascadeType.ALL)
    private List<Card> card;

}
