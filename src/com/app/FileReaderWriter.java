package com.app;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Reads and returns Moves access_token from creds file.
 * 
 * @author dan
 */
public class FileReaderWriter {
	
	public static String loadAccessTokenFromFile() {
		Properties prop = new Properties();
		InputStream input = null;

		String accessToken;
		try {

			input = new FileInputStream("creds");

			// load a properties file
			prop.load(input);

			// get the property value and print it out
			accessToken = prop.getProperty("access_token");
			System.out.println("Using access token: " + accessToken);
			
			return accessToken;
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
}
