package com.lichkin.customview;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.Utils;
import com.lichkin.activity.R;
import com.lichkin.utils.SystemUtils;

/**
 * 自定义MarkerView
 * 
 */
public class LineMarkerView extends MarkerView {

	private TextView tvContent;
	private LineChart mChart;
	private Context mContext;

	public LineMarkerView(Context context, LineChart chart, int layoutResource) {
		super(context, layoutResource);
		tvContent = (TextView) findViewById(R.id.tvContent);
		this.mContext = context;
		this.mChart = chart;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mikephil.charting.utils.MarkerView#refreshContent(com.github
	 * .mikephil.charting.data.Entry, int)
	 * 
	 * 每次重绘 markerview的时候会调用， 可以用户更新内容
	 */
	@Override
	public void refreshContent(Entry e, int dataSetIndex) {

		String value = Utils.formatNumber(e.getVal(), 0, true);

		tvContent.setText(value);

		int color = mChart.getData().getDataSetByIndex(dataSetIndex).getColor();
		if (color == getResources().getColor(R.color.fragment_bg)) {
			tvContent.setText("");
		}
	}

	@Override
	public int getXOffset() {
		return -(getWidth() / 2);// 水平居中
	}

	@Override
	public int getYOffset() {
		return (int) (-this.getMeasuredHeight() + 10
				* SystemUtils.getDensity(mContext)); // 让markerView居于被选择的数据之上
	}
}
