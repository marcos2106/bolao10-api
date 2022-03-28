
package br.com.segmedic.clubflex.domain;

import java.io.Serializable;
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
import br.com.segmedic.clubflex.domain.enums.SubscriptionLogAction;

@Entity
@Table(name = "subscription_log")
public class SubscriptionLog implements Serializable {

   private static final long serialVersionUID = -5415755179422137592L;

   @Id
   @Column(name = "idsubscription_log", nullable = false, columnDefinition = "BIGINT(20)")
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(name = "idsubscription", nullable = false, columnDefinition = "BIGINT(20)")
   private Long subscriptionId;

   @Column(name = "iduser", nullable = true, columnDefinition = "BIGINT(20)")
   private Long userId;

   @ManyToOne(cascade = CascadeType.MERGE, optional = true, fetch = FetchType.EAGER)
   @JoinColumn(name = "idreason", nullable = true)
   private Reason reason; // razão do cancelamento

   @Column(name = "date_time_log", nullable = false, columnDefinition = "DATETIME")
   private LocalDateTime dateTimeLog;

   @Enumerated(EnumType.STRING)
   @Column(name = "action", nullable = false)
   private SubscriptionLogAction action;

   @Column(name = "obs", nullable = true, length = 255)
   private String obs;

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public Long getSubscriptionId() {
      return subscriptionId;
   }

   public void setSubscriptionId(Long subscriptionId) {
      this.subscriptionId = subscriptionId;
   }

   public Long getUserId() {
      return userId;
   }

   public void setUserId(Long userId) {
      this.userId = userId;
   }

   public LocalDateTime getDateTimeLog() {
      return dateTimeLog;
   }

   public void setDateTimeLog(LocalDateTime dateTimeLog) {
      this.dateTimeLog = dateTimeLog;
   }

   public SubscriptionLogAction getAction() {
      return action;
   }

   public void setAction(SubscriptionLogAction action) {
      this.action = action;
   }

   public String getObs() {
      return obs;
   }

   public void setObs(String obs) {
      this.obs = obs;
   }

   public Reason getReason() {
      return reason;
   }

   public void setReason(Reason reason) {
      this.reason = reason;
   }

}
