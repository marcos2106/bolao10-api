
package br.com.bolao.bolao10.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.com.bolao.bolao10.domain.enums.Sex;
import br.com.bolao.bolao10.domain.enums.SubscriptionStatus;
import br.com.bolao.bolao10.support.Strings;

public class HolderStatus implements Serializable {

   private static final long serialVersionUID = -2440650018585566905L;

   private Long id;
   private String name;
   private String cpfCnpj;
   private LocalDate dateOfBirth;
   private Boolean isHolder;
   private Sex sex;
   private String email;
   private String cellPhone;
   private String homePhone;
   private String responsibleName;
   private String responsibleCpf;
   private String zipcode;
   private String address;
   private String complementAddress;
   private String uf;
   private String city;
   private String neighborhood;
   private String idClubCard;
   private String statusClubCard;
   private Long gatewayTicketId;
   private LocalDateTime updatedAt;
   private Long idSubscription;
   private SubscriptionStatus statusSubscription;

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
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

   public String getCpfCnpj() {
      return Strings.removeNoNumericChars(cpfCnpj);
   }

   public void setCpfCnpj(String cpfCnpj) {
      this.cpfCnpj = cpfCnpj;
   }

   @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
   public LocalDate getDateOfBirth() {
      return dateOfBirth;
   }

   public void setDateOfBirth(LocalDate dateOfBirth) {
      this.dateOfBirth = dateOfBirth;
   }

   public Boolean getIsHolder() {
      return isHolder;
   }

   public void setIsHolder(Boolean isHolder) {
      this.isHolder = isHolder;
   }

   public Sex getSex() {
      return sex;
   }

   public void setSex(Sex sex) {
      this.sex = sex;
   }

   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public String getCellPhone() {
      return cellPhone;
   }

   public void setCellPhone(String cellPhone) {
      this.cellPhone = cellPhone;
   }

   public String getHomePhone() {
      return homePhone;
   }

   public void setHomePhone(String homePhone) {
      this.homePhone = homePhone;
   }

   public String getResponsibleName() {
      return responsibleName;
   }

   public void setResponsibleName(String responsibleName) {
      this.responsibleName = responsibleName;
   }

   public String getResponsibleCpf() {
      return responsibleCpf;
   }

   public void setResponsibleCpf(String responsibleCpf) {
      this.responsibleCpf = responsibleCpf;
   }

   public String getZipcode() {
      return Strings.unNormalizeCEP(zipcode);
   }

   public void setZipcode(String zipcode) {
      this.zipcode = zipcode;
   }

   public String getAddress() {
      return address;
   }

   public void setAddress(String address) {
      this.address = address;
   }

   public String getComplementAddress() {
      return complementAddress;
   }

   public void setComplementAddress(String complementAddress) {
      this.complementAddress = complementAddress;
   }

   public String getUf() {
      return uf;
   }

   public void setUf(String uf) {
      this.uf = uf;
   }

   public String getNeighborhood() {
      return neighborhood;
   }

   public void setNeighborhood(String neighborhood) {
      this.neighborhood = neighborhood;
   }

   public String getCity() {
      return city;
   }

   public void setCity(String city) {
      this.city = city;
   }

   public Long getGatewayTicketId() {
      return gatewayTicketId;
   }

   public void setGatewayTicketId(Long gatewayTicketId) {
      this.gatewayTicketId = gatewayTicketId;
   }

   @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
   public LocalDateTime getUpdatedAt() {
      return updatedAt;
   }

   public void setUpdatedAt(LocalDateTime updatedAt) {
      this.updatedAt = updatedAt;
   }

   public String getCompleteAddress() {
      if (this.address == null) {
         return null;
      }
      return String.format("%s, %s - %s-%s", this.address, this.complementAddress, this.city, this.uf);
   }

   public Long getIdSubscription() {
      return idSubscription;
   }

   public void setIdSubscription(Long idSubscription) {
      this.idSubscription = idSubscription;
   }

   public String getCpfCnpjFmt() {
      if (this.cpfCnpj != null) {
         if (this.cpfCnpj.length() > 11) {
            return Strings.formatCNPJ(this.cpfCnpj);
         }
         else {
            return Strings.formatCPF(this.cpfCnpj);
         }
      }
      return null;
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
      HolderStatus other = (HolderStatus) obj;
      if (id == null) {
         if (other.id != null)
            return false;
      }
      else if (!id.equals(other.id))
         return false;
      return true;
   }

   public String getStatusClubCard() {
      return statusClubCard;
   }

   public void setStatusClubCard(String statusClubCard) {
      this.statusClubCard = statusClubCard;
   }

   public SubscriptionStatus getStatusSubscription() {
      return statusSubscription;
   }

   public void setStatusSubscription(SubscriptionStatus statusSubscription) {
      this.statusSubscription = statusSubscription;
   }

   /**
    * Recupera o valor do atributo idClubCard
    * 
    * @return o idClubCard
    */
   public String getIdClubCard() {
      return idClubCard;
   }

   /**
    * Atribui o novo valor de idClubCard
    * 
    * @param idClubCard idClubCard que será atribuído
    */
   public void setIdClubCard(String idClubCard) {
      this.idClubCard = idClubCard;
   }
}
