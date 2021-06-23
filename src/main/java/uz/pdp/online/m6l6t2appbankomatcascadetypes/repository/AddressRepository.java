package uz.pdp.online.m6l6t2appbankomatcascadetypes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.Address;

@RepositoryRestResource(path = "address")
public interface AddressRepository extends JpaRepository<Address,Integer> {


}
