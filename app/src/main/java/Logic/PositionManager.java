package Logic;

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
    public Position[] savedPositions = new Position[6];
    private int currentTrackingPositionIndex = -1;

    public PositionManager() {
        currentTrackingPositionIndex = -1;
        savedPositions = new Position[6];
        currentVelocity = new Velocity(-1,-1,-1);
        cachedPosition = new Position(-1,-1,-1);
    }

    public void updatePosition(double acceX, double acceY, double acceZ, double deltaTime) {
        if (currentTrackingPositionIndex == -1 || currentTrackingPositionIndex == 2) {
            return;
        }
        currentVelocity.add(Velocity.getVelocity(acceX,acceY,acceZ,deltaTime));
        cachedPosition.add(Position.getPosition(currentVelocity.x, currentVelocity.y, currentVelocity.z, deltaTime));
    }

    //For right down and left down.
    private void registerInitialPosition(int index) {
        savedPositions[index] = new Position(0,0,0);
        currentVelocity = new Velocity(0,0,0);
        cachedPosition = new Position(0,0,0);
    }

    public void registerPosition() {
        currentTrackingPositionIndex++;
        if (currentTrackingPositionIndex == 0 || currentTrackingPositionIndex == 3) {
            registerInitialPosition(currentTrackingPositionIndex);
        } else {
            savedPositions[currentTrackingPositionIndex] = cachedPosition;
            currentVelocity = new Velocity(0,0,0);
            cachedPosition = new Position(0,0,0);
        }
    }

    public double[] getCurrentPosition() {
        if (currentTrackingPositionIndex <  0) {
            return new double[] {-1,-1,-1};
        }
        return new double[] {
            savedPositions[currentTrackingPositionIndex].x,
            savedPositions[currentTrackingPositionIndex].y,
            savedPositions[currentTrackingPositionIndex].z };
    }
}
