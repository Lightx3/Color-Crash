package me.panda.states;

import java.util.HashMap;

import me.panda.colorcrash.ColorCrash;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class GameStateManager {
	
	private HashMap<String, GameState> gameStates;
	
	private boolean isInit;
	protected boolean isPaused;
	
	protected ColorCrash gameRef; //Connects camera and assets
	
	public GameStateManager(ColorCrash gameRef){
		gameStates = new HashMap<String, GameState>();
		this.gameRef = gameRef;
	}
	public void init(){
		gameStates.put("menu", new MenuState(this));
		isInit = true;
	}
	
	public void listen(){
		for (GameState state : gameStates.values()){
			state.listen();
		}
	}
	public void update(float dt){
		if (isPaused) {return;}
		for (GameState state : gameStates.values()){
			state.update(dt);
		}
	}
	public void render(SpriteBatch batch, ShapeRenderer sr){
		for (GameState state : gameStates.values()){
			state.render(batch, sr);
		}
	}
	
	public void addState(String str, GameState state){
		gameStates.put(str, state);
	}
	public void remove(String state){
		gameStates.remove(state);
	}
	public GameState getState(String state){
		return gameStates.get(state);
	}
	
	public boolean getInit() {return isInit;}
}
