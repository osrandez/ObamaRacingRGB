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
    private AtomicBoolean racismo;

    public ServerThread(ServerSocket sSok, Array<Player> players, ObamaRGBGameClass game, AtomicBoolean racismo){
        this.sSok = sSok;
        this.players = players;
        this.gamu = game;
        this.comiensa = new CountDownLatch(1);
        udpRAddresses = new ArrayMap<>();
        this.racismo = racismo;

        try {
            this.sSok.setSoTimeout(10000);
            udpIn = new UDPReceiveThread(players, racismo);
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

        udpOut = new UDPSenderThread(players, udpRAddresses, racismo);

        udpIn.setDaemon(true);
        udpIn.setDaemon(false);

        udpIn.start();
        udpOut.start();

        System.out.println("rPort: "+ udpRAddresses.getValueAt(0));

        try{
            sSok.close();
        } catch (IOException e) {
            System.out.println("Algo salio mal :(");
        }
    }
}
