package byui.anderson.eventcapture;

import android.Manifest;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

public class MainFragment extends Fragment {

    private Button recordButton;
    private Button playButton;

    private long preTime;
    private long postTime;

    private PermissionChecker permissionChecker;
    private Recorder recorder;
    private Player player;

////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

////////////////////////////////////////////////////////////////////////////////////////////////////

        recordButton = (Button) getActivity().findViewById(R.id.recordButton);
        playButton = (Button) getActivity().findViewById(R.id.playButton);

        preTime = 5000l;
        postTime = 5000l;

        permissionChecker = new PermissionChecker(new WeakReference<Activity>(getActivity()));
        recorder = new Recorder();
        player = new Player();

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
                getActivity().runOnUiThread(new Runnable() {
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
                getActivity().runOnUiThread(new Runnable() {
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
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new CountDownTimer(postTime, 1000) {
                            @Override
                            public void onTick(final long millisUntilFinished) {
                                Toast.makeText(
                                        getActivity(),
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
                                        getActivity(),
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
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        playButton.setEnabled(false);
                    }
                });
            }
        }).start();
    }
}