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

package org.xdat.gui.menus.mainWIndow;

import java.awt.Event;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.xdat.Main;
import org.xdat.actionListeners.mainMenu.MainNSGAIIIMenuActionListener;

/**
 * Chart menu for the {@link org.xdat.Main} window.
 */
public class MainNSGAIIIMenu extends JMenu {
	/** The version tracking unique identifier for Serialization. */
	static final long serialVersionUID = 0001;

	private JMenuItem NSGAIIIexecutionParametersItem = new JMenuItem("Set NSGAIII Execution Parameters", 'p');
	
	private JMenuItem runNSGAIIIItem = new JMenuItem("Run NSGAIII", 'r');

	/**
	 * Instantiates a new main chart menu.
	 * 
	 * @param mainWindow
	 *            the main window
	 */
	public MainNSGAIIIMenu(Main mainWindow) {
		super("NSGAIII");
		this.setMnemonic(KeyEvent.VK_N);
		MainNSGAIIIMenuActionListener cmd = new MainNSGAIIIMenuActionListener(mainWindow);
	
		NSGAIIIexecutionParametersItem.setMnemonic(KeyEvent.VK_P);
		NSGAIIIexecutionParametersItem.addActionListener(cmd);
		NSGAIIIexecutionParametersItem.setEnabled(true);
		this.add(NSGAIIIexecutionParametersItem);
		
		runNSGAIIIItem.setMnemonic(KeyEvent.VK_R);
		runNSGAIIIItem.addActionListener(cmd);
		runNSGAIIIItem.setEnabled(true);
		this.add(runNSGAIIIItem);
	}

//	/**
//	 * Specifies whether the menu item createMenuItem is enabled. This is
//	 * required because this item is only available when data is loaded.
//	 * 
//	 * @param enabled
//	 *            specifies whether the menu item createMenuItem is enabled.
//	 */
//	public void setItemsRequiringDataSheetEnabled(boolean enabled) {
//		this.createPCChartMenuItem.setEnabled(enabled);
//		this.createScatter2DChartMenuItem.setEnabled(enabled);
//	}

	/**
	 * Sets the ctrl accelerator.
	 * 
	 * @param mi
	 *            the menu item
	 * @param acc
	 *            the accelerator
	 */
	private void setCtrlAccelerator(JMenuItem mi, char acc) {
		KeyStroke ks = KeyStroke.getKeyStroke(acc, Event.CTRL_MASK);
		mi.setAccelerator(ks);
	}

}
