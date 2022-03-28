
package br.com.segmedic.clubflex.model;

import java.io.Serializable;
import java.math.BigDecimal;
import br.com.segmedic.clubflex.support.NumberUtils;

public class DebtReportCreditCardDetail implements Serializable {

   private static final long serialVersionUID = 703960245454338644L;

   private Long holderId;
   private Long invoiceId;
   private String holderName;
   private Long subscriptionId;
   private BigDecimal amountDebt;
   private String cellphone;
   private String homephone;
   private String mail;
   private String paymentType;
   private String noCard;
   private Integer numTries;
   private String dateLastTry;
   private String lastReturnCode;
   private String brand;
   private String finalCard;

   public Long getInvoiceId() {
      return invoiceId;
   }

   public void setInvoiceId(Long invoiceId) {
      this.invoiceId = invoiceId;
   }

   public Integer getNumTries() {
      return numTries;
   }

   public void setNumTries(Integer numTries) {
      this.numTries = numTries;
   }

   public String getDateLastTry() {
      return dateLastTry;
   }

   public void setDateLastTry(String dateLastTry) {
      this.dateLastTry = dateLastTry;
   }

   public String getLastReturnCode() {
      return lastReturnCode;
   }

   public void setLastReturnCode(String lastReturnCode) {
      this.lastReturnCode = lastReturnCode;
   }

   public String getBrand() {
      return brand;
   }

   public void setBrand(String brand) {
      this.brand = brand;
   }

   public String getFinalCard() {
      return finalCard;
   }

   public void setFinalCard(String finalCard) {
      this.finalCard = finalCard;
   }

   public Long getHolderId() {
      return holderId;
   }

   public void setHolderId(Long holderId) {
      this.holderId = holderId;
   }

   public String getHolderName() {
      return holderName;
   }

   public void setHolderName(String holderName) {
      this.holderName = holderName;
   }

   public Long getSubscriptionId() {
      return subscriptionId;
   }

   public void setSubscriptionId(Long subscriptionId) {
      this.subscriptionId = subscriptionId;
   }

   public BigDecimal getAmountDebt() {
      return amountDebt;
   }

   public void setAmountDebt(BigDecimal amountDebt) {
      this.amountDebt = amountDebt;
   }

   public String getCellphone() {
      return cellphone;
   }

   public void setCellphone(String cellphone) {
      this.cellphone = cellphone;
   }

   public String getHomephone() {
      return homephone;
   }

   public void setHomephone(String homephone) {
      this.homephone = homephone;
   }

   public String getMail() {
      return mail;
   }

   public void setMail(String mail) {
      this.mail = mail;
   }

   public String getPaymentType() {
      return paymentType;
   }

   public void setPaymentType(String paymentType) {
      this.paymentType = paymentType;
   }

   public String getNoCard() {
      return noCard;
   }

   public void setNoCard(String noCard) {
      this.noCard = noCard;
   }

   public String getAmountDebtFmt() {
      return NumberUtils.formatMoney(this.amountDebt);
   }
}
