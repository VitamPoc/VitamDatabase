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

import java.io.IOException;

/**
 * @author "Frederic Bregier"
 *
 */
public interface VitamReader {
	/**
	 * 
	 * @return null if no more line, or an array of String
	 * @throws IOException
	 */
	public String[] readOneLine() throws IOException;
	/**
	 * @param rank rank of structures used (ignored if single type)
	 * @return null if no more line, or an array of String
	 * @throws IOException
	 */
	public String[] readOneLine(int rank) throws IOException;
}
