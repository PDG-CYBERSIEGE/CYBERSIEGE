package pdg.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import pdg.game.Entity.Team;

public class GravityButton extends TextButton {

  private boolean gravityOff;
  private boolean validated;
  private Team team;

  public GravityButton(String text, Skin skin, Team team) {
    super(text, skin);

    validated = false;

    this.team = team;
    this.gravityOff = true;
    updateVisual();

    this.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            if (!validated){
              gravityOff = !gravityOff;
              updateVisual();
              team.changeGravity();
            }
          }
        });
  }

  private void updateVisual() {
    setText(!gravityOff ? "ON" : "OFF");
    setColor(!gravityOff ? Color.GREEN : Color.RED);
  }

  public void validated() {
    validated = true;
  }

  public void unValidated() {
    validated = false;
  }
}
