package com.app.zcustom.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.app.repository.entity.SchedulerConfiguration;
import com.app.service.SchedulerBaseService;
import com.app.zcustom.service.LumensoftService;

@Component
public class LumensoftInboundPriceScheduler extends SchedulerBaseService {
	
	public static final String SCHEDULER_NAME = "LUMENSOFT_INBOUND_PRICE_SCHEDULER";
	
	@Autowired
	private LumensoftService lumensoftService;
	
	@Scheduled(cron = "0 0 0 * * *", zone = "Asia/Karachi")
    public void executeJob() {
		SchedulerConfiguration cg = getSchedulerConfiguration(SCHEDULER_NAME);
		if (cg != null && cg.isEnabled() && startScheduler(cg)) {
			try {
				lumensoftService.fetchPriceFromLumensoft(cg);
			} finally {
				stopScheduler(cg);
			}
		}
	}
    
	

}
