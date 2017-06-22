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
    //the current moving velocity of the user
    private Velocity currentVelocity = new Velocity(-1,-1,-1);
    //the current calculated position of the user's phone in world's space
    private Position cachedPosition = new Position(-1,-1,-1);
    //Array of all the stored position of the users
    private Position[] savedPositions = new Position[6];
    //which step of the setup mode the user is at
    private int currentTrackingPositionIndex = -1;

    private long lastUpdateTime = -1;

    public PositionManager() {
        currentTrackingPositionIndex = -1;
        savedPositions = new Position[6];
        currentVelocity = new Velocity(-1,-1,-1);
        cachedPosition = new Position(-1,-1,-1);
    }

    public void updatePosition(double acceX, double acceY, double acceZ, double deltaTime) {
        if (currentTrackingPositionIndex < 0) {
            return;
        }
        //velocity = acceleration * deltaTime
        currentVelocity.add(Velocity.getVelocity(acceX,acceY,acceZ,deltaTime));
        //position = velocity * deltaTime
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
//            currentVelocity = new Velocity(0,0,0);
//            cachedPosition = new Position(0,0,0);
        }
    }

    //the current world position of the phone. This function is used to display the information on the screen
    public double[] getCurrentPosition() {
        if (currentTrackingPositionIndex <  0) {
            return new double[] {-1,-1,-1};
        }
        return new double[] {
            roundNumber(cachedPosition.x),
            roundNumber(cachedPosition.y),
            roundNumber(cachedPosition.z)};
    }

    //the position that is actually saved to the hardware.
    public double[] getSavedPosition() {
        if (currentTrackingPositionIndex <  0) {
            return new double[] {-1,-1,-1};
        }
        return new double[] {
                roundNumber(savedPositions[currentTrackingPositionIndex].x),
                roundNumber(savedPositions[currentTrackingPositionIndex].y),
                roundNumber(savedPositions[currentTrackingPositionIndex].z) };
    }

    public double[] getCurrentVelocity() {
        if (currentTrackingPositionIndex < 0) {
            return new double[] {-1,-1,-1};
        }
        return new double[] {
                roundNumber(currentVelocity.x),
                roundNumber(currentVelocity.y),
                roundNumber(currentVelocity.z)};
    }

    private double roundNumber(double d) {
        return Math.round(d * 100) / 100d;
    }

}
