package com.obamaracingrgb.net.server;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Array;
import com.obamaracingrgb.dominio.Player;
import com.obamaracingrgb.net.*;
import com.obamaracingrgb.game.ObamaRGBGameClass;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.CountDownLatch;

public class ServerThread extends Thread{
    private ServerSocket sSok;
    private Array<Player> players;
    private ObamaRGBGameClass gamu;
    private CountDownLatch comiensa;

    public ServerThread(ServerSocket sSok, Array<Player> players, ObamaRGBGameClass game){
        this.sSok = sSok;
        this.players = players;
        this.gamu = game;
        this.comiensa = new CountDownLatch(1);
        try {
            this.sSok.setSoTimeout(10000);
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
                    new TCPThread(con, players, gamu, comiensa).start();
                }
            } catch (IOException e) {
                //throw new RuntimeException(e);
                continue;
            }
        }

        comiensa.countDown();

        try{
            sSok.close();
        } catch (IOException e) {
            System.out.println("Algo salio mal :(");
        }
    }
}
