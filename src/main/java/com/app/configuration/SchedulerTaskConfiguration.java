package com.app.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableScheduling
public class SchedulerTaskConfiguration {
	
	@Bean
    public TaskScheduler taskScheduler() {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler() {
        private static final long serialVersionUID = -1L;
	        @Override
	        public void destroy() {
	            this.getScheduledThreadPoolExecutor().setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
	            super.destroy();
	        }
	    };
	    scheduler.setWaitForTasksToCompleteOnShutdown(true);
	    scheduler.setAwaitTerminationSeconds(60);
	    scheduler.setPoolSize(10);
	    return scheduler;
    }

}
