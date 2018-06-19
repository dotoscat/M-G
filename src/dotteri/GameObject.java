package dotteri;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.math.collision.*;

public class GameObject {
	Sprite sprite = null;
	Sphere sphere = null;
	boolean _exists = false;
	Vector2 position = null;
	Vector2 velocity = null;
	Vector2 offset = null;
	float mass = 0.f;
	
	GameObject(){
		sprite = new Sprite();
		sphere = new Sphere(Vector3.Zero, 0.f);
		velocity = new Vector2();
		position = new Vector2();
		offset = new Vector2();
	}
	
	public boolean exists(){
		return _exists;
	}
	
	public void setExists(boolean exists){
		_exists = exists;
	}
	
	public void setTexture(Texture texture){
		sprite.setTexture(texture);
	}
	
	public void setRegion(int x, int y, int width, int height){
		sprite.setRegion(x, y, width, height);
		sprite.setOrigin(width/2.f, height/2.f);
		offset.x = width/2.f;
		offset.y = height/2.f;
		sprite.setSize(width, height);
	}
	
	public void setRadius(float radius){
		sphere.radius = radius;
	}
	
	public boolean checkCollisionWith(GameObject gameobject){
		return sphere.overlaps(gameobject.sphere);
	}
	
	public void update(float time){
		position.x += velocity.x * time;
		position.y += velocity.y * time;
		sprite.setPosition(position.x-offset.x, position.y-offset.y);
		sphere.center.x = position.x;
		sphere.center.y = position.y;
	}
	
	public void setPosition(float x, float y){
		position.x = x;
		position.y = y;
		this.update(0.f);
	}
	
	public void resetVelocity(){
		velocity.x = 0.f;
		velocity.y = 0.f;
	}
	
	public void draw(SpriteBatch batch){
		sprite.draw(batch);
	}
	
	public void setMass(float mass){
		this.mass = mass;
	}
	
	public void applyGravityWith(GameObject gameobject, float time, float factor){
		float mm = this.mass*gameobject.mass;
		float distance = this.position.dst(gameobject.position);
		float force = -(mm/distance)*factor;
		Vector2 vforce = Vector2.tmp;
		
		float p = (float) Math.atan2(position.y-gameobject.position.y, position.x-gameobject.position.x);
		
		vforce.x = force * MathUtils.cos(p);
		vforce.y = force * MathUtils.sin(p);
		
		velocity.add(vforce.mul(time) );
	}
	
	public boolean isInsideArea(Rectangle area){
		return area.contains(position.x, position.y);
	}
	
	public void setColor(float r, float g, float b, float a){
		sprite.setColor(r, g, b, a);
	}
	
}
