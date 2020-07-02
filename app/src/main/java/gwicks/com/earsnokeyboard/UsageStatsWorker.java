package gwicks.com.earsnokeyboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import gwicks.com.earsnokeyboard.Setup.FinishInstallScreen;

public class UsageStatsWorker extends Worker {

    private static final String TAG = "WorkManagerUsage";
    public Context mContext;
    TransferUtility transferUtility;
    String Uri;
    String encryptedUri;
    Encryption mEncryption;

    String userID;
    static String folder = "/APPUSAGE/";

    public UsageStatsWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {


        super(context, workerParams);
        mContext = context;
        Log.d(TAG, "WorkManagerUsage: in");
    }


    @NonNull
    @Override
    public Result doWork() {


        Log.d(TAG, "onReceive: we have started onrecieve");
        userID = FinishInstallScreen.secureID;
        mEncryption = new Encryption();
        transferUtility = Util.getTransferUtility(mContext);
        Log.d(TAG, "onReceive: transfer utility = " + transferUtility);

        Uri = UStats.printCurrentUsageStatus(mContext);
        System.out.println("The uri is: " + Uri);

        String path = mContext.getExternalFilesDir(null) + "/APPUSAGE/";

        File directory = new File(path);
        if(!directory.exists()){
            directory.mkdirs();
        }

        ArrayList<File> files = new ArrayList<>(Arrays.asList(directory.listFiles()));
        Util.uploadFilesToBucket(files, true,logUploadCallback, mContext, folder);

        Data outputData = new Data.Builder().putString("Result", "Jobs Finished").build();
        return Result.success(outputData);

    }

    final Util.FileTransferCallback logUploadCallback = new Util.FileTransferCallback() {
        @SuppressLint("DefaultLocale")

        private String makeLogLine(final String name, final int id, final TransferState state) {
            Log.d("LogUploadTask", "This is AWSBIT");
            return String.format("%s | ID: %d | State: %s", name, id, state.toString());
        }

        @Override
        public void onCancel(int id, TransferState state) {
            Log.d(TAG, makeLogLine("Callback onCancel()", id, state));
        }

        @Override
        public void onStart(int id, TransferState state) {
            Log.d(TAG, makeLogLine("Callback onStart()", id, state));

        }

        @Override
        public void onComplete(int id, TransferState state) {
            Log.d(TAG, makeLogLine("Callback onComplete()", id, state));
            Log.d(TAG, "onComplete: should I delete here?");
        }

        @Override
        public void onError(int id, Exception e) {
            Log.d(TAG, makeLogLine("Callback onError()", id, TransferState.FAILED), e);
        }
    };

}
