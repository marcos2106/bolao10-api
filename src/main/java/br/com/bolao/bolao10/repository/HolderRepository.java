
package br.com.bolao.bolao10.repository;

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

import br.com.bolao.bolao10.domain.Holder;
import br.com.bolao.bolao10.domain.enums.Sex;
import br.com.bolao.bolao10.model.HolderFarma;
import br.com.bolao.bolao10.model.HolderFilter;
import br.com.bolao.bolao10.model.HolderStatus;
import br.com.bolao.bolao10.service.SubscriptionService;

@Repository
public class HolderRepository extends GenericRepository {

   @Autowired
   private EntityManager em;

   @Autowired
   private SubscriptionService subscriptionService;

   public Holder findByCpfCnpj(String cpfCnpj) {
      StringBuilder sql = new StringBuilder();
      sql.append(" select h ");
      sql.append(" from Holder h");
      sql.append(" where h.cpfCnpj = :cpfCnpj");

      TypedQuery<Holder> query = em.createQuery(sql.toString(), Holder.class);
      query.setParameter("cpfCnpj", cpfCnpj);

      try {
         return query.getResultList().get(0);
      }
      catch (Exception e) {
         return null;
      }
   }

   public Holder findById(Long id) {
      return super.find(Holder.class, id);
   }

   public Holder save(Holder holder) {
      if (holder.getId() == null) {
         super.persist(holder);
      }
      else {
         super.update(holder);
      }
      return holder;
   }

   public List<Holder> allHolders() {
      StringBuilder sql = new StringBuilder();
      sql.append(" select h ");
      sql.append(" from Holder h");
      TypedQuery<Holder> query = em.createQuery(sql.toString(), Holder.class);
      try {
         return query.getResultList();
      }
      catch (Exception e) {
         return null;
      }
   }

   @SuppressWarnings("unchecked")
   public List<HolderStatus> filter(HolderFilter filter, boolean ativos) {
      StringBuilder sql = new StringBuilder();
      sql.append(" select h.idholder, h.name, h.cpf_cnpj, h.date_of_birth, h.flag_isholder, h.sex, "
         + " h.email, h.cellPhone, h.homePhone, h.responsible_name, h.responsible_cpf, "
         + " h.zipcode, h.address, h.complement, h.uf, h.city, h.neighborhood, "
         + " h.gateway_ticket_id, h.updated_at, s.status, s.idsubscription, c.idclubcard, c.status as statusCC ");
      sql.append(" from holder h, subscription s, club_card c ");
      sql.append(" where h.idholder = s.idholder");
      sql.append(" and h.idholder = c.idholder");

      if (StringUtils.isNotBlank(filter.getCpfCnpjHolder())) {
         sql.append(" and h.cpf_cnpj = :cpfCnpj");
      }

      if (StringUtils.isNotBlank(filter.getNameHolder())) {
         sql.append(" and h.name like :name");
      }

      if (ativos) {
         sql.append(" and s.status = 'OK' ");
         sql.append(" and c.status = 'OK' ");
      }
      else {
         sql.append(" and s.status != 'OK' ");
         sql.append(" and c.status != 'OK' ");
      }
      sql.append(" and c.flag_card_holder = true ");
      Query query = em.createNativeQuery(sql.toString());

      if (StringUtils.isNotBlank(filter.getCpfCnpjHolder())) {
         query.setParameter("cpfCnpj", filter.getCpfCnpjHolder());
      }
      if (StringUtils.isNotBlank(filter.getNameHolder())) {
         query.setParameter("name", filter.getNameHolder().concat("%"));
      }

      List<Object[]> objects = query.getResultList();

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
         holder.setIdClubCard(getStringValue(o, 22));
         holder.setStatusClubCard(getStringValue(o, 23));
         holder.setUpdatedAt(
            (getStringValue(o, 19) != null && !getStringValue(o, 19).equals("")) ? LocalDateTime.parse(getStringValue(o, 19), formatter)
               : null);
         
         holdersStatus.add(holder);
      });
      try {
         return holdersStatus;
      }
      catch (Exception e) {
         return null;
      }
   }

   @SuppressWarnings("unchecked")
   public List<HolderStatus> filterDependent(HolderFilter filter, boolean ativos) {
      StringBuilder sql = new StringBuilder();
      sql.append(" select d.iddependent, d.name, d.cpf, d.date_of_birth, d.sex, ");
      sql.append(" d.email, d.phone, d.date_of_insert, s.status, s.idsubscription, c.idclubcard, c.status as statusCC ");
      sql.append(" from dependent d, subscription s, club_card c ");
      sql.append(" where d.idsubscription = s.idsubscription ");
      sql.append(" and d.iddependent = c.iddependent ");

      if (StringUtils.isNotBlank(filter.getCpfCnpjHolder())) {
         sql.append(" and d.cpf = :cpf");
      }
      if (StringUtils.isNotBlank(filter.getNameHolder())) {
         sql.append(" and d.name like :name");
      }

      if (ativos) {
         sql.append(" and s.status = 'OK' ");
         sql.append(" and c.status = 'OK' ");
         sql.append(" and d.status = 'OK' ");
      }
      else {
         sql.append(" and s.status != 'OK' ");
         sql.append(" and c.status != 'OK' ");
         sql.append(" and d.status != 'OK' ");
      }
      Query query = em.createNativeQuery(sql.toString());

      if (StringUtils.isNotBlank(filter.getCpfCnpjHolder())) {
         query.setParameter("cpf", filter.getCpfCnpjHolder());
      }
      if (StringUtils.isNotBlank(filter.getNameHolder())) {
         query.setParameter("name", filter.getNameHolder().concat("%"));
      }

      List<Object[]> objects = query.getResultList();

      List<HolderStatus> holdersStatus = new ArrayList<>();
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

      objects.forEach(o -> {
         HolderStatus holder = new HolderStatus();
         holder.setId(getLongValue(o, 1));
         holder.setName(getStringValue(o, 2));
         holder.setCpfCnpj(getStringValue(o, 3));
         holder.setDateOfBirth(
            (getStringValue(o, 4) != null && !getStringValue(o, 4).equals("")) ? LocalDate.parse(getStringValue(o, 4)) : null);
         holder.setIsHolder(false);
         holder.setSex((getStringValue(o, 5) != null && !getStringValue(o, 5).equals("")) ? Sex.valueOf(getStringValue(o, 5)) : null);
         holder.setEmail(getStringValue(o, 6));
         holder.setCellPhone(getStringValue(o, 7));
         holder.setUpdatedAt(
            (getStringValue(o, 8) != null && !getStringValue(o, 8).equals("")) ? LocalDateTime.parse(getStringValue(o, 8), formatter)
               : null);
         holder.setIdSubscription(getLongValue(o, 10));
         holder.setIdClubCard(getStringValue(o, 11));
         holder.setStatusClubCard(getStringValue(o, 12));

         holdersStatus.add(holder);
      });
      try {
         return holdersStatus;
      }
      catch (Exception e) {
         return null;
      }
   }

   @SuppressWarnings("unchecked")
   public List<HolderFarma> filterBasic(HolderFilter filter) {
      StringBuilder sql = new StringBuilder();
      sql.append(" select h.idholder, h.name, h.cpf_cnpj, h.flag_isholder, s.status, s.idsubscription, cc.idclubcard ");
      sql.append(" from holder h, subscription s, club_card cc");
      sql.append(" where h.idholder = s.idholder");
      sql.append(" and h.idholder = cc.idholder");

      if (StringUtils.isNotBlank(filter.getCpfCnpjHolder())) {
         sql.append(" and h.cpf_cnpj = :cpfCnpj");
      }

      Query query = em.createNativeQuery(sql.toString());

      if (StringUtils.isNotBlank(filter.getCpfCnpjHolder())) {
         query.setParameter("cpfCnpj", filter.getCpfCnpjHolder());
      }

      List<Object[]> objects = query.getResultList();

      List<HolderFarma> holdersFarma = new ArrayList<>();
      objects.forEach(o -> {
         HolderFarma holder = new HolderFarma();
         holder.setId(getLongValue(o, 1));
         holder.setName(getStringValue(o, 2));
         holder.setCpfCnpj(getStringValue(o, 3));
         holder.setIsHolder(getBooleanValue(o, 4));
         holdersFarma.add(holder);
      });
      try {
         return holdersFarma;
      }
      catch (Exception e) {
         return null;
      }
   }
}
