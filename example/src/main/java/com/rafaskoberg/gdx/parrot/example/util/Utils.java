package com.rafaskoberg.gdx.parrot.example.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisImageButton.VisImageButtonStyle;
import com.rafaskoberg.gdx.parrot.Parrot;
import com.rafaskoberg.gdx.parrot.example.SoundType;

public class Utils {

    /**
     * Creates a new translucent {@link Drawable} filled with the given color and alpha values. Note this method creates
     * a lot of resources, although small, and can cause memory problems if called too many times. If you intend to call
     * this method on each frame, it's a better idea to store the result somewhere.
     */
    public static Drawable createColorDrawable(Color color, float alpha) {
        Pixmap pixmapBg = new Pixmap(10, 10, Format.RGBA8888);
        pixmapBg.setColor(color.r, color.g, color.b, alpha);
        pixmapBg.fill();
        Texture textureBg = new Texture(pixmapBg);
        Sprite spriteBg = new Sprite(textureBg);
        pixmapBg.dispose();
        return new SpriteDrawable(spriteBg);
    }

    public static TextureRegionDrawable loadImageDrawable(String name) {
        Texture texture = new Texture(Gdx.files.internal("images/" + name + ".png"));
        texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        return new TextureRegionDrawable(new TextureRegion(texture));
    }

    public static VisImageButtonStyle createModernButtonStyle(TextureRegionDrawable baseDrawable) {
        return createModernButtonStyle(baseDrawable, false);
    }

    public static VisImageButtonStyle createModernButtonStyle(TextureRegionDrawable baseDrawable, boolean toggle) {
        VisImageButtonStyle style = new VisImageButtonStyle();
        style.imageUp = baseDrawable;
        style.imageDown = baseDrawable.tint(VisUI.getSkin().getColor("t-highlight-dark"));
        style.imageOver = baseDrawable.tint(VisUI.getSkin().getColor("t-highlight"));
        if(toggle) {
            style.imageChecked = baseDrawable.tint(VisUI.getSkin().getColor("t-highlight"));
        }
        return style;
    }

    /** Adds a {@link ClickListener} to the given button that plays sounds. */
    public static void addSoundToButton(Actor button, Parrot parrot) {
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);

                // Play click sound
                parrot.playSound(SoundType.UI_CLICK);
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                // Ignore if mouse was already over
                if(isOver()) return;

                super.enter(event, x, y, pointer, fromActor);

                // Ignore if mouse is not over
                if(!isOver()) return;

                // Ignore if fromActor is a child
                Actor actor = event.getListenerActor();
                Actor from = fromActor;
                while(from != null) {
                    if(from == actor) return;
                    from = from.getParent();
                }

                // Play hover sound
                parrot.playSound(SoundType.UI_HOVER);
            }
        });
    }
}
