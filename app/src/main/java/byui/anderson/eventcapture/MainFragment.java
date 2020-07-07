package byui.anderson.eventcapture;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Timer;
import java.util.TimerTask;

public class MainFragment extends Fragment {

    private long preTime;
    private long postTime;
    private FloatingActionButton recordFAB;
    private FloatingActionButton playFAB;
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


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String pre_time = sharedPreferences.getString("pre_time", "");
        String post_time = sharedPreferences.getString("post_time", "");

        Integer settingsPreTime = Integer.parseInt(pre_time);
        Integer settingsPostTime = Integer.parseInt(post_time);

////////////////////////////////////////////////////////////////////////////////////////////////////

        preTime = (settingsPreTime * 1000l);
        postTime = (settingsPostTime * 1000l);
        recordFAB = (FloatingActionButton) getActivity().findViewById(R.id.recordFAB);
        playFAB = (FloatingActionButton) getActivity().findViewById(R.id.playFAB);
        recorder = new Recorder();
        player = new Player();

////////////////////////////////////////////////////////////////////////////////////////////////////

        startListening();

////////////////////////////////////////////////////////////////////////////////////////////////////

        recordFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecording();
            }
        });
        playFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPlaying();
            }
        });
    }
////////////////////////////////////////////////////////////////////////////////////////////////////

    public void startListening() {
        recordFAB.setEnabled(false);
        playFAB.setEnabled(false);

        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                recorder.setIsListening(true);
                recorder.listen(11025);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        playFAB.setEnabled(true);
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
                        recordFAB.setEnabled(true);
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
                        playFAB.setEnabled(false);
                    }
                });
            }
        }).start();
    }
}