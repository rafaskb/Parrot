package com.rafaskoberg.gdx.parrot.sfx;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * {@link Sound} wrapper containing a lot of extra information to improve the sound handling.
 */
public class SoundInstance implements Poolable {
    protected Sound sound;
    protected ParrotSoundType type;
    protected long internalId;
    protected long id;
    protected float positionX;
    protected float positionY;
    protected float volumeFactor;
    protected float pitch;
    protected boolean isDying;
    protected boolean playMe;
    protected PlaybackMode playbackMode;
    protected long lastTouch;
    protected float duration;
    protected float time;
    protected float currentVolume;
    protected float currentPan;
    protected int boomChannel;
    private boolean persistent;

    // REMINDER: Reset members

    public SoundInstance() {
        reset();
    }

    /**
     * Returns the libGDX Sound instance managed by this wrapper.
     */
    public Sound getSound() {
        return sound;
    }

    /**
     * Returns the public ID of this instance, which is recognized by {@link SoundPlayerImpl}.
     */
    public long getId() {
        return id;
    }

    /**
     * Returns the {@link ParrotSoundType} of this instance.
     */
    public ParrotSoundType getType() {
        return type;
    }

    /**
     * Returns the {@link PlaybackMode} of this instance.
     */
    public PlaybackMode getPlaybackMode() {
        return playbackMode;
    }

    /**
     * Returns the current position of this sound in the X axis. Defaults to 0.
     */
    public float getPositionX() {
        return positionX;
    }

    /**
     * Returns the current position of this sound in the Y axis. Defaults to 0.
     */
    public float getPositionY() {
        return positionY;
    }

    /**
     * Returns the current volume of this sound, from 0 to 1.
     */
    public float getCurrentVolume() {
        return currentVolume;
    }

    /**
     * Returns the current pan of this sound, from -1 to 1.
     */
    public float getCurrentPan() {
        return currentPan;
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

    /**
     * Returns the pitch of this sound. Default is 1.0.
     */
    public float getPitch() {
        return pitch;
    }

    /**
     * Sets the pitch of this sound. Default is 1.0.
     */
    public void setPitch(float pitch) {
        if(sound != null) sound.setPitch(internalId, pitch);
    }

    /**
     * Returns the boom channel this sound is to be played on, or -1 if it should respect the {@link ParrotSoundType}'s value.
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
     * Returns whether or not this instance is valid. That is, if it has been played already and the internal Internal ID is valid.
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

    @Override
    public void reset() {
        this.sound = null;
        this.type = null;
        this.internalId = -1;
        this.id = -1;
        this.positionX = 0.0f;
        this.positionY = 0.0f;
        this.pitch = 1.0f;
        this.isDying = false;
        this.playMe = true;
        this.playbackMode = PlaybackMode.NORMAL;
        this.lastTouch = 0;
        this.duration = 0.0f;
        this.time = 0.0f;
        this.currentVolume = 1.0f;
        this.currentPan = 0.0f;
        this.boomChannel = -1;
        this.persistent = false;
    }
}
