
package br.com.bolao.bolao10.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.com.bolao.bolao10.domain.enums.InvoiceStatus;
import br.com.bolao.bolao10.support.NumberUtils;

@Table(name = "invoice")
public class Invoice implements Serializable {

   private static final long serialVersionUID = 5306072557733586204L;

   @Id
   @Column(name = "idinvoice", nullable = false, columnDefinition = "BIGINT(20)")
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @DateTimeFormat(pattern = "dd/MM/yyyy")
   @Column(name = "data_begin", nullable = false, columnDefinition = "DATE")
   private LocalDate competenceBegin; // competencia inicial

   @DateTimeFormat(pattern = "dd/MM/yyyy")
   @Column(name = "data_end", nullable = false, columnDefinition = "DATE")
   private LocalDate competenceEnd; // competencia final

   @ManyToOne(cascade = CascadeType.MERGE, optional = false, fetch = FetchType.EAGER)
   @JoinColumn(name = "idsubscription", nullable = false)
   private Subscription subscription; // assinatura na qual a fatura pertence

   @Enumerated(EnumType.STRING)
   @Column(name = "status", nullable = false,
            columnDefinition = "ENUM('REFUNDED', 'CANCELLED','OPENED','PAID','GENERATING') default 'OPENED'")
   private InvoiceStatus status; // status

   @Column(name = "amount", nullable = false, columnDefinition = "DECIMAL(10,2)")
   private BigDecimal amount; // valor

   @Column(name = "pay_amount", nullable = true, columnDefinition = "DECIMAL(10,2)")
   private BigDecimal payAmount;

   @DateTimeFormat(pattern = "dd/MM/yyyy")
   @Column(name = "due_date", nullable = false, columnDefinition = "DATE")
   private LocalDate dueDate; // data de vencimento

   @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
   @Column(name = "payment_date", nullable = true, columnDefinition = "DATE")
   private LocalDate paymentDate; // data de pagamento

   @Column(name = "transact_gateway_id", nullable = true, columnDefinition = "VARCHAR(20)")
   private String transactId; // id da transacao no gateway de pgt

   @Column(name = "transact_nsu", nullable = true, columnDefinition = "VARCHAR(22)")
   private String transactNsu; // nsu da transacao no gateway de pgt

   @Column(name = "authorization_code", nullable = true, columnDefinition = "VARCHAR(10)")
   private String authorizationCode; // authorizationCode da transacao no gateway de pgt

   @Column(name = "url_ticket", nullable = true, columnDefinition = "VARCHAR(100)")
   private String urlTicket; // url do boleto quando o type for boleto e nao cartao

   @Column(name = "barcode_ticket", nullable = true, columnDefinition = "VARCHAR(100)")
   private String barcodeTicket; // codigo de barras do boleto

   @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
   @Column(name = "payment_cancel_date", nullable = true, columnDefinition = "DATETIME")
   private LocalDateTime paymentCancelDate; // data/hora de cancelamento de pagamento

   @Column(name = "iduser", nullable = true, columnDefinition = "BIGINT(20)")
   private Long userResponsiblePayment; // usuario responsável pelo estorno, cancelamento ou pagamneto da fatura

   @Column(name = "nfe_id", nullable = true, columnDefinition = "VARCHAR(60)")
   private String nfeId;

   @Column(name = "nfe_number", nullable = true, columnDefinition = "VARCHAR(20)")
   private String nfeNumber;

   @Column(name = "nfe_link_download", nullable = true, columnDefinition = "VARCHAR(255)")
   private String nfeDownload;

   @Column(name = "nfe_link_xml", nullable = true, columnDefinition = "VARCHAR(255)")
   private String nfeDownloadXml;

   @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
   @Column(name = "date_time_registered_log", nullable = true, columnDefinition = "DATETIME")
   private LocalDateTime dateTimeRegisteredLog;

   @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
   @Column(name = "date_time_registered_bol_simples_log", nullable = false, columnDefinition = "DATETIME")
   private LocalDateTime dateTimeRegisteredBolSimplesLog;

   @Column(name = "installmentNumber", nullable = true, columnDefinition = "INT(10)")
   private Integer installmentNumber; // número de parcelas que o acordo pode ter

   @Transient
   private String justificationDenied;

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
   public LocalDate getCompetenceBegin() {
      return competenceBegin;
   }

   public void setCompetenceBegin(LocalDate competenceBegin) {
      this.competenceBegin = competenceBegin;
   }

   @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
   public LocalDate getCompetenceEnd() {
      return competenceEnd;
   }

   public void setCompetenceEnd(LocalDate competenceEnd) {
      this.competenceEnd = competenceEnd;
   }

   public Subscription getSubscription() {
      return subscription;
   }

   public void setSubscription(Subscription subscription) {
      this.subscription = subscription;
   }

   public InvoiceStatus getStatus() {
      return status;
   }

   public void setStatus(InvoiceStatus status) {
      this.status = status;
   }

   public BigDecimal getAmount() {
//      if (!InvoiceStatus.PAID.equals(this.status) && isOutDate()) {
//         return NumberUtils.calculateTax(this.dueDate, this.amount);
//      }
      return amount;
   }

   public BigDecimal getOriginalAmount() {
      return amount;
   }

   public BigDecimal getAmountDiff() {
      return this.getAmount().subtract(this.getOriginalAmount());
   }

   public void setAmount(BigDecimal amount) {
      this.amount = amount;
   }

   @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
   public LocalDate getDueDate() {
      return dueDate;
   }

   public void setDueDate(LocalDate dueDate) {
      this.dueDate = dueDate;
   }

   @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
   public LocalDate getPaymentDate() {
      return paymentDate;
   }

   public void setPaymentDate(LocalDate paymentDate) {
      this.paymentDate = paymentDate;
   }

   @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
   public LocalDateTime getPaymentCancelDate() {
      return paymentCancelDate;
   }

   public void setPaymentCancelDate(LocalDateTime paymentCancelDate) {
      this.paymentCancelDate = paymentCancelDate;
   }

   public String getTransactId() {
      return transactId;
   }

   public void setTransactId(String transactId) {
      this.transactId = transactId;
   }

   public String getTransactNsu() {
      return transactNsu;
   }

   public void setTransactNsu(String transactNsu) {
      this.transactNsu = transactNsu;
   }

   public String getAuthorizationCode() {
      return authorizationCode;
   }

   public void setAuthorizationCode(String authorizationCode) {
      this.authorizationCode = authorizationCode;
   }

   public String getUrlTicket() {
      return urlTicket;
   }

   public void setUrlTicket(String urlTicket) {
      this.urlTicket = urlTicket;
   }

   public String getBarcodeTicket() {
      return barcodeTicket;
   }

   public void setBarcodeTicket(String barcodeTicket) {
      this.barcodeTicket = barcodeTicket;
   }

   public Long getUserResponsiblePayment() {
      return userResponsiblePayment;
   }

   public void setUserResponsiblePayment(Long userResponsiblePayment) {
      this.userResponsiblePayment = userResponsiblePayment;
   }

   public BigDecimal getPayAmount() {
      return payAmount;
   }

   public void setPayAmount(BigDecimal payAmount) {
      this.payAmount = payAmount;
   }

   public BigDecimal getPayAmountTax() {
      // Verificar se já foi pago, se há valor pago e se pagou vencido (depois da data
      // de vencimento)
      if (this.status.equals(InvoiceStatus.PAID) && this.getPayAmount() != null
         && this.dueDate.isBefore(this.paymentDate)) {
         BigDecimal amoutWithTax = BigDecimal.ZERO;// NumberUtils.calculateTax(this.dueDate, this.amount);
         return amoutWithTax.subtract(this.getOriginalAmount());
      }
      return BigDecimal.ZERO;
      /*
       * if(this.getPayAmount() != null) { return this.getPayAmount().subtract(this.getOriginalAmount()); } return BigDecimal.ZERO;
       */
   }

   public String getRefoundAmountFmt() {
      if (this.getPayAmount() != null) {
         BigDecimal refound = this.getPayAmount().subtract(this.getAmount()).subtract(getPayAmountTax());
         return NumberUtils.formatMoney(refound);
      }
      return NumberUtils.formatMoney(BigDecimal.ZERO);
   }

   public String getNfeId() {
      return nfeId;
   }

   public void setNfeId(String nfeId) {
      this.nfeId = nfeId;
   }

   public String getNfeNumber() {
      return nfeNumber;
   }

   public void setNfeNumber(String nfeNumber) {
      this.nfeNumber = nfeNumber;
   }

   public String getNfeDownload() {
      return nfeDownload;
   }

   public void setNfeDownload(String nfeDownload) {
      this.nfeDownload = nfeDownload;
   }

   public String getNfeDownloadXml() {
      return nfeDownloadXml;
   }

   public void setNfeDownloadXml(String nfeDownloadXml) {
      this.nfeDownloadXml = nfeDownloadXml;
   }

   @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
   public LocalDateTime getDateTimeRegisteredLog() {
      return dateTimeRegisteredLog;
   }

   public void setDateTimeRegisteredLog(LocalDateTime dateTimeRegisteredLog) {
      this.dateTimeRegisteredLog = dateTimeRegisteredLog;
   }

   @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
   public LocalDateTime getDateTimeRegisteredBolSimplesLog() {
      return dateTimeRegisteredBolSimplesLog;
   }

   public void setDateTimeRegisteredBolSimplesLog(LocalDateTime dateTimeRegisteredBolSimplesLog) {
      this.dateTimeRegisteredBolSimplesLog = dateTimeRegisteredBolSimplesLog;
   }

   public Integer getInstallmentNumber() {
      return installmentNumber;
   }

   public void setInstallmentNumber(Integer installmentNumber) {
      this.installmentNumber = installmentNumber;
   }

   public String getJustificationDenied() {
      return justificationDenied;
   }

   public void setJustificationDenied(String justificationDenied) {
      this.justificationDenied = justificationDenied;
   }

   // Generateds
   public String getAmountFmt() {
      return NumberUtils.formatMoney(this.getAmount());
   }

   public String getPayAmountFmt() {
      return NumberUtils.formatMoney(this.getPayAmount());
   }

   public String getPayAmountTaxFmt() {
      return NumberUtils.formatMoney(this.getPayAmountTax());
   }

   public String getOriginalAmountFmt() {
      return NumberUtils.formatMoney(this.getOriginalAmount());
   }

   public Boolean isOutDate() {
      if (this.dueDate != null && (this.dueDate.isAfter(LocalDate.now()) || this.dueDate.isEqual(LocalDate.now()))) {
         return false;
      }
      else {
         if (InvoiceStatus.OPENED.equals(this.status)) {
            return true;
         }
         else {
            return false;
         }
      }
   }

   public Long getDelayBoletoSimples() {
      if (this.dateTimeRegisteredLog != null && this.dateTimeRegisteredBolSimplesLog != null) {
         return ChronoUnit.SECONDS.between(this.dateTimeRegisteredLog, this.dateTimeRegisteredBolSimplesLog);
      }
      return 0L;
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
      Invoice other = (Invoice) obj;
      if (id == null) {
         if (other.id != null)
            return false;
      }
      else if (!id.equals(other.id))
         return false;
      return true;
   }

}
