
package br.com.segmedic.clubflex.model;

import java.io.Serializable;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import br.com.segmedic.clubflex.support.Strings;

public class HolderFilter implements Serializable {

   private static final long serialVersionUID = -4838823651764601826L;

   private String nameHolder;
   private String cpfCnpjHolder;
   private Long numCard;

   @DateTimeFormat(pattern = "dd/MM/yyyy")
   private LocalDate dateBirth;

   public String getCpfCnpjHolder() {
      return Strings.removeNoNumericChars(cpfCnpjHolder);
   }

   public void setCpfCnpjHolder(String cpfCnpjHolder) {
      this.cpfCnpjHolder = cpfCnpjHolder;
   }

   public String getNameHolder() {
      return nameHolder;
   }

   public void setNameHolder(String nameHolder) {
      this.nameHolder = nameHolder;
   }

   public Long getNumCard() {
      return numCard;
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
