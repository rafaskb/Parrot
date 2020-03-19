package com.rafaskoberg.gdx.parrot.example;

import com.badlogic.gdx.audio.Music;
import com.rafaskoberg.gdx.parrot.music.ParrotMusicType;

public enum MusicType implements ParrotMusicType {
    CRYSTAL_CAVE("crystal_cave", 1.0f);

    private final String filename;
    private final float  volume;
    private       Music  music;

    MusicType(String filename, float volume) {
        this.filename = filename;
        this.volume = volume;
    }

    public String getFilename() {
        return filename;
    }

    @Override
    public Music getMusic() {
        return music;
    }

    public void setMusic(Music music) {
        this.music = music;
    }

    @Override
    public float getRelativeVolume() {
        return volume;
    }
}
