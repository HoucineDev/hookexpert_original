package com.app.ancea;

import java.util.HashMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.ImageView;

/**
 *
 * Composant qui utilise du Silicum, de la résine d'encapsulation ainsi que du
 * cuivre C19400
 *
 */
public class QFN implements InterfaceComposantLeadless {

	static ImageView image = new ImageView(
			QFN.class.getResource("images/QFN.png").toExternalForm());

	/* Coefficients composant QFN */

	public static final double beta = 4.3;
	public static final double vernis = 1.4;

	/* tableaux datasheet qfn cotations[i] = QFN_XX_XX[i] */

	// public static final String[] cotations = new String[] {
	// "Pas \"P\" (mm)", "Nombre de terminaisons x", "Nombre de terminaisons y", "a
	// (mm)", "b (mm)", "n", "Boîtier x (mm)", "Boîtier y (mm)", "e_boîtier (mmm)",
	// "Pad central x(mm)", "Pad central y (mm)", "e_leadframe (mm)", "Puce x (mm)",
	// "Puce y (mm)",
	// "e_puce (mm)"
	// };

	public static final String[] cotations = new String[] {
			"n", "x", "y", "z", "pas", "x1", "y1", "a", "b", "h", "x2", "y2",
			"z2", "x3", "z3"
	};

	private static final double[] QFN_12_05 = {
			12, 3, 3, 0.9, 0.5, 1.2, 1.2, 0.23, 0.55, 0.2, 1.0, 1.0, 0.2
	};
	private static final double[] QFN_16_05 = {
			16, 3, 3, 0.9, 0.5, 1.55, 1.55, 0.23, 0.4, 0.2, 1.30, 1.30, 0.2
	};
	private static final double[] QFN_24_05 = {
			24, 4, 4, 0.9, 0.5, 2.7, 2.7, 0.25, 0.4, 0.2, 2.26, 2.26, 0.2
	};
	private static final double[] QFN_16_065 = {
			16, 4, 4, 0.9, 0.65, 2.4, 2.4, 0.33, 0.6, 0.2, 2.01, 2.01, 0.2
	};
	private static final double[] QFN_24_065 = {
			24, 5, 5, 0.9, 0.65, 3.25, 3.25, 0.3, 0.4, 0.2, 2.72, 2.72, 0.2
	};
	private static final double[] QFN_24_08 = {
			24, 6, 6, 0.9, 0.8, 4, 4, 0.23, 0.4, 0.2, 3.35, 3.35, 0.2
	};
	private static final double[] QFN_56_04 = {
			56, 7, 7, 0.9, 0.4, 5.6, 6.6, 0.2, 0.4, 0.2, 4.69, 5.52, 0.2
	};
	private static final double[] QFN_48 = {
			48, 7, 7, 0.9, 0.5, 5.6, 5.6, 0.25, 0.4, 0.2, 4.69, 4.69, 0.2
	};
	private static final double[] QFN_52_05 = {
			52, 8, 8, 0.9, 0.5, 6.2, 6.2, 0.25, 0.5, 0.2, 5.19, 5.19, 0.2
	};
	private static final double[] QFN_44_065 = {
			44, 8, 8, 0.9, 0.69, 6.8, 6.8, 0.325, 0.4, 0.2, 5.69, 5.69, 0.2
	};
	private static final double[] QFN_56_05 = {
			54, 8, 8, 0.9, 0.65, 6.8, 6.8, 0.325, 0.4, 0.2, 5.44, 5.44, 0.2
	};
	private static final double[] QFN_64_05_VT2 = {
			64, 8.6, 8.6, 0.9, 0.5, 7.3, 7.3, 0.23, 0.4, 0.2, 6.11, 6.11, 0.2
	};
	private static final double[] QFN_68_05_VT5 = {
			68, 10, 10, 0.9, 0.5, 4.3, 4.3, 0.23, 0.6, 0.2, 3.60, 3.60, 0.2
	};
	private static final double[] QFN_68_05_COSAC = {
			68, 10, 10, 0.9, 0.5, 4.6, 4.6, 0.23, 0.4, 0.2, 3.85, 3.85, 0.2
	};
	private static final double[] QFN_68_05_THALES = {
			68, 10, 10, 0.85, 0.5, 8, 8, 0.23, 0.6, 0.2, 6.7, 6.7, 0.2
	};
	private static final double[] QFN_72_05 = {
			72, 10, 10, 0.9, 0.5, 6, 6, 0.25, 0.4, 0.2, 5.02, 5.02, 0.2
	};
	private static final double[] QFN_100_04 = {
			100, 12, 12, 0.9, 0.5, 10, 10, 0.25, 0.5, 0.2, 8.37, 8.37, 0.2
	};

	/**
	 * @param nomFormat String correspondant au format de la QFN choisit.
	 * @return une HashMap des caractéristique de la QFN si null retourne une
	 *         HashMap vide.
	 */
	public static HashMap<String, Double> getComposant(String nomFormat) {

		double[] dimensions;
		double calcul = 0;
		HashMap<String, Double> format = new HashMap<>();
		int i = 0;

		if (nomFormat.equals("QFN_12_05")) {
			dimensions = QFN_12_05;
		} else if (nomFormat.equals("QFN_16_05")) {
			dimensions = QFN_16_05;
		} else if (nomFormat.equals("QFN_24_08")) {
			dimensions = QFN_24_08;
		} else if (nomFormat.equals("QFN_24_05")) {
			dimensions = QFN_24_05;
		} else if (nomFormat.equals("QFN_16_065")) {
			dimensions = QFN_16_065;
		} else if (nomFormat.equals("QFN_24_065")) {
			dimensions = QFN_24_065;
		} else if (nomFormat.equals("QFN_56_04")) {
			dimensions = QFN_56_04;
		} else if (nomFormat.equals("QFN_48")) {
			dimensions = QFN_48;
		} else if (nomFormat.equals("QFN_52_05")) {
			dimensions = QFN_52_05;
		} else if (nomFormat.equals("QFN_44_065")) {
			dimensions = QFN_44_065;
		} else if (nomFormat.equals("QFN_56_05")) {
			dimensions = QFN_56_05;
		} else if (nomFormat.equals("QFN_64_05_VT2")) {
			dimensions = QFN_64_05_VT2;
		} else if (nomFormat.equals("QFN_68_05_VT5")) {
			dimensions = QFN_68_05_VT5;
		} else if (nomFormat.equals("QFN_68_05_COSAC")) {
			dimensions = QFN_68_05_COSAC;
		} else if (nomFormat.equals("QFN_68_05_THALES")) {
			dimensions = QFN_68_05_THALES;
		} else if (nomFormat.equals("QFN_72_05")) {
			dimensions = QFN_72_05;
		} else if (nomFormat.equals("QFN_72_05")) {
			dimensions = QFN_72_05;
		} else if (nomFormat.equals("QFN_100_04")) {
			dimensions = QFN_100_04;
		} else {
			dimensions = new double[0];
		}

		/*
		 * Les dimensions x3 y3 sont des calculs déduit de la feuille excel.
		 * Elles correspondent à la résine
		 */

		for (String s : cotations) {
			if (s.equals("x3")) {
				calcul = format.get("x") - (format.get("a") + format.get("x2"));
				format.put(s, Double
						.valueOf(Outils.StringWithCommaToDouble(Outils.nombreChiffreScientifiqueApresVirgule(calcul))));
			} else if (s.equals("z3")) {
				calcul = format.get("z") - (format.get("h") + format.get("z2"));
				format.put(s, calcul);
			} else {
				format.put(s, dimensions[i++]);
			}
		}

		double nx_ny = format.get("n") / 4;

		format.put("nx", nx_ny);
		format.put("ny", nx_ny);

		return format;
	}

	public static ObservableList<String> getComposants() {
		return FXCollections.observableArrayList(
				"QFN_12_05",
				"QFN_16_05",
				"QFN_24_05",
				"QFN_16_065",
				"QFN_24_065",
				"QFN_24_08",
				"QFN_56_04",
				"QFN_48",
				"QFN_52_05",
				"QFN_44_065",
				"QFN_56_05",
				"QFN_64_05_VT2",
				"QFN_68_05_VT5",
				"QFN_68_05_THALES",
				"QFN_68_05_COSAC",
				"QFN_72_05",
				"QFN_100_04");
	}

}
