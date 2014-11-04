package googlemaps.intro;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

public class ProgbarFragment extends Fragment {
	private static final int MAX_ALTITUDE = 10000;
	private static final int PROGRESS = 0x1;

	private ProgressBar mProgressRight, mProgressLeft;
	private int mProgressStatus = 0;

	private Handler mHandler = new Handler();

	@Override
	public void onAttach(Activity a)
	{
		super.onAttach(a);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// Creating view corresponding to the fragment
		View v = inflater.inflate(R.layout.fragment_progbar, container, false);

		mProgressRight = (ProgressBar) v.findViewById(R.id.progress_bar_left);
		mProgressRight.setProgress(75);

		mProgressLeft = (ProgressBar) v.findViewById(R.id.progress_bar_right);
		mProgressLeft.setProgress(25);
		//        // Start lengthy operation in a background thread
		//        new Thread(new Runnable() {
		//            public void run() {
		//                while (mProgressStatus < 100) {
		//                    mProgressStatus = doWork();
		//
		//                    // Update the progress bar
		//                    mHandler.post(new Runnable() {
		//                        public void run() {
		//                            mProgress.setProgress(mProgressStatus);
		//                        }
		//                    });
		//                }
		//            }
		//        }).start();
		return v;
	}
	
	public void setAltProgress(double altitude) {
		this.mProgressRight.setProgress((int) ((altitude / MAX_ALTITUDE) * 100));
	}

	public static void setColorOfProgressBar(ProgressBar mProgressBar, int mColor){
		mProgressBar.getIndeterminateDrawable().setColorFilter(mColor, Mode.MULTIPLY);
	}
}
