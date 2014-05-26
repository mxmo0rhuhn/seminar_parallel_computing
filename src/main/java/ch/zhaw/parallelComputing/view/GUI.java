package ch.zhaw.parallelComputing.view;

import ch.zhaw.parallelComputing.model.Computation;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    private final Computation comp;
    private String currentFile;

    public void enableStartButton() {
        startButton.setEnabled(true);
    }
    public GUI(Computation comp ) {
        super("Seminar paralell computing");
        this.comp = comp;
        currentFile = null;

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(currentFile != null) {
                    startButton.setEnabled(false);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            GUI.this.comp.start(currentFile);
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
                //FileNameExtensionFilter filter = new FileNameExtensionFilter("csv");
                //chooser.addChoosableFileFilter(filter);
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
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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
                GUI.this.logArea.append(text + "\n");
            }
        });
    }
}
