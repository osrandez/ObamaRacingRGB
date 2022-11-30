package com.obama.tests;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.*;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Disposable;

import java.io.File;

public class TestRun implements ApplicationListener {
    Model playerModels;
    ArrayMap<String, TestRun.Entity.Constructor> pConstructors;
    Array<Entity> players;

    Model levelModels;
    ArrayMap<String, TestRun.Entity.Constructor> lConstructors;
    Array<Entity> level;

    ModelBatch modelBatch;
    Environment environment;
    PerspectiveCamera cam;
    CameraInputController camController;


    btCollisionConfiguration colConfig;
    btCollisionDispatcher colDispatcher;
    btBroadphaseInterface broadPhase;
    btConstraintSolver constraintSolver;
    btDynamicsWorld dynamicsWorld;

    BitmapFont font;
    SpriteBatch batch;

    static class MotionState extends btMotionState { // Sincronizar los btRigidBody con visuales
        Matrix4 transform;
        @Override
        public void getWorldTransform (Matrix4 worldTrans) {
            worldTrans.set(transform);
        }
        @Override
        public void setWorldTransform (Matrix4 worldTrans) {
            transform.set(worldTrans);
        }
    } // Sobreescribir que pasa cuando bullet mueve el objeto fisico
    static class Entity extends ModelInstance implements Disposable {
        public final btRigidBody body;
        public final MotionState motionState;
        public Entity(Model model, String node, btRigidBody.btRigidBodyConstructionInfo info) {
            super(model, node);
            motionState = new TestRun.MotionState();
            motionState.transform = transform;
            body = new btRigidBody(info);
            body.setMotionState(new btMotionState());
        }
        public Entity(Model model, btRigidBody.btRigidBodyConstructionInfo info) {
            super(model);
            motionState = new TestRun.MotionState();
            motionState.transform = transform;
            body = new btRigidBody(info);
            body.setMotionState(new btMotionState());
        }

        static class Constructor implements Disposable {
            public final Model model;
            public final String node;
            public final btCollisionShape shape;
            public final btRigidBody.btRigidBodyConstructionInfo constructionInfo;
            private static final Vector3 localInertia = new Vector3();

            public Constructor (Model model, String node, btCollisionShape shape, float mass) {
                this.model = model;
                this.node = node;
                this.shape = shape;
                if (mass > 0f)
                    shape.calculateLocalInertia(mass, localInertia);
                else
                    localInertia.set(0, 0, 0);
                this.constructionInfo = new btRigidBody.btRigidBodyConstructionInfo(mass, null, shape, localInertia);
            }
            public Entity construct () {
                if (!"".equals(node))
                    return new TestRun.Entity(model, node, constructionInfo);
                else
                    return new TestRun.Entity(model, constructionInfo);
            }
            @Override
            public void dispose () {
                shape.dispose();
                constructionInfo.dispose();
            }
        }

        @Override
        public void dispose() {
            body.dispose();
            motionState.dispose();
        }
    }

    @Override
    public void create() {
        generalInit();
        loadModels();
        initInstances();
    }

    private void loadModels() {
        File playerDir = new File("assets"+File.separator+"playerModels");
        File mapDir = new File("assets"+File.separator+"mapModels");
        String path;

        ModelBuilder pmb = new ModelBuilder();
        ModelBuilder mmb = new ModelBuilder();

        AssetManager am = new AssetManager();

        for (File pj : playerDir.listFiles()) {
            if (pj.isDirectory()) {
                path = pj.listFiles((dir, name) -> name.endsWith(".g3db"))[0].getPath();
                am.load(path, Model.class);
            }
        }

        for (File item : mapDir.listFiles()) {
            if (item.isDirectory()) {
                path = item.listFiles((dir, name) -> name.endsWith(".g3bd"))[0].getPath();
                am.load(path, Model.class);
            }
        }

        am.finishLoading();
        pmb.begin();
        mmb.begin();

        for (String ruta : am.getAssetNames()) {
            path = ruta.substring(ruta.lastIndexOf(File.separator)+1, ruta.lastIndexOf("."));
            if (ruta.contains("playerModels")) {
                pmb.node(path, am.get(ruta));
            } else if (ruta.contains("mapModels")) {
                mmb.node(path, am.get(ruta));
            }
        }

        playerModels = pmb.end();
        levelModels = mmb.end();
    }

    private void initInstances() {
        pConstructors = new ArrayMap<>();
        lConstructors = new ArrayMap<>();
        players = new Array<>();
        level = new Array<>();

        playerModels.nodes.forEach(node ->
                pConstructors.put(
                        node.id,
                        new Entity.Constructor(playerModels, node.id, new btSphereShape(2),1)
                )
        );

        lConstructors.put("ground", new Entity.Constructor(levelModels, "ground", new btSphereShape(2),0));
    }

    private void generalInit() {
        Bullet.init();

        font = new BitmapFont();
        batch = new SpriteBatch();

        modelBatch = new ModelBatch();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        cam = new PerspectiveCamera(80, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(5f, 5f, 10f);
        cam.lookAt(0, 0, 0);
        cam.update();

        camController = new CameraInputController(cam);
        Gdx.input.setInputProcessor(camController);

        // Brea dura de bullet y su dynamicWorld
        colConfig = new btDefaultCollisionConfiguration();
        colDispatcher = new btCollisionDispatcher(colConfig);
        broadPhase = new btDbvtBroadphase();
        constraintSolver = new btSequentialImpulseConstraintSolver();
        dynamicsWorld = new btDiscreteDynamicsWorld(colDispatcher, broadPhase, constraintSolver, colConfig);
        dynamicsWorld.setGravity(new Vector3(0, -9.8f, 0));

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
