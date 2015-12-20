package core;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
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

	private int currentPopulationId;
	private NSGAIIIHistory history;
	private static final String title = "NSGAIII";
	private ChartPanel chartPanel;
	private boolean firstFrontOnly;
	private boolean showTargetPoints;
	private Constructor problemConstructor;
	private int numGenerations;
	private int executedGenerations;
	private int numObjectives;
	private double IGD;
	private JLabel label;
	private JSlider slider;
	
	public Main() {
		this.numGenerations = 250;
		this.numObjectives = 2;
		try {
			this.problemConstructor = DTLZ1.class.getConstructor(Integer.class);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		this.currentPopulationId = numGenerations;
		this.firstFrontOnly = false;
		this.showTargetPoints = true;
		this.chartPanel = createChart();
		this.label = new JLabel("IGD: --");
		this.slider = new JSlider(JSlider.VERTICAL, 0, numGenerations, 0);
		slider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent ce) {
				currentPopulationId = ((JSlider) ce.getSource()).getValue();
				resetChart();
			}
		});
		
		JFrame f = new JFrame(title);
		f.setTitle(title);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setLayout(new BorderLayout(0, 5));
		f.add(chartPanel, BorderLayout.CENTER);
		chartPanel.setMouseWheelEnabled(true);
		chartPanel.setHorizontalAxisTrace(true);
		chartPanel.setVerticalAxisTrace(true);

		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		panel.add(label);
		panel.add(createRunNSGAIII());
		panel.add(chooseProblemCB());
		panel.add(createNumObjectivesCB());
		panel.add(createNumGenerationsCB());
		panel.add(createFirstFrontCB());
		panel.add(createTargetPointsSeriesCB());
		panel.add(createTrace());
		panel.add(slider);
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
				NSGAIII alg;
				try {
					alg = new NSGAIII((Problem) problemConstructor.newInstance(numObjectives), numGenerations);
					alg.run();
					executedGenerations = alg.getNumGenerations();
					history = alg.getHistory();
					label.setText("IGD: " + alg.judgeResult(alg.getPopulation()));
					updateSlider();
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e1) {
					e1.printStackTrace();
				}
				resetChart();
			}
		});
		return run;
	}
	
	private JComboBox chooseProblemCB() {
		final JComboBox chooseProblemCB = new JComboBox();
		final String[] traceCmds = { "DTLZ1", "DTLZ2", "DTLZ3" };
		chooseProblemCB.setModel(new DefaultComboBoxModel(traceCmds));
		chooseProblemCB.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String problemName = String.valueOf(chooseProblemCB.getSelectedItem());

				Class c = null;
				try {
					c = Class.forName("problems." + problemName);
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				}
				try {
					problemConstructor = c.getConstructor( Integer.class );
				} catch (NoSuchMethodException | SecurityException e1) {
					e1.printStackTrace();
				}
			}
		});
		return chooseProblemCB;
	}
	
	private JComboBox createNumObjectivesCB() {
		final JComboBox numObjectivesCB = new JComboBox();
		final String[] traceCmds = { "2", "3", "5", "8", "10", "15"};
		numObjectivesCB.setModel(new DefaultComboBoxModel(traceCmds));
		numObjectivesCB.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				numObjectives = Integer.parseInt((String) numObjectivesCB.getSelectedItem());
			}
		});
		return numObjectivesCB;
	}
	
	private JComboBox createNumGenerationsCB() {
		final JComboBox numGenerationsCB = new JComboBox();
		final String[] traceCmds = { "250", "350", "400", "500", "600", "750", "1000", "1500"};
		numGenerationsCB.setModel(new DefaultComboBoxModel(traceCmds));
		numGenerationsCB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				numGenerations = Integer.parseInt((String) numGenerationsCB.getSelectedItem());
			}
		});
		return numGenerationsCB;
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
				resetChart();
			}
		});
		return firstFrontCB;
	}
	
	private JComboBox createTargetPointsSeriesCB() {
		final JComboBox targetPointsCB = new JComboBox();
		final String[] traceCmds = { "Show target points", "Hide target points" };
		targetPointsCB.setModel(new DefaultComboBoxModel(traceCmds));
		targetPointsCB.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (traceCmds[0].equals(targetPointsCB.getSelectedItem())) {
					showTargetPoints = true;
				} else {
					showTargetPoints = false;
				}
				resetChart();
			}
		});
		return targetPointsCB;
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

	private void updateSlider() {
		this.slider.setMaximum(executedGenerations);;
		slider.setLabelTable(slider.createStandardLabels(executedGenerations/10));
		slider.setPaintLabels(true);
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
		if (history != null) {
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
		if(showTargetPoints){
			for (Solution s : history.getTargetPoints().getSolutions()) {
				refPointsSeries.add(s.getObjective(0), s.getObjective(1));
			}
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

	private void resetChart() {
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