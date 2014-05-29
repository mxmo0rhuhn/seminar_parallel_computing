package ch.zhaw.parallelComputing.view;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
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
    private JTextField logfileName;
    private JPanel loggingArea;
    private JScrollPane loggingPane;
    private JList logList;
    private CheckBoxList loggingList;

    private boolean inputSelected = false;
    private final List<String> possibleFields;

    public SelectMapAttributes(String inFormat, String outFormat, List<String> possibleFields) {
        this.possibleFields = possibleFields;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setTitle("Select MAP configuration");

        dateInField.setText(inFormat);
        dateOutField.setText(outFormat);

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
        loggingList = new CheckBoxList();

        for(String elem : possibleFields) {
            loggingList.addCheckbox(new JCheckBox(elem, false));
        }
    }
    private void onOK() {
// add your code here
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        inputSelected = false;
        dispose();
    }

    private void createUIComponents() {
        dateSelector = new JComboBox(possibleFields.toArray());
        textSelector = new JComboBox(possibleFields.toArray());
        fillLoggingList();
        logList = loggingList;
    }

    public boolean isInputSelected() {
        return inputSelected;
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
