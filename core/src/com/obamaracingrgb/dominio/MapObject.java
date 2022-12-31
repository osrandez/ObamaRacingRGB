package com.obamaracingrgb.dominio;

import bulletUtils.BulletFlags;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.Disposable;

public class MapObject extends ModelInstance implements Disposable {
    public btRigidBody body;
    public MotionState motionState;

    public MapObject(Model model, btRigidBody.btRigidBodyConstructionInfo info) {
        super(model);
        motionState = new MotionState();
        motionState.transform = transform;
        this.body = new btRigidBody(info);
        body.setMotionState(motionState);

        //Parametros propios del suelo xd
        this.body.setCollisionFlags(this.body.getCollisionFlags()
                | btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT);
        this.body.setContactCallbackFlag(BulletFlags.GROUND);
        this.body.setContactCallbackFilter(0);
        this.body.setActivationState(Collision.DISABLE_DEACTIVATION);
    }
    public MapObject(Model model, String n, btRigidBody.btRigidBodyConstructionInfo info) {
        super(model,n);
        motionState = new MotionState();
        motionState.transform = transform;
        this.body = new btRigidBody(info);
        body.setMotionState(motionState);

        this.body.setCollisionFlags(this.body.getCollisionFlags()
                | btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT);
        this.body.setContactCallbackFlag(BulletFlags.GROUND);
        this.body.setContactCallbackFilter(0);
        this.body.setActivationState(Collision.DISABLE_DEACTIVATION);
    }


    public static class Constructor implements Disposable {
        public final Model model;
        public final String node;
        public final btCollisionShape shape;
        public final btRigidBody.btRigidBodyConstructionInfo constructionInfo;
        private static final Vector3 localInertia = new Vector3();

        public Constructor (Model model, String node, btCollisionShape shape) {
            this.model = model;
            this.node = node;
            this.shape = shape;
            localInertia.set(0, 0, 0);
            this.constructionInfo = new btRigidBody.btRigidBodyConstructionInfo(0, null, shape, localInertia);
        }
        public Constructor (Model model, btCollisionShape shape) {
            this.model = model;
            this.node = "";
            this.shape = shape;
            localInertia.set(0, 0, 0);
            this.constructionInfo = new btRigidBody.btRigidBodyConstructionInfo(0, null, shape, localInertia);
        }
        public MapObject construct () {
            if (!"".equals(node))
                return new MapObject(model, node, constructionInfo);
            else
                return new MapObject(model, constructionInfo);
        }
        @Override
        public void dispose () {
            shape.dispose();
            constructionInfo.dispose();
        }
    } // Factoria II: electric-bogaloo

    @Override
    public void dispose() {
        model.dispose();
        body.dispose();
        motionState.dispose();
    }
}
