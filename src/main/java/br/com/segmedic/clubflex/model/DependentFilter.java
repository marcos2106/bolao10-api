
package br.com.segmedic.clubflex.model;

import java.io.Serializable;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;

public class DependentFilter implements Serializable {

   private static final long serialVersionUID = -4838823651764601826L;

   private String nameDep;
   private String cpfCnpjDep;
   private Long numCard;
   @DateTimeFormat(pattern = "dd/MM/yyyy")
   private LocalDate dateBirth;

   public Long getNumCard() {
      return numCard;
   }

   public String getNameDep() {
      return nameDep;
   }

   public void setNameDep(String nameDep) {
      this.nameDep = nameDep;
   }

   public String getCpfCnpjDep() {
      return cpfCnpjDep;
   }

   public void setCpfCnpjDep(String cpfCnpjDep) {
      this.cpfCnpjDep = cpfCnpjDep;
   }

   public void setNumCard(Long numCard) {
      this.numCard = numCard;
   }

   @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
   public LocalDate getDateBirth() {
      return dateBirth;
   }

   public void setDateBirth(LocalDate dateBirth) {
      this.dateBirth = dateBirth;
   }

}
