package com.rafaskoberg.gdx.parrot.sfx;

/**
 * Represents the category of a {@link ParrotSoundType}.
 */
public interface ParrotSoundCategory {

    /**
     * Returns the maximum amount of voices of this category allowed to coexist.
     */
    int getVoices();

    /**
     * Returns whether or not this sound category is spatial, meaning distance factors should be calculated to modify
     * pan and volume, giving a 3D sensation. Note that spatial effects only work for mono sounds.
     */
    boolean isSpatial();
}
