package com.rafaskoberg.gdx.parrot.example;

import com.rafaskoberg.gdx.parrot.sfx.ParrotSoundCategory;

/**
 * Different categories of sounds we have, each containing different settings.
 */
public enum SoundCategory implements ParrotSoundCategory {
    PLAYER(16, true),
    ENVIRONMENT(16, true),
    WORLD(4, false),
    RESTRICTED(1, false),
    USER_INTERFACE(8, false);

    private final int voices;
    private final boolean isSpatial;

    SoundCategory(int voices, boolean isSpatial) {
        this.voices = voices;
        this.isSpatial = isSpatial;
    }

    @Override
    public int getVoices() {
        return voices;
    }

    @Override
    public boolean isSpatial() {
        return isSpatial;
    }
}
