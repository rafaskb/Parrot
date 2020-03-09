package com.rafaskoberg.gdx.parrot.util;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.SharedLibraryLoader;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Method;
import com.badlogic.gdx.utils.reflect.ReflectionException;

/** Utility class for the Parrot library. */
public class ParrotUtils {
    private static Method methodOpenALSoundDuration;

    /**
     * Returns whether or not this app is running on desktop.
     */
    public static boolean isDesktop() {
        return SharedLibraryLoader.isWindows || SharedLibraryLoader.isMac || SharedLibraryLoader.isLinux;
    }

    /**
     * Returns the duration of a {@link Sound} instance, if the current backend allows it. Currently it's only possible
     * to obtain the duration of a {@link Sound} on Desktop.
     *
     * @param defaultDuration Duration to be returned in case something goes wrong.
     */
    public static float getSoundDuration(Sound sound, float defaultDuration) {
        float duration = defaultDuration;
        try {
            if(isDesktop()) {
                if(methodOpenALSoundDuration == null) {
                    String className = "com.badlogic.gdx.backends.lwjgl3.audio.OpenALSound";
                    Class<? extends Sound> clazz = ClassReflection.forName(className);
                    methodOpenALSoundDuration = ClassReflection.getMethod(clazz, "duration");
                }
                duration = (float) methodOpenALSoundDuration.invoke(sound, float.class);
            }
        } catch(ReflectionException e) {
            e.printStackTrace();
        }
        return duration;
    }

    /**
     * Converts decibels to volume.
     */
    public static float dbToVolume(float dB) {
        return (float) Math.pow(10.0f, 0.05f * dB);
    }

    /**
     * Converts volume to decibels.
     */
    public static float volumeToDb(float volume) {
        return 20.0f * ((float) Math.log10(volume));
    }

}
