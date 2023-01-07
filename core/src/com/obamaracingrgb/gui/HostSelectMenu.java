package com.obamaracingrgb.gui;

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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.obamaracingrgb.dominio.Player;
import com.obamaracingrgb.game.ObamaRGBGameClass;
import com.obamaracingrgb.game.Track1;
import com.obamaracingrgb.net.server.ServerThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.atomic.AtomicBoolean;

public class HostSelectMenu implements Screen {
    private final ObamaRGBGameClass gamu;

    OrthographicCamera cam;

    private Stage stage;

    private Texture btSalirImg;
    private Texture btJugarImg;

    private ImageButton buttonSalir;
    private ImageButton buttonJugar;

    private ServerSocket sSok;
    private int port;

    private Viewport view;

    private Array<Player> yogadores;

    private Player actual;

    private Thread aceptarConexiones;
    private AtomicBoolean racismo;

    public HostSelectMenu(ObamaRGBGameClass game, final ServerSocket sSok, String modelName){
        this.sSok = sSok;
        this.gamu = game;



        actual = gamu.pConstructors.get(modelName).construct();

        yogadores = new Array<>();

        yogadores.add(actual);

        racismo = new AtomicBoolean(true);
        aceptarConexiones = new ServerThread(sSok, yogadores, this.gamu, racismo);
        aceptarConexiones.start();

        port = sSok.getLocalPort();

        cam = new OrthographicCamera(1920, 1080);
        cam.setToOrtho(false, 1920, 1080);

        view = new StretchViewport(1920, 1080, cam);

        stage = new Stage(new StretchViewport(1920, 1080));
        Gdx.input.setInputProcessor(stage);

        btSalirImg = new Texture(Gdx.files.internal("spriteAssets/Salir.png"));
        btJugarImg = new Texture(Gdx.files.internal("spriteAssets/jugar.png"));

        buttonSalir = new ImageButton(new TextureRegionDrawable(new TextureRegion(btSalirImg)));
        buttonSalir.setX(1920/4 - btSalirImg.getWidth()/2);
        buttonSalir.setY(-20);

        buttonJugar = new ImageButton(new TextureRegionDrawable(new TextureRegion(btJugarImg)));
        buttonSalir.setX(1920/4 - btSalirImg.getWidth()/2 + 1920/2);
        buttonSalir.setY(-20);

        buttonSalir.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    sSok.close();
                } catch (IOException e) {
                    System.out.println("cerramos mal xd");
                }
                gamu.setScreen(new MainMenu(gamu));
            }
        });

        buttonJugar.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("Momento juegos 88");
                aceptarConexiones.interrupt();
                Track1 tJuan = new Track1(gamu, yogadores, actual, racismo);
                gamu.setScreen(tJuan);
            }
        });

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

        gamu.sBatch.begin();
            gamu.font.draw(gamu.sBatch, "Port: " + this.port, gamu.izquierda + 850, gamu.abajo + 1030);
        gamu.sBatch.end();

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

    }
}
