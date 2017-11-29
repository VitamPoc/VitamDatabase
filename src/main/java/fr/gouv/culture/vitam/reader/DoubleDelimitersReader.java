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
public class DoubleDelimitersReader implements VitamReader {
	public File file;
	public String separatorL1;
	public String separatorL2;
	private FileInputStream in;
	private BufferedReader reader;
	private String oldLine = null;
	
	/**
	 * @param file
	 * @param separatorL1
	 *            separator of Level1 (CRLF for instance)
	 * @param separatorL2
	 *            separator of Level2 (for each Level1, separator of fields, for instance ",")
	 * @throws FileNotFoundException
	 */
	public DoubleDelimitersReader(File file, String separatorL1, String separatorL2)
			throws FileNotFoundException {
		this.file = file;
		this.separatorL1 = separatorL1;
		this.separatorL2 = separatorL2;

		this.in = new FileInputStream(file);
		this.reader = new BufferedReader(new InputStreamReader(in));
	}
	
	@Override
	public String[] readOneLine() throws IOException {
		if (this.reader != null) {
			while (true) {
				String line = reader.readLine();
				if (line == null) {
					reader.close();
					in.close();
					reader = null;
					in = null;
					// special case where we should use the oldLine as last line
					String toParse = oldLine;
					oldLine = null;
					if (toParse != null) {
						return toParse.split(separatorL2);
					}
					return null;
				}
				if (oldLine != null) {
					oldLine += "\n"+line;
				}
				int pos = line.indexOf(separatorL1);
				if (pos < 0) {
					// need a new line
					continue;
				}
				String toParse = oldLine.substring(0, pos);
				oldLine = oldLine.substring(pos+separatorL1.length());
				return toParse.split(separatorL2);
			}
		}
		return null;
	}

	@Override
	public String[] readOneLine(int rank) throws IOException {
		return readOneLine();
	}

}
