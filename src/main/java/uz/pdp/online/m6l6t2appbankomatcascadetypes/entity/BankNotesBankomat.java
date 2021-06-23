package uz.pdp.online.m6l6t2appbankomatcascadetypes.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)//table ga bo'layotgan o'zgarishlarni kuzatib turadi( configuratsiya ham qilish kere config package da)
public class BankNotesBankomat {
    @Id
    @GeneratedValue
    private UUID id ;


    @ManyToOne(optional = false)
    private Bankomat bankomat;

    @ManyToOne(optional = false)
    private BankNotes bankNotes ;

    @Column(nullable = false)
    private Integer quantity;

    @CreatedBy
    private UUID createdBy;

    @LastModifiedBy
    private UUID updatedBy;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

}