package com.app.ancea;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Resultat_temperature_WLP extends Resultat_temperature {

	ComposantWLP composant;

	DoubleProperty e1_traction;
	DoubleProperty e1_flexion;
	DoubleProperty cte1;

	public Resultat_temperature_WLP(
			ComposantWLP composant,
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

	public double calcul_e1_traction() {
		double calcul;
		Silicium s = new Silicium();
		double me_s = s.module_elastique(getTemperature());
		calcul = me_s;
		e1_traction.set(calcul);
		return calcul;
	}

	public double calcul_e1_flexion() {
		double calcul;
		Silicium s = new Silicium();
		double mf_s = s.module_elastique(getTemperature());
		calcul = mf_s;
		e1_flexion.set(calcul);
		return calcul;
	}

	protected double getCte1() {
		double calcul;
		Silicium s = new Silicium();
		double cd_s = s.coef_dil_xy(getTemperature());
		calcul = cd_s;
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
