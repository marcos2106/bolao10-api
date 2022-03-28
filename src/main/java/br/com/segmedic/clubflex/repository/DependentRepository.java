
package br.com.segmedic.clubflex.repository;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import br.com.segmedic.clubflex.domain.Dependent;
import br.com.segmedic.clubflex.domain.enums.DependentStatus;
import br.com.segmedic.clubflex.domain.enums.Sex;
import br.com.segmedic.clubflex.domain.enums.SubscriptionStatus;
import br.com.segmedic.clubflex.model.DependentFilter;
import br.com.segmedic.clubflex.model.HolderStatus;
import br.com.segmedic.clubflex.service.SubscriptionService;

@Repository
public class DependentRepository extends GenericRepository {

   @Autowired
   private EntityManager em;

   @Autowired
   private SubscriptionService subscriptionService;

   @Autowired
   private ClubCardRepository clubCardRepository;

   public Dependent findById(Long id) {
      return super.find(Dependent.class, id);
   }

   public Dependent findByDependentId(Long dependentId) {
      StringBuilder sql = new StringBuilder();
      sql.append(" select d ");
      sql.append(" from Dependent d JOIN FETCH d.subscription sub  ");
      sql.append(" where d.id = :dependentId");

      TypedQuery<Dependent> query = em.createQuery(sql.toString(), Dependent.class);
      query.setParameter("dependentId", dependentId);

      try {
         return query.getSingleResult();
      }
      catch (Exception e) {
         return null;
      }
   }

   public List<Dependent> findByCPFAndAtivo(String cpf) {
      StringBuilder sql = new StringBuilder();
      sql.append(" select d ");
      sql.append(" from Dependent d ");
      sql.append(" where d.cpf = :cpf ");
      sql.append(" and d.status = 'OK' ");

      TypedQuery<Dependent> query = em.createQuery(sql.toString(), Dependent.class);
      query.setParameter("cpf", cpf);

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
      sql.append(" select d.iddependent   								");
      sql.append(" from dependent d 										");
      sql.append(" where d.status = 'OK' 									");
      sql.append(" and d.date_of_removal <= NOW()							");
      return em.createNativeQuery(sql.toString()).getResultList();
   }

   @SuppressWarnings("unchecked")
   public boolean existsHolder(Long subscriptionId, String cpfHolder) {
      StringBuilder sql = new StringBuilder();
      sql.append(" select d.iddependent   								");
      sql.append(" from dependent d 										");
      sql.append(" where d.status = 'OK' 									");
      sql.append(" and d.cpf = :cpf							            ");
      sql.append(" and d.idsubscription = :sub							");

      try {
         List<Object> deps = em.createNativeQuery(sql.toString())
                  .setParameter("cpf", cpfHolder)
                  .setParameter("sub", subscriptionId)
                  .getResultList();
         return deps != null && deps.size() > 0;
      }
      catch (Exception e) {
         return false;
      }
   }

   @SuppressWarnings("unchecked")
   public boolean existsCpfHolder(String cpfHolder) {
      StringBuilder sql = new StringBuilder();
      sql.append(" select d.iddependent                           ");
      sql.append(" from dependent d                               ");
      sql.append(" where d.cpf = :cpf                               ");
      sql.append(" and status = 'OK'                               ");
      try {
         List<Object> deps = em.createNativeQuery(sql.toString())
                  .setParameter("cpf", cpfHolder)
                  .getResultList();
         return deps != null && deps.size() > 0;
      }
      catch (Exception e) {
         return false;
      }
   }

   public List<Dependent> listDependentsByStatus(DependentStatus status) {
      StringBuilder sql = new StringBuilder();
      sql.append(" select d ");
      sql.append(" from Dependent d ");
      sql.append(" where d.status = :status");

      TypedQuery<Dependent> query = em.createQuery(sql.toString(), Dependent.class);
      query.setParameter("status", status);

      try {
         return query.getResultList();
      }
      catch (Exception e) {
         return null;
      }
   }

   public List<Dependent> listAllDependents() {
      StringBuilder sql = new StringBuilder();
      sql.append(" select d ");
      sql.append(" from Dependent d ");
      TypedQuery<Dependent> query = em.createQuery(sql.toString(), Dependent.class);
      try {
         return query.getResultList();
      }
      catch (Exception e) {
         return null;
      }
   }

   @SuppressWarnings("unchecked")
   public List<HolderStatus> filter(DependentFilter filter) {

      StringBuilder sql = new StringBuilder();
      sql.append(" select h.idholder, h.name, h.cpf_cnpj, h.date_of_birth, h.flag_isholder, h.sex, \n" +
         " h.email, h.cellPhone, h.homePhone, h.responsible_name, h.responsible_cpf, \n" +
         " h.zipcode, h.address, h.complement, h.uf, h.city, h.neighborhood, \n" +
         " h.gateway_ticket_id, h.updated_at, s.status, c.flag_card_holder, s.idsubscription  \n" +
         " FROM subscription s \n" +
         " INNER JOIN holder h ON h.idholder = s.idholder \n" +
         " INNER JOIN club_card c ON c.idholder = h.idholder \n" +
         " WHERE 1=1 ");

      if (StringUtils.isNotBlank(filter.getCpfCnpjDep())) {
         sql.append(" and (h.cpf_cnpj = :cpfCnpj) and c.flag_card_holder = true");
      }
      if (StringUtils.isNotBlank(filter.getNameDep())) {
         sql.append(" and (h.name like :name) and c.flag_card_holder = true ");
      }
      if (filter.getDateBirth() != null) {
         sql.append(" AND (h.date_of_birth = :dateBirth) and c.flag_card_holder = true ");
      }
      if (filter.getNumCard() != null) {
         sql.append(" AND (c.idclubcard = :numCard) and c.flag_card_holder = true ");
      }

      Query query = em.createNativeQuery(sql.toString());

      if (StringUtils.isNotBlank(filter.getCpfCnpjDep())) {
         query.setParameter("cpfCnpj", filter.getCpfCnpjDep());
      }
      if (StringUtils.isNotBlank(filter.getNameDep())) {
         query.setParameter("name", filter.getNameDep().concat("%"));
      }
      if (filter.getDateBirth() != null) {
         query.setParameter("dateBirth", filter.getDateBirth());
      }
      if (filter.getNumCard() != null) {
         query.setParameter("numCard", filter.getNumCard());
      }

      List<Object[]> objects = query.getResultList();

      StringBuilder sql2 = new StringBuilder();
      sql2.append(" select h.idholder, h.name as nameHolder, h.cpf_cnpj, h.date_of_birth as birthHolder, h.flag_isholder, h.sex, \n" +
         " h.email, h.cellPhone, h.homePhone, h.responsible_name, h.responsible_cpf, \n" +
         " h.zipcode, h.address, h.complement, h.uf, h.city, h.neighborhood, \n" +
         " h.gateway_ticket_id, h.updated_at, s.status as statusSub, c.flag_card_holder, s.idsubscription, \n" +
         " d.iddependent, d.name as nameDep, d.cpf, d.date_of_birth as birthDep, d.status as statusDep, c.idclubcard \n" +
         " FROM subscription s \n" +
         " INNER JOIN holder h ON h.idholder = s.idholder \n" +
         " INNER JOIN dependent d ON d.idsubscription = s.idsubscription \n" +
         " INNER JOIN club_card c ON c.iddependent = d.iddependent \n" +
         " WHERE 1=1 ");

      if (StringUtils.isNotBlank(filter.getCpfCnpjDep())) {
         sql2.append(" AND (d.cpf = :cpfCnpjDep) ");
      }
      if (StringUtils.isNotBlank(filter.getNameDep())) {
         sql2.append(" AND (d.name LIKE :nameDep) ");
      }
      if (filter.getDateBirth() != null) {
         sql2.append(" AND (d.date_of_birth = :dateBirthDep) ");
      }
      if (filter.getNumCard() != null) {
         sql2.append(" AND (c.idclubcard = :numCard) ");
      }

      Query query2 = em.createNativeQuery(sql2.toString());

      if (StringUtils.isNotBlank(filter.getCpfCnpjDep())) {
         query2.setParameter("cpfCnpjDep", filter.getCpfCnpjDep());
      }
      if (StringUtils.isNotBlank(filter.getNameDep())) {
         query2.setParameter("nameDep", "%" + filter.getNameDep().concat("%"));
      }
      if (filter.getDateBirth() != null) {
         query2.setParameter("dateBirthDep", filter.getDateBirth());
      }
      if (filter.getNumCard() != null) {
         query2.setParameter("numCard", filter.getNumCard());
      }

      List<Object[]> objectsDep = query2.getResultList();

      // Add Uma lista na outra
      objects.addAll(objectsDep);

      List<HolderStatus> holdersStatus = new ArrayList<>();
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

      objects.forEach(o -> {
         HolderStatus holder = new HolderStatus();
         holder.setId(getLongValue(o, 1));
         holder.setName(getStringValue(o, 2));
         holder.setCpfCnpj(getStringValue(o, 3));
         holder.setDateOfBirth(
            (getStringValue(o, 4) != null && !getStringValue(o, 4).equals("")) ? LocalDate.parse(getStringValue(o, 4)) : null);
         holder.setIsHolder(getBooleanValue(o, 5));
         holder.setSex((getStringValue(o, 6) != null && !getStringValue(o, 6).equals("")) ? Sex.valueOf(getStringValue(o, 6)) : null);
         holder.setEmail(getStringValue(o, 7));
         holder.setCellPhone(getStringValue(o, 8));
         holder.setHomePhone(getStringValue(o, 9));
         holder.setResponsibleName(getStringValue(o, 10));
         holder.setResponsibleCpf(getStringValue(o, 11));
         holder.setZipcode(getStringValue(o, 12));
         holder.setAddress(getStringValue(o, 13));
         holder.setComplementAddress(getStringValue(o, 14));
         holder.setUf(getStringValue(o, 15));
         holder.setCity(getStringValue(o, 16));
         holder.setNeighborhood(getStringValue(o, 17));
         holder.setGatewayTicketId(getLongValue(o, 18));
         holder.setUpdatedAt(
            (getStringValue(o, 19) != null && !getStringValue(o, 19).equals("")) ? LocalDateTime.parse(getStringValue(o, 19), formatter)
               : null);
         holder.setStatusSubscription(
            (getStringValue(o, 20) != null && !getStringValue(o, 20).equals("")) ? SubscriptionStatus.valueOf(getStringValue(o, 20))
               : null);

         List<Dependent> listaDependente = new ArrayList<Dependent>();

         // TRUE - Holder
         if (getBooleanValue(o, 21)) {

            Long subscriptionId = new Long(getStringValue(o, 22));
            listaDependente = subscriptionService.listDependentBySubscriptionId(subscriptionId);

            for (Dependent dep : listaDependente) {
               dep.setClubCard(clubCardRepository.findByDependentId(dep.getId(), false).getId());
            }

            // FALSE - Depedent
         }
         else {
            Dependent dep = new Dependent();
            dep.setId(new Long(getStringValue(o, 23)));
            dep.setName(getStringValue(o, 24));
            dep.setCpf(getStringValue(o, 25));
            dep.setDateOfBirth(LocalDate.parse(getStringValue(o, 26)));
            dep.setStatus(DependentStatus.valueOf(getStringValue(o, 27)));
            dep.setClubCard(new Long(getStringValue(o, 28)));

            listaDependente.add(dep);
         }

         holder.setListDependents(listaDependente);

         holdersStatus.add(holder);
      });
      try {
         return holdersStatus;
      }
      catch (Exception e) {
         return null;
      }
   }
}
