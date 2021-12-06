/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rtj.guessnumber.data;

import com.rtj.guessnumber.models.Game;
import com.rtj.guessnumber.models.Round;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

/**
 *
 * @author blaine love
 */

@Repository
@Profile("database")
public class GameDatabaseDao implements GameDao {
    
    private final JdbcTemplate jdbcTemplate;
    
    @Autowired
    public GameDatabaseDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Game> getAll() {
        final String sql = "SELECT game_id, answer, finished FROM game;";
        List<Game> allGames = jdbcTemplate.query(sql, new GameMapper());
        
        ArrayList<Game> allGamesCensored = new ArrayList<Game>();
        for (Game game : allGames){
            if (!game.isFinished()) {
                game.setAnswer("****");
            }
            allGamesCensored.add(game);
        }

        return allGamesCensored;
    }
    
    @Override
    public Game add(Game game) {
        final String sql = "INSERT INTO game(answer, finished) VALUES(?,?);";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update((Connection conn) -> {
            
            PreparedStatement statement = conn.prepareStatement(
                sql,
                Statement.RETURN_GENERATED_KEYS);
            
            statement.setString(1, game.getAnswer());
            statement.setBoolean(2, game.isFinished());
            return statement;
        }, keyHolder);
        
        game.setId(keyHolder.getKey().intValue());
        
        return game;
    }
    
    @Override
    public Game findById(int id) {
        final String sql = "SELECT game_id, answer, finished "
                + "FROM game WHERE game_id = ?;";
        
        Game foundGame =  jdbcTemplate.queryForObject(sql, new GameMapper(), id);
        if (!foundGame.isFinished()) {
            foundGame.setAnswer("****");
        }
        return foundGame;
    }
    
    @Override
    public Game findByIdShowAnswers(int id) {
        final String sql = "SELECT game_id, answer, finished "
                + "FROM game WHERE game_id = ?;";
        
        Game foundGame =  jdbcTemplate.queryForObject(sql, new GameMapper(), id);
        return foundGame;
    }
    
    @Override
    public List<Round> getAllRoundsForGame(int id) {
        final String sql = "SELECT round_id, guess, result, time, game_id FROM rounds WHERE game_id = ?;";
        List<Round> allRounds = jdbcTemplate.query(sql, new RoundMapper(), id);
        allRounds.sort((o1,o2) -> o1.getTime().compareTo(o2.getTime()));
        
        return allRounds;
    }
    
    @Override
    public Round addRound(Round round) {
        final String sql = "INSERT INTO rounds(guess, result, time, game_id) VALUES(?,?,?,?);";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update((Connection conn) -> {
            
            PreparedStatement statement = conn.prepareStatement(
                sql,
                Statement.RETURN_GENERATED_KEYS);
            
            statement.setString(1, round.getGuess());
            statement.setString(2, round.getResult());
            statement.setString(3, round.getTime());
            statement.setInt(4, round.getGameId());
            return statement;
        }, keyHolder);
        
        round.setId(keyHolder.getKey().intValue());
        
        return round;
    }
    
    @Override
    public boolean update(Game game) {
        final String sql = "UPDATE game SET finished = ? WHERE game_id = ?;";
        
        return jdbcTemplate.update(sql,
                game.isFinished(),
                game.getId()) > 0;
    }
    
    private static final class GameMapper implements RowMapper<Game> {
        
        @Override
        public Game mapRow(ResultSet rs, int index) throws SQLException {
            Game gm = new Game();
            gm.setId(rs.getInt("game_id"));
            gm.setAnswer(rs.getString("answer"));
            gm.setFinished(rs.getBoolean("finished"));
            return gm;
        }
    }
    
    private static final class RoundMapper implements RowMapper<Round> {
        
        @Override
        public Round mapRow(ResultSet rs, int index) throws SQLException {
            Round rd = new Round();
            rd.setId(rs.getInt("round_id"));
            rd.setGuess(rs.getString("guess"));
            rd.setTime(rs.getString("time"));
            rd.setResult(rs.getString("result"));
            rd.setGameId(rs.getInt("game_id"));
            return rd;
        }
    }
    
}
