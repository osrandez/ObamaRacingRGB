package com.obamaracingrgb.net.server;

import com.badlogic.gdx.utils.Array;
import com.obamaracingrgb.dominio.Player;
import com.obamaracingrgb.game.ObamaRGBGameClass;

import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPSendThread extends Thread{
    private Array<Player> players;
    private DatagramSocket sendSocket;

    public UDPSendThread(Array<Player> players){
        this.players = players;
        try {
            sendSocket = new DatagramSocket();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {

    }
}
