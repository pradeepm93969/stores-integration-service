package com.app.scheduler;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.app.repository.IntegrationLogRepository;
import com.app.service.SchedulerBaseService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class IntegrationsLogPurgerScheduler extends SchedulerBaseService {
	
	public static final String SCHEDULER_NAME = "MAGENTO_OUTBOUND_PRICE_INVENTORY_SCHEDULER";
	
	@Autowired
	private IntegrationLogRepository repository;
	
	@Scheduled(cron = "0 0 23 * * *", zone = "Asia/Karachi")
	//@Scheduled(cron = "0 */2 * * * *", zone = "Asia/Karachi")
    public void executeJob() {
		OffsetDateTime oneWeek = OffsetDateTime.now().minusWeeks(1);
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd' 'HH:mm:ss");
		try {
			repository.deleteRecords(fmt.format(oneWeek));
		} catch (Exception e) {
			log.error("Error while purging the records : " + e.getLocalizedMessage());
		}
	}
	
}

