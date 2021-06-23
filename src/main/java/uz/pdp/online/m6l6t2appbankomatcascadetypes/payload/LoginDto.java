package uz.pdp.online.m6l6t2appbankomatcascadetypes.payload;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {
//    @NotNull
    @Email
    private String username;

//    @NotNull
    private String password;

}
