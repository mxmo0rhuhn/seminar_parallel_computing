package ch.zhaw.parallelComputing.view;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by Max Schrimpf
 */

public class Plotter {

    public static void main(String[] args) {
        XYDataset dataset1 = createDataset();
        XYDataset dataset2 = createDataset2();
        new Plotter().plot("Test", dataset1, dataset2);
    }

    private static void plot(String title, XYDataset dataset1, XYDataset dataset2) {

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Comparison",           // title
                "Date",                 // x-axis label
                "Input Value",          // y-axis label
                dataset1,               // data
                true,                   // create legend?
                true,                   // generate tooltips?
                false                   // generate URLs?
        );

        XYPlot plot = chart.getXYPlot();
        plot.setDataset(1, dataset2);

        Axis axis1 = plot.getRangeAxis(0);
        axis1.setLabelPaint(Color.red);

        NumberAxis axis2 = new NumberAxis("Twitter");
        axis2.setLabelPaint(Color.blue);
        plot.setRangeAxis(1, axis2);

        XYLineAndShapeRenderer renderer =  new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.blue);
        renderer.setBaseShapesVisible(false);
        plot.setRenderer(1, renderer);


        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(1024, 768));
        chartPanel.setMouseZoomable(true, false);

        String[] options = {"Print" , "OK"};
        int response = JOptionPane.showOptionDialog(
                null                       // Center in window.
                , chartPanel
                , title                      // Title in titlebar
                , JOptionPane.YES_NO_OPTION  // Option type
                , JOptionPane.PLAIN_MESSAGE  // messageType
                , null                       // Icon (none)
                , options                     // Button text as above.
                , "OK"                        // Default button
        );

        if(response == 0) {
            try {
                ChartUtilities.saveChartAsPNG(new File(title + ".png"), chart, 1920, 1080);
            } catch (IOException e) {
                System.err.println("Problem occurred creating chart.");
            }
        }
    }

    private static XYDataset createDataset() {
        TimeSeries s1 = new TimeSeries("L&G European Index Trust", Month.class);
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        s1.add(new Month(2, 2001), 181.8);
        s1.add(new Month(3, 2001), 167.3);
        s1.add(new Month(4, 2001), 153.8);
        s1.add(new Month(5, 2001), 167.6);
        s1.add(new Month(6, 2001), 158.8);
        s1.add(new Month(7, 2001), 148.3);
        s1.add(new Month(8, 2001), 153.9);
        s1.add(new Month(9, 2001), 142.7);
        s1.add(new Month(10, 2001), 123.2);
        s1.add(new Month(11, 2001), 131.8);
        s1.add(new Month(12, 2001), 139.6);
        s1.add(new Month(1, 2002), 142.9);
        s1.add(new Month(2, 2002), 138.7);
        s1.add(new Month(3, 2002), 137.3);
        s1.add(new Month(4, 2002), 143.9);
        s1.add(new Month(5, 2002), 139.8);
        s1.add(new Month(6, 2002), 137.0);
        s1.add(new Month(7, 2002), 132.8);
        dataset.addSeries(s1);
        return dataset;
    }
    private static XYDataset createDataset2() {
        TimeSeries s2 = new TimeSeries("Twitter" , Month.class);
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        s2.add(new Month(2, 2001), 129.6);
        s2.add(new Month(3, 2001), 123.2);
        s2.add(new Month(4, 2001), 117.2);
        s2.add(new Month(5, 2001), 124.1);
        s2.add(new Month(6, 2001), 122.6);
        s2.add(new Month(7, 2001), 119.2);
        s2.add(new Month(8, 2001), 116.5);
        s2.add(new Month(9, 2001), 112.7);
        s2.add(new Month(10, 2001), 101.5);
        s2.add(new Month(11, 2001), 106.1);
        s2.add(new Month(12, 2001), 110.3);
        s2.add(new Month(1, 2002), 111.7);
        s2.add(new Month(2, 2002), 111.0);
        s2.add(new Month(3, 2002), 109.6);
        s2.add(new Month(4, 2002), 113.2);
        s2.add(new Month(5, 2002), 111.6);
        s2.add(new Month(6, 2002), 108.8);
        s2.add(new Month(7, 2002), 101.6);
        dataset.addSeries(s2);
        return dataset;
    }
}
