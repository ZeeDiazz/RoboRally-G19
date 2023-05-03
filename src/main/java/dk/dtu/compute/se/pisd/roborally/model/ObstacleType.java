package dk.dtu.compute.se.pisd.roborally.model;

/**
 * @author ZeeDiazz (Zaid)
 * This enum contains the many sort obstacles seen in the game RoboRally.
 * Each type of obstacle has a unique behavior.
 */
public enum ObstacleType {
    BLUE_CONVEYOR_BELT,
    GREEN_CONVEYOR_BELT,
    PUSH_PANEL,
    GEAR,
    BOARD_LASER;

    public static ObstacleType getObstacleType(String name) {

        for (ObstacleType obstacleType : ObstacleType.values()) {
            if (name.equals(obstacleType.toString())) {
                return obstacleType;
            }
        }
        return null;
    }
}


