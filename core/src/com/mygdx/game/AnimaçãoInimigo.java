package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;

public class AnimaçãoInimigo {

    private Texture texture;
    private TextureRegion[][] frames;
    private Animation<TextureRegion> andarBaixo;
    private Animation<TextureRegion> andarCima;
    private Animation<TextureRegion> andarTras;
    private Animation<TextureRegion> andarfrente;
    private float stateTime;

    public AnimaçãoInimigo(String sprite, int colunas, int linhas, float duracaoFrame) {
        texture = new Texture(sprite);
        frames = new TextureRegion[linhas][colunas];


        TextureRegion tmpRegion;
        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                tmpRegion = new TextureRegion(texture, j * (texture.getWidth() / colunas), i * (texture.getHeight() / linhas),
                        texture.getWidth() / colunas, texture.getHeight() / linhas);
                frames[i][j] = tmpRegion;
            }
        }

        andarBaixo = new Animation<>(duracaoFrame, frames[0]);
        andarCima = new Animation<>(duracaoFrame, frames[1]);
        andarTras = new Animation<>(duracaoFrame, frames[2]);
        andarfrente = new Animation<>(duracaoFrame, frames[3]);

        stateTime = 0f;
    }


    public void update(float delta) {
        stateTime += delta;
    }

    // isso aqui retorna o frame de acordo com a direção mas nao ta funcionando cima e baixo
    public TextureRegion frameAtual(String direction) {
        TextureRegion frameAtual = null;
        if ("baixo".equals(direction)) {
            frameAtual = andarBaixo.getKeyFrame(stateTime, true);
        } else if ("cima".equals(direction)) {
            frameAtual = andarCima.getKeyFrame(stateTime, true);
        } else if ("tras".equals(direction)) {
            frameAtual = andarTras.getKeyFrame(stateTime, true);
        } else if ("frente".equals(direction)) {
            frameAtual = andarfrente.getKeyFrame(stateTime, true);
        }
        return frameAtual;
    }


    public void dispose() {
        texture.dispose();
    }
}
