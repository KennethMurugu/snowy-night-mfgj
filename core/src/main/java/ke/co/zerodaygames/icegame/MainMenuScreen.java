package ke.co.zerodaygames.icegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public class MainMenuScreen implements Screen {
    private OrthographicCamera orthographicCamera;
    private IceGameMain iceGameMain;
    private SpriteBatch spriteBatch;
    private Skin gameSkin;
    private Stage stage;

    private Image logoText;
    private Button btnPlay;
            //btnCredits;
    private Label labelHighScore, labelDevelopedForMFGJ;
    private Preferences preferences;

    public MainMenuScreen(IceGameMain iceGameMain, SpriteBatch spriteBatch, Skin gameSkin) {
        this.iceGameMain = iceGameMain;
        this.spriteBatch = spriteBatch;
        this.gameSkin = gameSkin;

        preferences = Gdx.app.getPreferences(Config.APP_PREFS);
        orthographicCamera = new OrthographicCamera(Config.GAME_WIDTH, Config.GAME_HEIGHT);
        orthographicCamera.setToOrtho(false);

        stage = new Stage(new StretchViewport(Config.GAME_WIDTH, Config.GAME_HEIGHT, orthographicCamera), spriteBatch);
        Gdx.input.setInputProcessor(stage);

        logoText = new Image(gameSkin.getRegion("logo_text"));
        logoText.pack();
        logoText.setOrigin(Align.center);
        btnPlay = new Button(gameSkin.getDrawable("btn_play_game_up"), gameSkin.getDrawable("btn_play_game_down"));
//        btnCredits = new Button(gameSkin.getDrawable("btn_credits_up"));
        labelHighScore = new Label("[ORANGE]High Score: " + preferences.getInteger(Config.HIGH_SCORE_PREF, 0),
                gameSkin.get("labelstyle_.75x", Label.LabelStyle.class));
        labelHighScore.setAlignment(Align.center);
        labelDevelopedForMFGJ = new Label("Developed by Kenneth Kimotho\nfor My First Game Jam - Winter 2020",
                gameSkin.get("labelstyle_.5x", Label.LabelStyle.class));
        labelDevelopedForMFGJ.setAlignment(Align.center);
        labelDevelopedForMFGJ.setColor(1, 1, 1, .75f);
        labelDevelopedForMFGJ.pack();
        labelDevelopedForMFGJ.setPosition(stage.getWidth() / 2f - labelDevelopedForMFGJ.getWidth() / 2f, 15);

        Table uiTable = new Table();
        uiTable.setTransform(true);
        uiTable.setSize(stage.getWidth(), stage.getHeight());
        uiTable.setOrigin(Align.center);
        uiTable.top();

        uiTable.row().spaceBottom(50f);
        uiTable.add(logoText).padTop(40f);
        uiTable.row().spaceBottom(25f);
        uiTable.add(btnPlay);
        uiTable.row().spaceBottom(35f);
//        uiTable.add(btnCredits);
        uiTable.add(labelHighScore).growX();

        stage.addActor(uiTable);
        stage.addActor(labelDevelopedForMFGJ);

        btnPlay.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                uiTable.setOrigin(Align.center);
                uiTable.addAction(Actions.sequence(
                        Actions.scaleTo(0f, 0f, .5f, Interpolation.swingIn),
                        Actions.run(new Runnable() {
                            @Override
                            public void run() {
                                iceGameMain.setScreen(new GameScreen(iceGameMain, spriteBatch, gameSkin));
                            }
                        })
                ));
            }
        });
    }

    @Override
    public void show() {
        // Intro animations
        logoText.setScale(0f);
        logoText.addAction(Actions.scaleTo(1f, 1f, 1f, Interpolation.swingOut));

        btnPlay.setColor(1, 1, 1, 0);
        btnPlay.addAction(Actions.delay(1.5f, Actions.alpha(1, .35f)));

//        btnCredits.setColor(1, 1, 1, 0);
//        btnCredits.addAction(Actions.delay(1.75f, Actions.alpha(1, .35f)));

        labelHighScore.setColor(1, 1, 1, 0);
        labelHighScore.addAction(Actions.delay(1.75f, Actions.alpha(1, .35f)));
    }

    @Override
    public void render(float delta) {
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
