package com.app.ancea;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Cette classe permet est l'interface permettant de créer un nouveau un
 * composant via onglet composant.
 *
 */
public class NouveauComposant extends AnchorPane {

	final private String path = Ressources.pathComposants;

	Button button_annuler, button_ok, buttonGenerationMatriceBilles;

	ComboBox<String> choix_composant;
	ComboBox<String> choix_dimension;
	ComboBox<String> choixLiaisonPuce;
	ComboBox<String> cbFormatLCC;

	Composant composant;

	GridPane gp_form;
	GridPane gp_selection_composant;
	GridPane gp_nouveau_circuit;
	GridPane gp_buttons_form;
	GridPane gpSelectionLiaisonPuce;
	GridPane gpSelectionFormatLCC;
	GridPane gpProprietesFormatLCC;

	ArrayList<CouchePcb> liste_couches_pcb;

	private TextField pas;
	private TextField nombre_billes_x;
	private TextField nombre_billes_y;
	private TextField nombre_ex1;
	private TextField nombre_ey1;
	private TextField nombre_nx;
	private TextField nombre_ny;
	private TextField pastille_x;
	private TextField pastille_y;
	private TextField pastille_z;
	private TextField cuivre_x;
	private TextField cuivre_y;
	private TextField cuivre_z;
	private TextField epaisseurLiaison;
	private TextField resine_x;
	private TextField resine_y;
	private TextField resine_z;
	private TextField pcb_x;
	private TextField pcb_y;

	private TextField nombre_couches;
	private TextField nombre_via_laser;
	private TextField nombre_enterres;

	private boolean initialized;
	private ImageView imageComposant;

	private VBox vboxLCCImageAndMatrix;

	CheckBox billes;
	ArrayList<Object> arrayNodes;

	ScrollPane sp;
	Stage stage;
	Scene scene;

	Button button_generer_pcb;

	TitledPane titre_nouveau_circuit;

	OngletPcb pcb;
	HBox hb_disposition_element_popup, hb_choix_pcb_choix_composant;
	HBox hb_parametres_pcb_image;
	HBox hbMatriceBilles;
	VBox vb_gp_form_gp_selection_composant, vb_parametres_pcb_couche_pcb,
			vbMatriceBillesEtTitre, vbComposantEtLiaison;

	GridPane gpMatriceBilles; // Matrice de billes d'un composant à Billes

	TableView<Propriete> tv_proprietes;
	Propriete epaisseur_total_nominal;
	boolean matriceBillesgeneree;
	int nbBillesMatriceBilles;
	Button buttonCalculNombreBillesMatricebilles;
	Label lab_NombreBillesMatricebilles;
	public OngletComposant ongletComposant;

	/**
	 * Classe permettant de créer un nouveau composant.
	 * 
	 * @param pcb       Permet de créer des pcb pour les CSP et BGA grâce à la
	 *                  classe OngletPcb
	 * @param composant Objet composant generique qui sera instancier en
	 *                  fonction du composant choisi par l'utilisateur.
	 */
	public NouveauComposant(OngletPcb pcb, OngletComposant onglet_composant) {
		System.out.println("[New] Create New composant Class");

		initialized = false;

		setPrefSize(350, 500);
		this.ongletComposant = onglet_composant;

		sp = new ScrollPane();

		this.vboxLCCImageAndMatrix = new VBox(10);

		this.pcb = pcb;

		this.choix_composant = new ComboBox<String>();
		this.choix_composant.getItems().addAll(Ressources.NOUVEAU_COMPOSANTS);

		this.choixLiaisonPuce = new ComboBox<String>();
		this.choixLiaisonPuce.getItems().addAll(Ressources.liaisonPuceBGA);

		this.cbFormatLCC = new ComboBox<String>();
		this.cbFormatLCC.getItems().addAll(LCC.getLCC());

		this.matriceBillesgeneree = false;
		this.nbBillesMatriceBilles = 0;
		this.lab_NombreBillesMatricebilles = new Label(
				"	Matrice de Billes du Composant: "
						+ this.nbBillesMatriceBilles + " Billes");
		init_button(onglet_composant);
		arrayNodes = new ArrayList<>();

		scene = new Scene(sp);

		stage = new Stage();
		stage.setTitle("Nouveau composant");
		// Fenêtre en plein écran
		stage.setMaximized(true);
		stage.setScene(scene);
		stage.initModality(Modality.WINDOW_MODAL);

		/* GridPane contenant les caracteristiques du composant */
		gp_form = new GridPane();
		gp_form.setPadding(new Insets(00, 20, 20, 20));
		gp_form.setHgap(10);
		gp_form.setVgap(10);

		/* GridPane contenant la selection du composant. */
		gp_selection_composant = new GridPane();
		gp_selection_composant.setPadding(new Insets(20, 20, 20, 20));
		gp_selection_composant.setHgap(10);
		gp_selection_composant.setVgap(10);

		gp_selection_composant.add(new Label("Type de composant"), 0, 0);
		gp_selection_composant.add(choix_composant, 1, 0);

		gpSelectionLiaisonPuce = new GridPane();
		gpSelectionLiaisonPuce.setPadding(new Insets(20, 20, 20, 20));
		gpSelectionLiaisonPuce.add(new Label("Liaison Puce"), 0, 0);
		gpSelectionLiaisonPuce.add(choixLiaisonPuce, 1, 0);
		gpSelectionLiaisonPuce.setHgap(10);
		gpSelectionLiaisonPuce.setVgap(10);
		gpSelectionLiaisonPuce.setVisible(false);

		this.gpSelectionFormatLCC = new GridPane();
		this.gpSelectionFormatLCC.setPadding(new Insets(20, 20, 20, 20));
		this.gpSelectionFormatLCC.add(new Label("Format LCC"), 0, 0);
		this.gpSelectionFormatLCC.add(this.cbFormatLCC, 1, 0);
		this.gpSelectionFormatLCC.setHgap(10);
		this.gpSelectionFormatLCC.setVgap(10);
		this.gpSelectionFormatLCC.setVisible(false);

		this.gpProprietesFormatLCC = new GridPane();
		this.gpProprietesFormatLCC.setVisible(false);

		vbComposantEtLiaison = new VBox();
		vbComposantEtLiaison.getChildren().addAll(
				gp_selection_composant,
				gpSelectionLiaisonPuce,
				this.gpSelectionFormatLCC,
				this.gpProprietesFormatLCC);

		gp_buttons_form = new GridPane();
		gp_buttons_form.setHgap(20);

		gpMatriceBilles = new GridPane();

		vbMatriceBillesEtTitre = new VBox();
		vbMatriceBillesEtTitre.getChildren().addAll(
				lab_NombreBillesMatricebilles,
				gpMatriceBilles,
				buttonCalculNombreBillesMatricebilles);
		vbMatriceBillesEtTitre.setVisible(false);

		init_proprietes();

		/* HBox contenant la gridpane du form */
		hb_disposition_element_popup = new HBox(10);

		/*
		 * HBox avec le choix du composant et du PCB(quand BGA est selectionné).
		 * Le PCB est ajouté après dans la fonction
		 * remplir_gridpane_parametre_bga()
		 */
		hb_choix_pcb_choix_composant = new HBox(20);
		hb_choix_pcb_choix_composant.getChildren().addAll(vbComposantEtLiaison);

		/*
		 * VBox contenant les deux HBox précédentes. Affiche d'abord la
		 * selection du composant puis le form associé dans le deuxieme HBox
		 */
		vb_gp_form_gp_selection_composant = new VBox(10);
		vb_gp_form_gp_selection_composant.getChildren().addAll(
				hb_choix_pcb_choix_composant,
				hb_disposition_element_popup,
				vbMatriceBillesEtTitre);

		this.cbFormatLCC.valueProperty()
				.addListener(new ChangeListener<String>() {

					@Override
					public void changed(
							ObservableValue<? extends String> arg0,
							String arg1,
							String arg2) {

						composant = new ComposantLCC(cbFormatLCC.getValue());

						gpProprietesFormatLCC.getChildren().clear();
						String[] str = LCC.getProperties(arg2);
						for (int i = 0; i < str.length; i += 2) {
							gpProprietesFormatLCC.add(new Label(str[i]), i, 0);
							gpProprietesFormatLCC.add(
									new Label(" : " + str[i + 1] + "  "),
									i + 1,
									0);
						}

						buttonGenerationMatriceBilles.fire();

					}
				});

		choix_composant.getSelectionModel().selectedItemProperty()
				.addListener(new ChangeListener<String>() {

					@Override
					public void changed(
							ObservableValue<? extends String> arg0,
							String arg1,
							String arg2) {
						System.out.println("[New] Create New composant Class CHoix de composant: " + arg2);
						switch (arg2) {
						case "BGA":
							gpSelectionLiaisonPuce.setVisible(true);
							buttonCalculNombreBillesMatricebilles
									.setVisible(true);
							buttonGenerationMatriceBilles.setVisible(true);
							init_parametres_pcb();
							remplir_gridpane_parametre_bga();
							break;
						case "WLP":
							gpSelectionLiaisonPuce.setVisible(false);
							buttonCalculNombreBillesMatricebilles
									.setVisible(true);
							buttonGenerationMatriceBilles.setVisible(true);
							remplir_gridpane_parametre_wlp();
							break;
						case "CBGA":
							gpSelectionLiaisonPuce.setVisible(false);
							buttonCalculNombreBillesMatricebilles
									.setVisible(true);
							buttonGenerationMatriceBilles.setVisible(true);
							remplir_gridpane_parametre_cbga();
							break;
						case "PCBGA":
							gpSelectionLiaisonPuce.setVisible(false);
							buttonCalculNombreBillesMatricebilles
									.setVisible(true);
							buttonGenerationMatriceBilles.setVisible(true);
							init_parametres_pcb();
							remplir_gridpane_parametre_pcbga();
							break;
						case "CSP":
							gpSelectionLiaisonPuce.setVisible(true);
							buttonCalculNombreBillesMatricebilles.setVisible(true);
							buttonGenerationMatriceBilles.setVisible(true);
							init_parametres_pcb();
							remplir_gridpane_parametre_bga();
							break;
						case "SBGA":
							gpSelectionLiaisonPuce.setVisible(true);
							buttonCalculNombreBillesMatricebilles
									.setVisible(true);
							buttonGenerationMatriceBilles.setVisible(true);
							init_parametres_pcb();
							remplir_gridpane_parametre_sbga();
							break;
						case "Résistance":
							gpSelectionLiaisonPuce.setVisible(false);
							init_parametres_resistance();
							remplir_gridpane_parametre_resitance();
							break;

						case "LCC":
							gpSelectionFormatLCC.setVisible(true);
							gpProprietesFormatLCC.setVisible(true);
							remplir_gridpane_parametre_lcc();
							break;
						default:
							gpSelectionLiaisonPuce.setVisible(false);
							break;
						}
					}
				});

		choixLiaisonPuce.getSelectionModel().selectedItemProperty()
				.addListener(new ChangeListener<String>() {

					@Override
					public void changed(
							ObservableValue<? extends String> arg0,
							String arg1,
							String arg2) {
						if (arg2 != null) {
							System.out.println("[New] Create New composant Class CHoix de Liaision = " + arg2);
							switch (arg2) {
							case "Underfill":

								epaisseurLiaison
										.setFont(new Font("SanSerif", 15));
								epaisseurLiaison.setPromptText(
										"Epaisseur Underfill (mm)");
								epaisseurLiaison.getStyleClass()
										.add("field-background");

								switch (choix_composant.getValue()) {

								case "SBGA":
									imageComposant = ComposantSBGA.sbgaUnderfill;
									break;

								case "BGA":
									imageComposant = ComposantBGA.bgaUnderFill;
									break;

								case "CSP":
									imageComposant = ComposantCSP.cspUnderfill;
									break;

								default:
									imageComposant = ComposantBGA.bgaUnderFill;

								}

								imageComposant.setFitHeight(180);
								imageComposant.setFitWidth(400);
								hb_parametres_pcb_image.getChildren().clear();
								hb_parametres_pcb_image.getChildren().addAll(
										gp_nouveau_circuit,
										imageComposant);
								break;

							case "Die Attach":
								epaisseurLiaison
										.setFont(new Font("SanSerif", 15));
								epaisseurLiaison.setPromptText(
										"Epaisseur Die Attach (mm)");
								epaisseurLiaison.getStyleClass()
										.add("field-background");

								switch (choix_composant.getValue()) {

								case "SBGA":
									imageComposant = ComposantSBGA.sbgaDieAttach;
									break;

								case "BGA":
									imageComposant = ComposantBGA.bgaDieAttach;
									break;

								case "CSP":
									imageComposant = ComposantCSP.cspDieAttach;
									break;

								default:
									imageComposant = ComposantBGA.bgaDieAttach;

								}

								imageComposant.setFitHeight(180);
								imageComposant.setFitWidth(400);
								hb_parametres_pcb_image.getChildren().clear();
								hb_parametres_pcb_image.getChildren().addAll(
										gp_nouveau_circuit,
										imageComposant);
								break;
							}
						}
					}
				});

		hb_disposition_element_popup.getChildren().add(gp_form);

		sp.setContent(vb_gp_form_gp_selection_composant);

		stage.showAndWait();
	}

	/**
	 * Initialisation de la combobox servant à choisir le format de la
	 * resistance. Elle utilise les formats qui sont statics et se trouvant dans
	 * Resistance.java Pour avoir une recherche sur la combo on utilise la
	 * classe ComboBoxAutoComplete
	 */
	protected void init_parametres_resistance() {
		composant = new Composant_Resistance();
		this.choix_dimension = new ComboBox<String>();
		ObservableList<String> resistances = FXCollections
				.observableArrayList(Resistance.getResistances());
		this.choix_dimension.setItems(resistances);
		this.choix_dimension.valueProperty().addListener(
				(obs, oldv, newv) -> ((Composant_Resistance) composant)
						.setDimensionComposant(
								Resistance.getDimension((newv))));
	}

	public void showValue(Resistance r) {
		System.out.println(r);
	}

	/**
	 * GridPane contenant les parametres du PCB lié au BGA
	 */
	public void init_parametres_pcb() {

		/* VBox servant à contenir la GridPane et l'image */
		hb_parametres_pcb_image = new HBox(20);

		gp_nouveau_circuit = new GridPane();
		gp_nouveau_circuit.setHgap(15);
		gp_nouveau_circuit.setVgap(15);
		gp_nouveau_circuit.setPadding(new Insets(0, 0, 0, 20));

		nombre_couches = generer_textfield("Nombre couches", false);
		nombre_via_laser = generer_textfield("Nombre via-laser", false);
		nombre_enterres = generer_textfield("Nombre Trous enterres", false);

		gp_nouveau_circuit.add(nombre_couches, 0, 0);
		gp_nouveau_circuit.add(nombre_via_laser, 0, 1);
		gp_nouveau_circuit.add(nombre_enterres, 0, 2);
		gp_nouveau_circuit.add(pcb.choix_resine, 0, 3);
		gp_nouveau_circuit.add(button_generer_pcb, 0, 4);

		/* TitledPane contenant les parametres du circuit et l'image */

		titre_nouveau_circuit = new TitledPane();
		titre_nouveau_circuit.setPadding(new Insets(0, 0, 0, 0));
		titre_nouveau_circuit.setText("Nouveau circuit");
		titre_nouveau_circuit.setCollapsible(false);
		titre_nouveau_circuit.setContent(hb_parametres_pcb_image);

		/*
		 * VBox contenant les couches paramètres PCB, image du PCB, couches PCB
		 * et Matrice Billes
		 */
		vb_parametres_pcb_couche_pcb = new VBox(20);
		vb_parametres_pcb_couche_pcb.setPadding(new Insets(0, 0, 0, 20));
		vb_parametres_pcb_couche_pcb.getChildren().addAll(
				titre_nouveau_circuit,
				pcb.titre_pcb,
				vbMatriceBillesEtTitre);

		switch (choix_composant.getSelectionModel().selectedItemProperty()
				.getValue()) {

		case "BGA":
			imageComposant = ComposantBGA.bgaDieAttach;
			composant = new ComposantBGA(pcb);
			break;

		case "CSP":
			imageComposant = ComposantCSP.cspDieAttach;
			composant = new ComposantCSP(pcb);
			break;

		case "WLP":
			imageComposant = ComposantWLP.imageNouveauComposant;
			break;

		case "SBGA":
			imageComposant = ComposantSBGA.sbgaDieAttach;
			composant = new ComposantSBGA(pcb);
			break;
		case "CBGA":
			imageComposant = ComposantCBGA.image;
			break;
		case "PCBGA":
			imageComposant = ComposantPCBGA.image;
			composant = new ComposantPCBGA(pcb);
			break;
		}

		imageComposant.setFitHeight(180);
		imageComposant.setFitWidth(400);
		hb_parametres_pcb_image.getChildren().addAll(gp_nouveau_circuit, imageComposant);
	}

	/**
	 * Initialisation des proprietes du PCB
	 */
	@SuppressWarnings("unchecked")
	public void init_proprietes() {

		tv_proprietes = new TableView<>();

		tv_proprietes.resize(50, 70);

		tv_proprietes
				.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		epaisseur_total_nominal = new Propriete("Epaisseur Total Nominal");

		final ObservableList<
				Propriete> list_proprietes_thermiques = FXCollections
						.observableArrayList(pcb.p_epaisseur_totale_nominale);

		TableColumn<
				Propriete,
				String> colonneVariable = new TableColumn<Propriete, String>(
						"Proprietes");
		colonneVariable
				.setCellValueFactory(c -> c.getValue().get_nom_variable());

		TableColumn<
				Propriete,
				String> colonneResultat = new TableColumn<Propriete, String>(
						"Resultats");
		colonneResultat.setCellValueFactory(c -> c.getValue().get_resultat());

		tv_proprietes.setItems(list_proprietes_thermiques);

		tv_proprietes.getColumns().addAll(colonneVariable, colonneResultat);
	}

	/**
	 * Génère un textfield stylisé et l'ajoute à une ArrayList d'Object (type
	 * Node).
	 * 
	 * @param prompText text inscrit dans le Textfield indiquant quelle valeur
	 *                  écrire.
	 * @return retourne un TextField avec un style appliqué.
	 */
	public TextField generer_textfield(String prompText, boolean ajoutArray) {
		TextField tf = new TextField();
		tf.setFont(new Font("SanSerif", 15));
		tf.setPromptText(prompText);
		tf.getStyleClass().add("field-background");
		if (ajoutArray) {
			arrayNodes.add(tf);
		}
		return tf;
	}

	/**
	 * Cette fonction vérifie si la chaine de caractère est valide
	 * 
	 */
	private boolean lineIsOk(String s) {
		return s == null || s.isEmpty() || !checkTextField(s);
	}

	/**
	 * Cette fonction vérifie que les champs pour créer un composant ne sont pas
	 * nuls
	 * 
	 * @param Le composant que l'on essaie de créer
	 * @return Un booléen concernant l'état des champs
	 */
	public boolean checkIsOk(String composant_choisi) {
		String error = "";
		if (composant_choisi.equals("WLP") || composant_choisi.equals("CBGA")) {

			if (lineIsOk(pas.getText())) {
				pas.setStyle("-fx-border-color: red;");
				error += "- Pas\n";
			} else {
				pas.setStyle("-fx-border-color: default;");
			}
			if (lineIsOk(pastille_x.getText())) {
				pastille_x.setStyle("-fx-border-color: red;");
				error += "- Puce x\n";
			} else {
				pastille_x.setStyle("-fx-border-color: default;");
			}
			if (lineIsOk(pastille_y.getText())) {
				pastille_y.setStyle("-fx-border-color: red;");
				error += "- Puce y\n";
			} else {
				pastille_y.setStyle("-fx-border-color: default;");
			}
			if (lineIsOk(pastille_z.getText())) {
				pastille_z.setStyle("-fx-border-color: red;");
				error += "- Puce z\n";
			} else {
				pastille_z.setStyle("-fx-border-color: default;");
			}
			if (lineIsOk(nombre_billes_x.getText())) {
				nombre_billes_x.setStyle("-fx-border-color: red;");
				error += "- Nombre de billes x\n";
			} else {
				nombre_billes_x.setStyle("-fx-border-color: default;");
			}
			if (lineIsOk(nombre_billes_y.getText())) {
				nombre_billes_y.setStyle("-fx-border-color: red;");
				error += "- Nombre de billes y\n";
			} else {
				nombre_billes_y.setStyle("-fx-border-color: default;");
			}

		} else if (composant_choisi.equals("BGA")
				|| composant_choisi.equals("CSP")
				|| composant_choisi.equals("SBGA")) {

			if (lineIsOk(this.nombre_couches.getText())) {
				this.nombre_couches.setStyle("-fx-border-color: red;");
				error += "- Nombre Couches\n";
			} else {
				this.nombre_couches.setStyle("-fx-border-color: default;");
			}
			if (lineIsOk(this.nombre_via_laser.getText())) {
				this.nombre_via_laser.setStyle("-fx-border-color: red;");
				error += "- Nombre via laser\n";
			} else {
				this.nombre_via_laser.setStyle("-fx-border-color: default;");
			}
			if (lineIsOk(this.nombre_enterres.getText())) {
				this.nombre_enterres.setStyle("-fx-border-color: red;");
				error += "- Nombre Trou enterres\n";
			} else {
				this.nombre_enterres.setStyle("-fx-border-color: default;");
			}
			if (this.choixLiaisonPuce.getValue() == null) {
				choixLiaisonPuce.setStyle("-fx-border-color: red;");
				error += "- Laison Puce\n";
			} else {
				choixLiaisonPuce.setStyle("-fx-border-color: default;");
			}
			if (lineIsOk(pas.getText())) {
				pas.setStyle("-fx-border-color: red;");
				error += "- Pas\n";
			} else {
				pas.setStyle("-fx-border-color: default;");
			}
			if (lineIsOk(nombre_billes_x.getText())) {
				nombre_billes_x.setStyle("-fx-border-color: red;");
				error += "- Nombre de billes x\n";
			} else {
				nombre_billes_x.setStyle("-fx-border-color: default;");
			}
			if (lineIsOk(nombre_billes_y.getText())) {
				nombre_billes_y.setStyle("-fx-border-color: red;");
				error += "- Nombre de billes y\n";
			} else {
				nombre_billes_y.setStyle("-fx-border-color: default;");
			}
			if (lineIsOk(pastille_x.getText())) {
				pastille_x.setStyle("-fx-border-color: red;");
				error += "- Puce x\n";
			} else {
				pastille_x.setStyle("-fx-border-color: default;");
			}
			if (lineIsOk(pastille_y.getText())) {
				pastille_y.setStyle("-fx-border-color: red;");
				error += "- Puce y\n";
			} else {
				pastille_y.setStyle("-fx-border-color: default;");
			}
			if (lineIsOk(pastille_z.getText())) {
				pastille_z.setStyle("-fx-border-color: red;");
				error += "- Puce z\n";
			} else {
				pastille_z.setStyle("-fx-border-color: default;");
			}
			if (lineIsOk(epaisseurLiaison.getText())) {
				epaisseurLiaison.setStyle("-fx-border-color: red;");
				error += "- Epaisseur Liaison\n";
			} else {
				epaisseurLiaison.setStyle("-fx-border-color: default;");
			}
			if (composant_choisi.equals("SBGA")) {
				if (lineIsOk(cuivre_x.getText())) {
					cuivre_x.setStyle("-fx-border-color: red;");
					error += "- Cuivre x\n";
				} else {
					cuivre_x.setStyle("-fx-border-color: default;");
				}
				if (lineIsOk(cuivre_y.getText())) {
					cuivre_y.setStyle("-fx-border-color: red;");
					error += "- Cuivre y\n";
				} else {
					cuivre_y.setStyle("-fx-border-color: default;");
				}
				if (lineIsOk(cuivre_z.getText())) {
					cuivre_z.setStyle("-fx-border-color: red;");
					error += "- Cuivre z\n";
				} else {
					cuivre_z.setStyle("-fx-border-color: default;");
				}
			} else {
				if (lineIsOk(resine_x.getText())) {
					resine_x.setStyle("-fx-border-color: red;");
					error += "- Resine x\n";
				} else {
					resine_x.setStyle("-fx-border-color: default;");
				}
				if (lineIsOk(resine_y.getText())) {
					resine_y.setStyle("-fx-border-color: red;");
					error += "- Resine y\n";
				} else {
					resine_y.setStyle("-fx-border-color: default;");
				}
				if (lineIsOk(resine_z.getText())) {
					resine_z.setStyle("-fx-border-color: red;");
					error += "- Resine z\n";
				} else {
					resine_z.setStyle("-fx-border-color: default;");
				}
			}
			if (lineIsOk(pcb_x.getText())) {
				pcb_x.setStyle("-fx-border-color: red;");
				error += "- PCB x\n";
			} else {
				pcb_x.setStyle("-fx-border-color: default;");
			}
			if (lineIsOk(pcb_y.getText())) {
				pcb_y.setStyle("-fx-border-color: red;");
				error += "- PCB y\n";
			} else {
				pcb_y.setStyle("-fx-border-color: default;");
			}

		} else if (composant_choisi.equals("PCBGA")) {

			if (lineIsOk(this.nombre_couches.getText())) {
				this.nombre_couches.setStyle("-fx-border-color: red;");
				error += "- Nombre Couches\n";
			} else {
				this.nombre_couches.setStyle("-fx-border-color: default;");
			}
			if (lineIsOk(this.nombre_via_laser.getText())) {
				this.nombre_via_laser.setStyle("-fx-border-color: red;");
				error += "- Nombre via laser\n";
			} else {
				this.nombre_via_laser.setStyle("-fx-border-color: default;");
			}
			if (lineIsOk(this.nombre_enterres.getText())) {
				this.nombre_enterres.setStyle("-fx-border-color: red;");
				error += "- Nombre Trou enterres\n";
			} else {
				this.nombre_enterres.setStyle("-fx-border-color: default;");
			}
			if (lineIsOk(pas.getText())) {
				pas.setStyle("-fx-border-color: red;");
				error += "- Pas\n";
			} else {
				pas.setStyle("-fx-border-color: default;");
			}
			if (lineIsOk(nombre_billes_x.getText())) {
				nombre_billes_x.setStyle("-fx-border-color: red;");
				error += "- Nombre de billes x\n";
			} else {
				nombre_billes_x.setStyle("-fx-border-color: default;");
			}
			if (lineIsOk(nombre_billes_y.getText())) {
				nombre_billes_y.setStyle("-fx-border-color: red;");
				error += "- Nombre de billes y\n";
			}
			if (lineIsOk(pcb_x.getText())) {
				pcb_x.setStyle("-fx-border-color: red;");
				error += "- PCB x\n";
			} else {
				pcb_x.setStyle("-fx-border-color: default;");
			}
			if (lineIsOk(pcb_y.getText())) {
				pcb_y.setStyle("-fx-border-color: red;");
				error += "- PCB y\n";
			} else {
				pcb_y.setStyle("-fx-border-color: default;");
			}
		} else if (composant_choisi.equals("LCC")) {
			if (lineIsOk(pas.getText())) {
				pas.setStyle("-fx-border-color: red;");
				error += "- Pas\n";
			} else {
				pas.setStyle("-fx-border-color: default;");
			}
			if (lineIsOk(pastille_x.getText())) {
				pastille_x.setStyle("-fx-border-color: red;");
				error += "- Puce x\n";
			} else {
				pastille_x.setStyle("-fx-border-color: default;");
			}
			if (lineIsOk(pastille_y.getText())) {
				pastille_y.setStyle("-fx-border-color: red;");
				error += "- Puce y\n";
			} else {
				pastille_y.setStyle("-fx-border-color: default;");
			}
			if (lineIsOk(pastille_z.getText())) {
				pastille_z.setStyle("-fx-border-color: red;");
				error += "- Puce z\n";
			} else {
				pastille_z.setStyle("-fx-border-color: default;");
			}
			if (lineIsOk(nombre_billes_x.getText())) {
				nombre_billes_x.setStyle("-fx-border-color: red;");
				error += "- Nombre de billes x\n";
			} else {
				nombre_billes_x.setStyle("-fx-border-color: default;");
			}
			if (lineIsOk(nombre_billes_y.getText())) {
				nombre_billes_y.setStyle("-fx-border-color: red;");
				error += "- Nombre de billes y\n";
			} else {
				nombre_billes_y.setStyle("-fx-border-color: default;");
			}

		} else {

			if (lineIsOk(pas.getText())) {
				pas.setStyle("-fx-border-color: red;");
				error += "- Pas\n";
			} else {
				pas.setStyle("-fx-border-color: default;");
			}
			if (lineIsOk(nombre_billes_x.getText())) {
				nombre_billes_x.setStyle("-fx-border-color: red;");
				error += "- Nombre de billes x\n";
			} else {
				nombre_billes_x.setStyle("-fx-border-color: default;");
			}
			if (lineIsOk(nombre_billes_y.getText())) {
				nombre_billes_y.setStyle("-fx-border-color: red;");
				error += "- Nombre de billes y\n";
			} else {
				nombre_billes_y.setStyle("-fx-border-color: default;");
			}
			if (lineIsOk(pastille_x.getText())) {
				pastille_x.setStyle("-fx-border-color: red;");
				error += "- Puce x\n";
			} else {
				pastille_x.setStyle("-fx-border-color: default;");
			}
			if (lineIsOk(pastille_y.getText())) {
				pastille_y.setStyle("-fx-border-color: red;");
				error += "- Puce y\n";
			} else {
				pastille_y.setStyle("-fx-border-color: default;");
			}
			if (lineIsOk(pastille_z.getText())) {
				pastille_z.setStyle("-fx-border-color: red;");
				error += "- Puce z\n";
			} else {
				pastille_z.setStyle("-fx-border-color: default;");
			}
			if (lineIsOk(resine_x.getText())) {
				resine_x.setStyle("-fx-border-color: red;");
				error += "- Resine x\n";
			} else {
				resine_x.setStyle("-fx-border-color: default;");
			}
			if (lineIsOk(resine_y.getText())) {
				resine_y.setStyle("-fx-border-color: red;");
				error += "- Resine y\n";
			} else {
				resine_y.setStyle("-fx-border-color: default;");
			}
			if (lineIsOk(resine_z.getText())) {
				resine_z.setStyle("-fx-border-color: red;");
				error += "- Resine z\n";
			} else {
				resine_z.setStyle("-fx-border-color: default;");
			}
			if (lineIsOk(pcb_x.getText())) {
				pcb_x.setStyle("-fx-border-color: red;");
				error += "- PCB x\n";
			} else {
				pcb_x.setStyle("-fx-border-color: default;");
			}
			if (lineIsOk(pcb_y.getText())) {
				pcb_y.setStyle("-fx-border-color: red;");
				error += "- PCB y\n";
			} else {
				pcb_y.setStyle("-fx-border-color: default;");
			}
		}
		if (error.length() > 0) {
			// Génération d'une erreur
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Paramètres invalides !");
			alert.setHeaderText("Veuillez remplir les champs en rouge");
			alert.setContentText("Paramètres incorrects:\n" + error);
			alert.showAndWait();
			return false;

		} else {

			return true;
		}

	}

	/**
	 * @param type String étant le type de composant à sauvegarder.
	 * @return retourne un String qui est le nom du composant à sauvegarder.
	 * 
	 */
	public String popup_sauvegarde_nom(String type) {
		TextInputDialog dialog = new TextInputDialog("Sauvegarde fichier");

		dialog.setTitle("Sauvegarde du composant");
		dialog.setHeaderText("Entrez uniquement le nom du composant");
		dialog.setContentText("Nom " + type + " :");

		try {
			Optional<String> result = dialog.showAndWait();
			return result.get();
		} catch (NoSuchElementException nse) {

		}

		return null;
	}

	/**
	 * Initialse les boutons de l'interface NouveauComposant. button_annuler
	 * ferme la fênetre. button_ok valide le choix et crée le nouvel objet.
	 * button_generer_pcb genere un nouveau pcb à partir de l'objet OngletPcb
	 * instancié dans le constructeur.
	 * 
	 */
	public void init_button(OngletComposant onglet_composant) {

		button_annuler = new Button("  Annuler  ");
		button_ok = new Button(" Valider ");
		button_generer_pcb = new Button(" Générer circuit ");
		buttonGenerationMatriceBilles = new Button(" Générer Matrice Billes");
		buttonGenerationMatriceBilles.setVisible(false);
		buttonCalculNombreBillesMatricebilles = new Button(
				" Calcul Nombre Billes");
		buttonCalculNombreBillesMatricebilles.setVisible(false);

		button_annuler.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {

				onglet_composant.clear_cb_categorie_composant();
				stage.close();

			}

		});

		button_generer_pcb.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				int trou, viaLaser, trouEnterre;

				if (!(nombre_couches.getText().length() == 0)
						&& !(nombre_via_laser.getText().length() == 0)
						&& !(nombre_enterres.getText().length() == 0)) {

					// Au cas où initialement il y avait une erreur
					nombre_couches.setStyle("-fx-border-color: default;");
					nombre_via_laser.setStyle("-fx-border-color: default;");
					nombre_enterres.setStyle("-fx-border-color: default;");

					pcb.vb_pcb.getChildren().clear();

					trou = Integer.parseInt(nombre_couches.getText());
					viaLaser = Integer.parseInt(nombre_via_laser.getText());
					trouEnterre = Integer.parseInt(nombre_enterres.getText());

					// Vérification de la parité du nombre de couches
					if (trou > 0 && trou % 2 == 0) {

						// Au cas où initialement il y avait une erreur sur le
						// nombre de couches
						nombre_couches.setStyle("-fx-border-color: default;");

						pcb.pcb = new Pcb(trou, viaLaser, trouEnterre);
						liste_couches_pcb = pcb.pcb.get_liste_couches_pcb();
						pcb.liste_couches_pcb = liste_couches_pcb;
						pcb.button_calculer.setVisible(true);
						pcb.vb_pcb.getChildren().addAll(liste_couches_pcb);
					} else {

						nombre_couches.setStyle("-fx-border-color: red;");

						Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("Erreur generation PCB");
						alert.setHeaderText("Problème de valeur");
						alert.setContentText(
								"Le nombre de couches est uniquement paire");
						alert.showAndWait();
					}
				} else {
					String erreur = "";
					if (nombre_couches.getText().length() == 0) {
						nombre_couches.setStyle("-fx-border-color: red;");
						erreur += "- Nombre de couches\n";
					} else {
						nombre_couches.setStyle("-fx-border-color: default;");
					}
					if (nombre_via_laser.getText().length() == 0) {
						nombre_via_laser.setStyle("-fx-border-color: red;");
						erreur += "- Nombre via laser\n";
					} else {
						nombre_via_laser.setStyle("-fx-border-color: default;");
					}
					if (nombre_enterres.getText().length() == 0) {
						nombre_enterres.setStyle("-fx-border-color: red;");
						erreur += "- Nombre de trous enterrés\n";
					} else {
						nombre_enterres.setStyle("-fx-border-color: default;");
					}
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Erreur generation PCB");
					alert.setHeaderText(
							"Veuillez compléter les champs en rouge");
					alert.setContentText("Paramètres incorrects:\n" + erreur);
					alert.showAndWait();
				}

			}
		});

		button_ok.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {

				switch (choix_composant.getSelectionModel().getSelectedItem()) {
				case "BGA":

					if (checkIsOk("BGA")) {
						creation_bga();
						initialized = true;
					}
					break;
				case "WLP":
					if (checkIsOk("WLP")) {
						creation_wlp();
						initialized = true;
					}
					break;
				case "CBGA":
					if (checkIsOk("CBGA")) {
						creation_cbga();
						initialized = true;
					}
					break;
				case "CSP":
					if (checkIsOk("CSP")) {
						creation_csp();
						initialized = true;
					}
					break;
				case "SBGA":
					if (checkIsOk("SBGA")) {
						creation_sbga();
						initialized = true;
					}
					break;
				case "Résistance":
					if (checkIsOk("Default")) {
						creation_resistance();
						initialized = true;
					}
					break;
				case "PCBGA":
					if (checkIsOk("PCBGA")) {
						creation_pcbga();
						initialized = true;
					}
					break;
				case "LCC":
					// if (checkIsOk("LCC")) {
					creationLCC();
					initialized = true;
					// }
					break;
				default:
					System.out.println(
							"La partie de code de ce nouveau type de composant n'a pas encore été créée");
					break;
				}

			}
		});

		buttonGenerationMatriceBilles.setOnAction(event -> {
			gpMatriceBilles.getChildren().clear();

			if (choix_composant.getValue().equals("BGA")
					|| choix_composant.getValue().equals("CSP")
					|| choix_composant.getValue().equals("WLP")
					|| choix_composant.getValue().equals("SBGA")
					|| choix_composant.getValue().equals("CBGA")
					|| choix_composant.getValue().equals("PCBGA")) {

				if (!lineIsOk(nombre_billes_x.getText())
						&& !lineIsOk(nombre_billes_y.getText())) {

					nombre_billes_x.setStyle("-fx-border-color: default;");
					nombre_billes_y.setStyle("-fx-border-color: default;");

					gpMatriceBilles = Outils.generer_matrice_Bille(
							gpMatriceBilles,
							Integer.valueOf(nombre_billes_x.getText()),
							Integer.valueOf(nombre_billes_y.getText()));

					this.matriceBillesgeneree = true;
					buttonCalculNombreBillesMatricebilles.setVisible(true);
					buttonGenerationMatriceBilles.setVisible(true);
					vbMatriceBillesEtTitre.setVisible(true);
					int nombreBilles = Integer
							.valueOf(nombre_billes_x.getText())
							* Integer.valueOf(nombre_billes_y.getText());
					this.lab_NombreBillesMatricebilles.setText(
							"Matrice de Billes du Composant: " + nombreBilles
									+ " Billes");
					this.clickButtonCalculNombreBillesMatriceBilles();
				} else {
					nombre_billes_x.setStyle("-fx-border-color: red;");
					nombre_billes_y.setStyle("-fx-border-color: red;");
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Erreur génération Matrice de Billes");
					alert.setHeaderText("Problème de valeur");
					alert.setContentText(
							"Veuillez remplir les champs en rouge");
					alert.showAndWait();
				}
			} else {
				if (this.cbFormatLCC.getValue() != null) {
					double nix = ((ComposantLCC) composant).getNombre_nix();
					double niy = ((ComposantLCC) composant).getNombre_niy();

					gpMatriceBilles = Outils.generer_matrice_Bille(
							gpMatriceBilles,
							Integer.valueOf((int) nix),
							Integer.valueOf((int) niy));
					if (choix_composant.getValue().equals("LCC")) {
						Outils.traitement_matrice_lcc(
								gpMatriceBilles,
								Integer.valueOf((int) nix),
								Integer.valueOf((int) niy));

						this.matriceBillesgeneree = true;
						buttonCalculNombreBillesMatricebilles.setVisible(true);
						buttonGenerationMatriceBilles.setVisible(true);
						vbMatriceBillesEtTitre.setVisible(true);
						int nombreBilles = Integer.valueOf((int) nix)
								* Integer.valueOf((int) niy);
						this.lab_NombreBillesMatricebilles.setText(
								"Matrice de Billes du Composant: "
										+ nombreBilles + " Billes");
						this.clickButtonCalculNombreBillesMatriceBilles();
					}
				}
			}

		});

		buttonCalculNombreBillesMatricebilles.setOnAction(event -> {
			this.nbBillesMatriceBilles = 0;
			for (Node node : gpMatriceBilles.getChildren()) {
				if (node instanceof Button) {
					if (((Button) node).getText().equals("●")) {
						this.nbBillesMatriceBilles++;
					}
				}
			}
			this.lab_NombreBillesMatricebilles.setText(
					"	Matrice de Billes du Composant: "
							+ this.nbBillesMatriceBilles + " Billes");
		});

	}

	private void clickButtonCalculNombreBillesMatriceBilles() {
		this.nbBillesMatriceBilles = 0;
		for (Node node : gpMatriceBilles.getChildren()) {
			if (node instanceof Button) {
				if (((Button) node).getText().equals("●")) {
					this.nbBillesMatriceBilles++;
				}
			}
		}
		this.lab_NombreBillesMatricebilles.setText(
				"	Matrice de Billes du Composant: "
						+ this.nbBillesMatriceBilles + " Billes");
	}

	public boolean estInitiliase() {
		return initialized;
	}

	protected void creation_resistance() {

		String file = popup_sauvegarde_nom("Resistance");

		if (file != null) {
			((Composant_Resistance) composant).setNom(file);

			try {
				PrintWriter pw = new PrintWriter(
						new BufferedWriter(
								new FileWriter(
										new File(path + file + ".res"))));
				pw.print(composant.toString());
				pw.close();
			} catch (IOException ioe) {
				Logger.getLogger(NouveauComposant.class.getName())
						.log(Level.SEVERE, null, ioe);
			}
		}

		stage.close();
	}

	/**
	 * Creation d'un nouveau Composant de type CSP. Il est pareil que le type
	 * BGA à l'exception du CTER qui est en plus dans les valeurs calculées
	 */
	protected void creation_csp() {
		pcb.button_calculer.fire();
		composant = new ComposantCSP(
				pcb,
				"",
				getPastille_x(),
				getPastille_y(),
				getPastille_z(),
				getPas(),
				getPresence_billes(),
				(int) getPcb_x(),
				(int) getPcb_y(),
				getResine_x(),
				getResine_y(),
				getResine_z(),
				getNombre_billes_x(),
				getNombre_billes_y(),
				choixLiaisonPuce.getValue(),
				getEpaisseurLiaison());

		String file = popup_sauvegarde_nom("CSP");
		composant.setNom(file);

		if (file != null && get_couches_pcb_bga().size() > 0) {

			System.out.println(this.toString());

			try {

				PrintWriter pw = new PrintWriter(
						new BufferedWriter(
								new FileWriter(
										new File(path + file + ".csp"))));
				pw.println(pcb.get_Resine().toString());
				for (int i = 0; i < get_couches_pcb_bga().size(); i++) {

					pw.println((get_couches_pcb_bga().get(i).toString()));
				}
				pw.println(composant.toString());

				// Ecriture de la matrice de Billes
				pw.println("Matrice Billes:");

				// Si on a pas générer la matrice de billes on la prend pleine
				if (!this.matriceBillesgeneree) {

					gpMatriceBilles = Outils.generer_matrice_Bille(
							gpMatriceBilles,
							Integer.valueOf(nombre_billes_x.getText()),
							Integer.valueOf(nombre_billes_y.getText()));
				}

				// Transformation du GridPane en Tableau
				// Node[][] gridPaneNodes = new Node[gpMatriceBilles
				// .getRowCount()][gpMatriceBilles.getColumnCount()];

				// La fonction getRowCount() non dispo sous java 8
				int nombreLignes = Integer.valueOf(nombre_billes_y.getText())
						+ 2;
				// La fonction getColumnCount() non dispo sous java 8
				int nombreColonnes = Integer.valueOf(nombre_billes_x.getText())
						+ 2;

				Node[][] gridPaneNodes = new Node[nombreLignes][nombreColonnes];

				for (Node child : gpMatriceBilles.getChildren()) {
					Integer column = GridPane.getColumnIndex(child);
					Integer row = GridPane.getRowIndex(child);
					if (column != null && row != null) {
						gridPaneNodes[row][column] = child;
					}
				}

				// La première et la dernière ligne ainsi que colonne ne sont
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

				pw.close();
			} catch (IOException ex) {
				Logger.getLogger(NouveauComposant.class.getName())
						.log(Level.SEVERE, null, ex);
			}
		}

		stage.close();

	}

	protected void creation_cbga() {
		composant = new ComposantCBGA(
				"",
				getPastille_x(),
				getPastille_y(),
				getPastille_z(),
				getPas(),
				getPresence_billes(),
				getNombre_billes_x(),
				getNombre_billes_y());

		String file = popup_sauvegarde_nom("CBGA");

		composant.setNom(file);

		try {

			PrintWriter pw = new PrintWriter(
					new BufferedWriter(
							new FileWriter(new File(path + file + ".cbga"))));

			pw.println(composant.toString());

			// Si on a pas générer la matrice de billes on la prend pleine
			if (!this.matriceBillesgeneree) {

				gpMatriceBilles = Outils.generer_matrice_Bille(
						gpMatriceBilles,
						Integer.valueOf(nombre_billes_x.getText()),
						Integer.valueOf(nombre_billes_y.getText()));
			}

			// La fonction getRowCount() non dispo sous java 8
			int nombreLignes = Integer.valueOf(nombre_billes_y.getText()) + 2;
			// La fonction getColumnCount() non dispo sous java 8
			int nombreColonnes = Integer.valueOf(nombre_billes_x.getText()) + 2;

			// Transformation du GridPane en Tableau
			// Node[][] gridPaneNodes = new Node[gpMatriceBilles
			// .getRowCount()][gpMatriceBilles.getColumnCount()];

			Node[][] gridPaneNodes = new Node[nombreLignes][nombreColonnes];

			for (Node child : gpMatriceBilles.getChildren()) {
				Integer column = GridPane.getColumnIndex(child);
				Integer row = GridPane.getRowIndex(child);
				if (column != null && row != null) {
					gridPaneNodes[row][column] = child;
				}
			}

			// La première et la dernière ligne ainsi que colonne ne sont
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

			pw.close();
		} catch (IOException ex) {
			Logger.getLogger(OngletPcb.class.getName())
					.log(Level.SEVERE, null, ex);
		}

		stage.close();
	}

	private void creationLCC() {

		String file = popup_sauvegarde_nom("LCC");

		try {

			PrintWriter pw = new PrintWriter(
					new BufferedWriter(
							new FileWriter(new File(path + file + ".lcc"))));

			pw.println(((ComposantLCC) composant).toString(false));
			pw.println("Matrice Billes:");
			// Si on a pas générer la matrice de billes on la prend pleine
			if (!this.matriceBillesgeneree) {

				gpMatriceBilles = Outils.generer_matrice_Bille(
						gpMatriceBilles,
						Integer.valueOf(
								(int) ((ComposantLCC) composant)
										.getNombre_niy()),
						Integer.valueOf(
								(int) ((ComposantLCC) composant)
										.getNombre_nix()));
			}

			// La fonction getRowCount() non dispo sous java 8
			int nombreLignes = Integer.valueOf(
					(int) ((ComposantLCC) composant).getNombre_niy()) + 2;
			// La fonction getColumnCount() non dispo sous java 8
			int nombreColonnes = Integer.valueOf(
					(int) ((ComposantLCC) composant).getNombre_nix()) + 2;

			// Transformation du GridPane en Tableau
			// Node[][] gridPaneNodes = new Node[gpMatriceBilles
			// .getRowCount()][gpMatriceBilles.getColumnCount()];

			Node[][] gridPaneNodes = new Node[nombreLignes][nombreColonnes];

			for (Node child : gpMatriceBilles.getChildren()) {
				Integer column = GridPane.getColumnIndex(child);
				Integer row = GridPane.getRowIndex(child);
				if (column != null && row != null) {
					gridPaneNodes[row][column] = child;
				}
			}

			// La première et la dernière ligne ainsi que colonne ne sont
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

			pw.close();
		} catch (IOException ex) {
			Logger.getLogger(OngletPcb.class.getName())
					.log(Level.SEVERE, null, ex);
		}

		stage.close();

	}

	/**
	 * Creation d'un composant WLP ainsi que la sauvegarde.
	 */
	protected void creation_wlp() {
		composant = new ComposantWLP(
				"",
				getPastille_x(),
				getPastille_y(),
				getPastille_z(),
				getPas(),
				getPresence_billes(),
				getNombre_billes_x(),
				getNombre_billes_y());

		String file = popup_sauvegarde_nom("WLP");

		composant.setNom(file);

		try {

			PrintWriter pw = new PrintWriter(
					new BufferedWriter(
							new FileWriter(new File(path + file + ".wlp"))));

			pw.println(composant.toString());

			// Si on a pas générer la matrice de billes on la prend pleine
			if (!this.matriceBillesgeneree) {

				gpMatriceBilles = Outils.generer_matrice_Bille(
						gpMatriceBilles,
						Integer.valueOf(nombre_billes_x.getText()),
						Integer.valueOf(nombre_billes_y.getText()));
			}

			// La fonction getRowCount() non dispo sous java 8
			int nombreLignes = Integer.valueOf(nombre_billes_y.getText()) + 2;
			// La fonction getColumnCount() non dispo sous java 8
			int nombreColonnes = Integer.valueOf(nombre_billes_x.getText()) + 2;

			// Transformation du GridPane en Tableau
			// Node[][] gridPaneNodes = new Node[gpMatriceBilles
			// .getRowCount()][gpMatriceBilles.getColumnCount()];

			Node[][] gridPaneNodes = new Node[nombreLignes][nombreColonnes];

			for (Node child : gpMatriceBilles.getChildren()) {
				Integer column = GridPane.getColumnIndex(child);
				Integer row = GridPane.getRowIndex(child);
				if (column != null && row != null) {
					gridPaneNodes[row][column] = child;
				}
			}

			// La première et la dernière ligne ainsi que colonne ne sont
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

			pw.close();
		} catch (IOException ex) {
			Logger.getLogger(OngletPcb.class.getName())
					.log(Level.SEVERE, null, ex);
		}

		stage.close();

	}

	public ArrayList<CouchePcb> get_couches_pcb_bga() {
		return this.pcb.get_couches_pcb();
	}

	public Pcb get_pcb_bga() {
		return this.pcb.get_pcb();
	}

	/**
	 * @param value String à tester. Il doit être sous la forme d'un Number.
	 * @return true si c'est un double false sinon.
	 */
	public boolean checkTextField(String value) {
		try {
			Double.parseDouble(value);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public void remplir_gridpane_parametre_lcc() {
		clear_pane();

		pas = generer_textfield("Pas", true);
		pas.setVisible(false);
		pastille_x = generer_textfield("X (mm)", true);
		pastille_x.setVisible(false);
		pastille_y = generer_textfield("Y (mm)", true);
		pastille_y.setVisible(false);
		pastille_z = generer_textfield("Z (mm)", true);
		pastille_z.setVisible(false);
		nombre_billes_x = generer_textfield("nx", true);
		nombre_billes_x.setVisible(false);
		nombre_billes_y = generer_textfield("ny", true);
		nombre_billes_y.setVisible(false);

		placer_element();

		ComposantLCC.image.setFitHeight(250);
		ComposantLCC.image.setFitWidth(250);
		buttonCalculNombreBillesMatricebilles.setVisible(true);
		buttonGenerationMatriceBilles.setVisible(true);
		this.vboxLCCImageAndMatrix.getChildren().clear();
		this.vboxLCCImageAndMatrix.getChildren().addAll(
				ComposantLCC.image,
				this.lab_NombreBillesMatricebilles,
				this.gpMatriceBilles,
				this.buttonCalculNombreBillesMatricebilles);
		this.vboxLCCImageAndMatrix.setAlignment(Pos.CENTER);
		hb_disposition_element_popup.getChildren()
				.add(this.vboxLCCImageAndMatrix);
	}

	/**
	 * Remplit la GridPane gp_form pour un composant WLP.
	 * 
	 */
	public void remplir_gridpane_parametre_wlp() {

		clear_pane();

		pas = generer_textfield("Pas", true);
		pastille_x = generer_textfield("X (mm)", true);
		pastille_y = generer_textfield("Y (mm)", true);
		pastille_z = generer_textfield("Z (mm)", true);
		nombre_billes_x = generer_textfield("Nombre de billes x", true);
		nombre_billes_y = generer_textfield("Nombre de billes y", true);
		billes = new CheckBox("Présence billes");
		billes.setVisible(false);
		arrayNodes.add(billes);

		placer_element();

		ComposantWLP.imageNouveauComposant.setFitHeight(250);
		ComposantWLP.imageNouveauComposant.setFitWidth(500);
		hb_disposition_element_popup.getChildren()
				.add(ComposantWLP.imageNouveauComposant);

		// stage.setWidth(850);
		// stage.setHeight(400);

		centrer_ecran();
	}

	/**
	 * Remplit la GridPane gp_form pour un composant WLP.
	 * 
	 */
	public void remplir_gridpane_parametre_cbga() {

		clear_pane();

		pas = generer_textfield("Pas", true);
		pastille_x = generer_textfield("X (mm)", true);
		pastille_y = generer_textfield("Y (mm)", true);
		pastille_z = generer_textfield("Z (mm)", true);
		nombre_billes_x = generer_textfield("Nombre de billes x", true);
		nombre_billes_y = generer_textfield("Nombre de billes y", true);
		billes = new CheckBox("Présence billes");
		billes.setVisible(false);
		arrayNodes.add(billes);

		placer_element();

		ComposantCBGA.image.setFitHeight(250);
		ComposantCBGA.image.setFitWidth(500);
		hb_disposition_element_popup.getChildren().add(ComposantCBGA.image);

		// stage.setWidth(850);
		// stage.setHeight(400);

		centrer_ecran();
	}

	protected Label generer_label(String nomLabel) {
		Label label = new Label(nomLabel);
		label.setFont(Font.font("Arial", FontWeight.BOLD, 18));

		return label;
	}

	protected void remplir_gridpane_parametre_resitance() {

		clear_pane();

		arrayNodes.add(choix_dimension);

		placer_element();

		stage.setWidth(700);
		stage.setHeight(550);

		centrer_ecran();

	}

	/**
	 * Remplit la GridPane gp_form pour un PCBGA.
	 */
	public void remplir_gridpane_parametre_pcbga() {

		clear_pane();

		arrayNodes.add(generer_label("Caractéristiques PCBGA"));
		pas = generer_textfield("Pas (mm)", true);
		nombre_billes_x = generer_textfield("Nombre de billes x", true);
		nombre_billes_y = generer_textfield("Nombre de billes y", true);
		pcb_x = generer_textfield("PCB x (mm) ", true);
		pcb_y = generer_textfield("PCB y (mm) ", true);
		billes = new CheckBox("Présence billes");
		billes.setVisible(false);
		arrayNodes.add(billes);

		placer_element();

		hb_disposition_element_popup.getChildren().clear();
		hb_disposition_element_popup.getChildren()
				.addAll(vb_parametres_pcb_couche_pcb, gp_form, tv_proprietes);

		stage.setHeight(700);
		stage.setWidth(1300);

		centrer_ecran();
	}

	/**
	 * Creation BGA et Sauvegarde.
	 */
	public void creation_pcbga() {
		pcb.button_calculer.fire();
		composant = new ComposantPCBGA(
				pcb,
				"",
				0.0,
				0.0,
				0.0,
				getPas(),
				getPresence_billes(),
				(int) getPcb_x(),
				(int) getPcb_y(),
				getNombre_billes_x(),
				getNombre_billes_y());

		String file = popup_sauvegarde_nom("PCBGA");
		composant.setNom(file);

		if (file != null && get_couches_pcb_bga().size() > 0) {

			try {

				PrintWriter pw = new PrintWriter(
						new BufferedWriter(
								new FileWriter(
										new File(path + file + ".pcbga"))));
				pw.println(pcb.get_Resine().toString());

				for (int i = 0; i < get_couches_pcb_bga().size(); i++) {

					pw.println(get_couches_pcb_bga().get(i).toString());
				}
				pw.println(composant.toString());

				// Ecriture de la matrice de Billes
				pw.println("Matrice Billes:");

				// Si on a pas générer la matrice on la prend pleine
				if (!this.matriceBillesgeneree) {
					gpMatriceBilles = Outils.generer_matrice_Bille(
							gpMatriceBilles,
							Integer.valueOf(nombre_billes_x.getText()),
							Integer.valueOf(nombre_billes_y.getText()));
				}

				// La fonction getRowCount() non dispo sous java 8
				int nombreLignes = Integer.valueOf(nombre_billes_y.getText())
						+ 2;
				// La fonction getColumnCount() non dispo sous java 8
				int nombreColonnes = Integer.valueOf(nombre_billes_x.getText())
						+ 2;

				// Transformation du GridPane en Tableau
				// Node[][] gridPaneNodes = new Node[gpMatriceBilles
				// .getRowCount()][gpMatriceBilles.getColumnCount()];

				Node[][] gridPaneNodes = new Node[nombreLignes][nombreColonnes];

				for (Node child : gpMatriceBilles.getChildren()) {
					Integer column = GridPane.getColumnIndex(child);
					Integer row = GridPane.getRowIndex(child);
					if (column != null && row != null) {
						gridPaneNodes[row][column] = child;
					}
				}

				// La première et la dernière ligne ainsi que colonne ne sont
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

				pw.close();
			} catch (IOException ex) {
				Logger.getLogger(OngletPcb.class.getName())
						.log(Level.SEVERE, null, ex);
			}
		}
		stage.close();
	}

	/**
	 * Remplit la GridPane gp_form pour un BGA.
	 */
	public void remplir_gridpane_parametre_bga() {

		clear_pane();

		arrayNodes.add(generer_label("Caractéristiques BGA"));
		pas = generer_textfield("Pas (mm)", true);
		nombre_billes_x = generer_textfield("Nombre de billes x", true);
		nombre_billes_y = generer_textfield("Nombre de billes y", true);
		pastille_x = generer_textfield("Puce x (mm) ", true);
		pastille_y = generer_textfield("Puce y (mm) ", true);
		pastille_z = generer_textfield("Puce z (mm) ", true);
		epaisseurLiaison = generer_textfield("Epaisseur Liaison (mm)", true);
		resine_x = generer_textfield("Resine x (mm)", true);
		resine_y = generer_textfield("Resine y (mm) ", true);
		resine_z = generer_textfield("Resine z (mm) ", true);
		pcb_x = generer_textfield("PCB x (mm) ", true);
		pcb_y = generer_textfield("PCB y (mm) ", true);
		billes = new CheckBox("Présence billes");
		billes.setVisible(false);
		arrayNodes.add(billes);

		placer_element();

		hb_disposition_element_popup.getChildren().clear();
		hb_disposition_element_popup.getChildren()
				.addAll(vb_parametres_pcb_couche_pcb, gp_form, tv_proprietes);
		buttonCalculNombreBillesMatricebilles.setVisible(true);
		buttonGenerationMatriceBilles.setVisible(true);
		stage.setHeight(700);
		stage.setWidth(1300);

		centrer_ecran();
	}

	/**
	 * Creation BGA et Sauvegarde.
	 */
	public void creation_bga() {
		pcb.button_calculer.fire();
		composant = new ComposantBGA(
				pcb,
				"",
				getPastille_x(),
				getPastille_y(),
				getPastille_z(),
				getPas(),
				getPresence_billes(),
				(int) getPcb_x(),
				(int) getPcb_y(),
				getResine_x(),
				getResine_y(),
				getResine_z(),
				getNombre_billes_x(),
				getNombre_billes_y(),
				choixLiaisonPuce.getValue(),
				getEpaisseurLiaison());

		String file = popup_sauvegarde_nom("BGA");
		composant.setNom(file);

		if (file != null && get_couches_pcb_bga().size() > 0) {

			try {

				PrintWriter pw = new PrintWriter(
						new BufferedWriter(
								new FileWriter(
										new File(path + file + ".bga"))));
				pw.println(pcb.get_Resine().toString());

				for (int i = 0; i < get_couches_pcb_bga().size(); i++) {

					pw.println(get_couches_pcb_bga().get(i).toString());
				}
				pw.println(composant.toString());

				// Ecriture de la matrice de Billes
				pw.println("Matrice Billes:");

				// Si on a pas générer la matrice on la prend pleine
				if (!this.matriceBillesgeneree) {
					gpMatriceBilles = Outils.generer_matrice_Bille(
							gpMatriceBilles,
							Integer.valueOf(nombre_billes_x.getText()),
							Integer.valueOf(nombre_billes_y.getText()));
				}

				// La fonction getRowCount() non dispo sous java 8
				int nombreLignes = Integer.valueOf(nombre_billes_y.getText())
						+ 2;
				// La fonction getColumnCount() non dispo sous java 8
				int nombreColonnes = Integer.valueOf(nombre_billes_x.getText())
						+ 2;

				// Transformation du GridPane en Tableau
				// Node[][] gridPaneNodes = new Node[gpMatriceBilles
				// .getRowCount()][gpMatriceBilles.getColumnCount()];

				Node[][] gridPaneNodes = new Node[nombreLignes][nombreColonnes];

				for (Node child : gpMatriceBilles.getChildren()) {
					Integer column = GridPane.getColumnIndex(child);
					Integer row = GridPane.getRowIndex(child);
					if (column != null && row != null) {
						gridPaneNodes[row][column] = child;
					}
				}

				// La première et la dernière ligne ainsi que colonne ne sont
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

				pw.close();
			} catch (IOException ex) {
				Logger.getLogger(OngletPcb.class.getName())
						.log(Level.SEVERE, null, ex);
			}
		}

		stage.close();
	}

	public void remplir_gridpane_parametre_sbga() {
		clear_pane();

		arrayNodes.add(generer_label("Caractéristiques SBGA"));
		pas = generer_textfield("Pas (mm)", true);
		nombre_billes_x = generer_textfield("Nombre de billes x", true);
		nombre_billes_y = generer_textfield("Nombre de billes y", true);
		pastille_x = generer_textfield("Puce x (mm) ", true);
		pastille_y = generer_textfield("Puce y (mm) ", true);
		pastille_z = generer_textfield("Puce z (mm) ", true);
		epaisseurLiaison = generer_textfield("Epaisseur Liaison (mm)", true);
		cuivre_x = generer_textfield("Cuivre x (mm)", true);
		cuivre_y = generer_textfield("Cuivre y (mm) ", true);
		cuivre_z = generer_textfield("Cuivre z (mm) ", true);
		pcb_x = generer_textfield("PCB x (mm) ", true);
		pcb_y = generer_textfield("PCB y (mm) ", true);
		billes = new CheckBox("Présence billes");
		billes.setVisible(false);
		arrayNodes.add(billes);

		placer_element();

		hb_disposition_element_popup.getChildren().clear();
		hb_disposition_element_popup.getChildren()
				.addAll(vb_parametres_pcb_couche_pcb, gp_form, tv_proprietes);

		stage.setHeight(700);
		stage.setWidth(1300);

		centrer_ecran();

	}

	/**
	 * Creation SBGA et Sauvegarde.
	 */
	public void creation_sbga() {
		pcb.button_calculer.fire();
		composant = new ComposantSBGA(
				pcb,
				"",
				getPastille_x(),
				getPastille_y(),
				getPastille_z(),
				getPas(),
				getPresence_billes(),
				(int) getPcb_x(),
				(int) getPcb_y(),
				getCuivre_x(),
				getCuivre_y(),
				getCuivre_z(),
				getNombre_billes_x(),
				getNombre_billes_y(),
				choixLiaisonPuce.getValue(),
				getEpaisseurLiaison());

		String file = popup_sauvegarde_nom("SBGA");
		composant.setNom(file);

		if (file != null && get_couches_pcb_bga().size() > 0) {

			try {

				PrintWriter pw = new PrintWriter(
						new BufferedWriter(
								new FileWriter(
										new File(path + file + ".sbga"))));
				pw.println(pcb.get_Resine().toString());

				for (int i = 0; i < get_couches_pcb_bga().size(); i++) {

					pw.println(get_couches_pcb_bga().get(i).toString());
				}
				pw.println(composant.toString());

				// Ecriture de la matrice de Billes
				pw.println("Matrice Billes:");

				// Si on a pas générer la matrice on la prend pleine
				if (!this.matriceBillesgeneree) {
					gpMatriceBilles = Outils.generer_matrice_Bille(
							gpMatriceBilles,
							Integer.valueOf(nombre_billes_x.getText()),
							Integer.valueOf(nombre_billes_y.getText()));
				}

				// La fonction getRowCount() non dispo sous java 8
				int nombreLignes = Integer.valueOf(nombre_billes_y.getText())
						+ 2;
				// La fonction getColumnCount() non dispo sous java 8
				int nombreColonnes = Integer.valueOf(nombre_billes_x.getText())
						+ 2;

				// Transformation du GridPane en Tableau
				// Node[][] gridPaneNodes = new Node[gpMatriceBilles
				// .getRowCount()][gpMatriceBilles.getColumnCount()];

				Node[][] gridPaneNodes = new Node[nombreLignes][nombreColonnes];

				for (Node child : gpMatriceBilles.getChildren()) {
					Integer column = GridPane.getColumnIndex(child);
					Integer row = GridPane.getRowIndex(child);
					if (column != null && row != null) {
						gridPaneNodes[row][column] = child;
					}
				}

				// La première et la dernière ligne ainsi que colonne ne sont
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

				pw.close();
			} catch (IOException ex) {
				Logger.getLogger(OngletPcb.class.getName())
						.log(Level.SEVERE, null, ex);
			}
		}

		stage.close();
	}

	/**
	 * Place les éléments automatiquement dans la GridPane gp_form.
	 */
	private void placer_element() {
		for (int i = 0; i < arrayNodes.size(); i++) {
			gp_form.add((Node) arrayNodes.get(i), 0, i);
		}
		if (choix_composant.getValue().equals("BGA")
				|| choix_composant.getValue().equals("CSP")
				|| choix_composant.getValue().equals("WLP")
				|| choix_composant.getValue().equals("SBGA")
				|| choix_composant.getValue().equals("CBGA")
				|| choix_composant.getValue().equals("PCBGA")
				|| choix_composant.getValue().equals("LCC")) {
			gp_buttons_form.getChildren().clear();
			gp_buttons_form.add(buttonGenerationMatriceBilles, 0, 0);
			gp_buttons_form.add(button_annuler, 0, 1);
			gp_buttons_form.add(button_ok, 1, 1);

		} else {
			gp_buttons_form.add(button_annuler, 0, 0);
			gp_buttons_form.add(button_ok, 1, 0);
		}
		gp_form.add(gp_buttons_form, 0, arrayNodes.size());
		// gp_form.add(button_annuler, 0, arrayNodes.size());
		// gp_form.add(button_ok, 1, arrayNodes.size());
	}

	/**
	 * Fonction qui recupère les dimensions de la fenêtre afin de la centrer au
	 * milieu de l'écran à chaque fois que les dimensions changent
	 */
	private void centrer_ecran() {
		Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
		stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
		stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);
	}

	/**
	 * Nettoie le pane en retirant les éléments de la HBox et re ajoute le
	 * GridPane qui sert de grille pour rentrer les valeurs des composants.
	 */
	private void clear_pane() {

		hb_disposition_element_popup.getChildren().clear();
		hb_disposition_element_popup.getChildren().add(gp_form);
		gp_form.getChildren().clear();
		this.vbMatriceBillesEtTitre.setVisible(false);
		this.choixLiaisonPuce.getSelectionModel().clearSelection();
		arrayNodes = new ArrayList<Object>();

	}

	public double getPas() {
		return Double.parseDouble(pas.getText());
	}

	public void setPas(TextField pas) {
		this.pas = pas;
	}

	public double getNombre_ex1() {
		return Double.parseDouble(nombre_ex1.getText());
	}

	public void setNombre_ex1(TextField nombre_ex1) {
		this.nombre_ex1 = nombre_ex1;
	}

	public double getNombre_ey1() {
		return Double.parseDouble(nombre_ey1.getText());
	}

	public void setNombre_ey1(TextField nombre_ey1) {
		this.nombre_ey1 = nombre_ey1;
	}

	public double getNombre_nx() {
		return Double.parseDouble(nombre_nx.getText());
	}

	public void setNombre_nx(TextField nombre_nx) {
		this.nombre_nx = nombre_nx;
	}

	public double getNombre_ny() {
		return Double.parseDouble(nombre_ny.getText());
	}

	public void setNombre_ny(TextField nombre_ny) {
		this.nombre_ny = nombre_ny;
	}

	public int getNombre_billes_x() {
		return Integer.parseInt(nombre_billes_x.getText());
	}

	public void setNombre_billes_x(TextField nombre_billes_x) {
		this.nombre_billes_x = nombre_billes_x;
	}

	public int getNombre_billes_y() {
		return Integer.parseInt(nombre_billes_y.getText());
	}

	public void setNombre_billes_y(TextField nombre_billes_y) {
		this.nombre_billes_y = nombre_billes_y;
	}

	public double getPastille_x() {
		return Double.parseDouble(pastille_x.getText());
	}

	public void setPastille_x(TextField pastille_x) {
		this.pastille_x = pastille_x;
	}

	public double getPastille_y() {
		return Double.parseDouble(pastille_y.getText());
	}

	public void setPastille_y(TextField pastille_y) {
		this.pastille_y = pastille_y;
	}

	public double getPastille_z() {
		return Double.parseDouble(pastille_z.getText());
	}

	public void setPastille_z(TextField pastille_z) {
		this.pastille_z = pastille_z;
	}

	public double getEpaisseurLiaison() {
		return Double.parseDouble(epaisseurLiaison.getText());
	}

	public void setEpaisseurLiaison(TextField epaisseur_liaison) {
		this.epaisseurLiaison = epaisseur_liaison;
	}

	public double getResine_x() {
		return Double.parseDouble(resine_x.getText());
	}

	public void setResine_x(TextField resine_x) {
		this.resine_x = resine_x;
	}

	public double getResine_y() {
		return Double.parseDouble(resine_y.getText());
	}

	public void setResine_y(TextField resine_y) {
		this.resine_y = resine_y;
	}

	public double getResine_z() {
		return Double.parseDouble(resine_z.getText());
	}

	public double getCuivre_x() {
		return Double.parseDouble(cuivre_x.getText());
	}

	public void setCuivre_x(TextField cuivre_x) {
		this.cuivre_x = cuivre_x;
	}

	public double getCuivre_y() {
		return Double.parseDouble(cuivre_y.getText());
	}

	public void setCuivre_y(TextField cuivre_y) {
		this.cuivre_y = cuivre_y;
	}

	public double getCuivre_z() {
		return Double.parseDouble(cuivre_z.getText());
	}

	public void setResine_z(TextField cuivre_z) {
		this.cuivre_z = cuivre_z;
	}

	public double getPcb_x() {
		return Double.parseDouble(pcb_x.getText());
	}

	public void setPcb_x(TextField pcb_x) {
		this.pcb_x = pcb_x;
	}

	public double getPcb_y() {
		return Double.parseDouble(pcb_y.getText());
	}

	public Composant getComposant() {
		return composant;
	}

	public void setComposant(Composant c) {
		this.composant = c;
	}

	public void setPcb_y(TextField pcb_y) {
		this.pcb_y = pcb_y;
	}

	public boolean getPresence_billes() {
		return billes.isSelected();
	}
}
