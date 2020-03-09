package com.rafaskoberg.gdx.parrot.sfx;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;

/**
 * Interface containing information about a type of sound.
 */
public interface SoundType {

    /**
     * Returns an {@link Array} of {@link Sound}s associated with this {@link SoundType}.
     */
    Array<Sound> getSounds();

    /**
     * Returns the {@link SoundCategory} of this sound.
     */
    SoundCategory getCategory();

    /**
     * Returns the maximum amount of voices of this sound type allowed to coexist.
     */
    int getVoices();

    /**
     * Returns the volume of this sound type, from 0 to 1. Defaults to 1
     */
    default float getVolume() {
        return 1;
    }

    /**
     * Returns the pitch of this sound type. Defaults to 1
     */
    default float getPitch() {
        return 0.1f;
    }

    /**
     * Returns the pitch variation of this sound type. Defaults to 0.05
     */
    default float getPitchVariation() {
        return 0.05f;
    }

    /**
     * Returns the default {@link PlaybackMode} of this sound type. Defaults to {@link PlaybackMode#NORMAL}.
     */
    default PlaybackMode getPlaybackMode() {
        return PlaybackMode.NORMAL;
    }

    /**
     * Returns a multiplier value used for {@link PlaybackMode#CONTINUOUS Continuous} sounds, which determines how
     * quickly the sound should die after it stops being touched. This value is used to multiply {@link
     * SoundSettings#continuousTimeout}. Defaults to 1.
     */
    default float getContinuityFactor() {
        return 1;
    }

}
