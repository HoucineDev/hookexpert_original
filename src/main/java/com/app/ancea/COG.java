package com.app.ancea;

/**
 * Cette classe contient les propriétés physiques du matériau COG présent dans
 * les Capacités.
 * 
 * @author Daniel
 *
 */
public class COG extends ModelPhysiqueStatique {

	static final double masseVolumique = 5.3;
	static final double moduleElasticite = 1.25E05;
	static final double coefficientPoisson = 0.3;

	/*
	 * Le COG n'a pas de coefficient de dilatation pur il dépend de la valeur de
	 * la Capacité, on prend donc une valeur minimum et maximum puis on
	 * apliquera une règle de proportionnalité selon la valeur de Capacité
	 */
	static final double coefficientDilatationMinK0 = 6.88E-06;
	static final double coefficientDilatationMinK1 = 3.35E-09;
	static final double coefficientDilatationMinK2 = -2.71E-11;
	static final double coefficientDilatationMinK3 = 5.35E-13;
	static final double coefficientDilatationMinK4 = -1.43E-15;

	static final double coefficientDilatationMaxK0 = 8.03E-06;
	static final double coefficientDilatationMaxK1 = 6.76E-10;
	static final double coefficientDilatationMaxK2 = 4.31E-12;
	static final double coefficientDilatationMaxK3 = 3.74E-13;
	static final double coefficientDilatationMaxK4 = -1.13E-15;

	static final double[] valeurCapaciteCOGMinMaxFormat402 = {
			5.00E-01, 1.00E02
	};

	static final double[] valeurCapaciteCOGMinMaxFormat603 = {
			5.00E-01, 4.70E02
	};

	static final double[] valeurCapaciteCOGMinMaxFormat805 = {
			1.00, 1.50E03
	};

	static final double[] valeurCapaciteCOGMinMaxFormat1206 = {
			1.00, 3.30E03
	};

	static final double[] valeurCapaciteCOGMinMaxFormat1210 = {
			1.00E03, 6.80E03
	};

	static final double[] valeurCapaciteCOGMinMaxFormat1812 = {
			4.70E03, 1.00E04
	};

	/*
	 * Les essais sur les capacités ayant eu lieu avec le format 1812 on
	 * applique ce facteur correcteur dans le calcul des cte des capacités
	 */
	// static final double[] valeurReelleMinMaxGammeCOG = {
	// valeurCapaciteCOGMinMaxFormat1812[0],
	// valeurCapaciteCOGMinMaxFormat1812[1]
	// };

	// static final double[] valeurMesuree1812COGMinMax = {
	// 4.70E02, 2.20E03
	// };

	// static final double facteurCorrecteurCOG = (valeurReelleMinMaxGammeCOG[1]
	// - valeurReelleMinMaxGammeCOG[0])
	// / (valeurMesuree1812COGMinMax[1] - valeurMesuree1812COGMinMax[0]);

	public COG() {
		super();
	}

	/**
	 * Renvoie la masse volumique du matériau COG. Cette méthode n'est utile que
	 * si la propriété physique est définie par plusieurs coefficients.
	 */
	public double masse_volumique(double temperature) {
		double liste[] = {
				COG.masseVolumique
		};
		return super.calcul(liste, temperature);
	}

	/**
	 * Renvoie le module d'élasticité du matériau COG. Cette méthode n'est utile
	 * que si la propriété physique est définie par plusieurs coefficients.
	 */
	public double module_elasticite(double temperature) {
		double liste[] = {
				COG.moduleElasticite
		};
		return super.calcul(liste, temperature);
	}

	/**
	 * Renvoie le coefficient de poisson du matériau COG. Cette méthode n'est
	 * utile que si la propriété physique est définie par plusieurs
	 * coefficients.
	 */
	public double coefficient_poisson(double temperature) {
		double liste[] = {
				COG.coefficientPoisson
		};
		return super.calcul(liste, temperature);
	}

	/**
	 * Renvoie le coefficient de dilatation min selon le plan xy du matériau
	 * COG. Cette méthode n'est utile que si la propriété physique est définie
	 * par plusieurs coefficients.
	 */
	public double coef_dil_xy_min(double temperature) {
		double liste[] = {
				COG.coefficientDilatationMinK0, COG.coefficientDilatationMinK1,
				COG.coefficientDilatationMinK2, COG.coefficientDilatationMinK3,
				COG.coefficientDilatationMinK4
		};
		return super.calcul(liste, temperature);
	}

	/**
	 * Renvoie le coefficient de dilatation min selon le plan xy du matériau
	 * COG. Cette méthode n'est utile que si la propriété physique est définie
	 * par plusieurs coefficients.
	 */
	public double coef_dil_xy_max(double temperature) {
		double liste[] = {
				COG.coefficientDilatationMaxK0, COG.coefficientDilatationMaxK1,
				COG.coefficientDilatationMaxK2, COG.coefficientDilatationMaxK3,
				COG.coefficientDilatationMaxK4
		};
		return super.calcul(liste, temperature);
	}

	/**
	 * Suivat le format demandé, la fonction renvoie le tableau adéquat.
	 * 
	 * @param format Le format du boitier
	 * @return La liste des valeurs MinMax des Capacités de ce boitier pour le
	 *         matériau COG.
	 */
	public double[] getValeursMinMaxCapaciteCOG(String format) {
		switch (format) {
		case "0402":
			return valeurCapaciteCOGMinMaxFormat402;
		case "0603":
			return valeurCapaciteCOGMinMaxFormat603;
		case "0805":
			return valeurCapaciteCOGMinMaxFormat805;
		case "1206":
			return valeurCapaciteCOGMinMaxFormat1206;
		case "1210":
			return valeurCapaciteCOGMinMaxFormat1210;
		case "1812":
			return valeurCapaciteCOGMinMaxFormat1812;
		default:
			return null;
		}
	}
}
