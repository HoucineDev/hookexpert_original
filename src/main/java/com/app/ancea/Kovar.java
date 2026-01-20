package com.app.ancea;

/**
 * Cette classe contient les propriétés physiques du matériau Kovar.
 * 
 * @author Daniel
 * 
 */
public class Kovar extends ModelPhysiqueStatique {

	static final double masse_volumique = 8.2;
	static final double conductivite_thermique = 17.0;
	static final double module_elasticite = 1.34E05;
	static final double coefficient_poisson = 0.317;
	static final double coefficient_dilatation = 5.30E-06;

	public Kovar() {
		super();
	}

	/**
	 * Renvoie la masse volumique du Kovar. Cette méthode n'est utile que si la
	 * propriété physique est définie par plusieurs coefficients.
	 * 
	 */
	public double masse_volumique(double temperature) {
		double liste[] = {
				Kovar.masse_volumique
		};
		return super.calcul(liste, temperature);
	}

	/**
	 * Renvoie la conductivité thermique du Kovar. Cette méthode n'est utile que
	 * si la propriété physique est définie par plusieurs coefficients.
	 */
	public double conductivite_thermique(double temperature) {
		double liste[] = {
				Kovar.conductivite_thermique
		};
		return super.calcul(liste, temperature);
	}

	/**
	 * Renvoie le module d'élasticité du Kovar. Cette méthode n'est utile que si
	 * la propriété physique est définie par plusieurs coefficients.
	 */
	public double module_elasticite(double temperature) {
		double liste[] = {
				Kovar.module_elasticite
		};
		return super.calcul(liste, temperature);
	}

	/**
	 * Renvoie le coefficient de poisson du Kovar. Cette méthode n'est utile que
	 * si la propriété physique est définie par plusieurs coefficients.
	 */
	public double coefficient_poisson(double temperature) {
		double liste[] = {
				Kovar.coefficient_poisson
		};
		return super.calcul(liste, temperature);
	}

	/**
	 * Renvoie le coefficient de dilatation selon le plan xy du Kovar. Cette
	 * méthode n'est utile que si la propriété physique est définie par
	 * plusieurs coefficients.
	 */
	public double coef_dil_xy(double temperature) {
		double liste[] = {
				Kovar.coefficient_dilatation
		};
		return super.calcul(liste, temperature);
	}

}
