package com.rafaskoberg.gdx.parrot.example;

import com.badlogic.gdx.audio.Music;
import com.rafaskoberg.gdx.parrot.music.ParrotMusicType;

public enum AmbienceType implements ParrotMusicType {
    PARROTS("parrots", "Parrots", 1.0f),
    RAIN("rain", "Rain", 1.0f),
    UNDERWATER("underwater", "Underwater", 1.0f),
    RESTAURANT("restaurant", "Restaurant", 1.0f);

    private final String filename;
    private final String trackName;
    private final float  volume;
    private       Music  music;

    AmbienceType(String filename, String trackName, float volume) {
        this.filename = filename;
        this.trackName = trackName;
        this.volume = volume;
    }

    public String getFilename() {
        return filename;
    }

    public String getTrackName() {
        return trackName;
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

    @Override
    public String toString() {
        return trackName;
    }
}
