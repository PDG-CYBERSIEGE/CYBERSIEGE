package com.pdg.game.DTO;

public record BlockDTO(
    String type, int health, int mass, boolean alive, float x, float y, int length) {}
