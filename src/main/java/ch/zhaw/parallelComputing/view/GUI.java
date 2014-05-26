package ch.zhaw.parallelComputing.view;

import ch.zhaw.parallelComputing.controller.ProjectLauncher;
import ch.zhaw.parallelComputing.model.Computation;
import sun.launcher.resources.launcher;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

/**
 * Created by Max Schrimpf
 */
public class GUI extends JFrame {
    private JPanel rootPanel;
    private JTextPane logPane;
    private JScrollPane logScrollPane;
    private JRadioButton showColumnsRadioButton;
    private JRadioButton evaluateRadioButton;
    private JButton selectInputButton;
    private JTextArea logArea;
    private JButton startButton;
    private JLabel selectedFileLabel;

    private final ProjectLauncher launcher;
    private final Computation comp;
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

    public GUI(String currentFile, Computation comp, ProjectLauncher launcher) {
        super("Seminar paralell computing");
        addWindowListener(exitListener);
        this.currentFile = currentFile;
        this.comp = comp;
        this.launcher = launcher;
        GUI.this.selectedFileLabel.setText(currentFile);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(GUI.this.currentFile != null) {
                    startButton.setEnabled(false);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            GUI.this.comp.start(GUI.this.currentFile);
                        }
                    }).start();
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
                chooser.addChoosableFileFilter(filter);
                chooser.setMultiSelectionEnabled(false);
                int option = chooser.showOpenDialog(GUI.this);

                if (option == JFileChooser.APPROVE_OPTION) {
                    String file = chooser.getSelectedFile().getAbsolutePath();
                    GUI.this.println("Input selected: " + file);
                    GUI.this.currentFile = file;
                    GUI.this.selectedFileLabel.setText(file);
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

    public void println(final String text) {
        SwingUtilities.invokeLater(new Runnable() {

            /** {@inheritDoc} */
            @Override
            public void run() {
                GUI.this.logArea.append(text);
            }
        });
    }
}
