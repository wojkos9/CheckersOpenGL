package game;

import graphics.Renderer;
import org.joml.Math;
import org.joml.Vector2i;
import player.Player;

import javax.swing.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;



public class GameManager {
    private int n_cells;
    private Map<Integer, Pawn> pawns;
    private Board board;
    private Vector2i current;
    private Pawn.Color team;
    private boolean must_take;
    private boolean busy;
    private boolean allowed_to_move;
    private int[] pawns_left = new int[2];
    private Player player1, player2;
    private Game game;

    private Semaphore animationsWaiting, checkPending;
    private Thread animatorThread;
    private Queue<AnimationThread> animations;

    public enum Message {Continue, Finish, Illegal}

    public GameManager(Game game, int n_cells) {
        this.game = game;
        this.n_cells = n_cells;
        pawns = new ConcurrentHashMap<>();
        board = new Board(n_cells);
        current = null;
        team = Pawn.Color.WHITE;
        must_take = false;
        allowed_to_move = true;
        busy = false;

        animations = new LinkedList<>();
        animationsWaiting = new Semaphore(0);
        checkPending = new Semaphore(1);
        animatorThread = new Thread() {
            @Override
            public void run() {
                super.run();
                while (true) {
                    try {
                        animationsWaiting.acquire();
                        AnimationThread a = animations.remove();
                        a.start();
                        a.join();
                        checkPending.acquire();
                        if (animations.isEmpty())
                            if (winner == null)
                                currentPlayer().notifyPlayer();
                            else
                                endGame();
                        checkPending.release();

                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        };
        animatorThread.start();
    }

    public void setPlayers(Player p1, Player p2) {
        player1 = p1;
        player2 = p2;
    }

    void startGame() {
        game.setAcceptInput(currentPlayer().requiresInput());
        currentPlayer().notifyPlayer();
    }

    boolean canMoveFrom(Vector2i cell) {
        if (current == null)
            return board.at(cell).color == team;
        else
            return moveCorrect(current, cell, must_take) != -1;
    }

    private Player currentPlayer() {
        return team==Pawn.Color.WHITE?player1:player2;
    }
    private Player nextPlayer() {
        return team!=Pawn.Color.WHITE?player1:player2;
    }
    private Player winner;
    private void setWinner(Player p) {
        winner = p;
    }
    public void giveUp(Player p) {
        setWinner(p==player1?player2:player1);
        endGame();
    }

    public void endGame() {
        game.stopTimer();
        String winner_msg = winner == null ? "Remis" : "Wygrały " + winner.getColor();
        JOptionPane.showMessageDialog(null, winner_msg, "Koniec gry", JOptionPane.INFORMATION_MESSAGE);
    }
    public void quit() {
        animatorThread.interrupt();
    }

    public Message select(Vector2i cell) {
        if (!board.inBounds(cell))
            return Message.Continue;
        if (current == null || board.at(cell).color == team && !must_take) {
            if (board.at(cell).color == team) {
                current = cell;
            } else {
                current = null;
            }
        } else {
            if (move(cell)) {
                if (!allowed_to_move || !canTakePawns(current)){
                    Pawn p = pawnAt(current);
                    if (!p.isKing() && team == Pawn.Color.WHITE ? current.x == n_cells-1 : current.x == 0) {
                        pushAnimation(p.makeKing());
                        board.crown(cell);
                    }
                    switchTeam();

                    return Message.Finish;
                }
            } else {
                return Message.Illegal;
            }
        }
        return Message.Continue;
    }
    private void pushAnimation(AnimationThread a) {
        animations.add(a);
        animationsWaiting.release();
    }

    public Vector2i getCurrent() {
        return current;
    }
    private int makeKey(Vector2i v) {
        return n_cells*v.x+v.y;
    }
    Pawn pawnAt(Vector2i pos) {
        return pawns.get(makeKey(pos));
    }
    public int moveCorrect(Vector2i from, Vector2i to, boolean must_take) {
        if (!board.inBounds(to) || !board.inBounds(from)) {
            return -1;
        }
        Vector2i diff = new Vector2i(to).sub(from);
        int n = Math.abs(diff.x);
        if (Math.abs(diff.y) != n) {
            return -1;
        }
        Pawn.Type place1 = board.at(from);
        Pawn.Type place2 = board.at(to);
        if (place1 == Pawn.Type.NONE || place1.color != team || place2 != Pawn.Type.NONE) {
            return -1;
        }
        Vector2i middle = new Vector2i(from).add(diff.x/2, diff.y/2);
        if (!board.at(from).king) {
            if (n==2) {
                if (board.at(middle)== Pawn.Type.NONE) {
                    return -1;
                }
            } else if (n!= 1) {
                return -1;
            }
        }
        Vector2i dir = new Vector2i(diff.x/n, diff.y/n);
        int pawn_taken = 0;
        for (Vector2i i = new Vector2i(from).add(dir); i.x != to.x; i.add(dir)) {
            Pawn.Type p = board.at(i);
            if (p == Pawn.Type.NONE) continue;
            if (p.color == team)
                return -1;
            Vector2i next = new Vector2i(i).add(dir);
            if (board.at(next)==Pawn.Type.NONE)
                pawn_taken++;
            else
                return -1;
        }
        if (must_take && pawn_taken==0)
            return -1;
        return pawn_taken;
    }

    void switchTeam() {
        if (pawns_left[1-team.ordinal()] < 1) {
            setWinner(currentPlayer());
        } else {
            team = getOpponent();
            current = null;
            allowed_to_move = true;
            must_take = false;
            game.setAcceptInput(currentPlayer().requiresInput());
        }
    }

    boolean move(Vector2i cell) {
        int taken = moveCorrect(current, cell, must_take);
        if (taken == -1)
            return false;
        Vector2i diff = new Vector2i(cell).sub(current);
        int n = Math.abs(diff.x);
        Vector2i dir = new Vector2i(diff.x/n, diff.y/n);
        Vector2i last = new Vector2i(current);
        current.set(cell);
        movePawn(last, cell);
        for (Vector2i i = new Vector2i(last).add(dir); i.x != cell.x; i.add(dir)) {
            killPawnAt(i);
        }
        if (taken == 0) {
            allowed_to_move = false;
        } else {
            must_take = true;
        }
        return true;
    }
    private void killPawnAt(Vector2i pos) {
        Pawn.Type t = board.at(pos);
        if (t != null && t != Pawn.Type.NONE) {
            pushAnimation(pawnAt(pos).fade());
            pawns_left[t== Pawn.Type.WHITE?0:1]--;
            pawns.remove(makeKey(pos));
            board.place(pos, Pawn.Type.NONE);
        }
    }

    public Board getBoard() {
        return board;
    }

    void placePawns(int rows) {
        for (int i = 0; i < rows; i++) {
            for (int j = i % 2; j < n_cells; j += 2) {
                placePawn(Pawn.Type.WHITE, i, j);
                placePawn(Pawn.Type.BLACK, n_cells-1-i, n_cells-1-j);
            }
        }
    }

    void placePawn(Pawn.Type color, int i, int j) {
        Vector2i pos = new Vector2i(i, j);
        if (board.at(pos) == Pawn.Type.NONE) {
            Pawn p = PawnFactory.create(color);
            p.set(i, j);
            pawns.put(makeKey(pos), p);
            board.place(pos, color);
            pawns_left[color== Pawn.Type.WHITE?0:1]++;
        }

    }
    private void movePawn(Vector2i from, Vector2i to) {
        Pawn pawn = pawnAt(from);
        pushAnimation(pawn.move(to.x, to.y));
        board.move(from, to);
        int key = makeKey(to);
        pawns.remove(key);
        pawns.put(key, pawn);
    }
    Map<Integer, Pawn> getPawns() {
        return pawns;
    }

    boolean canTakePawns(Vector2i p) {
        Vector2i[] directions = new Vector2i[]{
                new Vector2i(1, 1),
                new Vector2i(1, -1),
                new Vector2i(-1, 1),
                new Vector2i(-1, -1)};
        for (Vector2i d : directions) {
            Vector2i v = new Vector2i(p).add(d);
            if (!board.inBounds(v))
                continue;
            boolean is_king = board.at(p).king;
            for (int moves = 0; board.inBounds(v) && (is_king || moves < 1); v.add(d), moves++) {
                Pawn.Type t = board.at(v);
                if (t.color == getOpponent()) {
                    v.add(d);
                    System.out.println("Can take "+p+" -> "+v);
                    return board.at(v)== Pawn.Type.NONE;
                }
            }
        }
        return false;
    }

    Pawn.Color getOpponent() {
        return team == Pawn.Color.BLACK ? Pawn.Color.WHITE : Pawn.Color.BLACK;
    }
}
