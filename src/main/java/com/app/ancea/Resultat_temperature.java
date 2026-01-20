package com.app.ancea;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Resultat_temperature extends Resultat_temperature_Composant {
	
	protected Composant composant;
	
	private StringProperty temperature;
	private StringProperty module_traction;
	private StringProperty module_flexion;
	
	public Resultat_temperature(Composant composant, double temperature) {
		this.composant = composant;
		this.temperature = new SimpleStringProperty(String.valueOf(temperature));
	}
	

	protected double getTemperature() {
		return Double.parseDouble(temperature.getValue());
	}
	
	public StringProperty get_string_Temperature() {
		return temperature;
	}

	public void setTemperature(double temperature) {
		this.temperature.set(String.valueOf(temperature));
	}

	public StringProperty getModule_traction() {
		return module_traction;
	}

	public void setModule_traction(StringProperty module_traction) {
		this.module_traction = module_traction;
	}

	public StringProperty getModule_flexion() {
		return module_flexion;
	}

	public void setModule_flexion(StringProperty module_flexion) {
		this.module_flexion = module_flexion;
	}

}
