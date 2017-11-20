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

package org.xdat.actionListeners.mainMenu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;

import org.xdat.Main;
import org.xdat.gui.dialogs.NSGAIIISettingsDialog;
import org.xdat.gui.menus.mainWIndow.MainNSGAIIIMenu;
import org.xdat.workerThreads.NSGAIIIDataSheetCreationThread;

import algorithm.implementations.psea.PSEARunnner;
import experiment.ExecutionHistory;

/**
 * ActionListener for a {@link MainNSGAIIIMenu}.
 */
public class MainNSGAIIIMenuActionListener implements ActionListener {

	/** The main window. */
	private Main mainWindow;

	/** Flag to enable debug message printing for this class. */
	static final boolean printLog = false;

	/**
	 * Instantiates a new main NSGAIII menu action listener.
	 * 
	 * @param mainWindow
	 *            the main window
	 */
	public MainNSGAIIIMenuActionListener(Main mainWindow) {
		log("constructor called");
		this.mainWindow = mainWindow;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Set NSGAIII Execution Parameters")) {
			new NSGAIIISettingsDialog(this.mainWindow);
		} else if(e.getActionCommand().equals("Run NSGAIII")){
			if (mainWindow.getChartFrameCount() == 0 || JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(mainWindow, "This operation will close all charts.\n Are you sure you want to continue?", "Import Data", JOptionPane.OK_CANCEL_OPTION)) {
				mainWindow.disposeAllChartFrames();
				
				PSEARunnner.runPSEA();

				ProgressMonitor progressMonitor = new ProgressMonitor(mainWindow, "", "Building Chart...", 0, 100);
				progressMonitor.setProgress(0);
				NSGAIIIDataSheetCreationThread dataCreationThread = new NSGAIIIDataSheetCreationThread(ExecutionHistory.getInstance().getNumGenerations() - 1, this.mainWindow, progressMonitor);
				dataCreationThread.execute();				
				progressMonitor.setProgress(0);
			}
			
		} else {
			System.out.println(e.getActionCommand());
		}
	}

	/**
	 * Prints debug information to stdout when printLog is set to true.
	 * 
	 * @param message
	 *            the message
	 */
	private void log(String message) {
		if (MainNSGAIIIMenuActionListener.printLog && Main.isLoggingEnabled()) {
			System.out.println(this.getClass().getName() + "." + message);
		}
	}

}
