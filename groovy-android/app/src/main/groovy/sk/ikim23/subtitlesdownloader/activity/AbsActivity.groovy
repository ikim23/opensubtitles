package sk.ikim23.subtitlesdownloader.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.android.gms.tasks.Task
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import groovy.transform.CompileStatic
import sk.ikim23.subtitlesdownloader.R
import sk.ikim23.subtitlesdownloader.dialog.LanguageDialog

@CompileStatic
abstract class AbsActivity extends AppCompatActivity {

    static final int PERMISSION_REQUEST_STORAGE = 1
    final FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        remoteConfig.defaults = R.xml.remote_config_defaults
        remoteConfig.fetch(120).addOnCompleteListener(this, { Task<Void> task ->
            if (task.isSuccessful()) {
                boolean b = remoteConfig.activateFetched()
                String url = remoteConfig.getString(getString(R.string.remote_config_url))
                String userAgent = remoteConfig.getString(getString(R.string.remote_config_user_agent))
                Log.d(this.class.name, "remote config fetched: $b | $url | $userAgent")
            }
        })
    }

    @Override
    boolean onOptionsItemSelected(MenuItem item) {
        if (item.itemId == R.id.action_select_language) {
            LanguageDialog.show(this)
            return true
        }
        return false
    }

    @Override
    boolean onCreateOptionsMenu(Menu menu) {
        menuInflater.inflate(R.menu.menu_settings, menu)
        return true
    }

    /***************
     * PERMISSIONS *
     ***************/
    abstract View getLayout()

    abstract void onStoragePermissionGranted()

    void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            showRationale()
        } else {
            ActivityCompat.requestPermissions(this, [Manifest.permission.READ_EXTERNAL_STORAGE] as String[], PERMISSION_REQUEST_STORAGE)
        }
    }

    @Override
    final void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_STORAGE) {
            if (grantResults && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onStoragePermissionGranted()
            } else {
                showRationale()
            }
        }
    }

    void showRationale() {
        Snackbar.make(layout, R.string.rationale_external_storage, Snackbar.LENGTH_INDEFINITE).setAction(R.string.grant, {
            ActivityCompat.requestPermissions(owner, [Manifest.permission.READ_EXTERNAL_STORAGE] as String[], PERMISSION_REQUEST_STORAGE)
        }).show()
    }

}
