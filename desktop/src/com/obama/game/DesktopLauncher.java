package com.obama.game;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.obama.noSeUsarLaLibreria.CollisionChek;
import com.obama.noSeUsarLaLibreria.PhysTest;
import com.obama.noSeUsarLaLibreria.PhysTestLaSecuela;
import com.obama.tests.TestRun;
import com.obama.tests.TrisTest;

import java.util.Scanner;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.useVsync(false);
		config.setForegroundFPS(600);
		config.setIdleFPS(20);

		System.out.println("Tests");
		System.out.println("0. El bueno");
		System.out.println("1. CollisionCheker");
		System.out.println("2. PhysTest");
		System.out.println("3. PhysTest 2: Electric Boga-loo");
		System.out.println("4. Triste Test");
		System.out.println("5. TestRun1");
		String opt = new Scanner(System.in).nextLine();
		config.setTitle("ObamaRacingRGB");
		switch (opt) {
			default ->
					new Lwjgl3Application(new ObamaRGBGameClass(), config);
			case "1" ->
					new Lwjgl3Application(new CollisionChek(), config);
			case "2" ->
					new Lwjgl3Application(new PhysTest(), config);
			case "3" ->
					new Lwjgl3Application(new PhysTestLaSecuela(), config);
			case "4" ->
					new Lwjgl3Application(new TrisTest(), config);

		}
	}
}
