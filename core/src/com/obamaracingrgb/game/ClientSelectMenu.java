package com.obamaracingrgb.game;

import com.badlogic.gdx.Gdx;
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

public class ClientSelectMenu implements Screen {
    private final ObamaRGBGameClass gamu;

    OrthographicCamera cam;

    private Stage stage;

    private Texture btSalirImg;

    private ImageButton buttonSalir;


    public ClientSelectMenu(ObamaRGBGameClass game){
        this.gamu = game;

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        cam = new OrthographicCamera();
        cam.setToOrtho(false, 1920, 1080);

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