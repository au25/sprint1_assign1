package com.example.sprint1_assign;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    String mCurrentPhotoPath;
    private ArrayList<String> photos = null;
    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        photos = findPhotos(new Date(Long.MIN_VALUE), new Date(), "");
        if (photos.size() == 0) {
            displayPhoto(null);
        } else {
            displayPhoto(photos.get(index));
        }
    }

    public void takePhoto(View v) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.sprint1_assign", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private ArrayList<String> findPhotos(Date startTimestamp, Date endTimestamp, String keywords) {
        Toast pls5 = Toast.makeText(this, "FIND PHOTOS" + endTimestamp + "--" + keywords, Toast.LENGTH_LONG);
        pls5.show();
        File file = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath(), "/Android/data/com.example.sprint1_assign/files/Pictures");
        ArrayList<String> photos = new ArrayList<String>();
        File[] fList = file.listFiles();
        Toast pls6 = Toast.makeText(this, "" + (fList == null), Toast.LENGTH_LONG);
        pls6.show();
        try {
            if (fList != null) {
                for (File f : fList) {
                    if (((startTimestamp == null && endTimestamp == null) || (f.lastModified() >= startTimestamp.getTime()
                            && f.lastModified() <= endTimestamp.getTime())
                    ) && (keywords == "" || f.getPath().contains(keywords)))
                        photos.add(f.getPath());
                }
            }
        } catch (Exception ex) {
//            Crashes after photo taken
        }
        return photos;
    }

    public void scrollPhotos(View v) {
        if(photos.size() != 0){
            updatePhoto(photos.get(index), ((EditText) findViewById(R.id.etCaption)).getText().toString());
        }
        int i = 0;
        switch (v.getId()) {
            case R.id.btnPrev:
                if (index > 0) {
                    index--;

                }
                break;
            case R.id.btnNext:
                if (index < (photos.size() - 1)) {
                    index++;

                }
                break;
            default:
                break;
        }
        try {
            displayPhoto(photos.get(index));
        }catch(Exception ex){
//            Crashes after nav
        }
    }

    private void displayPhoto(String path) {
        ImageView iv = (ImageView) findViewById(R.id.ivGallery);
        TextView tv = (TextView) findViewById(R.id.tvTimestamp);
        EditText et = (EditText) findViewById(R.id.etCaption);
        if (path == null || path == "") {
            iv.setImageResource(R.mipmap.ic_launcher);
            et.setText("");
            tv.setText("");
        } else {
            iv.setImageBitmap(BitmapFactory.decodeFile(path));
            String[] attr = path.split("_");
            et.setText(attr[2]);
            tv.setText(attr[3] + "-" + attr[4]);
        }
    }

    private void updatePhoto(String path, String caption) {
        String[] attr = path.split("_");
        if (attr.length >= 3) {
            File to = new File(attr[0] + "_" + attr[1] + "_" + caption + "_" + attr[3] + "_" + attr[4] + "_" + attr[5]);
            Toast t1 = Toast.makeText(this, attr[3] + "--" + attr[4] + "--" + attr[2], Toast.LENGTH_LONG);
//            t1.show();
            File from = new File(path);
            if (!caption.equals(attr[2])) {
                from.renameTo(to);
                Toast renamed = Toast.makeText(this, "renamed", Toast.LENGTH_SHORT);
                renamed.show();
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "_caption_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void openSearchActivity(View view) {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivityForResult(intent, 345);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Toast pls = Toast.makeText(this, "not_ok: " + resultCode, Toast.LENGTH_SHORT);
        pls.show();
        if (resultCode == RESULT_OK) {
            Toast pls2 = Toast.makeText(this, "inside result_ok", Toast.LENGTH_SHORT);
            pls2.show();
            DateFormat format = new SimpleDateFormat("yyyy‐MM‐dd HH:mm:ss");
            Date startTimestamp, endTimestamp;
            try {
                String from = (String) data.getStringExtra("STARTTIMESTAMP");
                String to = (String) data.getStringExtra("ENDTIMESTAMP");
                startTimestamp = format.parse(from);
                endTimestamp = format.parse(to);
                Toast pls3 = Toast.makeText(this, "inside TRY: " + startTimestamp + " : " + endTimestamp, Toast.LENGTH_LONG);
                pls3.show();
            } catch (Exception ex) {
                startTimestamp = null;
                endTimestamp = null;
            }
            String keywords = (String) data.getStringExtra("KEYWORDS");
            Toast pls3 = Toast.makeText(this, "KEYWORD: " + keywords, Toast.LENGTH_LONG);
            pls3.show();
            index = 0;
            photos = findPhotos(startTimestamp, endTimestamp, keywords);
//            Toast pls4 = Toast.makeText(this, "PHOTO SIZE: " + photos.size(), Toast.LENGTH_LONG);
//            pls4.show();
            if (photos.size() == 0) {
                displayPhoto(null);
            } else {
                displayPhoto(photos.get(index));
            }
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                ImageView mImageView = (ImageView) findViewById(R.id.ivGallery);
                mImageView.setImageBitmap(BitmapFactory.decodeFile(mCurrentPhotoPath));
            }
        }
    }
}