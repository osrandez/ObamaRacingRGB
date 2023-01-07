package com.obamaracingrgb.dominio;

import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;

import java.util.concurrent.atomic.AtomicBoolean;


public class CollisionListener extends ContactListener {
    public CollisionListener(AtomicBoolean racismo, Player actual, AtomicBoolean ganado) {
        this.racismo = racismo;
        heGanado = ganado;
        this.actual = actual;
    }
    AtomicBoolean racismo;
    AtomicBoolean heGanado;
    Player actual;

    @Override
    public boolean onContactAdded(int userValue0, int partId0, int index0,
                                  int userValue1, int partId1, int index1) {
            if (userValue0 == actual.body.getUserValue() || userValue1 == actual.body.getUserValue()) {
                heGanado.set(true); // Comprobar en todas las maquinas si ha ganao o no
                racismo.set(false); // Cerrar puertos
            }

            return true;
    }
}
