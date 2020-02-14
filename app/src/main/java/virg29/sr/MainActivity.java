package virg29.sr;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends Activity {
    private AudioRecord recorder;
    Thread recordingThread;
    boolean isRecording = false;
    boolean destroyed;
    byte Data[];
    int bufferSize = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
    DBHelper sq;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        Button recordingButton = (Button)findViewById(R.id.recordingStartButton);
        recordingButton.setText("start");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            showToast("PERMISSION NOT GRANTED");
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, 1);
        }
        File f = new File(Environment.getExternalStorageDirectory() + "/sRecords");
        if(!f.isDirectory()) {
            File wallpaperDirectory = new File(Environment.getExternalStorageDirectory()+"/sRecords/");
            wallpaperDirectory.mkdirs();
        }
        sq = new DBHelper(this);
        updateMenu();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyAll();
    }
    private void destroyAll(){
        if(!destroyed){
            isRecording = false;
            recorder.stop();
            recorder.release();
            recorder = null;
            recordingThread = null;
            destroyed=true;
        }
    }
    private void updateMenu(){
        String[][] list = sq.getListRecords();
        LinearLayout layout = (LinearLayout)findViewById(R.id.menuBlock);
        layout.removeAllViewsInLayout();
        for(int i = 0;i<sq.massiveCountElements;i++){
            LayoutInflater inflater = LayoutInflater.from(this);
            LinearLayout custom_layout = (LinearLayout) inflater.inflate(R.layout.menublock, null, false);
            custom_layout.setId(Integer.valueOf(list[i][0]));
            ((TextView)custom_layout.findViewById(R.id.recordName)).setText(list[i][1]);
            ((TextView)custom_layout.findViewById(R.id.recordTime)).setText(list[i][2]);
            layout.addView(custom_layout);
        }
    }
    public void recordButtonListener(View view){
        Button recordingButton = (Button)findViewById(R.id.recordingStartButton);
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, 44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
        Data = new byte[bufferSize];
        if (recorder.getState() == AudioRecord.STATE_INITIALIZED) {

        } else {
            showToast("хуйня какаято с микро у тебя");
        }
        if(recordingButton.getText()=="start"){
            recordingButton.setText("stop");
            startRecording();
        }else{
            recordingButton.setText("start");
            stopRecording();
        }
    }
    public void elementButtonListener(View view){
        Intent intObj = new Intent(this, RecordInfoActivity.class);
        intObj.putExtra("id",((LinearLayout)view.getParent()).getId());
        startActivity(intObj);
    }
    private void startRecording() {
        destroyed=false;
        recorder.startRecording();
        isRecording = true;
        recordingThread = new Thread(new Runnable() {
            public void run() {
                String filepath = Environment.getExternalStorageDirectory().getPath()+"/sRecords/";
                Log.d("RECORDING THREAD",filepath);
                FileOutputStream os = null;
                try {
                    Long tsLong = System.currentTimeMillis()/1000;
                    String ts = tsLong.toString();
                    Date currentTime = Calendar.getInstance().getTime();
                    os = new FileOutputStream(filepath+ts+".pcm");
                    sq.appendRecord(ts+".pcm",currentTime.toString(),filepath+ts+".pcm");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                while(isRecording) {
                    recorder.read(Data, 0, Data.length);
                    try {
                        os.write(Data, 0, bufferSize);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        recordingThread.start();
    }
    private void stopRecording() {
        destroyed=true;
        destroyAll();
        updateMenu();
    }
    private void showToast(String text) {
        Toast toast = Toast.makeText(getApplicationContext(),
                text, Toast.LENGTH_SHORT);
        toast.show();
    }

}
