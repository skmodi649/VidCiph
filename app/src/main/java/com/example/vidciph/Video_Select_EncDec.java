package com.example.vidciph;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.security.NoSuchAlgorithmException;

public class Video_Select_EncDec extends AppCompatActivity {
    AESEncDec encrypter;
    TextView path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_select_encdec);
        path = findViewById(R.id.pathv);
        path.setText("");
    }
    ActivityResultLauncher<Intent> sActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        Uri uri = data.getData();

                        String selectedVideoPath = String.valueOf(uri);
                        if (selectedVideoPath != null) {
                            path.setText(selectedVideoPath);
                        }
                    }
                }
            });

    public void openFileDialog(View view){
        Intent data = new Intent(Intent.ACTION_GET_CONTENT);
        data.setType("video/*");
        data = Intent.createChooser(data, "choose a video");
        sActivityResultLauncher.launch(data);

    }

    public void AESEncDec(View view){
        if(path.getText().length() == 0){
            Toast.makeText(Video_Select_EncDec.this,"Please select a video!", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(Video_Select_EncDec.this, "Video Encrypted! Check Movies Folder!", Toast.LENGTH_LONG).show();
        }
    }

    // performing the AES Encryption Decryption operation

    private void init() throws NoSuchAlgorithmException {
        encrypter =new AESEncDec();
    }









    //this function returns null when using IO file manager
   /* public static String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    } */
}