package com.obamaracingrgb.dominio;

import bulletUtils.BulletFlags;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.Disposable;
import com.obamaracingrgb.net.utils.PlayerData;

public class Player extends ModelInstance implements Disposable {
    public final btRigidBody body;
    public final MotionState motionState; // Sincronisar matrices4
    public String name;
    public Player(Model model, btRigidBody.btRigidBodyConstructionInfo info) {
        super(model);
        motionState = new MotionState();
        motionState.transform = transform;
        this.body = new btRigidBody(info);
        body.setMotionState(motionState);
        body.setCollisionFlags(body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_NO_CONTACT_RESPONSE);
        body.setContactCallbackFlag(BulletFlags.OBJECT);
        body.setContactCallbackFilter(BulletFlags.GHOST);
    } // Para modelos unicos
    public Player(Model model, String n, btRigidBody.btRigidBodyConstructionInfo info) {
        super(model,n);
        name = n;
        motionState = new MotionState();
        motionState.transform = transform;
        this.body = new btRigidBody(info);
        body.setMotionState(motionState);
        body.setCollisionFlags(body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
        body.setContactCallbackFlag(BulletFlags.OBJECT);
        body.setContactCallbackFilter(0);
    } // Para meshes

    public float accelFactor() {
        return (1/body.getLocalInertia().len2())/10;
    }

    public static class Constructor implements Disposable {
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

    @Override
    public String toString() {
        return super.toString();
    }

    public PlayerData getNetData(int n) {
        return new PlayerData(n, body.getWorldTransform().getTranslation(new Vector3()).cpy(), body.getLinearVelocity().cpy());
    }

    public void consumeNetData(PlayerData data) {
        body.setWorldTransform(body.getWorldTransform().setToTranslation(data.pos));
        body.setLinearVelocity(data.linearSpd);
    }
}
