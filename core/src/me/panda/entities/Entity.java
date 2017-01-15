package me.panda.entities;

import com.badlogic.gdx.math.Rectangle;
import me.panda.colorcrash.ColorCrash;

public class Entity {
	
	protected float x;
	protected float y;
	protected int width;
	protected int height;
	
	public float xvel;
	public float yvel;
	protected float speed;
	
	protected Rectangle box;

	public Entity(float x, float y, int width, int height){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		xvel = yvel = speed = 0;
		box = new Rectangle(x, y, width, height);
	}
	public float getX() {return x;}
	public float getY() {return y;}
	public int getWidth() {return width;}
	public int getHeight() {return height;}
	
	protected void fixPos(){	
		x = x < 0 ? 0 : (x + width > ColorCrash.SCREEN_WIDTH ? ColorCrash.SCREEN_WIDTH : x);
		
		box.x = x;
		box.y = y;
	}
	public void setSize(int width, int height){
		this.width = width;
		this.height = height;
	}
	public void setX(float x) {this.x = x; fixPos();}
	public void setY(float y) {this.y = y; fixPos();}
	public Rectangle getRect() {return box;} //The box's cordinates need also to be updated
	
	public void setSpeed(float speed) {this.speed = speed;}
	public float getSpeed() {return speed;}
	

}
