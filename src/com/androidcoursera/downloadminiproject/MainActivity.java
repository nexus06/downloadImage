package com.androidcoursera.downloadminiproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

/**
 * A main Activity that prompts the user for a URL to an image and then uses
 * Intents and other Activities to download the image and view it.
 */
public class MainActivity extends LifecycleLoggingActivity implements
		OnClickListener {
	/**
	 * Debugging tag used by the Android logger.
	 */
	private final String TAG = getClass().getSimpleName();

	/**
	 * A value that uniquely identifies the request to download an image.
	 */
	final private static int DOWNLOAD_IMAGE_REQUEST = 1;
	final private static int FILTER_IMAGE_REQUEST = 2;

	private static final String DOWNLOAD_STATE_STRING = "DOWNLOAD_STATE_TRING";

	/**
	 * EditText field for entering the desired URL to an image.
	 */
	private EditText mUrlEditText;
	private Button downLoadButton;

	/**
	 * URL for the image that's downloaded by default if the user doesn't
	 * specify otherwise.
	 */
	private Uri mDefaultUrl = Uri
			.parse("http://www.dre.vanderbilt.edu/~schmidt/robot.png");

	interface ResultCommand {
		void execute(Intent data);

		void printError(Intent data);
	}

	/**optimized structure where command pipeline will be stored**/
	private SparseArray<ResultCommand> mResultArray = new SparseArray<>();

	/**flag for control user interaction**/
	private boolean mProcessButtonCLick = true;

	/**
	 * Hook method called when a new instance of Activity is created. One time
	 * initialization code goes here, e.g., UI layout and some class scope
	 * variable initialization.
	 * 
	 * @param savedInstanceState
	 *            object that contains saved state information.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "*** onCreate() ***");
		// Always call super class for necessary
		// initialization/implementation.
		// @@ TODO -- you fill in here.
		super.onCreate(savedInstanceState);
		// Set the default layout.
		// @@ TODO -- you fill in here.
		setContentView(R.layout.main_activity);

		// Cache the EditText that holds the urls entered by the user
		// (if any).
		// @@ TODO -- you fill in here.
		mUrlEditText = (EditText) findViewById(R.id.url);
		downLoadButton = (Button) findViewById(R.id.button1);

		downLoadButton.setOnClickListener(this);

		mResultArray.put(DOWNLOAD_IMAGE_REQUEST, new ResultCommand() {

			@Override
			public void printError(Intent data) {

				Utils.showToast(MainActivity.this,
						"Download operation canceled by user");

			}

			@Override
			public void execute(Intent data) {
				// call makeImageFilter factory
				// to create a intent that will launch the filterImageActivity
				// by passing the path of the downloaded image

				final Intent intent = makeFilterImageIntent(data.getData());
				Log.d(TAG, "FILTER IMAGE REQUEST startActivityForResult() "
						+ data.getData());
				startActivityForResult(intent, FILTER_IMAGE_REQUEST);

			}
		});

		mResultArray.put(FILTER_IMAGE_REQUEST, new ResultCommand() {

			@Override
			public void printError(Intent data) {

				Utils.showToast(MainActivity.this, "Filter canceled by user");

			}

			@Override
			public void execute(Intent data) {
				// call makeImageFilter factory
				// to create a intent that will launch the filterImageActivity
				// by passing the path of the downloaded image

				final Intent intent = makeGalleryIntent(data.getDataString());
				Log.d(TAG, "Gallery StartActivity() " + data.getDataString());
				startActivity(intent);
				mProcessButtonCLick = true;

			}
		});
	}

	/**
	 * Called by the Android Activity framework when the user clicks the
	 * "Find Address" button.
	 */
	public void downloadImage() {
		Log.d(TAG, "*** downloadImage() ***");
		try {
			// Hide the keyboard.
			hideKeyboard(this, mUrlEditText.getWindowToken());

			// Call the makeDownloadImageIntent() factory method to
			// create a new Intent to an Activity that can download an
			// image from the URL given by the user. In this case
			// it's an Intent that's implemented by the
			// DownloadImageActivity.
			// @@ TODO - you fill in here.
			Intent download = makeDownloadImageIntent(getUrl());
			// Start the Activity associated with the Intent, which
			// will download the image and then return the Uri for the
			// downloaded image file via the onActivityResult() hook
			// method.
			
			//if download intent has been filled correctly
			if(download != null){
				mProcessButtonCLick = false;
				startActivityForResult(download, DOWNLOAD_IMAGE_REQUEST);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Hook method called back by the Android Activity framework when an
	 * Activity that's been launched exits, giving the requestCode it was
	 * started with, the resultCode it returned, and any additional data from
	 * it.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "*** onActivityResult ***");
		// Check if the started Activity completed successfully.
		// @@ TODO -- you fill in here, replacing true with the right
		// code.
		if (resultCode == Activity.RESULT_OK) {
			mResultArray.get(requestCode).execute(data);
		}
		// Check if the started Activity did not complete successfully
		// and inform the user a problem occurred when trying to
		// download contents at the given URL.
		// @@ TODO -- you fill in here, replacing true with the right
		// code.
		else if (resultCode == Activity.RESULT_CANCELED) {
			mResultArray.get(requestCode).printError(data);
			// Allow user download image again
			mProcessButtonCLick = true;
		}
	}

	/**
	 * Factory method that returns an Intent for viewing the downloaded image in
	 * the Gallery app.
	 */
	private Intent makeGalleryIntent(String pathToImageFile) {
		Log.d(TAG, "*** makeGalleryIntent() ***" + pathToImageFile);
		// Create an intent that will start the Gallery app to view
		// the image.
		// TODO -- you fill in here, replacing "null" with the proper
		// code.
		Intent gallery = new Intent(Intent.ACTION_VIEW);

		Uri imageData = Uri.fromFile(new File(pathToImageFile));
		gallery.setDataAndType(imageData, "image/*");

		return gallery;
	}

	/**
	 * Factory method that returns an Intent for viewing the filtering image.
	 */
	private Intent makeFilterImageIntent(Uri uri) {
		Log.d(TAG, "*** makeFilterImageIntent() ***" + uri);

		Intent filter = new Intent(FilterImageActivity.FILTER_IMAGE_ACTION);

		Uri imageData = uri;
		filter.setDataAndType(imageData, "image/*");

		return filter;
	}

	/**
	 * Factory method that returns an Intent for downloading an image.
	 */
	private Intent makeDownloadImageIntent(Uri uri) {
		// Create an intent that will download the image from the web.
		// TODO -- you fill in here, replacing "null" with the proper
		// code.
		Intent downloadImage = null;

		if (uri != null) {
			if (mProcessButtonCLick) {
				Log.d(TAG, "*** makeDownloadImageIntent() ***" + uri.toString());
				downloadImage = new Intent(
						DownloadImageActivity.DOWNLOAD_IMAGE_ACTION);
				downloadImage.setData(uri);
			} else {
				Utils.showToast(this, "downloading in grogress");
			}

		}

		return downloadImage;
	}

	/**
	 * Get the URL to download based on user input.
	 */
	protected Uri getUrl() {

		// Get the text the user typed in the edit text (if anything).
		Uri url = Uri.parse(mUrlEditText.getText().toString());

		// If the user didn't provide a URL then use the default.
		String uri = url.toString();
		if (uri == null || uri.equals(""))
			url = mDefaultUrl;

		// Do a sanity check to ensure the URL is valid, popping up a
		// toast if the URL is invalid.
		// @@ TODO -- you fill in here, replacing "true" with the
		// proper code.
		if (URLUtil.isValidUrl(url.toString()))
			return url;
		else {
			Toast.makeText(this, "Invalid URL", Toast.LENGTH_SHORT).show();
			return null;
		}
	}

	/**
	 * This method is used to hide a keyboard after a user has finished typing
	 * the url.
	 */
	public void hideKeyboard(Activity activity, IBinder windowToken) {
		InputMethodManager mgr = (InputMethodManager) activity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		mgr.hideSoftInputFromWindow(windowToken, 0);
	}

	@Override
	public void onClick(View v) {
		// downLoadButton.setOnClickListener(null);
		if (mProcessButtonCLick) {
			downloadImage();
		}

	}
}
