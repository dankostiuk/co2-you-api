package com.app;

import java.io.IOException;

import com.app.service.ServiceLoader;

/**
 * Entry point from terminal. Calls ServiceExecuter to carry out service calls.
 * 
 * @author dan
 */
public class Main {
	
	public static void main(String[] args) throws IOException {
		
		ServiceLoader serviceLoader = new ServiceLoader();
		serviceLoader.runServices();
	}
}
