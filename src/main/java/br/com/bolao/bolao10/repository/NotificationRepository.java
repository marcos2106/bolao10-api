package br.com.segmedic.clubflex.repository;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;

import br.com.segmedic.clubflex.domain.Notification;
import br.com.segmedic.clubflex.domain.Subscription;
import br.com.segmedic.clubflex.domain.User;
import br.com.segmedic.clubflex.domain.enums.UserProfile;
import br.com.segmedic.clubflex.model.NotificationRequest;

@Repository
public class NotificationRepository extends GenericRepository {
	
	@Autowired
	private EntityManager em;
	
	public void save(Notification notification) {
		em.persist(notification);
	}

	public void save(Subscription sub, User user, NotificationRequest form) {
		Notification notification = new Notification();
		notification.setDescription(form.getDescription());
		notification.setSubscription(sub);
		notification.setDateUpdate(LocalDateTime.now());
		notification.setUser(user);
		save(notification);
	}
	

	@SuppressWarnings("unchecked")
	public List<NotificationRequest> listNotifications(Long subscriptionId) {

		StringBuilder sql = new StringBuilder();
		sql.append(" select ");
		sql.append(" u.name, ");
		sql.append(" u.profile, ");
		sql.append(" DATE_FORMAT(n.date_hour_update,'%d/%m/%Y %H:%i:%s') as dateHourUpdate, ");
		sql.append(" n.description      ");
		sql.append(" from notification n left join user u on n.iduser = u.iduser ");
		sql.append(" where n.idsubscription = :idsubscription ");
		sql.append(" order by n.date_hour_update desc ");
		
		Query query = em.createNativeQuery(sql.toString());
		query.setParameter("idsubscription", subscriptionId);
		
		List<NotificationRequest> listNotification = Lists.newArrayList();
		List<Object[]> objects = query.getResultList();
		objects.forEach(o->{
			NotificationRequest log = new NotificationRequest();
			log.setUser(getStringValue(o, 1));
			log.setDateTime(getStringValue(o, 3));
			log.setDescription(getStringValue(o, 4));
			
			for (UserProfile profile : UserProfile.values()) {
				if (profile.name().equals(getStringValue(o, 2))) {
					log.setProfile(profile.getDescribe());
					break;
				}
			} 
			
			listNotification.add(log);
		});
		return listNotification;
	}

}
