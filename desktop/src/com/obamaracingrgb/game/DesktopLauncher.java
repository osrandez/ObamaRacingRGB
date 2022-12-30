package com.obamaracingrgb.game;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.utils.Array;
import cosasFeas.*;
import tests.CollisionChek;
import tests.PhysTest;
import tests.PhysTestLaSecuela;
import tests.TestSerio1;

import java.util.Scanner;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.useVsync(false);

		Bullet.init();
		Array<Player> yogadores = new Array<>();
		// Cargas modelos jugadores
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
