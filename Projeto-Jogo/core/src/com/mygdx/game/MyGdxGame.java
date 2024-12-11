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
	private float xTiro, yTiro;
	private boolean ataque;
	private Array<Rectangle> inimigos;
	private long tempoInimigo;

	//Atribui valor e cria a instância
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("bg.png");
		tPersonagem = new Texture("personagem.png");
		personagem = new Sprite(tPersonagem);
		posX = Gdx.graphics.getWidth() / 2 - personagem.getWidth() / 2;
		posY = Gdx.graphics.getHeight() / 2 - personagem.getHeight() / 2;
		velocidade = 10;

		tTiro = new Texture("tiro2.png");
		tiro = new Sprite(tTiro);
		xTiro = posX;
		yTiro = posY;
		ataque = false;

		tInimigo = new Texture("Dissociado.png");
		inimigos = new Array<Rectangle>();
		tempoInimigo = 0;
	}

	int imgY = 0;
	@Override
	public void render () {
		this.movePersonagem();
		this.movimentoTiro();
		this.movimentoInimigo();

		ScreenUtils.clear(1, 0, 0, 1);

		batch.begin();
		batch.draw(img,0, 0);
		if(ataque){
			batch.draw(tiro, xTiro + personagem.getWidth() / 2 - 30, yTiro + personagem.getHeight() / 2 - 25);
		}

		batch.draw(personagem, posX, posY);

		for(Rectangle inimigo: inimigos){
			batch.draw(tInimigo, inimigo.x, inimigo.y);
		}
		batch.end();
	}


	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
		tPersonagem.dispose();
	}

	private void movePersonagem(){
		if(Gdx.input.isKeyPressed(Input.Keys.D)){
			//Limitando para que o personagem nao passe do tamanho da tela
			/*if(posX < 1280 -140){ // Setando os valores assim não fica dinamico e caso haja uma alteração teria que mudar todos
				posX += 10;
			}*/

			if(posX < Gdx.graphics.getWidth() - personagem.getWidth()){
				posX += velocidade;
			}
		}
		if(Gdx.input.isKeyPressed(Input.Keys.A)){
			if(posX > 0){
				posX -= velocidade;
			}
		}
		if(Gdx.input.isKeyPressed(Input.Keys.W)){
			if(posY < Gdx.graphics.getHeight() - personagem.getHeight()){
				posY += velocidade;
			}
		}
		if(Gdx.input.isKeyPressed(Input.Keys.S)){
			if(posY > 0){
				posY -= velocidade;
			}
		}
	}

	private void movimentoTiro(){
		if(Gdx.input.isKeyPressed(Input.Keys.SPACE) && !ataque){
			ataque = true;
			yTiro = posY;
		}
		if(ataque){
			if(xTiro < Gdx.graphics.getWidth()) {
				xTiro += 40;
			}else{
				xTiro = posX;
				ataque = false;
			}
		}else{
			xTiro = posX;
			yTiro = posY;
		}
	}

	private void spawnInimigos(){
		Rectangle inimigo = new Rectangle(Gdx.graphics.getWidth(),
				MathUtils.random(0, Gdx.graphics.getHeight() - tInimigo.getHeight()),
				tInimigo.getWidth(),
				tInimigo.getHeight());
		inimigos.add(inimigo);
		tempoInimigo = TimeUtils.nanoTime();

	}

	private void movimentoInimigo(){
		if(TimeUtils.nanoTime() - tempoInimigo > 1000000000){
			this.spawnInimigos();
		}

		for(Iterator<Rectangle> iter = inimigos.iterator();
		iter.hasNext();){
			Rectangle inimigo = iter.next();
			inimigo.x -= 400 * Gdx.graphics.getDeltaTime();
			if(inimigo.x + tInimigo.getWidth() < 0){
				iter.remove();
			}
		}
	}

}
