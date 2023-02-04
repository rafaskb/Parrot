package com.rafaskoberg.gdx.parrot;

import com.rafaskoberg.gdx.parrot.sfx.PlaybackMode;
import com.rafaskoberg.gdx.parrot.sfx.SoundInstance;

/**
 * Properties class containing settings for Parrot. Changes to it can be made at any time.
 */
public class ParrotSettings {

    /**
     * Perceived loudness exponential curve to be applied to linear-based audio algorithms (plain old 0-1). Defaults to 3
     * <p>
     * See <a href="https://www.dr-lex.be/info-stuff/volumecontrols.html#about">Programming Volume Controls</a>
     */
    public int loudnessExponentialCurve = 3;

    /**
     * In the spatial system, distances beyond this limit will have the maximum amount of reduction. Defaults to 15
     */
    public float soundDistanceLimit = 15f;

    /**
     * Amount of reduction applied to sounds, in decibels, according to their distance. Defaults to -2
     */
    public float soundDistanceReduction = -2f;

    /**
     * Distances beyond this limit will have the maximum amount of pan. Defaults to 10
     */
    public float soundPanLimit = 10f;

    /**
     * Amount of pan applied to sounds, according to their distance and position. Defaults to 0.33
     */
    public float soundPanReduction = 0.33f;

    /**
     * Speed in meters per second squared the sound should move, in case of continuous sounds. Defaults to 75
     */
    public float soundContinuousSpeed = 75.0f;

    /**
     * Time in seconds it will take for a {@link PlaybackMode#CONTINUOUS continuous} sound to die without being touched. Defaults to 0.35
     */
    public float soundContinuousTimeout = 0.35f;

    /**
     * Time in seconds it will take for a {@link PlaybackMode#CONTINUOUS continuous} sound to reach it's full volume. Defaults to 0.15
     */
    public float soundContinuousFadeIn = 0.15f;

    /**
     * Time in seconds it will take for a sound to stop playing after being considered dead. Defaults to 0.4
     */
    public float soundDeathFadeOut = 0.40f;

    /**
     * Duration in seconds that will be assigned to {@link SoundInstance}s on platforms that don't report the duration of sounds. Currently
     * that's true for all platforms except Desktop.
     */
    public float soundDurationOnUnsupportedPlatforms = 15;

    /**
     * Duration in seconds that a music track takes to fade in when it's played. Defaults to 0.8
     */
    public float musicFadeInDuration = 0.8f;

    /**
     * Duration in seconds that a music track takes to fade out when it's stopped. Defaults to 0.8
     */
    public float musicFadeOutDuration = 0.8f;

    /*
     *  Feature: "Ignore Distant Tiny Sounds"
     */

    /**
     * Flag that toggles the "Ignore Distant Tiny Sounds" feature, which ignores tiny sounds being played too far from the listener. Applies
     * only for spatial sounds. Defaults to false.
     */
    public boolean ignoreDistantTinySounds = false;

    /**
     * Distance threshold for tiny sounds that are too far from the listener to be ignored. -1 uses the same value * from
     * {@link #soundDistanceLimit}. Defaults to -1.
     */
    public float ignoreDistantTinySoundsDistance = -1;

    /**
     * Sound length threshold in seconds for tiny sounds that should be ignored when play too far from the listener. Defaults to 1.
     */
    public float ignoreDistantTinySoundsDuration = 1;

    /**
     * Determines the {@link PlaybackMode}s of tiny distant sounds that should be ignored. If false, just normal ones are ignored. If true,
     * all modes are ignored. Defaults to false.
     */
    public boolean ignoreDistantTinySoundsOfAllModes = false;

}
