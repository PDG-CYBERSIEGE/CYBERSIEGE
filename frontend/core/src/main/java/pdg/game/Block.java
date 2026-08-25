package pdg.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;
import jdk.internal.net.http.common.Pair;

public class Block {

    private Texture blockTexture;
    private Integer Health;
    private Body body;
    private Pair<Float, Float> position;

    public Block(String blockTexture, Integer Health, Body body){
        this.blockTexture = new Texture(blockTexture);
        this.Health = Health;
        this.body = body;
    }
}
