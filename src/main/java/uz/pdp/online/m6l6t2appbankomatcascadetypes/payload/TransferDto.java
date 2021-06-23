package uz.pdp.online.m6l6t2appbankomatcascadetypes.payload;

import lombok.Data;

@Data
public class TransferDto {

    private Integer bankomatId;
//    private Long login;
//    private String password;
    private Integer moneyAmount;

}
