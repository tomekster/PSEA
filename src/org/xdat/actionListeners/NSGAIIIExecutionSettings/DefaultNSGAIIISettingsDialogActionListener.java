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

package org.xdat.actionListeners.NSGAIIIExecutionSettings;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.xdat.Main;
import org.xdat.UserPreferences;
import org.xdat.actionListeners.parallelCoordinatesDisplaySettings.ChartSpecificDisplaySettingsDialogActionListener;
import org.xdat.gui.dialogs.NSGAIIISettingsDialog;
import org.xdat.gui.panels.ParallelCoordinatesChartDisplaySettingsPanel;

import algorithm.nsgaiii.NSGAIIIParameters;

/**
 * ActionListener for the Ok button of a
 * {@link ParallelCoordinatesChartDisplaySettingsPanel} that was instantiated
 * using the constructor form
 * {@link ParallelCoordinatesChartDisplaySettingsPanel}.
 * <p>
 * When a ChartDisplaySettingsPanel is instantiated without a ChartFrame object
 * as the last argument, the settings made in the panel are applied to the
 * default settings in the {@link UserPreferences}. In order to do this
 * correctly when the Ok button is pressed, the button must use this dedicated
 * ActionListener.
 * 
 * @see ChartSpecificDisplaySettingsDialogActionListener
 */
public class DefaultNSGAIIISettingsDialogActionListener implements ActionListener {

	/** Flag to enable debug message printing for this class. */
	static final boolean printLog = false;

	/** The dialog. */
	private NSGAIIISettingsDialog dialog;

	/**
	 * Instantiates a new default display settings dialog action listener.
	 * 
	 * @param dialog2
	 *            the DisplaySettingsDialog
	 */
	public DefaultNSGAIIISettingsDialogActionListener(NSGAIIISettingsDialog dialog2) {
		this.dialog = dialog2;
		log(" Constructor: Tic label color = " + UserPreferences.getInstance().getParallelCoordinatesAxisTicLabelFontColor().toString());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();
		if (actionCommand == "Ok") {
			NSGAIIIParameters.getInstance().setProblemName(this.dialog.getNSGAIIISettingsPanel().getProblemsSelection());
			NSGAIIIParameters.getInstance().setNumberObjectives(this.dialog.getNSGAIIISettingsPanel().getNumberObjectivesSelection());
			NSGAIIIParameters.getInstance().setNumberExplorationGenerations(this.dialog.getNSGAIIISettingsPanel().getNumberExplorationGenerationsSelection());
			NSGAIIIParameters.getInstance().setNumberExploitationGenerations(this.dialog.getNSGAIIISettingsPanel().getNumberExploitationGenerationsSelection());
			NSGAIIIParameters.getInstance().setNumberRuns(this.dialog.getNSGAIIISettingsPanel().getNumberRunsSelection());
			NSGAIIIParameters.getInstance().setElicitationInterval(this.dialog.getNSGAIIISettingsPanel().getElicitationsFrequency());
			NSGAIIIParameters.getInstance().setShowTargetPoints(this.dialog.getNSGAIIISettingsPanel().getShowTargetPointsSelection());
			NSGAIIIParameters.getInstance().setShowLambdas(this.dialog.getNSGAIIISettingsPanel().getShowLambdasSelection());
			NSGAIIIParameters.getInstance().setShowComparisons(this.dialog.getNSGAIIISettingsPanel().getShowComparisonsSelection());
			log(" OK: Tic label color = " + UserPreferences.getInstance().getParallelCoordinatesAxisTicLabelFontColor().toString());
			this.dialog.dispose();
		} else if (actionCommand == "Cancel") {
			this.dialog.dispose();
		} else if (actionCommand == "Yes" || actionCommand == "No") {
			// Do nothing
		} else {
			System.out.println("ChartDisplaySettingsActionListener: " + e.getActionCommand());
		}
	}

	/**
	 * Prints debug information to stdout when printLog is set to true.
	 * 
	 * @param message
	 *            the message
	 */
	private void log(String message) {
		if (DefaultNSGAIIISettingsDialogActionListener.printLog && Main.isLoggingEnabled()) {
			System.out.println(this.getClass().getName() + "." + message);
		}
	}
}
