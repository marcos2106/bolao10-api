
package br.com.segmedic.clubflex.model;

import java.io.Serializable;
import br.com.segmedic.clubflex.support.Strings;

public class Card implements Serializable {

   private static final long serialVersionUID = -3324858395112420778L;

   private String brand;
   private String number;
   private String validate;
   private String name;
   private String securityCode;
   private Long holderId;
   private Long idSubscription;
   private boolean choice;

   public String getBrand() {
      return brand;
   }

   public void setBrand(String brand) {
      this.brand = brand;
   }

   public String getNumber() {
      return Strings.removeNoNumericChars(number);
   }

   public void setNumber(String number) {
      this.number = number;
   }

   public String getValidate() {
      return validate;
   }

   public void setValidate(String validate) {
      this.validate = validate;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getSecurityCode() {
      return securityCode;
   }

   public void setSecurityCode(String securityCode) {
      this.securityCode = securityCode;
   }

   public Long getHolderId() {
      return holderId;
   }

   public void setHolderId(Long holderId) {
      this.holderId = holderId;
   }

   public String getValidateMonth() {
      if (this.validate != null) {
         try {
            return this.validate.split("/")[0];
         }
         catch (Exception e) {
            return null;
         }
      }
      return null;
   }

   public String getValidateYear() {
      if (this.validate != null) {
         try {
            return this.validate.split("/")[1];
         }
         catch (Exception e) {
            return null;
         }
      }
      return null;
   }

   /**
    * @return the choice
    */
   public boolean isChoice() {
      return choice;
   }

   /**
    * @param choice the choice to set
    */
   public void setChoice(boolean choice) {
      this.choice = choice;
   }

   /**
    * Recupera o valor do atributo idSubscription
    * 
    * @return o idSubscription
    */
   public Long getIdSubscription() {
      return idSubscription;
   }

   /**
    * Atribui o novo valor de idSubscription
    * 
    * @param idSubscription idSubscription que será atribuído
    */
   public void setIdSubscription(Long idSubscription) {
      this.idSubscription = idSubscription;
   }
}
