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

//        final Handler handler = new Handler();
//
//        Runnable checkOverlaySetting = new Runnable() {
//
//            @Override
//            //@TargetApi(23)
//            public void run() {
//                Log.d(TAG, "run: 1");
//                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//                    Log.d(TAG, "run: 2");
//                    //return;
//                }
//
//                // 18th Jan 2018, below works, trying to stop using the intent ( ie try back button below).
//                if (SetupSupport.isThisKeyboardSetAsDefaultIME(mContext)) {
//                    Log.d(TAG, "run: 3");
//                    //You have the permission, re-launch MainActivity
//                    Intent i = new Intent(SetupStepThreeOne.this, SetupStepThreeThree.class);
//                    Log.d(TAG, "run: 4");
//                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(i);
//                    return;
//                }
//
//
//                handler.postDelayed(this, 200);
//            }
//        };
//
//        InputMethodManager imeManager = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
//        imeManager.showInputMethodPicker();
//        handler.postDelayed(checkOverlaySetting, 1000);


    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

}

