package core;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
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
import org.jzy3d.analysis.AnalysisLauncher;

import core.hyperplane.ReferencePoint;
import history.NSGAIIIHistory;
import preferences.Comparison;
import problems.dtlz.DTLZ1;
import solutionRankers.NonDominationRanker;
import utils.Geometry;
import utils.MySeries;

/**
 * @see http://stackoverflow.com/questions/5522575
 */
public class Main {

	private int currentPopulationId;
	private NSGAIIIHistory history;
	private static final String title = "NSGAIII";
	private ChartPanel chartPanel;
	private ChartPanel chartPanelReferencePlane;
	private boolean firstFrontOnly;
	private boolean showTargetPoints;
	private boolean showSolDir;
	private boolean showGeneration;
	private boolean showChebDir;
	private Constructor problemConstructor;
	private int numGenerations;
	private int elicitationInterval;
	private int numRuns;
	private int executedGenerations;
	private int numObjectives;
	private JLabel labelIGD;
	private JSlider slider;
	private boolean interactive;
	private Plot3D plot;

	public Main() {
		this.interactive = true;
		this.numRuns = 1;
		this.numGenerations = 50;
		this.elicitationInterval = 25;
		this.numObjectives = 2;
		try {
			this.problemConstructor = DTLZ1.class.getConstructor(Integer.class);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		this.currentPopulationId = numGenerations;
		this.firstFrontOnly = false;
		this.showTargetPoints = true;
		this.showSolDir = true;
		this.showGeneration = true;
		this.showChebDir= true;
		this.chartPanel = createChart();
		this.chartPanelReferencePlane = createChartReferencePlane();
		this.labelIGD = new JLabel("IGD: --");
		this.slider = new JSlider(JSlider.HORIZONTAL, 0, numGenerations, 0);
		slider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent ce) {
				currentPopulationId = ((JSlider) ce.getSource()).getValue();
				resetChart();
			}
		});
		this.plot = null;

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
		panel.add(createNumGenerationsCB());
		panel.add(createNumberOfRunsCB());
		panel.add(createFirstFrontCB());
		panel.add(createShowGenSeriesCB());
		panel.add(createTargetPointsSeriesCB());
		panel.add(createSolDirSeriesCB());
		panel.add(createChebDirSeriesCB());
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

	private JComboBox createNumGenerationsCB() {
		final JComboBox numGenerationsCB = new JComboBox();
		final String[] traceCmds = { "50", "250", "350", "400", "500", "600", "750", "1000", "1250", "1500", "2000",
				"3000" };
		numGenerationsCB.setModel(new DefaultComboBoxModel(traceCmds));
		numGenerationsCB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				numGenerations = Integer.parseInt((String) numGenerationsCB.getSelectedItem());
			}
		});
		return numGenerationsCB;
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
	
	private JComboBox createSolDirSeriesCB() {
		final JComboBox solDirCB = new JComboBox();
		final String[] traceCmds = { "Show solution directions", "Hide solution directions" };
		solDirCB.setModel(new DefaultComboBoxModel(traceCmds));
		solDirCB.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (traceCmds[0].equals(solDirCB.getSelectedItem())) {
					showSolDir = true;
				} else {
					showSolDir = false;
				}
				resetChart();
			}
		});
		return solDirCB;
	}
	
	private JComboBox createShowGenSeriesCB() {
		final JComboBox showGenCB = new JComboBox();
		final String[] traceCmds = { "Show generation solutions", "Hide generation solutions" };
		showGenCB.setModel(new DefaultComboBoxModel(traceCmds));
		showGenCB.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (traceCmds[0].equals(showGenCB.getSelectedItem())) {
					showGeneration = true;
				} else {
					showGeneration = false;
				}
				resetChart();
			}
		});
		return showGenCB;
	}
	
	private JComboBox createChebDirSeriesCB() {
		final JComboBox chebDirCB = new JComboBox();
		final String[] traceCmds = { "Show chebyshev directions", "Hide chebyshev directions" };
		chebDirCB.setModel(new DefaultComboBoxModel(traceCmds));
		chebDirCB.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (traceCmds[0].equals(chebDirCB.getSelectedItem())) {
					showChebDir = true;
				} else {
					showChebDir= false;
				}
				resetChart();
			}
		});
		return chebDirCB;
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
			dataset = createDatasetReferencePlane();
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

	private XYDataset createDatasetReferencePlane() {
		ArrayList<Population> generationsHistory = history.getGenerations();
		ArrayList<ArrayList<ReferencePoint>> solutionDirectionsHistory = history.getSolutionDirectionsHistory();
		ArrayList<ArrayList<ReferencePoint>> chebyshevDirectionsHistory = history.getChebyshevDirectionsHistory();
		ArrayList <Comparison> comparisonsHistory = history.getPreferenceCollector().getComparisons();
		XYSeriesCollection result = new XYSeriesCollection();
		if (solutionDirectionsHistory != null && chebyshevDirectionsHistory != null) {
			ArrayList<Solution> generation = generationsHistory.get(currentPopulationId).getSolutions();
			ArrayList<ReferencePoint> solutionDirections = solutionDirectionsHistory.get(currentPopulationId);
			ArrayList<ReferencePoint> chebyshevDirections= chebyshevDirectionsHistory.get(currentPopulationId);
			ArrayList<XYSeries> series = createReferencePointsSeries(generation, solutionDirections, chebyshevDirections, new ArrayList<Comparison>(comparisonsHistory.subList(0, Integer.max(0,(currentPopulationId + elicitationInterval-1))/elicitationInterval)));
			for(XYSeries ser : series){
				result.addSeries(ser);
			}
		}	
		return result;
	}

	private ArrayList<XYSeries> createpopulationSeries(Population pop) {
		NonDominationRanker ndr = new NonDominationRanker();
		ArrayList<Population> fronts = ndr.sortPopulation(pop);
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

	private ArrayList<XYSeries> createReferencePointsSeries(ArrayList<Solution> generation, ArrayList<ReferencePoint> solutionDirections, ArrayList<ReferencePoint> chebyshevDirections, ArrayList<Comparison> comparisons) {
		ArrayList<XYSeries> result = new ArrayList<XYSeries>();
		XYSeries generationSeries= new XYSeries("Generation solutions");
		XYSeries solutionSeries= new XYSeries("Solution directions");
		XYSeries coherentChSeries = new XYSeries("Coherent chebyshev directions");
		XYSeries incoherentChSeries = new XYSeries("Incoherent chebyshev directions");
		XYSeries preferedSolutions = new XYSeries("Prefered solutions");
		XYSeries nonPreferedSolutions = new XYSeries("Non-prefered solutions");
		Solution t;
		
		if(showGeneration){
			for(Solution s : generation){
				t = Geometry.cast3dPointToPlane(s.getObjectives());
				generationSeries.add(t.getObjective(0), t.getObjective(1));	
			}
		}
		
		if(showSolDir){
			for (ReferencePoint rp : solutionDirections) {
				t = Geometry.cast3dPointToPlane(rp.getDim());
				solutionSeries.add(t.getObjective(0), t.getObjective(1));		
			}
		}
		if(showChebDir){
			for (ReferencePoint rp : chebyshevDirections) {
				if(rp.isCoherent()){
					t = Geometry.cast3dPointToPlane(rp.getDim());
					coherentChSeries.add(t.getObjective(0), t.getObjective(1));		
				} else{
					t = Geometry.cast3dPointToPlane(rp.getDim());
					incoherentChSeries.add(t.getObjective(0), t.getObjective(1));
				}
			}
		}
		for (Comparison c : comparisons) {
				t = Geometry.cast3dPointToPlane(c.getBetter().getObjectives());
				preferedSolutions.add(t.getObjective(0), t.getObjective(1));
				t = Geometry.cast3dPointToPlane(c.getWorse().getObjectives());
				nonPreferedSolutions.add(t.getObjective(0), t.getObjective(1));
		}
		
		result.add(solutionSeries);
		result.add(coherentChSeries);
		result.add(incoherentChSeries);
		result.add(preferedSolutions);
		result.add(nonPreferedSolutions);
		result.add(generationSeries);
		return result;
	}

	private void resetChart() {
		JFreeChart chart = chartPanel.getChart();
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setDataset(createDataset());
		
		//plot3D();
		
		if (this.interactive && this.numObjectives == 3) {
			JFreeChart chartRP = chartPanelReferencePlane.getChart();
			XYPlot plotRP = (XYPlot) chartRP.getPlot();
			plotRP.setDataset(createDatasetReferencePlane());
		}
	}

	private void plot3D() {
		if(this.plot == null){
			this.plot = new Plot3D(createJZY3DDataset());
			try {
				AnalysisLauncher.open(this.plot);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
			this.plot.update(createJZY3DDataset());
		}
	}

	private MySeries createJZY3DDataset() {
		Population pop = history.getGeneration(currentPopulationId);
		ArrayList <ReferencePoint> rp = history.getSolutionDirectionsHistory().get(currentPopulationId);
		ArrayList <Comparison> comparisons = new ArrayList<Comparison>(history.getPreferenceCollector().getComparisons().subList(0, Integer.max(0,(currentPopulationId + elicitationInterval-1))/elicitationInterval));		
		return new MySeries(pop.getSolutions(), rp, comparisons);
	}

	double runNSGAIIIOnce() {
		NSGAIII alg;
		double resIGD = -1;
		try {
			alg = new NSGAIII((Problem) problemConstructor.newInstance(numObjectives), numGenerations, interactive, elicitationInterval);
			alg.run();
			executedGenerations = alg.getNumGenerations();
			history = alg.getHistory();
			resIGD = alg.evaluateFinalResult(alg.getPopulation());
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
		resetChart();
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