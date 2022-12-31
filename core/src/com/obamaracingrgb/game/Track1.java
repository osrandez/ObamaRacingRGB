package com.obamaracingrgb.game;

import bulletUtils.BulletFlags;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
import com.obamaracingrgb.dominio.MapObject;
import com.obamaracingrgb.dominio.Player;

public class Track1 extends Game {
    Model model;
    public Array<Player> players;
    Array<MapObject> suelosVarios;
    ArrayMap<String, MapObject.Constructor> mapConstructors;
    ArrayMap<String, Player.Constructor> pConstructors;
    public Player actualPlayer;
    AssetManager am;
    btDynamicsWorld dynamicsWorld;

    PerspectiveCamera cam;
    CameraInputController camController;
    ModelBatch modelBatch;
    Environment environment;
    btCollisionConfiguration colConfig;
    btCollisionDispatcher dispatcher;
    BitmapFont font;
    SpriteBatch batch;
    btBroadphaseInterface broadPhase;

    btConstraintSolver constraintSolver;

    public Track1(Array<Player> yogadores, Player yogadorCliente) {
        super();
        if (yogadores!=null)
            this.players = yogadores;
        else
            this.players = new Array<>();

        this.actualPlayer = yogadorCliente;

        suelosVarios= new Array<>();
        mapConstructors = new ArrayMap<>();
    }

    public void create() {
        initThingos();
        loadMapAssets();
        loadModels();
        loadConstructors();
        buildLevel();
    }




    private void loadMapAssets() {
        am = new AssetManager();
        // am.load();



    }

    private void loadModels() {
        while (!am.update()) {}

        ModelBuilder mb = new ModelBuilder();
        mb.begin();
        mb.node().id = "ground";
        mb.part("ground", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.RED)))
                .box(400f, 0.2f, 400f);
        mb.node().id = "sphere";
        mb.part("sphere", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.GREEN)))
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
        model = mb.end();
    }

    private void loadConstructors() {
        mapConstructors = new ArrayMap<>();

        mapConstructors.put("sueloCuadrado", new MapObject.Constructor(model, "ground", new btBoxShape(new Vector3(200f, 0.1f, 200f))));
        mapConstructors.put("sphere", new MapObject.Constructor(model, "sphere", new btSphereShape(0.5f)));
        mapConstructors.put("box", new MapObject.Constructor(model, "box", new btBoxShape(new Vector3(0.5f, 0.5f, 0.5f))));
        mapConstructors.put("cone", new MapObject.Constructor(model, "cone", new btConeShape(0.5f, 2f)));
        mapConstructors.put("capsule", new MapObject.Constructor(model, "capsule", new btCapsuleShape(.5f, 1f)));
        mapConstructors.put("cylinder", new MapObject.Constructor(model, "cylinder", new btCylinderShape(new Vector3(.5f, 1f, .5f))));

        pConstructors = new ArrayMap<>();

        pConstructors.put("sphere", new Player.Constructor(model, "sphere", new btSphereShape(0.5f),1f));

    }

    private void initThingos() {
        // Arrancamos bullet y los batchs
        //Bullet.init();
        font = new BitmapFont();
        batch = new SpriteBatch();
        modelBatch = new ModelBatch();

        // Luz ambiental xd
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));


        // Camara y controlador
        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(3f, 7f, 10f);
        cam.lookAt(0, 4f, 0);
        cam.far = 1000f;
        cam.update();

        camController = new CameraInputController(cam);
        Gdx.input.setInputProcessor(camController);

        // Brea dura de bullet y su dynamicWorld
        colConfig = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(colConfig); // Lanzador de eventos :)
        broadPhase = new btDbvtBroadphase(); // Para aproximar la verga de colisiones sin cagar mucho rendimiento
        constraintSolver = new btSequentialImpulseConstraintSolver(); // tengo que mirar esto de nuevo
        dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, broadPhase, constraintSolver, colConfig); // como el collisionWorld pero gucci
        dynamicsWorld.setGravity(new Vector3(0, -30f, 0));
    }

    private void buildLevel(){
        MapObject object = mapConstructors.get("sueloCuadrado").construct();
        suelosVarios.add(object);
        dynamicsWorld.addRigidBody(object.body);


        Player obj = pConstructors.get("sphere").construct();
        obj.transform.setFromEulerAngles(MathUtils.random(360f), MathUtils.random(360f), MathUtils.random(360f));
        obj.transform.trn(MathUtils.random(-2.5f, 2.5f), 9f, MathUtils.random(-2.5f, 2.5f));
        obj.body.proceedToTransform(obj.transform);
        obj.body.setUserValue(players.size);
        obj.body.setCollisionFlags(obj.body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
        players.add(obj);
        dynamicsWorld.addRigidBody(obj.body);
        obj.body.setContactCallbackFlag(BulletFlags.OBJECT);
        obj.body.setContactCallbackFilter(0);
        obj.body.setFriction(20);
        actualPlayer=obj;

    }

    public void render() {
        // Tiempo entre frames, minimo por si hay penco lag a 2 fps para que funke el bullet
        final float delta = Math.min(1/30f, Gdx.graphics.getDeltaTime());

        // Movemos cam
        camController.update();

        // Updateamos luz
        Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1.f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        // Updatear aki fisicas
        Player pl = actualPlayer;
        Vector3 impulse = new Vector3();
        impulse.mulAdd(new Vector3(1,0,0).nor(),delta*pl.accelFactor());

        if (Gdx.input.isKeyPressed(Input.Keys.UP))
            pl.body.applyCentralImpulse(impulse);
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
            pl.body.applyCentralImpulse(impulse.cpy().rotate(new Vector3(0,1,0),180));
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
            pl.body.applyCentralImpulse(impulse.cpy().rotate(new Vector3(0,1,0),90));
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            pl.body.applyCentralImpulse(impulse.cpy().rotate(new Vector3(0,1,0),270));
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
            pl.body.applyCentralImpulse(new Vector3(0,10,0));
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            pl.body.setWorldTransform(pl.body.getWorldTransform().setToTranslation(0,5,0));
            pl.body.setLinearVelocity(new Vector3(0,0,0));
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            Vector3 xd = pl.body.getLinearVelocity().cpy();
            pl.body.applyCentralImpulse(new Vector3().mulAdd(xd,-delta*2.5f));
        }

        dynamicsWorld.stepSimulation(delta, 5, 1f/60f);

        modelBatch.begin(cam);
        modelBatch.render(suelosVarios, environment);
        modelBatch.render(players, environment);
        modelBatch.end();

        batch.begin();
        font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 20, 20);
        batch.end();
    }

    @Override
    public void dispose() {
        for(Player p : players){
            p.dispose();
        }
        players.clear();

        for(Player.Constructor p : pConstructors.values()){
            p.dispose();
        }
        pConstructors.clear();

        for(MapObject p : suelosVarios){
            p.dispose();
        }
        suelosVarios.clear();

        for(Player.Constructor p : pConstructors.values()){
            p.dispose();
        }
        pConstructors.clear();

        dispatcher.dispose();
        colConfig.dispose();
        broadPhase.dispose();

        modelBatch.dispose();
        model.dispose();

        batch.dispose();
        font.dispose();
    }
}
