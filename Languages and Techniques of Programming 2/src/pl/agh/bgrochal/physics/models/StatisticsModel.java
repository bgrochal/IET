package pl.agh.bgrochal.physics.models;

import java.util.*;

public class StatisticsModel {

	private Map<String, Integer> buttonClicks = new TreeMap<String, Integer>();
	private Map<String, Integer> fieldValues = new TreeMap<String, Integer>();
	private Map<Double, String> buttonTime = new TreeMap<Double, String>();
	
	private int overallButtonClicks;
	private long startTime;
	
	
	public StatisticsModel() {
		this.startTime = System.currentTimeMillis();
		this.overallButtonClicks = 0;
	}
	
	public void mapValueIncrement(String itemName, String itemSort) {
		
		Map<String, Integer> temp;
		
		if(itemSort == "button") {
			buttonTime.put(((double)(System.currentTimeMillis() - startTime)/1000), itemName);
			temp = buttonClicks;
			overallButtonClicks++;
		}
		else
			temp = fieldValues;
		
		if(!temp.containsKey(itemName))
			temp.put(itemName, 1);
		else
			temp.put(itemName, temp.get(itemName) + 1);		/* Method put() replaces value */
			
		if(itemSort == "button")
			buttonClicks = temp;
		else
			fieldValues = temp;
		
	}
	
	public int getOverallButtonClicks() {
		return this.overallButtonClicks;
	}
	
	public Map<String, Integer> getButtonClicks() {
		return this.buttonClicks;
	}
	
	public Map<String, Integer> getFieldValues() {
		return this.fieldValues;
	}
	
	public Map<Double, String> getButtonTime() {
		return this.buttonTime;
	}
	
}
