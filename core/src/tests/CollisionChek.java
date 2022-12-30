package tests;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.utils.Array;


public class CollisionChek implements ApplicationListener {
    PerspectiveCamera cam;
    CameraInputController camController;
    ModelBatch modelBatch;
    Array<ModelInstance> instances;
    Environment environment;

    Model model;
    ModelInstance suelo;
    ModelInstance bola;
    boolean collision;

    btCollisionShape groundShape;
    btCollisionShape ballShape;

    btCollisionObject groundObject;
    btCollisionObject ballObject;

    btCollisionConfiguration collisionConfig;
    btDispatcher dispatcher;


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
        mb.part("box", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.RED)))
                .box(5f, 1f, 5f);
        mb.node().id = "ball";
        mb.part("sphere", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.GREEN)))
                .sphere(1f, 1f, 1f, 10, 10);
        model = mb.end();

        suelo = new ModelInstance(model, "ground");
        bola = new ModelInstance(model, "ball");
        bola.transform.setToTranslation(0, 9f, 0);

        instances = new Array<>();
        instances.add(suelo);
        instances.add(bola);

        ballShape = new btSphereShape(0.5f);
        groundShape = new btBoxShape(new Vector3(2.5f, 0.5f, 2.5f));

        groundObject = new btCollisionObject();
        groundObject.setCollisionShape(groundShape);
        groundObject.setWorldTransform(suelo.transform);

        ballObject = new btCollisionObject();
        ballObject.setCollisionShape(ballShape);
        ballObject.setWorldTransform(bola.transform);

        collisionConfig = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(collisionConfig);
    }

    @Override
    public void resize(int width, int height) {

    }

    private boolean collisionCheck() {
        CollisionObjectWrapper co0 = new CollisionObjectWrapper(ballObject);
        CollisionObjectWrapper co1 = new CollisionObjectWrapper(groundObject);

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
    public void render() {
        final float delta = Math.min(1f/30f, Gdx.graphics.getDeltaTime());
        camController.update();

        Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1.f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        if (!collision) {
            bola.transform.translate(0f, -delta, 0f);
            ballObject.setWorldTransform(bola.transform);

            collision = collisionCheck();
        }

        modelBatch.begin(cam);
        modelBatch.render(instances, environment);
        modelBatch.end();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        groundObject.dispose();
        groundShape.dispose();

        ballObject.dispose();
        ballShape.dispose();

        dispatcher.dispose();
        collisionConfig.dispose();

        modelBatch.dispose();
        model.dispose();
    }
}
