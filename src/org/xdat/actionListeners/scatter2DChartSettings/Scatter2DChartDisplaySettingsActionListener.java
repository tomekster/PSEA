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

package org.xdat.actionListeners.scatter2DChartSettings;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JColorChooser;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.xdat.Main;
import org.xdat.chart.ScatterChart2D;
import org.xdat.chart.ScatterPlot2D;
import org.xdat.gui.dialogs.ScatterChart2DSettingsDialog;
import org.xdat.gui.frames.ChartFrame;

/**
 * ActionListener for a {@link ScatterChart2DSettingsDialog} that allows to
 * modify the Display Settings of a {@link ScatterChart2D}.
 */
public class Scatter2DChartDisplaySettingsActionListener implements ActionListener, ChangeListener {

	/** Flag to enable debug message printing for this class. */
	static final boolean printLog = false;

	/** the scatter 2d chart */
	private ScatterChart2D chart;

	/** the scatter 2d chart frame */
	private ChartFrame frame;

	/** the dialog */
	private ScatterChart2DSettingsDialog dialog;

	/**
	 * Instantiates a new chart display settings action listener to edit
	 * settings for a specific chart.
	 * 
	 * @param chartFrame
	 *            the chartFrame
	 * @param chart
	 *            the chart
	 * @param dialog
	 * 			the dialog
	 */
	public Scatter2DChartDisplaySettingsActionListener(ChartFrame chartFrame, ScatterChart2D chart, ScatterChart2DSettingsDialog dialog) {
		this.frame = chartFrame;
		this.chart = chart;
		this.dialog = dialog;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();
		if (actionCommand.equals("Display all designs")) {
			this.chart.getScatterPlot2D().setDisplayedDesignSelectionMode(ScatterPlot2D.SHOW_ALL_DESIGNS);
		} else if (actionCommand.equals("Display selected designs")) {
			this.chart.getScatterPlot2D().setDisplayedDesignSelectionMode(ScatterPlot2D.SHOW_SELECTED_DESIGNS);
		} else if (actionCommand.equals("Display designs visible in parallel chart: ")) {
			this.chart.getScatterPlot2D().setDisplayedDesignSelectionMode(ScatterPlot2D.SHOW_DESIGNS_ACTIVE_IN_PARALLEL_CHART);
		} else if (actionCommand.equals("Foreground Color")) {
			Color newColor = JColorChooser.showDialog(this.dialog, e.getActionCommand(), this.chart.getScatterPlot2D().getDecorationsColor());
			if (newColor != null)
				this.chart.getScatterPlot2D().setDecorationsColor(newColor);
			this.dialog.getFgColorButton().setCurrentColor(this.chart.getScatterPlot2D().getDecorationsColor());
		} else if (actionCommand.equals("Background Color")) {
			Color newColor = JColorChooser.showDialog(this.dialog, e.getActionCommand(), this.chart.getScatterPlot2D().getBackGroundColor());
			if (newColor != null)
				this.chart.getScatterPlot2D().setBackGroundColor(newColor);
			this.dialog.getBgColorButton().setCurrentColor(this.chart.getScatterPlot2D().getBackGroundColor());

		} else if (actionCommand.equals("Active Design Color")) {
			Color newColor = JColorChooser.showDialog(this.dialog, e.getActionCommand(), this.chart.getScatterPlot2D().getActiveDesignColor());
			if (newColor != null)
				this.chart.getScatterPlot2D().setActiveDesignColor(newColor);
			this.dialog.getStandardDesignColorButton().setCurrentColor(this.chart.getScatterPlot2D().getActiveDesignColor());

		} else if (actionCommand.equals("Selected Design Color")) {
			Color newColor = JColorChooser.showDialog(this.dialog, e.getActionCommand(), this.chart.getScatterPlot2D().getSelectedDesignColor());
			if (newColor != null)
				this.chart.getScatterPlot2D().setSelectedDesignColor(newColor);
			this.dialog.getSelectedDesignColorButton().setCurrentColor(this.chart.getScatterPlot2D().getSelectedDesignColor());

		} else if (actionCommand.equals("Set current settings as default")) {
			this.chart.setCurrentSettingsAsDefault();

		} else if (actionCommand.equals("Load default settings")) {
			this.chart.resetDisplaySettingsToDefault();
			this.frame.repaint();
			this.dialog.buildPanel(frame.getMainWindow(), this.frame, this.chart);
		} else {
			System.out.println("Scatter2DChartDisplaySettingsActionListener: " + e.getActionCommand());
		}
		this.frame.repaint();
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		int value = Integer.parseInt(((JSpinner) e.getSource()).getValue().toString());
		String source = ((JSpinner) e.getSource()).getName();
		ScatterPlot2D plot = this.chart.getScatterPlot2D();
		if (source.equals("dataPointSizeSpinner")) {
			plot.setDotRadius(value);
		} else {
			System.out.println("Scatter2DChartDisplaySettingsChangeListener: value: " + value);
			System.out.println("Scatter2DChartDisplaySettingsChangeListener: source: " + source);
		}
		frame.repaint();
	}

	/**
	 * Prints debug information to stdout when printLog is set to true.
	 * 
	 * @param message
	 *            the message
	 */
	private void log(String message) {
		if (Scatter2DChartDisplaySettingsActionListener.printLog && Main.isLoggingEnabled()) {
			System.out.println(this.getClass().getName() + "." + message);
		}
	}
}
