
package br.com.bolao.bolao10.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import br.com.bolao.bolao10.domain.enums.Sex;
import br.com.bolao.bolao10.support.Strings;

@Table(name = "holder", indexes = @Index(columnList = "cpf_cnpj"))
public class Holder implements Serializable {

   private static final long serialVersionUID = -2440650018585566905L;

   @Id
   @Column(name = "idholder", nullable = false, columnDefinition = "BIGINT(20)")
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(name = "name", nullable = false, columnDefinition = "VARCHAR(80)")
   private String name;

   @Column(name = "cpf_cnpj", nullable = false, columnDefinition = "VARCHAR(14)")
   private String cpfCnpj;

   @DateTimeFormat(pattern = "dd/MM/yyyy")
   @Column(name = "date_of_birth", nullable = true, columnDefinition = "DATE")
   private LocalDate dateOfBirth;

   @Column(name = "flag_isholder", nullable = false, columnDefinition = "CHAR(1)")
   private Boolean isHolder;

   @Enumerated(EnumType.STRING)
   @Column(name = "sex", nullable = true, columnDefinition = "ENUM('MALE','FEMALE') default 'MALE'")
   private Sex sex;

   @Column(name = "email", nullable = true, columnDefinition = "VARCHAR(80)")
   private String email;

   @Column(name = "cellphone", nullable = true, columnDefinition = "VARCHAR(14)")
   private String cellPhone;

   @Column(name = "homephone", nullable = true, columnDefinition = "VARCHAR(14)")
   private String homePhone;

   @Column(name = "responsible_name", nullable = true, columnDefinition = "VARCHAR(80)")
   private String responsibleName;

   @Column(name = "responsible_cpf", nullable = true, columnDefinition = "VARCHAR(11)")
   private String responsibleCpf;

   @Column(name = "zipcode", nullable = true, columnDefinition = "VARCHAR(8)")
   private String zipcode;

   @Column(name = "address", nullable = true, columnDefinition = "VARCHAR(80)")
   private String address;

   @Column(name = "number", nullable = true)
   private Long number;

   @Column(name = "complement", nullable = true, columnDefinition = "VARCHAR(50)")
   private String complementAddress;

   @Column(name = "uf", nullable = true, columnDefinition = "VARCHAR(2)")
   private String uf;

   @Column(name = "city", nullable = true, columnDefinition = "VARCHAR(50)")
   private String city;

   @Column(name = "neighborhood", nullable = true, columnDefinition = "VARCHAR(50)")
   private String neighborhood;

   @Column(name = "gateway_ticket_id", nullable = true)
   private Long gatewayTicketId;

   @Column(name = "updated_at", nullable = true)
   private LocalDateTime updatedAt;

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

   public Long getNumber() {
      return number;
   }

   public void setNumber(Long number) {
      this.number = number;
   }

   @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
   public LocalDateTime getUpdatedAt() {
      return updatedAt;
   }

   public void setUpdatedAt(LocalDateTime updatedAt) {
      this.updatedAt = updatedAt;
   }

   public String getCompleteAddress() {
      return String.format("%s, %s - %s-%s", this.address, this.complementAddress, this.city, this.uf);
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
      Holder other = (Holder) obj;
      if (id == null) {
         if (other.id != null)
            return false;
      }
      else if (!id.equals(other.id))
         return false;
      return true;
   }
}
