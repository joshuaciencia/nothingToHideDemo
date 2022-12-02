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

    }

    @Override
    public void render() {
        drawMaskBackground();
        drawCutOffLight();
        drawForeground();
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

        ScreenUtils.clear(0, 0, 0, 1);

        shapeRenderer.begin();
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.5f, 0.5f,0.5f,1);
        for(int r = 0; r < rays.length; r+=1) {

            Segment2D ray1 = rays[r];
            Segment2D ray2;

            if(r == rays.length-1) {
                ray2 = rays[0];
            }else {
                ray2 = rays[r + 1];
            }

            shapeRenderer.triangle(ray2.getB().x, ray2.getB().y, ray1.getA().x, ray1.getA().y, ray1.getB().x, ray1.getB().y);
        }

        shapeRenderer.setColor(0, 1,0,1);
        for(Segment2D segment2D: segments){
            shapeRenderer.line(segment2D.getA(), segment2D.getB());
        }


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

            float mosY = Gdx.input.getY();
            float mosX = Gdx.input.getX();

            mosY = Math.min(mosY, VIEWPORT_HEIGHT - 1);
            mosX = Math.min(mosX, VIEWPORT_WIDTH - 1);
            mosY = Math.max(mosY, 1);
            mosX = Math.max(mosX, 1);

            Vector2 a = new Vector2(mosX, VIEWPORT_HEIGHT - mosY);

            Vector2 b = new Vector2(segments[s].getB());
            Vector2 c = new Vector2(b).sub(a).nor().scl(VIEWPORT_WIDTH);
            Vector2 d = new Vector2(b).sub(a).nor().scl(VIEWPORT_WIDTH);
            c.setAngleDeg(c.angleDeg() + 0.0001f);
            d.setAngleDeg(d.angleDeg() + -0.0001f);



            Segment2D s1 = new Segment2D(
                    a,
                    b
            );
            Segment2D s2 = new Segment2D(
                    a,
                    new Vector2(a).add(c)
            );
            Segment2D s3 = new Segment2D(
                    a,
                    new Vector2(a).add(d)

            );



            rays[counter] = s1;
            rays[counter + 1] = s2;
            rays[counter + 2] = s3;
            counter += 3;
        }

    }

    private void sortRays() {

        Array<Segment2D> list = new Array<>();

        for(int r = 0; r < rays.length; r++) {
            list.add(rays[r]);
        }

        list.sort((e1, e2) -> {

            Vector2 v1 = new Vector2(e1.getB()).sub(e1.getA());
            Vector2 v2 = new Vector2(e2.getB()).sub(e2.getA());


            return Float.compare(v1.angleDeg(), v2.angleDeg());

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

        Vector2 tempS1A = new Vector2(s1.getA());
        Vector2 tempS1B = new Vector2(s1.getB());
        Vector2 tempS2A = new Vector2(s2.getA());
        Vector2 tempS2B = new Vector2(s2.getB());

        Vector2 p = tempS1A;
        Vector2 q = tempS2A;
        Vector2 r = tempS1B.sub(tempS1A);
        Vector2 s = tempS2B.sub(tempS2A);

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
