package com.obamaracingrgb.net.server;

import com.obamaracingrgb.dominio.Player;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

//todo Rehacer esto xd

public class Server{
    boolean open;
    private ConcurrentHashMap<InetAddress, PlayerData> list;

    public Server(){
        list = new ConcurrentHashMap<>();
        open = true;
    }

    public void startServer() {
        try(ServerSocket sSok = new ServerSocket(65535)) {
            while(open){
                Socket xd = sSok.accept();
                Thread t = new Thread(new TCPThread(xd, list));
                t.setDaemon(true);
                t.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

class PlayerData {
    Player pl;
    TCPThread tcpSock;

    public PlayerData() {

    }
}


class TCPThread implements Runnable{
    Socket s;
    ConcurrentHashMap<InetAddress, PlayerData> lista;

    public TCPThread(Socket s, ConcurrentHashMap<InetAddress, PlayerData> lista){
        this.s = s;
        this.lista = lista;
    }

    @Override
    public void run() {
        try(BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {
            String character = in.readLine();



            out.write("Si\r\n"); // feedback al cliente
            out.flush();



        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}



abstract class UDPThread implements Runnable {}


/*
class UDPThreadInput extends UDPThread{
}

class UDPThreadOutput extends UDPThread{

}
*/