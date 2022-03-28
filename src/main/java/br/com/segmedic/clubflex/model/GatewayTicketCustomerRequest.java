
package br.com.segmedic.clubflex.model;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import br.com.segmedic.clubflex.domain.Holder;

@JsonRootName(value = "customer")
public class GatewayTicketCustomerRequest implements Serializable {

   private static final long serialVersionUID = 1L;

   @JsonProperty("person_name")
   private String name;

   @JsonProperty("cnpj_cpf")
   private String cpfCnpj;

   @JsonProperty("zipcode")
   private String zipcode;

   @JsonProperty("address")
   private String address;

   @JsonProperty("city_name")
   private String city;

   @JsonProperty("state")
   private String uf;

   @JsonProperty("neighborhood")
   private String neighborhood;

   @JsonProperty("email")
   private String email;

   @JsonProperty("address_number")
   private Long number;

   public GatewayTicketCustomerRequest() {
      super();
   }

   public GatewayTicketCustomerRequest(Holder holder) {
      super();
      this.name = holder.getName();
      this.cpfCnpj = holder.getCpfCnpj();
      this.zipcode = holder.getZipcode();
      this.address = holder.getAddress();
      this.city = holder.getCity();
      this.uf = holder.getUf();
      this.neighborhood = holder.getNeighborhood();
      this.email = holder.getEmail();
      this.number = holder.getNumber();
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getCpfCnpj() {
      return cpfCnpj;
   }

   public void setCpfCnpj(String cpfCnpj) {
      this.cpfCnpj = cpfCnpj;
   }

   public String getZipcode() {
      return zipcode;
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

   public String getCity() {
      return city;
   }

   public void setCity(String city) {
      this.city = city;
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

   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public Long getNumber() {
      return number;
   }

   public void setNumber(Long number) {
      this.number = number;
   }
}
