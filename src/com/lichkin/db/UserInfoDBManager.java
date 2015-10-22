package com.lichkin.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lichkin.db.DeviceDataContract.UserInfoEntry;
import com.lichkin.entity.UserInfo;

public class UserInfoDBManager {

	private Context mContext;
	private DBHelper mDBHelper;
	private static UserInfoDBManager mManager;

	private UserInfoDBManager(Context context) {
		this.mContext = context;
		mDBHelper = new DBHelper(mContext);
	}

	public static UserInfoDBManager getInstance(Context context) {
		if (mManager == null) {
			mManager = new UserInfoDBManager(context);
		}
		return mManager;
	}

	public long saveUser(UserInfo userInfo) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		
		List<String> users = new ArrayList<>();
		String[] projection = { UserInfoEntry.COLUMN_NAME_LOGIN_ID};
		String selection = UserInfoEntry.COLUMN_NAME_LOGIN_ID + "=?";
		String selectionArgs[] = new String[] { userInfo.loginId };
		Cursor c = db.query(UserInfoEntry.TABLE_NAME, projection, selection,
				selectionArgs, null, null, null);
		
		while (c.moveToNext()) {
			String loginId = c.getString(c
					.getColumnIndexOrThrow(UserInfoEntry.COLUMN_NAME_LOGIN_ID));
			users.add(loginId);
		}
		if(!users.isEmpty()) {
			return 0;
		}
			
		ContentValues values = new ContentValues();
		values.put(UserInfoEntry.COLUMN_NAME_LOGIN_ID, userInfo.loginId);
		values.put(UserInfoEntry.COLUMN_NAME_USERNAME, userInfo.userName);
		values.put(UserInfoEntry.COLUMN_NAME_CELLPHONE, userInfo.cellphone);
		values.put(UserInfoEntry.COLUMN_NAME_USER_CARD, userInfo.userCard);
		return db.insert(UserInfoEntry.TABLE_NAME,
				UserInfoEntry.COLUMN_NAME_NULLABLE, values);
	}

	public int updateUserInfo(UserInfo userInfo) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(UserInfoEntry.COLUMN_NAME_USERNAME, userInfo.userName);
		values.put(UserInfoEntry.COLUMN_NAME_CELLPHONE, userInfo.cellphone);
		values.put(UserInfoEntry.COLUMN_NAME_USER_CARD, userInfo.userCard);
		String whereClause = UserInfoEntry.COLUMN_NAME_LOGIN_ID + " = ?";
		return db.update(UserInfoEntry.TABLE_NAME, values, whereClause,
				new String[] { userInfo.loginId });
	}

	public int deleteUserInfoById(String loginId) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		String whereClause = UserInfoEntry.COLUMN_NAME_LOGIN_ID + " = ?";
		return db.delete(UserInfoEntry.TABLE_NAME, whereClause,
				new String[] { loginId });
	}

	public int deleteUserInfoByCard(String userCard) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		String whereClause = UserInfoEntry.COLUMN_NAME_USER_CARD + " = ?";
		return db.delete(UserInfoEntry.TABLE_NAME, whereClause,
				new String[] { userCard });
	}

	public UserInfo getUserInfoById(String loginId) {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		String[] columns = new String[] { UserInfoEntry.COLUMN_NAME_LOGIN_ID,
				UserInfoEntry.COLUMN_NAME_USERNAME,
				UserInfoEntry.COLUMN_NAME_CELLPHONE,
				UserInfoEntry.COLUMN_NAME_USER_CARD };
		String selection = UserInfoEntry.COLUMN_NAME_LOGIN_ID + " = ?";
		Cursor cursor = db.query(UserInfoEntry.TABLE_NAME, columns, selection,
				new String[] { loginId }, null, null, null);
		UserInfo result = null;
		if (cursor.moveToFirst()) {
			String userName = cursor.getString(cursor
					.getColumnIndex(UserInfoEntry.COLUMN_NAME_USERNAME));
			String cellphone = cursor.getString(cursor
					.getColumnIndex(UserInfoEntry.COLUMN_NAME_CELLPHONE));
			String userCard = cursor.getString(cursor
					.getColumnIndex(UserInfoEntry.COLUMN_NAME_USER_CARD));
			result = new UserInfo(loginId, userName, cellphone, userCard);
		}
		return result;
	}

	public UserInfo getUserInfoByCard(String card) {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		String[] columns = new String[] { UserInfoEntry.COLUMN_NAME_LOGIN_ID,
				UserInfoEntry.COLUMN_NAME_USERNAME,
				UserInfoEntry.COLUMN_NAME_CELLPHONE,
				UserInfoEntry.COLUMN_NAME_USER_CARD };
		String selection = UserInfoEntry.COLUMN_NAME_USER_CARD + " = ?";
		Cursor cursor = db.query(UserInfoEntry.TABLE_NAME, columns, selection,
				new String[] { card }, null, null, null);
		UserInfo result = null;
		if (cursor.moveToFirst()) {
			String loginId = cursor.getString(cursor
					.getColumnIndex(UserInfoEntry.COLUMN_NAME_LOGIN_ID));
			String userName = cursor.getString(cursor
					.getColumnIndex(UserInfoEntry.COLUMN_NAME_USERNAME));
			String cellphone = cursor.getString(cursor
					.getColumnIndex(UserInfoEntry.COLUMN_NAME_CELLPHONE));
			result = new UserInfo(loginId, userName, cellphone, card);
		}
		return result;
	}
}
