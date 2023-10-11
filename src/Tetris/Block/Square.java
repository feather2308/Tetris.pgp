package Tetris.Block;

import Tetris.TetrisData;

public class Square extends Piece {
    public Square(TetrisData data) {
        super(data, 4, 1);
        c[0] = 0;   r[0] = 0;
        c[1] = -1;   r[1] = 0;
        c[2] = 0;   r[2] = -1;
        c[3] = -1;   r[3] = -1;
    }
}
