package ke.co.zerodaygames.icegame;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class ObstacleManager implements Disposable {
    private Array<Obstacle> obstacles;
    private GameScreen gameScreen;

    public ObstacleManager(Skin gameSkin, GameScreen gameScreen, World world) {
        this.gameScreen = gameScreen;
        obstacles = new Array<>(5);
        float lastYPos = Config.GAME_HEIGHT;
        for (int i = 0; i < 5; i++) {
            Obstacle obstacle = new Obstacle(gameSkin, world);
            obstacles.add(obstacle);


            float xPos = (float) (10 + Math.random() * ((Config.GAME_WIDTH - 10) - obstacle.getWidth()));
            float yPos = lastYPos = (float) (lastYPos + Math.random() * 500);


            lastYPos += obstacle.getHeight() + 10;


            obstacle.body.setTransform(xPos + obstacle.getWidth() / 2f, yPos
                    , 0);

            obstacle.body.setLinearVelocity(0, -100);
        }
    }

    public void render(float delta, SpriteBatch spriteBatch) {
        for (int i = 0; i < obstacles.size; i++) {
            Obstacle obstacle = obstacles.get(i);
            obstacle.render(spriteBatch, delta);

            // if the obstacle is below the screen (out if bounds), move it to the top of the screen
            // and increment player score
            if (obstacle.body.getPosition().y < -obstacle.getHeight()) {
                float yPos = (float) (Config.GAME_HEIGHT + Math.random() * (500));
                float xPos = (float) (10 + Math.random() * ((Config.GAME_WIDTH - 10) - obstacle.getWidth()));
                obstacle.body.setTransform(xPos, yPos, 0);

                // Increment score only if the obstacle did not collide with the player on the way down
                if (!obstacle.getObstacleData().collidedWithPlayer)
                    gameScreen.incPlayerScore();

                // Reset collision flag
                obstacle.getObstacleData().collidedWithPlayer = false;
            }
        }
    }

    @Override
    public void dispose() {
        for (int i = 0; i < obstacles.size; i++) {
            obstacles.get(i).dispose();
        }
    }
}
