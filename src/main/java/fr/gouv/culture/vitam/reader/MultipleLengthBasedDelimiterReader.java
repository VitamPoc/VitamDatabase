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
package fr.gouv.culture.vitam.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.waarp.common.logging.WaarpInternalLogger;
import org.waarp.common.logging.WaarpInternalLoggerFactory;

/**
 * @author "Frederic Bregier"
 * 
 */
public class MultipleLengthBasedDelimiterReader implements VitamReader {
	/**
	 * Internal Logger
	 */
	private static final WaarpInternalLogger logger = WaarpInternalLoggerFactory
			.getLogger(MultipleLengthBasedDelimiterReader.class);
	
	public File file;
	public int[][] limits;
	private FileInputStream in;
	private BufferedReader reader;
	private int linecpt = 0;

	/**
	 * @param file
	 * @param limits
	 * @throws FileNotFoundException
	 */
	public MultipleLengthBasedDelimiterReader(File file, int[][] limits)
			throws FileNotFoundException {
		this.file = file;
		this.limits = limits;

		this.in = new FileInputStream(file);
		this.reader = new BufferedReader(new InputStreamReader(in));
	}

	@Override
	public String[] readOneLine() throws IOException {
		if (this.reader != null) {
			String line = reader.readLine();
			linecpt++;
			if (line == null) {
				reader.close();
				in.close();
				reader = null;
				in = null;
				return null;
			}
			int rank = -1;
			int size = line.length();
			for (int i = 0; i < limits.length; i++) {
				if (limits[i][limits[i].length - 1] == size) {
					rank = i;
					break;
				}
			}
			if (rank < 0) {
				logger.warn("Enregistrement non reconnu en ligne: " + linecpt);
				return null;
			}
			String[] result = new String[limits[rank].length + 1];
			result[0] = Integer.toString(rank);
			int start = 0;
			for (int i = 0; i < limits[rank].length; i++) {
				result[i + 1] = line.substring(start, limits[rank][i]);
				start = limits[rank][i];
			}
			return result;
		}
		return null;
	}

	@Override
	public String[] readOneLine(int rank) throws IOException {
		if (this.reader != null) {
			String line = reader.readLine();
			linecpt++;
			if (line == null) {
				reader.close();
				in.close();
				reader = null;
				in = null;
				return null;
			}
			while (true) {
				int size = line.length();
				if (limits[rank][limits[rank].length - 1] != size) {
					line = reader.readLine();
					linecpt++;
					if (line == null) {
						reader.close();
						in.close();
						reader = null;
						in = null;
						return null;
					}
					continue;
				}
				String[] result = new String[limits[rank].length + 1];
				result[0] = Integer.toString(rank);
				int start = 0;
				for (int i = 0; i < limits[rank].length; i++) {
					result[i + 1] = line.substring(start, limits[rank][i]);
					start = limits[rank][i];
				}
				return result;
			}
		}
		return null;
	}

}
