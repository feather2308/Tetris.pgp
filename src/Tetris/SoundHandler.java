package Tetris;

import java.io.BufferedInputStream;
//import java.io.File;
import java.io.InputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

public class SoundHandler {
	private Clip clip;
	FloatControl gainControl;
	
	float volume = 0.8f;
	
	public SoundHandler(String filename) {
        try {        	
//            File soundFile = new File(filename);
        	InputStream InputStream = getClass().getResourceAsStream(filename);
            
            clip = AudioSystem.getClip();

            clip.addLineListener(new LineListener() {
                @Override
                public void update(LineEvent event) {
                    if (event.getType() == LineEvent.Type.STOP) {
                        try {
                        	clip.close();
                        	InputStream InputStream = getClass().getResourceAsStream(filename);
                            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(InputStream));
                        	clip.open(audioInputStream);
                            clip.start();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(InputStream));
            clip.open(audioInputStream);
            clip.start();
            
            // FloatControl을 여기서 얻음
            gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void controlSound(float volume) {
    	this.volume = volume;
        if (gainControl != null) {
            // 볼륨 조절
            float minVolume = gainControl.getMinimum();
            float maxVolume = gainControl.getMaximum();
            float range = maxVolume - minVolume;
            float scaledVolume = (range * volume) + minVolume;
            gainControl.setValue(scaledVolume);
        }
    }
    
    public void playSound(String filename) { // 효과음용
        try {
//            File soundFile = new File(filename);
        	InputStream InputStream = getClass().getResourceAsStream(filename);
            final Clip clip = AudioSystem.getClip();
            clip.addLineListener(new LineListener() {
                @Override
                public void update(LineEvent event) {
                    //CLOSE, OPEN, START, STOP
                    if (event.getType() == LineEvent.Type.STOP) {
                    	clip.close();
                    }
                }
            });
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(InputStream));
            clip.open(audioInputStream);
            
            // FloatControl을 여기서 얻음
            gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            if (gainControl != null) {
                // 볼륨 조절
                float minVolume = gainControl.getMinimum();
                float maxVolume = gainControl.getMaximum();
                float range = maxVolume - minVolume;
                float scaledVolume = (range * (this.volume + 0.08f)) + minVolume;
                gainControl.setValue(scaledVolume);
            }
            
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
