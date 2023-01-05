package com.obamaracingrgb.net.server;

import com.badlogic.gdx.utils.Array;
import com.obamaracingrgb.dominio.Player;
import com.obamaracingrgb.game.ObamaRGBGameClass;
import com.sun.org.apache.xpath.internal.operations.Bool;
import jdk.internal.org.jline.utils.WriterOutputStream;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

public class TCPThread extends Thread{
    private Socket conection;
    private final Array<Player> players;
    private ObamaRGBGameClass gamu;
    private CountDownLatch comiensa;

    public  TCPThread(Socket conection, Array<Player> players, ObamaRGBGameClass game, CountDownLatch comiensa){
        this.conection = conection;
        this.players = players;
        this.gamu = game;
        this.comiensa = comiensa;
    }

    @Override
    public void run() {
        String info;

        try(BufferedWriter out = new BufferedWriter(new OutputStreamWriter(conection.getOutputStream()));
        BufferedReader in = new BufferedReader(new InputStreamReader(conection.getInputStream()))) {

                info = in.readLine();   //id del objeto a crear
                synchronized (this.players) {
                    players.add(gamu.pConstructors.get(info).construct());
                    out.write("" + players.size + "\r\n");
                }
                out.flush();

                comiensa.await();
                //comiensa la partida
                for (Player p : players) {
                    String id = p.name;
                    out.write(id + "\r\n");
                }
                out.flush();

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

