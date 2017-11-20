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

package org.xdat.gui.panels;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;

import org.xdat.Main;
import org.xdat.actionListeners.NSGAIIIExecutionSettings.DefaultNSGAIIISettingsDialogActionListener;
import org.xdat.actionListeners.NSGAIIIExecutionSettings.NSGAIIISettingsActionListener;
import org.xdat.actionListeners.parallelCoordinatesDisplaySettings.ChartSpecificDisplaySettingsDialogActionListener;
import org.xdat.actionListeners.parallelCoordinatesDisplaySettings.DefaultDisplaySettingsDialogActionListener;
import org.xdat.chart.ParallelCoordinatesChart;
import org.xdat.gui.buttons.MinMaxSpinnerModel;
import org.xdat.gui.dialogs.NSGAIIISettingsDialog;
import org.xdat.gui.frames.ChartFrame;

import algorithm.implementations.psea.PSEAParameters;

/**
 * Panel to modify display settings for a
 * {@link org.xdat.org.xdat.chart.ParallelCoordinatesChart}.
 */
public class NSGAIIISettingsPanel extends JPanel {
	/** The version tracking unique identifier for Serialization. */
	static final long serialVersionUID = 0000;

	/** Flag to enable debug message printing for this class. */
	static final boolean printLog = false;

	/** The main window. */
	private Main mainWindow;

	/** The dialog on which the panel is located. */
	private NSGAIIISettingsDialog dialog;

	/** The chart frame to which the settings apply. */
	private ChartFrame chartFrame;

	/** The action listener */
	private NSGAIIISettingsActionListener cmd;

	/** The cancel button. */
	private JButton cancelButton = new JButton("Cancel");

	/** The ok button. */
	private JButton okButton = new JButton("Ok");
	
	private JComboBox <String> problemsComboBox = new JComboBox<>();
	
	private JComboBox <String> numObjectivesComboBox = new JComboBox<>();
	
	private JComboBox <String> maxExplorationGenerationsComboBox = new JComboBox<>();
	
	private JComboBox <String> maxExploitationGenerationsComboBox = new JComboBox<>();
	
	private JComboBox <String> numRunsComboBox = new JComboBox<>();
	
	private JSpinner elicitationFrequencySpinner = new JSpinner(new MinMaxSpinnerModel(1, 200));
	
	/** The show target points true button. */
	private JRadioButton showTargetPointsTrueButton = new JRadioButton("Yes");

	/** The show target points false button. */
	private JRadioButton showTargetPointsFalseButton = new JRadioButton("No");
	
	/** The show lambdas true button. */
	private JRadioButton showLambdasTrueButton = new JRadioButton("Yes");

	/** The show lambdas false button. */
	private JRadioButton showLambdasFalseButton = new JRadioButton("No");
	
	/** The show comparisons true button. */
	private JRadioButton showComparisonsTrueButton = new JRadioButton("Yes");

	/** The show comparisons false button. */
	private JRadioButton showComparisonsFalseButton = new JRadioButton("No");
	
	private ButtonGroup showTargetPointsButtonGroup = new ButtonGroup();
	
	private ButtonGroup showLambdasButtonGroup = new ButtonGroup();
	
	private ButtonGroup showComparisonsButtonGroup = new ButtonGroup();

	/**
	 * Instantiates a new chart display settings panel the allows editing the
	 * default settings in the user preferences.
	 * 
	 * @param mainWindow
	 *            the main window
	 * @param nsgaiiiSettingsDialog
	 *            the dialog on which the panel is located
	 * 
	 * @see DefaultDisplaySettingsDialogActionListener
	 */
	public NSGAIIISettingsPanel(Main mainWindow, NSGAIIISettingsDialog nsgaiiiSettingsDialog) {
		this.mainWindow = mainWindow;
		this.dialog = nsgaiiiSettingsDialog;

		buildPanel();
		// set states
		problemsComboBox.setName("problemsComboBox");
		numObjectivesComboBox.setName("numObjectivesComboBox");
		this.problemsComboBox.setSelectedItem(PSEAParameters.getInstance().getProblemName());
		this.numObjectivesComboBox.setSelectedItem(PSEAParameters.getInstance().getNumberObjectives());
		this.maxExplorationGenerationsComboBox.setSelectedItem(PSEAParameters.getInstance().getNumberExplorationGenerations());
		this.maxExploitationGenerationsComboBox.setSelectedItem(PSEAParameters.getInstance().getNumberExploitationGenerations());
		this.numRunsComboBox.setSelectedItem(PSEAParameters.getInstance().getNumberRuns());
		this.elicitationFrequencySpinner.setValue(PSEAParameters.getInstance().getElicitationInterval());
		if(PSEAParameters.getInstance().isShowTargetPoints()){
			this.showTargetPointsTrueButton.setSelected(true);
		}
		else{
			this.showTargetPointsFalseButton.setSelected(true);
		}
		
		if(PSEAParameters.getInstance().isShowLambdas()){
			this.showLambdasTrueButton.setSelected(true);
		}
		else{
			this.showLambdasFalseButton.setSelected(true);
		}
		
		if(PSEAParameters.getInstance().isShowComparisons()){
			this.showComparisonsTrueButton.setSelected(true);
		}
		else{
			this.showComparisonsFalseButton.setSelected(true);
		}
	}

	/**
	 * Instantiates a new chart display settings panel that allows editing a
	 * particular chart.
	 * 
	 * @param mainWindow
	 *            the main window
	 * @param dialog
	 *            the dialog on which the panel is located
	 * @param chartFrame
	 *            the chart which should be modified
	 * 
	 * @see ChartSpecificDisplaySettingsDialogActionListener
	 */
	public NSGAIIISettingsPanel(Main mainWindow, NSGAIIISettingsDialog dialog, ChartFrame chartFrame) {
		this.mainWindow = mainWindow;
		this.dialog = dialog;
		this.chartFrame = chartFrame;
		ParallelCoordinatesChart c = (ParallelCoordinatesChart) chartFrame.getChart();

		buildPanel();
		// set states
//		this.axisLabelVerticalOffsetCheckbox.setSelected(c.isVerticallyOffsetAxisLabels());
//		this.antiasingCheckbox.setSelected(c.isAntiAliasing());
//		this.numGenerationsComboBox.setSelected(c.isUseAlpha());
//		this.setShowFilteredDesignsSelection(c.isShowFilteredDesigns());
//		this.setShowDesignIDsSelection(c.isShowDesignIDs());
//		this.designLabelFontSizeSpinner.setValue(c.getDesignLabelFontSize());
//		this.designLineThicknessSpinner.setValue(c.getLineThickness());
//		this.selectedDesignLineThicknessSpinner.setValue(c.getSelectedDesignsLineThickness());
//		this.numRunsComboBox.setCurrentColor(c.getBackGroundColor());
//		this.activeDesignColorButton.setCurrentColor(c.getDefaultDesignColor(true, chartFrame.getChart().isUseAlpha()));
//		this.selectedDesignColorButton.setCurrentColor(c.getSelectedDesignColor());
//		this.filteredDesignColorButton.setCurrentColor(c.getDefaultDesignColor(false, chartFrame.getChart().isUseAlpha()));
//		this.filterColorButton.setCurrentColor(c.getFilterColor());
//		this.showOnlySelectedDesignsCheckBox.setSelected(c.isShowOnlySelectedDesigns());
//		this.filterWidthSpinner.setValue(c.getFilterWidth());
//		this.filterHeightSpinner.setValue(c.getFilterHeight());

	}

	/**
	 * Builds the panel.
	 */
	private void buildPanel() {
		// create components
		
		final String[] problems = { "DTLZ1", "DTLZ2", "DTLZ3", "DTLZ4", "WFG1", "WFG2", "WFG3", "WFG4", "WFG5", "WFG6", "WFG7", "WFG8", "WFG9"};
		problemsComboBox.setModel(new DefaultComboBoxModel(problems));
		
		final String[] objectives = { "3", "5", "8", "10", "15" };
		numObjectivesComboBox.setModel(new DefaultComboBoxModel<>(objectives));
		
		final String[] explorationGenerations = { "50","100", "150", "200", "250", "300", "350", "400", "500", "600", "750", "1000", "1250", "1500", "2000",
		"3000" };
		
		final String[] exploitationGenerations = { "50","100", "150", "200", "250", "300", "350", "400", "500", "600", "750", "1000", "1250", "1500", "2000",
		"3000" };
		
		maxExplorationGenerationsComboBox.setModel(new DefaultComboBoxModel<>(explorationGenerations));
		
		maxExploitationGenerationsComboBox.setModel(new DefaultComboBoxModel<>(exploitationGenerations));
		
		final String[] numRuns = { "1", "20" };
		numRunsComboBox.setModel(new DefaultComboBoxModel<>(numRuns));
		
		TitledSubPanel contentPanel = new TitledSubPanel("");
		JPanel contentInnerPanel = new JPanel(new BorderLayout());
		JPanel labelPanel = new JPanel(new GridLayout(0, 1));
		JPanel controlsPanel = new JPanel(new GridLayout(0, 1));
		TitledSubPanel buttonsPanel = new TitledSubPanel("");
		JPanel showTargetPointsRadioButtonsPanel = new JPanel(new GridLayout(1, 2));
		JPanel showLambdasRadioButtonsPanel = new JPanel(new GridLayout(1, 2));
		JPanel showComparisonsRadioButtonsPanel = new JPanel(new GridLayout(1, 2));

		showTargetPointsButtonGroup.add(showTargetPointsTrueButton);
		showTargetPointsButtonGroup.add(showTargetPointsFalseButton);
		showLambdasButtonGroup.add(showLambdasTrueButton);
		showLambdasButtonGroup.add(showLambdasFalseButton);
		showComparisonsButtonGroup.add(showComparisonsTrueButton);
		showComparisonsButtonGroup.add(showComparisonsFalseButton);
		
		JLabel problemsComboBoxLabel = new JLabel("Problem");
		JLabel numberObjectivesLabel = new JLabel("Number of objectives");
		JLabel maxExplorationGenerationsLabel = new JLabel("Max exploration generations");
		JLabel maxExploitationGenerationsLabel = new JLabel("Max exploitation generations");
		JLabel numberRunsLabel = new JLabel("Number of runs");
		JLabel elicitationFrequencyLabel = new JLabel("Elicitation frequency");
		JLabel showTargetPointsLabel = new JLabel("Show target points");
		JLabel showLambdasLabel = new JLabel("Show lambdas");
		JLabel showComparisonsLabel = new JLabel("Show comparisons");
		JPanel cancelButtonPanel = new JPanel();
		JPanel okButtonPanel = new JPanel();

		// set Layouts
		this.setLayout(new BorderLayout());
		contentPanel.setLayout(new BorderLayout());
		cancelButtonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		okButtonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		buttonsPanel.setLayout(new GridLayout(1, 2));

		// add components
		this.add(contentPanel, BorderLayout.CENTER);
		contentPanel.add(contentInnerPanel, BorderLayout.NORTH);
		contentInnerPanel.add(labelPanel, BorderLayout.CENTER);
		contentInnerPanel.add(controlsPanel, BorderLayout.EAST);
		this.add(buttonsPanel, BorderLayout.SOUTH);
		labelPanel.add(problemsComboBoxLabel);
		JPanel problemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		problemPanel.add(problemsComboBox);
		controlsPanel.add(problemPanel);
		labelPanel.add(numberObjectivesLabel);
		JPanel numObjectivesComboBoxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		numObjectivesComboBoxPanel.add(numObjectivesComboBox);
		controlsPanel.add(numObjectivesComboBoxPanel);
		labelPanel.add(maxExplorationGenerationsLabel);
		labelPanel.add(maxExploitationGenerationsLabel);
		JPanel numExplorationGenerationsComboBoxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		numExplorationGenerationsComboBoxPanel.add(maxExplorationGenerationsComboBox);
		controlsPanel.add(numExplorationGenerationsComboBoxPanel);
		JPanel numExploitationGenerationsComboBoxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		numExploitationGenerationsComboBoxPanel.add(maxExploitationGenerationsComboBox);
		controlsPanel.add(numExploitationGenerationsComboBoxPanel);
		labelPanel.add(numberRunsLabel);
		JPanel numRunsComboBoxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		numRunsComboBoxPanel.add(numRunsComboBox);
		controlsPanel.add(numRunsComboBoxPanel);
		labelPanel.add(elicitationFrequencyLabel);
		controlsPanel.add(elicitationFrequencySpinner);
		showTargetPointsRadioButtonsPanel.add(showTargetPointsTrueButton);
		showTargetPointsRadioButtonsPanel.add(showTargetPointsFalseButton);
		labelPanel.add(showTargetPointsLabel);
		controlsPanel.add(showTargetPointsRadioButtonsPanel);
		showLambdasRadioButtonsPanel.add(showLambdasTrueButton);
		showLambdasRadioButtonsPanel.add(showLambdasFalseButton);
		labelPanel.add(showLambdasLabel);
		controlsPanel.add(showLambdasRadioButtonsPanel);
		showComparisonsRadioButtonsPanel.add(showComparisonsTrueButton);
		showComparisonsRadioButtonsPanel.add(showComparisonsFalseButton);
		labelPanel.add(showComparisonsLabel);
		controlsPanel.add(showComparisonsRadioButtonsPanel);
		
		// buttons panel
		buttonsPanel.add(cancelButtonPanel);
		buttonsPanel.add(okButtonPanel);
		cancelButtonPanel.add(cancelButton);
		okButtonPanel.add(okButton);
	}

	/**
	 * Sets the action listener.
	 * 
	 * @param cmd
	 *            the new action listener
	 */
	public void setActionListener(NSGAIIISettingsActionListener cmd) {
		this.cmd = cmd;
		problemsComboBox.addActionListener(cmd);
		numObjectivesComboBox.addActionListener(cmd);
		maxExplorationGenerationsComboBox.addActionListener(cmd);
		maxExploitationGenerationsComboBox.addActionListener(cmd);
		numRunsComboBox.addActionListener(cmd);
		//TODO - SPINNER?
		showTargetPointsTrueButton.addActionListener(cmd);
		showTargetPointsFalseButton.addActionListener(cmd);
		showLambdasTrueButton.addActionListener(cmd);
		showLambdasFalseButton.addActionListener(cmd);
		showComparisonsTrueButton.addActionListener(cmd);
		showComparisonsFalseButton.addActionListener(cmd);
	}

	/**
	 * Tells the panel that the settings should be applied to the user
	 * preferences.
	 * 
	 * @see DefaultDisplaySettingsDialogActionListener
	 */
	public void setOkCancelButtonTargetDefaultSettings() {
		DefaultNSGAIIISettingsDialogActionListener cmd = new DefaultNSGAIIISettingsDialogActionListener(dialog);
		log("setOkCancelButtonTargetDefaultSettings called");
		cancelButton.addActionListener(cmd);
		okButton.addActionListener(cmd);
	}

	/**
	 * Gets the chart display settings action listener.
	 * 
	 * @return the chart display settings action listener
	 */
	public NSGAIIISettingsActionListener getChartDisplaySettingsActionListener() {
		return this.cmd;
	}

	/**
	 * Gets the show filtered designs selection.
	 * 
	 * @return the show filtered designs selection
	 */
	public String getProblemsSelection() {
		return problemsComboBox.getModel().getSelectedItem().toString();
	}
	
	public Integer getNumberObjectivesSelection() {
		return Integer.parseInt(numObjectivesComboBox.getModel().getSelectedItem().toString());
	}
	
	public Integer getNumberExplorationGenerationsSelection() {
		return Integer.parseInt(maxExplorationGenerationsComboBox.getModel().getSelectedItem().toString());
	}
	
	public Integer getNumberExploitationGenerationsSelection() {
		return Integer.parseInt(maxExploitationGenerationsComboBox.getModel().getSelectedItem().toString());
	}
	
	public Integer getNumberRunsSelection() {
		return Integer.parseInt(numRunsComboBox.getModel().getSelectedItem().toString());
	}
	
	public Integer getElicitationsFrequency() {
		return Integer.parseInt(elicitationFrequencySpinner.getValue().toString());
	}
	
	public boolean getShowTargetPointsSelection() {
		if (showTargetPointsTrueButton.getModel().equals(showTargetPointsButtonGroup.getSelection())) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean getShowLambdasSelection() {
		if (showLambdasTrueButton.getModel().equals(showLambdasButtonGroup.getSelection())) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean getShowComparisonsSelection() {
		if (showComparisonsTrueButton.getModel().equals(showComparisonsButtonGroup.getSelection())) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Prints debug information to stdout when printLog is set to true.
	 * 
	 * @param message
	 *            the message
	 */
	private void log(String message) {
		if (NSGAIIISettingsPanel.printLog && Main.isLoggingEnabled()) {
			System.out.println(this.getClass().getName() + "." + message);
		}
	}

}
