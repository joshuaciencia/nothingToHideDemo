package joshuaciencia.nothingtohide;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import joshuaciencia.nothingtohide.math.Intersect;
import joshuaciencia.nothingtohide.math.Segment2D;


public class Game extends DirectGame{

    private Viewport viewport;
    private OrthographicCamera camera;

    public static final int VIEWPORT_WIDTH = 840;
    public static final int VIEWPORT_HEIGHT = 360;

    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private Texture background, foreground;

    private Segment2D[] segments;
    private Segment2D[] rays;


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
        //drawMaskBackground();
        drawCutOffLight();
        //drawForeground();
    }

    private void drawMaskBackground(){

        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);

        Gdx.gl.glDepthFunc(GL20.GL_LESS);

        Gdx.gl.glDepthMask(true);

        batch.begin();

        Gdx.gl.glColorMask(true, true, true, true);

        batch.draw(background, 0, 0);

        batch.end();
    }

    private void drawCutOffLight(){
        createRays();
        sortRays();
        castRays();

//        ScreenUtils.clear(0, 0, 0, 1);
        shapeRenderer.begin();
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        for(int r = 0; r < rays.length; r++) {

            Segment2D ray1 = rays[r];
            Segment2D ray2;

            if(r == rays.length-1) {
                ray2 = rays[0];
            }else {
                ray2 = rays[r + 1];
            }
            shapeRenderer.setColor(1, 0,0,1);
            shapeRenderer.triangle(ray1.getA().x, ray1.getA().y, ray1.getB().x, ray1.getB().y, ray2.getB().x, ray2.getB().y);

        }
/*        shapeRenderer.setColor(1, 0,0,1);
        int mouseX = Gdx.input.getX();
        int mouseY = VIEWPORT_HEIGHT - Gdx.input.getY();
        shapeRenderer.triangle(
                10 + mouseX, 10 + mouseY,
                20 + mouseX, 30 + mouseY,
                30 + mouseX, 10 + mouseY);*/

        shapeRenderer.end();
    }

    private void drawForeground(){
        batch.begin();

        Gdx.gl.glColorMask(true, true, true, true);

        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

        Gdx.gl.glDepthFunc(GL20.GL_EQUAL);

        batch.draw(foreground, 0, 0);
        batch.end();
    }

    private void createRays() {
        rays = new Segment2D[segments.length * 3];

        int counter = 0;

        for(int s = 0; s < segments.length; s++) {
            int mouseX = Gdx.graphics.getWidth() / 2;
            int mouseY = Gdx.graphics.getHeight() / 2;
            Vector2 a = new Vector2(mouseX, mouseY);
            Vector2 b = segments[s].getB();
            Vector2 c = b.sub(a).nor();

            Segment2D s1 = new Segment2D(
                    a,
                    b
            );
            Segment2D s2 = new Segment2D(
                    a,
                    a.add(c.setAngleDeg(c.angleDeg() + 0.00001f).nor().scl(VIEWPORT_WIDTH))
            );
            Segment2D s3 = new Segment2D(
                    a,
                    a.add(c.setAngleDeg(c.angleDeg() - 0.00001f).nor().scl(VIEWPORT_WIDTH))
            );



            rays[counter] = s1;
            rays[counter + 1] = s2;
            rays[counter + 2] = s3;
            counter += 3;
        }

    }

    private void sortRays() {

        Array<Segment2D> list = new Array();

        for(int r = 0; r < rays.length; r++) {
            list.add(rays[r]);
        }

        list.sort((e1, e2) -> {

            Vector2 v1 = e1.getB().sub(e1.getA());
            Vector2 v2 = e2.getB().sub(e2.getA());



            if(v1.angleDeg() < v2.angleDeg())
                return 1;
            if(v1.angleDeg() > v2.angleDeg())
                return 2;

            return 0;
        });

        for(int i = 0; i < list.size; i++) {
            rays[i] = list.get(i);
        }

    }

    private void castRays() {
        for(int r = 0; r < rays.length; r++) {
            Segment2D ray = rays[r];

            Vector2 closestIntersect = ray.getB();
            float distToIntersect = VIEWPORT_WIDTH;

            for(int i = 0; i < segments.length; i++) {

                Intersect inter = getIntersection(ray, segments[i]);

                if(inter != null) {

                    float d =  inter.getDistance();

                    if(distToIntersect > d) {
                        distToIntersect = d;
                        closestIntersect = inter.getIntersect();
                    }
                }

            }

            ray.setB(closestIntersect);

        }
    }

    private Intersect getIntersection(Segment2D s1, Segment2D s2) {

        Vector2 p = s1.getA();
        Vector2 q = s2.getA();
        Vector2 r = s1.getB().sub(s1.getA());
        Vector2 s = s2.getB().sub(s2.getA());

        float t = (crossProduct(q, s) - crossProduct(p, s)) / crossProduct(r, s);
        float u = (crossProduct(p, r) - crossProduct(q, r)) / crossProduct(s, r);

        if(t >= 0 && t <= 1 && u >= 0 && u <= 1) {
            return new Intersect(new Vector2(p.x + t * r.x, p.y + t * r.y), t);
        }
        return null;
    }

    private float crossProduct(Vector2 a, Vector2 b) {
        return a.x * b.y - a.y * b.x;
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
