package com.josephbleau.game.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.josephbleau.game.event.EventListener;
import com.josephbleau.game.event.EventPublisher;
import com.josephbleau.game.event.events.CollissionEvent;
import com.josephbleau.game.event.events.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity is the base object representing all "things" in the game, whether it be a player, a stage, an item, or some
 * other interactive element. It has various properties that affect the way the game interprets, renders, and updates them.
 */
public class Entity implements EventListener, EventPublisher {

    /** Current x position **/
    protected float xPos;

    /** Current y position **/
    protected float yPos;

    /** Current x velocity **/
    protected float xVel;

    /** Current y velocity **/
    protected float yVel;

    /** The last x location before the most recent update tick **/
    private float xPrevPos;

    /** The last y location before the most recent update tick. **/
    private float yPrevPos;

    /** Rectangles representing the shape of the entity **/
    private List<Rectangle> rects;

    /** Color that the shape is rendered as **/
    private Color outlineColor;

    /** Active being set to true means update() will process **/
    private Boolean active;

    /** Hidden being set to true means that render() will process **/
    private Boolean hidden;

    /** Collidable being set to true means that collisions are registered and published **/
    private Boolean collidable;

    /** Solid being set to true means that objects cannot pass through this one **/
    private Boolean solid;

    public Entity() {
        this.xPos = 0;
        this.yPos = 0;
        this.xPrevPos = 0;
        this.yPrevPos = 0;

        this.rects = new ArrayList<>();
        this.outlineColor = Color.BLACK;

        this.active = false;
        this.hidden = true;
        this.collidable = true;
        this.solid = true;
    }

    public Entity(final float startingX, final float startingY, final List<Rectangle> rects) {
        this();

        this.xPos = startingX;
        this.yPos = startingY;

        for (Rectangle rect : rects) {
            this.rects.add(new Rectangle(rect.x, rect.y, rect.width, rect.height));
        }
    }

    public void render(ShapeRenderer shapeRenderer) {
        if (this.hidden) {
            return;
        }

        for (Rectangle rect : rects) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(this.outlineColor);
            shapeRenderer.rect(this.xPos + rect.x, this.yPos + rect.y, rect.width, rect.height);
            shapeRenderer.end();
        }
    }

    public void update(float delta) {
        if (!this.active) {
            return;
        }

        this.xPrevPos = this.xPos;
        this.yPrevPos = this.yPos;

        this.xPos += this.xVel;
        this.yPos += this.yVel;
    }

    public void spawn(float x, float y) {
        this.hidden = false;
        this.active = true;

        this.xPos = x;
        this.yPos = y;
    }

    @Override
    public void notify(Event event) {}

    public CollissionEvent intersects(Entity otherEntity) {
        for (Rectangle rect : getTranslatedRects()) {
            for (Rectangle otherRect : otherEntity.getTranslatedRects()) {
                if (otherRect.overlaps(rect)) {
                    return new CollissionEvent(this, rect, otherEntity, otherRect);
                }
            }
        }

        return null;
    }

    public void applyForce(float xVel, float yVel) {
        this.xVel += xVel;
        this.yVel += yVel;
    }

    public void rollback() {
        this.xPos = this.xPrevPos;
        this.yPos = this.yPrevPos;
    }

    public List<Rectangle> getRects() {
        return rects;
    }

    public List<Rectangle> getTranslatedRects() {
        List<Rectangle> translatedRects = new ArrayList<Rectangle>();

        for (Rectangle rect : getRects()) {
            translatedRects.add(new Rectangle(this.xPos + rect.x, this.yPos + rect.y, rect.width, rect.height));
        }

        return translatedRects;
    }

    public Color getOutlineColor() {
        return outlineColor;
    }

    public Boolean isCollidable() {
        return collidable;
    }
}
