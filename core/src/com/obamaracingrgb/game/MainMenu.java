package com.obamaracingrgb.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;

public class MainMenu implements Screen {

    private ObamaRGBGameClass gamu;

    OrthographicCamera cam;

    public MainMenu(final ObamaRGBGameClass game){
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
        gamu.batch.setProjectionMatrix(cam.combined);

        gamu.batch.begin();
        gamu.font.draw(gamu.batch, "Enterme para ir al siguiente menu xd", 100, 150);
        gamu.batch.end();

        if(Gdx.input.isKeyPressed(Input.Keys.ENTER)){
            ScreenUtils.clear(1,1,1,1);
            gamu.setScreen(new SubMenuScreen(gamu));
            dispose();
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
