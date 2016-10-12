package sk.zaymus.sub.ui;

import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Formats date table cells to "dd.MM.yyyy" date format.
 *
 * @author Mikulas Zaymus
 */
public class DateTableCellRender extends DefaultTableCellRenderer {

    private final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
        if (value instanceof Date) {
            value = sdf.format(value);
        }
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
    }

}
