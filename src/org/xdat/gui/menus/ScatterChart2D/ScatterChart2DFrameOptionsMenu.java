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

package org.xdat.gui.menus.ScatterChart2D;

import java.awt.Event;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.xdat.Main;
import org.xdat.actionListeners.scatter2DChartFrame.Scatter2DChartFrameOptionsMenuActionListener;
import org.xdat.chart.ScatterChart2D;
import org.xdat.gui.frames.ChartFrame;

/**
 * Options Menu for a {@link org.xdat.gui.frames.ChartFrame}
 */
public class ScatterChart2DFrameOptionsMenu extends JMenu {

	/** The version tracking unique identifier for Serialization. */
	static final long serialVersionUID = 0002;

	/** The action listener. */
	private Scatter2DChartFrameOptionsMenuActionListener cmd;

	/**
	 * Instantiates a new chart frame options menu.
	 * 
	 * @param mainWindow
	 *            the main window
	 * @param chartFrame
	 *            the chart frame
	 */
	public ScatterChart2DFrameOptionsMenu(Main mainWindow, ChartFrame chartFrame) {
		super("Options");
		this.setMnemonic(KeyEvent.VK_O);
		JMenuItem mi;
		cmd = new Scatter2DChartFrameOptionsMenuActionListener(mainWindow, (ScatterChart2D) chartFrame.getChart(), chartFrame);
		//
		mi = new JMenuItem("Settings", 's');
		mi.setMnemonic(KeyEvent.VK_S);
		mi.addActionListener(cmd);
		this.add(mi);
		//
		mi = new JMenuItem("Reset to Default", 'r');
		mi.setMnemonic(KeyEvent.VK_R);
		mi.addActionListener(cmd);
		this.add(mi);
		//
		mi = new JMenuItem("Export to png", 'x');
		mi.setMnemonic(KeyEvent.VK_X);
		mi.addActionListener(cmd);
		this.add(mi);
	}

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
