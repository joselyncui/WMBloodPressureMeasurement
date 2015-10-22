package com.lichkin.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;

import com.lichkin.activity.LoginActivity;
import com.lichkin.activity.R;

public class IndexFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setHasOptionsMenu(true);// ÏÔÊ¾fragmentµÄmenu
		View view = inflater.inflate(R.layout.fragment_index, container, false);
		ButterKnife.inject(this, view);
		System.out.println("index oncreate view");
		return view;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		System.out.println("index onresume view");
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		super.onCreateOptionsMenu(menu, inflater);
		// menu.add("µÇÂ¼").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);//µÇÂ¼menu
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		Intent intent = new Intent(getActivity(), LoginActivity.class);
		startActivity(intent);
		return super.onOptionsItemSelected(item);
	}

}
