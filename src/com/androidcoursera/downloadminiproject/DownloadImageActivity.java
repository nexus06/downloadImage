package com.androidcoursera.downloadminiproject;

import android.os.Handler;
import android.util.Log;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

/**
 * An Activity that downloads an image, stores it in a local file on the local
 * device, and returns a Uri to the image file.
 */
public class DownloadImageActivity extends GenericImageActivity {
	/**
	 * Debugging tag used by the Android logger.
	 */
	private final String TAG = getClass().getSimpleName();
	public final static String DOWNLOAD_IMAGE_ACTION = "action.DOWNLOAD_IMAGE";

	@Override
	protected Uri doInBackgroundHook(Context context, Uri uri) {
		Log.d(TAG, "downloading image---> " + uri.toString());
		// downloading image in background
		return Utils.downloadImage(context, uri);

	}

	@Override
	protected int startPointProgress() {
		
		return 1;
	}

}