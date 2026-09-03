package pdg.game;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import pdg.game.Entity.Entity;
import pdg.game.screens.FightScreen;

public class GameContactListener implements ContactListener {

  private FightScreen screen;

  public GameContactListener(FightScreen screen) {
    this.screen = screen;
  }

  @Override
  public void beginContact(Contact contact) {
    if (screen.currentPhase != FightScreen.GamePhase.BUILD) {
      Fixture fixtureA = contact.getFixtureA();
      Fixture fixtureB = contact.getFixtureB();

      Object objectA = fixtureA.getBody().getUserData();
      Object objectB = fixtureB.getBody().getUserData();

      if (objectA == null || objectB == null) {
        return;
      }
      if (objectA instanceof Entity && objectB instanceof Entity) {
        Entity entityA = (Entity) objectA;
        Entity entityB = (Entity) objectB;
        entityA.takeDamage(entityB.damageoutput());
        entityB.takeDamage(entityA.damageoutput());
      }
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
