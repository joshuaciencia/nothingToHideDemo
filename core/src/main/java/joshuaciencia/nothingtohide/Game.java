package joshuaciencia.nothingtohide;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Game extends DirectGame{

    private Texture iEye;
    private Viewport viewport;
    private TextureRegion textureRegion;

    public static final int VIEWPORT_WIDTH = 800;
    public static final int VIEWPORT_HEIGHT = 800;

    @Override
    public void create() {
        super.create();
        iEye = new Texture(Gdx.files.internal("carpet.png"));
        textureRegion = new TextureRegion(iEye);
        viewport = new FitViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
    }

    @Override
    public void render() {
        super.render();
        float deltaTime = Math.min(Gdx.graphics.getDeltaTime(), 1.0f / 60.0f);
        batch.setProjectionMatrix(viewport.getCamera().combined);
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
        viewport.update(width, height);
        super.resize(width, height);
    }
}
