
package br.com.bolao.bolao10.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.com.bolao.bolao10.domain.enums.SubscriptionStatus;

@Table(name = "subscription")
public class Subscription implements Serializable {

   private static final long serialVersionUID = -3043349472882681676L;

   @Id
   @Column(name = "idsubscription", nullable = false, columnDefinition = "BIGINT(20)")
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(name = "waiting_first_pay", nullable = true, columnDefinition = "CHAR(1)")
   private Boolean waitingFirstPay = true; // aguardando primeiro pagamento?

   @Enumerated(EnumType.STRING)
   @Column(name = "status", nullable = false,
            columnDefinition = "ENUM('CANCELED','OK','BLOCKED','OUT_OF_DATE','REQUESTED_CARD') default 'OK'")
   private SubscriptionStatus status; // status

   @DateTimeFormat(pattern = "dd/MM/yyyy")
   @Column(name = "data_begin", nullable = false, columnDefinition = "DATE")
   private LocalDate dateBegin; // data inicio de vigencia da assinatura

   @Column(name = "day_to_pay", nullable = false, columnDefinition = "SMALLINT(2)")
   private Integer dayForPayment; // dia acordado de pagamento de fatura

   @Column(name = "update_at_payday", nullable = true, columnDefinition = "DATETIME")
   private LocalDateTime updateAtPayDay; // ultima data de alteracao de dia de pagamento

   @ManyToOne(cascade = CascadeType.MERGE, optional = false, fetch = FetchType.EAGER)
   @JoinColumn(name = "idholder", nullable = false)
   private Holder holder; // titular

   @DateTimeFormat(pattern = "dd/MM/yyyy")
   @Column(name = "date_registred", nullable = false, columnDefinition = "DATE")
   private LocalDate dateOfRegistry; // data de registro

   @Column(name = "reason_cancellation", nullable = true, columnDefinition = "VARCHAR(255)")
   private String reasonCancellation; // motivo do cancelamento

   @Column(name = "date_cancellation", nullable = true, columnDefinition = "DATE")
   private LocalDate dateOfCancellation; // data de cancelamento

   @Column(name = "date_blocked", nullable = true, columnDefinition = "DATETIME")
   private LocalDateTime dateBlocked; // data de bloqueio

   @Column(name = "date_request_cancellation", nullable = true, columnDefinition = "DATETIME")
   private LocalDateTime dateRequestOfCancellation; // data de solciitação do cancelamento

   @Column(name = "date_last_competence", nullable = true, columnDefinition = "INT(4)")
   private Integer dateLastCompetence; // data da ultima competencia processada para a assinatura

   @Column(name = "days_to_block", nullable = true)
   private Long daysToBlock; // Quantidade de dias para bloquear assinatura apos vencimento
   // Agora a quantidade de dias é obtido pela configuração do plano da assiantura

   @ManyToOne(cascade = CascadeType.MERGE, optional = true, fetch = FetchType.EAGER)
   @JoinColumn(name = "iduser", nullable = true)
   private User user;

   @Column(name = "holder_only_reponsible_financial", nullable = false, columnDefinition = "CHAR(1) default '0'")
   private Boolean holderOnlyResponsibleFinance = false;

   @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
   @Column(name = "date_time_registered_log", nullable = true, columnDefinition = "DATETIME")
   private LocalDateTime dateTimeRegisteredLog;

   @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
   @Column(name = "accepted_contract", nullable = true, columnDefinition = "DATETIME")
   private LocalDateTime dateTimeAcceptedContract;

   @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
   public LocalDateTime getDateBlocked() {
      return dateBlocked;
   }

   public void setDateBlocked(LocalDateTime dateBlocked) {
      this.dateBlocked = dateBlocked;
   }

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public Boolean getWaitingFirstPay() {
      return waitingFirstPay;
   }

   public void setWaitingFirstPay(Boolean waitingFirstPay) {
      this.waitingFirstPay = waitingFirstPay;
   }

   public SubscriptionStatus getStatus() {
      return status;
   }

   public void setStatus(SubscriptionStatus status) {
      this.status = status;
   }

   @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
   public LocalDate getDateBegin() {
      return dateBegin;
   }

   public void setDateBegin(LocalDate dateBegin) {
      this.dateBegin = dateBegin;
   }

   public Integer getDayForPayment() {
      return dayForPayment;
   }

   public void setDayForPayment(Integer dayForPayment) {
      this.dayForPayment = dayForPayment;
   }

   public LocalDateTime getUpdateAtPayDay() {
      return updateAtPayDay;
   }

   public void setUpdateAtPayDay(LocalDateTime updateAtPayDay) {
      this.updateAtPayDay = updateAtPayDay;
   }

   public Holder getHolder() {
      return holder;
   }

   public void setHolder(Holder holder) {
      this.holder = holder;
   }

   @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
   public LocalDate getDateOfRegistry() {
      return dateOfRegistry;
   }

   public void setDateOfRegistry(LocalDate dateOfRegistry) {
      this.dateOfRegistry = dateOfRegistry;
   }

   public String getReasonCancellation() {
      return reasonCancellation;
   }

   public void setReasonCancellation(String reasonCancellation) {
      this.reasonCancellation = reasonCancellation;
   }

   @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
   public LocalDate getDateOfCancellation() {
      return dateOfCancellation;
   }

   public void setDateOfCancellation(LocalDate dateOfCancellation) {
      this.dateOfCancellation = dateOfCancellation;
   }

   @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
   public LocalDateTime getDateRequestOfCancellation() {
      return dateRequestOfCancellation;
   }

   public void setDateRequestOfCancellation(LocalDateTime dateRequestOfCancellation) {
      this.dateRequestOfCancellation = dateRequestOfCancellation;
   }

   public Integer getDateLastCompetence() {
      return dateLastCompetence;
   }

   public void setDateLastCompetence(Integer dateLastCompetence) {
      this.dateLastCompetence = dateLastCompetence;
   }

   public User getUser() {
      return user;
   }

   public void setUser(User user) {
      this.user = user;
   }

   public LocalDateTime getDateTimeRegisteredLog() {
      return dateTimeRegisteredLog;
   }

   public void setDateTimeRegisteredLog(LocalDateTime dateTimeRegisteredLog) {
      this.dateTimeRegisteredLog = dateTimeRegisteredLog;
   }

   public LocalDateTime getDateTimeAcceptedContract() {
      return dateTimeAcceptedContract;
   }

   public void setDateTimeAcceptedContract(LocalDateTime dateTimeAcceptedContract) {
      this.dateTimeAcceptedContract = dateTimeAcceptedContract;
   }

   public Boolean getHolderOnlyResponsibleFinance() {
      if (holderOnlyResponsibleFinance == null) {
         return Boolean.FALSE;
      }
      return holderOnlyResponsibleFinance;
   }

   public void setHolderOnlyResponsibleFinance(Boolean holderOnlyResponsibleFinance) {
      this.holderOnlyResponsibleFinance = holderOnlyResponsibleFinance;
   }

   /**
    * Recupera o valor do atributo daysToBlock
    * 
    * @return o daysToBlock
    */
   public Long getDaysToBlock() {
      return daysToBlock;
   }

   /**
    * Atribui o novo valor de daysToBlock
    * 
    * @param daysToBlock daysToBlock que será atribuído
    */
   public void setDaysToBlock(Long daysToBlock) {
      this.daysToBlock = daysToBlock;
   }

}
