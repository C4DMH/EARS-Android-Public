package gwicks.com.earsnokeyboard.Setup;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import gwicks.com.earsnokeyboard.R;

/**
 * Created by gwicks on 11/05/2018.
 * No longer used
 */

public class SetupStepThreeOne extends AppCompatActivity {
    private static final String TAG = "SetupStepThreeOne";

    ImageView button;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.step_three_one);
        updateStatusBarColor("#1281e8");

        mContext = this;

        button = (ImageView) findViewById(R.id.imageView25);

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Clicked");

                // Switch 23rd Jan for Keyboard issue
                isKeyboardSelected();
                //Workaround:
//                Intent i = new Intent(SetupStepThreeOne.this, SetupStepThreeThree.class);
//
//                startActivity(i);


            }
        });

    }

    public void updateStatusBarColor(String color){// Color must be in hexadecimal fromat
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.d(TAG, "updateStatusBarColor: color change being called!");
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(color));
        }
    }


    public void isKeyboardSelected() {

    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

}

