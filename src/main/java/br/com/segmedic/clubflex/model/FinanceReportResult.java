
package br.com.segmedic.clubflex.model;

import java.io.Serializable;
import java.math.BigDecimal;
import br.com.segmedic.clubflex.excel.ExcelField;
import br.com.segmedic.clubflex.support.NumberUtils;

public class FinanceReportResult implements Serializable {

   private static final long serialVersionUID = 3436645639361078692L;

   private String subscription;
   private String holderName;
   private String cellphone;
   private String homephone;
   private String registry;
   private String firstPayment;
   private String totalLife;
   private String planName;
   private String paymentType;
   private String price;
   private String user;
   private String profile;
   private String place;
   private String broker;
   private String status;
   private String noCard;

   @ExcelField(nome = "Assinatura", posicao = 0)
   public String getSubscription() {
      return subscription;
   }

   public void setSubscription(String subscription) {
      this.subscription = subscription;
   }

   @ExcelField(nome = "Titular", posicao = 1)
   public String getHolderName() {
      return holderName;
   }

   public void setHolderName(String holderName) {
      this.holderName = holderName;
   }

   @ExcelField(nome = "Celular", posicao = 2)
   public String getCellphone() {
      return cellphone;
   }

   public void setCellphone(String cellphone) {
      this.cellphone = cellphone;
   }

   @ExcelField(nome = "Telefone", posicao = 3)
   public String getHomephone() {
      return homephone;
   }

   public void setHomephone(String homephone) {
      this.homephone = homephone;
   }

   @ExcelField(nome = "Registro", posicao = 4)
   public String getRegistry() {
      return registry;
   }

   public void setRegistry(String registry) {
      this.registry = registry;
   }

   @ExcelField(nome = "Primeiro PGTO", posicao = 5)
   public String getFirstPayment() {
      return firstPayment;
   }

   public void setFirstPayment(String firstPayment) {
      this.firstPayment = firstPayment;
   }

   @ExcelField(nome = "Tot. Vidas", posicao = 6)
   public String getTotalLife() {
      return totalLife;
   }

   public void setTotalLife(String totalLife) {
      this.totalLife = totalLife;
   }

   @ExcelField(nome = "Plano", posicao = 7)
   public String getPlanName() {
      return planName;
   }

   public void setPlanName(String planName) {
      this.planName = planName;
   }

   @ExcelField(nome = "Tipo de PGTO", posicao = 8)
   public String getPaymentType() {
      return paymentType;
   }

   public void setPaymentType(String paymentType) {
      this.paymentType = paymentType;
   }

   @ExcelField(nome = "Cartão Informado", posicao = 9)
   public String getNoCard() {
      return noCard;
   }

   public void setNoCard(String noCard) {
      this.noCard = noCard;
   }

   @ExcelField(nome = "Preço", posicao = 10)
   public String getPrice() {
      if (this.price != null) {
         return NumberUtils.formatMoney(new BigDecimal(this.price));
      }
      else {
         return "0,00";
      }
   }

   public void setPrice(String price) {
      this.price = price;
   }

   @ExcelField(nome = "Assinante", posicao = 11)
   public String getUser() {
      return user;
   }

   public void setUser(String user) {
      this.user = user;
   }

   @ExcelField(nome = "Perfil Assinante", posicao = 12)
   public String getProfile() {
      return profile;
   }

   public void setProfile(String profile) {
      this.profile = profile;
   }

   @ExcelField(nome = "Local", posicao = 13)
   public String getPlace() {
      return place;
   }

   public void setPlace(String place) {
      this.place = place;
   }

   @ExcelField(nome = "Corretor", posicao = 14)
   public String getBroker() {
      return broker;
   }

   public void setBroker(String broker) {
      this.broker = broker;
   }

   @ExcelField(nome = "Status", posicao = 15)
   public String getStatus() {
      return status;
   }

   public void setStatus(String status) {
      this.status = status;
   }
}
