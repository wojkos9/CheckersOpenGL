package player;

import game.Game;
import game.Pawn;

public class HumanPlayer extends Player {
    public HumanPlayer(Game game, Pawn.Color team) {
        super(game, team, true);
    }

    @Override
    public void notifyPlayer() {
    }
}
