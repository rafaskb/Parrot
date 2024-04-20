package com.rafaskoberg.gdx.parrot.sfx;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.LongMap;
import com.badlogic.gdx.utils.LongMap.Entry;
import com.badlogic.gdx.utils.ObjectFloatMap;
import com.badlogic.gdx.utils.Pools;
import com.rafaskoberg.boom.Boom;
import com.rafaskoberg.gdx.parrot.Parrot;
import com.rafaskoberg.gdx.parrot.ParrotSettings;
import com.rafaskoberg.gdx.parrot.util.ParrotUtils;

import java.util.Comparator;

/**
 * Advanced spatial sound player that handles all sounds being played in the game.
 */
public class SoundPlayerImpl implements SoundPlayer {
    // Collections
    private final Array<SoundInstance> soundInstances;
    private final LongMap<SoundInstance> soundsById;
    private final LongMap<Array<Vector2>> continuousPositionsById;
    private final ObjectFloatMap<ParrotSoundCategory> pitchFactorsByCategory;

    // Members
    private final Parrot parrot;
    private final ParrotSettings settings;
    private final Vector2 listenerPosition;
    private final Vector2 tmpVec;
    protected Boom boom;
    private long nextId;
    private float rawVolume;
    private float masterVolume;
    private Comparator<SoundInstance> priorityComparator;

    public SoundPlayerImpl(Parrot parrot) {
        // Collections
        this.soundInstances = new Array<>();
        this.soundsById = new LongMap<>();
        this.continuousPositionsById = new LongMap<>();
        this.pitchFactorsByCategory = new ObjectFloatMap<>();

        // Members
        this.parrot = parrot;
        this.settings = parrot.getSettings();
        this.listenerPosition = new Vector2();
        this.tmpVec = new Vector2();
        this.nextId = 1;
        this.rawVolume = 1.0f;
        this.masterVolume = 1.0f;
        this.priorityComparator = Comparator.comparingInt(SoundInstance::getPriority).thenComparingLong(SoundInstance::getId);
    }

    @Override
    public float getSoundPlayerVolume() {
        return rawVolume;
    }

    @Override
    public void setSoundPlayerVolume(float volume) {
        this.rawVolume = MathUtils.clamp(volume, 0, 1);
        this.masterVolume = ParrotUtils.getPerceivedVolume(rawVolume, settings.loudnessExponentialCurve);
    }

    @Override
    public Vector2 getSpatialListenerCoordinates() {
        return listenerPosition;
    }

    @Override
    public void setSpatialListenerCoordinates(float x, float y) {
        listenerPosition.set(x, y);
    }

    @Override
    public void updateSounds(float delta) {
        // Manually solidify continuous sounds positions
        solidifyContinuousPositions(listenerPosition.x, listenerPosition.y, delta);

        // Iterate through sounds
        for(int i = 0; i < soundInstances.size; i++) {
            SoundInstance soundInstance = soundInstances.get(i);
            ParrotSoundType soundType = soundInstance.getType();
            ParrotSoundCategory category = soundType.getCategory();

            // Variables
            float continuityFactor = soundType.getContinuityFactor();
            float fadeInFactor = 1.0f;
            float distanceFactor = 1.0f;
            float lifeFactor = 1.0f;
            float pan = 0.0f;

            // Check if continuous sound should end
            if(soundInstance.isActive() && soundInstance.getPlaybackMode() == PlaybackMode.CONTINUOUS) {
                float inactivityTime = (System.currentTimeMillis() - soundInstance.lastTouch) / 1000f;
                float continuousTimeout = settings.soundContinuousTimeout * continuityFactor;
                if(inactivityTime > continuousTimeout) {
                    stopSound(soundInstance);
                }
            }

            // If sound has been played but has no internal ID, something terribly wrong happened to it. Kill sound!
            if(!soundInstance.playMe && soundInstance.internalId == (long) -1) {
                killSound(soundInstance);
                i--;
                continue;
            }

            // Calculate fade in factor
            if(soundInstance.getPlaybackMode() == PlaybackMode.CONTINUOUS) {
                float continuousFadein = settings.soundContinuousFadeIn * continuityFactor;
                fadeInFactor = MathUtils.clamp(soundInstance.time / continuousFadein, 0.0f, 1.0f);
            }

            // Kill expired sounds
            if(soundInstance.isExpired()) {
                stopSound(soundInstance);
            }

            // Update sound's time
            soundInstance.time += delta;

            // Check if instance should be killed and removed
            if(soundInstance.isDying) {
                float deadTime = (System.currentTimeMillis() - soundInstance.lastTouch) / 1000f;

                // Check if sound should be killed and removed
                if(deadTime > settings.soundDeathFadeOut) {
                    killSound(soundInstance);
                    i--;
                    continue;
                }

                // Otherwise calculate life factor
                lifeFactor = MathUtils.clamp(1.0f - (deadTime / settings.soundDeathFadeOut), 0.0f, 1.0f);
            }

            if(category != null && category.isSpatial()) {
                // Calculate distance factor
                tmpVec.set(soundInstance.positionX, soundInstance.positionY).sub(listenerPosition.x, listenerPosition.y);
                float dst = tmpVec.len();
                if(dst > settings.distanceRolloffMinDistance) {
                    float dstFactorRaw = MathUtils.clamp((dst - settings.distanceRolloffMinDistance) / settings.distanceRolloffMaxDistance, 0.0f, 1.0f);
                    distanceFactor = settings.distanceRolloffCurve.apply(settings.distanceRolloffMinGain, 1, 1 - dstFactorRaw);
                } else {
                    distanceFactor = 1;
                }

                // Calculate pan
                float dstX = tmpVec.x;
                boolean panFacingLeft = tmpVec.x < 0.0f;
                float panFactorRaw = MathUtils.clamp(Math.abs(dstX / settings.soundPanLimit), 0.0f, 1.0f);
                pan = Interpolation.linear.apply(0.0f, settings.soundPanReduction, panFactorRaw);
                if(panFacingLeft) pan *= -1.0f;
            }

            // Apply new attributes
            if(soundInstance.sound != null) {

                // Calculate volume variation
                if(soundInstance.playMe) {
                    soundInstance.volumeVariation = soundType.getVolumeVariation() * MathUtils.randomTriangular(-1, 1, 0);
                }

                // Calculate volume
                float volumeFactors = fadeInFactor * distanceFactor * lifeFactor * soundInstance.volumeFactor;
                float relativeVolume = ParrotUtils.getPerceivedVolume(soundType.getVolume() + soundInstance.volumeVariation, settings.loudnessExponentialCurve);
                float soundVolume = relativeVolume * volumeFactors;

                // If sound is dying, keep the same pan and don't increase the volume
                if(soundInstance.isDying) {
                    soundVolume = Math.min(soundVolume, soundInstance.currentVolume);
                    pan = soundInstance.currentPan;
                }

                // Apply master volume
                float finalVolume = soundVolume * masterVolume;

                // If sound is waiting to be played, play it
                if(soundInstance.playMe) {
                    soundInstance.playMe = false;
                    boolean normal = soundInstance.playbackMode == PlaybackMode.NORMAL;
                    long internalId;
                    float pitch = soundInstance.pitch;

                    // Apply random pitch variation to sound effect
                    float pitchVariation = soundType.getPitchVariation() * MathUtils.randomTriangular(-1, 1, 0);
                    pitch += pitchVariation;

                    // Apply category-based pitch factors
                    if(category != null) {
                        float pitchCategoryFactor = pitchFactorsByCategory.get(category, 1);
                        pitch *= pitchCategoryFactor;
                    }

                    if(boom == null) {
                        // Play sound normally
                        if(normal) {
                            internalId = soundInstance.sound.play(finalVolume, pitch, pan);
                        } else {
                            internalId = soundInstance.sound.loop(finalVolume, pitch, pan);
                        }
                    } else {
                        // Play sound through boom
                        int boomChannel = soundInstance.boomChannel;
                        if(normal) {
                            internalId = boom.play(soundInstance.sound, boomChannel, finalVolume, pitch, pan);
                        } else {
                            internalId = boom.loop(soundInstance.sound, boomChannel, finalVolume, pitch, pan);
                        }
                    }
                    soundInstance.internalId = internalId;
                }
                // Otherwise just apply changes
                else {
                    soundInstance.sound.setPan(soundInstance.internalId, pan, finalVolume);
                }

                // Store volume and pan to instance
                soundInstance.currentVolume = soundVolume;
                soundInstance.currentPan = pan;
            }
        }
    }

    /**
     * Iterates through all positions set to continuous sounds and solidify them to whichever position is closer to the camera center.
     */
    private void solidifyContinuousPositions(float centerX, float centerY, float delta) {
        if(continuousPositionsById.size > 0) {
            for(Entry<Array<Vector2>> entry : continuousPositionsById) {
                // Get sound
                long id = entry.key;
                SoundInstance soundInstance = soundsById.get(id, null);
                if(soundInstance != null) {

                    // Get positions
                    Array<Vector2> positions = entry.value;
                    int positionAmount = positions.size;
                    if(positionAmount > 0) {
                        // Measuring variables
                        float minDiffX = Float.NaN;
                        float minDiffY = Float.NaN;
                        float avgX = 0;
                        float avgY = 0;

                        // Iterate through registered positions
                        for(int i = 0; i < positionAmount; i++) {
                            Vector2 position = positions.get(i);

                            // Add to average count
                            avgX += position.x;
                            avgY += position.y;

                            // Calculate difference to camera center
                            float diffX = position.x - centerX;
                            float diffY = position.y - centerY;
                            if(Float.isNaN(minDiffX) || Math.abs(diffX) < Math.abs(minDiffX)) minDiffX = diffX;
                            if(Float.isNaN(minDiffY) || Math.abs(diffY) < Math.abs(minDiffY)) minDiffY = diffY;
                        }

                        // Fix average
                        avgX /= positionAmount;
                        avgY /= positionAmount;

                        // Check if average is closer to the camera than any other single position
                        float avgDiffX = avgX - centerX;
                        float avgDiffY = avgY - centerY;
                        if(Math.abs(avgDiffX) < Math.abs(minDiffX)) minDiffX = avgDiffX;
                        if(Math.abs(avgDiffY) < Math.abs(minDiffY)) minDiffY = avgDiffY;

                        // Calculate new position
                        float oldX = soundInstance.positionX;
                        float oldY = soundInstance.positionY;
                        float newX = centerX + minDiffX;
                        float newY = centerY + minDiffY;

                        // Limit sound speed
                        tmpVec.set(newX, newY).sub(oldX, oldY).limit(settings.soundContinuousSpeed * delta);
                        newX = oldX + tmpVec.x;
                        newY = oldY + tmpVec.y;

                        // Apply final position to sound
                        float finalX = (oldX + newX) / 2f;
                        float finalY = (oldY + newY) / 2f;
                        soundInstance.positionX = finalX;
                        soundInstance.positionY = finalY;
                    }

                    // Clear positions array
                    positions.clear();
                }
            }
        }
    }

    @Override
    public long playSound(ParrotSoundType type, int soundIndex, float x, float y, float volumeFactor, float pitch, PlaybackMode mode, int boomChannel) {
        // Make sure PlaybackMode is valid
        if(mode == null) mode = type.getPlaybackMode();
        if(mode == null) mode = PlaybackMode.NORMAL;

        // Sanitize sound index
        soundIndex = Math.min(soundIndex, type.getSounds().size - 1);

        // If sound is continuous, see if there's an active one
        if(mode == PlaybackMode.CONTINUOUS) {
            for(SoundInstance soundInstance : soundInstances) {
                if(soundInstance.getType() == type) {
                    if(soundInstance.playbackMode == mode) {
                        if(soundInstance.isActive()) {
                            soundInstance.lastTouch = System.currentTimeMillis();
                            // Sound is active, return shared ID
                            return soundInstance.id;
                        }
                    }
                }
            }
        }

        // Ensure we have a sound to play
        Array<Sound> sounds = type.getSounds();
        if(sounds == null || sounds.isEmpty()) {
            return -1;
        }

        // Get random sound
        final Sound sound;
        if(soundIndex >= 0) {
            sound = sounds.get(soundIndex);
        } else if(type.getNonRandomShuffle() != null) {
            sound = type.getNonRandomShuffle().get();
        } else {
            sound = type.getSounds().random();
        }

        // Ensure sound is valid
        if(sound == null) {
            return -1;
        }

        // Get sound duration
        float soundDuration = ParrotUtils.getSoundDuration(sound, settings.soundDurationOnUnsupportedPlatforms);

        // Ignore distant tiny sounds
        boolean isSpatial = type.getCategory().isSpatial();
        if(ignoreDistantTinySound(x, y, soundDuration, isSpatial, mode)) {
            return -1;
        }

        // Calculate pitch
        pitch = pitch * type.getPitch();

        // Create SoundInstance
        long id = getNextId();
        SoundInstance soundInstance = Pools.obtain(SoundInstance.class);
        soundInstance.sound = sound;
        soundInstance.type = type;
        soundInstance.id = id;
        soundInstance.duration = soundDuration;
        soundInstance.positionX = x;
        soundInstance.positionY = y;
        soundInstance.volumeFactor = volumeFactor;
        soundInstance.pitch = pitch;
        soundInstance.playbackMode = mode;
        soundInstance.lastTouch = System.currentTimeMillis();
        soundInstance.priority = type.getPriority();
        soundInstance.boomChannel = boomChannel;

        // Register SoundInstance
        registerSound(soundInstance);

        // Return external ID
        return id;
    }

    /**
     * Checks if the given sound should be ignored according to the "Ignore Distant Tiny Sounds" feature.
     *
     * @return true if the sound should be ignored.
     */
    private boolean ignoreDistantTinySound(float soundX, float soundY, float soundDuration, boolean isSpatial, PlaybackMode mode) {
        // Ensure the "Ignore Distant Tiny Sounds" feature is enabled
        if(!settings.ignoreDistantTinySounds) return false;

        // Ignore non-spatial sounds
        if(!isSpatial) return false;

        // Check mode
        boolean modeMatchesCriteria = settings.ignoreDistantTinySoundsOfAllModes || mode == PlaybackMode.NORMAL;
        if(!modeMatchesCriteria) return false;

        // Check sound duration
        boolean isTinySound = soundDuration <= settings.ignoreDistantTinySoundsDuration;
        if(!isTinySound) return false;

        // Check distance
        float distanceThreshold = settings.ignoreDistantTinySoundsDistance;
        if(distanceThreshold < 0) {
            distanceThreshold = settings.distanceRolloffMaxDistance;
        }
        float dst2 = listenerPosition.dst2(soundX, soundY);
        boolean isDistant = dst2 >= (distanceThreshold * distanceThreshold);
        if(!isDistant) return false;

        // Sound is tiny, is distant, and should be ignored
        return true;
    }

    @Override
    public SoundInstance getSound(long internalId) {
        if(internalId == (long) -1) return null;
        return soundsById.get(internalId, null);
    }

    @Override
    public Array<SoundInstance> getAllSounds() {
        return soundInstances;
    }

    @Override
    public boolean touchSound(SoundInstance soundInstance) {
        if(soundInstance == null) return false;

        // Touch instance
        if(soundInstance.isActive()) soundInstance.lastTouch = System.currentTimeMillis();

        // Return flag indicating if instance is valid
        return soundInstance.isValid();
    }

    @Override
    public void setSoundCoordinates(SoundInstance soundInstance, float x, float y) {
        if(soundInstance != null) {
            // Ignore dying sounds
            if(soundInstance.isDying) return;

            // Check if sound is continuous, and if so add an accumulated position
            if(soundInstance.playbackMode == PlaybackMode.CONTINUOUS) {
                Array<Vector2> coordsArray = continuousPositionsById.get(soundInstance.id, null);
                if(coordsArray == null) {
                    coordsArray = new Array<>();
                    continuousPositionsById.put(soundInstance.id, coordsArray);
                }
                Vector2 coords = new Vector2();
                coords.set(x, y);
                coordsArray.add(coords);
            }

            // Otherwise set position directly
            else {
                soundInstance.positionX = x;
                soundInstance.positionY = y;
            }
        }
    }

    @Override
    public void stopSound(ParrotSoundType type, boolean ignorePersistent) {
        if(type == null) return;
        for(int i = 0; i < soundInstances.size; i++) {
            SoundInstance soundInstance = soundInstances.get(i);
            if(ignorePersistent && soundInstance.isPersistent()) continue;
            if(soundInstance.getType() == type) {
                stopSound(soundInstance);
            }
        }
    }

    @Override
    public void stopSound(ParrotSoundCategory category, boolean ignorePersistent) {
        if(category == null) return;
        for(int i = 0; i < soundInstances.size; i++) {
            SoundInstance soundInstance = soundInstances.get(i);
            if(ignorePersistent && soundInstance.isPersistent()) continue;
            if(soundInstance.getType().getCategory() == category) {
                stopSound(soundInstance);
            }
        }
    }

    @Override
    public void stopAllSounds(boolean ignorePersistent) {
        for(int i = 0; i < soundInstances.size; i++) {
            SoundInstance soundInstance = soundInstances.get(i);
            if(ignorePersistent && soundInstance.isPersistent()) continue;
            stopSound(soundInstance);
        }
    }

    @Override
    public void stopSound(SoundInstance soundInstance) {
        if(soundInstance == null || soundInstance.isDying) return;
        soundInstance.lastTouch = System.currentTimeMillis();
        soundInstance.isDying = true;
        soundInstance.playMe = false;
    }

    @Override
    public float getSoundCategoryPitchFactor(ParrotSoundCategory category) {
        return pitchFactorsByCategory.get(category, 1);
    }

    @Override
    public void setSoundCategoryPitchFactor(ParrotSoundCategory category, float pitchFactor) {
        // Update factor
        float oldPitchFactor = pitchFactorsByCategory.get(category, 1);
        pitchFactorsByCategory.put(category, pitchFactor);
        float pitchDifference = pitchFactor - oldPitchFactor;

        // Apply difference to ongoing sounds
        if(!MathUtils.isZero(pitchDifference)) {
            for(int i = 0; i < soundInstances.size; i++) {
                SoundInstance soundInstance = soundInstances.get(i);
                if(soundInstance.getType().getCategory() == category) {
                    soundInstance.pitch += pitchDifference;
                    Sound sound = soundInstance.sound;
                    if(sound != null) {
                        sound.setPitch(soundInstance.internalId, soundInstance.pitch);
                    }
                }
            }
        }
    }

    @Override

    public void killSound(ParrotSoundType type) {
        if(type == null) return;
        for(int i = 0; i < soundInstances.size; i++) {
            SoundInstance soundInstance = soundInstances.get(i);
            if(soundInstance.getType() == type) {
                killSound(soundInstance);
                i--;
            }
        }
    }

    @Override
    public void killSound(ParrotSoundCategory category) {
        if(category == null) return;
        for(int i = 0; i < soundInstances.size; i++) {
            SoundInstance soundInstance = soundInstances.get(i);
            if(soundInstance.getType().getCategory() == category) {
                killSound(soundInstance);
                i--;
            }
        }
    }

    @Override
    public void killAllSounds() {
        for(int i = 0; i < soundInstances.size; i++) {
            SoundInstance soundInstance = soundInstances.get(i);
            killSound(soundInstance);
        }
        soundInstances.clear();
    }

    @Override
    public void killSound(SoundInstance soundInstance) {
        if(soundInstance == null) return;

        // Stop sound
        if(soundInstance.sound != null) soundInstance.sound.stop(soundInstance.internalId);

        // Unregister sound
        unregisterSound(soundInstance);
    }

    @Override
    public void setBoom(Boom boom) {
        this.boom = boom;
    }

    /**
     * Registers the given sound to all collections of this instance.
     */
    private void registerSound(SoundInstance soundInstance) {
        // Add sound to collections
        soundInstances.add(soundInstance);
        soundsById.put(soundInstance.id, soundInstance);

        // Limit voices
        limitVoices(soundInstance.getType());
    }

    /**
     * Unregisters the given sound from all collections of this instance.
     */
    private void unregisterSound(SoundInstance soundInstance) {
        // Remove from collections
        soundInstances.removeValue(soundInstance, true);
        soundsById.remove(soundInstance.id);

        // Remove continuous positions
        if(soundInstance.playbackMode == PlaybackMode.CONTINUOUS) {
            continuousPositionsById.remove(soundInstance.id);
        }

        // Free instances
        Pools.free(soundInstance);
    }

    /**
     * Limits the amount of voices of the given {@link ParrotSoundType} playing at once. If there are more voices than the allowed amount,
     * the oldest ones are killed.
     */
    private void limitVoices(ParrotSoundType type) {
        ParrotSoundCategory category = type.getCategory();

        // Get voice limits
        int availableVoicesForType = type.getVoices();
        int availableVoicesForCategory = category == null ? Integer.MAX_VALUE : category.getVoices();

        // Make sure we really have to limit voices at all
        int totalSoundsPlaying = soundInstances.size;
        if(totalSoundsPlaying <= availableVoicesForType && totalSoundsPlaying <= availableVoicesForCategory) {
            return;
        }

        // Sort sounds before limitting
        soundInstances.sort(priorityComparator);

        // Iterate through valid sound instances
        for(int i = soundInstances.size - 1; i >= 0; i--) {
            SoundInstance soundInstance = soundInstances.get(i);
            if(soundInstance.isActive() && !soundInstance.isExpired()) {

                // Stop sounds based on category
                if(soundInstance.getType().getCategory() == category) {
                    if(availableVoicesForCategory-- <= 0) {
                        stopSound(soundInstance);
                    }

                    // Stop sounds based on type
                    else if(soundInstance.getType() == type) {
                        if(availableVoicesForType-- <= 0) {
                            stopSound(soundInstance);
                        }
                    }
                }
            }
        }
    }

    /** Returns the next available sound ID to be used. */
    private long getNextId() {
        while(true) {
            nextId++;
            if(nextId == (long) -1)
                continue;
            if(!soundsById.containsKey(nextId))
                break;
        }
        return nextId;
    }

    @Override
    public void dispose() {
        Pools.freeAll(soundInstances);
        soundInstances.clear();
        soundsById.clear();
    }

}
