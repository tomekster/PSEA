package core;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

import core.hyperplane.ReferencePoint;
import history.NSGAIIIHistory;
import problems.DTLZ1;

/**
 * @see http://stackoverflow.com/questions/5522575
 */
public class DynamicChart {

	private int currentPopulationId;
	private NSGAIIIHistory history;
	private static final String title = "NSGAIII";
	private ChartPanel chartPanel;
	private JLabel generationNum;

	public DynamicChart(NSGAIIIHistory history) {
		this.history = history;
		this.currentPopulationId = 0;
		this.chartPanel = createChart();
		this.generationNum = new JLabel("0");
		
		JFrame f = new JFrame(title);
		f.setTitle(title);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setLayout(new BorderLayout(0, 5));
		f.add(chartPanel, BorderLayout.CENTER);
		chartPanel.setMouseWheelEnabled(true);
		chartPanel.setHorizontalAxisTrace(true);
		chartPanel.setVerticalAxisTrace(true);

		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		panel.add(generationNum);
		panel.add(createTrace());
		panel.add(createDate());
		panel.add(createZoom());
		f.add(panel, BorderLayout.SOUTH);
		f.pack();
		f.setLocationRelativeTo(null);
		f.setVisible(true);
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
		System.out.println(history.getGenerations().size());
		final JSlider date = new JSlider(JSlider.VERTICAL,0,history.getGenerations().size(),0);
		date.setLabelTable(date.createStandardLabels(50));
		date.setPaintLabels(true);
		date.addChangeListener(new ChangeListener() {

			@Override	
			public void stateChanged(ChangeEvent ce) {
				currentPopulationId = ((JSlider) ce.getSource()).getValue();
				JFreeChart chart = chartPanel.getChart();
				XYPlot plot = (XYPlot) chart.getPlot();
				plot.setDataset(createDataset());
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
		XYDataset dataset = createDataset();
		JFreeChart chart = ChartFactory.createScatterPlot("NSGAIII", "X", "Y", dataset, PlotOrientation.VERTICAL, true, // include
				true, // tooltips
				false // urls
		);

		XYPlot plot = chart.getXYPlot();
		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
		renderer.setBaseShapesVisible(true);
		// NumberFormat currency = NumberFormat.getCurrencyInstance();
		// currency.setMaximumFractionDigits(0);
		// NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		// rangeAxis.setNumberFormatOverride(currency);
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
		for (ReferencePoint rp : history.getReferencePoints()) {
			refPointsSeries.add(rp.getDim(0), rp.getDim(1));
		}
		result.addSeries(refPointsSeries);
		if (pop.getSolutions() != null) {
			XYSeries populationSeries = new XYSeries("Population");
			for (Solution s : pop.getSolutions()) {
				populationSeries.add(s.getObjective(0), s.getObjective(1));
			}
			result.addSeries(populationSeries);
		}
		return result;
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				NSGAIII alg = new NSGAIII(new DTLZ1(6), 400);
				alg.run();
				DynamicChart cpd = new DynamicChart(alg.getHistory());
			}
		});
	}
}