package com.obamaracingrgb.game;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.utils.Array;
import com.obamaracingrgb.dominio.Player;
import com.obamaracingrgb.net.client.Client;
import com.obamaracingrgb.net.server.Server;

import java.util.Scanner;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static int optParser(int max) {
		do {
			System.out.print("Elige pibe (0-"+max+"): ");
			String line = new Scanner(System.in).nextLine();
			int xd=-1;
			try {
				xd = Integer.parseInt(line);
			} catch (Exception ignored) {}
			if (xd > 0 || xd <= max)
				return xd;
		} while(true);
	}

	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.useVsync(false);

		System.out.println("Setup...");

		Bullet.init();
		Array<Player> yogadores = new Array<>();

		System.out.println("1. Cliente");
		System.out.println("2. Servidor");
		int cs = optParser(2);

		if (cs==1) { // Cliente
			Client c = new Client();
		} else { // Server
			Server s = new Server();
		}


		Track1 si = new Track1(yogadores, null);
		new Lwjgl3Application(si, config);

		// Cosa sexy

		/*
		System.out.println("Tests");
		System.out.println("0. El bueno");
		System.out.println("1. CollisionCheker");
		System.out.println("2. PhysTest");
		System.out.println("3. PhysTest 2: Electric Boga-loo");
		System.out.println("4. Seriote1");
		System.out.println("5. Sucio");
		String opt = new Scanner(System.in).nextLine();
		config.setTitle("ObamaRacingRGB");
		switch (opt) {
			case "1": {
				new Lwjgl3Application(new CollisionChek(), config);
				break;
			}

			case "2" : {
				new Lwjgl3Application(new PhysTest(), config);
				break;
			}

			case "3": {
				new Lwjgl3Application(new PhysTestLaSecuela(), config);
				break;
			}
			case "4": {
				new Lwjgl3Application(new TestSerio1(), config);
				break;
			}
			case "5": {
				new Lwjgl3Application(new PruebasSucias(), config);
				break;
			}
			default:
					new Lwjgl3Application(new ObamaRGBGameClass(), config);
		}

		 */
	}
}
