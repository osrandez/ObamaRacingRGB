package com.obamaracingrgb.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class ClientSelectMenu implements Screen {
    private final ObamaRGBGameClass gamu;

    OrthographicCamera cam;

    private Stage stage;

    private Texture btSalirImg;

    private ImageButton buttonSalir;

    private Viewport view;


    public ClientSelectMenu(ObamaRGBGameClass game){
        this.gamu = game;

        cam = new OrthographicCamera();
        cam.setToOrtho(false, 1920, 1080);

        view = new StretchViewport(1920, 1080);

        stage = new Stage(new StretchViewport(1920, 1080));
        Gdx.input.setInputProcessor(stage);

        btSalirImg = new Texture(Gdx.files.internal("spriteAssets/Salir.png"));

        buttonSalir = new ImageButton(new TextureRegionDrawable(new TextureRegion(btSalirImg)));
        buttonSalir.setX(1920/2 - btSalirImg.getWidth()/2);
        buttonSalir.setY(-20);

        buttonSalir.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gamu.setScreen(new MainMenu(gamu));
            }
        });

        stage.addActor(buttonSalir);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0.2f, 1);

        cam.update();

        stage.draw();

        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
            this.gamu.setScreen(new MainMenu(gamu));
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        this.stage.dispose();
        btSalirImg.dispose();
    }
}
