package com.rafaskoberg.gdx.parrot.example;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;
import com.rafaskoberg.gdx.parrot.sfx.ParrotSoundCategory;
import com.rafaskoberg.gdx.parrot.sfx.ParrotSoundType;
import com.rafaskoberg.gdx.parrot.sfx.PlaybackMode;
import com.rafaskoberg.gdx.parrot.util.NonRandomShuffle;

public enum SoundType implements ParrotSoundType {
    FOOTSTEPS("footsteps", SoundCategory.PLAYER, 3, 1.0f, 0.1f, 1.0f, 0.05f, PlaybackMode.NORMAL, 0.0f, 1.0f),
    WARNING_BEEP("warning_beep", SoundCategory.WORLD, 1, 0.8f, 0, 0.5f, 0.00f, PlaybackMode.ETERNAL, 0.0f, 1.0f),
    FLAMETHROWER_SPARK("flamethrower_spark", SoundCategory.PLAYER, 2, 1f, 0, 1.0f, 0.20f, PlaybackMode.NORMAL, 0.0f, 1.0f),
    FLAMETHROWER("flamethrower", SoundCategory.PLAYER, 1, 0.6f, 0, 1.0f, 0.00f, PlaybackMode.CONTINUOUS, 0.3f, 1.0f),
    UI_CLICK("ui_hover", SoundCategory.USER_INTERFACE, 2, 0.50f, 0, 1.5f, 0.0f, PlaybackMode.NORMAL, 0.0f, 1.0f),
    UI_HOVER("ui_hover", SoundCategory.USER_INTERFACE, 2, 0.35f, 0.2f, 1.0f, 0.03f, PlaybackMode.NORMAL, 0.0f, 1.0f);

    private final Array<Sound> sounds;
    private final String filename;
    private final SoundCategory category;
    private final int voices;
    private final float volume;
    private final float volumeVariation;
    private final float pitch;
    private final float pitchVariation;
    private final PlaybackMode playbackMode;
    private final float continuityFactor;
    private final NonRandomShuffle<Sound> nonRandomShuffle;

    SoundType(String filename, SoundCategory category, int voices, float volume, float volumeVariation, float pitch, float pitchVariation, PlaybackMode playbackMode, float continuityFactor, float nonRandomShuffleFactor) {
        this.sounds = new Array<>();
        this.filename = filename;
        this.category = category;
        this.voices = voices;
        this.volume = volume;
        this.volumeVariation = volumeVariation;
        this.pitch = pitch;
        this.pitchVariation = pitchVariation;
        this.playbackMode = playbackMode;
        this.continuityFactor = continuityFactor;
        this.nonRandomShuffle = new NonRandomShuffle<>(nonRandomShuffleFactor);
    }

    public String getFilename() {
        return filename;
    }

    @Override
    public Array<Sound> getSounds() {
        return sounds;
    }

    @Override
    public ParrotSoundCategory getCategory() {
        return category;
    }

    @Override
    public int getVoices() {
        return voices;
    }

    @Override
    public float getVolume() {
        return volume;
    }

    @Override
    public float getVolumeVariation() {
        return volumeVariation;
    }

    @Override
    public float getPitch() {
        return pitch;
    }

    @Override
    public float getPitchVariation() {
        return pitchVariation;
    }

    @Override
    public PlaybackMode getPlaybackMode() {
        return playbackMode;
    }

    @Override
    public float getContinuityFactor() {
        return continuityFactor;
    }

    @Override
    public NonRandomShuffle getNonRandomShuffle() {
        return nonRandomShuffle;
    }
}