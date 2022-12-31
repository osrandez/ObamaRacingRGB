package com.obamaracingrgb.net.client;

import com.badlogic.gdx.utils.Array;
import com.obamaracingrgb.dominio.Player;

import java.net.DatagramSocket;
import java.net.SocketException;



public abstract class UDPClient implements Runnable {}

class UDPInput extends UDPClient {
    Array<Player> playerList; // Referencia a la lista de players que renderiza
    DatagramSocket input;
    public UDPInput(Array<Player> xd) throws SocketException {
        input = new DatagramSocket();
        playerList = xd;
    }

    @Override
    public void run() {
        // Recibir lista de playerData por udp y actualizar
    }
}

class UDPOutput extends UDPClient {
    DatagramSocket output;
    Player self; // Referencia directa a nuestro player
    public UDPOutput(Player p) throws SocketException {
        output = new DatagramSocket();
        self=p;
    }

    @Override
    public void run() {
        // Enviar un playerData por udp

    }
}