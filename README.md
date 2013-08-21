Image-Sharing-Saving-and-Caching-in-Android
===========================================

Its an Android world and every thing is coming on Android mobile in form of some applications. As we know Images
are the most important part of the application that makes enhance the UI of application.

# About Sample application

1. This sample shows how can we load images from web as well as cache images locally.
2. We can also do Sampling of image accordingly for Memory optimization.
2. We can also share that image to our friends through various android application , installed in our Android device.
3. We also save that image in our device Gallery of SD Card in a application name folder.


# Running Sample

1. Download the project from [here] (https://github.com/VishnuGShephertz/Image-Sharing-Saving-and-Caching-in-Android-/archive/master.zip) and import it in the eclipse.<br/>
2. Replace imageUrl from which you want to load image.
3. Change the application name with your application name in res/strings.
4. Build and run the application.


# Design Details:

__Permission Required for AndroidManifest.xml file:__ Following permission are required for loading ,caching and saving image.
  
```
       <uses-permission android:name="android.permission.INTERNET" />
	     <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

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

```
