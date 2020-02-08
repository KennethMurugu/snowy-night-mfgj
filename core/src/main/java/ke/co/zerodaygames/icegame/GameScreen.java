package ke.co.zerodaygames.icegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;

/**
 * First screen of the application. Displayed after the application is created.
 */
public class GameScreen implements Screen {
    private static final String TAG = GameScreen.class.getSimpleName();

    private World world;
    private Box2DDebugRenderer box2DDebugRenderer;
    private OrthographicCamera orthographicCamera;
    private IceGameMain iceGameMain;
    private SpriteBatch spriteBatch;
    private Player player;
    private PlayerPlatform playerPlatform;
    private GameContactListener gameContactListener;


    private Skin gameSkin;
    private ObstacleManager obstacleManager;
    private boolean isGameOver = false, isGamePaused = false;
    private int scoreCount = 0;

    private Stage stage;
    private Label scoreLabel, hpLabel;
    private boolean isTouchingLeft = false, isTouchingRight = false;
    private Table tableHP;
    private Sound scoreIncSFX;
    private Table pauseUiTable, gameOverUiTable;
    private Image playerHpLossImage, tutorialOverlayImage;
    private Preferences preferences;

    public GameScreen(IceGameMain iceGameMain, SpriteBatch spriteBatch, Skin gameSkin) {
        this.iceGameMain = iceGameMain;
        this.spriteBatch = spriteBatch;
        this.gameSkin = gameSkin;

        preferences = Gdx.app.getPreferences(Config.APP_PREFS);

        orthographicCamera = new OrthographicCamera(Config.GAME_WIDTH, Config.GAME_HEIGHT);
        orthographicCamera.setToOrtho(false);

        stage = new Stage(new StretchViewport(Config.GAME_WIDTH, Config.GAME_HEIGHT, orthographicCamera), spriteBatch);
        Gdx.input.setInputProcessor(stage);

        world = new World(new Vector2(0, -10), true);
//        box2DDebugRenderer = new Box2DDebugRenderer();

        scoreIncSFX = Gdx.audio.newSound(Gdx.files.internal("sfx/gem_ping.wav"));

        playerPlatform = new PlayerPlatform(gameSkin, world);
        player = new Player(gameSkin, playerPlatform, world, this);
        obstacleManager = new ObstacleManager(gameSkin, this, world);
        gameContactListener = new GameContactListener(this);
        world.setContactListener(gameContactListener);

        //HP, Score, Pause
        createUpperUI();
        // Player movement controls
        createControls();
        // Pause Menu
        createPauseUI();
        // Game Over menu
        createGameOverUi();

        playerHpLossImage = new Image(gameSkin.getRegion("player_hp_loss"));
        tutorialOverlayImage = new Image(gameSkin.getRegion("tutorial_overlay"));

//        stage.setDebugAll(true);
    }

    private void createPauseUI() {
        Image gamePausedImage = new Image(gameSkin.getRegion("game_paused_overlay"));
        gamePausedImage.pack();
        gamePausedImage.setOrigin(Align.center);
        gamePausedImage.setPosition(stage.getWidth() / 2f - gamePausedImage.getWidth() / 2f,
                stage.getHeight() / 2f - gamePausedImage.getHeight() / 2f);

        Button btnContinue = new Button(gameSkin.getDrawable("btn_continue_up"), gameSkin.getDrawable("btn_continue_down"));
        Button btnMainMenu = new Button(gameSkin.getDrawable("btn_main_menu_up"), gameSkin.getDrawable("btn_main_menu_down"));

        pauseUiTable = new Table();
        pauseUiTable.setTouchable(Touchable.enabled);
        pauseUiTable.setTransform(true);
        pauseUiTable.setSize(stage.getWidth(), stage.getHeight());
        pauseUiTable.setOrigin(Align.center);

        pauseUiTable.row().spaceBottom(50f);
        pauseUiTable.add(gamePausedImage);
        pauseUiTable.row().spaceBottom(35f);
        pauseUiTable.add(btnContinue);
        pauseUiTable.row().spaceBottom(35f);
        pauseUiTable.add(btnMainMenu);


        btnContinue.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                togglePause();
            }
        });

        btnMainMenu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                iceGameMain.setScreen(new MainMenuScreen(iceGameMain, spriteBatch, gameSkin));
            }
        });
    }

    private void createGameOverUi() {
        Image gameOverImage = new Image(gameSkin.getRegion("game_over_overlay"));
        gameOverImage.pack();
        gameOverImage.setOrigin(Align.center);
        gameOverImage.setPosition(stage.getWidth() / 2f - gameOverImage.getWidth() / 2f,
                stage.getHeight() / 2f - gameOverImage.getHeight() / 2f);

        Label labelGameOverMsg = new Label("You failed to protect Snowy...\nTry again?",
                gameSkin.get("labelstyle_.75x", Label.LabelStyle.class));
        labelGameOverMsg.setAlignment(Align.center);

        Button btnRestart = new Button(gameSkin.getDrawable("btn_restart_game_up"), gameSkin.getDrawable("btn_restart_game_down"));
        Button btnMainMenu = new Button(gameSkin.getDrawable("btn_main_menu_up"), gameSkin.getDrawable("btn_main_menu_down"));

        gameOverUiTable = new Table();
        gameOverUiTable.setTouchable(Touchable.enabled);
        gameOverUiTable.setTransform(true);
        gameOverUiTable.setSize(stage.getWidth(), stage.getHeight());
        gameOverUiTable.setOrigin(Align.center);

        gameOverUiTable.row().spaceBottom(30f);
        gameOverUiTable.add(gameOverImage);
        gameOverUiTable.row().spaceBottom(50f);
        gameOverUiTable.add(labelGameOverMsg).growX();
        gameOverUiTable.row().spaceBottom(35f);
        gameOverUiTable.add(btnRestart);
        gameOverUiTable.row().spaceBottom(35f);
        gameOverUiTable.add(btnMainMenu);


        btnRestart.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                iceGameMain.setScreen(new GameScreen(iceGameMain, spriteBatch, gameSkin));
            }
        });

        btnMainMenu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                iceGameMain.setScreen(new MainMenuScreen(iceGameMain, spriteBatch, gameSkin));
            }
        });
    }

    private void createControls() {
        Actor controlLeft = new Actor();
        controlLeft.setSize(stage.getWidth() / 2f, stage.getHeight() * .65f);
        controlLeft.setPosition(0, 0);
        stage.addActor(controlLeft);

        Actor controlRight = new Actor();
        controlRight.setSize(stage.getWidth() / 2f, stage.getHeight() * .65f);
        controlRight.setPosition(stage.getWidth() - controlRight.getWidth(), 0);
        stage.addActor(controlRight);


        // Add listeners
        controlLeft.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                isTouchingLeft = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);

                isTouchingLeft = false;
            }
        });

        controlRight.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                isTouchingRight = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);

                isTouchingRight = false;
            }
        });
    }

    private void createUpperUI() {
        // Hp UI
        Image image = new Image(gameSkin.getRegion("hp_heart"));
        hpLabel = new Label("X3", gameSkin.get("labelstyle_.75x", Label.LabelStyle.class));
        tableHP = new Table();
        tableHP.setTransform(true);
        tableHP.add(image).width(50f).height(50f).spaceRight(15f);
        tableHP.add(hpLabel);
        tableHP.pack();
        tableHP.setOrigin(Align.center);
        // Score label
        scoreLabel = new Label("0", gameSkin.get("labelstyle_1x", Label.LabelStyle.class));
        scoreLabel.setAlignment(Align.center);
        // Pause button
        Button.ButtonStyle buttonStyle = new Button.ButtonStyle();
        buttonStyle.up = gameSkin.getDrawable("btn_pause_up");
        buttonStyle.down = gameSkin.getDrawable("btn_pause_up");
        Button btnPause = new Button(buttonStyle);

        Table upperUiTable = new Table();
        upperUiTable.padLeft(15f).padRight(10f).padTop(10f);
        upperUiTable.add(tableHP);
        upperUiTable.add(scoreLabel).padLeft(-40f).expandX();
        upperUiTable.add(btnPause).width(60f).height(60f);
        upperUiTable.pack();
        upperUiTable.setWidth(stage.getWidth());
        upperUiTable.setPosition(0, stage.getHeight() - upperUiTable.getHeight());

        stage.addActor(upperUiTable);

        // Add listeners
        btnPause.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                togglePause();
            }
        });
    }

    private void togglePause() {
        isGamePaused = !isGamePaused;
        Gdx.app.log(TAG, "[togglePause] isGamePaused=" + isGamePaused);

        if (isGamePaused) {
            stage.addActor(pauseUiTable);

            pauseUiTable.clearActions();
            pauseUiTable.setScale(0f);
            pauseUiTable.addAction(Actions.scaleTo(1f, 1f, .5f, Interpolation.swingOut));
        } else {
            pauseUiTable.remove();
        }
    }

    @Override
    public void show() {
        // Show controls
        tutorialOverlayImage.clearActions();
        tutorialOverlayImage.addAction(Actions.delay(3f, Actions.fadeOut(.5f)));
        tutorialOverlayImage.addAction(Actions.after(Actions.removeActor()));
        stage.addActor(tutorialOverlayImage);
    }

    @Override
    public void render(float delta) {
        spriteBatch.setProjectionMatrix(orthographicCamera.combined);
        spriteBatch.begin();

        spriteBatch.setColor(Color.WHITE);
        playerPlatform.draw(spriteBatch);
        player.render(spriteBatch, delta);
        obstacleManager.render(delta, spriteBatch);
        spriteBatch.end();

        stage.act();
        stage.draw();

        if (!isGameOver && !isGamePaused) {
            // Input controls
            if (isTouchingLeft) {
                playerPlatform.rotateAntiClockwise();
            } else if (isTouchingRight) {
                playerPlatform.rotateClockwise();
            }

            // Step through physics sim
            world.step(1 / 60f, 6, 2);
        }


//        box2DDebugRenderer.render(world, orthographicCamera.combined);
    }

    public void playerCollidedWithObstacle(Obstacle.ObstacleData obstacleData) {
        //If body has already collided, ignore; prevents multiple collisions
        if (obstacleData.collidedWithPlayer) return;

        obstacleData.collidedWithPlayer = true;
        player.collidedWithObstacle();
        hpLabel.setText("X" + player.getPlayerData().HP);

        tableHP.clearActions();
        tableHP.setScale(1f);
        tableHP.addAction(Actions.sequence(
                Actions.scaleTo(1.2f, 1.2f, .2f, Interpolation.swingOut),
                Actions.scaleTo(1f, 1f, .2f, Interpolation.pow2)
        ));

        playerHpLossImage.setPosition(player.getX() - playerHpLossImage.getWidth() / 2f, player.getY() + player.getHeight());
        playerHpLossImage.setColor(1, 1, 1, 1);
        playerHpLossImage.clearActions();
        playerHpLossImage.addAction(Actions.parallel(
                Actions.alpha(0f, 1f),
                Actions.moveBy(0, 25f, 1f)
        ));
        playerHpLossImage.addAction(Actions.after(Actions.removeActor()));
        stage.addActor(playerHpLossImage);

        // Check if the player is out of HP
        if (player.getPlayerData().HP <= 0) {
            //TODO End game now
            gameOver();
        }
    }

    public void incPlayerScore() {
        scoreCount++;
        scoreLabel.setText(scoreCount);
        scoreIncSFX.play(.5f);
    }

    public void gameOver() {
        isGameOver = true;

        stage.addActor(gameOverUiTable);
        gameOverUiTable.clearActions();
        gameOverUiTable.setScale(0f);
        gameOverUiTable.addAction(Actions.scaleTo(1f, 1f, 1f, Interpolation.swingOut));

        // Check if current high score has been beaten
        int currentHighScore = preferences.getInteger(Config.HIGH_SCORE_PREF, 0);
        if (scoreCount > currentHighScore) {
            preferences.putInteger(Config.HIGH_SCORE_PREF, scoreCount)
                    .flush();
        }
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    @Override
    public void resize(int width, int height) {
        // Resize your screen here. The parameters represent the new window size.
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
    }

    @Override
    public void dispose() {
        // Destroy screen's assets here.
        obstacleManager.dispose();
        world.dispose();
        stage.dispose();
        scoreIncSFX.dispose();
        player.dispose();
    }
}