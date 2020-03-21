package com.rafaskoberg.gdx.parrot.example;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;
import com.rafaskoberg.gdx.parrot.sfx.ParrotSoundCategory;
import com.rafaskoberg.gdx.parrot.sfx.ParrotSoundType;
import com.rafaskoberg.gdx.parrot.sfx.PlaybackMode;

public enum SoundType implements ParrotSoundType {
    FOOTSTEPS("footsteps", SoundCategory.PLAYER, 3, 1.0f, 1.0f, 0.05f, PlaybackMode.NORMAL, 0.0f),
    WARNING_BEEP("warning_beep", SoundCategory.ENVIRONMENT, 1, 0.5f, 0.5f, 0.00f, PlaybackMode.ETERNAL, 0.0f),
    FLAMETHROWER_SPARK("flamethrower_spark", SoundCategory.PLAYER, 2, 1f, 1.0f, 0.20f, PlaybackMode.NORMAL, 0.0f),
    FLAMETHROWER("flamethrower", SoundCategory.PLAYER, 1, 0.3f, 1.0f, 0.00f, PlaybackMode.CONTINUOUS, 0.3f);

    private final Array<Sound>  sounds;
    private final String        filename;
    private final SoundCategory category;
    private final int           voices;
    private final float         volume;
    private final float         pitch;
    private final float         pitchVariation;
    private final PlaybackMode  playbackMode;
    private final float         continuityFactor;

    SoundType(String filename, SoundCategory category, int voices, float volume, float pitch, float pitchVariation, PlaybackMode playbackMode, float continuityFactor) {
        this.sounds = new Array<>();
        this.filename = filename;
        this.category = category;
        this.voices = voices;
        this.volume = volume;
        this.pitch = pitch;
        this.pitchVariation = pitchVariation;
        this.playbackMode = playbackMode;
        this.continuityFactor = continuityFactor;
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

}