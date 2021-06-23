package uz.pdp.online.m6l6t2appbankomatcascadetypes.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.User;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.enums.RoleName;

@Service
public class UserType {

    public Integer getUserType(User user) {

        for (GrantedAuthority authority : user.getAuthorities()) {
            if (authority.getAuthority().equals(RoleName.ROLE_DIRECTOR.name())) {
                return 1;
            }
            if (authority.getAuthority().equals(RoleName.ROLE_OFFICE_EMPLOYEE.name())) {
                return 2;
            }
            if (authority.getAuthority().equals(RoleName.ROLE_REMOTE_EMPLOYEE.name())) {
                return 3;
            }
            if (authority.getAuthority().equals(RoleName.ROLE_CARD.name())){
                return 4;
            }
        }
        return 0;

    }


}
