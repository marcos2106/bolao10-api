
package br.com.segmedic.clubflex.model;

import java.io.Serializable;

public class PetRequest implements Serializable {

   private static final long serialVersionUID = -3334320778L;

   private Long id;
   private String description;
   private Integer quantity;
   private Long subscriptionId;

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public Integer getQuantity() {
      return quantity;
   }

   public void setQuantity(Integer quantity) {
      this.quantity = quantity;
   }

   public Long getSubscriptionId() {
      return subscriptionId;
   }

   public void setSubscriptionId(Long subscriptionId) {
      this.subscriptionId = subscriptionId;
   }

}
