package com.quartz;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.quartz.DailyTimeIntervalScheduleBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.TimeOfDay;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.app.Constants;

/**
 * Sets up FetchDataJob and configures quartz fire time.
 * 
 * @author dan
 */
public class QuartzListener implements ServletContextListener {

	Scheduler _scheduler = null;
	
	@Override
	public void contextDestroyed(ServletContextEvent servletContext) {
		System.out.println("Context Destroyed");
        try 
        {
                _scheduler.shutdown();
        } 
        catch (SchedulerException e) 
        {
                e.printStackTrace();
        }
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContext) {
		System.out.println("Context Initialized");
		
		ServletContext context = servletContext.getServletContext();
        context.setAttribute(Constants.CACHE_KEY, 
        		new ConcurrentHashMap<String, Map<String, String>>());
		
		try {
			// Setup the Job class and the Job group
			JobDetail job = newJob(FetchDataJob.class).withIdentity(
			                 "CronQuartzJob", "Group").build();
			
			// every 24 hours starting midnight
			Trigger trigger = TriggerBuilder.newTrigger().withSchedule(DailyTimeIntervalScheduleBuilder.dailyTimeIntervalSchedule()
			        .onEveryDay()
			        .startingDailyAt(TimeOfDay.hourAndMinuteOfDay(23, 59)).withIntervalInHours(24))
					.withIdentity("TriggerName", "Group")
				.build();
				
			// Setup the Job and Trigger with Scheduler & schedule jobs
			_scheduler = new StdSchedulerFactory().getScheduler();
			_scheduler.start();
			_scheduler.scheduleJob(job, trigger);
			
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
}
