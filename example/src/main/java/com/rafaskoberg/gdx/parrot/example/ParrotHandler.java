package com.rafaskoberg.gdx.parrot.example;

import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.rafaskoberg.gdx.parrot.Parrot;

public class ParrotHandler {
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

    public void onMusicButton(boolean checked) {
        if(checked) {
            parrot.playMusic(MusicType.CRYSTAL_CAVE, true, false);
        } else {
            parrot.stopMusic(MusicType.CRYSTAL_CAVE);
        }
    }
}
