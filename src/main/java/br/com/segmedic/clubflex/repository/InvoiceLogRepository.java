package br.com.segmedic.clubflex.repository;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.com.segmedic.clubflex.domain.InvoiceLog;

@Repository
public class InvoiceLogRepository extends GenericRepository {
	
	@Autowired
	private EntityManager em;
	
	public InvoiceLog findById(Long id) {
		try {
			return super.find(InvoiceLog.class, id);
		} catch (Exception e) {
			return null;
		}
	}
	
	public InvoiceLog save(InvoiceLog invoicelog) {
		if(invoicelog.getId() == null) {
			invoicelog.setDateRegistry(LocalDateTime.now());
			super.persist(invoicelog);
		}else {
			super.update(invoicelog);
		}
		return invoicelog;
	}

	public List<InvoiceLog> listInvoiceLogByInvoice(Long invoiceId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select log ");
		sql.append(" from InvoiceLog log");
		sql.append(" where log.invoice.id = :invoice ");
		sql.append(" order by log.dateRegistry desc ");
		
		TypedQuery<InvoiceLog> query = em.createQuery(sql.toString(), InvoiceLog.class);
		query.setParameter("invoice", invoiceId);
		
		try {
			return query.getResultList();
		} catch (Exception e) {
			return null;
		}
	}

}
