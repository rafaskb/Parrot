package com.rafaskoberg.gdx.parrot.sfx;

public class SoundSettings {
    /**
     * Perceived loudness exponential curve to be applied to linear-based audio algorithms (plain old 0-1). Defaults to
     * 4
     * <p>
     * See <a href="https://www.dr-lex.be/info-stuff/volumecontrols.html#about">Programming Volume Controls</a>
     */
    public float loudnessExponentialCurve = 4;

    /**
     * In the spatial system, distances beyond this limit will have the maximum amount of reduction. Defaults to 15
     */
    public float distanceLimit = 15f;

    /**
     * Amount of reduction applied to sounds, in decibels, according to their distance. Defaults to -2
     */
    public float distanceReduction = -2f;

    /**
     * Distances beyond this limit will have the maximum amount of pan. Defaults to 10
     */
    public float panLimit = 10f;

    /**
     * Amount of pan applied to sounds, according to their distance and position. Defaults to 0.33
     */
    public float panReduction = 0.33f;

    /**
     * Speed in meters per second squared the sound should move, in case of continuous sounds. Defaults to 75
     */
    public float continuousSpeed = 75.0f;

    /**
     * Time in seconds it will take for a {@link PlaybackMode#CONTINUOUS continuous} sound to die without being touched.
     * Defaults to 0.35
     */
    public float continuousTimeout = 0.35f;

    /**
     * Time in seconds it will take for a {@link PlaybackMode#CONTINUOUS continuous} sound to reach it's full volume.
     * Defaults to 0.15
     */
    public float continuousFadeIn = 0.15f;

    /**
     * Time in seconds it will take for a sound to stop playing after being considered dead. Defaults to 0.4
     */
    public float deathFadeOut = 0.40f;

    /**
     * Duration in seconds that will be assigned to {@link SoundInstance}s on platforms that don't report the duration
     * of sounds. Currently that's true for all platforms except Desktop.
     */
    public float unsupportedPlatformSoundDuration = 15;

}