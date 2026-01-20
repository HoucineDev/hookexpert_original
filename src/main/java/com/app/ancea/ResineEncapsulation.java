package com.app.ancea;

/**
 * 
 * Cette classe contient les propriétés physiques de la Resine d'encapsulation.
 *
 */
public class ResineEncapsulation extends ModelPhysiqueStatique {

	static final double masse_volumique_k0 = 1.9;

	static final double conductivite_thermique_k0 = 0.5;

	static final double module_elasticite_k0 = 20000;
	static final double module_elasticite_k1 = 0;
	static final double module_elasticite_k2 = 0;

	static final double module_cisaillement_k0 = 8333;

	static final double coefficient_poisson_k0 = 0.2;

	// Erreur ?
	// final double coef_dil_xy_k0 = 2E-05;
	static final double coefficient_dilatation_xy_k0 = 1E-05;
	static final double coefficient_dilatation_xy_k1 = 1E-08;
	static final double coefficient_dilatation_xy_k2 = -9E-11;
	static final double coefficient_dilatation_xy_k3 = 6E-13;
	// Erreur ?
	// final double coef_dil_xy_k4 = 2E-14;
	static final double coefficient_dilatation_xy_k4 = 2E-14;

	static final double coefficient_dilatation_z_k0 = 0;
	static final double coefficient_dilatation_z_k1 = 0;
	static final double coefficient_dilatation_z_k2 = 0;
	static final double coefficient_dilatation_z_k3 = 0;
	static final double coefficient_dilatation_z_k4 = 0;

	static final double amorti_k0 = 0;
	static final double amorti_k1 = 0;
	static final double amorti_k2 = 0;
	static final double amorti_k3 = 0;
	static final double amorti_k4 = 0;

	public ResineEncapsulation() {
		super();
	}

	public double masse_volumique(double temperature) {
		double liste[] = {
				ResineEncapsulation.masse_volumique_k0
		};
		return super.calcul(liste, temperature);
	}

	public double conductibilite(double temperature) {
		double liste[] = {
				ResineEncapsulation.conductivite_thermique_k0
		};
		return super.calcul(liste, temperature);
	}

	public double module_elastique(double temperature) {
		double liste[] = {
				ResineEncapsulation.module_elasticite_k0,
				ResineEncapsulation.module_elasticite_k1,
				ResineEncapsulation.module_elasticite_k2
		};
		return super.calcul(liste, temperature);
	}

	public double coef_poisson(double temperature) {
		double liste[] = {
				ResineEncapsulation.coefficient_poisson_k0
		};
		return super.calcul(liste, temperature);
	}

	public double coef_dil_xy(double temperature) {
		double liste[] = {
				ResineEncapsulation.coefficient_dilatation_xy_k0,
				ResineEncapsulation.coefficient_dilatation_xy_k1,
				ResineEncapsulation.coefficient_dilatation_xy_k2,
				ResineEncapsulation.coefficient_dilatation_xy_k3,
				ResineEncapsulation.coefficient_dilatation_xy_k4
		};
		return super.calcul(liste, temperature);
	}

	public double coef_dil_z(double temperature) {
		double liste[] = {
				ResineEncapsulation.coefficient_dilatation_z_k0,
				ResineEncapsulation.coefficient_dilatation_z_k1,
				ResineEncapsulation.coefficient_dilatation_z_k2,
				ResineEncapsulation.coefficient_dilatation_z_k3,
				ResineEncapsulation.coefficient_dilatation_z_k4
		};
		return super.calcul(liste, temperature);
	}

	public double amorti(double temperature) {
		double liste[] = {
				ResineEncapsulation.amorti_k0, ResineEncapsulation.amorti_k1,
				ResineEncapsulation.amorti_k2, ResineEncapsulation.amorti_k3,
				ResineEncapsulation.amorti_k4
		};
		return super.calcul(liste, temperature);
	}

	public double module_cisaillement(double temperature) {
		double liste[] = {
				ResineEncapsulation.module_cisaillement_k0
		};
		return super.calcul(liste, temperature);
	}

	public double module_tissus(double temperature, String tissus) {
		double k0 = 0;
		double k1 = 0;
		double k2 = 0;
		double k3 = 0;
		double k4 = 0;
		if (tissus.equals("104")) {
			k2 = -0.0268;
			k1 = -11.058;
			k0 = 10730;
		} else if (tissus.equals("106")) {
			k2 = -0.0226;
			k1 = -10.866;
			k0 = 10447;
		} else if (tissus.equals("1080")) {
			k2 = -0.0285;
			k1 = -13.153;
			k0 = 13809;
		} else if (tissus.equals("2165")) {
			k2 = -0.0299;
			k1 = -14.836;
			k0 = 16286;
		} else if (tissus.equals("2113")) {
			k2 = -0.0302;
			k1 = -15.094;
			k0 = 16665;
		} else if (tissus.equals("2125")) {
			k2 = -0.0304;
			k1 = -15.356;
			k0 = 17051;
		} else if (tissus.equals("2116")) {
			k2 = -0.0311;
			k1 = -16.172;
			k0 = 18252;
		} else if (tissus.equals("7628")) {
			k2 = -0.0323;
			k1 = -17.64;
			k0 = 20416;
		}
		double liste[] = {
				k0, k1, k2, k3, k4
		};
		return super.calcul(liste, temperature);
	}

}
