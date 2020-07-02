package byui.anderson.a1;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    private Button recordButton;
    private TextView timeTextView;

    private long preTime;
    private long postTime;
    private long buttonTime;

    private WeakReference<Activity> mainActivity;

    private PermissionChecker permissionChecker;
    private Recorder recorder;

////////////////////////////////////////////////////////////////////////////////////////////////////

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

////////////////////////////////////////////////////////////////////////////////////////////////////

        recordButton = (Button) findViewById(R.id.recordButton);
        timeTextView = (TextView) findViewById(R.id.timeTextView);

        preTime = 5000l;
        postTime = 5000l;
        buttonTime = 0l;

        mainActivity = new WeakReference<Activity>(MainActivity.this);

        permissionChecker = new PermissionChecker(mainActivity);
        recorder = new Recorder(mainActivity);

////////////////////////////////////////////////////////////////////////////////////////////////////

        permissionChecker.checkPermissions(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

////////////////////////////////////////////////////////////////////////////////////////////////////

        new Thread(new Runnable() {
            @Override
            public void run() {
                recorder.setIsRecording(true);
                recorder.setIsListening(true);
                recorder.listen(11025);
            }
        