package uz.pdp.online.m6l6t2appbankomatcascadetypes.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.Card;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.User;


import java.util.Optional;
import java.util.UUID;

//UUID ==> user id type
public class AuditingByWhoIdFirst implements AuditorAware<UUID> {
    @Override
    public Optional<UUID> getCurrentAuditor() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null
                && authentication.isAuthenticated()
                && !authentication.getPrincipal().equals("anonymousUser")
        ) {
            UUID id=null;
            boolean isUser;

            try {
                User user = (User) authentication.getPrincipal();
//                isUser=true;
                id=user.getId();

            } catch (Exception e) {

            }

            try {
                 Card card = (Card) authentication.getPrincipal();
                 id=card.getId();

            } catch (Exception e) {

            }

            assert id != null;
            return Optional.of(id);

        }
        return Optional.empty();
    }
}
