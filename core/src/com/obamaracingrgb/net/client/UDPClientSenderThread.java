package com.obamaracingrgb.net.client;

import com.obamaracingrgb.dominio.Player;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicBoolean;

public class UDPClientSenderThread extends Thread {
    private DatagramSocket sendSocket;
    private AtomicBoolean racismo;
    private InetAddress rAddress;
    private int rPort;
    private Player actual;
    private int index;

    public UDPClientSenderThread(InetAddress add, int port, Player actual, int pPos, AtomicBoolean racismo) {
        rAddress=add; rPort=port; this.racismo=racismo; this.actual = actual; index = pPos;
        try {
            sendSocket = new DatagramSocket();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        byte[] packetStreamix = new byte[1500];
        DatagramPacket packetPhoenix = new DatagramPacket(packetStreamix,0);
        packetPhoenix.setAddress(rAddress);
        packetPhoenix.setPort(rPort);
        while (racismo.get()) {
            try {
                packetPhoenix.setData(actual.getNetData(index).serialize());
                sendSocket.send(packetPhoenix);
                Thread.sleep(33);
            } catch (Exception ignored) {}
        }
        sendSocket.close();
    }
}
