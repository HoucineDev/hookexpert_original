package com.app.ancea;

/**
 * 
 * Cette classe contient les propriétés physiques de la résine 700G Hitachi.
 *
 */
public class Resine700GHitachi extends ModelPhysiqueStatique {

	static final double masse_volumique_k0 = 1.52;

	static final double conductivite_thermique_k0 = 0.2;

	static final double module_elasticite_k0 = 12000;
	static final double module_elasticite_k1 = -15.8;
	static final double module_elasticite_k2 = -0.0501;

	static final double coefficient_poisson_k0 = 0.33;

	static final double coefficient_dilatation_xy_k0 = 1.93E-05;
	static final double coefficient_dilatation_xy_k1 = 3.00E-08;
	static final double coefficient_dilatation_xy_k2 = 1.00E-10;

	static final double coefficient_dilatation_z_k0 = 0;
	static final double coefficient_dilatation_z_k1 = 0;
	static final double coefficient_dilatation_z_k2 = 0;

	static final double amorti_k0 = -0.0118;
	static final double amorti_k1 = 2.00E-05;
	static final double amorti_k2 = 4.00E-07;
	static final double amorti_k3 = 7.00E-10;
	static final double amorti_k4 = 1.00E-11;

	public Resine700GHitachi() {
		super();
	}

	public double masse_volumique(double temperature) {
		double liste[] = {
				Resine700GHitachi.masse_volumique_k0
		};
		return super.calcul(liste, temperature);
	}

	public double conductibilite(double temperature) {
		double liste[] = {
				Resine700GHitachi.conductivite_thermique_k0
		};
		return super.calcul(liste, temperature);
	}

	public double module_elastique(double temperature) {
		double liste[] = {
				Resine700GHitachi.module_elasticite_k0,
				Resine700GHitachi.module_elasticite_k1,
				Resine700GHitachi.module_elasticite_k2
		};
		return super.calcul(liste, temperature);
	}

	public double coef_poisson(double temperature) {
		double liste[] = {
				Resine700GHitachi.coefficient_poisson_k0
		};
		return super.calcul(liste, temperature);
	}

	public double coef_dil_xy(double temperature) {
		double liste[] = {
				Resine700GHitachi.coefficient_dilatation_xy_k0,
				Resine700GHitachi.coefficient_dilatation_xy_k1,
				Resine700GHitachi.coefficient_dilatation_xy_k2
		};
		return super.calcul(liste, temperature);
	}

	public double coef_dil_z(double temperature) {
		double liste[] = {
				Resine700GHitachi.coefficient_dilatation_z_k0,
				Resine700GHitachi.coefficient_dilatation_z_k1,
				Resine700GHitachi.coefficient_dilatation_z_k2
		};
		return super.calcul(liste, temperature);
	}

	public double amorti(double temperature) {
		double liste[] = {
				Resine700GHitachi.amorti_k0, Resine700GHitachi.amorti_k1,
				Resine700GHitachi.amorti_k2, Resine700GHitachi.amorti_k3,
				Resine700GHitachi.amorti_k4
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
			k2 = 3.668E-11;
			k1 = 1.636E-08;
			k0 = 1.358E-05;
		} else if (tissus.equals("106")) {
			k2 = 3.567E-11;
			k1 = 1.611E-08;
			k0 = 1.347E-05;
		} else if (tissus.equals("1078")) {
			k2 = 2.926E-11;
			k1 = 1.443E-08;
			k0 = 1.272E-05;
		} else if (tissus.equals("1080")) {
			k2 = 2.445E-11;
			k1 = 1.323E-08;
			k0 = 1.220E-05;
		} else if (tissus.equals("2113")) {
			k2 = 1.403E-11;
			k1 = 1.014E-08;
			k0 = 1.078E-05;
		} else if (tissus.equals("2116")) {
			k2 = 1.901E-11;
			k1 = 1.128E-08;
			k0 = 1.125E-05;
		} else if (tissus.equals("1506")) {
			k2 = 1.416E-11;
			k1 = 9.914E-09;
			k0 = 1.064E-05;
		} else if (tissus.equals("7628")) {
			k2 = 1.331E-11;
			k1 = 9.494E-09;
			k0 = 1.042E-05;
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
			k2 = 3.676E-11;
			k2 = 1.647E-08;
			k0 = 1.368E-05;
		} else if (tissus.equals("106")) {
			k2 = 3.575E-11;
			k1 = 1.623E-08;
			k0 = 1.357E-05;
		} else if (tissus.equals("1078")) {
			k2 = 2.915E-11;
			k1 = 1.442E-08;
			k0 = 1.278E-05;
		} else if (tissus.equals("1080")) {
			k2 = 3.131E-11;
			k1 = 1.529E-08;
			k0 = 1.333E-05;
		} else if (tissus.equals("2113")) {
			k2 = 3.446E-11;
			k1 = 1.654E-08;
			k0 = 1.429E-05;
		} else if (tissus.equals("2116")) {
			k2 = 1.950E-11;
			k1 = 1.181E-08;
			k0 = 1.169E-05;
		} else if (tissus.equals("1506")) {
			k2 = 2.444E-11;
			k1 = 1.364E-08;
			k0 = 1.282E-05;
		} else if (tissus.equals("7628")) {
			k2 = 2.143E-11;
			k1 = 1.270E-08;
			k0 = 1.237E-05;
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
			k2 = -0.0489;
			k1 = -19.014;
			k0 = 16655;
		} else if (tissus.equals("106")) {
			k2 = -0.0489;
			k1 = -19.15;
			k0 = 16848;
		} else if (tissus.equals("1078")) {
			k2 = -0.0486;
			k1 = -20.15;
			k0 = 18262;
		} else if (tissus.equals("1080")) {
			k2 = -0.0473;
			k1 = -20.63;
			k0 = 19149;
		} else if (tissus.equals("2113")) {
			k2 = -0.0435;
			k1 = -22.37;
			k0 = 22162;
		} else if (tissus.equals("2116")) {
			k2 = -0.0479;
			k1 = -22.481;
			k0 = 21574;
		} else if (tissus.equals("1506")) {
			k2 = -0.045;
			k1 = -23.015;
			k0 = 22802;
		} else if (tissus.equals("7628")) {
			k2 = -0.0452;
			k1 = -23.535;
			k0 = 23484;
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
			k2 = -0.0489;
			k1 = -19.014;
			k0 = 16655;
		} else if (tissus.equals("106")) {
			k2 = -0.0489;
			k1 = -19.15;
			k0 = 16848;
		} else if (tissus.equals("1078")) {
			k2 = -0.0486;
			k1 = -20.15;
			k0 = 18262;
		} else if (tissus.equals("1080")) {
			k2 = -0.0498;
			k1 = -20.279;
			k0 = 18231;
		} else if (tissus.equals("2113")) {
			k2 = -0.0532;
			k1 = -21.177;
			k0 = 18909;
		} else if (tissus.equals("2116")) {
			k2 = -0.0484;
			k1 = -22.42;
			k0 = 21403;
		} else if (tissus.equals("1506")) {
			k2 = -0.0514;
			k1 = -22.275;
			k0 = 20712;
		} else if (tissus.equals("7628")) {
			k2 = -0.0511;
			k1 = -22.885;
			k0 = 21603;
		}
		double liste[] = {
				k0, k1, k2, k3, k4
		};
		return super.calcul(liste, temperature);
	}

	public String toString() {
		return Ressources.RESINE700GHITACHI;
	}

}
