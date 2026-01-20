package com.app.ancea;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;

/**
 * 
 * Cette classe regroupe des outils utiles des formules mathématique, d'outils
 * de recherche de fichiers ou de modules de sauvegarde.
 *
 */
public class Outils {

	public static String doubleToString(
			double d,
			int nombreChiffreApresVirgule) {
		DecimalFormat format = new DecimalFormat("0.0");
		format.setMaximumFractionDigits(nombreChiffreApresVirgule);
		format.setRoundingMode(RoundingMode.FLOOR);

		String s = format.format(d);
		return s;
	}

	/**
	 * @param content Chaine de caractère à sauvegarder
	 * @param file    nom du fichier dans lequel il y aura content
	 */
	public static void sauvegarder_objet(String content, File file) {
		try {
			PrintWriter writer;
			writer = new PrintWriter(file);
			writer.println(content);
			writer.close();
		} catch (IOException ex) {
			Logger.getLogger(Outils.class.getName())
					.log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * @param n Nombre de composant qu'on veut partitionner.
	 * @return retourne un double correspondant à la largeur que doit avoir
	 *         chaque composant.
	 */
	public static double getWidth(int n) {

		Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

		return (primaryScreenBounds.getWidth() / n) - 40;
	}

	/**
	 * @param nombre nombre scientifique à parser
	 * @return un string de l'écriture scientifque parsée
	 */
	public static String nombreChiffreScientifiqueApresVirgule(double nombre) {
		NumberFormat formatter = new DecimalFormat("0.###E0");
		return formatter.format(nombre);
	}

	/**
	 * @param nom nom d'affichage du titledPane
	 * @return retourne un titledPane
	 */
	public static TitledPane generer_titledPane(String nom) {
		TitledPane tp = new TitledPane();
		tp = new TitledPane();
		tp.setText(nom);
		tp.setCollapsible(false);
		return tp;
	}

	/**
	 * @param d double à parser
	 * @return retourne un double avec 2 chiffres après la virgule.
	 */
	public static double formattedDouble(double d) {
		DecimalFormat df = new DecimalFormat("#.##");
		String dx = df.format(d);
		return Double.valueOf(dx);
	}

	/**
	 * @return Génère une gridpane stylisée qui aere les composants entre eux.
	 */
	public static GridPane generer_gridPane() {
		GridPane gp = new GridPane();
		gp.setPadding(new Insets(20, 20, 20, 10));
		gp.setVgap(10);
		gp.setHgap(10);

		return gp;
	}

	private static Label label(String nomLabel) {
		Label l = new Label(nomLabel);
		l.setFont(Font.font("Arial", 12));
		return l;
	}

	/**
	 * @param tf       TextField qui sera associer avec le label
	 * @param nomLabel String qui sera le nom du label
	 * @return Retourne un GridPane avec un label nomLabel et un textfield tf
	 */
	public static GridPane generer_textfield_avec_label(
			Node tf,
			String nomLabel) {
		GridPane gp = new GridPane();
		gp.add(label(nomLabel), 0, 0);
		gp.add(tf, 0, 1);
		return gp;
	}

	/**
	 * @param nomLabel Texte à afficher pour le label.
	 * @return retourne un label stylisé avec nomLabel indiqué.
	 */
	protected static Label generer_label(String nomLabel) {
		Label label = new Label(nomLabel);
		label.setFont(Font.font("Arial", FontWeight.BOLD, 18));

		return label;
	}

	/**
	 * Génère un textfield stylisé et l'ajoute à une ArrayList d'Object (type
	 * Node).
	 * 
	 * @param prompText text inscrit dans le Textfield indiquant quelle valeur
	 *                  écrire.
	 * @return retourne un TextField avec un style appliqué.
	 */
	public static TextField generer_textfield(String prompText) {
		TextField tf = new TextField();
		tf.setPromptText(prompText);
		tf.setPrefColumnCount(10);
		tf.setFont(new Font("SanSerif", 15));
		tf.getStyleClass().add("field-background");
		return tf;
	}

	/**
	 * @param d                         double à arrondir
	 * @param nombreChiffreApresVirgule int designant le nombrede chiffre que
	 *                                  l'on veut après la virgule
	 * @return retourne le double d arrondit à nombreChiffreApresVirgule
	 *         decimales.
	 * 
	 */
	public static double nombreChiffreApresVirgule(
			double d,
			int nombreChiffreApresVirgule) {
		int dix = 10;

		for (int i = 0; i < nombreChiffreApresVirgule - 1; i++) {
			dix *= 10;
		}

        return Math.floor(d * dix) / dix;

	}

	/**
	 * @param d double que l'on veut parser
	 * @return retourne d avec un chiffre après la virgule
	 */
	public static double doubleUnChiffreApresVirgule(double d) {
		DecimalFormat df = new DecimalFormat("#.#");
		String tmp = df.format(d).replace(',', '.');
		return Double.valueOf(tmp);
	}

//	public static double StringWithCommaToDouble(String stringToFormat) {
//		NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
//		Number number;
//		double d = Double.NaN;
//		try {
//			number = format.parse(stringToFormat.replace('.', ','));
//			d = number.doubleValue();
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		return d;
//	}

//	public static double StringWithCommaToDouble(String stringToFormat) {
//		try {
//			// Add debugging for JAR mode
//			// System.out.println("🔧 Converting string: '" + stringToFormat + "'");
//
//			if (stringToFormat == null || stringToFormat.trim().isEmpty()) {
//				System.out.println("🔧 Empty string, returning 0.0");
//				return 0.0;
//			}
//
//			// Handle scientific notation directly if present
//			if (stringToFormat.contains("E") || stringToFormat.contains("e")) {
//				try {
//					double result = Double.parseDouble(stringToFormat);
//					//System.out.println("🔧 Scientific notation parsed: " + result);
//					return result;
//				} catch (NumberFormatException e) {
//					System.out.println("🔧 Scientific notation parsing failed: " + e.getMessage());
//				}
//			}
//
//			// Original logic with enhanced error handling
//			NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
//			Number number = format.parse(stringToFormat.replace('.', ','));
//			double result = number.doubleValue();
//
//			//System.out.println("🔧 Converted result: " + result);
//			return result;
//
//		} catch (ParseException e) {
//			System.err.println("🔧 Parse error for '" + stringToFormat + "': " + e.getMessage());
//			e.printStackTrace();
//
//			// Fallback: try direct double parsing
//			try {
//				double fallback = Double.parseDouble(stringToFormat.replace(',', '.'));
//				System.out.println("🔧 Fallback parsing successful: " + fallback);
//				return fallback;
//			} catch (NumberFormatException nfe) {
//				System.err.println("🔧 Fallback parsing also failed: " + nfe.getMessage());
//				return 0.0; // Return 0.0 instead of NaN
//			}
//		}
//	}

	public static double StringWithCommaToDouble(String stringToFormat) {
		if (stringToFormat == null || stringToFormat.trim().isEmpty()) {
			return 0.0;
		}

		try {
			// Try direct parsing first (most reliable)
			return Double.parseDouble(stringToFormat.replace(',', '.'));
		} catch (NumberFormatException e1) {
			try {
				// Try locale-specific parsing
				NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
				return format.parse(stringToFormat).doubleValue();
			} catch (ParseException e2) {
				System.err.println("Failed to parse: " + stringToFormat);
				return 0.0;
			}
		}
	}

	/**
	 * @param path chemin du dossier
	 * @return retourne une liste des fichiers dans le dossier path.
	 */
	public static ArrayList<String> get_list_filename_with_extension(
			String path) {
		ArrayList<String> list_filename = new ArrayList<String>();

		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();

		if (listOfFiles != null) {

			for (int i = 0; i < listOfFiles.length; i++) {
				if (listOfFiles[i].isFile()) {
					list_filename.add(listOfFiles[i].getName());
				}
			}
			return list_filename;
		}
		return list_filename;
	}

	/**
	 * @param min nombre minimim
	 * @param max nombre max
	 * @return retourne un chiffre entre min et max
	 */
	public static int getRandomNumberInRange(int min, int max) {

		if (min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}

		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}

	/**
	 * @param path      chemin du dossier
	 * @param extension l'extension des noms des fichiers que l'on souhaite
	 * @return retourne une liste des fichiers dans le dossier path.
	 */
	public static ArrayList<String> get_list_filename_with_extension(
			String path,
			String extension) {
		ArrayList<String> list_filename = new ArrayList<String>();

		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();

		if (listOfFiles != null) {

			for (int i = 0; i < listOfFiles.length; i++) {
				if (listOfFiles[i].isFile()
						&& listOfFiles[i].getName().contains(extension)) {
					list_filename.add(listOfFiles[i].getName());
				}
			}
			return list_filename;
		}
		return list_filename;
	}

	/**
	 * @param path       Chemin du répértoire
	 * @param inFileName pattern que l'on souhaite trouver dans le nom du
	 *                   fichier. Si null, prend tous les noms du dossier.
	 * @return Retourne une ArrayList de string étant le nom de fichier avec le
	 *         pattern inFileName
	 */
	public static ArrayList<String> get_list_filename_directory(
			String path,
			String inFileName) {
		ArrayList<String> list_filename = new ArrayList<String>();

		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();

		if (listOfFiles != null) {

			for (int i = 0; i < listOfFiles.length; i++) {
				if (listOfFiles[i].isFile()) {
					if (inFileName != null) {
						if (listOfFiles[i].getName().contains(inFileName)) {
							list_filename.add(
									listOfFiles[i].getName()
											.split(inFileName)[0]);
						}
					} else {
						list_filename.add(listOfFiles[i].getName());
					}
				}
			}
			return list_filename;
		}
		return null;
	}

	/**
	 * This function computes the absolute difference (the gap) between two
	 * Doubles. If d1 = -5 and d2 = 3 it returns 8.
	 * 
	 * @param d1 A Double
	 * @param d2 Another Double
	 *
	 * @return The absolute difference between the two parameters
	 * 
	 */
	public static Double absolute_difference(Double d1, Double d2) {
		double result;

		if (d1 < 0 && d2 > 0) {

			result = Math.abs(d1) + d2;

		} else if (d1 < 0 && d2 < 0) {

			result = Math.abs(d1 - d2);

		} else if (d1 > 0 && d2 < 0) {

			result = d1 + Math.abs(d2);

		} else {

			result = Math.abs(d1 - d2);

		}
		return result;
	}

	/**
	 * A function which tells if the number is in [low;high].
	 * 
	 * @param low    minimum border
	 * @param high   maximum border
	 * @param number the number you want to test
	 * @return true if low <= number <= high
	 */
	public static boolean inRange(int low, int high, int number) {
		if ((number >= low) && (number <= high)) {
			return true;
		}
		return false;
	}

	/**
	 * A function which tells if the number is in [low;high].
	 * 
	 * @param low    minimum border
	 * @param high   maximum border
	 * @param number the number you want to test
	 * @return true if low <= number <= high
	 */
	public static boolean inRange(double low, double high, double number) {
		if ((number >= low) && (number <= high)) {
			return true;
		}
		return false;
	}

	/**
	 * A function which tells if the number is in ]low;high[.
	 * 
	 * @param low    minimum border
	 * @param high   maximum border
	 * @param number the number you want to test
	 * @return true if low <= number <= high
	 */
	public static boolean inRangeExclusive(
			double low,
			double high,
			double number) {
		if ((number > low) && (number < high)) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * A function which returns the sum of values of the array. Example :
	 * arraySum( [1 ; 2] ) = 3
	 * 
	 * @param array An array of Integer
	 * @return The sum of the number in the array
	 */
	public static Integer arraySum(Integer array[]) {
		Integer sum = 0;

		for (Integer number : array) {
			if (number != null) {
				sum += number;
			}
		}

		return sum;
	}

	/**
	 * 
	 * A function which returns the sum of values of the array. Example1 :
	 * arraySum( [1.0 ; 2.0] ) = 3.0 / Example2: arraySum( [-1.0 ; 2.0] ) = 1.0
	 * 
	 * @param array An array of Double
	 * @return The sum of the number in the array
	 */
	public static Double arraySum(Double array[]) {
		Double sum = 0d;

		for (int i = 0; i < array.length; i++) {
			if (array[i] != null) {

				sum += array[i];
			}

		}

		return sum;
	}

	/**
	 * A function which returns the maximum of an array of positive Doubles.
	 * This array can contain null values.
	 * 
	 * @param array An array of Double
	 * @return The maximum of the values
	 */
	public static Double arrayMax(Double array[]) {
		Double max = 0d;
		for (int i = 0; i < array.length; i++) {
			if (array[i] != null) {
				if (array[i] > max) {
					max = array[i];
				}
			}
		}
		return max;

	}

	/**
	 * 
	 * A function which returns the sum of absolute values of the array. Example
	 * : arrayAbsoluteSum( [-1.0 ; 2.0] ) = 3.0
	 * 
	 * @param array An array of Double
	 * @return The sum of the number in the array
	 */
	public static Double arrayAbsoluteSum(Double array[]) {
		Double sum = 0d;

		for (int i = 0; i < array.length; i++) {
			if (array[i] != null) {

				sum += Math.abs(array[i]);
			}

		}

		return sum;
	}

	/**
	 * A function which returns the average of the positive values of the array.
	 * 
	 * @param array An array of Doubles
	 * @return the average of the values compute only when values are > 0.
	 */
	public static Double arrayPositiveAverage(Double array[]) {
		int count = 0;
		Double average = 0d;

		for (Double value : array) {
			if (value > 0) {
				average += value;
				count++;
			}
		}

		return average / count;
	}

	/**
	 * Pretty print the matrix and handle null values.
	 * 
	 * @param matrix A matrix of Doubles
	 */
	public static void prettyPrintMatrix(Double matrix[][]) {

		for (Double[] row : matrix) {
			for (Double value : row) {
				if (value != null) {
					if (value == 0d) {
						System.out.print(" 0");
					} else if (value == value.intValue()) {
						System.out.print(" " + value.intValue());
					} else {
						System.out.print(" " + value);
					}
				} else {
					System.out.print(" " + ".");
				}
			}
			System.out.print("\n\n");
		}
	}

	/**
	 * Pretty print the matrix and handle null values.
	 * 
	 * @param matrix A matrix of Integers
	 */
	public static void prettyPrintMatrix(Integer matrix[][]) {

		for (Integer[] row : matrix) {
			for (Integer value : row) {
				if (value != null) {
					System.out.print(" " + value);
				} else {
					System.out.print(" " + ".");
				}
			}
			System.out.print("\n\n");
		}
	}

	/**
	 * Générère graphiquement une matrice de billes où l'utilisateur selectionne
	 * les billes présentes dans le composant déjà enregistré. Uniquement
	 * valable pour les Composants A billes.
	 * 
	 * @param nombre_billes_x
	 * @param nombre_billes_y
	 */
	public static GridPane generer_matrice_creuse(
			Composant ComposantABilles,
			GridPane matrix,
			int nombre_billes_x,
			int nombre_billes_y) {

		for (int i = 0; i < nombre_billes_y + 2; i++) {
			for (int j = 0; j < nombre_billes_x + 2; j++) {

				Button b = new Button();

				b.setMinWidth(26);
				b.setMaxWidth(26);
				b.setMinHeight(26);
				b.setMinHeight(26);

				// La valeur 0 correspond aux axes de la matrice
				if (Outils.inRange(1, nombre_billes_y, i)
						&& Outils.inRange(1, nombre_billes_x, j)) {

					if (((ComposantBilles) ComposantABilles).matriceBilles[i
							- 1][j - 1]) {
						b.setText("●");
						b.setStyle("-fx-border-color: black;");
					} else {
						b.setText(" ");
						b.setStyle("-fx-border-color: red;");
					}
					b.setOnMouseClicked(e -> {
						if (e.getButton() == MouseButton.SECONDARY) {
							for (Node node : matrix.getChildren()) {
								if ((GridPane.getRowIndex(node) == GridPane
										.getRowIndex(b))
										&& Outils.inRange(
												GridPane.getColumnIndex(b),
												GridPane.getColumnIndex(b) + 5,
												GridPane.getColumnIndex(node))
										&& (node instanceof Button)) {

									((Button) node).setText(" ");
									((Button) node)
											.setStyle("-fx-border-color: red;");
								}
							}
						} else {
							if (b.getText().equals("●")) {
								b.setText(" ");
								b.setStyle("-fx-border-color: red;");

							} else {
								b.setText("●");
								b.setStyle("-fx-border-color: black;");
							}
						}
					});

					matrix.add(b, j, i);

				} else if (i == 0 || i == nombre_billes_y + 1) {
					if (j != 0 && j != nombre_billes_x + 1) {
						Label lab_j = new Label(String.valueOf(j));
						lab_j.setStyle(
								"-fx-font-weight: bold;-fx-text-fill:red;");
						lab_j.setMinWidth(15);
						lab_j.setAlignment(Pos.CENTER);
						lab_j.setOnMouseClicked(e -> {

							for (Node node : matrix.getChildren()) {

								if (GridPane.getColumnIndex(node)
										.equals(GridPane.getColumnIndex(lab_j))
										&& node instanceof Button) {
									((Button) node).setText(" ");
									((Button) node)
											.setStyle("-fx-border-color: red;");
								}
							}

						});

						matrix.add(lab_j, j, i);
						GridPane.setHalignment(
								lab_j,
								javafx.geometry.HPos.CENTER);
					}
				} else if (j == 0 || j == nombre_billes_x + 1) {
					if (i != 0 && i != nombre_billes_y + 1) {
						Label lab_i = new Label(String.valueOf(i));
						lab_i.setStyle(
								"-fx-font-weight: bold;-fx-text-fill:red;");
						lab_i.setOnMouseClicked(e -> {

							for (Node node : matrix.getChildren()) {
								if (GridPane.getRowIndex(node)
										.equals(GridPane.getRowIndex(lab_i))
										&& node instanceof Button) {
									((Button) node).setText(" ");
									((Button) node)
											.setStyle("-fx-border-color: red;");
								}
							}

						});

						matrix.add(lab_i, j, i);
						GridPane.setHalignment(
								lab_i,
								javafx.geometry.HPos.CENTER);
					}
				}
			}
		}
		return matrix;
	}

	/**
	 * Générère graphiquement une matrice de billes où l'utilisateur selectionne
	 * les billes présentes dans le composant pas encore enregistré
	 * 
	 * @param nombre_billes_x
	 * @param nombre_billes_y
	 */
	public static GridPane generer_matrice_Bille(
			GridPane matrix,
			int nombre_billes_x,
			int nombre_billes_y) {

		for (int i = 0; i < nombre_billes_y + 2; i++) {
			for (int j = 0; j < nombre_billes_x + 2; j++) {

				Button b = new Button();

				b.setMinWidth(26);
				b.setMaxWidth(26);
				b.setMinHeight(26);
				b.setMinHeight(26);
				b.setText("●");
				b.setStyle("-fx-border-color: black;");

				// La valeur 0 correspond aux axes de la matrice
				if (Outils.inRange(1, nombre_billes_y, i)
						&& Outils.inRange(1, nombre_billes_x, j)) {

					b.setOnMouseClicked(e -> {
						if (e.getButton() == MouseButton.SECONDARY) {
							for (Node node : matrix.getChildren()) {
								if ((GridPane.getRowIndex(node) == GridPane
										.getRowIndex(b))
										&& Outils.inRange(
												GridPane.getColumnIndex(b),
												GridPane.getColumnIndex(b) + 5,
												GridPane.getColumnIndex(node))
										&& (node instanceof Button)) {

									((Button) node).setText(" ");
									((Button) node)
											.setStyle("-fx-border-color: red;");
								}
							}
						} else {
							if (b.getText().equals("●")) {
								b.setText(" ");
								b.setStyle("-fx-border-color: red;");

							} else {
								b.setText("●");
								b.setStyle("-fx-border-color: black;");
							}
						}
					});

					matrix.add(b, j, i);

				} else if (i == 0 || i == nombre_billes_y + 1) {
					if (j != 0 && j != nombre_billes_x + 1) {
						Label lab_j = new Label(String.valueOf(j));
						lab_j.setStyle(
								"-fx-font-weight: bold;-fx-text-fill:red;");
						lab_j.setMinWidth(15);
						lab_j.setAlignment(Pos.CENTER);
						lab_j.setOnMouseClicked(e -> {

							for (Node node : matrix.getChildren()) {

								if (GridPane.getColumnIndex(node)
										.equals(GridPane.getColumnIndex(lab_j))
										&& node instanceof Button) {
									((Button) node).setText(" ");
									((Button) node)
											.setStyle("-fx-border-color: red;");
								}
							}

						});

						matrix.add(lab_j, j, i);
						GridPane.setHalignment(
								lab_j,
								javafx.geometry.HPos.CENTER);
					}
				} else if (j == 0 || j == nombre_billes_x + 1) {
					if (i != 0 && i != nombre_billes_y + 1) {
						Label lab_i = new Label(String.valueOf(i));
						lab_i.setStyle(
								"-fx-font-weight: bold;-fx-text-fill:red;");
						lab_i.setOnMouseClicked(e -> {

							for (Node node : matrix.getChildren()) {
								if (GridPane.getRowIndex(node)
										.equals(GridPane.getRowIndex(lab_i))
										&& node instanceof Button) {
									((Button) node).setText(" ");
									((Button) node)
											.setStyle("-fx-border-color: red;");
								}
							}

						});

						matrix.add(lab_i, j, i);
						GridPane.setHalignment(
								lab_i,
								javafx.geometry.HPos.CENTER);
					}
				}
			}
		}
		return matrix;
	}

	public static void traitement_matrice_lcc(
			GridPane matrix,
			int nombre_billes_x,
			int nombre_billes_y) {

		for (Node node : matrix.getChildren()) {
			if (node instanceof Button) {

				boolean auBord = GridPane.getRowIndex(node) == 1
						|| GridPane.getColumnIndex(node) == 1
						|| GridPane.getRowIndex(node) == nombre_billes_y
						|| GridPane.getColumnIndex(node) == nombre_billes_x;

				boolean dansLesCoins = (GridPane.getRowIndex(node) == 1
						&& GridPane.getColumnIndex(node) == 1)
						|| (GridPane.getRowIndex(node) == 1 && GridPane
								.getColumnIndex(node) == nombre_billes_x)
						|| (GridPane.getColumnIndex(node) == 1 && GridPane
								.getRowIndex(node) == nombre_billes_y)
						|| (GridPane.getColumnIndex(node) == nombre_billes_x
								&& GridPane
										.getRowIndex(node) == nombre_billes_y);

				if ((auBord && dansLesCoins) || (!auBord && !dansLesCoins)) {
					((Button) node).setText(" ");
					((Button) node).setStyle("-fx-border-color: red;");
				}

			}
		}

	}

	/**
	 * Permet d'envoyer une alerte à l'utilisateur lorsqu'une erreur est
	 * rencontrée.
	 * 
	 * @param typeErreur  Le type d'erreur (juste ERROR pour le moment)
	 * @param titreErreur Le titre de l'erreur
	 * @param message     Le message que l'on souhaite afficher
	 */
	public static void alerteUtilisateur(
			String typeErreur,
			String titreErreur,
			String message) {

		if (typeErreur.equals("ERROR")) {

			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle(titreErreur);
			alert.setHeaderText(null);
			alert.setContentText(message);
			alert.showAndWait();
		}
	}

	/**
	 * This function can only handle positives values.This function can handle
	 * null values.
	 * 
	 * @param matrix A maxtrix of Double
	 * @return The maximum of the matrix
	 */
	public static double arrayMaxMatrix(Double matrix[][]) {
		double max = 0;

		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				if (matrix[i][j] != null) {
					if (matrix[i][j] > max) {
						max = matrix[i][j];
					}
				}
			}
		}
		return max;
	}

	/**
	 * Générère graphiquement une matrice de billes où l'utilisateur selectionne
	 * les billes présentes dans le composant déjà enregistré. Uniquement
	 * valable pour les Composants lcc.
	 * 
	 * @param nombre_billes_x
	 * @param nombre_billes_y
	 */
	public static GridPane generer_matrice_creuse_lcc(
			Composant ComposantLcc,
			GridPane matrix,
			int nombre_billes_x,
			int nombre_billes_y) {

		for (int i = 0; i < nombre_billes_y + 2; i++) {
			for (int j = 0; j < nombre_billes_x + 2; j++) {

				Button b = new Button();

				b.setMinWidth(26);
				b.setMaxWidth(26);
				b.setMinHeight(26);
				b.setMinHeight(26);

				// La valeur 0 correspond aux axes de la matrice
				if (Outils.inRange(1, nombre_billes_y, i)
						&& Outils.inRange(1, nombre_billes_x, j)) {

					if (((ComposantLCC) ComposantLcc).matriceDeBilles[i - 1][j
							- 1]) {
						b.setText("●");
						b.setStyle("-fx-border-color: black;");
					} else {
						b.setText(" ");
						b.setStyle("-fx-border-color: red;");
					}
					b.setOnMouseClicked(e -> {
						if (e.getButton() == MouseButton.SECONDARY) {
							for (Node node : matrix.getChildren()) {
								if ((GridPane.getRowIndex(node) == GridPane
										.getRowIndex(b))
										&& Outils.inRange(
												GridPane.getColumnIndex(b),
												GridPane.getColumnIndex(b) + 5,
												GridPane.getColumnIndex(node))
										&& (node instanceof Button)) {

									((Button) node).setText(" ");
									((Button) node)
											.setStyle("-fx-border-color: red;");
								}
							}
						} else {
							if (b.getText().equals("●")) {
								b.setText(" ");
								b.setStyle("-fx-border-color: red;");

							} else {
								b.setText("●");
								b.setStyle("-fx-border-color: black;");
							}
						}
					});

					matrix.add(b, j, i);

				} else if (i == 0 || i == nombre_billes_y + 1) {
					if (j != 0 && j != nombre_billes_x + 1) {
						Label lab_j = new Label(String.valueOf(j));
						lab_j.setStyle(
								"-fx-font-weight: bold;-fx-text-fill:red;");
						lab_j.setMinWidth(15);
						lab_j.setAlignment(Pos.CENTER);
						lab_j.setOnMouseClicked(e -> {

							for (Node node : matrix.getChildren()) {

								if (GridPane.getColumnIndex(node)
										.equals(GridPane.getColumnIndex(lab_j))
										&& node instanceof Button) {
									((Button) node).setText(" ");
									((Button) node)
											.setStyle("-fx-border-color: red;");
								}
							}

						});

						matrix.add(lab_j, j, i);
						GridPane.setHalignment(
								lab_j,
								javafx.geometry.HPos.CENTER);
					}
				} else if (j == 0 || j == nombre_billes_x + 1) {
					if (i != 0 && i != nombre_billes_y + 1) {
						Label lab_i = new Label(String.valueOf(i));
						lab_i.setStyle(
								"-fx-font-weight: bold;-fx-text-fill:red;");
						lab_i.setOnMouseClicked(e -> {

							for (Node node : matrix.getChildren()) {
								if (GridPane.getRowIndex(node)
										.equals(GridPane.getRowIndex(lab_i))
										&& node instanceof Button) {
									((Button) node).setText(" ");
									((Button) node)
											.setStyle("-fx-border-color: red;");
								}
							}

						});

						matrix.add(lab_i, j, i);
						GridPane.setHalignment(
								lab_i,
								javafx.geometry.HPos.CENTER);
					}
				}
			}
		}
		return matrix;
	}

}
