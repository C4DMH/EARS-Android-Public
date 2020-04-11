package gwicks.com.earsnokeyboard.Setup;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import gwicks.com.earsnokeyboard.R;

/**
 * Created by gwicks on 11/05/2018.
 * Part of the 3 page intro screen explaining what we are doing here
 */
public class SecondFragment extends Fragment {

    private static final String TAG = "SecondFragment";
//
//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        if (isVisibleToUser) {
//            ((SetupStepOne)getActivity()).updateStatusBarColor("#0075e1", this);
//            Log.d(TAG, "onCreateView: update coulour in : 2");
//
//        }
//        else {
//            Log.d(TAG, "setUserVisibleHint: in else");
//        }
//    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.splash_one, container, false);
        ((SetupStepOne)getActivity()).updateStatusBarColor("#0075e1", this);
        Log.d(TAG, "onCreateView: update coulour in : 2");






//        TextView tv = (TextView) v.findViewById(R.id.tvFragSecond);
//        tv.setText(getArguments().getString("msg"));

        return v;
    }

    public static SecondFragment newInstance(String text) {

        Log.d(TAG, "newInstance: second");

        SecondFragment f = new SecondFragment();


//        Bundle b = new Bundle();
//        b.putString("msg", text);
//
//        f.setArguments(b);

        return f;
    }


}

