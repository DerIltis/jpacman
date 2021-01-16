package nl.tudelft.jpacman.level;

import nl.tudelft.jpacman.board.Unit;
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
        PlayerCollisionTestOutcome expectedOutcome = new PlayerCollisionTestOutcome()
            .WithKiller(null)
            .WithPlayerAlive()
            .WithScore(pellet.getValue());

        collisions.collide(player, pellet);

        PlayerCollisionTestOutcome outcome = new PlayerCollisionTestOutcome()
            .WithKiller(player.getKiller())
            .WithPlayerIsAlive(player.isAlive())
            .WithScore(player.getScore());

        outcome.AssertTestOutcome(player);
        outcome.AssertNumberOfTimesPelletLeavesSquare(pellet, 1);
    }

    /**
     * Scenario S2.4: The player dies
     * "... my Pacman dies ..."
     */
    @Test
    public void PacmanCollidesIntoGhostTest()
    {

        PlayerCollisionTestOutcome expectedOutcome = new PlayerCollisionTestOutcome()
            .WithKiller(ghost)
            .WithPlayerDead()
            .WithScore(player.getScore());

        collisions.collide(player, ghost);

        PlayerCollisionTestOutcome outcome = new PlayerCollisionTestOutcome()
            .WithKiller(player.getKiller())
            .WithPlayerIsAlive(player.isAlive())
            .WithScore(player.getScore());

        outcome.AssertTestOutcome(player);
        // No pellet this time
    }

    /**
     * Scenario S3.4: The player dies.
     *
     */
    @Test
    public void GhostCollidesIntoPacmanTest()
    {
        PlayerCollisionTestOutcome expectedOutcome = new PlayerCollisionTestOutcome()
            .WithKiller(ghost)
            .WithPlayerDead()
            .WithScore(player.getScore());

        collisions.collide(ghost, player);
        PlayerCollisionTestOutcome outcome = new PlayerCollisionTestOutcome()
            .WithKiller(player.getKiller())
            .WithPlayerIsAlive(player.isAlive())
            .WithScore(player.getScore());

        outcome.AssertTestOutcome(player);
        // No pellet this time
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


        expectedOutcome.AssertTestOutcome(player);
        expectedOutcome.AssertNumberOfTimesPelletLeavesSquare(pellet, 0);
    }



    private class PlayerCollisionTestOutcome
    {
        private Unit Killer;
        private int Score;
        private Boolean IsPlayerAlive;

        public PlayerCollisionTestOutcome WithKiller(Unit killer)
        {
            Killer = killer;
            return this;
        }

        public PlayerCollisionTestOutcome WithScore(int score)
        {
            Score = score;
            return this;
        }

        public PlayerCollisionTestOutcome WithPlayerIsAlive(Boolean isAlive)
        {
            IsPlayerAlive = isAlive;
            return this;
        }

        public PlayerCollisionTestOutcome WithPlayerDead()
        {
            IsPlayerAlive = false;
            return this;
        }

        public PlayerCollisionTestOutcome WithPlayerAlive()
        {
            IsPlayerAlive = true;
            return this;
        }

        private PlayerCollisionTestOutcome()
        {

        }

        public void AssertTestOutcome(@NotNull Player player)
        {
            Assertions.assertEquals(player.getScore(), this.Score);
            Assertions.assertEquals(player.isAlive(), this.IsPlayerAlive);
            Assertions.assertEquals(player.getKiller(), this.Killer);
        }

        private void AssertNumberOfTimesPelletLeavesSquare(Pellet pellet, int expectedNumberOfTimes)
        {
            verify(pellet, times(expectedNumberOfTimes)).leaveSquare();
        }
    }

}
