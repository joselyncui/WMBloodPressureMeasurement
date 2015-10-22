package com.lichkin.fragments;

import java.util.ArrayList;
import java.util.List;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ValueFormatter;
import com.lichkin.activity.R;
import com.lichkin.customview.BarMarkerView;
import com.lichkin.db.HistoryDBManager;
import com.lichkin.entity.BSResult;
import com.lichkin.utils.DateUtil;
import com.lichkin.utils.PropertiesSharePrefs;
import com.lichkin.utils.UUIDS;

public class BSHistoryFragment extends BaseHistoryFragment implements
		OnChartValueSelectedListener {

	public final static String BS_DATA = "bs_data";

	@InjectView(R.id.bs_barchart)
	BarChart mChart;
	private Handler mHandler;
	private BluetoothGattCharacteristic mCommandCharac;
	private HistoryDBManager historyDBManager;
	private List<BSResult> bsResults;
	private int[] colors;
	private int mBarLastIndex = 0;
	String idCard = "";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_bs_history, container,
				false);
		ButterKnife.inject(this, view);

		mHandler = new Handler();
		historyDBManager = HistoryDBManager.getInstance(getActivity());

		initBarChart();
		// setData(20, 50);

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		getBsResult();
		mChart.setScaleMinima(bsResults.size() / 7f, 1);
		setData();
		mChart.animateY(1000);// Y轴动画
	}

	private void getBsResult() {
		idCard = PropertiesSharePrefs.getInstance(mContext).getProperty(
				PropertiesSharePrefs.TYPE_CARD, "");
		bsResults = historyDBManager.getBsResultsByUser(idCard);
	}

	private void initBarChart() {
		colors = new int[] { getResources().getColor(R.color.yellow_green),
				getResources().getColor(R.color.colorPrimary) };

		// enable the drawing of values
		mChart.setDrawValueAboveBar(true);
		mChart.setDescription("");

		// if more than 400 entries are displayed in the chart, no values will
		// be  drawn
		mChart.setMaxVisibleValueCount(400);
		mChart.setDoubleTapToZoomEnabled(false);
		mChart.setPinchZoom(false);// x y 轴单独缩放
		mChart.setScaleEnabled(false);// 设置不可缩放
		mChart.setDrawBarShadow(false);// 柱状图阴影
		mChart.setDrawGridBackground(false);
		mChart.setOnChartValueSelectedListener(this);

		XAxis xAxis = mChart.getXAxis();
		xAxis.setPosition(XAxisPosition.BOTTOM);// x轴位置
		xAxis.setDrawAxisLine(false);
		xAxis.setDrawGridLines(false);
		xAxis.setSpaceBetweenLabels(1);

		YAxis leftAxis = mChart.getAxisLeft();
		leftAxis.setDrawAxisLine(false);
		leftAxis.setDrawGridLines(false);
		leftAxis.setValueFormatter(new ValueFormatter() {

			@Override
			public String getFormattedValue(float value) {
				return String.valueOf( Utils.formatNumber(value, 1, true));
			}
		});

		YAxis rightAxis = mChart.getAxisRight();
		rightAxis.setDrawLabels(false);
		rightAxis.setDrawGridLines(false);
		rightAxis.setDrawAxisLine(false);
		
		mChart.getLegend().setEnabled(false);
		
		BarMarkerView mv = new BarMarkerView(getActivity(),
				R.layout.custom_marker_view);// 自定义标签
		mChart.setMarkerView(mv);// 设置标签
		mChart.setDescription("");// 设置单位
		mChart.setNoDataText("");

	}

	public void setData() {
		ArrayList<String> xVals = new ArrayList<String>();
		ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

		for (int i = 0; i < bsResults.size(); i++) {
			xVals.add(" ");
			yVals1.add(new BarEntry(Float.parseFloat(bsResults.get(i).bg), i));
		}

		for (int j = xVals.size(); j < 7; j++) {
			xVals.add(" ");
		}
		BarDataSet set1 = new BarDataSet(yVals1, "");
		set1.setBarSpacePercent(35f);
		set1.setColors(colors);

		ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
		dataSets.add(set1);

		BarData data = new BarData(xVals, dataSets);
		data.setDrawValues(false);
		mChart.setData(data);

	}

	// private void setData(int count, float range) {
	//
	// ArrayList<String> xVals = new ArrayList<String>();
	// for (int i = 0; i < count; i++) {
	// xVals.add(i+"");
	// }
	//
	// ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
	//
	// for (int i = 0; i < count; i++) {
	// float mult = (range + 1);
	// float val = (float) (Math.random() * mult);
	// yVals1.add(new BarEntry(val, i));
	// }
	//
	// BarDataSet set1 = new BarDataSet(yVals1, "");
	// set1.setBarSpacePercent(35f);
	// set1.setColors(colors);
	//
	// ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
	// dataSets.add(set1);
	//
	// BarData data = new BarData(xVals, dataSets);
	//
	// mChart.setData(data);
	// }

	@Override
	public boolean handleConnect() {
		return false;
	}

	@Override
	public boolean handleDisconnect() {
		return false;
	}

	@Override
	public boolean handleGetData(String data) {
		System.out.println(data);
		return false;
	}

	@Override
	public boolean handleServiceDiscover() {
		mBluetoothLeService.setCharacteristicNotification(
				getInfoCharacteristic(UUIDS.BS_RESULT_SERVICE,
						UUIDS.BS_RESULT_CHARAC), true);
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				mCommandCharac = getInfoCharacteristic(UUIDS.BS_RESULT_SERVICE,
						UUIDS.BS_CHARAC_COMMAND);
				mCommandCharac.setValue(BSResult.COMMAND_GET_REC_NO);
				mBluetoothLeService.writeCharacteristic(mCommandCharac);
			}
		}, 100);
		return false;
	}

	@Override
	public void onNothingSelected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
		if (null == e || e.getXIndex() > bsResults.size() - 1) {
			mChart.getData().getXVals().set(mBarLastIndex, " ");
			return;
		}
		String date = DateUtil.getFormatDate(DateUtil.DATA_FORMAT,
				bsResults.get(e.getXIndex()).date);
		mChart.getData().getXVals().set(mBarLastIndex, " ");
		mChart.getData().getXVals().set(e.getXIndex(), date);
		mBarLastIndex = e.getXIndex();

	}

}
