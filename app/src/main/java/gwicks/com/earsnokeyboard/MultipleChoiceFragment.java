package gwicks.com.earsnokeyboard;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;

public class MultipleChoiceFragment extends Fragment {

    private static final String TAG = "MultipleChoiceFragment";
    public static final String EXTRA_MESSAGE1 = "EXTRA_MESSAGE1";
    private View mView;
    private ArrayList<String> options;

    private RadioGroup radioGroup;
    private RadioButton radioButton;


    public static MultipleChoiceFragment newInstance(ArrayList<String> options){

        Log.d(TAG, "MultipleChoiceFragment: ");
        MultipleChoiceFragment f = new MultipleChoiceFragment();
        Bundle bdl = new Bundle(1);
        bdl.putStringArrayList(EXTRA_MESSAGE1, new ArrayList<String>());
        f.setArguments(bdl);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        options = getArguments().getStringArrayList("list");
        Log.d(TAG, "onCreateView: options size: " + options.size());

        for(String s : options){
            Log.d(TAG, "onCreateView2: " + s);
        }
        mView = inflater.inflate(R.layout.multiple_choice, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final View v = mView;

        radioGroup = (RadioGroup)v.findViewById(R.id.radioSex);

        int length = options.size();

        RadioButton[] radioButtons = new RadioButton[length];
        for(int i=0;i<length;i++){
            radioButtons[i] = new RadioButton(getActivity());
            radioButtons[i].setText(options.get(i));
            radioButtons[i].setId(i);
            radioButtons[i].setButtonDrawable(R.drawable.custom_radio_button);
            radioButtons[i].setTextColor(getResources().getColor(R.color.white));
            radioButtons[i].setTextSize(30);
            radioGroup.addView(radioButtons[i]);
        }
    }

    public boolean check(){
        int selectedID = radioGroup.getCheckedRadioButtonId();
        if(selectedID == -1){
            return false;
        }
        return true;
    }


    public String getSelection(){
        String s;

        int selectedID = radioGroup.getCheckedRadioButtonId();
        Log.d("tag","selectId" + selectedID );
        radioButton = (RadioButton)mView.findViewById(selectedID);
        Log.d("tag","radioButton" + radioButton);
        s = options.get(selectedID);
        Log.d(TAG, "addListenerOnButton: string is: " + s);
        return s;
    }
}
