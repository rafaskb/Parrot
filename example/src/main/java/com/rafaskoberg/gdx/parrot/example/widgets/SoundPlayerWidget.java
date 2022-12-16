package com.rafaskoberg.gdx.parrot.example.widgets;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisSlider;
import com.rafaskoberg.gdx.parrot.Parrot;
import com.rafaskoberg.gdx.parrot.example.SoundType;
import com.rafaskoberg.gdx.parrot.example.util.Constants;
import com.rafaskoberg.gdx.parrot.example.util.Utils;

public class SoundPlayerWidget extends Table {
    private static final float FOOTSTEP_INTERVAL = 0.35f;

    private final Parrot parrot;
    private Task footstepTask;
    private boolean isFlamethrowerActive = false;
    private long flamethrowerSoundId = 0;

    public SoundPlayerWidget(Parrot parrot) {
        this.parrot = parrot;

        // Create buttons
        TextButton buttonFootsteps = new TextButton("Footsteps", VisUI.getSkin(), "toggle");
        Utils.addSoundToButton(buttonFootsteps, parrot);
        buttonFootsteps.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onFootstepButton(buttonFootsteps.isChecked());
            }
        });

        TextButton buttonWarning = new TextButton("Warning Beep", VisUI.getSkin(), "toggle");
        Utils.addSoundToButton(buttonWarning, parrot);
        buttonWarning.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onWarningButton(buttonWarning.isChecked());
            }
        });

        TextButton buttonFlamethrower = new TextButton("Flamethrower", VisUI.getSkin(), "toggle");
        Utils.addSoundToButton(buttonFlamethrower, parrot);
        buttonFlamethrower.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onFlamethrowerButton(buttonFlamethrower.isChecked());
            }
        });

        // Volume icon
        VisImage volumeIcon = new VisImage(Utils.loadImageDrawable("volume"));
        volumeIcon.setScaling(Scaling.fit);
        volumeIcon.setColor(VisUI.getSkin().getColor("t-medium-dark"));
        volumeIcon.pack();

        // Sound slider
        VisSlider sliderSoundVolume = new VisSlider(0, 1, 0.01f, false);
        Utils.addSoundToButton(sliderSoundVolume, parrot);
        sliderSoundVolume.setValue(100);
        sliderSoundVolume.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                parrot.setSoundPlayerVolume(sliderSoundVolume.getValue());
            }
        });

        // Slider table
        Table sliderTable = new Table();
        sliderTable.defaults().pad(5);
        sliderTable.add(volumeIcon);
        sliderTable.add(sliderSoundVolume).growX();

        // Populate widget
        defaults().pad(5).center().growX().uniform();
        add(buttonFootsteps).row();
        add(buttonWarning).row();
        add(buttonFlamethrower).row();
        add(sliderTable).expandY().bottom();

        // Configure widget
        background(Utils.createColorDrawable(VisUI.getSkin().getColor("t-dark"), 0.75f));
        pad(5);
        defaults().uniformX().expand();
    }

    private void onFootstepButton(boolean checked) {
        if(checked) {
            footstepTask = Timer.schedule(new Task() {
                @Override
                public void run() {
                    parrot.playSound(SoundType.FOOTSTEPS, -1, 0, 0, 1, SoundType.FOOTSTEPS.getPlaybackMode(), Constants.BOOM_GENERAL_CHANNEL);
                }
            }, 0, FOOTSTEP_INTERVAL);
        } else {
            if(footstepTask != null) {
                footstepTask.cancel();
                footstepTask = null;
            }
        }
    }

    private void onWarningButton(boolean checked) {
        if(checked) {
            parrot.playSound(SoundType.WARNING_BEEP, -1, 0, 0, 1, SoundType.WARNING_BEEP.getPlaybackMode(), Constants.BOOM_GENERAL_CHANNEL);
        } else {
            parrot.stopSound(SoundType.WARNING_BEEP);
        }
    }

    private void onFlamethrowerButton(boolean checked) {
        if(checked) {
            parrot.playSound(SoundType.FLAMETHROWER_SPARK, -1, 0, 0, 1, SoundType.FLAMETHROWER_SPARK.getPlaybackMode(), Constants.BOOM_GENERAL_CHANNEL);
            flamethrowerSoundId = parrot.playSound(SoundType.FLAMETHROWER, 0, 0, 1, SoundType.FLAMETHROWER.getPlaybackMode(), Constants.BOOM_GENERAL_CHANNEL);
            isFlamethrowerActive = true;
        } else {
            flamethrowerSoundId = 0;
            isFlamethrowerActive = false;
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if(isFlamethrowerActive) {
            parrot.touchSound(flamethrowerSoundId);
        }
    }
}
