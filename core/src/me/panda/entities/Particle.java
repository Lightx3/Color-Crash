package me.panda.entities;

import com.badlogic.gdx.graphics.Color;
import me.panda.colorcrash.ColorCrash;

public class Particle extends Entity{
	
	public float alpha;
	public float xangle;
	public float yangle;

	public float delay;
	public boolean remove;
	public float xspeed;
	public float yspeed;
	
	public Color color;
	
	public Particle(float x, float y, int width, int height) {
		super(x, y, width, height);
		
		alpha = 1;
		delay = 0.01f;
		xspeed = (float) Math.cos(Math.random() * ((50 - -50) + 1) + -50); //Doesnt belong here
		yspeed = (float) Math.sin(Math.random() * ((50 - -50) + 1) + -50); //Doesnt belong here
		xangle = 0;
		yangle = 0;
		
		color = new Color(1, 0, 0, 1);
	}
	public void update(float dt){
		y += yspeed * dt;
		x += xspeed * dt;
	}
	public void trailEffect(float dt){
		alpha -= delay;
		y -= yspeed * dt;
		x += xspeed * dt;
		
		remove = (x < 0 || x + width > ColorCrash.SCREEN_WIDTH || y < 0 || y + height > ColorCrash.SCREEN_HEIGHT);
		
	}
	public Color getColor() {return color;}
	public void setColor(Color color) {this.color = color;}
	
}
