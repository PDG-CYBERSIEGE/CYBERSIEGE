package pdg.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import pdg.game.Entity.Robot;
import pdg.game.Entity.Team;
import pdg.game.Screen.ArenaScreen;

/**
 * Gère la visée (drag), la prévisualisation de trajectoire et le lancement
 * des robots d'une équipe.
 *
 * Différences majeures avec l'ancienne classe Catapult (tuto Angry Birds) :
 *  - Pas de SpriteBatch/camera/Box2DDebugRenderer propres : ArenaScreen en
 *    a déjà, partagés pour toute la scène. Canon dessine dans le batch déjà
 *    ouvert par ArenaScreen (voir draw(batch)).
 *  - Pas de world.step() ici : déjà fait une fois par frame dans
 *    ArenaScreen.update(), sinon la simulation avancerait trop vite dès
 *    qu'il y a 2 canons (1 par joueur) sur le même World.
 *  - Travaille uniquement en mètres (comme le reste d'ArenaScreen), plus de
 *    mélange pixels/mètres.
 *  - La trajectoire prévisualisée réutilise la vraie gravité du World
 *    (world.getGravity()) au lieu d'une constante dupliquée : elle ne peut
 *    donc plus se désynchroniser du tir réel.
 *  - Plus de système d'index/indexC façon "file d'oiseaux" : la file de
 *    robots disponibles, c'est directement team.robots (ArrayList<Robot>),
 *    parcourue dans l'ordre.
 *
 * API supposée sur Robot (à ajouter si elle n'existe pas déjà) :
 *   boolean isReady()      -> le cooldown de ce robot est-il écoulé ?
 *   void startCooldown()   -> relance le cooldown de ce robot après un tir
 * (reduceCooldown() existe déjà et est appelé depuis ArenaScreen.teamUpdate)
 */
public class Canon {

    private static final float MAX_DRAG_DISTANCE = 50f; // px écran
    private static final float POWER_MULTIPLIER = 3f;
    private static final float VELOCITY_SCALE = 1f / ArenaScreen.PPM;
    private static final int TRAJECTORY_POINT_COUNT = 30;
    private static final float TRAJECTORY_TIME_STEP = 0.1f;

    private final World world;
    private final Team team;
    private final Vector2 canonPosition; // en pixels, comme le reste des positions "brutes" d'ArenaScreen
    private final Texture trajectoryDotTexture;

    private final Vector2 dragStart = new Vector2();
    private boolean isDragging = false;
    private boolean isAiming = false;
    private float power = 0f;
    private float angle = 0f;

    private int nextRobotIndex = 0;
    private Robot loadedRobot;

    private InputProcessor cachedInputProcessor;

    public Canon(World world, Team team, Vector2 canonPosition, Texture trajectoryDotTexture) {
        this.world = world;
        this.team = team;
        this.canonPosition = canonPosition;
        this.trajectoryDotTexture = trajectoryDotTexture;
        loadNextRobot();
    }

    /** Positionne le prochain robot de la file sur le canon, prêt à être tiré. */
    private void loadNextRobot() {
        if (nextRobotIndex >= team.robots.size()) {
            loadedRobot = null; // plus aucun robot disponible pour cette manche
            return;
        }
        loadedRobot = team.robots.get(nextRobotIndex);
        // Kinematic plutôt que Dynamic tant qu'il attend : évite qu'il tombe
        // sous l'effet de la gravité avant même d'être tiré (bug présent
        // dans l'ancienne version Catapult, où le body restait Dynamic).
        loadedRobot.getBody().setType(BodyDef.BodyType.KinematicBody);
        loadedRobot.getBody().setLinearVelocity(0, 0);
        loadedRobot.getBody().setTransform(
            canonPosition.x / ArenaScreen.PPM, canonPosition.y / ArenaScreen.PPM, 0);
    }

    public boolean hasRobotLoaded() {
        return loadedRobot != null;
    }

    public InputProcessor getInputProcessor() {
        if (cachedInputProcessor != null) return cachedInputProcessor;

        final Rectangle inputArea = new Rectangle(
            canonPosition.x - 50, canonPosition.y - 50, 150, 150);

        cachedInputProcessor = new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (loadedRobot == null || !loadedRobot.isReady()) return false;

                Vector2 pos = new Vector2(screenX, Gdx.graphics.getHeight() - screenY);
                if (!inputArea.contains(pos.x, pos.y)) return false;

                dragStart.set(screenX, Gdx.graphics.getHeight() - screenY);
                isDragging = true;
                isAiming = true;
                return true;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                if (!isDragging) return false;

                Vector2 dragEnd = new Vector2(screenX, Gdx.graphics.getHeight() - screenY);
                float distance = dragStart.dst(dragEnd);
                if (distance > MAX_DRAG_DISTANCE) {
                    dragEnd = dragStart.cpy().lerp(dragEnd, MAX_DRAG_DISTANCE / distance);
                }

                power = dragStart.dst(dragEnd) * POWER_MULTIPLIER;
                angle = dragEnd.sub(dragStart).angleDeg() - 180;
                return true;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                if (!isDragging) return false;
                isDragging = false;
                isAiming = false;
                fire();
                return true;
            }
        };

        return cachedInputProcessor;
    }

    private void fire() {
        if (loadedRobot == null) return;

        Vector2 launchVelocity = new Vector2(power, 0f)
            .setAngleDeg(angle)
            .scl(VELOCITY_SCALE);

        loadedRobot.getBody().setType(BodyDef.BodyType.DynamicBody);
        loadedRobot.getBody().setLinearVelocity(launchVelocity);
        loadedRobot.startCooldown();

        nextRobotIndex++;
        loadNextRobot();
    }

    /**
     * Rien à mettre à jour physiquement ici (world.step() est géré par
     * ArenaScreen). Gardé pour une éventuelle logique future (ex: micro
     * animation de visée) plutôt que d'ajouter une méthode plus tard.
     */
    public void update(float delta) {
    }

    /** A appeler ENTRE batch.begin() et batch.end() dans ArenaScreen.render(). */
    public void draw(SpriteBatch batch) {
        if (!isAiming) return;

        Vector2 v = new Vector2(power * VELOCITY_SCALE, 0f).setAngleDeg(angle);
        float gravity = world.getGravity().y; // même gravité que la simulation réelle

        float t = 0f;
        for (int i = 0; i < TRAJECTORY_POINT_COUNT; i++) {
            float x = canonPosition.x / ArenaScreen.PPM + v.x * t;
            float y = canonPosition.y / ArenaScreen.PPM + v.y * t + 0.5f * gravity * t * t;
            if (y < 0) break;

            batch.draw(trajectoryDotTexture, x, y, 10 / ArenaScreen.PPM, 10 / ArenaScreen.PPM);
            t += TRAJECTORY_TIME_STEP;
        }
    }

    /**
     * trajectoryDotTexture est géré par l'Asset manager d'ArenaScreen
     * (chargé/libéré une seule fois là-bas), donc rien à disposer ici.
     */
    public void dispose() {
    }
}
