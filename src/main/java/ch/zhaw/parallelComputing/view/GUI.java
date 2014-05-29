package ch.zhaw.parallelComputing.view;

import ch.zhaw.parallelComputing.controller.ProjectLauncher;
import ch.zhaw.parallelComputing.model.CSVHandler;
import ch.zhaw.parallelComputing.model.sentiment.SentimentComputation;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Max Schrimpf
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

    private final ProjectLauncher launcher;
    private final SentimentComputation comp;
    private String currentInputFile;
    private String currentComparisonFile;

    private final WindowListener exitListener = new WindowAdapter() {

        @Override
        public void windowClosing(WindowEvent e) {
            int confirm = 1;
            if(comp.isResults()){
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

    public GUI(String currentInputFile, SentimentComputation comp, ProjectLauncher launcher) {
        super("Seminar paralell computing");

        addWindowListener(exitListener);
        this.currentInputFile = currentInputFile;
        this.comp = comp;
        this.launcher = launcher;
        this.selectInputButton.setText(currentInputFile);

        setStartButtonListener();
        setSelectInputButtonListener();
        setSelectComparisonButtonListener();

        setContentPane(rootPanel);
        setSize(new Dimension(800, 300));
        setResizable(false);
        logArea.setEditable(false);
        setLocationRelativeTo(null);
        pack();
        setVisible(true);
    }

    private void setStartButtonListener() {
        startButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(GUI.this.currentInputFile != null) {
                    if(evaluateRadioButton.isSelected()) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                GUI.this.startButton.setEnabled(false);
                                GUI.this.comp.start(GUI.this.currentInputFile);
                            }
                        }).start();
                    } else if (showColumnsRadioButton.isSelected()) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                DecimalFormat df = new DecimalFormat("000");
                                List<String> headers = CSVHandler.getHeaders(GUI.this.currentInputFile);

                                for(int i = 0; i < headers.size(); i++) {
                                    GUI.this.println(df.format(i) + " = " + headers.get(i));
                                }
                            }
                        }).start();
                    } else if (compareRadioButton.isSelected()) {
                        GUI.this.println("Compare with: " + currentComparisonFile );
//                            Plotter.plot("lulZ", CSVHandler.getDataset());
//                            GUI.this.currentInputFile = file;
                    } else {
                        GUI.this.println("No action selected");
                    }
                } else {
                    GUI.this.println("No input file selected");
                }
            }
        });
    }

    private void setSelectComparisonButtonListener() {
        selectCompareButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String compFile = getCSVFromDialog("Select comparison File");
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
                String compFile = getCSVFromDialog("Select input File");
                if(compFile != null) {
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

    public void println(final String text, final boolean newline){
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
}
