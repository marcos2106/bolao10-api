
package br.com.segmedic.clubflex.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
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
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import br.com.segmedic.clubflex.domain.enums.ClubCardStatus;
import br.com.segmedic.clubflex.domain.enums.DependentStatus;
import br.com.segmedic.clubflex.domain.enums.PaymentType;
import br.com.segmedic.clubflex.domain.enums.SubscriptionStatus;
import br.com.segmedic.clubflex.domain.enums.TypeSub;
import br.com.segmedic.clubflex.model.DependentType;
import br.com.segmedic.clubflex.support.NumberUtils;

@Entity
@Table(name = "subscription")
public class Subscription implements Serializable {

   private static final long serialVersionUID = -3043349472882681676L;

   @Id
   @Column(name = "idsubscription", nullable = false, columnDefinition = "BIGINT(20)")
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(name = "waiting_first_pay", nullable = true, columnDefinition = "CHAR(1)")
   private Boolean waitingFirstPay = true; // aguardando primeiro pagamento?

   @ManyToOne(cascade = CascadeType.MERGE, optional = false, fetch = FetchType.EAGER)
   @JoinColumn(name = "idplan", nullable = false)
   private Plan plan; // plano

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

   @Enumerated(EnumType.STRING)
   @Column(name = "payment_type", nullable = false, columnDefinition = "ENUM('TICKET','CREDIT_CARD','DEBIT_CARD') default 'TICKET'")
   private PaymentType paymentType; // forma de pagamento

   @OneToMany(mappedBy = "subscription", targetEntity = ClubCard.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
   @OrderBy("idholder")
   @JsonBackReference
   private Set<ClubCard> cards; // cartoes da assinatura

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

   @ManyToOne(cascade = CascadeType.MERGE, optional = true, fetch = FetchType.EAGER)
   @JoinColumn(name = "idcompany", nullable = true)
   private Company company; // empresa origem da assinatura

   @ManyToOne(cascade = CascadeType.MERGE, optional = true, fetch = FetchType.EAGER)
   @JoinColumn(name = "idbroker", nullable = true)
   private Broker broker; // vendedor de rua ou corretor

   @ManyToOne(cascade = CascadeType.MERGE, optional = true, fetch = FetchType.EAGER)
   @JoinColumn(name = "idreason", nullable = true)
   private Reason reason; // razão do cancelamento

   @OneToMany(mappedBy = "subscription", targetEntity = Dependent.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
   @JsonBackReference
   private Set<Dependent> dependents; // dependentes

   @OneToMany(mappedBy = "subscription", targetEntity = Pet.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
   @JsonBackReference
   private Set<Pet> pets; // pets

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

   @Enumerated(EnumType.STRING)
   @Column(name = "type_sub", nullable = false, columnDefinition = "ENUM('PJ','PF') default 'PF'")
   private TypeSub typeSub;

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

   public Plan getPlan() {
      return plan;
   }

   public void setPlan(Plan plan) {
      this.plan = plan;
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

   public PaymentType getPaymentType() {
      return paymentType;
   }

   public void setPaymentType(PaymentType paymentType) {
      this.paymentType = paymentType;
   }

   public Set<ClubCard> getCards() {
      return cards;
   }

   public void setCards(Set<ClubCard> cards) {
      this.cards = cards;
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

   public Company getCompany() {
      return company;
   }

   public void setCompany(Company company) {
      this.company = company;
   }

   public Broker getBroker() {
      return broker;
   }

   public void setBroker(Broker broker) {
      this.broker = broker;
   }

   public Reason getReason() {
      return reason;
   }

   public void setReason(Reason reason) {
      this.reason = reason;
   }

   public Set<Dependent> getDependents() {
      return dependents;
   }

   public void setDependents(Set<Dependent> dependents) {
      this.dependents = dependents;
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

   public TypeSub getTypeSub() {
      return typeSub;
   }

   public void setTypeSub(TypeSub typeSub) {
      this.typeSub = typeSub;
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

   public Set<Pet> getPets() {
      return pets;
   }

   public void setPets(Set<Pet> listaPets) {
      this.pets = listaPets;
   }

   public Integer getQuantityLifes() {
      Integer lifes = 0;
      if (!this.holderOnlyResponsibleFinance) {
         lifes += 1;
      }
      if (this.dependents != null) {
         lifes += this.dependents.size();
      }
      return lifes;
   }

   public BigDecimal getTotalPriceWithAccessionFee() {
      BigDecimal price = BigDecimal.ZERO;
      price = price.add(plan.getAccessionFee());
      if (!this.getHolderOnlyResponsibleFinance()) {
         price = price.add(plan.getPriceHolder());
      }
      price = price.add(getTotalDependentsPrice());
      return price;
   }

   public BigDecimal getTotalDependentsPrice() {
      BigDecimal price = BigDecimal.ZERO;
      if (this.dependents != null) {
         for (Dependent dependent : dependents) {
            if (DependentStatus.OK.equals(dependent.getStatus())) {
               if (TypeSub.PF.equals(typeSub)) {
                  price = price.add(plan.getPriceDependent());
               }
               else {
                  if (DependentType.HOLDER.equals(dependent.getType())) {
                     price = price.add(plan.getPriceHolder());
                  }
                  else {
                     price = price.add(plan.getPriceDependent());
                  }
               }
            }
         }
      }
      return price;
   }

   public BigDecimal getTotalPrice() {
      if (TypeSub.PJ.equals(this.typeSub)) {
         return getTotalPricePJ();
      }
      else {
         return getTotalPricePF();
      }
   }

   private BigDecimal getTotalPricePJ() {

      BigDecimal totalPrice = BigDecimal.ZERO;

      Set<ClubCard> cards = this.cards;
      if (cards != null) {
         for (ClubCard card : cards) {
            if (card.getStatus().equals(ClubCardStatus.OK)) {
               if (card.getIsCardOfHolder()) {
                  totalPrice = totalPrice.add(plan.getPriceHolder());
               }
               else {
                  totalPrice = totalPrice.add(plan.getPriceDependent());
               }
            }
         }
      }
      return totalPrice;
   }

   private BigDecimal getTotalPricePF() {
      BigDecimal totalPrice = BigDecimal.ZERO;
      Set<Dependent> listaDependentes = this.dependents;
      int dependentesRestantes = 0;
      for (Dependent dep : listaDependentes) {
         if (dep.getStatus().equals(DependentStatus.OK)) {
            dependentesRestantes++;
         }
      }

      // Se há apenas Resp. Financeiro, o número restantes é o numero de dependentes - 1 (um dependente é titular)
      if (this.getHolderOnlyResponsibleFinance()) {
         dependentesRestantes--;
      }

      // Ou Titular ou Resp. Financeiro sempre terá um valor financeiro de titular.
      totalPrice = totalPrice.add(plan.getPriceHolder());

      // dependentes
      if (dependentesRestantes > 0) {
         totalPrice = totalPrice.add(plan.getPriceDependent().multiply(new BigDecimal(dependentesRestantes)));
      }
      return totalPrice;
   }

   public String getTotalPriceFmt() {
      return NumberUtils.formatMoney(this.getTotalPrice());
   }

   public Integer getTotalDependetsOk() {
      int total = 0;
      for (Dependent dep : this.dependents) {
         if (dep.getStatus().equals(DependentStatus.OK)) {
            total++;
         }
      }
      return total;
   }
}
