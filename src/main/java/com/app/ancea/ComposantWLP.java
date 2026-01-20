package com.app.ancea;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;

public class ComposantWLP extends ComposantBilles
		implements InterfaceComposantBilles {

	public static final ImageView image = new ImageView(
			ComposantWLP.class.getResource("images/WLP.png").toExternalForm());
	public static final ImageView imageNouveauComposant = new ImageView(
			ComposantWLP.class.getResource("images/WLP.png").toExternalForm());

	public static final double beta = 4.9;
	public static final double vernis = 1.4;

	private Propriete poids, nombre_billes_x, nombre_billes_y;
	private ObservableList<Resultat_temperature_WLP> paliers_temperatures;

	public ComposantWLP(
			String nom,
			double puce_x,
			double puce_y,
			double puce_z,
			double pas,
			boolean billes,
			int nombreBillesX,
			int nombreBillesY) {

		super(nom, puce_x, puce_y, puce_z, pas, billes);

		poids = new Propriete("Poids");
		this.nombre_billes_x = new Propriete(
				"Nombre de billes x",
				nombreBillesX);
		this.nombre_billes_y = new Propriete(
				"Nombre de billes y",
				nombreBillesY);

		proprietes = FXCollections
				.observableArrayList(poids, nombre_billes_x, nombre_billes_y);

		paliers_temperatures = FXCollections.observableArrayList();
		for (int d : Ressources.tableau_temperatures) {
			paliers_temperatures.add(new Resultat_temperature_WLP(this, d));
		}

		setValues();

	}

	public ComposantWLP() {

		super();

		poids = new Propriete("Poids");
		nombre_billes_x = new Propriete("Nombre de billes x");
		nombre_billes_y = new Propriete("Nombre de billes y");

		proprietes = FXCollections
				.observableArrayList(poids, nombre_billes_x, nombre_billes_y);

		paliers_temperatures = FXCollections.observableArrayList();
		for (int d : Ressources.tableau_temperatures) {
			paliers_temperatures.add(new Resultat_temperature_WLP(this, d));
		}
	}

	public TableView<Resultat_temperature_WLP> get_tableView_temperature() {

		for (Resultat_temperature_WLP resultat : paliers_temperatures) {
			resultat.setValues();
		}

		TableView<Resultat_temperature_WLP> tv_temperatures = new TableView<
				Resultat_temperature_WLP>();

		TableColumn<
				Resultat_temperature_WLP,
				String> temperature = new TableColumn<>("Temperatures (°C)");
		temperature.setMinWidth(120);
		temperature.setStyle("-fx-alignment: CENTER;");
		temperature.setCellValueFactory(
				c -> c.getValue().get_string_Temperature());

		TableColumn<
				Resultat_temperature_WLP,
				Number> e1_traction = new TableColumn<>("E1 traction (MPa)");
		e1_traction.setMinWidth(150);
		e1_traction.setStyle("-fx-alignment: CENTER;");
		e1_traction.setCellValueFactory(c -> c.getValue().getE1_traction());

		TableColumn<
				Resultat_temperature_WLP,
				Number> e1_flexion = new TableColumn<>("E1 flexion (MPa)");
		e1_flexion.setMinWidth(150);
		e1_flexion.setStyle("-fx-alignment: CENTER;");
		e1_flexion.setCellValueFactory(c -> c.getValue().getE1_flexion());

		TableColumn<
				Resultat_temperature_WLP,
				Number> cte1 = new TableColumn<>("CTE1 (µm/m°C)");
		cte1.setMinWidth(120);
		cte1.setStyle("-fx-alignment: CENTER;");
		cte1.setCellValueFactory(c -> c.getValue().get_cte1());

		tv_temperatures.getColumns()
				.addAll(temperature, e1_traction, e1_flexion, cte1);
		tv_temperatures
				.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		tv_temperatures.setItems(paliers_temperatures);

		return tv_temperatures;
	}

	public void setValues() {
		calcul_poids();
		// calcul_nombre_billes_x();
		// calcul_nombre_billes_y();
	}

	public void calcul_poids() {
		Silicium s = new Silicium();
		double mv_silicium = s.masse_volumique(0);

		double calcul = 0;
		calcul = ((getPuce_x() * getPuce_y() * getPuce_z() * mv_silicium
				* 0.001));

		this.poids.set_resultat(Outils.doubleToString(calcul, 2));

	}

	public void setNombre_billes_x(int nombre_billes_x) {
		super.nombre_billes_x.set(nombre_billes_x);
		this.nombre_billes_x.set_resultat(String.valueOf(nombre_billes_x));
	}

	public int getNombre_billes_x() {
		return Integer.parseInt(this.nombre_billes_x.get_resultat().get());
	}

	public int getNombre_billes_y() {
		return Integer.parseInt(this.nombre_billes_y.get_resultat().get());
	}

	public void setNombre_billes_y(int nombre_billes_y) {
		super.nombre_billes_y.set(nombre_billes_y);
		this.nombre_billes_y.set_resultat(String.valueOf(nombre_billes_y));
	}

	// protected double getNombreBillesX() {
	// double calcul = 0;
	// calcul = this.getPuce_x() / this.getPas();
	// return calcul;
	// }

	// protected double getNombreBillesY() {
	// double calcul = 0;
	// calcul = this.getPuce_y() / this.getPas();
	// return calcul;
	// }

	// public void calcul_nombre_billes_x() {
	// nombre_billes_x
	// .set_resultat(Outils.doubleToString(getNombreBillesX(), 2));
	// }

	// public void calcul_nombre_billes_y() {
	// nombre_billes_y
	// .set_resultat(Outils.doubleToString(getNombreBillesY(), 2));
	// }

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

	public void charger_wlp(String path) {

		try {

			FileReader fr = new FileReader(path);
			BufferedReader br = new BufferedReader(fr);
			ArrayList<String> als = new ArrayList<String>();
			String line = br.readLine();

			while (line != null) {
				als.add(line);
				line = br.readLine();

			}

			String[] tabs = als.get(0).split(";");

			this.setPas(Double.valueOf(tabs[0]));
			this.setPresence_billes_angles(Boolean.valueOf(tabs[1]));
			this.setPuce_x(Double.valueOf(tabs[2]));
			this.setPuce_y(Double.valueOf(tabs[3]));
			this.setPuce_z(Double.valueOf(tabs[4]));
			this.setNombre_billes_x(
					((int) Outils.StringWithCommaToDouble(tabs[5])));
			this.setNombre_billes_y(
					((int) Outils.StringWithCommaToDouble(tabs[6])));

			setValues();

			this.matriceBilles = new boolean[this.getNombre_billes_y()][this
					.getNombre_billes_x()];

			try {
				if (als.get(1) != null) {
					// Matrice de Billes du Composant
					for (int i = 0; i < this.getNombre_billes_y(); i++) {
						String[] tabs2 = als.get(i + 1).split(";");

						for (int j = 0; j < this.getNombre_billes_x(); j++) {
							this.matriceBilles[i][j] = Boolean
									.valueOf(tabs2[j]);
						}
					}

				}
			} catch (IndexOutOfBoundsException e) {// Génération d'une erreur
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Erreur de Lecture du WLP !");
				alert.setHeaderText("Impossible de lire la matrice de billes");
				alert.setContentText("Vérifier fichier de Sauvegarde");
				alert.showAndWait();
			}

			br.close();
			fr.close();
		}

		catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		catch (IOException exception) {
			System.out.println(
					"Erreur lors de la lecture : " + exception.getMessage());
		}

	}

	// public int getNombre_billes_x() {
	// return (int) getNombreBillesX();
	// }

	// public int getNombre_billes_y() {
	// return (int) getNombreBillesY();
	// }

	public ImageView getImage() {
		return image;
	}

	public String toString() {
		return this.getPas() + ";" + this.getPresence_billes_angles() + ";"
				+ this.getPuce_x() + ";" + this.getPuce_y() + ";"
				+ this.getPuce_z() + ";" + this.getNombre_billes_x() + ";"
				+ this.getNombre_billes_y();
	}

	@Override
	public double getE1Traction(double temperature) {
		double e1_traction = 0;

		for (Resultat_temperature_WLP resultat : paliers_temperatures) {
			if (resultat.getTemperature() == temperature)
				return resultat.calcul_e1_traction();
		}

		Resultat_temperature_WLP resultat = new Resultat_temperature_WLP(
				this,
				temperature);
		e1_traction = resultat.calcul_e1_traction();

		paliers_temperatures.add(resultat);

		return e1_traction;
	}

	@Override
	public double getCte(double temperature) {
		double cte = 0;

		for (Resultat_temperature_WLP resultat : paliers_temperatures) {
			if (resultat.getTemperature() == temperature)
				return resultat.getCte1();
		}

		Resultat_temperature_WLP resultat = new Resultat_temperature_WLP(
				this,
				temperature);
		cte = resultat.getCte1();

		paliers_temperatures.add(resultat);

		return cte;
	}

	@Override
	public double getCoefficientFatigueA() {
		return 0.6;
	}

	@Override
	public double getCoefficientFatigueB() {
		return -.36687;
	}

	@Override
	public String getComposant_z() {
		return String.valueOf(getPuce_z());
	}

}
