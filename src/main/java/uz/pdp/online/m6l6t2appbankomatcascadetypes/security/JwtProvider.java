package uz.pdp.online.m6l6t2appbankomatcascadetypes.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.Role;


import java.util.Date;
import java.util.Set;

@Component
public class JwtProvider {
    private final long expireTime = 1000 * 60 * 60 * 24 * 18; //1 kun * 7 kun
    private final String secretKey = "MaxfiySozHechKimBilmasin";


    public String generateToken(String username, Set<Role> roles) {
        Date expireDate = new Date(System.currentTimeMillis() + expireTime);
        String token = Jwts
                .builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .claim("roles", roles)
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
        return token;

    }

    public String getEmailFromToken(String token) {

        try {
            String email = Jwts
                    .parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
            return email;

        } catch (Exception e) {

            return null;

        }

    }


}
