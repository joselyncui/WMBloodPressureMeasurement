package com.lichkin.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.lichkin.activity.R;

public class SpinnerAdapter extends ArrayAdapter<String> {
	private Context mContext;
	private List<String> mDeviceList;
	private Spinner mSpinner;

	public SpinnerAdapter(Spinner spinner, Context context, int resource,
			List<String> objects) {
		super(context, resource, objects);
		this.mContext = context;
		this.mDeviceList = objects;
		this.mSpinner = spinner;
		setDropDownViewResource(R.layout.spinner_item);
	}

	@SuppressLint("NewApi")
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		View view = LayoutInflater.from(getContext()).inflate(
				R.layout.spinner_item, null, true);
		TextView name = (TextView) view.findViewById(R.id.name);
		ImageView icon = (ImageView) view.findViewById(R.id.icon);
		name.setText(mDeviceList.get(position));
		int selectIndex = mSpinner.getSelectedItemPosition();
		switch (position) {
		case 0:
			int topBgId = position == selectIndex ? R.drawable.spinner_top_bg_press
					: R.drawable.spinner_top_bg;
			view.setBackground(mContext.getResources().getDrawable(topBgId));
			break;
		case 1:
			int midBgId = position == selectIndex ? R.drawable.spinner_middle_bg_press
					: R.drawable.spinner_middle_bg;
			view.setBackground(mContext.getResources().getDrawable(midBgId));
			break;
		case 2:
			int botBgId = position == selectIndex ? R.drawable.spinner_bottom_bg_press
					: R.drawable.spinner_bottom_bg;
			view.setBackground(mContext.getResources().getDrawable(botBgId));
			break;
		default:
			break;
		}
		int iconId = position == selectIndex ? R.drawable.icon_arrow_select
				: R.drawable.icon_arrow_nor;
		icon.setImageResource(iconId);
		return view;
	}

}
