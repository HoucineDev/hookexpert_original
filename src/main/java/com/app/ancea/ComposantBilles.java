package com.app.ancea;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class ComposantBilles extends Composant
		implements InterfaceComposantBilles {

	protected DoubleProperty Puce_x;
	protected DoubleProperty Puce_y;
	protected DoubleProperty Puce_z;

	protected DoubleProperty pas;

	protected BooleanProperty presence_billes_angles;

	protected IntegerProperty nombre_billes_x;
	protected IntegerProperty nombre_billes_y;

	protected boolean matriceBilles[][];

	public ComposantBilles(
			String nom,
			double Puce_x,
			double Puce_y,
			double Puce_z,
			double pas,
			boolean billes_angles) {
		super(nom);

		this.Puce_x = new SimpleDoubleProperty(Puce_x);
		this.Puce_y = new SimpleDoubleProperty(Puce_y);
		this.Puce_z = new SimpleDoubleProperty(Puce_z);

		this.nombre_billes_x = new SimpleIntegerProperty(0);
		this.nombre_billes_y = new SimpleIntegerProperty(0);

		this.pas = new SimpleDoubleProperty(pas);

		this.presence_billes_angles = new SimpleBooleanProperty(billes_angles);

	}

	public ComposantBilles() {
		super();

		this.Puce_x = new SimpleDoubleProperty(0);
		this.Puce_y = new SimpleDoubleProperty(0);
		this.Puce_z = new SimpleDoubleProperty(0);

		this.nombre_billes_x = new SimpleIntegerProperty(0);
		this.nombre_billes_y = new SimpleIntegerProperty(0);

		this.pas = new SimpleDoubleProperty(0);

		this.presence_billes_angles = new SimpleBooleanProperty(false);
	}

	public double getPuce_x() {
		return Puce_x.getValue();
	}

	public void setPuce_x(double Puce_x) {
		this.Puce_x.set(Puce_x);
	}

	public double getPuce_y() {
		return Puce_y.getValue();
	}

	public void setPuce_y(double Puce_y) {
		this.Puce_y.set(Puce_y);
	}

	public double getPuce_z() {
		return Puce_z.getValue();
	}

	public int getNombre_billes_x() {
		return nombre_billes_x.getValue();
	}

	public int getNombre_billes_y() {
		return nombre_billes_y.getValue();
	}

	public void setPuce_z(double Puce_z) {
		this.Puce_z.set(Puce_z);
	}

	public boolean getPresence_billes_angles() {
		return presence_billes_angles.getValue();
	}

	public void setPresence_billes_angles(boolean presence_billes_angles) {
		this.presence_billes_angles.set(presence_billes_angles);
	}

	public double getPas() {
		return pas.getValue();
	}

	public void setPas(double pas) {
		this.pas.set(pas);
	}

	@Override
	public double getE1Traction(double temperature) {
		// TODO Auto-generated method stub
		return (Double) null;
	}

	@Override
	public String getComposant_z() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getCte(double temperature) {
		// TODO Auto-generated method stub
		return (Double) null;
	}

	/**
	 * Cette fonction utilise la matrice de Billes du composant afin de calculer
	 * le Nombre X (ou la longueur caractéristique Lnp) associé à ce composant.
	 * Dans le cas d'un BGA on a besoin de deux points neutres corrigés qui eux
	 * dépendent de la température mais qui est inutile dans les autres cas (CSP
	 * ou WLP par ex).
	 * 
	 * @param composant                 Le composant à billes dont on cherche à
	 *                                  calculer le Nombre X
	 * 
	 * @param pointNeutreTCorrigeX      Valeur utile uniquement dans le cas d'un
	 *                                  BGA
	 * 
	 * @param pointNeutreTCorrigeY      Valeur utile uniquement dans le cas d'un
	 *                                  BGA
	 * 
	 * @param affichageMatricesDeCalcul En le passant à "true", permet
	 *                                  d'afficher toutes les étapes
	 *                                  intermédiaires si on le souhaite
	 * 
	 * @param sortieSouhaitee           Un paramètre permettant de modifier la
	 *                                  sortie de la fonction. Pour récupérer le
	 *                                  nombre X sortieSouhaitee vaudra
	 *                                  "nombreX", pour Lnp1, sortieSouhaitee
	 *                                  vaudra "lnp1".
	 * 
	 * @return Le nombre X calculé sur la base de la matrice de billes
	 */
	protected double calculNombreX(
			Composant composant,
			double pointNeutreTCorrigeX,
			double pointNeutreTCorrigeY,
			boolean affichageMatricesDeCalcul,
			String sortieSouhaitee) {

		boolean presenceWLP = false;
		boolean presenceBGA = false;
		boolean presenceSBGA = false;
		boolean presencePCBGA = false;

		if (composant instanceof ComposantWLP
				|| composant instanceof ComposantCBGA) {
			presenceWLP = true;
		}
		if ((composant instanceof ComposantSBGA)) {
			presenceSBGA = true;
		}
		if ((composant instanceof ComposantPCBGA)) {
			// Meme chemin que le WLP
			presenceWLP = true;
		}
		/*
		 * Un CSP est forcément un BGA mais ce n'est pas forcément réciproque
		 * idem pour les SBGA
		 */
		if ((composant instanceof ComposantBGA)
				&& (!(composant instanceof ComposantCSP))
				&& (!(composant instanceof ComposantSBGA))) {
			presenceBGA = true;
		}

		int nb_lignes = this.matriceBilles.length;
		int nb_colonnes = this.matriceBilles[0].length;

		Double matrice_x_y = (double) nb_colonnes / (double) nb_lignes;

		Double centreX = (((((double) nb_colonnes + 1) / 2) - 1) - 1)
				* this.getPas();
		Double centreY = (((((double) nb_lignes + 1) / 2) - 1) - 1)
				* this.getPas();

		Double centreNumeroX = (centreX / this.getPas()) + 1;
		Double centreNumeroY = (centreY / this.getPas()) + 1;
		Double puceX;
		Double puceY;

		int PuceColonneX1;
		int PuceColonneX2;
		int PuceRangeeY1;
		int PuceRangeeY2;

		if (presenceWLP) {
			puceX = (double) nb_colonnes;
			puceY = (double) nb_lignes;
		} else {

			puceX = ((ComposantBGA) composant).getPuce_x();
			puceY = ((ComposantBGA) composant).getPuce_y();

		}

		Double puce_x_y = puceX / puceY;

		if (!presenceWLP) {

			PuceColonneX1 = (int) (centreNumeroX
					- (((ComposantBGA) composant).getPuce_x()
							/ (2 * this.getPas())));
			PuceColonneX2 = (int) (centreNumeroX
					+ (((ComposantBGA) composant).getPuce_x()
							/ (2 * this.getPas())));

			PuceRangeeY1 = (int) (centreNumeroY
					- (((ComposantBGA) composant).getPuce_y()
							/ (2 * this.getPas())));
			PuceRangeeY2 = (int) (centreNumeroY
					+ (((ComposantBGA) composant).getPuce_y()
							/ (2 * this.getPas())));

		} else {

			PuceColonneX1 = 0;
			PuceColonneX2 = (int) nb_colonnes - 1;

			PuceRangeeY1 = 0;
			PuceRangeeY2 = (int) nb_lignes - 1;
		}

		// Matrice finale
		Double matrice_creuse[][] = new Double[nb_lignes][nb_colonnes];

		// Permet de comprendre le processus de calcul
		Integer matrice_intermediaire[][] = new Integer[nb_lignes][nb_colonnes];

		Double matriceDistanceCentreX[][] = new Double[nb_lignes][nb_colonnes];
		Double matriceDistanceCentreY[][] = new Double[nb_lignes][nb_colonnes];
		Double matriceDistanceCentreXBillesSousPuce[][] = new Double[nb_lignes][nb_colonnes];
		Double matriceDistanceCentreYBillesSousPuce[][] = new Double[nb_lignes][nb_colonnes];
		Double matriceDistanceCentreXYBillesSousPuce[][] = new Double[nb_lignes][nb_colonnes];
		;

		Integer matriceBillesSousPuce[][] = new Integer[nb_lignes][nb_colonnes];
		Integer matriceBillesSousZoneNeutreHorsPuceT[][] = new Integer[nb_lignes][nb_colonnes];

		Double distanceCentreX = 0d;
		Double distanceCentreY = 0d;

		Double pointNeutreTCorrigeNumeroX1 = null;
		Double pointNeutreTCorrigeNumeroX2 = null;
		Double pointNeutreTCorrigeNumeroY1 = null;
		Double pointNeutreTCorrigeNumeroY2 = null;

		boolean dansLaPuce;
		boolean dansZoneNeutreT = false;

		Double matriceColonneCalculIntermediairesT[][] = new Double[3][nb_colonnes];
		Double matriceLigneCalculIntermediairesT[][] = new Double[nb_lignes][3];

		int nombreX;
		double lnp1;

		if (presenceBGA || presenceSBGA) {

			if (pointNeutreTCorrigeX > centreX) {
				pointNeutreTCorrigeNumeroX1 = 1.0;
				pointNeutreTCorrigeNumeroX2 = (double) nb_colonnes;
			} else {
				pointNeutreTCorrigeNumeroX1 = centreNumeroX
						- (pointNeutreTCorrigeX / matrice_x_y);
				pointNeutreTCorrigeNumeroX2 = centreNumeroX
						+ (pointNeutreTCorrigeX / matrice_x_y);
			}

			if (pointNeutreTCorrigeY > centreY) {
				pointNeutreTCorrigeNumeroY1 = 1.0;
				pointNeutreTCorrigeNumeroY2 = (double) nb_lignes;
			} else {
				pointNeutreTCorrigeNumeroY1 = centreNumeroY
						- (pointNeutreTCorrigeY / matrice_x_y);
				pointNeutreTCorrigeNumeroY2 = centreNumeroY
						+ (pointNeutreTCorrigeY / matrice_x_y);
			}

		}

		for (int i = 0; i < nb_lignes; i++) {
			for (int j = 0; j < nb_colonnes; j++) {
				if (this.matriceBilles[i][j]) {

					dansLaPuce = Outils.inRange(PuceRangeeY1, PuceRangeeY2, i)
							&& Outils.inRange(PuceColonneX1, PuceColonneX2, j);

					if (presenceBGA) {
						dansZoneNeutreT = Outils.inRange(
								pointNeutreTCorrigeNumeroX1,
								pointNeutreTCorrigeNumeroX2,
								j)
								&& Outils.inRange(
										pointNeutreTCorrigeNumeroY1,
										pointNeutreTCorrigeNumeroY2,
										i);

					}

					distanceCentreX = 0d;
					distanceCentreY = 0d;

					matrice_intermediaire[i][j] = 1;

					distanceCentreX = -(centreNumeroX - j) * this.getPas();

					distanceCentreY = (centreNumeroY - i) * this.getPas();

					matriceDistanceCentreX[i][j] = distanceCentreX;

					matriceDistanceCentreY[i][j] = distanceCentreY;

					if (dansLaPuce) {
						// Matrice de 1 et 0
						matriceBillesSousPuce[i][j] = 1;

						distanceCentreX = 0d;
						distanceCentreY = 0d;

						distanceCentreX = -(centreNumeroX - j) * this.getPas();
						distanceCentreY = (centreNumeroY - i) * this.getPas();

						matriceDistanceCentreXBillesSousPuce[i][j] = distanceCentreX;

						matriceDistanceCentreYBillesSousPuce[i][j] = distanceCentreY;

					} else {

						matriceBillesSousPuce[i][j] = 0;
						matriceDistanceCentreXBillesSousPuce[i][j] = 0d;

						matriceDistanceCentreYBillesSousPuce[i][j] = 0d;
					}

					if (presenceBGA || presenceSBGA) {
						// Sous zone neutre mais hors zone puce T
						if (dansZoneNeutreT && !dansLaPuce) {
							matriceBillesSousZoneNeutreHorsPuceT[i][j] = 1;
						} else {
							matriceBillesSousZoneNeutreHorsPuceT[i][j] = 0;
						}
					}
				}
			}
		}
		int sumColonneT = 0;
		// Calculs intermédiares Colonne pour le nombre X à la température T1

		// Somme Colonne matrice Billes sous Zone Neutre hors puce T1
		for (int j = 0; j < nb_colonnes; j++) {
			for (int k = 0; k < nb_lignes; k++) {
				if (matriceBillesSousZoneNeutreHorsPuceT[k][j] != null) {
					sumColonneT += matriceBillesSousZoneNeutreHorsPuceT[k][j];
				}

			}
			matriceColonneCalculIntermediairesT[0][j] = (double) sumColonneT;
			sumColonneT = 0;
		}

		Double maxT = 0d;
		// Maximum de chaque colonne matrice Distance/centre sens y billes sous
		// puce
		for (int j = 0; j < nb_colonnes; j++) {
			for (int i = 0; i < nb_lignes; i++) {
				if (matriceDistanceCentreYBillesSousPuce[i][j] != null) {
					if (matriceDistanceCentreYBillesSousPuce[i][j] > maxT) {
						maxT = matriceDistanceCentreYBillesSousPuce[i][j];
					}
				}
			}
			matriceColonneCalculIntermediairesT[1][j] = maxT;
			maxT = 0d;
		}

		Double sommeProdT = 0d;
		for (int j = 0; j < nb_colonnes; j++) {
			for (int i = 0; i < nb_lignes; i++) {
				if (matriceDistanceCentreYBillesSousPuce[i][j] != null) {
					sommeProdT += Math
							.abs(matriceDistanceCentreYBillesSousPuce[i][j]);
				}
			}

			matriceColonneCalculIntermediairesT[2][j] = (double) Math.round(
					(sommeProdT + (matriceColonneCalculIntermediairesT[0][j]
							* matriceColonneCalculIntermediairesT[1][j])) / 2);
			sommeProdT = 0d;
		}

		for (int i = 0; i < nb_lignes; i++) {

			matriceLigneCalculIntermediairesT[i][0] = (double) Outils
					.arraySum(matriceBillesSousZoneNeutreHorsPuceT[i]);
			matriceLigneCalculIntermediairesT[i][1] = Outils
					.arrayMax(matriceDistanceCentreXBillesSousPuce[i]);
			matriceLigneCalculIntermediairesT[i][2] = (double) Math.round(
					(Outils.arrayAbsoluteSum(
							matriceDistanceCentreXBillesSousPuce[i])
							+ (matriceLigneCalculIntermediairesT[i][0]
									* matriceLigneCalculIntermediairesT[i][1]))
							/ 2);
		}

		int compteur = 0;
		Double moyenneT11 = 0d;

		/*
		 * for (int i = 0; i < nb_lignes; i++) { if
		 * (matriceLigneCalculIntermediairesT[i][2] > 0) { moyenneT11 +=
		 * matriceLigneCalculIntermediairesT[i][2]; compteur++; } }
		 * 
		 * moyenneT11 /= compteur;
		 */
		// On prend le maximum au lieu de la moyenne
		for (int i = 0; i < nb_lignes; i++) {
			if (matriceLigneCalculIntermediairesT[i][2] != null) {
				if (moyenneT11 < matriceLigneCalculIntermediairesT[i][2]) {
					moyenneT11 = matriceLigneCalculIntermediairesT[i][2];
				}
			}
		}
		Double moyenneT12 = 0d;
		// Moyenne sur ligne d'un tableau
		// moyenneT12 = Outils
		// .arrayPositiveAverage(matriceColonneCalculIntermediairesT[2]);

		// Max sur ligne d'un tableau
		moyenneT12 = Outils.arrayMax(matriceColonneCalculIntermediairesT[2]);

		nombreX = (int) Math.round(
				Math.sqrt(Math.pow(moyenneT11, 2) + Math.pow(moyenneT12, 2)));

		// Matrice Distance Centre XY Billes sous puce
		for (int i = 0; i < nb_lignes; i++) {
			for (int j = 0; j < nb_colonnes; j++) {
				if (matriceDistanceCentreXBillesSousPuce[i][j] != null
						&& matriceDistanceCentreYBillesSousPuce[i][j] != null) {

					matriceDistanceCentreXYBillesSousPuce[i][j] = Math.sqrt(
							Math.pow(
									matriceDistanceCentreXBillesSousPuce[i][j],
									2)
									+ Math.pow(
											matriceDistanceCentreYBillesSousPuce[i][j],
											2));
				}
			}
		}

		lnp1 = Outils.arrayMaxMatrix(matriceDistanceCentreXYBillesSousPuce);

		// Calcul de Lnp1
		// double lnp11, lnp12;

		// On cherche le maximum de la colonne 2 de cette matrice
		// lnp11 = matriceLigneCalculIntermediairesT[0][1];
		// for (int i = 0; i < nb_lignes; i++) {
		// if (matriceLigneCalculIntermediairesT[i][1] > lnp11) {
		// lnp11 = matriceLigneCalculIntermediairesT[i][1];
		// }
		// }

		// lnp12 = Outils.arrayMax(matriceColonneCalculIntermediairesT[1]);

		// lnp1 = Math.sqrt(Math.pow(lnp11, 2) + Math.pow(lnp12, 2));

		// permet d'afficher toutes les étapes intermédiaires si nécéssaires
		if (affichageMatricesDeCalcul) {

			System.out.println("Matrice Intermediaire:\n");
			Outils.prettyPrintMatrix(matrice_intermediaire);

			System.out.println("Matrice Distance/centre sens x:\n");
			Outils.prettyPrintMatrix(matriceDistanceCentreXBillesSousPuce);

			System.out.println("Matrice Distance/centre sens y:\n");
			Outils.prettyPrintMatrix(matriceDistanceCentreYBillesSousPuce);

			System.out.println("Matrice Billes sous puce:\n");
			Outils.prettyPrintMatrix(matriceBillesSousPuce);

			System.out.println(
					"Matrice Billes sous zone neutre et hors puce T:\n");
			Outils.prettyPrintMatrix(matriceBillesSousZoneNeutreHorsPuceT);

			System.out.println(
					"Matrice Distance/centre sens x Billes sous puce:\n");
			Outils.prettyPrintMatrix(matriceDistanceCentreXBillesSousPuce);

			System.out.println(
					"Matrice Distance/centre sens y Billes sous puce:\n");
			Outils.prettyPrintMatrix(matriceDistanceCentreYBillesSousPuce);

			System.out.println("Matrice Colonne Calcul Intermediaire T:\n");
			Outils.prettyPrintMatrix(matriceColonneCalculIntermediairesT);

			System.out.println("Matrice Ligne Calcul Intermediaire T:\n");
			Outils.prettyPrintMatrix(matriceLigneCalculIntermediairesT);

		}

		switch (sortieSouhaitee) {
		case "nombreX":
			return (double) nombreX;

		case "lnp1":
			return lnp1;

		default:
			Outils.alerteUtilisateur(
					"ERROR",
					"Erreur 'calculNombreX'",
					"'nombreX' ou 'lnp1' attendu comme parametre mais"
							+ sortieSouhaitee + " trouvé !");
			return (Double) null;

		}
	}

}
