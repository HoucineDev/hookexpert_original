package com.app.ancea;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.PrintResolution;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * @author FX603984 Classe permettant de faire l'interface graphique de l'onglet
 *         Composant.
 */
public class OngletComposant extends VBox {

	// classe representant l'onglet de calculs des composants

	OngletPcb pcb;

	final static private String path = Ressources.pathComposants;

	private Button printButton, buttonSauvegarder;

	private NouveauComposant nouveauComposant;

	private TableView<? extends Resultat_temperature> tv_temperature;
	private TableView<
			? extends Resultat_temperature_pcb> tv_temperature_pcb_composant;
	private TableView<Propriete> tv_proprietes_thermiques;

	private CheckBox check_affichage_pcb;
	public ComboBox<String> cb_compositions_existants, cb_composant_existant,
			cb_categorie_composant, cb_type_composant;

	private ComboBox<String> cb_materiau;
	private TextField tfValeurCapacite;

	private VBox onglet_composant;
	private VBox vb_matrice_creuse;
	private HBox hb_resultats_paliers;
	private HBox hbChoixComposantEtProprietes;
	private HBox hbMatriceCreuse;

	private Composant composant;

	private GridPane gp_titre_composant_existant;
	private GridPane gp_matrice_creuse;

	private TitledPane titre_proprietes_composant;
	private TitledPane titre_composant_existant;
	private TitledPane titre_palier_temperature;
	private TitledPane titre_palier_temperature_pcb_composant;
	private TitledPane titre_matrice_creuse;

	private FileChooser fileChooser;

	/**
	 * Interface graphique des composants
	 */
	public OngletComposant() {

		setPadding(new Insets(20, 20, 20, 20));

		System.out.println("OngletComposant: " + path );

		onglet_composant = new VBox();
		init_composant();

		pcb = new OngletPcb();
		pcb.init_pcb();

		tv_temperature = composant.get_tableView_temperature();

		tv_proprietes_thermiques = composant.get_tableView_proprietes();

		fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(new File(Ressources.pathComposants));

		initButtonSauvegarder();

		hbChoixComposantEtProprietes = new HBox(10);
		hbChoixComposantEtProprietes.setMaxHeight(300);

		hbMatriceCreuse = new HBox(15);
		hbMatriceCreuse.setMaxHeight(200);

		titre_composant_existant = generer_titledPane("Configurations existantes");
		titre_palier_temperature = generer_titledPane("Paliers températures composant");
		titre_palier_temperature.setMaxHeight(300);
		titre_palier_temperature_pcb_composant = generer_titledPane("Paliers températures PCB composant");
		titre_proprietes_composant = generer_titledPane("Propriétés composant");

		titre_matrice_creuse = generer_titledPane("Matrice Creuse");

		hb_resultats_paliers = new HBox(10);

		vb_matrice_creuse = new VBox(10);
		gp_matrice_creuse = new GridPane();

		init_print_button();

		init_composant_existant();

		titre_proprietes_composant.setContent(tv_proprietes_thermiques);
		// titre_matrice_creuse.setContent(tv_proprietes_thermiques);
		titre_palier_temperature.setContent(tv_temperature);

		hbChoixComposantEtProprietes.getChildren()
				.addAll(titre_composant_existant);
		HBox.setHgrow(titre_palier_temperature, Priority.ALWAYS);
		HBox.setHgrow(titre_composant_existant, Priority.NEVER);

		this.getChildren().addAll(
				hbChoixComposantEtProprietes,
				hbMatriceCreuse,
				hb_resultats_paliers,
				onglet_composant);
		this.setSpacing(10);
		getChildren().add(printButton);

	}

	private void initButtonSauvegarder() {
		buttonSauvegarder = new Button("Sauvegarder");
		buttonSauvegarder.setOnAction(event -> {
			if (composant instanceof ComposantBGA) {
				fileChooser.getExtensionFilters().clear();
				fileChooser.getExtensionFilters().add(
						new ExtensionFilter("Composant BGA(*.bga)", "*.bga"));
			}
			if (composant instanceof ComposantCSP) {
				fileChooser.getExtensionFilters().clear();
				fileChooser.getExtensionFilters().add(
						new ExtensionFilter("Composant CSP(*.csp)", "*.csp"));
			}
			if (composant instanceof ComposantSBGA) {
				fileChooser.getExtensionFilters().clear();
				fileChooser.getExtensionFilters().add(
						new ExtensionFilter(
								"Composant SBGA(*.sbga)",
								"*.sbga"));
			}
			if (composant instanceof ComposantWLP) {
				fileChooser.getExtensionFilters().clear();
				fileChooser.getExtensionFilters().add(
						new ExtensionFilter("Composant WLP(*.wlp)", "*.wlp"));
			}
			if (composant instanceof ComposantCBGA) {
				fileChooser.getExtensionFilters().clear();
				fileChooser.getExtensionFilters().add(
						new ExtensionFilter(
								"Composant CBGA(*.cbga)",
								"*.cbga"));
			}
			if (composant instanceof ComposantPCBGA) {
				fileChooser.getExtensionFilters().clear();
				fileChooser.getExtensionFilters().add(
						new ExtensionFilter(
								"Composant PCBGA(*.pcbga)",
								"*.pcbga"));
			}
			File file = fileChooser.showSaveDialog(getScene().getWindow());

			if (file != null) {
				try {

					PrintWriter pw = new PrintWriter(
							new BufferedWriter(
									new FileWriter(new File(file.toString()))));
					if (composant instanceof ComposantBilles) {

						if (composant instanceof ComposantCSP
								|| composant instanceof ComposantBGA
								|| composant instanceof ComposantSBGA) {

							pw.println(pcb.resine.toString());

							ArrayList<
									CouchePcb> liste_couches_pcb = ((ComposantBGA) composant).pcb.pcb
											.get_liste_couches_pcb();
							for (int i = 0; i < liste_couches_pcb.size(); i++) {
								System.out.println(
										"Couche PCB: " + liste_couches_pcb
												.get(i).toString());
								pw.println(
										(liste_couches_pcb.get(i).toString()));
							}
						} else if (composant instanceof ComposantPCBGA) {
							pw.println(pcb.resine.toString());

							ArrayList<
									CouchePcb> liste_couches_pcb = ((ComposantPCBGA) composant).pcb.pcb
											.get_liste_couches_pcb();
							for (int i = 0; i < liste_couches_pcb.size(); i++) {
								System.out.println(
										"Couche PCB: " + liste_couches_pcb
												.get(i).toString());
								pw.println(
										(liste_couches_pcb.get(i).toString()));
							}
						}
						pw.println(composant.toString());
						int nombreLignes = 0;
						int nombreColonnes = 0;
						if (composant instanceof ComposantCSP
								|| composant instanceof ComposantBGA
								|| composant instanceof ComposantSBGA) {
							// Ecriture de la matrice de Billes
							pw.println("Matrice Billes:");
							nombreLignes = (int) ((ComposantBGA) composant)
									.getNombre_billes_y() + 2;
							nombreColonnes = (int) ((ComposantBGA) composant)
									.getNombre_billes_x() + 2;
						} else if (composant instanceof ComposantPCBGA) {
							pw.println("Matrice Billes:");
							nombreLignes = (int) ((ComposantPCBGA) composant)
									.getNombre_billes_y() + 2;
							nombreColonnes = (int) ((ComposantPCBGA) composant)
									.getNombre_billes_x() + 2;
						} else if (composant instanceof ComposantWLP) {
							nombreLignes = (int) ((ComposantWLP) composant)
									.getNombre_billes_y() + 2;
							nombreColonnes = (int) ((ComposantWLP) composant)
									.getNombre_billes_x() + 2;
						} else if (composant instanceof ComposantCBGA) {
							nombreLignes = (int) ((ComposantCBGA) composant)
									.getNombre_billes_y() + 2;
							nombreColonnes = (int) ((ComposantCBGA) composant)
									.getNombre_billes_x() + 2;
						}

						// Transformation du GridPane en Tableau
						Node[][] gridPaneNodes = new Node[nombreLignes][nombreColonnes];

						for (Node child : gp_matrice_creuse.getChildren()) {
							Integer column = GridPane.getColumnIndex(child);
							Integer row = GridPane.getRowIndex(child);
							if (column != null && row != null) {
								gridPaneNodes[row][column] = child;
							}
						}

						// La première et la dernière ligne ainsi que colonne ne
						// sont
						// pas des infos que l'on veut sauvegarder
						for (int row = 1; row < nombreLignes - 1; row++) {
							for (int col = 1; col < nombreColonnes - 1; col++) {

								Node node = gridPaneNodes[row][col];

								if (node instanceof Button) {
									if (((Button) node).getText().equals("●")) {
										if (col == nombreColonnes - 2) {
											pw.print("true");
										} else {
											pw.print("true;");
										}
									} else {
										if (col == nombreColonnes - 2) {
											pw.print("false");
										} else {
											pw.print("false;");
										}
									}
								}
							}
							pw.print("\n");

						}
					}

					pw.close();
				} catch (IOException ex) {
					Logger.getLogger(OngletPcb.class.getName())
							.log(Level.SEVERE, null, ex);
				}
			}
			this.cb_composant_existant.getItems().clear();
			if (composant instanceof ComposantPCBGA) {
				this.cb_composant_existant.getItems().addAll(
						Outils.get_list_filename_with_extension(
								path,
								".pcbga"));
			} else if (composant instanceof ComposantSBGA) {
				this.cb_composant_existant.getItems().addAll(
						Outils.get_list_filename_with_extension(path, ".sbga"));
			} else if (composant instanceof ComposantCBGA) {
				this.cb_composant_existant.getItems().addAll(
						Outils.get_list_filename_with_extension(path, ".cbga"));
			} else if (composant instanceof ComposantCSP) {
				this.cb_composant_existant.getItems().addAll(
						Outils.get_list_filename_with_extension(path, ".csp"));
			} else if (composant instanceof ComposantWLP) {
				this.cb_composant_existant.getItems().addAll(
						Outils.get_list_filename_with_extension(path, ".wlp"));
			} else if (composant instanceof ComposantLCC) {
				this.cb_composant_existant.getItems().addAll(
						Outils.get_list_filename_with_extension(path, ".lcc"));
			} else if (composant instanceof ComposantBGA) {
				this.cb_composant_existant.getItems().addAll(
						Outils.get_list_filename_with_extension(path, ".bga"));
			}
		});
	}

	/* Genere des titledpane avec les bonnes dimensions */
	public TitledPane generer_titledPane(String nom) {
		TitledPane tp = new TitledPane();
		tp = new TitledPane();
		tp.setText(nom);
		tp.setCollapsible(false);
		return tp;
	}

	private void clear_composants_existants(int composant_restant) {
		while (gp_titre_composant_existant.getChildren()
				.size() > composant_restant) {
			gp_titre_composant_existant.getChildren().remove(
					gp_titre_composant_existant.getChildren().size() - 1);
		}
	}

	private void clear_panel(int composant_restant) {
		while (onglet_composant.getChildren().size() > composant_restant) {
			onglet_composant.getChildren()
					.remove(onglet_composant.getChildren().size() - 1);
		}

		while (getChildren().size() > 3) {
			getChildren().remove(getChildren().size() - 1);
		}
	}

	private void init_composant_existant() {

		gp_titre_composant_existant = Outils.generer_gridPane();

		cb_compositions_existants = new ComboBox<>();

		cb_categorie_composant = new ComboBox<String>(
				FXCollections.observableArrayList(
						"NOUVEAU COMPOSANT",
						"COMPOSANT A BILLES",
						"COMPOSANT LEADLESS",
						"COMPOSANT LEADFRAME"));

		cb_materiau = new ComboBox<String>();
		cb_materiau.setVisible(false);

		tfValeurCapacite = new TextField();
		tfValeurCapacite.setFont(new Font("SanSerif", 15));
		tfValeurCapacite.setPromptText("Valeur Capacité (pF)");
		tfValeurCapacite.getStyleClass().add("field-background");
		tfValeurCapacite.setVisible(false);

		cb_type_composant = new ComboBox<String>();
		cb_type_composant.setVisible(false);

		cb_composant_existant = new ComboBox<String>();
		new AutoCompleteComboBoxListener<>(cb_composant_existant);
		cb_composant_existant.setVisible(false);

		cb_composant_existant.getSelectionModel().selectedItemProperty()
				.addListener(new ChangeListener<String>() {

					@Override
					public void changed(
							ObservableValue<? extends String> arg0,
							String arg1,
							String arg2) {
						clear_panel(4);
						try {
							if (arg2 != null) {
								if (arg2.contains(".bga")) {
									System.out.println("[Composant] Selected Component BGA: " + arg2);

									OngletPcb pcb1;
									pcb1 = new OngletPcb();
									pcb1.init_pcb();

									System.out.println("[Composant] Component BGA file : " + (path + arg2));

									composant = new ComposantBGA(
											pcb1,
											path + arg2);

									ArrayList<CouchePcb> liste_couches_pcb = ((ComposantBGA) composant).pcb.pcb
													.get_liste_couches_pcb();
									pcb1.liste_couches_pcb = liste_couches_pcb;
									pcb1.button_calculer.setVisible(true);
									pcb1.vb_pcb.getChildren()
											.addAll(liste_couches_pcb);

									getChildren()
											.add(pcb1.vb_bar_pcb_couche_pcb);

									gp_matrice_creuse.getChildren().clear();
									gp_matrice_creuse = Outils
											.generer_matrice_creuse(
													composant,
													gp_matrice_creuse,
													((ComposantBGA) composant).nombre_billes_x
															.intValue(),
													((ComposantBGA) composant).nombre_billes_y
															.intValue());
									rafraichir_vue(".bga");

								} else if (arg2.contains(".wlp")) {
									composant = new ComposantWLP();
									((ComposantWLP) composant)
											.charger_wlp(path + arg2);

									gp_matrice_creuse.getChildren().clear();
									gp_matrice_creuse = Outils
											.generer_matrice_creuse(
													composant,
													gp_matrice_creuse,
													((ComposantWLP) composant)
															.getNombre_billes_x(),
													((ComposantWLP) composant)
															.getNombre_billes_y());
									rafraichir_vue(".wlp");

								} else if (arg2.contains(".cbga")) {

									composant = new ComposantCBGA();
									((ComposantCBGA) composant)
											.charger_cbga(path + arg2);

									gp_matrice_creuse.getChildren().clear();
									gp_matrice_creuse = Outils
											.generer_matrice_creuse(
													composant,
													gp_matrice_creuse,
													((ComposantCBGA) composant)
															.getNombre_billes_x(),
													((ComposantCBGA) composant)
															.getNombre_billes_y());
									rafraichir_vue(".cbga");

								} else if (arg2.contains(".csp")) {
									OngletPcb pcb1;
									pcb1 = new OngletPcb();
									pcb1.init_pcb();
									pcb1.button_calculer.setVisible(true);
									composant = new ComposantCSP(
											pcb1,
											path + arg2);

									ArrayList<
											CouchePcb> liste_couches_pcb = ((ComposantBGA) composant).pcb.pcb
													.get_liste_couches_pcb();
									pcb1.liste_couches_pcb = liste_couches_pcb;

									pcb1.vb_pcb.getChildren()
											.addAll(liste_couches_pcb);

									getChildren()
											.add(pcb1.vb_bar_pcb_couche_pcb);

									gp_matrice_creuse.getChildren().clear();
									gp_matrice_creuse = Outils
											.generer_matrice_creuse(
													composant,
													gp_matrice_creuse,
													((ComposantBGA) composant).nombre_billes_x
															.intValue(),
													((ComposantBGA) composant).nombre_billes_y
															.intValue());
									rafraichir_vue(".csp");
								} else if (arg2.contains(".sbga")) {

									OngletPcb pcb1;
									pcb1 = new OngletPcb();
									pcb1.init_pcb();

									composant = new ComposantSBGA(
											pcb1,
											path + arg2);

									ArrayList<
											CouchePcb> liste_couches_pcb = ((ComposantBGA) composant).pcb.pcb
													.get_liste_couches_pcb();
									pcb1.liste_couches_pcb = liste_couches_pcb;
									pcb1.button_calculer.setVisible(true);
									pcb1.vb_pcb.getChildren()
											.addAll(liste_couches_pcb);

									getChildren()
											.add(pcb1.vb_bar_pcb_couche_pcb);

									gp_matrice_creuse.getChildren().clear();
									gp_matrice_creuse = Outils
											.generer_matrice_creuse(
													composant,
													gp_matrice_creuse,
													((ComposantBGA) composant).nombre_billes_x
															.intValue(),
													((ComposantBGA) composant).nombre_billes_y
															.intValue());
									rafraichir_vue(".sbga");

								} else if (arg2.contains(".pcbga")) {
									OngletPcb pcb1;
									pcb1 = new OngletPcb();
									pcb1.init_pcb();

									composant = new ComposantPCBGA(
											pcb1,
											path + arg2);

									ArrayList<
											CouchePcb> liste_couches_pcb = ((ComposantPCBGA) composant).pcb.pcb
													.get_liste_couches_pcb();
									pcb1.liste_couches_pcb = liste_couches_pcb;
									pcb1.button_calculer.setVisible(true);
									pcb1.vb_pcb.getChildren()
											.addAll(liste_couches_pcb);

									getChildren()
											.add(pcb1.vb_bar_pcb_couche_pcb);

									gp_matrice_creuse.getChildren().clear();
									gp_matrice_creuse = Outils
											.generer_matrice_creuse(
													composant,
													gp_matrice_creuse,
													((ComposantPCBGA) composant).nombre_billes_x
															.intValue(),
													((ComposantPCBGA) composant).nombre_billes_y
															.intValue());
									rafraichir_vue(".pcbga");
								} else if (cb_type_composant.getValue()
										.equals("RESISTANCES")) {
									composant = new Composant_Resistance(arg2);
									rafraichir_vue("resistance");
								} else if (cb_type_composant.getValue()
										.equals("CAPACITES")) {
									String materiau_capacite = cb_materiau
											.getValue();
									Double capacite;

									try {
										capacite = Double.valueOf(
												tfValeurCapacite.getText());

										cb_materiau.setStyle(
												"-fx-border-color: default;");
										tfValeurCapacite.setStyle(
												"-fx-border-color: default;");

										composant = new Composant_Capacite(
												arg2,
												materiau_capacite,
												capacite);
										rafraichir_vue("capacite");
									} catch (NumberFormatException e) {
										tfValeurCapacite.setStyle(
												"-fx-border-color: red");
										Outils.alerteUtilisateur(
												"ERROR",
												"Problème Valeur",
												"Valeur de capacité non valide");
									}

								} else if (cb_type_composant.getValue()
										.equals("LCCC")) {
									// composant = new ComposantLCC(arg2);
									composant = new ComposantLCC(
											"",
											path + arg2);

									gp_matrice_creuse.getChildren().clear();
									gp_matrice_creuse = Outils
											.generer_matrice_creuse_lcc(
													composant,
													gp_matrice_creuse,
													(int) ((ComposantLCC) composant)
															.getNombre_nix(),
													(int) ((ComposantLCC) composant)
															.getNombre_niy());

									rafraichir_vue("lccc");
								} else if (cb_type_composant.getValue()
										.equals("QFN")) {
									composant = new ComposantQFN(arg2);
									rafraichir_vue("qfn");
								}

								tv_temperature = composant
										.get_tableView_temperature();

								if (cb_type_composant.getValue().equals("BGA")
										|| cb_type_composant.getValue()
												.equals("CSP")
										|| cb_type_composant.getValue()
												.equals("SBGA")) {

									tv_temperature_pcb_composant = ((ComposantBGA) composant)
											.get_tableView_temperature_pcb_composant();
									titre_palier_temperature_pcb_composant
											.setContent(
													tv_temperature_pcb_composant);
									getChildren().add(
											titre_palier_temperature_pcb_composant);
								} else if (cb_type_composant.getValue()
										.equals("PCBGA")) {
									tv_temperature_pcb_composant = ((ComposantPCBGA) composant)
											.get_tableView_temperature_pcb_composant();
									titre_palier_temperature_pcb_composant
											.setContent(
													tv_temperature_pcb_composant);
									getChildren().add(
											titre_palier_temperature_pcb_composant);
								}

								titre_palier_temperature
										.setContent(tv_temperature);

								hbChoixComposantEtProprietes.getChildren()
										.remove(tv_proprietes_thermiques);

								tv_proprietes_thermiques = composant
										.get_tableView_proprietes();

								titre_proprietes_composant
										.setContent(tv_proprietes_thermiques);

								onglet_composant.getChildren()
										.remove(composant);
								onglet_composant.getChildren()
										.addAll(composant);

							}

						} catch (Exception ioe) {
							Alert alert = new Alert(AlertType.ERROR);
							alert.setTitle("Erreur chargement");
							alert.setHeaderText("Il y a une erreur");
							alert.setContentText(
									"Probleme de chargement du profil PCB!");

							alert.showAndWait();

							ioe.printStackTrace();
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

						if (arg2 != null) {

							if (arg2.contains("COMPOSANT A BILLES")) {

								System.out.println("[Composant] Selected Component: " + arg2);

								cb_type_composant.setItems(
										FXCollections.observableArrayList(
												"BGA",
												"CSP",
												"WLP",
												"SBGA",
												"CBGA",
												"PCBGA"));
								gp_titre_composant_existant.add(
										new Label("Type de composant"),
										0,
										1);
								gp_titre_composant_existant
										.add(cb_type_composant, 1, 1);

								cb_type_composant.setVisible(true);

							} else if (arg2.contains("COMPOSANT LEADLESS")) {

								cb_type_composant.setItems(
										FXCollections.observableArrayList(
												"RESISTANCES",
												"CAPACITES",
												"LCCC"));
								gp_titre_composant_existant.add(
										new Label("Type de composant"),
										0,
										1);
								gp_titre_composant_existant
										.add(cb_type_composant, 1, 1);
								cb_type_composant.setVisible(true);
							} else if (arg2.contains("COMPOSANT LEADFRAME")) {

								cb_type_composant.setItems(
										FXCollections
												.observableArrayList("QFN"));
								gp_titre_composant_existant.add(
										new Label("Type de composant"),
										0,
										1);
								gp_titre_composant_existant
										.add(cb_type_composant, 1, 1);
								cb_type_composant.setVisible(true);
							} else if (arg2.contains("NOUVEAU COMPOSANT")) {
								System.out.println("[New] " + arg2);
								nouveauComposant();
							} else {
								clear_composants_existants(2);
							}
						}
					}
				});

		cb_type_composant.valueProperty()
				.addListener(new ChangeListener<String>() {

					@Override
					public void changed(
							ObservableValue<? extends String> arg0,
							String arg1,
							String arg2) {
						clear_composants_existants(4);

						cb_composant_existant.getItems().clear();
						if (arg2 != null) {
							if (arg2.contains("BGA") || arg2.contains("CSP")
									|| arg2.contains("WLP")
									|| arg2.contains("SBGA")
									|| arg2.contains("CBGA")
									|| arg2.contains("PCBGA")) {

								if (arg2.equals("BGA")) {
									cb_composant_existant.getItems().addAll(
											Outils.get_list_filename_with_extension(
													path,
													".bga"));
									System.out.println("[Composant] Selected Component Type: " + arg2);
								} else if (arg2.equals("CSP")) {
									cb_composant_existant.getItems().addAll(
											Outils.get_list_filename_with_extension(
													path,
													".csp"));
								} else if (arg2.equals("WLP")) {
									cb_composant_existant.getItems().addAll(
											Outils.get_list_filename_with_extension(
													path,
													".wlp"));
								} else if (arg2.equals("CBGA")) {
									cb_composant_existant.getItems().addAll(
											Outils.get_list_filename_with_extension(
													path,
													".cbga"));
								} else if (arg2.equals("SBGA")) {
									cb_composant_existant.getItems().addAll(
											Outils.get_list_filename_with_extension(
													path,
													".sbga"));
								} else if (arg2.equals("PCBGA")) {
									cb_composant_existant.getItems().addAll(
											Outils.get_list_filename_with_extension(
													path,
													".pcbga"));
								}

								gp_titre_composant_existant
										.add(new Label("Composant"), 0, 2);
								gp_titre_composant_existant
										.add(cb_composant_existant, 1, 2);
								gp_titre_composant_existant
										.add(buttonSauvegarder, 0, 3);
								cb_composant_existant.setVisible(true);

							} else if (arg2.contains("RESISTANCES")
									|| arg2.contains("LCCC")
									|| arg2.contains("CAPACITES")) {

								if (arg2.contains("RESISTANCES")) {
									cb_composant_existant.getItems().addAll(
											Resistance.getResistances());

									gp_titre_composant_existant
											.add(new Label("Format"), 0, 2);
									gp_titre_composant_existant
											.add(cb_composant_existant, 1, 2);
									cb_composant_existant.setVisible(true);
								}
								if (arg2.contains("CAPACITES")) {
									cb_materiau.setItems(
											FXCollections.observableArrayList(
													"X7R",
													"COG"));
									gp_titre_composant_existant
											.add(new Label("Matériau"), 0, 3);
									gp_titre_composant_existant
											.add(cb_materiau, 1, 3);

									gp_titre_composant_existant
											.add(new Label("Capacité"), 0, 2);
									gp_titre_composant_existant
											.add(tfValeurCapacite, 1, 2);

									cb_materiau.setVisible(true);
									tfValeurCapacite.setVisible(true);

								}
								if (arg2.contains("LCCC")) {
									cb_composant_existant.getItems().addAll(
											Outils.get_list_filename_with_extension(
													path,
													".lcc"));
									// cb_composant_existant.getItems()
									// .addAll(LCC.getLCC());

									gp_titre_composant_existant
											.add(new Label("Format"), 0, 2);
									gp_titre_composant_existant
											.add(cb_composant_existant, 1, 2);
									cb_composant_existant.setVisible(true);
								}

							} else if (arg2.contains("QFN")) {
								cb_composant_existant.getItems()
										.addAll(QFN.getComposants());

								gp_titre_composant_existant
										.add(new Label("Format"), 0, 2);
								gp_titre_composant_existant
										.add(cb_composant_existant, 1, 2);
								cb_composant_existant.setVisible(true);
							}
						}
					}
				});

		cb_materiau.valueProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(
					ObservableValue<? extends String> arg0,
					String arg1,
					String arg2) {

				if (arg2 != null) {
					if (arg2.contains("X7R")) {

						cb_composant_existant.getItems()
								.addAll(Capacite.getCapacites());
					}

					if (arg2.contains("COG")) {
						cb_composant_existant.getItems()
								.addAll(Capacite.getCapacites());
					}

					gp_titre_composant_existant.add(new Label("Format"), 0, 4);
					gp_titre_composant_existant
							.add(cb_composant_existant, 1, 4);
					cb_composant_existant.setVisible(true);
				}
			}
		});

		titre_composant_existant.setContent(gp_titre_composant_existant);

		gp_titre_composant_existant.add(new Label("Catégorie composant"), 0, 0);
		gp_titre_composant_existant.add(cb_categorie_composant, 1, 0);

	}

	/* TODO: FIN MENU ONGLET COMPOSANT */

	private void clear_vue() {
		while (hbChoixComposantEtProprietes.getChildren().size() > 1) {
			hbChoixComposantEtProprietes.getChildren().remove(
					hbChoixComposantEtProprietes.getChildren().size() - 1);
		}

		if (hb_resultats_paliers.getChildren().size() > 0)
			hb_resultats_paliers.getChildren()
					.remove(hb_resultats_paliers.getChildren().size() - 1);
		vb_matrice_creuse.getChildren().clear();
		hbMatriceCreuse.getChildren().clear();
	}

	private void rafraichir_vue(String type) {
		clear_vue();
		hb_resultats_paliers.getChildren().add(titre_palier_temperature);
		switch (type) {

		case ".bga":
			vb_matrice_creuse.getChildren().add(gp_matrice_creuse);

			if (((ComposantBGA) composant).getLiaisonPuce()
					.equals("Die Attach")) {
				hbChoixComposantEtProprietes.getChildren()
						.add(1, ComposantBGA.bgaDieAttach);
			} else if (((ComposantBGA) composant).getLiaisonPuce()
					.equals("Underfill")) {
				hbChoixComposantEtProprietes.getChildren()
						.add(1, ComposantBGA.bgaUnderFill);
			}
			hbMatriceCreuse.getChildren().add(vb_matrice_creuse);
			hbMatriceCreuse.setAlignment(Pos.CENTER);
			vb_matrice_creuse.setAlignment(Pos.CENTER);
			break;

		case ".pcbga":
			vb_matrice_creuse.getChildren().add(gp_matrice_creuse);

			hbChoixComposantEtProprietes.getChildren()
					.add(1, ComposantPCBGA.image);

			hbMatriceCreuse.getChildren().add(vb_matrice_creuse);
			hbMatriceCreuse.setAlignment(Pos.CENTER);
			vb_matrice_creuse.setAlignment(Pos.CENTER);
			break;

		case ".csp":
			vb_matrice_creuse.getChildren().add(gp_matrice_creuse);
			hbChoixComposantEtProprietes.getChildren()
					.add(1, vb_matrice_creuse);
			if (((ComposantBGA) composant).getLiaisonPuce()
					.equals("Die Attach")) {
				hbChoixComposantEtProprietes.getChildren()
						.add(1, ComposantCSP.cspDieAttach);
			} else if (((ComposantBGA) composant).getLiaisonPuce()
					.equals("Underfill")) {
				hbChoixComposantEtProprietes.getChildren()
						.add(1, ComposantCSP.cspUnderfill);
			}
			hbMatriceCreuse.getChildren().add(vb_matrice_creuse);
			hbMatriceCreuse.setAlignment(Pos.CENTER);
			vb_matrice_creuse.setAlignment(Pos.CENTER);
			break;

		case ".sbga":
			vb_matrice_creuse.getChildren().add(gp_matrice_creuse);
			hbChoixComposantEtProprietes.getChildren()
					.add(1, vb_matrice_creuse);
			if (((ComposantBGA) composant).getLiaisonPuce()
					.equals("Die Attach")) {
				hbChoixComposantEtProprietes.getChildren()
						.add(1, ComposantSBGA.sbgaDieAttach);
			} else if (((ComposantBGA) composant).getLiaisonPuce()
					.equals("Underfill")) {
				hbChoixComposantEtProprietes.getChildren()
						.add(1, ComposantSBGA.sbgaUnderfill);
			}

			hbMatriceCreuse.getChildren().add(vb_matrice_creuse);
			hbMatriceCreuse.setAlignment(Pos.CENTER);
			vb_matrice_creuse.setAlignment(Pos.CENTER);
			break;

		case "resistance":
			hbChoixComposantEtProprietes.getChildren()
					.add(1, Composant_Resistance.image);
			break;
		case "capacite":
			hbChoixComposantEtProprietes.getChildren()
					.add(1, Composant_Capacite.image);
			break;
		case "lccc":
			vb_matrice_creuse.getChildren().add(gp_matrice_creuse);
			hbMatriceCreuse.getChildren().add(vb_matrice_creuse);
			hbMatriceCreuse.setAlignment(Pos.CENTER);
			vb_matrice_creuse.setAlignment(Pos.CENTER);
			hbChoixComposantEtProprietes.getChildren()
					.add(1, ComposantLCC.image);
			break;
		case ".wlp":
			ComposantWLP.image.setFitWidth(500);
			ComposantWLP.image.setFitHeight(250);

			vb_matrice_creuse.getChildren().add(gp_matrice_creuse);
			hbMatriceCreuse.getChildren().add(vb_matrice_creuse);
			hbMatriceCreuse.setAlignment(Pos.CENTER);
			vb_matrice_creuse.setAlignment(Pos.CENTER);
			hbChoixComposantEtProprietes.getChildren()
					.add(1, ComposantWLP.image);
			break;

		case ".cbga":
			ComposantCBGA.image.setFitWidth(500);
			ComposantCBGA.image.setFitHeight(250);

			vb_matrice_creuse.getChildren().add(gp_matrice_creuse);
			hbMatriceCreuse.getChildren().add(vb_matrice_creuse);
			hbMatriceCreuse.setAlignment(Pos.CENTER);
			vb_matrice_creuse.setAlignment(Pos.CENTER);
			hbChoixComposantEtProprietes.getChildren()
					.add(1, ComposantCBGA.image);
			break;

		case "qfn":
			hbChoixComposantEtProprietes.getChildren()
					.add(1, ComposantQFN.image);
			break;

		default:

		}

		hbChoixComposantEtProprietes.getChildren()
				.addAll(titre_proprietes_composant);
		HBox.setHgrow(titre_proprietes_composant, Priority.ALWAYS);
	}

	public void init_check_affichage_pcb() {

		check_affichage_pcb = new CheckBox("Affichage du PCB");
		check_affichage_pcb.setSelected(true);
		check_affichage_pcb.selectedProperty()
				.addListener(new ChangeListener<Boolean>() {

					@Override
					public void changed(
							ObservableValue<? extends Boolean> observable,
							Boolean oldValue,
							Boolean newValue) {
						if (newValue) {
							hbChoixComposantEtProprietes.getChildren()
									.add(1, pcb.titre_pcb);
						} else {
							hbChoixComposantEtProprietes.getChildren()
									.remove(pcb.titre_pcb);
						}
					}
				});
	}

	public void init_print_button() {
		printButton = new Button("Print");
		onglet_composant.getChildren().add(printButton);
		printButton.setOnAction(
				(ActionEvent event) -> {

					// Creation du job d'impression.
					final PrinterJob printerJob = PrinterJob.createPrinterJob();
					// Affichage de la boite de dialog de configation de
					// l'impression.
					if (printerJob
							.showPrintDialog(this.getScene().getWindow())) {

						Printer printer = printerJob.getPrinter();
						PageLayout pageLayout = printer.createPageLayout(
								Paper.A3,
								PageOrientation.PORTRAIT,
								Printer.MarginType.HARDWARE_MINIMUM);

						double width = this.getWidth();
						double height = this.getHeight();

						PrintResolution resolution = printerJob.getJobSettings()
								.getPrintResolution();

						width /= resolution.getFeedResolution();

						height /= resolution.getCrossFeedResolution();

						double scaleX = pageLayout.getPrintableWidth() / width
								/ 700;
						double scaleY = pageLayout.getPrintableHeight() / height
								/ 600;

						Scale scale = new Scale(scaleX, scaleY);

						this.getTransforms().add(scale);

						// Lancement de l'impression.
						if (printerJob.printPage(pageLayout, this)) {
							// Fin de l'impression.
							printerJob.endJob();
						}
						this.getTransforms().remove(scale);
					}

				});

	}

	public void nouveauComposant() {
		System.out.println("[New] Create New composant");
		nouveauComposant = new NouveauComposant(pcb, this);

		if (nouveauComposant.estInitiliase()) {

			onglet_composant.getChildren().remove(composant);

			composant = nouveauComposant.getComposant();

			cb_compositions_existants.getItems().clear();

			cb_compositions_existants.getItems().addAll(Outils.get_list_filename_with_extension(path));

			tv_temperature = composant.get_tableView_temperature();

			titre_palier_temperature.setContent(tv_temperature);

			hbChoixComposantEtProprietes.getChildren()
					.remove(tv_proprietes_thermiques);

			tv_proprietes_thermiques = composant.get_tableView_proprietes();

			hbChoixComposantEtProprietes.getChildren()
					.add(tv_proprietes_thermiques);

			// onglet_composant.getChildren().add(composant);
		}
	}

	public void init_composant() {
		composant = new Composant();
		onglet_composant.getChildren().add(composant);
	}

	public Composant get_composant() {
		return this.composant;
	}

	public void exportToCsv() {

		fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(new File(Ressources.pathExcel));
		fileChooser.getExtensionFilters().add(
				new ExtensionFilter(
						"Resultat composant Excel (*.csv)",
						"*.csv"));

		File file = fileChooser.showSaveDialog(getScene().getWindow());

		if (file != null) {
			try (FileWriter writer = new FileWriter(file)) {
				writer.write("Resultats composant" + "\n");
				for (TableColumn<
						? extends Resultat_temperature,
						?> colonne : tv_temperature.getColumns()) {

					String colonneTableView = colonne.getText() + ";";
					writer.write(colonneTableView);

				}
				writer.write("\n");

				ObservableList<
						? extends Resultat_temperature> donneesComposant = tv_temperature
								.getItems();

				String stringDonneesPcbComposant = "";

				for (int i = 0; i < donneesComposant.size(); i++) {
					writer.write("\n");
					String ligneTemperatureTableView = donneesComposant.get(i)
							.toString();

					writer.write(ligneTemperatureTableView);
				}

				if (tv_temperature_pcb_composant.getItems() != null) {
					ObservableList<
							? extends Resultat_temperature_pcb> donneesPcbComposant = tv_temperature_pcb_composant
									.getItems();

					for (int i = 0; i < donneesComposant.size(); i++) {
						String s = "\n" + donneesPcbComposant.get(i).toString();
					}

					writer.write("\n\n");
					writer.write("Resultat thermomecanique du PCB composant");
					writer.write("\n");
					for (TableColumn<
							? extends Resultat_temperature,
							?> colonne : tv_temperature_pcb_composant
									.getColumns()) {

						String colonneTableView = colonne.getText() + ";";
						writer.write(colonneTableView);

					}
					writer.write(stringDonneesPcbComposant);
				}

			} catch (IOException e) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Problème lors de la sauvegarde");
				alert.setHeaderText(null);
				alert.setContentText(
						"La ressource est peut-être déjà utilisée");

				alert.showAndWait();

			}
		}

	}

	/**
	 * Cette fonction éfface la séléction de la ComboBox catégorie composant
	 */
	public void clear_cb_categorie_composant() {

		this.cb_categorie_composant.getSelectionModel().clearSelection();

	}

}
