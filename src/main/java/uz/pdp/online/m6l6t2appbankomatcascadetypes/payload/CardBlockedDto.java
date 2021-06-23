package uz.pdp.online.m6l6t2appbankomatcascadetypes.payload;

import lombok.Data;

@Data
public class CardBlockedDto {

    private Long cardNumber;
    private Boolean blocked;

}
