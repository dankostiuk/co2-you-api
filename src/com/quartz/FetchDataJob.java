package com.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.app.service.ServiceLoader;

/**
 * Entry point from web. Calls ServiceExecuter to carry out service calls.
 * This job is currently configured to run once every 6 hours on the hour.
 * 
 * @author dan
 */
public class FetchDataJob implements Job {
	
	@Override
    public void execute(JobExecutionContext context)
            throws JobExecutionException {
        
		ServiceLoader serviceLoader = new ServiceLoader();
		serviceLoader.runServices();
    }
}
