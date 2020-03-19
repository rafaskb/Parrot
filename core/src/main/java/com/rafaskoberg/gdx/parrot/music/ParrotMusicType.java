package com.rafaskoberg.gdx.parrot.music;

import com.badlogic.gdx.audio.Music;

/**
 * Interface containing information about a type of music.
 */
public interface ParrotMusicType {

    /**
     * Returns the {@link Music} instance associated with this music type.
     */
    Music getMusic();

    /**
     * Get the relative volume of the sound compared to other sounds.
     *
     * @return The relative volume of the sound.
     */
    float getRelativeVolume();

}
