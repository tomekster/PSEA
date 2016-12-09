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

package org.xdat.gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Closing adapter for the {@link org.xdat.Main} window..
 */
public class WindowClosingAdapter extends WindowAdapter {

	/** The exit system. */
	private boolean exitSystem;

	/**
	 * Instantiates a new window closing adapter.
	 * 
	 * @param exitSystem
	 *            the exit system
	 */
	public WindowClosingAdapter(boolean exitSystem) {
		this.exitSystem = exitSystem;
	}

	/**
	 * Instantiates a new window closing adapter.
	 */
	public WindowClosingAdapter() {
		this(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
	 */
	public void windowClosing(WindowEvent event) {
		event.getWindow().setVisible(false);
		event.getWindow().dispose();
		if (exitSystem) {
			System.exit(0);
		}
	}
}
