package uz.pdp.online.m6l6t2appbankomatcascadetypes.payload;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class RegisterDto {

    @NotNull
    @Size(min = 3,max = 50)
    private String firstName;


    //@Size(min = 3,max = 50,message = "lastName length 3>=length<=50")//(spring-boot-starter-validation=> shu biblatekani qosh buni ishlatishga JPA niki emas db ta'sir qimidi)
    @Length(min = 3,max = 50)//Hibernate niki size ham length ham database ga tegmidi faqat tekshiradi
    @NotNull
    private String lastName;

    @NotNull
    @Email
    private String email;

//    @NotNull(message = "role lar ni id si set collection da")
//    Set<Integer> role;

    @NotNull
    Integer roleId;

    @NotNull
    private String password;

}
