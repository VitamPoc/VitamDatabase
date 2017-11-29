/**
 * This file is part of Vitam Project.
 * 
 * Copyright 2010, Frederic Bregier, and individual contributors by the @author tags. See the
 * COPYRIGHT.txt in the distribution for a full listing of individual contributors.
 * 
 * All Vitam Project is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 * 
 * Vitam is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with Vitam. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package fr.gouv.culture.vitam.constance;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.waarp.common.database.exception.WaarpDatabaseException;
import org.waarp.common.database.exception.WaarpDatabaseSqlException;
import org.waarp.common.logging.WaarpInternalLogger;
import org.waarp.common.logging.WaarpInternalLoggerFactory;
import org.waarp.common.logging.WaarpSlf4JLoggerFactory;

import ch.qos.logback.classic.Level;

import fr.gouv.culture.vitam.constance.ConstanceIdentifier.ConstanceFormat;
import fr.gouv.culture.vitam.database.DbSchema;
import fr.gouv.culture.vitam.database.DbVitam2Database;
import fr.gouv.culture.vitam.database.utils.ConfigLoader;
import fr.gouv.culture.vitam.database.utils.StaticValues;
import fr.gouv.culture.vitam.writer.XmlWriter;

/**
 * Cindoc database structure
 * 
 * @author "Frederic Bregier"
 * 
 */
public class ConstanceCindoc {
	/**
	 * Internal Logger
	 */
	private static WaarpInternalLogger logger;
	
	/**
	 * FICHE D'APPLICATION (APPFICH et ZAPELIM) champ clé : APPLI ; longueur de clé : 4 ; numérique
	 * ; pas : 1 ; séparateur d'articles : "/" ; séparateur champ-contenu du champ "."
	 * 
	 */
	public static enum APPFICH {
		APPLI("Numéro de l'application = la référence de tous les autres instruments de recherche",
				Types.INTEGER),
		N1("Désignation du service versant : Code N1"),
		MTERE("Désignation du service versant :Ministère"),
		DIR("Désignation du service versant :Direction"),
		D_DIR("Désignation du service versant : Sous-direction"),
		BUR("Désignation du service versant : Bureau"),
		NOM(
				"Nom en clair avec l'abréviation courante utilisée le cas échéant (FQP, par exemple) ; ce nom sert là aussi de référence à tous les autres fichiers Constance"),
		SOURCE(
				"Seulement dans le cas de l'INSEE = dénominateur commun à plusieurs fichiers ou enquêtes d'une même application appelée source à l'INSEE",
				Types.INTEGER),
		CYCLE(
				"Description de la structure de l'articulation des différents fichiers d'une application : enquête annuelle, bisannuelle ou, plus compliqué cf appli 19"),
		N_FI_C(
				"Nombre de fichiers par cycle ou par élément de la structure de l'application cf app 19"),
		OBS(
				"Toute information qui semble nécessaire pour que le chercheur s'y retrouve (fichiers manquants, etc)"),
		N_FI_T("Nombre total de fichiers archivés dans l'application", Types.INTEGER),
		D_MO("Date de mise en œuvre de l'application"),
		D_FIN("Date de fin de l'application (si APP-VIV = non)"),
		APP_VIV("Application toujours vivante ou non"),
		OBJET(
				"Objet de l'application (souvent tiré de publications officielles ou de présentations générale de l'enquête)"),
		OBMIN("Objet de l'application en caractères enrichis (minuscules accentués)"),
		CHPS("Champ statistique = population couverte, échantillon, etc"),
		P_GEO("Portée géographique =  Métropole par ex."),
		D_F1("Date du premier fichier archivé ou de début de la période couverte"),
		D_FN("Date du dernier fichier archivé ou de fin de la période couverte"),
		P_ARCHI(
				"Programme d'archivage = s'il existe texte de la convention d'archivage passée avec le service versant (fréquence et dates des versements de fichiers à venir)"),
		LIEN_AP("Lien avec d'autres applications informatiques (archivées ou non)"),
		LIEN_FD("Lien avec le fonds d'archives (autres versements P3 le cas échéant)"),
		BIBLE(
				"Bibliographie = cotes des PO du CAC le cas échéant, publications officielles en lien direct avec l'application concernée"),
		NOP(
				"Nombre d'opérations d'archivage (et articulation de celles-ci) dont l'application fait l'objet : ex : 1 = 1970-1980 2 = 1981 3 = 1982"),
		P1("Numéro(s) de versement Priam 1 = cote archives nationales"),
		D_DON("Nom du fichier Texto du (des) dictionnaire(s) des données"),
		D_COD("Nom du fichier Texto du (des) dictionnaire(s) des codes"),
		F_STRUCT("Nom du fichier Texto des structures"),
		FICH(
				"Références des fiches archivistiques de fichiers (à l'intérieur du fichier FICH) – champ obsolète depuis migration textoligne à textowindows"),
		DATE("Date de rédaction ou de mise à jour de la fiche d'application"),
		ATOUF("Champ de gestion interne"),
		DOMAINE("Champ thématique de l'application");

		public String description;
		public String name;
		public int type;
		public String table;
		public String champ;
		public boolean pk;

		private APPFICH(String description) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = Types.LONGVARCHAR;
		}

		private APPFICH(String description, int type) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = type;
		}

		private APPFICH(String description, boolean pk) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = Types.LONGVARCHAR;
			this.pk = pk;
		}

		private APPFICH(String description, int type, boolean pk) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = type;
			this.pk = pk;
		}

		private APPFICH(String description, String table, String champ) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = Types.LONGVARCHAR;
			this.table = table;
			this.champ = champ;
		}

		private APPFICH(String description, int type, String table, String champ) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = type;
			this.table = table;
			this.champ = champ;
		}

		public static final String getSeparatorArticle() {
			return "/";
		}

		public static final String getSeparatorChamp() {
			return ".";
		}
	}

	/**
	 * FICHE D'APPLICATION (APPFICH et ZAPELIM) champ clé : APPLI ; longueur de clé : 4 ; numérique
	 * ; pas : 1 ; séparateur d'articles : "/" ; séparateur champ-contenu du champ "."
	 * 
	 */
	public static enum ZAPELIM {
		APPLI("Numéro de l'application = la référence de tous les autres instruments de recherche",
				Types.INTEGER),
		N1("Désignation du service versant : Code N1"),
		MTERE("Désignation du service versant :Ministère"),
		DIR("Désignation du service versant :Direction"),
		D_DIR("Désignation du service versant : Sous-direction"),
		BUR("Désignation du service versant : Bureau"),
		NOM(
				"Nom en clair avec l'abréviation courante utilisée le cas échéant (FQP, par exemple) ; ce nom sert là aussi de référence à tous les autres fichiers Constance"),
		SOURCE(
				"Seulement dans le cas de l'INSEE = dénominateur commun à plusieurs fichiers ou enquêtes d'une même application appelée source à l'INSEE",
				Types.INTEGER),
		CYCLE(
				"Description de la structure de l'articulation des différents fichiers d'une application : enquête annuelle, bisannuelle ou, plus compliqué cf appli 19"),
		N_FI_C(
				"Nombre de fichiers par cycle ou par élément de la structure de l'application cf app 19"),
		OBS(
				"Toute information qui semble nécessaire pour que le chercheur s'y retrouve (fichiers manquants, etc)"),
		N_FI_T("Nombre total de fichiers archivés dans l'application", Types.INTEGER),
		D_MO("Date de mise en œuvre de l'application"),
		D_FIN("Date de fin de l'application (si APP-VIV = non)"),
		APP_VIV("Application toujours vivante ou non"),
		OBJET(
				"Objet de l'application (souvent tiré de publications officielles ou de présentations générale de l'enquête)"),
		OBMIN("Objet de l'application en caractères enrichis (minuscules accentués)"),
		CHPS("Champ statistique = population couverte, échantillon, etc"),
		P_GEO("Portée géographique =  Métropole par ex."),
		D_F1("Date du premier fichier archivé ou de début de la période couverte"),
		D_FN("Date du dernier fichier archivé ou de fin de la période couverte"),
		P_ARCHI(
				"Programme d'archivage = s'il existe texte de la convention d'archivage passée avec le service versant (fréquence et dates des versements de fichiers à venir)"),
		LIEN_AP("Lien avec d'autres applications informatiques (archivées ou non)"),
		LIEN_FD("Lien avec le fonds d'archives (autres versements P3 le cas échéant)"),
		BIBLE(
				"Bibliographie = cotes des PO du CAC le cas échéant, publications officielles en lien direct avec l'application concernée"),
		NOP(
				"Nombre d'opérations d'archivage (et articulation de celles-ci) dont l'application fait l'objet : ex : 1 = 1970-1980 2 = 1981 3 = 1982"),
		P1("Numéro(s) de versement Priam 1 = cote archives nationales"),
		D_DON("Nom du fichier Texto du (des) dictionnaire(s) des données"),
		D_COD("Nom du fichier Texto du (des) dictionnaire(s) des codes"),
		F_STRUCT("Nom du fichier Texto des structures"),
		FICH(
				"Références des fiches archivistiques de fichiers (à l'intérieur du fichier FICH) – champ obsolète depuis migration textoligne à textowindows"),
		DATE("Date de rédaction ou de mise à jour de la fiche d'application"),
		ATOUF("Champ de gestion interne"),
		DOMAINE("Champ thématique de l'application");

		public String description;
		public String name;
		public int type;
		public String table;
		public String champ;
		public boolean pk;

		private ZAPELIM(String description) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = Types.LONGVARCHAR;
		}

		private ZAPELIM(String description, int type) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = type;
		}

		private ZAPELIM(String description, boolean pk) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = Types.LONGVARCHAR;
			this.pk = pk;
		}

		private ZAPELIM(String description, int type, boolean pk) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = type;
			this.pk = pk;
		}

		private ZAPELIM(String description, String table, String champ) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = Types.LONGVARCHAR;
			this.table = table;
			this.champ = champ;
		}

		private ZAPELIM(String description, int type, String table, String champ) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = type;
			this.table = table;
			this.champ = champ;
		}

		public static final String getSeparatorArticle() {
			return "/";
		}

		public static final String getSeparatorChamp() {
			return ".";
		}
	}

	/**
	 * FICHE DE DOCUMENTATION (DOC) champ clé : NUM ; longueur de clé : 4 ; numérique ; pas : 1 ;
	 * séparateur d'articles : "/" ; séparateur champ-contenu du champ "."
	 * 
	 */
	public static enum DOCUMENTATION {
		NUM("Référence Texto de la fiche", Types.INTEGER),
		PROV("Nom du ministère versant"),
		CORR("Nom du correspondant qui envoie la documentation"),
		APP(" (Type = chainant ; contrôles : APPFICH) Numéro de l'application"),
		OP(
				"Numéro de l'opération (= N° d'application + 2 chiffres pour le numéro de l'opération à l'intérieur de l'application)"),
		DOCB(
				"Description en texte libre d'un document ou d'un ensemble de documents (sans règles strictement établies, juste une tendance = fractionner la description, soit ne pas trop en mettre dans chaque fiche)"),
		DOS(
				"(en août 2004) Pour mémoire : ne sert plus Sert à préciser la référence de FICFICH dans les cas comme l'appli 0222 où la documentation est livrée et décrite de la même façon que les fichiers de données : VOIR FICFICH REF. nnnnn"),
		NFICHDOC(
				"(ajouté en 06/2002 pour décrire la conservation de la documentation sous forme numérique) ; sans modification de nom ce champ depuis juillet 2004 représente le nom original et inchangé de la documentation électronique à l'arrivée et telle qu'elle a été livrée) Nom du fichier électronique tel qu'il a été livré par le service versant"),
		NFICARCH(
				"(ajouté en juillet 2004, but = nom du fichier placé sous Constdoc\\Documentation et donc nom du fichier archivé par IG en même temps que le fichier de données ; alors que NFICHDOC représente le nom du fichier de documentation tel qu'il a été livré = le nom du fichier à l'arrivée) Nom du fichier de conservation (= nom donné par Constance selon ses règles de nomination établies pour l'archivage)"),
		DLTA(
				"(ajouté en 06/2002 pour décrire la conservation de la documentation sous forme numérique) Référence du support de conservation (original)"),
		DATEDLTA(
				"(ajouté en 06/2002 pour décrire la conservation de la documentation sous forme numérique) Date du support de conservation"),
		DLTB(
				"(ajouté en 06/2002 pour décrire la conservation de la documentation sous forme numérique) Référence du support de conservation (double)"),
		DATEDLTB(
				"(ajouté en 06/2002 pour décrire la conservation de la documentation sous forme numérique) Date du support de conservation (double)"),
		NBKOCTETD("Taille en Kilo octets du fichier électronique de conservation"),
		FORMATC(
				"(ajouté en 06/2002 pour décrire la conservation de la documentation sous forme numérique) Format de conservation du fichier électronique de documentation"),
		VERSIONC(
				"(ajouté en 06/2002 pour décrire la conservation de la documentation sous forme numérique) Version du format de conservation du fichier électronique de conservation (ex : Word.6, etc.)"),
		NOTE(
				"Notes sur le mode de réception, les 'annule et remplace', etc. et 'dessin de fichier' ou 'code' selon les cas"),
		DREC("Date de réception de la documentation"),
		PR1("Numéro de versement Priam1 et numéro d'article dans le versement");

		public String description;
		public String name;
		public int type;
		public String table;
		public String champ;
		public boolean pk;

		private DOCUMENTATION(String description) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = Types.LONGVARCHAR;
		}

		private DOCUMENTATION(String description, int type) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = type;
		}

		private DOCUMENTATION(String description, boolean pk) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = Types.LONGVARCHAR;
			this.pk = pk;
		}

		private DOCUMENTATION(String description, int type, boolean pk) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = type;
			this.pk = pk;
		}

		private DOCUMENTATION(String description, String table, String champ) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = Types.LONGVARCHAR;
			this.table = table;
			this.champ = champ;
		}

		private DOCUMENTATION(String description, int type, String table, String champ) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = type;
			this.table = table;
			this.champ = champ;
		}

		public static final String getSeparatorArticle() {
			return "/";
		}

		public static final String getSeparatorChamp() {
			return ".";
		}
	}

	/**
	 * FICHE D'OPERATION (OP) champ clé : NUM ; longueur de clé : 3 ; numérique ; pas : 1 ;
	 * séparateur d'articles : "/" ; séparateur champ-contenu du champ "."
	 * 
	 */
	public static enum OP {
		NUM("Référence Texto de la fiche", Types.INTEGER),
		APP("(Type = chainant ; contrôles : APPFICH) Numéro de l'application"),
		OP(
				"Numéro de l'opération (= N° d'application + 2 chiffres pour le numéro de l'opération à l'intérieur de l'application)"),
		D1("Date du premier fichier archivé ou de début de la période couverte"),
		DN("Date du dernier fichier archivé ou de fin de la période couverte"),
		DVT("Date du versement des fichiers (AAAA)"),
		RESPI("Initiales du responsable informaticien de l'opération"),
		RT("Date de la fiche de recette technique"),
		RESPA("Initiales de l'archiviste responsable de l'opération"),
		VT("Numéro du versement Priam1"),
		N_FI("Nombre de fichiers de l'opération"),
		NOTE("Généralement non rempli");

		public String description;
		public String name;
		public int type;
		public String table;
		public String champ;
		public boolean pk;

		private OP(String description) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = Types.LONGVARCHAR;
		}

		private OP(String description, int type) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = type;
		}

		private OP(String description, boolean pk) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = Types.LONGVARCHAR;
			this.pk = pk;
		}

		private OP(String description, int type, boolean pk) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = type;
			this.pk = pk;
		}

		private OP(String description, String table, String champ) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = Types.LONGVARCHAR;
			this.table = table;
			this.champ = champ;
		}

		private OP(String description, int type, String table, String champ) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = type;
			this.table = table;
			this.champ = champ;
		}


		public static final String getSeparatorArticle() {
			return "/";
		}

		public static final String getSeparatorChamp() {
			return ".";
		}
	}

	/**
	 * ZFICELI : FUSION DE ZFICELIM ET ZFICELIN champ clé : REF ; longueur de clé : 6 ; numérique ;
	 * pas : * ; séparateur d'articles : "=" ; séparateur champ-contenu du champ ":"
	 * 
	 */
	public static enum ZFICELI {
		REF("référence texto (Longueur : 6, pas :1,numérique, séparateur : =)", Types.INTEGER),
		REFL("Ancienne référence Textow"),
		N_APPLI("numéro d'application"),
		FACEDON("(ZFICELIN) face DON"),
		DATEDON("(ZFICELIN) Date DON"),
		DLT_A("(ZFICELIN) Référence DLT A"),
		DATEDLTA("(ZFICELIN) Date DLT A"),
		DLT_B("(ZFICELIN) Référence DLT B"),
		DATEDLTB("(ZFICELIN) Date DLT B"),
		N_ORDIN("(ZFICELIN) nom de l'ordinateur"),
		D_REGV3("date de 3ième régénération"),
		N_ORDIM("(ZFICELIM) nom de l'ordinateur"),
		N_SYSTEM("(ZFICELIM) Système (ZFICELIM)"),
		N_SYSTEN("(ZFICELIN) Système (ZFICELIN)"),
		FORM_ENRN("(ZFICELIN) format des enregistrements"),
		LG_ENRN("(ZFICELIN) longueur des enregistrements"),
		NB_OCTSN("(ZFICELIN) nombre d'octets"),
		NB_ENRN("(ZFICELIN) nombre d'enregistrements"),
		CLE_TRIN("(ZFICELIN) clés de tri"),
		RSTRUCTN("(ZFICELIN) nom de la structure"),
		DAT_ELIN("(ZFICELIN) date d'élimination"),
		MOTIFN("(ZFICELIN) motif de l'élimination"),
		RESPAN("(ZFICELIN) responsable de l'élimination"),
		NB_ENRM("(ZFICELIM) nombre d'enregistrements"),
		RESPAM("(ZFICELIM) responsable de l'élimination"),
		N_FICH("(ZFICELIM) nom du fichier"),
		N_EXTF("(ZFICELIM) ne sert pas"),
		EXPL("(ZFICELIM) ne sert pas"),
		N_VOLUME("(ZFICELIM) nom du support d'origine"),
		N_SECURI("(ZFICELIM) nom du support de sécurité"),
		ANNEE_F("(ZFICELIM) année du fichier"),
		D_REGV1("date de 1ière régénération"),
		DENSITE("(ZFICELIM) densité du support"),
		LABEL("(ZFICELIM) label pour les bandes"),
		FORMENRM("(ZFICELIM) format des enregistrements"),
		D_REGV2("date de 2ème régénération"),
		LG_BLK("(ZFICELIM) longueur des blocs"),
		LG_ENRM("(ZFICELIM) longueur des enregistrements"),
		NB_BLKS("(ZFICELIM) nombre de blocs"),
		NB_OCTSM("(ZFICELIM) nombre d'octets"),
		NB_ENR("(vérif 2002-2003)"),
		DLT1("(vérif 2002-2003)"),
		DATEDLT1("(vérif 2002-2003)"),
		DLT2("(vérif 2002-2003)"),
		DATEDLT2("(vérif 2002-2003)"),
		VDAT("(vérif 2002-2003)"),
		DATAVDAT("(vérif 2002-2003)"),
		CLE_TRIM("(ZFICELIM) clés de tri"),
		RSTRUCTM("(ZFICELIM) nom de la structure"),
		NB_POPUL("(ZFICELIM) population"),
		PROG("(ZFICELIM) nom du programme"),
		DATE_ELIM("(ZFICELIM) date d'élimination"),
		MOTIFM("(ZFICELIM) motif de l'élimination"),
		NOTE("Généralement non rempli");

		public String description;
		public String name;
		public int type;
		public String table;
		public String champ;
		public boolean pk;

		private ZFICELI(String description) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = Types.LONGVARCHAR;
		}

		private ZFICELI(String description, int type) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = type;
		}

		private ZFICELI(String description, boolean pk) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = Types.LONGVARCHAR;
			this.pk = pk;
		}

		private ZFICELI(String description, int type, boolean pk) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = type;
			this.pk = pk;
		}

		private ZFICELI(String description, String table, String champ) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = Types.LONGVARCHAR;
			this.table = table;
			this.champ = champ;
		}

		private ZFICELI(String description, int type, String table, String champ) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = type;
			this.table = table;
			this.champ = champ;
		}


		public static final String getSeparatorArticle() {
			return "=";
		}

		public static final String getSeparatorChamp() {
			return ":";
		}
	}

	/**
	 * DICTIONNAIRE DES DONNEES (DDON) champ clé : NUM ; longueur de clé : 6 ; numérique ; pas : 1 ;
	 * séparateur d'articles : "/" ; séparateur champ-contenu du champ "."
	 * 
	 */
	public static enum DDON {
		NUM("Référence Cindoc de la fiche", Types.INTEGER),
		N_APPLID(
				"Numéro et nom de l'application (séparés par un /), tels qu'ils figurent dans la référence cindoc et le champs NOM de APPFICH"),
		NOAPPLID(
				"(28/07/2004 : sur 6 caractères = 4-->appli et 2 --> opération) Numéro de l'application en clair (redondant avec le champ précédent) sur 4 caractères et numéro d'opération sur les deux derniers caractères"),
		N_DCOD(
				"Nom du dictionnaire des codes (celui qui figure dans le champ D-COD de la fiche d'application)"),
		N_DDON(
				"Nom du dictionnaire des données (celui qui figure dans le champ D-DON de la fiche d'application)"),
		N_STRUCT(
				"Nom du fichier de structures (celui qui figure dans le champ F-STRUCT de la fiche d'application)"),
		NUM_INF(
				"Numéro de l'information décrite (=référence Texto de l'information décrite dans le fichier de structures, établi au début du traitement par les informaticiens et qui donnait la référence de chaque donnée ; par la suite la référence de chaque donnée est devenue son nom symbolique SYMB)"),
		NOM("Nom de la donnée"),
		DEF("Définition de la donnée"),
		E_U("Echelle unité (=hectare par exemple)"),
		CODE("Signification du code le cas échéant (quand E-U est rempli CODE ne l'est pas)"),
		A_DEB("Date de début de validité de la donnée"),
		A_FIN("Date de fin de validité de la donnée"),
		SYMB("Nom symbolique identifiant la donnée (cf NUM-INF)"),
		FICHIER("Nom informatique du fichier contenant la donnée (= celui de FICFICH)"),
		DOC("Documentation associée à la donnée"),
		OBS("Observations éventuelles"),
		REFOLD("Ancienne référence Textow de la donnée");

		public String description;
		public String name;
		public int type;
		public String table;
		public String champ;
		public boolean pk;

		private DDON(String description) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = Types.LONGVARCHAR;
		}

		private DDON(String description, int type) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = type;
		}

		private DDON(String description, boolean pk) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = Types.LONGVARCHAR;
			this.pk = pk;
		}

		private DDON(String description, int type, boolean pk) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = type;
			this.pk = pk;
		}

		private DDON(String description, String table, String champ) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = Types.LONGVARCHAR;
			this.table = table;
			this.champ = champ;
		}

		private DDON(String description, int type, String table, String champ) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = type;
			this.table = table;
			this.champ = champ;
		}


		public static final String getSeparatorArticle() {
			return "/";
		}

		public static final String getSeparatorChamp() {
			return ".";
		}
	}

	/**
	 * DICTIONNAIRES DES DONNEES ELIMINES(ZDONELI) champ clé : NUM ; longueur de clé : 6 ; numérique
	 * ; pas : 1 ; séparateur d'articles : "/" ; séparateur champ-contenu du champ "."
	 * 
	 */
	public static enum ZDONELI {
		NUM("Référence Cindoc de la fiche (= la même, conservée, que la référence de DDON)",
				Types.INTEGER),
		N_APPLID(
				"Numéro et nom de l'application (séparés par un /), tels qu'ils figurent dans la référence cindoc et le champs NOM de APPFICH"),
		NOAPPLID("Numéro de l'application en clair (redondant avec le champ précédent)"),
		N_DCOD(
				"Nom du dictionnaire des codes (celui qui figure dans le champ D-COD de la fiche d'application)"),
		N_DDON(
				"Nom du dictionnaire des données (celui qui figure dans le champ D-DON de la fiche d'application)"),
		N_STRUCT(
				"Nom du fichier de structures (celui qui figure dans le champ F-STRUCT de la fiche d'application)"),
		NUM_INF(
				"Numéro de l'information décrite (=référence Texto de l'information décrite dans le fichier de structures, établi au début du traitement par les informaticiens et qui donnait la référence de chaque donnée ; par la suite la référence de chaque donnée est devenue son nom symbolique SYMB)"),
		NOM("Nom de la donnée"),
		DEF("Définition de la donnée"),
		E_U("Echelle unité (=hectare par exemple)"),
		CODE("Signification du code le cas échéant (quand E-U est rempli CODE ne l'est pas)"),
		A_DEB("Date de début de validité de la donnée"),
		A_FIN("Date de fin de validité de la donnée"),
		SYMB("Nom symbolique identifiant la donnée (cf NUM-INF)"),
		FICHIER("Nom informatique du fichier contenant la donnée (= celui de FICFICH)"),
		DOC("Documentation associée à la donnée"),
		OBS("Observations éventuelles"),
		REFOLD("Ancienne référence Textow de la donnée");

		public String description;
		public String name;
		public int type;
		public String table;
		public String champ;
		public boolean pk;

		private ZDONELI(String description) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = Types.LONGVARCHAR;
		}

		private ZDONELI(String description, int type) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = type;
		}

		private ZDONELI(String description, boolean pk) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = Types.LONGVARCHAR;
			this.pk = pk;
		}

		private ZDONELI(String description, int type, boolean pk) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = type;
			this.pk = pk;
		}

		private ZDONELI(String description, String table, String champ) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = Types.LONGVARCHAR;
			this.table = table;
			this.champ = champ;
		}

		private ZDONELI(String description, int type, String table, String champ) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = type;
			this.table = table;
			this.champ = champ;
		}


		public static final String getSeparatorArticle() {
			return "/";
		}

		public static final String getSeparatorChamp() {
			return ".";
		}
	}

	/**
	 * DICTIONNAIRE DES CODES (DCOD) champ clé : REF ; longueur de clé : 5 ; numérique ; pas : 1 ;
	 * séparateur d'articles : "/" ; séparateur champ-contenu du champ "."
	 * 
	 */
	public static enum DCOD {
		REF("Référence Cindoc de la fiche", Types.INTEGER),
		N_APPLIC(
				"Numéro et nom de l'application (séparés par une ,), tels qu'ils figurent dans la référence cindoc et le champs NOM de APPFICH"),
		N_DCOD(
				"Nom du dictionnaire des codes (celui qui figure dans le champ D-COD de la fiche d'application)"),
		N_DDON(
				"Nom du dictionnaire des données (celui qui figure dans le champ D-DON de la fiche d'application)"),
		NONUDO("Nom de la donnée du dictionnaire des données dont DCOD détaille le code"),
		A_DEB("Date de début de validité de la donnée"),
		A_FIN("Date de fin de validité de la donnée"),
		VAL("Valeur du code"),
		DEF("Définition de la valeur du code"),
		REFSV("Ancienne référence Textow de la donnée"),
		NOAPP("Numéro de l'application en clair (redondant avec le champ N-APPLIC)");

		public String description;
		public String name;
		public int type;
		public String table;
		public String champ;
		public boolean pk;

		private DCOD(String description) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = Types.LONGVARCHAR;
		}

		private DCOD(String description, int type) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = type;
		}

		private DCOD(String description, boolean pk) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = Types.LONGVARCHAR;
			this.pk = pk;
		}

		private DCOD(String description, int type, boolean pk) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = type;
			this.pk = pk;
		}

		private DCOD(String description, String table, String champ) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = Types.LONGVARCHAR;
			this.table = table;
			this.champ = champ;
		}

		private DCOD(String description, int type, String table, String champ) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = type;
			this.table = table;
			this.champ = champ;
		}


		public static final String getSeparatorArticle() {
			return "/";
		}

		public static final String getSeparatorChamp() {
			return ".";
		}
	}

	/**
	 * FICHES DE STRUCTURES (FICSTRUC) champ clé : REF ; longueur de clé : 6 ; numérique ; pas : 1 ;
	 * séparateur d'articles : "," ; séparateur champ-contenu du champ ":"
	 * 
	 */
	public static enum FICSTRUC {
		REF("Référence Cindoc de la fiche", Types.INTEGER),
		N_APPLIS(
				"Numéro et nom de l'application (séparés par une ,), tels qu'ils figurent dans la référence cindoc et le champs NOM de APPFICH"),
		NU_APPLIS("Numéro de l'application en clair (redondant avec le champ précédent)"),
		N_DDON(
				"Nom du dictionnaire des données (celui qui figure dans le champ D-DON de la fiche d'application)"),
		N_FSTRUC(
				"Nom du fichier de structures (celui qui figure dans le champ F-STRUCT de la fiche d'application)"),
		N_FICH("Nom informatique du fichier contenant la donnée (= celui de FICFICH)"),
		NUM_INF(
				"Numéro de l'information décrite (=référence Texto de l'information décrite dans le fichier de structures, établi au début du traitement par les informaticiens et qui donnait la référence de chaque donnée ; par la suite la référence de chaque donnée est devenue son nom symbolique SYMB)"),
		NU_FI("????"),
		SYMB("Nom symbolique identifiant la donnée (cf NUM-INF)"),
		N_STRUCT("Nom en clair de la structure"),
		POS_DEPA("Position de départ de la donnée"),
		LONG("Longueur de la donnée"),
		POS_ARRI("Position d'arrivée de la donnée"),
		TYP_DONN("Type de la donnée (numérique, alpha numérique...)"),
		NOM("Nom en clair de la donnée (normalisé pour DD)"),
		LIBELLE(
				"devenu NOTE (11/2003) Autre champ pour le nom en clair de la donnée (=nom provisoire tiré directement de la documentation de base)"),
		NOTE(
				"sert à désigner les fichiers qui sont en fait de la documentation renseignant les fichiers de données et non pas des fichiers de données"),
		REFANC("Ancienne référence Textow de la donnée");

		public String description;
		public String name;
		public int type;
		public String table;
		public String champ;
		public boolean pk;

		private FICSTRUC(String description) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = Types.LONGVARCHAR;
		}

		private FICSTRUC(String description, int type) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = type;
		}

		private FICSTRUC(String description, boolean pk) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = Types.LONGVARCHAR;
			this.pk = pk;
		}

		private FICSTRUC(String description, int type, boolean pk) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = type;
			this.pk = pk;
		}

		private FICSTRUC(String description, String table, String champ) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = Types.LONGVARCHAR;
			this.table = table;
			this.champ = champ;
		}

		private FICSTRUC(String description, int type, String table, String champ) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = type;
			this.table = table;
			this.champ = champ;
		}


		public static final String getSeparatorArticle() {
			return ",";
		}

		public static final String getSeparatorChamp() {
			return ":";
		}
	}

	/**
	 * NOUVEAU FICFICH juin 2010 : FICHIER DES FICHES TECHNIQUES DES FICHIERS
	 * 
	 */
	public static enum FICFICH {
		y1("Identification"),
		y10("Identifiant AN"),
		x100("NUMERO D'APPLICATION (N-APPLID)"),
		x101("NOM D'APPLICATION (NOMAP)"),
		x102("NUMERO DE VERSEMENT ET ARTICLE (VT)"),
		x103("N1"),
		y11("Identifiant fichier"),
		x110("NOM DU FICHIER VERSE (N-FICHV)"),
		x111("NOM DU FICHIER ARCHIVE (N-FICHA)"),
		x112("NOM EN CLAIR DU FICHIER (NCLAIR)"),
		x113("ANNEE DU FICHIER (ANNEE-FD)"),
		y2("Caractéristiques physiques ou techniques"),
		y20("Caractéristiques physiques ou techniques du  fichier archivé"),
		x200("FORMAT (FORMENRD)"),
		x201("LONGUEUR D'ENREGISTREMENT (LG-ENRD)"),
		x202("NOMBRE D'ENREGISTREMENTS (NB-ENRD)"),
		x203("NOMBRE D'OCTETS (NB-OCTSD)"),
		x204("VERSION D'ARCHIVAGE (Champ nouveau, créé en 2010)"),
		x205("OBSERVATIONS (NOTE)"),
		x206("ORDINATEUR UTILISE A L'ARCHIVAGE (N-ORDID)"),
		x207("SYSTEME D'EXPLOITATION UTILISE A L'ARCHIVAGE (N-SYSTED)"),
		y21("Caractéristiques physiques ou techniques du  fichier versé"),
		x210("SUPPORT DE VERSEMENT (CLESTRIT)"),
		x211("ORDINATEUR UTILISE AU VERSEMENT (N-ORDIT)"),
		x212("SYSTEME D'EXPLOITATION UTILISE AU VERSEMENT (N-SYSTET)"),
		y3("Conservation physique"),
		y30("Conservation physique du fichier archivé"),
		x300(
				"6EME SUPPORT HISTORIQUEMENT 2010 A NNNN REFERENCE DE LA CASSETTE LTOIV D'ARCHIVAGE (Champ nouveau, créé en 2010)"),
		x301(
				"6EME SUPPORT HISTORIQUEMENT 2010 A NNNN DATE D'ARCHIVAGE SUR LA CASSETTE LTOIV (Champ nouveau, créé en 2010)"),
		x302(
				"6EME SUPPORT HISTORIQUEMENT 2010 A NNNN REFERENCE DE LA CASSETTE LTOIV DE SECURITE (Champ nouveau, créé en 2010)"),
		x303(
				"6EME SUPPORT HISTORIQUEMENT 2010 A NNNN DATE D'ARCHIVAGE SUR LA CASSETTE LTOIV DE SECURITE (Champ nouveau, créé en 2010)"),
		y31("Conservation physique du fichier versé"),
		x310(
				"6EME SUPPORT HISTORIQUEMENT 2010 A NNNN REFERENCE DE LA CASSETTE LTOIV D'ARCHIVAGE (Champ nouveau, créé en 2010)"),
		x311(
				"6EME SUPPORT HISTORIQUEMENT 2010 A NNNN DATE D'ARCHIVAGE SUR LA CASSETTE LTOIV (Champ nouveau, créé en 2010)"),
		x312(
				"6EME SUPPORT HISTORIQUEMENT 2010 A NNNN REFERENCE DE LA CASSETTE LTOIV DE SECURITE (Champ nouveau, créé en 2010)"),
		x313(
				"6EME SUPPORT HISTORIQUEMENT 2010 A NNNN DATE D'ARCHIVAGE SUR LA CASSETTE LTOIV DE SECURITE (Champ nouveau, créé en 2010)"),
		y4("Traitements pour conservation définitive (traçabilité)"),
		y40("Conversion 1"),
		x400("DATE (Champ nouveau, créé en 2010)"),
		x401("NOM DU FICHIER A CONVERTIR"),
		y402("Caractéristiques du fichier avant conversion"),
		x4020("FORMAT (Champ nouveau, créé en 2010)"),
		x4021("VERSION DU FORMAT (Champ nouveau, créé en 2010)"),
		x4022("TAILLE (Champ nouveau, créé en 2010)"),
		x403("OUTIL UTILISE POUR LA CONVERSION (Champ nouveau, créé en 2010)"),
		x404("VERSION DE L'OUTIL UTILISE POUR LA CONVERSION (Champ nouveau, créé en 2010)"),
		x405(
				"OBSERVATIONS PROBLEMES RENCONTRES LORS DE LA CONVERSION (Champ nouveau, créé en 2010)"),
		y406("Caractéristiques du fichier après conversion"),
		x4060("NOUVEAU FORMAT (Champ nouveau, créé en 2010)"),
		x4061("NOUVELLE VERSION DU FORMAT (Champ nouveau, créé en 2010)"),
		x4062("NOUVELLE TAILLE (Champ nouveau, créé en 2010)"),
		y5("Historique de la conservation"),
		y50("1er support historiquement 1983 à 1997"),
		x500("NOM DU SUPPORT D'ARCHIVAGE (N-VOLUME)"),
		x501("NOM DU SUPPORT DE SECURITE (N-SECURI)"),
		y502("régénération"),
		x5020("DATE DE REGENERATION 1 (D-REGV1)"),
		x5021("DATE DE REGENERATION 2 (D-REGV2)"),
		x5022("DATE DE REGENERATION 3 (D-REGV3)"),
		x503("DENSITE (DENSITE)"),
		x504("LABEL (LABEL)"),
		y505("Enregistrements"),
		x5050("FORMAT (FORMENRT)"),
		x5051("LONGUEUR (LG-ENRT)"),
		x5052("NOMBRE (NB-ENRT)"),
		y506("blocs"),
		x5060("LONGUEUR (LG-BLK)"),
		x5061("NOMBRE DE BLOCS (NB-BLKS)"),
		x507("TAILLE (NB-OCTST)"),
		y51("2ème support historiquement 1997-1997"),
		x510("NOM DU SUPPORT (FACEDON)"),
		x511("DATE D'ARCHIVAGE (DATEDON)"),
		y52("3ème support historiquement 1997-fin 2001"),
		x520("NOM DU SUPPORT D'ARCHIVAGE (DLT-A)"),
		x521("DATE D'ARCHIVAGE (DATEDLTA)"),
		x522("NOM DU SUPPORT DE SECURITE (DLT-B)"),
		x523("DATE D'ARCHIVAGE DE SECURITE(DATEDLTB)"),
		x524("NUMERO DES SEGMENTS D'ARCHIVAGE DU FICHIER (DLT-ASEG)"),
		x525("NOM PHYSIQUE DES SECTIONS D'UN FICHIER DECOUPE (DECOUP)"),
		x526("NOMBRE D'OCTETS D'UN FICHIER DECOUPE (NB-OCTDE)"),
		x527("NOMBRE D'ENREGISTREMENTS D'UN FICHIER DECOUPE (NB-ENRDE)"),
		y53("4ème support historiquement 2002-2005"),
		x530("NOM DU SUPPORT D'ARCHIVAGE (DLTA)"),
		x531("DATE D'ARCHIVAGE (DLTAD)"),
		x532("NOM DU SUPPORT DE SECURITE (DLTB)"),
		x533("DATE D'ARCHIVAGE DE SECURITE (DLTBD)"),
		x534("NOM DU SUPPORT VDAT DE SECURITE (VDAT)"),
		x535("DATE D'ARCHIVAGE VDAT DE SECURITE (VDATD)"),
		y54("5ème support historiquement 2005-2010"),
		x540("NOM DU SUPPORT D'ARCHIVAGE (SDLTA)"),
		x541("DATE D'ARCHIVAGE (SDLTAD)"),
		x542("NOM DU SUPPORT DE SECURITE (SDLTB)"),
		x543("DATE D'ARCHIVAGE DE SECURITE (SDLTBD)"),
		y6("Gestion interne des bases"),
		x60(
				"DATE DE MISE A JOUR DE LA FICHE ; N.B. :CHAMPS SUPPRIMES EN 2010 : REFFICH, ART, DD, DC, ST,CLESTRI, PROG"),
		x61("ANCIENNE REFERENCE DE FICFICH AVANT MIGRATION 2002 (REFFICH)"),
		x62("ANCIENNE REFERENCE DE FICFICH AVANT MIGRATION 2010 (REFD)");

		public String description;
		public String name;
		public int type;
		public String table;
		public String champ;
		public boolean pk;

		private FICFICH(String description) {
			this.name = this.name().replaceFirst("[x]", "");
			this.description = description;
			this.type = Types.LONGVARCHAR;
		}

		private FICFICH(String description, int type) {
			this.name = this.name().replaceFirst("[x]", "");
			this.description = description;
			this.type = type;
		}

		private FICFICH(String description, boolean pk) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = Types.LONGVARCHAR;
			this.pk = pk;
		}

		private FICFICH(String description, int type, boolean pk) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = type;
			this.pk = pk;
		}

		private FICFICH(String description, String table, String champ) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = Types.LONGVARCHAR;
			this.table = table;
			this.champ = champ;
		}

		private FICFICH(String description, int type, String table, String champ) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = type;
			this.table = table;
			this.champ = champ;
		}


		public static final String getSeparatorArticle() {
			return "/";
		}

		public static final String getSeparatorChamp() {
			return ".";
		}
	}

	/**
	 * NOUVEAU ZFIFIC juin 2010 (FICHIER DES FICHES TECHNIQUES DES FICHIERS ÉLIMINÉS)
	 * 
	 */
	public static enum ZFIFIC {
		y1("Identification"),
		y10("Identifiant AN"),
		x100("NUMERO D'APPLICATION (N-APPLID)"),
		x101("NOM D'APPLICATION (NOMAP)"),
		x102("NUMERO DE VERSEMENT ET ARTICLE (VT)"),
		x103("N1"),
		y11("Identifiant fichier"),
		x110("NOM DU FICHIER VERSE (N-FICHV)"),
		x111("NOM DU FICHIER ARCHIVE (N-FICHA)"),
		x112("NOM EN CLAIR DU FICHIER (NCLAIR)"),
		x113("ANNEE DU FICHIER (ANNEE-FD)"),
		y2("Caractéristiques physiques ou techniques"),
		y20("Caractéristiques physiques ou techniques du  fichier archivé"),
		x200("FORMAT (FORMENRD)"),
		x201("LONGUEUR D'ENREGISTREMENT (LG-ENRD)"),
		x202("NOMBRE D'ENREGISTREMENTS (NB-ENRD)"),
		x203("NOMBRE D'OCTETS (NB-OCTSD)"),
		x204("VERSION D'ARCHIVAGE (Champ nouveau, créé en 2010)"),
		x205("OBSERVATIONS (NOTE)"),
		x206("ORDINATEUR UTILISE A L'ARCHIVAGE (N-ORDID)"),
		x207("SYSTEME D'EXPLOITATION UTILISE A L'ARCHIVAGE (N-SYSTED)"),
		y21("Caractéristiques physiques ou techniques du  fichier versé"),
		x210("SUPPORT DE VERSEMENT (CLESTRIT)"),
		x211("ORDINATEUR UTILISE AU VERSEMENT (N-ORDIT)"),
		x212("SYSTEME D'EXPLOITATION UTILISE AU VERSEMENT (N-SYSTET)"),
		y3("Conservation physique"),
		y30("Conservation physique du fichier archivé"),
		x300(
				"6EME SUPPORT HISTORIQUEMENT 2010 A NNNN REFERENCE DE LA CASSETTE LTOIV D'ARCHIVAGE (Champ nouveau, créé en 2010)"),
		x301(
				"6EME SUPPORT HISTORIQUEMENT 2010 A NNNN DATE D'ARCHIVAGE SUR LA CASSETTE LTOIV (Champ nouveau, créé en 2010)"),
		x302(
				"6EME SUPPORT HISTORIQUEMENT 2010 A NNNN REFERENCE DE LA CASSETTE LTOIV DE SECURITE (Champ nouveau, créé en 2010)"),
		x303(
				"6EME SUPPORT HISTORIQUEMENT 2010 A NNNN DATE D'ARCHIVAGE SUR LA CASSETTE LTOIV DE SECURITE (Champ nouveau, créé en 2010)"),
		y31("Conservation physique du fichier versé"),
		x310(
				"6EME SUPPORT HISTORIQUEMENT 2010 A NNNN REFERENCE DE LA CASSETTE LTOIV D'ARCHIVAGE (Champ nouveau, créé en 2010)"),
		x311(
				"6EME SUPPORT HISTORIQUEMENT 2010 A NNNN DATE D'ARCHIVAGE SUR LA CASSETTE LTOIV (Champ nouveau, créé en 2010)"),
		x312(
				"6EME SUPPORT HISTORIQUEMENT 2010 A NNNN REFERENCE DE LA CASSETTE LTOIV DE SECURITE (Champ nouveau, créé en 2010)"),
		x313(
				"6EME SUPPORT HISTORIQUEMENT 2010 A NNNN DATE D'ARCHIVAGE SUR LA CASSETTE LTOIV DE SECURITE (Champ nouveau, créé en 2010)"),
		y4("Traitements pour conservation définitive (traçabilité)"),
		y40("Conversion 1"),
		x400("DATE (Champ nouveau, créé en 2010)"),
		x401("NOM DU FICHIER A CONVERTIR"),
		y402("Caractéristiques du fichier avant conversion"),
		x4020("FORMAT (Champ nouveau, créé en 2010)"),
		x4021("VERSION DU FORMAT (Champ nouveau, créé en 2010)"),
		x4022("TAILLE (Champ nouveau, créé en 2010)"),
		x403("OUTIL UTILISE POUR LA CONVERSION (Champ nouveau, créé en 2010)"),
		x404("VERSION DE L'OUTIL UTILISE POUR LA CONVERSION (Champ nouveau, créé en 2010)"),
		x405(
				"OBSERVATIONS PROBLEMES RENCONTRES LORS DE LA CONVERSION (Champ nouveau, créé en 2010)"),
		y406("Caractéristiques du fichier après conversion"),
		x4060("NOUVEAU FORMAT (Champ nouveau, créé en 2010)"),
		x4061("NOUVELLE VERSION DU FORMAT (Champ nouveau, créé en 2010)"),
		x4062("NOUVELLE TAILLE (Champ nouveau, créé en 2010)"),
		y5("Historique de la conservation"),
		y50("1er support historiquement 1983 à 1997"),
		x500("NOM DU SUPPORT D'ARCHIVAGE (N-VOLUME)"),
		x501("NOM DU SUPPORT DE SECURITE (N-SECURI)"),
		y502("régénération"),
		x5020("DATE DE REGENERATION 1 (D-REGV1)"),
		x5021("DATE DE REGENERATION 2 (D-REGV2)"),
		x5022("DATE DE REGENERATION 3 (D-REGV3)"),
		x503("DENSITE (DENSITE)"),
		x504("LABEL (LABEL)"),
		y505("Enregistrements"),
		x5050("FORMAT (FORMENRT)"),
		x5051("LONGUEUR (LG-ENRT)"),
		x5052("NOMBRE (NB-ENRT)"),
		y506("blocs"),
		x5060("LONGUEUR (LG-BLK)"),
		x5061("NOMBRE DE BLOCS (NB-BLKS)"),
		x507("TAILLE (NB-OCTST)"),
		y51("2ème support historiquement 1997-1997"),
		x510("NOM DU SUPPORT (FACEDON)"),
		x511("DATE D'ARCHIVAGE (DATEDON)"),
		y52("3ème support historiquement 1997-fin 2001"),
		x520("NOM DU SUPPORT D'ARCHIVAGE (DLT-A)"),
		x521("DATE D'ARCHIVAGE (DATEDLTA)"),
		x522("NOM DU SUPPORT DE SECURITE (DLT-B)"),
		x523("DATE D'ARCHIVAGE DE SECURITE(DATEDLTB)"),
		x524("NUMERO DES SEGMENTS D'ARCHIVAGE DU FICHIER (DLT-ASEG)"),
		x525("NOM PHYSIQUE DES SECTIONS D'UN FICHIER DECOUPE (DECOUP)"),
		x526("NOMBRE D'OCTETS D'UN FICHIER DECOUPE (NB-OCTDE)"),
		x527("NOMBRE D'ENREGISTREMENTS D'UN FICHIER DECOUPE (NB-ENRDE)"),
		y53("4ème support historiquement 2002-2005"),
		x530("NOM DU SUPPORT D'ARCHIVAGE (DLTA)"),
		x531("DATE D'ARCHIVAGE (DLTAD)"),
		x532("NOM DU SUPPORT DE SECURITE (DLTB)"),
		x533("DATE D'ARCHIVAGE DE SECURITE (DLTBD)"),
		x534("NOM DU SUPPORT VDAT DE SECURITE (VDAT)"),
		x535("DATE D'ARCHIVAGE VDAT DE SECURITE (VDATD)"),
		y54("5ème support historiquement 2005-2010"),
		x540("NOM DU SUPPORT D'ARCHIVAGE (SDLTA)"),
		x541("DATE D'ARCHIVAGE (SDLTAD)"),
		x542("NOM DU SUPPORT DE SECURITE (SDLTB)"),
		x543("DATE D'ARCHIVAGE DE SECURITE (SDLTBD)"),
		y6("Gestion interne des bases"),
		x60(
				"DATE DE MISE A JOUR DE LA FICHE ; N.B. :CHAMPS SUPPRIMES EN 2010 : REFFICH, ART, DD, DC, ST,CLESTRI, PROG"),
		x61("ANCIENNE REFERENCE DE FICFICH AVANT MIGRATION 2002 (REFFICH)"),
		x62("ANCIENNE REFERENCE DE FICFICH AVANT MIGRATION 2010 (REFD)"),
		y7("Elimination"),
		x70("DATE D'ELIMINATION"),
		x71("MOTIF DE L'ELIMINATION");

		public String description;
		public String name;
		public int type;
		public String table;
		public String champ;
		public boolean pk;

		private ZFIFIC(String description) {
			this.name = this.name().replaceFirst("[x]", "");
			this.description = description;
			this.type = Types.LONGVARCHAR;
		}

		private ZFIFIC(String description, int type) {
			this.name = this.name().replaceFirst("[x]", "");
			this.description = description;
			this.type = type;
		}

		private ZFIFIC(String description, boolean pk) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = Types.LONGVARCHAR;
			this.pk = pk;
		}

		private ZFIFIC(String description, int type, boolean pk) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = type;
			this.pk = pk;
		}

		private ZFIFIC(String description, String table, String champ) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = Types.LONGVARCHAR;
			this.table = table;
			this.champ = champ;
		}

		private ZFIFIC(String description, int type, String table, String champ) {
			this.name = this.name().replace("_", "-");
			this.description = description;
			this.type = type;
			this.table = table;
			this.champ = champ;
		}


		public static final String getSeparatorArticle() {
			return "/";
		}

		public static final String getSeparatorChamp() {
			return ".";
		}
	}
	
	public static List<File> extractCindocStruct(File dst) throws IOException {
		ConstanceFormat []cchamps = ConstanceFormat.values();
		String header = "";
		for (ConstanceFormat constanceFormat : cchamps) {
			header += constanceFormat.getName() + "\t";
		}
		header = header.substring(0, header.length()-1);
		byte []bheader = header.getBytes();
		byte []crlf = "\r\n".getBytes();
		List<File> files = new ArrayList<File>();
		File file = extractAPPFICH(dst, crlf, bheader, cchamps.length);
		if (file != null) {
			files.add(file);
		}
		file = extractZAPELIM(dst, crlf, bheader, cchamps.length);
		if (file != null) {
			files.add(file);
		}
		file = extractDOCUMENTATION(dst, crlf, bheader, cchamps.length);
		if (file != null) {
			files.add(file);
		}
		file = extractOP(dst, crlf, bheader, cchamps.length);
		if (file != null) {
			files.add(file);
		}
		file = extractZFICELI(dst, crlf, bheader, cchamps.length);
		if (file != null) {
			files.add(file);
		}
		file = extractDDON(dst, crlf, bheader, cchamps.length);
		if (file != null) {
			files.add(file);
		}
		file = extractZDONELI(dst, crlf, bheader, cchamps.length);
		if (file != null) {
			files.add(file);
		}
		file = extractDCOD(dst, crlf, bheader, cchamps.length);
		if (file != null) {
			files.add(file);
		}
		file = extractFICSTRUC(dst, crlf, bheader, cchamps.length);
		if (file != null) {
			files.add(file);
		}
		file = extractFICFICH(dst, crlf, bheader, cchamps.length);
		if (file != null) {
			files.add(file);
		}
		file = extractZFIFIC(dst, crlf, bheader, cchamps.length);
		if (file != null) {
			files.add(file);
		}
		return files;
	}
	
	public static DbSchema createSchema(File current_file, ConfigLoader config, String tempTablePrefix) {
		ConstanceIdentifier identifier = new ConstanceIdentifier(current_file, 
				config.separator, tempTablePrefix);
		DbVitam2Database database = null;
		DbSchema schema = null;
		try {
			String jdbcPosition = config.databasePosition;
			if (config.databaseType.equals(StaticValues.TYPEH2)) {
				int pos = jdbcPosition.indexOf(".h2.db");
				if (pos > 0) {
					jdbcPosition = jdbcPosition.substring(0, pos);
				}
			}
			String jdbc = config.databaseJDBC_Start+
					jdbcPosition+
					config.databaseJDBC_Option;
			System.out.println("Connect: " + jdbc);
			database = new DbVitam2Database(config.databaseType, 
						jdbc, 
						config.databaseUser, 
						config.databasePassword);
			identifier.loadTechnicalDescription();
			identifier.printStructure();
			schema = identifier.getSimpleSchema();
			schema.createBuildOrder();
			schema.printOrder();
			database.dropDatabases(schema);
			database.createDatabases(schema);
			database.fillDatabases(schema, identifier);
			System.out
					.println(StaticValues.LBL.action_import.get());
			return schema;
		} catch (IOException e) {
			logger.warn(StaticValues.LBL.error_error.get() + e);
		} catch (WaarpDatabaseSqlException e) {
			logger.warn(StaticValues.LBL.error_error.get() + e);
		} catch (WaarpDatabaseException e) {
			logger.warn(StaticValues.LBL.error_error.get() + e);
		} finally {
			if (database != null) {
				database.close();
			}
		}
		return schema;
	}

	private static String[] getInfo(int cchamps, String data, String name, int rank, int type,
			String table, String champ, boolean pk) {
		// N_FICH, SYMB, POS_DEPA, LONG, POS_ARRI, TYP_DONN, NUM_INF, REF_TABLE, REF_CHAMP, ISPK
		String [] fields = new String[cchamps];
		// N_FICH
		fields[0] = data;
		// SYMB
		fields[1] = name;
		// POS_DEPA
		fields[2] = Integer.toString(rank);
		// LONG
		fields[3] = "";
		// POS_ARRI
		fields[4] = "";
		// TYP_DONN
		switch (type) {
			case Types.BIGINT:
			case Types.INTEGER:
				fields[5] = ConstanceDataType.NUM.name;
				break;
			default:
				fields[5] = ConstanceDataType.AN.name;
		}
		// NUM_INF
		fields[6] = "";
		if (table == null) {
			// REF_TABLE
			fields[7] = "";
			// REF_CHAMP
			fields[8] = "";
		} else {
			// REF_TABLE
			fields[7] = table;
			// REF_CHAMP
			fields[8] = champ;
		}
		fields[9] = pk ? "1" : "0";
		return fields;
	}
	
	public static final File extractAPPFICH(File dst, byte []crlf, byte []header, int cchamps) throws IOException {
		File file = new File(dst, "APPFICH.struct.txt");
		FileOutputStream out = new FileOutputStream(file);
		try {
			out.write(header);
			out.write(crlf);
			int rank = 0;
			String data = "APPFICH_Data.csv";
			for (APPFICH champ : APPFICH.values()) {
				rank++;
				String [] fields = getInfo(cchamps, data, champ.name(), rank, 
						champ.type, champ.table, champ.champ, champ.pk);
				ConstanceField field = new ConstanceField(fields);
				out.write(field.getTabFormat().getBytes());
				out.write(crlf);
			}
			return file;
		} finally {
			out.close();
		}
	}

	public static final File extractZAPELIM(File dst, byte []crlf, byte []header, int cchamps) throws IOException {
		File file = new File(dst, "ZAPELIM.struct.txt");
		FileOutputStream out = new FileOutputStream(file);
		try {
			out.write(header);
			out.write(crlf);
			int rank = 0;
			String data = "ZAPELIM_Data.csv";
			for (ZAPELIM champ : ZAPELIM.values()) {
				rank++;
				String [] fields = getInfo(cchamps, data, champ.name(), rank, 
						champ.type, champ.table, champ.champ, champ.pk);
				ConstanceField field = new ConstanceField(fields);
				out.write(field.getTabFormat().getBytes());
				out.write(crlf);
			}
			return file;
		} finally {
			out.close();
		}
	}

	public static final File extractDOCUMENTATION(File dst, byte []crlf, byte []header, int cchamps) throws IOException {
		File file = new File(dst, "DOCUMENTATION.struct.txt");
		FileOutputStream out = new FileOutputStream(file);
		try {
			out.write(header);
			out.write(crlf);
			int rank = 0;
			String data = "DOCUMENTATION_Data.csv";
			for (DOCUMENTATION champ : DOCUMENTATION.values()) {
				rank++;
				String [] fields = getInfo(cchamps, data, champ.name(), rank, 
						champ.type, champ.table, champ.champ, champ.pk);
				ConstanceField field = new ConstanceField(fields);
				out.write(field.getTabFormat().getBytes());
				out.write(crlf);
			}
			return file;
		} finally {
			out.close();
		}
	}

	public static final File extractOP(File dst, byte []crlf, byte []header, int cchamps) throws IOException {
		File file = new File(dst, "OP.struct.txt");
		FileOutputStream out = new FileOutputStream(file);
		try {
			out.write(header);
			out.write(crlf);
			int rank = 0;
			String data = "OP_Data.csv";
			for (OP champ : OP.values()) {
				rank++;
				String [] fields = getInfo(cchamps, data, champ.name(), rank, 
						champ.type, champ.table, champ.champ, champ.pk);
				ConstanceField field = new ConstanceField(fields);
				out.write(field.getTabFormat().getBytes());
				out.write(crlf);
			}
			return file;
		} finally {
			out.close();
		}
	}

	public static final File extractZFICELI(File dst, byte []crlf, byte []header, int cchamps) throws IOException {
		File file = new File(dst, "ZFICELI.struct.txt");
		FileOutputStream out = new FileOutputStream(file);
		try {
			out.write(header);
			out.write(crlf);
			int rank = 0;
			String data = "ZFICELI_Data.csv";
			for (ZFICELI champ : ZFICELI.values()) {
				rank++;
				String [] fields = getInfo(cchamps, data, champ.name(), rank, 
						champ.type, champ.table, champ.champ, champ.pk);
				ConstanceField field = new ConstanceField(fields);
				out.write(field.getTabFormat().getBytes());
				out.write(crlf);
			}
			return file;
		} finally {
			out.close();
		}
	}

	public static final File extractDDON(File dst, byte []crlf, byte []header, int cchamps) throws IOException {
		File file = new File(dst, "DDON.struct.txt");
		FileOutputStream out = new FileOutputStream(file);
		try {
			out.write(header);
			out.write(crlf);
			int rank = 0;
			String data = "DDON_Data.csv";
			for (DDON champ : DDON.values()) {
				rank++;
				String [] fields = getInfo(cchamps, data, champ.name(), rank, 
						champ.type, champ.table, champ.champ, champ.pk);
				ConstanceField field = new ConstanceField(fields);
				out.write(field.getTabFormat().getBytes());
				out.write(crlf);
			}
			return file;
		} finally {
			out.close();
		}
	}

	public static final File extractZDONELI(File dst, byte []crlf, byte []header, int cchamps) throws IOException {
		File file = new File(dst, "ZDONELI.struct.txt");
		FileOutputStream out = new FileOutputStream(file);
		try {
			out.write(header);
			out.write(crlf);
			int rank = 0;
			String data = "ZDONELI_Data.csv";
			for (ZDONELI champ : ZDONELI.values()) {
				rank++;
				String [] fields = getInfo(cchamps, data, champ.name(), rank, 
						champ.type, champ.table, champ.champ, champ.pk);
				ConstanceField field = new ConstanceField(fields);
				out.write(field.getTabFormat().getBytes());
				out.write(crlf);
			}
			return file;
		} finally {
			out.close();
		}
	}

	public static final File extractDCOD(File dst, byte []crlf, byte []header, int cchamps) throws IOException {
		File file = new File(dst, "DCOD.struct.txt");
		FileOutputStream out = new FileOutputStream(file);
		try {
			out.write(header);
			out.write(crlf);
			int rank = 0;
			String data = "DCOD_Data.csv";
			for (DCOD champ : DCOD.values()) {
				rank++;
				String [] fields = getInfo(cchamps, data, champ.name(), rank, 
						champ.type, champ.table, champ.champ, champ.pk);
				ConstanceField field = new ConstanceField(fields);
				out.write(field.getTabFormat().getBytes());
				out.write(crlf);
			}
			return file;
		} finally {
			out.close();
		}
	}

	public static final File extractFICSTRUC(File dst, byte []crlf, byte []header, int cchamps) throws IOException {
		File file = new File(dst, "FICSTRUC.struct.txt");
		FileOutputStream out = new FileOutputStream(file);
		try {
			out.write(header);
			out.write(crlf);
			int rank = 0;
			String data = "FICSTRUC_Data.csv";
			for (FICSTRUC champ : FICSTRUC.values()) {
				rank++;
				String [] fields = getInfo(cchamps, data, champ.name(), rank, 
						champ.type, champ.table, champ.champ, champ.pk);
				ConstanceField field = new ConstanceField(fields);
				out.write(field.getTabFormat().getBytes());
				out.write(crlf);
			}
			return file;
		} finally {
			out.close();
		}
	}

	public static final File extractFICFICH(File dst, byte []crlf, byte []header, int cchamps) throws IOException {
		File file = new File(dst, "FICFICH.struct.txt");
		FileOutputStream out = new FileOutputStream(file);
		try {
			out.write(header);
			out.write(crlf);
			int rank = 0;
			String data = "FICFICH_Data.csv";
			for (FICFICH champ : FICFICH.values()) {
				if (champ.name.startsWith("y")) {
					// ignore
					continue;
				}
				rank++;
				String [] fields = getInfo(cchamps, data, champ.name(), rank, 
						champ.type, champ.table, champ.champ, champ.pk);
				ConstanceField field = new ConstanceField(fields);
				out.write(field.getTabFormat().getBytes());
				out.write(crlf);
			}
			return file;
		} finally {
			out.close();
		}
	}

	public static final File extractZFIFIC(File dst, byte []crlf, byte []header, int cchamps) throws IOException {
		File file = new File(dst, "ZFIFIC.struct.txt");
		FileOutputStream out = new FileOutputStream(file);
		try {
			out.write(header);
			out.write(crlf);
			int rank = 0;
			String data = "ZFIFIC_Data.csv";
			for (ZFIFIC champ : ZFIFIC.values()) {
				if (champ.name.startsWith("y")) {
					// ignore
					continue;
				}
				rank++;
				String [] fields = getInfo(cchamps, data, champ.name(), rank, 
						champ.type, champ.table, champ.champ, champ.pk);
				ConstanceField field = new ConstanceField(fields);
				out.write(field.getTabFormat().getBytes());
				out.write(crlf);
			}
			return file;
		} finally {
			out.close();
		}
	}
	public static void exportCleanXml(File xml) {
		SAXReader reader = new SAXReader();
        Document document;
		try {
			document = reader.read(xml);
		} catch (DocumentException e) {
			logger.warn(StaticValues.LBL.error_error.get() + e);
			return;
		}
		DbSchema schema = DbSchema.buildFromXml(xml, document.getRootElement());
		System.out
				.println(StaticValues.LBL.action_import.get());
		XmlWriter writer = new XmlWriter(xml, "export");
		writer.add(schema);
		try {
			writer.write();
			System.out
					.println(StaticValues.LBL.action_export.get());
		} catch (IOException e) {
			logger.warn(StaticValues.LBL.error_error.get() + e);
		}
	}

	public static void main(String[] args) {
		WaarpInternalLoggerFactory.setDefaultFactory(new WaarpSlf4JLoggerFactory(Level.WARN));
		logger = WaarpInternalLoggerFactory.getLogger(ConstanceCindoc.class);
		if (args.length <= 0) {
			logger.warn("destination nécessaire");
			return;
		}
		StaticValues.initialize();
		File dst = new File(args[0]);
		try {
			List<File> files = extractCindocStruct(dst);
			// XXX FIXME : structure in order of dependencies ?
			// build schema but order needed
			File xml = new File(dst, "ConstanceCindoc_export.xml");
			XmlWriter writer = new XmlWriter(xml, "export");
			for (File file : files) {
				String filename = file.getName();
				int pos = filename.indexOf('.');
				String name = "CONSTANCE_" + filename.substring(0, pos);
				DbSchema schema = createSchema(file, StaticValues.config, name);
				writer.add(schema);
			}
			try {
				writer.write();
				System.out
						.println(StaticValues.LBL.action_export.get());
			} catch (IOException e) {
				logger.warn(StaticValues.LBL.error_error.get() + e);
			}
			exportCleanXml(xml);
		} catch (IOException e) {
			logger.warn(StaticValues.LBL.error_error.get() + e);
		}
	}
	
}
