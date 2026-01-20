package com.app.ancea;

//import com.sun.media.jfxmedia.logging.Logger;

/**
 * 
 * Classe mère des différents matériaux. Elle permet de réaliser le calcul des
 * propriétés physique lorsque celles-ci sont définies par plusieurs
 * coeefficients.
 *
 */
public class ModelPhysiqueStatique {

	/**
	 * Cette méthode permet de calculer la valeur de propriété physique à une
	 * température donnée en utilisant les diférents coefficients de la
	 * propriété.
	 * 
	 * @param coefficient Liste des coefficients classées par ordre croissant
	 *                    d'exposant. Ex: {1,2,3} -> 1 * X^0 + 2 * X^1 + 3 * X^2
	 * @param temperature La température à laquelle on souhaite calculer la
	 *                    propriété physique
	 * @return La valeur de la propriété physique
	 * 
	 */
	public double calcul(
			double coefficient[],
			// double k0,
			// double k1,
			// double k2,
			// double k3,
			// double k4,
			double temperature) {

		double res = 0;

		for (int i = 0; i < coefficient.length; i++) {
			res += coefficient[i] * Math.pow(temperature, i);
		}
		/*
		 * res = (k4 * Math.pow(temperature, 4)) + (k3 * Math.pow(temperature,
		 * 3)) + (k2 * Math.pow(temperature, 2)) + (k1 * Math.pow(temperature,
		 * 1)) + (k0 * Math.pow(temperature, 0));
		 */
		return res;
	}

	public double masse_volumique(double temperature) {
		return (Double) null;
	}

	public double conductibilite(double temperature) {
		return (Double) null;
	}

	public double module_elastique(double temperature) {
		return (Double) null;
	}

	public double coef_poisson(double temperature) {
		return (Double) null;
	}

	public double coef_dil_xy(double temperature) {
		return (Double) null;
	}

	public double coef_dil_x(double temperature, String tissus) {
		return (Double) null;
	}

	public double coef_dil_y(double temperature, String tissus) {
		return (Double) null;
	}

	public double coef_dil_z(double temperature) {
		return (Double) null;
	}

	public double amorti(double temperature) {
		return (Double) null;
	}

	public double module_tissus(double temperature, String tissus) {
		return (Double) null;
	}

	public double module_tissus_x(double temperature, String tissus) {
		return (Double) null;
	}

	public double module_tissus_y(double temperature, String tissus) {
		return (Double) null;
	}

}
