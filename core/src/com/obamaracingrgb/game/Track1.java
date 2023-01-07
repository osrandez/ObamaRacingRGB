package com.obamaracingrgb.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.btConstraintSolver;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.obamaracingrgb.dominio.CollisionListener;
import com.obamaracingrgb.dominio.GhostObject;
import com.obamaracingrgb.dominio.MapObject;
import com.obamaracingrgb.dominio.Player;
import com.obamaracingrgb.gui.RaceEndScreen;

import java.util.concurrent.atomic.AtomicBoolean;

public class Track1 implements Screen {
    Model model;
    public Array<Player> players;
    Array<MapObject> suelosVarios;
    ArrayMap<String, MapObject.Constructor> mapConstructors;
    ArrayMap<String, Player.Constructor> pConstructors;
    public Player actualPlayer;
    AssetManager am;
    btDynamicsWorld dynamicsWorld;

    PerspectiveCamera cam;
    ModelBatch modelBatch;
    Environment environment;
    btCollisionConfiguration colConfig;
    btCollisionDispatcher dispatcher;
    BitmapFont font;
    SpriteBatch batch;
    btBroadphaseInterface broadPhase;

    btConstraintSolver constraintSolver;
    ObamaRGBGameClass gamu;
    CollisionListener escuchadorFinal;
    AtomicBoolean racismo;
    AtomicBoolean heGanado;
    GhostObject meta;

    public Track1(ObamaRGBGameClass game, Array<Player> yogadores, Player actual, AtomicBoolean racismoRGB) {
        gamu=game;
        this.players = yogadores;

        racismo = racismoRGB;

        this.actualPlayer = actual;

        heGanado = new AtomicBoolean(false);
        escuchadorFinal= new CollisionListener(racismo, actualPlayer, heGanado);

        suelosVarios= new Array<>();
        mapConstructors = new ArrayMap<>();
        create();
    }

    public void create() {
        initThingos();
        loadModels();
        loadConstructors();
        buildLevel();
        meta = new GhostObject(model, "sphere", new btSphereShape(0.5f));
        meta.body.setUserValue(-1);
        dynamicsWorld.addCollisionObject(meta.body);

        for (int i=0; i<players.size;i++) {
            players.get(i).body.setUserValue(i);
        }
    }

    private void loadModels() {
        am = new AssetManager();
        am.load("suelo.g3db", Model.class);
        am.finishLoading();

        ModelBuilder mb = new ModelBuilder();
        mb.begin();
        mb.node().id = "ground";
        mb.part("ground", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.RED)))
                .box(400f, 0.2f, 400f);
        mb.node().id = "sphere";
        mb.part("sphere", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.RED)))
                .sphere(1f, 1f, 1f, 10, 10);
        mb.node().id = "box";
        mb.part("box", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.BLUE)))
                .box(1f, 1f, 1f);
        mb.node().id = "cone";
        mb.part("cone", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.YELLOW)))
                .cone(1f, 2f, 1f, 10);
        mb.node().id = "capsule";
        mb.part("capsule", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.CYAN)))
                .capsule(0.5f, 2f, 10);
        mb.node().id = "cylinder";
        mb.part("cylinder", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.MAGENTA)))
                .cylinder(1f, 2f, 1f, 10);
        mb.node("carreta", am.<Model>get("suelo.g3db"));
        model = mb.end();
    }

    private void loadConstructors() {
        mapConstructors.put("sueloCuadrado", new MapObject.Constructor(model, "ground", new btBoxShape(new Vector3(200f, 0.1f, 200f))));
        mapConstructors.put("sphere", new MapObject.Constructor(model, "sphere", new btSphereShape(0.5f)));
        mapConstructors.put("box", new MapObject.Constructor(model, "box", new btBoxShape(new Vector3(0.5f, 0.5f, 0.5f))));
        mapConstructors.put("cone", new MapObject.Constructor(model, "cone", new btConeShape(0.5f, 2f)));
        mapConstructors.put("capsule", new MapObject.Constructor(model, "capsule", new btCapsuleShape(.5f, 1f)));
        mapConstructors.put("cylinder", new MapObject.Constructor(model, "cylinder", new btCylinderShape(new Vector3(.5f, 1f, .5f))));
        mapConstructors.put("carreta", new MapObject.Constructor(model, "carreta", new btBoxShape(new Vector3(200f, 0.4f, 200f))));
    }

    private void initThingos() {
        font = gamu.font;
        batch = gamu.sBatch;
        modelBatch = gamu.mBatch;

        // Luz ambiental xd
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));


        // Camara y controlador
        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(actualPlayer.transform.getTranslation(new Vector3()).mulAdd(new Vector3(0,10,-15),1));
        cam.lookAt(actualPlayer.transform.getTranslation(new Vector3()));
        cam.far = 1000f;
        cam.update();

        // Brea dura de bullet y su dynamicWorld
        colConfig = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(colConfig); // Lanzador de eventos :)
        broadPhase = new btDbvtBroadphase(); // Para aproximar la verga de colisiones sin cagar mucho rendimiento
        constraintSolver = new btSequentialImpulseConstraintSolver(); // tengo que mirar esto de nuevo
        dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, broadPhase, constraintSolver, colConfig); // como el collisionWorld pero gucci
        dynamicsWorld.setGravity(new Vector3(0, -30f, 0));
    }

    private void buildLevel(){
        MapObject object = mapConstructors.get("carreta").construct();

        suelosVarios.add(object);
        dynamicsWorld.addRigidBody(object.body);

        object.body.setWorldTransform(object.body.getWorldTransform().rotate(0,1,0,90).setToTranslation(20,0,0));

        actualPlayer.transform.setFromEulerAngles(MathUtils.random(360f), MathUtils.random(360f), MathUtils.random(360f));
        actualPlayer.transform.trn(MathUtils.random(-2.5f, 2.5f), 5f, MathUtils.random(-2.5f, 2.5f));
        actualPlayer.transform.trn(0,0,-45);
        actualPlayer.body.proceedToTransform(actualPlayer.transform);

        for (int i=0; i<players.size; i++) {
            dynamicsWorld.addRigidBody(players.get(i).body);
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void render(final float delta) {
        //camController.update();
        cam.position.set(actualPlayer.transform.getTranslation(new Vector3()).mulAdd(new Vector3(0,13,-15),1));
        cam.update();

        // Updateamos luz
        Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1.f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        // Updatear aki fisicas
        Vector3 impulse = new Vector3(1,0,0).rotate(-90,0,1,0);
        impulse = new Vector3().mulAdd(impulse,3*delta*actualPlayer.accelFactor());

        if (Gdx.input.isKeyPressed(Input.Keys.UP))
            actualPlayer.body.applyCentralImpulse(impulse);
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
            actualPlayer.body.applyCentralImpulse(impulse.cpy().rotate(new Vector3(0,1,0),180));
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
            actualPlayer.body.applyCentralImpulse(impulse.cpy().rotate(new Vector3(0,1,0),90));
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            actualPlayer.body.applyCentralImpulse(impulse.cpy().rotate(new Vector3(0,1,0),270));
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
            actualPlayer.body.applyCentralImpulse(new Vector3(0,10,0));
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            actualPlayer.body.setWorldTransform(actualPlayer.body.getWorldTransform().setToTranslation(0,5,-45));
            actualPlayer.body.setLinearVelocity(new Vector3(0,0,0));
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            Vector3 xd = actualPlayer.body.getLinearVelocity().cpy();
            actualPlayer.body.applyCentralImpulse(new Vector3().mulAdd(xd,-delta*2.5f));
        }

        dynamicsWorld.stepSimulation(delta, 5, 1f/60f);

        modelBatch.begin(cam);
        modelBatch.render(suelosVarios, environment);
        modelBatch.render(players, environment);
        modelBatch.render(meta, environment);
        modelBatch.end();

        batch.begin();
        font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 20, 20);
        batch.end();

        if (!racismo.get()) {
            this.gamu.setScreen(new RaceEndScreen(this.gamu, this.heGanado));
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
        for(Player p : players){
            p.dispose();
        }

        for(MapObject p : suelosVarios){
            p.dispose();
        }
        suelosVarios.clear();

        dispatcher.dispose();
        colConfig.dispose();
        broadPhase.dispose();

        model.dispose();
    }
}
