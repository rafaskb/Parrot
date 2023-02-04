package com.rafaskoberg.gdx.parrot.example;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;
import com.rafaskoberg.gdx.parrot.sfx.ParrotSoundCategory;
import com.rafaskoberg.gdx.parrot.sfx.ParrotSoundType;
import com.rafaskoberg.gdx.parrot.sfx.PlaybackMode;
import com.rafaskoberg.gdx.parrot.util.NonRandomShuffle;

public enum SoundType implements ParrotSoundType {
    FOOTSTEPS(
        "footsteps", // Filename
        SoundCategory.PLAYER, // Category
        3, // Voices
        1.0f, // Volume
        0.1f, // Volume Variation
        1.0f, // Pitch
        0.05f, // Pitch Variation
        PlaybackMode.NORMAL, // Playback Mode
        0.0f, // Continuity Factor
        -1, // Priority
        1.0f // Non Random Shuffle Factor
    ),
    WARNING_BEEP(
        "warning_beep", // Filename
        SoundCategory.WORLD, // Category
        1, // Voices
        0.8f, // Volume
        0.05f, // Volume Variation
        0.5f, // Pitch
        0.15f, // Pitch Variation
        PlaybackMode.ETERNAL, // Playback Mode
        0.0f, // Continuity Factor
        0, // Priority
        1.0f // Non Random Shuffle Factor
    ),
    FLAMETHROWER_SPARK(
        "flamethrower_spark", // Filename
        SoundCategory.PLAYER, // Category
        2, // Voices
        1f, // Volume
        0, // Volume Variation
        1.0f, // Pitch
        0.20f, // Pitch Variation
        PlaybackMode.NORMAL, // Playback Mode
        0.0f, // Continuity Factor
        0, // Priority
        1.0f // Non Random Shuffle Factor
    ),
    FLAMETHROWER(
        "flamethrower", // Filename
        SoundCategory.PLAYER, // Category
        1, // Voices
        0.6f, // Volume
        0.1f, // Volume Variation
        1.0f, // Pitch
        0.00f, // Pitch Variation
        PlaybackMode.CONTINUOUS, // Playback Mode
        0.3f, // Continuity Factor
        0, // Priority
        1.0f // Non Random Shuffle Factor
    ),
    UI_CLICK(
        "ui_hover", // Filename
        SoundCategory.USER_INTERFACE, // Category
        2, // Voices
        0.50f, // Volume
        0, // Volume Variation
        1.5f, // Pitch
        0.0f, // Pitch Variation
        PlaybackMode.NORMAL, // Playback Mode
        0.0f, // Continuity Factor
        0, // Priority
        1.0f // Non Random Shuffle Factor
    ),
    UI_HOVER(
        "ui_hover", // Filename
        SoundCategory.USER_INTERFACE, // Category
        2, // Voices
        0.35f, // Volume
        0.1f, // Volume Variation
        1.0f, // Pitch
        0.03f, // Pitch Variation
        PlaybackMode.NORMAL, // Playback Mode
        0.0f, // Continuity Factor
        0, // Priority
        1.0f // Non Random Shuffle Factor
    );

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
    private final int priority;
    private final NonRandomShuffle<Sound> nonRandomShuffle;

    SoundType(String filename, SoundCategory category, int voices, float volume, float volumeVariation, float pitch, float pitchVariation, PlaybackMode playbackMode, float continuityFactor, int priority, float nonRandomShuffleFactor) {
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
        this.priority = priority;
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
    public int getPriority() {
        return priority;
    }

    @Override
    public NonRandomShuffle<Sound> getNonRandomShuffle() {
        return nonRandomShuffle;
    }
}