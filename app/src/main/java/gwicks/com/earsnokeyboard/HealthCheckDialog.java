package gwicks.com.earsnokeyboard;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import gwicks.com.earsnokeyboard.Setup.FinishInstallScreen;

/**
 * Created by gwicks on 30/05/2018.
 * Simple dialog to pop up if detects any risk states
 */

public class HealthCheckDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage("Based on your response, you indicated that you may feel unsafe.\n" +
                "We encourage you to talk with your parent or guardian as soon as possible.\n" +
                "If you cannot keep yourself safe, please call 911 or visit the closest emergency department.\n" +
                "There also are hotlines that may be of help to you: \n" +
                "(a) 1-800-SUICIDE,\n" +
                "(b) 1-888-SUICIDE,\n" +
                "(c) 1-800-273-TALK,\n" +
                "(d) 1-800-252-TEEN (Hotline for Teens),\n" +
                "(e) 1-800-850-8078 (Hotline for LGBT Teens).")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        Intent returnToFinish = new Intent(getActivity(), FinishInstallScreen.class);

                        startActivity(returnToFinish);
                    }
                });

        return builder.create();

    }

}

