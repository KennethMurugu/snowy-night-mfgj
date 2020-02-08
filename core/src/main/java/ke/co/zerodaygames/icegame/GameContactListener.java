package ke.co.zerodaygames.icegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

public class GameContactListener implements ContactListener {
    private static final String TAG = GameContactListener.class.getSimpleName();
    private GameScreen gameScreen;

    public GameContactListener(GameScreen gameScreen){
        this.gameScreen = gameScreen;
    }

    private void detectBallAndObstacleCollision(Contact contact){
        Body bodyA = contact.getFixtureA().getBody();
        Body bodyB = contact.getFixtureB().getBody();

        if(bodyA.getUserData() instanceof Player.PlayerData && bodyB.getUserData() instanceof Obstacle.ObstacleData){
            gameScreen.playerCollidedWithObstacle((Obstacle.ObstacleData) bodyB.getUserData());
        }
        else if(bodyA.getUserData() instanceof Obstacle.ObstacleData && bodyB.getUserData() instanceof Player.PlayerData){
            gameScreen.playerCollidedWithObstacle((Obstacle.ObstacleData) bodyA.getUserData());
        }
    }

    @Override
    public void beginContact(Contact contact) {
        detectBallAndObstacleCollision(contact);
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
