package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;


//Define os objetos que irá ser utilizados
public class MyGdxGame extends ApplicationAdapter {
    SpriteBatch batch;
    Texture img, tPersonagem, tTiro, tInimigo;

    private Sprite personagem, tiro;
    private float posX, posY, velocidade;
    private float xTiro, yTiro, dirX, dirY;
    private boolean ataque;
    private Array<Rectangle> inimigos;
    private long tempoInimigo;
    private int score;

    //Atribui valor e cria a instância
    @Override
    public void create() {
        batch = new SpriteBatch();
        img = new Texture("bg.png");
        tPersonagem = new Texture("personagem.png");
        personagem = new Sprite(tPersonagem);
        posX = Gdx.graphics.getWidth() / 2 - personagem.getWidth() / 2;
        posY = Gdx.graphics.getHeight() / 2 - personagem.getHeight() / 2;
        velocidade = 10;

        tTiro = new Texture("tiro2.png");
        tiro = new Sprite(tTiro);
        xTiro = personagem.getWidth() / 2;
        yTiro = personagem.getHeight() / 2;
        ataque = false;

        tInimigo = new Texture("Dissociado.png");
        inimigos = new Array<Rectangle>();
        tempoInimigo = 0;

        score = 0;
    }

    int imgY = 0;

    @Override
    public void render() {
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

        for (Rectangle inimigo : inimigos) {
            batch.draw(tInimigo, inimigo.x, inimigo.y);
        }
        batch.end();
    }


    @Override
    public void dispose() {
        batch.dispose();
        img.dispose();
        tPersonagem.dispose();
    }

    private void movePersonagem() {
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            //Limitando para que o personagem nao passe do tamanho da tela
			/*if(posX < 1280 -140){ // Setando os valores assim não fica dinamico e caso haja uma alteração teria que mudar todos
				posX += 10;g
			}*/

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
            xTiro += dirX * 40;
            yTiro += dirY * 40;


            if (xTiro < 0 || xTiro > Gdx.graphics.getWidth() || yTiro < 0 || yTiro > Gdx.graphics.getHeight()) {
                ataque = false;
            }
        } else {
            xTiro = posX;
            yTiro = posY;
        }
    }

    private void spawnInimigos() {

        Rectangle inimigo = new Rectangle();

        int lado = MathUtils.random(0, 3);

        if (lado == 0) {//topo
            inimigo.x = MathUtils.random(0, Gdx.graphics.getWidth() - tInimigo.getWidth());
            inimigo.y = Gdx.graphics.getHeight();
        }
        if (lado == 1) {//direita
            inimigo.x = Gdx.graphics.getWidth();
            inimigo.y = MathUtils.random(0, Gdx.graphics.getHeight() - tInimigo.getHeight());

        }
        if (lado == 2) {//base
            inimigo.x = MathUtils.random(0, Gdx.graphics.getWidth() - tInimigo.getWidth());
            inimigo.y = -tInimigo.getHeight();
        }
        if (lado == 3) {//esquerda
            inimigo.x = -tInimigo.getWidth();
            inimigo.y = MathUtils.random(0, Gdx.graphics.getHeight() - tInimigo.getHeight());
        }

        inimigo.width = tInimigo.getWidth();
        inimigo.height = tInimigo.getHeight();

        inimigos.add(inimigo);
        tempoInimigo = TimeUtils.nanoTime();
    }


    private void movimentoInimigo() {
        if (TimeUtils.nanoTime() - tempoInimigo > 1000000000) {
            this.spawnInimigos();
        }

        for (Iterator<Rectangle> iter = inimigos.iterator(); iter.hasNext(); ) {
            Rectangle inimigo = iter.next();

            if (colisao(inimigo.x, inimigo.y, inimigo.width, inimigo.height, xTiro, yTiro, tiro.getWidth(), tiro.getHeight())) {
                score += 10;
                ataque = false;
                iter.remove();
            }

            if (colisao(inimigo.x, inimigo.y, inimigo.width, inimigo.height, posX, posY, personagem.getWidth(), personagem.getHeight())) {
                //desenvolver vida se tiver, ou a tela de game overd
                iter.remove();
            }

            float dirX = posX - inimigo.x;
            float dirY = posY - inimigo.y;

            float magnitude = (float) Math.sqrt(dirX * dirX + dirY * dirY);
            dirX /= magnitude;
            dirY /= magnitude;


            inimigo.x += dirX * 200 * Gdx.graphics.getDeltaTime();
            inimigo.y += dirY * 200 * Gdx.graphics.getDeltaTime();


            if (inimigo.x + inimigo.width < 0 || inimigo.x > Gdx.graphics.getWidth() ||
                    inimigo.y + inimigo.height < 0 || inimigo.y > Gdx.graphics.getHeight()) {
                iter.remove();
            }
        }
    }

    private boolean colisao(float x1, float y1, float w1, float h1, float x2, float y2, float w2, float h2) {
        if (x1 + w1 > x2 && x1 < x2 + w2 && y1 + h1 > y2 && y1 < y2 + h2) {
            return true;
        }
        return false;
    }
}
