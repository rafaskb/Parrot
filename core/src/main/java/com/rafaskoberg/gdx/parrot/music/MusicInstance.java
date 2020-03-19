package com.rafaskoberg.gdx.parrot.music;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * {@link Music} wrapper containing a lot of extra information to improve audio handling.
 */
public class MusicInstance implements Poolable {
    protected Music           music;
    protected ParrotMusicType musicType;
    protected boolean         isLooping;
    protected boolean         shouldFadeIn;
    protected State           state;
    protected float           stateTimer;
    protected int             channel;
    protected int             boomChannel;
    protected float           targetVolume;

    // REMINDER: Reset members

    public MusicInstance() {
        reset();
    }

    @Override
    public void reset() {
        this.music = null;
        this.musicType = null;
        this.isLooping = false;
        this.shouldFadeIn = true;
        this.state = State.SILENT;
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
        DISPOSING;

        public boolean isActive() {
            return this == FADING_IN || this == PLAYING;
        }
    }

}