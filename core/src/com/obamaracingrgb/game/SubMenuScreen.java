package com.obamaracingrgb.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;

public class SubMenuScreen implements Screen {
    private ObamaRGBGameClass gamu;

    OrthographicCamera cam;
    public SubMenuScreen(final ObamaRGBGameClass game){
        this.gamu = game;

        cam = new OrthographicCamera();
        cam.setToOrtho(false, 1920, 1080);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0,0,0,1);

        cam.update();
        gamu.sBatch.setProjectionMatrix(cam.combined);

        gamu.sBatch.begin();
            gamu.font.draw(gamu.sBatch, "Texto :)", 100, 150);
        gamu.sBatch.end();

        if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
            ScreenUtils.clear(1,1,1,1);
            gamu.dispose();
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
