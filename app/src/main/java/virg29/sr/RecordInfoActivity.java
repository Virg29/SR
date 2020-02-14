package virg29.sr;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

public class RecordInfoActivity extends Activity {
    DBHelper sq;
     private int currentId;
     private String name;
     private String path;
     private String time;
     private String recognition;
     private EditText nameGraph;
     private TextView timeGraph;
     private TextView recognitionGraph;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_info);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        currentId = getIntent().getIntExtra("id", 0);
        sq = new DBHelper(this);
        String [] answer = sq.getDataById(currentId);
        name = answer[0];
        time = answer[1];
        recognition = answer[2];
        path = answer[3];
        nameGraph=findViewById(R.id.recordNameEditeble);
        timeGraph=findViewById(R.id.recordTimestampNotEditable);
        recognitionGraph=findViewById(R.id.recordRecognitionNotEditeble);
        nameGraph.setText(name);
        timeGraph.setText(time);
        recognitionGraph.setText(recognition);
    }
    private void returnToMain(){
        Intent intObj = new Intent(this, MainActivity.class);
        startActivity(intObj);
    }
    public void buttonSaveListener(View view){
        sq.updateRecordName(currentId, String.valueOf(nameGraph.getText()));
        returnToMain();
    }
    public void buttonDeleteListener(View view){
        File file = new File(path);
        file.delete();
        sq.deleteRecord(currentId);
        returnToMain();
    }
    public void buttonRecogniteListener(View view){

    }
}
