
package br.com.segmedic.clubflex.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import br.com.segmedic.clubflex.domain.enums.InvoiceType;
import br.com.segmedic.clubflex.domain.enums.PaymentType;
import br.com.segmedic.clubflex.support.NumberUtils;

public class ReceiptDetail implements Serializable {

   private static final long serialVersionUID = -35452348680243399L;

   private Long idSubscription;
   private Long idInvoice;
   private String holderName;
   private PaymentType subsPaymentType;
   @DateTimeFormat(pattern = "dd/MM/yyyy")
   private LocalDate paymentDate;
   private String planName;
   private PaymentType paymentType;
   private BigDecimal payAmount;
   private InvoiceType invoiceType;
   private String cellphone;
   private String neighborhood;
   private String zipCode;
   private BigDecimal payAmountTotal;

   public ReceiptDetail() {
   }

   public ReceiptDetail(Long idSubs, String nomeHolder, String nomePlano, PaymentType subsPaymentType, Long idInvoice,
      InvoiceType invoiceType,
      PaymentType paymentType, BigDecimal payAmount, LocalDate paymentDate, String cellhpone, String bairro, String zipCode) {
      setIdSubscription(idSubs);
      setIdInvoice(idInvoice);
      setHolderName(nomeHolder);
      setPlanName(nomePlano);
      setSubsPaymentType(subsPaymentType);
      setInvoiceType(invoiceType);
      setPaymentType(paymentType);
      setPayAmount(payAmount);
      setPaymentDate(paymentDate);
      setCellphone(cellhpone);
      setNeighborhood(bairro);
      setZipCode(zipCode);
   }

   public ReceiptDetail(Long idSubs, String nomeHolder, String nomePlano, PaymentType subsPaymentType, Long idInvoice,
      InvoiceType invoiceType,
      PaymentType paymentType, BigDecimal payAmount, LocalDate paymentDate, String cellhpone, String bairro, String zipCode,
      BigDecimal payAmountTotal) {
      setIdSubscription(idSubs);
      setIdInvoice(idInvoice);
      setHolderName(nomeHolder);
      setPlanName(nomePlano);
      setSubsPaymentType(subsPaymentType);
      setInvoiceType(invoiceType);
      setPaymentType(paymentType);
      setPayAmount(payAmount);
      setPaymentDate(paymentDate);
      setCellphone(cellhpone);
      setNeighborhood(bairro);
      setZipCode(zipCode);
      setPayAmountTotal(payAmountTotal);
   }

   @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
   public LocalDate getPaymentDate() {
      return paymentDate;
   }

   public void setPaymentDate(LocalDate paymentDate) {
      this.paymentDate = paymentDate;
   }

   public Long getIdSubscription() {
      return idSubscription;
   }

   public void setIdSubscription(Long idSubscription) {
      this.idSubscription = idSubscription;
   }

   public Long getIdInvoice() {
      return idInvoice;
   }

   public void setIdInvoice(Long idInvoice) {
      this.idInvoice = idInvoice;
   }

   public String getHolderName() {
      return holderName;
   }

   public void setHolderName(String holderName) {
      this.holderName = holderName;
   }

   public PaymentType getSubsPaymentType() {
      return subsPaymentType;
   }

   public void setSubsPaymentType(PaymentType subsPaymentType) {
      this.subsPaymentType = subsPaymentType;
   }

   public String getPlanName() {
      return planName;
   }

   public void setPlanName(String planName) {
      this.planName = planName;
   }

   public PaymentType getPaymentType() {
      return paymentType;
   }

   public void setPaymentType(PaymentType paymentType) {
      this.paymentType = paymentType;
   }

   public BigDecimal getPayAmount() {
      return payAmount;
   }

   public void setPayAmount(BigDecimal payAmount) {
      this.payAmount = payAmount;
   }

   public InvoiceType getInvoiceType() {
      return invoiceType;
   }

   public void setInvoiceType(InvoiceType invoiceType) {
      this.invoiceType = invoiceType;
   }

   public String getCellphone() {
      return cellphone;
   }

   public void setCellphone(String cellphone) {
      this.cellphone = cellphone;
   }

   public String getNeighborhood() {
      return neighborhood;
   }

   public void setNeighborhood(String neighborhood) {
      this.neighborhood = neighborhood;
   }

   public String getZipCode() {
      return zipCode;
   }

   public void setZipCode(String zipCode) {
      this.zipCode = zipCode;
   }

   public String getAmountPaidFmt() {
      return NumberUtils.formatMoney(this.getPayAmount());
   }

   /**
    * Recupera o valor do atributo payAmountTotal
    * 
    * @return o payAmountTotal
    */
   public BigDecimal getPayAmountTotal() {
      return payAmountTotal;
   }

   /**
    * Atribui o novo valor de payAmountTotal
    * 
    * @param payAmountTotal payAmountTotal que será atribuído
    */
   public void setPayAmountTotal(BigDecimal payAmountTotal) {
      this.payAmountTotal = payAmountTotal;
   }

}
