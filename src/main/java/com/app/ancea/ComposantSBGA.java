package com.app.ancea;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;

public class ComposantSBGA extends ComposantBGA {

	static ImageView sbgaUnderfill = new ImageView(
			ComposantSBGA.class.getResource("images/SBGA_Underfill.png")
					.toExternalForm());
	static ImageView sbgaDieAttach = new ImageView(
			ComposantSBGA.class.getResource("images/SBGA_DieAttach.png")
					.toExternalForm());

	public TableView<Resultat_temperature_SBGA> tv;

	public static final double vernis = 0;
	public static final double beta = 0;

	private static final Logger logger = Logger.getLogger(Main.class.getName());

	protected ObservableList<Resultat_temperature_SBGA> paliers_temperatures;

	public Propriete cuivreX, cuivreY, cuivreZ, cuivreZ2;

	public ComposantSBGA(
			OngletPcb pcb,
			String nom,
			double Puce_x,
			double Puce_y,
			double Puce_z,
			double pas,
			boolean billes_angles,
			int pcb_x,
			int pcb_y,
			double cuivre_x,
			double cuivre_y,
			double cuivre_z,
			int nombre_billes_x,
			int nombre_billes_y,
			String liaisonPuce,
			double epaisseurLiaison) {
		super(
				pcb,
				nom,
				Puce_x,
				Puce_y,
				Puce_z,
				pas,
				billes_angles,
				pcb_x,
				pcb_y,
				0,
				0,
				0,
				nombre_billes_x,
				nombre_billes_y,
				liaisonPuce,
				epaisseurLiaison);

		this.cuivreX = new Propriete("Cuivre x", cuivre_x);
		this.cuivreY = new Propriete("Cuivre y", cuivre_y);
		this.cuivreZ = new Propriete("Cuivre z", cuivre_z);
		this.cuivreZ2 = new Propriete("Cuivre z2", cuivre_z);

		Propriete blank = new Propriete("");

		paliers_temperatures = FXCollections.observableArrayList();
		for (int d : Ressources.tableau_temperatures) {
			paliers_temperatures.add(new Resultat_temperature_SBGA(this, d));
		}

		proprietes = FXCollections.observableArrayList(
				super.composant_x,
				super.composant_y,
				super.composant_z,
				blank,
				super.pcb_x,
				super.pcb_y,
				super.pcb_z,
				blank,
				this.cuivreX,
				this.cuivreY,
				this.cuivreZ,
				this.cuivreZ2,
				blank,
				super.pcb.p_epaisseur_totale_nominale,
				blank,
				super.p_pas,
				blank,
				super.p_nombre_billes_x,
				super.p_nombre_billes_y,
				blank,
				super.p_puce_x,
				super.p_puce_y,
				super.p_puce_z,
				blank,
				super.liaisonPuce,
				super.epaisseurLiaison);

	}

	public ComposantSBGA(OngletPcb pcb, String path) {
		super(pcb, path);

		this.cuivreX = new Propriete("Cuivre x", 0);
		this.cuivreY = new Propriete("Cuivre y", 0);
		this.cuivreZ = new Propriete("Cuivre z", 0);
		this.cuivreZ2 = new Propriete("Cuivre z2", 0);
		Propriete blank = new Propriete("");
		blank.set_resultat(" ");
		// chargement de cuivreX, cuivreY et cuivreZ
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

			String[] tabs = als.get(als.size() - 1).split(";");

			this.setCuivreX(Outils.StringWithCommaToDouble(tabs[14]));
			this.setCuivreY(Outils.StringWithCommaToDouble(tabs[15]));
			this.setCuivreZ(Outils.StringWithCommaToDouble(tabs[16]));
			this.setCuivreZ2(this.getCuivreZ());

			super.proprietes.add(blank);
			super.proprietes.add(this.cuivreX);
			super.proprietes.add(this.cuivreY);
			super.proprietes.add(this.cuivreZ);
			super.proprietes.add(this.cuivreZ2);

			/*
			 * LE SBGA n'a pas de résine Ces instructions font suite à une
			 * mauvaise architecture de départ
			 */
			super.proprietes.remove(super.resine_x);
			super.proprietes.remove(super.resine_y);
			super.proprietes.remove(super.resine_z);

			super.resine_x.set_resultat("null");
			super.resine_x.set_resultat("null");
			super.resine_x.set_resultat("null");

			super.composant_z
					.set_resultat(super.getComposantZ() + this.getCuivreZ());

		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}

		paliers_temperatures = FXCollections.observableArrayList();
		for (int d : Ressources.tableau_temperatures) {
			paliers_temperatures.add(new Resultat_temperature_SBGA(this, d));
		}
	}

	public ComposantSBGA(OngletPcb pcb) {
		super(pcb);

		this.cuivreX = new Propriete("Cuivre x", 0);
		this.cuivreY = new Propriete("Cuivre y", 0);
		this.cuivreZ = new Propriete("Cuivre z", 0);
		this.cuivreZ2 = new Propriete("Cuivre z2", 0);

		paliers_temperatures = FXCollections.observableArrayList();
		for (int d : Ressources.tableau_temperatures) {
			paliers_temperatures.add(new Resultat_temperature_SBGA(this, d));
		}
	}

	public TableView<Resultat_temperature_SBGA> get_tableView_temperature() {

		for (Resultat_temperature_SBGA resultat : paliers_temperatures) {
			resultat.setValues();
		}

		tv = new TableView<Resultat_temperature_SBGA>();
		tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		TableColumn<
				Resultat_temperature_SBGA,
				Number> temperature = new TableColumn<>("Temperatures (°C)");
		temperature.setCellValueFactory(
				c -> new SimpleDoubleProperty(c.getValue().getTemperature()));
		temperature.setStyle("-fx-alignment: CENTER;");
		temperature.setMinWidth(80);

		TableColumn<
				Resultat_temperature_SBGA,
				Number> e1_traction = new TableColumn<>("E1 x (MPa)");
		e1_traction.setCellValueFactory(
				c -> new SimpleIntegerProperty(
						(int) c.getValue().getE1_traction()));
		e1_traction.setStyle("-fx-alignment: CENTER;");
		e1_traction.setMinWidth(100);

		TableColumn<
				Resultat_temperature_SBGA,
				Number> e1_flexion = new TableColumn<>("E1f x (MPa)");
		e1_flexion.setCellValueFactory(
				c -> new SimpleIntegerProperty(
						(int) c.getValue().getE1_flexion()));
		e1_flexion.setStyle("-fx-alignment: CENTER;");
		e1_flexion.setMinWidth(100);

		// TableColumn<
		// Resultat_temperature_SBGA,
		// String> cte1 = new TableColumn<>("CTE1 (m/m/°C)");
		// cte1.setCellValueFactory(
		// c -> new SimpleStringProperty(
		// Outils.nombreChiffreScientifiqueApresVirgule(
		// c.getValue().getCte1())));
		// cte1.setStyle("-fx-alignment: CENTER;");
		// cte1.setMinWidth(20);

		TableColumn<
				Resultat_temperature_SBGA,
				String> cte2Plus1 = new TableColumn<>("CTE1+2 (m/m/°C)");
		cte2Plus1.setCellValueFactory(
				c -> new SimpleStringProperty(
						Outils.nombreChiffreScientifiqueApresVirgule(
								c.getValue().getCte2Plus1())));
		cte2Plus1.setStyle("-fx-alignment: CENTER;");
		cte2Plus1.setMinWidth(20);

		TableColumn<
				Resultat_temperature_SBGA,
				Number> e2_traction = new TableColumn<>("E2 x (MPa)");
		e2_traction.setCellValueFactory(
				c -> new SimpleIntegerProperty(
						(int) c.getValue().getE2_traction()));
		e2_traction.setStyle("-fx-alignment: CENTER;");
		e2_traction.setMinWidth(100);

		TableColumn<
				Resultat_temperature_SBGA,
				Number> e2_flexion = new TableColumn<>("E2f x (MPa)");
		e2_flexion.setCellValueFactory(
				c -> new SimpleIntegerProperty(
						(int) c.getValue().getE2_flexion()));
		e2_flexion.setStyle("-fx-alignment: CENTER;");
		e2_flexion.setMinWidth(100);

		TableColumn<
				Resultat_temperature_SBGA,
				String> cte2 = new TableColumn<>("CTE2 (m/m/°C)");
		cte2.setCellValueFactory(
				c -> new SimpleStringProperty(
						Outils.nombreChiffreScientifiqueApresVirgule(
								c.getValue().getCte2())));
		cte2.setStyle("-fx-alignment: CENTER;");
		cte2.setMinWidth(20);

		tv.getColumns().addAll(
				temperature,
				e1_traction,
				e1_flexion,
				// cte1,
				cte2Plus1,
				e2_traction,
				e2_flexion,
				cte2);

		tv.setItems(paliers_temperatures);

		return tv;
	}

	@SuppressWarnings("unchecked")
	public TableView<Propriete> get_tableView_proprietes() {

		TableView<Propriete> tvProprietes = new TableView<Propriete>();
		tvProprietes.setEditable(true);

		TableColumn<
				Propriete,
				String> colonneVariable = new TableColumn<Propriete, String>(
						"Proprietes");

		colonneVariable.setMinWidth(120);
		colonneVariable
				.setCellValueFactory(c -> c.getValue().get_nom_variable());

		TableColumn<
				Propriete,
				String> colonneResultat = new TableColumn<Propriete, String>(
						"Resultats");
		colonneResultat.setEditable(true);
		colonneResultat.setMinWidth(90);
		colonneResultat.setCellValueFactory(c -> c.getValue().get_resultat());
		colonneResultat.setCellFactory(TextFieldTableCell.forTableColumn());
		colonneResultat
				.setOnEditCommit((CellEditEvent<Propriete, String> event) -> {
					TablePosition<
							Propriete,
							String> position = event.getTablePosition();

					String nouvelleValeur = event.getNewValue();

					int row = position.getRow();
					event.getTableView().getItems().get(row)
							.set_resultat(nouvelleValeur);

					pcb.tous_calculs();

					this.calcul_poids();

					super.calcul_pcb_z();
					super.calcul_composant_x();
					super.calcul_composant_y();

					this.calcul_composant_z();

					for (Resultat_temperature_BGA resultat : paliers_temperatures) {
						resultat.calcul();
					}

					for (Resultat_temperature_pcb resultat : pcb.data) {
						resultat.setCalculs();
					}

					this.tv.refresh();
				});

		tvProprietes.getColumns().addAll(colonneVariable, colonneResultat);
		tvProprietes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		tvProprietes.setItems(proprietes);

		return tvProprietes;
	}

	/*
	 * A redéfinir avec la bonne valeur ! (Si c'est la même que dans le parent à
	 * supprimer)
	 */
	@Override
	public double getCoefficientFatigueA() {
		return 0.9492;
	}

	/*
	 * A redéfinir avec la bonne valeur ! (Si c'est la même que dans le parent à
	 * supprimer)
	 */
	@Override
	public double getCoefficientFatigueB() {
		return -0.466;
	}

	public ImageView getImage() {
		return image;
	}

	public void setCuivreX(double cuivre_x) {

		this.cuivreX.set_resultat(cuivre_x);

	}

	public void setCuivreY(double cuivre_y) {

		this.cuivreY.set_resultat(cuivre_y);
	}

	public void setCuivreZ(double cuivre_z) {

		this.cuivreZ.set_resultat(cuivre_z);

	}

	public void setCuivreZ2(double cuivre_z2) {

		this.cuivreZ2.set_resultat(cuivre_z2);

	}

	/**
	 * @return Le paramètre Cuivre X
	 */
	public double getCuivreX() {
		return Double.valueOf(this.cuivreX.get_resultat().get());
	}

	public double getCuivreY() {
		return Double.valueOf(this.cuivreY.get_resultat().get());
	}

	public double getCuivreZ() {
		return Double.valueOf(this.cuivreZ.get_resultat().get());
	}

	public double getCuivreZ2() {
		return Double.valueOf(this.cuivreZ2.get_resultat().get());
	}

	public String toString() {
		return super.getPas() + ";" + super.getNombre_billes_x() + ";"
				+ super.getNombre_billes_y() + ";"
				+ super.getPresence_billes_angles() + ";" + super.getPuce_x()
				+ ";" + getPuce_y() + ";" + getPuce_z() + ";" + "0.0" + ";"
				+ "0.0" + ";" + "0.0" + ";" + super.getPcb_x() + ";"
				+ super.getPcb_y() + ";" + super.getLiaisonPuce() + ";"
				+ super.getEpaisseurLiaison() + ";" + this.getCuivreX() + ";"
				+ this.getCuivreY() + ";" + this.getCuivreZ();
	}

	@Override
	public double calcul_poids() {

		Silicium s = new Silicium();
		double masseVolumiqueSilicium = s.masse_volumique(0);
		CuivreC19400 cu = new CuivreC19400();
		double masseVolumiqueCuivreC19400 = cu.masse_volumique(0);
		double calcul = 0;

		calcul = ((super.getPuce_x() * super.getPuce_y() * super.getPuce_z()
				* masseVolumiqueSilicium)
				+ (this.getCuivreX() * this.getCuivreY() * this.getCuivreZ()
						* masseVolumiqueCuivreC19400)
				+ (super.getPcb_x() * super.getPcb_y() * super.getPcb_z()
						* this.pcb.get_masse_volumique()))
				* 0.001;

		setPoids(calcul);
		return calcul;
	}

	@Override
	protected double getComposantZ() {
		double calcul;

		calcul = super.getPuce_z() + this.getCuivreZ() + super.getPcb_z()
				+ super.getEpaisseurLiaison();

		return calcul;
	}

	protected double getComposantZ2() {
		return this.getCuivreZ2() + super.getPcb_z();
	}

}
