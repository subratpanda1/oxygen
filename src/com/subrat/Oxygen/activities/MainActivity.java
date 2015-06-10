package com.subrat.Oxygen.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.subrat.Oxygen.R;
import com.subrat.Oxygen.activities.OxygenActivity;
import com.subrat.Oxygen.utilities.MathUtils;

public class MainActivity extends Activity {

    
    Button.OnClickListener onClickListener;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
    	System.loadLibrary("liquidfun");
        System.loadLibrary("liquidfun_jni");
        
        initializeMain();

        MathUtils.resources = getResources();
    }

    private void initializeMain() {
        onClickListener = new Button.OnClickListener() {
            public void onClick(View view) {
                startOxygenSimulator();
            }
        };

        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(Float.toString(getResources().getDisplayMetrics().xdpi) + " " +
                         Float.toString(getResources().getDisplayMetrics().density));

        Button button = (Button) findViewById(R.id.mainButton);
        button.setOnClickListener(onClickListener);
    }

    private void startOxygenSimulator() {
        // String displayMessage = getResources().getString(R.string.DisplayMessage);
        // AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        // alertDialog.setMessage(displayMessage);
        // alertDialog.show();
        Intent intent = new Intent(this, OxygenActivity.class);
        startActivity(intent);
    }
}
