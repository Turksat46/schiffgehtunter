package com.turksat46.schiffgehtunter.other;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class Music {

    MediaPlayer mediaPlayer;
    MediaPlayer soundsPlayer;
    MediaPlayer zombiePlayer;
    Thread musicThread;

    public Music(){
        try{
            String uri = String.valueOf(getClass().getResource("/com/turksat46/schiffgehtunter/music/mainmusic.mp3"));
            String soundsuri = String.valueOf(getClass().getResource("/com/turksat46/schiffgehtunter/music/click.mp3"));
            String zombey = String.valueOf(getClass().getResource("/com/turksat46/schiffgehtunter/music/zombie.mp3"));
            mediaPlayer = new MediaPlayer(new Media(uri));
            soundsPlayer = new MediaPlayer(new Media(soundsuri));
            zombiePlayer = new MediaPlayer(new Media(zombey));

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void play(){
        if(mediaPlayer != null && (musicThread == null || !musicThread.isAlive())){
            musicThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    mediaPlayer.play();
                }
            });
            musicThread.setDaemon(true);
            musicThread.start();
        }
    }

    public void playSound(){
        soundsPlayer.play();
    }

    public void playEasterEgg(){
        zombiePlayer.play();
    }

}
