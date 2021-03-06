package org.fog.utils;

public class Config {

	public static final float RESOURCE_MGMT_INTERVAL = 1000;
//	public static int MAX_SIMULATION_TIME = 1000*60*60;
	public static int MAX_SIMULATION_TIME = 1000*60*10*2;
	//public static int MAX_SIMULATION_TIME = 1000*10;
	
	//public static int MAX_SIMULATION_TIME = 1000*1000;
	
	public static int RESOURCE_MANAGE_INTERVAL = 1000;
	
	public static String FOG_DEVICE_ARCH = "x86";
	public static String FOG_DEVICE_OS = "Linux";
	public static String FOG_DEVICE_VMM = "Xen";
	public static double FOG_DEVICE_TIMEZONE = 10.0;
	
	public static double FOG_DEVICE_COST = 3.0;
	public static double FOG_DEVICE_COST_PER_MEMORY = 0.05;
	public static double FOG_DEVICE_COST_PER_STORAGE = 0.001;
	public static double FOG_DEVICE_COST_PER_BW = 0.0;
}
