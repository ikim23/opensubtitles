package sk.zaymus.sub.ui;

import java.awt.Component;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

/**
 * Renders JCheckbox elements in JList component.
 *
 * @author Mikulas Zaymus
 */
public class JCheckBoxListCellRender implements ListCellRenderer<JCheckBox> {

    private final static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

    @Override
    public Component getListCellRendererComponent(JList<? extends JCheckBox> list, JCheckBox checkbox, int index, boolean isSelected, boolean cellHasFocus) {
        checkbox.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
        checkbox.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
        checkbox.setEnabled(list.isEnabled());
        checkbox.setFont(list.getFont());
        checkbox.setFocusPainted(false);
        checkbox.setBorderPainted(true);
        checkbox.setBorder(isSelected ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);
        return checkbox;
    }

}
