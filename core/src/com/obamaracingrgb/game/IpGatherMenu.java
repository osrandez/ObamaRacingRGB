package com.obamaracingrgb.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;

public class IpGatherMenu implements Screen {
    private final ObamaRGBGameClass gamu;

    private Stage stage;

    private OrthographicCamera cam;

    private TextField testo;
    private BitmapFont fuentesita;

    public IpGatherMenu(ObamaRGBGameClass game){
        this.gamu = game;

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        cam = new OrthographicCamera();
        cam.setToOrtho(false, 1920, 1080);

        fuentesita = new BitmapFont();
        fuentesita.getData().setScale(3, 3);

        TextField.TextFieldStyle joquin = new TextField.TextFieldStyle();
        joquin.fontColor = Color.GOLD;
        joquin.font = fuentesita;
        joquin.cursor = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("spriteAssets/cursor.png"))));
        joquin.selection = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("spriteAssets/selection.png"))));
        joquin.background = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("spriteAssets/textoBkg.png"))));

        testo = new TextField("texto", joquin);
        testo.setX(910);
        testo.setY(540);
        testo.setHeight(80);
        testo.setWidth(800);
        testo.setAlignment(1);


        stage.addActor(testo);
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
        stage.dispose();
        fuentesita.dispose();
    }
}
