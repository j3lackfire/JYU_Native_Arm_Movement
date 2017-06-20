package Logic;

import static Logic.Velocity.getVelocity;

/**
 * Created by Le Pham Minh Duc on 6/19/2017.
 */

public class PositionManager {
    //singleton
    private static PositionManager instance;
    public static PositionManager getInstance() {
        if (instance == null) {
            instance = new PositionManager();
        }
        return instance;
    }

    /*
    the six position are
    Right: down, front, up - Left: down front up.
    */
    private Velocity currentVelocity = new Velocity(-1,-1,-1);
    private Position cachedPosition = new Position(-1,-1,-1);
    public Position[] setPositions = new Position[6];
    private int currentTrackingPosition = -1;

    public PositionManager() {
        currentTrackingPosition = -1;
    }

    public void updatePosition(double x, double y, double z, double deltaTime) {
        if (currentTrackingPosition == -1 || currentTrackingPosition == 2) {
            return;
        }
        currentVelocity.add(Velocity.getVelocity(x,y,z,deltaTime));
        cachedPosition.add(Position.getPosition(currentVelocity.x, currentVelocity.y, currentVelocity.z, deltaTime));
    }

    private void registerInitialPosition(int index) {
        setPositions[index] = new Position(0,0,0);
    }

    public void registerNextPosition() {
        currentTrackingPosition ++;
        currentVelocity = new Velocity(0,0,0);
        cachedPosition = new Position(0,0,0);
        if (currentTrackingPosition == 0 || currentTrackingPosition == 3) {
            registerInitialPosition(currentTrackingPosition);
        } else {
            setPositions[currentTrackingPosition] = cachedPosition;
        }
    }
}
