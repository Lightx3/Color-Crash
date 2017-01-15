package me.panda.colorcrash.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import me.panda.colorcrash.ColorCrash;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		//My phones resolution
		config.width = ColorCrash.SCREEN_WIDTH;
		config.height = ColorCrash.SCREEN_HEIGHT;
		config.title = ColorCrash.TITLE;
		config.resizable = false; //Will need to make this true to test scale
		config.useGL30 = true;
		
		new LwjglApplication(new ColorCrash(), config);
	}
}
