package com.obamaracingrgb.dominio;

import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;

import java.util.concurrent.atomic.AtomicBoolean;


public class CollisionListener extends ContactListener {
    public CollisionListener(AtomicBoolean racismo, Player actual) {
        this.racismo = racismo;
        heGanado = new AtomicBoolean(false);
        this.actual = actual;
    }
    AtomicBoolean racismo;
    AtomicBoolean heGanado;
    Player actual;

    @Override
    public boolean onContactAdded(btCollisionObject colObj0, int partId0, int index0, boolean match0,
            btCollisionObject colObj1, int partId1, int index1, boolean match1) {
        if (colObj0.checkCollideWith(actual.body) || colObj1.checkCollideWith(actual.body)){
            heGanado.set(true);
            actual.body.setWorldTransform(actual.body.getWorldTransform().setToTranslation(0,-10,0));
        }
        racismo.set(false);

        // Ir a final
        // todo oskar el puto lo hace


        return true;
    }
}
