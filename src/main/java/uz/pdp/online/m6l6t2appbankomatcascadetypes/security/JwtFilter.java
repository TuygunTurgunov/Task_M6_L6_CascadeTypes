package uz.pdp.online.m6l6t2appbankomatcascadetypes.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.Card;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.repository.CardRepository;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.service.AuthService;
//import uz.pdp.online.m6l5apphr.service.AuthService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    AuthService authService;

    @Autowired
    CardRepository cardRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {


        String authorization = httpServletRequest.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer")) {
            authorization = authorization.substring(7);
            String email = jwtProvider.getEmailFromToken(authorization);
            if (email != null) {
                UserDetails userDetails = authService.loadUserByUsername(email);

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                //Sistemaga shu user kirdi deb set qilindi
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        } else if (authorization != null && authorization.startsWith("Basic")) {
            String[] cardLoginAndPassword = getCardLoginAndPassword(authorization);
            Long cardNumber = Long.valueOf(cardLoginAndPassword[0]);

            Optional<Card> optionalCard = cardRepository.findByCardNumber(cardNumber);
            if (optionalCard.isPresent()) {
                Card card = optionalCard.get();
                String password = cardLoginAndPassword[1];
                UserDetails userDetails = authService.loadClientByCardNumber(cardNumber);
                if (passwordEncoder.matches(password, userDetails.getPassword())){
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    //Sistemaga shu user kirdi deb set qilindi
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);


                }

                if (passwordEncoder.matches(cardLoginAndPassword[1], card.getPassword())) {
                    card.setCountChance(0);
//                    card.setBlocked(false);
//                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//
//                    //Sistemaga shu user kirdi deb set qilindi
//                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
//

                } else if (card.getCountChance() < 2) {
                    card.setCountChance(card.getCountChance() + 1);
                } else {
                    card.setAccountNonLocked(false);
                }
                cardRepository.save(card);

            }





        }


        //Spring ni ozini filtiri Biz yozgan filtrlaga tushmasa o'zinikiga tushadi
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    public String[] getCardLoginAndPassword(String authorization) {
        String base64Credentials = authorization.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        return credentials.split(":");

    }

}
