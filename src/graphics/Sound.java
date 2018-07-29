package graphics;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

/**
 * Created by Nicolas on 06/11/2016
 *
 * This class purpose is loading a music file from the resources and let us be able to play, loop, pause and stop it.
 */

public class Sound {

    private final static float MUTE = 0.0f;
    private final static float LOUD = 1.0f;

    private Clip clip;

    public Sound(String soundName) {
        //The constructor loads the data from the music file into a Clip object.
        try {
            InputStream inputStream = getClass().getResourceAsStream("/resources/sounds/" + soundName + ".wav");
            if (inputStream != null) {
                InputStream bufferedIn = new BufferedInputStream(inputStream);
                AudioInputStream sound = AudioSystem.getAudioInputStream(bufferedIn);
                // load the sound into memory (a Clip)
                clip = AudioSystem.getClip();
                clip.open(sound);
            } else {
                throw new RuntimeException("Sound: file not found: " + soundName + ".wav");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException("Sound: Malformed URL: " + e);
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
            throw new RuntimeException("Sound: Unsupported Audio File: " + e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Sound: Input/Output Error: " + e);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
            throw new RuntimeException("Sound: Line Unavailable Exception Error: " + e);
        }
    }

    //We currently use this method only with MUTE=0 or LOUD=1 in parameter, to mute or unmute.
    //But it would be possible to set intermediate volumes
    private void setVolume(float VOLUME) {
        FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        float dB = (float) (Math.log(VOLUME) / Math.log(10.0) * 20.0);
        volume.setValue(dB);
    }

    public void play() {
        clip.setFramePosition(0);  // Must always rewind!
        clip.start();
    }

    public void loop() {
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void pause() {
        clip.stop();
    }

    public void stop() {
        clip.stop();
        clip.setFramePosition(0);
    }

    public void mute() {
        setVolume(MUTE);
    }

    public void unmute() {
        setVolume(LOUD);
    }

    public long getMicroSecondsDuration() {
        return clip.getMicrosecondLength();
    }
}
