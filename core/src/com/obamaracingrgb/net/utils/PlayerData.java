package com.obamaracingrgb.net.utils;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ByteArray;

import java.nio.charset.StandardCharsets;

public class PlayerData {
    public int index;
    public Vector3 pos;
    public Vector3 linearSpd;

    public PlayerData(int i, Vector3 p, Vector3 spd) {
        index=i; pos=p; linearSpd=spd;
    }

    public byte[] serialize() {
        return (index + "|" +
                pos.x + "-" + pos.y + "-" + pos.z + "|" +
                linearSpd.x + "-" + linearSpd.y + "-" + linearSpd.z).getBytes(StandardCharsets.UTF_8);
    }

    public static PlayerData deserialize(byte[] n) {
        String line = new String(n, StandardCharsets.UTF_8); // Oskar grande te chupo la gonada masculina izquierda
        String[] args = line.split("\\|");
        int index = Integer.parseInt(args[0]);
        String[] aux = args[1].split("-");
        Vector3 pos = new Vector3(Float.parseFloat(aux[0]), Float.parseFloat(aux[1]), Float.parseFloat(aux[2]));
        aux = args[2].split("-");
        Vector3 linearSpd = new Vector3(Float.parseFloat(aux[0]), Float.parseFloat(aux[1]), Float.parseFloat(aux[2]));
        return new PlayerData(index, pos, linearSpd);
    }

}
