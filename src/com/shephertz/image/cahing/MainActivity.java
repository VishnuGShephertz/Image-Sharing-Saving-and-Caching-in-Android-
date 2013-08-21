package com.shephertz.image.cahing;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
/*
 * @author Vishnu Garg
 */
public class MainActivity extends Activity {
    /** Called when the activity is first created. */
	
	private ImageView imgLogo;
	private ProgressBar progressbar;
	private ImageCacher imageCacher;
	private String appName;
	private String imageUrl="<Your Image Url>";
	private String imageName="myImage";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        imgLogo = (ImageView) findViewById(R.id.my_image);
        Resources res=getResources();
        appName = res.getString(R.string.app_name);
        imageCacher=new ImageCacher(this, -1);
        loadImage();
    } 

   public void loadImage()
   {
	   imageCacher.loadImage(imageUrl, imgLogo);
	   
   }
   
   public void onSaveClicked(View view){
		saveMyImage() ;
		
	}
	
	
	  void saveMyImage() {
		
			Bitmap bmImg =imageCacher.getBitmap(imageUrl);
	        File filename;
	        try {
	            String path1 = android.os.Environment.getExternalStorageDirectory().toString();
	            Log.i("in save()", "after mkdir");
	            File file=new File(path1 + "/"+appName);
	            if (!file.exists())
	            	file.mkdirs();
	            filename = new File(file.getAbsolutePath()+"/"+imageName+".jpg");
	            Log.i("in save()", "after file");
	            FileOutputStream out = new FileOutputStream(filename);
	            Log.i("in save()", "after outputstream");
	            bmImg.compress(Bitmap.CompressFormat.JPEG, 90, out);
	            out.flush();
	            out.close();
	            Log.i("in save()", "after outputstream closed");
	            ContentValues image = new ContentValues();
	            image.put(Images.Media.TITLE, appName);
	            image.put(Images.Media.DISPLAY_NAME, imageName);
	            image.put(Images.Media.DESCRIPTION, "App Image");
	            image.put(Images.Media.DATE_ADDED, System.currentTimeMillis());
	            image.put(Images.Media.MIME_TYPE, "image/jpg");
	            image.put(Images.Media.ORIENTATION, 0);
	             File parent = filename.getParentFile();
	             image.put(Images.ImageColumns.BUCKET_ID, parent.toString().toLowerCase().hashCode());
	             image.put(Images.ImageColumns.BUCKET_DISPLAY_NAME, parent.getName().toLowerCase());
	             image.put(Images.Media.SIZE, filename.length());
	             image.put(Images.Media.DATA, filename.getAbsolutePath());
	             Uri result = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, image);
	            Toast.makeText(getApplicationContext(),
	                    "File is Saved in  " + filename, Toast.LENGTH_SHORT).show();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }

	    }
	
	public void onShareClicked(View view){
	
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("text/plain");
			intent.putExtra(Intent.EXTRA_TEXT, "Hey friends checkout this image"+"\n\n"+imageUrl);
			intent.putExtra(android.content.Intent.EXTRA_SUBJECT,
					"Check My Image ");
			startActivity(Intent.createChooser(intent, "Share"));
			
	
	}
	
}