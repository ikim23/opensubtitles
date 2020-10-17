package sk.ikim23.subtitlesdownloader.activity.main

import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.support.v7.widget.RecyclerView
import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import groovy.transform.CompileStatic
import sk.ikim23.subtitlesdownloader.R
import sk.ikim23.subtitlesdownloader.utils.U

@CompileStatic
class VideoFileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements LoaderManager.LoaderCallbacks<Cursor> {

    static class VideoFile {
        String name
        String size
        String duration
        String path
    }

    interface OnListItemClickListener {
        void onListItemClick(int position)
    }

    static final int VIEW_TYPE_FILE = 1
    static final int VIEW_TYPE_DIR = 2
    final Formatter formatter = new Formatter()
    final ArrayList data = []
    final Context context
    final View placeHolder
    final OnListItemClickListener listener

    VideoFileAdapter(Context context, View placeHolder, OnListItemClickListener listener) {
        this.context = context
        this.placeHolder = placeHolder
        this.listener = listener
    }

    @Override
    RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.context)
        switch (viewType) {
            case VIEW_TYPE_FILE:
                return new ViewFile(inflater.inflate(R.layout.list_item_file, parent, false), listener)
            case VIEW_TYPE_DIR:
                return new ViewDir(inflater.inflate(R.layout.list_item_dir, parent, false))
        }
        return null
    }

    @Override
    void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position < data.size()) {
            switch (getItemViewType(position)) {
                case VIEW_TYPE_FILE:
                    VideoFile file = data.get(position)
                    ViewFile vHolder = holder as ViewFile
                    vHolder.position = position
                    vHolder.fileName.text = file.name
                    vHolder.fileSize.text = file.size
                    vHolder.fileDuration.text = file.duration
                    break
                case VIEW_TYPE_DIR:
                    TextView dir = holder.itemView as TextView
                    dir.text = data.get(position) as String
                    break
            }
        }
    }

    @Override
    int getItemViewType(int position) {
        if (position < data.size()) {
            def item = data.get(position)
            if (item instanceof VideoFile) {
                return VIEW_TYPE_FILE
            }
            if (item instanceof String) {
                return VIEW_TYPE_DIR
            }
        }
        return -1
    }

    @Override
    int getItemCount() {
        return data.size()
    }

    VideoFile getData(int position) {
        return data.get(position)
    }

    @Override
    Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = [
                MediaStore.Video.VideoColumns.DATA,
                MediaStore.Video.VideoColumns.DISPLAY_NAME,
                MediaStore.Video.VideoColumns.SIZE,
                MediaStore.Video.VideoColumns.DURATION,
        ]
        String orderBy = "$MediaStore.Video.VideoColumns.DATA DESC"
        return new CursorLoader(context, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null, orderBy)
    }

    @Override
    void onLoadFinished(Loader<Cursor> loader, Cursor c) {
        data.clear()
        if (c?.moveToFirst()) {
            VideoFile file
            String lastDir = null, fileDir
            while (!c.isAfterLast()) {
                file = new VideoFile()
                file.name = c.getString(c.getColumnIndex(MediaStore.Video.VideoColumns.DISPLAY_NAME))
                file.size = formatter.formatFileSize(context, c.getLong(c.getColumnIndex(MediaStore.Video.VideoColumns.SIZE)))
                file.duration = U.formatLongToTime(c.getLong(c.getColumnIndex(MediaStore.Video.VideoColumns.DURATION)))
                file.path = c.getString(c.getColumnIndex(MediaStore.Video.VideoColumns.DATA))
                fileDir = file.path.substring(0, file.path.size() - file.name.size())
                if (lastDir != fileDir) {
                    data.add(fileDir)
                    lastDir = fileDir
                }
                data.add(file)
                c.moveToNext()
            }
            c.close()
        }
        placeHolder.visibility = data ? View.GONE : View.VISIBLE
        notifyDataSetChanged()
    }

    @Override
    void onLoaderReset(Loader<Cursor> loader) {
    }

    private static class ViewFile extends RecyclerView.ViewHolder {
        final TextView fileName
        final TextView fileSize
        final TextView fileDuration
        int position

        ViewFile(View root, OnListItemClickListener listener) {
            super(root)
            root.onClickListener = { listener.onListItemClick(owner.position) }
            fileName = root.findViewById(R.id.file_name) as TextView
            fileSize = root.findViewById(R.id.file_size) as TextView
            fileDuration = root.findViewById(R.id.file_duration) as TextView
        }
    }

    private static class ViewDir extends RecyclerView.ViewHolder {
        ViewDir(View root) {
            super(root)
        }
    }

}
