package gwicks.com.earsnokeyboard;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;

import static com.google.android.gms.vision.face.FaceDetector.FAST_MODE;
import static com.google.android.gms.vision.face.FaceDetector.NO_CLASSIFICATIONS;
import static com.google.android.gms.vision.face.FaceDetector.NO_LANDMARKS;
import static java.lang.System.exit;

// This class uses Async Task to iterate through all photos taken in the last 24hours, detect which ones have face / faces in them, then crop that image out and trasnfer it as a new, slightly compresses JPG to /CroppedImages folder.

public class FaceDetectAndCrop extends AsyncTask<String, Void, String> {


    private static final String TAG = "FaceDetectAndCrop";
    Context mContext;
    String path;

    float x1;
    float y1;
    float x2;
    float y2;

    static String folder = "/CroppedImages/";
    private FaceDetector faceDetector;


    FaceDetectAndCrop(Context context){
        mContext = context;

//        mEncryption = new Encryption();
//        mTransferUtility = Util.getTransferUtility(mContext);

        path = mContext.getExternalFilesDir(null) + "/videoDIARY/CroppedImages/";
        File directory = new File(path);
        if(!directory.exists()){
            directory.mkdirs();
        }
    }

    // Testing method, not used in actual code

    public void detectAndCrop(Context context){



        path = mContext.getExternalFilesDir(null) + "/videoDIARY/CroppedImages/";
        File directory = new File(path);
        if(!directory.exists()){
            directory.mkdirs();
        }

        File newImageFile = new File (path, "12121.jpg" );

        Log.d(TAG, "detectAndCrop: is directory: " + newImageFile.isDirectory());
        Log.d(TAG, "detectAndCrop: is file: " + newImageFile.isFile());
        Log.d(TAG, "detectAndCrop: filepath: " + newImageFile.getAbsolutePath());

        OutputStream fOutputStream = null;

        String CameraD = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/Camera/";

        String fileString =CameraD + "IMG_20190414_195529.jpg";

        Log.d(TAG, "detectAndCrop: fileString: " + fileString);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable=true;

        Bitmap myBitmap = BitmapFactory.decodeFile(fileString);

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(myBitmap, myBitmap.getWidth()/10, myBitmap.getHeight() /10, false);
        Log.d(TAG, "scaled width: " + scaledBitmap.getWidth() + " height: " + scaledBitmap.getHeight());

        FaceDetector faceDetector = new
                FaceDetector.Builder(mContext.getApplicationContext()).setTrackingEnabled(false).setMode(FAST_MODE).setLandmarkType(NO_LANDMARKS).setClassificationType(NO_CLASSIFICATIONS)
                .build();
        if(!faceDetector.isOperational()){
            Log.d(TAG, "detectAndCrop: error, not face detector");
            return;
        }

        Frame frameScaled = new Frame.Builder().setBitmap(scaledBitmap).build();
        SparseArray<Face> faces = faceDetector.detect(frameScaled);
        int k = 0;

        Log.d(TAG, "onClick: 5");
        for(int i=0; i<faces.size(); i++) {
            Face thisFace = faces.valueAt(i);
            x1 = Math.max(0,thisFace.getPosition().x);
            y1 = Math.max(0,thisFace.getPosition().y);
            x2 = x1 + thisFace.getWidth();
            y2 = y1 + thisFace.getHeight();
            //tempCanvas.drawRoundRect(new RectF(x1, y1, x2, y2), 2, 2, myRectPaint);
            Log.d(TAG, "onClick: x,y,x,y: " + x1 + ", " + y1 + ", " + x2 + ", " + y2);

            int a = Math.round(x1) * 10;
            int b = Math.round(y1) * 10;
            int c = Math.round(x2-x1)* 10;
            int d = Math.round(y2- y1)* 10;
            int e = Math.round(x2) * 10;
            int f = Math.round(y2) * 10;
            Log.d(TAG, "onClick: int a: " + a + " int b " + b + " int c: " + e + "int f: " + f);



            Bitmap newBitnmap  = Bitmap.createBitmap(myBitmap, a,b,c,d);
            try{
                Log.d(TAG, "detectAndCrop: 1");
                fOutputStream = new FileOutputStream(newImageFile);
                Log.d(TAG, "detectAndCrop: 2");
                newBitnmap.compress(Bitmap.CompressFormat.WEBP, 40, fOutputStream);
                Log.d(TAG, "detectAndCrop: 3");
                fOutputStream.flush();
                fOutputStream.close();
                MediaStore.Images.Media.insertImage(mContext.getContentResolver(), newImageFile.getAbsolutePath(), newImageFile.getName(), newImageFile.getName());
                Log.d(TAG, "detectAndCrop: 4");

            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }catch (IOException e3) {
                e3.printStackTrace();
            }
            Log.d(TAG, "onClick: x,y,x,y: " + x1 +", " + y1 + ", " +  x2 +", " + y2);
        }
        faceDetector.release();
    }


    public boolean checkPermissionForReadExtertalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = mContext.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }


    @Override
    protected String doInBackground(String... strings) {

        path = mContext.getExternalFilesDir(null) + "/videoDIARY/CroppedImages/";
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // 14th April, attempt to merge photo upload receiver and cropping

        String CameraD = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/Camera/";
        File CameraDirectory = new File(CameraD);
        Long currentTime = System.currentTimeMillis();
        int dayTime = 24 * 60 * 60 * 1000;
        Long finalTime = currentTime - dayTime;
        Log.d(TAG, "getPhotos: the current time is: " + currentTime);

        ArrayList<File> photos = new ArrayList<>();

        Log.d(TAG, "doInBackground: Do I have permission to check the photos: " + checkPermissionForReadExtertalStorage());




        Log.d(TAG, "getPhotos: the cameraDirectory is: " + CameraDirectory.toString());
        File[] files = CameraDirectory.listFiles();
        if (!CameraDirectory.isDirectory()) {
            Log.d(TAG, "getPhotos: not a directory!! ");
            exit(0);
        }

        if (CameraDirectory.isDirectory()) {
            Log.d(TAG, "getPhotos: directory!! ");
        }

        // added return on 20th feb 2019 because of crash, not sure if just no photos or if a permissions problem?

        if (files == null) {
            Log.d(TAG, "getPhotos: NULLLLLLL");
            return "error";
        }

        Log.d(TAG, "getPhotos: size of array is: " + files.length);

        for (File CurFile : files) {
            String fileName = CurFile.toString();
            Log.d(TAG, "onReceive: filename is: " + fileName);

            Log.d(TAG, "getPhotos: the current file is: " + CurFile);

            if (CurFile.isDirectory()) {
                Log.d(TAG, "getPhotos: is a directory");
                //CameraDirectory=CurFile.getName();
                continue;
            }

            Log.d(TAG, "getPhotos: MODIFIED: " + CurFile.lastModified());
            Log.d(TAG, "getPhotos: HTE NAME IS: " + CurFile.getName());
            Date d = new Date(CurFile.lastModified());
            Log.d(TAG, "getPhotos: MODIFIED NUMBER 2: " + d);
            if ((CurFile.lastModified() > finalTime) && fileName.contains("jpg")) {
                Log.d(TAG, "***************************getPhotos: The phtot: " + CurFile + " was taken in the last 24 hours");
                photos.add(CurFile);
            }

        }
        faceDetector = new
                FaceDetector.Builder(mContext.getApplicationContext()).setTrackingEnabled(false).setMode(FAST_MODE).setLandmarkType(NO_LANDMARKS).setClassificationType(NO_CLASSIFICATIONS)
                .build();
        if (!faceDetector.isOperational()) {
            Log.d(TAG, "detectAndCrop: error, not face detector");
            return "error with detector";
        }

        for (File each : photos) {
            int j = 0;
            Log.d(TAG, "doInBackground: new PHOTO ----------------------------------------");


            OutputStream fOutputStream = null;

            String filename = each.getName();

            Log.d(TAG, "onReceive: path = " + each.getAbsolutePath());
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inMutable = true;

            Log.d(TAG, "doInBackground: cameraD + filename: " + CameraD + filename);

            Bitmap myBitmap = BitmapFactory.decodeFile(CameraD + filename);

            int width = (int) myBitmap.getWidth();
            int height = (int) myBitmap.getHeight();


            Log.d(TAG, "doInBackground: original width, height: " + width + ", " + height);
            Bitmap scaledBitmap;

            try {
                scaledBitmap = Bitmap.createScaledBitmap(myBitmap, myBitmap.getWidth() / 10, myBitmap.getHeight() / 10, false);
            } catch (Exception e) {
                Log.d(TAG, "doInBackground: error: " + e);
                return "error";
            }

//            Bitmap scaledBitmap = Bitmap.createScaledBitmap(myBitmap, myBitmap.getWidth() / 10, myBitmap.getHeight() / 10, false);
            Log.d(TAG, "scaled width: " + scaledBitmap.getWidth() + " height: " + scaledBitmap.getHeight());

            int scaledWidth = scaledBitmap.getWidth();
            int scaledHeight = (int) scaledBitmap.getHeight();


            Frame frameScaled = new Frame.Builder().setBitmap(scaledBitmap).build();
            SparseArray<Face> faces = faceDetector.detect(frameScaled);

            Log.d(TAG, "doInBackground: faces sie: " + faces.size());

            Log.d(TAG, "onClick: 5");
            for (int i = 0; i < faces.size(); i++) {
                Log.d(TAG, "doInBackground: NEW FACE _____________________________________");
                File newImageFile = new File(path, j +"_"+filename);

                Log.d(TAG, "doInBackground: newImageFile: " + newImageFile.toString() + "    " + newImageFile.getAbsolutePath());

                Face thisFace = faces.valueAt(i);
                x1 = Math.max(0, thisFace.getPosition().x);
                y1 = Math.max(0, thisFace.getPosition().y);
                x2 = Math.min(scaledWidth, x1 + thisFace.getWidth());
                y2 = Math.min(scaledHeight, y1 + thisFace.getHeight());
                //tempCanvas.drawRoundRect(new RectF(x1, y1, x2, y2), 2, 2, myRectPaint);

                Log.d(TAG, "onClick: x,y,x,y: " + x1 + ", " + y1 + ", " + x2 + ", " + y2);

                int a = Math.round(x1) * 10;
                int b = Math.round(y1) * 10;
                int c = Math.round(x2 - x1) * 10;
                int d = Math.round(y2 - y1) * 10;
                int e = Math.round(x2) * 10;
                int f = Math.round(y2) * 10;
                Log.d(TAG, "onClick: int a: " + a + " int b " + b + " int c: " + e + "int f: " + f);

                Log.d(TAG, "doInBackground: before new bitmap");

                Bitmap newBitnmap;

                try {
                    newBitnmap = Bitmap.createBitmap(myBitmap, a, b, c, d);
                } catch (Exception e1) {
                    Log.d(TAG, "doInBackground: error: " + e1);
                    return "error";
                }

                Log.d(TAG, "doInBackground: after new bitmap");
                try {
                    Log.d(TAG, "detectAndCrop: 1");
                    fOutputStream = new FileOutputStream(newImageFile);
                    Log.d(TAG, "detectAndCrop: 2");
                    newBitnmap.compress(Bitmap.CompressFormat.JPEG, 80, fOutputStream);
                    Log.d(TAG, "detectAndCrop: 3");
                    fOutputStream.flush();
                    Log.d(TAG, "doInBackground: 3.1");
                    fOutputStream.close();
                    Log.d(TAG, "doInBackground: 3.2");
                    //MediaStore.Images.Media.insertImage(mContext.getContentResolver(), newImageFile.getAbsolutePath(), newImageFile.getName(), newImageFile.getName());
                    Log.d(TAG, "doInBackground: 3.3");
                    // MediaStore.Images.Media.insertImage(mContext.getContentResolver(), newBitnmap, newImageFile.getName() + "1111", newImageFile.getName());
                    Log.d(TAG, "detectAndCrop: 4");

                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
                j++;
            }
        }
        faceDetector.release();

        return "something";
    }


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        Log.d(TAG, "onPostExecute: string s = " + s);
    }
}
