package com.rafaskoberg.gdx.parrot.music;

/**
 * Interface containing information about a type of music.
 */
public interface ParrotMusicType {

    /**
     * Get the relative volume of the sound compared to other sounds.
     *
     * @return The relative volume of the sound.
     */
    float getRelativeVolume();

}
