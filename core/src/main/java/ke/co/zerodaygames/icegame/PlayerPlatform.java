package ke.co.zerodaygames.icegame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class PlayerPlatform extends Sprite {
    public Body body;
    private float shapeHalfWidth, shapeHalfHeight;
    public static final int ROTATE_SPEED = 5, MOVE_SPEED = 5, MAX_ROTATION = 30;

    public PlayerPlatform(Skin gameSkin, World world) {
        super(gameSkin.getRegion("platform"));
        setSize(Config.GAME_WIDTH * .35f, getHeight());
        setOrigin(getWidth()/2f, getHeight()/2f);
        setPosition(Config.GAME_WIDTH / 2f - getWidth() / 2f, 100);
        //Size of the shape depends on the size of the sprite texture
        shapeHalfWidth = getWidth() * .475f;
        shapeHalfHeight = getHeight() * .275f;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(getX() + getWidth() / 2f, getY() + getHeight() / 2f);

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(shapeHalfWidth, shapeHalfHeight);

        this.body = world.createBody(bodyDef);
        body.setUserData("platform");

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.friction = .1f;


        body.createFixture(fixtureDef);


        polygonShape.dispose();

    }

    @Override
    public void draw(Batch batch) {
        //Draw the sprite at the position of the body
        setPosition(body.getPosition().x - getWidth() / 2f, body.getPosition().y - getHeight() / 2f);
        super.draw(batch);
    }

    public float getTop() {
        return body.getPosition().y + shapeHalfHeight * 2f;
    }

    public void rotateClockwise(){
        rotate(-ROTATE_SPEED);
        if(getRotation() < -MAX_ROTATION) {
            setRotation(-MAX_ROTATION);
        }
        body.setTransform(body.getPosition()
                        .add(MOVE_SPEED, 0),
                (float) (getRotation() * Config.DEGREES_TO_RADIANS));
    }

    public void rotateAntiClockwise(){
        rotate(ROTATE_SPEED);
        if(getRotation() > MAX_ROTATION) {
            setRotation(MAX_ROTATION);
        }
        body.setTransform(body.getPosition()
                .sub(MOVE_SPEED, 0),
                (float) (getRotation() * Config.DEGREES_TO_RADIANS));
    }

//    @Override
//    public void rotate(float degrees) {
//        super.rotate(degrees);
//        body.setTransform(body.getPosition(), (float) (getRotation() * Config.DEGREES_TO_RADIANS));
//    }
}
