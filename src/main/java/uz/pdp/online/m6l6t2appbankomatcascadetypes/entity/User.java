package uz.pdp.online.m6l6t2appbankomatcascadetypes.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false,length = 50)
    private String firstName;

    @Column(nullable = false,length = 50)
    private String lastName;


    @Column(unique = true,nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false,updatable = false)//==> admin update qila olmaydi
    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updateAt;

    @ManyToMany
    private Set<Role> roles;

    private Boolean accountNonExpired =true;//Bu userning amal qilish muddati o'tmagan

    private Boolean accountNonLocked =true;//Bu account block lanmagan.

    private Boolean credentialsNonExpired =true;//Bu account ishonchliligining muddati o'tmagan.

    private Boolean enabled =false;//Bu account email orqali  tasdiqlanganodan keyin true qilib qo'yamiz.

    private String emailCode;




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
        return this.email;// email field ni beriladi
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