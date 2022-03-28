package br.com.segmedic.clubflex.repository;

import java.time.LocalDateTime;

import org.springframework.stereotype.Repository;

import br.com.segmedic.clubflex.domain.SubscriptionLog;

@Repository
public class SubscriptionLogRepository extends GenericRepository {
	
	
	public SubscriptionLog saveLog(SubscriptionLog subscriptionLog) {
		subscriptionLog.setDateTimeLog(LocalDateTime.now());
		super.persist(subscriptionLog);
		return subscriptionLog;
	}
}