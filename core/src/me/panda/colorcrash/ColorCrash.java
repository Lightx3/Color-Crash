package me.panda.colorcrash;

import java.util.Stack;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;

import me.panda.entities.Particle;
import me.panda.states.GameStateManager;

public class ColorCrash extends ApplicationAdapter{
	
	public static int SCREEN_WIDTH = 800;
	public static  int SCREEN_HEIGHT = 680;
	public static float scl; //Adjust to scale images to fit other resolutions
	public static final String TITLE = "Color Crash";
	public static final float FRAME_RATE = 45; //A constant of 40 frames per second.
	
	private GameStateManager gsm;
	private SpriteBatch batch;
	private OrthographicCamera gameCam;
	private AssetManager assetManager;
	
	private ShapeRenderer sr;
	
	//Loading variables
	private int currentXLoc;
	private float delay;
	private final int[] LOCATIONS = {(SCREEN_WIDTH / 2) - 24, (SCREEN_WIDTH / 2) - 12,
			(SCREEN_WIDTH / 2) - 12 * 2, (SCREEN_WIDTH / 2) - 12 * 3};
	
	private Stack<Particle> touchEffects;
	
	@Override
	public void create () {
		scl = 1; //Not used
	
		assetManager = new AssetManager();
		
		if (Gdx.app.getType() == ApplicationType.Android){
			SCREEN_WIDTH = 360;
			SCREEN_HEIGHT = 640;
			
		}
		
		gameCam = new OrthographicCamera();
		gameCam.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);
		gameCam.update();

		touchEffects = new Stack<Particle>();
		
		//Adjust to platform
		batch = new SpriteBatch();
		batch.setProjectionMatrix(gameCam.combined.scl(scl));
		sr = new ShapeRenderer();
		sr.setProjectionMatrix(gameCam.combined.scl(scl));
		
		//Load Assets
		assetManager.load("data/audio/fail.wav", Sound.class);
		assetManager.load("data/audio/point.wav", Sound.class);
		assetManager.load("data/audio/menu-effect.wav", Sound.class);
		assetManager.load("data/audio/color-change.wav", Sound.class);
		assetManager.load("data/graphics/button.png", Texture.class);
		assetManager.load("data/audio/menu-song.mp3", Music.class);
		assetManager.load("data/audio/gameover.mp3", Sound.class);
		assetManager.load("data/graphics/play-up.png", Texture.class);
		assetManager.load("data/graphics/pause-up.png", Texture.class);
		assetManager.load("data/audio/listenSfx.wav", Sound.class);
		
		for (int i = 0; i < 10; i++){
			//Load Numbers
			assetManager.load("data/graphics/num_" + Integer.toString(i)+ ".png", Texture.class);
		}
		
		gsm = new GameStateManager(this);
	}
	@Override
	public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if (assetManager.update()){ //If game started
			Gdx.gl.glClearColor(1, 1, 1, 1);
			
			if (Gdx.input.justTouched()){
				Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
				gameCam.unproject(mousePos);
				touchEffects.push(new Particle(mousePos.x, mousePos.y, 0 ,0));
			}
			
			sr.begin(ShapeType.Line);
			sr.setColor(0.2f, 0.2f, 0.2f, 1);
			for (int i = 0; i < touchEffects.size(); i++){
				Particle particle = touchEffects.get(i);
				//Draws a circle with increase radius and alpha
				sr.circle(particle.getX(), particle.getY(), particle.getWidth());
				
				particle.setSize(particle.getWidth() + 10, 0);
				if (particle.getWidth() > 300){
					touchEffects.remove(i);
				}
			}
			sr.end();
			
			
			if (!gsm.getInit())gsm.init();
			gsm.listen();
			gsm.update(Math.min(Gdx.graphics.getDeltaTime(), 1 / FRAME_RATE)); //Anything that has to do with time will be multiplied by this
			gsm.render(batch, sr);
			
			//Background
			sr.begin(ShapeType.Filled);
			sr.setColor(Color.RED);
			sr.rect(0, 0, 10, SCREEN_HEIGHT / 2);
			sr.rect(0, 0, SCREEN_WIDTH / 2, 10);
			//Next
			sr.setColor(Color.BLUE);
			sr.rect(0, SCREEN_HEIGHT / 2, 10, SCREEN_HEIGHT / 2);
			sr.rect(0, SCREEN_HEIGHT - 10, SCREEN_WIDTH / 2, 10);
			//Next
			sr.setColor(Color.GREEN);
			sr.rect(SCREEN_WIDTH / 2, SCREEN_HEIGHT - 10, SCREEN_WIDTH / 2, 10);
			sr.rect(SCREEN_WIDTH - 10, SCREEN_HEIGHT / 2, 10, SCREEN_HEIGHT / 2);
			//Next
			sr.setColor(Color.YELLOW);
			sr.rect(SCREEN_WIDTH / 2, 0, SCREEN_WIDTH, 10);
			sr.rect(SCREEN_WIDTH - 10, 0, 10, SCREEN_HEIGHT / 2);
			sr.end();
			
		}else{
			Gdx.gl.glClearColor(0, 0, 0, 1);
			delay++;
			if (delay > 20){
				currentXLoc++;
				if (currentXLoc > LOCATIONS.length-1) currentXLoc = 0;
				delay = 0;
			}
			
			sr.begin(ShapeType.Filled);
			sr.setColor(Color.WHITE);
			sr.rect(LOCATIONS[currentXLoc], (SCREEN_HEIGHT / 2) - 24, 24, 24);
			sr.end();
			
			System.out.println("Loading: " + Math.round(assetManager.getProgress() * 100) + "%");
		}
		gameCam.position.x = SCREEN_WIDTH / 2;
		gameCam.position.y = SCREEN_HEIGHT / 2;
		gameCam.update();
		
	}
	
	public OrthographicCamera getCam() {return gameCam;}
	public AssetManager getAssets() {return assetManager;}
	
	@Override
	public void dispose () {
		batch.dispose();
		sr.dispose();
		assetManager.clear();
		assetManager.dispose();
	}

}
