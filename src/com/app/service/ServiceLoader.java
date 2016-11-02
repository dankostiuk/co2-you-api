package com.app.service;

import java.util.List;

import com.app.service.moves.MovesServiceExecutor;

import jersey.repackaged.com.google.common.collect.Lists;

/**
 * Loads a list of different serviceExecutors upon initialization and 
 * runs each service.
 * 
 * @author dan
 *
 */
public class ServiceLoader {
	
	private List<IServiceExecutor> _serviceExecutors;
	
	public ServiceLoader() {
		_serviceExecutors = Lists.newArrayList(new MovesServiceExecutor());
	}
	
	public void runServices() {
		for (IServiceExecutor serviceExecutor : getServiceExecutors()) {
			serviceExecutor.execute();
		}
	}
	
	public List<IServiceExecutor> getServiceExecutors() {
		return _serviceExecutors;
	}
}
