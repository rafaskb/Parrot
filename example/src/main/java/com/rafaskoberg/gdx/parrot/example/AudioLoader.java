package com.rafaskoberg.gdx.parrot.example;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.rafaskoberg.gdx.parrot.util.NonRandomShuffle;

public class AudioLoader {
    private static final String SOUNDS_DIR = "sounds/";
    private static final String MUSIC_DIR = "music/";
    private static final String AMBIENCE_DIR = "ambience/";
    private static final String[] EXTENSIONS = {".wav", ".ogg", ".mp3"};

    public static void load(SoundType soundType) {
        // Load file directly
        for(String extension : EXTENSIONS) {
            FileHandle file = Gdx.files.internal(SOUNDS_DIR + soundType.getFilename() + extension);
            if(file.exists()) {
                try {
                    Sound sound = Gdx.audio.newSound(file);
                    soundType.getSounds().add(sound);
                    break;
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // Load indexed files
        int index = 1;
        boolean successful = false;
        do {
            successful = false;
            for(String extension : EXTENSIONS) {
                FileHandle file = Gdx.files.internal(SOUNDS_DIR + soundType.getFilename() + "_" + index + extension);
                if(file.exists()) {
                    try {
                        Sound sound = Gdx.audio.newSound(file);
                        soundType.getSounds().add(sound);
                        successful = true;
                        index++;
                        break;
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } while(successful);

        // Register sounds to NonRandomShuffle
        NonRandomShuffle<Sound> nonRandomShuffle = soundType.getNonRandomShuffle();
        if(nonRandomShuffle != null) {
            nonRandomShuffle.setValues(soundType.getSounds().toArray(Sound.class));
        }
    }

    public static void load(MusicType musicType) {
        // Load file directly
        for(String extension : EXTENSIONS) {
            FileHandle file = Gdx.files.internal(MUSIC_DIR + musicType.getFilename() + extension);
            if(file.exists()) {
                try {
                    Music music = Gdx.audio.newMusic(file);
                    musicType.setMusic(music);
                    break;
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void load(AmbienceType ambienceType) {
        // Load file directly
        for(String extension : EXTENSIONS) {
            FileHandle file = Gdx.files.internal(AMBIENCE_DIR + ambienceType.getFilename() + extension);
            if(file.exists()) {
                try {
                    Music music = Gdx.audio.newMusic(file);
                    ambienceType.setMusic(music);
                    break;
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
