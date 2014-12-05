package com.c2point.tools;

public class Configuration {

	private static int major_version = 1;
	private static int mid_version = 0;
	private static int minor_version = 2;
	
	
	public static String getVersion() {
		
		return major_version + "." + mid_version + "." + minor_version;
	}
}
