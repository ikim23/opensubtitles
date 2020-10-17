package sk.ikim23.subtitlesdownloader.activity

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import groovy.transform.CompileStatic
import sk.ikim23.subtitlesdownloader.R

import java.text.SimpleDateFormat

@CompileStatic
class SubAdapter extends RecyclerView.Adapter<ViewHolder> {

    interface OnListItemClickListener {
        void onListItemClick(int position)
    }

    static final SimpleDateFormat dateFrom = new SimpleDateFormat('yyyy-MM-dd HH:mm:ss')
    static final SimpleDateFormat dateTo = new SimpleDateFormat('dd.MM.yyyy')
    final ArrayList<Map> data = []
    final Map<String, Integer> colors = [:]
    final OnListItemClickListener listener
    final Context context
    final View placeHolder
    final ArrayList<Integer> colorResIds

    SubAdapter(Context context, View placeHolder, OnListItemClickListener listener) {
        this.context = context
        this.placeHolder = placeHolder
        this.listener = listener
        colorResIds = Arrays.asList(context.getResources().getIntArray(R.array.color_picker)) as ArrayList<Integer>
    }

    @Override
    ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.context)
        return new ViewHolder(inflater.inflate(R.layout.list_item_sub, parent, false), listener)
    }

    @Override
    void onBindViewHolder(ViewHolder holder, int position) {
        Map sub = data[position]
        holder.position = position
        String key = sub.ISO639.toString()
        holder.lang.text = key
        Integer color = colors[key]
        if (!color) {
            if (!colorResIds) {
                colorResIds.addAll(colors.values())
            }
            color = colorResIds.remove(new Random().nextInt(colorResIds.size()))
            colors[key] = color
        }
        GradientDrawable drawable = ContextCompat.getDrawable(context, R.drawable.background_circle) as GradientDrawable
        drawable.color = color
        holder.lang.background = drawable
        holder.subName.text = sub.SubFileName.toString()
        holder.subFormat.text = sub.SubFormat.toString()
        holder.pubDate.text = dateTo.format(dateFrom.parse(sub.SubAddDate.toString()))
        holder.downCount.text = sub.SubDownloadsCnt.toString()
    }

    @Override
    int getItemCount() {
        return data.size()
    }

    void setData(ArrayList<Map> data) {
        placeHolder.visibility = data ? View.GONE : View.VISIBLE
        if (data) {
            this.data.clear()
            this.data.addAll(data)
            notifyDataSetChanged()
            Log.d(this.class.name, "data size: ${data.size()}")
        }
    }

    Map getMap(int position) {
        return data.get(position)
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView lang
        final TextView subName
        final TextView subFormat
        final TextView pubDate
        final TextView downCount
        int position

        ViewHolder(View root, OnListItemClickListener listener) {
            super(root)
            root.onClickListener = { listener.onListItemClick(owner.position) }
            lang = root.findViewById(R.id.language) as TextView
            subName = root.findViewById(R.id.sub_name) as TextView
            subFormat = root.findViewById(R.id.sub_format) as TextView
            pubDate = root.findViewById(R.id.pub_date) as TextView
            downCount = root.findViewById(R.id.download_count) as TextView
        }
    }

}
