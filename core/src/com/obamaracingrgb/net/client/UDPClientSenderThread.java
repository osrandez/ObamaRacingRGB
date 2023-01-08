package com.obamaracingrgb.net.client;

import com.obamaracingrgb.dominio.Player;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicBoolean;

public class UDPClientSenderThread extends Thread {
    private DatagramSocket sendSocket;
    private AtomicBoolean open;
    private InetAddress rAddress;
    private int rPort;
    private Player actual;
    private int index;

    public UDPClientSenderThread(InetAddress add, int port, Player actual, int pPos, AtomicBoolean open) {
        rAddress=add; rPort=port; this.open=open; this.actual = actual; index = pPos;
        try {
            sendSocket = new DatagramSocket();
        } catch (SocketException e) {
            System.out.println("crear el sok");
        }
    }

    @Override
    public void run() {
        byte[] packetStreamix = new byte[100];
        DatagramPacket packetPhoenix = new DatagramPacket(packetStreamix,0,100);
        packetPhoenix.setAddress(rAddress);
        packetPhoenix.setPort(rPort);
        while (open.get()) {
            try {
                packetPhoenix.setData(actual.getNetData(index).serialize());
                sendSocket.send(packetPhoenix);
                Thread.sleep(50);
            } catch (Exception ignored) {}
        }
        sendSocket.close();
    }
}
