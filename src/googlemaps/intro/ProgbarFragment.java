package googlemaps.intro;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

public class ProgbarFragment extends Fragment {

	private ProgressBar mProgressRight, mProgressLeft;

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

	public static void setColorOfProgressBar(ProgressBar mProgressBar, int mColor){
		mProgressBar.getIndeterminateDrawable().setColorFilter(mColor, Mode.MULTIPLY);
	}
}
