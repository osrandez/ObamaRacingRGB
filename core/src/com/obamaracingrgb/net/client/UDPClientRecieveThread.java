package com.obamaracingrgb.net.client;

import com.badlogic.gdx.utils.Array;
import com.obamaracingrgb.dominio.Player;
import com.obamaracingrgb.net.utils.PlayerData;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicBoolean;

public class UDPClientRecieveThread extends Thread{
    private Array<Player> players;
    private DatagramSocket recieveSocket;
    private int index;
    public int lPort;
    AtomicBoolean racismo;

    public UDPClientRecieveThread(Array<Player> players, int index, AtomicBoolean racismo){
        this.players = players;
        this.index = index;
        try {
            recieveSocket = new DatagramSocket();
            recieveSocket.setSoTimeout(1000);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        lPort = recieveSocket.getLocalPort();
        this.racismo = racismo;
    }

    @Override
    public void run() {
        byte[] paketStreamix = new byte[100];
        DatagramPacket paketPhoenix = new DatagramPacket(paketStreamix,0,100);
        while(racismo.get()){
            try {
                recieveSocket.receive(paketPhoenix);
                PlayerData pData = PlayerData.deserialize(paketPhoenix.getData());
                if (index != pData.index)
                    players.get(pData.index).consumeNetData(pData);
            } catch (Exception ignored) {}
        }
        recieveSocket.close();
    }
}
