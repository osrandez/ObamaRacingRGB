package com.obamaracingrgb.net.client;

import com.badlogic.gdx.utils.Array;
import com.obamaracingrgb.dominio.Player;
import com.obamaracingrgb.game.ObamaRGBGameClass;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class TCPServerConection extends Thread{
    private Socket conection;
    private ObamaRGBGameClass gamu;
    private Array<Player> players;
    private Player actual;
    private String modelName;
    private int pos;

    public TCPServerConection(Socket conection, ObamaRGBGameClass game, Array<Player> players, Player actual){
        this.conection = conection;
        this.gamu = game;
        this.players = players;
        this.actual = actual;
        this.modelName = actual.nodes.get(0).id;
        this.pos = -1;
    }

    @Override
    public void run() {
        try(BufferedWriter out = new BufferedWriter(new OutputStreamWriter(conection.getOutputStream()));
            BufferedReader in = new BufferedReader(new InputStreamReader(conection.getInputStream()))) {

            out.write(modelName + "\r\n");
            out.flush();
            //Mandamos el nombre del modelo a construir

            pos = Integer.parseInt(in.readLine());
            int contador = 0;
            //recibimos la posicion que tendremos en el array

            String line;
            while((line = in.readLine()) != null){
                System.out.println("Linea: "+line);
                if (contador==pos)
                    this.players.add(actual);
                else
                    this.players.add(gamu.pConstructors.get(line).construct());
            }
            //consutruimos la lista y sacamos actual
        }
        catch(IOException e){
            //nada que evr aqui :(
        }

        for(Player p : players){
            System.out.println(p.nodes.get(0).id);
        }

        try {
            conection.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
