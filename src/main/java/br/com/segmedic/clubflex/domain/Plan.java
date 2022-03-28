
package br.com.segmedic.clubflex.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import br.com.segmedic.clubflex.domain.enums.TypeSub;
import br.com.segmedic.clubflex.support.NumberUtils;

@Entity
@Table(name = "plan")
public class Plan implements Serializable {

   private static final long serialVersionUID = 8098325022684068313L;

   @Id
   @Column(name = "idplan", nullable = false, columnDefinition = "INT(10)")
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(name = "name", nullable = false, columnDefinition = "VARCHAR(80)")
   private String name;

   @Column(name = "price_holder", nullable = false, columnDefinition = "DECIMAL(10,2)")
   private BigDecimal priceHolder;

   @Column(name = "price_dependent", nullable = false, columnDefinition = "DECIMAL(10,2)")
   private BigDecimal priceDependent;

   @Column(name = "price_accession_fee", nullable = false, columnDefinition = "DECIMAL(10,2)")
   private BigDecimal accessionFee; // taxa de adesao

   @Column(name = "flag_active", nullable = true, columnDefinition = "CHAR(1) default 1")
   private Boolean isActive;

   @Column(name = "flag_avaliable_online", nullable = true, columnDefinition = "CHAR(1) default 0")
   private Boolean avaliableOnline; // disponivel no site

   @Column(name = "flag_fidelity", nullable = true, columnDefinition = "CHAR(1) default 0")
   private Boolean hasfidelity;

   @Column(name = "flag_automatic_renovation", nullable = false, columnDefinition = "CHAR(1) default 0")
   private Boolean automaticRenovation;

   @Column(name = "retry", nullable = false, columnDefinition = "CHAR(1) default 1")
   private Boolean retry;

   @Column(name = "attempts", nullable = true)
   private Long attempts;

   @Column(name = "flag_dependent", nullable = true, columnDefinition = "CHAR(1) default 1")
   private Boolean hasdependent;

   @Enumerated(EnumType.STRING)
   @Column(name = "type_sub", nullable = false, columnDefinition = "ENUM('PJ','PF') default 'PF'")
   private TypeSub typeSub;

   @Column(name = "flag_limit_dependent", nullable = false, columnDefinition = "CHAR(1) default 0")
   private Boolean hasLimitDependent;

   @Column(name = "limit_Dependent", nullable = true, columnDefinition = "BIGINT(10) DEFAULT 0")
   private Long limitDependent;

   // @Column(name = "days_to_block", nullable = false, columnDefinition = "BIGINT(10) DEFAULT 7")
   @Transient
   private Long daysToBlock; // Quantidade de dias para bloquear assinatura apos vencimento (padrão = 7 dias)

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public BigDecimal getPriceHolder() {
      return priceHolder;
   }

   public void setPriceHolder(BigDecimal priceHolder) {
      this.priceHolder = priceHolder;
   }

   public BigDecimal getPriceDependent() {
      return priceDependent;
   }

   public void setPriceDependent(BigDecimal priceDependent) {
      this.priceDependent = priceDependent;
   }

   public BigDecimal getAccessionFee() {
      if (accessionFee == null) {
         return BigDecimal.ZERO;
      }
      return accessionFee;
   }

   public void setAccessionFee(BigDecimal accessionFee) {
      this.accessionFee = accessionFee;
   }

   public Boolean getIsActive() {
      return isActive;
   }

   public void setIsActive(Boolean isActive) {
      this.isActive = isActive;
   }

   public Boolean getAvaliableOnline() {
      return avaliableOnline;
   }

   public void setAvaliableOnline(Boolean avaliableOnline) {
      this.avaliableOnline = avaliableOnline;
   }

   public Boolean getHasfidelity() {
      return hasfidelity;
   }

   public void setHasfidelity(Boolean hasfidelity) {
      this.hasfidelity = hasfidelity;
   }

   public Boolean getHasdependent() {
      return hasdependent;
   }

   public void setHasdependent(Boolean hasdependent) {
      this.hasdependent = hasdependent;
   }

   public TypeSub getTypeSub() {
      return typeSub;
   }

   public void setTypeSub(TypeSub typeSub) {
      this.typeSub = typeSub;
   }

   public String getAccessionFeeFmt() {
      return NumberUtils.formatMoney(this.accessionFee);
   }

   public String getPriceHolderFmt() {
      return NumberUtils.formatMoney(this.priceHolder);
   }

   public String getPriceDependentFmt() {
      return NumberUtils.formatMoney(this.priceDependent);
   }

   /**
    * Recupera o valor do atributo automaticRenovation
    * 
    * @return o automaticRenovation
    */
   public Boolean getAutomaticRenovation() {
      return automaticRenovation;
   }

   /**
    * Atribui o novo valor de automaticRenovation
    * 
    * @param automaticRenovation automaticRenovation que será atribuído
    */
   public void setAutomaticRenovation(Boolean automaticRenovation) {
      this.automaticRenovation = automaticRenovation;
   }

   /**
    * Recupera o valor do atributo retry
    * 
    * @return o retry
    */
   public Boolean getRetry() {
      return retry;
   }

   /**
    * Atribui o novo valor de retry
    * 
    * @param retry retry que será atribuído
    */
   public void setRetry(Boolean retry) {
      this.retry = retry;
   }

   /**
    * Recupera o valor do atributo attempts
    * 
    * @return o attempts
    */
   public Long getAttempts() {
      return attempts;
   }

   /**
    * Atribui o novo valor de attempts
    * 
    * @param attempts attempts que será atribuído
    */
   public void setAttempts(Long attempts) {
      this.attempts = attempts;
   }

   /**
    * Recupera o valor do atributo hasLimitDependent
    * 
    * @return o hasLimitDependent
    */
   public Boolean getHasLimitDependent() {
      return hasLimitDependent;
   }

   /**
    * Atribui o novo valor de hasLimitDependent
    * 
    * @param hasLimitDependent hasLimitDependent que será atribuído
    */
   public void setHasLimitDependent(Boolean hasLimitDependent) {
      this.hasLimitDependent = hasLimitDependent;
   }

   /**
    * Recupera o valor do atributo limitDependent
    * 
    * @return o limitDependent
    */
   public Long getLimitDependent() {
      return limitDependent;
   }

   /**
    * Atribui o novo valor de limitDependent
    * 
    * @param limitDependent limitDependent que será atribuído
    */
   public void setLimitDependent(Long limitDependent) {
      this.limitDependent = limitDependent;
   }

   public Long getDaysToBlock() {
    return daysToBlock;
   }
   
   public void setDaysToBlock(Long daysToBlock) {
    this.daysToBlock = daysToBlock;
   }

}
