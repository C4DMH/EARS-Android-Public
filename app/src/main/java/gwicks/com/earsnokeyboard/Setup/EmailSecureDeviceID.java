package gwicks.com.earsnokeyboard.Setup;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

/**
 * Created by gwicks on 11/05/2018.
 */

public class EmailSecureDeviceID extends DialogFragment {


    private String secureDeviceID;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        secureDeviceID = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);

    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Set the dialog title
        builder.setTitle("Please press Send Email to connect your email to your Device ID\nThis will enable us to connect your Survey answers to your phone data.")

                .setPositiveButton("Send Email", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        sendEmailDeviceID();

                    }
                });
        return builder.create();

    }

    public void sendEmailDeviceID() {
        String[] recipient = new String[]{"klmills@uoregon.edu"};


        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto","Randy.Auerbach@nyspi.columbia.edu", null));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {"kira.alqueza@nyspi.columbia.edu"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, secureDeviceID);
        emailIntent.putExtra(Intent.EXTRA_TEXT, "No Need to put anything here, just press send :)");
        startActivity(Intent.createChooser(emailIntent, "Select your most used email provider"));
    }
}