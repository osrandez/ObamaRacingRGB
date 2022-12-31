package com.obamaracingrgb.net.client;

import com.obamaracingrgb.dominio.Player;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;

public class Client {
    List<Player> playerList;
    Player pl;

    public void handleClient(){
        try(Socket TCPConnection = new Socket("localHost", 65535)) {
            Thread xd = new Thread(new TCPThread(TCPConnection));
            xd.setDaemon(true);
            xd.start();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

class TCPThread implements Runnable{

    Socket conection;

    public TCPThread(Socket conection){
        this.conection = conection;
    }

    @Override
    public void run() {
        try(BufferedWriter out = new BufferedWriter(new OutputStreamWriter(conection.getOutputStream()))) {
            out.write("1" + "\r\n");
        }catch(IOException e){
            e.printStackTrace();
        }

    }
}
