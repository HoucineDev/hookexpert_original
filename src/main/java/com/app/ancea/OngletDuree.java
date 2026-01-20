package com.app.ancea;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.*;
import javafx.stage.FileChooser.ExtensionFilter;

public class OngletDuree extends VBox {
	static final ImageView image = new ImageView(
			OngletDuree.class.getResource("images/cycle.png").toExternalForm());

	// classe représentant l'onglet de calculs des durées de vie

	VBox onglet_duree;
	Duree duree;
	Button printButton;

	private HBox hb_resultats;

	private HashMap<String, String> infoComposants;

	protected Propriete dommageTotal;
	protected Propriete nombreEquivalentDeCycleDeTest;

	private TitledPane titre_pcb_existant;
	private TitledPane titre_composant_existant;
	private TitledPane titre_cycle_reference;
	private TitledPane titre_cycle, titre_proprietes;

	private ComboBox<String> cb_categorie_composant;
	private ComboBox<String> cb_composant_brase;
	private ComboBox<String> cb_type_composant;
	private ComboBox<String> cb_composant_existant;
	private ComboBox<String> cb_pcb_existants;
	private ComboBox<String> cb_profils;

	final Tooltip tooltip = new Tooltip();

	private CheckBox cbx_matrice_creuse;

	private String PCB;
	private String pathProfil;
	private Composant composant;
	private BrasureComposant brasure;

	private boolean matrice_creuse;

	private Label n50;
	private final String stringN50 = "N50%: ";

	private FileChooser fileChooser;

	private ObservableList<ProfilDeVie> list_profils;

	private Button calculer, valider, sauvegarder, rafraichirPcb,
			rafraichirProfil, campagne_tests;

	private GridPane gp_titre_composant_existant;
	private GridPane gp_titre_parametres_profils;
	private GridPane gp_titre_pcb_existant;
	private GridPane gp_titre_profils;

	private GridPane gp_nb_phase, gp_profils;
	private GridPane gp_t1, gp_d1, gp_t2, gp_d2;

	private GridPane gp_x1t1, gp_x1t2, gp_x2t1, gp_x2t2;

	private TextField nb_phase;
	private TextField t1, d1, t2, d2;
	private TextField x1t1, x1t2, x2t1, x2t2;

	private TableView<ProfilDeVie> tv_phases;
	private TableView<Propriete> tv_resultats;

	private TableColumn<ProfilDeVie, String> colonne_temp1, colonne_temp2,
			colonne_duree1, colonne_duree2, colonne_occurences,
			colonne_dommages;
	private TableColumn<ProfilDeVie, Number> colonne_n50;

	private HBox hb_parametres, hb_cb_composant_brase_button_calculer,
			hb_x1t1_x1t2_x2t1_x2t2, hb_calcul_sauvegarder,
			hb_profildevie_proprietes;
	private VBox vb_parametres_composants;

	private ToggleGroup group;
	private RadioButton rb_creation_profil, rb_charger_profil;
	private Label labelTraitementCampagne;

	protected double intN50;
	protected String matCap;
	private boolean isAdminMode = false;

	public OngletDuree(int spacing) {

		super(spacing);

		setPadding(new Insets(20, 10, 10, 0));

		hb_parametres = new HBox(5);

		hb_cb_composant_brase_button_calculer = new HBox();
		hb_profildevie_proprietes = new HBox();
		hb_profildevie_proprietes.setPadding(new Insets(0, 20, 0, 0));
		hb_x1t1_x1t2_x2t1_x2t2 = new HBox(10);

		hb_resultats = new HBox(5);
		hb_resultats.setPadding(new Insets(20, 0, 0, 20));
		hb_resultats.setMaxHeight(310);

		init_parametres_cycle_profil();
		init_tableview();
		init_composant_existant();
		init_tableview_resultats();

		hb_profildevie_proprietes.getChildren()
				.addAll(titre_cycle, titre_proprietes);
		HBox.setHgrow(titre_cycle, Priority.NEVER);
		HBox.setHgrow(titre_proprietes, Priority.ALWAYS);
		this.getChildren().add(hb_profildevie_proprietes);

	}

	public void setAdminMode(boolean isAdmin) {
		this.isAdminMode = isAdmin;
		System.out.println("OngletDuree: setAdminMode called with: " + isAdmin); // Debug

		// Update visibility of admin-only components
		updateAdminVisibility();
	}

	private void setupCampagneTestsAction() {
		campagne_tests.setOnAction(event -> {
			// Sélection des fichiers sur le Thread UI
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Sélection du fichier de campagne");
			fileChooser.setInitialDirectory(new File(Ressources.pathCampagne));
			File sourceFile = fileChooser.showOpenDialog(getScene().getWindow());

			if (sourceFile == null) return;

			fileChooser.setTitle("Enregistrer les résultats");
			fileChooser.setInitialDirectory(new File(Ressources.pathExcel));
			fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV (*.csv)", "*.csv"));
			File destinationFile = fileChooser.showSaveDialog(getScene().getWindow());

			if (destinationFile == null) return;

			// Lancement du traitement
			runCampagneTask(sourceFile, destinationFile);
		});
	}

//	private void runCampagneTask(File source, File destination) {
//		Task<Integer> campagneTask = new Task<>() {
//			@Override
//			protected Integer call() throws Exception {
//				long totalLines = Files.lines(source.toPath()).count();
//				int processedCount = 0;
//
//				try (BufferedReader br = new BufferedReader(new FileReader(source));
//					 BufferedWriter fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destination), "UTF-8"))) {
//
//					// Écriture de l'en-tête
//					fw.write("Famille/PCB;PCB;Composant + Brasure;T1;t1;T2;t2;N50% test;B;dg1;dg2;dgfinal;A;Hi;Lnp1;NombreX;dgfinal/(A+1);N50% Calcule\n");
//
//					String line;
//					while ((line = br.readLine()) != null) {
//						if (isCancelled()) break;
//
//						// Appel de votre logique de calcul complexe
//						processSingleLine(line, fw, processedCount + 1);
//
//						processedCount++;
//						updateProgress(processedCount, totalLines);
//						updateMessage("Traitement : " + processedCount + " / " + totalLines);
//					}
//				}
//				return processedCount;
//			}
//		};
//
//		showProgressPopup(campagneTask);
//		new Thread(campagneTask).start();
//	}

	private void runCampagneTask(File source, File destination) {
		// Track start time for the "Resume" message
		long startTime = System.currentTimeMillis();

		Task<CampaignResult> campagneTask = new Task<>() {
			@Override
			protected CampaignResult call() throws Exception {
				long totalLines = Files.lines(source.toPath()).count();
				int processed = 0;
				int success = 0;
				int errors = 0;

				try (BufferedReader br = new BufferedReader(new FileReader(source));
					 BufferedWriter fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destination), "UTF-8"))) {

					fw.write("Famille/PCB;PCB;Composant + Brasure;T1;t1;T2;t2;N50% test;B;dg1;dg2;dgfinal;A;Hi;Lnp1;NombreX;dgfinal/(A+1);N50% Calcule\n");

					String line;
					while ((line = br.readLine()) != null) {
						if (isCancelled()) break;
						try {
							processSingleLine(line, fw, processed + 1);
							success++;
						} catch (Exception e) {
							errors++;
							System.err.println("Erreur ligne " + (processed + 1) + ": " + e.getMessage());
						}
						processed++;
						updateProgress(processed, totalLines);
						updateMessage("Traitement : " + processed + " / " + totalLines);
					}
				}
				return new CampaignResult(processed, success, errors, isCancelled());
			}
		};

		// Show the progress bar popup
		Stage progressPopup = showProgressPopupWithReturn(campagneTask);

		campagneTask.setOnSucceeded(e -> {
			progressPopup.close();
			CampaignResult res = campagneTask.getValue();
			long endTime = System.currentTimeMillis();
			java.time.Duration duration = java.time.Duration.ofMillis(endTime - startTime);

			// Call the new Resume/Success Dialog
			showCompletionDialog("Campagne Terminée", destination, res.processed, res.success, res.errors, duration, res.cancelled);
		});

		campagneTask.setOnFailed(e -> {
			progressPopup.close();
			Outils.alerteUtilisateur("ERROR", "Échec", "Une erreur critique est survenue durant le calcul.");
		});

		new Thread(campagneTask).start();
	}

	// Centralisation de votre logique métier originale
//	private void processSingleLine(String line, BufferedWriter fw, int index) throws IOException {
//		String[] data = line.split(";");
//		String pcbName = data[0];
//		String brasureName = data[1];
//		double t1Val = Double.parseDouble(data[2]);
//		double d1Val = Double.parseDouble(data[3]);
//		double t2Val = Double.parseDouble(data[4]);
//		double d2Val = Double.parseDouble(data[5]);
//		int n50Test = Integer.parseInt(data[6]);
//		double facteurForme = Double.parseDouble(data[7]);
//
//		Composant compTest = null;
//		BrasureComposant brasTest = null;
//		boolean isBille = false;
//		boolean isSbgaOrCbga = false;
//		boolean isPcbga = false;
//
//		// Identification du composant (votre logique d'extension)
//		if (brasureName.endsWith("brlcc")) {
//			brasTest = new BrasureLCC(Ressources.pathBrasuresTests + brasureName);
//			((BrasureLCC) brasTest).setCalculs();
//			compTest = new ComposantLCC("", Ressources.pathComposants + ((BrasureLCC) brasTest).getFormat() + ".lcc");
//		} else if (brasureName.endsWith("brcbga")) {
//			isBille = true; isSbgaOrCbga = true;
//			brasTest = new BrasureComposantBilles(Ressources.pathBrasuresTests + brasureName);
//			compTest = new ComposantCBGA();
//			((ComposantCBGA) compTest).charger_cbga(Ressources.pathComposants + ((BrasureComposantBilles) brasTest).getNom() + ".cbga");
//		} else if (brasureName.endsWith("brwlp")) {
//			isBille = true;
//			brasTest = new BrasureComposantBilles(Ressources.pathBrasuresTests + brasureName);
//			compTest = new ComposantWLP();
//			((ComposantWLP) compTest).charger_wlp(Ressources.pathComposants + ((BrasureComposantBilles) brasTest).getNom() + ".wlp");
//		} else if (brasureName.endsWith("brres")) {
//			brasTest = new BrasureResistance(Ressources.pathBrasuresTests + brasureName);
//			((BrasureResistance) brasTest).setCalculs();
//			compTest = new Composant_Resistance(((BrasureResistance) brasTest).getFormat());
//		} else if (brasureName.endsWith("brqfn")) {
//			brasTest = new BrasureQFN(Ressources.pathBrasuresTests + brasureName);
//			((BrasureQFN) brasTest).setCalculs();
//			compTest = new ComposantQFN(((BrasureQFN) brasTest).getFormat());
//		} else if (brasureName.endsWith("brcap")) {
//			brasTest = new BrasureCapacite(Ressources.pathBrasuresTests + brasureName);
//			((BrasureCapacite) brasTest).setCalculs();
//			compTest = new Composant_Capacite(((BrasureCapacite) brasTest).getFormat(), ((BrasureCapacite) brasTest).getMateriauCapacite(), ((BrasureCapacite) brasTest).getValeurCapacite());
//		} else if (brasureName.endsWith("brbga") || brasureName.endsWith("brcsp") || brasureName.endsWith("brsbga") || brasureName.endsWith("brpcbga")) {
//			isBille = true;
//			if(brasureName.endsWith("brsbga")) isSbgaOrCbga = true;
//			if(brasureName.endsWith("brpcbga")) isPcbga = true;
//			brasTest = new BrasureComposantBilles(Ressources.pathBrasuresTests + brasureName);
//			OngletPcb o = new OngletPcb(); o.init_pcb();
//			if(brasureName.endsWith("brbga")) compTest = new ComposantBGA(o, Ressources.pathComposants + ((BrasureComposantBilles) brasTest).getNom() + ".bga");
//			else if(brasureName.endsWith("brcsp")) compTest = new ComposantCSP(o, Ressources.pathComposants + ((BrasureComposantBilles) brasTest).getNom() + ".csp");
//			else if(brasureName.endsWith("brsbga")) compTest = new ComposantSBGA(o, Ressources.pathComposants + ((BrasureComposantBilles) brasTest).getNom() + ".sbga");
//			else if(brasureName.endsWith("brpcbga")) compTest = new ComposantPCBGA(o, Ressources.pathComposants + ((BrasureComposantBilles) brasTest).getNom() + ".pcbga");
//		}
//
//		// Calculs Physiques
//		ProfilDeVie pdv = new ProfilDeVie(t1Val, t2Val, d1Val, d2Val, 0.0, "PCB tests\\" + pcbName, compTest, brasTest);
//		double n50Calc = pdv.getN50();
//		double g1 = pdv.getyc11();
//		double g2 = pdv.getyc12();
//		double gFinal = Math.abs(g1 - g2);
//		double sc = brasTest.getSc();
//		double gSC = gFinal / (sc + 1);
//
//		// Écriture CSV
//		fw.write(index + ";");
//		fw.write(pcbName.replace(".pcb", "") + ";");
//		String cleanBrasure = brasureName;
//		if(isSbgaOrCbga) cleanBrasure = brasureName.substring(0, brasureName.length()-7);
//		else if(isPcbga) cleanBrasure = brasureName.substring(0, brasureName.length()-8);
//		else cleanBrasure = brasureName.substring(0, brasureName.length()-6);
//		fw.write(cleanBrasure + ";" + t1Val + ";" + d1Val + ";" + t2Val + ";" + d2Val + ";");
//		fw.write(n50Test + ";" + facteurForme + ";" + g1 + ";" + g2 + ";" + gFinal + ";" + sc + ";");
//
//		if (isBille) {
//			fw.write(";" + pdv.getLnp1ComposantABilles() + ";" + pdv.getNombreXComposantABilles() + ";");
//		} else {
//			fw.write(pdv.getHauteurIntegree() + ";;;");
//		}
//		fw.write(gSC + ";" + Math.round(n50Calc) + "\n");
//	}

	// Centralisation de votre logique métier originale avec extraction Hi pour tous
	private void processSingleLine(String line, BufferedWriter fw, int index) throws IOException {
		String[] data = line.split(";");
		String pcbName = data[0];
		String brasureName = data[1];
		double t1Val = Double.parseDouble(data[2]);
		double d1Val = Double.parseDouble(data[3]);
		double t2Val = Double.parseDouble(data[4]);
		double d2Val = Double.parseDouble(data[5]);
		int n50Test = Integer.parseInt(data[6]);
		double facteurForme = Double.parseDouble(data[7]);

		Composant compTest = null;
		BrasureComposant brasTest = null;
		boolean isBille = false;

		// --- Component Identification Logic ---
		if (brasureName.endsWith("brlcc")) {
			brasTest = new BrasureLCC(Ressources.pathBrasuresTests + brasureName);
			((BrasureLCC) brasTest).setCalculs();
			compTest = new ComposantLCC("", Ressources.pathComposants + ((BrasureLCC) brasTest).getFormat() + ".lcc");
		} else if (brasureName.endsWith("brcbga")) {
			isBille = true;
			brasTest = new BrasureComposantBilles(Ressources.pathBrasuresTests + brasureName);
			compTest = new ComposantCBGA();
			((ComposantCBGA) compTest).charger_cbga(Ressources.pathComposants + ((BrasureComposantBilles) brasTest).getNom() + ".cbga");
		} else if (brasureName.endsWith("brwlp")) {
			isBille = true;
			brasTest = new BrasureComposantBilles(Ressources.pathBrasuresTests + brasureName);
			compTest = new ComposantWLP();
			((ComposantWLP) compTest).charger_wlp(Ressources.pathComposants + ((BrasureComposantBilles) brasTest).getNom() + ".wlp");
		} else if (brasureName.endsWith("brres")) {
			brasTest = new BrasureResistance(Ressources.pathBrasuresTests + brasureName);
			((BrasureResistance) brasTest).setCalculs();
			compTest = new Composant_Resistance(((BrasureResistance) brasTest).getFormat());
		} else if (brasureName.endsWith("brqfn")) {
			brasTest = new BrasureQFN(Ressources.pathBrasuresTests + brasureName);
			((BrasureQFN) brasTest).setCalculs();
			compTest = new ComposantQFN(((BrasureQFN) brasTest).getFormat());
		} else if (brasureName.endsWith("brcap")) {
			brasTest = new BrasureCapacite(Ressources.pathBrasuresTests + brasureName);
			((BrasureCapacite) brasTest).setCalculs();
			compTest = new Composant_Capacite(((BrasureCapacite) brasTest).getFormat(), ((BrasureCapacite) brasTest).getMateriauCapacite(), ((BrasureCapacite) brasTest).getValeurCapacite());
		} else if (brasureName.endsWith("brbga") || brasureName.endsWith("brcsp") || brasureName.endsWith("brsbga") || brasureName.endsWith("brpcbga")) {
			isBille = true;
			brasTest = new BrasureComposantBilles(Ressources.pathBrasuresTests + brasureName);
			OngletPcb o = new OngletPcb(); o.init_pcb();
			if(brasureName.endsWith("brbga")) compTest = new ComposantBGA(o, Ressources.pathComposants + ((BrasureComposantBilles) brasTest).getNom() + ".bga");
			else if(brasureName.endsWith("brcsp")) compTest = new ComposantCSP(o, Ressources.pathComposants + ((BrasureComposantBilles) brasTest).getNom() + ".csp");
			else if(brasureName.endsWith("brsbga")) compTest = new ComposantSBGA(o, Ressources.pathComposants + ((BrasureComposantBilles) brasTest).getNom() + ".sbga");
			else if(brasureName.endsWith("brpcbga")) compTest = new ComposantPCBGA(o, Ressources.pathComposants + ((BrasureComposantBilles) brasTest).getNom() + ".pcbga");
		}

		// --- Physics Engine ---
		ProfilDeVie pdv = new ProfilDeVie(t1Val, t2Val, d1Val, d2Val, 0.0, "PCB tests\\" + pcbName, compTest, brasTest);

		// --- Data Extraction Logic (Inspired by your snippet) ---
		double hi = -1, lnp1 = -1, nbX = -1;

		if (isBille) {
			nbX = pdv.getNombreXComposantABilles();
			lnp1 = pdv.getLnp1ComposantABilles();
			if (brasTest instanceof BrasureComposantBilles brBilles) {
				// Check technology for proper Hi extraction
				if ("NSMD".equals(brBilles.getTechnologie())) {
					hi = brBilles.getHauteurIntegreeNsmd();
				} else {
					hi = brBilles.getHauteurIntegreeSmd();
				}
			}
		} else {
			hi = pdv.getHauteurIntegree(); // Base Hi for non-bille components

			if (compTest instanceof ComposantLCC lcc) {
				// Specific extraction for LCCC
				nbX = lcc.calculNombreX(lcc, false, "nombreX");
				lnp1 = lcc.calculNombreX(lcc, false, "lnp1");
			} else if (compTest instanceof ComposantQFN qfn) {
				// Specific extraction for QFN
				nbX = qfn.getXX();
				lnp1 = qfn.getLnp();
			}
		}

		// --- Final Calculations ---
		double n50Calc = pdv.getN50();
		double g1 = pdv.getyc11();
		double g2 = pdv.getyc12();
		double gFinal = Math.abs(g1 - g2);
		double sc = brasTest.getSc();
		double gSC = gFinal / (sc + 1);

		// --- CSV Generation ---
		fw.write(index + ";");
		fw.write(pcbName.replace(".pcb", "") + ";");

		// Clean extension from name for the display column
		String cleanName = brasureName.replaceAll("\\.br[a-z]+$", "");
		fw.write(cleanName + ";" + t1Val + ";" + d1Val + ";" + t2Val + ";" + d2Val + ";");
		fw.write(n50Test + ";" + facteurForme + ";" + g1 + ";" + g2 + ";" + gFinal + ";" + sc + ";");

		// Write the new metrics columns
		fw.write((hi != -1 ? hi : "") + ";" + (lnp1 != -1 ? lnp1 : "")  + ";" + (nbX != -1 ? nbX : "")  + ";");

		fw.write(gSC + ";" + Math.round(n50Calc) + "\n");
	}

	private Stage showProgressPopupWithReturn(Task<?> task) {
		Stage popup = new Stage();
		popup.initModality(Modality.APPLICATION_MODAL);
		popup.initStyle(StageStyle.UNDECORATED);

		VBox layout = new VBox(10);
		layout.setStyle("-fx-padding: 25; -fx-background-color: #f4f4f4; -fx-border-color: #333; -fx-border-radius: 5; -fx-background-radius: 5;");
		layout.setAlignment(javafx.geometry.Pos.CENTER);

		Label title = new Label("Campagne de Tests en cours");
		title.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

		ProgressBar pb = new ProgressBar();
		pb.setPrefWidth(300);
		pb.progressProperty().bind(task.progressProperty());

		Label msg = new Label();
		msg.textProperty().bind(task.messageProperty());

		Button cancel = new Button("Annuler le traitement");
		cancel.setOnAction(e -> task.cancel());

		layout.getChildren().addAll(title, pb, msg, cancel);
		popup.setScene(new Scene(layout));

		task.setOnSucceeded(e -> { popup.close(); Outils.alerteUtilisateur("CONFIRMATION", "Succès", "Traitement terminé !"); });
		task.setOnCancelled(e -> popup.close());
		task.setOnFailed(e -> { popup.close(); Outils.alerteUtilisateur("ERROR", "Erreur", "Le traitement a échoué."); });

		popup.show();
		return popup;
	}

	private void showCompletionDialog(String title, File outputFile, int processed, int success, int errors, java.time.Duration totalTime, boolean cancelled) {
		Stage stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setTitle(title);

		VBox root = new VBox(15);
		root.setPadding(new Insets(20));
		root.setAlignment(javafx.geometry.Pos.CENTER);
		root.setStyle("-fx-background-color: white; -fx-border-color: #dcdcdc; -fx-border-width: 1; -fx-background-radius: 10; -fx-border-radius: 10;");

		Label header = new Label(cancelled ? "⚠️ Traitement Annulé" : "✅ Calcul Terminé avec Succès");
		header.setFont(Font.font("System", javafx.scene.text.FontWeight.BOLD, 16));

		GridPane stats = new GridPane();
		stats.setHgap(10); stats.setVgap(5);
		stats.setAlignment(javafx.geometry.Pos.CENTER);
		stats.add(new Label("Total traités:"), 0, 0); stats.add(new Label(String.valueOf(processed)), 1, 0);
		stats.add(new Label("Succès:"), 0, 1);      stats.add(new Label(String.valueOf(success)), 1, 1);
		stats.add(new Label("Erreurs:"), 0, 2);     stats.add(new Label(String.valueOf(errors)), 1, 2);

		long s = totalTime.getSeconds();
		String timeStr = String.format("%d min %d sec", s / 60, s % 60);
		stats.add(new Label("Temps total:"), 0, 3); stats.add(new Label(timeStr), 1, 3);

		TextField pathDisplay = new TextField(outputFile.getAbsolutePath());
		pathDisplay.setEditable(false);
		pathDisplay.setPrefWidth(400);

		Button btnOpen = new Button("📂 Ouvrir le dossier");
		btnOpen.setOnAction(e -> {
			try { java.awt.Desktop.getDesktop().open(outputFile.getParentFile()); }
			catch (Exception ex) { ex.printStackTrace(); }
		});

		Button btnClose = new Button("Fermer");
		btnClose.setOnAction(e -> stage.close());

		HBox buttons = new HBox(10, btnOpen, btnClose);
		buttons.setAlignment(javafx.geometry.Pos.CENTER);

		root.getChildren().addAll(header, stats, new Label("Fichier enregistré sous :"), pathDisplay, buttons);
		stage.setScene(new Scene(root));
		stage.show();
	}

	// Simple internal class to hold results
	private static class CampaignResult {
		int processed, success, errors;
		boolean cancelled;
		CampaignResult(int p, int s, int e, boolean c) {
			this.processed = p; this.success = s; this.errors = e; this.cancelled = c;
		}
	}


	private void updateAdminVisibility() {
		// Campagne Tests button - admin only
		if (campagne_tests != null) {
			campagne_tests.setVisible(isAdminMode);
			campagne_tests.setManaged(isAdminMode); // Removes the empty space if hidden
			System.out.println("btnCampagneTests visibility: " + isAdminMode); // Debug
		}

		// Add other admin-only elements here
		// if (btnAutreAdminFeature != null) {
		//     btnAutreAdminFeature.setVisible(isAdminMode);
		//     btnAutreAdminFeature.setManaged(isAdminMode);
		// }
	}

	private double getWidth(double n) {

		Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

		return (primaryScreenBounds.getWidth() / n) - 40;
	}

	private double getDommageTotal() {
		double total = 0;
		int i = 0;

		while (colonne_dommages.getCellData(i) != null) {
			total += Outils
					.StringWithCommaToDouble(colonne_dommages.getCellData(i));
			i++;
		}
		return total;

	}

	private void refreshDommageTotalEtNombreCycleEquivalent() {

		double dommages = getDommageTotal();
		double n50CycleDeReference = Double.valueOf(intN50);
		dommageTotal.set_resultat(String.valueOf(dommages));
		nombreEquivalentDeCycleDeTest
				.set_resultat(String.valueOf(dommages * n50CycleDeReference));
	}

	private void init_tableview_resultats() {
		tv_resultats = new TableView<Propriete>();

		dommageTotal = new Propriete("Dommage total");
		nombreEquivalentDeCycleDeTest = new Propriete(
				"Nombre de cycle equivalent");

		TableColumn<
				Propriete,
				String> colonne_propriete = new TableColumn<Propriete, String>(
						"Proprietes");
		colonne_propriete
				.setCellValueFactory(c -> c.getValue().get_nom_variable());

		TableColumn<
				Propriete,
				String> colonne_resultat = new TableColumn<Propriete, String>(
						"Resultat");
		colonne_resultat.setCellValueFactory(c -> c.getValue().get_resultat());

		tv_resultats.getColumns().addAll(colonne_propriete, colonne_resultat);
		tv_resultats.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		tv_resultats.setItems(
				FXCollections.observableArrayList(
						dommageTotal,
						nombreEquivalentDeCycleDeTest));

		titre_proprietes = new TitledPane();
		titre_proprietes.setPrefWidth(getWidth(4));
		titre_proprietes.setCollapsible(false);
		titre_proprietes.setPadding(new Insets(0, 0, 0, 20));
		titre_proprietes.setText("Resultat proprietes");
		titre_proprietes.setContent(tv_resultats);
	}

	private void init_tableview() {

		colonne_temp1 = new TableColumn<ProfilDeVie, String>("Température T1");
		colonne_temp1.setCellValueFactory(new PropertyValueFactory<>("t1"));
		colonne_temp1.setCellFactory(
				TextFieldTableCell.<ProfilDeVie>forTableColumn());
		colonne_temp1.setOnEditCommit(event -> {
			String value = event.getNewValue();
			((ProfilDeVie) event.getTableView().getItems()
					.get(event.getTablePosition().getRow()))
							.setT1(Double.valueOf(value));
			refreshDommageTotalEtNombreCycleEquivalent();
			tv_phases.refresh();
		});

		colonne_temp2 = new TableColumn<ProfilDeVie, String>("Température T2");
		colonne_temp2.setCellValueFactory(new PropertyValueFactory<>("t2"));
		colonne_temp2.setCellFactory(
				TextFieldTableCell.<ProfilDeVie>forTableColumn());
		colonne_temp2.setOnEditCommit(event -> {
			String value = event.getNewValue();
			((ProfilDeVie) event.getTableView().getItems()
					.get(event.getTablePosition().getRow()))
							.setT2(Double.valueOf(value));
			refreshDommageTotalEtNombreCycleEquivalent();
			tv_phases.refresh();

		});

		colonne_duree1 = new TableColumn<ProfilDeVie, String>("Temps t1");
		colonne_duree1.setCellValueFactory(new PropertyValueFactory<>("d1"));
		colonne_duree1.setCellFactory(
				TextFieldTableCell.<ProfilDeVie>forTableColumn());
		colonne_duree1.setOnEditCommit(event -> {
			String value = event.getNewValue();
			((ProfilDeVie) event.getTableView().getItems()
					.get(event.getTablePosition().getRow()))
							.setD1(Double.valueOf(value));
			refreshDommageTotalEtNombreCycleEquivalent();
			tv_phases.refresh();

		});

		colonne_duree2 = new TableColumn<ProfilDeVie, String>("Temps t2");
		colonne_duree2.setCellValueFactory(new PropertyValueFactory<>("d2"));
		colonne_duree2.setCellFactory(
				TextFieldTableCell.<ProfilDeVie>forTableColumn());
		colonne_duree2.setOnEditCommit(event -> {
			String value = event.getNewValue();
			((ProfilDeVie) event.getTableView().getItems()
					.get(event.getTablePosition().getRow()))
							.setD2(Double.valueOf(value));
			refreshDommageTotalEtNombreCycleEquivalent();
			tv_phases.refresh();

		});

		colonne_occurences = new TableColumn<ProfilDeVie, String>("Occurences");
		colonne_occurences
				.setCellValueFactory(new PropertyValueFactory<>("occurences"));
		colonne_occurences.setCellFactory(
				TextFieldTableCell.<ProfilDeVie>forTableColumn());
		colonne_occurences.setOnEditCommit(event -> {
			String value = event.getNewValue();
			((ProfilDeVie) event.getTableView().getItems()
					.get(event.getTablePosition().getRow()))
							.setOccurences(value);
			refreshDommageTotalEtNombreCycleEquivalent();
			tv_phases.refresh();

		});

		colonne_n50 = new TableColumn<ProfilDeVie, Number>("N(50%)");
		colonne_n50.setCellValueFactory(
				c -> new SimpleIntegerProperty((int) c.getValue().getN50()));

		colonne_dommages = new TableColumn<ProfilDeVie, String>("Dommage");
		colonne_dommages.setCellValueFactory(
				c -> new SimpleStringProperty(
						Outils.nombreChiffreScientifiqueApresVirgule(
								Double.valueOf(
										c.getValue().getDommages().get()))));

		tv_phases.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		tv_phases.getColumns().addAll(
				colonne_temp1,
				colonne_duree1,
				colonne_temp2,
				colonne_duree2,
				colonne_occurences,
				colonne_n50,
				colonne_dommages);

		for (TableColumn<ProfilDeVie, ?> res : tv_phases.getColumns()) {
			res.setStyle("-fx-alignment: CENTER;");
		}
	}

	private void clear_composants_existants(int composant_restant) {
		while (gp_titre_composant_existant.getChildren()
				.size() > composant_restant) {
			gp_titre_composant_existant.getChildren().remove(
					gp_titre_composant_existant.getChildren().size() - 1);
		}
	}

	private void clear_parametre_phase(int composant_restant) {
		while (gp_titre_parametres_profils.getChildren()
				.size() > composant_restant) {
			gp_titre_parametres_profils.getChildren().remove(
					gp_titre_parametres_profils.getChildren().size() - 1);
		}
	}

	protected void exportToCsv() {

		fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(new File(Ressources.pathExcel));
		fileChooser.getExtensionFilters().add(
				new ExtensionFilter("Resultat PCB Excel (*.csv)", "*.csv"));

		File file = fileChooser.showSaveDialog(getScene().getWindow());

		if (file != null) {
			try (FileWriter writer = new FileWriter(file)) {
				for (TableColumn<ProfilDeVie, ?> colonne : tv_phases
						.getColumns()) {

					String colonneTableView = colonne.getText() + ";";
					writer.write(colonneTableView);

				}
				writer.write("\n");

				for (int i = 0; i < list_profils.size(); i++) {
					String ligneTemperatureTableView = list_profils.get(i)
							.toString() + "\n";
					writer.write(ligneTemperatureTableView);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void init_parametres_cycle_profil() {

		calculer = new Button("Calculer DV");
		calculer.setDisable(true);

		campagne_tests = new Button("Campagne Tests");
		labelTraitementCampagne = new Label("Veuillez Patienter");
		labelTraitementCampagne.setStyle(
				"-fx-font-weight: bold;-fx-text-fill:red;-fx-border-color: red;");
		labelTraitementCampagne.setVisible(false);

		rafraichirPcb = new Button("");
		Image imageMiseAJour = new Image(
				getClass().getResourceAsStream(
						"images/icons8-mises-à-jour-disponibles-24.png"));
		ImageView imageViewPcb = new ImageView(imageMiseAJour);
		rafraichirPcb.setGraphic(imageViewPcb);
		rafraichirPcb.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				cb_pcb_existants.getItems().clear();
				cb_pcb_existants.setItems(
						FXCollections.observableArrayList(
								Outils.get_list_filename_with_extension(
										Ressources.pathPCB)));

			}
		});

		rafraichirProfil = new Button("");
		ImageView imageViewProfil = new ImageView(imageMiseAJour);
		rafraichirProfil.setGraphic(imageViewProfil);
		rafraichirProfil.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {

				try {
					chargerProfil(pathProfil);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		valider = new Button("Valider");
		valider.setVisible(false);

		cbx_matrice_creuse = new CheckBox("Matrice creuse");
		cbx_matrice_creuse.setVisible(
				false); /* déjà codé, il faut juste passer la variable à true */
		cbx_matrice_creuse.selectedProperty()
				.addListener(new ChangeListener<Boolean>() {

					@Override
					public void changed(
							ObservableValue<? extends Boolean> arg0,
							Boolean arg1,
							Boolean arg2) {
						if (arg2) {
							hb_x1t1_x1t2_x2t1_x2t2.setVisible(true);
							matrice_creuse = arg2;
						} else {
							hb_x1t1_x1t2_x2t1_x2t2.setVisible(false);
							matrice_creuse = arg2;
						}

					}
				});

		cbx_matrice_creuse.setSelected(false);
		hb_x1t1_x1t2_x2t1_x2t2.setVisible(false);

		sauvegarder = new Button("Sauvegarder profil");
		sauvegarder.setVisible(false);
		Image image = new Image(
				getClass().getResourceAsStream(
						"images/icons8-sauvegarder-16.png"));
		sauvegarder.setGraphic(new ImageView(image));

		gp_titre_parametres_profils = Outils.generer_gridPane();

		n50 = new Label(stringN50);

		hb_calcul_sauvegarder = new HBox();
		hb_calcul_sauvegarder.getChildren().addAll(valider, sauvegarder);
		hb_calcul_sauvegarder.setPadding(new Insets(15, 0, 0, 0));

		group = new ToggleGroup();
		tv_phases = new TableView<ProfilDeVie>();
		tv_phases.setEditable(true);

		rb_creation_profil = new RadioButton("Création de profil");
		rb_creation_profil.setToggleGroup(group);
		rb_creation_profil.setDisable(true);
		rb_charger_profil = new RadioButton("Charger un profil");
		rb_charger_profil.setToggleGroup(group);
		rb_charger_profil.setDisable(true);

		gp_titre_parametres_profils.add(new Label("Cycle de référence"), 0, 0);
		gp_titre_parametres_profils.add(rb_charger_profil, 1, 3);
		gp_titre_parametres_profils.add(rb_creation_profil, 0, 3);
		gp_titre_parametres_profils.add(n50, 2, 1);
		gp_titre_parametres_profils.add(calculer, 2, 2);
		gp_titre_parametres_profils.add(campagne_tests, 2, 3);
		gp_titre_parametres_profils.add(labelTraitementCampagne, 2, 4);

		x1t1 = new TextField("");
		x1t1.setMaxWidth(40);
		x1t2 = new TextField("");
		x1t2.setMaxWidth(40);
		x2t1 = new TextField("");
		x2t1.setMaxWidth(40);
		x2t2 = new TextField("");
		x2t2.setMaxWidth(40);

		gp_x1t1 = Outils.generer_textfield_avec_label(x1t1, "x1 t1");
		gp_x1t2 = Outils.generer_textfield_avec_label(x1t2, "x1 t2");
		gp_x2t1 = Outils.generer_textfield_avec_label(x2t1, "x2 t1");
		gp_x2t2 = Outils.generer_textfield_avec_label(x2t2, "x2 t2");

		hb_x1t1_x1t2_x2t1_x2t2.getChildren()
				.addAll(gp_x1t1, gp_x1t2, gp_x2t1, gp_x2t2);

		t1 = Outils.generer_textfield("");
		t1.setText("125");
		t2 = Outils.generer_textfield("");
		t2.setText("-55");
		d1 = Outils.generer_textfield("");
		d1.setText("15");
		d2 = Outils.generer_textfield("");
		d2.setText("15");

		gp_t1 = Outils.generer_textfield_avec_label(t1, "Température T1 (°C)");
		gp_t2 = Outils.generer_textfield_avec_label(t2, "Température T2 (°C)");
		gp_d1 = Outils.generer_textfield_avec_label(d1, "Temps t1 (min)");
		gp_d2 = Outils.generer_textfield_avec_label(d2, "Temps t2 (min)");

		gp_titre_parametres_profils.add(gp_t1, 0, 1);
		gp_titre_parametres_profils.add(gp_d1, 1, 1);
		gp_titre_parametres_profils.add(gp_t2, 0, 2);
		gp_titre_parametres_profils.add(gp_d2, 1, 2);

		gp_titre_parametres_profils.add(hb_calcul_sauvegarder, 1, 4);

//		campagne_tests.setOnAction(new EventHandler<ActionEvent>() {
//			@Override
//			public void handle(ActionEvent arg0) {
//
//				// Liste des pcbs tests
//				ArrayList<String> liste_pcb_test = new ArrayList<String>();
//				// Liste des brasures tests
//				ArrayList<String> liste_brasure_test = new ArrayList<String>();
//				// Liste des cycles thermique tests
//				ArrayList<Double> liste_profil_test = new ArrayList<Double>();
//				// Liste des N50% testés réellement
//				ArrayList<Integer> liste_N50_test = new ArrayList<Integer>();
//				// Liste des facteurs de forme testés réellement
//				ArrayList<Double> liste_facteur_forme_test = new ArrayList<
//						Double>();
//
//				// Ligne de lecture du fichier
//				String line;
//
//				// Fichier de campagne
//				File file_campagne = null;
//
//				FileChooser fileChooser = new FileChooser();
//				fileChooser.setTitle("Selection du fichier de campagne de tests");
//				fileChooser.setInitialDirectory(new File(Ressources.pathCampagne));
//				file_campagne = fileChooser.showOpenDialog(getScene().getWindow());
//
//				if (file_campagne != null) {
//					labelTraitementCampagne.setVisible(true);
//					FileReader fr = null;
//					try {
//						fr = new FileReader(file_campagne);
//					} catch (FileNotFoundException e1) {
//						// TODO Auto-generated catch block
//						e1.printStackTrace();
//						Outils.alerteUtilisateur(
//								"ERROR",
//								"Problème fichier",
//								"Fichier non trouvé !");
//						labelTraitementCampagne.setVisible(false);
//						return;
//					}
//					BufferedReader br = new BufferedReader(fr);
//
//					int pb_ligne = 0;
//					try {
//						while ((line = br.readLine()) != null) {
//							// Numéro de ligne
//							pb_ligne++;
//
//							String[] strings = line.split(";");
//							// Nom pcb
//							liste_pcb_test.add(strings[0]);
//							// Nom brasure
//							liste_brasure_test.add(strings[1]);
//							// Temperature 1
//							liste_profil_test.add(Double.valueOf(strings[2]));
//							// Durée 1
//							liste_profil_test.add(Double.valueOf(strings[3]));
//							// Temperature 2
//							liste_profil_test.add(Double.valueOf(strings[4]));
//							// Durée 2
//							liste_profil_test.add(Double.valueOf(strings[5]));
//							// N50% testés réellement
//							liste_N50_test.add(Integer.valueOf(strings[6]));
//							// facteur de forme testés réellement
//							liste_facteur_forme_test
//									.add(Double.valueOf(strings[7]));
//						}
//						fr.close();
//					} catch (NumberFormatException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//						Outils.alerteUtilisateur(
//								"ERROR",
//								"Problèmes Données",
//								"Données corrompue ligne " + pb_ligne
//										+ "\ndu fichier "
//										+ file_campagne.getName());
//						labelTraitementCampagne.setVisible(false);
//						return;
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//						Outils.alerteUtilisateur(
//								"ERROR",
//								"Statut Campagne",
//								"Problème fichier " + file_campagne.getName());
//						labelTraitementCampagne.setVisible(false);
//						return;
//					} catch (ArrayIndexOutOfBoundsException e) {
//						Outils.alerteUtilisateur(
//								"ERROR",
//								"Statut Campagne",
//								"Problème ligne " + pb_ligne + "\n Fichier "
//										+ file_campagne.getName());
//						labelTraitementCampagne.setVisible(false);
//						return;
//					}
//
//					// Ouverture d'un classeur
//					fileChooser = new FileChooser();
//					fileChooser.setInitialDirectory(
//							new File(Ressources.pathExcel));
//					fileChooser.getExtensionFilters().add(
//							new ExtensionFilter(
//									"Resultat Campagne Tests (*.csv)",
//									"*.csv"));
//					File file = fileChooser
//							.showSaveDialog(getScene().getWindow());
//
//					// Ecriture dans le classeur
//					if (file != null) {
//						// FileWriter fw = null;
//						BufferedWriter fw = null;
//						try {
//							fw = new BufferedWriter(
//									new OutputStreamWriter(
//											new FileOutputStream(file),
//											"UTF-8"));
//						} catch (UnsupportedEncodingException
//								| FileNotFoundException e2) {
//							// TODO Auto-generated catch block
//							e2.printStackTrace();
//						}
//						try {
//							// fw = new FileWriter(file);
//							// fw = new OutputStreamWriter(fos, "UTF-16");
//							// Colonnes du Excel des résultats de la campagne
//							/*
//							 * fw.write( "Famille/PCB;" + "PCB;" +
//							 * "Composant + Brasure;" + "T1;" + "t1;" + "T2;" +
//							 * "t2;" + "N50% test;" + "\u03B2;" + "\u0394" +
//							 * "\u0263" + "1;" + "\u0394" + "\u0263" + "2;" +
//							 * "\u0394" + "\u0263" + "final;" + "A;" + "Hi;" +
//							 * "Lnp1;" + "NombreX;" + "\u0394" + "\u0263" +
//							 * "final/(A+1);" + "N50% Calcule\n");
//							 */
//
//							fw.write(
//									"Famille/PCB;" + "PCB;"
//											+ "Composant + Brasure;" + "T1;"
//											+ "t1;" + "T2;" + "t2;"
//											+ "N50% test;" + "B;" + "dg1;"
//											+ "dg2;" + "dgfinal;" + "A;" + "Hi;"
//											+ "Lnp1;" + "NombreX;"
//											+ "dgfinal/(A+1);"
//											+ "N50% Calcule\n");
//
//						} catch (IOException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//							try {
//								fw.close();
//							} catch (IOException e1) {
//								// TODO Auto-generated catch block
//								e1.printStackTrace();
//							}
//							return;
//						}
//						// Utile pour ProfilDeVie
//						int j = 0;
//						for (int i = 0; i < liste_pcb_test.size(); i++) {
//
//							Composant composant_test = null;
//							BrasureComposant brasure_test = null;
//
//							boolean presenceLCC = false;
//							boolean presenceWLP = false;
//							boolean presenceResistance = false;
//							boolean presenceQFN = false;
//							boolean presenceCapacite = false;
//							boolean presenceBga = false;
//							boolean presenceCsp = false;
//							boolean presenceSbga = false;
//							boolean presenceCbga = false;
//							boolean presencePcbga = false;
//							boolean presenceComposantABilles = false;
//
//							if (liste_brasure_test.get(i)
//									.substring(
//											liste_brasure_test.get(i).length()
//													- 5,
//											liste_brasure_test.get(i).length())
//									.equals("brlcc")) {
//
//								presenceLCC = true;
//
//								brasure_test = new BrasureLCC(
//										Ressources.pathBrasuresTests
//												+ liste_brasure_test.get(i));
//
//								((BrasureLCC) brasure_test).setCalculs();
//
//								// composant_test = new ComposantLCC(
//								// ((BrasureLCC) brasure_test)
//								// .getFormat());
//
//								composant_test = new ComposantLCC(
//										"",
//										Ressources.pathComposants
//												+ ((BrasureLCC) brasure_test)
//														.getFormat()
//												+ ".lcc");
//
//							} else if (liste_brasure_test.get(i)
//									.substring(
//											liste_brasure_test.get(i).length()
//													- 6,
//											liste_brasure_test.get(i).length())
//									.equals("brcbga")) {
//
//								presenceCbga = true;
//								presenceComposantABilles = true;
//
//								brasure_test = new BrasureComposantBilles(
//										Ressources.pathBrasuresTests
//												+ liste_brasure_test.get(i));
//
//								OngletPcb onglet = new OngletPcb();
//								onglet.init_pcb();
//
//								composant_test = new ComposantCBGA();
//								((ComposantCBGA) composant_test).charger_cbga(
//										Ressources.pathComposants
//												+ ((BrasureComposantBilles) brasure_test)
//														.getNom()
//												+ ".cbga");
//
//							} else if (liste_brasure_test.get(i)
//									.substring(
//											liste_brasure_test.get(i).length()
//													- 5,
//											liste_brasure_test.get(i).length())
//									.equals("brwlp")) {
//
//								presenceWLP = true;
//								presenceComposantABilles = true;
//
//								brasure_test = new BrasureComposantBilles(
//										Ressources.pathBrasuresTests
//												+ liste_brasure_test.get(i));
//
//								OngletPcb onglet = new OngletPcb();
//								onglet.init_pcb();
//
//								composant_test = new ComposantWLP();
//								((ComposantWLP) composant_test).charger_wlp(
//										Ressources.pathComposants
//												+ ((BrasureComposantBilles) brasure_test)
//														.getNom()
//												+ ".wlp");
//
//								/*
//								 * Identification d'une Resistance par
//								 * l'extension du fichier
//								 */
//							} else if (liste_brasure_test.get(i)
//									.substring(
//											liste_brasure_test.get(i).length()
//													- 5,
//											liste_brasure_test.get(i).length())
//									.equals("brres")) {
//
//								presenceResistance = true;
//
//								brasure_test = new BrasureResistance(
//										Ressources.pathBrasuresTests
//												+ liste_brasure_test.get(i));
//								((BrasureResistance) brasure_test).setCalculs();
//								composant_test = new Composant_Resistance(
//										((BrasureResistance) brasure_test)
//												.getFormat());
//
//							} else if (liste_brasure_test.get(i)
//									.substring(
//											liste_brasure_test.get(i).length()
//													- 5,
//											liste_brasure_test.get(i).length())
//									.equals("brqfn")) {
//
//								presenceQFN = true;
//
//								brasure_test = new BrasureQFN(
//										Ressources.pathBrasuresTests
//												+ liste_brasure_test.get(i));
//								((BrasureQFN) brasure_test).setCalculs();
//								composant_test = new ComposantQFN(
//										((BrasureQFN) brasure_test)
//												.getFormat());
//
//							} else if (liste_brasure_test.get(i)
//									.substring(
//											liste_brasure_test.get(i).length()
//													- 5,
//											liste_brasure_test.get(i).length())
//									.equals("brcap")) {
//
//								presenceCapacite = true;
//
//								brasure_test = new BrasureCapacite(
//										Ressources.pathBrasuresTests
//												+ liste_brasure_test.get(i));
//
//								((BrasureCapacite) brasure_test).setCalculs();
//
//								composant_test = new Composant_Capacite(
//										((BrasureCapacite) brasure_test)
//												.getFormat(),
//										((BrasureCapacite) brasure_test)
//												.getMateriauCapacite(),
//										((BrasureCapacite) brasure_test)
//												.getValeurCapacite());
//
//							} else if (liste_brasure_test.get(i)
//									.substring(
//											liste_brasure_test.get(i).length()
//													- 5,
//											liste_brasure_test.get(i).length())
//									.equals("brbga")) {
//
//								presenceBga = true;
//								presenceComposantABilles = true;
//
//								brasure_test = new BrasureComposantBilles(
//										Ressources.pathBrasuresTests
//												+ liste_brasure_test.get(i));
//
//								OngletPcb onglet = new OngletPcb();
//								onglet.init_pcb();
//
//								composant_test = new ComposantBGA(
//										onglet,
//										Ressources.pathComposants
//												+ ((BrasureComposantBilles) brasure_test)
//														.getNom()
//												+ ".bga");
//
//							} else if (liste_brasure_test.get(i)
//									.substring(
//											liste_brasure_test.get(i).length()
//													- 7,
//											liste_brasure_test.get(i).length())
//									.equals("brpcbga")) {
//
//								presencePcbga = true;
//								presenceComposantABilles = true;
//
//								brasure_test = new BrasureComposantBilles(
//										Ressources.pathBrasuresTests
//												+ liste_brasure_test.get(i));
//
//								OngletPcb onglet = new OngletPcb();
//								onglet.init_pcb();
//
//								composant_test = new ComposantPCBGA(
//										onglet,
//										Ressources.pathComposants
//												+ ((BrasureComposantBilles) brasure_test)
//														.getNom()
//												+ ".pcbga");
//
//							} else if (liste_brasure_test.get(i)
//									.substring(
//											liste_brasure_test.get(i).length()
//													- 5,
//											liste_brasure_test.get(i).length())
//									.equals("brcsp")) {
//
//								presenceCsp = true;
//								presenceComposantABilles = true;
//
//								brasure_test = new BrasureComposantBilles(
//										Ressources.pathBrasuresTests
//												+ liste_brasure_test.get(i));
//
//								OngletPcb onglet = new OngletPcb();
//								onglet.init_pcb();
//
//								composant_test = new ComposantCSP(
//										onglet,
//										Ressources.pathComposants
//												+ ((BrasureComposantBilles) brasure_test)
//														.getNom()
//												+ ".csp");
//							} else if (liste_brasure_test.get(i)
//									.substring(
//											liste_brasure_test.get(i).length()
//													- 6,
//											liste_brasure_test.get(i).length())
//									.equals("brsbga")) {
//
//								presenceSbga = true;
//								presenceComposantABilles = true;
//
//								brasure_test = new BrasureComposantBilles(
//										Ressources.pathBrasuresTests
//												+ liste_brasure_test.get(i));
//
//								OngletPcb onglet = new OngletPcb();
//								onglet.init_pcb();
//
//								composant_test = new ComposantSBGA(
//										onglet,
//										Ressources.pathComposants
//												+ ((BrasureComposantBilles) brasure_test)
//														.getNom()
//												+ ".sbga");
//							} else {
//								Outils.alerteUtilisateur(
//										"ERROR",
//										"Statut Campagne",
//										"Erreur Brasure: "
//												+ liste_brasure_test.get(i)
//												+ "\nLigne "
//												+ String.valueOf(i + 1)
//												+ " du fichier "
//												+ file.getName());
//								labelTraitementCampagne.setVisible(false);
//								return;
//							}
//
//							ProfilDeVie pdv = new ProfilDeVie(
//									liste_profil_test.get(j),
//									liste_profil_test.get(j + 2),
//									liste_profil_test.get(j + 1),
//									liste_profil_test.get(j + 3),
//									0.0,
//									"PCB tests\\" + liste_pcb_test.get(i),
//									composant_test,
//									brasure_test);
//
//							double intN50;
//							double gamma_c1;
//							double gamma_c2;
//
//							intN50 = pdv.getN50();
//							gamma_c1 = pdv.getyc11();
//							gamma_c2 = pdv.getyc12();
//
//							double hauteurIntegree;
//							double nombreX;
//							double lnp1;
//
//							if (presenceComposantABilles) {
//								nombreX = pdv.getNombreXComposantABilles();
//								lnp1 = pdv.getLnp1ComposantABilles();
//								hauteurIntegree = -1;
//							} else {
//								lnp1 = -1;
//								nombreX = -1;
//								hauteurIntegree = pdv.getHauteurIntegree();
//							}
//
//							double gamma_final;
//
//							gamma_final = Outils
//									.absolute_difference(gamma_c1, gamma_c2);
//
//							double section_critique = 0;
//							double gamma_section_critique = 0;
//
//							if (presenceLCC) {
//
//								section_critique = ((BrasureLCC) brasure_test)
//										.getSc();
//								// section_critique = 0;
//								// gamma_section_critique = 0;
//								gamma_section_critique = gamma_final
//										/ (section_critique + 1);
//							} else if (presenceWLP || presenceCbga
//									|| presenceBga || presenceSbga
//									|| presenceCsp || presencePcbga) {
//
//								section_critique = ((BrasureComposantBilles) brasure_test)
//										.getSc();
//								gamma_section_critique = gamma_final
//										/ (section_critique + 1);
//
//							} else if (presenceResistance) {
//
//								section_critique = ((BrasureResistance) brasure_test)
//										.getSc();
//								gamma_section_critique = gamma_final
//										/ (section_critique + 1);
//
//							} else if (presenceQFN) {
//
//								section_critique = ((BrasureQFN) brasure_test)
//										.getSc();
//								gamma_section_critique = gamma_final
//										/ (section_critique + 1);
//
//							} else if (presenceCapacite) {
//
//								section_critique = ((BrasureCapacite) brasure_test)
//										.getSc();
//								gamma_section_critique = gamma_final
//										/ (section_critique + 1);
//
//							} else {
//								Outils.alerteUtilisateur(
//										"ERROR",
//										"Statut Campagne",
//										"Cette partie de code doit être"
//												+ "\nimplémentée pour ce type de "
//												+ "composant ");
//								labelTraitementCampagne.setVisible(false);
//								return;
//							}
//
//							try {
//								fw.write(String.valueOf(i + 1));
//								fw.write(";");
//								// On écrit en supprimant l'extension ".pcb"
//								fw.write(
//										liste_pcb_test.get(i).substring(
//												0,
//												liste_pcb_test.get(i).length()
//														- 4));
//								fw.write(";");
//
//								/*
//								 * On écrit en supprimant l'extension ".brxxx"
//								 * Toutes les brasure sont sous la formee
//								 * ".brxxx" exeptées les brasure sbga
//								 * (".brsbga")
//								 */
//								if (presenceSbga || presenceCbga) {
//									fw.write(
//											liste_brasure_test.get(i).substring(
//													0,
//													liste_brasure_test.get(i)
//															.length() - 7));
//								} else if (presencePcbga) {
//									fw.write(
//											liste_brasure_test.get(i).substring(
//													0,
//													liste_brasure_test.get(i)
//															.length() - 8));
//								} else {
//									fw.write(
//											liste_brasure_test.get(i).substring(
//													0,
//													liste_brasure_test.get(i)
//															.length() - 6));
//								}
//
//								fw.write(";");
//
//								// T1, D1, T2, D2
//								for (int k = 0; k < 4; k++) {
//									fw.write(
//											String.valueOf(
//													liste_profil_test
//															.get(k + j)));
//									fw.write(";");
//								}
//
//								// N50% test
//								fw.write(String.valueOf(liste_N50_test.get(i)));
//								fw.write(";");
//
//								// facteur de forme test
//								fw.write(
//										String.valueOf(
//												liste_facteur_forme_test
//														.get(i)));
//								fw.write(";");
//
//								fw.write(String.valueOf(gamma_c1));
//								fw.write(";");
//
//								fw.write(String.valueOf(gamma_c2));
//								fw.write(";");
//
//								fw.write(String.valueOf(gamma_final));
//								fw.write(";");
//
//								fw.write(String.valueOf(section_critique));
//								fw.write(";");
//
//								if (presenceComposantABilles) {
//									fw.write(String.valueOf(""));
//									fw.write(";");
//									fw.write(String.valueOf(lnp1));
//									fw.write(";");
//									fw.write(String.valueOf(nombreX));
//									fw.write(";");
//								} else {
//									fw.write(String.valueOf(hauteurIntegree));
//									fw.write(";");
//									fw.write("");
//									fw.write(";");
//									fw.write("");
//									fw.write(";");
//								}
//
//								fw.write(
//										String.valueOf(gamma_section_critique));
//								fw.write(";");
//
//								fw.write(String.valueOf(Math.round(intN50)));
//								fw.write("\n");
//
//								/*
//								 * Concerne liste_profil_test dans ProfilDeVie,
//								 * les informations sont livrées par paquet de 4
//								 */
//								j += 4;
//
//							} catch (IOException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//								labelTraitementCampagne.setVisible(false);
//								return;
//							}
//
//						}
//						try {
//							fw.close();
//							labelTraitementCampagne.setVisible(false);
//							Alert alert = new Alert(AlertType.CONFIRMATION);
//							alert.setTitle("Statut Campagne");
//							alert.setContentText(
//									"Campagne réalisée et enregistrée avec succès ! ");
//							alert.showAndWait();
//						} catch (IOException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//							labelTraitementCampagne.setVisible(false);
//							Alert alert = new Alert(AlertType.ERROR);
//							alert.setTitle("Problème fichier");
//							alert.showAndWait();
//							return;
//						}
//
//					} else {
//						labelTraitementCampagne.setVisible(false);
//						Alert alert = new Alert(AlertType.ERROR);
//						alert.setTitle("Problème fichier");
//						alert.setContentText("Abandon de la Campagne");
//						alert.showAndWait();
//						return;
//					}
//				}
//			}
//		});

		setupCampagneTestsAction();
		
		// Pour desactiver la fonctionnalité campagne de test effacer "//" devant les 2 prochaines lignes
		//campagne_tests.setVisible(false);
		//campagne_tests.setDisable(true);
		
		calculer.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				ProfilDeVie pdv;
				try {
					if (t1.getText() != null && !t1.getText().isEmpty()
							&& t2.getText() != null && !t2.getText().isEmpty()
							&& d1.getText() != null && !d1.getText().isEmpty()
							&& d2.getText() != null
							&& !d2.getText().isEmpty()) {

						System.out.println("Debuging DV: " + d1.getText());
						System.out.println("Debuging DV: " + d1.getText());
						System.out.println("Debuging DV: " + d1.getText());
						System.out.println("Debuging DV: " + d1.getText());
						System.out.println("==============================");
						System.out.println("Debuging DV: " + Double.parseDouble(d1.getText()));
						System.out.println("Debuging DV: " + Double.parseDouble(d1.getText()));
						System.out.println("Debuging DV: " + Double.parseDouble(d1.getText()));
						System.out.println("Debuging DV: " + Double.parseDouble(d1.getText()));

						pdv = new ProfilDeVie(
								Double.parseDouble(t1.getText()),
								Double.parseDouble(t2.getText()),
								Double.parseDouble(d1.getText()),
								Double.parseDouble(d2.getText()),
								0,
								PCB,
								composant,
								brasure);
					} else {
						Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("Erreur Valeurs");
						alert.setContentText(
								"Les valeurs saisies ne sont pas valides");
						alert.showAndWait();
						return;
					}
				} catch (NumberFormatException e) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Erreur Valeurs");
					alert.setContentText(
							"Les valeurs saisies ne sont pas valides");
					alert.showAndWait();
					e.printStackTrace();
					return;
				}

				double res = pdv.getN50();
				System.out.println("[DureeDeVie] : " + res);
				intN50 = res;

				n50.setText(stringN50 + (int) res);

				if ((cb_type_composant.getValue().equals("BGA")
						|| cb_type_composant.getValue().equals("CSP")
						|| cb_type_composant.getValue().equals("WLP")
						|| cb_type_composant.getValue().equals("SBGA")
						|| cb_type_composant.getValue().equals("CBGA")
						|| cb_type_composant.getValue().equals("PCBGA"))
						&& matrice_creuse) {
					try {
						pdv.setX1t1(Double.valueOf(x1t1.getText()));
						pdv.setX1t2(Double.valueOf(x1t2.getText()));
						pdv.setX2t1(Double.valueOf(x2t1.getText()));
						pdv.setX2t2(Double.valueOf(x2t2.getText()));
						// tv_phases.setItems(FXCollections.observableArrayList(pdv));
					} catch (NumberFormatException nfe) {
						Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("Erreur chargement");
						alert.setContentText(
								"Entrer les valeurs pour la matrice creuse(x1t1...)");

						alert.showAndWait();
					}
				}
				// tv_phases.setItems(FXCollections.observableArrayList(pdv));

			}
		});

		group.selectedToggleProperty()
				.addListener(new ChangeListener<Toggle>() {

					@Override
					public void changed(
							ObservableValue<? extends Toggle> arg0,
							Toggle arg1,
							Toggle arg2) {

						if (arg2.equals(rb_creation_profil)) {

							clear_parametre_phase(11);
							nb_phase = Outils.generer_textfield("");
							gp_nb_phase = Outils.generer_textfield_avec_label(
									nb_phase,
									"Nombre de phases");
							gp_titre_parametres_profils.add(gp_nb_phase, 0, 4);
							gp_titre_parametres_profils
									.add(hb_calcul_sauvegarder, 1, 4);

							if (!cb_composant_brase.getSelectionModel()
									.getSelectedItem().isEmpty()) {
								valider.setVisible(true);

							}

							valider.setOnAction(
									new EventHandler<ActionEvent>() {

										@Override
										public void handle(ActionEvent arg0) {
											tv_phases.getItems().clear();

											int nb_phases = Integer.valueOf(
													nb_phase.getText());
											list_profils = FXCollections
													.observableArrayList();

											for (int i = 0; i < nb_phases; i++) {
												ProfilDeVie pdv = new ProfilDeVie(
														0,
														0,
														0,
														0,
														0,
														PCB,
														composant,
														brasure);
												if ((cb_type_composant
														.getValue()
														.equals("BGA")
														|| cb_type_composant
																.getValue()
																.equals("CSP")
														|| cb_type_composant
																.getValue()
																.equals("WLP")
														|| cb_type_composant
																.getValue()
																.equals("SBGA")
														|| cb_type_composant
																.getValue()
																.equals("CBGA")
														|| cb_type_composant
																.getValue()
																.equals(
																		"PCBGA"))
														&& matrice_creuse) {
													try {

														pdv.setX1t1(
																Double.valueOf(
																		x1t1.getText()));
														pdv.setX1t2(
																Double.valueOf(
																		x1t2.getText()));
														pdv.setX2t1(
																Double.valueOf(
																		x2t1.getText()));
														pdv.setX2t2(
																Double.valueOf(
																		x2t2.getText()));
													} catch (NumberFormatException nfe) {
														Alert alert = new Alert(
																AlertType.ERROR);
														alert.setTitle(
																"Erreur chargement");
														alert.setContentText(
																"Entrer les valeurs pour la matrice creuse(x1t1...)");

														alert.showAndWait();
													}
												} else {

												}

												pdv.setMatrice_creuse(
														matrice_creuse);
												list_profils.add(pdv);
											}
											tv_phases.getItems()
													.addAll(list_profils);
											sauvegarder.setVisible(true);

										}
									});

							sauvegarder.setOnAction(
									new EventHandler<ActionEvent>() {

										@Override
										public void handle(ActionEvent arg0) {
											String stringToSave = "";

											fileChooser = new FileChooser();
											fileChooser.getExtensionFilters()
													.add(
															new ExtensionFilter(
																	"Profil de vie (.profil)",
																	"*.profil"));
											fileChooser.setInitialDirectory(
													new File(
															Ressources.pathProfils));

											File file = fileChooser
													.showSaveDialog(
															getScene()
																	.getWindow());

											if (file != null) {

												for (ProfilDeVie pdv : list_profils) {
													stringToSave += pdv
															.toString() + "\n";
												}

												stringToSave = stringToSave
														.subSequence(
																0,
																stringToSave
																		.length()
																		- 1)
														.toString();

												Outils.sauvegarder_objet(
														stringToSave,
														file);
											}
										}
									});

						} else if (arg2.equals(rb_charger_profil)) {
							clear_parametre_phase(11);

							cb_profils.setItems(
									FXCollections.observableArrayList(
											Outils.get_list_filename_with_extension(
													Ressources.pathProfils)));

							cb_profils.valueProperty()
									.addListener(new ChangeListener<String>() {

										@Override
										public void changed(
												ObservableValue<
														? extends String> arg0,
												String arg1,
												String arg2) {
											try {

												if (arg2 != null) {
													pathProfil = Ressources.pathProfils
															+ arg2;
													chargerProfil(
															Ressources.pathProfils
																	+ arg2);

												}

											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									});

							gp_profils = Outils.generer_textfield_avec_label(
									cb_profils,
									"profils");
							gp_titre_parametres_profils.add(gp_profils, 0, 4);
							gp_titre_parametres_profils.add(rafraichirProfil, 1, 4);
						}

					}
				});

		titre_cycle_reference = new TitledPane();
		titre_cycle_reference.setMinWidth(520);
		titre_cycle_reference.setCollapsible(false);
		titre_cycle_reference.setPadding(new Insets(0, 0, 0, 20));
		titre_cycle_reference.setText("Paramètre de phase");
		titre_cycle_reference.setContent(gp_titre_parametres_profils);

		titre_cycle = new TitledPane();
		titre_cycle.setText("Profil de vie");
		titre_cycle.setPrefWidth(getWidth(1.30));
		titre_cycle.setPadding(new Insets(0, 0, 0, 20));
		titre_cycle.setContent(tv_phases);

	}

	private void chargerProfil(String path) throws Exception {
		FileReader fr = new FileReader(path);
		BufferedReader br = new BufferedReader(fr);

		String line;
		list_profils = FXCollections.observableArrayList();
		while ((line = br.readLine()) != null) {
			String[] strings = line.split(";");
			ProfilDeVie pdv = new ProfilDeVie(
					Double.valueOf(strings[0]),
					Double.valueOf(strings[2]),
					Double.valueOf(strings[1]),
					Double.valueOf(strings[3]),
					Double.valueOf(strings[4]),
					PCB,
					composant,
					brasure);
			list_profils.add(pdv);
		}
		tv_phases.setItems(list_profils);
		refreshDommageTotalEtNombreCycleEquivalent();
	}

	private void init_composant_existant() {

		cb_pcb_existants = new ComboBox<String>();
		cb_pcb_existants.setItems(
				FXCollections.observableArrayList(
						Outils.get_list_filename_with_extension(
								Ressources.pathPCB)));
		new AutoCompleteComboBoxListener<>(cb_pcb_existants);

		cb_categorie_composant = new ComboBox<String>(
				FXCollections.observableArrayList(
						"COMPOSANT A BILLES",
						"COMPOSANT LEADLESS",
						"COMPOSANT LEADFRAME"));

		cb_type_composant = new ComboBox<String>();
		cb_type_composant.setVisible(false);

		cb_composant_existant = new ComboBox<String>();
		cb_composant_existant.setVisible(false);

		cb_composant_brase = new ComboBox<String>();
		cb_composant_brase.setMaxWidth(200);
		new AutoCompleteComboBoxListener<>(cb_composant_brase);
		cb_composant_brase.setVisible(false);
		cb_composant_brase.setTooltip(tooltip);
		cb_composant_brase.setCellFactory(lv -> {

			return new ListCell<String>() {
				@Override
				protected void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					infoComposants = new HashMap<>();
					if (item != null) {

						setText(item);
						Tooltip tooltipComposantBrase = new Tooltip();
						tooltipComposantBrase.setFont(Font.font("", 15));
						BrasureComposant b;

						if (cb_categorie_composant.getValue()
								.equals("COMPOSANT A BILLES")) {
							b = new BrasureComposantBilles(
									Ressources.pathBrasures + item);
							infoComposants.put(item, b.infoComposant);

						} else if (cb_categorie_composant.getValue()
								.equals("COMPOSANT LEADFRAME")) {
							b = new BrasureComposantLeadframe(
									Ressources.pathBrasures + item);
							infoComposants.put(
									item,
									((BrasureComposantLeadframe) b).infoComposant);
						} else if (cb_categorie_composant.getValue()
								.equals("COMPOSANT LEADLESS")) {

							b = new BrasureComposantLeadless(
									Ressources.pathBrasures + item);
							infoComposants.put(
									item,
									((BrasureComposantLeadless) b)
											.getInfoComposant());
						}

						tooltipComposantBrase.setText(infoComposants.get(item));
						setTooltip(tooltipComposantBrase);
					} else {
						setText(null);
						setTooltip(null);
					}
				}
			};
		});

		cb_profils = new ComboBox<String>();
		cb_profils.setDisable(true);
		cb_profils.setMaxWidth(150);

		hb_cb_composant_brase_button_calculer.getChildren()
				.addAll(cb_composant_brase);

		cb_categorie_composant.valueProperty()
				.addListener(new ChangeListener<String>() {

					@Override
					public void changed(
							ObservableValue<? extends String> arg0,
							String arg1,
							String arg2) {
						clear_composants_existants(2);
						hb_resultats.getChildren().clear();

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
							hb_resultats.getChildren().clear();
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
							hb_resultats.getChildren().clear();

						} else if (arg2.contains("COMPOSANT LEADFRAME")) {

							cb_type_composant.setItems(
									FXCollections.observableArrayList("QFN"));
							gp_titre_composant_existant
									.add(new Label("Type de composant"), 0, 1);
							gp_titre_composant_existant
									.add(cb_type_composant, 1, 1);
							cb_type_composant.setVisible(true);
							hb_resultats.getChildren().clear();
						} else {
							clear_composants_existants(2);
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
						cb_composant_brase.getItems().clear();

						hb_resultats.getChildren().clear();
						if (arg2 != null) {
							if (arg2.equals("BGA") || arg2.equals("CSP")
									|| arg2.equals("WLP") || arg2.equals("SBGA")
									|| arg2.equals("CBGA")
									|| arg2.equals("PCBGA")) {
								if (arg2.equals("BGA")) {

									cb_composant_brase.getItems().addAll(
											Outils.get_list_filename_with_extension(
													Ressources.pathBrasures,
													".brbga"));

								} else if (arg2.equals("PCBGA")) {
									cb_composant_brase.getItems().addAll(
											Outils.get_list_filename_with_extension(
													Ressources.pathBrasures,
													".brpcbga"));

								} else if (arg2.equals("CSP")) {

									cb_composant_brase.getItems().addAll(
											Outils.get_list_filename_with_extension(
													Ressources.pathBrasures,
													".brcsp"));
								} else if (arg2.equals("WLP")) {

									cb_composant_brase.getItems().addAll(
											Outils.get_list_filename_with_extension(
													Ressources.pathBrasures,
													".brwlp"));
								} else if (arg2.equals("CBGA")) {

									cb_composant_brase.getItems().addAll(
											Outils.get_list_filename_with_extension(
													Ressources.pathBrasures,
													".brcbga"));
								} else if ((arg2.equals("SBGA"))) {

									cb_composant_brase.getItems().addAll(
											Outils.get_list_filename_with_extension(
													Ressources.pathBrasures,
													".brsbga"));
								} else {

								}

								gp_titre_composant_existant
										.add(new Label("Composant"), 0, 2);
								gp_titre_composant_existant
										.add(cb_composant_brase, 1, 2);
								cb_composant_brase.setVisible(true);

							} else if (arg2.contains("RESISTANCES")
									|| arg2.contains("LCCC")
									|| arg2.contains("CAPACITES")) {
								if (arg2.contains("RESISTANCES")) {

									cb_composant_brase.getItems().addAll(
											Outils.get_list_filename_with_extension(
													Ressources.pathBrasures,
													".brres"));
								} else if (arg2.contains("LCCC")) {
									cb_composant_brase.getItems().addAll(
											Outils.get_list_filename_with_extension(
													Ressources.pathBrasures,
													".brlcc"));
								} else if (arg2.contains("CAPACITES")) {
									cb_composant_brase.getItems().addAll(
											Outils.get_list_filename_with_extension(
													Ressources.pathBrasures,
													".brcap"));
								}

								gp_titre_composant_existant.add(
										new Label("Composants brasés"),
										0,
										2);
								gp_titre_composant_existant
										.add(cb_composant_brase, 1, 2);
								cb_composant_brase.setVisible(true);

							}

				else if (arg2.contains("QFN")) {
								cb_composant_brase.getItems().addAll(
										Outils.get_list_filename_with_extension(
												Ressources.pathBrasures,
												".brqfn"));
								gp_titre_composant_existant.add(
										new Label("Composants brasés"),
										0,
										2);
								gp_titre_composant_existant
										.add(cb_composant_brase, 1, 2);
								cb_composant_brase.setVisible(true);
							}
						}
					}
				});

		cb_composant_brase.valueProperty()
				.addListener(new ChangeListener<String>() {

					@Override
					public void changed(
							ObservableValue<? extends String> arg0,
							String arg1,
							String arg2) {

						if (arg2 != null) {
							if (cb_categorie_composant.getValue()
									.equals("COMPOSANT A BILLES")
									&& (arg2.contains(".brbga")
											|| arg2.contains(".brcsp")
											|| arg2.contains(".brwlp")
											|| arg2.contains(".brsbga")
											|| arg2.contains(".brcbga")
											|| arg2.contains(".brpcbga"))
									&& !cb_composant_brase.getValue()
											.isEmpty()) {

								if (!gp_titre_composant_existant.getChildren()
										.contains(cbx_matrice_creuse)
										|| !gp_titre_composant_existant
												.getChildren().contains(
														hb_x1t1_x1t2_x2t1_x2t2)) {
									gp_titre_composant_existant
											.add(cbx_matrice_creuse, 0, 3);
									gp_titre_composant_existant
											.add(hb_x1t1_x1t2_x2t1_x2t2, 1, 3);
								}

								brasure = new BrasureComposantBilles(
										Ressources.pathBrasures + arg2);

								OngletPcb onglet = new OngletPcb();
								onglet.init_pcb();
								if (cb_type_composant.getValue()
										.equals("BGA")) {

									composant = new ComposantBGA(
											onglet,
											Ressources.pathComposants
													+ ((BrasureComposantBilles) brasure)
															.getNom()
													+ ".bga");
								} else if (cb_type_composant.getValue()
										.equals("PCBGA")) {

									composant = new ComposantPCBGA(
											onglet,
											Ressources.pathComposants
													+ ((BrasureComposantBilles) brasure)
															.getNom()
													+ ".pcbga");
								} else if (cb_type_composant.getValue()
										.equals("CSP")) {

									composant = new ComposantCSP(
											onglet,
											Ressources.pathComposants
													+ ((BrasureComposantBilles) brasure)
															.getNom()
													+ ".csp");
								} else if (cb_type_composant.getValue()
										.equals("SBGA")) {

									composant = new ComposantSBGA(
											onglet,
											Ressources.pathComposants
													+ ((BrasureComposantBilles) brasure)
															.getNom()
													+ ".sbga");
								} else if (cb_type_composant.getValue()
										.equals("CBGA")) {
									composant = new ComposantCBGA();
									((ComposantCBGA) composant).charger_cbga(
											Ressources.pathComposants
													+ ((BrasureComposantBilles) brasure)
															.getNom()
													+ ".cbga");
								} else {
									composant = new ComposantWLP();
									((ComposantWLP) composant).charger_wlp(
											Ressources.pathComposants
													+ ((BrasureComposantBilles) brasure)
															.getNom()
													+ ".wlp");
								}
								if (rb_creation_profil.isSelected()) {
									valider.setVisible(true);
								} else {
									valider.setVisible(false);
								}
								calculer.setDisable(false);
								cb_profils.setDisable(false);
								//cbx_matrice_creuse.setVisible(true);

							} else if (cb_type_composant.getValue()
									.equals("RESISTANCES")
									&& !cb_composant_brase.getValue()
											.isEmpty()) {
								brasure = new BrasureResistance(
										Ressources.pathBrasures + arg2);
								((BrasureResistance) brasure).setCalculs();
								composant = new Composant_Resistance(
										((BrasureResistance) brasure)
												.getFormat());
								calculer.setDisable(false);
								if (rb_creation_profil.isSelected()) {
									valider.setVisible(true);
								} else {
									valider.setVisible(false);
								}
								cb_profils.setDisable(false);

							} else if (cb_type_composant.getValue()
									.equals("LCCC")
									&& !cb_composant_brase.getValue()
											.isEmpty()) {

								brasure = new BrasureLCC(
										Ressources.pathBrasures + arg2);
								((BrasureLCC) brasure).setCalculs();
								composant = new ComposantLCC(
										"",
										Ressources.pathComposants
												+ ((BrasureLCC) brasure)
														.getFormat()
												+ ".lcc");

								calculer.setDisable(false);
								if (rb_creation_profil.isSelected()) {
									valider.setVisible(true);
								} else {
									valider.setVisible(false);
								}
								cb_profils.setDisable(false);
							} else if (cb_type_composant.getValue()
									.equals("CAPACITES")
									&& !cb_composant_brase.getValue()
											.isEmpty()) {

								brasure = new BrasureCapacite(
										Ressources.pathBrasures + arg2);

								((BrasureCapacite) brasure).setCalculs();

								composant = new Composant_Capacite(
										((BrasureCapacite) brasure).getFormat(),
										((BrasureCapacite) brasure)
												.getMateriauCapacite(),
										((BrasureCapacite) brasure)
												.getValeurCapacite());

								calculer.setDisable(false);
								if (rb_creation_profil.isSelected()) {
									valider.setVisible(true);
								} else {
									valider.setVisible(false);
								}
								cb_profils.setDisable(false);
							} else if (cb_type_composant.getValue()
									.equals("QFN")
									&& !cb_composant_brase.getValue()
											.isEmpty()) {

								brasure = new BrasureQFN(
										Ressources.pathBrasures + arg2);
								((BrasureQFN) brasure).setCalculs();
								composant = new ComposantQFN(
										((BrasureQFN) brasure).getFormat());
								calculer.setDisable(false);
								if (rb_creation_profil.isSelected()) {
									valider.setVisible(true);
								} else {
									valider.setVisible(false);
								}
								cb_profils.setDisable(false);
							}

				else {
								calculer.setDisable(false);
								valider.setVisible(false);
								cb_profils.setDisable(false);
							}
							rb_charger_profil.setDisable(false);
							rb_creation_profil.setDisable(false);
						} else {
							rb_charger_profil.setDisable(true);
							rb_creation_profil.setDisable(true);
						}

					}
				});

		cb_pcb_existants.valueProperty()
				.addListener(new ChangeListener<String>() {

					@Override
					public void changed(
							ObservableValue<? extends String> arg0,
							String arg1,
							String arg2) {
						System.out.println("[DureeDeVie] Loaded Pcb: " + arg2);
						PCB = arg2;
					}
				});

		gp_titre_composant_existant = Outils.generer_gridPane();
		gp_titre_pcb_existant = Outils.generer_gridPane();

		vb_parametres_composants = new VBox(20);

		gp_titre_composant_existant.add(new Label("Catégorie"), 0, 0);
		gp_titre_composant_existant.add(cb_categorie_composant, 1, 0);

		gp_titre_pcb_existant.add(new Label("PCB"), 0, 0);
		gp_titre_pcb_existant.add(cb_pcb_existants, 1, 0);
		gp_titre_pcb_existant.add(rafraichirPcb, 2, 0);

		titre_composant_existant = new TitledPane();
		titre_composant_existant.setCollapsible(false);
		titre_composant_existant.setPadding(new Insets(0, 0, 0, 20));
		titre_composant_existant.setText("Composants brasés");
		titre_composant_existant.setContent(gp_titre_composant_existant);

		titre_pcb_existant = new TitledPane();
		titre_pcb_existant.setText("PCB existants");
		titre_pcb_existant.setPadding(new Insets(0, 0, 0, 20));
		titre_pcb_existant.setCollapsible(false);
		titre_pcb_existant.setContent(gp_titre_pcb_existant);

		vb_parametres_composants.getChildren()
				.addAll(titre_pcb_existant, titre_composant_existant);
		hb_parametres.getChildren()
				.addAll(vb_parametres_composants, titre_cycle_reference, image);
		HBox.setHgrow(vb_parametres_composants, Priority.NEVER);
		HBox.setHgrow(titre_cycle_reference, Priority.NEVER);
		HBox.setHgrow(image, Priority.NEVER);
		getChildren().add(hb_parametres);

	}
//	public void init_print_button() {
//		printButton = new Button("Print");
//		onglet_duree.getChildren().add(printButton);
//		printButton.setOnAction((ActionEvent event) -> {
//
//			// Création du job d'impression.
//			final PrinterJob printerJob = PrinterJob.createPrinterJob();
//			// Affichage de la boite de dialog de configation de l'impression.
//			if (printerJob.showPrintDialog(this.getScene().getWindow())) {
//
//				Printer printer = printerJob.getPrinter();
//				PageLayout pageLayout = printer.createPageLayout(Paper.A3, PageOrientation.PORTRAIT,
//						Printer.MarginType.HARDWARE_MINIMUM);
//
//				double width = this.getWidth();
//				double height = this.getHeight();
//
//				PrintResolution resolution = printerJob.getJobSettings().getPrintResolution();
//
//				width /= resolution.getFeedResolution();
//
//				height /= resolution.getCrossFeedResolution();
//
//				double scaleX = pageLayout.getPrintableWidth() / width / 700;
//				double scaleY = pageLayout.getPrintableHeight() / height / 600;
//
//				Scale scale = new Scale(scaleX, scaleY);
//
//				this.getTransforms().add(scale);
//
//				// Lancement de l'impression.
//				if (printerJob.printPage(pageLayout, this)) {
//					// Fin de l'impression.
//					printerJob.endJob();
//				}
//				this.getTransforms().remove(scale);
//			}
//
//		});
//
//	}

	public Duree choix_duree(
			OngletComposant composant,
			OngletBrasure brasure,
			OngletPcb circuit) {
		return new Duree();
	}

	public Duree get_duree() {
		return this.duree;
	}

	public void rafraichir_cb_pcb_existants() {
		this.cb_pcb_existants.getItems().clear();
		this.cb_pcb_existants.setItems(
				FXCollections.observableArrayList(
						Outils.get_list_filename_with_extension(
								Ressources.pathPCB)));
	}

}
