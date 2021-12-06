/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rtj.guessnumber.controllers;

import com.rtj.guessnumber.models.Game;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.rtj.guessnumber.data.GameDao;
import com.rtj.guessnumber.models.Round;
import com.rtj.guessnumber.service.GuessNumberService;

/**
 *
 * @author blaine love
 */

@RestController
@RequestMapping("/api/game")
public class GuessNumberController {

    private final GameDao dao;
    private GuessNumberService service;

    public GuessNumberController(GameDao dao, GuessNumberService service) {
        this.dao = dao;
        this.service = service;
    }

    @GetMapping
    public List<Game> all() {
        return service.getAll();
    }
    
    @PostMapping("/begin")
    @ResponseStatus(HttpStatus.CREATED)
    public int createGame() {
        return service.newGame();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Game> findById(@PathVariable int id) {
        Game result = service.findById(id);
        if (result == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/rounds/{id}")
    public List<Round> allRounds(@PathVariable int id) {
        return service.getAllRoundsForGame(id);
    }
    
    @PostMapping("/guess")
    @ResponseStatus(HttpStatus.CREATED)
    public Round guess(String guess, int gameId) {
        Game currentGame = getGame(gameId);
        return service.guess(guess, currentGame);
    }
    
    private Game getGame(int id) {
        Game result = service.findByIdShowAnswers(id);
        return result;
    }
  
}
