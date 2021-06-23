package uz.pdp.online.m6l6t2appbankomatcascadetypes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.Client;

import java.util.UUID;

@RepositoryRestResource(path = "client")
public interface ClientRepository extends JpaRepository<Client, UUID> {

}
