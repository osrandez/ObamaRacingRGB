package com.obamaracingrgb.game;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import cosasFeas.PruebasSucias;

import java.util.Scanner;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.useVsync(false);
		//config.setForegroundFPS(600);
		//config.setIdleFPS(20);

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
	}
}
