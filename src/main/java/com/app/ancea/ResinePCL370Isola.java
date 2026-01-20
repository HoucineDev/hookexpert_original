package com.app.ancea;

import java.util.Map;
import java.util.HashMap;

/**
 * Cette classe contient les propriétés physiques de la résine PCL370 Isola.
 * Optimisée pour réduire la duplication de code et améliorer la maintenabilité.
 */
public class ResinePCL370Isola extends ModelPhysiqueStatique {

	// Constantes de base pour les propriétés simples
	private static final double[] MASSE_VOLUMIQUE = {1.49};
	private static final double[] CONDUCTIVITE_THERMIQUE = {0.2};
	private static final double CAPACITE_THERMIQUE_MASSIQUE = 1170;
	private static final double[] MODULE_ELASTICITE = {3110, -6.01, -7.2E-03};
	private static final double[] COEFFICIENT_POISSON = {0.33};
	private static final double[] COEFFICIENT_DILATATION_XY = {4.21E-05, 7.00E-08, 4.00E-10};
	private static final double[] COEFFICIENT_DILATATION_Z = {6.34E05, 8.00E08, 2.00E-10};
	private static final double[] AMORTI = {3.46E-02, -1.00E-04, 1.00E-06, 6.00E-10, -1.00E-11};

	// Structure pour les propriétés dépendantes du tissu
	private static class TissueProperties {
		final double k0, k1, k2, k3, k4;

		TissueProperties(double k0, double k1, double k2, double k3, double k4) {
			this.k0 = k0; this.k1 = k1; this.k2 = k2; this.k3 = k3; this.k4 = k4;
		}

		TissueProperties(double k0, double k1, double k2) {
			this(k0, k1, k2, 0, 0);
		}

		double[] toArray() {
			return new double[]{k0, k1, k2, k3, k4};
		}
	}

	// Maps pour les propriétés de dilatation par tissu
	private static final Map<String, TissueProperties> COEF_DIL_X = Map.of(
			"1037", new TissueProperties(1.775E-05, 1.506E-08, 6.541E-11),
			"106",  new TissueProperties(1.724E-05, 1.453E-08, 6.280E-11),
			"1078", new TissueProperties(1.548E-05, 1.142E-08, 4.801E-11),
			"1080", new TissueProperties(1.416E-05, 9.483E-09, 3.907E-11),
			"2113", new TissueProperties(1.124E-05, 7.211E-09, 2.917E-11),
			"2116", new TissueProperties(1.134E-05, 5.785E-09, 2.295E-11),
			"1506", new TissueProperties(1.123E-05, 5.671E-09, 2.258E-11),
			"7628", new TissueProperties(1.093E-05, 5.332E-09, 2.116E-11)
	);

	private static final Map<String, TissueProperties> COEF_DIL_Y = Map.of(
			"1037", new TissueProperties(1.762E-05, 1.508E-08, 6.541E-11),
			"106",  new TissueProperties(1.751E-05, 1.454E-08, 6.280E-11),
			"1078", new TissueProperties(1.551E-05, 1.140E-08, 4.794E-11),
			"1080", new TissueProperties(1.627E-05, 1.221E-08, 5.147E-11),
			"2113", new TissueProperties(1.741E-05, 1.317E-08, 5.548E-11),
			"2116", new TissueProperties(1.284E-05, 7.486E-09, 3.020E-11),
			"1506", new TissueProperties(1.445E-05, 9.200E-09, 3.764E-11),
			"7628", new TissueProperties(1.360E-05, 8.094E-09, 3.282E-11)
	);

	private static final Map<String, TissueProperties> MODULE_TISSUS_X = Map.of(
			"1037", new TissueProperties(7879, -9.617, -0.0077),
			"106",  new TissueProperties(8075, -9.7632, -0.0077),
			"1078", new TissueProperties(9506, -10.86, -0.0079),
			"1080", new TissueProperties(10652, -11.61, -0.0078),
			"2113", new TissueProperties(14365, -14.119, -0.0076),
			"2116", new TissueProperties(12870, -13.445, -0.0083),
			"1506", new TissueProperties(14690, -14.541, -0.008),
			"7628", new TissueProperties(15314, -15.059, -0.0081)
	);

	private static final Map<String, TissueProperties> MODULE_TISSUS_Y = Map.of(
			"1037", new TissueProperties(7879, -9.6137, -0.0077),
			"106",  new TissueProperties(8075, -9.7632, -0.0077),
			"1078", new TissueProperties(9506, -10.86, -0.0079),
			"1080", new TissueProperties(9222, -10.774, -0.0081),
			"2113", new TissueProperties(9172, -11.13, -0.0089),
			"2116", new TissueProperties(12594, -13.288, -0.0084),
			"1506", new TissueProperties(11290, -12.609, -0.0089),
			"7628", new TissueProperties(12216, -13.315, -0.009)
	);

	public ResinePCL370Isola() {
		super();
	}

	// Propriétés de base simplifiées
	public double masse_volumique(double temperature) {
		return super.calcul(MASSE_VOLUMIQUE, temperature);
	}

	public double conductibilite(double temperature) {
		return super.calcul(CONDUCTIVITE_THERMIQUE, temperature);
	}

	public double module_elastique(double temperature) {
		return super.calcul(MODULE_ELASTICITE, temperature);
	}

	public double coef_poisson(double temperature) {
		return super.calcul(COEFFICIENT_POISSON, temperature);
	}

	public double coef_dil_xy(double temperature) {
		return super.calcul(COEFFICIENT_DILATATION_XY, temperature);
	}

	public double coef_dil_z(double temperature) {
		return super.calcul(COEFFICIENT_DILATATION_Z, temperature);
	}

	public double amorti(double temperature) {
		return super.calcul(AMORTI, temperature);
	}

	// Méthode générique pour les propriétés dépendantes du tissu
	private double calculerProprieteeTissu(double temperature, String tissus,
										   Map<String, TissueProperties> proprietes,
										   String nomPropriete) {
		TissueProperties props = proprietes.get(tissus);
		if (props == null) {
			throw new IllegalArgumentException("Tissu inconnu pour " + nomPropriete + ": " + tissus);
		}
		return super.calcul(props.toArray(), temperature);
	}

	public double coef_dil_x(double temperature, String tissus) {
		return calculerProprieteeTissu(temperature, tissus, COEF_DIL_X, "coefficient de dilatation X");
	}

	public double coef_dil_y(double temperature, String tissus) {
		return calculerProprieteeTissu(temperature, tissus, COEF_DIL_Y, "coefficient de dilatation Y");
	}

	public double module_tissus_x(double temperature, String tissus) {
		return calculerProprieteeTissu(temperature, tissus, MODULE_TISSUS_X, "module tissu X");
	}

	public double module_tissus_y(double temperature, String tissus) {
		return calculerProprieteeTissu(temperature, tissus, MODULE_TISSUS_Y, "module tissu Y");
	}

	// Méthodes utilitaires pour obtenir les tissus supportés
	public static String[] getTissusDisponibles() {
		return COEF_DIL_X.keySet().toArray(new String[0]);
	}

	public static boolean isTissuSupporte(String tissus) {
		return COEF_DIL_X.containsKey(tissus);
	}

	@Override
	public String toString() {
		return Ressources.RESINEPCL370ISOLA;
	}
}