package core;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import history.NSGAIIIHistory;
import problems.DTLZ1;
import utils.NonDominatedSort;

/**
 * @see http://stackoverflow.com/questions/5522575
 */
public class Main {

	public static final Problem problem = new DTLZ1(7);
	public static final int numGenerations = 400;
	
	private int currentPopulationId;
	private NSGAIIIHistory history;
	private static final String title = "NSGAIII";
	private ChartPanel chartPanel;
	private boolean firstFrontOnly;
	
	public Main() {
		this.currentPopulationId = numGenerations;
		this.firstFrontOnly = false;
		this.chartPanel = createChart();
		
		JFrame f = new JFrame(title);
		f.setTitle(title);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setLayout(new BorderLayout(0, 5));
		f.add(chartPanel, BorderLayout.CENTER);
		chartPanel.setMouseWheelEnabled(true);
		chartPanel.setHorizontalAxisTrace(true);
		chartPanel.setVerticalAxisTrace(true);

		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		panel.add(new JLabel("IGD: "));
		panel.add(createRunNSGAIII());
		panel.add(createFirstFrontCB());
		panel.add(createTrace());
		panel.add(createDate());
		panel.add(createZoom());
		f.add(panel, BorderLayout.SOUTH);
		f.pack();
		f.setLocationRelativeTo(null);
		f.setVisible(true);
	}

	private JButton createRunNSGAIII() {
		final JButton run = new JButton(new AbstractAction("Run NSGAIII") {

			@Override
			public void actionPerformed(ActionEvent e) {
				NSGAIII alg = new NSGAIII(problem, numGenerations);
				alg.run();
				history = alg.getHistory();
				resetChart();
			}
		});
		return run;
	}

	private JComboBox createFirstFrontCB() {
		final JComboBox firstFrontCB = new JComboBox();
		final String[] traceCmds = { "All fronts", "First fron only" };
		firstFrontCB.setModel(new DefaultComboBoxModel(traceCmds));
		firstFrontCB.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (traceCmds[0].equals(firstFrontCB.getSelectedItem())) {
					firstFrontOnly = false;
				} else {
					firstFrontOnly = true;
				}
			}
		});
		return firstFrontCB;
	}

	private JComboBox createTrace() {
		final JComboBox trace = new JComboBox();
		final String[] traceCmds = { "Enable Trace", "Disable Trace" };
		trace.setModel(new DefaultComboBoxModel(traceCmds));
		trace.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (traceCmds[0].equals(trace.getSelectedItem())) {
					chartPanel.setHorizontalAxisTrace(true);
					chartPanel.setVerticalAxisTrace(true);
					chartPanel.repaint();
				} else {
					chartPanel.setHorizontalAxisTrace(false);
					chartPanel.setVerticalAxisTrace(false);
					chartPanel.repaint();
				}
			}
		});
		return trace;
	}

	private JSlider createDate() {
		final JSlider date = new JSlider(JSlider.VERTICAL, 0, numGenerations, 0);
		date.setLabelTable(date.createStandardLabels(50));
		date.setPaintLabels(true);
		date.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent ce) {
				currentPopulationId = ((JSlider) ce.getSource()).getValue();
				resetChart();
			}
		});
		return date;
	}

	private JButton createZoom() {
		final JButton auto = new JButton(new AbstractAction("Auto Zoom") {

			@Override
			public void actionPerformed(ActionEvent e) {
				chartPanel.restoreAutoBounds();
			}
		});
		return auto;
	}

	private ChartPanel createChart() {
		XYDataset dataset = new XYSeriesCollection();
		if(history != null){
			dataset = createDataset();
		}
		JFreeChart chart = ChartFactory.createScatterPlot("NSGAIII", "X", "Y", dataset, PlotOrientation.VERTICAL, true, // include
				true, // tooltips
				false // urls
		);

		XYPlot plot = chart.getXYPlot();
		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
		renderer.setBaseShapesVisible(true);
		return new ChartPanel(chart);
	}

	private XYDataset createDataset() {
		Population pop;
		if (currentPopulationId == 0) {
			pop = history.getInitialPopulation();
		} else {
			pop = history.getGeneration(currentPopulationId - 1);
		}
		XYSeriesCollection result = new XYSeriesCollection();
		XYSeries refPointsSeries = new XYSeries("Reference points");
		for (Solution s : history.getReferencePoints().getSolutions()) {
			refPointsSeries.add(s.getObjective(0), s.getObjective(1));
		}
		result.addSeries(refPointsSeries);
		if (pop.getSolutions() != null) {
			ArrayList<XYSeries> frontSeries = createpopulationSeries(pop);
			if (firstFrontOnly) {
				result.addSeries(frontSeries.get(0));
			} else {
				for (XYSeries xys : frontSeries) {
					result.addSeries(xys);
				}
			}
		}
		return result;
	}

	private ArrayList<XYSeries> createpopulationSeries(Population pop) {
		ArrayList<Population> fronts = NonDominatedSort.execute(pop);
		ArrayList<XYSeries> resultSeries = new ArrayList<>();
		for (int frontId = 0; frontId < fronts.size(); frontId++) {
			XYSeries frontSeries = new XYSeries("Front " + frontId);
			for (Solution s : fronts.get(frontId).getSolutions()) {
				frontSeries.add(s.getObjective(0), s.getObjective(1));
			}
			resultSeries.add(frontSeries);
		}
		return resultSeries;
	}
	
	private void resetChart(){
		JFreeChart chart = chartPanel.getChart();
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setDataset(createDataset());
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				Main cpd = new Main();
			}
		});
	}
}