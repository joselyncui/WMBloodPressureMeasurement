package com.lichkin.fragments;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ValueFormatter;
import com.lichkin.activity.R;
import com.lichkin.customview.CustomToast;
import com.lichkin.customview.LineMarkerView;
import com.lichkin.db.HistoryDBManager;
import com.lichkin.entity.FHResult;
import com.lichkin.utils.DateUtil;
import com.lichkin.utils.PropertiesSharePrefs;
import com.lichkin.utils.UUIDS;

public class FHHistoryFragment extends BaseHistoryFragment {// implements
															// OnChartValueSelectedListener

	@InjectView(R.id.fh_history_chart)
	LineChart mChart;
	@InjectView(R.id.text_date)
	TextView mTxtDate;
	private CustomToast mToast;

	private HistoryDBManager mDbManager;
	private List<FHResult> mFHResults;
	private int mIndex = 0;
	LineMarkerView mv;
	String idCard = "";
	private Handler mHandler;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_fh_history, container,
				false);
		ButterKnife.inject(this, view);

		mDbManager = HistoryDBManager.getInstance(mContext);
		getFhResults();
		initLineChart();
		addEmptyData(-1);
		mChart.invalidate();

		mHandler = new Handler();
		// initData();
		return view;
	}

	@Override
	public void onResume() {
		mIndex = 0;
		getFhResults();

		if (!mFHResults.isEmpty()) {
			mChart.getData().removeDataSet(0);
			addDataSet(mIndex);
		}
		super.onResume();
	}

	private void getFhResults() {
		idCard = PropertiesSharePrefs.getInstance(mContext).getProperty(
				PropertiesSharePrefs.TYPE_CARD, "");
		mFHResults = mDbManager.getFhResultsByUser(idCard);
	}

	private void initLineChart() {
		// chart
		mChart.setDrawGridBackground(false);
		mChart.setDoubleTapToZoomEnabled(false);
		mChart.setDescription("");
		mChart.setDrawBorders(false);

		mChart.setScaleMinima(2, 1);
		XAxis xAxis = mChart.getXAxis();
		xAxis.setPosition(XAxisPosition.BOTTOM);// x轴位置
		xAxis.setDrawAxisLine(false);
		xAxis.setDrawGridLines(false);
		xAxis.setSpaceBetweenLabels(1);

		YAxis leftAxis = mChart.getAxisLeft();
		leftAxis.setDrawAxisLine(false);
		leftAxis.setDrawGridLines(false);
		leftAxis.setLabelCount(5);

		leftAxis.setValueFormatter(new ValueFormatter() {

			@Override
			public String getFormattedValue(float value) {
				return String.valueOf((int) value);
			}
		});
		;

		YAxis rightAxis = mChart.getAxisRight();
		rightAxis.setDrawLabels(false);
		rightAxis.setDrawGridLines(false);
		rightAxis.setDrawAxisLine(false);
		mChart.getLegend().setEnabled(false);
		mv = new LineMarkerView(getActivity(), mChart,
				R.layout.custom_marker_view);// 自定义标签
		mChart.setMarkerView(mv);// 设置标签
		mChart.setHighlightEnabled(true);
		mChart.setScaleEnabled(false);
		mChart.setNoDataText("");

	}

	private void addEmptyData(int position) {

		// create 30 x-vals
		ArrayList<String> xVals = new ArrayList<>();
		int size = position == -1 ? 60 : mFHResults.get(position).fhValues
				.size();

		for (int i = 1; i <= size; i++)
			xVals.add(i + "");

		LineData data = new LineData(xVals);
		mChart.setData(data);

		// 用于提高Y轴坐标的值
		ArrayList<Entry> yValsMax = new ArrayList<>();
		yValsMax.add(new Entry(300, 0));
		LineDataSet setMax = new LineDataSet(yValsMax, "");
		setMax.setLineWidth(2f);
		setMax.setCircleSize(2);
		int maxColor = getResources().getColor(R.color.fragment_bg);
		setMax.setCircleColor(maxColor);
		setMax.setHighLightColor(maxColor);
		setMax.setColor(maxColor);
		data.addDataSet(setMax);
		data.setDrawValues(false);

		mChart.invalidate();
	}

	private void addDataSet(int position) {
		addEmptyData(position);
		long date = mFHResults.get(position).date;
		mTxtDate.setText(DateUtil.getFormatDate(DateUtil.DATA_FORMAT, date));

		LineData data = mChart.getData();
		System.out.println("data " + data);
		if (data != null) {
			// create 10 y-vals
			ArrayList<Entry> yValsFh = new ArrayList<Entry>();
			List<Integer> fhValues = mFHResults.get(position).fhValues;

			for (int i = 0; i < fhValues.size(); i++) {
				yValsFh.add(new Entry(fhValues.get(i), i));
			}

			LineDataSet set = new LineDataSet(yValsFh,
					getString(R.string.fh_value));
			set.setLineWidth(2f);
			set.setCircleSize(2.5f);
			int color = getResources().getColor(R.color.colorPrimary);
			set.setColor(color);
			set.setCircleColor(color);
			set.setHighLightColor(color);
			data.addDataSet(set);
			data.setDrawValues(false);

			mChart.notifyDataSetChanged();
			mChart.animateY(600);
		}
	}

	// private void initData() {
	// mFHResults = new ArrayList<>();
	// for (int i = 0; i < 3; i++) {
	// ArrayList<Float> fhValues = new ArrayList<>();
	// for (int j = 0; j < 110; j++) {
	// float fh = (float) (Math.random() * 50f + 50f * 2);
	//
	// fhValues.add(fh);
	// }
	// FHResult fhResult = new FHResult(fhValues, new Date().getTime());
	//
	// mFHResults.add(fhResult);
	// }
	// }

	// @Override
	// public void onValueSelected(Entry e, int dataSetmIndex) {
	// float fh = mFHResults.get(0).fhValues.get(e.getXIndex());
	// Toast.makeText(mContext, "胎心值： " + (int) fh, Toast.LENGTH_SHORT).show();
	// }
	//
	// @Override
	// public void onNothingSelected() {
	//
	// }

	@OnClick(R.id.btn_previous)
	public void previousClick() {
		if (mFHResults.isEmpty()) {
			showToast("暂无数据");
			return;
		}
		if (mIndex >= (mFHResults.size() - 1)) {
			showToast(getString(R.string.msg_fist_data));
			return;
		}
		mChart.getData().removeDataSet(0);
		addDataSet(++mIndex);
	}

	@OnClick(R.id.btn_next)
	public void nextClick() {
		if (mFHResults.isEmpty()) {
			showToast("暂无数据");
			return;
		}
		if (mIndex <= 0) {
			showToast(getString(R.string.msg_last_data));
			return;
		}
		mChart.getData().removeDataSet(0);
		addDataSet(--mIndex);
	}

	private void showToast(String msg) {
		if (mToast != null) {
			mToast.cancel();
			mToast = null;
		}
		mToast = CustomToast.makeText(mHandler, mContext, msg, 1.5);
		mToast.show();
	}

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
		return false;
	}

	@Override
	public boolean handleServiceDiscover() {
		mBluetoothLeService.setCharacteristicNotification(
				getInfoCharacteristic(UUIDS.FH_RESULT_SERVICE,
						UUIDS.FH_RESULT_CHARAC), true);
		return false;
	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
		if (mToast != null) {
			mToast.cancel();
			mToast = null;
		}
	}
}
