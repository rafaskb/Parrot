package com.rafaskoberg.gdx.parrot.sfx;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;
import com.rafaskoberg.gdx.parrot.ParrotSettings;
import com.rafaskoberg.gdx.parrot.util.NonRandomShuffle;

/**
 * Interface containing information about a type of sound.
 */
public interface ParrotSoundType {

    /**
     * Returns an {@link Array} of {@link Sound}s associated with this {@link ParrotSoundType}.
     */
    Array<Sound> getSounds();

    /**
     * Returns the {@link ParrotSoundCategory} of this sound.
     */
    ParrotSoundCategory getCategory();

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
     * Returns the volume variation of this sound type. Defaults to 0
     */
    default float getVolumeVariation() {
        return 0f;
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
     * Returns a multiplier value used for {@link PlaybackMode#CONTINUOUS Continuous} sounds, which determines how quickly the sound should
     * die after it stops being touched. This value is used to multiply {@link ParrotSettings#soundContinuousTimeout}. Defaults to 1.
     */
    default float getContinuityFactor() {
        return 1;
    }

    /**
     * Returns the non random shuffle object of this sound type.
     * <li>The closer to 0 its factor is, the more random the sound index selection of a type will be.</li>
     * <li>The closer to 1, the more likely a sound index is to play if it hasn't played in a while, and vice versa.</li>
     */
    NonRandomShuffle<Sound> getNonRandomShuffle();

}
