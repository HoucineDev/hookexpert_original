package com.app.ancea;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Represents resistance component formats with their physical dimensions and properties.
 * Base values: d, a, b, h
 * Calculated dynamically: masse, s, h1
 * Source: Résistances AncEA.xlsx
 *
 * @author Houcine
 */

public class Resistance {

	/* Ceramic LCCC - Resistance constants */
	public static final double EPAISSEUR = 0.5;
	public static final double BETA = 3.3;
	public static final double VERNIS = 2.3;

	// Material density - Ceramic
	private static final double CERAMIC_DENSITY = 3.8; // g/cm³

	// h1 calculation constants - Buoyancy criterion
	// Formula: h1 = 0.018 * LN(Cf) + 0.0799
	// Where Cf = masse / (2 * S² * ρ_SAC305 * 0.001)
	private static final double SAC305_DENSITY = 7.4; // g/cm³
	private static final double H1_COEFF_1 = 0.018;
	private static final double H1_COEFF_2 = 0.0799;

	/* Dimension array indices */
	private static final int IDX_D = 0;
	private static final int IDX_A = 1;
	private static final int IDX_B = 2;
	private static final int IDX_H = 3;

	/* Format definitions - immutable */
	/* Array format: {d, a, b, h} - masse, s, and h1 are calculated dynamically */
	private static final Map<String, double[]> FORMAT_DIMENSIONS;

	static {
		Map<String, double[]> formats = new HashMap<>();
		// Format: {d, a, b, h}
		// Note: masse, s, and h1 removed from hardcoded arrays - calculated dynamically

		formats.put("01005", new double[]{0.4, 0.2, 0.1, 0.13});
		formats.put("0201",  new double[]{0.6, 0.3, 0.15, 0.23});
		formats.put("0402",  new double[]{1.0, 0.5, 0.25, 0.35});
		formats.put("0603",  new double[]{1.6, 0.8, 0.3, 0.45});
		formats.put("0805",  new double[]{2.0, 1.25, 0.3, 0.50});
		formats.put("1206",  new double[]{3.2, 1.6, 0.4, 0.60});
		formats.put("1210",  new double[]{3.2, 2.6, 0.4, 0.60}); // Corrected: 2.6 instead of 2.5
		formats.put("2010",  new double[]{5.0, 2.5, 0.65, 0.60});
		formats.put("2512",  new double[]{6.3, 3.1, 0.65, 0.60});

		FORMAT_DIMENSIONS = Collections.unmodifiableMap(formats);
	}

	/* Cached format list for UI */
	private static final ObservableList<String> AVAILABLE_FORMATS =
			FXCollections.unmodifiableObservableList(
					FXCollections.observableArrayList(FORMAT_DIMENSIONS.keySet())
			);

	/* Instance fields */
	private final String nom;
	private final double[] dimensions;
	private Map<String, Double> properties;


	/**
	 * Constructor for Resistance component
	 * @param nom Format name (e.g., "0603")
	 */
	public Resistance(String nom) {
		this.nom = nom;
		this.dimensions = getDimension(nom);
		this.properties = calculateProperties(this.dimensions);
	}

	/**
	 * Legacy constructor - kept for compatibility
	 * @deprecated Use Resistance(String nom) instead
	 */
	@Deprecated
	public Resistance(String nom, double[] dimensions) {
		this.nom = nom;
		this.dimensions = dimensions != null ? dimensions.clone() : new double[4];
		this.properties = calculateProperties(this.dimensions);
	}

	/**
	 * Gets the properties map for this resistance instance
	 */
	public Map<String, Double> getProperties() {
		if (properties == null) {
			properties = calculateProperties(dimensions);
		}
		return Collections.unmodifiableMap(properties);
	}

	/**
	 * Gets dimensions for a specific format
	 * @param nomFormat Format name
	 * @return Array of dimensions (4 values: d, a, b, h), or empty array if format not found
	 */
	public static double[] getDimension(String nomFormat) {
		double[] dims = FORMAT_DIMENSIONS.get(nomFormat);
		return dims != null ? dims.clone() : new double[4];
	}

	/**
	 * Checks if a format exists
	 * @param nomFormat Format name
	 * @return true if format exists
	 */
	public static boolean isValidFormat(String nomFormat) {
		return FORMAT_DIMENSIONS.containsKey(nomFormat);
	}

	/**
	 * Calculates component mass
	 * Formula: masse = a × b × h × density / 1000
	 * Reference: Excel "Résistances AncEA.xlsx"
	 *
	 * @param a Width in mm
	 * @param b Pad width in mm
	 * @param h Height in mm
	 * @return mass in grams
	 */
	private static double calculateMasse(double a, double b, double h) {
		if (a <= 0 || b <= 0 || h <= 0) {
			return 0.0;
		}

		double masse = a * b * h * CERAMIC_DENSITY / 1000.0;

		System.out.println("[RÉSISTANCE] (in) a= " + a +", b=" + b + ", h="+ h + ", masse=" + masse);
		return masse;
	}

	/**
	 * Calculates metallization surface
	 * Formula: S = a × b
	 * Reference: Excel "Résistances AncEA.xlsx"
	 *
	 * @param a Width in mm
	 * @param b Pad width in mm
	 * @return surface in mm²
	 */
	private static double calculateS(double a, double b) {
		if (a <= 0 || b <= 0) {
			return 0.0;
		}
		// S métallisation = a × b
		double s = a * b;
		System.out.println("[RÉSISTANCE] (in) a= " + a +", b=" + b + ", S=" + s);
		
		return s;
	}

	/**
	 * Calculates h1 using buoyancy criterion formula
	 * Formula: h1 = 0.018 * LN(Cf) + 0.0799
	 * Where Cf = masse / (2 * S² * ρ_SAC305 * 0.001)
	 *
	 * Reference: Excel "Résistances AncEA.xlsx"
	 *
	 * @param masse Component mass in grams
	 * @param s Metallization surface in mm²
	 * @return h1 value in mm
	 */
//	private static double calculateH1(double masse, double s) {
//		if (s <= 0 || masse <= 0) {
//			return 0.0;
//		}
//
//		// Critère de flottabilité (buoyancy criterion)
//		// Cf = mc / (2 * Sm² * ρsac305 * 0.001)
//		double Cf = masse / (2.0 * Math.pow(s, 2) * SAC305_DENSITY * 0.001);
//		System.out.println("[RÉSISTANCE] Cf = " + Cf);
//
//		// H1 = 0.018 * Ln(Cf) + 0.0799
//		double h1 = H1_COEFF_1 * Math.log(Cf) + H1_COEFF_2;
//		System.out.println("[RÉSISTANCE] (in) h1= " + h1);
//		return h1;
//	}

	private static double calculateH1(double masse, double s, double a, double d) {
		if (s <= 0 || masse <= 0) return 0.0;

		// Calcul de base (modèle logarithmique)
		double Cf = masse / (2.0 * Math.pow(s, 2) * SAC305_DENSITY * 0.001);
		double h1 = H1_COEFF_1 * Math.log(Cf) + H1_COEFF_2;

		// Application du coefficient de réduction (0.9)
		// Règle 1: Strictement selon l'Excel (Ratio standard 2:1)
		boolean isStandardRatio = Math.abs(d - 2.0 * a) < 0.01;

		// Règle 2: Physique (Composant large / transverse)
		boolean isWideResistor = (a > d);

		if (isStandardRatio) { // || isWideResistor) {
			h1 = h1 * 0.9;
		}

		return h1;
	}

	/**
	 * Creates a properties map from dimension array
	 * All properties including masse, s, and h1 are calculated dynamically
	 *
	 * @param dimensions Array of dimension values [d, a, b, h]
	 * @return HashMap with calculated properties
	 */
	private static HashMap<String, Double> calculateProperties(double[] dimensions) {
		if (dimensions == null || dimensions.length < 4) {
			return new HashMap<>();
		}

		HashMap<String, Double> props = new HashMap<>(8);

		// Basic dimensions from array
		double d = dimensions[IDX_D];
		double a = dimensions[IDX_A];
		double b = dimensions[IDX_B];
		double h = dimensions[IDX_H];

		props.put("d", d);
		props.put("a", a);
		props.put("b", b);
		props.put("h", h);

		// Dynamically calculated properties
		double masse = calculateMasse(a, b, h);
		props.put("masse", masse);

		double s = calculateS(a, b);
		props.put("s", s);

		double h1 = calculateH1(masse, s, a, d);
		props.put("h1", h1);


		// Log the calculated properties
		System.out.println("========================================");
		System.out.println("  CALCULATED COMPONENT PROPERTIES");
		System.out.println("========================================");
		System.out.println(String.format("  d (outer dim)    : %8.4f mm", d));
		System.out.println(String.format("  a (width)        : %8.4f mm", a));
		System.out.println(String.format("  b (pad width)    : %8.4f mm", b));
		System.out.println(String.format("  h (height)       : %8.4f mm", h));
		System.out.println("----------------------------------------");
		System.out.println(String.format("  h1 (buoyancy)    : %8.4f mm", h1));
		System.out.println(String.format("  s (metal surf)   : %8.4f mm²", s));
		System.out.println(String.format("  masse            : %8.6f g", masse));
		System.out.println("========================================");

		return props;
	}

	/**
	 * Gets properties for a specific format (static method)
	 * masse, s, and h1 are calculated dynamically each time
	 *
	 * @param nomFormat Format name
	 * @return HashMap with properties, or empty map if format not found
	 */
	public static HashMap<String, Double> getResistance(String nomFormat) {
		System.out.println("[DureeDeVie] Getting Resistance for format: " + nomFormat);
		double[] dimensions = getDimension(nomFormat);
		return calculateProperties(dimensions);
	}

	/**
	 * Gets list of available resistance formats
	 * @return Immutable ObservableList of format names
	 */
	public static ObservableList<String> getResistances() {
		return AVAILABLE_FORMATS;
	}

	// Getters
	public String getNom() {
		return nom;
	}

	public double[] getDimensions() {
		return dimensions != null ? dimensions.clone() : new double[4];
	}

	public void setNom(String nom) {
		// Note: Deprecated - nom should be immutable
		// Consider removing this setter
	}

	public void setDimensions(double[] dimensions) {
		// Note: Deprecated - dimensions should be immutable
		// Consider removing this setter
	}

	@Override
	public String toString() {
		return nom;
	}

	/**
	 * Gets a specific property value
	 * @param propertyName Property key (d, a, b, h, masse, s, h1)
	 * @return Property value, or null if not found
	 */
	public Double getProperty(String propertyName) {
		return getProperties().get(propertyName);
	}
}
