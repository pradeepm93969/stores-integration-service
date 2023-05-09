package com.app.zcustom.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.app.repository.entity.SchedulerConfiguration;
import com.app.service.SchedulerBaseService;
import com.app.zcustom.service.MagentoService;

@Component
public class MagentoOutboundPriceInventoryScheduler extends SchedulerBaseService {
	
	public static final String SCHEDULER_NAME = "MAGENTO_OUTBOUND_PRICE_INVENTORY_SCHEDULER";
	
	@Autowired
	private MagentoService magentoService;
	
	@Scheduled(cron = "0 */2 * * * *", zone = "Asia/Karachi")
    public void executeJob() {
		SchedulerConfiguration cg = getSchedulerConfiguration(SCHEDULER_NAME);
		if (cg != null && cg.isEnabled() && startScheduler(cg)) {
			try {
				magentoService.updatePriceInventoryToMagento(cg);
			} finally {
				stopScheduler(cg);
			}
		}
	}
    
	
}
