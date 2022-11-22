package com.obama.game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Disposable;

public class PhysTest implements ApplicationListener {
    static class Objeto extends ModelInstance implements Disposable{
        btCollisionObject btColObj;
        boolean moving;
        static class Constructor implements Disposable{
            private final Model model;
            private final String node;
            private final btCollisionShape cShape;
            public Constructor(Model m, String n, btCollisionShape c) {
                model=m;
                node=n;
                cShape=c;
            }
            public Constructor(Model m, btCollisionShape c) {
                model=m;
                node=null;
                cShape=c;
            }

            public Objeto construct() {
                if (node==null)
                    return new Objeto(model, cShape);
                else
                    return new Objeto(model, node, cShape);
            }

            @Override
            public void dispose() {
                cShape.dispose();
            }
        }

        public Objeto(Model model, String node, btCollisionShape shape) {
            super(model, node);
            btColObj = new btCollisionObject();
            btColObj.setCollisionShape(shape);
        }

        public Objeto(Model model, btCollisionShape shape) {
            super(model);
            btColObj = new btCollisionObject();
            btColObj.setCollisionShape(shape);
        }

        @Override
        public void dispose() {
            btColObj.dispose();
        }
    }
    Array<Objeto> instances;
    ArrayMap<String, Objeto.Constructor> constructors;

    PerspectiveCamera cam;
    CameraInputController camController;
    ModelBatch modelBatch;
    Environment environment;
    Model model;

    btCollisionConfiguration colConfig;
    btCollisionDispatcher dispatcher;

    private boolean collisionCheck(btCollisionObject c1, btCollisionObject c2) {
        CollisionObjectWrapper co0 = new CollisionObjectWrapper(c1);
        CollisionObjectWrapper co1 = new CollisionObjectWrapper(c2);

        btCollisionAlgorithmConstructionInfo ci = new btCollisionAlgorithmConstructionInfo();
        ci.setDispatcher1(dispatcher);
        btCollisionAlgorithm algorithm = new btSphereBoxCollisionAlgorithm(null, ci, co0.wrapper, co1.wrapper, false);

        btDispatcherInfo info = new btDispatcherInfo();
        btManifoldResult result = new btManifoldResult(co0.wrapper, co1.wrapper);

        algorithm.processCollision(co0.wrapper, co1.wrapper, info, result);

        boolean r = result.getPersistentManifold().getNumContacts() > 0;

        result.dispose();
        info.dispose();
        algorithm.dispose();
        ci.dispose();
        co1.dispose();
        co0.dispose();

        return r;
    }

    @Override
    public void create() {
        Bullet.init();
        modelBatch = new ModelBatch();

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(3f, 7f, 10f);
        cam.lookAt(0, 4f, 0);
        cam.update();

        camController = new CameraInputController(cam);
        Gdx.input.setInputProcessor(camController);



        ModelBuilder mb = new ModelBuilder();
        mb.begin();
        mb.node().id = "ground";
        mb.part("ground", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.RED)))
                .box(5f, 1f, 5f);
        mb.node().id = "sphere";
        mb.part("sphere", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.GREEN)))
                .sphere(1f, 1f, 1f, 10, 10);
        mb.node().id = "box";
        mb.part("box", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.BLUE)))
                .box(1f, 1f, 1f);
        mb.node().id = "cone";
        mb.part("cone", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.YELLOW)))
                .cone(1f, 2f, 1f, 10);
        mb.node().id = "capsule";
        mb.part("capsule", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.CYAN)))
                .capsule(0.5f, 2f, 10);
        mb.node().id = "cylinder";
        mb.part("cylinder", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.MAGENTA)))
                .cylinder(1f, 2f, 1f, 10);
        model = mb.end();

        constructors = new ArrayMap<>(String.class, Objeto.Constructor.class);
        constructors.put("ground", new Objeto.Constructor(model, "ground", new btBoxShape(new Vector3(2.5f, 0.5f, 2.5f))));
        constructors.put("sphere", new Objeto.Constructor(model, "sphere", new btSphereShape(0.5f)));
        constructors.put("box", new Objeto.Constructor(model, "box", new btBoxShape(new Vector3(0.5f, 0.5f, 0.5f))));
        constructors.put("cone", new Objeto.Constructor(model, "cone", new btConeShape(0.5f, 2f)));
        constructors.put("capsule", new Objeto.Constructor(model, "capsule", new btCapsuleShape(.5f, 1f)));
        //constructors.put("cylinder", new Objeto.Constructor(model, "cylinder", new btCylinderShape(new Vector3(.5f, 1f, .5f))));
        AssetManager am = new AssetManager();
        am.load("playerModel/obamaPrisme/obama_prisme.g3db", Model.class);
        while (!am.update()) {/*esperar carga*/}
        constructors.put("obama", new Objeto.Constructor(am.get("playerModel/obamaPrisme/obama_prisme.g3db", Model.class), new btSphereShape(0.5f)));

        instances = new Array<>();
        instances.add(constructors.get("ground").construct());

        colConfig = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(colConfig);
    }

    @Override
    public void resize(int width, int height) {

    }

    int spawnTimer=120;

    @Override
    public void render() {
        final float delta = Math.min(1/30f, Gdx.graphics.getDeltaTime());

        camController.update();

        Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1.f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);



        for (Objeto xd : instances) {
            if (xd.moving) {
                xd.transform.trn(0,-delta,0);
                xd.btColObj.setWorldTransform(xd.transform);
                if (collisionCheck(xd.btColObj, instances.get(0).btColObj)){
                    xd.moving=false;
                }
            }
        }

        modelBatch.begin(cam);
        modelBatch.render(instances, environment);
        modelBatch.end();

        if ((spawnTimer-=1) < 1) {
            spawn();
            spawnTimer = 180;
        }
    }

    public void spawn() {
        Objeto obj = constructors.values[1+MathUtils.random(constructors.size-2)].construct();
        obj.moving = true;
        obj.transform.setFromEulerAngles(MathUtils.random(360f), MathUtils.random(360f), MathUtils.random(360f));
        obj.transform.trn(MathUtils.random(-2.5f, 2.5f), 9f, MathUtils.random(-2.5f, 2.5f));
        obj.btColObj.setWorldTransform(obj.transform);
        instances.add(obj);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        instances.forEach(Objeto::dispose);
        instances.clear();

        constructors.values().forEach(Objeto.Constructor::dispose);
        constructors.clear();

        dispatcher.dispose();
        colConfig.dispose();

        modelBatch.dispose();
        model.dispose();
    }
}
