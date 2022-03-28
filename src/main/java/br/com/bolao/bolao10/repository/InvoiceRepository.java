
package br.com.bolao.bolao10.repository;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.com.bolao.bolao10.domain.Invoice;
import br.com.bolao.bolao10.domain.enums.InvoiceStatus;
import br.com.bolao.bolao10.model.SubscriptionFilter;

@Repository
public class InvoiceRepository extends GenericRepository {

   @Autowired
   private EntityManager em;

   public List<Invoice> listLastBySubscriptionId(Long subscriptionId) {
      StringBuilder sql = new StringBuilder();
      sql.append(" select i ");
      sql.append(" from Invoice i");
      sql.append(" where i.subscription.id = :subscriptionId");
      sql.append(" order by i.dueDate ASC ");

      TypedQuery<Invoice> query = em.createQuery(sql.toString(), Invoice.class);
      query.setParameter("subscriptionId", subscriptionId);
      query.setMaxResults(12);

      try {
         return query.getResultList();
      }
      catch (Exception e) {
         return null;
      }
   }

   public List<Invoice> listBySubscriptionId(Long subscriptionId) {
      StringBuilder sql = new StringBuilder();
      sql.append(" select i ");
      sql.append(" from Invoice i");
      sql.append(" where i.subscription.id = :subscriptionId");
      sql.append(" and i.status <> 'CANCELLED' ");
      sql.append(" order by i.dueDate ASC ");

      TypedQuery<Invoice> query = em.createQuery(sql.toString(), Invoice.class);
      query.setParameter("subscriptionId", subscriptionId);

      try {
         return query.getResultList();
      }
      catch (Exception e) {
         return null;
      }
   }

   public Invoice findById(Long id) {
      try {
         return super.find(Invoice.class, id);
      }
      catch (Exception e) {
         return null;
      }
   }

   public Invoice findGatewayId(String transactId) {
      StringBuilder sql = new StringBuilder();
      sql.append(" select i ");
      sql.append(" from Invoice i");
      sql.append(" where i.transactId = :transactId");

      TypedQuery<Invoice> query = em.createQuery(sql.toString(), Invoice.class);
      query.setParameter("transactId", transactId);

      try {
         return query.getSingleResult();
      }
      catch (Exception e) {
         return null;
      }
   }

   public Invoice updatePay(Invoice invoice) {
      StringBuilder sql = new StringBuilder();
      sql.append(" UPDATE invoice SET                ");
      sql.append(" payment_date = :datePaid,         ");
      sql.append(" transact_nsu = :nsu,              ");
      sql.append(" authorization_code = :aut,        ");
      sql.append(" transact_gateway_id = :tdid,      ");
      sql.append(" pay_amount = :amount,         	   ");
      sql.append(" status = :statuspay,              ");
      sql.append(" payment_type = :paytype           ");
      sql.append(" WHERE idinvoice = :id             ");

      em.createNativeQuery(sql.toString())
               .setParameter("datePaid", invoice.getPaymentDate())
               .setParameter("nsu", invoice.getTransactNsu())
               .setParameter("aut", invoice.getAuthorizationCode())
               .setParameter("tdid", invoice.getTransactId())
               .setParameter("amount", invoice.getPayAmount())
               .setParameter("statuspay", invoice.getStatus().name())
               .setParameter("id", invoice.getId())
               .executeUpdate();

      return invoice;
   }

   public Invoice save(Invoice invoice) {
      if (invoice.getId() == null) {
         invoice.setDateTimeRegisteredLog(LocalDateTime.now());
         super.persist(invoice);
      }
      else {
         super.update(invoice);
      }
      return invoice;
   }

   public List<Invoice> listInvoiceByStatus(InvoiceStatus status) {
      StringBuilder sql = new StringBuilder();
      sql.append(" select i ");
      sql.append(" from Invoice i");
      sql.append(" where i.status = :status ");

      TypedQuery<Invoice> query = em.createQuery(sql.toString(), Invoice.class);
      query.setParameter("status", status);
      query.setMaxResults(512);

      try {
         return query.getResultList();
      }
      catch (Exception e) {
         return null;
      }
   }

   public List<Invoice> listInvoiceBySubscriptionIdAndStatus(Long subscriptionId, InvoiceStatus status) {
      StringBuilder sql = new StringBuilder();
      sql.append(" select i ");
      sql.append(" from Invoice i");
      sql.append(" where i.status = :status ");
      sql.append("   and i.subscription.id = :subscriptionId");

      TypedQuery<Invoice> query = em.createQuery(sql.toString(), Invoice.class);
      query.setParameter("status", status);
      query.setParameter("subscriptionId", subscriptionId);

      try {
         return query.getResultList();
      }
      catch (Exception e) {
         return null;
      }
   }

   public Invoice getLastInvoicePay(Long subscriptionId) {
      StringBuilder sql = new StringBuilder();
      sql.append(" select i ");
      sql.append(" from Invoice i");
      sql.append(" where i.status = :status ");
      sql.append("   and i.subscription.id = :subscriptionId");
      sql.append(" order by i.id DESC ");

      TypedQuery<Invoice> query = em.createQuery(sql.toString(), Invoice.class);
      query.setParameter("status", InvoiceStatus.PAID);
      query.setParameter("subscriptionId", subscriptionId);
      query.setMaxResults(1);

      try {
         return query.getSingleResult();
      }
      catch (Exception e) {
         return null;
      }
   }

   public Invoice getLastCompetence(Long subscriptionId) {
      StringBuilder sql = new StringBuilder();
      sql.append(" select i ");
      sql.append(" from Invoice i");
      sql.append(" where i.subscription.id = :subscriptionId");
      sql.append(" order by i.competenceEnd DESC ");

      TypedQuery<Invoice> query = em.createQuery(sql.toString(), Invoice.class);
      query.setParameter("subscriptionId", subscriptionId);
      query.setMaxResults(1);

      try {
         return query.getSingleResult();
      }
      catch (Exception e) {
         return null;
      }
   }

   public Invoice getFirstInvoice(Long subscriptionId) {
      StringBuilder sql = new StringBuilder();
      sql.append(" select i 									");
      sql.append(" from Invoice i								");
      sql.append(" where i.subscription.id = :subscriptionId  ");
      sql.append(" order by i.id ASC 						    ");

      TypedQuery<Invoice> query = em.createQuery(sql.toString(), Invoice.class);
      query.setParameter("subscriptionId", subscriptionId);
      query.setMaxResults(1);

      try {
         return query.getSingleResult();
      }
      catch (Exception e) {
         return null;
      }
   }

   public List<Invoice> listLastWithoutCancelledBySubscriptionId(Long subscriptionId) {
      StringBuilder sql = new StringBuilder();
      sql.append(" select i ");
      sql.append(" from Invoice i");
      sql.append(" where i.subscription.id = :subscriptionId");
      sql.append(" and i.status <> :status ");
      sql.append(" and i.dueDate > :duedate ");
      sql.append(" order by i.dueDate ASC ");

      TypedQuery<Invoice> query = em.createQuery(sql.toString(), Invoice.class);
      query.setParameter("subscriptionId", subscriptionId);
      query.setParameter("status", InvoiceStatus.CANCELLED);
      query.setParameter("duedate", LocalDate.now().minusMonths(12));

      try {
         return query.getResultList();
      }
      catch (Exception e) {
         return null;
      }
   }

   @SuppressWarnings("unchecked")
   public List<BigInteger> listInvoicesToRecurrence(LocalDate beginDueDate, LocalDate endDueDate) {
      StringBuilder sql = new StringBuilder();
      sql.append(" select distinct i.idinvoice from invoice i, subscription s ");

      sql.append(" WHERE i.idsubscription = s.idsubscription        ");
      sql.append(" and s.status = 'OK'                              ");
      sql.append(" and i.status = 'OPENED'                         ");
      sql.append(" and i.payment_type IN ('CREDIT_CARD','DEBIT_CARD')   ");
      sql.append(" and i.due_date between :duedateBegin and :duedateEnd ");
      sql.append(" order by i.due_date desc							    ");

      Query query = em.createNativeQuery(sql.toString());
      query.setParameter("duedateBegin", beginDueDate);
      query.setParameter("duedateEnd", endDueDate);

      return query.getResultList();
   }

   @SuppressWarnings("unchecked")
   public List<BigInteger> listInvoicesToRecurrenceReponsibleFinancial(LocalDate beginDueDate, LocalDate endDueDate, Boolean filter) {
      StringBuilder sql = new StringBuilder();
      sql.append(" select distinct i.idinvoice from invoice i, subscription s ");

      sql.append(" WHERE i.idsubscription = s.idsubscription        ");
      sql.append(" and s.status = 'OK'                              ");
      if (!filter) {
         sql.append(" and s.holder_only_reponsible_financial = 0         ");
      }
      else {
         sql.append(" and s.holder_only_reponsible_financial = 1         ");
      }
      sql.append(" and i.status = 'OPENED'                         ");
      sql.append(" and i.payment_type IN ('CREDIT_CARD','DEBIT_CARD')   ");
      sql.append(" and i.due_date between :duedateBegin and :duedateEnd ");
      sql.append(" order by i.due_date desc                        ");

      Query query = em.createNativeQuery(sql.toString());
      query.setParameter("duedateBegin", beginDueDate);
      query.setParameter("duedateEnd", endDueDate);

      return query.getResultList();
   }

   public List<Invoice> listPaidWithoutNfeId() {
      StringBuilder sql = new StringBuilder();
      sql.append(" select i ");
      sql.append(" from Invoice i");
      sql.append(" where i.status = :status ");
      sql.append(" and i.nfeId is null ");

      TypedQuery<Invoice> query = em.createQuery(sql.toString(), Invoice.class);
      query.setParameter("status", InvoiceStatus.PAID);

      try {
         return query.getResultList();
      }
      catch (Exception e) {
         return null;
      }
   }

   public List<Invoice> listPaidWithoutNfeNumber() {
      StringBuilder sql = new StringBuilder();
      sql.append(" select i ");
      sql.append(" from Invoice i");
      sql.append(" where i.status = :status ");
      sql.append(" and i.nfeNumber is null ");
      sql.append(" and i.nfeId is not null ");

      TypedQuery<Invoice> query = em.createQuery(sql.toString(), Invoice.class);
      query.setParameter("status", InvoiceStatus.PAID);

      try {
         return query.getResultList();
      }
      catch (Exception e) {
         return null;
      }
   }

   @SuppressWarnings("unchecked")
   public List<BigInteger> listInvoiceWithoutPaymentAndDue() {
      StringBuilder sql = new StringBuilder();
      sql.append(" select idinvoice                           ");
      sql.append(" from invoice                               ");
      sql.append(" where payment_type in ('TICKET','TICKETS') ");
      sql.append(" and status = 'OPENED'                      ");
      sql.append(" and due_date <= now()                      ");

      Query query = em.createNativeQuery(sql.toString());
      try {
         return query.getResultList();
      }
      catch (Exception e) {
         return null;
      }
   }

   public List<Invoice> findByNsu(String nsu) {
      StringBuilder sql = new StringBuilder();
      sql.append(" select i ");
      sql.append(" from Invoice i");
      sql.append(" where i.transactNsu = :nsu OR i.authorizationCode = :nsu ");

      TypedQuery<Invoice> query = em.createQuery(sql.toString(), Invoice.class);
      query.setParameter("nsu", nsu.trim());

      try {
         return query.getResultList();
      }
      catch (Exception e) {
         return null;
      }
   }

   @SuppressWarnings("unchecked")
   public List<BigInteger> listInvoiceWithoutPaymentAndDueToday() {
      StringBuilder sql = new StringBuilder();
      sql.append(" select idinvoice                           ");
      sql.append(" from invoice                               ");
      sql.append(" where payment_type in ('TICKET','TICKETS') ");
      sql.append(" and status = 'OPENED'                      ");
      sql.append(" and DATE(NOW()) = DATE(due_date)           ");

      Query query = em.createNativeQuery(sql.toString());
      try {
         return query.getResultList();
      }
      catch (Exception e) {
         return null;
      }
   }

   @SuppressWarnings("unchecked")
   public List<BigInteger> ListInvoiceWithoutPaymentAndDue10DaysBefore() {
      StringBuilder sql = new StringBuilder();
      sql.append(" select idinvoice                                       ");
      sql.append(" from invoice                                           ");
      sql.append(" where payment_type in ('TICKET','TICKETS')             ");
      sql.append(" and status = 'OPENED'                                  ");
      sql.append(" and DATE_SUB(due_date , INTERVAL 10 DAY) = DATE(NOW()) ");

      Query query = em.createNativeQuery(sql.toString());
      try {
         return query.getResultList();
      }
      catch (Exception e) {
         return null;
      }
   }

   @SuppressWarnings("unchecked")
   public List<BigInteger> ListInvoiceWithoutPaymentAndDue3Days() {
      StringBuilder sql = new StringBuilder();
      sql.append(" select idinvoice                                       ");
      sql.append(" from invoice                                           ");
      sql.append(" where payment_type in ('TICKET','TICKETS')             ");
      sql.append(" and status = 'OPENED'                                  ");
      sql.append(" and DATE_ADD(due_date , INTERVAL 3 DAY) = DATE(NOW())  ");

      Query query = em.createNativeQuery(sql.toString());
      try {
         return query.getResultList();
      }
      catch (Exception e) {
         return null;
      }
   }

   @SuppressWarnings("unchecked")
   public List<BigInteger> ListInvoiceWithoutPaymentAndDue3DaysCredit() {
      StringBuilder sql = new StringBuilder();
      sql.append(" select idinvoice                                       ");
      sql.append(" from invoice                                           ");
      sql.append(" where payment_type in ('CREDIT_CARD')             ");
      sql.append(" and status = 'OPENED'                                  ");
      sql.append(" and DATE_ADD(due_date , INTERVAL 3 DAY) = DATE(NOW())  ");

      Query query = em.createNativeQuery(sql.toString());
      try {
         return query.getResultList();
      }
      catch (Exception e) {
         return null;
      }
   }

   @SuppressWarnings("unchecked")
   public List<BigInteger> ListInvoiceWithoutPaymentAndDue8Days() {
      StringBuilder sql = new StringBuilder();
      sql.append(" select idinvoice                                       ");
      sql.append(" from invoice                                           ");
      sql.append(" where payment_type in ('TICKET','TICKETS')             ");
      sql.append(" and status = 'OPENED'                                  ");
      sql.append(" and DATE_ADD(due_date , INTERVAL 8 DAY) = DATE(NOW())  ");

      Query query = em.createNativeQuery(sql.toString());
      try {
         return query.getResultList();
      }
      catch (Exception e) {
         return null;
      }
   }

   @SuppressWarnings("unchecked")
   public List<BigInteger> ListInvoiceWithoutPaymentAndDue8DaysCredit() {
      StringBuilder sql = new StringBuilder();
      sql.append(" select idinvoice                                       ");
      sql.append(" from invoice                                           ");
      sql.append(" where payment_type in ('CREDIT_CARD')                  ");
      sql.append(" and status = 'OPENED'                                  ");
      sql.append(" and DATE_ADD(due_date , INTERVAL 8 DAY) = DATE(NOW())  ");

      Query query = em.createNativeQuery(sql.toString());
      try {
         return query.getResultList();
      }
      catch (Exception e) {
         return null;
      }
   }

   @SuppressWarnings("unchecked")
   public List<BigInteger> ListInvoiceWithoutPaymentAndDue13Days() {
      StringBuilder sql = new StringBuilder();
      sql.append(" select idinvoice                                       ");
      sql.append(" from invoice                                           ");
      sql.append(" where payment_type in ('TICKET','TICKETS')             ");
      sql.append(" and status = 'OPENED'                                  ");
      sql.append(" and DATE_ADD(due_date , INTERVAL 13 DAY) = DATE(NOW()) ");

      Query query = em.createNativeQuery(sql.toString());
      try {
         return query.getResultList();
      }
      catch (Exception e) {
         return null;
      }
   }

   @SuppressWarnings("unchecked")
   public List<BigInteger> ListInvoiceWithoutPaymentAndDue13DaysCredit() {
      StringBuilder sql = new StringBuilder();
      sql.append(" select idinvoice                                       ");
      sql.append(" from invoice                                           ");
      sql.append(" where payment_type in ('CREDIT_CARD')             ");
      sql.append(" and status = 'OPENED'                                  ");
      sql.append(" and DATE_ADD(due_date , INTERVAL 13 DAY) = DATE(NOW()) ");

      Query query = em.createNativeQuery(sql.toString());
      try {
         return query.getResultList();
      }
      catch (Exception e) {
         return null;
      }
   }

   @SuppressWarnings("unchecked")
   public List<BigInteger> ListInvoiceWithoutPaymentAndDue18Days() {
      StringBuilder sql = new StringBuilder();
      sql.append(" select idinvoice                                       ");
      sql.append(" from invoice                                           ");
      sql.append(" where payment_type in ('TICKET','TICKETS')             ");
      sql.append(" and status = 'OPENED'                                  ");
      sql.append(" and DATE_ADD(due_date , INTERVAL 18 DAY) = DATE(NOW()) ");

      Query query = em.createNativeQuery(sql.toString());
      try {
         return query.getResultList();
      }
      catch (Exception e) {
         return null;
      }
   }

   @SuppressWarnings("unchecked")
   public List<BigInteger> ListInvoiceWithoutPaymentAndDue18DaysCredit() {
      StringBuilder sql = new StringBuilder();
      sql.append(" select idinvoice                                       ");
      sql.append(" from invoice                                           ");
      sql.append(" where payment_type in ('CREDIT_CARD')             ");
      sql.append(" and status = 'OPENED'                                  ");
      sql.append(" and DATE_ADD(due_date , INTERVAL 18 DAY) = DATE(NOW()) ");

      Query query = em.createNativeQuery(sql.toString());
      try {
         return query.getResultList();
      }
      catch (Exception e) {
         return null;
      }
   }

   public Boolean validPayInvoice(Long invoiceId, Long idSubs, LocalDate dueDate) {

      StringBuilder sql = new StringBuilder();
      sql.append(" select i ");
      sql.append(" from Invoice i");
      sql.append(" where i.status = 'OPENED' ");
      sql.append(" and i.dueDate < :dueDate ");
      sql.append(" and i.subscription.id = :idSubs ");

      TypedQuery<Invoice> query = em.createQuery(sql.toString(), Invoice.class);
      query.setParameter("dueDate", dueDate);
      query.setParameter("idSubs", idSubs);

      try {
         if (query.getResultList() != null) {
            return query.getResultList().size() > 0;
         }
         return Boolean.FALSE;
      }
      catch (Exception e) {
         return null;
      }
   }

   public List<Invoice> filterInvoice(SubscriptionFilter filter) {

      StringBuilder sql = new StringBuilder();
      sql.append(" select distinct i ");
      sql.append(" from Invoice i");
      sql.append(" where i.subscription.id = :idSubs ");
      sql.append(" and i.status = 'PAID' ");

      sql.append(" and (NOT i.competenceBegin > :dateEnd) ");
      sql.append(" and (NOT i.competenceEnd < :dateBegin) ");

      TypedQuery<Invoice> query = em.createQuery(sql.toString(), Invoice.class);
      query.setParameter("idSubs", filter.getIdSubscription());
      query.setParameter("dateBegin", filter.getDateBegin());
      query.setParameter("dateEnd", filter.getDateEnd());

      try {
         return query.getResultList();
      }
      catch (Exception e) {
         return null;
      }
   }

}
