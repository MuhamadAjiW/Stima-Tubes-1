package Services;

import Models.*;
import Services.Common.Tester;

import java.util.*;

public class BotService {
    private GameObject bot;
    private PlayerAction playerAction;
    private GameState gameState;

    public BotService() {
        this.playerAction = new PlayerAction();
        this.gameState = new GameState();
    }

    public GameObject getBot() {
        return this.bot;
    }

    public void setBot(GameObject bot) {
        this.bot = bot;
    }

    public PlayerAction getPlayerAction() {
        return this.playerAction;
    }

    public void setPlayerAction(PlayerAction playerAction) {
        this.playerAction = playerAction;
    }

    public void setPlayerHeading(int direction) {
        this.playerAction.heading = direction;
    }

    public static int cachedTick = 0;
    public void computeNextPlayerAction(PlayerAction playerAction) {
        StateMachine botState = new StateMachine();
        
        if (!gameState.getGameObjects().isEmpty()) { // kalo game belum beres
            if (cachedTick != gameState.world.currentTick){
                playerAction = botState.determineAction(gameState, playerAction, bot);
                Tester.appendFile("Action: " + playerAction.action.name(), "testlog.txt");
                cachedTick = gameState.world.currentTick;
            }
        }

        this.playerAction = playerAction;
    }

    public GameState getGameState() {
        return this.gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
        updateSelfState();
    }

    private void updateSelfState() {
        Optional<GameObject> optionalBot = gameState.getPlayerGameObjects().stream().filter(gameObject -> gameObject.id.equals(bot.id)).findAny();
        optionalBot.ifPresent(bot -> this.bot = bot);
    }
}
