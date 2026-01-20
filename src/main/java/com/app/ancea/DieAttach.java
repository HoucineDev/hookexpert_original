package com.app.ancea;

/**
 * Cette classe contient les propriétés physiques du matériau DieAttach présent
 * dans les BGA et CSP.
 * 
 * @author Daniel
 *
 */
public class DieAttach extends ModelPhysiqueStatique {

	static final double masseVolumique = 2.5;
	static final double conductiviteThermique = 3.6;

	static final double moduleElasticiteK0 = 9.62E03;
	static final double moduleElasticiteK1 = -3.84;
	static final double moduleElasticiteK2 = 1.59E-01;
	static final double moduleElasticiteK3 = 9.22E-04;
	static final double moduleElasticiteK4 = -5.84E-05;
	static final double moduleElasticiteK5 = -3.39E-07;
	static final double moduleElasticiteK6 = 3.79E-09;

	static final double coefficientPoisson = 0.3;

	static final double coefficientDilatationK0 = 3.07E-05;
	static final double coefficientDilatationK1 = 5.24E-08;
	static final double coefficientDilatationK2 = 7.21E-10;
	static final double coefficientDilatationK3 = 2.18E-11;
	static final double coefficientDilatationK4 = -6.60E-13;
	static final double coefficientDilatationK5 = 2.21E-15;
	static final double coefficientDilatationK6 = -8.69E-19;

	public DieAttach() {
		super();
	}

	/**
	 * Renvoie la masse volumique du matériau DieAttach. Cette méthode n'est
	 * utile que si la propriété physique est définie par plusieurs
	 * coefficients.
	 */
	public double masse_volumique(double temperature) {
		double liste[] = {
				DieAttach.masseVolumique
		};
		return super.calcul(liste, temperature);
	}

	/**
	 * Renvoie le module d'élasticité du matériau DieAttach. Cette méthode n'est
	 * utile que si la propriété physique est définie par plusieurs
	 * coefficients.
	 */
	public double module_elasticite(double temperature) {
		double liste[] = {
				DieAttach.moduleElasticiteK0, DieAttach.moduleElasticiteK1,
				DieAttach.moduleElasticiteK2, DieAttach.moduleElasticiteK3,
				DieAttach.moduleElasticiteK4, DieAttach.moduleElasticiteK5,
				DieAttach.moduleElasticiteK6
		};
		return super.calcul(liste, temperature);
	}

	/**
	 * Renvoie le coefficient de poisson du matériau DieAttach. Cette méthode
	 * n'est utile que si la propriété physique est définie par plusieurs
	 * coefficients.
	 */
	public double coefficient_poisson(double temperature) {
		double liste[] = {
				DieAttach.coefficientPoisson
		};
		return super.calcul(liste, temperature);
	}

	/**
	 * Renvoie le coefficient de dilatation selon le plan xy du matériau
	 * DieAttach. Cette méthode n'est utile que si la propriété physique est
	 * définie par plusieurs coefficients.
	 */
	public double coef_dil_xy(double temperature) {
		double liste[] = {
				DieAttach.coefficientDilatationK0,
				DieAttach.coefficientDilatationK1,
				DieAttach.coefficientDilatationK2,
				DieAttach.coefficientDilatationK3,
				DieAttach.coefficientDilatationK4,
				DieAttach.coefficientDilatationK5,
				DieAttach.coefficientDilatationK6
		};
		return super.calcul(liste, temperature);
	}

}
