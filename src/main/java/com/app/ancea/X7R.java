package com.app.ancea;

/**
 * Cette classe contient les propriétés physiques du matériau X7R présent dans
 * les Capacités.
 * 
 * @author Daniel
 *
 */
public class X7R extends ModelPhysiqueStatique {

	static final double masseVolumique = 5.6;
	static final double moduleElasticite = 1.15E05;
	static final double coefficientPoisson = 0.3;

	/*
	 * Le X7R n'a pas de coefficient de dilatation pur il dépend de la valeur de
	 * la Capacité, on prend donc une valeur minimum et maximum puis on
	 * apliquera une règle de proportiionnalité selon la veur de Capacité
	 */
	static final double coefficientDilatationMinK0 = 6.59E-06;
	static final double coefficientDilatationMinK1 = 7.29E-09;
	static final double coefficientDilatationMinK2 = -1.04E-10;
	static final double coefficientDilatationMinK3 = -9.09E-13;
	static final double coefficientDilatationMinK4 = 9.3E-15;

	static final double coefficientDilatationMaxK0 = 7.57E-06;
	static final double coefficientDilatationMaxK1 = 6.81E-09;
	static final double coefficientDilatationMaxK2 = -1.95E-10;
	static final double coefficientDilatationMaxK3 = 7.64E-13;
	static final double coefficientDilatationMaxK4 = 2.32E-15;

	static final double[] valeurCapaciteX7RMinMaxFormat402 = {
			1.00E02, 1.00E04
	};

	static final double[] valeurCapaciteX7RMinMaxFormat603 = {
			1.00E02, 1.00E05
	};

	static final double[] valeurCapaciteX7RMinMaxFormat805 = {
			4.70E02, 4.70E05
	};

	static final double[] valeurCapaciteX7RMinMaxFormat1206 = {
			2.20E03, 1.00E06
	};

	static final double[] valeurCapaciteX7RMinMaxFormat1210 = {
			2.20E04, 2.20E06
	};

	static final double[] valeurCapaciteX7RMinMaxFormat1812 = {
			1.00E05, 4.70E06
	};

	/*
	 * Les essais sur les capacités ayant eu lieu avec le format 1812 on
	 * applique ce facteur correcteur dans le calcul des cte des capacités
	 */
	// static final double[] valeurReelleMinMaxGammeX7R = {
	// valeurCapaciteX7RMinMaxFormat1812[0],
	// valeurCapaciteX7RMinMaxFormat1812[1]
	// };

	// static final double[] valeurMesuree1812X7RMinMax = {
	// 2.20E05, 4.70E06
	// };

	// static final double facteurCorrecteurX7R = (valeurReelleMinMaxGammeX7R[1]
	// - valeurReelleMinMaxGammeX7R[0])
	// / (valeurMesuree1812X7RMinMax[1] - valeurMesuree1812X7RMinMax[0]);

	public X7R() {
		super();
	}

	/**
	 * Renvoie la masse volumique du matériau X7R. Cette méthode n'est utile que
	 * si la propriété physique est définie par plusieurs coefficients.
	 */
	public double masse_volumique(double temperature) {
		double liste[] = {
				X7R.masseVolumique
		};
		return super.calcul(liste, temperature);
	}

	/**
	 * Renvoie le module d'élasticité du matériau X7R. Cette méthode n'est utile
	 * que si la propriété physique est définie par plusieurs coefficients.
	 */
	public double module_elasticite(double temperature) {
		double liste[] = {
				X7R.moduleElasticite
		};
		return super.calcul(liste, temperature);
	}

	/**
	 * Renvoie le coefficient de poisson du matériau X7R. Cette méthode n'est
	 * utile que si la propriété physique est définie par plusieurs
	 * coefficients.
	 */
	public double coefficient_poisson(double temperature) {
		double liste[] = {
				X7R.coefficientPoisson
		};
		return super.calcul(liste, temperature);
	}

	/**
	 * Renvoie le coefficient de dilatation max selon le plan xy du matériau
	 * X7R. Cette méthode n'est utile que si la propriété physique est définie
	 * par plusieurs coefficients.
	 */
	public double coef_dil_xy_max(double temperature) {
		double liste[] = {
				X7R.coefficientDilatationMaxK0, X7R.coefficientDilatationMaxK1,
				X7R.coefficientDilatationMaxK2, X7R.coefficientDilatationMaxK3,
				X7R.coefficientDilatationMaxK4
		};
		return super.calcul(liste, temperature);
	}

	/**
	 * Renvoie le coefficient de dilatation min selon le plan xy du matériau
	 * X7R. Cette méthode n'est utile que si la propriété physique est définie
	 * par plusieurs coefficients.
	 */
	public double coef_dil_xy_min(double temperature) {
		double liste[] = {
				X7R.coefficientDilatationMinK0, X7R.coefficientDilatationMinK1,
				X7R.coefficientDilatationMinK2, X7R.coefficientDilatationMinK3,
				X7R.coefficientDilatationMinK4
		};
		return super.calcul(liste, temperature);
	}

	/**
	 * Suivat le format demandé, la fonction renvoie le tableau adéquat.
	 * 
	 * @param format Le format du boitier
	 * @return La liste des valeurs MinMax des Capacités de ce boitier pour le
	 *         matériau X7R.
	 */
	public double[] getValeursMinMaxCapaciteX7R(String format) {
		switch (format) {
		case "0402":
			return valeurCapaciteX7RMinMaxFormat402;
		case "0603":
			return valeurCapaciteX7RMinMaxFormat603;
		case "0805":
			return valeurCapaciteX7RMinMaxFormat805;
		case "1206":
			return valeurCapaciteX7RMinMaxFormat1206;
		case "1210":
			return valeurCapaciteX7RMinMaxFormat1210;
		case "1812":
			return valeurCapaciteX7RMinMaxFormat1812;
		default:
			return null;
		}
	}
}
