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

package org.xdat.gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.xdat.Main;
import org.xdat.gui.frames.ChartFrame;

/**
 * Closing adapter for the {@link org.xdat.gui.frames.ChartFrame}.
 */
public class ChartFrameClosingAdapter extends WindowAdapter {

	/** The main window. */
	private Main mainWindow;

	/** The chart frame. */
	private ChartFrame chartFrame;

	/**
	 * Instantiates a new chart frame closing adapter.
	 * 
	 * @param chartFrame
	 *            the chart frame
	 * @param mainWindow
	 *            the main window
	 */
	public ChartFrameClosingAdapter(ChartFrame chartFrame, Main mainWindow) {
		super();
		this.mainWindow = mainWindow;
		this.chartFrame = chartFrame;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
	 */
	public void windowClosing(WindowEvent event) {
		mainWindow.removeChartFrame(chartFrame);
		chartFrame.setVisible(false);
		chartFrame.dispose();
	}
}
