package com.pdg.game.DTO;

public record BlockDTO(
    String type, long uuid, int health, int mass, boolean alive, float x, float y, float angle, int length) {}
