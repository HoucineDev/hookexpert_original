package com.app.ancea;

import java.io.File;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Screen;

public class OngletBrasure extends VBox {

	// classe représentant l'onglet de calculs des brasures

	private final String path = Ressources.pathComposants;

	private OngletPcb pcb;

	VBox onglet_brasure;
	private VBox vbox_composant_existant;
	// Brasure brasure;
	Button printButton;
	Button effacerButton;

	private BrasureComposant brasure;
	private Alert alert;

	private TitledPane titre_composant_existant;
	private TitledPane titre_parametres_brasage;
	private TitledPane titre_donnee_geometrique,
			titre_donnee_geometrique_optimisee,
			titre_tenue_en_fatigue_et_duree_de_vie;
	private TitledPane titre_resusltat;

	private ComboBox<String> cb_composant_existant, cb_categorie_composant,
			cb_type_composant;
	private ComboBox<String> cb_format_resistances;
	private ComboBox<String> cb_technologie_brasage;
	private ComboBox<String> cb_materiau;

	private ComboBox<String> cbBrasuresExistantes;
	private Label labBrasuresExistantes;
	private HBox hboxBrasuresExistantes;
	private GridPane gpBrasureExistante;
	private TitledPane titre_brasures_existantes;
	private ComboBox<String> cbTypeComposantBrasure;
	private ComboBox<String> cbFamilleComposantBrasure;

	private TextField tfValeurCapacite;

	private HBox hb_parametres_brasage, hb_calculer_sauvegarder;
	private HBox hb_resultats_brasage;

	private TableView<Propriete> tv_donnee_geometrique,
			tv_donnee_geometrique_optimisee, tv_tenue_fatigue;
	private TableView<Propriete> tv_resultat;

	private Composant composant;

	private GridPane gp_titre_composant_existant;
	private GridPane gp_parametre_brasure, gp_resultat_db1_dbille;

	private TextField a1, b1, c1;
	private TextField as, bs, es;
	private TextField am, bm, hm;
	private GridPane gp_a1, gp_b1, gp_c1;
	private GridPane gp_as, gp_bs, gp_es;
	private GridPane gp_am, gp_bm, gp_hm;

	private Label d1, dbille;

	private FileChooser fileChooser;

	private TextField h1, db1;
	private TextField d2, dEs;
	private GridPane gp_h1, gp_db1;
	private GridPane gp_d2, gp_dEs;

	private Button calculer;
	private Button sauvegarder;
	private String technologie;

	private Propriete blank;

	/**
	 * Interface graphique de l'onglet Brasure
	 * 
	 * @param spacing Espace entre les differents objets
	 */
	public OngletBrasure(int spacing) {

		super(spacing);

		onglet_brasure = new VBox();

		init_hbox();

		fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(new File(Ressources.pathBrasures));

		init_alert();
		init_composant_existant();
		init_brasures_existantes();

		hb_parametres_brasage.getChildren().addAll(titre_composant_existant);

		technologie = "NSMD";

		getChildren().addAll(
				hb_parametres_brasage,
				this.hboxBrasuresExistantes,
				hb_resultats_brasage);

	}

	/**
	 * Toggles visibility of the 'Hi' column in the tv_billes table
	 * based on admin status.
	 */
	public void setAdminMode(boolean isAdmin) {
		// The table variable is tv_billes, not phbille_brasee
		if (tv_resultat != null) {
			for (TableColumn<Propriete, ?> col : tv_resultat.getColumns()) {
				if ("h_i (mm)".equals(col.getText())) {
					col.setVisible(isAdmin); // Hide for User, Show for Admin
				}
			}
		}
	}

	/**
	 * Initialise les resultats des composants.
	 */
	@SuppressWarnings("unchecked")
	private void init_brasure_composant_leadless() {

		if (cb_type_composant.getValue().equals("RESISTANCES")) {
			brasure = new BrasureResistance();
		} else if (cb_type_composant.getValue().equals("LCCC")) {
			brasure = new BrasureLCC();
		} else if (cb_type_composant.getValue().equals("CAPACITES")) {
			brasure = new BrasureCapacite();
		} else if (cb_type_composant.getValue().equals("QFN")) {
			brasure = new BrasureQFN();
		}

		ObservableList<Propriete> donnee_brasure_resistance = FXCollections
				.observableArrayList();

		if (cb_categorie_composant.getValue().equals("COMPOSANT LEADFRAME")) {
			donnee_brasure_resistance = FXCollections.observableArrayList(
					((BrasureComposantLeadframe) brasure).heq,
					((BrasureComposantLeadframe) brasure).Sc,
					((BrasureComposantLeadframe) brasure).Hi);
		}
		if (cb_categorie_composant.getValue().equals("COMPOSANT LEADLESS")) {
			donnee_brasure_resistance = FXCollections.observableArrayList(
					((BrasureComposantLeadless) brasure).heq,
					((BrasureComposantLeadless) brasure).Sc,
					((BrasureComposantLeadless) brasure).Hi);
		}

		if (cb_type_composant.getValue().equals("LCCC")) {
			donnee_brasure_resistance
					.add(((BrasureComposantLeadless) brasure).gainSmdNsmd);
		}

		tv_resultat = new TableView<Propriete>();
		tv_resultat.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		TableColumn<
				Propriete,
				String> tc_cotations = new TableColumn<Propriete, String>(
						"Cotations");
		tc_cotations.setCellValueFactory(c -> c.getValue().get_nom_variable());

		TableColumn<
				Propriete,
				String> tc_resultats = new TableColumn<Propriete, String>(
						"Resultats");
		tc_resultats.setCellValueFactory(c -> c.getValue().get_resultat());

		tv_resultat.getColumns().addAll(tc_cotations, tc_resultats);
		tv_resultat.setItems(donnee_brasure_resistance);

		titre_resusltat = new TitledPane();
		titre_resusltat.setText("Resultat Brasure");
		titre_resusltat.setContent(tv_resultat);

	}

	private void init_titre_parametre() {

		gp_parametre_brasure = new GridPane();
		gp_parametre_brasure.setPadding(new Insets(20, 20, 20, 20));
		gp_parametre_brasure.setVgap(5);
		gp_parametre_brasure.setHgap(15);

		titre_parametres_brasage = new TitledPane();
		titre_parametres_brasage.setCollapsible(false);
		titre_parametres_brasage.setText("Parametres de brasage");
		titre_parametres_brasage.setContent(gp_parametre_brasure);

		calculer = new Button("Calculer");
		sauvegarder = new Button("Sauvegarder");

		hb_calculer_sauvegarder.getChildren().clear();
		hb_calculer_sauvegarder.getChildren().addAll(calculer, sauvegarder);

		Image image = new Image(
				getClass().getResourceAsStream(
						"images/icons8-sauvegarder-16.png"));
		sauvegarder.setGraphic(new ImageView(image));

		GridPane.setHalignment(calculer, HPos.LEFT);
		GridPane.setHalignment(sauvegarder, HPos.RIGHT);
	}

	private void init_param_brasure_leadless_et_leadfram() {

		init_titre_parametre();

		as = Outils.generer_textfield("");
		bs = Outils.generer_textfield("");
		es = Outils.generer_textfield("");

		gp_as = Outils.generer_textfield_avec_label(as, "as (mm)");
		gp_bs = Outils.generer_textfield_avec_label(bs, "bs (mm)");
		gp_es = Outils.generer_textfield_avec_label(es, "es (mm)");

		a1 = Outils.generer_textfield("");
		b1 = Outils.generer_textfield("");
		c1 = Outils.generer_textfield("");

		gp_a1 = Outils.generer_textfield_avec_label(a1, "a1 (mm)");
		gp_b1 = // cb_type_composant.getValue().equals("LCCC") ||
				// cb_type_composant.getValue().equals("QFN")
				// ? Outils.generer_textfield_avec_label(b1, "ecu (mm)")
				// : Outils.generer_textfield_avec_label(b1, "b1 (mm)");
				Outils.generer_textfield_avec_label(b1, "b1 (mm)");
		gp_c1 = cb_type_composant.getValue().equals("LCCC")
				|| cb_type_composant.getValue().equals("QFN")
						? Outils.generer_textfield_avec_label(c1, "ecu (mm)")
						: Outils.generer_textfield_avec_label(c1, "c1 (mm)");
		// Outils.generer_textfield_avec_label(c1, "c1 (mm)");

		// colonne, ligne
		gp_parametre_brasure
				.add(Outils.generer_label("Ecran serigraphie"), 0, 0);
		gp_parametre_brasure.add(gp_as, 0, 1);
		gp_parametre_brasure.add(gp_bs, 0, 2);
		gp_parametre_brasure.add(gp_es, 0, 3);

		gp_parametre_brasure
				.add(Outils.generer_label("Plage de brasage"), 1, 0);
		gp_parametre_brasure.add(gp_a1, 1, 1);

		/*
		 * if (cb_type_composant.getValue().equals("LCCC")) { am =
		 * Outils.generer_textfield(""); bm = Outils.generer_textfield(""); hm =
		 * Outils.generer_textfield("");
		 * 
		 * gp_am = Outils.generer_textfield_avec_label(am, "am (mm)"); gp_bm =
		 * Outils.generer_textfield_avec_label(bm, "bm (mm)"); gp_hm =
		 * Outils.generer_textfield_avec_label(hm, "hm (mm)");
		 * gp_parametre_brasure .add(Outils.generer_label("Métalisation"), 2,
		 * 0); gp_parametre_brasure.add(gp_am, 2, 1);
		 * gp_parametre_brasure.add(gp_bm, 2, 2);
		 * gp_parametre_brasure.add(gp_hm, 2, 3); }
		 */
		if (cb_type_composant.getValue().equals("LCCC") || cb_type_composant.getValue().equals("QFN")) {
			gp_parametre_brasure.add(gp_b1, 1, 2);
			gp_parametre_brasure.add(gp_c1, 1, 3);
		} else {
			gp_parametre_brasure.add(gp_b1, 1, 2);
			gp_parametre_brasure.add(gp_c1, 1, 3);
		}

		gp_parametre_brasure.add(calculer, 0, 4);
		gp_parametre_brasure.add(sauvegarder, 1, 4);

		sauvegarder.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				fileChooser.getExtensionFilters().clear();
				if (cb_type_composant.getValue().equals("RESISTANCES")) {
					fileChooser.getExtensionFilters().add(
							new ExtensionFilter(
									"Brasure Resistance (*.brres)",
									"*.brres"));
				} else if (cb_type_composant.getValue().equals("LCCC")) {
					fileChooser.getExtensionFilters().add(
							new ExtensionFilter(
									"Brasure LCCC (*.brlcc)",
									"*.brlcc"));
				} else if (cb_type_composant.getValue().equals("QFN")) {
					fileChooser.getExtensionFilters().add(
							new ExtensionFilter(
									"Brasure QFN (*.brqfn)",
									"*.brqfn"));
				} else if (cb_type_composant.getValue().equals("CAPACITES")) {
					try {

						double valeur = Double
								.parseDouble(tfValeurCapacite.getText());

					} catch (NumberFormatException e) {
						tfValeurCapacite.setStyle("-fx-border-color: red;");
						Outils.alerteUtilisateur(
								"ERROR",
								"Problème Valeur Capacité",
								"Valeur non valide");
						return;
					}

					tfValeurCapacite.setStyle("-fx-border-color: default;");
					fileChooser.getExtensionFilters().add(
							new ExtensionFilter(
									"Brasure Capacite (*.brcap)",
									"*.brcap"));

				}

				File file = fileChooser.showSaveDialog(getScene().getWindow());

				if (file != null) {
					try {
						if (cb_categorie_composant.getValue()
								.equals("COMPOSANT LEADLESS")) {
							if (!cb_type_composant.getValue().equals("LCCC")) {
								((BrasureComposantLeadless) brasure).setFormat(
										cb_format_resistances.getValue());
							} else {
								((BrasureComposantLeadless) brasure).setFormat(
										cb_composant_existant.getValue());
							}
							((BrasureComposantLeadless) brasure)
									.setA1(Double.valueOf(a1.getText()));
							((BrasureComposantLeadless) brasure)
									.setB1(Double.valueOf(b1.getText()));
							if (cb_type_composant.getValue() == "LCCC") {
								// En attendant de décider si on garde epaisseur
								// cuivre
								((BrasureComposantLeadless) brasure)
										.setC1(Double.valueOf(0.0));

							} else {

								((BrasureComposantLeadless) brasure)
										.setC1(Double.valueOf(c1.getText()));
							}
							((BrasureComposantLeadless) brasure)
									.setAs(Double.valueOf(as.getText()));
							((BrasureComposantLeadless) brasure)
									.setBs(Double.valueOf(bs.getText()));
							((BrasureComposantLeadless) brasure)
									.setEs(Double.valueOf(es.getText()));

							if (cb_type_composant.getValue()
									.equals("CAPACITES")) {
								((BrasureCapacite) brasure)
										.setMateriau(cb_materiau.getValue());
								((BrasureCapacite) brasure).setvaleurCapacite(
										Double.valueOf(
												tfValeurCapacite.getText()));
								Outils.sauvegarder_objet(
										((BrasureCapacite) brasure).toString(),
										file);
							} else if (cb_type_composant.getValue()
									.equals("LCCC")) {
								((BrasureLCC) brasure).setTechnologie(
										cb_technologie_brasage.getValue());
								Outils.sauvegarder_objet(
										((BrasureLCC) brasure).toString(),
										file);
							} else {

								Outils.sauvegarder_objet(
										((BrasureComposantLeadless) brasure)
												.toString(),
										file);
							}
						}

						else {
							((BrasureComposantLeadframe) brasure).setFormat(
									cb_format_resistances.getValue());

							((BrasureComposantLeadframe) brasure)
									.setA1(Double.valueOf(a1.getText()));
							((BrasureComposantLeadframe) brasure)
									.setB1(Double.valueOf(b1.getText()));
							// ((BrasureComposantLeadframe) brasure)
							// .setC1(Double.valueOf(c1.getText()));

							if (cb_type_composant.getValue() == "QFN") {
								// En attendant de décider si on garde epaisseur
								// cuivre
								((BrasureComposantLeadframe) brasure)
										.setC1(Double.valueOf(0.0));
							} else {

								((BrasureComposantLeadless) brasure)
										.setC1(Double.valueOf(c1.getText()));
							}

							((BrasureComposantLeadframe) brasure)
									.setAs(Double.valueOf(as.getText()));
							((BrasureComposantLeadframe) brasure)
									.setBs(Double.valueOf(bs.getText()));
							((BrasureComposantLeadframe) brasure)
									.setEs(Double.valueOf(es.getText()));

							if (cb_type_composant.getValue().equals("QFN")) {
								((BrasureQFN) brasure)
										.setTechnologie(technologie);
							}

							Outils.sauvegarder_objet(
									((BrasureComposantLeadframe) brasure)
											.toString(),
									file);

						}
					} catch (NumberFormatException e) {
						alert.showAndWait();
						e.printStackTrace();
					}
				}
			}
		});

		calculer.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				try {
					if (cb_categorie_composant.getValue()
							.equals("COMPOSANT LEADLESS")) {

						((BrasureComposantLeadless) brasure)
								.setA1(Double.valueOf(a1.getText()));
						((BrasureComposantLeadless) brasure)
								.setB1(Double.valueOf(b1.getText()));
						if (cb_type_composant.getValue() == "LCCC") {
							// En attendant de décider si on garde epaisseur
							// cuivre
							((BrasureComposantLeadless) brasure)
									.setC1(Double.valueOf(0.0));

						} else {

							((BrasureComposantLeadless) brasure)
									.setC1(Double.valueOf(c1.getText()));
						}

						((BrasureComposantLeadless) brasure)
								.setAs(Double.valueOf(as.getText()));
						((BrasureComposantLeadless) brasure)
								.setBs(Double.valueOf(bs.getText()));
						((BrasureComposantLeadless) brasure)
								.setEs(Double.valueOf(es.getText()));

					}

					else {
						((BrasureComposantLeadframe) brasure)
								.setA1(Double.valueOf(a1.getText()));
						((BrasureComposantLeadframe) brasure)
								.setB1(Double.valueOf(b1.getText()));
						// ((BrasureComposantLeadframe) brasure)
						// .setC1(Double.valueOf(c1.getText()));

						if (cb_type_composant.getValue() == "QFN") {
							// En attendant de décider si on garde epaisseur
							// cuivre
							((BrasureComposantLeadframe) brasure)
									.setC1(Double.valueOf(0.0));
						} else {

							((BrasureComposantLeadless) brasure)
									.setC1(Double.valueOf(c1.getText()));
						}

						((BrasureComposantLeadframe) brasure)
								.setAs(Double.valueOf(as.getText()));
						((BrasureComposantLeadframe) brasure)
								.setBs(Double.valueOf(bs.getText()));
						((BrasureComposantLeadframe) brasure)
								.setEs(Double.valueOf(es.getText()));
					}

					if (cb_type_composant.getValue().equals("RESISTANCES")) {
						((BrasureResistance) brasure).setCalculs();
					}
					if (cb_type_composant.getValue().equals("LCCC")) {
						technologie = cb_technologie_brasage.getValue();
						((BrasureLCC) brasure).setTechnologie(technologie);
						((BrasureLCC) brasure)
								.setFormat(cb_composant_existant.getValue());
						((BrasureLCC) brasure).setCalculs();

					}
					if (cb_type_composant.getValue().equals("QFN")) {
						((BrasureQFN) brasure).setTechnologie(technologie);
						((BrasureQFN) brasure).setCalculs();

					}
					if (cb_type_composant.getValue().equals("CAPACITES")) {
						((BrasureCapacite) brasure).setCalculs();
					}
				} catch (NumberFormatException e) {
					alert.showAndWait();
					e.printStackTrace();
				}
			}
		});
	}

	private void init_brasures_existantes() {

		this.cbTypeComposantBrasure = new ComboBox<String>(
				FXCollections.observableArrayList(
						"COMPOSANT A BILLES",
						"COMPOSANT LEADLESS",
						"COMPOSANT LEADFRAME"));

		this.cbFamilleComposantBrasure = new ComboBox<String>();
		this.cbFamilleComposantBrasure.setVisible(false);

		this.cbBrasuresExistantes = new ComboBox<String>();
		this.cbBrasuresExistantes.setVisible(false);

		this.gpBrasureExistante = Outils.generer_gridPane();

		this.labBrasuresExistantes = new Label();
		this.labBrasuresExistantes.setVisible(false);

		this.cbTypeComposantBrasure.valueProperty()
				.addListener(new ChangeListener<String>() {

					@Override
					public void changed(
							ObservableValue<? extends String> arg0,
							String arg1,
							String arg2) {

						clearBrasuresExistantes(2);
						hb_resultats_brasage.getChildren().clear();
						cbBrasuresExistantes.getItems().clear();

						if (arg2.contains("COMPOSANT A BILLES")) {

							cbFamilleComposantBrasure.setItems(
									FXCollections.observableArrayList(
											"BGA",
											"CSP",
											"WLP",
											"SBGA",
											"CBGA",
											"PCBGA"));
							gpBrasureExistante
									.add(new Label("Type de composant"), 0, 1);
							gpBrasureExistante
									.add(cbFamilleComposantBrasure, 1, 1);

							cbFamilleComposantBrasure.setVisible(true);
							hb_resultats_brasage.getChildren().clear();
						} else if (arg2.contains("COMPOSANT LEADLESS")) {

							cbFamilleComposantBrasure.setItems(
									FXCollections.observableArrayList(
											"RESISTANCES",
											"CAPACITES",
											"LCCC"));
							gpBrasureExistante
									.add(new Label("Type de composant"), 0, 1);
							gpBrasureExistante
									.add(cbFamilleComposantBrasure, 1, 1);
							cbFamilleComposantBrasure.setVisible(true);
							hb_resultats_brasage.getChildren().clear();
						} else if (arg2.contains("COMPOSANT LEADFRAME")) {

							cbFamilleComposantBrasure.setItems(
									FXCollections.observableArrayList("QFN"));
							gpBrasureExistante
									.add(new Label("Type de composant"), 0, 1);
							gpBrasureExistante
									.add(cbFamilleComposantBrasure, 1, 1);
							cbFamilleComposantBrasure.setVisible(true);
							hb_resultats_brasage.getChildren().clear();
						} else {
							// clear_brasures_existants(2);
						}

					}
				});

		cbFamilleComposantBrasure.valueProperty().addListener(
				new ChangeListener<String>() { /* TODO: FAMILLE COMPOSANT */

					@Override
					public void changed(
							ObservableValue<? extends String> arg0,
							String arg1,
							String arg2) {
						clearBrasuresExistantes(4);

						cbBrasuresExistantes.getItems().clear();
						hb_resultats_brasage.getChildren().clear();
						if (arg2 != null) {

							if (arg2.equals("BGA")) {
								cbBrasuresExistantes.getItems().addAll(
										Outils.get_list_filename_directory(
												Ressources.pathBrasures,
												".brbga"));
							} else if (arg2.equals("PCBGA")) {
								cbBrasuresExistantes.getItems().addAll(
										Outils.get_list_filename_directory(
												Ressources.pathBrasures,
												".brpcbga"));
							} else if (arg2.equals("CSP")) {
								cbBrasuresExistantes.getItems().addAll(
										Outils.get_list_filename_directory(
												Ressources.pathBrasures,
												".brcsp"));
							} else if (arg2.equals("WLP")) {
								cbBrasuresExistantes.getItems().addAll(
										Outils.get_list_filename_directory(
												Ressources.pathBrasures,
												".brwlp"));
							} else if (arg2.equals("CBGA")) {
								cbBrasuresExistantes.getItems().addAll(
										Outils.get_list_filename_directory(
												Ressources.pathBrasures,
												".brcbga"));
							} else if (arg2.equals("SBGA")) {
								cbBrasuresExistantes.getItems().addAll(
										Outils.get_list_filename_directory(
												Ressources.pathBrasures,
												".brsbga"));
							} else if (arg2.equals("LCCC")) {
								cbBrasuresExistantes.getItems().addAll(
										Outils.get_list_filename_directory(
												Ressources.pathBrasures,
												".brlcc"));
							} else if (arg2.equals("CAPACITES")) {
								cbBrasuresExistantes.getItems().addAll(
										Outils.get_list_filename_directory(
												Ressources.pathBrasures,
												".brcap"));
							} else if (arg2.equals("RESISTANCES")) {
								cbBrasuresExistantes.getItems().addAll(
										Outils.get_list_filename_directory(
												Ressources.pathBrasures,
												".brres"));
							} else if (arg2.equals("QFN")) {
								cbBrasuresExistantes.getItems().addAll(
										Outils.get_list_filename_directory(
												Ressources.pathBrasures,
												".brqfn"));
							}

							gpBrasureExistante.add(new Label("Brasure"), 0, 2);
							gpBrasureExistante.add(cbBrasuresExistantes, 1, 2);
							cbBrasuresExistantes.setVisible(true);

						}
					}
				});

		cbBrasuresExistantes.valueProperty().addListener(
				new ChangeListener<String>() { /* TODO: FAMILLE COMPOSANT */

					@Override
					public void changed(
							ObservableValue<? extends String> arg0,
							String arg1,
							String arg2) {

						labBrasuresExistantes.setText("");
						labBrasuresExistantes.setVisible(false);
						String donnees = " Données Brasure:\n";

						if (arg2 != null) {
							if (cbTypeComposantBrasure.getValue() != null) {
								if (cbTypeComposantBrasure.getValue()
										.equals("COMPOSANT A BILLES")) {

									BrasureComposantBilles brCompBilles;

									switch (cbFamilleComposantBrasure
											.getValue()) {

									case "BGA":
										brCompBilles = new BrasureComposantBilles(
												Ressources.pathBrasures + arg2
														+ ".brbga");
										break;

									case "CSP":
										brCompBilles = new BrasureComposantBilles(
												Ressources.pathBrasures + arg2
														+ ".brcsp");
										break;
									case "SBGA":
										brCompBilles = new BrasureComposantBilles(
												Ressources.pathBrasures + arg2
														+ ".brsbga");
										break;
									case "WLP":
										brCompBilles = new BrasureComposantBilles(
												Ressources.pathBrasures + arg2
														+ ".brwlp");
										break;
									case "PCBGA":
										brCompBilles = new BrasureComposantBilles(
												Ressources.pathBrasures + arg2
														+ ".brpcbga");
										break;
									case "CBGA":
										brCompBilles = new BrasureComposantBilles(
												Ressources.pathBrasures + arg2
														+ ".brcbga");
										break;
									default:
										brCompBilles = null;
									}

									donnees += "\n Technologie : "
											+ brCompBilles.getTechnologie();
									donnees += "\n h1 : "
											+ brCompBilles.getH1();
									donnees += "\n db1 : "
											+ brCompBilles.getDb1();

									if (brCompBilles.getTechnologie()
											.equals("SMD")) {
										donnees += "\n d2 : "
												+ brCompBilles.getD2();
									}
									donnees += "\n es : "
											+ brCompBilles.getEs();
									donnees += "\n des : "
											+ brCompBilles.getdEs();

								} else if (cbTypeComposantBrasure.getValue()
										.equals("COMPOSANT LEADLESS")) {

									if (cbFamilleComposantBrasure
											.getValue() != null) {

										switch (cbFamilleComposantBrasure
												.getValue()) {

										case "CAPACITES":
											BrasureCapacite brcap = new BrasureCapacite(
													Ressources.pathBrasures
															+ arg2 + ".brcap");
											donnees += "\n Capacite (en pF) : "
													+ brcap.getValeurCapacite();
											donnees += "\n Matériau : "
													+ brcap.materiau_capacite;
											donnees += "\n Format : "
													+ brcap.getFormat();
											donnees += "\n as : "
													+ brcap.getAs();
											donnees += "\n bs : "
													+ brcap.getBs();
											donnees += "\n es : "
													+ brcap.getEs();
											donnees += "\n a1 : "
													+ brcap.getA1();
											donnees += "\n b1 : "
													+ brcap.getB1();
											donnees += "\n c1 : "
													+ brcap.getC1();
											break;

										case "RESISTANCES":
											BrasureResistance brres = new BrasureResistance(
													Ressources.pathBrasures
															+ arg2 + ".brres");
											donnees += "\n Format : "
													+ brres.getFormat();
											donnees += "\n as : "
													+ brres.getAs();
											donnees += "\n bs : "
													+ brres.getBs();
											donnees += "\n es : "
													+ brres.getEs();
											donnees += "\n a1 : "
													+ brres.getA1();
											donnees += "\n b1 : "
													+ brres.getB1();
											donnees += "\n c1 : "
													+ brres.getC1();
											break;
										case "LCCC":
											BrasureLCC brlcc = new BrasureLCC(
													Ressources.pathBrasures
															+ arg2 + ".brlcc");
											donnees += "\n Technologie : "
													+ brlcc.getTechnologie();
											donnees += "\n Format : "
													+ brlcc.getFormat();
											donnees += "\n as : "
													+ brlcc.getAs();
											donnees += "\n bs : "
													+ brlcc.getBs();
											donnees += "\n es : "
													+ brlcc.getEs();
											donnees += "\n a1 : "
													+ brlcc.getA1();
											donnees += "\n b1 : "
													+ brlcc.getB1();
											donnees += "\n am : "
													+ brlcc.composant.get("a");
											donnees += "\n bm : "
													+ brlcc.composant.get("b");
											donnees += "\n hm : "
													+ brlcc.composant.get("h");
											break;

										default:
											System.out.println("Error");
										}
									}
								} else if (cbTypeComposantBrasure.getValue()
										.equals("COMPOSANT LEADFRAME")) {
									BrasureQFN brqfn = new BrasureQFN(
											Ressources.pathBrasures + arg2
													+ ".brqfn");

									donnees += "\n Technologie : "
											+ brqfn.getTechnologie();
									donnees += "\n Format : "
											+ brqfn.getFormat();
									donnees += "\n as : " + brqfn.getAs();
									donnees += "\n bs : " + brqfn.getBs();
									donnees += "\n es : " + brqfn.getEs();
									donnees += "\n a1 : " + brqfn.getA1();
									donnees += "\n b1 : " + brqfn.getB1();

								}
								labBrasuresExistantes.setText(donnees);
								gpBrasureExistante.getChildren()
										.remove(labBrasuresExistantes);
								gpBrasureExistante
										.add(labBrasuresExistantes, 0, 3);
								labBrasuresExistantes.setVisible(true);
							}
						}
					}
				});

		this.titre_brasures_existantes = new TitledPane();
		this.titre_brasures_existantes.setCollapsible(false);
		// this.titre_brasures_existantes.setPadding(new Insets(0, 0, 0, 20));
		this.titre_brasures_existantes.setText("Brasure Existante");
		this.titre_brasures_existantes.setContent(gpBrasureExistante);

		gpBrasureExistante.add(new Label("Catégorie Composant"), 0, 0);
		gpBrasureExistante.add(cbTypeComposantBrasure, 1, 0);

		this.hboxBrasuresExistantes.getChildren().clear();
		this.hboxBrasuresExistantes.getChildren()
				.add(this.titre_brasures_existantes);
		// this.labBrasuresExistantes = new Label("Brasures Existantes");
		// this.cbBrasuresExistantes = new ComboBox<String>();
		// new AutoCompleteComboBoxListener<>(this.cbBrasuresExistantes);
		// this.hboxBrasuresExistantes = new HBox();
		// this.hboxBrasuresExistantes.getChildren()
		// .addAll(this.labBrasuresExistantes, this.cbBrasuresExistantes);

	}

	/**
	 * Initialisation du TitledPane des composants existants.
	 */
	private void init_composant_existant() {

		cb_categorie_composant = new ComboBox<String>(
				FXCollections.observableArrayList(
						"COMPOSANT A BILLES",
						"COMPOSANT LEADLESS",
						"COMPOSANT LEADFRAME"));

		cb_materiau = new ComboBox<String>();
		cb_materiau.setVisible(false);

		tfValeurCapacite = new TextField();
		tfValeurCapacite.setVisible(false);

		cb_type_composant = new ComboBox<String>();
		cb_type_composant.setVisible(false);

		cb_composant_existant = new ComboBox<String>();
		cb_composant_existant.setVisible(false);

		cb_composant_existant.valueProperty()
				.addListener(new ChangeListener<String>() {

					@Override
					public void changed(
							ObservableValue<? extends String> arg0,
							String arg1,
							String arg2) {

						clear_composants_existants(8);
						if (arg2 != null) {
							if (arg2.contains(".bga") || arg2.contains(".csp")
									|| arg2.contains(".wlp")) {

							} else if (arg2.contains("RESISTANCES")
									|| arg2.contains("LCCC")
									|| arg2.contains("CAPACITES")) {

							}
						}
					}

				});

		cb_categorie_composant.valueProperty()
				.addListener(new ChangeListener<String>() {

					@Override
					public void changed(
							ObservableValue<? extends String> arg0,
							String arg1,
							String arg2) {
						clear_composants_existants(2);
						hb_resultats_brasage.getChildren().clear();

						if (arg2.contains("COMPOSANT A BILLES")) {

							cb_type_composant.setItems(
									FXCollections.observableArrayList(
											"BGA",
											"CSP",
											"WLP",
											"SBGA",
											"CBGA",
											"PCBGA"));
							gp_titre_composant_existant
									.add(new Label("Type de composant"), 0, 1);
							gp_titre_composant_existant
									.add(cb_type_composant, 1, 1);

							cb_type_composant.setVisible(true);
							hb_resultats_brasage.getChildren().clear();
						} else if (arg2.contains("COMPOSANT LEADLESS")) {

							cb_type_composant.setItems(
									FXCollections.observableArrayList(
											"RESISTANCES",
											"CAPACITES",
											"LCCC"));
							gp_titre_composant_existant
									.add(new Label("Type de composant"), 0, 1);
							gp_titre_composant_existant
									.add(cb_type_composant, 1, 1);
							cb_type_composant.setVisible(true);
							hb_resultats_brasage.getChildren().clear();
						} else if (arg2.contains("COMPOSANT LEADFRAME")) {

							cb_type_composant.setItems(
									FXCollections.observableArrayList("QFN"));
							gp_titre_composant_existant
									.add(new Label("Type de composant"), 0, 1);
							gp_titre_composant_existant
									.add(cb_type_composant, 1, 1);
							cb_type_composant.setVisible(true);
							hb_resultats_brasage.getChildren().clear();
						} else {
							clear_composants_existants(2);
						}

					}
				});

		cb_type_composant.valueProperty().addListener(
				new ChangeListener<String>() { /* TODO: FAMILLE COMPOSANT */

					@Override
					public void changed(
							ObservableValue<? extends String> arg0,
							String arg1,
							String arg2) {
						clear_composants_existants(4);

						cb_composant_existant.getItems().clear();
						hb_resultats_brasage.getChildren().clear();
						if (arg2 != null) {
							if (arg2.equals("BGA") || arg2.equals("CSP")
									|| arg2.equals("WLP") || arg2.equals("SBGA")
									|| arg2.equals("CBGA")
									|| arg2.equals("PCBGA")) {
								if (arg2.equals("BGA")) {
									cb_composant_existant.getItems().addAll(
											Outils.get_list_filename_directory(
													path,
													".bga"));
								} else if (arg2.equals("PCBGA")) {
									cb_composant_existant.getItems().addAll(
											Outils.get_list_filename_directory(
													path,
													".pcbga"));
								} else if (arg2.equals("CSP")) {
									cb_composant_existant.getItems().addAll(
											Outils.get_list_filename_directory(
													path,
													".csp"));
								} else if (arg2.equals("WLP")) {
									cb_composant_existant.getItems().addAll(
											Outils.get_list_filename_directory(
													path,
													".wlp"));
								} else if (arg2.equals("CBGA")) {
									cb_composant_existant.getItems().addAll(
											Outils.get_list_filename_directory(
													path,
													".cbga"));
								} else if (arg2.equals("SBGA")) {
									cb_composant_existant.getItems().addAll(
											Outils.get_list_filename_directory(
													path,
													".sbga"));
								} else {
									// cb_composant_existant.getItems().clear();
								}

								init_technologie_brasage_bga();
								init_parametres_bga();

								gp_titre_composant_existant
										.add(new Label("Composant"), 0, 2);
								gp_titre_composant_existant
										.add(cb_composant_existant, 1, 2);
								cb_composant_existant.setVisible(true);

							} else if (arg2.contains("RESISTANCES")
									|| arg2.contains("LCCC")
									|| arg2.contains("CAPACITES")
									|| arg2.contains("QFN")) {

								init_format_leadless_leadframe();
								init_param_leadless();

								if (arg2.contains("LCCC")
										|| arg2.contains("QFN")) {
									init_technologie_brasage_lccc();

									gp_c1.setVisible(false);

									gp_titre_composant_existant.add(
											new Label("Composant Existant"),
											0,
											3);
									if (arg2.equals("LCCC")) {
										cb_composant_existant.getItems().addAll(
												Outils.get_list_filename_directory(
														path,
														".lcc"));
										gp_titre_composant_existant.add(
												cb_composant_existant,
												1,
												3);
									} else {
										gp_titre_composant_existant.add(
												cb_format_resistances,
												1,
												3);
									}

									cb_composant_existant.setVisible(true);

								} else if (arg2.contains("CAPACITES")) {
									cb_materiau.setItems(
											FXCollections.observableArrayList(
													"X7R",
													"COG"));
									gp_titre_composant_existant
											.add(new Label("Materiau"), 0, 3);
									gp_titre_composant_existant
											.add(cb_materiau, 1, 3);

									gp_titre_composant_existant.add(
											new Label(
													"Valeur Capacité (en pF)"),
											0,
											2);
									gp_titre_composant_existant
											.add(tfValeurCapacite, 1, 2);

									cb_materiau.setVisible(true);
									tfValeurCapacite.setVisible(true);

								} else {
									gp_titre_composant_existant
											.add(new Label("Format"), 0, 2);
									gp_titre_composant_existant
											.add(cb_format_resistances, 1, 2);
									cb_composant_existant.setVisible(true);
								}
							}
						}
					}
				});

		cb_materiau.valueProperty().addListener(
				new ChangeListener<String>() { /* TODO: FAMILLE COMPOSANT */

					@Override
					public void changed(
							ObservableValue<? extends String> arg0,
							String arg1,
							String arg2) {
						if (arg2 != null) {

							gp_titre_composant_existant
									.add(new Label("Format"), 0, 4);
							gp_titre_composant_existant
									.add(cb_format_resistances, 1, 4);
							cb_composant_existant.setVisible(true);
						}
					}
				});

		gp_titre_composant_existant = Outils.generer_gridPane();

		gp_titre_composant_existant.add(new Label("Catégorie composant"), 0, 0);
		gp_titre_composant_existant.add(cb_categorie_composant, 1, 0);

		titre_composant_existant = new TitledPane();
		titre_composant_existant.setCollapsible(false);
		titre_composant_existant.setPadding(new Insets(0, 0, 0, 20));
		titre_composant_existant.setText("Composant existant");
		titre_composant_existant.setContent(gp_titre_composant_existant);
	}

	/**
	 * Removes last elements from GridPane 'gp_titre_composant_existant'.
	 * 
	 * @param composant_restant Number of elements you want to let in the
	 *                          GridPane
	 */
	private void clear_composants_existants(int composant_restant) {
		while (gp_titre_composant_existant.getChildren()
				.size() > composant_restant) {
			gp_titre_composant_existant.getChildren().remove(
					gp_titre_composant_existant.getChildren().size() - 1);
		}
	}

	private void clearBrasuresExistantes(int brasuresRestantes) {

		while (this.gpBrasureExistante.getChildren()
				.size() > brasuresRestantes) {

			gpBrasureExistante.getChildren()
					.remove(gpBrasureExistante.getChildren().size() - 1);
		}
	}

	private void init_technologie_brasage_lccc() {

		cb_technologie_brasage = new ComboBox<>();
		cb_technologie_brasage
				.setItems(FXCollections.observableArrayList("NSMD"));

		gp_titre_composant_existant.add(new Label("Technologie"), 0, 2);
		gp_titre_composant_existant.add(cb_technologie_brasage, 1, 2);

		cb_technologie_brasage.valueProperty()
				.addListener(new ChangeListener<String>() {

					@Override
					public void changed(
							ObservableValue<? extends String> arg0,
							String arg1,
							String arg2) {

						technologie = arg2;
					}
				});

		technologie = cb_technologie_brasage.getValue();
	}

	private void init_technologie_brasage_bga() {

		d1 = new Label("d1: 0");
		d1.setFont(Font.font("Arial", FontWeight.BOLD, 13));

		dbille = new Label("dbille: 0");
		dbille.setFont(Font.font("Arial", FontWeight.BOLD, 13));

		cb_technologie_brasage = new ComboBox<>();
		cb_technologie_brasage
				.setItems(FXCollections.observableArrayList("NSMD", "SMD"));

		gp_titre_composant_existant.add(new Label("Technologie"), 0, 3);
		gp_titre_composant_existant.add(cb_technologie_brasage, 1, 3);

		cb_technologie_brasage.valueProperty()
				.addListener(new ChangeListener<String>() {

					@Override
					public void changed(
							ObservableValue<? extends String> arg0,
							String arg1,
							String arg2) {

						technologie = arg2;

						init_resultats_bga(technologie);

						if (arg2.equals("NSMD")) {
							if (arg1 != null) {
								gp_parametre_brasure.getChildren().remove(
										gp_parametre_brasure.getChildren()
												.size() - 1);
								gp_parametre_brasure.getChildren().remove(
										gp_parametre_brasure.getChildren()
												.size() - 1);
								gp_parametre_brasure.getChildren().remove(
										gp_parametre_brasure.getChildren()
												.size() - 1);
								gp_parametre_brasure.getChildren().remove(
										gp_parametre_brasure.getChildren()
												.size() - 1);
								gp_parametre_brasure.getChildren().remove(
										gp_parametre_brasure.getChildren()
												.size() - 1);
								hb_parametres_brasage.getChildren().remove(
										hb_parametres_brasage.getChildren()
												.size() - 1);

							}

							gp_parametre_brasure.add(gp_d2, 1, 1);
							gp_parametre_brasure.add(gp_es, 1, 2);
							gp_parametre_brasure.add(gp_dEs, 1, 3);
							gp_parametre_brasure
									.add(hb_calculer_sauvegarder, 1, 4);

							gp_parametre_brasure.add(d1, 0, 3);
							gp_parametre_brasure.add(dbille, 0, 4);

							hb_parametres_brasage.getChildren().add(
									((BrasureComposantBilles) brasure).imageNSMD);

						} else if (arg2.equals("SMD")) {
							if (arg1 != null) {
								gp_parametre_brasure.getChildren().remove(
										gp_parametre_brasure.getChildren()
												.size() - 1);
								gp_parametre_brasure.getChildren().remove(
										gp_parametre_brasure.getChildren()
												.size() - 1);
								gp_parametre_brasure.getChildren().remove(
										gp_parametre_brasure.getChildren()
												.size() - 1);
								gp_parametre_brasure.getChildren().remove(
										gp_parametre_brasure.getChildren()
												.size() - 1);
								gp_parametre_brasure.getChildren().remove(
										gp_parametre_brasure.getChildren()
												.size() - 1);
								gp_parametre_brasure.getChildren().remove(
										gp_parametre_brasure.getChildren()
												.size() - 1);
								hb_parametres_brasage.getChildren().remove(
										hb_parametres_brasage.getChildren()
												.size() - 1);

							}

							gp_parametre_brasure.add(gp_es, 1, 1);
							gp_parametre_brasure.add(gp_dEs, 1, 2);
							gp_parametre_brasure
									.add(hb_calculer_sauvegarder, 1, 3);

							gp_parametre_brasure.add(d1, 0, 3);
							gp_parametre_brasure.add(dbille, 0, 4);

							hb_parametres_brasage.getChildren().add(
									((BrasureComposantBilles) brasure).imageSMD);
						}

				else {
							titre_parametres_brasage.setVisible(false);
						}

					}
				});

	}

	private double getWidth(int n) {

		Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

		return (primaryScreenBounds.getWidth() / n) - 40;
	}

	private void init_tableview_bga() {

		double width = technologie.equals("NSMD") ? getWidth(2) : getWidth(2);

		tv_donnee_geometrique = new TableView<Propriete>();
		tv_donnee_geometrique.setMinWidth(width);
		tv_donnee_geometrique
				.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		tv_donnee_geometrique_optimisee = new TableView<Propriete>();
		tv_donnee_geometrique_optimisee.setMinWidth(width);
		tv_donnee_geometrique_optimisee
				.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		tv_tenue_fatigue = new TableView<Propriete>();
		tv_tenue_fatigue.setMinWidth(width);
		tv_tenue_fatigue
				.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		ObservableList<Propriete> donnees_donne_geometrique;
		ObservableList<Propriete> donnees_donne_geometrique_optimisee;

		ObservableList<Propriete> donnees_tenue_fatigue_et_gain_duree_de_vie;
		ObservableList<
				Propriete> donnees_tenue_fatigue_et_gain_duree_de_vie_optimisee;

		blank = new Propriete(" ");
		blank.set_resultat(" ");

		if (technologie.equals("NSMD")) {
			donnees_donne_geometrique = FXCollections.observableArrayList(
					((BrasureComposantBilles) brasure).hs1,
					((BrasureComposantBilles) brasure).hs2,
					((BrasureComposantBilles) brasure).hb,
					((BrasureComposantBilles) brasure).dr2,
					((BrasureComposantBilles) brasure).Rb,
					blank,
					blank,
					((BrasureComposantBilles) brasure).section_resistante,
					((BrasureComposantBilles) brasure).hauteur_integree);

			donnees_donne_geometrique_optimisee = FXCollections
					.observableArrayList(
							((BrasureComposantBilles) brasure).d2_optimisee,
							((BrasureComposantBilles) brasure).hs1_optimisee,
							((BrasureComposantBilles) brasure).hs2Ec2,
							((BrasureComposantBilles) brasure).dr2_optimisee,
							((BrasureComposantBilles) brasure).Rb_optimise,
							blank,
							((BrasureComposantBilles) brasure).hb_optimisee,
							((BrasureComposantBilles) brasure).section_resistante_optimisée,
							((BrasureComposantBilles) brasure).hauteur_integree_optimisee,
							blank,
							((BrasureComposantBilles) brasure).gain_duree_de_vie);

			donnees_tenue_fatigue_et_gain_duree_de_vie = FXCollections
					.observableArrayList(
							((BrasureComposantBilles) brasure).d1,
							((BrasureComposantBilles) brasure).dbille,
							blank);
			donnees_tenue_fatigue_et_gain_duree_de_vie_optimisee = FXCollections
					.observableArrayList(
							((BrasureComposantBilles) brasure).section_resistante_optimisée,
							((BrasureComposantBilles) brasure).hauteur_integree_optimisee);

			tv_donnee_geometrique_optimisee
					.setItems(donnees_donne_geometrique_optimisee);
		} else {
			donnees_donne_geometrique = FXCollections.observableArrayList(
					((BrasureComposantBilles) brasure).hs1,
					((BrasureComposantBilles) brasure).hs2,
					((BrasureComposantBilles) brasure).d2,
					((BrasureComposantBilles) brasure).hb,
					((BrasureComposantBilles) brasure).Rb);

			donnees_tenue_fatigue_et_gain_duree_de_vie = FXCollections
					.observableArrayList(
							((BrasureComposantBilles) brasure).section_resistante,
							((BrasureComposantBilles) brasure).hauteur_integree,
							blank,
							((BrasureComposantBilles) brasure).gain_duree_de_vie);
		}

		TableColumn<
				Propriete,
				String> tc_dimension_geometrie = new TableColumn<
						Propriete,
						String>("Dimensions");
		tc_dimension_geometrie
				.setCellValueFactory(c -> c.getValue().get_nom_variable());

		TableColumn<
				Propriete,
				String> tc_resultat_geometrie = new TableColumn<
						Propriete,
						String>("Resultats");
		tc_resultat_geometrie
				.setCellValueFactory(c -> c.getValue().get_resultat());

		tv_donnee_geometrique.getColumns()
				.addAll(tc_dimension_geometrie, tc_resultat_geometrie);
		tv_donnee_geometrique.setItems(donnees_donne_geometrique);

		TableColumn<
				Propriete,
				String> tc_dimension_fatigue = new TableColumn<
						Propriete,
						String>("Dimensions");
		tc_dimension_fatigue
				.setCellValueFactory(c -> c.getValue().get_nom_variable());

		TableColumn<
				Propriete,
				String> tc_resultat_fatigue = new TableColumn<
						Propriete,
						String>("Resultats");
		tc_resultat_fatigue
				.setCellValueFactory(c -> c.getValue().get_resultat());

		tv_tenue_fatigue.getColumns()
				.addAll(tc_dimension_fatigue, tc_resultat_fatigue);
		tv_tenue_fatigue.setItems(donnees_tenue_fatigue_et_gain_duree_de_vie);

		TableColumn<
				Propriete,
				String> tc_dimension_optimisee = new TableColumn<
						Propriete,
						String>("Dimensions");
		tc_dimension_optimisee
				.setCellValueFactory(c -> c.getValue().get_nom_variable());

		TableColumn<
				Propriete,
				String> tc_resultat_optimisee = new TableColumn<
						Propriete,
						String>("Resultats");
		tc_resultat_optimisee
				.setCellValueFactory(c -> c.getValue().get_resultat());

		TableColumn<
				Propriete,
				String> tc_dimension = new TableColumn<Propriete, String>(
						"Dimensions");
		tc_dimension.setCellValueFactory(c -> c.getValue().get_nom_variable());

		TableColumn<
				Propriete,
				String> tc_resultat = new TableColumn<Propriete, String>(
						"Resultats");
		tc_dimension.setCellValueFactory(c -> c.getValue().get_resultat());

		tv_donnee_geometrique_optimisee.getColumns()
				.addAll(tc_dimension_optimisee, tc_resultat_optimisee);

	}

	private void init_resultats_bga(String technologie) {

		init_tableview_bga();

		titre_donnee_geometrique = new TitledPane();
		titre_donnee_geometrique.setCollapsible(false);
		titre_donnee_geometrique.setText("Données géometriques du joint brasé");
		titre_donnee_geometrique.setContent(tv_donnee_geometrique);
		titre_donnee_geometrique.setAlignment(Pos.CENTER);

		titre_donnee_geometrique_optimisee = new TitledPane();
		titre_donnee_geometrique_optimisee.setCollapsible(false);
		titre_donnee_geometrique_optimisee
				.setText("Données géometriques du joint brasé optimisée");
		titre_donnee_geometrique_optimisee
				.setContent(tv_donnee_geometrique_optimisee);
		titre_donnee_geometrique_optimisee.setAlignment(Pos.CENTER);

		titre_tenue_en_fatigue_et_duree_de_vie = new TitledPane();
		titre_tenue_en_fatigue_et_duree_de_vie.setCollapsible(false);
		titre_tenue_en_fatigue_et_duree_de_vie
				.setText("Tenue en fatigue et durée de vie");
		titre_tenue_en_fatigue_et_duree_de_vie.setContent(tv_tenue_fatigue);
		titre_tenue_en_fatigue_et_duree_de_vie.setAlignment(Pos.CENTER);

		if (technologie.equals("NSMD")) {
			hb_resultats_brasage.getChildren().clear();
			hb_resultats_brasage.getChildren().addAll(
					titre_donnee_geometrique,
					titre_donnee_geometrique_optimisee);
		} else if (technologie.equals("SMD")) {
			hb_resultats_brasage.getChildren().clear();
			hb_resultats_brasage.getChildren().addAll(
					titre_donnee_geometrique,
					titre_tenue_en_fatigue_et_duree_de_vie);
		} else {

		}
	}

	/**
	 * initialise les composants pour les brasures des resistances et les
	 * ajoutes à la vu.
	 */
	private void init_param_leadless() {

		hb_parametres_brasage.getChildren().clear();

		init_brasure_composant_leadless();
		init_param_brasure_leadless_et_leadfram();

		hb_parametres_brasage.getChildren()
				.addAll(titre_composant_existant, titre_parametres_brasage);
		if (cb_type_composant.getValue().equals("RESISTANCES")) {
			hb_parametres_brasage.getChildren().add(BrasureResistance.image);
		} else if (cb_type_composant.getValue().equals("LCCC")) {
			hb_parametres_brasage.getChildren().add(BrasureLCC.image);
		} else if (cb_type_composant.getValue().equals("CAPACITES")) {
			hb_parametres_brasage.getChildren().add(BrasureCapacite.image);
		} else if (cb_type_composant.getValue().equals("QFN")) {
			hb_parametres_brasage.getChildren().add(BrasureQFN.image);
		}
		hb_resultats_brasage.getChildren().add(titre_resusltat);

	}

	/**
	 * Initialise le format des resistances, place les elements dans le gridpane
	 * des composants existants et ajoute un listener sur la combobox pour créer
	 * un nouveau composant à chaque fois qu'on clique dessus
	 */
	private void init_format_leadless_leadframe() {

		cb_format_resistances = new ComboBox<String>();

		if (cb_type_composant.getValue().equals("RESISTANCES")) {

			cb_format_resistances.setItems(Resistance.getResistances());
		}

		if (cb_type_composant.getValue().equals("LCCC")) {

			cb_format_resistances.setItems(LCC.getLCC());
		}

		if (cb_type_composant.getValue().equals("CAPACITES")) {

			cb_format_resistances.setItems(Capacite.getCapacites());
		}

		if (cb_type_composant.getValue().equals("QFN")) {

			cb_format_resistances.setItems(QFN.getComposants());
		}

		cb_format_resistances.valueProperty()
				.addListener(new ChangeListener<String>() {

					@Override
					public void changed(
							ObservableValue<? extends String> arg0,
							String arg1,
							String arg2) {
						if (arg1 != null) {
							if (!arg1.equals(arg2)) {
								if (cb_type_composant
										.getValue() == "CAPACITES") {
									clear_composants_existants(10);
								} else if (cb_type_composant
										.getValue() == "LCCC") {
									clear_composants_existants(8);
								} else {
									clear_composants_existants(6);
								}
							}
						}

						if (arg2 != null) {
							if (cb_type_composant.getValue().equals("RESISTANCES")) {
								titre_parametres_brasage.setText("Parametres de brasage pour la resistance " + arg2);
								BrasureResistance b = (BrasureResistance) brasure;
								((BrasureResistance) brasure).setFormat(arg2);

								gp_titre_composant_existant.add(new Label("d: " + String.valueOf(b.getResistance().get("d"))),0,3);
								gp_titre_composant_existant.add(new Label("a: " + String.valueOf(b.getResistance().get("a"))),0, 4);
								gp_titre_composant_existant.add(new Label("b: " + String.valueOf(b.getResistance().get("b"))),0,5);
								gp_titre_composant_existant.add(new Label("h: " + String.valueOf(b.getResistance().get("h"))),1,3);
								gp_titre_composant_existant.add(new Label("masse (g): " + String.valueOf(b.getResistance().get("masse"))),1,4);
							} else if (cb_type_composant.getValue().equals("LCCC")) {
								titre_parametres_brasage.setText(
										"Parametres de brasage pour la LCCC "
												+ arg2);
								BrasureLCC b = ((BrasureLCC) brasure);
								((BrasureLCC) brasure).setFormat(arg2);

								gp_titre_composant_existant.add(
										new Label(
												"x: " + String.valueOf(
														b.getComposant()
																.get("x"))),
										0,
										4);
								gp_titre_composant_existant.add(
										new Label(
												"y: " + String.valueOf(
														b.getComposant()
																.get("y"))),
										0,
										5);
								gp_titre_composant_existant.add(
										new Label(
												"z: " + String.valueOf(
														b.getComposant()
																.get("z"))),
										0,
										6);
								gp_titre_composant_existant.add(
										new Label(
												"pas: " + String.valueOf(
														b.getComposant()
																.get("pas"))),
										0,
										7);
								gp_titre_composant_existant.add(
										new Label(
												"a: " + String.valueOf(
														b.getComposant()
																.get("a"))),
										0,
										8);
								gp_titre_composant_existant.add(
										new Label(
												"b: " + String.valueOf(
														b.getComposant()
																.get("b"))),
										1,
										4);
								gp_titre_composant_existant.add(
										new Label(
												"h: " + String.valueOf(
														b.getComposant()
																.get("h"))),
										1,
										5);
								gp_titre_composant_existant.add(
										new Label(
												"n: " + String.valueOf(
														b.getComposant()
																.get("n"))),
										1,
										6);
								gp_titre_composant_existant.add(
										new Label(
												"nx: " + String.valueOf(
														b.getComposant()
																.get("nx"))),
										1,
										7);
								gp_titre_composant_existant.add(
										new Label(
												"ny: " + String.valueOf(
														b.getComposant()
																.get("ny"))),
										1,
										8);

							} else if (cb_type_composant.getValue()
									.equals("CAPACITES")) {
								titre_parametres_brasage.setText(
										"Parametres de brasage pour la Capacité "
												+ arg2);
								BrasureCapacite b = (BrasureCapacite) brasure;
								((BrasureCapacite) brasure).setFormat(arg2);

								gp_titre_composant_existant.add(
										new Label(
												"d: " + String.valueOf(
														b.getComposant()
																.get("d"))),
										0,
										5);
								gp_titre_composant_existant.add(
										new Label(
												"a: " + String.valueOf(
														b.getComposant()
																.get("a"))),
										0,
										6);
								gp_titre_composant_existant.add(
										new Label(
												"b: " + String.valueOf(
														b.getComposant()
																.get("b"))),
										0,
										7);
								gp_titre_composant_existant.add(
										new Label(
												"h: " + String.valueOf(
														b.getComposant()
																.get("h"))),
										1,
										5);
								gp_titre_composant_existant.add(
										new Label(
												"masse (g): " + String.valueOf(
														b.getComposant()
																.get("masse"))),
										1,
										6);
							}

				else if (cb_type_composant.getValue().equals("QFN")) {
								titre_parametres_brasage.setText(
										"Parametres de brasage pour le QFN  "
												+ arg2);
								BrasureQFN b = ((BrasureQFN) brasure);
								((BrasureQFN) brasure).setFormat(arg2);

								gp_titre_composant_existant.add(
										new Label(
												"x: " + String.valueOf(
														b.getComposant()
																.get("x"))),
										0,
										4);
								gp_titre_composant_existant.add(
										new Label(
												"y: " + String.valueOf(
														b.getComposant()
																.get("y"))),
										0,
										5);
								gp_titre_composant_existant.add(
										new Label(
												"z: " + String.valueOf(
														b.getComposant()
																.get("z"))),
										0,
										6);
								gp_titre_composant_existant.add(
										new Label(
												"pas: " + String.valueOf(
														b.getComposant()
																.get("pas"))),
										0,
										7);
								gp_titre_composant_existant.add(
										new Label(
												"a: " + String.valueOf(
														b.getComposant()
																.get("a"))),
										0,
										8);
								gp_titre_composant_existant.add(
										new Label(
												"b: " + String.valueOf(
														b.getComposant()
																.get("b"))),
										1,
										4);
								gp_titre_composant_existant.add(
										new Label(
												"h: " + String.valueOf(
														b.getComposant()
																.get("h"))),
										1,
										5);
								gp_titre_composant_existant.add(
										new Label(
												"n: " + String.valueOf(
														b.getComposant()
																.get("n"))),
										1,
										6);
								gp_titre_composant_existant.add(
										new Label(
												"nx: " + String.valueOf(
														b.getComposant()
																.get("nx"))),
										1,
										7);
								gp_titre_composant_existant.add(
										new Label(
												"ny: " + String.valueOf(
														b.getComposant()
																.get("ny"))),
										1,
										8);
							}
						}

					}
				});

	}

	private void init_alert() {
		alert = new Alert(AlertType.ERROR);
		alert.setContentText(
				"Des valeurs sont nulles, veuillez remplir tous les champs");
	}

	/**
	 * Place les elements de la brasure du BGA dans le gridpane et ajoute un
	 * listener sur le bouton ok.
	 */
	private void init_dimension_bga() {

		init_titre_parametre();

		pcb = new OngletPcb();

		h1 = Outils.generer_textfield("");
		db1 = Outils.generer_textfield("");

		gp_h1 = Outils.generer_textfield_avec_label(h1, "h1");
		gp_db1 = Outils.generer_textfield_avec_label(db1, "db1");

		d2 = Outils.generer_textfield("");
		es = Outils.generer_textfield("");
		dEs = Outils.generer_textfield("");

		gp_d2 = Outils.generer_textfield_avec_label(d2, "d2");
		gp_es = Outils.generer_textfield_avec_label(es, "es");
		gp_dEs = Outils.generer_textfield_avec_label(dEs, "dEs");

		brasure = new BrasureComposantBilles();

		gp_parametre_brasure
				.add(Outils.generer_label("Dimensions en mm"), 0, 0);
		gp_parametre_brasure.add(gp_h1, 0, 1);
		gp_parametre_brasure.add(gp_db1, 0, 2);

		sauvegarder.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				fileChooser.getExtensionFilters().clear();
				if (cb_type_composant.getValue().equals("BGA")) {
					fileChooser.getExtensionFilters().add(
							new ExtensionFilter(
									"Brasure BGA (*.brbga)",
									"*.brbga"));
				} else if (cb_type_composant.getValue().equals("WLP")) {
					fileChooser.getExtensionFilters().add(
							new ExtensionFilter(
									"Brasure WLP (*.brwlp)",
									"*.brwlp"));
				} else if (cb_type_composant.getValue().equals("CBGA")) {
					fileChooser.getExtensionFilters().add(
							new ExtensionFilter(
									"Brasure CBGA (*.brcbga)",
									"*.brcbga"));
				} else if (cb_type_composant.getValue().equals("CSP")) {
					fileChooser.getExtensionFilters().add(
							new ExtensionFilter(
									"Brasure CSP (*.brcsp)",
									"*.brcsp"));
				} else if (cb_type_composant.getValue().equals("SBGA")) {
					fileChooser.getExtensionFilters().add(
							new ExtensionFilter(
									"Brasure SBGA (*.brsbga)",
									"*.brsbga"));
				} else if (cb_type_composant.getValue().equals("PCBGA")) {
					fileChooser.getExtensionFilters().add(
							new ExtensionFilter(
									"Brasure PCBGA (*.brpcbga)",
									"*.brpcbga"));
				} else {
					alert = new Alert(AlertType.ERROR);
					alert.setContentText(
							"Je ne connais pas ce type de brasure !");
				}

				File file = fileChooser.showSaveDialog(getScene().getWindow());

				if (file != null) {
					try {
						((BrasureComposantBilles) brasure)
								.setNom(cb_composant_existant.getValue());
						((BrasureComposantBilles) brasure)
								.setH1(Double.valueOf(h1.getText()));
						((BrasureComposantBilles) brasure)
								.setDb1(Double.valueOf(db1.getText()));
						((BrasureComposantBilles) brasure)
								.setTechnologie(technologie);

						if (technologie.equals("NSMD")) {
							((BrasureComposantBilles) brasure)
									.setd2(Double.valueOf(d2.getText()));
							((BrasureComposantBilles) brasure)
									.setEs(Double.valueOf(es.getText()));
							((BrasureComposantBilles) brasure)
									.setDes(Double.valueOf(dEs.getText()));
						} else if (technologie.equals("SMD")) {
							((BrasureComposantBilles) brasure)
									.setEs(Double.valueOf(es.getText()));
							((BrasureComposantBilles) brasure)
									.setDes(Double.valueOf(dEs.getText()));
						}

						Outils.sauvegarder_objet(
								((BrasureComposantBilles) brasure).toString(),
								file);
					} catch (NumberFormatException e) {
						alert.showAndWait();
						e.printStackTrace();
					}
				}
			}
		});

		calculer.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				try {
					((BrasureComposantBilles) brasure)
							.setH1(Double.valueOf(h1.getText()));
					((BrasureComposantBilles) brasure)
							.setDb1(Double.valueOf(db1.getText()));
					((BrasureComposantBilles) brasure)
							.setTechnologie(technologie);
					if (technologie.equals("NSMD")) {
						((BrasureComposantBilles) brasure)
								.setd2(Double.valueOf(d2.getText()));
						((BrasureComposantBilles) brasure)
								.setEs(Double.valueOf(es.getText()));
						((BrasureComposantBilles) brasure)
								.setDes(Double.valueOf(dEs.getText()));
					} else if (technologie.equals("SMD")) {
						((BrasureComposantBilles) brasure)
								.setEs(Double.valueOf(es.getText()));
						((BrasureComposantBilles) brasure)
								.setDes(Double.valueOf(dEs.getText()));
					}
					((BrasureComposantBilles) brasure).setCalculs();
					d1.setText(
							"d1: " + (Outils.nombreChiffreApresVirgule(
									((BrasureComposantBilles) brasure).getD1(),
									Ressources.NOMBRE_CHIFFRE_APRES_VIRGULE)));
					dbille.setText(
							"dbille: " + (Outils.nombreChiffreApresVirgule(
									((BrasureComposantBilles) brasure)
											.getDbille(),
									Ressources.NOMBRE_CHIFFRE_APRES_VIRGULE)));
				} catch (NumberFormatException e) {
					alert.showAndWait();
					e.printStackTrace();

				}

			}
		});

	}

	protected void init_parametres_bga() {
		hb_parametres_brasage.getChildren().clear();
		init_dimension_bga();
		hb_parametres_brasage.getChildren()
				.addAll(titre_composant_existant, titre_parametres_brasage);
	}

	public double getH1() {
		return Double.parseDouble(h1.getText());
	}

	public void setH1(String h1) {
		this.h1.setText(h1);
	}

	public double getDb1() {
		return Double.parseDouble(db1.getText());
	}

	public void setDb1(String db1) {
		this.db1.setText(db1);
	}

	public double getA1() {
		return Double.parseDouble(a1.getText());
	}

	public void setA1(TextField a1) {
		this.a1 = a1;
	}

	public double getB1() {
		return Double.parseDouble(b1.getText());
	}

	public void setB1(TextField b1) {
		this.b1 = b1;
	}

	public double getC1() {
		return Double.parseDouble(c1.getText());
	}

	public void setC1(TextField c1) {
		this.c1 = c1;
	}

	public double getAs() {
		return Double.parseDouble(as.getText());
	}

	public void setAs(TextField as) {
		this.as = as;
	}

	public double getBs() {
		return Double.parseDouble(bs.getText());
	}

	public void setBs(TextField bs) {
		this.bs = bs;
	}

	public double getEs() {
		return Double.parseDouble(es.getText());
	}

	public void setEs(TextField es) {
		this.es = es;
	}

//	public void init_print_button() {
//	printButton = new Button("Print");
//	onglet_brasure.getChildren().add(printButton);
//	printButton.setOnAction((ActionEvent event) -> {
//
//		// Création du job d'impression.
//		final PrinterJob printerJob = PrinterJob.createPrinterJob();
//		// Affichage de la boite de dialog de configation de l'impression.
//		if (printerJob.showPrintDialog(this.getScene().getWindow())) {
//
//			Printer printer = printerJob.getPrinter();
//			PageLayout pageLayout = printer.createPageLayout(Paper.A3, PageOrientation.PORTRAIT,
//					Printer.MarginType.HARDWARE_MINIMUM);
//
//			double width = this.getWidth();
//			double height = this.getHeight();
//
//			PrintResolution resolution = printerJob.getJobSettings().getPrintResolution();
//
//			width /= resolution.getFeedResolution();
//
//			height /= resolution.getCrossFeedResolution();
//
//			double scaleX = pageLayout.getPrintableWidth() / width / 700;
//			double scaleY = pageLayout.getPrintableHeight() / height / 600;
//
//			Scale scale = new Scale(scaleX, scaleY);
//
//			this.getTransforms().add(scale);
//
//			// Lancement de l'impression.
//			if (printerJob.printPage(pageLayout, this)) {
//				// Fin de l'impression.
//				printerJob.endJob();
//			}
//			this.getTransforms().remove(scale);
//		}
//
//	});
//
//}

//	public void init_brasure() {
//
//		brasure = new BrasureBille(); // à changer , pour mettre le type de brasure en fonction du type de composant
//										// choisit
//										// dans l'onglet composant,car pour l'instant on teste juste avec la brasure du
//										// BGA
//		onglet_brasure.getChildren().add(brasure);
//
//	}

//	public void init_effacer_button() {
//		effacerButton = new Button("Effacer");
//		onglet_brasure.getChildren().add(effacerButton);
//		effacerButton.setOnAction((ActionEvent event) -> {
//			onglet_brasure.getChildren().remove(brasure);
//			init_brasure();
//
//		});
//
//	}

	public Brasure get_brasure() {
		// return this.brasure;

		return new Brasure();
	}

	public String getTechnologie() {
		return technologie;
	}

	public void setTechnologie(String technologie) {
		this.technologie = technologie;
	}

	public void init_hbox() {
		this.hboxBrasuresExistantes = new HBox(20);
		this.hboxBrasuresExistantes.setPadding(new Insets(20, 0, 0, 20));
		this.hb_parametres_brasage = new HBox(20);
		this.hb_parametres_brasage.setPadding(new Insets(20, 0, 0, 0));

		this.hb_resultats_brasage = new HBox(20);
		this.hb_resultats_brasage.setPadding(new Insets(20, 0, 0, 20));
		this.hb_resultats_brasage.setMaxHeight(310);

		this.hb_calculer_sauvegarder = new HBox(20);
	}

}
