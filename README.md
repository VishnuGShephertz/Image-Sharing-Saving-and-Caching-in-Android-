Image-Sharing-Saving-and-Caching-in-Android
===========================================

# About Sample application

1. This sample shows how can we upload image from Android SD Card to AppHq server using Upload Service API of App42.
2. How can we load images from web as well as cache images locally.
3. We can also do Sampling of image accordingly for Memory optimization.
4. We can also share that image to our friends through various android application , installed in our Android device.
5. We also save that image in our device Gallery of SD Card in a application name folder.


# Running Sample
1. [Register] (https://apphq.shephertz.com/register) with App42 platform.
2. Create an app once, you are on Quick start page after registration.
3. If you are already registered, login to [AppHQ] (http://apphq.shephertz.com) console and create an app from App Manager Tab.
1. Download the project from [here] (https://github.com/VishnuGShephertz/Image-Sharing-Saving-and-Caching-in-Android-/archive/master.zip) and import it in the eclipse.<br/>
5. Open MainActivity.java file of sample project and make following changes.

```
A. Replace api-Key and secret-Key that you have received in step 2 or 3 at line number 54 and 55.
```
3. Change the application name with your application name in res/strings.
4. Build and run the application.


# Design Details:

__Get image from Sd card:__ If you want to upload image from android sd card on server using App42 Upload Service API,You have to get it from Android Sd Card first.
You can follow code written in MainActivty.java file.
  
```
        /*
  	 * This function opens Sd card gallery to select image to upload on Server
	 */
	private void browsePhoto(String imageName) {
		if (this.imageName != null && !this.imageName.equals(""))
			this.imageName = imageName;
		Intent i = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(i, RESULT_LOAD_IMAGE);
	}
	 /*
  	 * Get callback in your application when you select paricular image to upload on server
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


```
__Upload image using App42 Upload Service:__ After you get image from Android Sd card as explain abobe. You have to upload this image on App42 Server using App42 upload Service API.
```
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

```


__Share Image to friends:__ If you want to share image to your friend using various application installed in your device.
You can follow code written in MainActivty.java file.
  
```
      Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("text/plain");
			intent.putExtra(Intent.EXTRA_TEXT, "Hey friends checkout this image"+"\n\n"+imageUrl);
			intent.putExtra(android.content.Intent.EXTRA_SUBJECT,
					"Check My Image ");
			startActivity(Intent.createChooser(intent, "Share"));

```

__Loading and Caching of Image:__ ImageCacher.java file contains all type of code required to share image from web or cache accordingly.

If we want to load image without sampling that we should use in our Android Activity class file:  
```
        ImageView imgLogo = (ImageView) findViewById(R.id.my_image);
        ImageCacher imageCacher=new ImageCacher(this, -1);
        imageCacher.loadImage(imageUrl, imgLogo);

```
If we want to load image with sampling that we should use in our Android Activity class file:  
```
        ImageView imgLogo = (ImageView) findViewById(R.id.my_image);
        ImageCacher imageCacher=new ImageCacher(this, SamplingImageSize);
        imageCacher.loadImage(imageUrl, imgLogo);

```
If we want to load image from web or cache we should follow this code :

```
      public  Bitmap getBitmap(String url) {
		String filename = String.valueOf(url.hashCode());
		File f = new File(cacheDir, filename);
		// from SD cache
		Bitmap b;
		if(isSamplingReq){
			b = decodeWithoutSampling(f);
		}
		else{
			b = decodewithSampleing(f);
		}
		if (b != null)
			return b;
		// from web
		try {
			Bitmap bitmap = null;
			InputStream is = new URL(url).openStream();
			OutputStream os = new FileOutputStream(f);
			Utility.CopyStream(is, os);
			os.close();
			if(isSamplingReq){
				bitmap = decodeWithoutSampling(f);
			}
			else{
				bitmap = decodewithSampleing(f);
			}
			return bitmap;
		} catch (Throwable ex) {
			 ex.printStackTrace();
	           if(ex instanceof OutOfMemoryError)
	              clearCache();
	           return null;
		}
	}

```

Code for Image Sampling :

```
      private Bitmap decodewithSampleing(File f) {
		try {
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < requiredSize
						|| height_tmp / 2 < requiredSize)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
		}
		return null;
	}

```

Code for without Sampling :

```
       private Bitmap decodeWithoutSampling(File f){
		try {
			return BitmapFactory.decodeStream(new FileInputStream(f));
			
		} catch (Exception e) {
		}
		return null;
	}

```

__Save Image in SD Card Gallery:__ If you want to save Image in Android SD Card Gallery,
You can follow code written in MainActivty.java file.
  
```
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

```
__Permission Required for AndroidManifest.xml file:__ Following permission are required for loading ,caching and saving image.
  
```
       <uses-permission android:name="android.permission.INTERNET" />
	     <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

```
