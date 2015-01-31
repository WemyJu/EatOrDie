package com.bubblegray.eatordie;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class SelectQuestion extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_question);
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
    
    static String[] foodType={
		"start","正餐","小點心","餓死了","有點飽","下午茶","宵夜"
		,"甜的","鹹的","米飯","麵食","餃類","關東煮"
		,"重鹹","清淡","中式","西式","日式","泰式"
		,"冷的","熱的","平價","高檔","速食","定食"
		,"肉類","蔬菜","牛肉","豬肉","雞肉","鴨肉","鵝肉","魚肉"
	};
	static Boolean visited[]={true};
	static int RanNum()
	{
		Random rand = new Random();
		return rand.nextInt(31)+1;
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
		while(id=RanNum()!=0 && !visited[id]){}
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
}
