package nl.tudelft.jpacman.level;

import nl.tudelft.jpacman.npc.Ghost;
import nl.tudelft.jpacman.points.DefaultPointCalculator;
import nl.tudelft.jpacman.sprite.PacManSprites;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

/**
 * A test class corresponding to "Lesson 1.5: Decision Table Design and Testing Guidelines" for the PlayerCollisions class.
 *
 * TODO: I have read in the clean code book, that test code has to be maintained as well.
 *       All tests in this suite correspond to a decision table, and thus there are several "actions" for each variant that involve
 *       * player.isAlive(),
 *       * player.getScore(),
 *       * pellet.leaveSquare(),
 *       * player.getKiller(),
 *
 *       Thus, there should be a single verifyMethod that takes as an argument all expected values of all actions.
 * @author Marcel Bechmann
 */
public class PlayerCollisionsTest {

    private PlayerCollisions collisions;
    private Player player;
    private Ghost ghost;
    private Pellet pellet;
    @BeforeEach
    public void initialize()
    {
        PacManSprites sprites = new PacManSprites();
        collisions = new PlayerCollisions(new DefaultPointCalculator());
        player = new Player(sprites.getPacmanSprites(), sprites.getPacManDeathAnimation());
        assert player.isAlive();
        assert player.getScore() == 0;

        ghost = mock(Ghost.class);
        pellet = mock(Pellet.class);
        when(pellet.getValue()).thenReturn(1);
    }

    /**
     * Scenario S2.1: The player consumes
     * "... I earn the points for the pellet ..."
     */
    @Test
    public void PacmanCollidesIntoPelletTest()
    {
        int expectedScore = pellet.getValue();
        collisions.collide(player, pellet);
        Assertions.assertEquals(expectedScore, player.getScore());
        Assertions.assertEquals(true, player.isAlive());
        verify(pellet, times(1)).leaveSquare();
    }

    /**
     * Scenario S2.4: The player dies
     * "... my Pacman dies ..."
     */
    @Test
    public void PacmanCollidesIntoGhostTest()
    {
        collisions.collide(player, ghost);
        Assertions.assertEquals(false, player.isAlive());
        Assertions.assertEquals(ghost, player.getKiller());
    }

    /**
     * Scenario S3.4: The player dies.
     *
     */
    @Test
    public void GhostCollidesIntoPacmanTest()
    {
        collisions.collide(ghost, player);
        Assertions.assertEquals(false, player.isAlive());
        Assertions.assertEquals(ghost, player.getKiller());
    }

    /**
     * Scenario S3.2: The ghost moves over a square with a pellet.
     * The points of pacman remain unchanged
     */
    @Test
    public void GhostCollidesIntoPelletTest()
    {
        PlayerCollisionTestOutcome expectedOutcome = new PlayerCollisionTestOutcome()
            .WithKiller(null)
            .WithPlayerAlive()
            .WithScore(player.getScore());

        collisions.collide(ghost, pellet);
        PlayerCollisionTestOutcome outcome = new PlayerCollisionTestOutcome()
            .WithKiller(null)
            .WithPlayerAlive()
            .WithScore(player.getScore());

        outcome.AssertTestOutcome(expectedOutcome);
        outcome.AssertNumberOfTimesPelletLeavesSquare(pellet, 0);
    }



    private class PlayerCollisionTestOutcome
    {
        private Ghost Killer;
        private int Score;
        private Boolean IsPlayerAlive;

        public PlayerCollisionTestOutcome WithKiller(Ghost killer)
        {
            Killer = killer;
            return this;
        }

        public PlayerCollisionTestOutcome WithScore(int score)
        {
            Score = score;
            return this;
        }

        public PlayerCollisionTestOutcome WithPlayerAlive()
        {
            IsPlayerAlive = true;
            return this;
        }

        public PlayerCollisionTestOutcome WithPlayerDead()
        {
            IsPlayerAlive = false;
            return this;
        }


        private PlayerCollisionTestOutcome()
        {

        }

        public void AssertTestOutcome(@NotNull PlayerCollisionTestOutcome expectedOutcome)
        {
            Assertions.assertEquals(expectedOutcome.Score, this.Score);
            Assertions.assertEquals(expectedOutcome.IsPlayerAlive, this.IsPlayerAlive);
            Assertions.assertEquals(expectedOutcome.Killer, this.Killer);
        }

        private void AssertNumberOfTimesPelletLeavesSquare(Pellet pellet, int expectedNumberOfTimes)
        {
            verify(pellet, times(expectedNumberOfTimes)).leaveSquare();
        }
    }

}
