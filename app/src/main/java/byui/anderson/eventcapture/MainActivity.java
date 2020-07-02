package byui.anderson.eventcapture;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private Button recordButton;
    private Button playButton;
    private Toolbar toolbar;
    private long preTime;
    private long postTime;
    private PermissionChecker permissionChecker;
    private Recorder recorder;
    private Player player;

////////////////////////////////////////////////////////////////////////////////////////////////////

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

////////////////////////////////////////////////////////////////////////////////////////////////////

        recordButton = (Button) findViewById(R.id.recordButton);
        playButton = (Button) findViewById(R.id.playButton);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        preTime = 5000l;
        postTime = 5000l;
        permissionChecker = new PermissionChecker(new WeakReference<Activity>(MainActivity.this));
        recorder = new Recorder();
        player = new Player();

        setSupportActionBar(toolbar);

////////////////////////////////////////////////////////////////////////////////////////////////////

        permissionChecker.checkPermissions(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

////////////////////////////////////////////////////////////////////////////////////////////////////

        startListening();

////////////////////////////////////////////////////////////////////////////////////////////////////

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecording();
            }
        });
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPlaying();
            }
        });
    }
////////////////////////////////////////////////////////////////////////////////////////////////////

    public void startListening() {
        recordButton.setEnabled(false);
        playButton.setEnabled(false);
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                recorder.setIsListening(true);
                recorder.listen(11025);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        playButton.setEnabled(true);
                    }
                });
            }
        }).start();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recordButton.setEnabled(true);
                    }
                });
            }
        }, preTime);
    }
    public void startRecording() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new CountDownTimer(postTime, 1000) {
                            @Override
                            public void onTick(final long millisUntilFinished) {
                                Toast.makeText(
                                        MainActivity.this,
                                        "Recording for " +
                                                ((millisUntilFinished / 1000) + 1) +
                                                " more seconds",
                                        Toast.LENGTH_SHORT).show();
                            }
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onFinish() {
                                recorder.setIsListening(false);
                                recorder.record(preTime, postTime);
                                Toast.makeText(
                                        MainActivity.this,
                                        "Done recording!",
                                        Toast.LENGTH_SHORT).show();
                                startListening();
                            }
                        }.start();
                    }
                });
            }
        }).start();
    }
    public void startPlaying() {
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                player.play(11025);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        playButton.setEnabled(false);
                    }
                });
            }
        }).start();
    }
    public void startSettings() {
        Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(settingsIntent);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                startSettings();
        }
        return true;
    }
}