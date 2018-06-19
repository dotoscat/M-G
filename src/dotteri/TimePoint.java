package dotteri;

import com.badlogic.gdx.graphics.g2d.*;

public class TimePoint extends GameObject {

	float time;
	int point;
	StringBuilder str = null;
	BitmapFont.TextBounds bounds = null;
	
	TimePoint(){
		str = new StringBuilder(2);
	}
	
	public void setTimePoint(int timepoint){
		time = timepoint;
		point = timepoint;
		str.setLength(0);
		str.append(timepoint);
		bounds = MG.game.timepoint_font.getBounds(str);
	}
	
	public float getTime(){
		return time;
	}
	
	public int getPoint(){
		return point;
	}
	
	public void draw(SpriteBatch batch){
		super.draw(batch);
		if (bounds == null){
			return;
		}
		MG.game.timepoint_font.draw(batch, str,
				position.x-(bounds.width/2.f), position.y+(bounds.height/2.f));
	}
	
}
