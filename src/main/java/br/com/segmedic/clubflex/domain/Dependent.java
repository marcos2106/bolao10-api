
package br.com.segmedic.clubflex.domain;

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
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import br.com.segmedic.clubflex.domain.enums.DependentStatus;
import br.com.segmedic.clubflex.domain.enums.Sex;
import br.com.segmedic.clubflex.model.DependentType;
import br.com.segmedic.clubflex.support.Strings;

@Entity
@Table(name = "dependent", indexes = @Index(columnList = "cpf"))
public class Dependent implements Serializable {

   private static final long serialVersionUID = -8999902326148958223L;

   @Id
   @Column(name = "iddependent", nullable = false, columnDefinition = "BIGINT(20)")
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @ManyToOne(optional = false, fetch = FetchType.LAZY)
   @JoinColumn(name = "idsubscription", nullable = false)
   @JsonIgnore
   private Subscription subscription;

   @Column(name = "name", nullable = false, columnDefinition = "VARCHAR(80)")
   private String name;

   @Column(name = "cpf", nullable = false, columnDefinition = "VARCHAR(11)")
   private String cpf;

   @Column(name = "email", nullable = true, columnDefinition = "VARCHAR(80)")
   private String email;

   @Column(name = "phone", nullable = true, columnDefinition = "VARCHAR(14)")
   private String phone;

   @Enumerated(EnumType.STRING)
   @Column(name = "sex", nullable = true, columnDefinition = "ENUM('MALE','FEMALE') default 'MALE'")
   private Sex sex;

   @DateTimeFormat(pattern = "dd/MM/yyyy")
   @Column(name = "date_of_birth", nullable = true, columnDefinition = "DATE")
   private LocalDate dateOfBirth;

   @Enumerated(EnumType.STRING)
   @Column(name = "status", nullable = false, columnDefinition = "ENUM('REMOVED','OK')")
   private DependentStatus status;

   @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
   @Column(name = "date_of_removal", nullable = true, columnDefinition = "DATETIME")
   private LocalDateTime dateOfRemoval;

   @Column(name = "reason_cancellation", nullable = true, columnDefinition = "VARCHAR(255)")
   private String reasonCancellation; // motivo do cancelamento

   @ManyToOne(cascade = CascadeType.MERGE, optional = true, fetch = FetchType.EAGER)
   @JoinColumn(name = "idreason", nullable = true)
   private Reason reason; // razão do cancelamento

   @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
   @Column(name = "date_of_insert", nullable = false, columnDefinition = "DATETIME")
   private LocalDateTime dateOfInsert;

   @Column(name = "cpf_holder", nullable = true, columnDefinition = "VARCHAR(11)")
   private String cpfHolder;

   @Transient
   private Long clubCard;

   private transient DependentType type;

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public Subscription getSubscription() {
      return subscription;
   }

   public void setSubscription(Subscription subscription) {
      this.subscription = subscription;
   }

   public String getName() {
      if (name != null) {
         return name.toUpperCase();
      }
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public String getPhone() {
      return phone;
   }

   public void setPhone(String phone) {
      this.phone = phone;
   }

   public Sex getSex() {
      return sex;
   }

   public void setSex(Sex sex) {
      this.sex = sex;
   }

   public String getCpf() {
      return Strings.removeNoNumericChars(cpf);
   }

   public void setCpf(String cpf) {
      this.cpf = cpf;
   }

   @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
   public LocalDate getDateOfBirth() {
      return dateOfBirth;
   }

   public void setDateOfBirth(LocalDate dateOfBirth) {
      this.dateOfBirth = dateOfBirth;
   }

   public DependentStatus getStatus() {
      return status;
   }

   public void setStatus(DependentStatus status) {
      this.status = status;
   }

   @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
   public LocalDateTime getDateOfRemoval() {
      return dateOfRemoval;
   }

   public void setDateOfRemoval(LocalDateTime dateOfRemoval) {
      this.dateOfRemoval = dateOfRemoval;
   }

   public String getReasonCancellation() {
      return reasonCancellation;
   }

   public void setReasonCancellation(String reasonCancellation) {
      this.reasonCancellation = reasonCancellation;
   }

   public Reason getReason() {
      return reason;
   }

   public void setReason(Reason reason) {
      this.reason = reason;
   }

   @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
   public LocalDateTime getDateOfInsert() {
      return dateOfInsert;
   }

   public void setDateOfInsert(LocalDateTime dateOfInsert) {
      this.dateOfInsert = dateOfInsert;
   }

   public String getCpfHolder() {
      return cpfHolder;
   }

   public void setCpfHolder(String cpfHolder) {
      this.cpfHolder = cpfHolder;
   }

   public DependentType getType() {
      return type;
   }

   public void setType(DependentType type) {
      this.type = type;
   }

   public Long getClubCard() {
      return clubCard;
   }

   public void setClubCard(Long clubCard) {
      this.clubCard = clubCard;
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
      Dependent other = (Dependent) obj;
      if (id == null) {
         if (other.id != null)
            return false;
      }
      else if (!id.equals(other.id))
         return false;
      return true;
   }

}
