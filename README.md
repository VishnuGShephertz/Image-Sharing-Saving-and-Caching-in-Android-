Image-Sharing-Saving-and-Caching-in-Android
===========================================

Its an Android world and everythis is coming on Android mobile in form of some applications. As we know Images
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






