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

/**
 * CHamps Constance du fichier structure
 *  
 * @author "Frederic Bregier"
 * 
 */
public class ConstanceField {
	public String N_FICH;
	public String SYMB;
	public int POS_DEPA = -1;
	public int LONG = -1;
	public int POS_ARRI = -1;
	public String TYP_DONN;
	public int NUM_INF = -1;
	public String REF_TABLE;
	public String REF_CHAMP;
	public boolean ISPK;

	/**
	 * @param fields
	 */
	public ConstanceField(String[] fields) {
		switch (fields.length) {
			case 10:
				if (fields[9] != null && fields[9].length() > 0) {
					ISPK = fields[9].equals("1");
				} else {
					ISPK = false;
				}
			case 9:
				REF_CHAMP = fields[8];
			case 8:
				REF_TABLE = fields[7];
			case 7:
				if (fields[6] != null && fields[6].trim().length() > 0) {
					NUM_INF = Integer.parseInt(fields[6]);
				} else {
					NUM_INF = -1;
				}
			case 6:
				TYP_DONN = fields[5];
			case 5:
				if (fields[4] != null && fields[4].trim().length() > 0) {
					POS_ARRI = Integer.parseInt(fields[4]);
				} else {
					POS_ARRI = -1;
				}
			case 4:
				if (fields[3] != null && fields[3].trim().length() > 0) {
					LONG = Integer.parseInt(fields[3]);
				} else {
					LONG = -1;
				}
			case 3:
				if (fields[2] != null && fields[2].trim().length() > 0) {
					POS_DEPA = Integer.parseInt(fields[2]);
				} else {
					POS_DEPA = -1;
				}
			case 2:
				SYMB = fields[1];
			case 1:
				N_FICH = fields[0];
		}
	}

	public String toString() {
		if (REF_TABLE != null) {
			return N_FICH + "," + SYMB + "," + POS_DEPA + "," + LONG + "," + POS_ARRI + "," + TYP_DONN
					+ "," + NUM_INF + "," + REF_TABLE + "," + REF_CHAMP + "," + (ISPK ? "1" : "0");
		}
		return N_FICH + "," + SYMB + "," + POS_DEPA + "," + LONG + "," + POS_ARRI + "," + TYP_DONN
				+ "," + NUM_INF;
	}
	public String getTabFormat() {
		if (REF_TABLE != null) {
			return N_FICH + "\t" + SYMB + "\t" + POS_DEPA + "\t" + LONG + "\t" + POS_ARRI + "\t" + TYP_DONN
					+ "\t" + NUM_INF + "\t" + REF_TABLE + "\t" + REF_CHAMP + "\t" + (ISPK ? "1" : "0");
		}
		return N_FICH + "\t" + SYMB + "\t" + POS_DEPA + "\t" + LONG + "\t" + POS_ARRI + "\t" + TYP_DONN
				+ "\t" + NUM_INF;
	}
}
