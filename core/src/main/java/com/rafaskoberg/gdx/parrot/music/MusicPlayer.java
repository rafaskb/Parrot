package com.rafaskoberg.gdx.parrot.music;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Disposable;
import com.rafaskoberg.boom.Boom;

/**
 * Music system that handles all music tracks being played in the game.
 */
public interface MusicPlayer extends Disposable {

    /**
     * Returns the linear volume of this {@link MusicPlayer}, as passed to {@link #setMusicPlayerVolume(float)}.
     */
    float getMusicPlayerVolume();

    /**
     * Sets the volume of this {@link MusicPlayer} respecting perceived loudness equations.
     *
     * @param volume Linear volume from 0 to 1 to be set
     */
    void setMusicPlayerVolume(float volume);

    /**
     * Returns the linear volume of a certain channel of this {@link MusicPlayer}, as passed to {@link
     * #setMusicChannelVolume(int, float)}.
     *
     * @param channel Internal channel
     */
    float getMusicChannelVolume(int channel);

    /**
     * Sets the volume of a certain channel of this {@link MusicPlayer} respecting perceived loudness equations.
     *
     * @param channel Internal channel
     * @param volume  Linear volume from 0 to 1 to be set
     */
    void setMusicChannelVolume(int channel, float volume);

    /**
     * Updates all music instances. Must be called constantly.
     */
    void updateMusic(float delta);

    /**
     * Register a {@link ParrotMusicType} to be played the next time this instance is updated.
     *
     * @return The {@link Music} instance that will be played.
     */
    default Music playMusic(ParrotMusicType musicType) {
        return playMusic(musicType, false, true, 0, -1);
    }

    /**
     * Register a {@link ParrotMusicType} to be played the next time this instance is updated.
     *
     * @param loop Whether or not the music should loop.
     * @return The {@link Music} instance that will be played.
     */
    default Music playMusic(ParrotMusicType musicType, boolean loop) {
        return playMusic(musicType, loop, true, 0, -1);
    }

    /**
     * Register a {@link ParrotMusicType} to be played the next time this instance is updated.
     *
     * @param loop   Whether or not the music should loop.
     * @param fadeIn Whether or not the music should fade-in when it starts playing.
     * @return The {@link Music} instance that will be played.
     */
    default Music playMusic(ParrotMusicType musicType, boolean loop, boolean fadeIn) {
        return playMusic(musicType, loop, fadeIn, 0, -1);
    }

    /**
     * Register a {@link ParrotMusicType} to be played the next time this instance is updated with the given
     * attributes.
     *
     * @param loop        Whether or not the music should loop.
     * @param fadeIn      Whether or not the music should fade-in when it starts playing.
     * @param channel     Internal channel to play this music through.
     * @param boomChannel Boom channel to play this music through.
     * @return The {@link Music} instance that will be played.
     */
    Music playMusic(ParrotMusicType musicType, boolean loop, boolean fadeIn, int channel, int boomChannel);

    /**
     * Gracefully pauses the music of the given type currently being played.
     */
    default void pauseMusic(ParrotMusicType musicType) {
        pauseMusic(musicType, true);
    }

    /**
     * Pauses the music of the given type currently being played.
     *
     * @param gracefully Whether or not the action should be progressive.
     */
    void pauseMusic(ParrotMusicType musicType, boolean gracefully);

    /**
     * Gracefully pauses the music being played in the given channel.
     *
     * @param channel Internal music channel.
     */
    default void pauseMusicChannel(int channel) {
        pauseMusicChannel(channel, true);
    }

    /**
     * Pauses the music being played in the given channel.
     *
     * @param channel    Internal music channel.
     * @param gracefully Whether or not the action should be progressive.
     */
    void pauseMusicChannel(int channel, boolean gracefully);

    /**
     * Gracefully resumes the music of the given type currently being played.
     */
    default void resumeMusic(ParrotMusicType musicType) {
        resumeMusic(musicType, true);
    }

    /**
     * Resumes the music of the given type currently being played.
     *
     * @param gracefully Whether or not the action should be progressive.
     */
    void resumeMusic(ParrotMusicType musicType, boolean gracefully);

    /**
     * Gracefully resumes the music being played in the given channel.
     *
     * @param channel Internal music channel.
     */
    default void resumeMusicChannel(int channel) {
        resumeMusicChannel(channel, true);
    }

    /**
     * Resumes the music being played in the given channel.
     *
     * @param channel    Internal music channel.
     * @param gracefully Whether or not the action should be progressive.
     */
    void resumeMusicChannel(int channel, boolean gracefully);

    /**
     * Gracefully stops the music of the given type currently being played.
     */
    default void stopMusic(ParrotMusicType musicType) {
        stopMusic(musicType, true);
    }

    /**
     * Stops the music of the given type currently being played.
     *
     * @param gracefully Whether or not the action should be progressive.
     */
    void stopMusic(ParrotMusicType musicType, boolean gracefully);

    /**
     * Gracefully stops the music being played in the given channel.
     *
     * @param channel Internal music channel.
     */
    default void stopMusicChannel(int channel) {
        stopMusicChannel(channel, true);
    }

    /**
     * Stops the music being played in the given channel.
     *
     * @param channel    Internal music channel.
     * @param gracefully Whether or not the action should be progressive.
     */
    void stopMusicChannel(int channel, boolean gracefully);

    /**
     * Gracefully stops all music being played.
     */
    default void stopAllMusic() {
        stopAllMusic(true);
    }

    /**
     * Stops all music being played.
     *
     * @param gracefully Whether or not the action should be progressive.
     */
    void stopAllMusic(boolean gracefully);

    /**
     * Returns whether or not there is any music currently being played by this player.
     */
    boolean isMusicPlaying();

    /**
     * Returns whether or not there is any music currently being played by this player in a specific channel.
     *
     * @param channel Internal music channel.
     */
    boolean isMusicPlaying(int channel);

    /**
     * Returns the current music being played in the given channel, in case the channel is active.
     *
     * @param channel Internal music channel.
     * @return The music type being played.
     */
    ParrotMusicType getCurrentMusic(int channel);

    /**
     * Sets the {@link Boom} instance responsible for playing music tracks.
     */
    void setBoom(Boom boom);

}
