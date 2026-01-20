package com.app.ancea;

/**
 * Cette classe sert à créer des cycles thermiques. 
 * Son principal interet est de rendre le code plus clair en passsant cette objet en parametre des fonctions/ constructeurs 
 * au lieu de rentrer toutes les valeurs une a une.
 *
 */
public class CycleThermique {
	
	private double temperature1, temperature2;
	private double duree1, duree2;
	
	public CycleThermique(double temperature1, double duree1, double temperature2, double duree2) {
		this.temperature1 = temperature1;
		this.temperature2 = temperature2;
		
		this.duree1 = duree1;
		this.duree2 = duree2;
	}

	public double getTemperature1() {
		return temperature1;
	}

	public void setTemperature1(double temperature1) {
		this.temperature1 = temperature1;
	}

	public double getTemperature2() {
		return temperature2;
	}

	public void setTemperature2(double temperature2) {
		this.temperature2 = temperature2;
	}

	public double getDuree1() {
		return duree1;
	}

	public void setDuree1(double duree1) {
		this.duree1 = duree1;
	}

	public double getDuree2() {
		return duree2;
	}

	public void setDuree2(double duree2) {
		this.duree2 = duree2;
	}
	
	public String toString() {
		return "temperature 1: "+temperature1+" duree1: "+duree1+" temperature 2:"+temperature2+" duree 2: "+duree2;
	}
}
