package me.panda.entities;

public class RegularTile extends Tile{

	private double xspeed;
	public RegularTile(float x, float y, int width, int height) {
		super(x, y, width, height);
		
		xspeed = 200;
		
	}

	@Override
	public void update(float dt, float worldSpeed) {
		y -= (speed + worldSpeed) * dt;
		x += Math.cos(xspeed * dt);
		xspeed += 2;
		
		width = height += wasTouched ? 2 : 0;
		if (width > Tile.TILE_SIZE+10){
			setSize(Tile.TILE_SIZE, Tile.TILE_SIZE);
			wasTouched = false;
		}
		
		fixPos();
		
	}

}
