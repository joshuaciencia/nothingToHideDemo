package joshuaciencia.nothingtohide;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Game extends DirectGame{

    private Texture iEye;
    private Viewport viewport;
    private OrthographicCamera camera;
    private TextureRegion textureRegion;

    public static final int VIEWPORT_WIDTH = 1000;
    public static final int VIEWPORT_HEIGHT = 500;


    @Override
    public void create() {
        super.create();
        iEye = new Texture(Gdx.files.internal("props.png"));
        textureRegion = new TextureRegion(iEye);
        camera = new OrthographicCamera();
        viewport = new FitViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, camera);

    }

    @Override
    public void render() {
        super.render();
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(textureRegion, 0, 0);
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
