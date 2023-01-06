package com.obamaracingrgb.net.server;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.obamaracingrgb.dominio.Player;
import com.obamaracingrgb.game.ObamaRGBGameClass;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

public class TCPThread extends Thread{
    private Socket conection;
    private final Array<Player> players;
    private ObamaRGBGameClass gamu;
    private CountDownLatch comiensa;

    private static ArrayMap<InetAddress, Integer> udpRAddresses;
    public int udpLPort;
    public int udpRPort;

    public  TCPThread(Socket conection, Array<Player> players, ObamaRGBGameClass game, CountDownLatch comiensa, ArrayMap<InetAddress, Integer> addrs){
        this.conection = conection;
        this.players = players;
        this.gamu = game;
        this.comiensa = comiensa;
        if (udpRAddresses==null)
            udpRAddresses = addrs;
    }

    @Override
    public void run() {
        String info;

        try(BufferedWriter out = new BufferedWriter(new OutputStreamWriter(conection.getOutputStream()));
            BufferedReader in = new BufferedReader(new InputStreamReader(conection.getInputStream()))) {

            info = in.readLine();
            System.out.println(info);
            synchronized (this.players) {
                players.add(gamu.pConstructors.get(info).construct());
                out.write((players.size-1) + "\r\n"); // Indice
            }
            out.flush();

            comiensa.await();
            //comiensa la partida


            // Mandamos lPort
            out.write(udpLPort+"\r\n");
            out.flush();

            // Recibimos rPort
            info = in.readLine();
            udpRPort = Integer.parseInt(info);

            // Puteamos el puerto epicamente
            udpRAddresses.put(conection.getInetAddress(), udpRPort);

            // Pasamos lista de jugadores
            for (int i=0; i<players.size; i++) {
                out.write(players.get(i).name + "\r\n");
            }

        }catch (IOException e){
            //tampoco
        }
        catch (InterruptedException e) {
            //no
        }

        try {
            conection.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

