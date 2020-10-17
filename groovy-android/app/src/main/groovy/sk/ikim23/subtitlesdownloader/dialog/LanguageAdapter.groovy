package sk.ikim23.subtitlesdownloader.dialog

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import groovy.transform.CompileStatic
import groovy.transform.TupleConstructor
import org.json.JSONArray
import org.json.JSONObject
import sk.ikim23.subtitlesdownloader.R
import sk.ikim23.subtitlesdownloader.utils.U

@CompileStatic
class LanguageAdapter extends RecyclerView.Adapter<ViewHolder> {

    static final String LANG_ID = 'id'
    static final String LANG_NAME = 'name'
    final LanguageDialog dialog
    final ArrayList<Language> langs
    final CompoundButton.OnCheckedChangeListener listener = { CompoundButton cb, boolean isChecked ->
        owner.langs.get((int) cb.getTag()).checked = isChecked
        owner.updateDialogButtons()
    } as CompoundButton.OnCheckedChangeListener
    boolean isAllChecked

    LanguageAdapter(Context context, LanguageDialog dialog) {
        // load all languages
        Set<String> checkedIds = U.getLanguageIdsAsSet(context)
        JSONArray jsonArray = new JSONArray(FirebaseRemoteConfig.instance.getString(context.getString(R.string.remote_config_languages)))
        JSONObject jsonObject
        int len = jsonArray.length()
        langs = new ArrayList<>(len)
        for (int i = 0; i < len; i++) {
            jsonObject = jsonArray.getJSONObject(i)
            String id = jsonObject.get(LANG_ID)
            langs.add(new Language(id, jsonObject.get(LANG_NAME).toString(), checkedIds.contains(id)))
        }
        // set dialog buttons
        this.dialog = dialog
        updateDialogButtons()
    }

    void updateDialogButtons() {
        int checkedCount = checkedCount
        isAllChecked = checkedCount == itemCount
        dialog.positiveButtonEnabled = checkedCount > 0
        dialog.allSelected = !isAllChecked
    }

    String getCheckedLanguageIds() {
        StringBuilder sb = new StringBuilder()
        for (Language lang : langs) {
            if (lang.checked) {
                sb.append(lang.id).append(',')
            }
        }
        if (sb.length() > 0) {
            sb.length = sb.length() - 1
        }
        return sb.toString()
    }

    Integer getCheckedCount() {
        int count = 0
        for (Language lang : langs) {
            if (lang.checked) {
                count++
            }
        }
        return count
    }

    boolean toggleItems() {
        isAllChecked = !isAllChecked
        for (Language lang : langs) {
            lang.checked = isAllChecked
        }
        notifyDataSetChanged()
        return isAllChecked
    }

    @Override
    ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.context)
        CheckBox checkBox = inflater.inflate(R.layout.list_item_language, parent, false) as CheckBox
        checkBox.onCheckedChangeListener = listener
        return new ViewHolder(checkBox)
    }

    @Override
    void onBindViewHolder(ViewHolder holder, int position) {
        Language lang = langs.get(position)
        CheckBox checkBox = holder.itemView as CheckBox
        checkBox.setTag(position)
        checkBox.setText(lang.name)
        checkBox.setChecked(lang.checked)

    }

    @Override
    int getItemCount() {
        return langs.size()
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View root) {
            super(root)
        }
    }

    @TupleConstructor
    private static class Language {
        String id
        String name
        boolean checked
    }

}