package com.rafaskoberg.gdx.parrot.example;

import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.rafaskoberg.gdx.parrot.Parrot;

public class ParrotHandler {
    private static final float FOOTSTEP_INTERVAL = 0.5f;

    private Parrot  parrot;
    private Task    footstepTask;
    private boolean isFlamethrowerActive = false;
    private long    flamethrowerSoundId  = 0;

    public ParrotHandler(Parrot parrot) {
        this.parrot = parrot;
    }

    public void update(float delta) {
        if(isFlamethrowerActive) {
            parrot.touchSound(flamethrowerSoundId);
        }
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

    public void onFlamethrowerButton(boolean checked) {
        if(checked) {
            parrot.playSound(SoundType.FLAMETHROWER_SPARK);
            flamethrowerSoundId = parrot.playSound(SoundType.FLAMETHROWER);
            isFlamethrowerActive = true;
        } else {
            flamethrowerSoundId = 0;
            isFlamethrowerActive = false;
        }
    }
}
