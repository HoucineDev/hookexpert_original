package com.app.ancea;

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

public class ComposantCSP extends ComposantBGA {

	static ImageView image = new ImageView(
			ComposantCSP.class.getResource("images/CSP.png").toExternalForm());
	static ImageView imageNouveauComposant = new ImageView(
			ComposantCSP.class.getResource("images/CSP.png").toExternalForm());
	static ImageView cspDieAttach = new ImageView(
			ComposantCSP.class.getResource("images/CSP_Die_Attach.png")
					.toExternalForm());
	static ImageView cspUnderfill = new ImageView(
			ComposantCSP.class.getResource("images/CSP_Underfill.png")
					.toExternalForm());

	public TableView<Resultat_temperature_CSP> tv;

	public static final double beta = 4.9;
	public static final double vernis = 1.4;

	private static final Logger logger = Logger.getLogger(Main.class.getName());

	ComposantCSP composant;

	protected ObservableList<Resultat_temperature_CSP> paliers_temperatures;

	public ComposantCSP(
			OngletPcb pcb,
			String nom,
			double Puce_x,
			double Puce_y,
			double Puce_z,
			double pas,
			boolean billes_angles,
			int pcb_x,
			int pcb_y,
			double resine_x,
			double resine_y,
			double resine_z,
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
				resine_x,
				resine_y,
				resine_z,
				nombre_billes_x,
				nombre_billes_y,
				liaisonPuce,
				epaisseurLiaison);

		paliers_temperatures = FXCollections.observableArrayList();
		for (int d : Ressources.tableau_temperatures) {
			paliers_temperatures.add(new Resultat_temperature_CSP(this, d));
		}
	}

	public ComposantCSP(OngletPcb pcb, String path) {
		super(pcb, path);
		paliers_temperatures = FXCollections.observableArrayList();
		for (int d : Ressources.tableau_temperatures) {
			paliers_temperatures.add(new Resultat_temperature_CSP(this, d));
		}
	}

	public ComposantCSP(OngletPcb pcb) {
		super(pcb);
		paliers_temperatures = FXCollections.observableArrayList();
		for (int d : Ressources.tableau_temperatures) {
			paliers_temperatures.add(new Resultat_temperature_CSP(this, d));
		}
	}

	public double getCter(double temperature) {
		double cter = 0;

		for (Resultat_temperature_CSP resultat : paliers_temperatures) {
			if (resultat.getTemperature() == temperature) {
				logger.severe("CTER(composant): " + resultat.getCter());
				return resultat.getCter();
			}
		}

		Resultat_temperature_CSP resultat = new Resultat_temperature_CSP(
				this,
				temperature);
		cter = resultat.getCter();

		paliers_temperatures.add(resultat);
		logger.severe("CTER(composant): " + resultat.getCter());
		return cter;
	}

	public TableView<Resultat_temperature_CSP> get_tableView_temperature() {

		for (Resultat_temperature_CSP resultat : paliers_temperatures) {
			resultat.setValues();
		}

		tv = new TableView<Resultat_temperature_CSP>();
		tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		TableColumn<
				Resultat_temperature_CSP,
				Number> temperature = new TableColumn<>("Temperatures (°C)");
		temperature.setCellValueFactory(
				c -> new SimpleDoubleProperty(c.getValue().getTemperature()));
		temperature.setStyle("-fx-alignment: CENTER;");
		temperature.setMinWidth(80);

		TableColumn<
				Resultat_temperature_CSP,
				Number> e1_traction = new TableColumn<>("E1 x (MPa)");
		e1_traction.setCellValueFactory(
				c -> new SimpleIntegerProperty(
						(int) c.getValue().getE1_traction()));
		e1_traction.setStyle("-fx-alignment: CENTER;");
		e1_traction.setMinWidth(100);

		TableColumn<
				Resultat_temperature_CSP,
				Number> e1_flexion = new TableColumn<>("E1f x (MPa)");
		e1_flexion.setCellValueFactory(
				c -> new SimpleIntegerProperty(
						(int) c.getValue().getE1_flexion()));
		e1_flexion.setStyle("-fx-alignment: CENTER;");
		e1_flexion.setMinWidth(100);

		// TableColumn<
		// Resultat_temperature_CSP,
		// String> cte1 = new TableColumn<>("CTE1 (m/m/°C)");
		// cte1.setCellValueFactory(
		// c -> new SimpleStringProperty(
		// Outils.nombreChiffreScientifiqueApresVirgule(
		// c.getValue().getCte1())));
		// cte1.setStyle("-fx-alignment: CENTER;");
		// cte1.setMinWidth(20);

		TableColumn<
				Resultat_temperature_CSP,
				String> cte2Plus1 = new TableColumn<>("CTE1+2 (m/m/°C)");
		cte2Plus1.setCellValueFactory(
				c -> new SimpleStringProperty(
						Outils.nombreChiffreScientifiqueApresVirgule(
								c.getValue().getCte2Plus1())));
		cte2Plus1.setStyle("-fx-alignment: CENTER;");
		cte2Plus1.setMinWidth(20);

		TableColumn<
				Resultat_temperature_CSP,
				Number> e2_traction = new TableColumn<>("E2 x (MPa)");
		e2_traction.setCellValueFactory(
				c -> new SimpleIntegerProperty(
						(int) c.getValue().getE2_traction()));
		e2_traction.setStyle("-fx-alignment: CENTER;");
		e2_traction.setMinWidth(100);

		TableColumn<
				Resultat_temperature_CSP,
				Number> e2_flexion = new TableColumn<>("E2f x(MPa)");
		e2_flexion.setCellValueFactory(
				c -> new SimpleIntegerProperty(
						(int) c.getValue().getE2_flexion()));
		e2_flexion.setStyle("-fx-alignment: CENTER;");
		e2_flexion.setMinWidth(100);

		TableColumn<
				Resultat_temperature_CSP,
				String> cte2 = new TableColumn<>("CTE2 (m/m/°C)");
		cte2.setCellValueFactory(
				c -> new SimpleStringProperty(
						Outils.nombreChiffreScientifiqueApresVirgule(
								c.getValue().getCte2())));
		cte2.setStyle("-fx-alignment: CENTER;");
		cte2.setMinWidth(20);

		TableColumn<
				Resultat_temperature_CSP,
				String> cter = new TableColumn<>("CTER(1 et 2) (m/m/°C)");
		cter.setCellValueFactory(
				c -> new SimpleStringProperty(
						Outils.nombreChiffreScientifiqueApresVirgule(
								c.getValue().getCter())));
		cter.setStyle("-fx-alignment: CENTER;");
		cter.setMinWidth(100);

		tv.getColumns().addAll(
				temperature,
				e1_traction,
				e1_flexion,
				// cte1,
				cte2Plus1,
				e2_traction,
				e2_flexion,
				cte2,
				cter);

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

					calcul_poids();
					calcul_pcb_z();
					calcul_composant_x();
					calcul_composant_y();
					calcul_composant_z();

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

	@Override
	public double getCoefficientFatigueA() {
		return 0.9492;
	}

	@Override
	public double getCoefficientFatigueB() {
		return -0.466;
	}

	public ImageView getImage() {
		return image;
	}

}
