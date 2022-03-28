
package br.com.segmedic.clubflex.model;

import java.io.Serializable;
import java.util.List;
import br.com.segmedic.clubflex.domain.Dependent;
import br.com.segmedic.clubflex.domain.enums.SubscriptionStatus;
import br.com.segmedic.clubflex.support.Strings;

public class HolderFarma implements Serializable {

   private static final long serialVersionUID = -2440650018585566905L;

   private Long id;
   private String name;
   private String cpfCnpj;
   private Boolean isHolder;
   private Long idSubscription;
   private Long idClubcard;
   private SubscriptionStatus status;
   private List<Dependent> listDependents;

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public String getName() {
      if (name != null) {
         return name.toUpperCase();
      }
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getCpfCnpj() {
      return Strings.removeNoNumericChars(cpfCnpj);
   }

   public void setCpfCnpj(String cpfCnpj) {
      this.cpfCnpj = cpfCnpj;
   }

   public Boolean getIsHolder() {
      return isHolder;
   }

   public void setIsHolder(Boolean isHolder) {
      this.isHolder = isHolder;
   }

   public List<Dependent> getListDependents() {
      return listDependents;
   }

   public void setListDependents(List<Dependent> listDependents) {
      this.listDependents = listDependents;
   }

   public Long getIdSubscription() {
      return idSubscription;
   }

   public void setIdSubscription(Long idSubscription) {
      this.idSubscription = idSubscription;
   }

   public Long getIdClubcard() {
      return idClubcard;
   }

   public void setIdClubcard(Long idClubcard) {
      this.idClubcard = idClubcard;
   }

   public String getCpfCnpjFmt() {
      if (this.cpfCnpj != null) {
         if (this.cpfCnpj.length() > 11) {
            return Strings.formatCNPJ(this.cpfCnpj);
         }
         else {
            return Strings.formatCPF(this.cpfCnpj);
         }
      }
      return null;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      HolderFarma other = (HolderFarma) obj;
      if (id == null) {
         if (other.id != null)
            return false;
      }
      else if (!id.equals(other.id))
         return false;
      return true;
   }

   /**
    * @return the status
    */
   public SubscriptionStatus getStatus() {
      return status;
   }

   /**
    * @param status the status to set
    */
   public void setStatus(SubscriptionStatus status) {
      this.status = status;
   }

}
