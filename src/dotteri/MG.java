package dotteri;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

public class MG {

	public static String title = "M-G (Ludum Dare)";
	public static int WIDTH = 800;
	public static int HEIGHT = 600;
	public static Game game = null;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MG.game = new Game();
		LwjglApplication app = new LwjglApplication(MG.game, MG.title, MG.WIDTH, MG.HEIGHT, true);
	}

}
