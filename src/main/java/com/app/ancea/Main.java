package com.app.ancea;

import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Main extends Application {

	private static final String pathLog = String.join(File.separator, Ressources.pathLogs, "logHookeXpert.log");

	private static final String APP_VERSION = "1.0.0";

	// classe principale dans laquelle sont créés les 4 onglets de calculs
	private static final Logger logger = Logger.getLogger(Main.class.getName());
	private Handler fichierLog;

	private OngletPcb fenetre_circuit;
	private OngletComposant fenetre_composant;
	private OngletBrasure fenetre_brasure;
	private OngletDuree fenetre_durees;
	private OngletCarte fenetre_carte;
	private TabPane tabPane;

	private BorderPane mainLayout; // Add this line

	// License management
	private LicenseManager licenseManager;
	private LicenseManager.LicenseStatus licenseStatus;

	@Override
	public void start(Stage primaryStage) throws Exception {

		// ============================================================
		// LICENSE SYSTEM COMPLETELY DISABLED - For fastest app launch
		// LicenseManager is NOT instantiated to avoid slow HardwareFingerprint
		// generation (PowerShell/WMIC commands for CPU ID, motherboard, etc.)
		// ============================================================
		licenseManager = null; // Not initialized to skip slow hardware fingerprinting
		// Set as LICENSED immediately - no validation
		licenseStatus = new LicenseManager.LicenseStatus(
				LicenseManager.LicenseType.LICENSED,
				365L, // Days remaining (avoids trial banners)
				"License system disabled for fast startup",
				null // No expiration date
		);
		logger.info("License system disabled - app opens immediately");
		// ============================================================
		// END LICENSE SECTION
		// ============================================================

		Ressources.ensureDirs();
		initLogger();

		fenetre_circuit = new OngletPcb();
		ScrollPane sp_circuit = new ScrollPane(fenetre_circuit);

		fenetre_composant = new OngletComposant();
		ScrollPane sp_composant = new ScrollPane(fenetre_composant);

		fenetre_brasure = new OngletBrasure(10);
		ScrollPane sp_brasure = new ScrollPane(fenetre_brasure);

		fenetre_durees = new OngletDuree(10);
		ScrollPane sp_duree_de_vie = new ScrollPane(fenetre_durees);

		fenetre_carte = new OngletCarte(10);
		ScrollPane sp_carte = new ScrollPane(fenetre_carte);

		Tab tabPcb = new Tab("PCB");
		tabPcb.setContent(sp_circuit);
		tabPcb.setClosable(false);

		Tab tabComposant = new Tab("Composant");
		tabComposant.setContent(sp_composant);
		tabComposant.setClosable(false);

		Tab tabBrasure = new Tab("Brasure");
		tabBrasure.setContent(sp_brasure);
		tabBrasure.setClosable(false);

		Tab tabDureeDeVie = new Tab("Durée de vie");
		tabDureeDeVie.setContent(sp_duree_de_vie);
		tabDureeDeVie.setClosable(false);

		Tab tabCarte = new Tab("Carte");
		tabCarte.setContent(sp_carte);
		tabCarte.setClosable(false);

		tabPane = new TabPane();

		tabPane.getTabs().addAll(tabPcb, tabComposant, tabBrasure,
				tabDureeDeVie, tabCarte);

		MenuBar menuBar = init_bar_menu(primaryStage);

		// BorderPane borderPane = new BorderPane();

		mainLayout = new BorderPane();
		mainLayout.setTop(menuBar);
		mainLayout.setCenter(tabPane);

		// ============================================================
		// 1. UPDATE THE STATUS BAR LOGIC (Inside start method)
		// ============================================================

		// Logic: Show banner ONLY if days <= 30 (regardless if it's a Key or Internal
		// Trial)
		if (licenseStatus.getDaysRemaining() <= 30) {
			Label trialBanner = new Label();

			// Customize text based on type
			if (licenseStatus.getType() == LicenseManager.LicenseType.TRIAL) {
				trialBanner.setText("VERSION D'ESSAI - " + licenseStatus.getDaysRemaining() + " jour(s) restant(s)");
			} else {
				// It is a license key, but short term (<= 30 days)
				trialBanner.setText(
						"LICENCE PROVISOIRE / ESSAI - Expire dans " + licenseStatus.getDaysRemaining() + " jours");
			}

			trialBanner.setStyle(
					"-fx-background-color: #cd6027; " +
							"-fx-text-fill: white; " +
							"-fx-font-weight: bold; " +
							"-fx-padding: 5 10 5 10; " +
							"-fx-font-size: 12px;");
			trialBanner.setMaxWidth(Double.MAX_VALUE);
			trialBanner.setAlignment(Pos.CENTER);
			mainLayout.setBottom(trialBanner);
		}
		// ELSE: If days > 30, we do NOTHING. The bottom of borderPane remains
		// null/empty.

		Screen screen = Screen.getPrimary();
		Rectangle2D bounds = screen.getVisualBounds();

		Group root = new Group();
		root.getChildren().add(mainLayout);

		Scene scene = new Scene(root, bounds.getWidth(), bounds.getHeight(),
				Color.rgb(0, 0, 0, 0));

		try {
			URL cssResource = getClass().getResource("styles/application.css");
			if (cssResource != null) {
				scene.getStylesheets().add(cssResource.toExternalForm());
				System.out.println("CSS loaded successfully: " + cssResource);
			} else {
				System.out.println("Warning: CSS file not found - continuing without stylesheet");
				// Optionally add a fallback CSS file or inline styles
			}
		} catch (Exception e) {
			System.err.println("Error loading CSS: " + e.getMessage());
			// Continue without CSS
		}

		mainLayout.prefWidthProperty().bind(scene.widthProperty());
		mainLayout.prefHeightProperty().bind(scene.heightProperty());

		fenetre_circuit.prefWidthProperty().bind(mainLayout.widthProperty());
		fenetre_composant.prefWidthProperty().bind(mainLayout.widthProperty());
		fenetre_durees.prefWidthProperty().bind(mainLayout.widthProperty());
		fenetre_carte.prefWidthProperty().bind(mainLayout.widthProperty());

		// Update title based on license status
		String titleSuffix = switch (licenseStatus.getType()) {
			case TRIAL -> " - ESSAI (" + licenseStatus.getDaysRemaining() + " jours)";
			case LICENSED -> " - Licence activée";
			default -> "";
		};
		primaryStage.setTitle("AnCEA" + titleSuffix);

		// primaryStage.setTitle("HookeXpert");
		// System.out.println("HookeXpert: " + Ressources.LOGO_SAFRAN);

		// Safe image loading
		try {
			Image logoImage = loadImageSafely(Ressources.LOGO_SAFRAN);
			if (logoImage != null) {
				primaryStage.getIcons().add(logoImage);
			} else {
				System.err.println("Warning: Application logo not found - continuing without icon");
			}
		} catch (Exception e) {
			System.err.println("Error loading application icon: " + e.getMessage());
			// Continue without icon
		}
		primaryStage.setMaximized(true);
		// primaryStage.setX(bounds.getMinX());
		// primaryStage.setY(bounds.getMinY());
		// primaryStage.setWidth(bounds.getWidth());
		// primaryStage.setHeight(bounds.getHeight());
		primaryStage.setScene(scene);

		primaryStage.show();
	}

	// Safe image loading method (unchanged)
	private Image loadImageSafely(String imagePath) {
		try {
			// First try to load as a resource from classpath
			URL resourceUrl = getClass().getResource(imagePath);
			if (resourceUrl != null) {
				System.out.println("Successfully loaded image: " + resourceUrl);
				return new Image(resourceUrl.toExternalForm());
			}

			// Try loading as stream
			InputStream stream = getClass().getResourceAsStream(imagePath);
			if (stream != null) {
				return new Image(stream);
			}

			// Try loading from local file system as fallback
			File imageFile = new File("src/main/resources" + imagePath);
			if (imageFile.exists()) {
				return new Image(imageFile.toURI().toString());
			}

			System.err.println("Image not found: " + imagePath);
			return null;
		} catch (Exception e) {
			System.err.println("Error loading image: " + imagePath + " - " + e.getMessage());
			return null;
		}
	}

	private void initLogger() {
		try {
			fichierLog = new FileHandler(pathLog);
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
		fichierLog.setFormatter(new SimpleFormatter());
		fichierLog.setLevel(Level.OFF);
		logger.setLevel(Level.OFF);
		logger.addHandler(fichierLog);

	}

	private void updateAdminMode(Stage primaryStage, boolean isAdmin) {
		logger.info("Updating admin mode: " + isAdmin);

		// Update all components that have admin-specific features
		if (fenetre_brasure != null) {
			fenetre_brasure.setAdminMode(isAdmin);
		}
		if (fenetre_durees != null) {
			fenetre_durees.setAdminMode(isAdmin);
		}
		// Add other components here as needed (fenetre_carte, etc.)
		if (fenetre_carte != null) {
			// If OngletCarte has admin features, uncomment:
			// fenetre_carte.setAdminMode(isAdmin);
		}

		// Update the window title to show admin status
		if (primaryStage != null) {
			String baseTitle = "AnCEA";
			if (licenseStatus != null && licenseStatus.getType() == LicenseManager.LicenseType.LICENSED) {
				baseTitle += " - Licence activée";
			}
			if (isAdmin) {
				baseTitle += " [ADMIN]";
			}
			primaryStage.setTitle(baseTitle);
		}

		// FIXED: Access the field directly. No casting required.
		// if (mainLayout != null && licenseStatus != null) {
		// if (licenseStatus.getType() == LicenseManager.LicenseType.LICENSED &&
		// licenseStatus.getDaysRemaining() > 30) {
		//
		// mainLayout.setBottom(null); // Safely remove trial banner
		// }
		// }

		// CORRECTED: Use the mainLayout field directly.
		// This replaces the old code that was trying to cast
		// tabPane.getScene().getRoot()
		if (mainLayout != null && licenseStatus != null) {
			if (licenseStatus.getType() == LicenseManager.LicenseType.LICENSED &&
					licenseStatus.getDaysRemaining() > 30) {

				mainLayout.setBottom(null); // Safely remove trial banner from the bottom
			}
		}

		// // Remove trial banner if present
		// if (tabPane != null && tabPane.getScene() != null) {
		// BorderPane root = (BorderPane) tabPane.getScene().getRoot();
		// if (root != null && licenseStatus != null &&
		// licenseStatus.getType() == LicenseManager.LicenseType.LICENSED &&
		// licenseStatus.getDaysRemaining() > 30) {
		// root.setBottom(null); // Remove trial banner
		// }
		// }

		// NO POPUP - The UI updates silently
		// The user sees [ADMIN] in title and admin buttons appear immediately
	}

	public static void main(String... args) {
		Application.launch(args);
	}

	public MenuBar init_bar_menu(Stage s) {
		MenuBar barMenu = new MenuBar();

		Menu menuFichier = new Menu("Fichier");
		Menu menuPCB = new Menu("PCB");
		Menu menuHelp = new Menu("Aide");

		MenuItem ItemQuitter = new MenuItem("Exit");
		MenuItem ItemImprimer = new MenuItem("Imprimer");

		MenuItem ItemChargementPCB = new MenuItem("Chargement PCB");
		MenuItem ItemSauvegardePCB = new MenuItem("Sauvegarde PCB");

		Menu menuExport = new Menu("Exporter vers Excel");
		MenuItem menuItemExportResultatPCB = new MenuItem("Resultat PCB ");
		menuItemExportResultatPCB.setOnAction(event -> {
			fenetre_circuit.exportToCsv();
		});

		MenuItem menuItemExportResultatProfilDeVie = new MenuItem(
				"Resultat profil de vie");
		menuItemExportResultatProfilDeVie.setOnAction(event -> {
			fenetre_durees.exportToCsv();
		});

		MenuItem menuItemExportResultatComposant = new MenuItem(
				"Resultat composant");
		menuItemExportResultatComposant.setOnAction(event -> {
			fenetre_composant.exportToCsv();
		});

		MenuItem menuItemExportResultatCarte = new MenuItem("Resultat carte");
		menuItemExportResultatCarte.setOnAction(event -> {
			fenetre_carte.exportToCsv();
		});

		menuExport.getItems().addAll(menuItemExportResultatPCB,
				menuItemExportResultatProfilDeVie,
				menuItemExportResultatComposant, menuItemExportResultatCarte);

		ItemQuitter.setAccelerator(
				new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN));
		ItemImprimer.setAccelerator(
				new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN));

		ItemImprimer.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				Printer printer = Printer.getDefaultPrinter();
				PageLayout pageLayout = printer.createPageLayout(
						Paper.NA_LETTER, PageOrientation.PORTRAIT,
						Printer.MarginType.DEFAULT);
				double scaleX = pageLayout.getPrintableWidth() / s.getWidth();
				double scaleY = pageLayout.getPrintableHeight() / s.getWidth();
				tabPane.getTransforms().add(new Scale(scaleX, scaleY));

				PrinterJob job = PrinterJob.createPrinterJob();
				if (job != null) {
					boolean success = job.printPage(tabPane.getSelectionModel()
							.getSelectedItem().getContent());
					if (success) {
						job.endJob();
					}
				}
			}
		});
		ItemQuitter.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				s.close();
			}
		});

		ItemChargementPCB.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				try {
					fenetre_circuit.vb_pcb.getChildren().clear();
					fenetre_circuit.liste_couches_pcb = fenetre_circuit.pcb
							.get_liste_couches_pcb();
					fenetre_circuit.nombre_vialaser.setText(String
							.valueOf(fenetre_circuit.pcb.nombre_vialaser));
					fenetre_circuit.nombre_couche.setText(
							String.valueOf(fenetre_circuit.pcb.nombre_couche));
					fenetre_circuit.vb_pcb.getChildren()
							.addAll(fenetre_circuit.pcb.al);
					fenetre_circuit.button_calculer.setVisible(true);
				} catch (NullPointerException npe) {

					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Erreur Chargement PCB");
					alert.setHeaderText("Probleme de chargement");
					alert.setContentText(
							"Vous devez d'abord générer un circuit");
					alert.showAndWait();
				}

			}
		});
		ItemSauvegardePCB.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {

				FileChooser fileChooser = new FileChooser();

				// Set extension filter
				FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
						"PCB (*.pcb)", "*.pcb");
				fileChooser.getExtensionFilters().add(extFilter);

				// Show save file dialog
				File file = fileChooser.showSaveDialog(new Stage());

				if (file != null) {

					try {

						PrintWriter pw = new PrintWriter(
								new BufferedWriter(new FileWriter(file)));
						pw.println(fenetre_circuit.get_Resine().toString());
						for (int i = 0; i < fenetre_circuit.pcb.al
								.size(); i++) {

							pw.println(
									(fenetre_circuit.pcb.al.get(i).toString()));
						}

						pw.close();

					} catch (IOException ex) {
						Logger.getLogger(OngletPcb.class.getName())
								.log(Level.SEVERE, null, ex);
					}
				}
				// Actualisation des différentes ComboBox
				fenetre_circuit.rafraichir_cb_liste_pcb();
				fenetre_durees.rafraichir_cb_pcb_existants();
				fenetre_carte.rafraichir_cbPcb();
			}
		});

		// --- NEW CODE START ---

		// Create the "Enter License" menu item
		MenuItem itemEnterLicense = new MenuItem("Entrer une licence");
		// itemEnterLicense.setOnAction(event -> {
		// // Create a new dialog instance or reuse logic
		// LicenseDialog dialog = new LicenseDialog(licenseManager);
		// dialog.showActivationDialog(s);
		//
		// // Optional: Refresh license status after dialog closes
		// licenseStatus = licenseManager.checkLicense();
		// if (licenseStatus.getType() == LicenseManager.LicenseType.LICENSED) {
		// s.setTitle("HookeXpert - Licence activée");
		// // Remove trial banner if it exists (requires logic to find the label in
		// borderPane)
		// }
		// });

		// License system is disabled - show info message instead
		itemEnterLicense.setOnAction(event -> {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Système de licence");
			alert.setHeaderText("Licence désactivée");
			alert.setContentText("Le système de licence est désactivé pour un démarrage rapide de l'application.");
			alert.showAndWait();
		});

		MenuItem itemAbout = new MenuItem("À propos");
		itemAbout.setOnAction(event -> showAboutPopup(s));

		// Add the new item to the Help menu
		menuHelp.getItems().addAll(itemEnterLicense, itemAbout);

		// --- NEW CODE END ---

		menuFichier.getItems().addAll(menuExport, ItemImprimer, ItemQuitter);
		menuPCB.getItems().addAll(ItemChargementPCB, ItemSauvegardePCB);

		barMenu.getMenus().addAll(menuFichier, menuPCB, menuHelp);

		return barMenu;

	}

	/**
	 * Show the about popup window.
	 */
	// private void showAboutPopup(Stage owner) {
	// Stage popupStage = new Stage();
	// popupStage.initModality(Modality.WINDOW_MODAL);
	// popupStage.initOwner(owner);
	// popupStage.setTitle("À propos de HookeXpert");
	// popupStage.setResizable(false);
	//
	// VBox content = new VBox(15);
	// content.setAlignment(Pos.CENTER);
	// content.setPrefSize(400, 400); // Slightly larger to accommodate license info
	//
	// // Load logo
	// Image logoImage = loadImageSafely(Ressources.LOGO_HE_FULL);
	// if (logoImage != null) {
	// ImageView logoView = new ImageView(logoImage);
	// logoView.setFitWidth(200);
	// logoView.setPreserveRatio(true);
	// logoView.setSmooth(true);
	// content.getChildren().add(logoView);
	// }
	//
	// // App version
	// Text versionText = new Text("Version: " + APP_VERSION);
	// versionText.setTextAlignment(TextAlignment.CENTER);
	// versionText.setStyle("-fx-font-weight: bold;");
	// content.getChildren().add(versionText);
	//
	// // License status
	// String licenseText = "";
	// switch (licenseStatus.getType()) {
	// case LICENSED:
	// licenseText = "✓ Version sous licence";
	// break;
	// case TRIAL:
	// licenseText = "⏰ Essai: " + licenseStatus.getDaysRemaining() + " jour(s)
	// restant(s)";
	// break;
	// case EXPIRED:
	// licenseText = "⚠️ Licence requise";
	// break;
	// }
	// Text licenseStatusText = new Text(licenseText);
	// licenseStatusText.setTextAlignment(TextAlignment.CENTER);
	// licenseStatusText.setStyle("-fx-font-size: 11px;");
	// content.getChildren().add(licenseStatusText);
	//
	// // Copyright
	// Text copyrightText = new Text("© 2025 HookeXpert. Tous droits
	// réservés.\nDéveloppé par Hooke-Electronics.");
	// copyrightText.setTextAlignment(TextAlignment.CENTER);
	// copyrightText.setWrappingWidth(280);
	// content.getChildren().add(copyrightText);
	//
	// // OK button
	// Button okButton = new Button("OK");
	// okButton.setOnAction(e -> popupStage.close());
	// okButton.setDefaultButton(true);
	// content.getChildren().add(okButton);
	//
	// Scene popupScene = new Scene(content);
	// URL aboutCss = getClass().getResource("styles/about.css");
	// if (aboutCss != null) {
	// popupScene.getStylesheets().add(aboutCss.toExternalForm());
	// logger.fine("Loaded about dialog CSS.");
	// }
	// popupStage.setScene(popupScene);
	// popupStage.showAndWait();
	// }

	// ============================================================
	// 2. UPDATE THE ABOUT POPUP (Replace showAboutPopup method)
	// ============================================================

	private void showAboutPopup(Stage owner) {
		Stage popupStage = new Stage();
		popupStage.initModality(Modality.WINDOW_MODAL);
		popupStage.initOwner(owner);
		popupStage.setTitle("À propos de AnCEA");
		popupStage.setResizable(false);

		VBox content = new VBox(15);
		content.setAlignment(Pos.CENTER);
		content.setPadding(new javafx.geometry.Insets(20)); // Add padding
		content.setPrefSize(400, 450);

		// Load logo
		Image logoImage = loadImageSafely(Ressources.LOGO_HE_FULL);
		if (logoImage != null) {
			ImageView logoView = new ImageView(logoImage);
			logoView.setFitWidth(200);
			logoView.setPreserveRatio(true);
			logoView.setSmooth(true);
			content.getChildren().add(logoView);
		}

		// App version
		Text versionText = new Text("Version: " + APP_VERSION);
		versionText.setTextAlignment(TextAlignment.CENTER);
		versionText.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
		content.getChildren().add(versionText);

		// ================= LICENSE INFORMATION LOGIC =================
		VBox licenseBox = new VBox(5);
		licenseBox.setAlignment(Pos.CENTER);
		licenseBox.setStyle(
				"-fx-background-color: #f8f9fa; -fx-padding: 10; -fx-background-radius: 5; -fx-border-color: #dee2e6; -fx-border-radius: 5;");

		Text typeText = new Text();
		Text expirationText = new Text();
		Text daysText = new Text();

		long days = licenseStatus.getDaysRemaining();
		LocalDate expDate = licenseStatus.getExpirationDate();

		// Determine Title based on your rule: <= 30 is Trial, > 30 is Full
		if (days <= 30) {
			typeText.setText("VERSION D'ESSAI");
			typeText.setFill(Color.ORANGE);
		} else {
			typeText.setText("✓ LICENCE COMPLÈTE");
			typeText.setFill(Color.GREEN);
		}
		typeText.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");

		// Expiration Date
		if (expDate != null) {
			expirationText.setText("Expire le : " + expDate.toString());
		} else {
			expirationText.setText("Mode : Démo interne");
		}

		// Days Remaining
		if (licenseStatus.getType() == LicenseManager.LicenseType.EXPIRED) {
			daysText.setText("Statut : Expirée");
			daysText.setFill(Color.RED);
		} else {
			daysText.setText("Temps restant : " + days + " Jours");
		}

		licenseBox.getChildren().addAll(typeText, new Separator(), expirationText, daysText);
		content.getChildren().add(licenseBox);
		// =============================================================

		// Copyright
		Text copyrightText = new Text("© 2025 AnCEA. Tous droits réservés.\nDéveloppé par Hooke-Electronics.");
		copyrightText.setTextAlignment(TextAlignment.CENTER);
		copyrightText.setStyle("-fx-fill: #6c757d; -fx-font-size: 11px;");
		content.getChildren().add(copyrightText);

		// OK button
		Button okButton = new Button("OK");
		okButton.setOnAction(e -> popupStage.close());
		okButton.setDefaultButton(true);
		okButton.setPrefWidth(100);
		content.getChildren().add(okButton);

		Scene popupScene = new Scene(content);
		// (Optional CSS loading here)
		popupStage.setScene(popupScene);
		popupStage.showAndWait();
	}
}