
package br.com.bolao.bolao10.model;

import java.io.Serializable;
import java.time.LocalDate;

import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

public class SubscriptionDetail implements Serializable {

   private static final long serialVersionUID = -3545389088680243399L;

   private Long subscriptionId;
   private String holderName;
   private String status;
   @DateTimeFormat(pattern = "dd/MM/yyyy")
   private LocalDate dateBegin;
   private String amount;
   private Integer numberDependents;
   private String plan;
   private String log;
   private String dateLog;
   private String userResponsable;

   public Long getSubscriptionId() {
      return subscriptionId;
   }

   public void setSubscriptionId(Long subscriptionId) {
      this.subscriptionId = subscriptionId;
   }

   public String getHolderName() {
      return holderName;
   }

   public void setHolderName(String holderName) {
      this.holderName = holderName;
   }

   public String getStatus() {
      return status;
   }

   public void setStatus(String status) {
      this.status = status;
   }

   @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
   public LocalDate getDateBegin() {
      return dateBegin;
   }

   public void setDateBegin(LocalDate dateBegin) {
      this.dateBegin = dateBegin;
   }

   public String getUserResponsable() {
      if (StringUtils.isBlank(this.userResponsable)) {
         return "Sistema";
      }
      return userResponsable;
   }

   public void setUserResponsable(String userResponsable) {
      this.userResponsable = userResponsable;
   }

   public String getAmount() {
      return amount;
   }

   public void setAmount(String amount) {
      this.amount = amount;
   }

   public Integer getNumberDependents() {
      return numberDependents;
   }

   public void setNumberDependents(Integer numberDependents) {
      this.numberDependents = numberDependents;
   }

   public String getPlan() {
      return plan;
   }

   public void setPlan(String plan) {
      this.plan = plan;
   }

   public String getLog() {
      return log;
   }

   public void setLog(String log) {
      this.log = log;
   }

   public String getDateLog() {
      return dateLog;
   }

   public void setDateLog(String dateLog) {
      this.dateLog = dateLog;
   }

}
