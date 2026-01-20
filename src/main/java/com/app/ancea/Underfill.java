package com.app.ancea;

/**
 * Cette classe contient les propriétés physiques du matériau underfill présent
 * dans les BGA et CSP.
 * 
 * @author Daniel
 *
 */
public class Underfill extends ModelPhysiqueStatique {

	static final double masseVolumique = 1.9;
	static final double conductiviteThermique = 0.6;

	static final double moduleElasticiteK0 = 8.69E03;
	static final double moduleElasticiteK1 = -2.12E01;
	static final double moduleElasticiteK2 = -2.72E-02;
	static final double moduleElasticiteK3 = 5.74E-03;
	static final double moduleElasticiteK4 = -2.05E-05;
	static final double moduleElasticiteK5 = -1.25E-06;
	static final double moduleElasticiteK6 = 7.21E-09;

	static final double moduleCisaillementK0 = 3.34E03;
	static final double moduleCisaillementK1 = -8.15;
	static final double moduleCisaillementK2 = -1.05E-02;
	static final double moduleCisaillementK3 = 2.21E-03;
	static final double moduleCisaillementK4 = -7.88E-06;
	static final double moduleCisaillementK5 = -4.83E-07;
	static final double moduleCisaillementK6 = 2.77E-09;

	static final double coefficientPoisson = 0.3;

	static final double coefficientDilatationK0 = 2.17E-05;
	static final double coefficientDilatationK1 = 2.70E-08;
	static final double coefficientDilatationK2 = -1.50E-10;
	static final double coefficientDilatationK3 = -2.44E-12;
	static final double coefficientDilatationK4 = 8.69E-14;
	static final double coefficientDilatationK5 = 4.49E-16;
	static final double coefficientDilatationK6 = -3.97E-18;

	public Underfill() {
		super();
	}

	/**
	 * Renvoie la masse volumique du matériau Underfill. Cette méthode n'est
	 * utile que si la propriété physique est définie par plusieurs
	 * coefficients.
	 */
	public double masse_volumique(double temperature) {
		double liste[] = {
				Underfill.masseVolumique
		};
		return super.calcul(liste, temperature);
	}

	/**
	 * Renvoie le module d'élasticité du matériau Underfill. Cette méthode n'est
	 * utile que si la propriété physique est définie par plusieurs
	 * coefficients.
	 */
	public double module_elasticite(double temperature) {
		double liste[] = {
				Underfill.moduleElasticiteK0, Underfill.moduleElasticiteK1,
				Underfill.moduleElasticiteK2, Underfill.moduleElasticiteK3,
				Underfill.moduleElasticiteK4, Underfill.moduleElasticiteK5,
				Underfill.moduleElasticiteK6
		};
		return super.calcul(liste, temperature);
	}

	/**
	 * Renvoie le coefficient de poisson du matériau Underfill. Cette méthode
	 * n'est utile que si la propriété physique est définie par plusieurs
	 * coefficients.
	 */
	public double coefficient_poisson(double temperature) {
		double liste[] = {
				Underfill.coefficientPoisson
		};
		return super.calcul(liste, temperature);
	}

	/**
	 * Renvoie le coefficient de dilatation selon le plan xy du matériau
	 * Underfill. Cette méthode n'est utile que si la propriété physique est
	 * définie par plusieurs coefficients.
	 */
	public double coef_dil_xy(double temperature) {
		double liste[] = {
				Underfill.coefficientDilatationK0,
				Underfill.coefficientDilatationK1,
				Underfill.coefficientDilatationK2,
				Underfill.coefficientDilatationK3,
				Underfill.coefficientDilatationK4,
				Underfill.coefficientDilatationK5,
				Underfill.coefficientDilatationK6
		};
		return super.calcul(liste, temperature);
	}

	/**
	 * Renvoie le module de cisaillement du matériau Underfill. Cette méthode
	 * n'est utile que si la propriété physique est définie par plusieurs
	 * coefficients.
	 */
	public double module_cisaillement(double temperature) {
		double liste[] = {
				Underfill.moduleCisaillementK0, Underfill.moduleCisaillementK1,
				Underfill.moduleCisaillementK2, Underfill.moduleCisaillementK3,
				Underfill.moduleCisaillementK4, Underfill.moduleCisaillementK5,
				Underfill.moduleCisaillementK6
		};
		return super.calcul(liste, temperature);
	}
}
