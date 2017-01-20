/* Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.ghost;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;


enum Players {
    PLAYER,
    COMPUTER
}

public class GhostActivity extends AppCompatActivity {
    private static final String TAG = "GhostActivity";

    private static final String KEY_USER_TURN = "keyUserTurn";
    private static final String KEY_CURRENT_WORD = "keyCurrentWord";
    private static final String KEY_SAVED_STATUS = "keySavedStatus";

    private GhostDictionary dictionary;
    private Players userTurn;
    private Random random = new Random();
    private String currentWord = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost);
        AssetManager assetManager = getAssets();
        try {
            InputStream inputStream = assetManager.open("words.txt");

            dictionary = new FastDictionary(inputStream);
            // new FastDictionary(inputStream);
        } catch (IOException e) {
            Toast toast = Toast.makeText(this, R.string
                    .load_dictionary_failed, Toast.LENGTH_LONG);
            toast.show();
        }

        ((Button) findViewById(R.id.resetButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentWord = "";
                onStart(null);
            }
        });

        if (savedInstanceState == null)
            onStart(null);
        else {
            boolean userTurn = savedInstanceState.getBoolean(KEY_USER_TURN);
            if (userTurn)
                this.userTurn = Players.PLAYER;
            else
                this.userTurn = Players.COMPUTER;
            currentWord = savedInstanceState.getString(KEY_CURRENT_WORD);
            String status = savedInstanceState.getString(KEY_SAVED_STATUS);
            ((TextView) findViewById(R.id.ghostText)).setText(currentWord);
            ((TextView) findViewById(R.id.gameStatus)).setText(status);
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_CURRENT_WORD, currentWord);
        outState.putBoolean(KEY_USER_TURN, userTurn == Players.PLAYER);
        outState.putString(KEY_SAVED_STATUS, ((TextView) findViewById(R.id
                .gameStatus)).getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ghost, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        int unicode = event.getUnicodeChar();
        if (('A' <= unicode && unicode <= 'Z') || 'a' <= unicode && unicode
                <= 'z') {
            String character = ((char) unicode + "").toLowerCase();
            currentWord += character;
            ((TextView) findViewById(R.id.ghostText)).setText(currentWord);
            computerTurn();
        }
        return super.onKeyUp(keyCode, event);
    }

    /**
     * Handler for the "Reset" button.
     * Randomly determines whether the game starts with a user turn or a
     * computer turn.
     *
     * @param view
     * @return true
     */
    public boolean onStart(View view) {
        userTurn = random.nextBoolean() ? Players.COMPUTER : Players.PLAYER;
        TextView text = (TextView) findViewById(R.id.ghostText);
        text.setText("");
        TextView label = (TextView) findViewById(R.id.gameStatus);
        switch (userTurn) {
            case PLAYER:
                label.setText(R.string.user_turn);
                break;
            case COMPUTER:
                label.setText(R.string.computer_turn);
                computerTurn();
                break;
        }
        return true;
    }

    /**
     * Challenges the current word. Returns true if the challenge was
     * successful, false otherwise.
     *
     * @param fromUser
     * @return
     */
    private boolean doChallenge(Players fromUser) {
        TextView status = (TextView) findViewById(R.id.gameStatus);
        if (dictionary.isWord(currentWord)) {
            switch (fromUser) {
                case COMPUTER: // It is a word! The user loses.
                    status.setText(String.format("%s is a word. The computer " +
                            "wins!", currentWord));
                    break;
                case PLAYER: // The computer loses, it has formed a word.
                    status.setText(String.format("%s is a word. You win!",
                            currentWord));
            }
            return true;
        } else if (TextUtils.isEmpty(dictionary.getAnyWordStartingWith
                (currentWord))) {
            switch (fromUser) {
                case COMPUTER: // This is not a valid word prefix. The user
                    // loses.
                    status.setText(String.format("%s is an invalid prefix. " +
                                    "The computer wins!",
                            currentWord));
                    break;
                case PLAYER:
                    status.setText(String.format("%s is an invalid prefix. " +
                            "You win!", currentWord));
            }
            return true;
        }
        if (fromUser == Players.PLAYER) {
            // We've challenged and failed. The user loses.
            status.setText(String.format("%s is a valid prefix and not a word" +
                            ". The computer wins!",
                    currentWord));
        }
        return false;
    }

    private void computerTurn() {
        TextView status = (TextView) findViewById(R.id.gameStatus);

        // Checks if the user's currentWord is a full word, or if it is an
        // invalid prefix.
        boolean challengeSuccessful = doChallenge(Players.COMPUTER);
        if (challengeSuccessful) {
            return;
        }

        userTurn = Players.COMPUTER;

        // TODO(you): Use a Handler to post this after some time (half second?)

        // Do computer turn stuff then make it the user's turn again
        status.setText(R.string.computer_turn);
        String next = dictionary.getGoodWordStartingWith(currentWord);
        currentWord += next.charAt(currentWord.length());
        // We can now update the text field with the computer's word.
        ((TextView) findViewById(R.id.ghostText)).setText(currentWord);

        challengeSuccessful = doChallenge(Players.PLAYER);
        if (challengeSuccessful) {
            return;
        }

        // Keep playing...
        userTurn = Players.COMPUTER;
        status.setText(R.string.user_turn);
    }
}