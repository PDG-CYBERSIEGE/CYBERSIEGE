package com.pdg.game;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.pdg.game.Entity.Entity;

public class GameContactListener implements ContactListener {

  public GameContactListener() {}

  @Override
  public void beginContact(Contact contact) {

    Fixture fixtureA = contact.getFixtureA();
    Fixture fixtureB = contact.getFixtureB();

    Object objectA = fixtureA.getBody().getUserData();
    Object objectB = fixtureB.getBody().getUserData();

    if (objectA == null || objectB == null) {
      return;
    }
    if (objectA instanceof Entity entityA && objectB instanceof Entity entityB) {
      entityA.takeDamage(entityB.damageoutput());
      entityB.takeDamage(entityA.damageoutput());
    }
  }

  @Override
  public void endContact(Contact contact) {
    // Rien pour le moment.
  }

  @Override
  public void preSolve(Contact contact, com.badlogic.gdx.physics.box2d.Manifold oldManifold) {
    // Rien pour le moment.
  }

  @Override
  public void postSolve(Contact contact, com.badlogic.gdx.physics.box2d.ContactImpulse impulse) {
    // On pourra utiliser impulse ici plus tard
    // pour calculer les dégâts avec plus de précision.
  }
}
