package com.app.ancea;

/**
 * 
 * Cette classe contient les propriétés physiques de la résine 1755V Panasonic.
 *
 */
public class Resine1755VPanasonic extends ModelPhysiqueStatique {

	static final double masse_volumique_k0 = 1.42;

	static final double conductivite_thermique_k0 = 0.2;

	static final double module_elasticite_k0 = 3295; /* 4109.2 */
	static final double module_elasticite_k1 = -7.3128; /* -7.4793 */
	static final double module_elasticite_k2 = -0.00111; /* -0.00132 */

	static final double coefficient_poisson_k0 = 0.33;

	static final double coefficient_dilatation_xy_k0 = 4.39E-05;
	static final double coefficient_dilatation_xy_k1 = 9.00E-08;
	static final double coefficient_dilatation_xy_k2 = 5.00E-10; /* 3.00E-10 */

	static final double coefficient_dilatation_z_k0 = 5.25E-05;
	static final double coefficient_dilatation_z_k1 = 4.00E-08;
	static final double coefficient_dilatation_z_k2 = 6.00E-10;

	static final double amorti_k0 = 7.00E-03;
	static final double amorti_k1 = 6.00E-05;
	static final double amorti_k2 = 2.00E-06;
	static final double amorti_k3 = 3.00E-08;
	static final double amorti_k4 = 1.00E-10;

	public Resine1755VPanasonic() {
		super();
	}

	public double masse_volumique(double temperature) {
		double[] liste = {
				Resine1755VPanasonic.masse_volumique_k0
		};
		return super.calcul(liste, temperature);
	}

	public double conductibilite(double temperature) {
		double[] liste = {
				Resine1755VPanasonic.conductivite_thermique_k0
		};
		return super.calcul(liste, temperature);
	}

	public double module_elastique(double temperature) {
		double liste[] = {
				Resine1755VPanasonic.module_elasticite_k0,
				Resine1755VPanasonic.module_elasticite_k1,
				Resine1755VPanasonic.module_elasticite_k2
		};
		return super.calcul(liste, temperature);
	}

	public double coef_poisson(double temperature) {
		double liste[] = {
				Resine1755VPanasonic.coefficient_poisson_k0
		};
		return super.calcul(liste, temperature);
	}

	public double coef_dil_xy(double temperature) {
		double liste[] = {
				Resine1755VPanasonic.coefficient_dilatation_xy_k0,
				Resine1755VPanasonic.coefficient_dilatation_xy_k1,
				Resine1755VPanasonic.coefficient_dilatation_xy_k2
		};
		return super.calcul(liste, temperature);
	}

	public double coef_dil_z(double temperature) {
		double liste[] = {
				Resine1755VPanasonic.coefficient_dilatation_z_k0,
				Resine1755VPanasonic.coefficient_dilatation_z_k1,
				Resine1755VPanasonic.coefficient_dilatation_z_k2
		};
		return super.calcul(liste, temperature);
	}

	public double amorti(double temperature) {
		double liste[] = {
				Resine1755VPanasonic.amorti_k0, Resine1755VPanasonic.amorti_k1,
				Resine1755VPanasonic.amorti_k2, Resine1755VPanasonic.amorti_k3,
				Resine1755VPanasonic.amorti_k4
		};
		return super.calcul(liste, temperature);
	}

	public double coef_dil_x(double temperature, String tissus) {
		double k0 = 0;
		double k1 = 0;
		double k2 = 0;
		double k3 = 0;
		double k4 = 0;
        switch (tissus) {
            case "1037" -> {
                k2 = 1.086E-10;
                k1 = 2.131E-08;
                k0 = 1.869E-05;
            }
            case "106" -> {
                k2 = 1.049E-10;
                k1 = 2.058E-08;
                k0 = 1.836E-05;
            }
            case "1078" -> {
                k2 = 8.357E-11;
                k1 = 1.629E-08;
                k0 = 1.631E-05;
            }
            case "1080" -> {
                k2 = 7.025E-11;
                k1 = 1.358E-08;
                k0 = 1.493E-05;
            }
            case "2113" -> {
                k2 = 4.451E-11;
                k1 = 8.366E-09;
                k0 = 1.193E-05;
            }
            case "2116" -> {
                k2 = 5.430E-11;
                k1 = 1.037E-08;
                k0 = 1.308E-05;
            }
            case "1506" -> {
                k2 = 4.363E-11;
                k1 = 8.208E-09;
                k0 = 1.179E-05;
            }
            case "7628" -> {
                k2 = 4.112E-11;
                k1 = 7.715E-09;
                k0 = 1.146E-05;
            }
        }
		double[] liste = {
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
			k2 = 1.088E-10;
			k1 = 2.134E-08;
			k0 = 1.877E-05;
		} else if (tissus.equals("106")) {
			k2 = 1.051E-10;
			k1 = 2.061E-08;
			k0 = 1.844E-05;
		} else if (tissus.equals("1078")) {
			k2 = 8.353E-11;
			k1 = 1.627E-08;
			k0 = 1.634E-05;
		} else if (tissus.equals("1080")) {
			k2 = 8.941E-11;
			k1 = 1.740E-08;
			k0 = 1.713E-05;
		} else if (tissus.equals("2113")) {
			k2 = 9.661E-11;
			k1 = 1.872E-08;
			k0 = 1.831E-05;
		} else if (tissus.equals("2116")) {
			k2 = 5.655E-11;
			k1 = 1.075E-08;
			k0 = 1.349E-05;
		} else if (tissus.equals("1506")) {
			k2 = 6.914E-11;
			k1 = 1.320E-08;
			k0 = 1.520E-05;
		} else if (tissus.equals("7628")) {
			k2 = 6.143E-11;
			k1 = 1.163E-08;
			k0 = 1.429E-05;
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
			k2 = -0.0016;
			k1 = -10.765;
			k0 = 7881;
		} else if (tissus.equals("106")) {
			k2 = -0.0016;
			k1 = -10.914;
			k0 = 8071;
		} else if (tissus.equals("1078")) {
			k2 = -0.0018;
			k1 = -11.981;
			k0 = 9463;
		} else if (tissus.equals("1080")) {
			k2 = -0.0019;
			k1 = -12.669;
			k0 = 10567;
		} else if (tissus.equals("2113")) {
			k2 = -0.0021;
			k1 = -15.009;
			k0 = 14170;
		} else if (tissus.equals("2116")) {
			k2 = -0.0022;
			k1 = -14.515;
			k0 = 12758;
		} else if (tissus.equals("1506")) {
			k2 = -0.0023;
			k1 = -15.485;
			k0 = 14515;
		} else if (tissus.equals("7628")) {
			k2 = -0.0023;
			k1 = -16.015;
			k0 = 15137;
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
			k2 = -0.0016;
			k1 = -14.513;
			k0 = 7881;
		} else if (tissus.equals("106")) {
			k2 = -0.0016;
			k1 = -13.821;
			k0 = 8071;
		} else if (tissus.equals("1078")) {
			k2 = -0.0018;
			k1 = -14.379;
			k0 = 9463;
		} else if (tissus.equals("1080")) {
			k2 = -0.0018;
			k1 = -12.421;
			k0 = 9198;
		} else if (tissus.equals("2113")) {
			k2 = -0.002;
			k1 = -11.942;
			k0 = 9183;
		} else if (tissus.equals("2116")) {
			k2 = -0.0022;
			k1 = -11.981;
			k0 = 12492;
		} else if (tissus.equals("1506")) {
			k2 = -0.0022;
			k1 = -10.914;
			k0 = 11244;
		} else if (tissus.equals("7628")) {
			k2 = -0.0023;
			k1 = -10.769;
			k0 = 12152;
		}
		double liste[] = {
				k0, k1, k2, k3, k4
		};
		return super.calcul(liste, temperature);
	}

	public String toString() {
		return Ressources.RESINE1755VPANASONIC;
	}

}
