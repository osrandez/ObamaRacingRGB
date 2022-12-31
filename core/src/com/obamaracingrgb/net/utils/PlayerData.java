package com.obamaracingrgb.net.utils;

import com.badlogic.gdx.math.Vector3;

public class PlayerData { // Rezo para que esto sea serializable
    public int index;
    public Vector3 pos;
    public Vector3 linearSpd;
    // Asi me gusta

    public PlayerData(int i, Vector3 p, Vector3 spd) {
        index=i; pos=p; linearSpd=spd;
    }
    // Forzamos que tenga valores iniciales para no tener nullPointers
    // nosotros en corto: new PlayerData(null,null, xd, null);

}
