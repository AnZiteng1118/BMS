package com.AmoSmartRF.bluetooth.le;

import com.lin.bluetooth.le.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;

public class HistogramMainFragment extends Fragment {
	
	//private double[] data = new double[] { 3.567, 1.000, 1.000, 2, 4, 1, 1, 2, 4, 1, 1, 2, 3 };
	private double[] text = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,0,0,0 ,0,0,0};
	private HistogramView histogramView;
	private double[] recvData=new double[]{4.144,4.145,4.142,4.105,4.032,4.139,4.139,4.153,4.149,4.134,0,0,0,0,0,0};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View histogramLayout = inflater.inflate(R.layout.activity_histogram_main,
				container, false);
		histogramView = (HistogramView) histogramLayout.findViewById(R.id.histogram_main);
		
		histogramView.setProgress(recvData);
		histogramView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				int step = (v.getWidth() - 30) / 14;
				int x = (int) event.getX();
				for (int i = 0; i < 16; i++) {
					if (x > (30 + step * (i + 1) - 30)
							&& x < (30 + step * (i + 1) + 30)) {
						text[i] = 1;
						for (int j = 0; j < 16; j++) {
							if (i != j) {
								text[j] = 0;
							}
						}
						histogramView.setText(text);
					}
				}

				return false;
			}
		});
		return histogramLayout;
	}
}
