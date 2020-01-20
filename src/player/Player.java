package player;

import game.Game;
import game.GameManager;
import game.Pawn;

public abstract class Player {
    protected Game game;
    protected GameManager manager;
    protected Pawn.Color team;
    private boolean requires_input;
    public Player(Game game, Pawn.Color team, boolean requires_input) {
        this.game = game;
        this.team = team;
        this.manager = game.getManager();
        this.requires_input = requires_input;
    }
    public abstract void notifyPlayer();
    public boolean requiresInput() {
        return requires_input;
    }
    protected void giveUp() {
        manager.giveUp(this);
    }
    public Pawn.Color getColor() {
        return team;
    }
}
