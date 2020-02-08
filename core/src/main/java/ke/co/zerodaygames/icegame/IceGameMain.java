package ke.co.zerodaygames.icegame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class IceGameMain extends Game {
    private SpriteBatch spriteBatch;
    private Skin gameSkin;
    private TextureRegion bg;
    private Array<Snowflake> snowflakes;
    private Music music;

    @Override
    public void create() {
        // Check stored preferences for high score
        Preferences preferences = Gdx.app.getPreferences(Config.APP_PREFS);
        int highScore = preferences.getInteger(Config.HIGH_SCORE_PREF, -1);
        if(highScore == -1){
            preferences.putInteger(Config.HIGH_SCORE_PREF, 0);
            preferences.flush();
        }

        spriteBatch = new SpriteBatch();
        gameSkin = new Skin(new TextureAtlas(Gdx.files.internal("game_pack.atlas")));
        bg = gameSkin.get("ice_bg", TextureRegion.class);

        music = Gdx.audio.newMusic(Gdx.files.internal("sfx/Song-My-Mother-Taught-Me.mp3"));
        music.play();
        music.setVolume(.35f);
        music.setLooping(true);


        FreeTypeFontGenerator.FreeTypeFontParameter freeTypeFontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        freeTypeFontParameter.color = Color.WHITE;
        freeTypeFontParameter.shadowColor = new Color(Color.BLACK).sub(0, 0, 0, .7f);
        freeTypeFontParameter.shadowOffsetX = 2;
        freeTypeFontParameter.shadowOffsetY = 2;


        FreeTypeFontGenerator freeTypeFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Nunito-Bold.ttf"));
        freeTypeFontParameter.size = 48;
        gameSkin.add("font_1x", getFont(freeTypeFontGenerator, freeTypeFontParameter));
        freeTypeFontParameter.size = 36;
        gameSkin.add("font_.75x", getFont(freeTypeFontGenerator, freeTypeFontParameter));
        freeTypeFontParameter.size = 24;
        gameSkin.add("font_.5x", getFont(freeTypeFontGenerator, freeTypeFontParameter));

        freeTypeFontGenerator.dispose();


        // Label styles
        Label.LabelStyle labelStyle_1x = new Label.LabelStyle();
        labelStyle_1x.font = gameSkin.getFont("font_1x");
        Label.LabelStyle labelStyle_point_75 = new Label.LabelStyle();
        labelStyle_point_75.font = gameSkin.getFont("font_.75x");
        Label.LabelStyle labelStyle_point_5 = new Label.LabelStyle();
        labelStyle_point_5.font = gameSkin.getFont("font_.5x");

        gameSkin.add("labelstyle_1x", labelStyle_1x);
        gameSkin.add("labelstyle_.75x", labelStyle_point_75);
        gameSkin.add("labelstyle_.5x", labelStyle_point_5);

        snowflakes = new Array<>(50);
        for (int i = 0; i < 50; i++) {
            Snowflake snowflake = new Snowflake(gameSkin.getRegion("snowflake"));
            snowflakes.add(snowflake);
        }

        setScreen(new MainMenuScreen(this, spriteBatch, gameSkin));
    }

    private BitmapFont getFont(FreeTypeFontGenerator freeTypeFontGenerator, FreeTypeFontGenerator.FreeTypeFontParameter fontParameter){
        BitmapFont font = freeTypeFontGenerator.generateFont(fontParameter);
        font.getData().markupEnabled = true;
        return font;
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        spriteBatch.begin();
        spriteBatch.setColor(Color.WHITE);
        spriteBatch.draw(bg, 0, 0);
        spriteBatch.end();

        super.render();

        spriteBatch.begin();
        for (int i = 0; i < snowflakes.size; i++) {
            Snowflake snowflake = snowflakes.get(i);
            snowflake.setPosition(snowflake.getX() - (snowflake.getSpeed() * .3f * delta), snowflake.getY() - (snowflake.getSpeed() * delta));

            if (snowflake.getY() < -snowflake.getHeight() || snowflake.getX() < -snowflake.getWidth()) {
                snowflake.reset();
            }

            snowflake.draw(spriteBatch);
        }
        spriteBatch.end();
    }

    @Override
    public void setScreen(Screen screen) {
        if (getScreen() != null)
            getScreen().dispose();

        super.setScreen(screen);
    }

    @Override
    public void dispose() {
        super.dispose();

        if (getScreen() != null)
            getScreen().dispose();
        gameSkin.dispose();
        music.dispose();
        spriteBatch.dispose();
        snowflakes.clear();
    }
}