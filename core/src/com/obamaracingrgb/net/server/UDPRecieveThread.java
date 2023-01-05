package com.obamaracingrgb.net.server;

import com.badlogic.gdx.utils.Array;
import com.obamaracingrgb.dominio.Player;
import com.obamaracingrgb.game.ObamaRGBGameClass;
import com.obamaracingrgb.net.utils.PlayerData;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPRecieveThread extends Thread{
    private Array<Player> players;
    private DatagramSocket recieveSocket;

    public UDPRecieveThread(Array<Player> players){
        this.players = players;
        try {
            recieveSocket = new DatagramSocket();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void run() {
        byte[] buffer = new byte[1500];
        DatagramPacket paketPhoenix = new DatagramPacket(buffer, 1500);    //lo siento mucho
        PlayerData currentPlayer;
        while(!this.isInterrupted()){
            try {
                recieveSocket.receive(paketPhoenix);
                currentPlayer = PlayerData.deserialize(paketPhoenix.getData());
                players.get(currentPlayer.index).consumeNetData(currentPlayer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
