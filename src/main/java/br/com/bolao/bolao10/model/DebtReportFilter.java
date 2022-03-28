
package br.com.segmedic.clubflex.model;

import java.io.Serializable;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;

public class DebtReportFilter implements Serializable {

   private static final long serialVersionUID = 1288364540691L;

   @DateTimeFormat(pattern = "dd/MM/yyyy")
   private LocalDate dateBegin;

   @DateTimeFormat(pattern = "dd/MM/yyyy")
   private LocalDate dateEnd;

   private Integer days;
   private Long plan;
   private boolean lastInvoice;
   private String[] codes;

   @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
   public LocalDate getDateBegin() {
      return dateBegin;
   }

   public void setDateBegin(LocalDate dateBegin) {
      this.dateBegin = dateBegin;
   }

   @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
   public LocalDate getDateEnd() {
      return dateEnd;
   }

   public void setDateEnd(LocalDate dateEnd) {
      this.dateEnd = dateEnd;
   }

   public Integer getDays() {
      return (this.days == null) ? 30 : this.days;
   }

   public void setDays(Integer days) {
      this.days = days;
   }

   public boolean isLastInvoice() {
      return lastInvoice;
   }

   public void setLastInvoice(boolean lastInvoice) {
      this.lastInvoice = lastInvoice;
   }

   public String[] getCodes() {
      return codes;
   }

   public void setCodes(String[] codes) {
      this.codes = codes;
   }

   public Long getPlan() {
      return plan;
   }

   public void setPlan(Long plan) {
      this.plan = plan;
   }

}
