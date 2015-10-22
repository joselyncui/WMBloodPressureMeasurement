package com.lichkin.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lichkin.db.DeviceDataContract.BPDataEntry;
import com.lichkin.db.DeviceDataContract.BSDataEntry;
import com.lichkin.db.DeviceDataContract.FHDataEntry;
import com.lichkin.entity.BPResult;
import com.lichkin.entity.BSResult;
import com.lichkin.entity.FHResult;
import com.lichkin.utils.DateUtil;

public class HistoryDBManager {

	private Context mContext;
	private DBHelper mDBHelper;
	private static HistoryDBManager mHistoryDBManager;
	private int maxCount = 300;

	private HistoryDBManager(Context context) {
		this.mContext = context;
		mDBHelper = new DBHelper(mContext);
	}

	public static HistoryDBManager getInstance(Context context) {
		if (mHistoryDBManager == null) {
			mHistoryDBManager = new HistoryDBManager(context);
		}
		return mHistoryDBManager;
	}

	/**
	 * 获得所有的血压计数据
	 * 
	 * @return
	 */
	public List<BPResult> getBpResultsByUser(String idCard) {
		List<BPResult> bpResults = new ArrayList<>();
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		String[] projection = { BPDataEntry.COLUMN_NAME_ID,
				BPDataEntry.COLUMN_NAME_SZVALUE,
				BPDataEntry.COLUMN_NAME_SSVALUE,
				BPDataEntry.COLUMN_NAME_HEART_RATE,
				BPDataEntry.COLUMN_NAME_DATE, BPDataEntry.COLUMN_NAME_CARD,
				BPDataEntry.COLUMN_NAME_REMARKS };
		String selection = BPDataEntry.COLUMN_NAME_CARD + "=?";
		String selectionArgs[] = new String[] { idCard };
		Cursor c = db.query(BPDataEntry.TABLE_NAME, projection, selection, selectionArgs,
				null, null, null);

		while (c.moveToNext()) {
			int id = c.getInt(c
					.getColumnIndexOrThrow(BPDataEntry.COLUMN_NAME_ID));
			float szValue = c.getFloat(c
					.getColumnIndexOrThrow(BPDataEntry.COLUMN_NAME_SZVALUE));
			float ssValue = c.getFloat(c
					.getColumnIndexOrThrow(BPDataEntry.COLUMN_NAME_SSVALUE));
			float hr = c.getFloat(c
					.getColumnIndexOrThrow(BPDataEntry.COLUMN_NAME_HEART_RATE));
			long date = c.getLong(c
					.getColumnIndexOrThrow(BPDataEntry.COLUMN_NAME_DATE));
			String card = c.getString(c
					.getColumnIndexOrThrow(BPDataEntry.COLUMN_NAME_CARD));
			String remarks = c.getString(c
					.getColumnIndexOrThrow(BPDataEntry.COLUMN_NAME_REMARKS));
			bpResults.add(new BPResult(id, card, szValue, ssValue, hr, date,
					remarks));
		}
		return bpResults;
	}

	public void updateBPResult(BPResult bpResult) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(BPDataEntry.COLUMN_NAME_CARD, bpResult.userCard);
		values.put(BPDataEntry.COLUMN_NAME_SSVALUE, bpResult.sbp);
		values.put(BPDataEntry.COLUMN_NAME_SZVALUE, bpResult.dbp);
		values.put(BPDataEntry.COLUMN_NAME_HEART_RATE, bpResult.pulse);
		values.put(BPDataEntry.COLUMN_NAME_DATE, bpResult.date);
		values.put(BPDataEntry.COLUMN_NAME_REMARKS, bpResult.remarks);
		values.put(BPDataEntry.COLUMN_NAME_STATUS, bpResult.status);
		String where = BPDataEntry.COLUMN_NAME_ID + " = ?";
		String[] whereArgs = new String[] { String.valueOf(bpResult.id) };
		db.update(BPDataEntry.TABLE_NAME, values, where, whereArgs);
	}
	
	public void updateBpUserCard(String userCard){
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(BPDataEntry.COLUMN_NAME_CARD, userCard);
		String where = BPDataEntry.COLUMN_NAME_CARD + " = ?";
		String[] whereArgs = new String[] { "" };
		db.update(BPDataEntry.TABLE_NAME, values, where, whereArgs);
	} 

	public List<BPResult> getBpResultsByStatus(int status) {

		List<BPResult> bpResults = new ArrayList<>();
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		String[] projection = { BPDataEntry.COLUMN_NAME_ID,
				BPDataEntry.COLUMN_NAME_SZVALUE,
				BPDataEntry.COLUMN_NAME_SSVALUE,
				BPDataEntry.COLUMN_NAME_HEART_RATE,
				BPDataEntry.COLUMN_NAME_DATE, BPDataEntry.COLUMN_NAME_CARD,
				BPDataEntry.COLUMN_NAME_REMARKS };
		String selection = BPDataEntry.COLUMN_NAME_STATUS + "=?";
		String selectionArgs[] = new String[] { String.valueOf(status) };
		String orderBy = BPDataEntry.COLUMN_NAME_DATE + " DESC";
		Cursor c = db.query(BPDataEntry.TABLE_NAME, projection, selection,
				selectionArgs, null, null, orderBy);
		while (c.moveToNext()) {
			int id = c.getInt(c
					.getColumnIndexOrThrow(BPDataEntry.COLUMN_NAME_ID));
			float szValue = c.getFloat(c
					.getColumnIndexOrThrow(BPDataEntry.COLUMN_NAME_SZVALUE));
			float ssValue = c.getFloat(c
					.getColumnIndexOrThrow(BPDataEntry.COLUMN_NAME_SSVALUE));
			float hr = c.getFloat(c
					.getColumnIndexOrThrow(BPDataEntry.COLUMN_NAME_HEART_RATE));
			long date = c.getLong(c
					.getColumnIndexOrThrow(BPDataEntry.COLUMN_NAME_DATE));
			String card = c.getString(c
					.getColumnIndexOrThrow(BPDataEntry.COLUMN_NAME_CARD));
			String remarks = c.getString(c
					.getColumnIndexOrThrow(BPDataEntry.COLUMN_NAME_REMARKS));
			bpResults.add(new BPResult(id, card, szValue, ssValue, hr, date,
					remarks));
		}
		return bpResults;
	}

	/**
	 * 删除已上传的数据，只留最近300条
	 * 
	 */
	public void deleteBpDatas() {

		List<BPResult> results = getBpResultsByStatus(1);
		System.out.println("bp results " + results.size());
		if (results.size() <= (maxCount - 1)) {
			return;
		}

		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		StringBuilder selection = new StringBuilder();
		List<String> argList = new ArrayList<>();
		selection.append(BPDataEntry.COLUMN_NAME_ID + " in (");
		System.out.println("sql before " + selection.toString());

		for (int i = results.size() - 1; i > (maxCount - 1); i--) {

			selection.append("?,");
			System.out.println("id " + results.get(i).id);
			argList.add(results.get(i).id + "");
			System.out.println("sql in " + i + "  " + selection.toString());
		}
		System.out.println("sql after " + selection.toString());
		selection.deleteCharAt(selection.length() - 1);// 去掉最后一个逗号
		selection.append(")");
		System.out.println("sql " + selection.toString());

		String[] whereArgs = (String[]) argList.toArray(new String[argList
				.size()]);

		db.delete(BPDataEntry.TABLE_NAME, selection.toString(), whereArgs);
	}

	/**
	 * 标记血压记录已上传
	 * 
	 * @param bpResults
	 * @return
	 */
	public void changeBpStatus(List<BPResult> bpResults) {
		for (BPResult bpResult : bpResults) {
			bpResult.status = 1;
			updateBPResult(bpResult);
		}
	}

	/**
	 * 添加血压计历史
	 * 
	 * @param bpResult
	 * @return
	 */
	public long addBpResult(BPResult bpResult) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(BPDataEntry.COLUMN_NAME_SZVALUE, bpResult.dbp);
		values.put(BPDataEntry.COLUMN_NAME_SSVALUE, bpResult.sbp);
		values.put(BPDataEntry.COLUMN_NAME_HEART_RATE, bpResult.pulse);
		values.put(BPDataEntry.COLUMN_NAME_HEART_RATE_STATE,
				bpResult.heartRateState);
		values.put(BPDataEntry.COLUMN_NAME_DATE, bpResult.date);
		values.put(BPDataEntry.COLUMN_NAME_STATUS, bpResult.status);
		values.put(BPDataEntry.COLUMN_NAME_CARD, bpResult.userCard);
		values.put(BPDataEntry.COLUMN_NAME_REMARKS, bpResult.remarks);
		long newRowId = db.insert(BPDataEntry.TABLE_NAME,
				BPDataEntry.COLUMN_NAME_NULLABLE, values);
		return newRowId;
	}

	/**
	 * 获得所有血糖数据
	 * 
	 * @return
	 */
	public List<BSResult> getBsResultsByUser(String idCard) {
		List<BSResult> bsResults = new ArrayList<>();
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		String[] projection = { BSDataEntry.COLUMN_NAME_ID,
				BSDataEntry.COLUMN_NAME_BSVALUE, BSDataEntry.COLUMN_NAME_DATE,
				BSDataEntry.COLUMN_NAME_CARD, BSDataEntry.COLUMN_NAME_REMARKS,
				BSDataEntry.COLUMN_NAME_MESURE_TIME };
		String selection = BSDataEntry.COLUMN_NAME_CARD + "=?";
		String selectionArgs[] = new String[] { idCard };
		Cursor c = db.query(BSDataEntry.TABLE_NAME, projection, selection, selectionArgs,
				null, null, null);
		while (c.moveToNext()) {
			int id = c.getInt(c
					.getColumnIndexOrThrow(BSDataEntry.COLUMN_NAME_ID));
			String bsValue = c.getString(c
					.getColumnIndexOrThrow(BSDataEntry.COLUMN_NAME_BSVALUE));
			long date = c.getLong(c
					.getColumnIndexOrThrow(BSDataEntry.COLUMN_NAME_DATE));
			String card = c.getString(c
					.getColumnIndexOrThrow(BSDataEntry.COLUMN_NAME_CARD));
			String remarks = c.getString(c
					.getColumnIndexOrThrow(BSDataEntry.COLUMN_NAME_REMARKS));
			bsResults.add(new BSResult(id, card, bsValue, date, remarks,
					DateUtil.getFormatDate(DateUtil.DATA_FORMAT, date)));// measure
																			// time
																			// 先存放date值
		}
		return bsResults;
	}

	public void deleteBsDatas() {

		List<BSResult> results = getBsResultsByStatus(1);
		if (results.size() <= (maxCount - 1)) {
			return;
		}

		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		StringBuilder selection = new StringBuilder();
		List<String> argList = new ArrayList<>();
		selection.append(BSDataEntry.COLUMN_NAME_ID + " in (");
		for (int i = results.size() - 1; i > (maxCount - 1); i--) {
			selection.append("?,");
			argList.add(results.get(i).id + "");
		}
		selection.deleteCharAt(selection.length() - 1);// 去掉最后一个逗号
		selection.append(")");

		String[] whereArgs = (String[]) argList.toArray(new String[argList
				.size()]);

		db.delete(BSDataEntry.TABLE_NAME, selection.toString(), whereArgs);
	}

	public List<BSResult> getBsResultsByStatus(int status) {
		List<BSResult> bsResults = new ArrayList<>();
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		String[] projection = { BSDataEntry.COLUMN_NAME_ID,
				BSDataEntry.COLUMN_NAME_BSVALUE, BSDataEntry.COLUMN_NAME_DATE,
				BSDataEntry.COLUMN_NAME_CARD, BSDataEntry.COLUMN_NAME_REMARKS,
				BSDataEntry.COLUMN_NAME_MESURE_TIME };
		String selection = BSDataEntry.COLUMN_NAME_STATUS + "=?";
		String[] args = { String.valueOf(status) };
		String orderBy = BSDataEntry.COLUMN_NAME_DATE + " DESC";
		Cursor c = db.query(BSDataEntry.TABLE_NAME, projection, selection,
				args, null, null, orderBy);
		while (c.moveToNext()) {
			int id = c.getInt(c
					.getColumnIndexOrThrow(BSDataEntry.COLUMN_NAME_ID));
			String bsValue = c.getString(c
					.getColumnIndexOrThrow(BSDataEntry.COLUMN_NAME_BSVALUE));
			long date = c.getLong(c
					.getColumnIndexOrThrow(BSDataEntry.COLUMN_NAME_DATE));
			String card = c.getString(c
					.getColumnIndexOrThrow(BSDataEntry.COLUMN_NAME_CARD));
			String remarks = c.getString(c
					.getColumnIndexOrThrow(BSDataEntry.COLUMN_NAME_REMARKS));
			bsResults.add(new BSResult(id, card, bsValue, date, remarks,
					DateUtil.getFormatDate(DateUtil.DATA_FORMAT, date)));// measure
																			// time
																			// 先存放date值
			System.out.println("measure time "
					+ DateUtil.getFormatDate(DateUtil.DATA_FORMAT, date));
		}
		return bsResults;
	}

//	public BSResult getBsResultByTime(String meatureTime) {
//		BSResult result = null;
//		SQLiteDatabase db = mDBHelper.getWritableDatabase();
//		String[] projection = { BSDataEntry.COLUMN_NAME_ID,
//				BSDataEntry.COLUMN_NAME_BSVALUE, BSDataEntry.COLUMN_NAME_DATE,
//				BSDataEntry.COLUMN_NAME_CARD, BSDataEntry.COLUMN_NAME_REMARKS,
//				BSDataEntry.COLUMN_NAME_MESURE_TIME };
//		String selection = BSDataEntry.COLUMN_NAME_MESURE_TIME + "=?";
//		String[] args = { String.valueOf(meatureTime) };
//		Cursor c = db.query(BSDataEntry.TABLE_NAME, projection, selection,
//				args, null, null, null);
//		while (c.moveToNext()) {
//			int id = c.getInt(c
//					.getColumnIndexOrThrow(BSDataEntry.COLUMN_NAME_ID));
//			String bsValue = c.getString(c
//					.getColumnIndexOrThrow(BSDataEntry.COLUMN_NAME_BSVALUE));
//			long date = c.getLong(c
//					.getColumnIndexOrThrow(BSDataEntry.COLUMN_NAME_DATE));
//			String card = c.getString(c
//					.getColumnIndexOrThrow(BSDataEntry.COLUMN_NAME_CARD));
//			String remarks = c.getString(c
//					.getColumnIndexOrThrow(BSDataEntry.COLUMN_NAME_REMARKS));
//			String measureTime = c
//					.getString(c
//							.getColumnIndexOrThrow(BSDataEntry.COLUMN_NAME_MESURE_TIME));
//			result = new BSResult(id, card, bsValue, date, remarks, measureTime);
//		}
//		return result;
//	}

	/**
	 * 添加血糖历史
	 * 
	 * @param bsResult
	 * @return
	 */
	public long addBsResult(BSResult bsResult) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(BSDataEntry.COLUMN_NAME_BSVALUE, bsResult.bg);
		values.put(BSDataEntry.COLUMN_NAME_STATUS, bsResult.status);
		values.put(BSDataEntry.COLUMN_NAME_DATE, bsResult.date);
		values.put(BSDataEntry.COLUMN_NAME_CARD, bsResult.userCard);
		values.put(BSDataEntry.COLUMN_NAME_REMARKS, bsResult.remarks);
		values.put(BSDataEntry.COLUMN_NAME_MESURE_TIME, bsResult.measureTime);
		long newRowId = db.insert(BSDataEntry.TABLE_NAME,
				BSDataEntry.COLUMN_NAME_NULLABLE, values);
		return newRowId;
	}

	public void updateBsResult(BSResult bsResult) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(BSDataEntry.COLUMN_NAME_BSVALUE, bsResult.bg);
		values.put(BSDataEntry.COLUMN_NAME_STATUS, bsResult.status);
		values.put(BSDataEntry.COLUMN_NAME_DATE, bsResult.date);
		values.put(BSDataEntry.COLUMN_NAME_CARD, bsResult.userCard);
		values.put(BSDataEntry.COLUMN_NAME_REMARKS, bsResult.remarks);
		values.put(BSDataEntry.COLUMN_NAME_MESURE_TIME, bsResult.measureTime);
		String where = BSDataEntry.COLUMN_NAME_ID + " = ?";
		String[] whereArgs = new String[] { String.valueOf(bsResult.id) };
		db.update(BSDataEntry.TABLE_NAME, values, where, whereArgs);
	}
	
	public void updateBsUserCard(String userCard){
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(BSDataEntry.COLUMN_NAME_CARD, userCard);
		String where = BSDataEntry.COLUMN_NAME_CARD + " = ?";
		String[] whereArgs = new String[] { "" };
		db.update(BSDataEntry.TABLE_NAME, values, where, whereArgs);
	} 

	public void changeBsSate(List<BSResult> bsResults) {
		for (BSResult bsResult : bsResults) {
			bsResult.status = 1;
			updateBsResult(bsResult);
		}
	}

	/**
	 * 获得所有胎心值
	 * 
	 * @return
	 */
	public List<FHResult> getFhResultsByUser(String idCard) {
		List<FHResult> fhResults = new ArrayList<>();
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		String[] projection = { FHDataEntry.COLUMN_NAME_ID,
				FHDataEntry.COLUMN_NAME_FHVALUES, FHDataEntry.COLUMN_NAME_DATE,
				FHDataEntry.COLUMN_NAME_CARD, FHDataEntry.COLUMN_NAME_REMARKS };
		String selection = FHDataEntry.COLUMN_NAME_CARD + "=?";
		String selectionArgs[] = new String[] { idCard };
		Cursor c = db.query(FHDataEntry.TABLE_NAME, projection, selection, selectionArgs,
				null, null, FHDataEntry.COLUMN_NAME_DATE + " desc");
		while (c.moveToNext()) {
			int id = c.getInt(c
					.getColumnIndexOrThrow(FHDataEntry.COLUMN_NAME_ID));
			String fhValues = c.getString(c
					.getColumnIndexOrThrow(FHDataEntry.COLUMN_NAME_FHVALUES));
			long date = c.getLong(c
					.getColumnIndexOrThrow(FHDataEntry.COLUMN_NAME_DATE));
			String card = c.getString(c
					.getColumnIndexOrThrow(FHDataEntry.COLUMN_NAME_CARD));
			String remarks = c.getString(c
					.getColumnIndexOrThrow(FHDataEntry.COLUMN_NAME_REMARKS));

			fhResults.add(new FHResult(id, card, fhValues, date, remarks));
		}
		return fhResults;
	}

	public void deleteFhDatas() {

		List<FHResult> results = getFhResultsByStatus(1);
		if (results.size() <= (maxCount - 1)) {
			return;
		}

		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		StringBuilder selection = new StringBuilder();
		List<String> argList = new ArrayList<>();
		selection.append(FHDataEntry.COLUMN_NAME_ID + " in (");
		for (int i = results.size() - 1; i > (maxCount - 1); i--) {
			selection.append("?,");
			argList.add(results.get(i).id + "");
		}
		selection.deleteCharAt(selection.length() - 1);// 去掉最后一个逗号
		selection.append(")");

		String[] whereArgs = (String[]) argList.toArray(new String[argList
				.size()]);

		db.delete(FHDataEntry.TABLE_NAME, selection.toString(), whereArgs);
	}

	public List<FHResult> getFhResultsByStatus(int status) {
		List<FHResult> fhResults = new ArrayList<>();
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		String[] projection = { FHDataEntry.COLUMN_NAME_ID,
				FHDataEntry.COLUMN_NAME_FHVALUES, FHDataEntry.COLUMN_NAME_DATE,
				FHDataEntry.COLUMN_NAME_CARD, FHDataEntry.COLUMN_NAME_REMARKS };
		String selections = FHDataEntry.COLUMN_NAME_STATUS + "=?";
		String[] args = { String.valueOf(status) };
		String orderBy = FHDataEntry.COLUMN_NAME_DATE + " DESC";
		Cursor c = db.query(FHDataEntry.TABLE_NAME, projection, selections,
				args, null, null, orderBy);
		while (c.moveToNext()) {
			int id = c.getInt(c
					.getColumnIndexOrThrow(FHDataEntry.COLUMN_NAME_ID));
			String fhValues = c.getString(c
					.getColumnIndexOrThrow(FHDataEntry.COLUMN_NAME_FHVALUES));
			long date = c.getLong(c
					.getColumnIndexOrThrow(FHDataEntry.COLUMN_NAME_DATE));
			String card = c.getString(c
					.getColumnIndexOrThrow(FHDataEntry.COLUMN_NAME_CARD));
			String remarks = c.getString(c
					.getColumnIndexOrThrow(FHDataEntry.COLUMN_NAME_REMARKS));
			fhResults.add(new FHResult(id, card, fhValues, date, remarks));
		}
		return fhResults;
	}

	/**
	 * 添加胎心历史
	 * 
	 * @param fhResult
	 * @return
	 */
	public long addFhResult(FHResult fhResult) {
		System.out.println("fh " + fhResult.fhValues.size());
		if (fhResult.fhValues.isEmpty()) {
			return 0;
		}
		System.out.println("enter");
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(FHDataEntry.COLUMN_NAME_FHVALUES,
				listToStr(fhResult.fhValues, ","));
		values.put(FHDataEntry.COLUMN_NAME_STATUS, fhResult.status);
		values.put(FHDataEntry.COLUMN_NAME_DATE, fhResult.date);
		values.put(FHDataEntry.COLUMN_NAME_CARD, fhResult.userCard);
		values.put(FHDataEntry.COLUMN_NAME_REMARKS, fhResult.remarks);
		long newRowId = db.insert(FHDataEntry.TABLE_NAME,
				FHDataEntry.COLUMN_NAME_NULLABLE, values);
		return newRowId;
	}

	public void updateFhResult(FHResult fhResult) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(FHDataEntry.COLUMN_NAME_FHVALUES,
				listToStr(fhResult.fhValues, ","));
		values.put(FHDataEntry.COLUMN_NAME_STATUS, fhResult.status);
		values.put(FHDataEntry.COLUMN_NAME_DATE, fhResult.date);
		values.put(FHDataEntry.COLUMN_NAME_CARD, fhResult.userCard);
		values.put(FHDataEntry.COLUMN_NAME_REMARKS, fhResult.remarks);
		String where = FHDataEntry.COLUMN_NAME_ID + " = ?";
		String[] whereArgs = new String[] { String.valueOf(fhResult.id) };
		db.update(FHDataEntry.TABLE_NAME, values, where, whereArgs);
	}
	
	public void updateFhUserCard(String userCard){
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(FHDataEntry.COLUMN_NAME_CARD, userCard);
		String where = FHDataEntry.COLUMN_NAME_CARD + " = ?";
		String[] whereArgs = new String[] { "" };
		db.update(FHDataEntry.TABLE_NAME, values, where, whereArgs);
	}
	

	public void changeFhState(List<FHResult> fhResults) {
		for (FHResult fhResult : fhResults) {
			fhResult.status = 1;
			updateFhResult(fhResult);
		}
	}

	/**
	 * 将list 安分隔符拼接成字符后窜
	 * 
	 * @param list
	 * @param separator
	 * @return String
	 */
	private <T> String listToStr(List<T> list, String separator) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0, size = list.size(); i < size; i++) {
			sb.append(list.get(i));
			sb.append(separator);
		}

		return sb.toString();

	}
}
