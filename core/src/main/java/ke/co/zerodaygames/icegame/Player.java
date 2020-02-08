package ke.co.zerodaygames.icegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;

public class Player extends Sprite implements Disposable {
    private ParticleEffect particleEffect;
    private Body body;
    private float shapeSize;
    private PlayerData playerData;
    private boolean justColliedWithObstacle = false;
    private Sound sizzle;
    private GameScreen gameScreen;

    public Player(Skin gameSkin, PlayerPlatform playerPlatform, World world, GameScreen gameScreen) {
        super(gameSkin.getRegion("player"));
        this.gameScreen = gameScreen;
        playerData = new PlayerData();


        setSize(70f, 70f);
        setOrigin(getWidth() / 2f, getHeight() / 2f);
        setPosition(Config.GAME_WIDTH / 2f - getWidth() / 2f, playerPlatform.getTop());
        shapeSize = getWidth() * .4f;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(getX() + getWidth() / 2f, getY() + getHeight() / 2f);
        bodyDef.gravityScale = 100;

        body = world.createBody(bodyDef);
        body.setUserData(playerData);

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(shapeSize);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        fixtureDef.density = 100;
        fixtureDef.restitution = .2f;
        fixtureDef.friction = 1f;

        body.createFixture(fixtureDef);

        circleShape.dispose();

        particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal("pfx/player_hit/player_hit.pfx"),
                Gdx.files.internal("pfx/player_hit/"));

        sizzle = Gdx.audio.newSound(Gdx.files.internal("sfx/sizzle-cig-extinguish.wav"));


    }

    public void render(SpriteBatch spriteBatch, float dt) {
        setPosition(body.getPosition().x - getWidth() / 2f, body.getPosition().y - getHeight() / 2f);
        setRotation((float) (body.getAngle() / Config.DEGREES_TO_RADIANS));

        super.draw(spriteBatch);

        // check if player has fallen below the screen
        if (!gameScreen.isGameOver() && getY() < -getHeight() - 5) {
            gameScreen.gameOver();
        }

        if (justColliedWithObstacle) {
            particleEffect.setPosition(getX() + getWidth() / 2f, getY() + getWidth() / 2f);
            particleEffect.update(dt);
            particleEffect.draw(spriteBatch);

            if (particleEffect.isComplete()) {
                justColliedWithObstacle = false;
            }
        }
    }

    public void collidedWithObstacle() {
        // Reduce HP
        playerData.HP--;
        justColliedWithObstacle = true;
        particleEffect.reset();
        sizzle.play();
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    @Override
    public void dispose() {
        particleEffect.dispose();
        sizzle.dispose();
    }

    public static class PlayerData {
        public int HP = 3;
    }

}
