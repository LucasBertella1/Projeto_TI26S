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
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;

import java.util.Iterator;

public class MyGdxGame extends ApplicationAdapter {
    SpriteBatch batch;
    Texture img, tPersonagem, tTiro, tEspecial, tBoss;
    private AnimaçãoInimigo orcAnimacao;
    private Array<OrcInimigo> orcs;
    private Sprite personagem, tiro, especial, boss;
    private float posX, posY, velocidade;
    private float xTiro, yTiro, dirX, dirY, xEspecial, yEspecial;
    private boolean ataque, spawnBoss;
    private long tempoInimigo;
    private int score, inimigosMortos, scoreEspecial;
    private Sound somTiro, somEspecial, somMatarInimigo;
    private Music musicaFundo, bossMusic;
    private OrthographicCamera camera;


    private Rectangle bossRect;
    private int bossVida;

    /*Especial*/
    private boolean especialVisivel;
    private boolean especialDisponivel;
    private boolean ataqueEspecial;

    @Override
    public void create() {
        orcAnimacao = new AnimaçãoInimigo("orc1_walk_full.png", 6, 4, 0.1f);

        batch = new SpriteBatch();
        img = new Texture("bg.png");
        tPersonagem = new Texture("mago.png");
        tEspecial = new Texture("especial.png");
        tBoss = new Texture("Dissociado.png");

        personagem = new Sprite(tPersonagem);
        especial = new Sprite(tEspecial);
        boss = new Sprite(tBoss);

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
        inimigosMortos = 0;

        especialVisivel = false;
        especialDisponivel = false;
        ataqueEspecial = false;
        scoreEspecial = MathUtils.random(5, 10);

        musicaFundo = Gdx.audio.newMusic(Gdx.files.internal("musicafundo.mp3"));
        musicaFundo.setLooping(true);
        musicaFundo.setVolume(0.5f);
        musicaFundo.play();

        bossMusic = Gdx.audio.newMusic(Gdx.files.internal("musicaBoss.mp3"));

        somTiro = Gdx.audio.newSound(Gdx.files.internal("soundTiro.mp3"));
        somEspecial = Gdx.audio.newSound(Gdx.files.internal("soundEspecial.mp3"));
        somMatarInimigo = Gdx.audio.newSound(Gdx.files.internal("som_matar_inimigo.mp3"));

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        orcAnimacao.update(delta);

        this.movePersonagem();
        this.movimentoTiro();
        this.movimentoInimigo();
        this.verificaEspecial();
        this.movimentoEspecial();
        this.bossFinal();

        camera.update();
        batch.setProjectionMatrix(camera.combined);

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

        if (especialVisivel) {
            batch.draw(especial, xEspecial, yEspecial);
        }

        if (ataqueEspecial) {
            batch.draw(especial, xEspecial, yEspecial);
        }

        if (spawnBoss) {
            batch.draw(boss, bossRect.x, bossRect.y);
        }

        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        img.dispose();
        tPersonagem.dispose();
        orcAnimacao.dispose();
        tBoss.dispose();

        musicaFundo.dispose();
        bossMusic.dispose();

        somTiro.dispose();
        somEspecial.dispose();
        somMatarInimigo.dispose();
    }

    private void bossFinal() {
        if (!spawnBoss && inimigosMortos >= 3) {
            spawnBoss = true;
            musicaFundo.stop();
            bossMusic.setLooping(true);
            bossMusic.play();

            bossRect = new Rectangle(Gdx.graphics.getWidth() / 2 - boss.getWidth() / 2, Gdx.graphics.getHeight() - boss.getHeight(), boss.getWidth(), boss.getHeight());
            bossVida = 20;

            camera.zoom = 1.5f;
            Gdx.app.postRunnable(() -> {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                camera.zoom = 1f;
            });
        }

        if (spawnBoss) {
            bossRect.y -= 100 * Gdx.graphics.getDeltaTime();
            if (bossRect.y < Gdx.graphics.getHeight() / 2) {
                bossRect.y = Gdx.graphics.getHeight() / 2;
            }

            if (colisao(bossRect.x, bossRect.y, bossRect.width, bossRect.height, xTiro, yTiro, tiro.getWidth(), tiro.getHeight())) {
                bossVida--;
                ataque = false;

                if (bossVida <= 0) {
                    spawnBoss = false;
                    bossMusic.stop();
                    musicaFundo.play();
                    score += 100;
                }
            }
        }
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


        int vida;
        if (inimigosMortos >= 100) {
            vida = 5;
        } else if (inimigosMortos >= 75) {
            vida = 4;
        } else if (inimigosMortos >= 50) {
            vida = 3;
        } else if (inimigosMortos >= 25) {
            vida = 2;
        } else {
            vida = 1;
        }

        orcs.add(new OrcInimigo(rect, orcAnimacao, vida));
        tempoInimigo = TimeUtils.nanoTime();
    }

    private void movimentoInimigo() {
        if (TimeUtils.nanoTime() - tempoInimigo > 1000000000) {
            this.spawnInimigos();
        }

        for (Iterator<OrcInimigo> iter = orcs.iterator(); iter.hasNext(); ) {
            OrcInimigo orc = iter.next();
            if (colisao(orc.rect.x, orc.rect.y, 64, 64, xTiro, yTiro, tiro.getWidth(), tiro.getHeight())) {
                if (ataqueEspecial) {
                    orc.vida -= 2;
                } else {
                    orc.vida--;
                }

                ataque = false;

                if (orc.vida <= 0) {
                    //somMatarInimigo.play();
                    score += 10;
                    inimigosMortos++;
                    iter.remove();
                }

                if (inimigosMortos >= scoreEspecial) {
                    spawnEspecial();
                }
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

            somTiro.play();

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

    private void verificaEspecial() {

        if (especialVisivel && colisao(xEspecial, yEspecial, especial.getWidth(), especial.getHeight(), posX, posY, personagem.getWidth(), personagem.getHeight())) {
            especialVisivel = false;
            especialDisponivel = true;
        }

        if (especialDisponivel && Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            ataqueEspecial = true;
            especialDisponivel = false;

            somEspecial.play();

            xEspecial = posX + personagem.getWidth() / 2 - especial.getWidth() / 2;
            yEspecial = posY + personagem.getHeight() / 2 - especial.getHeight() / 2;

            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

            float dx = mouseX - xEspecial;
            float dy = mouseY - yEspecial;

            float magnitude = (float) Math.sqrt(dx * dx + dy * dy);
            dirX = dx / magnitude;
            dirY = dy / magnitude;
        }
    }

    private void spawnEspecial() {
        xEspecial = MathUtils.random(0, Gdx.graphics.getWidth() - especial.getWidth());
        yEspecial = MathUtils.random(0, Gdx.graphics.getHeight() - especial.getHeight());
        especialVisivel = true;

        scoreEspecial += MathUtils.random(5, 10);
    }

    private void movimentoEspecial() {
        if (ataqueEspecial) {
            xEspecial += dirX * 15;
            yEspecial += dirY * 15;


            if (xEspecial < 0 || xEspecial > Gdx.graphics.getWidth() || yEspecial < 0 || yEspecial > Gdx.graphics.getHeight()) {
                ataqueEspecial = false;
            }


            for (Iterator<OrcInimigo> iter = orcs.iterator(); iter.hasNext(); ) {
                OrcInimigo orc = iter.next();
                if (colisao(orc.rect.x, orc.rect.y, 64, 64, xEspecial, yEspecial, especial.getWidth(), especial.getHeight())) {
                    orc.vida -= 2;
                    if (orc.vida <= 0) {
                        score += 10;
                        inimigosMortos++;
                        iter.remove();
                    }
                    ataqueEspecial = false;
                    break;
                }
            }
        }
    }
}
