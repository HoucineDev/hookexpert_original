package com.app.ancea;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.converter.NumberStringConverter;

public class OngletCarte extends VBox {

	private static final Logger logger = Logger.getLogger(Main.class.getName());

	private GridPane gpCarteExistante, gpConfigurationeCarte,
			gpTitreParametresProfils;
	private GridPane gpT1, gpD1, gpT2, gpD2;
	private TitledPane titreCarteExistante, titreConfigurationCarte,
			titreParametreProfil;
	private TextField tfNombreArticle, tfNombreAnnees, temperature1, temps1,
			temperature2, temps2;
	private ComboBox<String> cbPcb, cbCarte, cbProfilDeVie;
	private CheckBox cbVernisPulverise;
	private Button buttonGenererCarte, buttonSauvegarderCarte, buttonRafraichir,
			buttonCalculer;
	private Label labelMois;
	private final String nombreDeMois = "Nombre de mois: ";
	private String cheminProfilDeVie, cheminPCB, nomProfilDeVie;
	private SetupCarte carte;
	private TableView<SetupComposantCarte> tvListeComposantSurCarte;
	private FileChooser fileChooser;
	private int nbMois;
	private HashMap<String, Integer> composantsSurCarte;
	private VBox VbCarteExistanteEtConfigurationCarte;
	private HBox hbParametrageCarte;
	private CycleThermique cycle;
	private boolean vernis;

	public static final ImageView image = new ImageView(
			OngletCarte.class.getResource("images/CARTE.png").toExternalForm());

	public OngletCarte(int spacing) {

		super(10);

		fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(new File(Ressources.pathCartes));
		fileChooser.getExtensionFilters()
				.add(new ExtensionFilter("Carte électronique", "*.carte"));

		setPadding(new Insets(20, 20, 20, 20));

		initConfigurationCarte();
		initCarteExistante();
		initParametresProfilDeVie();
		addComposantDansInterface();
	}

	protected void initLineChart() {
//		System.out.println("composantsSurCarte: "+composantsSurCarte+" cheminProfilDeVie: "+cheminProfilDeVie+" cheminPCB: "+cheminPCB+"cycle: "+cycle+" nbMois: "+nbMois);
//		System.out.println("Cycle: "+cycle);
		carte = new SetupCarte(
				composantsSurCarte,
				cheminProfilDeVie,
				cheminPCB,
				cycle,
				nbMois,
				vernis);
		refreshGraphics();
	}

	private void refreshGraphics() {

		while (getChildren().size() > 2) {
			getChildren().remove(getChildren().size() - 1);
		}

		getChildren().addAll(
				carte.getLineChartProbabiliteDeDefaillance(),
				carte.getLineChartDensiteDeProbabilite());

	}

	private void initParametresProfilDeVie() {
		gpTitreParametresProfils = Outils.generer_gridPane();

		buttonCalculer = new Button("Calculer");
		buttonCalculer.setOnAction(event -> {
			try {
				if (!(cbPcb.getValue().length() == 0)
						&& !(cbProfilDeVie.getValue().length() == 0)
						&& !(tfNombreAnnees.getText().length() == 0)) {
					initLineChart();
					refreshTableView();

				}
			}

			catch (NullPointerException e) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Une erreur s'est produite");
				alert.setHeaderText(null);
				alert.setContentText(
						"Verifiez que tous les champs soient remplis");

				alert.showAndWait();
			}

		});

		tfNombreAnnees = Outils.generer_textfield("");
		tfNombreAnnees.textProperty()
				.addListener((observable, oldValue, newValue) -> {
					try {
						nbMois = Integer.parseInt(newValue) * 12;
						labelMois.setText(nombreDeMois + nbMois);

					} catch (NumberFormatException | NullPointerException e) {
						Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("Une erreur s'est produite");
						alert.setHeaderText(null);
						alert.setContentText(
								"La valeur rentrée n'est pas un chiffre");

						alert.showAndWait();
						logger.warning(
								"La valeur rentrée n'est pas un chiffre");
					}
				});
		labelMois = new Label(nombreDeMois);

		cbProfilDeVie = new ComboBox<>(
				FXCollections.observableArrayList(
						Outils.get_list_filename_with_extension(
								Ressources.pathProfils)));
		cbProfilDeVie.setMaxWidth(160);
		cbProfilDeVie.valueProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(
					ObservableValue<? extends String> arg0,
					String arg1,
					String arg2) {
				if (arg2 != null) {
					cheminProfilDeVie = Ressources.pathProfils + arg2;
					nomProfilDeVie = arg2;
				}

			}
		});
		titreParametreProfil = Outils
				.generer_titledPane("Paramètre profil de vie");
		titreParametreProfil.setContent(gpTitreParametresProfils);

		cycle = new CycleThermique(0, 0, 0, 0);

		temperature1 = Outils.generer_textfield("");
		temperature1.textProperty()
				.addListener((observable, oldValue, newValue) -> {
					try {
						cycle.setTemperature1(Double.valueOf(newValue));
					} catch (NumberFormatException nfe) {
						cycle.setTemperature1(0);
					}
				});
		temperature1.setText("125");

		temperature2 = Outils.generer_textfield("");
		temperature2.textProperty()
				.addListener((observable, oldValue, newValue) -> {
					try {
						cycle.setTemperature2(Double.valueOf(newValue));
					} catch (NumberFormatException nfe) {
						cycle.setTemperature2(0);
					}
				});
		temperature2.setText("-55");
		temps1 = Outils.generer_textfield("");
		temps1.textProperty().addListener((observable, oldValue, newValue) -> {
			try {
				cycle.setDuree1(Double.valueOf(newValue));
			} catch (NumberFormatException nfe) {
				cycle.setDuree1(0);
			}
		});

		temps1.setText("30");
		temps2 = Outils.generer_textfield("");
		temps2.textProperty().addListener((observable, oldValue, newValue) -> {
			try {
				cycle.setDuree2(Double.valueOf(newValue));
			} catch (NumberFormatException nfe) {
				cycle.setDuree2(0);
			}
		});
		temps2.setText("30");

		cycle.setTemperature1(Double.valueOf(temperature1.getText()));
		cycle.setDuree1(Double.valueOf(temps1.getText()));
		cycle.setTemperature2(Double.valueOf(temperature2.getText()));
		cycle.setDuree2(Double.valueOf(temps2.getText()));

		gpT1 = Outils.generer_textfield_avec_label(
				temperature1,
				"Température T1 (°C)");
		gpD1 = Outils.generer_textfield_avec_label(temps1, "Temps t1 (min)");
		gpT2 = Outils.generer_textfield_avec_label(
				temperature2,
				"Temperature T2 (°C)");
		gpD2 = Outils.generer_textfield_avec_label(temps2, "Temps t2 (min)");

		gpTitreParametresProfils.add(gpT1, 0, 0);
		gpTitreParametresProfils.add(gpD1, 1, 0);
		gpTitreParametresProfils.add(gpT2, 0, 1);
		gpTitreParametresProfils.add(gpD2, 1, 1);
		gpTitreParametresProfils
				.add(new Label("Choix du profil de vie:"), 0, 3);
		gpTitreParametresProfils.add(cbProfilDeVie, 1, 3);
		gpTitreParametresProfils.add(new Label("Nombre d'années: "), 0, 4);
		gpTitreParametresProfils.add(tfNombreAnnees, 1, 4);
		gpTitreParametresProfils.add(labelMois, 0, 5);
		gpTitreParametresProfils.add(buttonCalculer, 0, 6);

	}

	private void initCarteExistante() {

		cbCarte = new ComboBox<String>(
				FXCollections.observableArrayList(
						Outils.get_list_filename_with_extension(Ressources.pathCartes)));
		cbCarte.getSelectionModel().selectedItemProperty()
				.addListener(new ChangeListener<String>() {

					@Override
					public void changed(
							ObservableValue<? extends String> arg0,
							String arg1,
							String arg2) {
						System.out.println("[Fuck] arg2: " + arg2);
						chargerCarte(arg2);
					}
				});
		gpCarteExistante = Outils.generer_gridPane();
		gpCarteExistante.add(new Label("Carte:"), 0, 0);
		gpCarteExistante.add(cbCarte, 1, 0);
		titreCarteExistante = Outils.generer_titledPane("Carte existante");
		VbCarteExistanteEtConfigurationCarte = new VBox(10);
		VbCarteExistanteEtConfigurationCarte.getChildren()
				.addAll(titreCarteExistante, titreConfigurationCarte);
		titreCarteExistante.setContent(gpCarteExistante);
	}

	private void chargerCarte(String nomCarte) {
		composantsSurCarte = new HashMap<String, Integer>();

		System.out.println(nomCarte);
		System.out.println("Frank: " + Ressources.pathCartes + nomCarte);

		try (BufferedReader br = Files.newBufferedReader(
				Paths.get(Ressources.pathCartes + nomCarte))) {

			String line;
			int i = 0;
			while ((line = br.readLine()) != null) {
				System.out.println("Fiona: " + line);
				String[] mots = line.split(";");
				if (i == 0) {
					System.out.println(i + ": cbPcb.getSelectionModel().select(mots[0]) " +  mots[0]);
					cbPcb.getSelectionModel().select(mots[0]);
				} else if (i == 1) {
					System.out.println(i + ": cbProfilDeVie.getSelectionModel().select(mots[0]) " +  mots[0]);
					cbProfilDeVie.getSelectionModel().select(mots[0]);
				} else if (i == 2) {
					System.out.println(i + ": tfNombreAnnees.setText(String.valueOf(mots[0])) " +  mots[0]);
					tfNombreAnnees.setText(String.valueOf(mots[0]));
				} else {
					System.out.println(i + ": composantsSurCarte.put " +  mots[0] + " : " + mots[1]);
					composantsSurCarte.put(mots[0], Integer.parseInt(mots[1]));
				}
				i++;
			}
			initLineChart();
			refreshTableView();

		} catch (IOException e) {
			logger.info("Problème de chargement des données");
		}
	}

	private void initConfigurationCarte() {

		buttonRafraichir = new Button();

		Image imageMiseAJour = new Image(getClass().getResourceAsStream("images/icons8-mises-à-jour-disponibles-24.png"));
		ImageView imageViewPcb = new ImageView(imageMiseAJour);
		buttonRafraichir.setGraphic(imageViewPcb);

		buttonRafraichir.setOnAction(event -> {
			chargerCarte(cbCarte.getValue());
		});

		composantsSurCarte = new HashMap<>();
		hbParametrageCarte = new HBox(20);

		initTableView();

		cheminProfilDeVie = "";

		cbPcb = new ComboBox<String>(
				FXCollections.observableArrayList(
						Outils.get_list_filename_with_extension(
								Ressources.pathPCB)));
		cbPcb.setMaxWidth(160);
		cbPcb.valueProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(
					ObservableValue<? extends String> arg0,
					String arg1,
					String arg2) {
				if (arg2 != null) {
					cheminPCB = arg2;
				}

			}
		});
		cbVernisPulverise = new CheckBox();
		cbVernisPulverise.setOnAction(event -> {
			vernis = cbVernisPulverise.isSelected();
		});

		buttonGenererCarte = new Button("Construire la carte");
		buttonGenererCarte.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				ChoixComposant choixComposants = new ChoixComposant(
						composantsSurCarte,
						OngletCarte.this);
			}

		});
		buttonSauvegarderCarte = new Button("Sauvegarder");
		buttonSauvegarderCarte.setOnAction(event -> {

			File file = fileChooser.showSaveDialog(getScene().getWindow());

			try (FileOutputStream fos = new FileOutputStream(file);
					ObjectOutputStream oos = new ObjectOutputStream(fos)) {

				String nomPcb = "";

				if (!cbPcb.getValue().isEmpty()
						&& !cbProfilDeVie.getValue().isEmpty()) {
					nomPcb = cbPcb.getValue() + "\n";
				}

				else {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Une erreur s'est produite");
					alert.setHeaderText(null);
					alert.setContentText("");

					alert.showAndWait();
				}

				StringBuilder stringToSave = new StringBuilder(nomPcb);
				stringToSave.append(nomProfilDeVie + "\n");
				stringToSave.append(nbMois);
				for (Map.Entry composant : composantsSurCarte.entrySet()) {
					stringToSave.append("\n");
					stringToSave.append(
							composant.getKey() + ";" + composant.getValue());
				}
				Outils.sauvegarder_objet(stringToSave.toString(), file);

			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		});

		gpConfigurationeCarte = Outils.generer_gridPane();
		tfNombreArticle = Outils.generer_textfield("");
		gpConfigurationeCarte.add(new Label("Choix PCB:"), 0, 0);
		gpConfigurationeCarte.add(cbPcb, 1, 0);

		gpConfigurationeCarte.add(new Label("Vernis pulvérisé "), 0, 2);
		gpConfigurationeCarte.add(cbVernisPulverise, 1, 2);

		gpConfigurationeCarte.add(buttonGenererCarte, 0, 3);
		gpConfigurationeCarte.add(buttonSauvegarderCarte, 1, 3);

		titreConfigurationCarte = Outils
				.generer_titledPane("Configuration carte");
		titreConfigurationCarte.setContent(gpConfigurationeCarte);

	}

	protected void refreshTableView() {
		tvListeComposantSurCarte.setItems(
				FXCollections.observableArrayList(carte.getComposantsCarte()));

	}

	public void initTableView() {
		tvListeComposantSurCarte = new TableView<SetupComposantCarte>();
		tvListeComposantSurCarte.setEditable(true);

		TableColumn<
				SetupComposantCarte,
				String> colonneNomComposant = new TableColumn<
						SetupComposantCarte,
						String>("Composants");
		colonneNomComposant.setCellValueFactory(
				c -> new SimpleStringProperty(c.getValue().getNomComposant()));

		TableColumn<
				SetupComposantCarte,
				Number> colonneNombreDeComposant = new TableColumn<
						SetupComposantCarte,
						Number>("Nombre");
		colonneNombreDeComposant.setEditable(true);
		colonneNombreDeComposant.setCellFactory(
				TextFieldTableCell.<SetupComposantCarte, Number>forTableColumn(
						new NumberStringConverter()));
		colonneNombreDeComposant.setCellValueFactory(
				new PropertyValueFactory("nombreComposant"));
		colonneNombreDeComposant.setOnEditCommit(
				(CellEditEvent<SetupComposantCarte, Number> event) -> {
					TablePosition<
							SetupComposantCarte,
							Number> position = event.getTablePosition();

					int nouvelleValeur = event.getNewValue().intValue();
					int row = position.getRow();
					SetupComposantCarte composantSurCarte = event.getTableView()
							.getItems().get(row);
					composantSurCarte.setNombreComposant(nouvelleValeur);
					composantsSurCarte.put(
							composantSurCarte.getNomComposant(),
							nouvelleValeur);
					carte.calculsProbabiliteEtDensite(
							cheminProfilDeVie,
							cheminPCB,
							composantsSurCarte);
					carte.refreshLineChart();

				});

		TableColumn<
				SetupComposantCarte,
				String> colonneBeta = new TableColumn<
						SetupComposantCarte,
						String>("Beta");
		colonneBeta.setCellValueFactory(new PropertyValueFactory("beta"));

		TableColumn<
				SetupComposantCarte,
				Number> colonneN63 = new TableColumn<
						SetupComposantCarte,
						Number>("N63.2%");
		colonneN63.setCellValueFactory(
				c -> new SimpleDoubleProperty(
						Outils.nombreChiffreApresVirgule(
								c.getValue().getN63(),
								Ressources.NOMBRE_CHIFFRE_APRES_VIRGULE)));

		TableColumn<
				SetupComposantCarte,
				Number> colonneD63 = new TableColumn<
						SetupComposantCarte,
						Number>("D63%");
		colonneD63.setCellValueFactory(
				c -> new SimpleDoubleProperty(
						Outils.nombreChiffreApresVirgule(
								c.getValue().getD63(),
								Ressources.NOMBRE_CHIFFRE_APRES_VIRGULE)));

		TableColumn<
				SetupComposantCarte,
				Number> colonneMu = new TableColumn<
						SetupComposantCarte,
						Number>("Mu (mois)");
		colonneMu.setCellValueFactory(
				c -> new SimpleIntegerProperty((int) c.getValue().getMu()));

		tvListeComposantSurCarte
				.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		tvListeComposantSurCarte.getColumns().addAll(
				colonneNomComposant,
				colonneNombreDeComposant,
				colonneBeta,
				colonneN63,
				colonneD63,
				colonneMu);
		for (TableColumn<SetupComposantCarte, ?> res : tvListeComposantSurCarte
				.getColumns()) {
			res.setStyle("-fx-alignment: CENTER;");
		}
	}

	private void addComposantDansInterface() {
		hbParametrageCarte.getChildren().addAll(
				VbCarteExistanteEtConfigurationCarte,
				titreParametreProfil,
				image);

		getChildren().addAll(hbParametrageCarte, tvListeComposantSurCarte);

	}

	public void exportToCsv() {
		StringBuilder stringToConcat = new StringBuilder();
		StringBuilder stringFinal = new StringBuilder();

		// Remplissage d'une colonne pour un composant avec les N x différents s
		for (Map.Entry<String, Integer> entry : composantsSurCarte.entrySet()) {
			stringToConcat.append(entry.getKey() + ";");
			stringToConcat.append("\n");
			stringToConcat.append(entry.getValue() + ";");

			for (SetupComposantCarte composantSurCarte : carte
					.getComposantsCarte()) {
				for (double pourcentage : Ressources.POURCENTAGE_DEFAILLANCE) {
					stringToConcat.append("\n");

					double sommeProbabiliteDeDefaillance = composantSurCarte
							.getProbabiliteDeDefaillance(1, pourcentage);

					System.out.println(
							"Probabilite de defaillance: "
									+ (sommeProbabiliteDeDefaillance));
					stringToConcat.append(sommeProbabiliteDeDefaillance + ";");
				}

			}
		}

	}

	/**
	 * Rafraichit la ComboBox des Pcbs disponibles
	 */
	public void rafraichir_cbPcb() {
		this.cbPcb.getItems().clear();
		this.cbPcb.setItems(
				FXCollections.observableArrayList(
						Outils.get_list_filename_with_extension(
								Ressources.pathPCB)));
	}

}
