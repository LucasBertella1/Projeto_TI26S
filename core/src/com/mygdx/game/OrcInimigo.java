package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class OrcInimigo {
    public Rectangle rect;
    private AnimaçãoInimigo animacao;
    public int vida;

    public OrcInimigo(Rectangle rect, AnimaçãoInimigo animacao, int vida) {
        this.rect = rect;
        this.animacao = animacao;
        this.vida = vida;
    }

    public TextureRegion getCurrentFrame(String direction) {
        return animacao.frameAtual(direction);
    }
}
