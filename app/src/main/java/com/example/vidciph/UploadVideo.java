package com.example.vidciph;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;

public class UploadVideo extends AppCompatActivity {
    private static final String FILE_NAME_ENC = "EncVid";
    // Initialize variables
    VideoView videoView;
    Button encryptv;
    ProgressDialog progressDialog;
    Uri videouri;
    private long fileSize = 0;
    ProgressDialog progressBar;
    private int progressBarStatus = 0;
    private Handler progressBarHandler = new Handler();

    File myDir;

    String my_key = "nbmumP8z5NfBiKmW"; // 16 char = 128 bit
    String my_spec_key = "q1nZ4VjoxIWVgXRQ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_video);

        // Assign variables
        videoView=findViewById(R.id.videoView);
        // initialise layout
        encryptv = findViewById(R.id.encryptv);
        // Get data from main activity
        Bundle bundle=getIntent().getExtras();
        // Check condition
        if(bundle!=null)
        {
            // When bundle not equal to null
            // Initialize uri
            videouri=Uri.parse(bundle.getString("uri"));
            // Set video uri
            videoView.setVideoURI(videouri);
            // Start video
            videoView.start();
        }
        //Init path
        myDir = new File(Environment.getExternalStorageDirectory().toString()+"/Movies");

        encryptv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog progressBar = new ProgressDialog(UploadVideo.this);
                progressBar.setCancelable(true);//you can cancel it by pressing back button
                progressBar.setMessage("File Encrypting and Uploading ...");
                progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressBar.setProgress(0);//initially progress is 0
                progressBar.setMax(100);//sets the maximum value 100
                progressBar.show();//displays the progress bar
                //reset progress bar and filesize status
                progressBarStatus = 0;
                fileSize = 0;

                new Thread(new Runnable() {
                    public void run() {
                        while (progressBarStatus < 100) {
                            // performing operation
                            progressBarStatus = doOperation();
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            // Updating the progress bar
                            progressBarHandler.post(new Runnable() {
                                public void run() {
                                    progressBar.setProgress(progressBarStatus);
                                }
                            });
                        }
                        // performing operation if file is downloaded,
                        if (progressBarStatus >= 100) {
                            // sleeping for 1 second after operation completed
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            // close the progress bar dialog
                            progressBar.dismiss();
                        }
                    }
                }).start();
                byte[] b = convertVideoToBytes(videouri);

                Toast.makeText(UploadVideo.this, "Video Encrypted and Uploaded!", Toast.LENGTH_SHORT).show();

                // Creating File
                File outputFileEnc = new File(myDir, FILE_NAME_ENC);

                try{
                    InputStream is = new ByteArrayInputStream(b);
                    AESEncDec.encryptToFile(my_key, my_spec_key, is, new FileOutputStream(outputFileEnc));
                } catch (IOException exception) {
                    exception.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (InvalidAlgorithmParameterException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                } catch (NullPointerException e){
                    e.printStackTrace();
                }

            }

        });
    }

    // checking how much file is downloaded and updating the filesize
    public int doOperation() {
        //The range of ProgressDialog starts from 0 to 10000
        while (fileSize <= 10000) {
            fileSize++;
            if (fileSize == 1000) {
                return 10;
            } else if (fileSize == 2000) {
                return 20;
            } else if (fileSize == 3000) {
                return 30;
            } else if (fileSize == 4000) {
                return 40; // you can add more else if
            }
         /* else {
                return 100;
            }*/
        }//end of while
        return 100;
    }//end of doOperation

    // method for getting the file type

    private String getfiletype(Uri videouri) {
        ContentResolver r = getContentResolver();
        // get the file type ,in this case its mp4
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(r.getType(videouri));
    }


  /*  public void uploadvideo() {
        if (videouri != null) {
            // save the selected video in Firebase storage
            final StorageReference reference = FirebaseStorage.getInstance().getReference("Files/" + System.currentTimeMillis() + "." + getfiletype(videouri));
            reference.putFile(videouri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful()) ;
                    // get the link of video
                    String downloadUri = uriTask.getResult().toString();
                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Video");
                    HashMap<String, String> map = new HashMap<>();
                    map.put("videolink", downloadUri);
                    reference1.child("" + System.currentTimeMillis()).setValue(map);
                    // Video uploaded successfully
                    // Dismiss dialog
                    progressDialog.dismiss();
                    Toast.makeText(UploadVideo.this, "Video Uploaded!!", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Error, Image not uploaded
                    progressDialog.dismiss();
                    Toast.makeText(UploadVideo.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                // Progress Listener for loading
                // percentage on the dialog box
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    // show the progress bar
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploaded " + (int) progress + "%");
                }
            });
        }
    } */

    public static byte[] convertVideoToBytes(Uri uri) {
        byte[] videoBytes = null;
        try {//  w  w w  . j ava 2s . c  o m
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            File file = new File(uri.toString());
            FileInputStream fis = new FileInputStream(file);
            byte[] buf = new byte[1024];
            int n;
            while (-1 != (n = fis.read(buf)))
                baos.write(buf, 0, n);

            videoBytes = baos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return videoBytes;
    }
}