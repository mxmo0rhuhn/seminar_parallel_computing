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

import ch.zhaw.parallelComputing.model.sentiment.FileIterator;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A configuration dialog that returns a File iterator if all given information are valid.
 *
 * @author Max Schrimpf
 */
public class MapAttributesDialog extends JDialog {
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

    private boolean logging = true;
    private String logFileName = null;
    private List<Integer> logFields;

    public MapAttributesDialog(Component parent, FileIterator iterator, List<String> possibleFields) {
        this.possibleFields = possibleFields;

        $$$setupUI$$$();
        logFields = iterator.getLogFields();
        if (logFields == null) {
            logging = false;
            logFields = new ArrayList<>();
        }
        logFileName = iterator.getLogFileName();
        if (logFileName == null) {
            logging = false;
            logFileName = "";
        }

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setTitle("Select MAP configuration");

        if (dateSelector.getItemCount() > iterator.getKeyID()) {
            dateSelector.setSelectedIndex(iterator.getKeyID());
        }
        if (textSelector.getItemCount() > iterator.getTweetID()) {

            textSelector.setSelectedIndex(iterator.getTweetID());
        }

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

        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    private void fillLoggingList() {
        boolean selected;
        int i = 0;
        for (String elem : possibleFields) {
            if (logFields.contains(i)) {
                selected = true;
            } else {
                selected = false;
            }
            loggingList.addCheckbox(new JCheckBox(elem, selected));
            i++;
        }
    }

    private boolean validDates() {
        if (FileIterator.isValidDateFormat(dateInField.getText())
                && FileIterator.isValidDateFormat(dateOutField.getText())) {
            return true;
        }
        return false;
    }

    private boolean validLogging() {
        if ((loggingCheckBox.isSelected() && loggingList.getSelectedBoxes().size() > 0
                && logfileNameField.getText().length() > 0) || !loggingCheckBox.isSelected()) {
            return true;
        }
        return false;
    }

    private void onOK() {
// add your code here
        if (validDates() && validLogging()) {
            inputSelected = true;
            logging = loggingCheckBox.isSelected();
            dispose();
        } else {
            if (!validDates()) {
                infoField.setText("Not a valid date format");
                if (!FileIterator.isValidDateFormat(dateInField.getText())) {
                    dateInField.setForeground(Color.red);
                } else {
                    dateInField.setForeground(Color.green);
                }
                if (!FileIterator.isValidDateFormat(dateOutField.getText())) {
                    dateOutField.setForeground(Color.red);
                } else {
                    dateOutField.setForeground(Color.green);
                }
            } else {
                infoField.setText("Logging properties not valid");
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

        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
        DecimalFormat decimalFormat = (DecimalFormat) numberFormat;
        decimalFormat.setGroupingUsed(false);
        offsetField = new JFormattedTextField(decimalFormat);
        loggingList = new CheckBoxList();
        logList = loggingList;
    }

    public FileIterator getIterator() {
        if (inputSelected == true) {
            if (logging) {
                return new FileIterator(Long.parseLong(offsetField.getText().trim()), dateSelector.getSelectedIndex()
                        , textSelector.getSelectedIndex(), dateInField.getText(), dateOutField.getText()
                        , logfileNameField.getText(), loggingList.getSelectedBoxes());
            } else {
                return new FileIterator(Long.parseLong(offsetField.getText().trim()), dateSelector.getSelectedIndex()
                        , textSelector.getSelectedIndex(), dateInField.getText(), dateOutField.getText());
            }
        }
        return null;
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));
        panel1.add(panel2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonOK = new JButton();
        buttonOK.setText("OK");
        panel2.add(buttonOK, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonCancel = new JButton();
        buttonCancel.setText("Cancel");
        panel2.add(buttonCancel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(9, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        dateOutField = new JTextField();
        panel3.add(dateOutField, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        dateInField = new JTextField();
        panel3.add(dateInField, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Date format in");
        panel3.add(label1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Date format out");
        panel3.add(label2, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        infoField = new JLabel();
        infoField.setForeground(new Color(-3407719));
        infoField.setText("  ");
        panel3.add(infoField, new GridConstraints(8, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Text field");
        panel3.add(label3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Date field");
        panel3.add(label4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel3.add(dateSelector, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel3.add(textSelector, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        logfileNameField = new JTextField();
        logfileNameField.setToolTipText("Filename for logging File");
        panel3.add(logfileNameField, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        loggingPane = new JScrollPane();
        panel3.add(loggingPane, new GridConstraints(7, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        loggingPane.setViewportView(logList);
        final JLabel label5 = new JLabel();
        label5.setText("Tweets per MAP task");
        panel3.add(label5, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel3.add(offsetField, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        loggingCheckBox = new JCheckBox();
        loggingCheckBox.setText("Log MAP part results");
        panel3.add(loggingCheckBox, new GridConstraints(5, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Local logfile name");
        panel3.add(label6, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

    private class CheckBoxList extends JList {
        protected Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

        public CheckBoxList() {
            setCellRenderer(new CellRenderer());

            addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    int index = locationToIndex(e.getPoint());

                    if (index != -1) {
                        JCheckBox checkbox = (JCheckBox) getModel().getElementAt(index);
                        checkbox.setSelected(!checkbox.isSelected());
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
                if (curBox.isSelected()) {
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

        protected class CellRenderer implements ListCellRenderer {
            public Component getListCellRendererComponent(JList list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                JCheckBox checkbox = (JCheckBox) value;
                checkbox.setBackground(isSelected ? getSelectionBackground() : getBackground());
                checkbox.setForeground(isSelected ? Color.BLUE : getForeground());
                checkbox.setEnabled(isEnabled());
                checkbox.setFont(getFont());
                checkbox.setFocusPainted(false);
                checkbox.setBorderPainted(true);
                checkbox.setBorder(isSelected ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);
                return checkbox;
            }
        }
    }
}
