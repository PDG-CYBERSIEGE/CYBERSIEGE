package pdg.game.ui;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import pdg.game.Entity.Team;

public class VerifyButton extends TextButton {

  private boolean activable = false;

  public VerifyButton(String text, Skin skin) {
    super(text, skin);
    setActivable(false);
  }

  public void setActivable(boolean activable) {
    this.activable = activable;
    setDisabled(!activable);
    setTouchable(activable ? Touchable.enabled : Touchable.disabled);
    getColor().a = activable ? 1f : 0.5f;
  }

  public boolean isActivable() {
    return activable;
  }

  /** A appeler chaque frame pour synchroniser l'état avec l'équipe. */
  public void update(Team team) {
    setActivable(team.isGravityEnabled() && team.isFullyPlaced());
  }
}
