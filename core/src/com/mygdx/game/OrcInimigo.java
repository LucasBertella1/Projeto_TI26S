package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class OrcInimigo {
    public Rectangle rect;
    private AnimaçãoInimigo animacao;

    public OrcInimigo(Rectangle rect, AnimaçãoInimigo animacao) {
        this.rect = rect;
        this.animacao = animacao;
    }

    public TextureRegion getCurrentFrame(String direction) {
        return animacao.frameAtual(direction);
    }
}
