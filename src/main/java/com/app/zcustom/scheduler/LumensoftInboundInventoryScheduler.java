package com.app.zcustom.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.app.repository.entity.SchedulerConfiguration;
import com.app.service.SchedulerBaseService;
import com.app.zcustom.service.LumensoftService;

@Component
public class LumensoftInboundInventoryScheduler extends SchedulerBaseService {
	
	public static final String SCHEDULER_NAME = "LUMENSOFT_INBOUND_INVENTORY_SCHEDULER";
	
	@Autowired
	private LumensoftService lumensoftService;
	
	@Scheduled(cron = "0 */15 * * * *", zone = "Asia/Karachi")
    public void executeJob() {
		SchedulerConfiguration cg = getSchedulerConfiguration(SCHEDULER_NAME);
		if (cg != null && cg.isEnabled() && startScheduler(cg)) {
			try {
				lumensoftService.fetchInventoryFromLumensoft(cg);
			} finally {
				stopScheduler(cg);
			}
		}
	}
    
	
}
