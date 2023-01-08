package com.obamaracingrgb.net.server;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.obamaracingrgb.dominio.Player;
import com.obamaracingrgb.game.ObamaRGBGameClass;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerThread extends Thread{
    private ServerSocket sSok;
    private Array<Player> players;
    private ObamaRGBGameClass gamu;
    private CountDownLatch comiensa;
    private ArrayMap<InetAddress, Integer> udpRAddresses;

    private UDPSenderThread udpOut;
    private UDPReceiveThread udpIn;
    private AtomicBoolean open;
    private  AtomicBoolean cancel;

    public ServerThread(ServerSocket sSok, Array<Player> players, ObamaRGBGameClass game, AtomicBoolean open, AtomicBoolean cancel){
        this.sSok = sSok;
        this.players = players;
        this.gamu = game;
        this.comiensa = new CountDownLatch(1);
        udpRAddresses = new ArrayMap<>();
        this.open = open;
        this.cancel = cancel;

        try {
            this.sSok.setSoTimeout(1000);
            udpIn = new UDPReceiveThread(players, open);
        }catch (SocketException e){}
    }

    @Override
    public void run() {
        while(!this.isInterrupted()){
            try{
                Socket con = sSok.accept();
                if(this.isInterrupted()){
                    con.close();
                }else{
                    new TCPThread(con, players, gamu, comiensa, udpRAddresses, udpIn.lPort).start();
                }
            } catch (IOException e) {
                //throw new RuntimeException(e);
                continue;
            }
        }

        System.out.println("Pre CountDown");

        comiensa.countDown();

        for (int i=0; i<players.size; i++) {
            System.out.println(players.get(i).nodes.get(0).id);
        }

        udpOut = new UDPSenderThread(players, udpRAddresses, open);

        udpIn.setDaemon(true);
        udpOut.setDaemon(true);

        if(!cancel.get()) {
            System.out.println("lansando udp");
            udpIn.start();
            udpOut.start();
        }


        //System.out.println("rPort: "+ udpRAddresses.getValueAt(0));   //DEBUG


        try{
            sSok.close();
        } catch (IOException e) {
            System.out.println("Algo salio mal :(");
        }
    }
}
