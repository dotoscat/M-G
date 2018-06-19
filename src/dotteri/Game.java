package dotteri;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.freetype.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.audio.*;

public class Game implements ApplicationListener {
	
	AssetManager assets = new AssetManager();
	
	SpriteBatch batch = null;
	OrthographicCamera camera = null;
	
	enum Status{
		TITLE,
		RUNNING,
		GAMEOVER,
	}
	
	Status status = Status.TITLE;
	
	BitmapFont title = null;
	BitmapFont HUD = null;
	public BitmapFont timepoint_font = null;
	
	Game_Control game_control = new Game_Control();
	
	GameObject player = null;
	GameObject gravity_ball = null;
	
	Rectangle area = new Rectangle();
	
	static int TIMEPOINT = 7;
	TimePoint[] timepoint = null;
	
	float time = 0.f;
	StringBuilder strtime = new StringBuilder(8);
	int points = 0;
	StringBuilder strpoints = new StringBuilder(8);
	StringBuilder gameover_message = new StringBuilder(64);
	StringBuilder strbestscore = new StringBuilder(64);
	int bestpoints = 0;
	
	Sound end = null;
	Sound gettimepoint = null;
	Sound setgravityball = null;
	
	@Override
	public void create() {
		// TODO Auto-generated method stub
		assets.load("assets/player.png", Texture.class);
		assets.load("assets/gravity_ball.png", Texture.class);
		assets.load("assets/time_point.png", Texture.class);
		assets.load("assets/end.wav", Sound.class);
		assets.load("assets/gettimepoint.wav", Sound.class);
		assets.load("assets/setgravityball.wav", Sound.class);
		assets.finishLoading();
		
		end = (Sound)assets.get("assets/end.wav");
		gettimepoint = (Sound)assets.get("assets/gettimepoint.wav");
		setgravityball = (Sound)assets.get("assets/setgravityball.wav");
		
		Gdx.input.setInputProcessor(game_control);
		
		FreeTypeFontGenerator fontgenerator = new FreeTypeFontGenerator(Gdx.files.internal("assets/00TT.TTF"));
		title = fontgenerator.generateFont(77);
		title.setColor(1.f, 1.f, 1.f, 1.f);
		HUD = fontgenerator.generateFont(22);
		timepoint_font = fontgenerator.generateFont(16);
		fontgenerator.dispose();
		
		camera = new OrthographicCamera(MG.WIDTH, MG.HEIGHT);
		camera.translate(MG.WIDTH/2.f, MG.HEIGHT/2.f);
		camera.update();
		
		batch = new SpriteBatch();
		batch.setShader(SpriteBatch.createDefaultShader() );
		batch.setProjectionMatrix(camera.combined);
		
		player = new GameObject();
		player.setTexture((Texture)assets.get("assets/player.png"));
		player.setRegion(0, 0, 32, 32);
		player.setPosition(400, 300);
		player.setMass(1.f);
		player.setRadius(16.f);
		
		gravity_ball = new GameObject();
		gravity_ball.setTexture((Texture)assets.get("assets/gravity_ball.png"));
		gravity_ball.setRegion(0, 0, 32, 32);
		gravity_ball.setMass(1.f);
		gravity_ball.setRadius(16.f);
		
		timepoint = new TimePoint[TIMEPOINT];
		
		for(int i = 0; i < TIMEPOINT; i += 1){
			timepoint[i] = new TimePoint();
			timepoint[i].setRadius(16.f);
			timepoint[i].setTexture((Texture)assets.get("assets/time_point.png"));
			timepoint[i].setRegion(0, 0, 32, 32);
		}
		
		area.x = 0.f;
		area.y = 0.f;
		area.width = MG.WIDTH;
		area.height = MG.HEIGHT;
		
		this.checkRecord();
		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render() {
		// TODO Auto-generated method stub
		float time = Gdx.graphics.getDeltaTime();
		this.time -= time;
		Gdx.gl.glClearColor(0.f, 0.f, 0.f, 0.f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.begin();
		
		switch(status){
		case TITLE:
			this.drawTITLE();
			break;
		case RUNNING:
			if (this.time <= 0.f){
				this.setGameOverByTimeUp();
			}
			if(gravity_ball.exists()){
				player.applyGravityWith(gravity_ball, time, 10000.f);
				gravity_ball.update(time);
				player.update(time);
				if (player.checkCollisionWith(gravity_ball)){
					this.setGameOverByTouchGravityBall();//later set GAME OVER like a house
				}
				else if (!player.isInsideArea(area)){
					this.setGameOverByExceedBounds();//later set GAME OVER like a house
				}
				player.draw(batch);
				gravity_ball.draw(batch);
			}else{
				player.update(time);
				player.draw(batch);
			}
			for(int i = 0; i < TIMEPOINT; i += 1){
				if (!timepoint[i].exists()){
					if (MathUtils.random(60) == 60){
						timepoint[i].setExists(true);
						timepoint[i].setPosition(MathUtils.random(MG.WIDTH), MathUtils.random(MG.HEIGHT) );
						while(timepoint[i].checkCollisionWith(player)){
							timepoint[i].setPosition(MathUtils.random(MG.WIDTH), MathUtils.random(MG.HEIGHT) );
						}
						int value = MathUtils.random(2);
						if (value == 0){
							timepoint[i].setTimePoint(3);
						}
						else if (value == 1){
							timepoint[i].setTimePoint(5);
						}
						else if (value == 2){
							timepoint[i].setTimePoint(7);
						}	
					}
					continue;
				}
				if (player.checkCollisionWith(timepoint[i]) ){
					gettimepoint.play();
					timepoint[i].setExists(false);
					this.time += timepoint[i].getTime();
					points += timepoint[i].getPoint();
					continue;
				}
				timepoint[i].draw(batch);
			}
			this.updateAndDrawHUD();
			break;
		case GAMEOVER:
			this.drawGAMEOVER();
			break;
		}
		batch.end();
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		title.dispose();
		HUD.dispose();
		batch.dispose();
		assets.dispose();
	}

	private void drawTITLE(){
		title.draw(batch, "M-G", 77.f, MG.HEIGHT-77.f);
		HUD.draw(batch, "by Oscar Triano Garcia (teritriano@gmail.com)", 77, MG.HEIGHT-150.f);
		HUD.draw(batch, strbestscore, 77.f, MG.HEIGHT-200.f);
		HUD.draw(batch, "version Ludum Dare", 77.f, MG.HEIGHT-300.f);
	}
	
	private void drawGAMEOVER(){
		title.draw(batch, "GAME OVER", 77.f, MG.HEIGHT-77.f);
		HUD.draw(batch, gameover_message, 77.f, MG.HEIGHT-150.f);
	}
	
	private void updateAndDrawHUD(){
		//update and draw HUD
		strtime.setLength(0);
		strtime.append(MathUtils.round(this.time));
		strtime.append(" seconds");
		HUD.draw(batch, strtime, 8.f, MG.HEIGHT-8.f);
		strpoints.setLength(0);
		strpoints.append(points);
		strpoints.append(" points");
		HUD.draw(batch, strpoints, 256.f, MG.HEIGHT-8.f);
		HUD.draw(batch, strbestscore, 400.f, MG.HEIGHT-8.f);
	}
	
	public boolean isTITLE(){
		return status == Status.TITLE;
	}
	
	public void setTITLE(){
		status = Status.TITLE;
	}
	
	public void setRUNNING(){
		for(int i = 0; i < TIMEPOINT; i += 1){
			timepoint[i].setExists(false);
		}
		time = 10.f;
		points = 0;
		player.setPosition(MG.WIDTH/2.f, MG.HEIGHT/2.f);
		player.resetVelocity();
		gravity_ball.setExists(false);
		status = Status.RUNNING;
	}
	
	public boolean isRUNNING(){
		return status == Status.RUNNING;
	}
	
	public void setGravityBall(float x, float y){
		gravity_ball.setExists(true);
		gravity_ball.setPosition(x, y);
	}
	
	private void setGameOverByTimeUp(){
		status = Status.GAMEOVER;
		end.play();
		gameover_message.setLength(0);
		gameover_message.append("by time up");
		this.checkRecord();
	}
	
	private void setGameOverByTouchGravityBall(){
		status = Status.GAMEOVER;
		end.play();
		gameover_message.setLength(0);
		gameover_message.append("by gravity ball");
		this.checkRecord();
	}
	
	private void setGameOverByExceedBounds(){
		status = Status.GAMEOVER;
		end.play();
		gameover_message.setLength(0);
		gameover_message.append("by exceed bounds");
		this.checkRecord();
	}
	
	private void checkRecord(){
		if (points >= bestpoints){
			bestpoints = points;
			strbestscore.setLength(0);
			strbestscore.append("best: ");
			strbestscore.append(points);
			strbestscore.append(" points");
		}
	}
	
	public boolean isGAMEOVER(){
		return status == Status.GAMEOVER;
	}
	
	public void playSoundSetGravityBall(){
		setgravityball.play();
	}
	
}
