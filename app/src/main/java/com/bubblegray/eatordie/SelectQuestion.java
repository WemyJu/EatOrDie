package com.bubblegray.eatordie;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class SelectQuestion extends ActionBarActivity{
    private TextView mTextView, choice1, choice2;
    private int i;
    private ArrayList<Integer> decisions = new ArrayList<Integer>();
    private static boolean visited[]= new boolean[40];
    private CountDownTimer CDT;
    private static final String[] foodType={
            "start","正餐","小點心","餓死了","有點飽","下午茶","宵夜"
            ,"甜的","鹹的","米飯","麵食","餃類","關東煮"
            ,"重鹹","清淡","中式","西式","日式","泰式"
            ,"冷的","熱的","平價","高檔","速食","定食"
            ,"肉類","蔬菜","牛肉","豬肉","雞肉","鴨肉","鵝肉","魚肉"
    };
    private final int numOfQuestions = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_question);

        mTextView = (TextView) findViewById(R.id.textView11);
        choice1 = (TextView) findViewById(R.id.choice1);
        choice1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decided(v);
            }
        });
        choice2 = (TextView) findViewById(R.id.choice2);
        choice2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                decided(v);
            }
        });
        Arrays.fill(visited, Boolean.TRUE);
        setup();
        getQuestionAndSetUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_question, menu);
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

    void getQuestionAndSetUI() {
        if (i >= numOfQuestions) {
            Collections.sort(decisions);
            Intent it = new Intent(this, ResultList.class);
            int numOfKeyWord = decisions.size();
            for(int j=0; j<decisions.size(); j++) {
                if(j>=1 && j<=6){
                    numOfKeyWord--;
                }
                else {
                    it.putExtra(j + "", foodType[decisions.get(j)]);
                }
            }
            it.putExtra("numOfKeyWord", numOfKeyWord);
            startActivity(it);
        } else {
            int id = TwoChooseOne();
            choice1.setText(foodType[id]);
            choice2.setText(foodType[id + 1]);

            CDT = new CountDownTimer(5000, 500) {
                @Override
                public void onFinish() {
                    // TODO Auto-generated method stub
                    i++;
                    getQuestionAndSetUI();
                }

                @Override
                public void onTick(long millisUntilFinished) {
                    // TODO Auto-generated method stub
                    mTextView.setText((millisUntilFinished / 1000) + 1 + "");
                }
            }.start();
        }
    }



    static int RanNum()
	{
		Random rand = new Random();
		return rand.nextInt(30)+1;
	}

	static void setup()
	{
	    Map food=new HashMap();
		//build the id-food map
		for(int i=1;i<foodType.length;i++)
			food.put(i, foodType[i]);
	}

	public int TwoChooseOne(){
		int id=0;
		//random out a number that hasn't visit
		while((id=RanNum())!=0 && !visited[id]){}
		visited[id]=false;
		if(id%2==1)
		    visited[id+1]=true;
		else
		{
		    visited[id-1]=true;
		    id-=1;
		}
	    return id;
	}

    public void decided(View v){
        i++;
        CDT.cancel();
        decisions.add(Arrays.asList(foodType).indexOf((String) ((TextView) v).getText()));
        getQuestionAndSetUI();
    }
}
