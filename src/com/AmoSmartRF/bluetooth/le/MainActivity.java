package com.AmoSmartRF.bluetooth.le;

import com.lin.bluetooth.le.R;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

@SuppressLint("ClickableViewAccessibility")
public class MainActivity extends Activity {
	private double[] data = new double[] { 3.567, 1.000, 1.000, 2, 4, 1, 1, 2, 4, 1, 1, 2, 3 };
	private double[] text = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	private HistogramView histogramView;
	private String[] recvData=new String[13];
	private double[] recvData2=new double[13];
	@SuppressLint("ClickableViewAccessibility")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.histogram);
		histogramView = (HistogramView) this.findViewById(R.id.histogram);
		getActionBar().setTitle("电压柱状图");

		/*Intent intent = getIntent() ;
		double recvData[] = intent.getDoubleArrayExtra("DATA") ;
		*/
		Intent intent = getIntent();
		Bundle bundle = this.getIntent().getExtras();
		recvData = bundle.getStringArray("voldata2") ;
		
		for(int i=0;i<13;i++){
			recvData2[i]=Double.parseDouble(recvData[i]);
		}
		
		histogramView.setProgress(recvData2);
		histogramView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				int step = (v.getWidth() - 30) / 14;
				int x = (int) event.getX();
				for (int i = 0; i < 13; i++) {
					if (x > (30 + step * (i + 1) - 30)
							&& x < (30 + step * (i + 1) + 30)) {
						text[i] = 1;
						for (int j = 0; j < 13; j++) {
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
		

	}

}