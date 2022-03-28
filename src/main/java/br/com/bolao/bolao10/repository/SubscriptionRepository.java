
package br.com.segmedic.clubflex.repository;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import br.com.segmedic.clubflex.domain.Dependent;
import br.com.segmedic.clubflex.domain.Invoice;
import br.com.segmedic.clubflex.domain.Subscription;
import br.com.segmedic.clubflex.domain.SubscriptionLog;
import br.com.segmedic.clubflex.domain.enums.DependentStatus;
import br.com.segmedic.clubflex.domain.enums.PaymentType;
import br.com.segmedic.clubflex.domain.enums.SubscriptionStatus;
import br.com.segmedic.clubflex.domain.enums.TypeSub;
import br.com.segmedic.clubflex.domain.enums.UserProfile;
import br.com.segmedic.clubflex.model.FinanceReportResult;
import br.com.segmedic.clubflex.model.OperationalReportFilter;
import br.com.segmedic.clubflex.model.PlanReportFilter;
import br.com.segmedic.clubflex.model.SubLog;
import br.com.segmedic.clubflex.model.SubscriptionFilter;

@Repository
public class SubscriptionRepository extends GenericRepository {

   @Autowired
   private EntityManager em;

   public Subscription save(Subscription subscription) {
      if (subscription.getId() == null) {
         subscription.setDateTimeRegisteredLog(LocalDateTime.now());
         super.persist(subscription);
      }
      else {
         // Não estava funcionando o update. O Holder da Subs ficava em branco.
         // super.update(subscription);
         super.persist(subscription);
      }
      return subscription;
   }

   public Subscription update(Subscription subscription) {
      if (subscription.getId() == null) {
         subscription.setDateTimeRegisteredLog(LocalDateTime.now());
         super.persist(subscription);
      }
      else {
         super.update(subscription);
      }
      return subscription;
   }

   @SuppressWarnings("unchecked")
   public List<BigInteger> ListSubscriptionIdWithoutCard() {
      StringBuilder sql = new StringBuilder();
      sql.append(" select sb.idholder                                       ");
      sql.append(" FROM subscription sb                                       ");
      sql.append(" LEFT JOIN  credit_card cc on sb.idholder = cc.idholder   ");
      sql.append(" where (sb.payment_type = 'CREDIT_CARD' or sb.payment_type = 'DEBIT_CARD')");
      sql.append(" and (sb.status = 'OK' or sb.status = 'REQUESTED_CARD')");
      sql.append(" and cc.idholder is null");
      sql.append(" and (DATE_ADD(sb.date_registred , INTERVAL 3 DAY) = DATE(NOW()) or "
         + "DATE_ADD(sb.date_registred , INTERVAL 6 DAY) = DATE(NOW()) or DATE_ADD(sb.date_registred ,"
         + " INTERVAL 9 DAY) = DATE(NOW()))");

      Query query = em.createNativeQuery(sql.toString());
      try {
         return query.getResultList();
      }
      catch (Exception e) {
         return null;
      }
   }

   public BigInteger SubscriptionIdWithoutCard(Long subId) {
      StringBuilder sql = new StringBuilder();
      sql.append(" select sb.idholder                                       ");
      sql.append(" FROM subscription sb                                       ");
      sql.append(" LEFT JOIN  credit_card cc on sb.idholder = cc.idholder   ");
      sql.append(" where cc.idholder is null");
      sql.append(" and sb.idsubscription = :subId");
      try {
         return (BigInteger) em.createNativeQuery(sql.toString())
                  .setParameter("subId", subId)
                  .getSingleResult();
      }
      catch (Exception e) {
         return null;
      }
   }

   public List<Subscription> findByHolderId(Long holderId) {
      StringBuilder sql = new StringBuilder();
      sql.append(" select s ");
      sql.append(" from Subscription s");
      sql.append(" where s.holder.id = :holderId");

      TypedQuery<Subscription> query = em.createQuery(sql.toString(), Subscription.class);
      query.setParameter("holderId", holderId);

      try {
         return query.getResultList();
      }
      catch (Exception e) {
         return null;
      }
   }

   public List<Subscription> findByReasonId(Long idreason) {
      StringBuilder sql = new StringBuilder();
      sql.append(" select s ");
      sql.append(" from Subscription s");
      sql.append(" where s.reason.id = :idreason");

      TypedQuery<Subscription> query = em.createQuery(sql.toString(), Subscription.class);
      query.setParameter("idreason", idreason);

      try {
         return query.getResultList();
      }
      catch (Exception e) {
         return null;
      }
   }

   public List<Subscription> findByHolderCpfCnpj(String cpfCnpj) {
      StringBuilder sql = new StringBuilder();
      sql.append(" select s ");
      sql.append(" from Subscription s");
      sql.append(" where s.holder.cpfCnpj = :cpfCnpj");

      TypedQuery<Subscription> query = em.createQuery(sql.toString(), Subscription.class);
      query.setParameter("cpfCnpj", cpfCnpj);

      try {
         return query.getResultList();
      }
      catch (Exception e) {
         return null;
      }
   }

   public List<Subscription> findByHolderCpfCnpjNotResponsibleFinancial(String cpfCnpj) {
      StringBuilder sql = new StringBuilder();
      sql.append(" select s ");
      sql.append(" from Subscription s");
      sql.append(" where s.holder.cpfCnpj = :cpfCnpj ");
      sql.append(" and s.holderOnlyResponsibleFinance = FALSE ");
      sql.append(" and (s.status = :ok OR s.status = :bloqueado OR s.status = :request) ");

      TypedQuery<Subscription> query = em.createQuery(sql.toString(), Subscription.class);
      query.setParameter("cpfCnpj", cpfCnpj);
      query.setParameter("ok", SubscriptionStatus.OK);
      query.setParameter("bloqueado", SubscriptionStatus.BLOCKED);
      query.setParameter("request", SubscriptionStatus.REQUESTED_CARD);

      try {
         return query.getResultList();
      }
      catch (Exception e) {
         return null;
      }
   }

   public List<Subscription> findByDependentCpf(String cpf) {
      StringBuilder sql = new StringBuilder();
      sql.append(" select d ");
      sql.append(" from Dependent d ");
      sql.append(" where d.cpf = :cpf");

      TypedQuery<Dependent> query = em.createQuery(sql.toString(), Dependent.class);
      query.setParameter("cpf", cpf);

      try {
         return query.getResultList().stream().map(d -> {
            return d.getSubscription();
         }).collect(Collectors.toList());
      }
      catch (Exception e) {
         return null;
      }
   }

   public List<Subscription> findByDependentOkByCpf(String cpf) {
      StringBuilder sql = new StringBuilder();
      sql.append(" select d ");
      sql.append(" from Dependent d ");
      sql.append(" where d.cpf = :cpf ");
      sql.append(" and d.status = :status ");

      TypedQuery<Dependent> query = em.createQuery(sql.toString(), Dependent.class);
      query.setParameter("cpf", cpf);
      query.setParameter("status", DependentStatus.OK);

      try {
         return query.getResultList().stream().map(d -> {
            return d.getSubscription();
         }).collect(Collectors.toList());
      }
      catch (Exception e) {
         return null;
      }
   }

   public Subscription getLastByHolderId(Long holderId) {
      StringBuilder sql = new StringBuilder();
      sql.append(" select s ");
      sql.append(" from Subscription s");
      sql.append(" where s.holder.id = :holderId");
      sql.append(" order by s.dateOfRegistry DESC, s.dateTimeRegisteredLog DESC ");

      TypedQuery<Subscription> query = em.createQuery(sql.toString(), Subscription.class);
      query.setParameter("holderId", holderId);
      query.setMaxResults(1);

      try {
         return query.getResultList().get(0);
      }
      catch (Exception e) {
         return null;
      }
   }

   public Subscription findById(Long id) {
      return super.find(Subscription.class, id);
   }

   @SuppressWarnings("unchecked")
   public List<BigInteger> listNoShouldBeBlocked() {
      StringBuilder sql = new StringBuilder();
      sql.append(" select a.idsubscription   														 ");
      sql.append(" from invoice a inner join subscription b on a.idsubscription = b.idsubscription ");
      sql.append(" where a.status = 'OPENED' 														 ");
      sql.append("   and b.status = 'BLOCKED' 												     ");
      sql.append("   and a.due_date < current_date() 												 ");
      // sql.append(" and b.waiting_first_pay = '0' ");
      sql.append(" group by a.idsubscription														 ");
      sql.append(" having count(*) < 3 limit 5000;												 ");
      return em.createNativeQuery(sql.toString()).getResultList();
   }

   @SuppressWarnings("unchecked")
   public List<BigInteger> listShouldBeBlocked() {
      StringBuilder sql = new StringBuilder();
      sql.append(" select a.idsubscription   														 ");
      sql.append(" from invoice a inner join subscription b on a.idsubscription = b.idsubscription ");
      sql.append(" where a.status = 'OPENED' 														 ");
      sql.append("   and b.status = 'OK' 												     		 ");
      sql.append("   and a.due_date < current_date() 												 ");
      // sql.append(" and b.waiting_first_pay = '0' ");
      sql.append(" group by a.idsubscription														 ");
      sql.append(" having count(*) >= 3 limit 5000; 												 ");
      return em.createNativeQuery(sql.toString()).getResultList();
   }

   @SuppressWarnings("unchecked")
   public List<BigInteger> listRenewTickets() {
      StringBuilder sql = new StringBuilder();

      sql.append(" SELECT i.idinvoice                       ");
      sql.append(" FROM subscription s LEFT JOIN invoice i  ");
      sql.append(" ON i.idsubscription  = s.idsubscription  ");
      sql.append(" WHERE s.payment_type = 'TICKETS'         ");
      sql.append(" and s.status = 'OK'                      ");
      sql.append(" and invoice_type = 'DEFAULT'             ");
      sql.append(" and i.due_date < NOW()                   ");
      sql.append(" and i.due_date = (                       ");
      sql.append(" SELECT MAX(due_date)                     ");
      sql.append(" FROM invoice i2                          ");
      sql.append(" WHERE i2.idsubscription = s.idsubscription )");
      sql.append(" GROUP BY s.idsubscription ");

      return em.createNativeQuery(sql.toString()).getResultList();
   }

   public List<Invoice> listAdviseTickets() {
      StringBuilder sql = new StringBuilder();

      LocalDate dataInicio = LocalDate.now();
      LocalDate dataFim = LocalDate.now().plusMonths(1).minusDays(1);

      sql.append(" SELECT i                       ");
      sql.append(" FROM Invoice i , Subscription s ");
      sql.append(" WHERE s.paymentType = 'TICKETS'   ");
      sql.append(" and i.subscription.id = s.id ");
      sql.append(" and s.status = 'OK'        ");
      sql.append(" and i.type = 'DEFAULT' ");
      sql.append(" and i.status = 'OPENED' ");
      sql.append(" and i.dueDate BETWEEN :dataInicio and :dataFim ");

      TypedQuery<Invoice> query = em.createQuery(sql.toString(), Invoice.class);
      query.setParameter("dataInicio", dataInicio);
      query.setParameter("dataFim", dataFim);

      try {
         return query.getResultList();
      }
      catch (Exception e) {
         return null;
      }
   }

   public List<Invoice> listAdviseTickets2DaysToDue() {
      StringBuilder sql = new StringBuilder();

      LocalDate dataInicio = LocalDate.now().plusDays(2);

      sql.append(" SELECT i                       ");
      sql.append(" FROM Invoice i , Subscription s ");
      sql.append(" WHERE s.paymentType = 'TICKETS'   ");
      sql.append(" and i.subscription.id = s.id ");
      sql.append(" and s.status = 'OK'        ");
      sql.append(" and i.type = 'DEFAULT' ");
      sql.append(" and i.status = 'OPENED' ");
      sql.append(" and i.dueDate = :dataInicio");

      TypedQuery<Invoice> query = em.createQuery(sql.toString(), Invoice.class);
      query.setParameter("dataInicio", dataInicio);

      try {
         return query.getResultList();
      }
      catch (Exception e) {
         return null;
      }
   }

   @SuppressWarnings("unchecked")
   public List<BigInteger> listShouldBeBlockedByParam(LocalDate dataComparacao) {
      StringBuilder sql = new StringBuilder();
      sql.append(" select a.idsubscription   														 ");
      sql.append(" from invoice a inner join subscription b on a.idsubscription = b.idsubscription ");
      sql.append(" where a.status = 'OPENED' 														 ");
      sql.append("   and b.status = 'OK' 												     		 ");
      sql.append("   and a.due_date <= :dataVencimento 											 ");
      return em.createNativeQuery(sql.toString())
               .setParameter("dataVencimento", dataComparacao).getResultList();
   }

   @SuppressWarnings("unchecked")
   public List<BigInteger> listActiveSubscriptions(Integer actualCompetence, TypeSub typesub) {
      StringBuilder sql = new StringBuilder();
      sql.append(" select idsubscription   															");
      sql.append("   from subscription 																");
      sql.append("  where status = 'OK' 																");
      sql.append("   and payment_type <> 'TICKETS' 													");
      sql.append("   and date_cancellation is null													");
      sql.append("   and type_sub = :typesub													        ");
      sql.append("   and (date_last_competence <> :actualCompetence OR date_last_competence IS NULL)  ");

      return em.createNativeQuery(sql.toString())
               .setParameter("actualCompetence", actualCompetence)
               .setParameter("typesub", typesub.name())
               .getResultList();
   }

   @SuppressWarnings("unchecked")
   public List<BigInteger> listActiveBlockedSubscriptions(Integer actualCompetence, TypeSub typesub) {
      StringBuilder sql = new StringBuilder();
      sql.append(" select idsubscription                                               ");
      sql.append("   from subscription                                                 ");
      sql.append("   where (status = 'OK' or status = 'BLOCKED')                        ");
      sql.append("   and payment_type <> 'TICKETS'                                        ");
      sql.append("   and date_cancellation is null                                     ");
      sql.append("   and type_sub = :typesub                                             ");
      sql.append("   and (date_last_competence <> :actualCompetence OR date_last_competence IS NULL)  ");

      return em.createNativeQuery(sql.toString())
               .setParameter("actualCompetence", actualCompetence)
               .setParameter("typesub", typesub.name())
               .getResultList();
   }

   @SuppressWarnings("unchecked")
   public List<BigInteger> listActiveSubscriptions(Integer actualCompetence, Integer dayOfPayment, TypeSub typesub) {
      StringBuilder sql = new StringBuilder();
      sql.append(" select idsubscription   															");
      sql.append("   from subscription 																");
      sql.append("  where status = 'OK' 																");
      sql.append("   and payment_type <> 'TICKETS' 													");
      sql.append("   and date_cancellation is null													");
      sql.append("   and type_sub = :typesub													        ");
      sql.append("   and day_to_pay = :dayOfPayment													");
      sql.append("   and (date_last_competence <> :actualCompetence OR date_last_competence IS NULL)  ");

      return em.createNativeQuery(sql.toString())
               .setParameter("actualCompetence", actualCompetence)
               .setParameter("typesub", typesub.name())
               .setParameter("dayOfPayment", dayOfPayment)
               .getResultList();
   }

   @SuppressWarnings("unchecked")
   public List<BigInteger> listActiveSubscriptionsInvoices(Integer actualCompetence, Integer dayOfPayment, TypeSub typesub) {
      StringBuilder sql = new StringBuilder();
      sql.append(" select idsubscription                                               ");
      sql.append("   from subscription                                                 ");
      sql.append("  where status = 'OK'                                                ");
      sql.append("   and payment_type <> 'TICKETS'                                        ");
      sql.append("   and date_cancellation is null                                     ");
      sql.append("   and type_sub = :typesub                                             ");
      // sql.append(" and day_to_pay = :dayOfPayment ");
      sql.append("   and (date_last_competence <> :actualCompetence OR date_last_competence IS NULL)  ");

      return em.createNativeQuery(sql.toString())
               .setParameter("actualCompetence", actualCompetence)
               .setParameter("typesub", typesub.name())
               // .setParameter("dayOfPayment", dayOfPayment)
               .getResultList();
   }

   public List<Subscription> filter(SubscriptionFilter filter) {
      Map<String, Object> params = Maps.newConcurrentMap();

      StringBuilder sql = new StringBuilder();
      sql.append(" select distinct s ");
      sql.append(" from Subscription s");
      sql.append(" left join fetch s.dependents dep");
      sql.append(" left join fetch s.cards card");
      sql.append(" where 1=1 ");

      if (filter.getIdSubscription() != null) {
         sql.append(" and s.id = :id ");
         params.put("id", filter.getIdSubscription());
      }

      if (filter.getCardNumber() != null) {
         sql.append(" and card.id = :clubid ");
         params.put("clubid", filter.getCardNumber());
      }

      if (StringUtils.isNotBlank(filter.getCpfCnpjHolder())) {
         sql.append(" and s.holder.cpfCnpj = :cpfCnpj ");
         params.put("cpfCnpj", filter.getCpfCnpjHolder());
      }

      if (StringUtils.isNotBlank(filter.getNameHolder())) {
         sql.append(" and s.holder.name LIKE :name ");
         params.put("name", filter.getNameHolder().concat("%"));
      }

      if (StringUtils.isNotBlank(filter.getDependentName())) {
         sql.append(" and dep.name LIKE :nameDep ");
         params.put("nameDep", filter.getDependentName().concat("%"));
      }

      if (filter.getDateBegin() != null && filter.getDateEnd() != null) {
         sql.append(" and s.dateOfRegistry BETWEEN :dataBegin AND :dataEnd ");
         params.put("dataBegin", filter.getDateBegin());
         params.put("dataEnd", filter.getDateEnd());
      }

      if (filter.getPlanId() != null) {
         sql.append(" and s.plan.id = :planId ");
         params.put("planId", filter.getPlanId());
      }

      if (filter.getStatus() != null) {
         sql.append(" and s.status = :status ");
         params.put("status", filter.getStatus());
      }

      if (StringUtils.isNotBlank(filter.getPhone())) {
         sql.append(" and (s.holder.cellPhone = :phone1 or s.holder.homePhone = :phone2) ");
         params.put("phone1", filter.getPhone());
         params.put("phone2", filter.getPhone());
      }

      if (StringUtils.isNotBlank(filter.getEmail())) {
         sql.append(" and s.holder.email = :email ");
         params.put("email", filter.getEmail());
      }

      TypedQuery<Subscription> query = em.createQuery(sql.toString(), Subscription.class);
      params.entrySet().forEach(p -> {
         query.setParameter(p.getKey(), p.getValue());
      });

      try {
         return query.getResultList();
      }
      catch (Exception e) {
         return null;
      }
   }

   @SuppressWarnings("unchecked")
   public List<BigInteger> listPreCancelled() {
      StringBuilder sql = new StringBuilder();
      sql.append(" select s.idsubscription   								");
      sql.append(" from subscription s 									");
      sql.append(" where s.status = 'OK' 									");
      sql.append(" and s.date_cancellation = current_date()				");
      return em.createNativeQuery(sql.toString()).getResultList();
   }

   public List<Subscription> filterReportFilter(PlanReportFilter filter) {
      Map<String, Object> params = Maps.newConcurrentMap();

      StringBuilder sql = new StringBuilder();
      sql.append(" select distinct s ");
      sql.append(" from Subscription s");
      sql.append(" left join fetch s.dependents dep");
      sql.append(" left join fetch s.cards card");
      sql.append(" where 1=1 ");

      if (filter.getIdBroker() != null) {
         sql.append(" and s.broker.id = :idBroker ");
         params.put("idBroker", filter.getIdBroker());
      }

      if (filter.getDateBegin() != null && filter.getDateEnd() != null) {
         sql.append(" and s.dateOfRegistry BETWEEN :dataBegin AND :dataEnd ");
         params.put("dataBegin", filter.getDateBegin());
         params.put("dataEnd", filter.getDateEnd());
      }

      if (filter.getIdPlan() != null) {
         sql.append(" and s.plan.id = :planId ");
         params.put("planId", filter.getIdPlan());
      }

      if (filter.getIdUser() != null) {
         sql.append(" and s.user.id = :userId ");
         params.put("userId", filter.getIdUser());
      }

      TypedQuery<Subscription> query = em.createQuery(sql.toString(), Subscription.class);
      params.entrySet().forEach(p -> {
         query.setParameter(p.getKey(), p.getValue());
      });

      try {
         return query.getResultList();
      }
      catch (Exception e) {
         return null;
      }
   }

   public void saveLog(SubscriptionLog log) {
      super.persist(log);
   }

   @SuppressWarnings("unchecked")
   public List<SubLog> listLog(Long subscriptionId) {
      StringBuilder sql = new StringBuilder();
      sql.append(" select ");
      sql.append(" b.name, ");
      sql.append(" b.profile, ");
      sql.append(" DATE_FORMAT(a.date_time_log,'%d/%m/%Y %H:%i:%s') as datetimelog, ");
      sql.append(" a.action,  ");
      sql.append(" a.obs,      ");
      sql.append(" r.description      ");
      sql.append(" from subscription_log a left join user b on a.iduser = b.iduser ");
      sql.append("        left join reason r on r.idreason = a.idreason ");
      sql.append(" where a.idsubscription = :idsubscription ");
      sql.append(" order by a.date_time_log ");

      Query query = em.createNativeQuery(sql.toString());
      query.setParameter("idsubscription", subscriptionId);

      List<SubLog> logs = Lists.newArrayList();
      List<Object[]> objects = query.getResultList();
      objects.forEach(o -> {
         SubLog log = new SubLog();
         log.setUser(getStringValue(o, 1));
         log.setProfile(getStringValue(o, 2));
         log.setDateTime(getStringValue(o, 3));
         log.setAction(getStringValue(o, 4));
         log.setObs(getStringValue(o, 5));
         log.setReason(getStringValue(o, 6));
         logs.add(log);
      });

      return logs;
   }

   @SuppressWarnings("unchecked")
   public List<FinanceReportResult> generateReportOperational(OperationalReportFilter filter) {
      StringBuilder sql = new StringBuilder();
      sql.append(" select ");
      sql.append(" a.idsubscription, ");
      sql.append(" b.name as holdername, ");
      sql.append(" DATE_FORMAT(a.date_registred,'%d/%m/%Y') as registry, ");
      sql.append(
         " DATE_FORMAT((select payment_date from invoice i where i.idsubscription = a.idsubscription and i.status = 'PAID' order by payment_date asc limit 1),'%d/%m/%Y') as first_payment, ");
      sql.append(
         " (IF(length(b.cpf_cnpj) = 11, '1', '0') + (select count(*) from dependent dep where dep.idsubscription = a.idsubscription and dep.status = 'OK')) as total_lifes, ");
      sql.append(" c.name as planname, ");
      sql.append(" a.payment_type, ");
      sql.append(
         " (IF(length(b.cpf_cnpj) = 11, c.price_holder , '0') + (c.price_dependent * (select count(*) from dependent dep where dep.idsubscription = a.idsubscription and dep.status = 'OK'))) as price, ");
      sql.append(" d.name as user_name, ");
      sql.append(" d.profile as user_profile, ");
      sql.append(" e.name as place, ");
      sql.append(" f.name as broker, ");
      sql.append(" a.status, ");
      sql.append(" b.cellphone as cellphone, ");
      sql.append(" b.homephone as homephone ");
      sql.append(" from subscription a inner join holder b on a.idholder = b.idholder ");
      sql.append("        inner join plan c on a.idplan = c.idplan ");
      sql.append("        left join user d on a.iduser = d.iduser ");
      sql.append("        left join company e on a.idcompany = e.idcompany ");
      sql.append("        left join broker f on a.idbroker = f.idbroker ");
      sql.append("where a.date_registred between :dataIni and :dataFim ");

      Query query = em.createNativeQuery(sql.toString());
      query.setParameter("dataIni", filter.getDateBegin());
      query.setParameter("dataFim", filter.getDateEnd());

      List<FinanceReportResult> reports = Lists.newArrayList();
      List<Object[]> objects = query.getResultList();
      objects.forEach(o -> {
         FinanceReportResult report = new FinanceReportResult();
         report.setSubscription(getStringValue(o, 1));
         report.setHolderName(getStringValue(o, 2));
         report.setRegistry(getStringValue(o, 3));
         report.setFirstPayment(getStringValue(o, 4));
         report.setTotalLife(getStringValue(o, 5));
         report.setPlanName(getStringValue(o, 6));
         report.setPaymentType(PaymentType.valueOf(getStringValue(o, 7)).getDescribe());
         report.setPrice(getStringValue(o, 8));
         report.setUser(getStringValue(o, 9));
         report.setProfile(UserProfile.valueOf(getStringValue(o, 10)).getDescribe());
         report.setPlace(getStringValue(o, 11));
         report.setBroker(getStringValue(o, 12));
         report.setStatus(SubscriptionStatus.valueOf(getStringValue(o, 13)).getDescribe());
         report.setCellphone(getStringValue(o, 14));
         report.setHomephone(getStringValue(o, 15));
         reports.add(report);
      });
      return reports;
   }

   public List<Subscription> listSubscriptionActiveNotTickets() {

      StringBuilder sql = new StringBuilder();
      sql.append(" select s ");
      sql.append(" from Subscription s ");
      sql.append(" WHERE s.status = 'OK' ");
      sql.append(" AND s.paymentType <> 'TICKETS' ");

      TypedQuery<Subscription> query = em.createQuery(sql.toString(), Subscription.class);
      try {
         return query.getResultList();
      }
      catch (Exception e) {
         return null;
      }
   }

   public List<Subscription> listSubscriptionActiveTicketsAndCard(String tipos) {

      StringBuilder sql = new StringBuilder();
      sql.append(" select s ");
      sql.append(" from Subscription s ");
      sql.append(" WHERE s.status = 'OK' ");

      if (tipos.equals("B")) {
         sql.append(" AND s.paymentType in ('TICKET','TICKETS') ");
      }
      else if (tipos.equals("C")) {
         sql.append(" AND s.paymentType in ('CREDIT_CARD','DEBIT_CARD') ");
      }
      else {
         sql.append(" AND s.paymentType in ('TICKET','TICKETS','CREDIT_CARD','DEBIT_CARD') ");
      }

      TypedQuery<Subscription> query = em.createQuery(sql.toString(), Subscription.class);
      try {
         return query.getResultList();
      }
      catch (Exception e) {
         return null;
      }
   }

   @SuppressWarnings("unchecked")
   public List<BigInteger> listShouldBeBlockedByDateDue() {
      StringBuilder sql = new StringBuilder();

      sql.append("select  distinct a.idsubscription ");
      sql.append("from invoice a inner join subscription b on a.idsubscription = b.idsubscription ");
      //sql.append("inner join plan p on p.idplan = b.idplan "); Bloqueio pelo parametro do plano
      sql.append("where a.status = 'OPENED' ");
      sql.append("and b.status = 'OK' ");
      sql.append("and a.due_date < current_date()   ");
      sql.append("and b.days_to_block is not NULL "); // Para bloquear pelo plano, comentar essa linha
      sql.append("and CURRENT_DATE() >= DATE_ADD(a.due_date, INTERVAL b.days_to_block DAY) "); // Para bloquear pelo plano, comentar essa linha
      //sql.append("and CURRENT_DATE() >= DATE_ADD(a.due_date, INTERVAL p.days_to_block DAY) "); Bloqueio pelo parametro do plano

      Query query = em.createNativeQuery(sql.toString());

      try {

         return query.getResultList();
      }
      catch (Exception e) {
         return null;
      }
   }
}
