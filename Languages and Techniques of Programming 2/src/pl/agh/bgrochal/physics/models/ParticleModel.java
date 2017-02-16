package pl.agh.bgrochal.physics.models;

import java.util.*;
import java.io.*;

public class ParticleModel {

	private double mass;
	private double relativeVelocity;
	
	private String unit;
	private String name;
	private double energy;
	
	private Properties particlesProp;
	
	private enum availableUnits {
	    eV, keV, MeV, GeV, TeV;
	}
	
	private enum availableNames {
		Helium, Proton, Carbon, Lithium, Neutron;
	}
	
	
	public ParticleModel() {
		this.name = "";
		this.unit = "";
		loadProperties();
	}
	
	public void loadProperties() {
		particlesProp = new Properties();
		InputStream ins = null;
		
		try {
			ins = new FileInputStream("properties/Particles.properties");
			particlesProp.load(ins);
		}
		catch(IOException e) {
			return;
		}
	}
	
	public void countRelativeVelocity() {
		energy = convertEnergyUnit();
		mass = getParticleMass();
		
		relativeVelocity = Math.sqrt(1- Math.pow((mass/(mass+energy)), 2));
	}
	
	public void countEnergy() {
		mass = getParticleMass();
		
		energy = mass*(1/Math.sqrt(1 - Math.pow(relativeVelocity, 2)) - 1);		/* [MeV] */
		
		availableUnits conversion = availableUnits.valueOf(unit);
		switch(conversion) {
			case eV:
				energy *= (1e6);
				break;
			case keV:
				energy *= (1e3);
				break;
			case GeV:
				energy /= (1e3);
				break;
			case TeV:
				energy /= (1e6);
				break;
			default:
				break;
		}
	}
	
	public double convertEnergyUnit() {
		availableUnits conversion = availableUnits.valueOf(unit);
		switch(conversion) {
			case eV:
				return energy/(1e6);
			case keV:
				return energy/(1e3);
			case MeV:
				return energy;
			case GeV:
				return energy*(1e3);
			case TeV:
				return energy*(1e6);
			default:
				return energy;
		}
	}
	
	private double getParticleMass() {										/* particle mass * c^2 [MeV] */
		availableNames conversion = availableNames.valueOf(name);
		String tempVal;
		
		switch(conversion) {
			case Helium:
				tempVal = particlesProp.getProperty("HeliumMass");
				break;
			case Proton:
				tempVal = particlesProp.getProperty("ProtonMass");
				break;
			case Carbon:
				tempVal = particlesProp.getProperty("CarbonMass");
				break;
			case Lithium:
				tempVal = particlesProp.getProperty("LithiumMass");
				break;
			case Neutron:
				tempVal = particlesProp.getProperty("NeutronMass");
				break;
			default:
				tempVal = "0";
		}
		
		try {
			return Double.parseDouble(tempVal);
		}
		catch(NumberFormatException e) {
			return 0;
		}
	}
	
	public boolean compareName(String pname) {
		for(availableNames particle : availableNames.values()) {
			if(particle.name().equalsIgnoreCase(pname))
				return true;
		}
		
		return false;
	}
	
	public boolean compareUnit(String punit) {
		for(availableUnits units : availableUnits.values()) {
			if(units.name().equalsIgnoreCase(punit))
				return true;
		}
		
		return false;
	}
	
	public void setUnit(String unit) {
		this.unit = unit;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setEnergy(double energy) {
		this.energy = energy;
	}
	
	public void setRelativeVelocity(double velocity) {
		this.relativeVelocity = velocity;
	}
	
	public Double getRelativeVelocity() {
		return this.relativeVelocity;
	}
	
	public Double getEnergy() {
		return this.energy;
	}
	
	public String getName() {
		return this.name;
	}

}
