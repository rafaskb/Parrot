package com.rafaskoberg.gdx.parrot.example;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.rafaskoberg.boom.BoomChannel;
import com.rafaskoberg.boom.effect.echo.EchoData;
import com.rafaskoberg.boom.effect.reverb.ReverbPreset;
import com.rafaskoberg.gdx.parrot.Parrot;
import com.rafaskoberg.gdx.parrot.example.util.Constants;
import com.rafaskoberg.gdx.parrot.example.widgets.AmbiencePlayerWidget;
import com.rafaskoberg.gdx.parrot.example.widgets.MusicPlayerWidget;
import com.rafaskoberg.gdx.parrot.example.widgets.SoundPlayerWidget;

public class ParrotExample extends ApplicationAdapter {
    private Parrot      parrot;
    private Stage       stage;
    private SpriteBatch batch;

    @Override
    public void create() {
        // Load Parrot
        parrot = new Parrot();

        // Configure Boom
        if(parrot.getBoom() != null) {
            BoomChannel boomChannel = parrot.getBoom().createChannel(Constants.BOOM_GENERAL_CHANNEL);
            boomChannel.addReverb(ReverbPreset.CAVE);
        }

        // Load VisUI
        VisUI.load(Gdx.files.internal("skin/tinted.json"));

        // Create batch and stage
        this.batch = new SpriteBatch();
        this.stage = new Stage(new ScreenViewport(), batch);
        Gdx.input.setInputProcessor(stage);

        // Load sounds
        for(SoundType soundType : SoundType.values()) {
            AudioLoader.load(soundType);
        }

        // Load music
        for(MusicType musicType : MusicType.values()) {
            AudioLoader.load(musicType);
        }

        // Load ambience
        for(AmbienceType ambienceType : AmbienceType.values()) {
            AudioLoader.load(ambienceType);
        }

        // Create UI
        createUi();
    }

    private void createUi() {
        // Create root table
        Table rootTable = new Table();
        rootTable.setFillParent(true);
        this.stage.addActor(rootTable);

        AmbiencePlayerWidget ambiencePlayerWidget = new AmbiencePlayerWidget(parrot);
        ambiencePlayerWidget.pack();

        SoundPlayerWidget soundPlayerWidget = new SoundPlayerWidget(parrot);
        soundPlayerWidget.pack();

        MusicPlayerWidget musicPlayerWidget = new MusicPlayerWidget(parrot);
        musicPlayerWidget.pack();

        // Configure table
        rootTable.setFillParent(true);
        rootTable.add(ambiencePlayerWidget).colspan(2).growX().top();
        rootTable.row();
        rootTable.add().grow();
        rootTable.add(soundPlayerWidget).growY().right();
        rootTable.row();
        rootTable.add(musicPlayerWidget).colspan(2).growX().bottom();
        rootTable.pack();
    }

    public void update(float delta) {
        stage.act(delta);
        parrot.updateSounds(0, 0, delta);
        parrot.updateMusic(delta);
    }

    @Override
    public void render() {
        update(Gdx.graphics.getDeltaTime());

        Gdx.gl.glClearColor(0, 0, 0, 1);
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
}
