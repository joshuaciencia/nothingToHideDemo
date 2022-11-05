package joshuaciencia.nothingtohide;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import joshuaciencia.nothingtohide.math.Segment2D;

public class Game extends DirectGame{

    private Viewport viewport;
    private OrthographicCamera camera;

    public static final int VIEWPORT_WIDTH = 840;
    public static final int VIEWPORT_HEIGHT = 360;

    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private Texture background, foreground;

    Segment2D[] segments;

    @Override
    public void create() {
        super.create();
        camera = new OrthographicCamera();
        viewport = new FitViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, camera);
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
        Gdx.gl20.glLineWidth(2);
        foreground = new Texture(Gdx.files.internal("foreground.png"));
        background = new Texture(Gdx.files.internal("background.png"));
        batch = new SpriteBatch();

        segments = new Segment2D[] {

            // Border
            new Segment2D(new Vector2(0,0), new Vector2(VIEWPORT_WIDTH,0)),
            new Segment2D(new Vector2(VIEWPORT_WIDTH,0), new Vector2(VIEWPORT_WIDTH,VIEWPORT_HEIGHT)),
            new Segment2D(new Vector2(VIEWPORT_WIDTH, VIEWPORT_HEIGHT), new Vector2(0,VIEWPORT_HEIGHT)),
            new Segment2D(new Vector2(0, VIEWPORT_HEIGHT), new Vector2(0,0)),

            // polygon #1

            new Segment2D(new Vector2(100, 150), new Vector2(120, 50)),
            new Segment2D(new Vector2(120, 50), new Vector2(200, 80)),
            new Segment2D(new Vector2(200, 80), new Vector2(140, 210)),
            new Segment2D(new Vector2(140, 210), new Vector2(100, 150)),

            // polygon #2

            new Segment2D(new Vector2(100, 200), new Vector2(120, 250)),
            new Segment2D(new Vector2(120, 250), new Vector2(60, 300)),
            new Segment2D(new Vector2(60, 300), new Vector2(100, 200)),

            // polygon #3

            new Segment2D(new Vector2(200, 260), new Vector2(220, 150)),
            new Segment2D(new Vector2(220, 150), new Vector2(300, 200)),
            new Segment2D(new Vector2(300, 200), new Vector2(350, 320)),
            new Segment2D(new Vector2(350, 320), new Vector2(200, 260)),

            // polygon #4

                new Segment2D(new Vector2(540, 60), new Vector2(560, 40)),
                new Segment2D(new Vector2(560, 40), new Vector2(570, 70)),
                new Segment2D(new Vector2(570, 70), new Vector2(540, 60)),

            // polygon #5

            new Segment2D(new Vector2(650, 190), new Vector2(760, 170)),
            new Segment2D(new Vector2(760, 170), new Vector2(740, 270)),
            new Segment2D(new Vector2(740, 270), new Vector2(630, 290)),
            new Segment2D(new Vector2(630, 290), new Vector2(650, 190)),

            // polygon #6

            new Segment2D(new Vector2(600, 95), new Vector2(780, 50)),
            new Segment2D(new Vector2(780, 50), new Vector2(680, 150)),
            new Segment2D(new Vector2(680, 150), new Vector2(600, 95)),
        };

    }

    @Override
    public void render() {
        //clear screen

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //clear depth buffer with 1.0 :

        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);


        //set the function to LESS

        Gdx.gl.glDepthFunc(GL20.GL_LESS);

        //enable depth writing

        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

        //Enable depth mask and disable RGBA color writing

        Gdx.gl.glDepthMask(true);

        batch.begin();

        Gdx.gl.glColorMask(true, true, true, true);

        batch.draw(background, 0, 0);

        batch.end();

        shapeRenderer.begin();
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);

        for (Segment2D segment: segments){
            shapeRenderer.rectLine(segment.getA(), segment.getB(), 2);

        }
        shapeRenderer.end();

        batch.begin();
        //Enable RGBA color writing

        Gdx.gl.glColorMask(true, true, true, true);

        //Enable testing

        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

        //Discards pixels outside masked shapes

        Gdx.gl.glDepthFunc(GL20.GL_EQUAL);

        batch.draw(foreground, 0, 0);

        batch.end();
    }

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
    }

    @Override
    public void pause() {
        super.pause();
    }

    @Override
    public void resume() {
        super.resume();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        super.resize(width, height);
    }
}
