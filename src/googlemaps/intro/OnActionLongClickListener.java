package googlemaps.intro;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class OnActionLongClickListener implements OnItemLongClickListener {
	public int[] mActions = new int[] {
			R.drawable.sc2land_or,
			R.drawable.sc2xor,
			R.drawable.sc2fire,
			R.drawable.sc2arrowup,
			R.drawable.sc2arrowdown,
			R.drawable.sc2twistleftlit,
			R.drawable.sc2target,
			R.drawable.sc2diag,
	};

	public String[] mActionsDesc = new String[] {
			"Takeoff/Land\nTake off if the drone is grounded, or immediately land the drone.",
			"Emergency Shutdown/Resume",
			"Hover",
			"Move Up",
			"Move Down",
			"Spin Lock\nIf on, rotate screen to spin drone left/right.",
			"Drone Deselect\nDeselect all selected drones.",
			"Transform\nChange icon to StarCraft II drone models."
	};

	@Override
	public boolean onItemLongClick(AdapterView<?> av, View v, int position,
			long id) {
		LayoutInflater inflater = LayoutInflater.from(av.getContext());

		RelativeLayout rl = (RelativeLayout) v;
		View layout = inflater.inflate(R.layout.toast_hint,
				(ViewGroup) rl.findViewById(R.id.toast_layout_root));
		ImageView image = (ImageView) layout.findViewById(R.id.toastimage);
		image.setImageResource(mActions[position]);
		TextView text = (TextView) layout.findViewById(R.id.toasttext);
		text.setText(mActionsDesc[position]);

		final Toast toast = new Toast(av.getContext());
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(layout);
		toast.setGravity(Gravity.BOTTOM, 0, 0);
		toast.show();
		return true;
	}
}
