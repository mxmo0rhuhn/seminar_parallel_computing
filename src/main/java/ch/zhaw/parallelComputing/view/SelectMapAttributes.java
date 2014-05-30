package ch.zhaw.parallelComputing.view;

import ch.zhaw.parallelComputing.model.sentiment.FileIterator;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class SelectMapAttributes extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField dateOutField;
    private JTextField dateInField;
    private JComboBox dateSelector;
    private JComboBox textSelector;
    private JLabel infoField;
    private JCheckBox loggingCheckBox;
    private JTextField logfileNameField;
    private JScrollPane loggingPane;
    private JList logList;
    private JTextField offsetField;
    private CheckBoxList loggingList;

    private boolean inputSelected = false;
    private final List<String> possibleFields;

    private int tsdIndex = 0;
    private int tweetIndex = 0;

    private SimpleDateFormat dateParser = null;
    private SimpleDateFormat targetDate = null;

    private boolean logging = false;
    private String logFileName = null;
    private List<Integer> logFields;

    public SelectMapAttributes(FileIterator iterator, List<String> possibleFields) {
        this.possibleFields = possibleFields;
        logFields = iterator.getLogFields();
        if(logFields == null) {
            logging = false;
            logFields = new ArrayList<>();
        }
        logFileName = iterator.getLogFileName();
        if(logFileName == null) {
            logging = false;
            logFileName = "";
        }

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setTitle("Select MAP configuration");

        dateSelector.setSelectedIndex(iterator.getKeyID());
        textSelector.setSelectedIndex(iterator.getTweetID());
        dateInField.setText(iterator.getSourceFormatString());
        dateOutField.setText(iterator.getTargetFormatString());
        offsetField.setText(iterator.getOffset().toString());
        loggingCheckBox.setSelected(logging);
        logfileNameField.setText(logFileName);
        fillLoggingList();

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

    private void fillLoggingList() {
        boolean selected;
        int i = 0;
        for(String elem : possibleFields) {
            if(logFields.contains(i)) {
                selected = true;
            } else {
                selected = false;
            }
            loggingList.addCheckbox(new JCheckBox(elem, selected));
            i++;
        }
    }
    private void onOK() {
// add your code here
        if(FileIterator.isValidDateFormat(dateInField.getText())
                && FileIterator.isValidDateFormat(dateOutField.getText())) {
            inputSelected = true;
        dispose();
        } else {
            infoField.setText("Not a valid date format");
            if(!FileIterator.isValidDateFormat(dateInField.getText())) {
                dateInField.setForeground(Color.red);
            } else {
                dateInField.setForeground(Color.green);
            }
            if(!FileIterator.isValidDateFormat(dateOutField.getText())) {
                dateOutField.setForeground(Color.red);
            } else {
                dateOutField.setForeground(Color.green);
            }
        }
    }

    private void onCancel() {
// add your code here if necessary
        inputSelected = false;
        dispose();
    }

    private void createUIComponents() {
        dateSelector = new JComboBox(possibleFields.toArray());
        textSelector = new JComboBox(possibleFields.toArray());

        MaskFormatter formatter = null;
        try {
            formatter = new MaskFormatter("#####");
        } catch (ParseException e) {
           // will not happen
        }
        offsetField = new JFormattedTextField(formatter);
        loggingList = new CheckBoxList();
        logList = loggingList;
    }

    public FileIterator getIterator() {
        if(inputSelected == true) {
           return new FileIterator(Long.parseLong(offsetField.getText()), dateSelector.getSelectedIndex()
                                   , textSelector.getSelectedIndex(), dateInField.getText() , dateOutField.getText()
                                   , logfileNameField.getText(), loggingList.getSelectedBoxes());
        }
        return null;
    }

    private class CheckBoxList extends JList
    {
        protected Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

        public CheckBoxList()
        {
            setCellRenderer(new CellRenderer());

            addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    int index = locationToIndex(e.getPoint());

                    if (index != -1) {
                        JCheckBox checkbox = (JCheckBox) getModel().getElementAt(index);
                        checkbox.setSelected( !checkbox.isSelected());
                        repaint();
                    }
                }
            }
            );

            setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        }

        public List<Integer> getSelectedBoxes() {
            List<Integer> returnList = new ArrayList<>();
            ListModel currentList = this.getModel();
            JCheckBox curBox;
            for (int i = 0; i < currentList.getSize(); i++) {
                curBox = (JCheckBox) currentList.getElementAt(i);
                if(curBox.isSelected()) {
                   returnList.add(i);
                }
            }
            return returnList;
        }

        public void addCheckbox(JCheckBox checkBox) {
            ListModel currentList = this.getModel();
            JCheckBox[] newList = new JCheckBox[currentList.getSize() + 1];
            for (int i = 0; i < currentList.getSize(); i++) {
                newList[i] = (JCheckBox) currentList.getElementAt(i);
            }
            newList[newList.length - 1] = checkBox;
            setListData(newList);
        }

        protected class CellRenderer implements ListCellRenderer
        {
            public Component getListCellRendererComponent( JList list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                JCheckBox checkbox = (JCheckBox) value;
                checkbox.setBackground(isSelected ? getSelectionBackground() : getBackground());
                checkbox.setForeground(isSelected ? getSelectionForeground() : getForeground());
                checkbox.setEnabled(isEnabled());
                checkbox.setFont(getFont());
                checkbox.setFocusPainted(false);
                checkbox.setBorderPainted(true);
                checkbox.setBorder(isSelected ? UIManager.getBorder( "List.focusCellHighlightBorder") : noFocusBorder);
                return checkbox;
            }
        }
    }
}
