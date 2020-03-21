package com.rafaskoberg.gdx.parrot.example;

import com.badlogic.gdx.audio.Music;
import com.rafaskoberg.gdx.parrot.music.ParrotMusicType;

public enum MusicType implements ParrotMusicType {
    CRYSTAL_CAVE("crystal_cave", "Crystal Cave", "The Cynic Project", 0.8f),
    SNOWLAND("snowland", "Snowland", "Matthew Pablo", 0.8f),
    THE_LAST_ENCOUNTER("the_last_encounter", "The Last Encounter", "Matthew Pablo", 1.0f),
    IMMINENT_THREAT("imminent_threat", "Imminent Threat", "Matthew Pablo", 0.8f),
    RIVERSIDE_RIDE("riverside_ride", "Riverside Ride", "Matthew Pablo", 0.8f);

    private final String filename;
    private final String trackName;
    private final String artistName;
    private final float  volume;
    private       Music  music;

    MusicType(String filename, String trackName, String artistName, float volume) {
        this.filename = filename;
        this.trackName = trackName;
        this.artistName = artistName;
        this.volume = volume;
    }

    public String getFilename() {
        return filename;
    }

    public String getTrackName() {
        return trackName;
    }

    public String getArtistName() {
        return artistName;
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
