package com.turksat46.schiffgehtunter.other;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class Music {

    private static Music instance;
    MediaPlayer mediaPlayer;
    MediaPlayer soundsPlayer;
    MediaPlayer zombiePlayer;
    MediaPlayer winPlayer;
    MediaPlayer lostPlayer;
    MediaPlayer missPlayer;
    MediaPlayer hitPlayer;
    MediaPlayer shipDestroyedPlayer;
    Thread musicThread;

    private Music(){
        try{
            String uri = String.valueOf(getClass().getResource("/com/turksat46/schiffgehtunter/music/mainmusic.mp3"));
            String soundsuri = String.valueOf(getClass().getResource("/com/turksat46/schiffgehtunter/music/click.mp3"));
            String zombey = String.valueOf(getClass().getResource("/com/turksat46/schiffgehtunter/music/zombie.mp3"));
            String winSound = String.valueOf(getClass().getResource("/com/turksat46/schiffgehtunter/music/levelup.mp3"));
            String missSound = String.valueOf(getClass().getResource("/com/turksat46/schiffgehtunter/music/miss.mp3"));
            String hitSound = String.valueOf(getClass().getResource("/com/turksat46/schiffgehtunter/music/hit.mp3"));
            String shipDestroyedSound = String.valueOf(getClass().getResource("/com/turksat46/schiffgehtunter/music/shipdestroyed.mp3"));
            mediaPlayer = new MediaPlayer(new Media(uri));
            soundsPlayer = new MediaPlayer(new Media(soundsuri));
            zombiePlayer = new MediaPlayer(new Media(zombey));
            winPlayer = new MediaPlayer(new Media(winSound));
            missPlayer = new MediaPlayer(new Media(missSound));
            hitPlayer = new MediaPlayer(new Media(hitSound));
            shipDestroyedPlayer = new MediaPlayer(new Media(shipDestroyedSound));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Music getInstance() {
        if (instance == null) {
            instance = new Music();
        }
        return instance;
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
        soundsPlayer.stop();
        soundsPlayer.seek(Duration.ZERO);
        soundsPlayer.play();
    }

    public void playEasterEgg(){
        zombiePlayer.stop();
        zombiePlayer.seek(Duration.ZERO);
        zombiePlayer.play();
    }

    public void playWin(){
        winPlayer.stop();
        winPlayer.seek(Duration.ZERO);
        winPlayer.play();
    }

    public void playMiss(){
        missPlayer.stop();
        missPlayer.seek(Duration.ZERO);
        missPlayer.play();
    }

    public void playHit(){
        hitPlayer.stop();
        hitPlayer.seek(Duration.ZERO);
        hitPlayer.play();
    }

    public void playShipDestroyed(){
        shipDestroyedPlayer.stop();
        shipDestroyedPlayer.seek(Duration.ZERO);
        shipDestroyedPlayer.play();
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();  // Stoppt die Hintergrundmusik
        }
        if (soundsPlayer != null) {
            soundsPlayer.stop();  // Stoppt den Soundeffekt
        }
        if (zombiePlayer != null) {
            zombiePlayer.stop();  // Stoppt das Zombie-Geräusch
        }
        if (musicThread != null && musicThread.isAlive()) {
            musicThread.interrupt();  // Stoppt den Musik-Thread, falls er noch läuft
        }
    }

    public void volume(double volume){
        mediaPlayer.setVolume(volume);
    }
}
