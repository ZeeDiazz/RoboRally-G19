package dk.dtu.compute.se.pisd.roborally.server;

import org.springframework.stereotype.Component;

/**
 * @auther Felix Schmidt (Felix732)
 */

@Component
public class TemplateResponseMessage implements ResponseMessage{
    private static final String template = "Hello, %s!";
    private static final String playerReady = "Player %d is ready in lobby %d!";
    private static final String playerNotReady = "Player %d is not ready in lobby %d!";
    private static final String lobbyCreated = "Lobby %d was created!";
    private static final String lobbyJoined = "Player %d joined lobby %d!";
    private static final String lobbyLeaved = "Player %d left lobby %d!";
    private static final String lobbyDeleted = "Deleted lobby %d";

    @Override
    public String greetingMessage(String name) {
        return String.format(template, name);
    }
    @Override
    public String getPlayerReadyMessage(int playerId, int lobbyId) {
        return String.format(playerReady, playerId, lobbyId);
    }

    @Override
    public String getPlayerNotReadyMessage(int playerId, int lobbyId) {
        return String.format(playerNotReady, playerId, lobbyId);
    }
    @Override
    public String getLobbyCreatedMessage(int lobbyId){
        return String.format(lobbyCreated, lobbyId);
    }
    @Override
    public String getLobbyJoinedMessage(int lobbyId, int playerId){
        return String.format(lobbyJoined, playerId, lobbyId);
    }
    @Override
    public String getLobbyLeavedMessage(int lobbyId, int playerId){
        return String.format(lobbyLeaved, playerId, lobbyId);
    }
    @Override
    public String getLobbyDeletedMessage(int lobbyId){
        return String.format(lobbyDeleted, lobbyId);
    }

    @Override
    public String getLobbyAlreadyExistsMessage(int lobbyId){
        return String.format("Lobby %d already exists!", lobbyId);
    }
}
