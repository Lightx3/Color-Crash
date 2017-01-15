package me.panda.entities;

import me.panda.colorcrash.ColorCrash;

import com.badlogic.gdx.graphics.Color;

public abstract class Tile extends Entity{

	protected int currentColor;
	protected int maxDifficulty;	
	protected boolean wasTouched;

	public static final Color[] ALL_COLORS = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.PURPLE, Color.PINK};
	
	public static final int TILE_SIZE = 50;

	public Tile(float x, float y, int width, int height) {
		super(x, y, width, height);
	}
	
	public abstract void update(float dt, float worldSpeed);
	
	public boolean outBound() {return (x + width < 0 || x + width > ColorCrash.SCREEN_WIDTH ||
			y + height < 0 || y + height > ColorCrash.SCREEN_HEIGHT);}

	public Color getColor() {return ALL_COLORS[currentColor];}
	public void setColor(int level){
		int r = (int) (Math.random() * ((level - 0) + 1) - 0);
		
		currentColor = r;
		maxDifficulty = level;
	}
	
	public void jumpColors(){currentColor = currentColor >= maxDifficulty ? 0 : currentColor+1;}
	public void setTouched(boolean b){wasTouched = b;}
	public boolean getTouched() {return wasTouched;}

}
