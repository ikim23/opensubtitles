package sk.ikim23.subtitlesdownloader.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import groovy.transform.CompileStatic
import sk.ikim23.subtitlesdownloader.R
import sk.ikim23.subtitlesdownloader.utils.U

@CompileStatic
class LanguageDialog {

    final RecyclerView recyclerView
    final LanguageAdapter adapter
    final Button positiveButton
    final Button neutralButton

    static void show(Context context) {
        new LanguageDialog(context)
    }

    private LanguageDialog(Context context) {
        // init recycler view
        recyclerView = LayoutInflater.from(context).inflate(R.layout.dialog_language, null) as RecyclerView
        recyclerView.layoutManager = new LinearLayoutManager(context)
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        // alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
        builder.setTitle(R.string.select_languages)
                .setView(recyclerView)
                .setPositiveButton(R.string.ok, { DialogInterface dialog, int which -> U.setLanguageIds(context, adapter.checkedLanguageIds) })
                .setNegativeButton(R.string.cancel, { DialogInterface dialog, int which -> dialog.dismiss() })
                .setNeutralButton(R.string.select_all, { DialogInterface dialog, int which -> })
        Dialog dialog = builder.create()
        dialog.show()
        positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        neutralButton = dialog.getButton(AlertDialog.BUTTON_NEUTRAL)
        neutralButton.onClickListener = { View v -> adapter.toggleItems() }
        // recycler view adapter
        adapter = new LanguageAdapter(context, this)
        recyclerView.adapter = adapter
    }

    void setPositiveButtonEnabled(boolean enabled) {
        positiveButton.enabled = enabled
    }

    void setAllSelected(boolean allSelected) {
        neutralButton.text = allSelected ? R.string.select_all : R.string.deselect_all
    }

}
