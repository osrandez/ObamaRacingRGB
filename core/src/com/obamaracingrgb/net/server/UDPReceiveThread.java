package com.obamaracingrgb.net.server;

import com.badlogic.gdx.utils.Array;
import com.obamaracingrgb.dominio.Player;
import com.obamaracingrgb.net.utils.PlayerData;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPReceiveThread extends Thread{
    private Array<Player> players;
    private DatagramSocket receiveSocket;
    // TUS MUERTOS OSCAR ESTO SE DELETREA "RECEIVE" AL IGUAL QUE "DECEIVE"
    // POR ESO SE PRONUNCIA CON UNA I, YA SE QUE ES "DECEIVING" PERO LLEVO 20 MINS CON ESTO
    public int lPort;

    public UDPReceiveThread(Array<Player> players){
        this.players = players;
        try {
            receiveSocket = new DatagramSocket();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        lPort= receiveSocket.getLocalPort();
    }

    @Override
    public void run() {
        byte[] buffer = new byte[1500];
        DatagramPacket paketPhoenix = new DatagramPacket(buffer, 1500);    //lo siento mucho
        PlayerData currentPlayer;
        while(!this.isInterrupted()){
            try {
                receiveSocket.receive(paketPhoenix);
                currentPlayer = PlayerData.deserialize(paketPhoenix.getData());
                players.get(currentPlayer.index).consumeNetData(currentPlayer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
