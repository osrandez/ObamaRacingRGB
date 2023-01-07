package com.obamaracingrgb.dominio;

import bulletUtils.BulletFlags;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btGhostObject;
import com.badlogic.gdx.utils.Disposable;

public class GhostObject extends ModelInstance implements Disposable {
    public btGhostObject body;

    public GhostObject(Model model, btCollisionShape shape) {
        super(model);
        body= new btGhostObject();
        body.setCollisionFlags(body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_NO_CONTACT_RESPONSE);
        body.setCollisionShape(shape);
        body.setWorldTransform(new Matrix4().setToTranslation(10,2,10));
        transform.set(body.getWorldTransform());
        body.setContactCallbackFlag(BulletFlags.GHOST);
        body.setContactCallbackFilter(0);
        this.body.setActivationState(Collision.DISABLE_DEACTIVATION);
    }

    public GhostObject(Model model, String n, btCollisionShape shape) {
        super(model, n);
        body= new btGhostObject();
        body.setCollisionFlags(body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_NO_CONTACT_RESPONSE);
        body.setCollisionShape(shape);
        body.setWorldTransform(new Matrix4().setToTranslation(10,2,10));
        transform.set(body.getWorldTransform());
        body.setContactCallbackFlag(BulletFlags.GHOST);
        body.setContactCallbackFilter(0);
        this.body.setActivationState(Collision.DISABLE_DEACTIVATION);
    }

    @Override
    public void dispose() {
        model.dispose();
        body.dispose();
    }
}
