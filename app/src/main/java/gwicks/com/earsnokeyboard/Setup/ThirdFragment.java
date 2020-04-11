package gwicks.com.earsnokeyboard.Setup;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import gwicks.com.earsnokeyboard.R;

/**
 * Created by gwicks on 11/05/2018.
 * Part of the 3 page intro screen explaining what we are doing here
 */
public class ThirdFragment extends Fragment {

    private static final String TAG = "ThirdFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.splash_two, container, false);
        //((SetupStepOne)getActivity()).updateStatusBarColor("#0075e1", this);
        Log.d(TAG, "onCreateView: update coulour in : 3");
        return v;
    }

    public static ThirdFragment newInstance(String text) {

        Log.d(TAG, "newInstance: third");

        ThirdFragment f = new ThirdFragment();
        return f;
    }

}
