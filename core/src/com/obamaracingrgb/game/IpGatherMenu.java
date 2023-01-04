package com.obamaracingrgb.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class IpGatherMenu implements Screen {
    private final ObamaRGBGameClass gamu;

    private Stage stage;

    private OrthographicCamera cam;

    private TextField testoIP;
    private TextField testoPort;
    private BitmapFont fuentesita;

    private Texture btSalirImg;
    private Texture btJugarImg;

    private ImageButton buttonSalir;
    private ImageButton buttonJugar;

    private Viewport view;

    public IpGatherMenu(ObamaRGBGameClass game){
        this.gamu = game;

        cam = new OrthographicCamera();
        cam.setToOrtho(false, 1920, 1080);

        view = new StretchViewport(1920, 1080, cam);

        stage = new Stage(new StretchViewport(1920, 1080));
        Gdx.input.setInputProcessor(stage);

        fuentesita = new BitmapFont();
        fuentesita.getData().setScale(3, 3);

        TextField.TextFieldStyle joquin = new TextField.TextFieldStyle();
        joquin.fontColor = Color.GOLD;
        joquin.font = fuentesita;
        joquin.cursor = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("spriteAssets/cursor.png"))));
        joquin.selection = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("spriteAssets/selection.png"))));
        joquin.background = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("spriteAssets/text.png"))));

        testoIP = new TextField("Server IP", joquin);
        testoIP.setX(752 - 300);
        testoIP.setY(540);
        testoIP.setHeight(70);
        testoIP.setWidth(415);
        testoIP.setAlignment(1);

        testoPort = new TextField("Server port", joquin);
        testoPort.setX(752 + 300);
        testoPort.setY(540);
        testoPort.setHeight(70);
        testoPort.setWidth(415);
        testoPort.setAlignment(1);

        btSalirImg = new Texture(Gdx.files.internal("spriteAssets/Salir.png"));
        btJugarImg = new Texture(Gdx.files.internal("spriteAssets/jugar.png"));

        buttonSalir = new ImageButton(new TextureRegionDrawable(new TextureRegion(btSalirImg)));
        buttonSalir.setX(1920/4 - btSalirImg.getWidth()/2);
        buttonSalir.setY(-20);

        buttonJugar = new ImageButton(new TextureRegionDrawable(new TextureRegion(btJugarImg)));
        buttonJugar.setX(1920/4 - btSalirImg.getWidth()/2 + 1920/2);
        buttonJugar.setY(-20);

        buttonSalir.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gamu.setScreen(new MainMenu(gamu));
            }
        });

        buttonJugar.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try{
                    Socket conection = new Socket(testoIP.getText(), Integer.parseInt(testoPort.getText()));
                    gamu.setScreen(new PlayerSelectionScreen(gamu, conection));
                } catch (Exception e) {     //ponemo exception porque no me importa que falle, hacemo lo mismo
                    gamu.setScreen(new MainMenu(gamu));
                }
                //System.out.println("IP: " + testoIP.getText());
                //System.out.println("Puerto: " + testoPort.getText());
            }
        });


        stage.addActor(testoIP);
        stage.addActor(testoPort);
        stage.addActor(buttonSalir);
        stage.addActor(buttonJugar);
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
        stage.dispose();
        fuentesita.dispose();
    }
}
