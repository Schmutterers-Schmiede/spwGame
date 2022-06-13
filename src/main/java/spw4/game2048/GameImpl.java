package spw4.game2048;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class GameImpl implements Game
{
    public static final int BOARD_SIZE = 4;

    public final Random random = new Random();

    private int[][] board;
    private int score;
    private int moves;

    public GameImpl()
    {   }

    public void setBoardForTests(int[][] inBoard)
    {
        board = inBoard;
    }

    public int getMoves()
    {
        return moves;
    }

    public int getScore()
    {
        return score;
    }

    public int getValueAt(int x, int y)
    {
        if (x < 0 || x >= BOARD_SIZE || y < 0 || y >= BOARD_SIZE)
            throw new IndexOutOfBoundsException();

        return board[y][x];
    }

    public boolean isOver()
    {
        // iterate over the board and if there is a zero (empty tile), return false
        for (int y = 0; y < BOARD_SIZE; y++)
            for (int x = 0; x < BOARD_SIZE; x++)
                if (board[y][x] == 0)
                    return false;

        // all values are greater than 0 -> no spaces between existing tiles
        // check if there are any adjacent tiles with the same value
        for (int y = 0; y < BOARD_SIZE; y++)
        {
            for (int x = 0; x < BOARD_SIZE; x++)
            {
                if (y < BOARD_SIZE - 1 && board[y][x] == board[y + 1][x])
                    return false;
                if (x < BOARD_SIZE - 1 && board[y][x] == board[y][x + 1])
                    return false;
            }
        }

        return true;
    }

    public boolean isWon()
    {
        // iterate through the board and check if there is at least a 2048 tile
        for (int y = 0; y < BOARD_SIZE; y++)
            for (int x = 0; x < BOARD_SIZE; x++)
                if (board[y][x] >= 2048)
                    return true;

        return false;
    }

    @Override
    public String toString()
    {
        var sb = new StringBuilder();

        for (int y = 0; y < BOARD_SIZE; y++)
        {
            for (int x = 0; x < BOARD_SIZE; x++)
            {
                sb.append(String.format("%5d", board[y][x]));
                sb.append(" ");
            }

            if (y < BOARD_SIZE - 1)
                sb.append("\n\n");
        }

        return sb.toString();
    }

    public void initialize()
    {
        board = new int[BOARD_SIZE][BOARD_SIZE];
        score = 0;
        moves = 0;

        generateRandomTile();
        generateRandomTile();
    }

    private void generateRandomTile()
    {
        // not even possible to get called because of the way main is implented
//        if (isOver())
//            throw new IllegalStateException();

        // generate a random number between 0 and 1
        var rdmVal = random.nextInt(10);

        // 90 % chance of generating a 2, 10 % chance of generating a 4
        var val = (rdmVal == 0 ? 4 : 2);

        // get empty cells
        var emptyTiles = new ArrayList<Point>();

        for (int y = 0; y < BOARD_SIZE; y++)
            for (int x = 0; x < BOARD_SIZE; x++)
                if (board[y][x] == 0)
                    emptyTiles.add(new Point(x, y));

        var rdmTile = emptyTiles.get(random.nextInt(emptyTiles.size()));

//        System.out.println("rdmTile.x: " + rdmTile.x + ", rdmTile.y: " + rdmTile.y);
        // set the value at the random position
        board[rdmTile.y][rdmTile.x] = val;
    }

    public void move(Direction direction)
    {
        var moveSuccess = false;

        switch (direction)
        {
            case up -> moveSuccess = moveUp();
            case left -> moveSuccess = moveLeft();
            case down -> moveSuccess = moveDown();
            case right -> moveSuccess = moveRight();
            default -> {}
        }

        if (moveSuccess)
        {
            generateRandomTile();
            moves++;
        }
    }

    private boolean moveTileHorizontally(Direction dir, int x, int y)
    {
        int step = (dir == Direction.right ? 1 : -1);

        var move = true;
        var moveSuccess = false;

        int xHead = x;
        int xTail = x;

        if (board[y][x] == 0)
            return false; //move not successful

        while(move)
        {
            xHead += step;

            if( dir == Direction.right && xHead >= BOARD_SIZE ||
                dir == Direction.left && xHead < 0)
                move = false;
            else if (board[y][xHead] == 0)
            {
                board[y][xHead] = board[y][xTail];
                board[y][xTail] = 0;
                xTail += step;
                moveSuccess = true;
            }
            else if(board[y][xHead] == board[y][xTail])
            {
                board[y][xHead] *= 2;
                board[y][xTail] = 0;
                move = false;
                moveSuccess = true;
                score += board[y][xHead];
            }
            else
                move = false;
        }

        return moveSuccess;
    }

    private boolean moveTileVertically(Direction dir, int x, int y)
    {
        var step = (dir == Direction.down ? 1 : -1);

        var move = true;
        var moveSuccess = false;

        int yHead = y;
        int yTail = y;

        if (board[y][x] == 0)
            return false; //move not successful

        while(move)
        {
            yHead += step;

            if( dir == Direction.down && yHead >= BOARD_SIZE ||
                dir == Direction.up && yHead < 0)
                move = false;
            else if (board[yHead][x] == 0)
            {
                board[yHead][x] = board[yTail][x];
                board[yTail][x] = 0;
                yTail += step;
                moveSuccess = true;
            }
            else if(board[yHead][x] == board[yTail][x])
            {
                board[yHead][x] *= 2;
                board[yTail][x] = 0;
                move = false;
                moveSuccess = true;
                score += board[yHead][x];
            }
            else
                move = false;
        }

        return moveSuccess;
    }

    private boolean moveRight()
    {
        var moveSuccess = false;

        for (int y = 0; y < BOARD_SIZE; y++)
            for (int x = 2; x >= 0; x--)
                moveSuccess |= moveTileHorizontally(Direction.right, x, y);

        return moveSuccess;
    }

    private boolean moveLeft()
    {
        var moveSuccess = false;

        for (int y = 0; y < BOARD_SIZE; y++)
            for (int x = 1; x < BOARD_SIZE; x++)
                moveSuccess |= moveTileHorizontally(Direction.left, x, y);

        return moveSuccess;
    }

    private boolean moveUp()
    {
        var moveSuccess = false;

        for (int y = 1; y < BOARD_SIZE; y++)
            for (int x = 0; x < BOARD_SIZE; x++)
                moveSuccess |= moveTileVertically(Direction.up, x, y);

        return moveSuccess;
    }

    private boolean moveDown()
    {
        var moveSuccess = false;

        for (int y = 2; y >= 0; y--)
            for (int x = 0; x < BOARD_SIZE; x++)
                moveSuccess |= moveTileVertically(Direction.down, x, y);

        return moveSuccess;
    }
}