package ke.co.zerodaygames.icegame;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Snowflake extends Sprite {
    private float speed;

    public Snowflake(TextureRegion region) {
        super(region);

        float size = (float) Utils.random(10,25);
        setSize(size, size);

        reset();
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void reset(){
        float xPos = (float) Utils.random(Config.GAME_WIDTH * .65f, Config.GAME_WIDTH * 1.5f);
        float yPos = (float) Utils.random(Config.GAME_HEIGHT + 15, Config.GAME_HEIGHT + 1000);
        speed = (float) Utils.random(100, 200);
        setPosition(xPos, yPos);
    }
}
