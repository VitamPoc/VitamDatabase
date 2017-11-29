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

/**
 * @author "Frederic Bregier"
 * 
 */
public class SimpleDelimiterReader implements VitamReader {
	public File file;
	public String separator;
	private FileInputStream in;
	private BufferedReader reader;

	/**
	 * @param filecsv
	 * @param separator
	 * @throws FileNotFoundException
	 */
	public SimpleDelimiterReader(File filecsv, String separator) throws FileNotFoundException {
		this.file = filecsv;
		this.separator = separator;

		this.in = new FileInputStream(filecsv);
		this.reader = new BufferedReader(new InputStreamReader(in));
	}

	@Override
	public String[] readOneLine() throws IOException {
		if (this.reader != null) {
			String line = reader.readLine();
			if (line == null) {
				reader.close();
				in.close();
				reader = null;
				in = null;
				return null;
			}
			String [] result = line.split(separator);
			if (line.endsWith(separator)) {
				String []temp = new String[result.length+1];
				System.arraycopy(result, 0, temp, 0, result.length);
				temp[result.length] = null;
				result = temp;
			}
			return result;
		}
		return null;
	}

	@Override
	public String[] readOneLine(int rank) throws IOException {
		return readOneLine();
	}

}
