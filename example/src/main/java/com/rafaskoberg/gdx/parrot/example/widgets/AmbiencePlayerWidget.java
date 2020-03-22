package com.rafaskoberg.gdx.parrot.example.widgets;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisSlider;
import com.rafaskoberg.gdx.parrot.Parrot;
import com.rafaskoberg.gdx.parrot.example.AmbienceType;
import com.rafaskoberg.gdx.parrot.example.SoundType;
import com.rafaskoberg.gdx.parrot.example.util.Constants;
import com.rafaskoberg.gdx.parrot.example.util.Utils;

public class AmbiencePlayerWidget extends Table {
    private final Parrot                     parrot;
    private       VisImageButton             playButton;
    private       VisSelectBox<AmbienceType> selectBox;

    public AmbiencePlayerWidget(Parrot parrot) {
        this.parrot = parrot;

        // Create ambience label
        VisLabel ambienceLabel = new VisLabel("Ambience", "default-font", "t-medium-dark");
        ambienceLabel.setAlignment(Align.center);

        // Create inner tables
        Table controllerTable = createControllerTable(parrot);
        Table sliderTable = createSliderTable(parrot);

        // Configure widget
        background(Utils.createColorDrawable(VisUI.getSkin().getColor("t-dark"), 0.5f));
        pad(5);
        defaults().uniformX().expand();

        // Populate widget
        add(ambienceLabel);
        add(controllerTable);
        add(sliderTable);
    }

    private Table createControllerTable(Parrot parrot) {
        // Play button
        playButton = new VisImageButton(Utils.createModernButtonStyle(Utils.loadImageDrawable("play-small"), true));
        Utils.addSoundToButton(playButton, parrot);
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onPlayButton();
            }
        });

        // Create SelectBox
        selectBox = new VisSelectBox<>();
        Utils.addSoundToButton(selectBox, parrot);
        selectBox.setItems(AmbienceType.values());
        selectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onSelectBox(selectBox.getSelected());
            }
        });

        // Create table
        Table table = new Table();
        table.defaults().pad(5).space(0, 20, 0, 20).center().grow();
        table.add(playButton);
        table.add(selectBox);
        return table;
    }

    private Table createSliderTable(Parrot parrot) {
        // Volume icon
        VisImage volumeIcon = new VisImage(Utils.loadImageDrawable("volume"));
        volumeIcon.setScaling(Scaling.fit);
        volumeIcon.setColor(VisUI.getSkin().getColor("t-medium-dark"));
        volumeIcon.pack();

        // Music slider
        VisSlider sliderMusicVolume = new VisSlider(0, 1, 0.01f, false);
        Utils.addSoundToButton(sliderMusicVolume, parrot);
        sliderMusicVolume.setValue(100);
        sliderMusicVolume.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                parrot.setMusicChannelVolume(Constants.AMBIENCE_CHANNEL, sliderMusicVolume.getValue());
            }
        });

        // Create table
        Table table = new Table();
        table.defaults().pad(5).center().grow();
        table.add(volumeIcon);
        table.add(sliderMusicVolume);
        return table;
    }

    private void onPlayButton() {
        // Is ambience playing
        boolean isPlaying = parrot.isMusicPlaying(Constants.AMBIENCE_CHANNEL);

        // Play ambience
        if(!isPlaying) {
            AmbienceType ambienceType = selectBox.getSelected();
            parrot.playMusic(ambienceType, true, true, Constants.AMBIENCE_CHANNEL, -1);
            playButton.setChecked(true);
        }

        // Stop ambience
        if(isPlaying) {
            parrot.stopMusicChannel(Constants.AMBIENCE_CHANNEL, true);
            playButton.setChecked(false);
        }
    }

    private void onSelectBox(AmbienceType selectedAmbience) {
        // Is ambience playing
        boolean isPlaying = parrot.isMusicPlaying(Constants.AMBIENCE_CHANNEL);

        // Play ambience
        if(isPlaying) {
            parrot.playMusic(selectedAmbience, true, true, Constants.AMBIENCE_CHANNEL, -1);
        }

        // Play sound effect
        parrot.playSound(SoundType.UI_HOVER);
    }
}
