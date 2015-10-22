package com.lichkin.customview;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.Utils;
import com.lichkin.activity.R;

/**
 * 自定义MarkerView
 * 
 */
public class BarMarkerView extends MarkerView {

	private TextView tvContent;

	public BarMarkerView(Context context, int layoutResource) {
		super(context, layoutResource);

		tvContent = (TextView) findViewById(R.id.tvContent);

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

		if (e instanceof CandleEntry) {
			CandleEntry ce = (CandleEntry) e;
			tvContent.setText("" + Utils.formatNumber(ce.getHigh(), 1, true));
		} else {
			tvContent.setText("" + Utils.formatNumber(e.getVal(), 1, true));
		}
	}

	@Override
	public int getXOffset() {
		return -(getWidth() / 2);// 水平居中
	}

	@Override
	public int getYOffset() {
		return -getHeight()+10; // 让markerView居于被选择的数据之上
	}
}
