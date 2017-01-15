package me.panda.states;
import java.util.Stack;

import me.panda.colorcrash.ColorCrash;
import me.panda.entities.Particle;
import me.panda.entities.RegularTile;
import me.panda.entities.Tile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class PlayState extends GameState{
	
	private Stack<Tile> gameTiles;
	
	private int gameScore;
	private int playerScore;
	private float gameSpeed;
	private int difficulty_level;
	private int spawnRate;
	private int maxRate;
	private boolean resetColor;
	
	private Stack<Particle> explosion;

	public PlayState(GameStateManager gsm) {
		super(gsm);
		
		gameTiles = new Stack<Tile>();
		gameScore = 0;
		playerScore = 0;
		difficulty_level = 1;
		gameSpeed = 100;
		spawnRate = maxRate = 90;
		
		//hehehe
		explosion = new Stack<Particle>();		
	}
	private void createExplosion(){
		int size = 10;
		for (int i = 0; i < 100; i++){
			explosion.push(new Particle(ColorCrash.SCREEN_WIDTH / 2, ColorCrash.SCREEN_HEIGHT-100, size, size));
			Particle particle = explosion.peek();
			
			//Each random properties
			int xspeed = (int) (Math.random() * ((300 - (-300)) + 1) - (-300));
			int yspeed = (int) (Math.random() * ((300 - (-300)) + 1) - (-300));
			
			particle.xspeed = (float) Math.cos(xspeed) * 300;
			particle.yspeed = (float) Math.sin(yspeed) * 300;
			particle.setColor(Tile.ALL_COLORS[(int) (Math.random() * ((Tile.ALL_COLORS.length-1 - 0) + 1) - 0)]);
		}
	}
	private void addTiles(){
		float tmpx = (float) (Math.random() * ((ColorCrash.SCREEN_WIDTH - (Tile.TILE_SIZE+20) - 20) - Tile.TILE_SIZE) + Tile.TILE_SIZE);
			
		gameTiles.push(new RegularTile(tmpx, ColorCrash.SCREEN_HEIGHT+Tile.TILE_SIZE, 
				Tile.TILE_SIZE, Tile.TILE_SIZE));
		
		Tile tile = gameTiles.peek();
		tile.setColor(difficulty_level);
		tile.setSpeed((float) (Math.random() * (80 - 20) + 20));
		if (tile.getColor() == ((MenuState) gsm.getState("menu")).getCurrentColor()){
			tile.jumpColors();
		}
		
	}

	@Override
	protected void listen() {
		if (Gdx.input.justTouched()){
			Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
			gsm.gameRef.getCam().unproject(mousePos);
			
			//Mouse square = 5x5
			if (gsm.isPaused == false){
				for (Tile tile : gameTiles){
					if (tile.getRect().overlaps(new Rectangle(mousePos.x, mousePos.y, 5 ,5))){
						tile.setTouched(true);
						tile.jumpColors();
						
						((Sound) gsm.gameRef.getAssets().get("data/audio/point.wav")).play(0.5f);
					}
				}
			}
		}
	}
	//Add a stop time feature or slowdown feature. (Get it from a yellow tile)

	@Override
	protected void update(float dt) {
		for (int i = 0; i < gameTiles.size(); i++){
			gameTiles.get(i).update(dt, gameSpeed);
			if (gameTiles.get(i).getY() < 0){
				if (gameTiles.get(i).getColor() != ((MenuState) gsm.getState("menu")).getCurrentColor()){
					((MenuState) gsm.getState("menu")).increaseSize();
					
					((Sound) gsm.gameRef.getAssets().get("data/audio/fail.wav")).play(0.5f);
				}else{
					gameScore++;
					playerScore++;
					if (gameScore > 15){
						gameScore = 0;
						difficulty_level += difficulty_level < Tile.ALL_COLORS.length-1 ? 1 : 0;
						((Sound) gsm.gameRef.getAssets().get("data/audio/listenSfx.wav")).play();
						resetColor = true;				
					}
					//gameSpeed += 5f;
					//maxRate -= 1;
				}
				gameTiles.remove(i);
			}
		}
		spawnRate++;
		if (spawnRate > maxRate){
			spawnRate = 0;
			addTiles();
		}
		
		if (resetColor){
			resetColor = false;
			gameTiles.clear();
			((MenuState) gsm.getState("menu")).updateColor(difficulty_level);
			
			createExplosion();
		}
		for (int i = 0; i < explosion.size(); i++){
			explosion.get(i).update(dt);
			if (explosion.get(i).remove){
				explosion.remove(i);
			}
		}
	}

	@Override
	protected void render(SpriteBatch batch, ShapeRenderer sr) {
		sr.begin(ShapeType.Line);
		for (int j = 0; j < 15; j++){
			for (int i = 0; i < gameTiles.size(); i++){
				Tile tile = gameTiles.get(i);
				sr.setColor(tile.getColor());
	
				sr.line(tile.getX() + tile.getWidth() / 2, tile.getY() + tile.getHeight() / 2, tile.getX() + tile.getWidth() / 2, ColorCrash.SCREEN_HEIGHT);
				sr.line(0, tile.getY() + tile.getHeight() / 2, tile.getX() + tile.getWidth() / 2, tile.getY() + tile.getHeight() / 2);
				sr.line(tile.getX() + tile.getWidth() / 2, tile.getY() + tile.getHeight() / 2, tile.getX() + tile.getWidth() / 2, 0);
				sr.line(tile.getX() + tile.getWidth() / 2, tile.getY() + tile.getHeight() / 2, ColorCrash.SCREEN_WIDTH, tile.getY() + tile.getHeight() / 2);
			}
		}
		sr.end();
		sr.begin(ShapeType.Filled);
		for (int i = 0; i < gameTiles.size(); i++){
			Tile tile = gameTiles.get(i);
			sr.setColor(tile.getColor());
			sr.rect(tile.getX(), tile.getY(), tile.getWidth(), tile.getHeight());
		}
		for (int i = 0; i < explosion.size(); i++){
			sr.setColor(explosion.get(i).getColor());
			sr.rect(explosion.get(i).getX(), explosion.get(i).getY(), explosion.get(i).getWidth(),
					explosion.get(i).getHeight());
		}
		sr.end();
		
		batch.begin();
		String nums = Integer.toString(playerScore);
		for(int i = 0; i < nums.length(); i++){
			batch.draw((Texture)gsm.gameRef.getAssets().get("data/graphics/num_" + nums.charAt(i)+".png"),  
					(ColorCrash.SCREEN_WIDTH - (35 * nums.length()+1)) + i * 24,
					ColorCrash.SCREEN_HEIGHT - 40);
		}
		batch.end();
		
		
	}

}
