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

public class ServerThread extends Thread{
    private ServerSocket sSok;
    private Array<Player> players;
    private ObamaRGBGameClass gamu;
    private CountDownLatch comiensa;
    private ArrayMap<InetAddress, Integer> udpRAddresses;

    private static UDPSenderThread udpOut;
    private static UDPReceiveThread udpIn;

    public ServerThread(ServerSocket sSok, Array<Player> players, ObamaRGBGameClass game){
        this.sSok = sSok;
        this.players = players;
        this.gamu = game;
        this.comiensa = new CountDownLatch(1);
        udpRAddresses = new ArrayMap<>();

        try {
            this.sSok.setSoTimeout(10000);
            udpIn = new UDPReceiveThread(players);
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
                    new TCPThread(con, players, gamu, comiensa, udpRAddresses).start();
                }
            } catch (IOException e) {
                //throw new RuntimeException(e);
                continue;
            }
        }

        System.out.println("Pre CountDown");


        comiensa.countDown();

        /*
        synchronized (this.players) {
            for(Player p : players){
                System.out.println(p.nodes.get(0).id);
            }
        }
         */
        for (int i=0; i<players.size; i++) {
            System.out.println(players.get(i).nodes.get(0).id);
        }


        try{
            sSok.close();
        } catch (IOException e) {
            System.out.println("Algo salio mal :(");
        }
    }
}
