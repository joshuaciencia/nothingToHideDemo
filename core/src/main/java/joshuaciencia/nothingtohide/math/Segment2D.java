package joshuaciencia.nothingtohide.math;

import com.badlogic.gdx.math.Vector2;
import joshuaciencia.nothingtohide.Game;

public class Segment2D
{
    private Vector2 a;
    private Vector2 b;

    public Segment2D(Vector2 a, Vector2 b){
        b.y = Game.VIEWPORT_HEIGHT - b.y;
        a.y = Game.VIEWPORT_HEIGHT - a.y;
        this.a = a;
        this.b = b;
    }

    public Vector2 getA() {
        return a;
    }

    public void setA(Vector2 a) {
        this.a = a;
    }

    public Vector2 getB() {
        return b;
    }

    public void setB(Vector2 b) {
        this.b = b;
    }
}
