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

package org.xdat.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.HeadlessException;

import javax.swing.JDialog;
import javax.swing.JTabbedPane;

import org.xdat.Main;
import org.xdat.actionListeners.NSGAIIIExecutionSettings.NSGAIIISettingsActionListener;
import org.xdat.actionListeners.parallelCoordinatesDisplaySettings.AxisDisplaySettingsActionListener;
import org.xdat.actionListeners.parallelCoordinatesDisplaySettings.ParallelChartDisplaySettingsActionListener;
import org.xdat.chart.ParallelCoordinatesChart;
import org.xdat.gui.WindowClosingAdapter;
import org.xdat.gui.frames.ChartFrame;
import org.xdat.gui.panels.AxisDisplaySettingsPanel;
import org.xdat.gui.panels.NSGAIIISettingsPanel;
import org.xdat.gui.panels.ParallelCoordinatesChartDisplaySettingsPanel;

/**
 * Dialog to modify display settings for a
 * {@link org.xdat.org.xdat.chart.ParallelCoordinatesChart}s, its axes or the
 * {@link org.xdat.UserPreferences} for these display settings
 */
public class NSGAIIISettingsDialog extends JDialog {

	/** The version tracking unique identifier for Serialization. */
	static final long serialVersionUID = 0002;

	/** Flag to enable debug message printing for this class. */
	private static final boolean printLog = false;

	/** The main window. */
	private Main mainWindow;

	/** The chart display settings panel. */
	private NSGAIIISettingsPanel nSGAIIISettingsPanel;

	/**
	 * Instantiates a new display settings dialog.
	 * 
	 * @param mainWindow
	 *            the main window
	 * @throws HeadlessException
	 *             the headless exception
	 */
	public NSGAIIISettingsDialog(Main mainWindow) throws HeadlessException {
		super(mainWindow, "NSGAIII Settings", true);
		this.mainWindow = mainWindow;

		nSGAIIISettingsPanel = new NSGAIIISettingsPanel(this.mainWindow, this);
		nSGAIIISettingsPanel.setActionListener(new NSGAIIISettingsActionListener(mainWindow, nSGAIIISettingsPanel, this));
		buildDialog();

		nSGAIIISettingsPanel.setOkCancelButtonTargetDefaultSettings();
		this.setVisible(true);
	}

	/**
	 * Instantiates a new display settings dialog.
	 * 
	 * @param mainWindow
	 *            the main window
	 * @param chart
	 *            the chart
	 * @param chartFrame
	 *            the chart frame
	 * @throws HeadlessException
	 *             the headless exception
	 */
	public NSGAIIISettingsDialog(Main mainWindow, ParallelCoordinatesChart chart, ChartFrame chartFrame) throws HeadlessException {
		super(chartFrame, "Display Settings");
		this.setModal(true);
		this.mainWindow = mainWindow;

		nSGAIIISettingsPanel = new NSGAIIISettingsPanel(this.mainWindow, this, chartFrame);
		nSGAIIISettingsPanel.setActionListener(new NSGAIIISettingsActionListener(mainWindow, nSGAIIISettingsPanel, chart, this));

		buildDialog();

		log("preferred size : " + this.getPreferredSize().width + ", " + this.getPreferredSize().getHeight());
		this.setVisible(true);

	}

	/**
	 * Builds the dialog.
	 */
	private void buildDialog() {
		log("constructor called");
		this.addWindowListener(new WindowClosingAdapter(false));
		this.setResizable(false);

		// create components
		JTabbedPane tabbedPane = new JTabbedPane();

		// set Layouts
		this.setLayout(new BorderLayout());

		// add components
		this.add(tabbedPane, BorderLayout.CENTER);

		tabbedPane.add("General", nSGAIIISettingsPanel);
		// pack
		this.pack();

		// set location and make visible
		int left = (int) (0.5 * this.mainWindow.getSize().width) - (int) (this.getSize().width * 0.5) + this.mainWindow.getLocation().x;
		int top = (int) (0.5 * this.mainWindow.getSize().height) - (int) (this.getSize().height * 0.5) + this.mainWindow.getLocation().y;
		this.setLocation(left, top);
	}

	/**
	 * Gets the axis display settings panel.
	 * 
	 * @return the axis display settings panel
	 */
	public NSGAIIISettingsPanel getNSGAIIISettingsPanel() {
		return nSGAIIISettingsPanel;
	}

	/**
	 * Prints debug information to stdout when printLog is set to true.
	 * 
	 * @param message
	 *            the message
	 */
	private static final void log(String message) {
		if (NSGAIIISettingsDialog.printLog && Main.isLoggingEnabled()) {
			System.out.println("DisplaySettingsDialog." + message);
		}
	}

}
