package com.rafaskoberg.gdx.parrot.music;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import com.rafaskoberg.boom.Boom;
import com.rafaskoberg.gdx.parrot.Parrot;
import com.rafaskoberg.gdx.parrot.ParrotSettings;
import com.rafaskoberg.gdx.parrot.music.MusicInstance.State;
import com.rafaskoberg.gdx.parrot.util.ParrotUtils;

public class MusicPlayerImpl implements MusicPlayer {
    private static final float MIN_VOLUME = 0.000001f;

    // Collections
    private final Array<MusicInstance> musicInstances;

    // Members
    private final Parrot                parrot;
    private final ParrotSettings        settings;
    private final LoudnessInterpolation interpolation;
    private       float                 masterVolume;

    public MusicPlayerImpl(Parrot parrot) {
        // Collections
        this.musicInstances = new Array<>();

        // Members
        this.parrot = parrot;
        this.settings = parrot.getSettings();
        this.interpolation = new LoudnessInterpolation();
        this.masterVolume = 1;
    }

    @Override
    public void setMusicVolume(float volume) {
        // Update master volume
        float perceivedVolume = (float) Math.pow(volume, settings.loudnessExponentialCurve);
        this.masterVolume = MathUtils.clamp(perceivedVolume, 0.0f, 1.0f);

        // Calculate volume and factor differences in decibels
        float volumeDiffDb = ParrotUtils.volumeToDb(this.masterVolume - volume);

        // Apply new volume values to music player
        this.masterVolume = volume;

        // Change volumes
        for(MusicInstance musicInstance : musicInstances) {
            musicInstance.targetVolume = ParrotUtils.volumeToDb(musicInstance.targetVolume) + volumeDiffDb;
        }
    }

    @Override
    public void updateMusic(float delta) {
        Boom boom = parrot.getBoom();

        // Iterate through all music
        for(int i = musicInstances.size - 1; i >= 0; i--) {
            MusicInstance musicInstance = musicInstances.get(i);
            Music music = musicInstance.music;

            // Store old state
            State oldState = musicInstance.state;

            // Update state timer
            musicInstance.stateTimer += delta;

            // If music is null, dispose instance
            if(music == null) {
                musicInstance.state = State.DISPOSING;
            }

            // Update states
            switch(musicInstance.state) {

                case SILENT: {
                    // Play music
                    music.setPosition(0);
                    music.setVolume(MIN_VOLUME);
                    music.setLooping(musicInstance.isLooping);
                    if(boom == null) {
                        music.play();
                    } else {
                        boom.play(music, musicInstance.boomChannel);
                    }
                    musicInstance.state = musicInstance.shouldFadeIn ? State.FADING_IN : State.PLAYING;
                    break;
                }

                case FADING_IN: {
                    // Process fade-in
                    float progress = MathUtils.clamp(musicInstance.stateTimer / Math.max(settings.musicFadeInDuration, 0.00001f), 0, 1);
                    float perceivedProgress = interpolation.applyIn(progress);
                    float volume = MathUtils.lerp(0, musicInstance.targetVolume, perceivedProgress);
                    music.setVolume(Math.max(volume, MIN_VOLUME));
                    if(musicInstance.stateTimer > settings.musicFadeInDuration) {
                        musicInstance.state = State.PLAYING;
                    }
                    break;
                }

                case PLAYING: {
                    // Adjust volume
                    music.setVolume(Math.max(musicInstance.targetVolume, MIN_VOLUME));

                    // Dispose instances that are no longer playing
                    if(!music.isPlaying()) {
                        musicInstance.state = State.DISPOSING;
                    }
                    break;
                }

                case FADING_OUT: {
                    // Process fade-out
                    float progress = MathUtils.clamp(musicInstance.stateTimer / Math.max(settings.musicFadeOutDuration, 0.00001f), 0, 1);
                    float perceivedProgress = interpolation.applyOut(progress);
                    float volume = MathUtils.lerp(musicInstance.targetVolume, 0, perceivedProgress);
                    music.setVolume(Math.max(volume, MIN_VOLUME));
                    if(musicInstance.stateTimer > settings.musicFadeOutDuration) {
                        music.stop();
                        music.setPosition(0);
                        musicInstance.state = State.DISPOSING;
                    }
                    break;
                }
            }

            // Reset state timer
            if(oldState != musicInstance.state) {
                musicInstance.stateTimer = 0;
            }

            // Dispose instances
            if(musicInstance.state == State.DISPOSING) {
                musicInstances.removeIndex(i);
                Pools.free(musicInstance);
            }
        }
    }

    @Override
    public Music playMusic(ParrotMusicType musicType, boolean loop, boolean fadeIn, int channel, int boomChannel) {
        // Stop channel
        stopMusicChannel(channel, true);

        // Make sure music is valid
        Music music = musicType.getMusic();
        if(music != null) {

            // Stop music if it's already playing
            if(music.isPlaying()) {
                music.stop();
                music.setPosition(0);
            }

            // Reuse an existing MusicInstance of the same type
            MusicInstance musicInstance = null;
            for(int i = musicInstances.size - 1; i >= 0; i--) {
                MusicInstance instance = musicInstances.get(i);
                if(instance.musicType == musicType) {
                    musicInstance = instance;
                    break;
                }
            }

            // Create new MusicInstance if necessary
            if(musicInstance == null) {
                musicInstance = Pools.obtain(MusicInstance.class);
                musicInstances.add(musicInstance);
            }

            // Configure MusicInstance
            musicInstance.state = State.SILENT;
            musicInstance.music = music;
            musicInstance.musicType = musicType;
            musicInstance.isLooping = loop;
            musicInstance.shouldFadeIn = fadeIn;
            musicInstance.targetVolume = masterVolume * musicType.getRelativeVolume();// * channelVolume;
            musicInstance.channel = channel;
            musicInstance.boomChannel = boomChannel;

            // Configure Music
            music.setPosition(0);
            music.setVolume(MIN_VOLUME);

            return music;
        }

        return null;
    }

    @Override
    public void stopMusic(ParrotMusicType musicType, boolean gracefully) {
        for(MusicInstance musicInstance : musicInstances) {
            if(musicInstance.musicType == musicType) {
                stopMusicInstance(musicInstance, gracefully);
            }
        }
    }

    @Override
    public void stopMusicChannel(int channel, boolean gracefully) {
        for(MusicInstance musicInstance : musicInstances) {
            if(musicInstance.channel == channel) {
                stopMusicInstance(musicInstance, gracefully);
            }
        }
    }

    @Override
    public void stopAllMusic(boolean gracefully) {
        for(MusicInstance musicInstance : musicInstances) {
            stopMusicInstance(musicInstance, gracefully);
        }
    }

    /**
     * Stops a {@link MusicInstance} in case it's playing.
     *
     * @param gracefully Whether or not the action should be progressive.
     */
    private void stopMusicInstance(MusicInstance musicInstance, boolean gracefully) {
        if(musicInstance.state.isActive()) {
            musicInstance.state = State.FADING_OUT;
            musicInstance.stateTimer = 0;
            musicInstance.targetVolume = musicInstance.music.getVolume();
            if(!gracefully) {
                musicInstance.stateTimer = Float.MAX_VALUE;
            }
        }
    }

    @Override
    public boolean isMusicPlaying() {
        if(!isPowered()) {
            for(MusicInstance musicInstance : musicInstances) {
                if(musicInstance != null && musicInstance.state.isActive()) {
                    Music music = musicInstance.music;
                    if(music != null && music.isPlaying()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean isMusicPlaying(int channel) {
        if(isPowered()) {
            for(MusicInstance musicInstance : musicInstances) {
                if(musicInstance != null && musicInstance.channel == channel && musicInstance.state.isActive()) {
                    Music music = musicInstance.music;
                    return music != null && music.isPlaying();
                }
            }
        }
        return false;
    }

    /**
     * Returns whether or not this player is powered or not.
     */
    private boolean isPowered() {
        return this.masterVolume > 0.000001f; // -120db
    }

    @Override
    public void dispose() {
        for(MusicInstance musicInstance : musicInstances) {
            Music music = musicInstance.music;
            if(music != null && music.isPlaying()) {
                music.stop();
            }
        }
        Pools.freeAll(musicInstances);
        musicInstances.clear();
    }

    class LoudnessInterpolation {
        float applyIn(float a) {
            int power = settings.loudnessExponentialCurve;
            return (float) Math.pow(a, power);
        }

        float applyOut(float a) {
            int power = settings.loudnessExponentialCurve;
            return (float) Math.pow(a - 1, power) * (power % 2 == 0 ? -1 : 1) + 1;
        }
    }

}
