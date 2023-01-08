package com.obamaracingrgb.net.client;

import com.badlogic.gdx.utils.Array;
import com.obamaracingrgb.dominio.Player;
import com.obamaracingrgb.game.ObamaRGBGameClass;
import com.obamaracingrgb.game.Track1;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class TCPServerConection extends Thread{
    private Socket conection;
    private ObamaRGBGameClass gamu;
    private Array<Player> players;
    private Player actual;
    private String modelName;
    private int pos;

    UDPClientRecieveThread udpIn;
    UDPClientSenderThread udpOut;
    AtomicBoolean iniciar;
    public AtomicBoolean open;

    public TCPServerConection(Socket conection, ObamaRGBGameClass game, Array<Player> players, Player actual, AtomicBoolean iniciar){
        this.conection = conection;
        this.gamu = game;
        this.players = players;
        this.actual = actual;
        this.modelName = actual.nodes.get(0).id;
        this.pos = -1;
        this.iniciar = iniciar;
        this.open = new AtomicBoolean(true);
        System.out.println("ModelName: "+ modelName);
    }

    @Override
    public void run() {
        try(BufferedWriter out = new BufferedWriter(new OutputStreamWriter(conection.getOutputStream()));
            BufferedReader in = new BufferedReader(new InputStreamReader(conection.getInputStream()))) {

            out.write(modelName + "\r\n");
            out.flush();
            System.out.println("Flushiamos name");

            pos = Integer.parseInt(in.readLine());
            System.out.println("Nuestra pos es "+pos);

            open = new AtomicBoolean(true);

            udpIn = new UDPClientRecieveThread(players, pos, open);
            int lPort = udpIn.lPort;

            // Recibimos rPort
            int rPort = Integer.parseInt(in.readLine());
            System.out.println("RPort: "+rPort);

            // Mandamos lPort
            out.write(lPort+"\r\n");
            out.flush();
            System.out.println("LPort: "+lPort);

            udpOut = new UDPClientSenderThread(conection.getInetAddress(), rPort, actual, pos, open);
            System.out.println("UDPOut");

            int contador = 0;
            String line;
            while((line = in.readLine()) != null){
                System.out.println("Linea: "+line);
                if (contador==pos) {
                    this.players.add(actual);
                    System.out.println("Nosotros");
                } else {
                    this.players.add(gamu.pConstructors.get(line).construct());
                    contador++;
                }
            }

            udpIn.setDaemon(true);
            udpOut.setDaemon(true);

            udpIn.start();
            udpOut.start();

            System.out.println(players.get(pos)==actual);


            iniciar.set(true);
            //consutruimos la lista y sacamos actual
        }
        catch(IOException e){
            //nada que evr aqui :(
        }

        System.out.println("Procedemos a printear");
        for (int i=0; i<players.size;i++) {
            System.out.println(players.get(i).nodes.get(0).id);
        }

        try {
            conection.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
