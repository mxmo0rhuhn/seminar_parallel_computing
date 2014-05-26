package ch.zhaw.parallelComputing.view;

import org.apache.xpath.operations.Bool;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;


/**
 * Displays the console in a own window
 *
 * @author Max Schrimpf
 */
@SuppressWarnings("serial")
// Wird nicht Serialisiert
public class ConsoleOutput extends JFrame {
    private JTextArea textArea;
    private JPanel panel;
    private final boolean active;

    /**
     * Creates a new window for console logging.
     */
    public ConsoleOutput(boolean active) {
        this.active = active;
        if(active)  {
            start();
        }
    }

    private void start() {
        this.textArea = new JTextArea();
        this.textArea.setEditable(false);

        this.textArea.setFont(new Font( Font.MONOSPACED, Font.PLAIN, 18 ));
        this.panel = new JPanel(new BorderLayout());
        this.panel.add(new JScrollPane(this.textArea), BorderLayout.CENTER);

        add(this.panel);

        setTitle("Log");
        setVisible(true);
    }

    /**
     * Displays some text upon the virtual console
     *
     * @param text
     *            The text to display
     */
}