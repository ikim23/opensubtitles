package sk.ikim23.subtitlesdownloader.activity.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import groovy.transform.CompileStatic
import sk.ikim23.subtitlesdownloader.R
import sk.ikim23.subtitlesdownloader.activity.AbsActivity
import sk.ikim23.subtitlesdownloader.activity.search.SearchActivity

@CompileStatic
class MainActivity extends AbsActivity implements VideoFileAdapter.OnListItemClickListener {

    static final int VIDEO_FILE_LOADER_ID = 2
    CoordinatorLayout layout
    RecyclerView recyclerView
    VideoFileAdapter videoFileAdapter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        layout = findViewById(R.id.coordinator_layout) as CoordinatorLayout
        initRecyclerView()
        requestStoragePermission()
    }

    @Override
    void onListItemClick(int position) {
        VideoFileAdapter.VideoFile file = videoFileAdapter.getData(position)
        Log.d(this.class.name, "item: $file")
        Intent intent = new Intent(this, SearchActivity)
        intent.data = Uri.fromFile(new File(file.path))
        startActivity(intent)
    }

    @Override
    View getLayout(){
        return layout
    }

    @Override
    void onStoragePermissionGranted() {
        supportLoaderManager.restartLoader(VIDEO_FILE_LOADER_ID, null, videoFileAdapter)
    }

    void initRecyclerView() {
        recyclerView = findViewById(R.id.recycler_view) as RecyclerView
        recyclerView.layoutManager = new LinearLayoutManager(this)
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        videoFileAdapter = new VideoFileAdapter(this, findViewById(R.id.placeholder), this)
        recyclerView.adapter = videoFileAdapter
    }

}
