package player;

import game.Board;
import game.Game;
import game.GameManager;
import game.Pawn;
import org.joml.Vector2i;
import utils.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AI extends Player {
    private Board board;
    public AI(Game game, Pawn.Color team) {
        super(game, team, false);
        board = manager.getBoard();
    }
    void makeMove() {
        int n = board.getSize();
        Pair<Vector2i, Vector2i> best_move;
        int max_gain = -1;
        Vector2i from = manager.getCurrent();
        Vector2i move = new Vector2i();
        best_move = new Pair<>(new Vector2i(), new Vector2i());
        if (from != null) {
            planMove(from, board.at(from).king, move);
            best_move.i.set(from);
            best_move.j.set(move);
        } else {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    Pawn.Type p = board.at(i, j);
                    if (p.color == team) {
                        from = new Vector2i(i, j);
                        int gain = planMove(from, p.king, move);
                        if (gain > max_gain) {
                            max_gain = gain;
                            best_move.i.set(from);
                            best_move.j.set(move);
                        }
                    }
                }
            }
            if (max_gain < 1) {
                System.out.println("RANDOM " + (team == Pawn.Color.WHITE ? "WHITE" : "BLACK"));
                best_move = getRandomMove();
                if (best_move != null)
                    System.out.println(best_move.i + " " + best_move.j);

            }
        }
        if (best_move == null) {
            giveUp();
        } else {
            manager.select(best_move.i);
            GameManager.Message response = manager.select(best_move.j);
            if (response == GameManager.Message.Illegal) {
                giveUp();
            }
        }
    }
    List<Integer> getOwnPawns() {
        int n = board.getSize();
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (board.at(i, j).color == team){
                    indices.add(i*n+j);
                }
            }
        }
        return indices;
    }
    Pair<Vector2i, Vector2i> getRandomMove() {
        List<Integer> order = getOwnPawns();
        Collections.shuffle(order);
        Vector2i from = new Vector2i();
        Vector2i to = new Vector2i();
        Board board = manager.getBoard();
        int n = board.getSize();
        for (int elem : order) {
            from.set(elem/n, elem%n);
            if (planMove(from, board.at(from).king, to) != -1) {
                return new Pair<>(from, to);
            }
        }
        return null;
    }

    int planMove(Vector2i from, boolean is_king, Vector2i out_move) {
        List<Vector2i> directions = new ArrayList<>(Arrays.asList(
                new Vector2i(1, 1),
                new Vector2i(1, -1),
                new Vector2i(-1, 1),
                new Vector2i(-1, -1)
        ));
        Collections.shuffle(directions);
        int max_gain = -1;
        for (Vector2i dir : directions) {
            Vector2i v = new Vector2i(from).add(dir);
            if (!board.inBounds(v))
                continue;
            for (int moves = 0; board.inBounds(v) && (is_king || moves < 2); v.add(dir), moves++) {
                int gain = manager.moveCorrect(from, v, false);
                if (gain > max_gain) {
                    out_move.set(v);
                    max_gain = gain;
                }
            }
        }
        return max_gain;
    }

    @Override
    public void notifyPlayer() {
        System.out.println("NOTIFY"+(team== Pawn.Color.WHITE?"WHITE":"BLACK"));
        makeMove();
    }

}
