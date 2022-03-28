
package br.com.segmedic.clubflex.model;

import java.io.Serializable;

public class CancelDependentRequest implements Serializable {

   private static final long serialVersionUID = 1876052718618671640L;

   private Long idsubscription;
   private Long iddependent;
   private String reasonCancel;
   private Long reason;
   private boolean immediateDependent;

   public Long getIdsubscription() {
      return idsubscription;
   }

   public void setIdsubscription(Long idsubscription) {
      this.idsubscription = idsubscription;
   }

   public Long getIddependent() {
      return iddependent;
   }

   public void setIddependent(Long iddependent) {
      this.iddependent = iddependent;
   }

   public String getReasonCancel() {
      return reasonCancel;
   }

   public void setReasonCancel(String reasonCancel) {
      this.reasonCancel = reasonCancel;
   }

   public Long getReason() {
      return reason;
   }

   public void setReason(Long reason) {
      this.reason = reason;
   }

   /**
    * Recupera o valor do atributo immediateDependent
    * 
    * @return o immediateDependent
    */
   public boolean isImmediateDependent() {
      return immediateDependent;
   }

   /**
    * Atribui o novo valor de immediateDependent
    * 
    * @param immediateDependent immediateDependent que será atribuído
    */
   public void setImmediateDependent(boolean immediateDependent) {
      this.immediateDependent = immediateDependent;
   }

}
