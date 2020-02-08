package ke.co.zerodaygames.icegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;

public class Obstacle extends Sprite implements Disposable {
    public Body body;
    private ParticleEffect particleEffect;
    private ObstacleData obstacleData;

    public Obstacle(Skin gameSkin, World world) {
        super(gameSkin.getRegion("obstacle_1"));

        obstacleData = new ObstacleData();

        setPosition(Config.GAME_WIDTH / 2f - getWidth() / 2f, Config.GAME_HEIGHT);
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(getX() + getWidth() / 2f, getY() + getHeight() / 2f);
        bodyDef.fixedRotation = true;


        body = world.createBody(bodyDef);
        body.setUserData(obstacleData);

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(getWidth() * .425f, getHeight() * .25f, new Vector2(0, 0), 0);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.isSensor = true;

        body.createFixture(fixtureDef);

        polygonShape.dispose();

        particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal("pfx/obstacle_flame/obstacle_flame.pfx"),
                Gdx.files.internal("pfx/obstacle_flame/"));

        particleEffect.reset();
        particleEffect.setPosition(getX() + getWidth() / 2f, getY());
    }

    public void render(Batch batch, float delta) {
        //Draw the sprite at the position of the body
        setPosition(body.getPosition().x - getWidth() / 2f, body.getPosition().y - getHeight() / 2f);
        particleEffect.update(delta);
        particleEffect.setPosition(getX() + getWidth() / 2f, getY() + 15);


        draw(batch);
        particleEffect.draw(batch);
    }

    public ObstacleData getObstacleData() {
        return obstacleData;
    }

    @Override
    public void dispose() {
        particleEffect.dispose();
    }

    public static class ObstacleData {
        public boolean collidedWithPlayer = false;
    }
}
