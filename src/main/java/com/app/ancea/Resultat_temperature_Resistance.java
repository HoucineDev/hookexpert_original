package com.app.ancea;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Resultat_temperature_Resistance extends Resultat_temperature {

	Composant_Resistance composant;

	DoubleProperty epaisseur, cte, e1, v;

	public Resultat_temperature_Resistance(
			Composant composant,
			double temperature) {
		super(composant, temperature);

		epaisseur = new SimpleDoubleProperty(Resistance.EPAISSEUR);
		cte = new SimpleDoubleProperty();
		e1 = new SimpleDoubleProperty(Ceramique.module_elasticite);
		v = new SimpleDoubleProperty(Ceramique.coefficient_poisson);

	}

	DoubleProperty getEpaisseur() {
		return epaisseur;
	}

	/**
	 * DoubleProperty getCte() { return cte; }
	 **/
	///// Ajouté
	public void setValues() {
		calcul_cte1();
	}

	//////
	DoubleProperty getE1() {
		return e1;
	}

	DoubleProperty getV() {
		return v;
	}

	/**
	 * 
	 * @return La valeur de dilatation de la céramique en fonction de la
	 *         température.
	 */
	protected double getCte1() {
		double calcul = 0;
		Ceramique c = new Ceramique();
		double cd_resistance = c.coef_dil_xy(getTemperature());
		calcul = cd_resistance;
		return calcul;
	}

	public DoubleProperty get_cte1() {
		return cte;
	}

	public void calcul_cte1() {

		cte.set(
				Double.valueOf(
						Outils.nombreChiffreScientifiqueApresVirgule(getCte1())
								.replace(',', '.')));

	}

//////////////////////////////////////////////////////////////

	public String toString() {
		return getTemperature() + ";" + e1.get() + ";" + cte.get();
	}

}
