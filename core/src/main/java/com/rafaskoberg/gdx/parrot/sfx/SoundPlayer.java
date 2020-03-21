package com.rafaskoberg.gdx.parrot.sfx;

import com.badlogic.gdx.utils.Disposable;

/**
 * Spatial sound player that handles all sounds being played in the game.
 */
public interface SoundPlayer extends Disposable {

    /**
     * Sets the volume of this {@link SoundPlayer} respecting perceived loudness equations.
     *
     * @param volume Linear volume from 0 to 1 to be set
     */
    void setSoundVolume(float volume);

    /**
     * Updates all the sounds. Must be called constantly.
     */
    default void updateSounds(float delta) {
        updateSounds(0, 0, delta);
    }

    /**
     * Updates all the sounds. Must be called constantly.
     *
     * @param x X coordinate of the sound listener, for spatial sounds.
     * @param y Y coordinate of the sound listener, for spatial sounds.
     */
    void updateSounds(float x, float y, float delta);

    /**
     * Register a {@link ParrotSoundType} to be played the next time this instance is updated.
     *
     * @return the ID associated with the created {@link SoundInstance}, which can be used for most operations in this
     * class.
     */
    default long playSound(ParrotSoundType type) {
        return playSound(type, 0, 0, 1, null);
    }

    /**
     * Register a {@link ParrotSoundType} to be played the next time this instance is updated with a specific pitch
     * value.
     *
     * @return the ID associated with the created {@link SoundInstance}, which can be used for most operations in this
     * class.
     */
    default long playSound(ParrotSoundType type, float pitch) {
        return playSound(type, 0, 0, pitch, null);
    }

    /**
     * Register a {@link ParrotSoundType} to be played the next time this instance is updated with the given
     * attributes.
     *
     * @return the ID associated with the created {@link SoundInstance}, which can be used for most operations in this
     * class.
     */
    default long playSound(ParrotSoundType type, float x, float y) {
        return playSound(type, x, y, 1, null);
    }

    /**
     * Register a {@link ParrotSoundType} to be played the next time this instance is updated with the given
     * attributes.
     *
     * @return the ID associated with the created {@link SoundInstance}, which can be used for most operations in this
     * class.
     */
    default long playSound(ParrotSoundType type, float x, float y, PlaybackMode mode) {
        return playSound(type, x, y, 1, mode);
    }

    /**
     * Register a {@link ParrotSoundType} to be played the next time this instance is updated with the given
     * attributes.
     *
     * @return the ID associated with the created {@link SoundInstance}, which can be used for most operations in this
     * class.
     */
    default long playSound(ParrotSoundType type, float x, float y, float pitch, PlaybackMode mode) {
        return playSound(type, x, y, 1, mode, -1);
    }

    /**
     * Register a {@link ParrotSoundType} to be played the next time this instance is updated with the given
     * attributes.
     *
     * @return the ID associated with the created {@link SoundInstance}, which can be used for most operations in this
     * class.
     */
    long playSound(ParrotSoundType type, float x, float y, float pitch, PlaybackMode mode, int boomChannel);

    /**
     * Returns the {@link SoundInstance} associated with the given ID, if any. Might return {@code null}.
     */
    SoundInstance getSound(long internalId);

    /**
     * Touches the sound instance associated with the given ID, so it doesn't die at the wrong time.
     *
     * @return boolean value indicating if the sound is still being handled.
     */
    default boolean touchSound(long internalId) {
        return touchSound(getSound(internalId));
    }

    /**
     * Touches the sound instance, so it doesn't die at the wrong time.
     *
     * @return boolean value indicating if the sound is still being handled.
     */
    default boolean touchSound(SoundInstance soundInstance) {
        if(soundInstance == null) return false;

        // Touch instance
        if(soundInstance.isActive()) soundInstance.lastTouch = System.currentTimeMillis();

        // Return flag indicating if instance is valid
        return soundInstance.isValid();
    }

    /**
     * Touches the sound instance associated with the given ID, so it doesn't die at the wrong time.
     *
     * @return boolean value indicating if the sound is still being handled.
     */
    default boolean isSoundValid(long id) {
        SoundInstance soundInstance = getSound(id);
        return soundInstance != null && soundInstance.isValid();
    }

    /**
     * Sets the position of the sound associated with the given ID. Note this will only work on mono sounds.
     */
    default void setSoundCoordinates(long id, float x, float y) {
        setSoundCoordinates(getSound(id), x, y);
    }

    /**
     * Sets the position of the given {@link SoundInstance}. Note this will only work on mono sounds.
     */
    void setSoundCoordinates(SoundInstance soundInstance, float x, float y);

    /**
     * Stops the sound associated with the given ID.
     */
    default void stopSound(long id) {
        stopSound(getSound(id));
    }

    /**
     * Calls {@link #stopSound(ParrotSoundType, boolean)} with {@code false}.
     */
    default void stopSound(ParrotSoundType type) {
        stopSound(type, false);
    }

    /**
     * Stops all the currently active sounds under the given {@link ParrotSoundType}.
     *
     * @param ignorePersistent Whether or not {@link SoundInstance#isPersistent() persistent sounds} should be ignored.
     */
    void stopSound(ParrotSoundType type, boolean ignorePersistent);

    /**
     * Calls {@link #stopSound(ParrotSoundCategory, boolean)} with {@code false}.
     */
    default void stopSound(ParrotSoundCategory category) {
        stopSound(category, false);
    }

    /**
     * Stops all the currently active sounds under the given {@link ParrotSoundCategory}.
     *
     * @param ignorePersistent Whether or not {@link SoundInstance#isPersistent() persistent sounds} should be ignored.
     */
    void stopSound(ParrotSoundCategory category, boolean ignorePersistent);

    /**
     * Calls {@link #stopAllSounds()} with {@code false}.
     */
    default void stopAllSounds() {
        stopAllSounds(false);
    }

    /**
     * Stops all sounds.
     *
     * @param ignorePersistent Whether or not {@link SoundInstance#isPersistent() persistent sounds} should be ignored.
     */
    void stopAllSounds(boolean ignorePersistent);

    /**
     * Stops the given {@link SoundInstance}.
     */
    void stopSound(SoundInstance soundInstance);

    /**
     * Instantly kills the sound associated with the given ID.
     */
    default void killSound(long id) {
        if(id == -1) return;
        killSound(getSound(id));
    }

    /**
     * Instantly kills all the currently active sounds under the given {@link ParrotSoundType}.
     */
    void killSound(ParrotSoundType type);

    /**
     * Instantly kills all the currently active sounds under the given {@link ParrotSoundCategory}.
     */
    void killSound(ParrotSoundCategory category);

    /**
     * Instantly kills all sounds.
     */
    void killAllSounds();

    /**
     * Instantly kills the given {@link SoundInstance}.
     */
    default void killSound(SoundInstance soundInstance) {
        if(soundInstance == null) return;

        // Stop sound
        if(soundInstance.sound != null) soundInstance.sound.stop(soundInstance.internalId);

        // Nullify instance
        soundInstance.sound = null;
        soundInstance.id = -1;
        soundInstance.internalId = -1;
        soundInstance.playMe = false;
    }

}
