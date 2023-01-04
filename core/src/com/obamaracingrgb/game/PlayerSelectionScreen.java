package com.obamaracingrgb.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ScreenUtils;
import com.obamaracingrgb.dominio.Player;
import com.obamaracingrgb.game.ObamaRGBGameClass;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlayerSelectionScreen implements Screen {
    private ObamaRGBGameClass gamu;
    private PerspectiveCamera cam;

    private Array<ModelInstance> players;
    private ModelInstance actual;

    private ImageButton buttonUp;
    private ImageButton buttonDown;
    private ImageButton buttonPlay;

    private Texture btUpImg;
    private Texture btDownImg;
    private Texture btPlayImg;

    private Stage stage;
    private boolean hots;

    public PlayerSelectionScreen(ObamaRGBGameClass game, final boolean host){
        this.gamu = game;
        this.hots = host;

        cam = new PerspectiveCamera();

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(2.1f, 0f, -1.5f);
        cam.lookAt(0, 0f, 0);
        cam.rotate(new Vector3(0, 1, 0), 23);
        cam.far = 1000f;
        cam.update();

        players = new Array();

        for(ObjectMap.Entry<String, Player.Constructor> con : gamu.pConstructors){
            players.add(new ModelInstance(con.value.model, con.key));
        }

        actual = players.get(0);

        btUpImg = new Texture(Gdx.files.internal("spriteAssets/arrowUp.png"));
        btDownImg = new Texture(Gdx.files.internal("spriteAssets/arrowDown.png"));
        btPlayImg = new Texture(Gdx.files.internal("spriteAssets/jugar.png"));

        buttonUp = new ImageButton(new TextureRegionDrawable(new TextureRegion(btUpImg)));
        buttonUp.setX(1150);
        buttonUp.setY(860);

        buttonDown = new ImageButton(new TextureRegionDrawable(new TextureRegion(btDownImg)));
        buttonDown.setX(1150);
        buttonDown.setY(40);

        buttonPlay = new ImageButton(new TextureRegionDrawable(new TextureRegion(btPlayImg)));
        buttonPlay.setX(1920/4 - btPlayImg.getWidth()/2);
        buttonPlay.setY(-20);

        buttonPlay.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(host){
                    try{
                        ServerSocket sSok = new ServerSocket(0);
                        gamu.setScreen(new HostSelectMenu(gamu));
                    } catch (IOException e) {
                        gamu.setScreen(new MainMenu(gamu));
                    }
                }else{
                    gamu.setScreen(new ClientSelectMenu(gamu));
                }
            }
        });

        buttonUp.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                rotateNext();
            }
        });

        buttonDown.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                rotatePrev();
            }
        });

        stage.addActor(buttonDown);
        stage.addActor(buttonUp);
        stage.addActor(buttonPlay);



    }
    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1.f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        cam.update();

        gamu.mBatch.begin(cam);
            gamu.mBatch.render(actual);
        gamu.mBatch.end();

        actual.transform.rotate(new Vector3(0, 1, 0), 40*delta);

        stage.draw();

        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
            gamu.setScreen(new MainMenu(gamu));
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.UP)){
            rotateNext();
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.DOWN)){
            rotatePrev();
        }
    }

    private void rotateNext(){
        int indice = players.indexOf(actual, true);
        actual = players.get((++indice) % players.size);
    }

    private void rotatePrev(){
        int indice = players.indexOf(actual, true);
        actual = players.get((--indice == -1 ? (indice = players.size-1) : indice) % players.size);
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
        btPlayImg.dispose();
        btDownImg.dispose();
        btUpImg.dispose();
    }
}
