package com.obamaracingrgb.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.utils.ArrayMap;
import com.obamaracingrgb.gui.MainMenu;
import com.obamaracingrgb.dominio.Player;

public class ObamaRGBGameClass extends Game {
    public static final int izquierda = -1920/2;
    public static final int abajo = -1080/2;


    public SpriteBatch sBatch;
    public ModelBatch mBatch;
    public BitmapFont font;
    public Model playerModels;
    public ArrayMap<String, Player.Constructor> pConstructors;

    @Override
    public void create() {
        sBatch = new SpriteBatch();
        mBatch = new ModelBatch();
        font = new BitmapFont();

        font.getData().setScale(3, 3);

        Bullet.init();
        playerModels = loadPlayerModels();
        pConstructors = loadPlayerConstructors(playerModels);

        Gdx.graphics.setWindowedMode(1080, 720);
        //Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());

        this.setScreen(new MainMenu(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        sBatch.dispose();
        mBatch.dispose();
        font.dispose();
        playerModels.dispose();
        for(Player.Constructor c : pConstructors.values()){
            c.dispose();
        }
    }


    private static Model loadPlayerModels() {
        AssetManager am = new AssetManager();
        am.load("playerModel/obamaPrisme/obama_prisme.g3db", Model.class);
        am.load("playerModel/marianoCuboid/mariano_cuboid.g3db", Model.class);
        am.load("playerModel/poliedroSanchez/poliedro_sanchez.g3db", Model.class);
        am.finishLoading(); // No queremos multithread, cargamos al iniciar

        ModelBuilder mb = new ModelBuilder();
        mb.begin();
        mb.node("obama", am.<Model>get("playerModel/obamaPrisme/obama_prisme.g3db"));
        mb.node("cuboy", am.<Model>get("playerModel/marianoCuboid/mariano_cuboid.g3db"));
        mb.node("sanchez", am.<Model>get("playerModel/poliedroSanchez/poliedro_sanchez.g3db"));
        //am.dispose();
        return mb.end();
    }
    private static ArrayMap<String, Player.Constructor> loadPlayerConstructors(Model model) {
        ArrayMap<String, Player.Constructor> res = new ArrayMap<>();
        res.put("obama", new Player.Constructor(model, "obama", new btSphereShape(0.5f),1f));
        res.put("cuboy", new Player.Constructor(model, "cuboy", new btSphereShape(0.5f),1f));
        res.put("sanchez", new Player.Constructor(model, "sanchez", new btSphereShape(0.5f),1f));
        //res.put("nekoArc", new Player.Constructor(model, "nekoArc", new btSphereShape(0.5f),0.5f));
        return res;
    }
}
