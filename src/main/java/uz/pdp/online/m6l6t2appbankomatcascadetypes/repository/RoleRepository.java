package uz.pdp.online.m6l6t2appbankomatcascadetypes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.Role;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.enums.RoleName;

public interface RoleRepository extends JpaRepository<Role,Integer> {
    Role findByRoleName(RoleName roleName);
}
