package sk.zaymus.sub.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.JCheckBox;
import sk.zaymus.sub.opensubtitles.OSConfig;
import sk.zaymus.sub.pojo.Language;

/**
 * List model stores data of languages. Displays each language as JCheckbox.
 *
 * @see sk.zaymus.sub.pojo.Language
 * @see javax.swing.JCheckBox
 *
 * @author Mikulas Zaymus
 */
public class JCheckBoxListModel extends AbstractListModel<JCheckBox> {

    private final List<Tuple<Language, JCheckBox>> list = new ArrayList<>();

    /**
     *
     * @return size of list data
     */
    @Override
    public int getSize() {
        return list.size();
    }

    /**
     *
     * @param i index of element
     * @return element at index
     */
    @Override
    public JCheckBox getElementAt(int i) {
        return list.get(i).getValue();
    }

    /**
     * Add all languages to list model.
     *
     * @param langs list of languages
     */
    public void addAll(List<Language> langs) {
        langs.stream().forEach(l -> {
            list.add(new Tuple(l, new JCheckBox(l.getLanguageName() + " (" + l.getSubLanguageID() + ')')));
        });
    }

    /**
     * Performs click on JCheckbox at index position.
     *
     * @param i JCheckbox index
     */
    public void performClick(int i) {
        JCheckBox cb = list.get(i).getValue();
        cb.setSelected(!cb.isSelected());
    }

    /**
     * Set all JCheckboxes.
     *
     * @param select true if all buttons should be selected, otherwise false
     */
    public void setSelectedAll(boolean select) {
        list.stream().forEach(t -> {
            t.getValue().setSelected(select);
        });
    }

    /**
     *
     * @return true if all JCheckboxes are selected, otherwise false
     */
    public boolean isSelectedAll() {
        for (Tuple<Language, JCheckBox> t : list) {
            if (!t.getValue().isSelected()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Sets all JCheckboxes represented by languages from parameter as selected.
     *
     * @param langs ISO639-2 language codes
     */
    public void setSelected(String[] langs) {
        HashSet<String> set = new HashSet<>(Arrays.asList(langs));
        list.stream().filter(t -> (set.contains(t.getKey().getSubLanguageID()))).forEach(t -> {
            t.getValue().setSelected(true);
        });
    }

    /**
     *
     * @return sequence of ISO639-2 language codes separated by comma
     */
    public String getSubLanguageID() {
        StringBuilder ids = new StringBuilder();
        list.stream().filter(t -> (t.getValue().isSelected())).forEach(t -> {
            ids.append(OSConfig.LANGUAGES_SEPARATOR).append(t.getKey().getSubLanguageID());
        });
        return ids.length() > 0 ? ids.substring(OSConfig.LANGUAGES_SEPARATOR.length()) : OSConfig.ALL_LANGUAGES;
    }

}
