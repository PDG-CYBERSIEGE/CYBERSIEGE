package pdg.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;

public class Asset {

    private final AssetManager manager = new AssetManager();

    public void loadSprites() {
        FileHandle folder = Gdx.files.internal("sprites");

        for (FileHandle file : folder.list(".png")) {
            manager.load(file.path(), Texture.class);
        }
    }

    public Texture getSprite(String name) {
        return manager.get(
            "sprites/" + name + ".png",
            Texture.class
        );
    }

    public void finishLoading() {
        manager.finishLoading();
    }

    public void dispose() {
        manager.dispose();
    }
}
