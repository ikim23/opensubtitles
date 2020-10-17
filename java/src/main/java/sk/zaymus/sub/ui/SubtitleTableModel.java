package sk.zaymus.sub.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JButton;
import javax.swing.UIManager;
import javax.swing.table.AbstractTableModel;
import sk.zaymus.sub.pojo.Subtitles;

/**
 *
 * @author Mikulas Zaymus
 */
public class SubtitleTableModel extends AbstractTableModel {

    private final JButton btn = new JButton(UIManager.getIcon("FileView.floppyDriveIcon"));
    private List<Subtitles> subs = new ArrayList<>();

    /**
     *
     * @param subs new table data
     */
    public void resetData(List<Subtitles> subs) {
        this.subs = subs;
        fireTableDataChanged();
    }

    /**
     *
     * @param r index of subtitles
     * @return subtitles at index position
     */
    public Subtitles getSubtitles(int r) {
        return subs.get(r);
    }

    /**
     *
     * @return number of rows
     */
    @Override
    public int getRowCount() {
        return subs.size();
    }

    /**
     *
     * @return number of columns
     */
    @Override
    public int getColumnCount() {
        return 7;
    }

    /**
     *
     * @param c column index
     * @return name of column
     */
    @Override
    public String getColumnName(int c) {
        switch (c) {
            case 0:
                return "Name";
            case 1:
                return "Language";
            case 2:
                return "Downloads";
            case 3:
                return "Score";
            case 4:
                return "Created";
            case 5:
                return "Format";
            case 6:
                return "";
            default:
                return null;
        }
    }

    /**
     *
     * @param c column index
     * @return class of column
     */
    @Override
    public Class<?> getColumnClass(int c) {
        switch (c) {
            case 0:
                return String.class;
            case 1:
                return String.class;
            case 2:
                return Integer.class;
            case 3:
                return Double.class;
            case 4:
                return Date.class;
            case 5:
                return String.class;
            case 6:
                return JButton.class;
            default:
                return null;
        }
    }

    /**
     * Retrieves value of cell.
     *
     * @param r index of row
     * @param c index of column
     * @return cell value at specified position
     */
    @Override
    public Object getValueAt(int r, int c) {
        Subtitles sub = subs.get(r);
        switch (c) {
            case 0:
                return sub.getMovieReleaseName();
            case 1:
                return sub.getSubLanguageID();
            case 2:
                return sub.getSubDownloadsCnt();
            case 3:
                return sub.getSubRating();
            case 4:
                return sub.getSubAddDate();
            case 5:
                return sub.getSubFormat();
            case 6:
                return btn;
            default:
                return null;
        }
    }

}
