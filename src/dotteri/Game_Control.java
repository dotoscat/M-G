package dotteri;

import com.badlogic.gdx.InputAdapter;

public class Game_Control extends InputAdapter{
	public boolean touchDown(int screenX, int screenY, int pointer, int button){
		if (MG.game.isTITLE() ){
			MG.game.setRUNNING();
		}
		else if (MG.game.isRUNNING() ){
			MG.game.playSoundSetGravityBall();
			MG.game.setGravityBall(screenX, MG.HEIGHT-screenY);
		}
		else if (MG.game.isGAMEOVER() ){
			MG.game.setTITLE();
		}
		return true;
	}

	public boolean touchUp(int screenX, int screenY, int pointer, int button){
		return true;
	}
}
