package com.rafaskoberg.gdx.parrot.sfx;

/**
 * List of modes a {@link SoundType} can be played.
 */
public enum PlaybackMode {

    /**
     * Normal mode, the sound is played once and then naturally dies.
     */
    NORMAL,

    /**
     * Special looping mode, the sound must be played constantly, otherwise it will start to fade and eventually be
     * killed. Useful for when the sound owner can't control when to stop playing it (e.g. A flamethrower firing bullets
     * constantly, but not knowing when to stop).
     * <p>
     * Continuous sounds have a mandatory limit of one voice at once, which means once two or more entities attempt to
     * play the same sound with this mode, the same instance will be shared among them.
     */
    CONTINUOUS,

    /**
     * Default looping mode, the sound is played once and loops forever until it's explicitly stopped or killed.
     */
    ETERNAL
}
