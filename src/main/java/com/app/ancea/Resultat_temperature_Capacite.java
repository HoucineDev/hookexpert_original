package com.app.ancea;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Resultat_temperature_Capacite extends Resultat_temperature {

	Composant_Resistance composant;

	DoubleProperty epaisseur, cte, e1, v;

	public Resultat_temperature_Capacite(
			Composant composant,
			double temperature) {
		super(composant, temperature);

		// cte = new SimpleDoubleProperty(Capacite.coefficient_de_dilatation);
		cte = new SimpleDoubleProperty();
		e1 = new SimpleDoubleProperty(Capacite.module_elasticite);
		v = new SimpleDoubleProperty(Capacite.coefficient_poisson);

	}

	// Mis en commentaire car nouvelle fonction DoubleProperty getCte() { return
	// cte; }

	///// Ajouté
	public void setValues() {
		calcul_cte1();
	}

	/**
	 * 
	 * @return Le coefficient de dilatation selon matériau utilisé (COG ou X7R).
	 */
	/*
	 * protected double getCte1() { double calcul = 0;
	 * 
	 * if (Capacite.x7r_utilise) { X7R x7r = new X7R(); double cd_x7r =
	 * x7r.coef_dil_xy(getTemperature()); calcul = cd_x7r; } else if
	 * (Capacite.cog_utilise) { COG cog = new COG(); double cd_cog =
	 * cog.coef_dil_xy(getTemperature()); calcul = cd_cog; } else {
	 * System.out.println("Erreur X7R ou COG non recu"); } return calcul; }
	 */

	public double getCte1() {

		double calcul;
		double valeurCapaciteReelle = Capacite.valeur_capacite;
		Double valeurCapaciteMin;
		Double valeurCapaciteMax;
		double coefficientDilatationMax;
		double coefficientDilatationMin;
		// double facteurCorrecteur;

		if (Capacite.x7r_utilise) {
			X7R x7r = new X7R();
			coefficientDilatationMax = x7r.coef_dil_xy_max(getTemperature());
			coefficientDilatationMin = x7r.coef_dil_xy_min(getTemperature());
			try {
				valeurCapaciteMin = x7r
						.getValeursMinMaxCapaciteX7R(Capacite.formatBoitier)[0];
				valeurCapaciteMax = x7r
						.getValeursMinMaxCapaciteX7R(Capacite.formatBoitier)[1];
			} catch (NullPointerException e) {
				valeurCapaciteMin = (Double) null;
				valeurCapaciteMax = (Double) null;
				Outils.alerteUtilisateur(
						"ERROR",
						"Format Inconnu",
						"Format non implémenté dans X7R");
			}
			// facteurCorrecteur = x7r.facteurCorrecteurX7R;

		} else if (Capacite.cog_utilise) {

			COG cog = new COG();
			coefficientDilatationMax = cog.coef_dil_xy_max(getTemperature());
			coefficientDilatationMin = cog.coef_dil_xy_min(getTemperature());
			try {
				valeurCapaciteMin = cog
						.getValeursMinMaxCapaciteCOG(Capacite.formatBoitier)[0];
				valeurCapaciteMax = cog
						.getValeursMinMaxCapaciteCOG(Capacite.formatBoitier)[1];
			} catch (NullPointerException e) {
				valeurCapaciteMin = (Double) null;
				valeurCapaciteMax = (Double) null;
				Outils.alerteUtilisateur(
						"ERROR",
						"Format Inconnu",
						"Format non implémenté dans COG");
			}
			// facteurCorrecteur = cog.facteurCorrecteurCOG;

		} else {
			coefficientDilatationMax = (Double) null;
			coefficientDilatationMin = (Double) null;
			valeurCapaciteMax = (Double) null;
			valeurCapaciteMin = (Double) null;
			// facteurCorrecteur = (Double) null;

			Outils.alerteUtilisateur(
					"ERROR",
					"Erreur Materiau Capacité",
					"Matériau non reconnu (X7R ou COG)");
		}

		calcul = (valeurCapaciteReelle - valeurCapaciteMin)
				/ (valeurCapaciteMax - valeurCapaciteMin)
				* (coefficientDilatationMax - coefficientDilatationMin)
				+ coefficientDilatationMin;

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
