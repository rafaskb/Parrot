package com.rafaskoberg.gdx.parrot.example;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.rafaskoberg.boom.BoomChannel;
import com.rafaskoberg.boom.effect.reverb.ReverbPreset;
import com.rafaskoberg.gdx.parrot.Parrot;
import com.rafaskoberg.gdx.parrot.ParrotSettings;
import com.rafaskoberg.gdx.parrot.example.util.Constants;
import com.rafaskoberg.gdx.parrot.example.util.Utils;
import com.rafaskoberg.gdx.parrot.example.widgets.AmbiencePlayerWidget;
import com.rafaskoberg.gdx.parrot.example.widgets.MusicPlayerWidget;
import com.rafaskoberg.gdx.parrot.example.widgets.SoundPlayerWidget;

public class ParrotExample extends ApplicationAdapter {
    private Parrot      parrot;
    private Stage       stage;
    private SpriteBatch batch;
    private Image       parrotIcon;
    private boolean     wasMusicPlaying;

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

        // Create ambience player widget
        AmbiencePlayerWidget ambiencePlayerWidget = new AmbiencePlayerWidget(parrot);
        ambiencePlayerWidget.pack();

        // Create sound player widget
        SoundPlayerWidget soundPlayerWidget = new SoundPlayerWidget(parrot);
        soundPlayerWidget.pack();

        // Create music player widget
        MusicPlayerWidget musicPlayerWidget = new MusicPlayerWidget(parrot);
        musicPlayerWidget.pack();

        // Create parrot icon
        parrotIcon = new Image(Utils.loadImageDrawable("Parrot"));
        parrotIcon.setAlign(Align.center);
        parrotIcon.setScaling(Scaling.fit);
        parrotIcon.getColor().a = 0.0f;
        parrotIcon.pack();

        // Configure table
        rootTable.setFillParent(true);
        rootTable.add(ambiencePlayerWidget).colspan(2).growX().top();
        rootTable.row();
        rootTable.add(parrotIcon).expand();
        rootTable.add(soundPlayerWidget).growY().right();
        rootTable.row();
        rootTable.add(musicPlayerWidget).colspan(2).growX().bottom();
        rootTable.pack();
    }

    private void update(float delta) {
        // Update stage
        stage.act(delta);

        // Update listener position based on mouse coordinates
        float x = Gdx.input.getX() - Gdx.graphics.getWidth() / 2;
        float y = Gdx.graphics.getHeight() - Gdx.input.getY() - Gdx.graphics.getHeight() / 2;
        float worldX = x / 50; // Cheap conversion from screen coordinates to pseudo world coordinates
        float worldY = y / 50; // Cheap conversion from screen coordinates to pseudo world coordinates
        parrot.setSpatialListenerCoordinates(worldX, worldY);

        // Update sound category pitch values based on mouse coordinates
        float yRate = MathUtils.clamp(y / Gdx.graphics.getHeight(), 0, 1);
        float pitchFactor = MathUtils.lerp(0.33f, 2.0f, yRate);
        parrot.setSoundCategoryPitchFactor(SoundCategory.PLAYER, pitchFactor);

        // Update parrot
        parrot.update(delta);

        // Update parrot icon
        boolean isMusicPlaying = parrot.isMusicPlaying();
        if(isMusicPlaying != wasMusicPlaying) {
            wasMusicPlaying = isMusicPlaying;

            // Clear actions
            parrotIcon.clearActions();

            // Add new fade action
            ParrotSettings settings = parrot.getSettings();
            float alpha = isMusicPlaying ? 1 : 0;
            float duration = isMusicPlaying ? settings.musicFadeInDuration : settings.musicFadeOutDuration;
            parrotIcon.addAction(Actions.alpha(alpha, duration, Interpolation.sine));
        }
        float parrotIconRgb = parrot.getMusicChannelVolume(Constants.MUSIC_CHANNEL);
        parrotIcon.getColor().r = parrotIconRgb;
        parrotIcon.getColor().g = 0.25f + parrotIconRgb * 0.75f;
        parrotIcon.getColor().b = 0.5f + parrotIconRgb * 0.5f;
    }

    @Override
    public void render() {
        // Update app
        update(Gdx.graphics.getDeltaTime());

        // Clean screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw stage
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // Resize stage
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        VisUI.dispose();
        stage.dispose();
    }
}
