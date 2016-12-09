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
package org.xdat.gui.tables;

import java.awt.Event;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;

import org.xdat.Main;
import org.xdat.data.DataSheet;

/**
 * The Class DataTableCellEditor. <br>
 * Cell editor for the table displaying the JTable with the {@link DataSheet}.
 * 
 */
public class DataTableCellEditor extends DefaultCellEditor {

	/** Flag to enable debug message printing for this class. */
	static final boolean printLog = false;

	/**
	 * Instantiates a new Data Table Cell Editor.
	 * 
	 */
	public DataTableCellEditor() {
		super(new JTextField());

	}

	@Override
	public boolean isCellEditable(EventObject anEvent) {
		if (anEvent.getClass() == KeyEvent.class && ((KeyEvent) anEvent).getKeyCode() == (Event.ESCAPE)) {
			if (anEvent.getSource().getClass().equals(DataTable.class)) {
				DataTable srcTable = (DataTable) anEvent.getSource();
				srcTable.getSelectionModel().clearSelection();
			}
			return false;
		} else if (anEvent.getClass().equals(MouseEvent.class) && ((MouseEvent) anEvent).getClickCount() < 2) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Prints debug information to stdout when printLog is set to true.
	 * 
	 * @param message
	 *            the message
	 */
	private void log(String message) {
		if (DataTableCellEditor.printLog && Main.isLoggingEnabled()) {
			System.out.println(this.getClass().getName() + "." + message);
		}
	}
}
