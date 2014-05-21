package com.shephertz.image.cahing;

import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.shephertz.app42.paas.sdk.android.App42API;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.upload.Upload;
import com.shephertz.app42.paas.sdk.android.upload.UploadFileType;

/*
 * @author Vishnu Garg
 */
public class MainActivity extends Activity {
	/** Called when the activity is first created. */

	private ImageView imgLogo;
	private ProgressDialog progressDialog;
	private ImageCacher imageCacher;
	private String appName;
	private String imageUrl = "<Your image Url>";
	private String imageName = "myImage";
	private final int RESULT_LOAD_IMAGE = 1;
	private final String Description="upload image for testing image operations";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		imgLogo = (ImageView) findViewById(R.id.my_image);
		Resources res = getResources();
		appName = res.getString(R.string.app_name);
		imageCacher = new ImageCacher(this, -1);
		progressDialog=new ProgressDialog(this);
		App42API.initialize(this, "<Your App42 Api Key>",
				"Your App42 secret Key");
	}

	public void loadImage(String imageUrl) {
		imageCacher.loadImage(imageUrl, imgLogo);

	}

	public void onSaveClicked(View view) {
		saveMyImage();
	}

	public void onUploadClicked(View view) {
		browsePhotoDialog();
	}

	/*
	 * used to create alert dialog when logout option is selected
	 * 
	 * @param name of friend whom you want to sahre image
	 */
	public void browsePhotoDialog() {
		AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
		alertbox.setTitle("Upload image");
		final EditText input = new EditText(this);
		input.setHint("Image name");
		alertbox.setView(input);
		alertbox.setPositiveButton("Browse Pic",
				new DialogInterface.OnClickListener() {
					// do something when the button is clicked
					public void onClick(DialogInterface arg0, int arg1) {
						browsePhoto(input.getText().toString());
					}
				});
		alertbox.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {

					}
				});
		alertbox.show();
	}

	/*
	 * Call when user clicks on browse photo
	 */
	private void browsePhoto(String imageName) {
		if (this.imageName != null && !this.imageName.equals(""))
			this.imageName = imageName;
		Intent i = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(i, RESULT_LOAD_IMAGE);
	}

	/*
	 * Callback when user select image from gallery for upload and call and he
	 * has to send autherize callback
	 * 
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK
				&& null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };
			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
			progressDialog.setMessage("uploading image");
			progressDialog.show();
			uploadImage(picturePath);
			
		}
	}

	void saveMyImage() {
		Bitmap bmImg = imageCacher.getBitmap(imageUrl);
		File filename;
		try {
			String path1 = android.os.Environment.getExternalStorageDirectory()
					.toString();
			Log.i("in save()", "after mkdir");
			File file = new File(path1 + "/" + appName);
			if (!file.exists())
				file.mkdirs();
			filename = new File(file.getAbsolutePath() + "/" + imageName
					+ ".jpg");
			Log.i("in save()", "after file");
			FileOutputStream out = new FileOutputStream(filename);
			Log.i("in save()", "after outputstream");
			bmImg.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.flush();
			out.close();
			Log.i("in save()", "after outputstream closed");
			//File parent = filename.getParentFile();
			ContentValues image = getImageContent(filename);
			Uri result = getContentResolver().insert(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, image);
			Toast.makeText(getApplicationContext(),
					"File is Saved in  " + filename, Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public ContentValues getImageContent(File parent) {
		ContentValues image = new ContentValues();
		image.put(Images.Media.TITLE, appName);
		image.put(Images.Media.DISPLAY_NAME, imageName);
		image.put(Images.Media.DESCRIPTION, "App Image");
		image.put(Images.Media.DATE_ADDED, System.currentTimeMillis());
		image.put(Images.Media.MIME_TYPE, "image/jpg");
		image.put(Images.Media.ORIENTATION, 0);
		image.put(Images.ImageColumns.BUCKET_ID, parent.toString()
				.toLowerCase().hashCode());
		image.put(Images.ImageColumns.BUCKET_DISPLAY_NAME, parent.getName()
				.toLowerCase());
		image.put(Images.Media.SIZE, parent.length());
		image.put(Images.Media.DATA, parent.getAbsolutePath());
		return image;
	}

	public void onShareClicked(View view) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, "Hey friends checkout this image"
				+ "\n\n" + imageUrl);
		intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Check My Image ");
		startActivity(Intent.createChooser(intent, "Share"));
	}

	void onUploadResult(final Object uploadObj){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				progressDialog.dismiss();
				Upload upload=(Upload) uploadObj;
				imageUrl=upload.getFileList().get(0).getUrl();
				Toast.makeText(MainActivity.this, "Image is successfully uploaded now you can try out other operations", Toast.LENGTH_SHORT).show();
				loadImage(imageUrl);
			}
		});
		
		
	}
	void onUploadError(final Exception e){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				progressDialog.dismiss();
				System.out.println(e.toString());
				Toast.makeText(MainActivity.this, "Exception is : "+e.toString(), Toast.LENGTH_SHORT).show();

			}
		});
	}
	private void uploadImage(String imagePath) {
		App42API.buildUploadService().uploadFile(imageName, imagePath,
				UploadFileType.IMAGE, Description, new App42CallBack() {

					public void onSuccess(Object uploadObj) {
						// TODO Auto-generated method stub
						onUploadResult(uploadObj);

					}
					public void onException(Exception ex) {
						// TODO Auto-generated method stub
						System.out.println(ex);
						onUploadError(ex);
					}
				});
	}
}