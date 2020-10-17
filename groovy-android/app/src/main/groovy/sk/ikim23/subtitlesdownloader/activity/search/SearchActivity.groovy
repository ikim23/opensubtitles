package sk.ikim23.subtitlesdownloader.activity.search

import android.animation.ObjectAnimator
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ProgressBar
import com.google.firebase.crash.FirebaseCrash
import groovy.net.xmlrpc.XMLRPCServerProxy
import sk.ikim23.subtitlesdownloader.R
import sk.ikim23.subtitlesdownloader.activity.AbsActivity
import sk.ikim23.subtitlesdownloader.activity.SubAdapter
import sk.ikim23.subtitlesdownloader.utils.Fluent
import sk.ikim23.subtitlesdownloader.utils.U

import java.util.zip.GZIPInputStream

class SearchActivity extends AbsActivity implements SubAdapter.OnListItemClickListener {

    CoordinatorLayout layout
    RecyclerView recyclerView
    SubAdapter subAdapter
    File movieFile
    Map sub

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        if (intent && intent.data) {
            movieFile = new File(intent.data.path)
            layout = findViewById(R.id.coordinator_layout) as CoordinatorLayout
            initRecyclerView()
            U.listener = { owner.searchSubtitles() }
//            searchSubtitles()
            requestStoragePermission()
        }
    }

    @Override
    void onListItemClick(int position) {
        sub = subAdapter.getMap(position)
        Log.d(this.class.name, "$sub.SubDownloadLink")
        if (!U.isConnected(this)) {
            showSnackBar(R.string.no_internet)
            return
        }
        // download subtitles to disk
        File subFile = new File(movieFile.parentFile, sub.SubFileName)
        if (subFile.exists()) {
            showSnackBar(R.string.file_exists, R.string.rewrite, {
                downloadSubtitles(sub.SubDownloadLink, subFile)
            })
        } else {
            downloadSubtitles(sub.SubDownloadLink, subFile)
        }
    }

    @Override
    View getLayout() {
        return layout
    }

    @Override
    void onStoragePermissionGranted() {
        searchSubtitles()
    }

    void searchSubtitles() {
        if (!U.isConnected(this)) {
            showSnackBar(R.string.no_internet, R.string.retry, { searchSubtitles() })
            return
        }
        if (movieFile) {
            ProgressBar progressBar = showProgressBar()
            Fluent.async {
                String movieHash = U.computeHash(movieFile)
                long movieByteSize = movieFile.length()
                Log.d(this.class.name, "hash: $movieHash")

                String url = remoteConfig.getString(getString(R.string.remote_config_url))
                String userAgent = remoteConfig.getString(getString(R.string.remote_config_user_agent))
                String subLanguageId = U.getLanguageIds(owner)
                try {
                    XMLRPCServerProxy server = new XMLRPCServerProxy(url, true)
                    def result = server.LogIn('', '', '', userAgent)
                    result = server.SearchSubtitles(result.token, [[moviehash: movieHash, sublanguageid: subLanguageId, moviebytesize: movieByteSize]])
                    Log.d(this.class.name, "download finished with status: $result.status")
                    switch (result.status) {
                        case '200 OK':
                            def data = result.data.inject([]) { data, kv ->
                                data << [
                                        SubFileName    : kv.SubFileName,
                                        ISO639         : kv.ISO639,
                                        SubDownloadsCnt: kv.SubDownloadsCnt,
                                        SubAddDate     : kv.SubAddDate,
                                        SubFormat      : kv.SubFormat,
                                        SubDownloadLink: kv.SubDownloadLink,
                                ]
                            }
                            Log.d(this.class.name, data.toString())
                            return data
                        case '407 Download limit reached': showSnackBar(R.string.download_limit_reached)
                    }
                } catch (any) {
                    Log.e(this.class.name, "Error while downloading data: ${any.fillInStackTrace()}")
                    FirebaseCrash.report(any)
                }
                return null
            }.then { data ->
                progressBar.visibility = View.GONE
                subAdapter.data = data
            }
        }
    }

    void downloadSubtitles(String subDownloadLink, File subFile) {
        if (!U.isConnected(this)) {
            showSnackBar(R.string.no_internet, R.string.retry, {
                downloadSubtitles(subDownloadLink, subFile)
            })
            return
        }
        Fluent.async {
            Log.d(this.class.name, "start downloading, save to file: ${subFile.toString()}")
            subFile.withOutputStream { out ->
                out << new GZIPInputStream(new URL(subDownloadLink).openStream())
            }
        }.then {
            showSnackBar(R.string.download_finished, R.string.play_movie, {
                Intent intent = new Intent(Intent.ACTION_VIEW)
                intent.data = Uri.fromFile(movieFile)
                intent.type = "video/*"
                startActivity(Intent.createChooser(intent, getString(R.string.play_movie)))
            })
        }
    }

    void initRecyclerView() {
        recyclerView = findViewById(R.id.recycler_view) as RecyclerView
        recyclerView.layoutManager = new LinearLayoutManager(this)
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        subAdapter = new SubAdapter(this, findViewById(R.id.placeholder), this)
        recyclerView.adapter = subAdapter
    }

    ProgressBar showProgressBar() {
        ProgressBar progressBar = findViewById(R.id.progress_bar) as ProgressBar
        ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", 0, 100)
        animation.interpolator = new AccelerateDecelerateInterpolator()
        animation.repeatCount = ObjectAnimator.INFINITE
        animation.repeatMode = ObjectAnimator.RESTART
        animation.duration = 1000
        animation.start()
        return progressBar
    }

    void showSnackBar(int msgId, int btnId = 0, View.OnClickListener listener = null) {
        Snackbar snackBar = Snackbar.make(layout, msgId, Snackbar.LENGTH_SHORT)
        if (listener) {
            snackBar.duration = Snackbar.LENGTH_INDEFINITE
            snackBar.setAction(btnId, listener)
        }
        snackBar.show()
    }

}
