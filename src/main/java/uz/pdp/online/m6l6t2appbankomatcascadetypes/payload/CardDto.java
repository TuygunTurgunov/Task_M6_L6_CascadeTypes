package uz.pdp.online.m6l6t2appbankomatcascadetypes.payload;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.Bank;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.CardType;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.Client;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.UUID;

@Data
public class CardDto {

//    @NotNull(message = "card unique number not be null")
//    private Long cardNumber;

    @NotNull(message = "Bank id not be null")
    private Integer bankId;


    private Integer cvvCode;


    private UUID clientId;


    private Long expireDate;

    private Double amountMoney;


    private String password;


    private Integer cardTypeId;

}