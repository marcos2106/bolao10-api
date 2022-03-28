package br.com.segmedic.clubflex.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.beust.jcommander.internal.Lists;

import br.com.segmedic.clubflex.domain.Plan;
import br.com.segmedic.clubflex.domain.enums.PaymentType;
import br.com.segmedic.clubflex.domain.enums.TypeSub;

@Repository
public class PlanRepository extends GenericRepository {
	
	@Autowired
	private EntityManager em;
	
	public List<Plan> listAllActive() {
		StringBuilder sql = new StringBuilder();
		sql.append(" select p ");
		sql.append(" from Plan p");
		sql.append(" where p.isActive = :active");
		sql.append(" order by p.name ");
		
		TypedQuery<Plan> query = em.createQuery(sql.toString(), Plan.class);
		query.setParameter("active", Boolean.TRUE);
		
		try {
			return query.getResultList();
		} catch (Exception e) {
			return null;
		}
	}
	
	public List<Plan> listAllActive(TypeSub type) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select p ");
		sql.append(" from Plan p");
		sql.append(" where p.isActive = :active");
		sql.append(" and p.typeSub = :type ");
		sql.append(" order by p.name ");
		
		TypedQuery<Plan> query = em.createQuery(sql.toString(), Plan.class);
		query.setParameter("active", Boolean.TRUE);
		query.setParameter("type", type);
		
		try {
			return query.getResultList();
		} catch (Exception e) {
			return null;
		}
	}

	public List<Plan> listAvaliableToSite() {
		StringBuilder sql = new StringBuilder();
		sql.append(" select p ");
		sql.append(" from Plan p");
		sql.append(" where p.isActive = :active");
		sql.append("   and p.avaliableOnline = :avaliable");
		sql.append("   and p.typeSub = :type ");
		sql.append(" order by p.name ");
		
		TypedQuery<Plan> query = em.createQuery(sql.toString(), Plan.class);
		query.setParameter("active", Boolean.TRUE);
		query.setParameter("avaliable", Boolean.TRUE);
		query.setParameter("type", TypeSub.PF);
		
		try {
			return query.getResultList();
		} catch (Exception e) {
			return null;
		}
	}

	public Plan findById(Long id) {
		return super.find(Plan.class, id);
	}

	public List<Plan> listAll() {
		StringBuilder sql = new StringBuilder();
		sql.append(" select p 		 ");
		sql.append(" from Plan p	 ");
		sql.append(" order by p.name ");
		
		try {
			return em.createQuery(sql.toString(), Plan.class).getResultList();
		} catch (Exception e) {
			return null;
		}
	}
	
	public List<Plan> listAll(TypeSub type) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select p 		 ");
		sql.append(" from Plan p	 ");
		sql.append(" Where p.typeSub = :type ");
		sql.append(" order by p.name ");
		
		try {
			return em.createQuery(sql.toString(), Plan.class).setParameter("type", type).getResultList();
		} catch (Exception e) {
			return null;
		}
	}

	public void removeAllPaymentsType(Long planId) {
		em.createNativeQuery("delete from plan_payment_type where idplan = :idplan")
		  .setParameter("idplan", planId)
		  .executeUpdate();
	}

	public void addPaymentType(Long planId, PaymentType type) {
		em.createNativeQuery("insert into plan_payment_type (idplan, payment_type) values (:idplan, :payment)")
		  .setParameter("idplan", planId)
		  .setParameter("payment", type.name())
		  .executeUpdate();
	}

	@SuppressWarnings("unchecked")
	public List<PaymentType> listAllPaymentsType(Long planId) {
		try {
			List<String> resultList = em.createNativeQuery("select payment_type from plan_payment_type where idplan = :idplan")
					.setParameter("idplan", planId)
					.getResultList();
			
			List<PaymentType> list = Lists.newArrayList();
			resultList.forEach(p->{
				list.add(PaymentType.valueOf(p));
			});
			return list;
		} catch (Exception e) {
			return null;
		}
	}

	
}
