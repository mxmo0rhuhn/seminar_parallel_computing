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
    private String currentFile;

    public void enableStartButton() {
        startButton.setEnabled(true);
    }

    WindowListener exitListener = new WindowAdapter() {

        @Override
        public void windowClosing(WindowEvent e) {
            int confirm = 1;
            if(comp.isResults()){
               confirm = 0;
            } else {
                confirm = JOptionPane.showOptionDialog(GUI.this, "A computation is running - Are You Sure to Close Application?", "Exit Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
            }
            if (confirm == 0) {
                launcher.exit();
            }
        }
    };

    public GUI(String currentFile, SentimentComputation comp, ProjectLauncher launcher) {
        super("Seminar paralell computing");
        addWindowListener(exitListener);
        this.currentFile = currentFile;
        this.comp = comp;
        this.launcher = launcher;
        GUI.this.selectInputButton.setText(currentFile);

        startButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(GUI.this.currentFile != null) {
                    if(evaluateRadioButton.isSelected()) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                GUI.this.startButton.setEnabled(false);
                                GUI.this.comp.start(GUI.this.currentFile);
                            }
                        }).start();
                    } else if (showColumnsRadioButton.isSelected()) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                DecimalFormat df = new DecimalFormat("000");
                                List<String> headers = CSVHandler.getHeaders(GUI.this.currentFile);

                                for(int i = 0; i < headers.size(); i++) {
                                    GUI.this.println(df.format(i) + " = " + headers.get(i));
                                }
                            }
                        }).start();
                    } else if (compareRadioButton.isSelected()) {
                        File workingDirectory = new File(System.getProperty("user.dir"));
                        JFileChooser chooser = new JFileChooser();
                        chooser.setDialogTitle("Compare with");
                        chooser.setCurrentDirectory(workingDirectory);
                        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");
                        chooser.setFileFilter(filter);
                        chooser.setMultiSelectionEnabled(false);
                        int option = chooser.showOpenDialog(GUI.this);

                        if (option == JFileChooser.APPROVE_OPTION) {
                            String file = chooser.getSelectedFile().getAbsolutePath();
                            GUI.this.println("Compare with: " + file);
//                            Plotter.plot("lulZ", CSVHandler.getDataset());
//                            GUI.this.currentFile = file;
                        } else {
                            GUI.this.println("Canceled");
                        }
                    } else {
                        GUI.this.println("No Action selected");
                    }
                } else {
                    GUI.this.println("No File selected");
                }
            }
        });

        selectInputButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                File workingDirectory = new File(System.getProperty("user.dir"));
                JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(workingDirectory);
                FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");
                chooser.setFileFilter(filter);
                chooser.setMultiSelectionEnabled(false);
                int option = chooser.showOpenDialog(GUI.this);

                if (option == JFileChooser.APPROVE_OPTION) {
                    String file = chooser.getSelectedFile().getAbsolutePath();
                    GUI.this.println("Input selected: " + file);
                    GUI.this.currentFile = file;
                    GUI.this.selectInputButton.setText(file);
                } else {
                    GUI.this.println("Canceled");
                }
            }
        });

        setContentPane(rootPanel);
        setSize(new Dimension(800, 300));
        setResizable(false);
        setLocationRelativeTo(null);
        pack();
        setVisible(true);
    }

    private void createUIComponents() {
        this.logArea.setEditable(false);
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
}
