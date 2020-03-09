package com.rafaskoberg.gdx.parrot.sfx;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.LongMap;
import com.badlogic.gdx.utils.LongMap.Entry;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pools;
import com.rafaskoberg.boom.Boom;
import com.rafaskoberg.gdx.parrot.util.ParrotUtils;

/**
 * Advanced spatial sound player that handles all sounds being played in the game.
 */
public class SoundPlayerImpl implements SoundPlayer {
    // Collections
    private final Array<SoundInstance>                           soundInstances;
    private final ObjectMap<SoundType, Array<Sound>>             soundsByType; // TODO This should live inside SoundType
    private final LongMap<SoundInstance>                         soundsById;
    private final ObjectMap<SoundType, Array<SoundInstance>>     soundInstancessByType;
    private final ObjectMap<SoundCategory, Array<SoundInstance>> soundsByCategory;
    private final LongMap<Array<Vector2>>                        continuousPositionsById;

    // Members
    private final SoundSettings settings     = new SoundSettings();
    private final Vector2       tmpVec       = new Vector2();
    private       long          nextId       = 1;
    private       float         masterVolume = 1.0f;
    private       Boom          boom;

    public SoundPlayerImpl() {
        this.soundInstances = new Array<>();
        this.soundsByType = new ObjectMap<>();
        this.soundsById = new LongMap<>();
        this.soundInstancessByType = new ObjectMap<>();
        this.soundsByCategory = new ObjectMap<>();
        this.continuousPositionsById = new LongMap<>();
    }

    @Override
    public void setSoundVolume(float volume) {
        float perceivedVolume = (float) Math.pow(volume, settings.loudnessExponentialCurve);
        this.masterVolume = MathUtils.clamp(perceivedVolume, 0.0f, 1.0f);
    }

    @Override
    public SoundSettings getSoundSettings() {
        return settings;
    }

    @Override
    public void updateSounds(Camera camera, float delta) {
        // Get camera position
        float centerX = camera.position.x;
        float centerY = camera.position.y;

        // Manually solidify continuous sounds positions
        solidifyContinuousPositions(centerX, centerY, delta);

        // Iterate through sounds
        for(int i = 0; i < soundInstances.size; i++) {
            SoundInstance soundInstance = soundInstances.get(i);
            SoundType soundType = soundInstance.getType();
            SoundCategory category = soundType.getCategory();

            // Variables
            float continuityFactor = soundType.getContinuityFactor();
            float fadeInFactor = 1.0f;
            float distanceFactor = 1.0f;
            float lifeFactor = 1.0f;
            float pan = 0.0f;

            // Check if continuous sound should end
            if(soundInstance.isActive() && soundInstance.getPlaybackMode() == PlaybackMode.CONTINUOUS) {
                float inactivityTime = (System.currentTimeMillis() - soundInstance.lastTouch) / 1000f;
                float continuousTimeout = settings.continuousTimeout * continuityFactor;
                if(inactivityTime > continuousTimeout) {
                    stopSound(soundInstance);
                }
            }

            // If sound has been played but has no internal ID, something terribly wrong happened to it. Kill sound!
            if(!soundInstance.playMe && soundInstance.internalId == (long) -1) {
                killSound(soundInstance);
                soundInstances.removeIndex(i);
                i--;
                continue;
            }

            // Calculate fade in factor
            if(soundInstance.getPlaybackMode() == PlaybackMode.CONTINUOUS) {
                float continuousFadein = settings.continuousFadeIn * continuityFactor;
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
                if(deadTime > settings.deathFadeOut) {
                    killSound(soundInstance);
                    soundInstances.removeIndex(i);
                    i--;
                    continue;
                }

                // Otherwise calculate life factor
                lifeFactor = MathUtils.clamp(1.0f - (deadTime / settings.deathFadeOut), 0.0f, 1.0f);
            }

            if(category.isSpatial()) {
                // Calculate distance factor
                tmpVec.set(soundInstance.positionX, soundInstance.positionY).sub(centerX, centerY);
                float dst = tmpVec.len();
                float dstFactorRaw = MathUtils.clamp(dst / settings.distanceLimit, 0.0f, 1.0f);
                distanceFactor = Interpolation.linear.apply(1.0f, 1.0f - ParrotUtils.dbToVolume(settings.distanceReduction), dstFactorRaw);

                // Calculate pan
                float dstX = tmpVec.x;
                boolean panFacingLeft = tmpVec.x < 0.0f;
                float panFactorRaw = MathUtils.clamp(Math.abs(dstX / settings.panLimit), 0.0f, 1.0f);
                pan = Interpolation.linear.apply(0.0f, settings.panReduction, panFactorRaw);
                if(panFacingLeft) pan *= -1.0f;
            }

            // Apply new attributes
            if(soundInstance.sound != null) {
                // Calculate volume
                float volumeFactors = fadeInFactor * distanceFactor * lifeFactor;
                float soundVolume = soundInstance.getType().getVolume() * volumeFactors;

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

                    // TODO Apply category-based pitch factors

                    // Apply random pitch variation to sound effect
                    float pitchVariation = soundType.getPitchVariation() * MathUtils.randomTriangular(-1, 1, 0);
                    pitch += pitchVariation;

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
     * Iterates through all positions set to continuous sounds and solidify them to whichever position is closer to the
     * camera center.
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

                            // Free Vector2 instance
                            Pools.free(position);
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
                        tmpVec.set(newX, newY).sub(oldX, oldY).limit(settings.continuousSpeed * delta);
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
    public long playSound(SoundType type, float x, float y, float pitch, PlaybackMode mode, int boomChannel) {
        // Make sure PlaybackMode is valid
        if(mode == null) mode = type.getPlaybackMode();
        if(mode == null) mode = PlaybackMode.NORMAL;

        // If sound is continuous, see if there's an active one
        if(mode == PlaybackMode.CONTINUOUS) {
            Array<SoundInstance> typeSounds = soundInstancessByType.get(type, null);
            if(typeSounds != null) {
                for(int i = typeSounds.size - 1; i >= 0; i--) {
                    SoundInstance soundInstance = typeSounds.get(i);
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

        // Get random sound
        Array<Sound> array = soundsByType.get(type, null);
        if(array != null) {
            Sound sound = array.random();
            if(sound != null) {

                // Calculate pitch
                pitch = pitch * type.getPitch();

                // Create SoundInstance
                long id = getNextId();
                SoundInstance soundInstance = Pools.obtain(SoundInstance.class);
                soundInstance.sound = sound;
                soundInstance.type = type;
                soundInstance.id = id;
                soundInstance.duration = ParrotUtils.getSoundDuration(sound, settings.unsupportedPlatformSoundDuration);
                soundInstance.positionX = x;
                soundInstance.positionY = y;
                soundInstance.pitch = pitch;
                soundInstance.playbackMode = mode;
                soundInstance.lastTouch = System.currentTimeMillis();
                if(boomChannel != -1) {
                    soundInstance.boomChannel = boomChannel;
                }

                // Register SoundInstance
                registerSound(soundInstance);

                // Return external ID
                return id;
            }
        }
        return -1;
    }

    @Override
    public SoundInstance getSound(long internalId) {
        if(internalId == (long) -1) return null;
        return soundsById.get(internalId, null);
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
                Vector2 coords = Pools.obtain(Vector2.class);
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
    public void stopSound(SoundType type, boolean ignorePersistent) {
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
    public void stopSound(SoundCategory category, boolean ignorePersistent) {
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

    public void killSound(SoundType type) {
        if(type == null) return;
        for(int i = 0; i < soundInstances.size; i++) {
            SoundInstance soundInstance = soundInstances.get(i);
            if(soundInstance.getType() == type) {
                killSound(soundInstance);
                soundInstances.removeIndex(i);
                i--;
            }
        }
    }

    @Override
    public void killSound(SoundCategory category) {
        if(category == null) return;
        for(int i = 0; i < soundInstances.size; i++) {
            SoundInstance soundInstance = soundInstances.get(i);
            if(soundInstance.getType().getCategory() == category) {
                killSound(soundInstance);
                soundInstances.removeIndex(i);
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

        // Nullify instance
        soundInstance.sound = null;
        soundInstance.id = -1;
        soundInstance.internalId = -1;
        soundInstance.playMe = false;
    }

    /**
     * Registers the given sound to all collections of this instance.
     */
    private void registerSound(SoundInstance soundInstance) {
        soundInstances.add(soundInstance);

        soundsById.put(soundInstance.id, soundInstance);

        SoundType type = soundInstance.getType();
        Array<SoundInstance> typeSounds = soundInstancessByType.get(type, null);
        if(typeSounds == null) {
            typeSounds = new Array<>();
            soundInstancessByType.put(type, typeSounds);

        }
        typeSounds.add(soundInstance);

        SoundCategory category = type.getCategory();
        Array<SoundInstance> categorySounds = soundsByCategory.get(category, null);
        if(categorySounds == null) {
            categorySounds = new Array<>();
            soundsByCategory.put(category, categorySounds);

        }
        categorySounds.add(soundInstance);

        // Check voice limit
        if(typeSounds.size > type.getVoices())
            limitVoices(type);
        if(categorySounds.size > category.getVoices())
            limitVoices(category);
    }

    /**
     * Unregisters the given sound from all collections of this instance.
     */
    private void unregisterSound(SoundInstance soundInstance) {
        // Remove from direct collections
        soundInstances.removeValue(soundInstance, true);
        soundsById.remove(soundInstance.id);

        // Remove from SoundType collection
        Array<SoundInstance> typeSounds = soundInstancessByType.get(soundInstance.getType(), null);
        if(typeSounds != null) {
            typeSounds.removeValue(soundInstance, true);
        }

        // Remove from SoundCategory collection
        Array<SoundInstance> categorySounds = soundsByCategory.get(soundInstance.getType().getCategory(), null);
        if(categorySounds != null) {
            categorySounds.removeValue(soundInstance, true);
        }

        // Remove continuous positions
        if(soundInstance.playbackMode == PlaybackMode.CONTINUOUS) {
            continuousPositionsById.remove(soundInstance.id);
        }

        // Free instances
        Pools.free(soundInstance);
    }

    /**
     * Limits the amount of voices of the given {@link SoundType} playing at once. If there are more voices than the
     * allowed amount, the oldest ones are killed.
     */
    private void limitVoices(SoundType type) {
        int voicesAvailable = type.getVoices();

        Array<SoundInstance> typeSounds = soundInstancessByType.get(type, null);
        if(typeSounds.size <= voicesAvailable) return;

        // Iterate through valid sounds
        for(int i = typeSounds.size - 1; i >= 0; i--) {
            SoundInstance soundInstance = typeSounds.get(i);
            if(soundInstance.isActive() && !soundInstance.isExpired()) {

                // If there are no voices available, stop sound
                if(voicesAvailable <= 0) {
                    stopSound(soundInstance);
                }

                // Count voice
                voicesAvailable--;
            }
        }
    }

    /**
     * Limits the amount of voices of the given {@link SoundCategory} playing at once. If there are more voices than the
     * allowed amount, the oldest ones are killed.
     */
    private void limitVoices(SoundCategory category) {
        int voicesAvailable = category.getVoices();

        Array<SoundInstance> categorySounds = soundsByCategory.get(category, null);
        if(categorySounds.size <= voicesAvailable) return;

        // Iterate through valid sounds
        for(int i = categorySounds.size - 1; i >= 0; i--) {
            SoundInstance soundInstance = categorySounds.get(i);
            if(soundInstance.isActive() && !soundInstance.isExpired()) {

                // If there are no voices available, stop sound
                if(voicesAvailable <= 0) {
                    stopSound(soundInstance);
                }

                // Count voice
                voicesAvailable--;
            }
        }
    }

    /**
     * Returns the {@link Boom} instance associated with this player, if any.
     */
    public Boom getBoom() {
        return boom;
    }

    /**
     * Sets the {@link Boom} instance associated with this player.
     */
    public void setBoom(Boom boom) {
        this.boom = boom;
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
        soundInstances.clear();
        soundsById.clear();
        soundInstancessByType.clear();
        soundsByCategory.clear();
        soundsByType.clear();
    }

}
