package gwicks.com.earsnokeyboard.Setup;

public class GetStudyData implements GetRawData.OnDownloadComplete {
    String mStudy;

    GetStudyData(String study){
        mStudy = study;
    }

    @Override
    public void onDownloadComplete(String data, DownloadStatus status) {

    }


    public void createStudy(String study){

        GetRawData createStudyGetRawData = new GetRawData(this);
        createStudyGetRawData.execute("https://7ocx4sxhze.execute-api.us-west-2.amazonaws.com/default/get-study-variables?study=" + study);

    }
}
