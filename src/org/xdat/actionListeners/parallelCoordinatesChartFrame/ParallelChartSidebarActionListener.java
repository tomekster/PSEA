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

package org.xdat.actionListeners.parallelCoordinatesChartFrame;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.xdat.Main;
import org.xdat.UserPreferences;
import org.xdat.chart.ParallelCoordinatesChart;
import org.xdat.data.Cluster;
import org.xdat.data.ClusterSet;
import org.xdat.data.DataSheet;
import org.xdat.gui.buttons.ColorChoiceButton;
import org.xdat.gui.panels.ParallelCoordinatesChartPanel;
import org.xdat.gui.panels.ParallelCoordinatesChartSidebarPanel;

/**
 * ActionListener for a
 * {@link org.xdat.org.xdat.gui.panels.ParallelCoordinatesChartDisplaySettingsPanel} that
 * allows to modify the Display Settings of a
 * {@link org.xdat.org.xdat.chart.ParallelCoordinatesChart}.
 */
public class ParallelChartSidebarActionListener implements ActionListener, ChangeListener {

	/**
	 * Flag to enable debug message printing for this class.
	 */
	static final boolean printLog = false;

	/**
	 * The user preferences.
	 */
	private UserPreferences userPreferences;

	/**
	 * The panel on which the settings controls are located.
	 */
	private ParallelCoordinatesChartSidebarPanel sidePanel;
	
	/**
	 * The main Window
	 */
	private Main mainWindow;

	/**
	 * The parallel Coordinates Chart panel
	 */
	private ParallelCoordinatesChartPanel chartPanel;

	/**
	 * The active design color.
	 * 
	 * @see org.xdat.org.xdat.chart.ParallelCoordinatesChart#getDefaultDesignColor(boolean)
	 */
	private Color activeDesignColor;

	/**
	 * Instantiates a new chart dside panel action listener 
	 * 
	 * @param mainWindow
	 *            the main window
	 * @param panel
	 *            the panel
	 *  @param chartPanel
	 *  			the chart panel
	 */
	public ParallelChartSidebarActionListener(Main mainWindow, ParallelCoordinatesChartSidebarPanel panel, ParallelCoordinatesChartPanel chartPanel) {
		this.userPreferences = UserPreferences.getInstance();
		this.mainWindow = mainWindow;
		this.sidePanel = panel;
		this.chartPanel = chartPanel;
		this.activeDesignColor = ((ParallelCoordinatesChart) chartPanel.getChart()).getDefaultDesignColor(true,  chartPanel.getChart().isUseAlpha());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();
		ParallelCoordinatesChart chart = (ParallelCoordinatesChart) chartPanel.getChart();

		if (actionCommand.equals("Active Design Color")) {
			Color newColor = JColorChooser.showDialog(sidePanel.getChartFrame(), "Background Color", this.activeDesignColor);
			if (newColor != null) {
				if (newColor.getAlpha() == 255) {
					int alphaValue = sidePanel.getActiveDesignAlphaSlider().getValue();
					newColor = new Color(newColor.getRed(), newColor.getGreen(), newColor.getBlue(), alphaValue);
				}

				this.activeDesignColor = newColor;
				chart.setActiveDesignColor(newColor);
				this.sidePanel.getActiveDesignColorButton().setCurrentColor(this.activeDesignColor);
				this.sidePanel.getActiveDesignAlphaSlider().setValue(newColor.getAlpha());
				chartPanel.repaint();
			}
		} else if (actionCommand.equals("Add Cluster")) {
			DataSheet data = mainWindow.getDataSheet();
			data.getClusterSet().createBuffer();
			data.getClusterSet().addClusterToBuffer();
			data.getClusterSet().applyChanges();

			this.sidePanel.updateClusterList(data.getClusterSet());
		} else if (actionCommand.equals("Remove")) {
			JButton button = (JButton) e.getSource();
			ClusterSet clusterSet = mainWindow.getDataSheet().getClusterSet();
			clusterSet.createBuffer();
			clusterSet.removeClusterFromBuffer(button.getName());
			clusterSet.applyChanges();

			this.sidePanel.updateClusterList(clusterSet);
			this.chartPanel.revalidate();
			this.chartPanel.repaint();

		} else if (actionCommand.equals("Apply")) {
			JButton button = (JButton) e.getSource();
			DataSheet dataSheet = chart.getDataSheet();
			Cluster cluster = dataSheet.getClusterSet().getCluster(button.getName());
			for (int i = 0; i < dataSheet.getDesignCount(); i++) {
				if (dataSheet.getDesign(i).isActive(chart)) {
					dataSheet.getDesign(i).setCluster(cluster);
				}
			}
			this.chartPanel.setPreferredSize(this.chartPanel.getPreferredSize());

			for (int i = 0; i < this.mainWindow.getChartFrameCount(); i++) {
				this.mainWindow.getChartFrame(i).validate();
				this.mainWindow.getChartFrame(i).repaint();
			}
		} else if (actionCommand.equals("Active")) {
			JCheckBox checkBox = (JCheckBox) e.getSource();
			ClusterSet clusterSet = mainWindow.getDataSheet().getClusterSet();
			clusterSet.getCluster(checkBox.getName()).setActive(checkBox.isSelected());

			for (int i = 0; i < this.mainWindow.getChartFrameCount(); i++) {
				this.mainWindow.getChartFrame(i).validate();
				this.mainWindow.getChartFrame(i).repaint();
			}
		} else if (actionCommand.equals("clusterColor")) {
			ColorChoiceButton button = (ColorChoiceButton) e.getSource();
			Cluster cluster = mainWindow.getDataSheet().getClusterSet().getCluster(button.getName());

			Color newColor = JColorChooser.showDialog(sidePanel.getChartFrame(), "Cluster Color", cluster.getActiveDesignColor(true));
			if (newColor != null) {
				if (newColor.getAlpha() == 255) {
					int alpha = sidePanel.getClusterAlphaSlider(cluster).getValue();
					newColor = new Color(newColor.getRed(), newColor.getGreen(), newColor.getBlue(), alpha);
				}

				cluster.setActiveDesignColor(newColor);
				button.setCurrentColor(newColor);
				sidePanel.getClusterAlphaSlider(cluster).setValue(newColor.getAlpha());
				chartPanel.repaint();
			}
		} else {
			log(e.getActionCommand());
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		Object source = e.getSource();
		ParallelCoordinatesChart chart = (ParallelCoordinatesChart) chartPanel.getChart();

		if (source instanceof JSlider) {
			JSlider slider = (JSlider) source;
			int value = slider.getValue();

			if (slider.getName().equals("activeDesignAlphaSlider")) {
				Color newColor = new Color(activeDesignColor.getRed(), activeDesignColor.getGreen(), activeDesignColor.getBlue(), value);
				this.activeDesignColor = newColor;
				chart.setActiveDesignColor(newColor);
				this.sidePanel.getActiveDesignColorButton().setCurrentColor(this.activeDesignColor);
				this.sidePanel.getActiveDesignAlphaSlider().setValue(slider.getValue());
				chartPanel.repaint();
				Color oldDefaultColor = UserPreferences.getInstance().getParallelCoordinatesActiveDesignDefaultColor();
				UserPreferences.getInstance().setParallelCoordinatesActiveDesignDefaultColor(new Color(oldDefaultColor.getRed(), oldDefaultColor.getGreen(), oldDefaultColor.getBlue(), value));
			} else {
				Cluster cluster = mainWindow.getDataSheet().getClusterSet().getCluster(slider.getName());
				if(cluster != null){
					Color newColor = new Color(cluster.getActiveDesignColor(true).getRed(), cluster.getActiveDesignColor(true).getGreen(), cluster.getActiveDesignColor(true).getBlue(), value);
					cluster.setActiveDesignColor(newColor);
					chartPanel.repaint();
				}
			}
		} else {
			log("ParallelChartSidebarActionListener: " + e.toString());
		}
	}

	/**
	 * Prints debug information to stdout when printLog is set to true.
	 * 
	 * @param message
	 *            the message
	 */
	private void log(String message) {
		if (ParallelChartSidebarActionListener.printLog && Main.isLoggingEnabled()) {
			System.out.println(this.getClass().getName() + "." + message);
		}
	}

	/**
	 * Gets the active design color.
	 * 
	 * @return the active design color
	 */
	public Color getActiveDesignColor() {
		return activeDesignColor;
	}
}
