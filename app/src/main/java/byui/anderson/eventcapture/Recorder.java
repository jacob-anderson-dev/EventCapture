package byui.anderson.eventcapture;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTimestamp;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import androidx.annotation.RequiresApi;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class Recorder {

    private Boolean isListening;
    private Integer minBufferSize;
    private long startTime;

    private List<Short> audioData;
    private List<AudioTimestamp> timeData;

    private File file;
    private AudioRecord audioRecord;

////////////////////////////////////////////////////////////////////////////////////////////////////

    public Recorder() {
        this.isListening = false;
        this.minBufferSize = 0;
        this.startTime = 0;
        this.audioData = null;
        this.timeData = null;
        this.file = new File(Environment.getExternalStorageDirectory(), "test.pcm");
        this.audioRecord = null;
    }
    public Recorder(File file) {
        this.isListening = false;
        this.minBufferSize = 0;
        this.startTime = 0;
        this.audioData = null;
        this.timeData = null;
        this.file = file;
        this.audioRecord = null;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    public Boolean getIsListening() {
        return isListening;
    }
    public void setIsListening(Boolean isListening) {
        this.isListening = isListening;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void listen(Integer frequency) {
        //Initialize AudioRecord and all Lists
        initializeListen(frequency);
        short[] audioBuffer = new short[minBufferSize];

        //Start listening through the microphone
        audioRecord.startRecording();
        while (isListening) {
            //Read in a small number of shorts into audioBuffer
            Integer numberOfShortsRead = audioRecord.read(audioBuffer, 0, minBufferSize);

            //Read all of audioBuffer into audioData
            for (Integer i = 0; i < numberOfShortsRead; i++) {
                audioData.add(audioBuffer[i]);
            }

            //Make a TimeStamp and add it to timeData
            AudioTimestamp audioTimestamp = new AudioTimestamp();
            audioRecord.getTimestamp(audioTimestamp, AudioTimestamp.TIMEBASE_MONOTONIC);
            timeData.add(audioTimestamp);
        }
        //Stop listening
        audioRecord.stop();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void record(long preTime, long postTime) {
        try {
            file.createNewFile();
            OutputStream outputStream = new FileOutputStream(file);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
            DataOutputStream dataOutputStream = new DataOutputStream(bufferedOutputStream);

            for (long i = getStartIndex(preTime, postTime); i < audioData.size(); i++) {
                dataOutputStream.writeShort(audioData.get((int) i));
            }

            dataOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private long getStartIndex(long preTime, long postTime) {
        //Initialize framePosition
        //endTime = duration of total recording in milliseconds
        long framePosition = 0;
        long endTime = (timeData.get(timeData.size() - 1).nanoTime / 1000000) - (startTime);

        //Loop through timeData
        for (Integer i = 0; i < timeData.size(); i++) {
            //time = current time in milliseconds
            long time = ((timeData.get(i).nanoTime / 1000000) - (startTime));

            //If the current time is preTime and postTime before endTime
            if ((time / 100) == ((endTime - (preTime + postTime)) / 100)) {
                //return the framePosition
                framePosition = timeData.get(i).framePosition;
            }
        }

        return framePosition;
    }

    private void initializeListen(Integer frequency) {
        minBufferSize = AudioRecord.getMinBufferSize(
                frequency,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        audioRecord = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                frequency,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                minBufferSize);
        audioData = new ArrayList<>();
        timeData = new ArrayList<>();
        startTime = SystemClock.uptimeMillis();
    }
}
