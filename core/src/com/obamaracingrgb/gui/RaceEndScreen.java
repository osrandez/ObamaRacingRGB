package com.obamaracingrgb.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.obamaracingrgb.game.ObamaRGBGameClass;

import java.util.concurrent.atomic.AtomicBoolean;

public class RaceEndScreen implements Screen {
    private ObamaRGBGameClass gamu;
    private OrthographicCamera cam;
    private Viewport view;
    private AtomicBoolean ganado;

    public RaceEndScreen(ObamaRGBGameClass game, AtomicBoolean ganado){
        this.gamu = game;
        this.ganado = ganado;
        cam = new OrthographicCamera();
        cam.setToOrtho(false, 1920, 1080);
        view = new StretchViewport(1920, 1080, cam);
    }
    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        this.gamu.sBatch.begin();
            this.gamu.font.draw(this.gamu.sBatch, this.ganado.get()? "true":"!true", 500, 500);
            //this.gamu.font.draw(this.gamu.sBatch, "tu madre la negra", 500, 500);
        this.gamu.sBatch.end();

        if(Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)){
            if(this.ganado.get()){
                this.gamu.setScreen(new MainMenu(this.gamu));
            }else{
                //los perdedores al pozo >:)
                Gdx.app.exit();
            }
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

    }
}
