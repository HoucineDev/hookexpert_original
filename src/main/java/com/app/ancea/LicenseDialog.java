package com.app.ancea;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.InputStream;
import java.net.URL;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * License dialog for HookeXpert.
 *
 * FIXED VERSION:
 * - No popup after activation
 * - Callback triggers BEFORE dialog closes
 * - UI updates immediately without restart
 */
public class LicenseDialog {

    private static final Logger logger = Logger.getLogger(LicenseDialog.class.getName());

    // Developer contact info
    private static final String DEVELOPER_EMAIL = "houcine.latif@outlook.com";
    private static final String DEVELOPER_PHONE = "+33 6 67 19 81 74";
    private static final String DEVELOPER_NAME = "Houcine Latif";

    private final LicenseManager licenseManager;
    private boolean licenseActivated = false;

    // Callback to notify when license is activated
    private Consumer<LicenseActivationResult> onLicenseActivated;

    public LicenseDialog(LicenseManager licenseManager) {
        this.licenseManager = licenseManager;
    }

    /**
     * Set callback to be notified when license is successfully activated.
     */
    public void setOnLicenseActivated(Consumer<LicenseActivationResult> callback) {
        this.onLicenseActivated = callback;
    }

    public boolean isLicenseActivated() {
        return licenseActivated;
    }

    /**
     * Show the trial expiration dialog (when trial is expired).
     */
    public boolean showExpiredDialog(Stage owner) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        if (owner != null) {
            dialog.initOwner(owner);
        }
        dialog.setTitle("Licence requise - HookeXpert");
        dialog.setResizable(false);

        VBox root = new VBox(15);
        root.setPadding(new Insets(25));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f5f5f5;");

        // Title
        Label titleLabel = new Label("Période d'essai expirée");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.web("#c0392b"));

        Label messageLabel = new Label(
                "Votre période d'essai est terminée.\n" +
                        "Pour continuer à utiliser HookeXpert, veuillez contacter\n" +
                        "le développeur avec votre ID Machine pour obtenir une licence."
        );
        messageLabel.setTextAlignment(TextAlignment.CENTER);
        messageLabel.setWrapText(true);
        messageLabel.setStyle("-fx-font-size: 13px;");

        // Machine ID section
        VBox machineIdBox = createMachineIdSection();

        // License entry section
        Label statusLabel = new Label("");
        statusLabel.setStyle("-fx-font-size: 11px;");
        VBox licenseBox = createLicenseEntrySection(dialog, statusLabel, true);

        // Contact info
        VBox contactBox = createContactSection();

        // Exit button
        Button exitButton = new Button("Quitter l'application");
        exitButton.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white;");
        exitButton.setOnAction(e -> {
            licenseActivated = false;
            dialog.close();
        });

        root.getChildren().addAll(
                titleLabel,
                messageLabel,
                new Separator(),
                machineIdBox,
                new Separator(),
                licenseBox,
                new Separator(),
                contactBox,
                exitButton
        );

        Scene scene = new Scene(root, 450, 580);
        dialog.setScene(scene);

        Image windowIcon = loadImageSafely(Ressources.LOGO_SAFRAN);
        if (windowIcon != null) {
            dialog.getIcons().add(windowIcon);
        }

        dialog.showAndWait();
        return licenseActivated;
    }

    /**
     * Show the manual license activation dialog (from menu).
     * NO POPUP - Just closes after successful activation.
     */
    public void showActivationDialog(Stage owner) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        if (owner != null) {
            dialog.initOwner(owner);
        }
        dialog.setTitle("Activation de la licence - HookeXpert");
        dialog.setResizable(false);

        VBox root = new VBox(15);
        root.setPadding(new Insets(25));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f5f5f5;");

        // Title
        Label titleLabel = new Label("Gestion de Licence");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        titleLabel.setTextFill(Color.web("#2c3e50"));

        Label messageLabel = new Label(
                "Pour activer la version complète, veuillez envoyer votre ID Machine\n" +
                        "au développeur. Une fois la clé reçue, collez-la ci-dessous."
        );
        messageLabel.setTextAlignment(TextAlignment.CENTER);
        messageLabel.setWrapText(true);
        messageLabel.setStyle("-fx-font-size: 13px;");

        // Machine ID section
        VBox machineIdBox = createMachineIdSection();

        // License entry section
        Label statusLabel = new Label("");
        statusLabel.setStyle("-fx-font-size: 11px;");
        VBox licenseBox = createLicenseEntrySection(dialog, statusLabel, false);

        // Contact info (compact)
        Label contactInfo = new Label(
                "Support: " + DEVELOPER_EMAIL + " | " + DEVELOPER_PHONE
        );
        contactInfo.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 11px;");

        Button closeButton = new Button("Fermer");
        closeButton.setOnAction(e -> dialog.close());

        root.getChildren().addAll(
                titleLabel,
                messageLabel,
                new Separator(),
                machineIdBox,
                new Separator(),
                licenseBox,
                new Separator(),
                contactInfo,
                closeButton
        );

        Scene scene = new Scene(root, 450, 520);
        dialog.setScene(scene);

        Image windowIcon = loadImageSafely(Ressources.LOGO_SAFRAN);
        if (windowIcon != null) {
            dialog.getIcons().add(windowIcon);
        }

        dialog.showAndWait();
    }

    /**
     * Creates the machine ID display section.
     */
    private VBox createMachineIdSection() {
        Label machineIdTitle = new Label("Votre ID Machine:");
        machineIdTitle.setFont(Font.font("System", FontWeight.BOLD, 12));

        TextField machineIdField = new TextField(licenseManager.getMachineId());
        machineIdField.setEditable(false);
        machineIdField.setStyle("-fx-font-family: monospace; -fx-font-size: 12px; -fx-background-color: #fff;");
        machineIdField.setPrefWidth(350);

        Button copyButton = new Button("📋 Copier l'ID Machine");
        copyButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand;");
        copyButton.setOnAction(e -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(licenseManager.getMachineId());
            clipboard.setContent(content);
            copyButton.setText("✓ Copié!");
            copyButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");

            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    Platform.runLater(() -> {
                        copyButton.setText("📋 Copier l'ID Machine");
                        copyButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand;");
                    });
                } catch (InterruptedException ignored) {}
            }).start();
        });

        VBox box = new VBox(8, machineIdTitle, machineIdField, copyButton);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    /**
     * Creates the license entry section.
     *
     * @param dialog The dialog stage
     * @param statusLabel Label to show status messages
     * @param isExpiredDialog If true, this is the expired dialog (different styling)
     */
    private VBox createLicenseEntrySection(Stage dialog, Label statusLabel, boolean isExpiredDialog) {
        Label licenseTitle = new Label(isExpiredDialog ?
                "Entrez votre clé de licence:" :
                "Collez votre clé de licence reçue:");
        licenseTitle.setFont(Font.font("System", FontWeight.BOLD, 12));

        TextField licenseField = new TextField();
        licenseField.setPromptText(isExpiredDialog ?
                "Collez votre clé de licence ici" :
                "Clé de licence...");
        licenseField.setStyle("-fx-font-family: monospace; -fx-font-size: 12px;");
        licenseField.setPrefWidth(350);

        Button activateButton = new Button(isExpiredDialog ?
                "🔓 Activer la licence" :
                "Valider la licence");
        activateButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");

        activateButton.setOnAction(e -> {
            String key = licenseField.getText().trim();
            if (key.isEmpty()) {
                statusLabel.setText("⚠️ Le champ est vide");
                statusLabel.setTextFill(Color.web("#e67e22"));
                return;
            }

            if (licenseManager.activateLicense(key)) {
                boolean isAdmin = licenseManager.isAdmin();
                licenseActivated = true;

                // Update status label with success message
                String successMsg = "✓ Licence activée avec succès!";
                if (isAdmin) {
                    successMsg += " (Mode Admin)";
                }
                statusLabel.setText(successMsg);
                statusLabel.setTextFill(Color.web("#27ae60"));

                // TRIGGER CALLBACK FIRST (before closing dialog)
                // This updates the UI immediately
                if (onLicenseActivated != null) {
                    onLicenseActivated.accept(new LicenseActivationResult(true, isAdmin));
                }

                // NO POPUP - Just close after a brief delay so user sees success message
                new Thread(() -> {
                    try {
                        Thread.sleep(800); // Brief pause to see success message
                        Platform.runLater(dialog::close);
                    } catch (InterruptedException ignored) {}
                }).start();

            } else {
                statusLabel.setText("✗ Clé invalide pour cet ID Machine");
                statusLabel.setTextFill(Color.web("#c0392b"));
            }
        });

        VBox box = new VBox(8, licenseTitle, licenseField, activateButton, statusLabel);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    /**
     * Creates the contact information section.
     */
    private VBox createContactSection() {
        Label contactTitle = new Label("Contactez le développeur:");
        contactTitle.setFont(Font.font("System", FontWeight.BOLD, 12));

        Label contactInfo = new Label(
                "📧 " + DEVELOPER_EMAIL + "\n" +
                        "📱 " + DEVELOPER_PHONE + "\n" +
                        "🏢 " + DEVELOPER_NAME
        );
        contactInfo.setStyle("-fx-text-fill: #555; -fx-font-size: 12px;");
        contactInfo.setTextAlignment(TextAlignment.CENTER);

        VBox box = new VBox(8, contactTitle, contactInfo);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    /**
     * Show trial warning dialog.
     */
    public static void showTrialWarning(Stage owner, long daysRemaining) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.initOwner(owner);
        alert.setTitle("Période d'essai");
        alert.setHeaderText("Attention: Essai bientôt terminé");
        alert.setContentText(
                "Il vous reste " + daysRemaining + " jour(s) d'essai.\n\n" +
                        "Contactez le développeur pour obtenir une licence complète."
        );
        alert.showAndWait();
    }

    private static Image loadImageSafely(String imagePath) {
        try {
            if (imagePath == null || imagePath.isEmpty()) {
                return null;
            }

            URL resourceUrl = LicenseDialog.class.getResource(imagePath);
            if (resourceUrl != null) {
                return new Image(resourceUrl.toExternalForm());
            }

            InputStream stream = LicenseDialog.class.getResourceAsStream(imagePath);
            if (stream != null) {
                return new Image(stream);
            }

            java.io.File imageFile = new java.io.File("src/main/resources" + imagePath);
            if (imageFile.exists()) {
                return new Image(imageFile.toURI().toString());
            }

            return null;
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error loading image: " + imagePath, e);
            return null;
        }
    }

    // ==================== RESULT CLASS ====================

    public static class LicenseActivationResult {
        private final boolean success;
        private final boolean isAdmin;

        public LicenseActivationResult(boolean success, boolean isAdmin) {
            this.success = success;
            this.isAdmin = isAdmin;
        }

        public boolean isSuccess() { return success; }
        public boolean isAdmin() { return isAdmin; }
    }
}