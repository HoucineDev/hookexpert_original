package com.app.ancea;

import java.util.HashMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Capacite implements InterfaceComposantLeadless {

	// Ces variables dépendent du matériau utilisé: X7R ou COG
	public static double masse_volumique;
	public static double conductivite_thermique;
	public static double module_elasticite;
	public static double coefficient_poisson;
	public static double valeur_capacite;
	public static String formatBoitier;
	public static final double beta = 3.3;
	public static final double vernis = 2.3;

	// Type de matériau utilisé pour la Capacité
	public static boolean x7r_utilise = false;
	public static boolean cog_utilise = false;

	/* Tableaux représentants les dimensions des capacités */

	private static final double[] FORMAT_01005 = {
			0.4, 0.2, 0.1, 0.20, 5.20E-05, 2.00E-02, 4.20E-02
	};
	private static final double[] FORMAT_0201 = {
			0.6, 0.3, 0.15, 0.30, 1.76E-04, 4.50E-02, 4.05E-02
	};
	private static final double[] FORMAT_0402 = {
			1, 0.5, 0.25, 0.35, 5.69E-04, 1.25E-01, 3.97E-02
	};
	private static final double[] FORMAT_0603 = {
			1.6, 0.8, 0.3, 0.60, 2.50E-03, 2.40E-01, 3.30E-02
	};
	private static final double[] FORMAT_0805 = {
			2, 1.25, 0.3, 0.80, 6.50E-03, 3.75E-01, 2.49E-02
	};
	private static final double[] FORMAT_1206 = {
			3.2, 1.6, 0.4, 1.00, 1.66E-02, 6.40E-01, 1.49E-02
	};
	private static final double[] FORMAT_1210 = {
			3.2, 2.6, 0.4, 1.20, 3.24E-02, 1.04E+00, 8.88E-03
	};
	private static final double[] FORMAT_1812 = {
			4.5, 3.2, 0.6, 1.40, 6.55E-02, 1.92E+00, 5.49E-03
	};
	private static final double[] FORMAT_2010 = {
			5, 2.5, 0.65, 1.50, 6.09E-02, 1.63E+00, 1.59E-03
	};
	private static final double[] FORMAT_2512 = {
			6.3, 3.1, 0.65, 1.50, 9.52E-02, 2.015E+00, 9.7013E-03
	};

	public Capacite(
			String materiau_utilise,
			double valeurCapacite,
			String formatUtilise) {

		Capacite.formatBoitier = formatUtilise;
		Capacite.valeur_capacite = valeurCapacite;
		if (materiau_utilise.equals("X7R")) {

			Capacite.masse_volumique = X7R.masseVolumique;
			Capacite.module_elasticite = X7R.moduleElasticite;
			Capacite.coefficient_poisson = X7R.coefficientPoisson;
			Capacite.x7r_utilise = true;
			Capacite.cog_utilise = false;

		} else if (materiau_utilise.equals("COG")) {

			Capacite.masse_volumique = COG.masseVolumique;
			Capacite.module_elasticite = COG.moduleElasticite;
			Capacite.coefficient_poisson = COG.coefficientPoisson;
			Capacite.x7r_utilise = false;
			Capacite.cog_utilise = true;
		} else {
			System.out.println("Wrong type received !");
		}

	}

	/**
	 * 
	 * @param nomFormat Le format dont on souhaite construire la HashMap.
	 *                  Exemple: "01005".
	 * @return Une HashMap où les clés sont les caractéristiques de la capacité
	 *         ("d", "a", "b", "h", "masse", "s", "h1"); et où les valeurs
	 *         associées aux clés sont des résultats dependant du format de
	 *         capacité choisi.
	 */
	public static HashMap<String, Double> getCapacite(String nomFormat) {
		double[] dimensions;

		HashMap<String, Double> format = new HashMap<>();
		int i = 0;

		double calcul = 0;

		if (nomFormat.equals("01005"))
			dimensions = FORMAT_01005;
		else if (nomFormat.equals("0201"))
			dimensions = FORMAT_0201;
		else if (nomFormat.equals("0402"))
			dimensions = FORMAT_0402;
		else if (nomFormat.equals("0603"))
			dimensions = FORMAT_0603;
		else if (nomFormat.equals("0805"))
			dimensions = FORMAT_0805;
		else if (nomFormat.equals("1206"))
			dimensions = FORMAT_1206;
		else if (nomFormat.equals("1210"))
			dimensions = FORMAT_1210;
		else if (nomFormat.equals("1812"))
			dimensions = FORMAT_1812;
		else if (nomFormat.equals("2010"))
			dimensions = FORMAT_2010;
		else if (nomFormat.equals("2512"))
			dimensions = FORMAT_2512;
		else
			dimensions = new double[FORMAT_01005.length];

		for (String s : new String[] {
				"d", "a", "b", "h", "masse", "s", "h1"
		}) {
			if (s.equals("h1")) {
				// calcul = -2.3154 * Math.pow(format.get("masse"), 2)
				// / (2 * format.get("s")) + 0.04;
				calcul = 0.05 - format.get("masse") / (2 * format.get("s"));
				format.put(s, calcul);
			} else {
				format.put(s, dimensions[i++]);
			}

		}

		return format;
	}

	/**
	 * 
	 * @return Une liste contenant les différents formats existants des
	 *         Capacités. Note: On utilise ici une "ObservableList" et non un
	 *         array car on a besoin de mettre des "Listeners d'évènements"
	 *         dessus.
	 */
	public static ObservableList<String> getCapacites() {
		ObservableList<String> capacites = FXCollections.observableArrayList(
				"01005",
				"0201",
				"0402",
				"0603",
				"0805",
				"1206",
				"1210",
				"1812",
				"2010",
				"2512");
		return capacites;
	}
}
