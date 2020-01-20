package game;

import org.joml.Vector2i;

import java.lang.reflect.Array;
import java.util.Arrays;

public class Board {
    private int n_cells;
    private Pawn.Type[][] cells;
    public Board(int n_cells) {
        this.n_cells = n_cells;
        this.cells = new Pawn.Type[n_cells][n_cells];
        for (int i = 0; i < n_cells; i++) {
            for (int j = 0; j < n_cells; j++) {
                cells[i][j] = Pawn.Type.NONE;
            }
        }
    }
    public Pawn.Type at(Vector2i pos) {
        return inBounds(pos) ? cells[pos.x][pos.y] : null;
    }
    public Pawn.Type at(int i, int j) {
        return inBounds(i, j) ? cells[i][j] : null;
    }
    void crown(Vector2i pos) {
        Pawn.Type t = at(pos);
        if (t != Pawn.Type.NONE)
            cells[pos.x][pos.y] = t.color== Pawn.Color.WHITE ? Pawn.Type.WHITE_KING : Pawn.Type.BLACK_KING;
    }

    boolean inBounds(int x, int y) {
        return  x >= 0 && x < n_cells &&
                y >= 0 && y < n_cells;
    }

    public boolean inBounds(Vector2i cell) {
        return  cell.x >= 0 && cell.x < n_cells &&
                cell.y >= 0 && cell.y < n_cells;
    }
    boolean move(Vector2i from, Vector2i to) {
        if (inBounds(from) && inBounds(to)) {
            cells[to.x][to.y] = cells[from.x][from.y];
            cells[from.x][from.y] = Pawn.Type.NONE;
            return true;
        }
        return false;
    }
    void place(Vector2i into, Pawn.Type t) {
        if (inBounds(into))
            cells[into.x][into.y] = t;
    }
    public int getSize() {
        return n_cells;
    }
}
