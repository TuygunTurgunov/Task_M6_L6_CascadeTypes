package uz.pdp.online.m6l6t2appbankomatcascadetypes.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.*;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.enums.BankName;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.enums.CardTypeName;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.enums.MoneyName;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.entity.enums.RoleName;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.repository.*;
import uz.pdp.online.m6l6t2appbankomatcascadetypes.service.AuthService;


import java.util.Collections;
import java.util.UUID;

/**
 * Project run bo'lishi bilan ishga tushadigan class FAQAT application.properties dagi
 *          ba'zi  buyruqlarga bog'lab qoyishimiz kerak , bu class ni faqat bir marta o'qishligi uchun
 */

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BankRepository bankRepository;

    @Autowired
    private BankNotesRepository bankNotesRepository;

    @Autowired
    private CardTypeRepository cardTypeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthService authService;



    /**
     * spring.sql.init.enabled ==>shuni  application propertiesdagi qiymatini opkeladi
     */
    @Value(value = "${spring.sql.init.enabled}")
    boolean runFirstWithDataSql;


    /**
     *spring.jpa.hibernate.ddl-auto ==>shuni  application propertiesdagi qiymatini opkeladi
     */
    @Value(value = "${spring.jpa.hibernate.ddl-auto}")
    String runDDL;

    @Override
    public void run(String... args) throws Exception {
        if(runDDL.equals("create")){

            Role roleDirector=new Role();
            roleDirector.setRoleName(RoleName.ROLE_DIRECTOR);
            roleRepository.save(roleDirector);
            Role roleManager=new Role();
            roleManager.setRoleName(RoleName.ROLE_OFFICE_EMPLOYEE);
            roleRepository.save(roleManager);
            Role roleWorker=new Role();
            roleWorker.setRoleName(RoleName.ROLE_REMOTE_EMPLOYEE);
            roleRepository.save(roleWorker);

            Role roleCard=new Role();
            roleCard.setRoleName(RoleName.ROLE_CARD);
            roleRepository.save(roleCard);

            Bank bankXalq=new Bank();
            bankXalq.setBankName(BankName.XALQ_BANK);
            bankRepository.save(bankXalq);

            Bank bankAsiaAlliance=new Bank();
            bankAsiaAlliance.setBankName(BankName.ASIA_ALLIANCE_BANK);
            bankRepository.save(bankAsiaAlliance);

            Bank bankAgro=new Bank();
            bankAgro.setBankName(BankName.AGRO_BANK);
            bankRepository.save(bankAgro);

            CardType uzCard=new CardType();
            uzCard.setCardTypeName(CardTypeName.UZCARD);
            cardTypeRepository.save(uzCard);

            CardType humo=new CardType();
            humo.setCardTypeName(CardTypeName.HUMO);
            cardTypeRepository.save(humo);

            CardType visa=new CardType();
            visa.setCardTypeName(CardTypeName.VISA);
            cardTypeRepository.save(visa);

            BankNotes birMing=new BankNotes();
            birMing.setMoneyName(MoneyName.BIR_MING);
            birMing.setMoneyIntType(1000);
            bankNotesRepository.save(birMing);

            BankNotes beshMing=new BankNotes();
            beshMing.setMoneyName(MoneyName.BESH_MING);
            beshMing.setMoneyIntType(5000);
            bankNotesRepository.save(beshMing);

            BankNotes onMing=new BankNotes();
            onMing.setMoneyName(MoneyName.ONG_MING);
            onMing.setMoneyIntType(10000);
            bankNotesRepository.save(onMing);

            BankNotes ellikMing=new BankNotes();
            ellikMing.setMoneyName(MoneyName.ELLIK_MING);
            ellikMing.setMoneyIntType(50000);
            bankNotesRepository.save(ellikMing);

            BankNotes yuzMing=new BankNotes();
            yuzMing.setMoneyName(MoneyName.YUZ_MING);
            yuzMing.setMoneyIntType(100000);
            bankNotesRepository.save(yuzMing);

            BankNotes oneDollar=new BankNotes();
            oneDollar.setMoneyName(MoneyName.ONE_DOLLAR);
            oneDollar.setMoneyIntType(1);
            bankNotesRepository.save(oneDollar);

            BankNotes fiveDollar=new BankNotes();
            fiveDollar.setMoneyName(MoneyName.FIVE_DOLLAR);
            fiveDollar.setMoneyIntType(5);
            bankNotesRepository.save(fiveDollar);

            BankNotes tenDollar=new BankNotes();
            tenDollar.setMoneyName(MoneyName.TEN_DOLLAR);
            tenDollar.setMoneyIntType(10);
            bankNotesRepository.save(tenDollar);

            BankNotes twentyDollar=new BankNotes();
            twentyDollar.setMoneyName(MoneyName.TWENTY_DOLLAR);
            twentyDollar.setMoneyIntType(20);
            bankNotesRepository.save(twentyDollar);

            BankNotes fiftyDollar=new BankNotes();
            fiftyDollar.setMoneyName(MoneyName.FIFTY_DOLLAR);
            fiftyDollar.setMoneyIntType(50);
            bankNotesRepository.save(fiftyDollar);

            BankNotes hundredDollar=new BankNotes();
            hundredDollar.setMoneyName(MoneyName.ONE_HUNDRED_DOLLAR);
            hundredDollar.setMoneyIntType(100);
            bankNotesRepository.save(hundredDollar);



        }


        if (runDDL.equals("create")){
            User director=new User();
            director.setFirstName("murod");
            director.setLastName("rasulov");
            director.setEmail("murodrasulov1467@gmail.com");
            director.setPassword(passwordEncoder.encode("1467"));
            director.setRoles(Collections.singleton(roleRepository.findByRoleName(RoleName.ROLE_DIRECTOR)));
            director.setEmailCode(UUID.randomUUID().toString());
            userRepository.save(director);//yoki Arrays.asList() qilib agar ko'p user bosa
            authService.sendEmail(director.getEmail(), director.getEmailCode());
        }
    }
}
