/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rtj.guessnumber.data;

import com.rtj.guessnumber.models.Game;
import com.rtj.guessnumber.models.Round;
import java.util.List;

public interface GameDao {

    List<Game> getAll();
    Game add(Game game);
    Game findById(int id);
    Game findByIdShowAnswers(int id);
    List<Round> getAllRoundsForGame(int id);
    Round addRound(Round round);
    boolean update(Game game);
    
}   

