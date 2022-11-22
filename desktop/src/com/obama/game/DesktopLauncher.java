package com.obama.game;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import java.util.Scanner;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.useVsync(false);
		config.setForegroundFPS(600);

		System.out.println("Tests");
		System.out.println("0. El bueno");
		System.out.println("1. CollisionCheker");
		System.out.println("2. PhysTest");
		String opt = new Scanner(System.in).nextLine();
		config.setTitle("ObamaRacingRGB");
		switch (opt) {
			case "1" ->
					new Lwjgl3Application(new CollisionChek(), config);
			case "2" ->
					new Lwjgl3Application(new PhysTest(), config);
			default ->
					new Lwjgl3Application(new ObamaRGBGameClass(), config);
		}
	}
}
