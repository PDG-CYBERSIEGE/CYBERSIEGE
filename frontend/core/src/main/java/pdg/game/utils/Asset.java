package pdg.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;

public class Asset {

  private final AssetManager manager = new AssetManager();

  /**
   * Parcourt récursivement tout le dossier assets (sprites/, ui/, ...) et met en file d'attente
   * tous les .png trouvés, puis bloque jusqu'à ce qu'ils soient tous chargés. Synchrone
   * volontairement : ArenaScreen appelle getTexture() juste après loadSprites(), donc les textures
   * doivent déjà être prêtes à ce moment-là.
   */
  public void loadSprites() {
    FileHandle root = Gdx.files.internal("");
    Gdx.app.log(
        "Asset",
        "Racine assets resolue vers: "
            + root.file().getAbsolutePath()
            + " (exists="
            + root.exists()
            + ")");
    queueDirectory(root);
    manager.finishLoading();
  }

  private void queueDirectory(FileHandle dir) {
    if (!dir.exists()) {
      return;
    }
    for (FileHandle file : dir.list()) {
      if (file.isDirectory()) {
        queueDirectory(file);
      } else if ("png".equalsIgnoreCase(file.extension())) {
        Gdx.app.log("Asset", "Texture mise en queue: " + file.path());
        manager.load(file.path(), Texture.class);
      }
    }
  }

  /**
   * Récupère une texture par son chemin complet relatif au dossier assets, par ex.
   * "sprites/king.png" ou "ui/back_button.png" — c'est ce chemin qui a été utilisé comme clé lors
   * du load() dans queueDirectory().
   */
  public Texture getTexture(String path) {
    return manager.get(path, Texture.class);
  }

  /** Raccourci pour les sprites du dossier "sprites/" identifiés juste par leur nom. */
  public Texture getSprite(String name) {
    return getTexture("sprites/" + name + ".png");
  }

  public void finishLoading() {
    manager.finishLoading();
  }

  public void dispose() {
    manager.dispose();
  }
}
