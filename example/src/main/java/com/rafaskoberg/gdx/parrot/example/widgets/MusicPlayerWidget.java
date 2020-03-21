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
import com.kotcrab.vis.ui.widget.VisSlider;
import com.rafaskoberg.gdx.parrot.Parrot;
import com.rafaskoberg.gdx.parrot.example.MusicType;
import com.rafaskoberg.gdx.parrot.example.util.Constants;
import com.rafaskoberg.gdx.parrot.example.util.Utils;

public class MusicPlayerWidget extends Table {
    private final Parrot         parrot;
    private       int            curentMusicTypeIndex = 0;
    private       VisImageButton playButton;
    private       VisLabel       trackLabel;
    private       VisLabel       artistLabel;

    public MusicPlayerWidget(Parrot parrot) {
        this.parrot = parrot;

        // Create inner tables
        Table creditsTable = createCreditsTable(parrot);
        Table controllerTable = createControllerTable(parrot);
        Table sliderTable = createSliderTable(parrot);

        // Configure widget
        background(Utils.createColorDrawable(VisUI.getSkin().getColor("t-dark"), 0.5f));
        pad(5);
        defaults().uniformX().expand();

        // Populate widget
        add(creditsTable);
        add(controllerTable);
        add(sliderTable);

        // Update labels
        updateLabels();
    }

    private Table createCreditsTable(Parrot parrot) {
        // Track label
        trackLabel = new VisLabel("", "default-font", "t-white");
        trackLabel.setAlignment(Align.center);

        // Artist label
        artistLabel = new VisLabel("", "small-font", "t-medium");
        artistLabel.setAlignment(Align.center);

        // Create table
        Table table = new Table();
        table.defaults().pad(5).center().grow();
        table.add(trackLabel);
        table.row();
        table.add(artistLabel);
        return table;
    }

    private Table createControllerTable(Parrot parrot) {
        // Previous button
        VisImageButton previousButton = new VisImageButton(Utils.createModernButtonStyle(Utils.loadImageDrawable("previous")));
        Utils.addSoundToButton(previousButton, parrot);
        previousButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onSkipButton(false);
            }
        });

        // Play/Stop button
        playButton = new VisImageButton(Utils.createModernButtonStyle(Utils.loadImageDrawable("play"), true));
        Utils.addSoundToButton(playButton, parrot);
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onPlayStopButton();
            }
        });

        // Next button
        VisImageButton nextButton = new VisImageButton(Utils.createModernButtonStyle(Utils.loadImageDrawable("next")));
        Utils.addSoundToButton(nextButton, parrot);
        nextButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onSkipButton(true);
            }
        });

        // Create table
        Table table = new Table();
        table.defaults().pad(5).space(0, 20, 0, 20).center().grow();
        table.add(previousButton);
        table.add(playButton);
        table.add(nextButton);
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
                parrot.setMusicChannelVolume(Constants.MUSIC_CHANNEL, sliderMusicVolume.getValue());
            }
        });

        // Create table
        Table table = new Table();
        table.defaults().pad(5).center().grow();
        table.add(volumeIcon);
        table.add(sliderMusicVolume);
        return table;
    }

    private void onPlayStopButton() {
        // Is music playing
        boolean isPlaying = parrot.isMusicPlaying(Constants.MUSIC_CHANNEL);

        // Play music
        if(!isPlaying) {
            MusicType musicType = MusicType.values()[curentMusicTypeIndex];
            parrot.playMusic(musicType, true, true, Constants.MUSIC_CHANNEL, -1);
            playButton.setChecked(true);
        }

        // Stop music
        if(isPlaying) {
            parrot.stopMusicChannel(Constants.MUSIC_CHANNEL, true);
            playButton.setChecked(false);
        }
    }

    /**
     * Called when the skip buttons are clicked.
     *
     * @param next true if the widget should skip to the next song, or false to skip to the previous one.
     */
    private void onSkipButton(boolean next) {
        MusicType[] musicTypes = MusicType.values();
        int limit = musicTypes.length;

        // Advance index
        curentMusicTypeIndex += next ? 1 : -1;

        // Wrap index around the array boundaries
        if(curentMusicTypeIndex < 0) {
            curentMusicTypeIndex += limit;
        } else if(curentMusicTypeIndex >= limit) {
            curentMusicTypeIndex -= limit;
        }

        // Update labels
        updateLabels();

        // Play music
        if(parrot.isMusicPlaying(Constants.MUSIC_CHANNEL)) {
            MusicType musicType = musicTypes[curentMusicTypeIndex];
            parrot.playMusic(musicType, true, true, Constants.MUSIC_CHANNEL, -1);
        }
    }

    private void updateLabels() {
        MusicType musicType = MusicType.values()[curentMusicTypeIndex];
        trackLabel.setText(musicType.getTrackName());
        artistLabel.setText("by " + musicType.getArtistName());
    }
}
