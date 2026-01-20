package com.app.ancea;

/**
 * Cette classe contient les propriétés physiques de la céramiques utilisées
 * dans les LCCC et les Résistances.
 * 
 * @author Daniel
 *
 */
public class Ceramique extends ModelPhysiqueStatique {

	// Propriétés Céramique
	public static final double masse_volumique = 3.8;
	public static final double conductivite_thermique = 17.0;
	public static final double module_elasticite = 2.80E05;
	public static final double coefficient_poisson = 0.3; //0.25;

	static final double coefficient_dilatation_k0 = 4.78E-06;
	static final double coefficient_dilatation_k1 = 7.91E-09;

	public Ceramique() {
		super();
	}

	/**
	 * Renvoie la masse volumique de la céramique. Cette méthode n'est utile que
	 * si la propriété physique est définie par plusieurs coefficients.
	 */
	public double masse_volumique(double temperature) {
		double liste[] = {
				Ceramique.masse_volumique
		};
		return super.calcul(liste, temperature);
	}

	/**
	 * Renvoie la conductivité thermique de la céramique. Cette méthode n'est
	 * utile que si la propriété physique est définie par plusieurs
	 * coefficients.
	 */
	public double conductivite_thermique(double temperature) {
		double liste[] = {
				Ceramique.conductivite_thermique
		};
		return super.calcul(liste, temperature);
	}

	/**
	 * Renvoie le module d'élasticité de la céramique. Cette méthode n'est utile
	 * que si la propriété physique est définie par plusieurs coefficients.
	 */
	public double module_elasticite(double temperature) {
		double liste[] = {
				Ceramique.module_elasticite
		};
		return super.calcul(liste, temperature);
	}

	/**
	 * Renvoie le coefficient de poisson de la céramique. Cette méthode n'est
	 * utile que si la propriété physique est définie par plusieurs
	 * coefficients.
	 */
	public double coefficient_poisson(double temperature) {
		double liste[] = {
				Ceramique.coefficient_poisson
		};
		return super.calcul(liste, temperature);
	}

	/**
	 * Renvoie le coefficient de dilatation selon le plan xy de la céramique.
	 * Cette méthode n'est utile que si la propriété physique est définie par
	 * plusieurs coefficients.
	 */
	public double coef_dil_xy(double temperature) {
		double liste[] = {
				Ceramique.coefficient_dilatation_k0,
				Ceramique.coefficient_dilatation_k1
		};
		return super.calcul(liste, temperature);
	}

}
