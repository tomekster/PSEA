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

package org.xdat.workerThreads;

import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

import org.xdat.Main;
import org.xdat.data.DataSheet;

/**
 * A thread that runs in the background to create a new datasheet. This takes
 * away this potentially long-running task from the EDT. <br>
 * At the same time a ProgressMonitor is used to show progress, if required.
 * 
 */
public class NSGAIIIDataSheetCreationThread extends SwingWorker {
	/** Flag to enable debug message printing for this class. */
	static final boolean printLog = false;
	/** Id of generation that should be displayed. */
	private int generationId;
	/** The main window. */
	private Main mainWindow;

	/** The progress monitor. */
	private ProgressMonitor progressMonitor;

	/**
	 * Instantiates a new data sheet creation thread.
	 * 
	 * @param pathToInputFile
	 *            The path to the input file to be imported
	 * @param dataHasHeaders
	 *            Specifies, whether the data to be imported has headers
	 * @param mainWindow
	 *            The main window
	 * @param progressMonitor
	 *            The progress monitor
	 */
	public NSGAIIIDataSheetCreationThread(int generationId, Main mainWindow, ProgressMonitor progressMonitor) {
		this.generationId = generationId;
		this.mainWindow = mainWindow;
		this.progressMonitor = progressMonitor;
	}

	@Override
	public Object doInBackground() {
		log("do in background invoked from Thread " + Thread.currentThread().getId());
		DataSheet dataSheet = new DataSheet(this.generationId, this.progressMonitor);

		log("do in background: data sheet created.");
		if (this.progressMonitor.isCanceled()) {
			log("do in background: progress monitor is cancelled.");
			// this.mainWindow.setDataSheet(null);
			// this.mainWindow.getMainMenuBar().setItemsRequiringDataSheetEnabled(false);
			this.mainWindow.repaint();
		} else {
			log("do in background: progress monitor is completed.");
			this.mainWindow.setDataSheet(dataSheet);
			this.mainWindow.getMainMenuBar().setItemsRequiringDataSheetEnabled(true);
		}

		this.progressMonitor.close();
		return null;
	}

	/**
	 * Prints debug information to stdout when printLog is set to true.
	 * 
	 * @param message
	 *            the message
	 */
	private void log(String message) {
		if (NSGAIIIDataSheetCreationThread.printLog && Main.isLoggingEnabled()) {
			System.out.println(this.getClass().getName() + "." + message);
		}
	}
}
