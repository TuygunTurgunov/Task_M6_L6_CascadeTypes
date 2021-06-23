package uz.pdp.online.m6l6t2appbankomatcascadetypes.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;
//@GeneratedValue(generator = "UUID")
//@GenericGenerator(name = "UUID",strategy = "org.hibernate.id.UUIDGenerator",
//        parameters = {
//                @org.hibernate.annotations.Parameter(
//                        name = "uuid_gen_strategy_class",
//                        value = "org.hibernate.id.uuid.CustomVersionOneStrategy"
//                )
//        })
//@Column(name = "id",updatable = false,nullable = false)

//@GeneratedValue(generator = "system-uuid")
//@GenericGenerator(name = "system-uuid",strategy = "uuid")
//@Column(name = "id",unique = true)

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Client {
    //    @Id
//    @Type(type = "org.hibernate.type.PostgresUUIDType")
//    @GeneratedValue(generator = "uuid")
//    @GenericGenerator(name = "uuid",strategy = "org.hibernate.id.UUIDGenerator")
//    private UUID id;
//    @Id
//    @Type(type = "org.hibernate.type.PostgresUUIDType")
//    @GeneratedValue(generator = "uuid")
//    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")

//    @Id
//    @GenericGenerator(name = "uuid",strategy = "uuid2")
//    @GeneratedValue(generator = "uuid")
//    @Column(name = "id",unique = true,nullable = false)
//    @Type(type = "pg-uuid")
//    private UUID id;

    //    @Id
//    @Type(type = "org.hibernate.type.PostgresUUIDType")
//    @GeneratedValue(generator = "uuid")
//    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
//    private UUID id;
    @Id
    @GeneratedValue
    private UUID id;

    private String fullName;

    @Column(unique = true)
    private String passportSer;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Card> card;

}
