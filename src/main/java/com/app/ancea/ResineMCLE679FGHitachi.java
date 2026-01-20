package com.app.ancea;

/**
 * 
 * Cette classe contient les propriétés physiques de la résine MCLE679FG
 * Hitachi.
 *
 */
public class ResineMCLE679FGHitachi extends ModelPhysiqueStatique {

	static final double masse_volumique_k0 = 1.58;

	static final double conductivite_thermique_k0 = 0.2;

	static final double module_elasticite_k0 = 5870;
	static final double module_elasticite_k1 = -12.1;
	static final double module_elasticite_k2 = -0.0341;

	static final double coefficient_poisson_k0 = 0.33;

	static final double coefficient_dilatation_xy_k0 = 3.24E-05;
	static final double coefficient_dilatation_xy_k1 = 8.00E-08;
	static final double coefficient_dilatation_xy_k2 = 5.00E-10;

	static final double coefficient_dilatation_z_k0 = 7.00E-05;
	static final double coefficient_dilatation_z_k1 = 8.00E-08;
	static final double coefficient_dilatation_z_k2 = 3.00E-10;

	static final double amorti_k0 = 7.00E-04;
	static final double amorti_k1 = 9.00E-05;
	static final double amorti_k2 = 2.00E-06;
	static final double amorti_k3 = -2.00E08;
	static final double amorti_k4 = 4.00E-11;

	public ResineMCLE679FGHitachi() {
		super();
	}

	public double masse_volumique(double temperature) {
		double liste[] = {
				ResineMCLE679FGHitachi.masse_volumique_k0
		};
		return super.calcul(liste, temperature);
	}

	public double conductibilite(double temperature) {
		double liste[] = {
				ResineMCLE679FGHitachi.conductivite_thermique_k0
		};
		return super.calcul(liste, temperature);
	}

	public double module_elastique(double temperature) {
		double liste[] = {
				ResineMCLE679FGHitachi.module_elasticite_k0,
				ResineMCLE679FGHitachi.module_elasticite_k1,
				ResineMCLE679FGHitachi.module_elasticite_k2
		};
		return super.calcul(liste, temperature);
	}

	public double coef_poisson(double temperature) {
		double liste[] = {
				ResineMCLE679FGHitachi.coefficient_poisson_k0
		};
		return super.calcul(liste, temperature);
	}

	public double coef_dil_xy(double temperature) {
		double liste[] = {
				ResineMCLE679FGHitachi.coefficient_dilatation_xy_k0,
				ResineMCLE679FGHitachi.coefficient_dilatation_xy_k1,
				ResineMCLE679FGHitachi.coefficient_dilatation_xy_k2
		};
		return super.calcul(liste, temperature);
	}

	public double coef_dil_z(double temperature) {
		double liste[] = {
				ResineMCLE679FGHitachi.coefficient_dilatation_z_k0,
				ResineMCLE679FGHitachi.coefficient_dilatation_z_k1,
				ResineMCLE679FGHitachi.coefficient_dilatation_z_k2
		};
		return super.calcul(liste, temperature);
	}

	public double amorti(double temperature) {
		double liste[] = {
				ResineMCLE679FGHitachi.amorti_k0,
				ResineMCLE679FGHitachi.amorti_k1,
				ResineMCLE679FGHitachi.amorti_k2,
				ResineMCLE679FGHitachi.amorti_k3,
				ResineMCLE679FGHitachi.amorti_k4
		};
		return super.calcul(liste, temperature);
	}

	public double coef_dil_x(double temperature, String tissus) {
		double k0 = 0;
		double k1 = 0;
		double k2 = 0;
		double k3 = 0;
		double k4 = 0;

		if (tissus.equals("1037")) {
			k2 = 1.036E-10;
			k1 = 2.883E-08;
			k0 = 1.756E-05;
		} else if (tissus.equals("106")) {
			k2 = 9.940E-11;
			k1 = 2.811E-08;
			k0 = 1.733E-05;
		} else if (tissus.equals("1078")) {
			k2 = 7.476E-11;
			k1 = 2.361E-08;
			k0 = 1.579E-05;
		} else if (tissus.equals("1080")) {
			k2 = 5.900E-11;
			k1 = 2.051E-08;
			k0 = 1.470E-05;
		} else if (tissus.equals("2113")) {
			k2 = 3.057E-11;
			k1 = 1.383E-08;
			k0 = 1.214E-05;
		} else if (tissus.equals("2116")) {
			k2 = 4.243E-11;
			k1 = 1.654E-08;
			k0 = 1.314E-05;
		} else if (tissus.equals("1506")) {
			k2 = 3.046E-11;
			k1 = 1.362E-08;
			k0 = 1.201E-05;
		} else if (tissus.equals("7628")) {
			k2 = 2.826E-11;
			k1 = 1.293E-08;
			k0 = 1.170E-05;
		}
		double liste[] = {
				k0, k1, k2, k3, k4
		};
		return super.calcul(liste, temperature);
	}

	public double coef_dil_y(double temperature, String tissus) {
		double k0 = 0;
		double k1 = 0;
		double k2 = 0;
		double k3 = 0;
		double k4 = 0;

		if (tissus.equals("1037")) {
			k2 = 1.036E-10;
			k1 = 2.895E-08;
			k0 = 1.767E-05;
		} else if (tissus.equals("106")) {
			k2 = 9.930E-11;
			k1 = 2.824E-08;
			k0 = 1.744E-05;
		} else if (tissus.equals("1078")) {
			k2 = 7.455E-11;
			k1 = 2.358E-08;
			k0 = 1.583E-05;
		} else if (tissus.equals("1080")) {
			k2 = 8.037E-11;
			k1 = 2.522E-08;
			k0 = 1.662E-05;
		} else if (tissus.equals("2113")) {
			k2 = 8.726E-11;
			k1 = 2.748E-08;
			k0 = 1.791E-05;
		} else if (tissus.equals("2116")) {
			k2 = 4.348E-11;
			k1 = 1.725E-08;
			k0 = 1.363E-05;
		} else if (tissus.equals("1506")) {
			k2 = 5.607E-11;
			k1 = 2.074E-08;
			k0 = 1.528E-05;
		} else if (tissus.equals("7628")) {
			k2 = 4.760E-11;
			k1 = 1.875E-08;
			k0 = 1.450E-05;
		}
		double liste[] = {
				k0, k1, k2, k3, k4
		};
		return super.calcul(liste, temperature);
	}

	public double module_tissus_x(double temperature, String tissus) {
		double k0 = 0;
		double k1 = 0;
		double k2 = 0;
		double k3 = 0;
		double k4 = 0;
		if (tissus.equals("1037")) {
			k2 = -0.0344;
			k1 = -15.798;
			k0 = 10849;
		} else if (tissus.equals("106")) {
			k2 = -0.0344;
			k1 = -15.951;
			k0 = 11053;
		} else if (tissus.equals("1078")) {
			k2 = -0.0347;
			k1 = -17.068;
			k0 = 12537;
		} else if (tissus.equals("1080")) {
			k2 = -0.0336;
			k1 = -17.651;
			k0 = 13635;
		} else if (tissus.equals("2113")) {
			k2 = -0.0311;
			k1 = -19.712;
			k0 = 17226;
		} else if (tissus.equals("2116")) {
			k2 = -0.0353;
			k1 = -19.681;
			k0 = 15998;
		} else if (tissus.equals("1506")) {
			k2 = -0.0328;
			k1 = -20.37;
			k0 = 17663;
		} else if (tissus.equals("7628")) {
			k2 = -0.0333;
			k1 = -20.939;
			k0 = 18321;
		}
		double liste[] = {
				k0, k1, k2, k3, k4
		};
		return super.calcul(liste, temperature);
	}

	public double module_tissus_y(double temperature, String tissus) {
		double k0 = 0;
		double k1 = 0;
		double k2 = 0;
		double k3 = 0;
		double k4 = 0;
		if (tissus.equals("1037")) {
			k2 = -0.0344;
			k1 = -15.798;
			k0 = 10849;
		} else if (tissus.equals("106")) {
			k2 = -0.0344;
			k1 = -15.951;
			k0 = 11053;
		} else if (tissus.equals("1078")) {
			k2 = -0.0347;
			k1 = -17.068;
			k0 = 12537;
		} else if (tissus.equals("1080")) {
			k2 = -0.0359;
			k1 = -17.166;
			k0 = 12332;
		} else if (tissus.equals("2113")) {
			k2 = -0.0347;
			k1 = -17.068;
			k0 = 12537;
		} else if (tissus.equals("2116")) {
			k2 = -0.0357;
			k1 = -19.597;
			k0 = 15752;
		} else if (tissus.equals("1506")) {
			k2 = -0.0385;
			k1 = -19.351;
			k0 = 14630;
		} else if (tissus.equals("7628")) {
			k2 = -0.0386;
			k1 = -20.046;
			k0 = 15573;
		}
		double liste[] = {
				k0, k1, k2, k3, k4
		};
		return super.calcul(liste, temperature);
	}

	public String toString() {
		return Ressources.RESINEMCLE679FGHITACHI;
	}

}
