/*
 * Name: serviceInterface.java
 * Desc: This class lists the methods the UpdaterService uses as the interface between 
 * the PSIRT database and the application's activities
 */
package com.criticalalerts;

import java.util.ArrayList;

public interface serviceInterface {
	
	public int getNumOfPsirts();
	public ArrayList<PSIRT> getAllPsirts();
	public String[] getAllCustomerIds();
	public String[]	getAllInventoryIds();
}
