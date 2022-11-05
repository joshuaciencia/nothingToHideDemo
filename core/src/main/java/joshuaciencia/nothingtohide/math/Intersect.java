package joshuaciencia.nothingtohide.math;

import com.badlogic.gdx.math.Vector2;

public class Intersect{
    private Vector2 intersect;
    private float distance;

    public Intersect(Vector2 i, float d){
        this.intersect = i;
        this.distance = d;
    }

    public Vector2 getIntersect() {
        return intersect;
    }

    public float getDistance() {
        return distance;
    }
}
