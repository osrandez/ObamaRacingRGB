package com.obamaracingrgb.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;

public class MainMenu implements Screen {

    private ObamaRGBGameClass gamu;
    PerspectiveCamera cam;
    Stage stage;
    ImageButton buttonHost;
    ImageButton buttonJoin;
    ImageButton buttonExit;

    Texture btHostImg;
    Texture btJoinImg;
    Texture btExitImg;

    Texture titulo;
    Texture fondo;

    ModelInstance obama;

    public MainMenu(final ObamaRGBGameClass game){
        this.gamu = game;

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        cam = new PerspectiveCamera();

        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(2f, 4.5f, 2f);
        cam.lookAt(0, 4.2f, 0);
        cam.rotate(new Vector3(0, 1, 0), 20);
        cam.far = 1000f;
        cam.update();

        obama = new ModelInstance(gamu.playerModels, "obama");
        obama.transform.setToTranslation(0f, 4f, 0f);

        titulo = new Texture(Gdx.files.internal("spriteAssets/title.png"));
        fondo = new Texture(Gdx.files.internal("spriteAssets/fondo.jpg"));
        btHostImg = new Texture(Gdx.files.internal("spriteAssets/hostPlay.png"));
        btJoinImg = new Texture(Gdx.files.internal("spriteAssets/joinPlay.png"));
        btExitImg = new Texture(Gdx.files.internal("spriteAssets/exit.png"));

        buttonHost = new ImageButton(new TextureRegionDrawable(new TextureRegion(btHostImg)));
        buttonHost.setX(-100);
        buttonHost.setY(500);

        buttonJoin = new ImageButton(new TextureRegionDrawable(new TextureRegion(btJoinImg)));
        buttonJoin.setX(-50);
        buttonJoin.setY(310);

        buttonExit = new ImageButton(new TextureRegionDrawable(new TextureRegion(btExitImg)));
        buttonExit.setX(0);
        buttonExit.setY(120);

        buttonHost.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //System.out.println("Host presionao xd");
                gamu.setScreen(new PlayerSelectionScreen(gamu, true));
            }
        });

        buttonJoin.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //System.out.println("Join presionao xd");
                gamu.setScreen(new IpGatherMenu(gamu));
            }
        });

        buttonExit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //dispose();
                //gamu.dispose();
                Gdx.app.exit();
            }
        });

        /*
        myTexture = new Texture(Gdx.files.internal("myTexture.png"));
        myTextureRegion = new TextureRegion(myTexture);
        myTexRegionDrawable = new TextureRegionDrawable(myTextureRegion);
        button = new ImageButton(myTexRegionDrawable);
        button.setX(0);
        button.setY(700);
        button.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                //System.out.println("Button Pressed");
                gamu.setScreen(new SubMenuScreen(gamu));
            }
        });
         */

        stage.addActor(buttonHost);
        stage.addActor(buttonJoin);
        stage.addActor(buttonExit);
        //stage.add(new TextButton("Custom Btn ", textButtonStyle));
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        //ScreenUtils.clear(0, 0, 0.2f, 1);
        Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1.f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        cam.update();

        gamu.sBatch.begin();
        //gamu.font.draw(gamu.sBatch, "tumadre", 20 ,20);
        gamu.sBatch.draw(fondo, 0, 0);
        gamu.sBatch.end();


        gamu.mBatch.begin(cam);
        gamu.mBatch.render(obama);
        gamu.mBatch.end();

        gamu.sBatch.begin();
        gamu.sBatch.draw(titulo, 0, 760);
        gamu.sBatch.end();



        obama.transform.rotate(new Vector3(0, 1, 0), 40*delta);

        stage.draw();

        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
            ScreenUtils.clear(1,1,1,1);
            //this.dispose();   //asi no
            //gamu.dispose();   //asi tampoco
            Gdx.app.exit();     //asi me gusta xd
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

        //texturas varias
        btHostImg.dispose();
        btExitImg.dispose();
        btJoinImg.dispose();
        titulo.dispose();
    }
}


