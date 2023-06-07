package dk.dtu.compute.se.pisd.roborally.server;

/**
 * @auther Felix Schmidt (Felix732)
 */

public interface ResponseMessage {
    String greetingMessage(String name);
    String getPlayerReadyMessage(int playerId, int lobbyId);
    String getPlayerNotReadyMessage(int playerId, int lobbyId);
    String getLobbyCreatedMessage(int lobbyId);

    String getLobbyJoinedMessage(int playerId, int lobbyId);

    String getLobbyLeavedMessage(int playerId, int lobbyId);

    String getLobbyDeletedMessage(int lobbyId);

}
