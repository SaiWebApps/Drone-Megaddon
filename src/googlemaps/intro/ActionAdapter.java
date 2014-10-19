package googlemaps.intro;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ActionAdapter extends BaseAdapter 
{
	private Context context;
	private List<Action> actionList;
	
	public ActionAdapter(Context context, List<Action> actionList)
	{
		this.context = context;
		this.actionList = actionList;
	}

	@Override
	public int getCount() {
		if (actionList == null) {
			return 0;
		}
		return actionList.size();
	}

	@Override
	public Object getItem(int position) {
		if (position < 0 || position >= actionList.size()) {
			return null;
		}
		return actionList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * For each item in the list of posts, display the post details.
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		convertView = inflater.inflate(R.layout.action_item, parent, false);
		
		Action action = actionList.get(position);
		ImageView commandImage = (ImageView) convertView.findViewById(R.id.command_image);
		commandImage.setImageResource(action.getImageId());
		return convertView;
	}
}