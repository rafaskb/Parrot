package com.rafaskoberg.gdx.parrot.example;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.rafaskoberg.gdx.parrot.Parrot;

public class ParrotHandler {
    private static final int   MUSIC_CHANNEL     = 1;
    private static final int   AMBIENCE_CHANNEL  = 2;
    private static final float FOOTSTEP_INTERVAL = 0.5f;

    private Parrot parrot;
    private Task   footstepTask;

    public ParrotHandler(Parrot parrot) {
        this.parrot = parrot;
    }

    public void onFootstepButton(boolean checked) {
        if(checked) {
            footstepTask = Timer.schedule(new Task() {
                @Override
                public void run() {
                    parrot.playSound(SoundType.FOOTSTEPS);
                }
            }, 0, FOOTSTEP_INTERVAL);
        } else {
            if(footstepTask != null) {
                footstepTask.cancel();
                footstepTask = null;
            }
        }
    }

    public void onWarningButton(boolean checked) {
        if(checked) {
            parrot.playSound(SoundType.WARNING_BEEP);
        } else {
            parrot.stopSound(SoundType.WARNING_BEEP);
        }
    }

    public void onMusicButton(boolean checked) {
        if(checked) {
            MusicType[] values = MusicType.values();
            MusicType musicType = values[MathUtils.random(values.length - 1)];
            parrot.playMusic(musicType, true, true, MUSIC_CHANNEL, -1);
        } else {
            parrot.stopMusicChannel(MUSIC_CHANNEL);
        }
    }
}
