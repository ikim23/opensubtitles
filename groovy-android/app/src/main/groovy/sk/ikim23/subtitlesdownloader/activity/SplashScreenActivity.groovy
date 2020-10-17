package sk.ikim23.subtitlesdownloader.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import groovy.transform.CompileStatic
import sk.ikim23.subtitlesdownloader.activity.main.MainActivity

@CompileStatic
class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        Intent intent = new Intent(this, MainActivity.class)
        startActivity(intent)
        finish()
    }

}
