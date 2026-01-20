package com.app.ancea;

import java.util.HashMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.ImageView;

public class LCC implements InterfaceComposantLeadless {

	public static final double beta = 7.3;
	public static final double vernis = 2.3;

	static ImageView image = new ImageView(
			LCC.class.getResource("images/LCCC.png").toExternalForm());

	private static double[] LCC3_x_0955 = {
			3.05, 2.54, 1.02, 0.955, 0.5, 0.76, 0.51, 0, 3, 0
	};
	private static double[] LCC4_x_127 = {
			5.59, 3.81, 2.032, 1.27, 0.635, 1.016, 0.889, 4, 0, 2
	};
	private static double[] LCC4_254 = {
			5, 3.2, 1.3, 2.54, 1.2, 1.0, 0.87, 4.0, 2.0, 0.0
	};
	private static double[] LCC6_x_127 = {
			6.223, 4.318, 2.032, 1.27, 0.635, 1.651, 0.889, 6.0, 0.0, 3.0
	};
	private static double[] LCC6_127 = {
			5, 3.2, 1.3, 1.27, 0.9, 1, 0.55, 6, 3, 0
	};
	private static double[] LCC16_127 = {
			7.239, 8.89, 1.54, 1.27, 0.635, 1.143, 1.016, 16, 4, 4
	};
	private static double[] LCC18_x_127 = {
			8.89, 7.239, 1.905, 1.27, 0.635, 1.143, 1.016, 18, 5, 4
	};
	private static double[] LCC20_127 = {
			8.89, 8.89, 1.905, 1.27, 0.635, 1.27, 1.016, 20, 5, 5
	};
	private static double[] LCC28_127 = {
			11.43, 11.43, 1.905, 1.27, 0.635, 1.27, 1.016, 28, 7, 7
	};
	private static double[] LCC32_127 = {
			13.97, 11.43, 1.75, 1.27, 0.632, 1.27, 1.016, 32, 9, 7
	};
	private static double[] LCC44_127 = {
			16.51, 16.51, 1.905, 1.27, 0.632, 1.27, 1.016, 44, 11, 11
	};
	private static double[] LCC68_127 = {
			24.13, 24.13, 2.032, 1.27, 0.889, 1.27, 1.016, 68, 17, 17
	};
	private static double[] LCC48_1 = {
			14.22, 14.22, 2.032, 1, 0.508, 1.016, 1.016, 48, 12, 12
	};
	private static double[] LCC40_1 = {
			12.19, 12.19, 2.032, 1, 0.508, 1.016, 1.016, 40, 12, 12
	};

	private HashMap<String, Double> format;

	/**
	 * @param nomFormat String correspondant au format de la LCC.
	 * @return Retourne une HashMap avec une LCC si null retounre une HashMap
	 *         vide.
	 */
	public static HashMap<String, Double> getLCCC(String nomFormat) {

		double[] dimensions;
		HashMap<String, Double> format = new HashMap<>();
		int i = 0;

		if (nomFormat.equals("LCC4_x_127")) {
			dimensions = LCC4_x_127;
		} else if (nomFormat.equals("LCC4_254")) {
			dimensions = LCC4_254;
		} else if (nomFormat.equals("LCC6_x_127")) {
			dimensions = LCC6_x_127;
		} else if (nomFormat.equals("LCC6_127")) {
			dimensions = LCC6_127;
		} else if (nomFormat.equals("LCC16_127")) {
			dimensions = LCC16_127;
		} else if (nomFormat.equals("LCC18_x_127")) {
			dimensions = LCC18_x_127;
		} else if (nomFormat.equals("LCC20_127")) {
			dimensions = LCC20_127;
		} else if (nomFormat.equals("LCC28_127")) {
			dimensions = LCC28_127;
		} else if (nomFormat.equals("LCC32_127")) {
			dimensions = LCC32_127;
		} else if (nomFormat.equals("LCC44_127")) {
			dimensions = LCC44_127;
		} else if (nomFormat.equals("LCC68_127")) {
			dimensions = LCC68_127;
		} else if (nomFormat.equals("LCC48_1")) {
			dimensions = LCC48_1;
		} else if (nomFormat.equals("LCC40_1")) {
			dimensions = LCC40_1;
		} else if (nomFormat.equals("LCC3_x_0955")) {
			dimensions = LCC3_x_0955;
		} else
			dimensions = null;

		if (dimensions != null) {
			for (String s : new String[] {
					"x", "y", "z", "pas", "a", "b", "h", "n", "nx", "ny"
			}) {
				format.put(s, dimensions[i++]);
			}
		}

		return format;
	}

	public static ObservableList<String> getLCC() {
		return FXCollections.observableArrayList(
				"LCC3_x_0955",
				"LCC4_x_127",
				"LCC4_254",
				"LCC6_x_127",
				"LCC6_127",
				"LCC16_127",
				"LCC18_x_127",
				"LCC20_127",
				"LCC28_127",
				"LCC32_127",
				"LCC44_127",
				"LCC68_127",
				"LCC48_1",
				"LCC40_1");
	}

	public static String[] getProperties(String nomFormat) {
		String[] str = new String[20];

		str[0] = "x";
		str[1] = String.valueOf(LCC.getLCCC(nomFormat).get("x"));
		str[2] = "y";
		str[3] = String.valueOf(LCC.getLCCC(nomFormat).get("y"));
		str[4] = "z";
		str[5] = String.valueOf(LCC.getLCCC(nomFormat).get("z"));
		str[6] = "pas";
		str[7] = String.valueOf(LCC.getLCCC(nomFormat).get("pas"));
		str[8] = "a";
		str[9] = String.valueOf(LCC.getLCCC(nomFormat).get("a"));
		str[10] = "b";
		str[11] = String.valueOf(LCC.getLCCC(nomFormat).get("b"));
		str[12] = "h";
		str[13] = String.valueOf(LCC.getLCCC(nomFormat).get("h"));
		str[14] = "n";
		str[15] = String.valueOf(LCC.getLCCC(nomFormat).get("n"));
		str[16] = "nx";
		str[17] = String.valueOf(LCC.getLCCC(nomFormat).get("nx"));
		str[18] = "ny";
		str[19] = String.valueOf(LCC.getLCCC(nomFormat).get("ny"));

		return str;
	}
}
