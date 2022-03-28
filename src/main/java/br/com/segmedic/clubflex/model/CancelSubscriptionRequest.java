
package br.com.segmedic.clubflex.model;

import java.io.Serializable;

public class CancelSubscriptionRequest implements Serializable {

   private static final long serialVersionUID = 1876052718618671640L;

   private Long idreason;
   private String reasonCancel;
   private Boolean exemptFine;

   public Long getIdreason() {
      return idreason;
   }

   public void setIdreason(Long idreason) {
      this.idreason = idreason;
   }

   public String getReasonCancel() {
      return reasonCancel;
   }

   public void setReasonCancel(String reasonCancel) {
      this.reasonCancel = reasonCancel;
   }

   public Boolean getExemptFine() {
      return exemptFine;
   }

   public void setExemptFine(Boolean exemptFine) {
      this.exemptFine = exemptFine;
   }
}
