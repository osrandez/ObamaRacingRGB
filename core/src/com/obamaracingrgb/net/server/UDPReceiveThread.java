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
    private AtomicBoolean open;

    public UDPReceiveThread(Array<Player> players, AtomicBoolean open){
        this.players = players;
        this.open = open;
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
        System.out.println("UDPThread Runniando");
        System.out.println("--- Puerto: " + receiveSocket.getLocalPort() + "  IP: " + receiveSocket.getLocalAddress().getHostAddress());
        byte[] paketStreamix = new byte[100];
        DatagramPacket paketPhoenix = new DatagramPacket(paketStreamix,0, 100);    //lo siento mucho // yo no
        PlayerData currentPlayer;
        while(open.get()){
            try {
                System.out.println("UDP Intenta resivir");
                receiveSocket.receive(paketPhoenix);
                System.out.println(PlayerData.deserialize(paketPhoenix.getData()));
                currentPlayer = PlayerData.deserialize(paketPhoenix.getData());
                players.get(currentPlayer.index).consumeNetData(currentPlayer);
            } catch (IOException e) {
                System.out.println("No resive");
                //throw new RuntimeException("UDP RECIBIR MAL");
            }
        }
        receiveSocket.close();
    }
}
