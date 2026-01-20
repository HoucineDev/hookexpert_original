package com.app.ancea;

import java.util.logging.Logger;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Resultat_temperature_CSP extends Resultat_temperature_BGA{
	
	ComposantCSP composant;

	private double cter;
	
	private static final Logger logger = Logger.getLogger(Main.class.getName());

	
	public Resultat_temperature_CSP(ComposantCSP composant, double temperature) {
		super(composant, temperature);
		
		this.composant = composant;
		
		cter = 0;
		
	}
	
	public void setValues() {
		calcul();
		setCter(calcul_cter());
	}
	
	public double calcul_cter() {
		double calcul = 0;
		calcul = (calcul_cte1() * composant.getPuce_x()
				+ (calcul_cte2() * (composant.getResine_x() - composant.getPuce_x()))) / composant.getResine_x();
		logger.severe("CTER: "+calcul);
		setCter(calcul);
		return calcul;

	}

	public double getCter() {
		logger.fine("valeur CTER: "+cter);
		return cter;
	}

	public void setCter(double cter) {
		this.cter = cter;
	}
	
	

}
