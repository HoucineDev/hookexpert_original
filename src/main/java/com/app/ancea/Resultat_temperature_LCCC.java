package com.app.ancea;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Resultat_temperature_LCCC extends Resultat_temperature {

	Composant_Resistance composant;

	DoubleProperty epaisseur;
	DoubleProperty e1;
	DoubleProperty v;
	DoubleProperty cte;

	public Resultat_temperature_LCCC(Composant composant, double temperature) {
		super(composant, temperature);

		epaisseur = new SimpleDoubleProperty(Resistance.EPAISSEUR);
		cte = new SimpleDoubleProperty();
		e1 = new SimpleDoubleProperty(Ceramique.module_elasticite);
		v = new SimpleDoubleProperty(Ceramique.coefficient_poisson);

	}

	DoubleProperty getEpaisseur() {
		return epaisseur;
	}

	// mis en commentaire Car nouvelle fonction DoubleProperty getCte() { return
	// cte; }

	///// Ajouté
	public void setValues() {
		calcul_cte1();
	}

	/**
	 * 
	 * @return La valeur de la dilatation de la Céramique en fonction de la
	 *         température.
	 */
	protected double getCte1() {
		double calcul = 0;

		Ceramique c = new Ceramique();
		double cd_lcc = c.coef_dil_xy(getTemperature());
		calcul = cd_lcc;
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
	DoubleProperty getE1() {
		return e1;
	}

	DoubleProperty getV() {
		return v;
	}

	public String toString() {
		return getTemperature() + ";" + e1.get() + ";" + cte.get();
	}

}
