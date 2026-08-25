package pdg.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;

public class King {
    private Texture texture;
    private Integer Health;
    private Body body;
    private float rectX,rectY;


    public King(String pigTexture, Integer Health, Body body){
        this.texture = new Texture(pigTexture);
        this.Health = Health;
        this.body = body;
    }
}
