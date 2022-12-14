package com.obamaracingrgb.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class TestSerio1 extends Game {
    Array<Disposable> disposables = new Array<>();

    PerspectiveCamera cam;
    CameraInputController camController;

    Environment environment;
    ModelBatch modelBatch;

    AssetManager am;



    @Override
    public void create() {
        Bullet.init();
        am = new AssetManager();
        am.load("playerModel/poliedroSanchez/poliedro_sanchez.g3db", Model.class);

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(1f, 1f, 1f);
        cam.lookAt(0,0,0);
        cam.near = 1f;
        cam.far = 1000f;
        cam.update();

        camController = new CameraInputController(cam);
        Gdx.input.setInputProcessor(camController);

        modelBatch = new ModelBatch();
        disposables.add(modelBatch);

        while (!am.update()) {} // xd

        //PlayerEntity sanche = new PlayerEntity(am.<Model>get("playerModel/poliedroSanchez/poliedro_sanchez.g3db"));
        //players.add(sanche);

        ModelBuilder mb = new ModelBuilder();
        mb.begin();
        mb.node().id = "ground";
        mb.part("box", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.RED)))
                .box(200f, 0.1f, 200f);
        mb.node().id = "ball";
        mb.part("sphere", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.GREEN)))
                .sphere(1f, 1f, 1f, 10, 10);
        Model model = mb.end();
        disposables.add(model);

        /*
        FloorEntity suelo =
                new FloorEntity(
                        model,
                        new btBoxShape(new Vector3(200f, 0.1f, 200f)),
                        "ground"
                );
        floorConstruction.add(suelo);
        */


        // disposables.addAll(players);
        // disposables.addAll(floorConstruction);
    }


    public volatile static float delta;
    @Override
    public void render() {
        delta = Math.min(1/30f,Gdx.graphics.getDeltaTime());

        camController.update();

        Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1.f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        modelBatch.begin(cam);
        //modelBatch.render(floorConstruction, environment);
        //modelBatch.render(players, environment);
        modelBatch.end();
    }

    @Override
    public void dispose() {
        for (Disposable d : disposables)
            d.dispose();
    }
}

class Entity extends ModelInstance implements Disposable {
    Array<Disposable> disposables;
    public Entity(Model model) {
        super(model);
        disposables = new Array<>();
        disposables.add(model);
    }
    public Entity(Model model, String... rootNodeIds) {
        super(model, rootNodeIds);
        disposables = new Array<>();
        disposables.add(model);
    }

    public void dispose() {
        for (Disposable d : disposables)
            d.dispose();
    }
}


abstract class HitboxEntity extends Entity{
    Array<Disposable> disposables;
    btCollisionObject hitbox;
    public HitboxEntity(Model model) {
        super(model);
    }
    public HitboxEntity(Model model, String... rootNodeIds) {
        super(model, rootNodeIds);
    }
}

class BolaEntity extends HitboxEntity{
    public BolaEntity(Model model){
        super(model);
    }
}