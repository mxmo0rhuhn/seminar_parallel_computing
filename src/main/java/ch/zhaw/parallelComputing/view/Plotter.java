/*
 * Copyright (c) 2014 Max Schrimpf
 *
 * This file is part of the parallel computing term paper for the Zurich university of applied sciences.
 *
 * It is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package ch.zhaw.parallelComputing.view;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Wrapper for the plotting library.
 *
 * @author Max Schrimpf
 */
public class Plotter {

    /**
     * Creates a PNG plot of the given data set
     * @param title the name of the plot picture
     * @param dataset1 the first data set for plotting
     * @param dataset2 the second data set for plotting
     */
    public static void getPlotAsPNG(String title, XYDataset dataset1, XYDataset dataset2) {
        JFreeChart chart = plot(title, dataset1, dataset2);
        try {
            ChartUtilities.saveChartAsPNG(new File(title + ".png"), chart, 1920, 1080);
        } catch (IOException e) {
            System.err.println("Problem occurred creating chart.");
        }
    }

    /**
     * Displays a plot of the given data sets as a dialog with a save as PNG option
     * @param parent component to set the dialog relative to
     * @param title the name of the Plot and the header of the dialog
     * @param dataset1 the first data set for plotting
     * @param dataset2 the second data set for plotting
     */
    public static void plotWithDialog(Component parent, String title, XYDataset dataset1, XYDataset dataset2) {
        JFreeChart chart = plot(title, dataset1, dataset2);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(1024, 768));
        chartPanel.setMouseZoomable(true, false);

        String[] options = {"Print", "OK"};
        int response = JOptionPane.showOptionDialog(
                parent                       // Center in window.
                , chartPanel
                , title                      // Title in titlebar
                , JOptionPane.YES_NO_OPTION  // Option type
                , JOptionPane.PLAIN_MESSAGE  // messageType
                , null                       // Icon (none)
                , options                     // Button text as above.
                , "OK"                        // Default button
        );

        if (response == 0) {
            getPlotAsPNG(title, dataset1, dataset2);
        }
    }

    /**
     * Generates a plot for comparing to datasets
     * @param title the name of the plot
     * @param dataset1 the first data set for plotting
     * @param dataset2 the second data set for plotting
     * @return a chart object for displaying or saving as picture
     */
    public static JFreeChart plot(String title, XYDataset dataset1, XYDataset dataset2) {

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                title,                  // title
                "Date",                 // x-axis label
                "Input Value",          // y-axis label
                dataset1,               // data
                true,                   // create legend?
                true,                   // generate tooltips?
                false                   // generate URLs?
        );

        XYPlot plot = chart.getXYPlot();

        Axis axis1 = plot.getRangeAxis(0);
        axis1.setLabelPaint(Color.red);

        NumberAxis axis2 = new NumberAxis("Twitter");
        axis2.setLabelPaint(Color.blue);
        plot.setRangeAxis(1, axis2);
        plot.setDataset(1, dataset2);
        plot.mapDatasetToRangeAxis(1, 1);

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.blue);
        renderer.setBaseShapesVisible(false);
        plot.setRenderer(1, renderer);

        return chart;
    }
}
