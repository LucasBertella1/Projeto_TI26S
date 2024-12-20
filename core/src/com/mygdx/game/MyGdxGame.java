package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class MyGdxGame extends ApplicationAdapter {
    SpriteBatch batch;
    Texture img, tPersonagem, tTiro;
    private AnimaçãoInimigo orcAnimacao;
    private Array<OrcInimigo> orcs;
    private Sprite personagem, tiro;
    private float posX, posY, velocidade;
    private float xTiro, yTiro, dirX, dirY;
    private boolean ataque;
    private long tempoInimigo;
    private int score;

    @Override
    public void create() {
        orcAnimacao = new AnimaçãoInimigo("orc1_walk_full.png", 6, 4, 0.1f);

        batch = new SpriteBatch();
        img = new Texture("bg.png");
        tPersonagem = new Texture("mago.png");
        personagem = new Sprite(tPersonagem);
        posX = Gdx.graphics.getWidth() / 2 - personagem.getWidth() / 2;
        posY = Gdx.graphics.getHeight() / 2 - personagem.getHeight() / 2;
        velocidade = 10;

        tTiro = new Texture("tiro2.png");
        tiro = new Sprite(tTiro);
        xTiro = personagem.getWidth() / 2;
        yTiro = personagem.getHeight() / 2;
        ataque = false;

        orcs = new Array<>();
        tempoInimigo = 0;

        score = 0;
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        orcAnimacao.update(delta);

        this.movePersonagem();
        this.movimentoTiro();
        this.movimentoInimigo();

        ScreenUtils.clear(1, 0, 0, 1);

        batch.begin();
        batch.draw(img, 0, 0);
        if (ataque) {
            batch.draw(tiro, xTiro + personagem.getWidth() / 2 - 30, yTiro + personagem.getHeight() / 2 - 25);
        }


        batch.draw(personagem, posX, posY);


        for (OrcInimigo orc : orcs) {
            String direcao = determineDirecao(orc);
            batch.draw(orc.getCurrentFrame(direcao), orc.rect.x, orc.rect.y);
        }
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        img.dispose();
        tPersonagem.dispose();
        orcAnimacao.dispose();
    }

    private String determineDirecao(OrcInimigo orc) {
        if (orc.rect.x < posX) {
            return "frente";
        } else if (orc.rect.x > posX) {
            return "tras";
        } else if (orc.rect.y < posY) {
            return "baixo";
        } else {
            return "cima";
        }
    }

    private void spawnInimigos() {

        Rectangle rect = new Rectangle();

        int lado = MathUtils.random(0, 3);


        if (lado == 0) {
            rect.x = MathUtils.random(0, Gdx.graphics.getWidth() - 64);
            rect.y = Gdx.graphics.getHeight();
        }
        if (lado == 1) {
            rect.x = Gdx.graphics.getWidth();
            rect.y = MathUtils.random(0, Gdx.graphics.getHeight() - 64);
        }
        if (lado == 2) {
            rect.x = MathUtils.random(0, Gdx.graphics.getWidth() - 64);
            rect.y = -64;
        }
        if (lado == 3) {
            rect.x = -64;
            rect.y = MathUtils.random(0, Gdx.graphics.getHeight() - 64);
        }

        rect.width = 64;
        rect.height = 64;


        orcs.add(new OrcInimigo(rect, orcAnimacao));
        tempoInimigo = TimeUtils.nanoTime();
    }

    private void movimentoInimigo() {
        if (TimeUtils.nanoTime() - tempoInimigo > 1000000000) {
            this.spawnInimigos();
        }


        for (Iterator<OrcInimigo> iter = orcs.iterator(); iter.hasNext(); ) {
            OrcInimigo orc = iter.next();


            if (colisao(orc.rect.x, orc.rect.y, 64, 64, xTiro, yTiro, tiro.getWidth(), tiro.getHeight())) {
                score += 10;
                ataque = false;
                iter.remove();
            }


            float dirX = posX - orc.rect.x;
            float dirY = posY - orc.rect.y;
            float magnitude = (float) Math.sqrt(dirX * dirX + dirY * dirY);
            dirX /= magnitude;
            dirY /= magnitude;


            orc.rect.x += dirX * 200 * Gdx.graphics.getDeltaTime();
            orc.rect.y += dirY * 200 * Gdx.graphics.getDeltaTime();
        }
    }

    private boolean colisao(float x1, float y1, float w1, float h1, float x2, float y2, float w2, float h2) {
        return x1 + w1 > x2 && x1 < x2 + w2 && y1 + h1 > y2 && y1 < y2 + h2;
    }

    private void movePersonagem() {
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            if (posX < Gdx.graphics.getWidth() - personagem.getWidth()) {
                posX += velocidade;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            if (posX > 0) {
                posX -= velocidade;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            if (posY < Gdx.graphics.getHeight() - personagem.getHeight()) {
                posY += velocidade;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            if (posY > 0) {
                posY -= velocidade;
            }
        }
    }

    private void movimentoTiro() {
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && !ataque) {
            ataque = true;

            xTiro = posX;
            yTiro = posY;

            float dx = mouseX - posX;
            float dy = mouseY - posY;

            float magnitude = (float) Math.sqrt(dx * dx + dy * dy);
            dirX = dx / magnitude;
            dirY = dy / magnitude;
        }

        if (ataque) {
            xTiro += dirX * 10;
            yTiro += dirY * 10;

            if (xTiro < 0 || xTiro > Gdx.graphics.getWidth() || yTiro < 0 || yTiro > Gdx.graphics.getHeight()) {
                ataque = false;
            }
        } else {
            xTiro = posX;
            yTiro = posY;
        }
    }
}
