package uz.pdp.online.m6l6t2appbankomatcascadetypes.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Card implements UserDetails {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID",strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id",updatable = false,nullable = false)
    private UUID id;

    @Column(nullable = false,unique = true)
    private Long cardNumber;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    private Bank bank;

    private Integer cvvCode;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    private Client client;

    @Column(nullable = false)
    private Timestamp expireDate;

    @Column(nullable = false)
    private Double amountMoney;

    @Column(nullable = false)
    private String password;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    private CardType cardType;

    //blocklash uchun sanoq
    private Integer countChance;





    @ManyToMany
    private Set<Role> roles;

    private Boolean accountNonExpired =true;//Bu userning amal qilish muddati o'tmagan

    private Boolean accountNonLocked =true;//Bu account block lanmagan.

    private Boolean credentialsNonExpired =true;//Bu account ishonchliligining muddati o'tmagan.

    private Boolean enabled =true;//Bu account email orqali  tasdiqlanganodan keyin true qilib qo'yamiz.






//------- BU USERDETAILS NING METHOD LARI. MAJBURIY SISTEMADA USER SIFATIDA KORINISHI UCHUN


    //Bu USER ning huquqlari ro'yhati
    @Override// User ni role(lavozimi) va permission(huquq) larini beriladi
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles;
        //Role table da interface implement qilindi GrantedAuthority qilib breadidan
    }

    //USER ning USERNAME ni  qautaruvchi method
    @Override
    public String getUsername() {
        return this.cardNumber.toString();// unique field ni beriladi
    }

    @Override//BU Accountning amal qilish muddati o'tmaganligi qaytaruvchi method
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override//Bu Account bloklanmagan ligini qaytaruvchi method
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override//Bu Account sistemada ishonchliligini muddatini tugagan yoki tugamanligini qaytaradi
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    @Override//Bu accountning yoniq yoki o'chiq ligini qaytaradi
    public boolean isEnabled() {
        return this.enabled;
    }

}