package com.rafaskoberg.gdx.parrot.sfx;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;

/**
 * {@link Sound} wrapper containing a lot of extra information to improve the sound handling.
 */
public class SoundInstance {
    protected Sound        sound         = null;
    protected SoundType    type          = null;
    protected long         internalId    = -1;
    protected long         id            = -1;
    protected float        positionX     = 0.0f;
    protected float        positionY     = 0.0f;
    protected float        pitch         = 1.0f;
    protected boolean      isDying       = false;
    protected boolean      playMe        = true;
    protected PlaybackMode playbackMode  = PlaybackMode.NORMAL;
    protected long         lastTouch     = 0;
    protected float        duration      = 0.0f;
    protected float        time          = 0.0f;
    protected float        currentVolume = 1.0f;
    protected float        currentPan    = 0.0f;
    protected int          boomChannel   = -1;
    private   boolean      persistent    = false;

    protected SoundInstance() {
    }

    /**
     * Returns the public ID of this instance, which is recognized by {@link SoundPlayerImpl}.
     */
    public long getId() {
        return id;
    }

    /**
     * Returns the {@link SoundType} of this instance.
     */
    public SoundType getType() {
        return type;
    }

    /**
     * Returns the {@link PlaybackMode} of this instance.
     */
    public PlaybackMode getPlaybackMode() {
        return playbackMode;
    }

    /**
     * Returns the duration in seconds of the sound associated with this instance. Returns {@code 0} in case of errors.
     */
    public float getDuration() {
        return duration;
    }

    /**
     * Manually sets the duration in seconds of the sound associated with this instance.
     */
    public void setDuration(float duration) {
        this.duration = duration;
    }

    /** Sets the pitch of this sound. Default is 1.0. */
    public void setPitch(float pitch) {
        if(sound != null) sound.setPitch(internalId, pitch);
    }

    /**
     * Returns the boom channel this sound is to be played on, or -1 if it should respect the {@link SoundType}'s
     * value.
     */
    public int getBoomChannel() {
        return boomChannel;
    }

    /**
     * Returns {@code true} if this sound is persistent, meaning it won't be stopped after switching rooms.
     */
    public boolean isPersistent() {
        return persistent;
    }

    /**
     * Sets whether or not this sound is persistent. If {@code true}, it won't be stopped after switching rooms.
     */
    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
    }

    /**
     * Returns whether or not this instance is valid. That is, if it has been played already and the internal Internal
     * ID is valid.
     */
    public boolean isValid() {
        return !(internalId == -1 && !playMe);
    }

    /**
     * Returns whether or not this sound is valid and active.
     */
    public boolean isActive() {
        return isValid() && !isDying;
    }

    /**
     * Returns whether or not this sound's timer has expired its duration. Only works on the Desktop backend.
     */
    protected boolean isExpired() {
        boolean looping = playbackMode != PlaybackMode.NORMAL;
        float duration = getDuration();
        return !looping && !MathUtils.isZero(duration) && time > duration;
    }

}
