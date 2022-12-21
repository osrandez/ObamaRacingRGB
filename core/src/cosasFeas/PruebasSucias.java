package cosasFeas;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.*;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Disposable;


// Ensuciame esta tenemos juego
public class PruebasSucias extends Game {
    static class Player extends ModelInstance implements Disposable {
        public final btRigidBody body;
        public final MotionState motionState; // Sincronisar matrices4
        public Player(Model model, btRigidBody.btRigidBodyConstructionInfo info) {
            super(model);
            motionState = new MotionState();
            motionState.transform = transform;
            this.body = new btRigidBody(info);
            body.setMotionState(motionState);
        } // Para modelos unicos
        public Player(Model model, String n, btRigidBody.btRigidBodyConstructionInfo info) {
            super(model,n);
            motionState = new MotionState();
            motionState.transform = transform;
            this.body = new btRigidBody(info);
            body.setMotionState(motionState);
        } // Para meshes

        public float accelFactor() {
            return 2*(1/body.getLocalInertia().len2());
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
            public Constructor (Model model, btCollisionShape shape, float mass) {
                this.model = model;
                this.node = "";
                this.shape = shape;
                if (mass > 0f)
                    shape.calculateLocalInertia(mass, localInertia);
                else
                    localInertia.set(0, 0, 0);
                this.constructionInfo = new btRigidBody.btRigidBodyConstructionInfo(mass, null, shape, localInertia);
            }
            public Player construct () {
                if (!"".equals(node))
                    return new Player(model, node, constructionInfo);
                else
                    return new Player(model, constructionInfo);
            }
            @Override
            public void dispose () {
                shape.dispose();
                constructionInfo.dispose();
            }
        } // Factoria

        @Override
        public void dispose() {
            body.dispose();
            motionState.dispose();
        }
    }
    class CollisionListener extends ContactListener {
        @Override
        public boolean onContactAdded (int userValue0, int partId0, int index0, boolean match0,
                                       int userValue1, int partId1, int index1, boolean match1) {
            /*
            if (match0)
                ((ColorAttribute)instances.get(userValue0).materials.get(0).get(ColorAttribute.Diffuse)).color.set(Color.WHITE);
            if (match1)
                ((ColorAttribute)instances.get(userValue1).materials.get(0).get(ColorAttribute.Diffuse)).color.set(Color.WHITE);
            */
            return true;
        }
    } // Sobreescribir que pasa cuando hay contacto
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
    }
    Array<Player> instances;
    ArrayMap<String, Player.Constructor> constructors;
    PerspectiveCamera cam;
    CameraInputController camController;
    ModelBatch modelBatch;
    Environment environment;
    Model model;
    btCollisionConfiguration colConfig;
    btCollisionDispatcher dispatcher;
    BitmapFont font;
    SpriteBatch batch;
    btBroadphaseInterface broadPhase;
    CollisionListener contactListener;

    btDynamicsWorld dynamicsWorld;
    btConstraintSolver constraintSolver;

    float spawnTimer=1;

    final static short GROUND_FLAG = 1<<8; // Flag suelo (inmovible)
    final static short OBJECT_FLAG = 1<<9; // Flag item (tiene fisicotas guays)
    final static short ALL_FLAG = -1; // Colision con todos los items

    @Override
    public void create() {
        initThingos();
        loadModels();
        loadConstructors();


        Player object = constructors.get("ground").construct();
        object.body.setCollisionFlags(object.body.getCollisionFlags()
                | btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT); // operador de bits OR
        instances.add(object);
        dynamicsWorld.addRigidBody(object.body);
        object.body.setContactCallbackFlag(GROUND_FLAG); // IMPORTANTE ESTE ES EL FLAG QUE REPRESENTA EL OBJETO
        object.body.setContactCallbackFilter(0); // FILTRO PARA LAS CALLBACKS (0 implica que nunca se van a producir para el suelo)
        object.body.setActivationState(Collision.DISABLE_DEACTIVATION);
        // Al ser kinematico, se queda en sleeping_island
        // Podemos hacer body.activate(); // pero funciona muy mal
        // Podemos hacer body.state = btCol···.Active; // Pero perdemos rendimiento
        // De esta forma lo dejamos como "Activo" para que los objetos que colisionen con el
        // no entren en estado "awaiting_deactivation" y al final "sleeping_island" junto al suelo

        spawn();
        spawn();
    }

    // Inicializar la brea dura, mirar adentro para mas info
    private void initThingos() {
        // Arrancamos bullet y los batchs
        Bullet.init();
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
        cam.update();

        camController = new CameraInputController(cam);
        Gdx.input.setInputProcessor(camController);

        // Brea dura de bullet y su dynamicWorld
        colConfig = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(colConfig); // Lanzador de eventos :)
        broadPhase = new btDbvtBroadphase(); // Para aproximar la verga de colisiones sin cagar mucho rendimiento
        constraintSolver = new btSequentialImpulseConstraintSolver(); // tengo que mirar esto de nuevo
        dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, broadPhase, constraintSolver, colConfig); // como el collisionWorld pero gucci
        dynamicsWorld.setGravity(new Vector3(0, -10f, 0));
        contactListener = new CollisionListener(); // Sobreescribir el ContactListener por defecto

        instances = new Array<>();
    }

    // Cargar modelos con el assetManager (bloquea hasta tener el modelo terminado)
    private void loadModels() {
        AssetManager am = new AssetManager();
        am.load("playerModel/obamaPrisme/obama_prisme.g3db", Model.class);
        am.load("playerModel/marianoCuboid/mariano_cuboid.g3db", Model.class);
        am.load("playerModel/poliedroSanchez/poliedro_sanchez.g3db", Model.class);
        while (true)
            if (am.update()) break;// esperar carga

        ModelBuilder mb = new ModelBuilder();
        mb.begin();
        mb.node("obama", am.get("playerModel/obamaPrisme/obama_prisme.g3db", Model.class));
        mb.node("cuboy", am.get("playerModel/marianoCuboid/mariano_cuboid.g3db", Model.class));
        mb.node("sanchez", am.get("playerModel/poliedroSanchez/poliedro_sanchez.g3db", Model.class));
        mb.node().id = "ground";
        mb.part("ground", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.RED)))
                .box(20f, 0.2f, 20f);
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
    }

    // Carga la factoria de instancias
    private void loadConstructors() {
        constructors = new ArrayMap<>(String.class, Player.Constructor.class);
        constructors.put("ground", new Player.Constructor(model, "ground", new btBoxShape(new Vector3(10f, 0.1f, 10f)), 0f));
        constructors.put("sphere", new Player.Constructor(model, "sanchez", new btSphereShape(0.5f), 1f));
        constructors.put("box", new Player.Constructor(model, "box", new btBoxShape(new Vector3(0.5f, 0.5f, 0.5f)), 1f));
        constructors.put("cone", new Player.Constructor(model, "cone", new btConeShape(0.5f, 2f), 1f));
        constructors.put("capsule", new Player.Constructor(model, "capsule", new btCapsuleShape(.5f, 1f), 1f));
        constructors.put("cylinder", new Player.Constructor(model, "cylinder", new btCylinderShape(new Vector3(.5f, 1f, .5f)), 1f));
    }

    // Por defecto, reescalar el render a la resolucion
    @Override
    public void resize(int width, int height) {

    }

    // Mostrar por pantalla :)
    float angle, speed = 90f;
    @Override
    public void render() {
        // Tiempo entre frames, minimo por si hay penco lag a 2 fps para que funke el bullet
        final float delta = Math.min(1/30f, Gdx.graphics.getDeltaTime());

        // Movemos cam
        camController.update();

        // Updateamos luz
        Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1.f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        // Updatear aki fisicas
        Player pl = instances.get(1);
        Vector3 impulse = new Vector3();
        impulse.mulAdd(cam.direction.cpy().nor(),delta*pl.accelFactor());

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            pl.body.applyCentralImpulse(impulse);
        }





        // Esta verga sincroniza las transform gracias al MotionState
        // Esta verga produce eventos para el CollisionListener
        dynamicsWorld.stepSimulation(delta, 5, 1f/60f);

        modelBatch.begin(cam);
        modelBatch.render(instances, environment);
        modelBatch.end();

        batch.begin();
        font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 20, 20);
        batch.end();


    }

    public void spawn() {
        Player obj = constructors.get("sphere").construct();
        obj.transform.setFromEulerAngles(MathUtils.random(360f), MathUtils.random(360f), MathUtils.random(360f));
        obj.transform.trn(MathUtils.random(-2.5f, 2.5f), 9f, MathUtils.random(-2.5f, 2.5f));
        obj.body.proceedToTransform(obj.transform);
        obj.body.setUserValue(instances.size);
        obj.body.setCollisionFlags(obj.body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
        instances.add(obj);
        dynamicsWorld.addRigidBody(obj.body);
        obj.body.setContactCallbackFlag(OBJECT_FLAG);
        obj.body.setContactCallbackFilter(GROUND_FLAG);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        for(Player p : instances){
            p.dispose();
        }
        instances.clear();

        for(Player.Constructor p : constructors.values()){
            p.dispose();
        }
        constructors.clear();

        dispatcher.dispose();
        colConfig.dispose();
        contactListener.dispose();
        broadPhase.dispose();

        modelBatch.dispose();
        model.dispose();

        batch.dispose();
        font.dispose();
    }
}



