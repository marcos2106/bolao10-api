
package br.com.segmedic.clubflex.model;

import java.io.Serializable;
import java.util.List;
import br.com.segmedic.clubflex.domain.Dependent;
import br.com.segmedic.clubflex.domain.User;

public class AddDependentRequest implements Serializable {

   private static final long serialVersionUID = 1096697896985156490L;

   private Long subscriptionId;
   private List<Dependent> dependents;
   User user;

   public Long getSubscriptionId() {
      return subscriptionId;
   }

   public void setSubscriptionId(Long subscriptionId) {
      this.subscriptionId = subscriptionId;
   }

   public List<Dependent> getDependents() {
      return dependents;
   }

   public void setDependents(List<Dependent> dependents) {
      this.dependents = dependents;
   }

   /**
    * Recupera o valor do atributo user
    * 
    * @return o user
    */
   public User getUser() {
      return user;
   }

   /**
    * Atribui o novo valor de user
    * 
    * @param user user que será atribuído
    */
   public void setUser(User user) {
      this.user = user;
   }

}
