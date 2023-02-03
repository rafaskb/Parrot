package com.rafaskoberg.gdx.parrot.music;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * {@link Music} wrapper containing a lot of extra information to improve audio handling.
 */
public class MusicInstance implements Poolable {
    protected Music music;
    protected ParrotMusicType musicType;
    protected boolean isLooping;
    protected State state;
    protected State nextState;
    protected float stateTimer;
    protected int channel;
    protected int boomChannel;
    protected float targetVolume;

    // REMINDER: Reset members

    public MusicInstance() {
        reset();
    }

    /**
     * Returns the libGDX Music instance managed by this wrapper.
     */
    public Music getMusic() {
        return music;
    }

    /**
     * Returns {@link ParrotMusicType} of this instance.
     */
    public ParrotMusicType getMusicType() {
        return musicType;
    }

    /**
     * Returns the internal state of this instance.
     */
    public State getState() {
        return state;
    }

    /**
     * Returns the internal channel being used for this music instance. Defaults to 0.
     */
    public int getChannel() {
        return channel;
    }

    /**
     * Returns the boom channel this music is to be played on. Defaults to -1.
     */
    public int getBoomChannel() {
        return boomChannel;
    }

    /**
     * Returns whether this music instance is playing by checking if the current state is active.
     */
    public boolean isPlaying() {
        return state.isActive();
    }

    @Override
    public void reset() {
        this.music = null;
        this.musicType = null;
        this.isLooping = false;
        this.state = State.SILENT;
        this.nextState = null;
        this.stateTimer = 0f;
        this.channel = 0;
        this.boomChannel = -1;
        this.targetVolume = 1f;
    }

    public enum State {
        SILENT,
        FADING_IN,
        PLAYING,
        FADING_OUT,
        PAUSED,
        DISPOSING;

        public boolean isActive() {
            return this == FADING_IN || this == PLAYING || this == PAUSED;
        }
    }

}
