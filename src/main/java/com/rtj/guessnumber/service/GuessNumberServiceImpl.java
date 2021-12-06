/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.rtj.guessnumber.service;

import com.rtj.guessnumber.data.GameDao;
import com.rtj.guessnumber.models.Game;
import com.rtj.guessnumber.models.Round;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Random;
import org.springframework.stereotype.Component;

/**
 *
 * @author Blaine
 */

@Component
public class GuessNumberServiceImpl implements GuessNumberService{
    
    GameDao dao;
    
    public GuessNumberServiceImpl(GameDao gameDao) {
        this.dao = gameDao;
    }
    
    @Override
    public List<Game> getAll() {
       return dao.getAll();
    }
    
    @Override
    public int newGame() {
        Random randomNum = new Random();
        int firstNum = randomNum.nextInt(10);
        int secondNum = randomNum.nextInt(10);
        while(firstNum == secondNum){
            secondNum = randomNum.nextInt(10);
        }
        int thirdNum = randomNum.nextInt(10);
        while(firstNum == thirdNum || secondNum == thirdNum) {
            thirdNum = randomNum.nextInt(10);
        }
        int fourthNum = randomNum.nextInt(10);
        while(firstNum == fourthNum || secondNum == fourthNum || thirdNum == fourthNum) {
            fourthNum = randomNum.nextInt(10);
        }
        String answer = Integer.toString(firstNum) + Integer.toString(secondNum) + Integer.toString(thirdNum) + Integer.toString(fourthNum);
        
        Game game = new Game();
        game.setAnswer(answer);
        game.setFinished(false);
        dao.add(game);
        return game.getId();
    }
    
    @Override
    public Game findById(int id) {
        return dao.findById(id);
    }
    
    @Override
    public Game findByIdShowAnswers(int id) {
        return dao.findByIdShowAnswers(id);
    }
    
    @Override
    public List<Round> getAllRoundsForGame(int id){
        return dao.getAllRoundsForGame(id);
    }
    
    @Override
    public Round guess(String guess, Game currentGame){
        Round round = new Round();
        round.setGuess(guess);
        round.setGameId(currentGame.getId());
        
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        round.setTime(sdf.format(timestamp));
        
        String res = calculateResult(guess, currentGame);
        round.setResult(res);
        dao.addRound(round);
        return round;
    }
    
    private String calculateResult(String guess, Game currentGame){
        String res = "e:%s:p:%s";
        int exactMatches = 0;
        int partialMatches = 0;
        
        String answer = currentGame.getAnswer();
        char[] guessChar = guess.toCharArray();
        for (char num : guessChar){
            if (answer.indexOf(num) != -1 && answer.indexOf(num) == guess.indexOf(num)){
                exactMatches = exactMatches + 1;
            } else if (answer.contains(String.valueOf(num))) {
                partialMatches = partialMatches + 1;
            }
        }
        if (exactMatches == 4){
            currentGame.setFinished(true);
            dao.update(currentGame);
        }
        return String.format(res, exactMatches, partialMatches);
    }
}
