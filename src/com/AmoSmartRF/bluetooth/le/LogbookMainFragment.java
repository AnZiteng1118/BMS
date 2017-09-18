package com.AmoSmartRF.bluetooth.le;

import com.lin.bluetooth.le.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LogbookMainFragment extends Fragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View schoolLayout = inflater.inflate(R.layout.activity_school_main,
				container, false);
		return schoolLayout;
	}
}
