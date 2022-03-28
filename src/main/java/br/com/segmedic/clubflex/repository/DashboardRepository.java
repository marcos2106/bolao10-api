
package br.com.segmedic.clubflex.repository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import br.com.segmedic.clubflex.model.DashGraphSubs;

@Repository
public class DashboardRepository extends GenericRepository {

   @Autowired
   private EntityManager em;

   public BigDecimal getTraficAmount(LocalDate begin, LocalDate end) {
      Query query = em.createNativeQuery(
         "select sum(pay_amount) as paid from invoice where status = 'PAID' and payment_date between :begin and :end ");
      query.setParameter("begin", begin);
      query.setParameter("end", end);
      try {
         return (BigDecimal) query.getSingleResult();
      }
      catch (Exception e) {
         return BigDecimal.ZERO;
      }
   }

   public BigDecimal getTotalSubscriptions(LocalDate begin, LocalDate end) {
      Query query = em.createNativeQuery("select count(*) as total from subscription where date_registred between :begin and :end ");
      query.setParameter("begin", begin);
      query.setParameter("end", end);
      try {
         return new BigDecimal((BigInteger) query.getSingleResult());
      }
      catch (Exception e) {
         return BigDecimal.ZERO;
      }
   }

   public BigDecimal getDebitQuantity(LocalDate beginActual, LocalDate endActual) {
      StringBuilder sql = new StringBuilder();
      sql.append("select count(*) as total_invoice												  	");
      sql.append("from subscription s inner join invoice a on a.idsubscription = s.idsubscription  	");
      sql.append("where a.status = 'OPENED' 														  	");
      sql.append("and a.due_date BETWEEN :beginActual AND :endActual									");

      Query query = em.createNativeQuery(sql.toString());
      query.setParameter("beginActual", beginActual);
      query.setParameter("endActual", endActual);

      try {
         return new BigDecimal((BigInteger) query.getSingleResult());
      }
      catch (Exception e) {
         return BigDecimal.ZERO;
      }
   }

   public BigDecimal getDebitAmount(LocalDate begin, LocalDate end) {
      StringBuilder sql = new StringBuilder();
      sql.append("select SUM(a.amount) as total_amount											  ");
      sql.append("from subscription s inner join invoice a on a.idsubscription = s.idsubscription  ");
      sql.append("where a.status = 'OPENED' 														  ");
      sql.append("and a.due_date BETWEEN :begin AND :end											  ");

      Query query = em.createNativeQuery(sql.toString());
      query.setParameter("begin", begin);
      query.setParameter("end", end);

      try {
         return (BigDecimal) query.getSingleResult();
      }
      catch (Exception e) {
         return BigDecimal.ZERO;
      }
   }

   public DashGraphSubs getSubsLastYear() {
      StringBuilder sql = new StringBuilder();
      sql.append("select count(*) as total, DATE_FORMAT(date_registred,'%b/%y') as monthYear,  ");
      sql.append("DATE_FORMAT(date_registred,'%b') as monthName  							  ");
      sql.append("from subscription 														 	  ");
      sql.append("where date_registred between :begin and :end 								  ");
      sql.append("group by monthYear															  ");
      sql.append("order by date_registred													  ");

      Query query = em.createNativeQuery(sql.toString());
      query.setParameter("begin", LocalDate.now().minusMonths(12).withDayOfMonth(1));
      query.setParameter("end", LocalDate.now());

      try {
         @SuppressWarnings("unchecked")
         List<Object[]> objects = query.getResultList();

         DashGraphSubs dash = new DashGraphSubs();
         objects.forEach(o -> {
            dash.getSubs().add(getStringValue(o, 1));
            dash.getMonths().add(getStringValue(o, 3));
         });

         return dash;
      }
      catch (Exception e) {
         return null;
      }
   }

   public void saveDebtStatistics(LocalDate datetime, int quantity) {
      em.createNativeQuery("DELETE FROM debt_statistics WHERE month = :month AND year = :year ")
               .setParameter("month", datetime.getMonthValue())
               .setParameter("year", datetime.getYear())
               .executeUpdate();

      em.createNativeQuery("INSERT INTO debt_statistics (month, year, quantity) VALUES (:month, :year, :quantity) ")
               .setParameter("month", datetime.getMonthValue())
               .setParameter("year", datetime.getYear())
               .setParameter("quantity", quantity)
               .executeUpdate();

   }

   @SuppressWarnings("unchecked")
   public List<Object[]> getDebtHistoryLastSixMouth() {
      try {
         return em.createNativeQuery("SELECT * FROM debt_statistics ORDER BY month, year LIMIT 6")
                  .getResultList();
      }
      catch (Exception e) {
         return null;
      }
   }

}
