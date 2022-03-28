
package br.com.segmedic.clubflex.domain;

import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "credit_card")
public class CreditCard implements Serializable {

   private static final long serialVersionUID = 1L;

   @Id
   @Column(name = "idcredit_card", nullable = false, columnDefinition = "BIGINT(20)")
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(name = "brand", nullable = false, columnDefinition = "VARCHAR(20)")
   private String brand;

   @Column(name = "final_number", nullable = false, columnDefinition = "CHAR(4)")
   private String finalNumbers;

   @JsonIgnore
   @Column(name = "hash_card", nullable = false, columnDefinition = "VARCHAR(255)")
   private String cardHash;

   @ManyToOne(cascade = CascadeType.MERGE, optional = false, fetch = FetchType.EAGER)
   @JoinColumn(name = "idholder", nullable = false)
   private Holder holder;

   @Column(name = "recurrency", nullable = true, columnDefinition = "BIGINT(10) DEFAULT 0")
   private Long recurrency;

   @Column(name = "last_return_code", nullable = true, columnDefinition = "VARCHAR(10)  DEFAULT '00'")
   private String lastReturnCode;

   @DateTimeFormat(pattern = "dd/MM/yyyy")
   @Column(name = "date_last_recurrency", nullable = true, columnDefinition = "DATE")
   private LocalDate dateLastRecurrency;

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public String getBrand() {
      return brand;
   }

   public void setBrand(String brand) {
      this.brand = brand;
   }

   public String getFinalNumbers() {
      return finalNumbers;
   }

   public void setFinalNumbers(String finalNumbers) {
      this.finalNumbers = finalNumbers;
   }

   public String getCardHash() {
      return cardHash;
   }

   public void setCardHash(String cardHash) {
      this.cardHash = cardHash;
   }

   public Holder getHolder() {
      return holder;
   }

   public void setHolder(Holder holder) {
      this.holder = holder;
   }

   /**
    * Recupera o valor do atributo recurrency
    * 
    * @return o recurrency
    */
   public Long getRecurrency() {
      return recurrency;
   }

   /**
    * Atribui o novo valor de recurrency
    * 
    * @param recurrency recurrency que será atribuído
    */
   public void setRecurrency(Long recurrency) {
      this.recurrency = recurrency;
   }

   /**
    * Recupera o valor do atributo lastReturnCode
    * 
    * @return o lastReturnCode
    */
   public String getLastReturnCode() {
      return lastReturnCode;
   }

   /**
    * Atribui o novo valor de lastReturnCode
    * 
    * @param lastReturnCode lastReturnCode que será atribuído
    */
   public void setLastReturnCode(String lastReturnCode) {
      this.lastReturnCode = lastReturnCode;
   }

   /**
    * Recupera o valor do atributo dateLastRecurrency
    * 
    * @return o dateLastRecurrency
    */
   public LocalDate getDateLastRecurrency() {
      return dateLastRecurrency;
   }

   /**
    * Atribui o novo valor de dateLastRecurrency
    * 
    * @param dateLastRecurrency dateLastRecurrency que será atribuído
    */
   public void setDateLastRecurrency(LocalDate dateLastRecurrency) {
      this.dateLastRecurrency = dateLastRecurrency;
   }

}
