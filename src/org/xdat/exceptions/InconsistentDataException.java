/*
 *  Copyright 2014, Enguerrand de Rochefort
 * 
 * This file is part of xdat.
 *
 * xdat is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * xdat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with xdat.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package org.xdat.exceptions;

/**
 * This exception is thrown when the user tries to update the current
 * {@link org.xdat.org.xdat.data.DataSheet} with data from a file that is not compatible,
 * for example because the number of parameters does not fit.
 */
public class InconsistentDataException extends Exception {

	/** The version tracking unique identifier for Serialization. */
	static final long serialVersionUID = 0000;

	/**
	 * Instantiates a new inconsistent data exception.
	 * 
	 * @param pathToInputFile
	 *            the path to input file
	 */
	public InconsistentDataException(String pathToInputFile) {
		super("The data found in file \"" + pathToInputFile + "\" is not compatible with the current dataSheet.");
	}

}
