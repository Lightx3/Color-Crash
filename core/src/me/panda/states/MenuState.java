package me.panda.states;

import java.util.Random;
import java.util.Stack;

import me.panda.colorcrash.ColorCrash;
import me.panda.entities.Particle;
import me.panda.entities.Tile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class MenuState extends GameState{

	private Stack<Rectangle> ground;
	private int currentColor;
	private int currentDifficulty;
	
	private float ypos;
	private float stateTime;
	private int waveHeight;
	
	private boolean gameStarted;
	private boolean gameOver;
	private boolean startingGame;
	
	private Stack<Particle> crumbs;
	
	private float delay;
	
	public MenuState(GameStateManager gsm) {
		super(gsm);
		
		ground = new Stack<Rectangle>();
		currentDifficulty = 1;
		currentColor = (int) (Math.random() * ((currentDifficulty - 0) + 1) - 0);
		ypos = 0;
		gameStarted = false;
		gameOver = false;
		crumbs = new Stack<Particle>();
		waveHeight = 80; //Used to increase height
		
		Music menuSong = gsm.gameRef.getAssets().get("data/audio/menu-song.mp3");
		menuSong.play();
		menuSong.setLooping(true);
		menuSong.setVolume(0.4f);
	
		addRect();
	
	}
	private void spawnCrumbs(){
		if (crumbs.size() > 10) return;
		
		Random random = new Random();
		
		int tmpx = random.nextInt(ColorCrash.SCREEN_WIDTH) + 10;
		int tmpy = random.nextInt(ColorCrash.SCREEN_HEIGHT + 100) + ColorCrash.SCREEN_HEIGHT;
		
		crumbs.push(new Particle(tmpx, tmpy, 5, 5));
		crumbs.peek().setColor(Tile.ALL_COLORS[random.nextInt(Tile.ALL_COLORS.length)]);
		crumbs.peek().setSpeed(random.nextFloat() + 300);
		
	}
	private void addRect(){
		int maxSpace = ColorCrash.SCREEN_WIDTH+100;
		
		for (int i = 0; i < ground.size(); i++){
			if (ground.get(i).getX() > 0){
				maxSpace -= 75;
			}
		}
		for (int i = maxSpace; i > 0;){
			int width = Math.abs((int) (Math.random() * ((80 - 75) + 1) - 75));//random.nextInt(80) + 75;
			int height = Math.abs((int) (Math.random() * ((waveHeight - (waveHeight - 5)) + 1) - (waveHeight - 5)));

			if (height >= ColorCrash.SCREEN_HEIGHT / 2){
				gameOver = true;
				
				((Music) gsm.gameRef.getAssets().get("data/audio/menu-song.mp3")).stop();
				((Sound) gsm.gameRef.getAssets().get("data/audio/gameover.mp3")).play(0.5f);
				
				gsm.remove("play");
				gsm.remove("menu");
				gsm.isPaused = false;
				gsm.addState("menu", new MenuState(gsm));
			}
			
			int lastPos = 0;
			if (ground.size() > 1){
				lastPos = (int) ground.get(ground.indexOf(ground.lastElement())).getX();
				lastPos += (int) ground.get(ground.indexOf(ground.lastElement())).getWidth();
			}
			ground.push(new Rectangle(lastPos, 0, width, height));
			
			i -= lastPos;
		}
	}
	
	public void increaseSize(){
		waveHeight += Tile.TILE_SIZE / 2;
	}
	@Override
	protected void listen() {
		if (gameOver) return;
		if (Gdx.input.justTouched() && !gameStarted && !startingGame){
			startingGame = true;
			((Sound) gsm.gameRef.getAssets().get("data/audio/menu-effect.wav")).play(0.4f);
		}else if (Gdx.input.justTouched()){
			Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
			gsm.gameRef.getCam().unproject(mousePos);
			
			Rectangle mouseRect = new Rectangle(mousePos.x, mousePos.y, 5, 5);
			Rectangle buttonRect = new Rectangle (15, ColorCrash.SCREEN_HEIGHT-45, 32,32);
			
			if (buttonRect.overlaps(mouseRect)){
				gsm.isPaused = !gsm.isPaused;
			}
			
		}
	}

	@Override
	protected void update(float dt) {
		for (int i = 0; i < ground.size(); i++){
			ground.get(i).x -= 120 * dt;
			if (ground.get(i).getX() < -(ground.get(i).getWidth() + 10)){
				ground.remove(i);
			}
		}
		
		ypos += 29 * stateTime;
		
		//MoveCrumbs
		for (int i = 0; i < crumbs.size(); i++){
			Particle particle = crumbs.get(i);
			
			particle.setY(particle.getY() - particle.getSpeed() * dt);
			if (particle.getY() < -100){
				crumbs.remove(i);
			}
		}
		
		//Start Effect
		if (startingGame){
			if (Gdx.app.getType() == ApplicationType.Android) Gdx.input.vibrate(new long[] { 0, 200, 200, 200}, -1); 
			
			gsm.gameRef.getCam().position.x += (float) (Math.random() * ((9 - -9) + 1) + -9);
			gsm.gameRef.getCam().update();
			delay++;
			spawnCrumbs();
			if (delay > 100){
				gameStarted = true;
				startingGame = false;
				
				gsm.addState("play", new PlayState(gsm));
			}
		}	
		stateTime = dt;
		addRect();
	}
	public Color getCurrentColor(){return Tile.ALL_COLORS[currentColor];}
	public int getDifficulty() {return currentDifficulty;}
	public void updateColor(int dif){
		currentDifficulty = dif;
		if (currentColor+1 > currentDifficulty){
			currentColor = 0;
		}else{
			currentColor++;
		}
		
	}

	@Override
	protected void render(SpriteBatch batch, ShapeRenderer sr) {
		
		batch.setProjectionMatrix(gsm.gameRef.getCam().combined.scl(ColorCrash.scl));
		sr.setProjectionMatrix(gsm.gameRef.getCam().combined.scl(ColorCrash.scl));
		
		sr.begin(ShapeType.Filled);
		for (Particle particle : crumbs){
			sr.setColor(particle.getColor());
			sr.rect(particle.getX(), particle.getY(), particle.getWidth(), particle.getHeight());
		}
		
		sr.setColor(Tile.ALL_COLORS[currentColor]);
		for (Rectangle rect : ground){
			sr.rect(rect.x, rect.y, rect.width, rect.height);
		}
		sr.end();
		
		String path = gsm.isPaused ? "play-up.png" : "pause-up.png";
		batch.begin();
		batch.draw((Texture)gsm.gameRef.getAssets().get("data/graphics/" + path), 15, ColorCrash.SCREEN_HEIGHT - 45);
		
		if (!gameStarted && !startingGame){
			batch.draw((Texture)gsm.gameRef.getAssets().get("data/graphics/button.png"),
					(ColorCrash.SCREEN_WIDTH / 2) - (140 / 2), 
					(ColorCrash.SCREEN_HEIGHT / 2 - (125 / 2)) + (float)Math.sin(ypos));
		}
		batch.end();
		
		
	}

}
