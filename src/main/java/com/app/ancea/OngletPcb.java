package com.app.ancea;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class OngletPcb extends VBox {

	/*
	 * 
	 * 
	 * 
	 * classe Circuit: classe utilisee pour rempresenter le circuit imprime
	 * usine (CIU ou PCB en anglais), elle utilise la classe pcb qui represente
	 * graphiquement le PCB (ensemble de couches electriques et isolantes). Elle
	 * utilise egalement la classe ResultatBox pour afficher un tableau des
	 * resultats obtenues (rbl) sur les proprietes thermomecaniques du PCB e des
	 * temperatures clees predefinies (et aussi aux temperatures definies par
	 * l'utilisateur : res_final_user1 et res_final_user12)
	 * 
	 * 
	 */

	final private String path = Ressources.pathPCB;
	private FileChooser fileChooser;

	ArrayList<CouchePcb> liste_couches_pcb;// Tableau des couches que notre pcb
											// et que l'on utilise dans les
											// calculs

	VBox vbx_resultats_final;
	TableView<Resultat_temperature_pcb> tb_Resultat_temperature_pcb;

	ObservableList<Resultat_temperature_pcb> data;

	TitledPane titre_graphes;
	HBox hb_graph_e_cte;
	LineChart<Number, Number> lineChart;
	LineChart<Number, Number> lineChart2;

	Pcb pcb;

	VBox vb_pcb;
	VBox vb_graphes;
	VBox vb_bar_pcb_couche_pcb;
	HBox hb_bar_pcb;

	ArrayList<String> noms_fichiers = Outils
			.get_list_filename_directory(path, ".pcb");

	Label label_nombre_couche;// laisser en champs ou pas??
	TextField nombre_couche;

	Label label_nombre_vialaser;
	TextField nombre_vialaser;

	Label label_debut_trou_enterre;
	TextField trou_enterre;

	TextField champ_nb_couche;
	TextField champ_nb_via_laser;
	TextField champ_trou_enterre;

	ComboBox<String> cb_circuits_existants;
	ComboBox<String> choix_resine = new ComboBox<String>();
	ModelPhysiqueStatique resine;

	VBox vb_tool_bar;
	Button button_generer_pcb;
	Button buttonSave;
	Button buttonLoad;
	Button buttonPrint;

	Button button_calculer;

	VBox model_geometrique;
	TextField totale_nominal_reel;
	TextField cuivre_reel;
	TextField cuivre_thermique;
	TextField isolant_verre_epoxy;
	TextField taux_ponderal_resine;

	GridPane gp_nouveau_circuit;
	HBox hb_nombre_couches;
	HBox hb_nombre_via_lasers;
	HBox hb_trous_enterres;

	GridPane gp_parametre_pcb;
	GridPane gp_circuits_existants;

	TableView<Propriete> tb_prop_thermo;
	Propriete p_epaisseur_totale_nominale;
	Propriete p_masse_volumique;
	Propriete p_conductivite_thermique_xy;
	Propriete p_conductivite_thermique_z;
	Propriete p_conductivite_thermique_5t;

	VBox proprietes_thermomecanique;
	VBox vbx_proprietes_thermomecanique;
	Text text_proprietes_thermomecanique;
	TextField epaisseur_totale_nominale;
	TextField masse_volumique;
	TextField conductivite_xy;
	TextField conductivite_z;
	TextField conductivite_z_trou_trversant;

	VBox parametre_stratifie;
	TextField ra;
	TextField rb;
	TextField ps;
	TextField pa;
	TextField pb;
	TextField Va;
	TextField Vb;
	TextField a_xy;
	TextField b_xy;
	TextField ya;
	TextField yb;
	TextField y_xy;
	TextField y_z;

	HBox hb_gridpane_pcb;

	TitledPane titre_nouveau_circuit;
	TitledPane titre_circuit_existant;
	TitledPane titre_parametres;
	TitledPane titre_Resultat_temperature_pcb;
	TitledPane titre_pcb;

	double totalNominalReel;

	/*
	 * Permet de generer un Label et TextField avec une position posLabel dans
	 * le GridPane(nouveau_circuit)
	 * 
	 * @param stringLabel valeur affiche du label
	 * 
	 * @param posLabel position du label dans la GridPane ex:
	 * generer_label_et_textfield("nb_couches",1) generera un label et un
	 * textfield e la positon 0,1 et 1,1
	 * 
	 * @return return void
	 */
	public TextField generer_label_et_textfield(
			String stringLabel,
			int posLabel) {
		TextField tf = new TextField();
		tf.setFont(new Font("SanSerif", 13));
		tf.setPromptText(stringLabel);
		tf.getStyleClass().add("field-background");
		gp_nouveau_circuit.add(tf, 0, posLabel);
		return tf;
	}

	public TextField generer_textfield(String prompText) {
		TextField tf = new TextField();
		tf.setFont(new Font("SanSerif", 15));
		tf.setPromptText(prompText);
		tf.getStyleClass().add("field-background");
		return tf;
	}

	public void init_parametres_pcb() {
		this.champ_nb_couche = generer_label_et_textfield("Nombre couches", 0);
		this.champ_nb_via_laser = generer_label_et_textfield(
				"Nombre vias-laser",
				1);
		this.champ_trou_enterre = generer_label_et_textfield(
				"Trous enterres",
				2);

	}

	public OngletPcb() {

		cb_circuits_existants = new ComboBox<String>();
		cb_circuits_existants.setTooltip(new Tooltip());
		cb_circuits_existants.getItems().addAll(noms_fichiers);

		/* Vertical box du PCB */
		vb_pcb = new VBox();

		totalNominalReel = 0;

		tb_Resultat_temperature_pcb = Resultat_temperature_pcb.get_tableview();
		tb_Resultat_temperature_pcb.setPrefHeight(289);

		hb_gridpane_pcb = new HBox(20);
		hb_gridpane_pcb.setPadding(new Insets(20, 20, 20, 20));

		gp_nouveau_circuit = Outils.generer_gridPane();

		gp_parametre_pcb = Outils.generer_gridPane();

		gp_circuits_existants = Outils.generer_gridPane();

		titre_nouveau_circuit = new TitledPane();
		titre_nouveau_circuit.setText("Nouveau circuit");
		titre_nouveau_circuit.setCollapsible(false);
		titre_nouveau_circuit.setContent(gp_nouveau_circuit);

		new AutoCompleteComboBoxListener<>(cb_circuits_existants);

		titre_circuit_existant = new TitledPane();
		titre_circuit_existant.setText("PCB existants");
		titre_circuit_existant.setCollapsible(false);
		titre_circuit_existant.setContent(gp_circuits_existants);

		titre_parametres = new TitledPane();
		titre_parametres.setText("Paramètres circuit");
		titre_parametres.setCollapsible(false);
		titre_parametres.setContent(gp_parametre_pcb);

		titre_Resultat_temperature_pcb = new TitledPane();
		titre_Resultat_temperature_pcb.setText("Résultat en fonction des températures");
		titre_Resultat_temperature_pcb.setContent(tb_Resultat_temperature_pcb);

		gp_parametre_pcb.add(new Label("Choix résine: "), 0, 0);
		gp_parametre_pcb.add(choix_resine, 1, 0);

		VBox vb_titre = new VBox(20);
		vb_titre.setPadding(new Insets(0, 0, 0, 0));
		vb_titre.getChildren().addAll(
				titre_circuit_existant,
				titre_nouveau_circuit,
				titre_parametres);

		init_parametre_stratifie();

		init_model_geometrique();

		init_proprietes_thermomecaniques();

		init_parametres_pcb();

		init_resine();

		init_button_generer_pcb();

		gp_nouveau_circuit.add(button_generer_pcb, 3, 1);
		gp_circuits_existants.add(new Label("Choix PCB: "), 0, 0);
		gp_circuits_existants.add(cb_circuits_existants, 1, 0);

		vb_tool_bar = new VBox(20);

		// mettre le hb_tool_bar.add(buttonLoad) dan init_button_load
		vb_tool_bar.setSpacing(10);

		getChildren().addAll(
				hb_gridpane_pcb,
				vb_tool_bar,
				titre_Resultat_temperature_pcb);

		init_bar_Pcb();

		vb_bar_pcb_couche_pcb = new VBox();
		vb_bar_pcb_couche_pcb.getChildren().addAll(hb_bar_pcb, vb_pcb);

		/* TitledPane contenant les couches PCB avec le bouton calculer */

		titre_pcb = new TitledPane();
		titre_pcb.setText("Couches PCB");
		titre_pcb.setCollapsible(false);
		titre_pcb.setContent(vb_bar_pcb_couche_pcb);

		/* initialisation du bouton calculer */

		init_button_calculer();

		// getChildren().add(model_geometrique);

		getChildren().add(proprietes_thermomecanique);

		vb_graphes = new VBox();
		getChildren().add(vb_graphes);
		// init_line_charts();

		setSpacing(10);

		hb_gridpane_pcb.getChildren()
				.addAll(vb_titre, titre_pcb, tb_prop_thermo);
		HBox.setHgrow(vb_titre, Priority.NEVER);
		HBox.setHgrow(titre_pcb, Priority.ALWAYS);
		HBox.setHgrow(tb_prop_thermo, Priority.NEVER);

		cb_circuits_existants.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				try {
					pcb = new Pcb();

					pcb.load_Circuit(
							Ressources.pathPCB
									+ cb_circuits_existants.getValue()
									+ ".pcb");
					vb_pcb.getChildren().clear();
					liste_couches_pcb = pcb.get_liste_couches_pcb();
					nombre_vialaser = new TextField(
							String.valueOf(pcb.nombre_vialaser));
					nombre_couche = new TextField(
							String.valueOf(pcb.nombre_couche));
					for (int i = 0; i < Ressources.RESINES.length; i++) {
						if (pcb.resine.toString() == Ressources.RESINES[i]
								.toString()) {
							choix_resine.setValue(pcb.resine.toString());
						}
					}
					vb_pcb.getChildren().addAll(pcb.al);
					button_calculer.setVisible(true);
					titre_pcb.setText(
							"Couches " + cb_circuits_existants.getValue());
				} catch (NullPointerException npe) {
					npe.printStackTrace();
				}
			}
		});
		// getChildren().add(parametre_stratifie)

	}

	protected void exportToCsv() {

		fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(new File(Ressources.pathExcel));
		fileChooser.getExtensionFilters().add(
				new ExtensionFilter("Resultat PCB Excel (*.csv)", "*.csv"));

		File file = fileChooser.showSaveDialog(getScene().getWindow());

		if (file != null) {
			try (FileWriter writer = new FileWriter(file)) {
				for (TableColumn<
						Resultat_temperature_pcb,
						?> colonne : tb_Resultat_temperature_pcb.getColumns()) {

					String colonneTableView = colonne.getText() + ";";
					writer.write(colonneTableView);

				}
				writer.write("\n");

				for (int i = 0; i < data.size(); i++) {
					String ligneTemperatureTableView = data.get(i).toString()
							+ "\n";
					writer.write(ligneTemperatureTableView);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void init_proprietes_thermomecaniques() {
		proprietes_thermomecanique = new VBox();

		tb_prop_thermo = new TableView<>();
		tb_prop_thermo.setVisible(true);

		tb_prop_thermo
				.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		p_epaisseur_totale_nominale = new Propriete("Epaisseur PCB (mm)");
		p_masse_volumique = new Propriete("Masse volumique (g/cm^3)");
		p_conductivite_thermique_xy = new Propriete(
				"Cond. thermique x;y (W.m/°C)");
		p_conductivite_thermique_z = new Propriete("Cond. thermique z (W.m/°C)");
		p_conductivite_thermique_5t = new Propriete(
				"Cond. thermique z (5 TM/cm^2)");

		final ObservableList<
				Propriete> data_proprietes_thermiques = FXCollections
						.observableArrayList(
								p_epaisseur_totale_nominale,
								p_masse_volumique,
								p_conductivite_thermique_xy,
								p_conductivite_thermique_z,
								p_conductivite_thermique_5t);

		TableColumn<Propriete, String> colonneVariable = new TableColumn<>("Propriétés thermomécaniques");
		colonneVariable.setCellValueFactory(c -> c.getValue().get_nom_variable());
		colonneVariable.setMinWidth(160);

		TableColumn<
				Propriete,
				String> colonneResultat = new TableColumn<>("Résultats");
		colonneResultat.setMinWidth(40);
		colonneResultat.setCellValueFactory(c -> c.getValue().get_resultat());

		tb_prop_thermo.setItems(data_proprietes_thermiques);

		tb_prop_thermo.getColumns().addAll(colonneVariable, colonneResultat);

		VBox vb_titre_and_tableview = new VBox();
		vb_titre_and_tableview.getChildren().add(tb_prop_thermo);

		proprietes_thermomecanique.getChildren().addAll(vb_titre_and_tableview);

		Label label_epaisseur_totale_nominale = new Label("epaisseur totale nominale:");
		epaisseur_totale_nominale = new TextField(Ressources.TEXTFIELD_VALEURE_DEFAUT);
		epaisseur_totale_nominale.setStyle(Ressources.TEXTFIELD_STYLE);
		epaisseur_totale_nominale.setMaxWidth(Ressources.TEXTFIELD_WIDTH);
		epaisseur_totale_nominale.setPrefHeight(Ressources.TEXTFIELD_HEIGHT);
		epaisseur_totale_nominale.setEditable(false);

		Label label_masse_volumique = new Label("masse volumique:");
		masse_volumique = new TextField(Ressources.TEXTFIELD_VALEURE_DEFAUT);
		masse_volumique.setStyle(Ressources.TEXTFIELD_STYLE);
		masse_volumique.setMaxWidth(Ressources.TEXTFIELD_WIDTH);
		masse_volumique.setPrefHeight(Ressources.TEXTFIELD_HEIGHT);
		masse_volumique.setEditable(false);

		Label label_conductivite_xy = new Label("conductivite_thermique_xy:");
		conductivite_xy = new TextField(Ressources.TEXTFIELD_VALEURE_DEFAUT);
		conductivite_xy.setStyle(Ressources.TEXTFIELD_STYLE);
		conductivite_xy.setMaxWidth(Ressources.TEXTFIELD_WIDTH);
		conductivite_xy.setPrefHeight(Ressources.TEXTFIELD_HEIGHT);
		conductivite_xy.setEditable(false);

		Label label_conductivite_z = new Label("conductivite_thermique_z:");
		conductivite_z = new TextField(Ressources.TEXTFIELD_VALEURE_DEFAUT);
		conductivite_z.setStyle(Ressources.TEXTFIELD_STYLE);
		conductivite_z.setMaxWidth(Ressources.TEXTFIELD_WIDTH);
		conductivite_z.setPrefHeight(Ressources.TEXTFIELD_HEIGHT);
		conductivite_z.setEditable(false);

		Label label_conductivite_z_trou_trversant = new Label("conductivite thermique z (5 trous traversant/cme):");
		conductivite_z_trou_trversant = new TextField(Ressources.TEXTFIELD_VALEURE_DEFAUT);
		conductivite_z_trou_trversant.setStyle(Ressources.TEXTFIELD_STYLE);
		conductivite_z_trou_trversant.setMaxWidth(Ressources.TEXTFIELD_WIDTH);
		conductivite_z_trou_trversant.setPrefHeight(Ressources.TEXTFIELD_HEIGHT);
		conductivite_z_trou_trversant.setEditable(false);
	}

	public void init_model_geometrique() {// Non visible pour amelioration
											// graphique
		model_geometrique = new VBox();

		Text text_model_geometrique = new Text("Modele geometrique");
		text_model_geometrique.setFill(Color.RED);
		text_model_geometrique.setFont(Font.font("Normal", 20));

		Label label_isolant_epoxy = new Label("isolant epoxy :");
		isolant_verre_epoxy = new TextField(
				Ressources.TEXTFIELD_VALEURE_DEFAUT);
		isolant_verre_epoxy.setStyle(Ressources.TEXTFIELD_STYLE);
		isolant_verre_epoxy.setPrefWidth(Ressources.TEXTFIELD_WIDTH);
		isolant_verre_epoxy.setPrefHeight(Ressources.TEXTFIELD_HEIGHT);
		isolant_verre_epoxy.setEditable(false);

		Label label_cuivre_reel = new Label("Cu reel:");
		cuivre_reel = new TextField(Ressources.TEXTFIELD_VALEURE_DEFAUT);
		cuivre_reel.setStyle(Ressources.TEXTFIELD_STYLE);
		cuivre_reel.setPrefWidth(Ressources.TEXTFIELD_WIDTH);
		cuivre_reel.setPrefHeight(Ressources.TEXTFIELD_HEIGHT);
		cuivre_reel.setEditable(false);

		Label label_cuivre_thermique = new Label("Cu thermique:");
		cuivre_thermique = new TextField(Ressources.TEXTFIELD_VALEURE_DEFAUT);
		cuivre_thermique.setStyle(Ressources.TEXTFIELD_STYLE);
		cuivre_thermique.setPrefWidth(Ressources.TEXTFIELD_WIDTH);
		cuivre_thermique.setPrefHeight(Ressources.TEXTFIELD_HEIGHT);
		cuivre_thermique.setEditable(false);

		Label label_totale_nominal_reel = new Label("total nominal reel :");
		totale_nominal_reel = new TextField(
				Ressources.TEXTFIELD_VALEURE_DEFAUT);
		totale_nominal_reel.setStyle(Ressources.TEXTFIELD_STYLE);
		totale_nominal_reel.setPrefWidth(Ressources.TEXTFIELD_WIDTH);
		totale_nominal_reel.setPrefHeight(Ressources.TEXTFIELD_HEIGHT);
		totale_nominal_reel.setEditable(false);

		Label label_taux_ponderal_resine = new Label("taux ponderal resine :");
		taux_ponderal_resine = new TextField(Ressources.TEXTFIELD_VALEURE_DEFAUT);
		taux_ponderal_resine.setStyle(Ressources.TEXTFIELD_STYLE);
		taux_ponderal_resine.setPrefWidth(Ressources.TEXTFIELD_WIDTH);
		taux_ponderal_resine.setPrefHeight(Ressources.TEXTFIELD_HEIGHT);
		taux_ponderal_resine.setEditable(false);

		HBox hbx_model_geometrique = new HBox();

		hbx_model_geometrique.getChildren().addAll(
				label_isolant_epoxy,
				isolant_verre_epoxy,
				label_cuivre_reel,
				cuivre_reel,
				label_cuivre_thermique,
				cuivre_thermique,
				label_totale_nominal_reel,
				totale_nominal_reel,
				label_taux_ponderal_resine,
				taux_ponderal_resine);

		model_geometrique.getChildren()
				.addAll(text_model_geometrique, hbx_model_geometrique);

	}

	public void init_parametre_stratifie() {// les parametre stratifie sont
											// initialise graphiquement car
		// philippe voulais qu'on les affiche et ne sait pas encore s'il veut
		// que l'on
		// mette certain de ces parametres
		// apparaissant graphiquement dans la derniere version du logiciel (pour
		// le
		// moment ils n'aparaissent pas)
		parametre_stratifie = new VBox();
		parametre_stratifie.setMaxWidth(Ressources.TEXTFIELD_WIDTH);
		Label label_ra = new Label("ra");
		ra = new TextField(Ressources.TEXTFIELD_VALEURE_DEFAUT);
		ra.setStyle(Ressources.TEXTFIELD_STYLE);
		ra.setPrefWidth(Ressources.TEXTFIELD_WIDTH);
		ra.setPrefHeight(Ressources.TEXTFIELD_HEIGHT);
		ra.setEditable(false);

		Label label_rb = new Label("rb");
		rb = new TextField(Ressources.TEXTFIELD_VALEURE_DEFAUT);
		rb.setStyle(Ressources.TEXTFIELD_STYLE);
		rb.setPrefWidth(Ressources.TEXTFIELD_WIDTH);
		rb.setPrefHeight(Ressources.TEXTFIELD_HEIGHT);
		rb.setEditable(false);

		Label label_ps = new Label("ps");
		ps = new TextField(Ressources.TEXTFIELD_VALEURE_DEFAUT);
		ps.setStyle(Ressources.TEXTFIELD_STYLE);
		ps.setPrefWidth(Ressources.TEXTFIELD_WIDTH);
		ps.setPrefHeight(Ressources.TEXTFIELD_HEIGHT);
		ps.setEditable(false);

		Label label_pa = new Label("pa");
		pa = new TextField(Ressources.TEXTFIELD_VALEURE_DEFAUT);
		pa.setStyle(Ressources.TEXTFIELD_STYLE);
		pa.setPrefWidth(Ressources.TEXTFIELD_WIDTH);
		pa.setPrefHeight(Ressources.TEXTFIELD_HEIGHT);
		pa.setEditable(false);

		Label label_pb = new Label("pb");
		pb = new TextField(Ressources.TEXTFIELD_VALEURE_DEFAUT);
		pb.setStyle(Ressources.TEXTFIELD_STYLE);
		pb.setPrefWidth(Ressources.TEXTFIELD_WIDTH);
		pb.setPrefHeight(Ressources.TEXTFIELD_HEIGHT);
		pb.setEditable(false);

		Label label_Va = new Label("Va");
		Va = new TextField(Ressources.TEXTFIELD_VALEURE_DEFAUT);
		Va.setStyle(Ressources.TEXTFIELD_STYLE);
		Va.setPrefWidth(Ressources.TEXTFIELD_WIDTH);
		Va.setPrefHeight(Ressources.TEXTFIELD_HEIGHT);
		Va.setEditable(false);

		Label label_Vb = new Label("Vb");
		Vb = new TextField(Ressources.TEXTFIELD_VALEURE_DEFAUT);
		Vb.setStyle(Ressources.TEXTFIELD_STYLE);
		Vb.setPrefWidth(Ressources.TEXTFIELD_WIDTH);
		Vb.setPrefHeight(Ressources.TEXTFIELD_HEIGHT);
		Vb.setEditable(false);

		Label label_a_xy = new Label("a_xy");
		a_xy = new TextField(Ressources.TEXTFIELD_VALEURE_DEFAUT);
		a_xy.setStyle(Ressources.TEXTFIELD_STYLE);
		a_xy.setPrefWidth(Ressources.TEXTFIELD_WIDTH);
		a_xy.setPrefHeight(Ressources.TEXTFIELD_HEIGHT);
		a_xy.setEditable(false);

		Label label_b_xy = new Label("b_xy");
		b_xy = new TextField(Ressources.TEXTFIELD_VALEURE_DEFAUT);
		b_xy.setStyle(Ressources.TEXTFIELD_STYLE);
		b_xy.setPrefWidth(Ressources.TEXTFIELD_WIDTH);
		b_xy.setPrefHeight(Ressources.TEXTFIELD_HEIGHT);
		b_xy.setEditable(false);

		Label label_ya = new Label("ya");
		ya = new TextField(Ressources.TEXTFIELD_VALEURE_DEFAUT);
		ya.setStyle(Ressources.TEXTFIELD_STYLE);
		ya.setPrefWidth(Ressources.TEXTFIELD_WIDTH);
		ya.setPrefHeight(Ressources.TEXTFIELD_HEIGHT);
		ya.setEditable(false);

		Label label_yb = new Label("yb");
		yb = new TextField(Ressources.TEXTFIELD_VALEURE_DEFAUT);
		yb.setStyle(Ressources.TEXTFIELD_STYLE);
		yb.setPrefWidth(Ressources.TEXTFIELD_WIDTH);
		yb.setPrefHeight(Ressources.TEXTFIELD_HEIGHT);
		yb.setEditable(false);

		Label label_y_xy = new Label("y_xy");
		y_xy = new TextField(Ressources.TEXTFIELD_VALEURE_DEFAUT);
		y_xy.setStyle(Ressources.TEXTFIELD_STYLE);
		y_xy.setPrefWidth(Ressources.TEXTFIELD_WIDTH);
		y_xy.setPrefHeight(Ressources.TEXTFIELD_HEIGHT);
		y_xy.setEditable(false);

		Label label_y_z = new Label("y_z");
		y_z = new TextField(Ressources.TEXTFIELD_VALEURE_DEFAUT);
		y_z.setStyle(Ressources.TEXTFIELD_STYLE);
		y_z.setPrefWidth(Ressources.TEXTFIELD_WIDTH);
		y_z.setPrefHeight(Ressources.TEXTFIELD_HEIGHT);
		y_z.setEditable(false);

		parametre_stratifie.getChildren().addAll(
				label_ra,
				ra,
				label_rb,
				rb,
				label_ps,
				ps,
				label_pa,
				pa,
				label_pb,
				pb,
				label_Va,
				Va,
				label_Vb,
				Vb,
				label_a_xy,
				a_xy,
				label_b_xy,
				b_xy,
				label_ya,
				ya,
				label_yb,
				yb,
				label_y_xy,
				y_xy,
				label_y_z,
				y_z);
	}

	public void init_button_calculer() {

		button_calculer = new Button("calculer");
		vb_bar_pcb_couche_pcb.getChildren().add(button_calculer);
		button_calculer.setVisible(false);
		button_calculer.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				try {

					tous_calculs();

					// Decommenter tout ce qui suit une fois que les formule des
					// conditions d'cceptation des pcbs sera au point.
					/*
					 * if (!verificationSymetrieGlobalePcb()) {
					 * 
					 * String error = ""; if
					 * (!EcartAdmissibleEpaisseurCuivreEquivalenteMemeStratifie(
					 * )) { error += "\n- Condition stratifié non respectée"; }
					 * if (!EcartSymetrieAdmissibleEpaisseurCuivreEquivalente())
					 * { error +=
					 * "\n- Condition symétrie globale non respectée"; } Alert
					 * alert = new Alert(AlertType.ERROR);
					 * alert.setTitle("Probleme Symétrie PCB");
					 * alert.setHeaderText("Exigences non respéctées");
					 * alert.setContentText(error); alert.showAndWait();
					 * 
					 * }
					 */

				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		});

	}

	private Node getNode(GridPane gridPane, int col, int row) {
		for (Node node : gridPane.getChildren()) {
			if (GridPane.getColumnIndex(node) == col
					&& GridPane.getRowIndex(node) == row) {
				return node;
			}
		}
		return null;
	}

	public void init_button_generer_pcb() {
		button_generer_pcb = new Button("Générer circuit");

		button_generer_pcb.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				try {

					vb_pcb.getChildren().clear();

					// int i1 = 0, i2 = 0, i3 = 0;
					int nb_couche = 0;
					int nb_vialaser = 0;
					int nb_trou_enterre = 0;

					nb_couche = Integer.parseInt(champ_nb_couche.getText());
					nb_vialaser = Integer.parseInt(champ_nb_via_laser.getText());
					nb_trou_enterre = Integer.parseInt(champ_trou_enterre.getText());

					// Positivité et parité du nombre de couches
					if (nb_couche > 0 && nb_couche % 2 == 0) {

						champ_nb_couche.setStyle("-fx-border-color: default;");
						champ_nb_via_laser.setStyle("-fx-border-color: default;");
						champ_trou_enterre.setStyle("-fx-border-color: default;");

						pcb = new Pcb(nb_couche, nb_vialaser, nb_trou_enterre);

						liste_couches_pcb = pcb.get_liste_couches_pcb();

						button_calculer.setVisible(true);
						vb_pcb.getChildren().addAll(liste_couches_pcb);

						titre_pcb.setText("Couches PCB d'un nouveau circuit");

					} else {

						Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("Probleme de valeurs");
						alert.setHeaderText("Paramètres incrrects");
						alert.setContentText(
								"Le nombre de couches doit être pair !");
						champ_nb_couche.setStyle("-fx-border-color: red;");
						alert.showAndWait();

					}

				} catch (Exception e) {
					champ_nb_couche.setStyle("-fx-border-color: red;");
					champ_nb_via_laser.setStyle("-fx-border-color: red;");
					champ_trou_enterre.setStyle("-fx-border-color: red;");

					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Erreur generation PCB");
					alert.setHeaderText("Probleme de valeur");
					alert.setContentText(
							"Les valeurs du PCB ne sont pas correctes");
					alert.showAndWait();
				}

			}
		});
	}

	public void tous_calculs() {
		calculs_intermediaires();
		calculs_final_valeures_clees();
	}

	public void init_resine() {
		choix_resine.getItems().addAll(Ressources.RESINES);
		resine = new Resine1755VPanasonic();
		choix_resine.setValue(Ressources.RESINE1755VPANASONIC);
		choix_resine.valueProperty().addListener(new ChangeListener<String>() {
			public void changed(
					ObservableValue<? extends String> arg0,
					String arg1,
					String arg2) {

				resine = get_Resine();//

			}
		});

	}

	public ModelPhysiqueStatique get_Resine() {
		if (choix_resine.getValue().equals(Ressources.RESINE1755VPANASONIC)) {
			return new Resine1755VPanasonic();
		} else if (choix_resine.getValue()
				.equals(Ressources.RESINEPCL370ISOLA)) {
			return new ResinePCL370Isola();
		} else if (choix_resine.getValue()
				.equals(Ressources.RESINE700GHITACHI)) {
			return new Resine700GHitachi();
		} else if (choix_resine.getValue()
				.equals(Ressources.RESINEMCLE679FGHITACHI)) {
			return new ResineMCLE679FGHitachi();
		}

		else {
			return new Resine1755VPanasonic();
		}
	}

	public void load_circuit(Pcb pcb) {
		// ciu = new Ciu();

		pcb.load_Circuit(path + cb_circuits_existants.getValue() + ".pcb");
		vb_pcb.getChildren().clear();
		liste_couches_pcb = pcb.get_liste_couches_pcb();
		this.nombre_vialaser.setText(String.valueOf(pcb.nombre_vialaser));
		this.nombre_couche.setText(String.valueOf(pcb.nombre_couche));
		vb_pcb.getChildren().addAll(pcb.al);
		button_calculer.setVisible(true);
	}

	/**
	 * @param pcb    Onglet PCB correspondant à l'interface graphique
	 * @param pcbName chargement du PCB uniquement avec le nom
	**/

	public void load_circuit(Pcb pcb, String pcbName) {
		if (pcb == null || pcbName == null || pcbName.isEmpty()) {
			throw new IllegalArgumentException("PCB object or name cannot be null or empty");
		}

        //String.format("%s.pcb", pcbName);  // Safe concat for filename
        final Path fullPath = Ressources.PATH_PCB.resolve(pcbName).normalize().toAbsolutePath();  // Uses Path for separator handling
		System.out.println("fileName: " + pcbName);
		System.out.println("fullPath: " + fullPath);
		System.out.println("fullPath.normalize(): " + fullPath.normalize());
		System.out.println("Ressources.PATH_PCB: " + Ressources.PATH_PCB);

		pcb.load_Circuit(fullPath.toString());  // Pass the full string path if loadCircuit expects a string
		// Or better: update loadCircuit to take Path and use Files.newBufferedReader(fullPath) for reading

		this.resine = pcb.resine;
		this.liste_couches_pcb = pcb.get_liste_couches_pcb();
		tous_calculs();
	}

	/**
	 * @param pcb       Onglet PCB correspondant à l'interface graphique.
	 * @param cheminPCB chargement du PCB avec son chemin complet
	 */
	public void loadCircuitWithPathPCB(Pcb pcb, String cheminPCB) {
		pcb.load_Circuit(cheminPCB);
		liste_couches_pcb = pcb.get_liste_couches_pcb();
		tous_calculs();
	}

	public void init_line_charts() {

		if (titre_graphes != null) {
			getChildren().remove(titre_graphes);
		}

		hb_graph_e_cte = new HBox(150);
		hb_graph_e_cte.setPadding(new Insets(20, 20, 20, 20));

		final NumberAxis xAxis = new NumberAxis();
		final NumberAxis yAxis = new NumberAxis();

		yAxis.setAutoRanging(false);
		yAxis.setTickUnit(100.0);

		yAxis.setUpperBound(
				Math.max(
						Integer.parseInt(
								data.get(0).get_module_flexion_x().getValue()),
						Integer.parseInt(
								data.get(0).get_module_traction_x()
										.getValue())));
		yAxis.setLowerBound(
				Math.min(
						Integer.parseInt(
								data.get(data.size() - 1).get_module_flexion_x()
										.getValue()),
						Integer.parseInt(
								data.get(data.size() - 1)
										.get_module_traction_x().getValue())));

		xAxis.setLabel("Température(°C)");
		yAxis.setLabel("E(MPa)");

		lineChart = new LineChart<Number, Number>(xAxis, yAxis);

		XYChart.Series<
				Number,
				Number> series1 = new XYChart.Series<Number, Number>();
		series1.setName("module traction");
		XYChart.Series<
				Number,
				Number> series1_2 = new XYChart.Series<Number, Number>();
		series1_2.setName("module flexion");
		for (int i = 0; i < data.size(); i++) {
			series1.getData().add(
					new XYChart.Data<Number, Number>(
							data.get(i).temperature,
							Integer.parseInt(
									data.get(i).get_module_traction_x()
											.getValue())));
			series1_2.getData().add(
					new XYChart.Data<Number, Number>(
							data.get(i).temperature,
							Integer.parseInt(
									data.get(i).get_module_flexion_x()
											.getValue())));
		}

		final NumberAxis xAxis2 = new NumberAxis();
		final NumberAxis yAxis2 = new NumberAxis();

		yAxis2.setAutoRanging(false);

		yAxis2.setUpperBound(
				Math.max(
						data.get(data.size() - 1)
								.get_double_module_dilatation_plan(),
						data.get(data.size() - 1)
								.get_double_module_dilatation_z()));

		yAxis2.setLowerBound(
				Math.min(
						data.get(0).get_double_module_dilatation_plan(),
						data.get(0).get_double_module_dilatation_z()));

		xAxis2.setLabel("Température(°C)");
		yAxis2.setLabel("CTE(um/m/°C)");

		lineChart2 = new LineChart<Number, Number>(xAxis2, yAxis2);

		XYChart.Series<
				Number,
				Number> series2 = new XYChart.Series<Number, Number>();
		XYChart.Series<
				Number,
				Number> series3 = new XYChart.Series<Number, Number>();

		series2.setName("dilatation plan");
		series3.setName("dilatation axe z");

		for (int i = 0; i < data.size(); i++) {
			series2.getData().add(
					new XYChart.Data<Number, Number>(
							data.get(i).temperature,
							data.get(i).get_double_module_dilatation_plan()));
			series3.getData().add(
					new XYChart.Data<Number, Number>(
							data.get(i).temperature,
							data.get(i).get_double_module_dilatation_z()));
		}

		lineChart.getData().add(series1);
		lineChart.getData().add(series1_2);

		lineChart2.getData().add(series2);
		lineChart2.getData().add(series3);

		// ressources
		hb_graph_e_cte.getChildren().clear();
		hb_graph_e_cte.getChildren().addAll(lineChart, lineChart2);

		titre_graphes = new TitledPane();
		titre_graphes.setText("E et CTE");
		titre_graphes.setContent(hb_graph_e_cte);
		getChildren().add(titre_graphes);
	}

	public void circuit_simple() {
		getChildren().removeAll(
				proprietes_thermomecanique,
				buttonPrint,
				vb_graphes,
				button_calculer);

		vb_tool_bar.getChildren().removeAll(buttonSave, buttonLoad);

	}

	public void init_bar_Pcb() {

		hb_bar_pcb = new HBox();
		// NUMEROTER CORRECTEMENT LES TEXTES

		Text text_epaisseur_cuivre = new Text("Épaisseur\ncuivre (μm)");
		text_epaisseur_cuivre.setTextAlignment(TextAlignment.CENTER);
		text_epaisseur_cuivre.setStyle(Ressources.TEXTFIELD_STYLE);
		HBox.setMargin(text_epaisseur_cuivre, new Insets(0, 0, 0, 140));

		Text text_epaisseur_cuivre_recharge = new Text(
				"Épaisseur cuivre\nrecharge (μm)");
		text_epaisseur_cuivre_recharge.setTextAlignment(TextAlignment.CENTER);
		text_epaisseur_cuivre_recharge.setStyle(Ressources.TEXTFIELD_STYLE);
		HBox.setMargin(text_epaisseur_cuivre_recharge, new Insets(0, 0, 0, 10));

		Text text_tissus = new Text("Tissus");
		text_tissus.setTextAlignment(TextAlignment.CENTER);
		text_tissus.setStyle(Ressources.TEXTFIELD_STYLE);
		HBox.setMargin(text_tissus, new Insets(0, 0, 0, 15));

		Text text_nombre = new Text("Nombre");
		text_nombre.setTextAlignment(TextAlignment.CENTER);
		text_nombre.setStyle(Ressources.TEXTFIELD_STYLE);
		HBox.setMargin(text_nombre, new Insets(0, 0, 0, 40));

		Text text_taux_remplissage = new Text("Taux\nremplissage");
		text_taux_remplissage.setTextAlignment(TextAlignment.CENTER);
		text_taux_remplissage.setStyle(Ressources.TEXTFIELD_STYLE);
		HBox.setMargin(text_taux_remplissage, new Insets(0, 0, 0, 20));

		Text text_epaisseur_apres_pressage = new Text(
				"Épaisseur après\npressage (μm)");
		text_epaisseur_apres_pressage.setTextAlignment(TextAlignment.CENTER);
		;
		text_epaisseur_apres_pressage.setStyle(Ressources.TEXTFIELD_STYLE);
		HBox.setMargin(text_epaisseur_apres_pressage, new Insets(0, 0, 0, 10));

		Text text_epaisseur_cuivre_equivalent = new Text(
				"Épaisseur cuivre\nequivalent (μm)");
		text_epaisseur_cuivre_equivalent.setTextAlignment(TextAlignment.CENTER);
		text_epaisseur_cuivre_equivalent.setStyle(Ressources.TEXTFIELD_STYLE);
		HBox.setMargin(
				text_epaisseur_cuivre_equivalent,
				new Insets(0, 0, 0, 10));

		Text text_taux_resine = new Text("Taux\n de résine");
		text_taux_resine.setTextAlignment(TextAlignment.CENTER);
		text_taux_resine.setStyle(Ressources.TEXTFIELD_STYLE);
		HBox.setMargin(text_taux_resine, new Insets(0, 0, 0, 10));

		hb_bar_pcb.getChildren().add(text_epaisseur_cuivre);
		hb_bar_pcb.getChildren().add(text_epaisseur_cuivre_recharge);
		hb_bar_pcb.getChildren().add(text_tissus);
		hb_bar_pcb.getChildren().add(text_nombre);
		hb_bar_pcb.getChildren().add(text_taux_remplissage);
		hb_bar_pcb.getChildren().add(text_epaisseur_apres_pressage);
		hb_bar_pcb.getChildren().add(text_epaisseur_cuivre_equivalent);
		hb_bar_pcb.getChildren().add(text_taux_resine);

		// hbx.setSpacing(20);
		hb_bar_pcb.setStyle("-fx-background-color:  #D3D3D3");
		hb_bar_pcb.setMaxWidth(600);
		getChildren().add(hb_bar_pcb);

	}

	public void calculs_circuit() {
		for (int i = 0; i < (liste_couches_pcb.size() + 1) / 2; i++) {
			if (i % 2 == 0) {
				// on calcule l'epaisseur cuivre équivalent des couches paires (électriques)

				liste_couches_pcb.get(i).calcul();

			} else {
				// on donne au couche impaire (isolante) leurs couche electrique supérieure et inférieure
				liste_couches_pcb.get(i).calcul(
						(CoucheElectriquePcb) liste_couches_pcb.get(i - 1),
						(CoucheElectriquePcb) liste_couches_pcb.get(i + 1));

			}

		}

	}

	public double getTotaleNominalReel() {

		double d = ((Double.parseDouble(this.cuivre_reel.getText())
				+ Double.parseDouble(this.isolant_verre_epoxy.getText()))
				+ Ressources.VERNI_EPARGNE);

		return d;
	}

	// Nouvelle méthode pour calculer l'épaisseur physique du cuivre (sans pondération %)
	public double getCuivreNominalTotal() {
		double totalCuivre = 0;
		for (CouchePcb couche : liste_couches_pcb) {
			if (couche instanceof CoucheElectriquePcb) {
				CoucheElectriquePcb ce = (CoucheElectriquePcb) couche;
				// On ajoute l'épaisseur brute + la recharge
				totalCuivre += ce.get_epaisseur_cuivre() + ce.get_epaisseur_cuivre_recharge();
			}
		}
		// Conversion µm vers mm (/1000)
		return totalCuivre / 1000.0;
	}

	// Correction de la méthode calcul_totale_nominal_reel
	public void calcul_totale_nominal_reel() {

		// 1. Récupérer l'épaisseur des isolants pressés (Correct, car prend en compte le fluage)
		double epaisseurIsolants = Double.parseDouble(this.isolant_verre_epoxy.getText());

		// 2. Récupérer l'épaisseur physique du cuivre (Nouveau calcul)
		double epaisseurCuivres = getCuivreNominalTotal();

		// 3. Somme totale avec le vernis
		double total = epaisseurIsolants + epaisseurCuivres + Ressources.VERNI_EPARGNE;

		totale_nominal_reel.setText(String.valueOf(total));

		// Mise à jour de la propriété affichée
		p_epaisseur_totale_nominale.set_resultat(
				Outils.nombreChiffreApresVirgule(total, Ressources.NOMBRE_CHIFFRE_APRES_VIRGULE)
		);
	}

//	public void calcul_totale_nominal_reel() {
//		double d = ((Double.parseDouble(this.cuivre_reel.getText())
//				+ Double.parseDouble(this.isolant_verre_epoxy.getText()))
//				+ Ressources.VERNI_EPARGNE);
//		totale_nominal_reel.setText(String.valueOf(d));
//
//	}

	public void calcul_epaisseur_isolant_epoxy() {
		double calcul = 0;
		for (int i = 0; i < liste_couches_pcb.size(); i++) {
			if (!(i % 2 == 0)) {
				calcul = calcul + ((CoucheIsolantePcb) liste_couches_pcb.get(i))
						.get_epaisseur_apres_pressage();
			}

		}
		calcul = calcul / 1000;
		isolant_verre_epoxy.setText(String.valueOf(calcul));

	}

	public void calcul_cuivre_nominal_reel() {
		double calcul = 0;
		for (int i = 0; i < liste_couches_pcb.size(); i++) {
			if (i % 2 == 0) {
				calcul = calcul + ((((CoucheElectriquePcb) liste_couches_pcb
						.get(i)).get_epaisseur_cuivre()
						+ ((CoucheElectriquePcb) liste_couches_pcb.get(i))
								.get_epaisseur_cuivre_recharge())
						* ((CoucheElectriquePcb) liste_couches_pcb.get(i))
								.get_taux_remplissage());
			}

		}
		calcul = calcul / 100000;
		this.cuivre_reel.setText(String.valueOf(calcul));

	}

	public void calcul_cuivre_calcul_thermique() {
		double calcul = 0;
		for (int i = 0; i < liste_couches_pcb.size(); i++) {
			if (i % 2 == 0) {
				calcul = calcul
						+ (((CoucheElectriquePcb) liste_couches_pcb.get(i))
								.get_epaisseur_cuivre_equivalent());
			}

		}
		calcul = calcul / 1000;
		this.cuivre_thermique.setText(String.valueOf(calcul));

	}

	protected double getCuivreThermique() {
		double calcul = 0;
		for (int i = 0; i < liste_couches_pcb.size(); i++) {
			if (i % 2 == 0) {
				calcul = calcul
						+ (((CoucheElectriquePcb) liste_couches_pcb.get(i))
								.get_epaisseur_cuivre_equivalent());
			}

		}

		return calcul;
	}

	public void calcul_taux_ponderal_resine() {
		double calcul1 = 0;
		double calcul2 = 0;
		double res = 0;
		for (int i = 0; i < liste_couches_pcb.size(); i++) {
			if (!(i % 2 == 0)) {
				calcul1 = calcul1
						+ (((CoucheIsolantePcb) liste_couches_pcb.get(i))
								.get_nombre()
								* ((CoucheIsolantePcb) liste_couches_pcb.get(i))
										.get_epaisseur_stratifie()
								* ((CoucheIsolantePcb) liste_couches_pcb.get(i))
										.get_taux_resine());

				calcul2 = calcul2
						+ (((CoucheIsolantePcb) liste_couches_pcb.get(i))
								.get_nombre()
								* ((CoucheIsolantePcb) liste_couches_pcb.get(i))
										.get_epaisseur_stratifie());
			}

		}
		res = calcul1 / calcul2;
		taux_ponderal_resine.setText(String.valueOf(res));

	}

	public void calcul_ra() {
		double calcul = Double.parseDouble(taux_ponderal_resine.getText());
		double arrondi = ((double) Math.round(calcul * 100) / 100) / 100;

		ra.setText(String.valueOf(arrondi));

	}

	public void calcul_rb() {
		double calcul = 1 - Double.parseDouble(ra.getText());

		rb.setText(String.valueOf(calcul));

	}

	public void calcul_pa() {
		double calcul = 0;

		calcul = this.resine.masse_volumique(0);
		pa.setText(String.valueOf(calcul));

	}

	public void calcul_pb() {
		double calcul = 0;
		VerreE verre = new VerreE();
		calcul = verre.masse_volumique(0);

		pb.setText(String.valueOf(calcul));

	}

	public void calcul_ps() {
		double calcul1 = 0;
		calcul1 = this.get_ra() / this.get_pa();
		double calcul2 = 0;
		calcul2 = this.get_rb() / this.get_pb();

		double calcul = 1 / (calcul1 + calcul2);

		ps.setText(String.valueOf(calcul));

	}

	public void calcul_Va() {
		double calcul = 0;
		calcul = ((this.get_ra() * (Double.parseDouble(this.ps.getText())))
				/ this.get_pa());
		Va.setText(String.valueOf(calcul));
	}

	public void calcul_Vb() {
		double calcul = 0;
		calcul = 1 - Double.parseDouble(this.Va.getText());
		Vb.setText(String.valueOf(calcul));
	}

	public void calcul_a_xy() {
		double calcul = 0;
		calcul = Math.pow(this.get_Va(), (0.5));
		a_xy.setText(String.valueOf(calcul));
	}

	public void calcul_b_xy() {
		double calcul = 0;
		calcul = 1 - this.get_a_xy();
		b_xy.setText(String.valueOf(calcul));
	}

	public void calcul_ya() {
		double calcul = 0;

		calcul = this.resine.conductibilite(0);
		ya.setText(String.valueOf(calcul));
	}

	public void calcul_yb() {
		double calcul = 0;
		VerreE verre = new VerreE();
		calcul = verre.conductibilite(0);
		yb.setText(String.valueOf(calcul));
	}

	public void calcul_y_xy() {
		double calcul1 = 0;
		calcul1 = (this.get_ya() * this.get_yb())
				/ ((this.get_a_xy() * this.get_yb())
						+ (this.get_b_xy() * this.get_ya()));
		double calcul2 = 0;
		calcul2 = calcul1 * this.get_a_xy();

		double calcul = 0;
		calcul = calcul2 + (this.get_yb() * this.get_b_xy());

		this.y_xy.setText(String.valueOf(calcul));

	}

	public void calcul_y_z() {
		double calcul = 0;
		double calcul1 = 0;
		double calcul2 = 0;
		calcul1 = this.get_Va() / this.get_ya();
		calcul2 = this.get_Vb() / this.get_yb();
		calcul = 1 / (calcul1 + calcul2);

		this.y_z.setText(String.valueOf(calcul));

	}

	public void calcul_epaisseur_totale_nominale() {

		double d = Double.valueOf(this.totale_nominal_reel.getText());
		String s = String.valueOf(
				Outils.nombreChiffreApresVirgule(
						d,
						Ressources.NOMBRE_CHIFFRE_APRES_VIRGULE));
		p_epaisseur_totale_nominale.set_resultat(s);
	}

	public double getMasseVolumique() {
		double calcul = 0;
		double p_cuivre = 0;

		p_cuivre = Ressources.P_CUIVRE;
		calcul = ((this.get_ps() * this.get_isolant_verre_epoxy())
				+ (p_cuivre * this.get_cuivre_thermique()))
				/ (this.get_isolant_verre_epoxy()
						+ this.get_cuivre_thermique());

		return calcul;
	}

	public void calcul_masse_volumique() {

		System.out.println("calcul_masse_volumique: " + getMasseVolumique());
		p_masse_volumique.set_resultat(
				Outils.doubleToString(getMasseVolumique(), 3));

		System.out.println("calcul_masse_volumique: " + Outils.doubleToString(getMasseVolumique(), 3));
		System.out.println("calcul_masse_volumique: " + p_masse_volumique);
	}

	public void calcul_conductivite_xy() {
		double y_cuivre = 0;

		y_cuivre = Ressources.Y_CUIVRE;
		double calcul = 0;
		calcul = ((this.get_y_xy() * this.get_isolant_verre_epoxy())
				+ (y_cuivre * this.get_cuivre_thermique()))
				/ (this.get_isolant_verre_epoxy()
						+ this.get_cuivre_thermique());

		p_conductivite_thermique_xy.set_resultat(
				Outils.doubleToString(
						calcul,
						Ressources.NOMBRE_CHIFFRE_APRES_VIRGULE));
	}

	public void calcul_conductivite_z() {
		double calcul = 0;

		double y_cuivre = Ressources.Y_CUIVRE;
		calcul = (this.get_isolant_verre_epoxy() + this.get_cuivre_thermique())
				/ ((this.get_isolant_verre_epoxy() / this.get_y_z())
						+ (this.get_cuivre_thermique() / y_cuivre));
		p_conductivite_thermique_z.set_resultat(
				Outils.doubleToString(
						calcul,
						Ressources.NOMBRE_CHIFFRE_APRES_VIRGULE));
	}

	public void calcul_conductivite_z_trou_traversant() {

		double y_cuivre = Ressources.Y_CUIVRE;
		double calcul1 = 0;
		double calcul2 = 0;
		double calcul = 0;
		calcul1 = ((100 * this.get_conductivite_z())
				+ (Ressources.NOMBRE_TROU_TRAVERSANT * Math.PI)
						* ((Math.pow(Ressources.DIAMETRE_PERCAGE, 2))
								- Math.pow(
										(Ressources.DIAMETRE_PERCAGE
												- Ressources.VERNI_EPARGNE),
										2))
						/ 4 * y_cuivre);

		calcul2 = 100
				+ (Ressources.NOMBRE_TROU_TRAVERSANT * Math.PI) * (Math.pow(
						(Math.pow(Ressources.DIAMETRE_PERCAGE, 2))
								- (Ressources.DIAMETRE_PERCAGE
										- Ressources.VERNI_EPARGNE),
						2) / 4);

		calcul = calcul1 / calcul2;
		p_conductivite_thermique_5t.set_resultat(
				Outils.doubleToString(
						calcul,
						Ressources.NOMBRE_CHIFFRE_APRES_VIRGULE));
	}

	public double calcul_module_resine(double temperature) {
		return this.resine.module_elastique(temperature);// remplacer ici par
															// l'interr
	}

	public double calcul_a_xy_verre(double temperature) {
		VerreE verre = new VerreE();
		return verre.coef_dil_xy(temperature);
	}

	public double calcul_a_xy_resine(double temperature) {
		return this.resine.coef_dil_xy(temperature);
	}

	public double calcul_module_traction_verre(double temperature) {
		VerreE verre = new VerreE();
		return verre.module_elastique(temperature);
	}

	public int calcul_traction_ciu(double temperature) {

		double tractionXY = 0;

		for (Resultat_temperature_pcb resultat : data) {
			if (resultat.getTemperature() == temperature) {
				return (int) resultat.getCalculTractionXY();
			}
		}

		Resultat_temperature_pcb resultat = new Resultat_temperature_pcb(
				this,
				temperature);
		tractionXY = resultat.getCalculTractionXY();

		return (int) tractionXY;

	}

	public int calcul_cisaillement_xy_ciu(double temperature) {

		double module_traction_verre = 0;
		double module_resine = 0;
		double calcul_module_traction_materiau_isolant = 0;
		double calcul_module_traction_ciu = 0;
		double calcul_cisaillement_ciu = 0;

		VerreE verre = new VerreE();

		module_traction_verre = verre.module_elastique(temperature);

		module_resine = this.resine.module_elastique(temperature);

		calcul_module_traction_materiau_isolant = (module_resine
				* module_traction_verre
				* (Math.pow((2 * this.get_Va() + this.get_Vb()), 2))
				/ (4 * module_traction_verre * this.get_Va()
						+ 2 * module_resine * this.get_Vb()))
				+ (module_traction_verre * this.get_Vb() / 2);

		calcul_module_traction_ciu = (this.get_isolant_verre_epoxy()
				* calcul_module_traction_materiau_isolant
				+ this.get_cuivre_thermique() * Ressources.E_CUIVRE)
				/ (this.get_cuivre_thermique()
						+ this.get_isolant_verre_epoxy());

		calcul_module_traction_ciu = (double) Math
				.round(calcul_module_traction_ciu * 100) / 100;

		calcul_cisaillement_ciu = calcul_module_traction_ciu
				/ (2 * (1 + Ressources.COEF_POISSON));
		return (int) calcul_cisaillement_ciu;

	}

	public int calcul_cisaillement_z_ciu(double temperature) {

		double ga = 0;
		double gb = 0;
		double calcul_g_verre_global = 0;
		double calcul_cisaillement_z_ciu = 0;

		ga = this.calcul_module_resine(temperature)
				/ (2 * (1 + Ressources.COEF_POISSON));

		gb = this.calcul_module_traction_verre(temperature)
				/ (2 * (1 + Ressources.COEF_POISSON));

		calcul_g_verre_global = (this.get_a_xy() + this.get_b_xy())
				/ ((this.get_a_xy() / ga) + (this.get_b_xy() / gb));

		calcul_cisaillement_z_ciu = (this.get_isolant_verre_epoxy()
				+ this.get_cuivre_thermique())
				/ ((this.get_isolant_verre_epoxy() / calcul_g_verre_global)
						+ (this.get_cuivre_thermique() / Ressources.G_CUIVRE));

		return (int) calcul_cisaillement_z_ciu;

	}

	public double calcul_coef_dil_xy_ciu(double temperature) {
		double cteXY = 0;

		for (Resultat_temperature_pcb resultat : data) {
			if (resultat.getTemperature() == temperature) {
				return resultat.getCteXY();
			}
		}

		Resultat_temperature_pcb resultat = new Resultat_temperature_pcb(
				this,
				temperature);
		cteXY = resultat.getCteXY();

		return cteXY;

	}

	public double calcul_coef_dil_z_ciu(double temperature) {
		double a_verre = 0;
		double a_resine = 0;

		double calcul_coef_dil_z_materiau_isolant = 0;
		double calcul_coef_dil_z_ciu = 0;

		a_verre = this.calcul_a_xy_verre(temperature);

		a_resine = this.calcul_a_xy_resine(temperature);

		calcul_coef_dil_z_materiau_isolant = a_resine * this.get_Va()
				+ a_verre * this.get_Vb();

		calcul_coef_dil_z_ciu = (this.get_isolant_verre_epoxy()
				* calcul_coef_dil_z_materiau_isolant
				+ this.get_cuivre_reel() * Ressources.CTE_CUIVRE)
				/ (this.get_isolant_verre_epoxy() + this.get_cuivre_reel());

		calcul_coef_dil_z_ciu = (double) Math.round(calcul_coef_dil_z_ciu * 100)
				/ 100;

		return calcul_coef_dil_z_ciu;

	}

	public int calcul_flexion_ciu(double temperature) {
		/*
		 * double epaisseur_totale_ciu = 0; double epaisseur_differentielle = 0;
		 * double epaisseur_precedente = 0; double epaisseur_courrante = 0;
		 * double module_tissus = 0;
		 * 
		 * double module_couche_centrale = this.resine.module_tissus(
		 * temperature, ((CoucheIsolantePcb) this.liste_couches_pcb
		 * .get((this.liste_couches_pcb.size() / 2))) .get_choix_tissus());
		 * 
		 * double epaisseur_centrale = +(this.liste_couches_pcb
		 * .get((this.liste_couches_pcb.size() / 2))
		 * .epaisseur_totale_couche());
		 * 
		 * epaisseur_precedente = epaisseur_centrale; epaisseur_courrante =
		 * epaisseur_centrale;
		 * 
		 * for (int i = ((this.liste_couches_pcb.size() / 2) - 1); i >= 0; i--)
		 * {
		 * 
		 * if (i % 2 == 0) {
		 * 
		 * epaisseur_courrante = epaisseur_courrante + (((CoucheElectriquePcb)
		 * this.liste_couches_pcb.get(i)) .epaisseur_totale_couche() * 2);
		 * 
		 * epaisseur_differentielle = epaisseur_differentielle +
		 * (Ressources.E_CUIVRE (Math.pow(epaisseur_courrante, 3) -
		 * (Math.pow(epaisseur_precedente, 3))));
		 * 
		 * epaisseur_precedente = epaisseur_precedente + (((CoucheElectriquePcb)
		 * this.liste_couches_pcb.get(i)) .epaisseur_totale_couche() * 2);
		 * 
		 * }
		 * 
		 * else {
		 * 
		 * module_tissus = this.resine.module_tissus( temperature,
		 * ((CoucheIsolantePcb) this.liste_couches_pcb.get(i))
		 * .get_choix_tissus());
		 * 
		 * epaisseur_courrante = epaisseur_courrante + (((CoucheIsolantePcb)
		 * this.liste_couches_pcb.get(i)) .epaisseur_totale_couche() * 2);
		 * 
		 * epaisseur_differentielle = epaisseur_differentielle + (module_tissus
		 * * (Math.pow(epaisseur_courrante, 3) - (Math.pow(epaisseur_precedente,
		 * 3))));
		 * 
		 * epaisseur_precedente = epaisseur_precedente + (((CoucheIsolantePcb)
		 * this.liste_couches_pcb.get(i)) .epaisseur_totale_couche() * 2);
		 * 
		 * }
		 * 
		 * epaisseur_totale_ciu = epaisseur_totale_ciu +
		 * this.liste_couches_pcb.get(i).epaisseur_totale_couche() 2;
		 * module_tissus = 0;
		 * 
		 * }
		 * 
		 * epaisseur_differentielle = epaisseur_differentielle +
		 * module_couche_centrale * (Math.pow(epaisseur_centrale, 3));
		 * 
		 * epaisseur_totale_ciu = epaisseur_totale_ciu + epaisseur_centrale;
		 * 
		 * double calcul = epaisseur_differentielle /
		 * (Math.pow(epaisseur_totale_ciu, 3));
		 * 
		 * return (int) calcul;
		 */
		double flexionXY = 0;

		for (Resultat_temperature_pcb resultat : data) {
			if (resultat.getTemperature() == temperature) {
				return (int) resultat.getModuleFlexionXY();
			}
		}

		Resultat_temperature_pcb resultat = new Resultat_temperature_pcb(
				this,
				temperature);
		flexionXY = resultat.getModuleFlexionXY();

		return (int) flexionXY;

	}

	public void calculs_intermediaires() {
		calculs_circuit();
		calcul_epaisseur_isolant_epoxy();
		calcul_cuivre_nominal_reel();
		calcul_cuivre_calcul_thermique();
		calcul_totale_nominal_reel();
		calcul_taux_ponderal_resine();
		calcul_ra();
		calcul_rb();
		calcul_pa();
		calcul_pb();
		calcul_ps();
		calcul_Va();
		calcul_Vb();
		calcul_a_xy();
		calcul_b_xy();
		calcul_ya();
		calcul_yb();
		calcul_y_xy();
		calcul_y_z();
		calcul_epaisseur_totale_nominale();
		calcul_masse_volumique();
		calcul_conductivite_xy();
		calcul_conductivite_z();
		calcul_conductivite_z_trou_traversant();
	}

	public void calcul_final(double temperateur) {
		calcul_traction_ciu(temperateur);
		calcul_coef_dil_xy_ciu(temperateur);
		calcul_coef_dil_z_ciu(temperateur);
		calcul_cisaillement_xy_ciu(temperateur);
		calcul_cisaillement_z_ciu(temperateur);
		calcul_flexion_ciu(temperateur);
	}

	public void calculs_final_valeures_clees() {

		data = FXCollections.observableArrayList();

		for (double d : Ressources.tableau_temperatures) {
			calcul_final(d);
			data.add(new Resultat_temperature_pcb(this, d));
		}

		tb_Resultat_temperature_pcb.setItems(data);
		this.init_line_charts();
	}

	public double get_cuivre_thermique() {
		return Double.parseDouble(this.cuivre_thermique.getText());
	}

	public double get_cuivre_reel() {
		return Double.parseDouble(this.cuivre_reel.getText());
	}

	public double get_isolant_verre_epoxy() {
		return Double.parseDouble(this.isolant_verre_epoxy.getText());
	}

	public double get_ra() {
		return Double.parseDouble(this.ra.getText());
	}

	public double get_pa() {
		return Double.parseDouble(this.pa.getText());
	}

	public double get_rb() {
		return Double.parseDouble(this.rb.getText());
	}

	public double get_pb() {
		return Double.parseDouble(this.pb.getText());
	}

	public double get_ps() {
		return Double.parseDouble(this.ps.getText());
	}

	public double get_Va() {
		return Double.parseDouble(this.Va.getText());
	}

	public double get_Vb() {
		return Double.parseDouble(this.Vb.getText());
	}

	public double get_a_xy() {
		return Double.parseDouble(this.a_xy.getText());
	}

	public double get_b_xy() {
		return Double.parseDouble(this.b_xy.getText());
	}

	public double get_ya() {
		return Double.parseDouble(this.ya.getText());
	}

	public double get_yb() {
		return Double.parseDouble(this.yb.getText());
	}

	public double get_y_xy() {
		return Double.parseDouble(this.y_xy.getText());
	}

	public double get_y_z() {
		return Double.parseDouble(this.y_z.getText());
	}

	public double get_conductivite_z() {
		return Double.parseDouble(this.conductivite_z.getText());
	}

	public double get_masse_volumique() {
		return Double.parseDouble(this.masse_volumique.getText());
	}

	public double get_totale_nominal_reel() {
		return Outils
				.StringWithCommaToDouble(this.totale_nominal_reel.getText());
		// return Double.parseDouble(this.totale_nominal_reel.getText());
	}

	public ArrayList<CouchePcb> get_couches_pcb() {
		return this.liste_couches_pcb;
	}

	public Pcb get_pcb() {
		return this.pcb;
	}

	public void init_pcb() {
		this.pcb = new Pcb();
		this.liste_couches_pcb = pcb.get_liste_couches_pcb();
		System.out.println("Init PCB List Couche !");
		for (CouchePcb e_pcb : this.liste_couches_pcb) {
			System.out.println(e_pcb);
		}
	}

	/**
	 * Rafraîchit la liste des items de la ComboBox des pcb sauvegardées
	 */
	public void rafraichir_cb_liste_pcb() {
		this.cb_circuits_existants.getItems().clear();
		this.noms_fichiers = Outils.get_list_filename_directory(path, ".pcb");
		this.cb_circuits_existants.getItems().addAll(this.noms_fichiers);
	}

	/**
	 * @return Un booléen pour déterminer si la symétrie globale du Pcb est
	 *         respecté (Cela évite les trop grands déséquillibres).
	 */
	public boolean verificationSymetrieGlobalePcb() {

		return EcartAdmissibleEpaisseurCuivreEquivalenteMemeStratifie()
				&& EcartSymetrieAdmissibleEpaisseurCuivreEquivalente();
	}

	/**
	 * @return Un booléen pour déterminer si l'écart admissible du taux de
	 *         remplissage de cuivre sur le même stratifié est respecté.
	 */
	public boolean EcartAdmissibleEpaisseurCuivreEquivalenteMemeStratifie() {

		double epaisseurCuivreEquivalentCouchePrecedente;
		double epaisseurCuivreEquivalentCoucheSuivante;
		double epaisseurCuivreEntourantStratifie;
		boolean exigencesConformes = true;

		for (int i = 0; i < pcb.al.size(); i++) {

			// Couche impaire
			if (i % 2 != 0) {

				if (((CoucheIsolantePcb) pcb.al.get(i)).isStratifie()) {

					// On regarde les couches qui entourent la couche stratifié
					epaisseurCuivreEquivalentCouchePrecedente = ((CoucheElectriquePcb) pcb.al
							.get(i - 1)).get_epaisseur_cuivre_equivalent();
					epaisseurCuivreEquivalentCoucheSuivante = ((CoucheElectriquePcb) pcb.al
							.get(i + 1)).get_epaisseur_cuivre_equivalent();
					epaisseurCuivreEntourantStratifie = ((CoucheElectriquePcb) pcb.al
							.get(i - 1)).get_epaisseur_cuivre();

					if (!(Math.abs(
							epaisseurCuivreEquivalentCouchePrecedente
									- epaisseurCuivreEquivalentCoucheSuivante) <= (Math
											.log(
													epaisseurCuivreEntourantStratifie)
											- 3.3))) {

						// Coloration taux remplisage en rouge
						((CoucheElectriquePcb) pcb.al
								.get(i - 1)).taux_remplissage.setStyle(
										"-fx-font-weight: bold;-fx-text-fill:red;-fx-border-color: red;");
						((CoucheElectriquePcb) pcb.al
								.get(i + 1)).taux_remplissage.setStyle(
										"-fx-font-weight: bold;-fx-text-fill:red;-fx-border-color: red;");

						exigencesConformes = false;

					} else {
						// Coloration taux remplissage en "normal"
						((CoucheElectriquePcb) pcb.al
								.get(i - 1)).taux_remplissage.setStyle(
										"-fx-font-weight: default;-fx-text-fill: default;-fx-border-color: default;");
						((CoucheElectriquePcb) pcb.al
								.get(i + 1)).taux_remplissage.setStyle(
										"-fx-font-weight: default;-fx-text-fill: default;-fx-border-color: default;");

					}
				}
			}
		}
		return exigencesConformes;
	}

	/**
	 * @return Un booléen pour déterminer si l'écart de symétrie globale
	 *         admissible est respecté.
	 */
	public boolean EcartSymetrieAdmissibleEpaisseurCuivreEquivalente() {

		double epaisseurCuivreEquivalentCoucheSymetrique1;
		double epaisseurCuivreEquivalentCoucheSymetrique2;
		double epaisseurCuivre;
		double epaisseurCuivreRecharge;
		boolean exigencesConformes = true;

		for (int i = 0; i < (pcb.al.size() + 1) / 4; i++) {

			epaisseurCuivreEquivalentCoucheSymetrique1 = ((CoucheElectriquePcb) pcb.al
					.get(2 * i)).get_epaisseur_cuivre_equivalent();

			epaisseurCuivreEquivalentCoucheSymetrique2 = ((CoucheElectriquePcb) pcb.al
					.get((pcb.al.size() - 1) - 2 * i))
							.get_epaisseur_cuivre_equivalent();

			epaisseurCuivre = ((CoucheElectriquePcb) pcb.al.get(2 * i))
					.get_epaisseur_cuivre();

			epaisseurCuivreRecharge = ((CoucheElectriquePcb) pcb.al.get(2 * i))
					.get_epaisseur_cuivre_recharge();

			if (!(Math.abs(
					epaisseurCuivreEquivalentCoucheSymetrique1
							- epaisseurCuivreEquivalentCoucheSymetrique2) <= (Math
									.log(
											epaisseurCuivre
													+ epaisseurCuivreRecharge)
									- 3.3))) {

				((CoucheElectriquePcb) pcb.al.get(2 * i)).taux_remplissage
						.setStyle(
								"-fx-font-weight: bold;-fx-text-fill:red;-fx-border-color: red;");

				((CoucheElectriquePcb) pcb.al.get(
						(pcb.al.size() - 1) - 2 * i)).taux_remplissage.setStyle(
								"-fx-font-weight: bold;-fx-text-fill:red;-fx-border-color: red;");

				exigencesConformes = false;

			} else {

				((CoucheElectriquePcb) pcb.al.get(2 * i)).taux_remplissage
						.setStyle(
								"-fx-font-weight: default;-fx-text-fill:default;-fx-border-color: default;");

				((CoucheElectriquePcb) pcb.al.get(
						(pcb.al.size() - 1) - 2 * i)).taux_remplissage.setStyle(
								"-fx-font-weight: default;-fx-text-fill:default;-fx-border-color: default;");

			}

		}

		return exigencesConformes;
	}
}
