package com.rafaskoberg.gdx.parrot;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.Vector2;
import com.rafaskoberg.boom.Boom;
import com.rafaskoberg.gdx.parrot.music.MusicPlayer;
import com.rafaskoberg.gdx.parrot.music.MusicPlayerImpl;
import com.rafaskoberg.gdx.parrot.music.ParrotMusicType;
import com.rafaskoberg.gdx.parrot.sfx.ParrotSoundCategory;
import com.rafaskoberg.gdx.parrot.sfx.ParrotSoundType;
import com.rafaskoberg.gdx.parrot.sfx.PlaybackMode;
import com.rafaskoberg.gdx.parrot.sfx.SoundInstance;
import com.rafaskoberg.gdx.parrot.sfx.SoundPlayer;
import com.rafaskoberg.gdx.parrot.sfx.SoundPlayerImpl;

/** TODO Javadocs */
public class Parrot implements SoundPlayer, MusicPlayer {
    protected ParrotSettings settings;
    protected Boom           boom;
    protected SoundPlayer    soundPlayer;
    protected MusicPlayer    musicPlayer;

    /** TODO Javadocs */
    public Parrot() {
        this.settings = new ParrotSettings();
        this.boom = Boom.init();
        this.soundPlayer = new SoundPlayerImpl(this);
        this.musicPlayer = new MusicPlayerImpl(this);
    }

    /**
     * Returns the {@link ParrotSettings} instance controlling the settings of this library. Changes to it can be made
     * at any time.
     */
    public ParrotSettings getSettings() {
        return settings;
    }

    /**
     * Updates all music and sound effects handled by Parrot. Must be called constantly.
     *
     * @param delta Time in seconds since the last frame.
     */
    public void update(float delta) {
        updateMusic(delta);
        updateSounds(delta);
    }

    /**
     * Returns the {@link Boom} instance responsible for playing music and sound effects, in case it's available for the
     * current platform.
     */
    public Boom getBoom() {
        return boom;
    }

    /**
     * Sets the {@link Boom} instance responsible for playing music and sound effects.
     */
    public void setBoom(Boom boom) {
        this.boom = boom;
    }

    @Override
    public float getSoundPlayerVolume() {
        return soundPlayer.getSoundPlayerVolume();
    }

    @Override
    public void setSoundPlayerVolume(float volume) {
        soundPlayer.setSoundPlayerVolume(volume);
    }

    @Override
    public Vector2 getSpatialListenerCoordinates() {
        return soundPlayer.getSpatialListenerCoordinates();
    }

    @Override
    public void setSpatialListenerCoordinates(float x, float y) {
        soundPlayer.setSpatialListenerCoordinates(x, y);
    }

    @Override
    public void updateSounds(float delta) {
        soundPlayer.updateSounds(delta);
    }

    @Override
    public long playSound(ParrotSoundType type, float x, float y, float pitch, PlaybackMode mode, int boomChannel) {
        return soundPlayer.playSound(type, x, y, pitch, mode, boomChannel);
    }

    @Override
    public SoundInstance getSound(long internalId) {
        return soundPlayer.getSound(internalId);
    }

    @Override
    public void setSoundCoordinates(SoundInstance soundInstance, float x, float y) {
        soundPlayer.setSoundCoordinates(soundInstance, x, y);
    }

    @Override
    public void stopSound(ParrotSoundType type, boolean ignorePersistent) {
        soundPlayer.stopSound(type, ignorePersistent);
    }

    @Override
    public void stopSound(ParrotSoundCategory category, boolean ignorePersistent) {
        soundPlayer.stopSound(category, ignorePersistent);
    }

    @Override
    public void stopSound(SoundInstance soundInstance) {
        soundPlayer.stopSound(soundInstance);
    }

    @Override
    public void stopAllSounds(boolean ignorePersistent) {
        soundPlayer.stopAllSounds(ignorePersistent);
    }

    @Override
    public void killSound(ParrotSoundType type) {
        soundPlayer.killSound(type);
    }

    @Override
    public void killSound(ParrotSoundCategory category) {
        soundPlayer.killSound(category);
    }

    @Override
    public void killAllSounds() {
        soundPlayer.killAllSounds();
    }

    @Override
    public float getMusicPlayerVolume() {
        return musicPlayer.getMusicPlayerVolume();
    }

    @Override
    public void setMusicPlayerVolume(float volume) {
        musicPlayer.setMusicPlayerVolume(volume);
    }

    @Override
    public float getMusicChannelVolume(int channel) {
        return musicPlayer.getMusicChannelVolume(channel);
    }

    @Override
    public void setMusicChannelVolume(int channel, float volume) {
        musicPlayer.setMusicChannelVolume(channel, volume);
    }

    @Override
    public void updateMusic(float delta) {
        musicPlayer.updateMusic(delta);
    }

    @Override
    public Music playMusic(ParrotMusicType musicType, boolean loop, boolean fadeIn, int channel, int boomChannel) {
        return musicPlayer.playMusic(musicType, loop, fadeIn, channel, boomChannel);
    }

    @Override
    public void stopMusic(ParrotMusicType musicType, boolean gracefully) {
        musicPlayer.stopMusic(musicType, gracefully);
    }

    @Override
    public void stopMusicChannel(int channel, boolean gracefully) {
        musicPlayer.stopMusicChannel(channel, gracefully);
    }

    @Override
    public void stopAllMusic(boolean gracefully) {
        musicPlayer.stopAllMusic(gracefully);
    }

    @Override
    public boolean isMusicPlaying() {
        return musicPlayer.isMusicPlaying();
    }

    @Override
    public boolean isMusicPlaying(int channel) {
        return musicPlayer.isMusicPlaying(channel);
    }

    @Override
    public void dispose() {
        soundPlayer.dispose();
        musicPlayer.dispose();
    }

}
