package com.gohasu.coinman.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;

public class CoinMan extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture[] man;
	Texture dizzyMan;
	int manState;
	int pause = 0;

	float gravity = 0.2f;
	float velocity = 0;
	int manY = 0;
	Rectangle manRectangle;

	BitmapFont font;

	int score = 0;
	int gameState = 0;

	Random random;

	ArrayList<Integer> coinXs = new ArrayList<>();
	ArrayList<Integer> coinYs = new ArrayList<>();
	ArrayList<Rectangle> coinRectangles = new ArrayList<>();
	Texture coin;
	int coinCount;

	ArrayList<Integer> bombXs = new ArrayList<>();
	ArrayList<Integer> bombYs = new ArrayList<>();
	ArrayList<Rectangle> bombRectangles = new ArrayList<>();
	Texture bomb;
	int bombCount;



	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		man = new Texture[4];
		man[0] = new Texture("frame-1.png");
		man[1] = new Texture("frame-2.png");
		man[2] = new Texture("frame-3.png");
		man[3] = new Texture("frame-4.png");

		manY = Gdx.graphics.getHeight()/2;

		coin = new Texture("coin.png");
		random = new Random();

		bomb = new Texture("bomb.png");

		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);

		dizzyMan = new Texture("dizzy-1.png");
	}

	public void makeCoin() {
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		coinYs.add((int)height);
		coinXs.add(Gdx.graphics.getWidth());

	}

	public void makeBomb() {
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		bombYs.add((int) height);
		bombXs.add(Gdx.graphics.getWidth());
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if (gameState ==1) {				//Game is live
			//COIN
			if (coinCount < 100) {
				coinCount++;
			} else {
				coinCount = 0;
				makeCoin();
			}

			coinRectangles.clear();
			for (int i=0; i<coinXs.size(); i++) {
				batch.draw(coin, coinXs.get(i), coinYs.get(i));
				// change position of coinX on screen width which adjust the speed of coin moving
				coinXs.set(i, coinXs.get(i) - 6);

				// add coins visible on screen to Rectangle arrayList
				coinRectangles.add(new Rectangle(coinXs.get(i), coinYs.get(i), coin.getWidth(), coin.getHeight()));
			}

			// BOMB
			if (bombCount < 200) {
				bombCount++;
			} else {
				bombCount = 0;
				makeBomb();
			}


			bombRectangles.clear();
			for (int j=0; j<bombYs.size(); j++) {
				batch.draw(bomb, bombXs.get(j), bombYs.get(j));
				bombXs.set(j, bombXs.get(j) - 10);

				bombRectangles.add(new Rectangle(bombXs.get(j), bombYs.get(j), bomb.getWidth(), bomb.getHeight()));
			}

			if (Gdx.input.justTouched()) {			//if user just tap the phone screen
				velocity = -10;
			}

			//To make the man run slower with pause
			if (pause < 6) {
				pause++;
			} else {
				pause = 0;
				//to make the man change his state
				if (manState < 3) {
					manState++;
				} else {
					manState = 0;
				}
			}

			//make the man fall with gravity
			velocity += gravity;
			manY -= velocity;

			if (manY <= 0) {		//make the man can't fall beyond the screen
				manY = 0;
			}

		} else if (gameState == 0) {			//waiting to start
			if (Gdx.input.justTouched()) {
				gameState = 1;
			}

		} else if (gameState == 2) {			//game over

			if (Gdx.input.justTouched()) {
				gameState = 1;
				manY = Gdx.graphics.getHeight()/2;
				score = 0;
				velocity = 0;
				coinXs.clear();
				coinYs.clear();
				coinCount = 0;
				bombXs.clear();
				bombYs.clear();
				bombCount = 0;
			}
		}

		if (gameState == 2) {

			batch.draw(dizzyMan, Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY);

		} else {

			batch.draw(man[manState], Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY);
		}
		// create Rectangle for the man with position of man and size of the man
		manRectangle = new Rectangle(Gdx.graphics.getWidth()/2 - man[manState].getWidth()/2, manY,
				man[manState].getWidth(),
				man[manState].getHeight());

		for (int i =0; i< coinRectangles.size(); i++) {
			if (Intersector.overlaps(manRectangle, coinRectangles.get(i))) {
				score++;
				//To make the coin disappear when man hit it
				coinRectangles.remove(i);
				coinXs.remove(i);
				coinYs.remove(i);
				break;
			}
		}

		for (int i =0; i< bombRectangles.size(); i++) {
			if (Intersector.overlaps(manRectangle, bombRectangles.get(i))) {
				gameState = 2;
			}
		}

		font.draw(batch, String.valueOf(score), 100, 200);

		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
