package com.obamaracingrgb.net.server;

import com.badlogic.gdx.utils.Array;
import com.obamaracingrgb.dominio.Player;
import com.obamaracingrgb.net.utils.PlayerData;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicBoolean;

public class UDPReceiveThread extends Thread{
    private Array<Player> players;
    private DatagramSocket receiveSocket;
    // TUS MUERTOS OSCAR ESTO SE DELETREA "RECEIVE" AL IGUAL QUE "DECEIVE"
    // POR ESO SE PRONUNCIA CON UNA I, YA SE QUE ES "DECEIVING" PERO LLEVO 20 MINS CON ESTO
    public int lPort;
    private AtomicBoolean racismo;

    public UDPReceiveThread(Array<Player> players, AtomicBoolean racismo){
        this.players = players;
        this.racismo = racismo;
        try {
            receiveSocket = new DatagramSocket();
            receiveSocket.setSoTimeout(1000);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        lPort= receiveSocket.getLocalPort();
    }

    @Override
    public void run() {
        byte[] paketStreamix = new byte[1500];
        DatagramPacket paketPhoenix = new DatagramPacket(paketStreamix, 1500);    //lo siento mucho // yo no
        PlayerData currentPlayer;
        while(racismo.get()){
            try {
                receiveSocket.receive(paketPhoenix);
                currentPlayer = PlayerData.deserialize(paketPhoenix.getData());
                players.get(currentPlayer.index).consumeNetData(currentPlayer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        receiveSocket.close();
    }
}
