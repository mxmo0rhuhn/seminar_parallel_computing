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

import ch.zhaw.parallelComputing.model.CSVHandler;
import ch.zhaw.parallelComputing.model.sentiment.FileIterator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;

/**
 * Dialog for all needed options to run a plotting job.
 *
 * @author Max Schrimpf
 */
public class ComparisonDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField resultDateFormat;
    private JTextField comparisonDateFormat;
    private JLabel infoLabel;
    private final String resultFile;
    private final String comparisonFile;
    private final GUI parent;

    private String resultFileFormatString;
    private String comparisonFileFormatString;

    public ComparisonDialog(GUI parent, String resultFile, String resultFileFormatString,
                            String currentComparisonFile, String comparisonFileFormatString) {
        setTitle("Configure Comparison");
        setContentPane(contentPane);
        this.resultFile = resultFile;
        this.resultFileFormatString = resultFileFormatString;
        this.comparisonFile = currentComparisonFile;
        this.comparisonFileFormatString = comparisonFileFormatString;
        this.parent = parent;

        setLocationRelativeTo(parent);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        resultDateFormat.setText(resultFileFormatString);
        comparisonDateFormat.setText(comparisonFileFormatString);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
// add your code here
        if (FileIterator.isValidDateFormat(resultDateFormat.getText())
                && FileIterator.isValidDateFormat(comparisonDateFormat.getText())) {
            resultFileFormatString = resultDateFormat.getText();
            comparisonFileFormatString = comparisonDateFormat.getText();

            if (parent != null) {
                parent.setGetCurrentComparisonFileDateFormat(comparisonFileFormatString);
            }

            Plotter.plot(parent, "Comparison",
                    CSVHandler.getDataset(comparisonFile, new SimpleDateFormat(comparisonFileFormatString)),
                    CSVHandler.getDataset(resultFile, new SimpleDateFormat(resultFileFormatString)));
            dispose();
        } else {
            infoLabel.setText("Not a valid date format");
            if (!FileIterator.isValidDateFormat(resultDateFormat.getText())) {
                resultDateFormat.setForeground(Color.red);
            } else {
                resultDateFormat.setForeground(Color.green);
            }
            if (!FileIterator.isValidDateFormat(comparisonDateFormat.getText())) {
                comparisonDateFormat.setForeground(Color.red);
            } else {
                comparisonDateFormat.setForeground(Color.green);
            }
        }
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }
}
