package com.app.ancea;

import java.util.logging.Logger;

public class Resultat_temperature_SBGA extends Resultat_temperature_BGA {

	private double cter;

	private static final Logger logger = Logger.getLogger(Main.class.getName());

	public Resultat_temperature_SBGA(
			ComposantSBGA composant,
			double temperature) {
		super(composant, temperature);

		this.composant = composant;

		cter = 0;

	}

	public void setValues() {
		calcul();
		setCter(calcul_cter());
	}

	public double calcul_cter() {
		double cuivre_x = ((ComposantSBGA) composant).getCuivreX();
		double pastille_x = composant.getPuce_x();
		double calcul = 0;

		calcul = (calcul_cte1() * pastille_x
				+ (calcul_cte2() * (cuivre_x - pastille_x))) / cuivre_x;

		logger.severe("CTER: " + calcul);
		setCter(calcul);

		return calcul;

	}

	public double getCter() {
		logger.fine("valeur CTER: " + cter);
		return cter;
	}

	public void setCter(double cter) {
		this.cter = cter;
	}

}
