package com.app.ancea;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Resultat_temperature_CBGA extends Resultat_temperature {

	ComposantWLP composant;

	DoubleProperty e1_traction;
	DoubleProperty e1_flexion;
	DoubleProperty cte1;

	public Resultat_temperature_CBGA(
			ComposantCBGA composant,
			double temperature) {
		super(composant, temperature);

		e1_traction = new SimpleDoubleProperty();
		e1_flexion = new SimpleDoubleProperty();
		cte1 = new SimpleDoubleProperty();

	}

	public void setValues() {
		e1_traction.set(calcul_e1_traction());
		e1_flexion.set(calcul_e1_flexion());
		calcul_cte1();
	}

	/**
	 * @return Le module de traction e1.
	 */
	public double calcul_e1_traction() {
		Ceramique c = new Ceramique();
		double moduleElasticiteCeramique = c
				.module_elasticite(getTemperature());
		e1_traction.set(moduleElasticiteCeramique);
		return moduleElasticiteCeramique;
	}

	/**
	 * @return Le module de flexion e1.
	 */
	public double calcul_e1_flexion() {
		double moduleFlexionCeramique;
		Ceramique c = new Ceramique();
		moduleFlexionCeramique = c.module_elasticite(getTemperature());
		e1_flexion.set(moduleFlexionCeramique);
		return moduleFlexionCeramique;
	}

	/**
	 * @return Le coefficient de dilatation cte1.
	 */
	protected double getCte1() {
		double calcul;
		Ceramique c = new Ceramique();
		calcul = c.coef_dil_xy(getTemperature());
		return calcul;
	}

	public void calcul_cte1() {

		cte1.set(
				Double.valueOf(
						Outils.nombreChiffreScientifiqueApresVirgule(getCte1())
								.replace(',', '.')));

	}

	public DoubleProperty get_cte1() {
		return cte1;
	}

	public DoubleProperty getE1_flexion() {
		return e1_flexion;
	}

	public DoubleProperty getE1_traction() {
		return e1_traction;
	}

}
