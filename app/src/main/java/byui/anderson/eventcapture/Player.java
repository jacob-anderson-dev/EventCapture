package byui.anderson.eventcapture;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;
import android.os.Environment;

import androidx.annotation.RequiresApi;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class Player {

    private short[] audioData;
    private File file;
    private AudioTrack audioTrack;

////////////////////////////////////////////////////////////////////////////////////////////////////

    public Player() {
        this.file = new File(Environment.getExternalStorageDirectory(), "test.pcm");
        this.audioTrack = null;
        this.audioData = null;
    }
    public Player(File file) {
        this.file = file;
        this.audioTrack = null;
        this.audioData = null;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void play(Integer frequency) {
        try {
            InputStream inputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            DataInputStream dataInputStream = new DataInputStream(bufferedInputStream);

            Integer shortSizeInBytes = Short.SIZE / Byte.SIZE;
            Integer bufferSizeInBytes = (int) (file.length() / shortSizeInBytes);

            audioData = new short[bufferSizeInBytes];

            for (Integer i = 0; dataInputStream.available() > 0; i++) {
                audioData[i] = dataInputStream.readShort();
            }
            dataInputStream.close();

            audioTrack = new AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    frequency,
                    AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSizeInBytes,
                    AudioTrack.MODE_STREAM);

            audioTrack.play();
            audioTrack.write(audioData, 0, bufferSizeInBytes);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void visualize() {

    }
}
