package com.rafaskoberg.gdx.parrot;

import com.badlogic.gdx.graphics.Camera;
import com.rafaskoberg.boom.Boom;
import com.rafaskoberg.gdx.parrot.sfx.SoundInstance;
import com.rafaskoberg.gdx.parrot.sfx.SoundPlayer;
import com.rafaskoberg.gdx.parrot.sfx.SoundPlayerImpl;
import com.rafaskoberg.gdx.parrot.sfx.SoundSettings;
import com.rafaskoberg.gdx.parrot.sfx.ParrotSoundType;
import com.rafaskoberg.gdx.parrot.sfx.PlaybackMode;
import com.rafaskoberg.gdx.parrot.sfx.ParrotSoundCategory;

/** TODO Javadocs */
public class Parrot implements SoundPlayer {
    protected Boom        boom;
    protected SoundPlayer soundPlayer;

    /** TODO Javadocs */
    public Parrot() {
        this.boom = Boom.init();
        this.soundPlayer = new SoundPlayerImpl();
    }

    @Override
    public void setSoundVolume(float volume) {
        soundPlayer.setSoundVolume(volume);
    }

    @Override
    public SoundSettings getSoundSettings() {
        return soundPlayer.getSoundSettings();
    }

    @Override
    public void updateSounds(Camera camera, float delta) {
        soundPlayer.updateSounds(camera, delta);
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
    public void stopAllSounds(boolean ignorePersistent) {
        soundPlayer.stopAllSounds(ignorePersistent);
    }

    @Override
    public void stopSound(SoundInstance soundInstance) {
        soundPlayer.stopSound(soundInstance);
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
    public void dispose() {
        soundPlayer.dispose();
        // TODO
    }

}
