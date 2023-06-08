package dk.dtu.compute.se.pisd.roborally.restful;

import org.springframework.stereotype.Component;

@Component
public abstract class ResourceLocation {
    public static final String baseLocation = "http://localhost:8080";
    public static final String allGames = "/games";
    public static final String specificGame = "/game";
    public static final String joinGame = specificGame + "/join";
    public static final String leaveGame = specificGame + "/leave";
    public static final String gameStatus = specificGame + "/status";
    public static final String saveGame = specificGame + "/save";
    public static final String playerInfo = "/player";
}
