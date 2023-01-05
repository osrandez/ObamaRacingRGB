package com.obamaracingrgb.net.client;

import com.badlogic.gdx.utils.Array;
import com.obamaracingrgb.dominio.Player;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPClientRecieveThread extends Thread{
    private Array<Player> players;
    private DatagramSocket recieveSocket;
    private int index;
    public int lPort;

    public UDPClientRecieveThread(Array<Player> players, int index){
        this.players = players;
        this.index = index;
        try {
            recieveSocket = new DatagramSocket();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        lPort = recieveSocket.getLocalPort();
    }

    @Override
    public void run() {
        DatagramPacket paketPhoenix;
        while(!this.isInterrupted()){

        }
    }
}
