package com.obamaracingrgb.net.client;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.obamaracingrgb.dominio.Player;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Client {
    Array<Player> playerList;
    Player pl;
    int plNumber;

    public void handleClient(ArrayMap<String, Player.Constructor> pConstructors){
        try(Socket s = new Socket("localHost", 65535)) {
            BufferedWriter output = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
            BufferedReader input  = new BufferedReader(new InputStreamReader(s.getInputStream()));
            Scanner sc = new Scanner(System.in);

            // Seleccion de personaje
            System.out.println("Personajes");
            System.out.println("cuboy");
            System.out.println("obama");
            System.out.println("sanchez");
            System.out.print("Elige y no te cagues: ");
            String character = sc.nextLine();

            // Mandamos pj
            output.write(character+"\r\n");
            output.flush();

            // Vamos a recibir una lista de nombres del servidor
            List<String> plNames = new ArrayList<>();

            // Necesitamos un string para terminar
            while (!(character = input.readLine()).equalsIgnoreCase("dameTusUDP")) {
                // Indicador de QUE PERSONAJE ES EL CLIENTE
                if (character.endsWith(" (tu)")) {
                    character = character.substring(0, character.indexOf(" (tu)")); // Aki quitamos el " (tu)"
                    plNumber = plNames.size(); // Aki registramos quienes somos
                }
                plNames.add(character); // Añadir lista
                System.out.println(character); // Mostrar
            }

            // Hacer la lista (de Player)
            playerList = new Array<>();
            for (String name : plNames) {
                playerList.add(pConstructors.get(character).construct());
            }
            pl = playerList.get(plNumber); // Sacamos una referencia a nosotros

            // Hacer los UDP



        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
