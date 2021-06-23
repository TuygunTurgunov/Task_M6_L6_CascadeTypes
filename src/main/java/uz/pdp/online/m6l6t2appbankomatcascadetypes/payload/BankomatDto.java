package uz.pdp.online.m6l6t2appbankomatcascadetypes.payload;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class BankomatDto {


    @NotNull
    private Integer cardTypeId;
    @NotNull
    private Integer bankId;
    @NotNull
    private Integer maxAmountMoney;
    @NotNull
    private Integer minAmountMoney;
    @NotNull
    private Integer totalAmountMoney;
    @NotNull
    private Integer addressId;
    @NotNull
    private UUID userId;

}