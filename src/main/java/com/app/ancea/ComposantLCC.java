package com.app.ancea;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;

public class ComposantLCC extends ComposantLeadless {

	static ImageView image = new ImageView(
			Composant_Resistance.class.getResource("images/LCCC.png")
					.toExternalForm());

	private ObservableList<Resultat_temperature_LCCC> paliers_temperatures;
	private Propriete pas, x, y, z, nombre_ex1, nombre_ey1, nombre_nix,
			nombre_niy, nombre_billes_x, nombre_billes_y;

	private Propriete a, b, h;

	public ObservableList<Propriete> proprietes;

	public boolean matriceDeBilles[][];

	public ComposantLCC(
			String nom,
			double pas,
			double x,
			double y,
			double z,
			int nombre_billes_x,
			int nombre_billes_y) {

		super(nom);

		this.pas = new Propriete("Pas", pas);
		this.x = new Propriete("X", x);
		this.y = new Propriete("Y", y);
		this.z = new Propriete("Z", z);
		this.nombre_billes_x = new Propriete(
				"Nombre Billes x",
				nombre_billes_x);
		this.nombre_billes_y = new Propriete(
				"Nombre Billes x",
				nombre_billes_y);

		this.nombre_ex1 = new Propriete("Nombre ex1:", getNombre_ex1());
		this.nombre_ex1 = new Propriete("Nombre ey1:", getNombre_ey1());

		this.nombre_nix = new Propriete("Nombre nix:", getNombre_nix());
		this.nombre_niy = new Propriete("Nombre niy:", getNombre_niy());


		Propriete blank = new Propriete("");
		blank.set_resultat(" ");

		this.proprietes = FXCollections.observableArrayList(
				this.pas,
				blank,
				this.x,
				this.y,
				this.z,
				blank,
				this.nombre_ex1,
				this.nombre_ey1,
				blank,
				this.nombre_nix,
				this.nombre_niy,
				this.nombre_billes_x,
				this.nombre_billes_y);

		paliers_temperatures = FXCollections.observableArrayList();
		for (int d : Ressources.tableau_temperatures) {
			paliers_temperatures.add(new Resultat_temperature_LCCC(this, d));
		}
	}

	public ComposantLCC(String nom, String path) {

		super(nom);

		this.pas = new Propriete("Pas", 0.0);
		this.x = new Propriete("X", 0.0);
		this.y = new Propriete("Y", 0.0);
		this.z = new Propriete("Z", 0.0);
		this.nombre_ex1 = new Propriete("Nombre ex1", 0.0);
		this.nombre_ey1 = new Propriete("Nombre ey1", 0.0);
		this.nombre_nix = new Propriete("Nombre nix", 0.0);
		this.nombre_niy = new Propriete("Nombre niy", 0.0);
		this.nombre_billes_x = new Propriete("Nombre Billes x", 0);
		this.nombre_billes_y = new Propriete("Nombre Billes x", 0);

		Propriete blank = new Propriete("");
		blank.set_resultat(" ");

		this.proprietes = FXCollections.observableArrayList(
				this.pas,
				blank,
				this.x,
				this.y,
				this.z,
				blank,
				this.nombre_ex1,
				this.nombre_ey1,
				blank,
				this.nombre_nix,
				this.nombre_niy,
				blank,
				this.nombre_billes_x,
				this.nombre_billes_y);

		paliers_temperatures = FXCollections.observableArrayList();
		for (int d : Ressources.tableau_temperatures) {
			paliers_temperatures.add(new Resultat_temperature_LCCC(this, d));
		}

		// chargement d'un LCC
		try {
			FileReader fr = new FileReader(path);
			BufferedReader br = new BufferedReader(fr);

			ArrayList<String> als = new ArrayList<String>();
			ArrayList<String> als2 = new ArrayList<String>();
			String line = br.readLine();

			while ((line != null) && (!line.equals("Matrice Billes:"))) {
				als.add(line);
				line = br.readLine();

			}
			ArrayList<Double> array = new ArrayList<Double>();

			String name = als.get(0);
			composant = LCC.getLCCC(name);

			// Lecture matrice de billes
			while (line != null) {
				als2.add(line);
				line = br.readLine();

			}
			this.matriceDeBilles = new boolean[(int) this
					.getNombre_niy()][(int) this.getNombre_nix()];

			for (int i = 0; i < this.getNombre_niy(); i++) {
				String[] tabs2 = als2.get(i + 1).split(";");
				for (int j = 0; j < this.getNombre_nix(); j++) {
					this.matriceDeBilles[i][j] = Boolean.valueOf(tabs2[j]);
				}
			}

			this.pas.set_resultat(composant.get("pas"));
			this.x.set_resultat(composant.get("x"));
			this.y.set_resultat(composant.get("y"));
			this.z.set_resultat(composant.get("z"));
			this.nombre_ex1.set_resultat(getNombre_ex1());
			this.nombre_ey1.set_resultat(getNombre_ey1());
			this.nombre_nix.set_resultat(getNombre_nix());
			this.nombre_niy.set_resultat(getNombre_niy());
			this.nombre_billes_x.set_resultat(composant.get("nx"));
			this.nombre_billes_y.set_resultat(composant.get("ny"));

		} catch (

		FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	public ComposantLCC() {
		super();

		setProprietes();

		paliers_temperatures = FXCollections.observableArrayList();
		for (int d : Ressources.tableau_temperatures) {
			paliers_temperatures.add(new Resultat_temperature_LCCC(this, d));
		}
	}

	public ComposantLCC(String nom) {
		super(nom);
		
		composant = LCC.getLCCC(nom);

		setProprietes();

		paliers_temperatures = FXCollections.observableArrayList();
		for (int d : Ressources.tableau_temperatures) {
			paliers_temperatures.add(new Resultat_temperature_LCCC(this, d));
		}
	}

	public TableView<Propriete> get_tableView_proprietes() {

		TableView<Propriete> tv = new TableView<Propriete>();

		TableColumn<
				Propriete,
				String> colonneVariable = new TableColumn("Proprietes");

		colonneVariable.setMinWidth(200);
		colonneVariable.setStyle("-fx-alignment: CENTER;");
		colonneVariable
				.setCellValueFactory(c -> c.getValue().get_nom_variable());

		TableColumn<
				Propriete,
				String> colonneResultat = new TableColumn("Resultats");
		colonneResultat.setMinWidth(90);
		colonneResultat.setStyle("-fx-alignment: CENTER;");
		colonneResultat.setCellValueFactory(c -> c.getValue().get_resultat());

		tv.getColumns().addAll(colonneVariable, colonneResultat);

		tv.setItems(proprietes);
		tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		return tv;
	}

	public TableView<
			? extends Resultat_temperature> get_tableView_temperature() {

		// Ajouté
		for (Resultat_temperature_LCCC resultat : paliers_temperatures) {
			resultat.setValues();
		} ///

		TableView<Resultat_temperature_LCCC> tv_temperatures = new TableView<
				Resultat_temperature_LCCC>();

		TableColumn<
				Resultat_temperature_LCCC,
				String> temperature = new TableColumn<>("Temperatures (°C)");
		temperature.setMinWidth(120);
		temperature.setStyle("-fx-alignment: CENTER;");
		temperature.setCellValueFactory(
				c -> c.getValue().get_string_Temperature());

		TableColumn<
				Resultat_temperature_LCCC,
				Number> cte = new TableColumn<>("CTE (m/m/°C)");
		cte.setMinWidth(120);
		cte.setStyle("-fx-alignment: CENTER;");
		cte.setCellValueFactory(c -> c.getValue().get_cte1());

		TableColumn<
				Resultat_temperature_LCCC,
				Number> e1 = new TableColumn<>("E (MPa)");
		e1.setMinWidth(120);
		e1.setStyle("-fx-alignment: CENTER;");
		e1.setCellValueFactory(c -> c.getValue().getE1());

		tv_temperatures.getColumns().addAll(temperature, e1, cte);
		tv_temperatures
				.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		tv_temperatures.setItems(paliers_temperatures);

		return tv_temperatures;
	}

	/**
	 * @return Le module d'élasticité de la Céramique.
	 */
	@Override
	public double getE() {
		return Ceramique.module_elasticite;
	}

	protected double getNpxX() {
		double calcul = 0;

		if (composant.get("nx") == 0) {
			calcul = 0;
		} else {
			calcul = (composant.get("nx") - 1) / 2.0;
		}
		return calcul;
	}

	/**
	 * Permet de passer de la prise en compte d'un seul coefficient de
	 * dilatation à autant que l'on souhaite.
	 * 
	 * @param La température à laquelle on souhaite connaître la dilatation.
	 **/
	@Override
	public double getCte(double temperature) {
		double cte = 0;

		for (Resultat_temperature_LCCC resultat : paliers_temperatures) {
			if (resultat.getTemperature() == temperature)
				return resultat.getCte1();
		}

		Resultat_temperature_LCCC resultat = new Resultat_temperature_LCCC(
				this,
				temperature);
		cte = resultat.getCte1();

		paliers_temperatures.add(resultat);

		return cte;
	}
	///////

	public double getNpxY() {
		double calcul = 0;

		if (composant.get("ny") == 0) {
			calcul = 0;
		} else {
			calcul = (composant.get("ny") - 1) / 2.0;
		}

		return calcul;
	}

	public double getX() {

		double calcul;

		if (composant.get("nx") == 0) {
			calcul = composant.get("x") / 2;
		} else {
			calcul = (composant.get("nx") - 1) / 2 * composant.get("pas");
		}

		return calcul;
	}

	public double getY() {
		double calcul;

		if (composant.get("ny") == 0 || composant.get("nx") > 0) {
			calcul = composant.get("y") / 2;
		} else {
			calcul = (composant.get("ny") - 1) / 2 * composant.get("pas");
		}

		return calcul;
	}

	protected double getU0X() {
		return getNpxX();
	}

	public double getU0Y() {
		return getNpxY();
	}

	protected double getXX() {
		double calcul = 0;

		calcul = ((getNpxX() + 1) * (2 * getU0X() + getNpxX() * (-1)) / 2)
				* composant.get("pas");
		return calcul;
	}

	protected double getXY() {
		double calcul = 0;

		calcul = ((getNpxY() + 1) * (2 * getU0Y() + getNpxY() * (-1)) / 2)
				* composant.get("pas");

		return calcul;
	}

	@Override
	public double getCoefficientFatigueA() {
		return 0.9289;
	}

	@Override
	public double getCoefficientFatigueB() {
		return -0.429;
	}

	public double getNombre_ex1() {
		if (this.composant.get("nx") == 0) {
			return this.composant.get("x");
		}
		return (this.composant.get("nx") - 1) * this.composant.get("pas");
	}

	public double getNombre_ey1() {
		if (this.composant.get("ny") == 0) {
			return this.composant.get("y");
		}
		return (this.composant.get("ny") - 1) * this.composant.get("pas");
	}

	public double getNombre_nix() {
		if (this.composant.get("nx") == 0) {
			return Double.valueOf(
					(int) (((this.composant.get("x")
							- this.composant.get("pas")))
							/ this.composant.get("pas")) + 2);
		}
		return this.getNombre_ex1() / this.composant.get("pas") + 3;
	}

	public double getNombre_niy() {
		if (this.composant.get("ny") == 0) {
			return Double.valueOf(
					(int) ((this.composant.get("y") - this.composant.get("pas"))
							/ this.composant.get("pas")) + 2);
		}
		return this.getNombre_ey1() / composant.get("pas") + 3;
	}

	/**
	 * Cette fonction utilise la matrice de Billes du composant afin de calculer
	 * le Nombre X (ou la longueur caractéristique Lnp) associé à ce composant.
	 * 
	 * @param composant                 Le composant LCC dont on cherche à
	 *                                  calculer le Nombre X (ou Lnp1)
	 * 
	 * @param affichageMatricesDeCalcul En le passant à "true", permet
	 *                                  d'afficher toutes les étapes
	 *                                  intermédiaires si on le souhaite
	 * 
	 * @param sortieSouhaitee           Un paramètre permettant de modifier la
	 *                                  sortie de la fonction. Pour récupérer le
	 *                                  nombre X sortieSouhaitee vaudra
	 *                                  "nombreX", pour Lnp1, sortieSouhaitee
	 *                                  vaudra "lnp1".
	 * 
	 * @return Le nombre X(ou Lnp1, ou XX ou XY) calculé sur la base de la
	 *         matrice de billes
	 */
	public double calculNombreX(
			Composant composant,
			boolean affichageMatricesDeCalcul,
			String sortieSouhaitee) {

		int nb_lignes = this.matriceDeBilles.length;
		int nb_colonnes = this.matriceDeBilles[0].length;

		Double matrice_x_y = (double) nb_colonnes / (double) nb_lignes;

		Double centreX = (((((double) nb_colonnes + 1) / 2) - 1) - 1)
				* this.composant.get("pas");
		Double centreY = (((((double) nb_lignes + 1) / 2) - 1) - 1)
				* this.composant.get("pas");

		Double centreNumeroX = (centreX / this.composant.get("pas")) + 1;
		Double centreNumeroY = (centreY / this.composant.get("pas")) + 1;
		Double puceX;
		Double puceY;

		int PuceColonneX1;
		int PuceColonneX2;
		int PuceRangeeY1;
		int PuceRangeeY2;

		puceX = (double) nb_colonnes;
		puceY = (double) nb_lignes;

		Double puce_x_y = puceX / puceY;

		PuceColonneX1 = 0;
		PuceColonneX2 = (int) nb_colonnes - 1;

		PuceRangeeY1 = 0;
		PuceRangeeY2 = (int) nb_lignes - 1;

		// Matrice finale
		Double matrice_creuse[][] = new Double[nb_lignes][nb_colonnes];

		// Permet de comprendre le processus de calcul
		Integer matrice_intermediaire[][] = new Integer[nb_lignes][nb_colonnes];

		Double matriceDistanceCentreX[][] = new Double[nb_lignes][nb_colonnes];
		Double matriceDistanceCentreY[][] = new Double[nb_lignes][nb_colonnes];
		Double matriceDistanceCentreXBillesSousPuce[][] = new Double[nb_lignes][nb_colonnes];
		Double matriceDistanceCentreYBillesSousPuce[][] = new Double[nb_lignes][nb_colonnes];
		Double matriceDistanceCentreXYBillesSousPuce[][] = new Double[nb_lignes][nb_colonnes];
		;

		Integer matriceBillesSousPuce[][] = new Integer[nb_lignes][nb_colonnes];
		Integer matriceBillesSousZoneNeutreHorsPuceT[][] = new Integer[nb_lignes][nb_colonnes];

		Double distanceCentreX = 0d;
		Double distanceCentreY = 0d;

		Double pointNeutreTCorrigeNumeroX1 = null;
		Double pointNeutreTCorrigeNumeroX2 = null;
		Double pointNeutreTCorrigeNumeroY1 = null;
		Double pointNeutreTCorrigeNumeroY2 = null;

		boolean dansLaPuce;
		boolean dansZoneNeutreT = false;

		Double matriceColonneCalculIntermediairesT[][] = new Double[3][nb_colonnes];
		Double matriceLigneCalculIntermediairesT[][] = new Double[nb_lignes][3];

		int nombreX;
		double lnp1;

		for (int i = 0; i < nb_lignes; i++) {
			for (int j = 0; j < nb_colonnes; j++) {
				if (this.matriceDeBilles[i][j]) {

					dansLaPuce = Outils.inRange(PuceRangeeY1, PuceRangeeY2, i)
							&& Outils.inRange(PuceColonneX1, PuceColonneX2, j);

					distanceCentreX = 0d;
					distanceCentreY = 0d;

					matrice_intermediaire[i][j] = 1;

					distanceCentreX = -(centreNumeroX - j)
							* this.composant.get("pas");

					distanceCentreY = (centreNumeroY - i)
							* this.composant.get("pas");

					matriceDistanceCentreX[i][j] = distanceCentreX;

					matriceDistanceCentreY[i][j] = distanceCentreY;

					if (dansLaPuce) {
						// Matrice de 1 et 0
						matriceBillesSousPuce[i][j] = 1;

						distanceCentreX = 0d;
						distanceCentreY = 0d;

						distanceCentreX = -(centreNumeroX - j)
								* this.composant.get("pas");
						distanceCentreY = (centreNumeroY - i)
								* this.composant.get("pas");

						matriceDistanceCentreXBillesSousPuce[i][j] = distanceCentreX;

						matriceDistanceCentreYBillesSousPuce[i][j] = distanceCentreY;

					} else {

						matriceBillesSousPuce[i][j] = 0;
						matriceDistanceCentreXBillesSousPuce[i][j] = 0d;

						matriceDistanceCentreYBillesSousPuce[i][j] = 0d;
					}
				}
			}
		}
		int sumColonneT = 0;
		// Calculs intermédiares Colonne pour le nombre X à la température T1

		// Somme Colonne matrice Billes sous Zone Neutre hors puce T1
		for (int j = 0; j < nb_colonnes; j++) {
			for (int k = 0; k < nb_lignes; k++) {
				if (matriceBillesSousZoneNeutreHorsPuceT[k][j] != null) {
					sumColonneT += matriceBillesSousZoneNeutreHorsPuceT[k][j];
				}

			}
			matriceColonneCalculIntermediairesT[0][j] = (double) sumColonneT;
			sumColonneT = 0;
		}

		Double maxT = 0d;
		// Maximum de chaque colonne matrice Distance/centre sens y billes sous
		// puce
		for (int j = 0; j < nb_colonnes; j++) {
			for (int i = 0; i < nb_lignes; i++) {
				if (matriceDistanceCentreYBillesSousPuce[i][j] != null) {
					if (matriceDistanceCentreYBillesSousPuce[i][j] > maxT) {
						maxT = matriceDistanceCentreYBillesSousPuce[i][j];
					}
				}
			}
			matriceColonneCalculIntermediairesT[1][j] = maxT;
			maxT = 0d;
		}

		Double sommeProdT = 0d;
		for (int j = 0; j < nb_colonnes; j++) {
			for (int i = 0; i < nb_lignes; i++) {
				if (matriceDistanceCentreYBillesSousPuce[i][j] != null) {
					sommeProdT += Math
							.abs(matriceDistanceCentreYBillesSousPuce[i][j]);
				}
			}

			matriceColonneCalculIntermediairesT[2][j] = (double) Math.round(
					(sommeProdT + (matriceColonneCalculIntermediairesT[0][j]
							* matriceColonneCalculIntermediairesT[1][j])) / 2);
			sommeProdT = 0d;
		}

		for (int i = 0; i < nb_lignes; i++) {

			matriceLigneCalculIntermediairesT[i][0] = (double) Outils
					.arraySum(matriceBillesSousZoneNeutreHorsPuceT[i]);
			matriceLigneCalculIntermediairesT[i][1] = Outils
					.arrayMax(matriceDistanceCentreXBillesSousPuce[i]);
			matriceLigneCalculIntermediairesT[i][2] = (double) Math.round(
					(Outils.arrayAbsoluteSum(
							matriceDistanceCentreXBillesSousPuce[i])
							+ (matriceLigneCalculIntermediairesT[i][0]
									* matriceLigneCalculIntermediairesT[i][1]))
							/ 2);
		}

		int compteur = 0;
		Double moyenneT11 = 0d;

		// Calcul moyenne d'une colonne d'une matrice
		// for (int i = 0; i < nb_lignes; i++) {
		// if (matriceLigneCalculIntermediairesT[i][2] > 0) {
		// moyenneT11 += matriceLigneCalculIntermediairesT[i][2];
		// compteur++;
		// }
		// }

		// moyenneT11 /= compteur;

		// Calcul max d'une colonne d'une matrice
		for (int i = 0; i < nb_lignes; i++) {
			if (matriceLigneCalculIntermediairesT[i][2] != null) {
				if (moyenneT11 < matriceLigneCalculIntermediairesT[i][2]) {
					moyenneT11 = matriceLigneCalculIntermediairesT[i][2];
				}
			}
		}

		Double moyenneT12 = 0d;
		// Moyenne sur ligne d'un tableau
		// moyenneT12 = Outils
		// .arrayPositiveAverage(matriceColonneCalculIntermediairesT[2]);

		// Max sur ligne d'un tableau
		moyenneT12 = Outils.arrayMax(matriceColonneCalculIntermediairesT[2]);

		nombreX = (int) Math.round(
				Math.sqrt(Math.pow(moyenneT11, 2) + Math.pow(moyenneT12, 2)));

		// Matrice Distance Centre XY Billes sous puce
		for (int i = 0; i < nb_lignes; i++) {
			for (int j = 0; j < nb_colonnes; j++) {
				if (matriceDistanceCentreXBillesSousPuce[i][j] != null
						&& matriceDistanceCentreYBillesSousPuce[i][j] != null) {

					matriceDistanceCentreXYBillesSousPuce[i][j] = Math.sqrt(
							Math.pow(
									matriceDistanceCentreXBillesSousPuce[i][j],
									2)
									+ Math.pow(
											matriceDistanceCentreYBillesSousPuce[i][j],
											2));
				}
			}
		}

		lnp1 = Outils.arrayMaxMatrix(matriceDistanceCentreXYBillesSousPuce);

		// Calcul de Lnp1
		// double lnp11, lnp12;

		// On cherche le maximum de la colonne 2 de cette matrice
		// lnp11 = matriceLigneCalculIntermediairesT[0][1];
		// for (int i = 0; i < nb_lignes; i++) {
		// if (matriceLigneCalculIntermediairesT[i][1] > lnp11) {
		// lnp11 = matriceLigneCalculIntermediairesT[i][1];
		// }
		// }

		// lnp12 = Outils.arrayMax(matriceColonneCalculIntermediairesT[1]);

		// lnp1 = Math.sqrt(Math.pow(lnp11, 2) + Math.pow(lnp12, 2));

		// permet d'afficher toutes les étapes intermédiaires si nécéssaires
		if (affichageMatricesDeCalcul) {

			System.out.println("Matrice Intermediaire:\n");
			Outils.prettyPrintMatrix(matrice_intermediaire);

			System.out.println("Matrice Distance/centre sens x:\n");
			Outils.prettyPrintMatrix(matriceDistanceCentreXBillesSousPuce);

			System.out.println("Matrice Distance/centre sens y:\n");
			Outils.prettyPrintMatrix(matriceDistanceCentreYBillesSousPuce);

			System.out.println("Matrice Billes sous puce:\n");
			Outils.prettyPrintMatrix(matriceBillesSousPuce);

			System.out.println(
					"Matrice Billes sous zone neutre et hors puce T:\n");
			Outils.prettyPrintMatrix(matriceBillesSousZoneNeutreHorsPuceT);

			System.out.println(
					"Matrice Distance/centre sens x Billes sous puce:\n");
			Outils.prettyPrintMatrix(matriceDistanceCentreXBillesSousPuce);

			System.out.println(
					"Matrice Distance/centre sens y Billes sous puce:\n");
			Outils.prettyPrintMatrix(matriceDistanceCentreYBillesSousPuce);

			System.out.println("Matrice Colonne Calcul Intermediaire T:\n");
			Outils.prettyPrintMatrix(matriceColonneCalculIntermediairesT);

			System.out.println("Matrice Ligne Calcul Intermediaire T:\n");
			Outils.prettyPrintMatrix(matriceLigneCalculIntermediairesT);

			System.out.println("NombreX: " + nombreX);
			System.out.println("Lnp1: " + lnp1);

		}

		switch (sortieSouhaitee) {
		case "nombreX":
			return (double) nombreX;

		case "lnp1":
			return lnp1;

		case "nombreXX":
			return moyenneT12;

		case "nombreXY":
			return moyenneT11;

		default:
			Outils.alerteUtilisateur(
					"ERROR",
					"Erreur 'calculNombreX'",
					"'nombreX' ou 'lnp1' attendu comme parametre mais"
							+ sortieSouhaitee + " trouvé !");
			return (Double) null;

		}
	}

	public String toString(boolean avecMatriceBilles) {

		String str = "";
		str += super.getNom();

		if (avecMatriceBilles) {
			str += "\n";
			for (int i = 0; i < this.composant.get("ny"); i++) {
				for (int j = 0; j < this.composant.get("nx"); j++) {
					str += this.matriceDeBilles[i][j];
					str += ";";
				}
				str += "\n";
			}
		}
		return str;
	}
}
