
package br.com.segmedic.clubflex.domain;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;
import br.com.segmedic.clubflex.domain.enums.PetEnum;

@Entity
@Table(name = "pet")
public class Pet implements Serializable {

   private static final long serialVersionUID = -8999902326148958223L;

   @Id
   @Column(name = "idpet", nullable = false, columnDefinition = "BIGINT(20)")
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @ManyToOne(optional = false, fetch = FetchType.LAZY)
   @JoinColumn(name = "idsubscription", nullable = false)
   @JsonIgnore
   private Subscription subscription;

   @Column(name = "quantity", nullable = false, columnDefinition = "INT(3)")
   private Integer quantity;

   @Enumerated(EnumType.STRING)
   @Column(name = "pet", nullable = true, columnDefinition = "ENUM('DOGS','CATS','BIRDS','RODENTS','ARACHNIDS','OTHERS') default 'DOGS'")
   private PetEnum pet;

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public Subscription getSubscription() {
      return subscription;
   }

   public void setSubscription(Subscription subscription) {
      this.subscription = subscription;
   }

   public Integer getQuantity() {
      return quantity;
   }

   public void setQuantity(Integer quantity) {
      this.quantity = quantity;
   }

   public PetEnum getPet() {
      return pet;
   }

   public void setPet(PetEnum pet) {
      this.pet = pet;
   }

}
