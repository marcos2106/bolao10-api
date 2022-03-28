
package br.com.segmedic.clubflex.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import br.com.segmedic.clubflex.domain.Dependent;
import br.com.segmedic.clubflex.domain.Holder;
import br.com.segmedic.clubflex.domain.enums.PaymentType;
import br.com.segmedic.clubflex.domain.enums.TypeSub;

public class CreateSubscriptionRequest implements Serializable {

   private static final long serialVersionUID = 4303721545631985132L;

   private Holder holder;
   private Card creditCard;
   private Long planId;
   private Integer dayForPayment;
   private PaymentType paymentType;
   private List<Dependent> dependents;
   private List<PetRequest> pets;
   private Long brokerId;
   private Long companyId;
   private Boolean holderOnlyResponsibleFinance;
   private TypeSub type;
   private Boolean acceptedContract = false;

   @DateTimeFormat(pattern = "dd/MM/yyyy")
   private LocalDate dateBegin;

   public Holder getHolder() {
      return holder;
   }

   public void setHolder(Holder holder) {
      this.holder = holder;
   }

   public Card getCreditCard() {
      return creditCard;
   }

   public void setCreditCard(Card creditCard) {
      this.creditCard = creditCard;
   }

   public Long getPlanId() {
      return planId;
   }

   public void setPlanId(Long planId) {
      this.planId = planId;
   }

   public Integer getDayForPayment() {
      return dayForPayment;
   }

   public void setDayForPayment(Integer dayForPayment) {
      this.dayForPayment = dayForPayment;
   }

   public PaymentType getPaymentType() {
      return paymentType;
   }

   public void setPaymentType(PaymentType paymentType) {
      this.paymentType = paymentType;
   }

   public List<Dependent> getDependents() {
      return dependents;
   }

   public void setDependents(List<Dependent> dependents) {
      this.dependents = dependents;
   }

   public List<PetRequest> getPets() {
      return pets;
   }

   public void setPets(List<PetRequest> pets) {
      this.pets = pets;
   }

   public Long getBrokerId() {
      return brokerId;
   }

   public void setBrokerId(Long brokerId) {
      this.brokerId = brokerId;
   }

   public Long getCompanyId() {
      return companyId;
   }

   public void setCompanyId(Long companyId) {
      this.companyId = companyId;
   }

   public Boolean getHolderOnlyResponsibleFinance() {
      return holderOnlyResponsibleFinance;
   }

   public void setHolderOnlyResponsibleFinance(Boolean holderOnlyResponsibleFinance) {
      this.holderOnlyResponsibleFinance = holderOnlyResponsibleFinance;
   }

   public TypeSub getType() {
      return type;
   }

   public void setType(TypeSub type) {
      this.type = type;
   }

   public Boolean getAcceptedContract() {
      return acceptedContract;
   }

   public void setAcceptedContract(Boolean acceptedContract) {
      this.acceptedContract = acceptedContract;
   }

   @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
   public LocalDate getDateBegin() {
      return dateBegin;
   }

   public void setDateBegin(LocalDate dateBegin) {
      this.dateBegin = dateBegin;
   }

}
