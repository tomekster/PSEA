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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.xdat.Main;
import org.xdat.chart.Axis;
import org.xdat.chart.Filter;
import org.xdat.chart.ParallelCoordinatesChart;
import org.xdat.data.DataSheet;
import org.xdat.data.Design;
import org.xdat.exceptions.NoAxisFoundException;
import org.xdat.gui.frames.ChartFrame;
import org.xdat.gui.menus.parallelCoordinatesChart.ParallelCoordinatesContextMenu;
import org.xdat.gui.tables.DataSheetTableColumnModel;

import history.ExecutionHistory;

/**
 * Panel that is used to display a
 * {@link org.xdat.org.xdat.chart.ParallelCoordinatesChart}.
 */
public class ParallelCoordinatesChartPanel extends ChartPanel implements MouseMotionListener, MouseListener, MouseWheelListener {
	/** The version tracking unique identifier for Serialization. */
	static final long serialVersionUID = 0005;

	/** Flag to enable debug message printing for this class. */
	static final boolean printLog = false;

	/** The main window. */
	private Main mainWindow;

	/** The chart frame. */
	private ChartFrame chartFrame;

	/** The chart . */
	private ParallelCoordinatesChart chart;

	/**
	 * The buffered image that is used to make redrawing the chart more
	 * efficient.
	 */
	private BufferedImage bufferedImage;

	/** Reference to a filter that is currently being dragged by the user. */
	private Filter draggedFilter;

	/**
	 * When the user is dragging a filter, the initial x position is stored in
	 * this field.
	 */
	private int dragStartX;

	/**
	 * When the user is dragging a filter, the initial y position is stored in
	 * this field.
	 */
	private int dragStartY;

	/** Stores how far left or right the mouse was dragged for further use. */
	private int dragCurrentX;

	/** Stores how far up or down the mouse was dragged for further use. */
	private int dragOffsetY;

	/** Reference to an axis that is currently being dragged by the user. */
	private Axis draggedAxis;
	
	/** Stores all designs under the cursor to display them in their selection color */
	private HashSet<Integer> hoverList;

	/** Stores all lines in a map with references to their respective ids */
	private Map<int[], HashSet<Integer>> lineMap;

	/** Stores the drag selection state */
	private boolean dragSelecting = false;

	/**
	 * Instantiates a new parallel coordinates chart panel.
	 * 
	 * @param mainWindow
	 *            the main Window
	 * @param chartFrame 
	 * 			the chart frame
	 * @param chart
	 *            the chart
	 */
	public ParallelCoordinatesChartPanel(Main mainWindow, ChartFrame chartFrame, ParallelCoordinatesChart chart) {
		super(mainWindow.getDataSheet(), chart);
		this.mainWindow = mainWindow;
		this.chartFrame = chartFrame;
		this.chart = chart;
		log("constructor called");
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addMouseWheelListener(this);

		this.lineMap = new LinkedHashMap<int[], HashSet<Integer>>();
		this.hoverList = new HashSet<Integer>();
	}

	/**
	 * Overridden to implement the painting of the chart.
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent(Graphics g) {
		long begin = System.currentTimeMillis();
		Graphics cg;
		BufferedImage canvas;
		if(this.chart.isAntiAliasing() || this.chart.isUseAlpha() ){
			canvas = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
			cg = canvas.getGraphics();
		}
		else{
			canvas = null;
			cg = g;
		}
		if(this.chart.isAntiAliasing() ){
			Graphics2D graphics2D = (Graphics2D) cg;
			graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}

		super.paintComponent(cg);

		if (this.draggedAxis != null) {
			cg.drawImage(this.bufferedImage, 0, 0, this);
			log("paintComponent: drawing dragged axis ");
			cg.setColor(new Color(100, 100, 100));
			int yPosition = chart.getAxisTopPos();
			int xPosition = this.dragCurrentX;
			cg.drawLine(xPosition - 1, yPosition, xPosition - 1, yPosition + (chart.getAxisHeight()));
			cg.drawLine(xPosition, yPosition, xPosition, yPosition + (chart.getAxisHeight()));
			cg.drawLine(xPosition + 1, yPosition, xPosition + 1, yPosition + (chart.getAxisHeight()));
		} else if (this.dragSelecting) {
			cg.drawImage(this.bufferedImage, 0, 0, this);
			this.drawSelection(cg);
		} else {
			this.drawDesigns(cg);
			this.drawAxes(cg);
		}
		log("Painting took "+(System.currentTimeMillis()-begin)+" ms");

		if(canvas != null){
			g.drawImage(canvas, 0, 0, null);
		}
	}
	
	/**
	 * Draws the selection rectangle
	 */
	private void drawSelection(Graphics g) {
		if (dragSelecting) {
			Rectangle rec = getSelectionRectangle();

			g.setColor(new Color(50, 50, 50, 150));
			g.drawRect(rec.x, rec.y, rec.width, rec.height);

			g.setColor(new Color(50, 50, 50, 50));
			g.fillRect(rec.x, rec.y, rec.width, rec.height);
		}
	}
	
	/** 
	 * Gets the region that currently is in the selection 
	 * @return the rectangle describing the drag selection
	 * */
	public Rectangle getSelectionRectangle() {
		return pointsToRectangle(dragStartX, dragStartY, dragCurrentX, dragOffsetY);
	}

	/**
	 * Builds a rectangle from 2 points
	 * @param x1 the x coordinate of the first point
	 * @param y1 the y coordinate of the first point
	 * @param x2 the x coordinate of the second point 
	 * @param y2 the y coordinate of the second point
	 * @return the rectangle
	 */
	public Rectangle pointsToRectangle(int x1, int y1, int x2, int y2) {
		if (x1 <= x2 && y1 >= y2) {
			// Quadrant 1
			return new Rectangle(x1, y2, x2 - x1, y1 - y2);
		} else if (x1 > x2 && y1 > y2) {
			// Quadrant 2
			return new Rectangle(x2, y2, x1 - x2, y1 - y2);
		} else if (x1 > x2 && y1 < y2) {
			// Quadrant 3
			return new Rectangle(x2, y1, x1 - x2, y2 - y1);
		} else {
			// Quadrant 4
			return new Rectangle(x1, y1, x2 - x1, y2 - y1);
		}
	}

	/**
	 * Returns the axis (actually its id) that is currently in the drag zone
	 * @param x the x coordinate where to look
	 * @return the axis id 
	 */
	public int withinAxisDragZone(int x) {
		for (int i = 0; i < chart.getAxisCount(); i++) {
			Axis axis = chart.getAxis(i);
			int axisPos = axis.getUpperFilter().getXPos();

			if (axis.isActive() && x >= axisPos - 10 && x < axisPos + 10) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Draws the lines representing the designs.
	 * 
	 * @param g
	 *            the graphics object
	 */
	public void drawDesigns(final Graphics g) {
		if(chart.isAntiAliasing()){
			Graphics2D graphics2D = (Graphics2D) g;
			graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
		ParallelCoordinatesChart chart = ((ParallelCoordinatesChart) getChart());
		int axisTopPos = chart.getAxisTopPos();
		int designLabelFontSize = chart.getDesignLabelFontSize();
		int axisCount = chart.getAxisCount();
		double[] axisRanges = new double[axisCount];
		int[] axisHeights = new int[axisCount];
		int[] axisWidths = new int[axisCount];
		double[] axisMaxValues = new double[axisCount];
		double[] axisMinValues = new double[axisCount];
		int[] axisTicLabelFontsizes = new int[axisCount];
		boolean[] axisActiveFlags = new boolean[axisCount];
		boolean[] axisInversionFlags = new boolean[axisCount];
		for (int i = 0; i < axisCount; i++) // read all the display settings and
		// put them into arrays to improve
		// rendering speed of the chart
		{
			axisRanges[i] = chart.getAxis(i).getMax() - chart.getAxis(i).getMin();
			axisHeights[i] = chart.getAxisHeight();
			axisWidths[i] = chart.getAxis(i).getWidth();
			axisMaxValues[i] = chart.getAxis(i).getMax();
			axisMinValues[i] = chart.getAxis(i).getMin();
			axisTicLabelFontsizes[i] = chart.getAxis(i).getTicLabelFontSize();
			axisActiveFlags[i] = chart.getAxis(i).isActive();
			axisInversionFlags[i] = chart.getAxis(i).isAxisInverted();
		}

		this.lineMap.clear();
		boolean useAlpha = chart.isUseAlpha();
		for (int designIndex = 0; designIndex < getDataSheet().getDesignCount(); designIndex++) // draw
		// all
		// designs
		{
			Design currentDesign = getDataSheet().getDesign(designIndex);
			// log("drawDesigns: currentDesign.isInsideBounds(chart) = "+currentDesign.isInsideBounds(chart));
			if (!currentDesign.isInsideBounds(chart)) // do not draw design if
			// it is not inside
			// bounds of the chart
			{
				log("design not inside bounds, continue");
				continue;
			}
			boolean firstAxisDrawn = false;
			boolean currentDesignClusterActive = true;
			if (currentDesign.getCluster() != null) // determine if design
			// belongs to an active
			// cluster
			{
				currentDesignClusterActive = currentDesign.getCluster().isActive();
			}

			// log("drawDesigns: currentDesignClusterActive = "+currentDesignClusterActive);
			boolean currentDesignActive = true;
			currentDesignActive = currentDesign.isActive(chart); // determine if
			// current
			// design is
			// active
			// log("drawDesigns: currentDesign.isActive(chart) = "+currentDesign.isActive(chart));

			boolean displayDesign; // flag that determines if design will be
			// displayed

			if (chart.isShowOnlySelectedDesigns()) {
				displayDesign = currentDesign.isSelected() && (currentDesignActive || chart.isShowFilteredDesigns()) && (currentDesignClusterActive);
			} else {
				displayDesign = (currentDesignActive || chart.isShowFilteredDesigns()) && (currentDesignClusterActive);
			}

			if (displayDesign) // only draw design if the cluster is active and
			// the design is active (or inactive design
			// drawing is active)
			{
				int lineThickness;
				if ((chart.isShowOnlySelectedDesigns() || !currentDesign.isSelected())&&(!hoverList.contains(currentDesign.getId()))) {
					g.setColor(chart.getDesignColor(currentDesign, currentDesignActive, useAlpha));
					lineThickness = chart.getDesignLineThickness(currentDesign);
					if(designIndex < getDataSheet().getDesignCount()-ExecutionHistory.getInstance().getNumLambdas()-2){
						g.setColor(Color.green);
					}
					else{
						g.setColor(Color.red);
					}
				} else {
					g.setColor(chart.getSelectedDesignColor());
					lineThickness = chart.getSelectedDesignsLineThickness();
				}
				
				
				int xPositionCurrent = getMarginLeft();
				int yPositionCurrent = axisTopPos;
				int xPositionLast = xPositionCurrent;
				int yPositionLast;
				for (int i = 0; i < axisCount; i++) {
					int yPosition = axisTopPos;
					if (axisActiveFlags[i]) {
						double value = currentDesign.getDoubleValue(getDataSheet().getParameter(i));

						int yPositionRelToBottom;
						if (axisRanges[i] == 0) {
							yPositionRelToBottom = (int) (axisHeights[i] * 0.5);
						} else {
							double ratio;
							if (axisInversionFlags[i]) {
								ratio = (axisMaxValues[i] - value) / axisRanges[i];
							} else {
								ratio = (value - axisMinValues[i]) / axisRanges[i];
							}
							yPositionRelToBottom = (int) (axisHeights[i] * ratio);
						}

						yPositionLast = yPositionCurrent;
						yPositionCurrent = yPosition + (axisHeights[i]) - yPositionRelToBottom;

						if (firstAxisDrawn) {
							xPositionCurrent = xPositionCurrent + (int) (axisWidths[i] * 0.5);

							if (lineThickness == 0) {
								g.drawLine(xPositionLast - 3, yPositionLast, xPositionLast + 3, yPositionLast);
								g.drawLine(xPositionCurrent - 3, yPositionCurrent, xPositionCurrent + 3, yPositionCurrent);
							} else {
								for (int t = 1; t <= lineThickness; t++) {
									int deltaY = -((int) (t / 2)) * (2 * (t % 2) - 1);
									g.drawLine(xPositionLast, yPositionLast + deltaY, xPositionCurrent, yPositionCurrent + deltaY);
									int[] key = {xPositionLast, yPositionLast + deltaY, xPositionCurrent, yPositionCurrent + deltaY};

									if (!lineMap.containsKey(key)) {
										lineMap.put(key, new HashSet<Integer>());
									}

									lineMap.get(key).add(currentDesign.getId());
								}
							}

						} else {
							firstAxisDrawn = true;
							if (chart.isShowDesignIDs()) {
								FontMetrics fm = g.getFontMetrics();
								g.setFont(new Font("SansSerif", Font.PLAIN, designLabelFontSize));
								g.drawString(Integer.toString(currentDesign.getId()), xPositionCurrent - 5 - fm.stringWidth(Integer.toString(currentDesign.getId())), yPositionCurrent + (int) (0.5 * chart.getAxis(i).getTicLabelFontSize()));
							}
						}
						xPositionLast = xPositionCurrent;
						xPositionCurrent = xPositionCurrent + (int) (axisWidths[i] * 0.5);
					}
				}
			}
		}
	}

	/**
	 * Draws the axes.
	 * 
	 * @param g
	 *            the graphics object
	 */
	public void drawAxes(Graphics g) {
		ParallelCoordinatesChart chart = ((ParallelCoordinatesChart) this.getChart());
		int xPosition = this.getMarginLeft();
		int yPosition = chart.getAxisTopPos();
		FontMetrics fm = g.getFontMetrics();
		Axis lastAxis = null;
		Axis currentAxis;
		int drawnAxisCount = 0;
		for (int i = 0; i < chart.getAxisCount(); i++) {
			// log("drawing axis "+chart.getAxis(i).getName());
			if (chart.getAxis(i).isActive()) {
				// axes
				currentAxis = chart.getAxis(i);
				if (null != lastAxis) {
					xPosition = xPosition + (int) (lastAxis.getWidth() * 0.5) + (int) (currentAxis.getWidth() * 0.5);
				}

				String axisLabel = currentAxis.getName();
				int slenX = fm.stringWidth(axisLabel);
				g.setFont(new Font("SansSerif", Font.PLAIN, currentAxis.getAxisLabelFontSize()));

				int yLabelOffset = 0;
				if (chart.isVerticallyOffsetAxisLabels()) {
					yLabelOffset = ((drawnAxisCount++) % 2) * (chart.getMaxAxisLabelFontSize() + chart.getAxisLabelVerticalDistance());
				}

				// Axis Mainlabel Drawing

				g.setColor(currentAxis.getAxisLabelFontColor());
				g.drawString(axisLabel, xPosition - (int) (0.5 * slenX), chart.getMaxAxisLabelFontSize() + chart.getTopMargin() + yLabelOffset);

				g.setColor(currentAxis.getAxisColor());
				g.drawLine(xPosition, yPosition, xPosition, yPosition + (chart.getAxisHeight()));

				// Filters

				Filter uf = currentAxis.getUpperFilter();
				Filter lf = currentAxis.getLowerFilter();

				uf.setXPos(xPosition);
				lf.setXPos(xPosition);

				// lower Filter triangle Drawing

				g.setColor(chart.getFilterColor());
				g.drawLine(uf.getXPos(), uf.getYPos(), uf.getXPos() - chart.getFilterWidth(), uf.getYPos() - chart.getFilterHeight());
				g.drawLine(uf.getXPos(), uf.getYPos(), uf.getXPos() + chart.getFilterWidth(), uf.getYPos() - chart.getFilterHeight());
				g.drawLine(uf.getXPos() - chart.getFilterWidth(), uf.getYPos() - chart.getFilterHeight(), uf.getXPos() + chart.getFilterWidth(), uf.getYPos() - chart.getFilterHeight());

				// upper Filter triangle Drawing

				g.drawLine(lf.getXPos(), lf.getYPos(), lf.getXPos() - chart.getFilterWidth(), lf.getYPos() + chart.getFilterHeight());
				g.drawLine(lf.getXPos(), lf.getYPos(), lf.getXPos() + chart.getFilterWidth(), lf.getYPos() + chart.getFilterHeight());
				g.drawLine(lf.getXPos() - chart.getFilterWidth(), lf.getYPos() + chart.getFilterHeight(), lf.getXPos() + chart.getFilterWidth(), lf.getYPos() + chart.getFilterHeight());

				g.setFont(new Font("SansSerif", Font.PLAIN, currentAxis.getTicLabelFontSize()));
				// log("Font size: "+currentAxis.getTicLabelFontSize());
				if ((uf == this.draggedFilter || lf == this.draggedFilter) && currentAxis.getParameter().isNumeric()) {
					g.drawString(String.format(currentAxis.getTicLabelFormat(), this.draggedFilter.getValue()), this.draggedFilter.getXPos() + chart.getFilterWidth() + 4, this.draggedFilter.getYPos() - chart.getFilterHeight());
				}

				// Filteraxis Drawing

				if (null != lastAxis) {
					g.drawLine(lastAxis.getUpperFilter().getXPos(), lastAxis.getUpperFilter().getYPos(), uf.getXPos(), uf.getYPos());
					g.drawLine(lastAxis.getLowerFilter().getXPos(), lastAxis.getLowerFilter().getYPos(), lf.getXPos(), lf.getYPos());
				}

				// tics

				int ticSize = currentAxis.getTicLength();
				int ticCount = currentAxis.getTicCount();
				double ticSpacing; // must be double to avoid large round off
									// errors
				if (ticCount > 1)
					ticSpacing = chart.getAxisHeight() / ((double) (ticCount - 1));
				else
					ticSpacing = 0;
				double axisRange = currentAxis.getRange();
				double ticValueDifference = axisRange / ((double) (ticCount - 1));
				for (int ticID = 0; ticID < ticCount; ticID++) {
					int currentTicYPos;
					if (currentAxis.isAxisInverted())
						currentTicYPos = yPosition + chart.getAxisHeight() - (int) (ticID * ticSpacing);
					else
						currentTicYPos = yPosition + (int) (ticID * ticSpacing);
					g.setColor(currentAxis.getAxisColor());
					if (ticCount > 1)
						g.drawLine(xPosition, currentTicYPos, xPosition + ticSize, currentTicYPos);
					else
						g.drawLine(xPosition, yPosition + (int) (chart.getAxisHeight() / 2), xPosition + ticSize, yPosition + (int) (chart.getAxisHeight() / 2));

					g.setColor(currentAxis.getAxisTicLabelFontColor());

					String ticLabel;
					g.setFont(new Font("SansSerif", Font.PLAIN, currentAxis.getTicLabelFontSize()));
					if (currentAxis.getParameter().isNumeric()) {
						Double ticValue;
						if (ticCount > 1) {
							ticValue = currentAxis.getMax() - ticValueDifference * ticID;
							ticLabel = String.format(currentAxis.getTicLabelFormat(), ticValue);
							g.drawString(ticLabel, xPosition + ticSize + 7, currentTicYPos + (int) (0.5 * currentAxis.getTicLabelFontSize()));
						} else {
							ticValue = currentAxis.getMax();
							ticLabel = String.format(currentAxis.getTicLabelFormat(), ticValue);
							g.drawString(ticLabel, xPosition + 2 * ticSize, yPosition + ((int) (chart.getAxisHeight() / 2)) + (int) (0.5 * currentAxis.getTicLabelFontSize()));
						}

					} else {
						if (ticCount > 1) {
							ticLabel = currentAxis.getParameter().getStringValueOf(currentAxis.getMax() - ticValueDifference * ticID);
							g.drawString(ticLabel, xPosition + 2 * ticSize, currentTicYPos + (int) (0.5 * currentAxis.getTicLabelFontSize()));
						} else {
							ticLabel = currentAxis.getParameter().getStringValueOf(currentAxis.getMax());
							g.drawString(ticLabel, xPosition + 2 * ticSize, yPosition + ((int) (chart.getAxisHeight() / 2)) + (int) (0.5 * currentAxis.getTicLabelFontSize()));
						}
					}
				}

				lastAxis = currentAxis;
			}
		}
	}

	/**
	 * Finds the axis at a given location in the chart.
	 * 
	 * @param x
	 *            the location
	 * @return the found axis
	 */
	private Axis getAxisAtLocation(int x) throws NoAxisFoundException {
		for (int i = 0; i < this.chart.getAxisCount(); i++) {
			Filter uf = this.chart.getAxis(i).getUpperFilter();
			if // check if this axis was meant by the click
			(this.chart.getAxis(i).isActive() && x >= uf.getXPos() - 0.5 * this.chart.getAxis(i).getWidth() && x < uf.getXPos() + 0.5 * this.chart.getAxis(i).getWidth()) {
				return this.chart.getAxis(i);
			}
		}
		throw new NoAxisFoundException(x);
	}

	/**
	 * Finds the new index when dragging an axis to a given x location.
	 * 
	 * @param x
	 *            the location
	 * @return the found index
	 */
	private int getNewAxisIndexAtLocation(int x) throws NoAxisFoundException {
		for (int i = 0; i < this.chart.getAxisCount(); i++) {
			Filter uf = this.chart.getAxis(i).getUpperFilter();
			if // check if this axis was meant by the click
			(this.chart.getAxis(i).isActive() && x < uf.getXPos()) {
				log("getNewAxisIndexAtLocation: returning index " + i);
				return i;
			}
		}
		log("getNewAxisIndexAtLocation: returning index " + this.chart.getAxisCount());
		return this.chart.getAxisCount();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == 1 && updateHoverList(e.getX(), e.getY())) {
			List<Integer> newSelection = new ArrayList<Integer>();
			DataSheet dataSheet = this.chart.getDataSheet();
			for(Integer designId : this.hoverList){
				Design d = dataSheet.getDesignByID(designId);
				d.setSelected(!d.isSelected());
			}
			
			for(int i=0; i<dataSheet.getDesignCount(); i++){
				if(dataSheet.getDesign(i).isSelected())
					newSelection.add(i);
			}
			
			this.mainWindow.getDataSheetTablePanel().setSelectedRows(newSelection);
		}
		else if (e.getButton() == 3) {
			log("mouseClicked: button " + e.getButton());
			int x = e.getX();
			int y = e.getY();

			log("mouseClicked: x " + x);
			log("mouseClicked: y " + y);
			try {
				Axis axis = this.getAxisAtLocation(x);
				log("Clicked on axis " + axis.getName());
				(new ParallelCoordinatesContextMenu(this.mainWindow, this.chartFrame, axis)).show(this, x, y);
			} catch (NoAxisFoundException e1) {
				e1.printStackTrace();
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent e) {
		this.hoverList.clear();
		this.repaint();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent e) {
		ParallelCoordinatesChart chart = ((ParallelCoordinatesChart) this.getChart());
		dragStartX = e.getX();
		dragStartY = e.getY();
		for (int i = 0; i < chart.getAxisCount(); i++) {
			Filter uf = chart.getAxis(i).getUpperFilter();
			Filter lf = chart.getAxis(i).getLowerFilter();
			if // check whether the drag operation started on the upper filter
			(chart.getAxis(i).isActive() && dragStartY >= uf.getYPos() - chart.getFilterHeight() && dragStartY <= uf.getYPos() && dragStartX >= uf.getXPos() - chart.getFilterWidth() && dragStartX <= uf.getXPos() + chart.getFilterWidth()) {
				this.draggedFilter = uf;
				this.dragOffsetY = uf.getYPos() - dragStartY;
				setCursor(new Cursor(Cursor.MOVE_CURSOR));
			} else if // check whether the drag operation started on the lower
						// filter
			(chart.getAxis(i).isActive() && dragStartY >= lf.getYPos() && dragStartY <= lf.getYPos() + chart.getFilterHeight() && dragStartX >= lf.getXPos() - chart.getFilterWidth() && dragStartX <= lf.getXPos() + chart.getFilterWidth()) {
				this.draggedFilter = lf;
				this.dragOffsetY = lf.getYPos() - dragStartY;
				setCursor(new Cursor(Cursor.MOVE_CURSOR));
			}
		}
		if (this.draggedFilter == null){
			this.storeBufferedImage();
			if ( e.getButton() == 1 && withinAxisDragZone(e.getX()) > -1) {
				try {
					this.draggedAxis = this.getAxisAtLocation(dragStartX);
					setCursor(new Cursor(Cursor.MOVE_CURSOR));
					log("mousePressed: Drag started, dragged axis : " + this.draggedAxis.getName());
				} catch (NoAxisFoundException e1) {
					log("No axis found. ");
					if (this.printLog) {
						e1.printStackTrace();
					}
				}
			} else if (e.getButton() == 1) {
				this.hoverList.clear();
				this.dragSelecting = true;
			}
		}
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent e) {
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

		boolean repaintRequired = false;
		if (this.draggedFilter != null) {
			repaintRequired = true;
			this.draggedFilter = null;
		}
		if (this.draggedAxis != null) {
			repaintRequired = true;
			try {
				int newIndex = this.getNewAxisIndexAtLocation(e.getX() - 1); // column index starts at  one, param index at 0
				DataSheetTableColumnModel cm = (DataSheetTableColumnModel) this.mainWindow.getDataSheetTablePanel().getDataTable().getColumnModel();
				int currentIndex = this.mainWindow.getDataSheet().getParameterIndex(this.draggedAxis.getName()) + 1;
				log("mouseReleased: dragged axis " + this.draggedAxis.getName() + " had index " + currentIndex);
				if (newIndex > currentIndex) {
					cm.moveColumn(currentIndex, newIndex);
				} else if (newIndex < currentIndex) {
					cm.moveColumn(currentIndex, newIndex + 1);
				}
			} catch (NoAxisFoundException e1) {
				e1.printStackTrace();
			}
			this.draggedAxis = null;
		}
		if (dragSelecting) {
			repaintRequired = true;
			dragSelecting = false;

			Rectangle rec = getSelectionRectangle();

			dragStartX = dragStartY = dragCurrentX = dragOffsetY = 0;

			if (rec.x > 1 && rec.y > 1) {
				for (int i = 0; i < chart.getAxisCount(); i++) {
					if(!chart.getAxis(i).isActive()){
						continue;
					}
					Filter uf = chart.getAxis(i).getUpperFilter();
					Filter lf = chart.getAxis(i).getLowerFilter();

					if (rec.contains(uf.getXPos(), rec.y + 1)) {
						log("axis " + chart.getAxis(i).getName() + " is in selection");
						uf.setYPos(rec.y);
						lf.setYPos(rec.y + rec.height);
					}
				}
			}
		}
		if (repaintRequired) {
			this.mainWindow.repaintAllChartFrames();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	public void mouseMoved(MouseEvent e) {
		boolean needRepaint = false;

		if (withinAxisDragZone(e.getX()) > -1) {
			setCursor(new Cursor(Cursor.HAND_CURSOR));
		} else {
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}

		if (this.hoverList.size() > 0) {
			needRepaint = true;
		}

		this.hoverList.clear();

		if (withinAxisDragZone(e.getX()) < 0) {
			needRepaint = needRepaint || updateHoverList(e.getX(), e.getY());
		}

		if (needRepaint) {
			this.repaint();
		}
	}

	/**
	 * updates the hover list
	 * @param x the current mouse x position
	 * @param y the current mouse y position
	 * @return whether lines were added to the list
	 */
	private boolean updateHoverList(int x, int y) {
		boolean designsFound = false;
		this.hoverList.clear();
		for (Map.Entry<int[], HashSet<Integer>> mapEntry : this.lineMap.entrySet()) {
			int[] coords = mapEntry.getKey();
			HashSet<Integer> designIDs = mapEntry.getValue();

			int xStart = coords[0];
			int yStart = coords[1];
			int xEnd = coords[2];
			int yEnd = coords[3];

			if (x > xStart && x < xEnd && Math.min(yStart, yEnd) < y && Math.max(yStart, yEnd) > y) {
				int xDelta = xEnd - xStart;
				int xMiddle = xStart + (xDelta / 2);

				int yDelta = Math.abs(yEnd - yStart) / 2;
				int yMiddle = yEnd > yStart ? yStart + yDelta : yEnd + yDelta;

				int designCount = designIDs.size();
				int yOffset = (int) ((100. * (double) designCount) / (double) this.chart.getDataSheet().getDesignCount());

				int[] xPoints = new int[4];
				int[] yPoints = new int[4];

				// start
				xPoints[0] = xStart;
				yPoints[0] = yStart;

				// top
				xPoints[1] = xMiddle;
				yPoints[1] = yMiddle - yOffset - 2;

				// end
				xPoints[2] = xEnd;
				yPoints[2] = yEnd;

				// low
				xPoints[3] = xMiddle;
				yPoints[3] = yMiddle + yOffset + 2;

				Polygon poly = new Polygon(xPoints, yPoints, xPoints.length);

				if (poly.contains(x, y)) {
					this.hoverList.addAll(designIDs);
					designsFound = true;
				}
			}
		}
		return designsFound;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent
	 * )
	 */
	public void mouseDragged(MouseEvent e) {
		if (this.draggedFilter != null) {
			// try to make the filter follow the drag operation, but always keep
			// it within axis boundaries and opposite filter
			this.draggedFilter.setYPos(Math.max(Math.min(e.getY() + this.dragOffsetY, this.draggedFilter.getLowestPos()), this.draggedFilter.getHighestPos()));
			log("ChartPanel: Dragging filter on axis " + this.draggedFilter.getAxis().getName());
			log("ChartPanel: Related parameter name is " + this.draggedFilter.getAxis().getParameter().getName());
			repaint();
		} else if (this.draggedAxis != null) {
			this.dragCurrentX = e.getX();
			this.repaint();
		} else if (dragSelecting) {
			this.dragCurrentX = e.getX();
			this.dragOffsetY = e.getY();
			this.repaint();
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int modifier = e.getModifiers();
		if (modifier == 0) {
			this.chart.incrementAxisWidth(-e.getUnitsToScroll());
		} else if (modifier == 2) {
			int x = e.getX();
			try {
				Axis axis = this.getAxisAtLocation(x);
				log("wheeled on axis " + axis.getName());
				axis.setWidth(Math.max(0, axis.getWidth() - e.getUnitsToScroll()));
			} catch (NoAxisFoundException e1) {
			}
		}

		else if (modifier == 8) {
			int x = e.getX();
			try {
				Axis axis = this.getAxisAtLocation(x);
				log("wheeled on axis " + axis.getName());
				axis.setTicCount(Math.max(2, axis.getTicCount() - e.getWheelRotation()));
			} catch (NoAxisFoundException e1) {
			}
		}
		this.repaint();

	}

	/**
	 * Writes the current chart state to the temporary buffered image.
	 */
	public void storeBufferedImage() {
		this.bufferedImage = new BufferedImage(chartFrame.getChartPanel().getWidth(), chartFrame.getChartPanel().getHeight(), BufferedImage.TYPE_INT_ARGB);
		;
		Graphics g = this.bufferedImage.createGraphics();
		this.paintComponent(g);
		g.dispose();
	}

	/**
	 * Prints debug information to stdout when printLog is set to true.
	 * 
	 * @param message
	 *            the message
	 */
	private void log(String message) {
		if (ParallelCoordinatesChartPanel.printLog && Main.isLoggingEnabled()) {
			System.out.println(this.getClass().getName() + "." + message);
		}
	}

}
