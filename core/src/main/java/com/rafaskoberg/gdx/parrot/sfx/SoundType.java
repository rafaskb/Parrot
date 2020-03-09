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
     * Returns the volume of this sound type, from {@code 0.0} to {@code 1.0}.
     */
    float getVolume();

    /**
     * Returns the pitch of this sound type.
     */
    float getPitch();

    /**
     * Returns the pitch variation of this sound type.
     */
    float getPitchVariation();

    /**
     * Returns the default {@link PlaybackMode} of this sound type.
     */
    PlaybackMode getPlaybackMode();

    /**
     * Returns a multiplier value used for Continuous sounds, namely how quickly should the sounds fade in or stop
     * playing.
     */
    float getContinuityFactor();

}
