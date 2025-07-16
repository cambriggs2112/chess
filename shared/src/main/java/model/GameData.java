package model;

import chess.ChessGame;

/**
 * Container for game data (gameID, whiteUsername, blackUsername, gameName, game)
 */
public record GameData(Integer gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {}
