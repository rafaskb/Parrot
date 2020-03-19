package com.rafaskoberg.gdx.parrot.example;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.VisUI.SkinScale;
import com.kotcrab.vis.ui.widget.VisSlider;
import com.rafaskoberg.gdx.parrot.Parrot;

public class ParrotExample extends ApplicationAdapter {
    private Parrot        parrot;
    private Stage         stage;
    private SpriteBatch   batch;
    private ParrotHandler parrotHandler;

    @Override
    public void create() {
        // Load Parrot
        parrot = new Parrot();

        // Load VisUI
        VisUI.load(SkinScale.X2);

        // Create batch and stage
        this.batch = new SpriteBatch();
        this.stage = new Stage(new ScreenViewport(), batch);
        Gdx.input.setInputProcessor(stage);

        // Create ParrotHandler
        this.parrotHandler = new ParrotHandler(parrot);

        // Load sounds
        for(SoundType soundType : SoundType.values()) {
            AudioLoader.load(soundType);
        }

        // Load music
        for(MusicType musicType : MusicType.values()) {
            AudioLoader.load(musicType);
        }

        // Create UI
        createUi();
    }

    private void createUi() {
        // Create root table
        Table rootTable = new Table();
        rootTable.setFillParent(true);
        this.stage.addActor(rootTable);

        // Create buttons
        TextButton buttonFootsteps = new TextButton("Footsteps", VisUI.getSkin(), "toggle");
        buttonFootsteps.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                parrotHandler.onFootstepButton(buttonFootsteps.isChecked());
            }
        });

        TextButton buttonMusic = new TextButton("Music", VisUI.getSkin(), "toggle");
        buttonMusic.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                parrotHandler.onMusicButton(buttonMusic.isChecked());
            }
        });

        // Create sliders
        VisSlider sliderSoundVolume = new VisSlider(0, 1, 0.01f, true);
        sliderSoundVolume.setValue(100);
        sliderSoundVolume.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                parrot.setSoundVolume(sliderSoundVolume.getValue());
            }
        });
        VisSlider sliderMusicVolume = new VisSlider(0, 1, 0.01f, true);
        sliderMusicVolume.setValue(100);
        sliderMusicVolume.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                parrot.setMusicVolume(sliderMusicVolume.getValue());
            }
        });

        // Configure table
        rootTable.pad(50f);
        rootTable.row().uniform().expand().growX().space(40).center();
        rootTable.add(buttonFootsteps, buttonMusic, sliderSoundVolume, sliderMusicVolume);
        rootTable.pack();
    }

    public void update(float delta) {
        stage.act(delta);
        parrot.updateSounds(stage.getCamera(), delta);
        parrot.updateMusic(delta);
    }

    @Override
    public void render() {
        update(Gdx.graphics.getDeltaTime());

        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        VisUI.dispose();
        stage.dispose();
    }

    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Parrot Example");
        config.setWindowedMode(1024, 576);
        config.setAudioConfig(128, 4096, 9); // Increase libGDX's default audio limits which are pretty low
        config.setResizable(false);
        new Lwjgl3Application(new ParrotExample(), config);
    }

}
