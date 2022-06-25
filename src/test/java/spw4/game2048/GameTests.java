package spw4.game2048;

import static org.junit.jupiter.api.Assertions.*;
import static spw4.game2048.GameImpl.BOARD_SIZE;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

public class GameTests
{
    GameImpl sut;

    @BeforeEach
    void setup()
    {
        sut = new GameImpl();
    }

    @Test
    void getMoves_noMoves_returnsZero()
    {
        sut.initialize();

        var moves = sut.getMoves();
        assertEquals(0, moves);
    }

    @Test
    void getMoves_notPossible_moveCountNotIncremented()
    {
        sut.setBoardForTests(new int[][]
        {
            { 16, 2, 32, 2 },
            { 0, 0, 0, 0 },
            { 0, 0, 0, 0 },
            { 0, 0, 0, 0 }
        });

        var movesBefore = sut.getMoves();
        sut.move(Direction.up);
        var movesAfter = sut.getMoves();

        assertEquals(movesBefore, movesAfter);
    }

    @RepeatedTest(10)
    void getMoves_twoMoves_ReturnsTwo()
    {
        sut.setBoardForTests(new int[][]
        {
            { 0, 0, 0, 0 },
            { 0, 2, 4, 0 },
            { 0, 2, 2, 0 },
            { 0, 0, 0, 0 }
        });

        sut.move(Direction.left);
        sut.move(Direction.right);

        var res = sut.getMoves();

        assertEquals(2, res);
    }

    @Test
    void getScore_noMoves_returnsZero()
    {
        sut.initialize();

        var score = sut.getScore();
        assertEquals(0, score);
    }

    @RepeatedTest(5)
    void getScore_twoMoves_returnsScore()
    {
        sut.setBoardForTests(new int[][]
        {
            { 0, 2, 0, 0 },
            { 0, 2, 0, 0 },
            { 0, 2, 0, 0 },
            { 0, 2, 0, 0 }
        });

        sut.move(Direction.down);
        sut.move(Direction.down);

        assertEquals(16, sut.getScore());
    }

    @Test
    void getValueAt_notInitialized_throwsException()
    {
        assertThrows(NullPointerException.class, () -> sut.getValueAt(0, 0));
    }

    @Test
    void getValueAt_withValidCoordinates_returnsValue()
    {
        sut.setBoardForTests(new int[][]
        {
            {16, 2, 32, 2},
            {8, 64, 2, 16},
            {2, 32, 16, 8},
            {4, 8, 4, 2}
        });

        assertAll
        (
            () -> assertEquals(16, sut.getValueAt(0, 0)),
            () -> assertEquals(2, sut.getValueAt(3, 0)),
            () -> assertEquals(4, sut.getValueAt(0, 3)),
            () -> assertEquals(2, sut.getValueAt(3, 3))
        );
    }

    @Test
    void getValueAt_moveTileUp_returnsValue()
    {
        sut.setBoardForTests(new int[][]
            {
                { 16, 2, 0, 2 },
                { 0, 0, 0, 0 },
                { 0, 0, 2, 0 },
                { 0, 0, 0, 0 }
            });

        sut.move(Direction.up);

        var res = sut.getValueAt(2, 0);

        assertEquals(2, res);
    }

    @Test
    void getValueAt_withInvalidIndex_throwsException()
    {
        sut.initialize();

        assertAll
        (
            () -> assertThrows(IndexOutOfBoundsException.class, () -> sut.getValueAt(-1, 0)),
            () -> assertThrows(IndexOutOfBoundsException.class, () -> sut.getValueAt(0, -1)),
            () -> assertThrows(IndexOutOfBoundsException.class, () -> sut.getValueAt(BOARD_SIZE, 0)),
            () -> assertThrows(IndexOutOfBoundsException.class, () -> sut.getValueAt(0, BOARD_SIZE))
        );
    }

//    irrelevant test due to change of initialize func
//    @Test
//    void testEmptyBoard()
//    {
//        sut.initialize();
//
//        var tileVal = sut.getValueAt(0, 0);
//        assertEquals(0, tileVal);
//    }

    @Test
    void isOver_noMoves_returnsFalse()
    {
        sut.initialize();

        var res = sut.isOver();
        assertFalse(res);
    }

    @Test
    void isOver_mergeableNeighborVertically_returnsFalse()
    {
        sut.setBoardForTests(new int[][]
        {
            { 2, 4, 8, 16 },
            { 2, 8, 16, 32 },
            { 4, 16, 32, 16 },
            { 8, 32, 16, 32 }
        });

        var res = sut.isOver();
        assertFalse(res);
    }

    @Test
    void isOver_mergeableNeighborHorizontally_returnsFalse()
    {
        sut.setBoardForTests(new int[][]
        {
            { 4,   2, 4, 8 },
            { 8,  16, 8, 4 },
            { 4,   8, 4, 8 },
            { 16, 16, 8, 4 }
        });

        var res = sut.isOver();
        assertFalse(res);
    }

    @Test
    void isOver_fullBoard_returnsTrue()
    {
        sut.setBoardForTests(new int[][]
        {
            { 16, 2, 32, 2 },
            { 8, 64, 2, 16 },
            { 2, 32, 16, 8 },
            { 4, 8, 4, 2 }
        });

        var res = sut.isOver();

        assertTrue(res);
    }

    @Test
    void isWon_noMoves_returnsFalse()
    {
        sut.initialize();

        var res = sut.isWon();
        assertFalse(res);
    }

    @Test
    void isWon_boardFullContainsWinningTile_returnsTrue()
    {
        sut.setBoardForTests(new int[][]
        {
            { 2048, 2, 32, 2 },
            { 8, 64, 2, 16 },
            { 2, 32, 16, 8 },
            { 4, 8, 4, 2 }
        });

        var res = sut.isWon();

        assertTrue(res);
    }

    @Test
    void isWon_mergedTilesToWin_returnsTrue()
    {
        sut.setBoardForTests(new int[][]
        {
            { 1024, 4, 8, 16 },
            { 1024, 8, 16, 32 },
            { 4, 16, 32, 16 },
            { 8, 32, 16, 32 }
        });

        var isWonBefore = sut.isWon();
        sut.move(Direction.up);
        var isWonAfter = sut.isWon();

        assertAll
        (
            () -> assertFalse(isWonBefore),
            () -> assertTrue(isWonAfter)
        );
    }

    // toString tests irrelevant due to quick changes of toString func
    @Test
    void toString_returnsBoardString()
    {
        sut.setBoardForTests(new int[][]
        {
            { 16, 2, 32, 2 },
            { 8, 64, 2, 16 },
            { 2, 32, 16, 8 },
            { 4, 8, 4, 2 }
        });

        var res = sut.toString();

        var expected =
              "   16     2    32     2 \n"
            + "\n"
            + "    8    64     2    16 \n"
            + "\n"
            + "    2    32    16     8 \n"
            + "\n"
            + "    4     8     4     2 ";

        assertEquals(expected, res);
    }

    @RepeatedTest(10)
    void initialize_containsTwoFilledTiles()
    {
        sut.initialize();

        var count = 0;

        for (int i = 0; i < BOARD_SIZE; i++)
            for (int j = 0; j < BOARD_SIZE; j++)
                if (sut.getValueAt(i, j) != 0)
                    count++;

        assertEquals(2, count);
    }
}