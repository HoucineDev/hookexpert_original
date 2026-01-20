package com.app.ancea;

/**
 * 
 * Cette classe contient les propriétés physiques du matériau Silicium.
 *
 */
public class Silicium extends ModelPhysiqueStatique {

	static final double masse_volumique_k0 = 2.329;

	static final double conductivite_thermique_k0 = 141;

	static final double module_elasticite_k0 = 156000;
	static final double module_elasticite_k1 = 0;
	static final double module_elasticite_k2 = 0;

	static final double module_cisaillement_k0 = 64092;

	static final double coefficient_poisson_k0 = 0.217;

	static final double coefficient_dilatation_xy_k0 = 2.3625E-06;
	static final double coefficient_dilatation_xy_k1 = 1.03E-08;
	static final double coefficient_dilatation_xy_k2 = -3.00E-11;
	static final double coefficient_dilatation_xy_k3 = 7.00E-14;
	static final double coefficient_dilatation_xy_k4 = 7.00E-17;

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

	public Silicium() {
		super();
	}

	public double masse_volumique(double temperature) {
		double liste[] = {
				Silicium.masse_volumique_k0
		};
		return super.calcul(liste, temperature);
	}

	public double conductibilite(double temperature) {
		double liste[] = {
				Silicium.conductivite_thermique_k0
		};
		return super.calcul(liste, temperature);
	}

	public double module_elastique(double temperature) {
		double liste[] = {
				Silicium.module_elasticite_k0, Silicium.module_elasticite_k1,
				Silicium.module_elasticite_k2
		};
		return super.calcul(liste, temperature);
	}

	public double coef_poisson(double temperature) {
		double liste[] = {
				Silicium.coefficient_poisson_k0
		};
		return super.calcul(liste, temperature);
	}

	public double coef_dil_xy(double temperature) {
		double liste[] = {
				Silicium.coefficient_dilatation_xy_k0,
				Silicium.coefficient_dilatation_xy_k1,
				Silicium.coefficient_dilatation_xy_k2,
				Silicium.coefficient_dilatation_xy_k3,
				Silicium.coefficient_dilatation_xy_k4
		};
		return super.calcul(liste, temperature);
	}

	public double coef_dil_z(double temperature) {
		double liste[] = {
				Silicium.coefficient_dilatation_z_k0,
				Silicium.coefficient_dilatation_z_k1,
				Silicium.coefficient_dilatation_z_k2,
				Silicium.coefficient_dilatation_z_k3,
				Silicium.coefficient_dilatation_z_k4
		};
		return super.calcul(liste, temperature);
	}

	public double amorti(double temperature) {
		double liste[] = {
				Silicium.amorti_k0, Silicium.amorti_k1, Silicium.amorti_k2,
				Silicium.amorti_k3, Silicium.amorti_k4
		};
		return super.calcul(liste, temperature);
	}

	public double module_cisaillement(double temperature) {
		double liste[] = {
				Silicium.module_cisaillement_k0
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
