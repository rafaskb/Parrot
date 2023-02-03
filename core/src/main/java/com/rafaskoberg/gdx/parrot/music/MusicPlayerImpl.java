package com.rafaskoberg.gdx.parrot.music;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntFloatMap;
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
    private final IntFloatMap volumesByChannel;
    private final IntFloatMap rawVolumesByChannel;

    // Members
    private final Parrot parrot;
    private final ParrotSettings settings;
    private final LoudnessInterpolation interpolation;
    protected Boom boom;
    private float rawVolume;
    private float masterVolume;

    public MusicPlayerImpl(Parrot parrot) {
        // Collections
        this.musicInstances = new Array<>();
        this.volumesByChannel = new IntFloatMap();
        this.rawVolumesByChannel = new IntFloatMap();

        // Members
        this.parrot = parrot;
        this.settings = parrot.getSettings();
        this.interpolation = new LoudnessInterpolation();
        this.rawVolume = 1;
        this.masterVolume = 1;
    }

    @Override
    public void setBoom(Boom boom) {
        this.boom = boom;
    }

    @Override
    public float getMusicPlayerVolume() {
        return rawVolume;
    }

    @Override
    public void setMusicPlayerVolume(float volume) {
        // Set raw volume
        this.rawVolume = MathUtils.clamp(volume, 0, 1);

        // Calculate perceived volume
        float perceivedVolume = ParrotUtils.getPerceivedVolume(rawVolume, settings.loudnessExponentialCurve);

        // Calculate volume and factor differences in decibels
        float volumeDiffDb = ParrotUtils.volumeToDb(this.masterVolume) - ParrotUtils.volumeToDb(perceivedVolume);

        // Update master volume
        this.masterVolume = perceivedVolume;

        // Change volumes
        for(MusicInstance musicInstance : musicInstances) {
            musicInstance.targetVolume = ParrotUtils.dbToVolume(ParrotUtils.volumeToDb(musicInstance.targetVolume) - volumeDiffDb);
        }
    }

    @Override
    public float getMusicChannelVolume(int channel) {
        return rawVolumesByChannel.get(channel, 1);
    }

    @Override
    public void setMusicChannelVolume(int channel, float volume) {
        // Calculate raw volume
        float rawVolume = MathUtils.clamp(volume, 0, 1);
        rawVolumesByChannel.put(channel, rawVolume);

        // Calculate perceived volume
        float perceivedVolume = ParrotUtils.getPerceivedVolume(rawVolume, settings.loudnessExponentialCurve);

        // Calculate volume and factor differences in decibels
        float oldVolume = volumesByChannel.get(channel, 1);
        float volumeDiffDb = ParrotUtils.volumeToDb(oldVolume) - ParrotUtils.volumeToDb(perceivedVolume);

        // Update channel volume
        volumesByChannel.put(channel, perceivedVolume);

        // Change volumes
        for(MusicInstance musicInstance : musicInstances) {
            if(musicInstance.channel == channel) {
                musicInstance.targetVolume = ParrotUtils.dbToVolume(ParrotUtils.volumeToDb(musicInstance.targetVolume) - volumeDiffDb);
            }
        }
    }

    @Override
    public void updateMusic(float delta) {
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
                    if(boom == null) {
                        music.play();
                    } else {
                        boom.play(music, musicInstance.boomChannel);
                    }
                    music.setPosition(musicInstance.initialPosition);
                    music.setVolume(MIN_VOLUME);
                    music.setLooping(musicInstance.isLooping);
                    boolean shouldFadeIn = musicInstance.nextState == State.FADING_IN;
                    musicInstance.state = shouldFadeIn ? State.FADING_IN : State.PLAYING;
                    musicInstance.nextState = shouldFadeIn ? State.PLAYING : null;
                    break;
                }

                case FADING_IN: {
                    // Play music if necessary
                    if(!music.isPlaying()) {
                        music.play();
                    }

                    // Process fade-in
                    float progress = MathUtils.clamp(musicInstance.stateTimer / Math.max(settings.musicFadeInDuration, 0.00001f), 0, 1);
                    float perceivedProgress = interpolation.applyIn(progress);
                    float volume = MathUtils.lerp(0, musicInstance.targetVolume, perceivedProgress);
                    music.setVolume(MathUtils.clamp(volume, MIN_VOLUME, 1));
                    if(musicInstance.stateTimer > settings.musicFadeInDuration) {
                        musicInstance.state = State.PLAYING;
                        musicInstance.nextState = null;
                    }
                    break;
                }

                case PLAYING: {
                    if(music.isPlaying()) {
                        // Adjust volume
                        music.setVolume(MathUtils.clamp(musicInstance.targetVolume, MIN_VOLUME, 1));
                    } else {
                        // Dispose instances that are no longer playing
                        musicInstance.state = State.DISPOSING;
                    }
                    break;
                }

                case FADING_OUT: {
                    // Process fade-out
                    float progress = MathUtils.clamp(musicInstance.stateTimer / Math.max(settings.musicFadeOutDuration, 0.00001f), 0, 1);
                    float perceivedProgress = interpolation.applyOut(progress);
                    float volume = MathUtils.lerp(musicInstance.targetVolume, MIN_VOLUME, perceivedProgress);
                    if(music.isPlaying()) {
                        music.setVolume(MathUtils.clamp(volume, MIN_VOLUME, 1));
                    }
                    if(musicInstance.stateTimer > settings.musicFadeOutDuration) {
                        music.pause();
                        if(musicInstance.nextState == State.PAUSED) {
                            musicInstance.state = State.PAUSED;
                        } else {
                            music.setPosition(0);
                            musicInstance.state = State.DISPOSING;
                        }
                        musicInstance.nextState = null;
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
                musicInstance.music.stop();
                Pools.free(musicInstance);
            }
        }
    }

    @Override
    public MusicInstance playMusic(ParrotMusicType musicType, boolean loop, boolean fadeIn, float position, int channel, int boomChannel) {
        // Stop channel
        stopMusicChannel(channel, true);

        // Make sure music is valid
        Music music = musicType.getMusic();
        if(music != null) {

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

            // Calculate perceived relative volume
            float relativeVolume = ParrotUtils.getPerceivedVolume(musicType.getRelativeVolume(), settings.loudnessExponentialCurve);

            // Configure MusicInstance
            musicInstance.state = State.SILENT;
            musicInstance.nextState = fadeIn ? State.FADING_IN : State.PLAYING;
            musicInstance.music = music;
            musicInstance.musicType = musicType;
            musicInstance.isLooping = loop;
            musicInstance.targetVolume = masterVolume * relativeVolume * volumesByChannel.get(channel, 1);
            musicInstance.initialPosition = position;
            musicInstance.channel = channel;
            musicInstance.boomChannel = boomChannel;

            // Configure Music
            if(music.isPlaying()) {
                music.setVolume(MIN_VOLUME);
            }
            music.pause();
            music.setPosition(0);

            return musicInstance;
        }

        return null;
    }

    @Override
    public void pauseMusic(ParrotMusicType musicType, boolean gracefully) {
        for(MusicInstance musicInstance : musicInstances) {
            if(musicInstance.musicType == musicType) {
                pauseMusicInstance(musicInstance, gracefully);
            }
        }
    }

    @Override
    public void pauseMusicChannel(int channel, boolean gracefully) {
        for(MusicInstance musicInstance : musicInstances) {
            if(musicInstance.channel == channel) {
                pauseMusicInstance(musicInstance, gracefully);
            }
        }
    }

    @Override
    public void pauseMusicInstance(MusicInstance musicInstance, boolean gracefully) {
        State state = musicInstance.state;
        boolean isActive = state.isActive();
        boolean isPaused = state == State.PAUSED;
        boolean isSilent = state == State.SILENT;
        if(!isPaused && (isActive || isSilent)) {
            musicInstance.state = State.FADING_OUT;
            musicInstance.nextState = State.PAUSED;
            musicInstance.stateTimer = 0;
            musicInstance.targetVolume = musicInstance.music.getVolume();
            if(!gracefully || isSilent) {
                musicInstance.stateTimer = Float.MAX_VALUE;
            }
        }
    }

    @Override
    public void resumeMusic(ParrotMusicType musicType, boolean gracefully) {
        for(MusicInstance musicInstance : musicInstances) {
            if(musicInstance.musicType == musicType) {
                resumeMusicInstance(musicInstance, gracefully);
            }
        }
    }

    @Override
    public void resumeMusicChannel(int channel, boolean gracefully) {
        for(MusicInstance musicInstance : musicInstances) {
            if(musicInstance.channel == channel) {
                resumeMusicInstance(musicInstance, gracefully);
            }
        }
    }

    @Override
    public void resumeMusicInstance(MusicInstance musicInstance, boolean gracefully) {
        State state = musicInstance.state;
        if(state == State.PAUSED || state == State.FADING_OUT) {
            musicInstance.state = State.FADING_IN;
            musicInstance.nextState = State.PLAYING;
            musicInstance.stateTimer = 0;
            float relativeVolume = ParrotUtils.getPerceivedVolume(musicInstance.musicType.getRelativeVolume(), settings.loudnessExponentialCurve);
            musicInstance.targetVolume = masterVolume * relativeVolume * volumesByChannel.get(musicInstance.channel, 1);
            if(!gracefully) {
                musicInstance.stateTimer = Float.MAX_VALUE;
            }
        }
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

    @Override
    public void stopMusicInstance(MusicInstance musicInstance, boolean gracefully) {
        State state = musicInstance.state;
        boolean isActive = state.isActive();
        boolean isSilent = state == State.SILENT;
        if(isActive || isSilent) {
            musicInstance.state = State.FADING_OUT;
            musicInstance.nextState = State.DISPOSING;
            musicInstance.stateTimer = 0;
            musicInstance.targetVolume = musicInstance.music.getVolume();
            if(!gracefully || isSilent) {
                musicInstance.stateTimer = Float.MAX_VALUE;
            }
        }
    }

    @Override
    public boolean isMusicPlaying() {
        if(isPowered()) {
            for(MusicInstance musicInstance : musicInstances) {
                if(musicInstance != null && musicInstance.state.isActive()) {
                    return true;
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

    @Override
    public ParrotMusicType getCurrentMusic(int channel) {
        if(isPowered()) {
            for(MusicInstance musicInstance : musicInstances) {
                if(musicInstance != null && musicInstance.channel == channel && musicInstance.state.isActive()) {
                    return musicInstance.musicType;
                }
            }
        }
        return null;
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
