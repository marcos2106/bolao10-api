
package br.com.segmedic.clubflex.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import br.com.segmedic.clubflex.support.NumberUtils;

public class DebtReport implements Serializable {

   private static final long serialVersionUID = -250263258165410760L;

   private List<DebtReportDetail> details;
   private List<DebtReportCreditCardDetail> detailsCreditCard;
   private List<DebtReportTotal> total;

   public List<DebtReportDetail> getDetails() {
      return details;
   }

   public void setDetails(List<DebtReportDetail> details) {
      this.details = details;
   }

   public List<DebtReportCreditCardDetail> getDetailsCreditCard() {
      return detailsCreditCard;
   }

   public void setDetailsCreditCard(List<DebtReportCreditCardDetail> detailsCreditCard) {
      this.detailsCreditCard = detailsCreditCard;
   }

   public List<DebtReportTotal> getTotal() {
      return total;
   }

   public void setTotal(List<DebtReportTotal> total) {
      this.total = total;
   }

   public BigDecimal getTotalGeneral() {
      if (this.total != null && !this.total.isEmpty()) {
         return this.total.stream().map(DebtReportTotal::getTotal).reduce(BigDecimal::add).get();
      }
      return BigDecimal.ZERO;
   }

   public String getTotalGeneralFmt() {
      return NumberUtils.formatMoney(getTotalGeneral());
   }
}
