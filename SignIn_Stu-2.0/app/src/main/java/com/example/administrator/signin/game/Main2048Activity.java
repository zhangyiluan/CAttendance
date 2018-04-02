package com.example.administrator.signin.game;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.administrator.signin.R;
import com.example.administrator.signin.game.Game2048Layout.OnGame2048Listener;

public class Main2048Activity extends AppCompatActivity implements OnGame2048Listener{
    private Game2048Layout mGame2048Layout;

    private TextView mScore;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2048);

        mScore = (TextView) findViewById(R.id.id_score);
        mGame2048Layout = (Game2048Layout) findViewById(R.id.id_game2048);
        mGame2048Layout.setOnGame2048Listener(this);
    }

    @Override
    public void onScoreChange(int score)
    {
        mScore.setText("SCORE: " + score);
    }

    @Override
    public void onGameOver()
    {
        new AlertDialog.Builder(this).setTitle("GAME OVER")
                .setMessage("YOU HAVE GOT " + mScore.getText())
                .setPositiveButton("RESTART", new OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        mGame2048Layout.restart();
                    }
                }).setNegativeButton("EXIT", new OnClickListener()
        {

            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                finish();
            }
        }).show();
    }

}


