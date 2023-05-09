package com.app.service;

import java.time.OffsetDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.app.repository.entity.SchedulerConfiguration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class SchedulerBaseService {
	
	private static final String RUNNING= "RUNNING";
	private static final String STOPPED= "STOPPED";
	
	@Value("${custom.schedulers.enabled}")
	private boolean enabled;
	
	@Autowired
	private SchedulerConfigurationService schedulerConfigurationService;
	
	public void logDebug(SchedulerConfiguration sc, String msg) {
		if (sc.isDebug()) {
			log.debug(msg);
		}
	}
	
	public SchedulerConfiguration getSchedulerConfiguration(String id) {
		return schedulerConfigurationService.getById(id);
	}
	
	public int getNextRetryCount(SchedulerConfiguration sc, int current) {
		return current == sc.getRetryCount() ? current : current+1;
	}
	
	public OffsetDateTime getNextRunTime(SchedulerConfiguration sc, int currentRetryCount) {
		int factor = sc.getExponentialFactor();
		while (currentRetryCount > 0) {
			factor = factor * factor;
			currentRetryCount--;
		}
		return OffsetDateTime.now().plusSeconds(sc.getNextRunSeconds() 
				+ factor * sc.getNextRunSeconds());
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public boolean startScheduler(SchedulerConfiguration sc) {
		boolean started = false;
		if (enabled) {
			SchedulerConfiguration dbSc = schedulerConfigurationService.getById(sc.getId());
			if (dbSc.getStatus().equalsIgnoreCase(RUNNING)) {
				if (OffsetDateTime.now().isAfter(dbSc.getUpdatedAt().plusSeconds(dbSc.getRestartSeconds()))) {
					dbSc.setStatus(RUNNING);
					schedulerConfigurationService.save(dbSc);
					started = true;
				}
			} else {
				dbSc.setStatus(RUNNING);
				schedulerConfigurationService.save(dbSc);
				started = true;
			}
		}
		return started;
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void stopScheduler(SchedulerConfiguration sc) {
		SchedulerConfiguration dbSc = schedulerConfigurationService.getById(sc.getId());
		dbSc.setStatus(STOPPED);
		dbSc.setLastSuccessfulEndAt(OffsetDateTime.now());
		dbSc.setLastSuccessfulStartAt(dbSc.getUpdatedAt());
		schedulerConfigurationService.save(dbSc);
	}

}
