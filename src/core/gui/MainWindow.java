package core.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
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

import core.Evaluator;
import core.Population;
import core.Problem;
import core.algorithm.RST_NSGAIII;
import core.points.ReferencePoint;
import core.points.Solution;
import history.ExecutionHistory;
import preferences.Comparison;
import problems.dtlz.DTLZ1;
import solutionRankers.ChebyshevRanker;
import solutionRankers.ChebyshevRankerBuilder;
import solutionRankers.NonDominationRanker;
import utils.Geometry;

/**
 * @see http://stackoverflow.com/questions/5522575
 */
public class MainWindow {

	private int currentPopulationId;
	private ExecutionHistory history;
	private static final String title = "NSGAIII";
	private ChartPanel chartPanel;
	private ChartPanel chartPanelReferencePlane;
	private boolean firstFrontOnly;
	private boolean showTargetPoints;
	private boolean showSolDir;
	private boolean showSpreadSolutions;
	private boolean showPreferenceGeneration;
	private boolean showComparisons;
	private boolean showLambda;
	private Constructor problemConstructor;
	private int numExplorationGenerations;
	private int numExploitationGenerations;
	private int numElicitations1;
	private int numElicitations2;
	private int elicitationInterval;
	private int numLambdas;
	private double 	spreadThreshold;
	private int numRuns;
	private int executedGenerations;
	private int numObjectives;
	private JLabel labelIGD;
	private JSlider slider;
	private boolean interactive;

	public MainWindow() {
		this.interactive = true;
		this.numRuns = 1;
		this.numExplorationGenerations = 100;
		this.numExploitationGenerations = 100;
		this.elicitationInterval = 20;
		this.numObjectives = 2;
		try {
			this.problemConstructor = DTLZ1.class.getConstructor(Integer.class);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		this.currentPopulationId = 1;
		this.firstFrontOnly = false;
		this.showTargetPoints = true;
		this.showSolDir = false;
		this.showSpreadSolutions = true;
		this.showPreferenceGeneration = true;
		this.showLambda= true;
		this.showComparisons = true;
		this.chartPanel = createChart();
		this.chartPanelReferencePlane = createChartReferencePlane();
		this.labelIGD = new JLabel("IGD: --");
		this.slider = new JSlider(JSlider.HORIZONTAL, 0, currentPopulationId, 0);
		this.history = ExecutionHistory.getInstance();
		slider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent ce) {
				currentPopulationId = ((JSlider) ce.getSource()).getValue();
				resetChart();
			}
		});

		JFrame f = new JFrame(title);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setLayout(new BorderLayout(5, 5));
		f.add(chartPanel, BorderLayout.WEST);
		chartPanel.setMouseWheelEnabled(true);
		chartPanel.setHorizontalAxisTrace(true);
		chartPanel.setVerticalAxisTrace(true);
		chartPanel.setPreferredSize(new Dimension(600, 600));
		
		f.add(chartPanelReferencePlane, BorderLayout.EAST);
		chartPanelReferencePlane.setMouseWheelEnabled(true);
		chartPanelReferencePlane.setHorizontalAxisTrace(true);
		chartPanelReferencePlane.setVerticalAxisTrace(true);
		chartPanelReferencePlane.setPreferredSize(new Dimension(600, 600));
		
		JPanel panel = new JPanel(new FlowLayout());
		panel.setPreferredSize(new Dimension(40,100));
		panel.add(labelIGD);
		panel.add(createRunNSGAIIIButton());
		panel.add(createInteractiveCV());
		panel.add(chooseProblemComboBox());

		panel.add(createNumObjectivesCB());
		panel.add(createNumExplorationGenerationsCB());
		panel.add(createNumExploitationGenerationsCB());
		panel.add(createNumberOfRunsCB());
		panel.add(createFirstFrontCB());
		panel.add(createShowSpreadSeriesCB());
		panel.add(createShowPreferenceSeriesCB());
		panel.add(createTargetPointsSeriesCB());
		panel.add(createSolDirSeriesCB());
		panel.add(createChebDirSeriesCB());
		panel.add(createComparisonSeriesCB());
		panel.add(createTrace());
		panel.add(slider);
		panel.add(createZoom());

		f.add(panel, BorderLayout.SOUTH);
		f.pack();
		f.setLocationRelativeTo(null);
		f.setVisible(true);
	}

	private JButton createRunNSGAIIIButton() {
		final JButton run = new JButton(new AbstractAction("Run NSGAIII") {
			@Override
			public void actionPerformed(ActionEvent e) {
				runNSGAIIInTimes(numRuns);
			}
		});
		return run;
	}

	private Component createInteractiveCV() {
		final JComboBox interactiveCB = new JComboBox();
		final String[] traceCmds = { "Interactive", "Non-Interactive" };
		interactiveCB.setModel(new DefaultComboBoxModel(traceCmds));
		interactiveCB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				interactive = ((String) interactiveCB.getSelectedItem()).equals("Interactive");
			}
		});
		return interactiveCB;
	}

	private JComboBox chooseProblemComboBox() {
		final JComboBox chooseProblemCB = new JComboBox();
		final String[] traceCmds = { "DTLZ1", "DTLZ2", "DTLZ3", "DTLZ4", "WFG1", "WFG2", "WFG3", "WFG4", "WFG5", "WFG6", "WFG7", "WFG8", "WFG9"};
		chooseProblemCB.setModel(new DefaultComboBoxModel(traceCmds));
		chooseProblemCB.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String problemName = String.valueOf(chooseProblemCB.getSelectedItem());
				Class c = null;
				try {
					if(problemName.contains("DTLZ")) {
						c = Class.forName("problems.dtlz." + problemName);
					} else if(problemName.contains("WFG")) {
						c = Class.forName("problems.wfg." + problemName);
					}
					problemConstructor = c.getConstructor(Integer.class);
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				} catch (NoSuchMethodException | SecurityException e1) {
					e1.printStackTrace();
				}
			}
		});
		return chooseProblemCB;
	}

	private JComboBox createNumObjectivesCB() {
		final JComboBox numObjectivesCB = new JComboBox();
		final String[] traceCmds = { "2", "3", "5", "8", "10", "15" };
		numObjectivesCB.setModel(new DefaultComboBoxModel(traceCmds));
		numObjectivesCB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				numObjectives = Integer.parseInt((String) numObjectivesCB.getSelectedItem());
			}
		});
		return numObjectivesCB;
	}

	private JComboBox createNumExplorationGenerationsCB() {
		final JComboBox numExplorationGenerationsCB = new JComboBox();
		final String[] traceCmds = { "50", "250", "350", "400", "500", "600", "750", "1000", "1250", "1500", "2000",
				"3000" };
		numExplorationGenerationsCB.setModel(new DefaultComboBoxModel(traceCmds));
		numExplorationGenerationsCB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				numExplorationGenerations = Integer.parseInt((String) numExplorationGenerationsCB.getSelectedItem());
			}
		});
		return numExplorationGenerationsCB;
	}
	
	private JComboBox createNumExploitationGenerationsCB() {
		final JComboBox numExploitationGenerationsCB = new JComboBox();
		final String[] traceCmds = { "50", "250", "350", "400", "500", "600", "750", "1000", "1250", "1500", "2000",
				"3000" };
		numExploitationGenerationsCB.setModel(new DefaultComboBoxModel(traceCmds));
		numExploitationGenerationsCB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				numExploitationGenerations = Integer.parseInt((String) numExploitationGenerationsCB.getSelectedItem());
			}
		});
		return numExploitationGenerationsCB;
	}
	
	private JComboBox createNumElicitations1CB() {
		final JComboBox numElicitations1CB = new JComboBox();
		final String[] traceCmds = { "50", "100", "150", "200", "250", "300", "350", "400", "500", "600", "750", "1000", "1250", "1500", "2000",
				"3000" };
		numElicitations1CB.setModel(new DefaultComboBoxModel(traceCmds));
		numElicitations1CB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				numElicitations1= Integer.parseInt((String) numElicitations1CB.getSelectedItem());
			}
		});
		return numElicitations1CB;
	}
	
	private JComboBox createNumElicitations2CB() {
		final JComboBox numElicitations2CB = new JComboBox();
		final String[] traceCmds = { "50", "100", "150", "200", "250", "300", "350", "400", "500", "600", "750", "1000", "1250", "1500", "2000",
				"3000" };
		numElicitations2CB.setModel(new DefaultComboBoxModel(traceCmds));
		numElicitations2CB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				numElicitations2 = Integer.parseInt((String) numElicitations2CB.getSelectedItem());
			}
		});
		return numElicitations2CB;
	}
	

	private JComboBox createNumberOfRunsCB() {
		final JComboBox numRunsCB = new JComboBox();
		final String[] traceCmds = { "1", "20" };	
		numRunsCB.setModel(new DefaultComboBoxModel(traceCmds));
		numRunsCB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				numRuns = Integer.parseInt((String) numRunsCB.getSelectedItem());
			}
		});
		return numRunsCB;
	}

	private JCheckBox createFirstFrontCB() {
		final JCheckBox firstFrontCB = new JCheckBox("First front only", firstFrontOnly);
		firstFrontCB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				firstFrontOnly = firstFrontCB.isSelected();
				resetChart();
			}
		});
		return firstFrontCB;
	}

	private JCheckBox createTargetPointsSeriesCB() {
		final JCheckBox targetPointsCB = new JCheckBox("Show target points", showTargetPoints);
		targetPointsCB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showTargetPoints = targetPointsCB.isSelected();
				resetChart();
			}
		});
		return targetPointsCB;
	}
	
	private JCheckBox createSolDirSeriesCB() {
		final JCheckBox solDirCB = new JCheckBox("Show solution directions", showSolDir);
		solDirCB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showSolDir = solDirCB.isSelected();
				resetChart();
			}
		});
		return solDirCB;
	}
	
	private JCheckBox createShowSpreadSeriesCB() {
		final JCheckBox showSpreadCB = new JCheckBox("Show spread solutions", showSpreadSolutions);
		showSpreadCB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showSpreadSolutions = showSpreadCB.isSelected();
				resetChart();
			}
		});
		return showSpreadCB;
	}
	
	private JCheckBox createShowPreferenceSeriesCB() {
		final JCheckBox showPreferenceCB = new JCheckBox("Show preference solutions", showPreferenceGeneration);
		showPreferenceCB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showPreferenceGeneration = showPreferenceCB.isSelected();
				resetChart();
			}
		});
		return showPreferenceCB;
	}
	
	private JCheckBox createChebDirSeriesCB() {
		final JCheckBox chebDirCB = new JCheckBox("Show chebyshev directions", showLambda);
		chebDirCB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showLambda = chebDirCB.isSelected();
				resetChart();
			}
		});
		return chebDirCB;
	}
	
	private JCheckBox createComparisonSeriesCB() {
		final JCheckBox comparisonsCB = new JCheckBox("Show comparisons", showComparisons);
		comparisonsCB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showComparisons = comparisonsCB.isSelected();
				resetChart();
			}
		});
		return comparisonsCB;
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
		this.slider.setMaximum(executedGenerations);
		slider.setLabelTable(slider.createStandardLabels(executedGenerations / 5));
		slider.setPaintLabels(true);
		slider.setValue(executedGenerations);
	}

	private JButton createZoom() {
		final JButton auto = new JButton(new AbstractAction("Auto Zoom") {
			@Override
			public void actionPerformed(ActionEvent e) {
				chartPanel.restoreAutoBounds();
				chartPanelReferencePlane.restoreAutoBounds();
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

	private ChartPanel createChartReferencePlane() {
		XYDataset dataset = new XYSeriesCollection();
		if (this.interactive && history != null) {
			dataset = createDatasetOnHyperplane();
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
		pop = history.getGeneration(currentPopulationId);
		
		XYSeriesCollection result = new XYSeriesCollection();
		XYSeries refPointsSeries = new XYSeries("Reference points");
		if (showTargetPoints) {
			for (Solution s : history.getTargetPoints().getSolutions()) {
				refPointsSeries.add(s.getObjective(0), s.getObjective(1));
			}
		}
		result.addSeries(refPointsSeries);
		if (pop.getSolutions() != null) {
			ArrayList<XYSeries> frontGenerationSeries = createpopulationSeries(pop, "SpreadPop");
			if (firstFrontOnly) {
				result.addSeries(frontGenerationSeries.get(0));
			} else {
				for (XYSeries xys : frontGenerationSeries) {
					result.addSeries(xys);
				}
			}
		}
		return result;
	}

	private XYDataset createDatasetOnHyperplane() {
		ArrayList<Population> generationsHistory = history.getGenerations();
		ArrayList<ArrayList<ReferencePoint>> lambdaDirectionsHistory = history.getLambdaDirectionsHistory();
		ArrayList <Comparison> comparisonsHistory = history.getPreferenceCollector().getComparisons();
		XYSeriesCollection result = new XYSeriesCollection();
		if (lambdaDirectionsHistory != null) {
			ArrayList<Solution> generation = generationsHistory.get(currentPopulationId).getSolutions();
			ArrayList<ReferencePoint> lambdaDirections= lambdaDirectionsHistory.get(currentPopulationId);
			
			ArrayList<XYSeries> series = createReferencePointsSeries(generation, lambdaDirections, new ArrayList<Comparison>(comparisonsHistory.subList(0, Integer.min(currentPopulationId/elicitationInterval, comparisonsHistory.size()))));
			for(XYSeries ser : series){
				result.addSeries(ser);
			}
		}	
		return result;
	}

	private ArrayList<XYSeries> createpopulationSeries(Population pop, String popName) {
		NonDominationRanker ndr = new NonDominationRanker();
		ArrayList<Population> fronts = ndr.sortPopulation(pop);
		ArrayList<XYSeries> resultSeries = new ArrayList<>();
		for (int frontId = 0; frontId < fronts.size(); frontId++) {
			XYSeries frontSeries = new XYSeries(popName + " front " + frontId);
			for (Solution s : fronts.get(frontId).getSolutions()) {
				frontSeries.add(s.getObjective(0), s.getObjective(1));
			}
			resultSeries.add(frontSeries);
		}
		return resultSeries;
	}

	private ArrayList<XYSeries> createReferencePointsSeries(ArrayList<Solution> generation, ArrayList<ReferencePoint> lambda, ArrayList<Comparison> comparisons) {
		ArrayList<XYSeries> result = new ArrayList<XYSeries>();
		XYSeries generationSeries= new XYSeries("Preference generation");
		XYSeries lambdaSeries = new XYSeries("Lambdas");
		XYSeries preferedSolutions = new XYSeries("Prefered solutions");
		XYSeries nonPreferedSolutions = new XYSeries("Non-prefered solutions");
		
		double t[];
		
		if(showSpreadSolutions){
			for(Solution s : generation){
				t = Geometry.cast3dPointToPlane(s.getObjectives());
				generationSeries.add(t[0], t[1]);	
			}
		}
		
		if(showLambda){
			for (ReferencePoint rp : lambda) {
				t = Geometry.cast3dPointToPlane(rp.getDim());
				lambdaSeries.add(t[0], t[1]);		
			}
		}
		if(showComparisons){
			for (Comparison c : comparisons) {
					t = Geometry.cast3dPointToPlane(c.getBetter().getObjectives());
					preferedSolutions.add(t[0], t[1]);
					t = Geometry.cast3dPointToPlane(c.getWorse().getObjectives());
					nonPreferedSolutions.add(t[0], t[1]);
			}
		}
		
		result.add(lambdaSeries);
		result.add(preferedSolutions);
		result.add(nonPreferedSolutions);
		result.add(generationSeries);
		return result;
	}

	private void resetChart() {
		JFreeChart chart = chartPanel.getChart();
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setDataset(createDataset());
		
		if (this.interactive && this.numObjectives == 3) {
			JFreeChart chartRP = chartPanelReferencePlane.getChart();
			XYPlot plotRP = (XYPlot) chartRP.getPlot();
			plotRP.setDataset(createDatasetOnHyperplane());
		}
	}

	double runNSGAIIIOnce() {
		RST_NSGAIII alg;
		double resIGD = -1;
		try {
			Problem problem = (Problem) problemConstructor.newInstance(numObjectives);
			ChebyshevRanker cr = ChebyshevRankerBuilder.getExperimentalRanker(1, numObjectives, null);
			alg = new RST_NSGAIII(problem,  cr);																							
			alg.run();
			executedGenerations = alg.getGeneration();
			Evaluator.evaluateRun(problem, cr, alg.getPopulation());
			updateSlider();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e1) {
			e1.printStackTrace();
		}
		return resIGD;
	}

	void runNSGAIIInTimes(int n) {
		ArrayList<Double> resIgd = new ArrayList<Double>(n);
		for (int i = 0; i < n; i++) {
			resIgd.add(runNSGAIIIOnce());
			System.out.println(i + ": " + resIgd.get(i));
		}
		resIgd.sort(null);
		DecimalFormat format = new DecimalFormat("#.0000000");
		String resWorse = format.format(resIgd.get(n - 1));
		String resMed = format.format(resIgd.get(n / 2));
		String resBest = format.format(resIgd.get(0));

		labelIGD.setText("[" + resWorse + ", " + resMed + ", " + resBest + "]");
		System.out.println(labelIGD.getText());
		
		System.out.println("Generation min: " + history.getFinalMinDist());
		System.out.println("Generation avg: " + history.getFinalAvgDist());
		resetChart();
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() { new MainWindow(); }
		});
	}
}