
package br.com.segmedic.clubflex.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import br.com.segmedic.clubflex.domain.InvoiceLog;
import br.com.segmedic.clubflex.domain.Subscription;
import br.com.segmedic.clubflex.domain.enums.Functionality;
import br.com.segmedic.clubflex.domain.enums.InvoiceType;
import br.com.segmedic.clubflex.domain.enums.PaymentType;
import br.com.segmedic.clubflex.model.DebtReportCreditCardDetail;
import br.com.segmedic.clubflex.model.DebtReportDetail;
import br.com.segmedic.clubflex.model.DebtReportFilter;
import br.com.segmedic.clubflex.model.DebtReportTotal;
import br.com.segmedic.clubflex.model.LeadReport;
import br.com.segmedic.clubflex.model.MotionDetail;
import br.com.segmedic.clubflex.model.MotionReportFilter;
import br.com.segmedic.clubflex.model.OperationalReportFilter;
import br.com.segmedic.clubflex.model.ProductivityReportFilter;
import br.com.segmedic.clubflex.model.ProductivityReportObject;
import br.com.segmedic.clubflex.model.ReceiptDetail;
import br.com.segmedic.clubflex.model.ReceiptReport;
import br.com.segmedic.clubflex.model.SubscriptionDetail;
import br.com.segmedic.clubflex.model.SubscriptionReportFilter;
import br.com.segmedic.clubflex.model.UserByPlanReportFilter;
import br.com.segmedic.clubflex.model.UserByPlanReportPlan;

@Repository
public class ReportRepository extends GenericRepository {

   @Autowired
   private EntityManager em;

   @Autowired
   private InvoiceLogRepository invoiceLogRepository;

   @Autowired
   private SubscriptionRepository subscriptionRepository;

   private BigDecimal somaValorReceber = BigDecimal.ZERO;

   @Transactional
   public ReceiptReport receiptReport(ReceiptReport report, OperationalReportFilter filter) {
      // Recebidos
      String sql = " SELECT new br.com.segmedic.clubflex.model.ReceiptDetail(s.id, h.name, p.name, s.paymentType, i.id, "
         + " i.type, i.paymentType, i.payAmount, i.paymentDate, h.cellPhone, h.neighborhood, h.zipcode) "
         + " FROM Invoice i, Subscription s, Plan p, Holder h "
         + " WHERE s.holder.id=h.id "
         + " AND i.subscription.id=s.id "
         + " AND s.plan.id=p.id "
         + " AND i.status = 'PAID' "
         + " AND i.paymentDate between :dateBegin and :dateEnd ";
      TypedQuery<ReceiptDetail> queryRecebidos = em.createQuery(sql, ReceiptDetail.class);

      // Query queryRecebidos = em.createNativeQuery(sql.toString());
      queryRecebidos.setParameter("dateBegin", filter.getDateBegin());
      queryRecebidos.setParameter("dateEnd", filter.getDateEnd());
      report.setDetailsReceived(queryRecebidos.getResultList());
      return report;
   }

   @SuppressWarnings("unchecked")
   @Transactional
   public List<ReceiptDetail> listSubscriptionActiveNotTickets(LocalDate dateBegin, LocalDate dateEnd) {

      List<ReceiptDetail> listReceiptDetail = new ArrayList<ReceiptDetail>();

      String sql =

         " SELECT "
            + " s.idsubscription, h.name as holder_name, p.name as plan_name, s.payment_type, h.cellphone, h.neighborhood, h.zipcode, i.idinvoice, i.amount, i.invoice_type, i.payment_type as invoice_pt, i.due_date,  "
            + "  CASE  "
            + "     WHEN s.type_sub = 'PJ' THEN pj.totalPreco "
            + "     WHEN s.type_sub = 'PF' and s.holder_only_reponsible_financial = '1' THEN pf1.precoDep - pf2.precoDep + pf3.precoHldr "
            + "    WHEN s.type_sub = 'PF' and s.holder_only_reponsible_financial = '0' THEN pf4.preco "
            + "  END valorPRECO"
            + " FROM "
            + "   subscription s "
            + "   join invoice i on  i.idsubscription = s.idsubscription "
            + "  join plan p on p.idplan = s.idplan "
            + "  join holder h on s.idholder = h.idholder "
            + "  left join "
            + "  ( "
            + "     SELECT "
            + "        s.idsubscription, "
            + "        SUM(CASE cc.flag_card_holder WHEN '1' THEN p.price_holder WHEN '0' THEN p.price_dependent END) AS totalPreco "
            + "     FROM "
            + "         subscription s, "
            + "          club_card cc, "
            + "          plan p "
            + "      WHERE "
            + "         s.idsubscription = cc.idsubscription "
            + "          AND p.idplan     = s.idplan "
            + "          AND cc.status    = 'OK' "
            + "       group by "
            + "          s.idsubscription "
            + "    ) pj on pj.idsubscription = s.idsubscription "
            + "       left join "
            + "    ( "
            + "     SELECT "
            + "         s.idsubscription, "
            + "         sum(p.price_dependent) as precoDep "
            + "      FROM "
            + "         subscription s, "
            + "        dependent d, "
            + "           plan p "
            + "        WHERE "
            + "           s.idsubscription = d.idsubscription "
            + "          AND p.idplan = s.idplan "
            + "           AND d.status = 'OK' "
            + "        group by "
            + "          s.idsubscription "
            + "     ) pf1 on pf1.idsubscription = s.idsubscription "
            + "     left join "
            + "    ( "
            + "        SELECT "
            + "          s.idsubscription, "
            + "          sum(p.price_dependent) as precoDep "
            + "      FROM "
            + "          subscription s, "
            + "          plan p "
            + "       WHERE "
            + "         p.idplan = s.idplan "
            + "      GROUP BY "
            + "          s.idsubscription "
            + "    ) pf2 on pf2.idsubscription = s.idsubscription "
            + "       left join "
            + "   ( "
            + "      SELECT "
            + "         s.idsubscription, "
            + "          sum(p.price_holder) as precoHldr "
            + "       FROM "
            + "          subscription s, "
            + "          plan p "
            + "       WHERE "
            + "          p.idplan = s.idplan "
            + "       GROUP BY "
            + "           s.idsubscription "
            + "      ) pf3 on pf3.idsubscription = s.idsubscription "
            + "       left join "
            + "    ( "
            + "       SELECT "
            + "          idsubscription, "
            + "        sum(preco) preco "
            + "      FROM "
            + "       ( "
            + "           SELECT "
            + "         s.idsubscription, "
            + "      p.price_dependent as preco "
            + "         FROM "
            + "            subscription s, "
            + "             dependent d, "
            + "              plan p "
            + "           WHERE "
            + "             s.idsubscription = d.idsubscription "
            + "            AND p.idplan = s.idplan "
            + "             AND d.status = 'OK' "
            + "        UNION "
            + "        SELECT "
            + "            s.idsubscription, "
            + "           p.price_holder as preco "
            + "         FROM "
            + "             subscription s, "
            + "             holder h, "
            + "            plan p "
            + "           WHERE "
            + "            s.idholder = h.idholder "
            + "             AND p.idplan = s.idplan "
            + "        ) base "
            + "        GROUP BY "
            + "         idsubscription "
            + "     ) pf4 on pf4.idsubscription = s.idsubscription "
            + "   WHERE "
            + "      s.status = 'OK' and "
            + "    s.payment_type <> 'TICKETS' and "
            + " i.payment_date is null AND "
            + " i.status <> 'CANCELLED'and "
            + "    i.due_date BETWEEN  :dateBegin and :dateEnd "
            + "    ORDER BY s.idsubscription ";

      try {
         List<Object[]> data = em.createNativeQuery(sql.toString())
                  .setParameter("dateBegin", dateBegin).setParameter("dateEnd", dateEnd).getResultList();

         if (data != null && !data.isEmpty()) {
            data.forEach(o -> {

               ReceiptDetail rDetail =
                  new ReceiptDetail(getLongValue(o, 1),
                     getStringValue(o, 2),
                     getStringValue(o, 3),
                     PaymentType.valueOf(getStringValue(o, 4)),
                     getLongValue(o, 8),
                     InvoiceType.valueOf(getStringValue(o, 10)),
                     PaymentType.valueOf(getStringValue(o, 11)),
                     getBigDecimalValue(o, 9),
                     getLocalDate(o, 12),
                     getStringValue(o, 5),
                     getStringValue(o, 6),
                     getStringValue(o, 7),
                     getBigDecimalValue(o, 13));

               listReceiptDetail.add(rDetail);

            });
         }

         return listReceiptDetail;

      }
      catch (Exception e) {
         return null;
      }
   }

   @Transactional
   public ReceiptReport toReceiptReport(ReceiptReport report, BigDecimal somaValorReceber, OperationalReportFilter filter) {

      // A Receber - SOMENTE CARNÊS
      String sql = " SELECT new br.com.segmedic.clubflex.model.ReceiptDetail(s.id, h.name, p.name, s.paymentType, i.id, "
         + " i.type, i.paymentType, i.amount, i.dueDate, h.cellPhone, h.neighborhood, h.zipcode) "
         + " FROM Invoice i, Subscription s, Plan p, Holder h "
         + " WHERE s.holder.id=h.id "
         + " AND i.paymentType = 'TICKETS' "
         + " AND i.subscription.id=s.id "
         + " AND s.plan.id=p.id "
         + " AND i.status = 'OPENED' AND i.dueDate >= CURDATE() AND i.dueDate between :dateBegin and :dateEnd ";
      TypedQuery<ReceiptDetail> queryReceber = em.createQuery(sql, ReceiptDetail.class);
      queryReceber.setParameter("dateBegin", filter.getDateBegin());
      queryReceber.setParameter("dateEnd", filter.getDateEnd());
      report.setDetailsReceivable(queryReceber.getResultList());

      // A Receber - DEMAIS FORMAS DE PAGAMENTO (MENOS CARNE)
      List<ReceiptDetail> listRec = listSubscriptionActiveNotTickets(filter.getDateBegin(), filter.getDateEnd());
      for (ReceiptDetail rec : listRec) {
         report.getDetailsReceivable().add(rec);
         this.somaValorReceber = this.somaValorReceber.add(rec.getPayAmount());
      }
      return report;
   }

   @Transactional
   public ReceiptReport defaultingReport(ReceiptReport report, OperationalReportFilter filter) {

      String sql = " SELECT new br.com.segmedic.clubflex.model.ReceiptDetail(s.id, h.name, p.name, s.paymentType, i.id, "
         + " i.type, i.paymentType, i.amount, i.dueDate, h.cellPhone, h.neighborhood, h.zipcode) "
         + " FROM Invoice i, Subscription s, Plan p, Holder h "
         + " WHERE s.holder.id=h.id "
         + " AND i.subscription.id=s.id "
         + " AND s.plan.id=p.id "
         + " AND i.status = 'OPENED' AND i.dueDate < CURDATE() AND i.dueDate between :dateBegin and :dateEnd ";
      TypedQuery<ReceiptDetail> queryInadimplentes = em.createQuery(sql, ReceiptDetail.class);
      queryInadimplentes.setParameter("dateBegin", filter.getDateBegin());
      queryInadimplentes.setParameter("dateEnd", filter.getDateEnd());
      report.setDetailsDefaulters(queryInadimplentes.getResultList());

      return report;

   }

   @Transactional
   public ReceiptReport sumReceiptValues(ReceiptReport report, OperationalReportFilter filter) {

      // Soma Valores Recebidos
      String sql = " SELECT SUM(i.pay_amount) as totalSoma "
         + " FROM invoice i, subscription s, plan p, holder h "
         + " WHERE s.idHolder=h.idHolder "
         + " AND i.idSubscription=s.idSubscription "
         + " AND s.idPlan=p.idPlan AND i.status = 'PAID' "
         + " AND i.payment_date between :dateBegin and :dateEnd ";
      Query queryValorRecebidos = em.createNativeQuery(sql);
      queryValorRecebidos.setParameter("dateBegin", filter.getDateBegin());
      queryValorRecebidos.setParameter("dateEnd", filter.getDateEnd());
      if (queryValorRecebidos.getSingleResult() == null) {
         report.setTotalValueReceived(BigDecimal.ZERO);
      }
      else {
         report.setTotalValueReceived((BigDecimal) queryValorRecebidos.getSingleResult());
      }

      return report;
   }

   @Transactional
   public ReceiptReport sumToReceiveValuesOnlyTickets(ReceiptReport report, BigDecimal somaValorReceber, OperationalReportFilter filter) {

      // Soma Valores A Receber - SOMENTE CARNÊS
      String sql = " SELECT SUM(a.amount) as totalSoma "
         + " FROM invoice a WHERE a.status = 'OPENED' "
         + " AND a.due_date >= CURDATE() "
         + " AND a.due_date between :dateBegin and :dateEnd ";
      Query queryValorReceber = em.createNativeQuery(sql);
      queryValorReceber.setParameter("dateBegin", filter.getDateBegin());
      queryValorReceber.setParameter("dateEnd", filter.getDateEnd());
      if (queryValorReceber.getSingleResult() == null) {
         report.setTotalValueReceivable(BigDecimal.ZERO);
      }
      else {
         report.setTotalValueReceivable((BigDecimal) queryValorReceber.getSingleResult());
      }

      // Soma Valores A Receber - DEMAIS FORMAS DE PAGAMENTO (MENOS CARNE)
      report.setTotalValueReceivable(report.getTotalValueReceivable().add(somaValorReceber));

      return report;

   }

   @Transactional
   public ReceiptReport sumDefaultingReport(ReceiptReport report, OperationalReportFilter filter) {

      // Soma Valores Inadimplentes
      String sql = " SELECT SUM(a.amount) as totalSoma "
         + " FROM invoice a WHERE a.status = 'OPENED' "
         + " AND a.due_date < CURDATE() "
         + " AND a.due_date between :dateBegin and :dateEnd ";
      Query queryValorInadimplentes = em.createNativeQuery(sql);
      queryValorInadimplentes.setParameter("dateBegin", filter.getDateBegin());
      queryValorInadimplentes.setParameter("dateEnd", filter.getDateEnd());
      if (queryValorInadimplentes.getSingleResult() == null) {
         report.setTotalValueDefaulters(BigDecimal.ZERO);
      }
      else {
         report.setTotalValueDefaulters((BigDecimal) queryValorInadimplentes.getSingleResult());
      }
      return report;
   }

   public ReceiptReport generateReceiptReport(OperationalReportFilter filter) {

      ReceiptReport report = new ReceiptReport();

      try {
         this.somaValorReceber = BigDecimal.ZERO;
         receiptReport(report, filter);
         toReceiptReport(report, this.somaValorReceber, filter);
         defaultingReport(report, filter);
         sumReceiptValues(report, filter);
         sumToReceiveValuesOnlyTickets(report, this.somaValorReceber, filter);
         sumDefaultingReport(report, filter);

      }
      catch (Exception exc) {
         System.out.println(exc);
      }

      return report;
   }

   @SuppressWarnings("unchecked")
   public List<MotionDetail> generateMotionReport(MotionReportFilter filter, boolean isDetail) {
      StringBuilder sql = new StringBuilder();
      sql.append("select 																				");
      sql.append("a.idinvoice, 																		");
      sql.append("a.invoice_type,																	    ");
      sql.append("a.data_begin,																	    ");
      sql.append("a.data_end, 																		");
      sql.append("a.pay_amount, 																		");
      sql.append("a.due_date, 																		");
      sql.append("a.payment_date,																	    ");
      sql.append("DATEDIFF(a.payment_date, a.due_date) as delay, 									    ");
      sql.append("UPPER(c.name) as holdername, 														");
      sql.append("c.cpf_cnpj as holdercpf, 															");
      sql.append("a.payment_type,																		");
      sql.append("a.idsubscription, 																    ");
      sql.append("a.nfe_number, 																        ");
      sql.append("a.authorization_code, 																");
      sql.append("d.name, 																			");
      sql.append("a.transact_gateway_id, 																");
      sql.append("a.transact_nsu,																		");
      sql.append("a.amount, 																			");
      sql.append("a.date_time_registered_log															");
      sql.append("from invoice a inner join subscription b on a.idsubscription = b.idsubscription     ");
      sql.append("               inner join holder c on b.idholder = c.idholder               		");
      sql.append("               left join user d on d.iduser = a.iduser               		        ");
      sql.append("where a.status = 'PAID' 															");
      sql.append("and a.payment_date between :dateBegin and :dateEnd									");

      if (filter.getSubscriptionId() != null) {
         sql.append("and a.idsubscription = :subscriptionId 											");
      }

      if (filter.getUserResponsiblePayment() != null) {
         if (filter.getUserResponsiblePayment() == 0) {
            sql.append("and a.iduser is null  														");
         }
         else {
            sql.append("and a.iduser = :iduser 														");
         }
      }

      sql.append("order by a.payment_date																");

      Query query = em.createNativeQuery(sql.toString());
      query.setParameter("dateBegin", filter.getDateBegin());
      query.setParameter("dateEnd", filter.getDateEnd());

      if (filter.getSubscriptionId() != null) {
         query.setParameter("subscriptionId", filter.getSubscriptionId());
      }

      if (filter.getUserResponsiblePayment() != null && filter.getUserResponsiblePayment() != 0) {
         query.setParameter("iduser", filter.getUserResponsiblePayment());
      }

      List<Object[]> objects = query.getResultList();
      List<MotionDetail> details = Lists.newArrayList();

      objects.forEach(o -> {
         MotionDetail motion = new MotionDetail();

         Long idInvoice = getLongValue(o, 1);
         BigDecimal valorOriginal = getBigDecimalValue(o, 18);
         String usuario = getStringValue(o, 15);
         LocalDate dataHoraRegistro = null;

         if (o[18] != null) {
            dataHoraRegistro = LocalDate.parse(o[18].toString().substring(0, 10));
         }

         motion.setInvoiceId(idInvoice);
         motion.setInvoiceType(InvoiceType.valueOf(getStringValue(o, 2)));
         motion.setCompetenceBegin(getLocalDate(o, 3));
         motion.setCompetenceEnd(getLocalDate(o, 4));
         motion.setAmountPaid(getBigDecimalValue(o, 5));
         motion.setDueDate(getLocalDate(o, 6));
         motion.setPaymentDate(getLocalDate(o, 7));
         motion.setDelay(getIntegerValue(o, 8));
         motion.setHolderName(getStringValue(o, 9));
         motion.setHolderCpf(getStringValue(o, 10));
         motion.setPaymentType(PaymentType.valueOf(getStringValue(o, 11)));
         motion.setSubscriptionId(getLongValue(o, 12));
         motion.setNfeNumber(getStringValue(o, 13));
         motion.setAuthorizationCode(getStringValue(o, 14));
         motion.setUserResponsable(getStringValue(o, 15));
         motion.setTransactionId(getStringValue(o, 16));
         motion.setNsu(getStringValue(o, 17));
         details.add(motion);

         if (isDetail) {
            List<InvoiceLog> listaLog = invoiceLogRepository.listInvoiceLogByInvoice(idInvoice);

            listaLog.forEach(log -> {

               MotionDetail motionLog = new MotionDetail();

               // Repetição dos dados da fatura
               motionLog.setInvoiceId(motion.getInvoiceId());
               motionLog.setCompetenceBegin(motion.getCompetenceBegin());
               motionLog.setCompetenceEnd(motion.getCompetenceEnd());
               motionLog.setDueDate(motion.getDueDate());
               motionLog.setHolderName(motion.getHolderName());
               motionLog.setHolderCpf(motion.getHolderCpf());
               motionLog.setDelay(motion.getDelay());
               motionLog.setPaymentType(motion.getPaymentType());
               motionLog.setSubscriptionId(motion.getSubscriptionId());
               motionLog.setAuthorizationCode(motion.getAuthorizationCode());
               motionLog.setNsu(motion.getNsu());

               motionLog.setDescription(log.getDescription());
               motionLog.setAmountPaid(log.getAmount());
               LocalDate dataRegistro = LocalDate.of(log.getDateRegistry().getYear(), log.getDateRegistry().getMonthValue(),
                  log.getDateRegistry().getDayOfMonth());
               motionLog.setPaymentDate(dataRegistro);
               motionLog.setUserResponsable(log.getUser().getName());
               details.add(motionLog);
            });

            if (listaLog.size() > 0) {
               MotionDetail motionLog = new MotionDetail();

               // Repetição dos dados da fatura
               motionLog.setInvoiceId(motion.getInvoiceId());
               motionLog.setCompetenceBegin(motion.getCompetenceBegin());
               motionLog.setCompetenceEnd(motion.getCompetenceEnd());
               motionLog.setDueDate(motion.getDueDate());
               motionLog.setHolderName(motion.getHolderName());
               motionLog.setHolderCpf(motion.getHolderCpf());
               motionLog.setDelay(motion.getDelay());
               motionLog.setPaymentType(motion.getPaymentType());
               motionLog.setSubscriptionId(motion.getSubscriptionId());
               motionLog.setAuthorizationCode(motion.getAuthorizationCode());
               motionLog.setNsu(motion.getNsu());

               motionLog.setDescription(Functionality.CRIAR_FATURA.getDescribe());
               motionLog.setAmountPaid(valorOriginal);
               motionLog.setPaymentDate(dataHoraRegistro);
               motionLog.setUserResponsable(usuario);
               details.add(motionLog);
            }
         }
      });

      return details;
   }

   @SuppressWarnings("unchecked")
   public List<SubscriptionDetail> generateSubscriptionReport(SubscriptionReportFilter filter) {
      StringBuilder sql = new StringBuilder();

      sql.append(" SELECT sl.idsubscription, s.payment_type, UPPER(h.name) as holdername, ");
      sql.append(
         " CASE s.status WHEN 'OK' THEN 'Ativa' WHEN 'BLOCKED' THEN 'Bloqueada' WHEN 'CANCELED' THEN 'Cancelada' WHEN 'OUT_OF_DATE' THEN 'Fora de Vigência' ELSE '-' END as status, ");
      sql.append(" s.data_begin, p.name as namePlan, sl.obs, DATE_FORMAT(sl.date_time_log, '%d/%m/%Y %H:%i:%s') as dateTimeLog, u.name ");
      sql.append(" FROM subscription_log sl, subscription s, plan p, holder h, user u ");
      sql.append(" WHERE sl.idsubscription = s.idsubscription ");
      sql.append(" AND s.idplan = p.idplan ");
      sql.append(" AND h.idholder = s.idholder ");
      sql.append(" AND u.iduser = sl.iduser ");
      sql.append(" AND sl.date_time_log between :dateBegin and :dateEnd ");
      sql.append(" AND sl.action = 'ATUALIZACAO_FORMA_PAGAMENTO' ");

      if (filter.getUserResponsiblePayment() != null) {
         if (filter.getUserResponsiblePayment() == 0) {
            sql.append(" and sl.iduser is null ");
         }
         else {
            sql.append(" and sl.iduser = :iduser ");
         }
      }

      sql.append("order by date_time_log desc");

      Query query = em.createNativeQuery(sql.toString());
      query.setParameter("dateBegin", filter.getDateBegin());
      query.setParameter("dateEnd", filter.getDateEnd());

      if (filter.getUserResponsiblePayment() != null && filter.getUserResponsiblePayment() != 0) {
         query.setParameter("iduser", filter.getUserResponsiblePayment());
      }

      List<Object[]> objects = query.getResultList();
      List<SubscriptionDetail> details = Lists.newArrayList();

      objects.forEach(o -> {
         SubscriptionDetail detail = new SubscriptionDetail();

         detail.setSubscriptionId(getLongValue(o, 1));
         detail.setPaymentType(PaymentType.valueOf(getStringValue(o, 2)));
         detail.setHolderName(getStringValue(o, 3));
         detail.setStatus(getStringValue(o, 4));
         detail.setDateBegin(getLocalDate(o, 5));
         detail.setPlan(getStringValue(o, 6));
         detail.setLog(getStringValue(o, 7));

         detail.setDateLog(getStringValue(o, 8));
         detail.setUserResponsable(getStringValue(o, 9));

         Subscription sub = subscriptionRepository.findById(detail.getSubscriptionId());
         detail.setAmount(sub.getTotalPriceFmt());
         detail.setNumberDependents(sub.getTotalDependetsOk());

         details.add(detail);
      });
      return details;
   }

   @SuppressWarnings("unchecked")
   public List<DebtReportDetail> generateDetailDebtReport(Integer days) {

      StringBuilder sql = new StringBuilder();
      sql.append(" select  																			");
      sql.append(" UCASE(c.idholder) as holderId,														");
      sql.append(" UCASE(c.name) as holderName,														");
      sql.append(" a.idsubscription,  																");
      sql.append(" sum(a.amount) as total_amount,  													");
      sql.append(" count(*) as invoices_opened,														");
      sql.append(" c.cellphone, 																		");
      sql.append(" c.homephone, 																		");
      sql.append(" c.email,																			");
      sql.append(" b.payment_type 																	");
      sql.append(" from invoice a inner join subscription b on a.idsubscription = b.idsubscription 	");
      sql.append(" 	            inner join holder c on b.idholder = c.idholder 				        ");
      sql.append(" where a.status = 'OPENED' 															");
      sql.append(" and a.due_date <= :dateYesterday 													");
      sql.append(" and a.payment_type <> 'CREDIT_CARD' 													");
      sql.append(" group by a.idsubscription  														");
      sql.append(" order by c.name  																	");

      List<Object[]> objects = em.createNativeQuery(sql.toString())
               .setParameter("dateYesterday", LocalDate.now().minusDays(days))
               .getResultList();

      List<DebtReportDetail> details = Lists.newArrayList();

      objects.forEach(o -> {
         DebtReportDetail detail = new DebtReportDetail();
         detail.setHolderId(getLongValue(o, 1));
         detail.setHolderName(getStringValue(o, 2));
         detail.setSubscriptionId(getLongValue(o, 3));
         detail.setAmountDebt(getBigDecimalValue(o, 4));
         detail.setQuantityInvoices(getIntegerValue(o, 5));
         detail.setCellphone(getStringValue(o, 6));
         detail.setHomephone(getStringValue(o, 7));
         detail.setMail(getStringValue(o, 8));
         detail.setPaymentType(PaymentType.valueOf(getStringValue(o, 9)).getDescribe());
         details.add(detail);
      });
      return details;
   }

   @SuppressWarnings("unchecked")
   public List<DebtReportCreditCardDetail> generateCreditCardReport(DebtReportFilter filter) {

      Map<String, Object> params = Maps.newConcurrentMap();
      StringBuilder sql = new StringBuilder();
      sql.append(" select  																			");
      sql.append(" UCASE(c.idholder) as holderId,												");
      sql.append(" UCASE(c.name) as holderName,													");
      sql.append(" a.idsubscription,  																");
      sql.append(" a.amount,  													               ");
      sql.append(" c.cellphone, 																		");
      sql.append(" c.homephone, 																		");
      sql.append(" c.email,																			");
      sql.append(" b.payment_type, 																	");
      sql.append(" cc.brand,                                                        ");
      sql.append(" cc.final_number,                                                 ");
      sql.append(" cc.last_return_code,                                             ");
      sql.append(" DATE_FORMAT(cc.date_last_recurrency,'%d/%m/%Y') as dateLast,     ");
      sql.append(" cc.recurrency,                                                   ");
      if (filter.isLastInvoice())
         sql.append(" MAX(a.idinvoice) as idinvoice                                 ");
      else
         sql.append(" a.idinvoice                                                   ");
      sql.append(" from invoice a inner join subscription b on a.idsubscription = b.idsubscription 	 ");
      sql.append(" 	            inner join holder c on b.idholder = c.idholder 				             ");
      sql.append(" 	            inner join plan p on b.idplan = p.idplan 				                   ");
      sql.append(" 	            left join credit_card cc on cc.idholder = c.idholder  		          ");
      sql.append(" where a.status = 'OPENED' 															             ");
      sql.append(" and cc.last_return_code <> 00   													             ");
      sql.append(" and a.payment_type = 'CREDIT_CARD'                                                  ");
      if (filter.getDateBegin() != null) {
         sql.append(" and cc.date_last_recurrency >= :dataInicio ");
         params.put("dataInicio", filter.getDateBegin());
      }
      if (filter.getDateEnd() != null) {
         sql.append(" and cc.date_last_recurrency <= :dataFim ");
         params.put("dataFim", filter.getDateEnd());
      }
      if (filter.getPlan() != null) {
         sql.append(" and p.idplan = :idPlan ");
         params.put("idPlan", filter.getPlan());
      }
      if (filter.getCodes() != null && filter.getCodes().length > 0) {
         String codigos = "";
         for (int i = 0; i < filter.getCodes().length; i++) {
            codigos += filter.getCodes()[i] + ", ";
         }
         sql.append(" and cc.last_return_code IN (" + codigos.substring(0, codigos.length() - 2) + ") ");
      }
      sql.append(" and a.due_date <= :dateYesterday ");
      params.put("dateYesterday", LocalDate.now().minusDays(filter.getDays()));

      if (filter.isLastInvoice()) {
         sql.append(" group by b.idsubscription ");
      }
      Query query = em.createNativeQuery(sql.toString() + " order by c.name");

      params.entrySet().forEach(p -> {
         query.setParameter(p.getKey(), p.getValue());
      });

      List<Object[]> objects = query.getResultList();

      List<DebtReportCreditCardDetail> details = Lists.newArrayList();

      objects.forEach(o -> {
         DebtReportCreditCardDetail detail = new DebtReportCreditCardDetail();
         detail.setHolderId(getLongValue(o, 1));
         detail.setHolderName(getStringValue(o, 2));
         detail.setSubscriptionId(getLongValue(o, 3));
         detail.setAmountDebt(getBigDecimalValue(o, 4));
         detail.setCellphone(getStringValue(o, 5));
         detail.setHomephone(getStringValue(o, 6));
         detail.setMail(getStringValue(o, 7));
         detail.setPaymentType(PaymentType.valueOf(getStringValue(o, 8)).getDescribe());
         detail.setBrand(getStringValue(o, 9));
         detail.setFinalCard(getStringValue(o, 10));
         detail.setLastReturnCode(getStringValue(o, 11));
         detail.setDateLastTry(getStringValue(o, 12));
         detail.setNumTries(getIntegerValue(o, 13));
         detail.setInvoiceId(getLongValue(o, 14));
         details.add(detail);
      });
      return details;
   }

   @SuppressWarnings("unchecked")
   public List<DebtReportTotal> generateTotalDebtReport(DebtReportFilter filter) {

      Map<String, Object> params = Maps.newConcurrentMap();
      StringBuilder sql = new StringBuilder();
      sql.append(" select  					          ");
      sql.append(" a.payment_type,    	      ");
      sql.append(" sum(a.amount) as amount   	      ");
      sql.append(" from invoice a inner join subscription b on a.idsubscription = b.idsubscription     ");
      sql.append("               inner join holder c on b.idholder = c.idholder                        ");
      sql.append("               left join credit_card cc on cc.idholder = c.idholder                  ");
      sql.append(" where a.status = 'OPENED'  	  ");

      if (filter.getDateBegin() != null) {
         sql.append(" and cc.date_last_recurrency >= :dataInicio ");
         params.put("dataInicio", filter.getDateBegin());
      }
      if (filter.getDateEnd() != null) {
         sql.append(" and cc.date_last_recurrency <= :dataFim ");
         params.put("dataFim", filter.getDateEnd());
      }
      if (filter.getCodes() != null && filter.getCodes().length > 0) {
         sql.append(" and cc.last_return_code IN (:codigos) ");
         params.put("codigos", filter.getCodes());
      }

      sql.append(" and a.due_date <= :dateYesterday ");
      params.put("dateYesterday", LocalDate.now().minusDays(filter.getDays()));

      Query query = em.createNativeQuery(sql.toString() + " group by a.payment_type");

      params.entrySet().forEach(p -> {
         query.setParameter(p.getKey(), p.getValue());
      });

      List<Object[]> objects = query.getResultList();

      List<DebtReportTotal> totais = Lists.newArrayList();

      objects.forEach(o -> {
         DebtReportTotal total = new DebtReportTotal();
         total.setPaymentType(PaymentType.valueOf(getStringValue(o, 1)));
         total.setTotal(getBigDecimalValue(o, 2));
         totais.add(total);
      });
      return totais;
   }

   @SuppressWarnings("unchecked")
   public List<LeadReport> generateLeadReport() {
      List<Object[]> objects = em.createNativeQuery(
         "select idholder, name, cpf_cnpj, email, cellphone from holder where idholder not in (select idholder from subscription) order by name")
               .getResultList();

      List<LeadReport> report = Lists.newArrayList();
      objects.forEach(o -> {
         LeadReport lead = new LeadReport();
         lead.setHolderId(getLongValue(o, 1));
         lead.setName(getStringValue(o, 2));
         lead.setCpfCnpj(getStringValue(o, 3));
         lead.setEmail(getStringValue(o, 4));
         lead.setCellphone(getStringValue(o, 5));
         report.add(lead);
      });
      return report;
   }

   @SuppressWarnings("unchecked")
   public List<ProductivityReportObject> generateReportProductivityBrokers(ProductivityReportFilter filter) {
      StringBuilder sql = new StringBuilder();
      sql.append(" select u.name, count(*) as total ");
      sql.append(" from subscription s left join broker u on s.idbroker = u.idbroker ");
      sql.append(" where s.date_registred between :dataini and :datafim ");
      // sql.append(" and s.status <> 'CANCELED' ");
      sql.append(" group by u.idbroker ");
      sql.append(" order by u.name ");

      List<Object[]> objects = em.createNativeQuery(sql.toString())
               .setParameter("dataini", filter.getDateBegin())
               .setParameter("datafim", filter.getDateEnd())
               .getResultList();

      List<ProductivityReportObject> report = Lists.newArrayList();
      objects.forEach(o -> {
         ProductivityReportObject object = new ProductivityReportObject();
         object.setDescribe(getStringValue(o, 1));
         object.setQuantity(getLongValue(o, 2));
         report.add(object);
      });

      return report;
   }

   @SuppressWarnings("unchecked")
   public List<ProductivityReportObject> generateReportProductivityCompanies(ProductivityReportFilter filter) {
      StringBuilder sql = new StringBuilder();
      sql.append(" select c.name, count(*) as total  ");
      sql.append(" from subscription s left join company c on s.idcompany = c.idcompany ");
      sql.append(" where s.date_registred between :dataini and :datafim ");
      // sql.append(" and s.status <> 'CANCELED' ");
      sql.append(" group by s.idcompany  ");
      sql.append(" order by c.name ");

      List<Object[]> objects = em.createNativeQuery(sql.toString())
               .setParameter("dataini", filter.getDateBegin())
               .setParameter("datafim", filter.getDateEnd())
               .getResultList();

      List<ProductivityReportObject> report = Lists.newArrayList();
      objects.forEach(o -> {
         ProductivityReportObject object = new ProductivityReportObject();
         object.setDescribe(getStringValue(o, 1));
         object.setQuantity(getLongValue(o, 2));
         report.add(object);
      });

      return report;
   }

   @SuppressWarnings("unchecked")
   public List<ProductivityReportObject> generateReportProductivityUsers(ProductivityReportFilter filter) {
      StringBuilder sql = new StringBuilder();
      sql.append(" select u.name, count(*) as total ");
      sql.append(" from subscription s left join user u on s.iduser = u.iduser ");
      sql.append(" where s.date_registred between :dataini and :datafim ");
      // sql.append(" and s.status <> 'CANCELED' ");
      sql.append(" group by u.iduser ");
      sql.append(" order by u.name ");

      List<Object[]> objects = em.createNativeQuery(sql.toString())
               .setParameter("dataini", filter.getDateBegin())
               .setParameter("datafim", filter.getDateEnd())
               .getResultList();

      List<ProductivityReportObject> report = Lists.newArrayList();
      objects.forEach(o -> {
         ProductivityReportObject object = new ProductivityReportObject();
         object.setDescribe(getStringValue(o, 1));
         object.setQuantity(getLongValue(o, 2));
         report.add(object);
      });

      return report;
   }

   @SuppressWarnings("unchecked")
   public List<ProductivityReportObject> generateReportProductivityPlans(ProductivityReportFilter filter) {
      StringBuilder sql = new StringBuilder();
      sql.append(" select u.name, count(*) as total ");
      sql.append(" from subscription s left join plan u on s.idplan = u.idplan ");
      sql.append(" where s.date_registred between :dataini and :datafim ");
      // sql.append(" and s.status <> 'CANCELED' ");
      sql.append(" group by u.idplan ");
      sql.append(" order by u.name ");

      List<Object[]> objects = em.createNativeQuery(sql.toString())
               .setParameter("dataini", filter.getDateBegin())
               .setParameter("datafim", filter.getDateEnd())
               .getResultList();

      List<ProductivityReportObject> report = Lists.newArrayList();
      objects.forEach(o -> {
         ProductivityReportObject object = new ProductivityReportObject();
         object.setDescribe(getStringValue(o, 1));
         object.setQuantity(getLongValue(o, 2));
         report.add(object);
      });

      return report;
   }

   @SuppressWarnings("unchecked")
   public List<UserByPlanReportPlan> plansAvaliablesOnPeriodByUser(UserByPlanReportFilter filter) {
      StringBuilder sql = new StringBuilder();
      sql.append("select b.idplan, b.name, count(*) as total ");
      sql.append("from subscription a inner join plan b on a.idplan = b.idplan ");
      sql.append("where date_registred between :dataini and :datafim ");
      sql.append("group by b.idplan ");
      sql.append("order by b.name ");

      List<Object[]> objects = em.createNativeQuery(sql.toString())
               .setParameter("dataini", filter.getDateBegin())
               .setParameter("datafim", filter.getDateEnd())
               .getResultList();

      List<UserByPlanReportPlan> report = Lists.newArrayList();
      objects.forEach(o -> {
         UserByPlanReportPlan object = new UserByPlanReportPlan();
         object.setId(getLongValue(o, 1));
         object.setName(getStringValue(o, 2));
         object.setQuantity(getIntegerValue(o, 3));
         report.add(object);
      });

      return report;
   }

   @SuppressWarnings("unchecked")
   public List<UserByPlanReportPlan> plansAvaliablesOnPeriodByBroker(UserByPlanReportFilter filter) {
      StringBuilder sql = new StringBuilder();
      sql.append("select b.idplan, b.name, count(*) as total ");
      sql.append("from subscription a inner join plan b on a.idplan = b.idplan ");
      sql.append("where date_registred between :dataini and :datafim ");
      sql.append("and a.idbroker is not null ");
      sql.append("group by b.idplan ");
      sql.append("order by b.name ");

      List<Object[]> objects = em.createNativeQuery(sql.toString())
               .setParameter("dataini", filter.getDateBegin())
               .setParameter("datafim", filter.getDateEnd())
               .getResultList();

      List<UserByPlanReportPlan> report = Lists.newArrayList();
      objects.forEach(o -> {
         UserByPlanReportPlan object = new UserByPlanReportPlan();
         object.setId(getLongValue(o, 1));
         object.setName(getStringValue(o, 2));
         object.setQuantity(getIntegerValue(o, 3));
         report.add(object);
      });

      return report;
   }

   public Integer getUserByPlanReportHorizontal(UserByPlanReportFilter filter, Long planId, Long userId) {
      StringBuilder sql = new StringBuilder();
      sql.append("select count(*) as total ");
      sql.append("from subscription ");
      sql.append("where date_registred between :dataini and :datafim ");
      sql.append("and iduser = :user and idplan = :plan ");

      Object object = em.createNativeQuery(sql.toString())
               .setParameter("dataini", filter.getDateBegin())
               .setParameter("datafim", filter.getDateEnd())
               .setParameter("user", userId)
               .setParameter("plan", planId)
               .getSingleResult();

      return Integer.valueOf(object.toString());
   }

   public Integer getBrokerByPlanReportHorizontal(UserByPlanReportFilter filter, Long planId, Long brokerId) {
      StringBuilder sql = new StringBuilder();
      sql.append("select count(*) as total ");
      sql.append("from subscription ");
      sql.append("where date_registred between :dataini and :datafim ");
      sql.append("and idbroker = :broker and idplan = :plan ");

      Object object = em.createNativeQuery(sql.toString())
               .setParameter("dataini", filter.getDateBegin())
               .setParameter("datafim", filter.getDateEnd())
               .setParameter("broker", brokerId)
               .setParameter("plan", planId)
               .getSingleResult();

      return Integer.valueOf(object.toString());
   }

}
