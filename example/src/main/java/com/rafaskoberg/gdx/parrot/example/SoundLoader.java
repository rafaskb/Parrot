package com.rafaskoberg.gdx.parrot.example;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;

public class SoundLoader {
    private static final String   ROOT_DIR   = "example/assets/sounds/";
    private static final String[] EXTENSIONS = {".wav", ".ogg", ".mp3"};

    public static void load(SoundType soundType) {
        // Load file directly
        for(String extension : EXTENSIONS) {
            FileHandle file = Gdx.files.internal(ROOT_DIR + soundType.getFilename() + extension);
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
                FileHandle file = Gdx.files.internal(ROOT_DIR + soundType.getFilename() + "_" + index + extension);
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
    }

}
