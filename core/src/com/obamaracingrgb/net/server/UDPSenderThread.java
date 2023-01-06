package com.obamaracingrgb.net.server;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.obamaracingrgb.dominio.Player;

import java.io.IOException;
import java.net.*;

public class UDPSenderThread extends Thread{
    private Array<Player> players;
    private DatagramSocket sendSocket;
    private ArrayMap<InetAddress, Integer> clientes;

    public UDPSenderThread(Array<Player> players, ArrayMap<InetAddress, Integer> rAddressses){
        this.players = players;
        try {
            sendSocket = new DatagramSocket();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        clientes = rAddressses;
    }

    @Override
    public void run() {
        int size = players.size;
        byte[] paketStreamix = new byte[1500];
        DatagramPacket paketPhoenix = new DatagramPacket(paketStreamix,1500);
        while (!isInterrupted()) {
            try {
                for (int player=0; player<size; player++) { // Recorremos players
                    for (int client=0; client<size; client++) { // Recorremos clientes
                        paketPhoenix.setData(players.get(player).getNetData(player).serialize());
                        paketPhoenix.setAddress(clientes.getKeyAt(client));
                        paketPhoenix.setPort(clientes.getValueAt(client));
                        sendSocket.send(paketPhoenix);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e); // JAMAS
            }
        }
    }
}
