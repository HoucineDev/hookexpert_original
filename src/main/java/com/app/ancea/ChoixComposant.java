package com.app.ancea;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;


import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.cell.CheckBoxTableCell;

import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

public class ChoixComposant extends BorderPane {

	private ObservableList<String> listeComposant;
	
	private ObservableList<String> listComposantCochesDansListView;
	private ListView<String> listComposantsCoches;
	
	private GridPane gp_options, gpTitreComposantExistant, gpTitrePcbExistant;
	private TextField search_bar;

	private TableView<String> tvComposant;
	private TableColumn<String, Boolean> colonneAjoutComposant;
	private TableColumn<String,String> colonneNom, colonneDetails;
	
	private HBox hbResultats, hbParametres;
	private VBox vbParametresComposants;
	private TitledPane titreComposantsExistants;
	
	private HashMap<String, Integer> composants;
	private OngletCarte onglet;
	
	private ComboBox<String> cbCategorieComposant, cbTypeComposant, cbProfils;
	
	private Button buttonValider, buttonAnnuler;

	public ChoixComposant(HashMap<String, Integer> composantSurCarte, OngletCarte onglet) {
		Stage stage = new Stage();
		stage.setTitle("Choix composant");
		stage.setScene(new Scene(this, 800, 500));
		stage.initModality(Modality.WINDOW_MODAL);

		composants = composantSurCarte;
		this.onglet = onglet;
		
		initComposantExistant();
		initInterface();

		stage.showAndWait();
	}
	
	/**
	 * Initialisation de l'IHM
	 */
	private void initInterface() {
		
		HBox hbButtonValiderAnnuler = new HBox(20);
		
		chargerListComposantListView();
		
		buttonValider = new Button("VALIDER");
		buttonValider.setOnAction(event -> {
			
			Stage stage = (Stage) getScene().getWindow();
			stage.close();
			
		});
		buttonAnnuler = new Button("Annuler");
		buttonAnnuler.setOnAction(event -> {
			Stage stage = (Stage) getScene().getWindow();
			stage.close();
		});
		
		hbButtonValiderAnnuler.getChildren().addAll(buttonValider, buttonAnnuler);
		BorderPane.setMargin(hbButtonValiderAnnuler, new Insets(10,10,10,10));
		BorderPane.setAlignment(hbButtonValiderAnnuler, Pos.BOTTOM_CENTER);
		
		gp_options = new GridPane();

		TitledPane titre_choix_type_composant = new TitledPane();
		titre_choix_type_composant.setText("Filtre composant");
		titre_choix_type_composant.setContent(gp_options);
		titre_choix_type_composant.setCollapsible(false);

		this.setTop(titreComposantsExistants);
		this.setCenter(tvComposant);
		this.setRight(listComposantsCoches);
		this.setBottom(hbButtonValiderAnnuler);
		
	}
	
	private void chargerListComposantListView() {
		listComposantCochesDansListView = FXCollections.observableArrayList();
		for(String nomComposant: composants.keySet()) {
			listComposantCochesDansListView.add(nomComposant);
		}
		listComposantsCoches = new ListView<String>(listComposantCochesDansListView);
	}

	/**
	 * Initialisation de la recherche de composants
	 * 
	 */
	private void initComposantExistant() {
		
		hbParametres = new HBox();
		hbResultats = new HBox();
		
		search_bar = new TextField();

		cbCategorieComposant = new ComboBox<String>(
				FXCollections.observableArrayList("COMPOSANT A BILLES", "COMPOSANT LEADLESS", "COMPOSANT LEADFRAME"));

		cbTypeComposant = new ComboBox<String>();
		cbTypeComposant.setVisible(false);
		
		cbProfils = new ComboBox<String>();
		cbProfils.setDisable(true);
		cbProfils.setMaxWidth(150);
		listeComposant = FXCollections.observableArrayList(Outils.get_list_filename_with_extension(Ressources.pathBrasures));
		
		createTableview();

		cbCategorieComposant.valueProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				clear_composants_existants(2);
				hbResultats.getChildren().clear();

				if (arg2.contains("COMPOSANT A BILLES")) {

					cbTypeComposant.setItems(FXCollections.observableArrayList("BGA", "CSP", "WLP"));
					gpTitreComposantExistant.add(new Label("Type de composant"), 0, 1);
					gpTitreComposantExistant.add(cbTypeComposant, 1, 1);

					cbTypeComposant.setVisible(true);
					hbResultats.getChildren().clear();
				} else if (arg2.contains("COMPOSANT LEADLESS")) {

					cbTypeComposant.setItems(FXCollections.observableArrayList("RESISTANCES", "CAPACITES", "LCCC"));
					gpTitreComposantExistant.add(new Label("Type de composant"), 0, 1);
					gpTitreComposantExistant.add(cbTypeComposant, 1, 1);
					cbTypeComposant.setVisible(true);
					hbResultats.getChildren().clear();

				} else if (arg2.contains("COMPOSANT LEADFRAME")) {

					cbTypeComposant.setItems(FXCollections.observableArrayList("QFN"));
					gpTitreComposantExistant.add(new Label("Type de composant"), 0, 1);
					gpTitreComposantExistant.add(cbTypeComposant, 1, 1);
					cbTypeComposant.setVisible(true);
					hbResultats.getChildren().clear();
				} else {
					clear_composants_existants(2);
				}

			}
		});

		cbTypeComposant.valueProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				clear_composants_existants(4);

				hbResultats.getChildren().clear();
				if (arg2 != null) {
					if (arg2.contains("BGA") || arg2.contains("CSP") || arg2.contains("WLP")) {
						if (arg2.contains("BGA")) {
							listeComposant = FXCollections.observableArrayList(Outils.get_list_filename_with_extension(Ressources.pathBrasures, ".brbga"));

						} else if (arg2.contains("CSP")) {
							listeComposant = FXCollections.observableArrayList(Outils.get_list_filename_with_extension(Ressources.pathBrasures, ".brcsp"));
						} else if (arg2.contains("WLP")) {
							listeComposant = FXCollections.observableArrayList(Outils.get_list_filename_with_extension(Ressources.pathBrasures, ".brwlp"));
						} 
						
					} else if (arg2.contains("RESISTANCES") || arg2.contains("LCCC") || arg2.contains("CAPACITES")) {
						if (arg2.contains("RESISTANCES")) {

							listeComposant = FXCollections.observableArrayList(Outils.get_list_filename_with_extension(Ressources.pathBrasures, ".brres"));
						} else if (arg2.contains("LCCC")) {
							listeComposant = FXCollections.observableArrayList(Outils.get_list_filename_with_extension(Ressources.pathBrasures, ".brlcc"));
						} else if (arg2.contains("CAPACITES")) {
							listeComposant = FXCollections.observableArrayList(Outils.get_list_filename_with_extension(Ressources.pathBrasures, ".brcap"));
						}
					}
					
					else if (arg2.contains("QFN")) {
						listeComposant = FXCollections.observableArrayList(Outils.get_list_filename_with_extension(Ressources.pathBrasures, ".brqfn"));
					}
					updateTableview();
				}
			}
		});

		gpTitreComposantExistant = Outils.generer_gridPane();
		gpTitrePcbExistant = Outils.generer_gridPane();

		vbParametresComposants = new VBox(20);

		gpTitreComposantExistant.add(new Label("Catégorie"), 0, 0);
		gpTitreComposantExistant.add(cbCategorieComposant, 1, 0);

		titreComposantsExistants = new TitledPane();
		titreComposantsExistants.setCollapsible(false);
		titreComposantsExistants.setText("Création de la carte");
		titreComposantsExistants.setContent(gpTitreComposantExistant);


		vbParametresComposants.getChildren().addAll(titreComposantsExistants);
		hbParametres.getChildren().addAll(vbParametresComposants);
		getChildren().add(hbParametres);
		
		gpTitreComposantExistant.add(new Label("Filtre"), 0, 3);
		gpTitreComposantExistant.add(search_bar,1,3);

	}
	
	/**
	 * @param Nombre de composant à retirer de gpTitreComposantExistant
	 */
	private void clear_composants_existants(int composant_restant) {
		while (gpTitreComposantExistant.getChildren().size() > composant_restant) {
			gpTitreComposantExistant.getChildren().remove(gpTitreComposantExistant.getChildren().size() - 1);
		}
	}
	
	/**
	 * Initialisation de la tableview
	 */
	private void createTableview() {
		
		tvComposant = new TableView<>();
		tvComposant.setEditable(true);
		
		colonneAjoutComposant = new TableColumn<>();
		colonneAjoutComposant.setEditable(true);
		colonneAjoutComposant.setCellValueFactory(new Callback<CellDataFeatures<String, Boolean>, ObservableValue<Boolean>>() {

			@Override
			public ObservableValue<Boolean> call(CellDataFeatures<String, Boolean> value) {
				
				boolean isChecked = composants.keySet().contains(value.getValue());
				SimpleBooleanProperty booleanAjoutSurCarte = new SimpleBooleanProperty(isChecked);
				
				booleanAjoutSurCarte.addListener(new ChangeListener<Boolean>() {

					@Override
					public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
						if(arg2) {
							composants.put(value.getValue(), 1);
							listComposantCochesDansListView.add(value.getValue());
						}
						else {
							composants.remove(value.getValue());
							listComposantCochesDansListView.remove(value.getValue());
						}
					}
				});
				
				return booleanAjoutSurCarte;
			}
			
        });
		colonneAjoutComposant.setCellFactory(new Callback<TableColumn<String, Boolean>, TableCell<String, Boolean>>() {

			@Override
			public TableCell<String, Boolean> call(TableColumn<String, Boolean> arg0) {
				CheckBoxTableCell<String, Boolean> cell = new CheckBoxTableCell<String, Boolean>();
				cell.setAlignment(Pos.CENTER);
				return cell;
			}
        	
        });

		colonneNom = new TableColumn<>("Nom");
		colonneNom.setCellValueFactory(nom -> new SimpleStringProperty(nom.getValue()));
		colonneNom.setMinWidth(120);
		
		colonneDetails = new TableColumn<>("Details");
		colonneDetails.setMinWidth(120);
		colonneDetails.setCellValueFactory(detail -> {
			BrasureComposant brasure;
			if (cbCategorieComposant.getValue().equals("COMPOSANT A BILLES")) {
				brasure = new BrasureComposantBilles(Ressources.pathBrasures
						+
						detail.getValue());
				System.out.println("Hello There: " + Ressources.pathBrasures + detail.getValue());
				return new SimpleStringProperty(brasure.infoComposant);
			}
			else if (cbCategorieComposant.getValue().equals("COMPOSANT LEADLESS")) {
				brasure = new BrasureComposantLeadless(Ressources.pathBrasures+detail.getValue());
				return new SimpleStringProperty( ((BrasureComposantLeadless)brasure).infoComposant );
			}
			else {
				brasure = new BrasureComposantLeadframe(Ressources.pathBrasures+detail.getValue());
				return new SimpleStringProperty( ((BrasureComposantLeadframe)brasure).infoComposant );
			}

		});
		
		tvComposant.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		tvComposant.getColumns().addAll(colonneAjoutComposant, colonneNom, colonneDetails);
	}

	/**
	 * Rafraichi la tableview
	 */
	public void updateTableview() {
		tvComposant.setItems(listeComposant);
	}
}
