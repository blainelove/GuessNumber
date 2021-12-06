/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.rtj.guessnumber.service;

import com.rtj.guessnumber.models.Game;
import com.rtj.guessnumber.models.Round;
import java.util.List;

/**
 *
 * @author Blaine
 */

public interface GuessNumberService {
    public List<Game> getAll();
    public int newGame();
    public Game findById(int id);
    public Game findByIdShowAnswers(int id);
    public List<Round> getAllRoundsForGame(int id);
    public Round guess(String guess, Game currentGame);
}
