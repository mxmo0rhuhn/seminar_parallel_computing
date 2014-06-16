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

import ch.zhaw.parallelComputing.controller.ProjectLauncher;
import ch.zhaw.parallelComputing.model.CSVHandler;
import ch.zhaw.parallelComputing.model.sentiment.FileIterator;
import ch.zhaw.parallelComputing.model.sentiment.SentimentComputation;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

/**
 * GUI of the application. Controls the workflow and displays the dialogs for all needed user interactions.
 *
 * @author Max Schrimpf
 */
public class GUI extends JFrame {
    private JPanel rootPanel;
    private JRadioButton showColumnsRadioButton;
    private JRadioButton evaluateRadioButton;
    private JRadioButton compareRadioButton;
    private JScrollPane logScrollPane;
    private JTextArea logArea;
    private JButton startButton;
    private JButton selectInputButton;
    private JButton selectCompareButton;
    private JButton selectResultButton;

    private FileIterator iterator;
    private final ProjectLauncher launcher;
    private final SentimentComputation comp;
    private String currentInputFile;
    private String currentResultFile;
    private String currentComparisonFile;

    private String currentComparisonFileDateFormat;

    private final WindowListener exitListener = new WindowAdapter() {

        @Override
        public void windowClosing(WindowEvent e) {
            int confirm = 1;
            if (comp.hasResults()) {
                confirm = 0;
            } else {
                confirm = JOptionPane.showOptionDialog(GUI.this, "A computation is running - Are You Sure to Close Application?", "Exit Confirmation",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
            }
            if (confirm == 0) {
                launcher.exit();
            }
        }
    };

    public GUI(FileIterator iterator, String currentInputFile, String currentResultFile, String currentComparisonFile,
               String comparisonFileDateFormat, SentimentComputation comp, ProjectLauncher launcher) {
        super("Seminar paralell computing");

        addWindowListener(exitListener);
        this.currentInputFile = currentInputFile;
        this.currentResultFile = currentResultFile;
        this.currentComparisonFile = currentComparisonFile;
        this.comp = comp;
        this.launcher = launcher;
        this.iterator = iterator;
        this.selectInputButton.setText(currentInputFile);
        this.selectResultButton.setText(currentResultFile);
        this.selectCompareButton.setText(currentComparisonFile);
        this.currentComparisonFileDateFormat = comparisonFileDateFormat;

        setStartButtonListener();
        setSelectInputButtonListener();
        setSelectResultButtonListener();
        setSelectComparisonButtonListener();

        setContentPane(rootPanel);
        setSize(new Dimension(800, 300));
        setResizable(false);
        logArea.setEditable(false);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        getRootPane().setDefaultButton(startButton);
        startButton.requestFocus();
    }

    private void setStartButtonListener() {
        startButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (GUI.this.currentInputFile != null) {
                    if (showColumnsRadioButton.isSelected()) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                DecimalFormat df = new DecimalFormat("000");
                                List<String> headers = CSVHandler.getHeaders(GUI.this.currentInputFile);

                                MapAttributesDialog dialog = new MapAttributesDialog(GUI.this, GUI.this.iterator, headers);

                                FileIterator newIterator = dialog.getIterator();
                                if (newIterator != null) {
                                    GUI.this.iterator = newIterator;
                                    GUI.this.showColumnsRadioButton.setSelected(false);
                                    GUI.this.evaluateRadioButton.setSelected(true);
                                } else {
                                    System.out.println("Could not get Configuration");
                                }
                            }
                        }).start();
                    } else if (evaluateRadioButton.isSelected()) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                GUI.this.startButton.setEnabled(false);
                                GUI.this.evaluateRadioButton.setSelected(false);
                                GUI.this.compareRadioButton.setSelected(true);
                                GUI.this.iterator.setFile(currentInputFile);
                                GUI.this.comp.start(GUI.this.iterator, currentResultFile);
                            }
                        }).start();
                    } else if (compareRadioButton.isSelected()) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                GUI.this.println("Compare with: " + currentComparisonFile);

                                ComparisonDialog dialog = new ComparisonDialog(GUI.this,
                                        GUI.this.currentResultFile, GUI.this.iterator.getTargetFormatString(),
                                        GUI.this.currentComparisonFile, GUI.this.currentComparisonFileDateFormat);
                            }
                        }).start();
                    } else {
                        GUI.this.println("No action selected");

                    }
                } else {
                    GUI.this.println("No input file selected");
                }
            }
        });
    }

    private void setSelectResultButtonListener() {
        selectResultButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String resultFile = getCSVFromDialog("Select result file");
                if (resultFile != null) {
                    GUI.this.println("Write result to: " + resultFile);
                    GUI.this.selectResultButton.setText(resultFile);
                    GUI.this.currentResultFile = resultFile;
                } else {
                    GUI.this.println("Canceled");
                }

            }
        });
    }

    private void setSelectComparisonButtonListener() {
        selectCompareButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String compFile = getCSVFromDialog("Select comparison file");
                if (compFile != null) {
                    GUI.this.println("Compare with: " + compFile);
                    GUI.this.selectCompareButton.setText(compFile);
                    GUI.this.currentComparisonFile = compFile;
                } else {
                    GUI.this.println("Canceled");
                }

            }
        });
    }

    private void setSelectInputButtonListener() {
        selectInputButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String compFile = getCSVFromDialog("Select input file");
                if (compFile != null) {
                    GUI.this.println("Input file: " + compFile);
                    GUI.this.selectInputButton.setText(compFile);
                    GUI.this.currentInputFile = compFile;
                } else {
                    GUI.this.println("Canceled");
                }
            }
        });
    }

    private String getCSVFromDialog(String title) {
        File workingDirectory = new File(System.getProperty("user.dir"));
        JFileChooser chooser = new JFileChooser(title);
        chooser.setCurrentDirectory(workingDirectory);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");
        chooser.setFileFilter(filter);
        chooser.setMultiSelectionEnabled(false);
        int option = chooser.showOpenDialog(this);

        if (option == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile().getAbsolutePath();
        }
        return null;
    }

    public void println(String line) {
        println(line, true);
    }

    public void println(final String text, final boolean newline) {
        SwingUtilities.invokeLater(new Runnable() {

            /** {@inheritDoc} */
            @Override
            public void run() {
                if (newline) {
                    GUI.this.logArea.append(text + System.getProperty("line.separator"));
                } else {
                    GUI.this.logArea.append(text);
                }
            }
        });
    }

    public void enableStartButton() {
        startButton.setEnabled(true);
    }

    public void setCurrentComparisonFileDateFormat(String currentComparisonFileDateFormat) {
        this.currentComparisonFileDateFormat = currentComparisonFileDateFormat;
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        rootPanel = new JPanel();
        rootPanel.setLayout(new BorderLayout(0, 0));
        rootPanel.setBorder(BorderFactory.createTitledBorder(""));
        logScrollPane = new JScrollPane();
        logScrollPane.setFont(new Font("Monospaced", logScrollPane.getFont().getStyle(), 12));
        rootPanel.add(logScrollPane, BorderLayout.CENTER);
        logScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), null));
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setLineWrap(false);
        logArea.setText("");
        logScrollPane.setViewportView(logArea);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(4, 4, new Insets(0, 0, 0, 0), -1, -1));
        rootPanel.add(panel1, BorderLayout.NORTH);
        selectInputButton = new JButton();
        selectInputButton.setHideActionText(false);
        selectInputButton.setHorizontalTextPosition(0);
        selectInputButton.setText("Select input file");
        panel1.add(selectInputButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        selectCompareButton = new JButton();
        selectCompareButton.setHideActionText(true);
        selectCompareButton.setHorizontalTextPosition(0);
        selectCompareButton.setText("Select comparison file");
        panel1.add(selectCompareButton, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        compareRadioButton = new JRadioButton();
        compareRadioButton.setText("Compare ");
        panel1.add(compareRadioButton, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        evaluateRadioButton = new JRadioButton();
        evaluateRadioButton.setSelected(false);
        evaluateRadioButton.setText("Determine Sentiment");
        panel1.add(evaluateRadioButton, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(399, 28), null, 0, false));
        showColumnsRadioButton = new JRadioButton();
        showColumnsRadioButton.setSelected(true);
        showColumnsRadioButton.setText("Select Columns");
        panel1.add(showColumnsRadioButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(399, 28), null, 0, false));
        startButton = new JButton();
        startButton.setText("Start");
        panel1.add(startButton, new GridConstraints(3, 2, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        selectResultButton = new JButton();
        selectResultButton.setText("Select result file");
        panel1.add(selectResultButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        ButtonGroup buttonGroup;
        buttonGroup = new ButtonGroup();
        buttonGroup.add(evaluateRadioButton);
        buttonGroup.add(showColumnsRadioButton);
        buttonGroup.add(compareRadioButton);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return rootPanel;
    }
}
