package me.panda.states;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public abstract class GameState {
	
	protected GameStateManager gsm;
	
	public GameState(GameStateManager gsm){
		this.gsm = gsm;
	}
	
	protected abstract void listen();
	protected abstract void update(float dt);
	protected abstract void render(SpriteBatch batch, ShapeRenderer sr);
	
}