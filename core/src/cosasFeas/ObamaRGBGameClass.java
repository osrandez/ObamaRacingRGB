/*
* Copia de seguridad de la clase
* Este archivo no se usa
* */

package cosasFeas;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.ScreenUtils;


public class ObamaRGBGameClass extends Game {
	public PerspectiveCamera cam;
	public CameraInputController camController;
	public ModelBatch modelBatch;
	public SpriteBatch batch;
	public AssetManager assets;
	public Array<ModelInstance> instances = new Array<>();
	public Environment environment;
	public boolean loading;
	BitmapFont font;

	@Override
	public void create () {
		font = new BitmapFont();
		batch = new SpriteBatch();
		modelBatch = new ModelBatch();
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

		assets = new AssetManager();
		assets.load("playerModel/obamaPrisme/obama_prisme.g3db", Model.class);
		assets.load("playerModel/poliedroSanchez/poliedro_sanchez.g3db", Model.class);
		assets.load("playerModel/marianoCuboid/mariano_cuboid.g3db", Model.class);
		assets.load("cancer.g3db", Model.class);
		loading = true;
	}

	private void doneLoading() {
		Model obama = assets.get("playerModel/obamaPrisme/obama_prisme.g3db", Model.class, true);
		Model cuboy = assets.get("playerModel/marianoCuboid/mariano_cuboid.g3db", Model.class, true);
		Model sanchez=assets.get("playerModel/poliedroSanchez/poliedro_sanchez.g3db", Model.class, true);
		Model cancer=assets.get("cancer.g3db", Model.class, true);
		ModelInstance shipInstance=null;
		for (int x = -60; x <= 60; x += 3f) {
			for (int z = -60; z <= 60; z += 3) {
				/*
				switch(Math.abs((x+z)%9)) {
					case 0 -> shipInstance = new ModelInstance(obama);
					case 3 -> shipInstance = new ModelInstance(cuboy);
					case 6 -> shipInstance = new ModelInstance(sanchez);
				}*/
				shipInstance = new ModelInstance(cancer);
				shipInstance.transform.setToTranslation(x, 0, z);
				instances.add(shipInstance);
			}
		}
		loading = false;
	}


	float aux = 0;
	@Override
	public void render () {
		if (loading && assets.update())
			doneLoading();

		final float delta = Math.min(1f/30f, Gdx.graphics.getDeltaTime());
		camController.update();

		visCount = 0;



		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		//Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0));

		modelBatch.begin(cam);
		//modelBatch.render(instances, environment);
		for(ModelInstance xd : instances){
			if(isVisible(cam, xd)){
				modelBatch.render(xd, environment);
				visCount++;
			}
		}
		modelBatch.end();
		batch.begin();
		font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 20, 20);
		font.draw(batch, "Objects drawn: " + visCount, 100, 20);
		batch.end();


		float wave;
		float offset = 0;
		for (ModelInstance mi : instances) {
			mi.transform.rotate(new Vector3(0,1,0), delta*100);
			Vector3 xd = mi.transform.getTranslation(new Vector3(0,0,0));
			wave = (float) (2*Math.sin((offset+=5)+aux/0.3));
			mi.transform.setTranslation(xd.x,wave,xd.z);

		}
		aux+=delta;
	}

	private int visCount;
	private Vector3 position = new Vector3();
	protected boolean isVisible(final Camera cam, final ModelInstance instance) {
		instance.transform.getTranslation(position);
		return cam.frustum.pointInFrustum(position);
	}

	@Override
	public void dispose () {
		batch.dispose();
		modelBatch.dispose();
		instances.clear();
		assets.dispose();
		font.dispose();
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
}