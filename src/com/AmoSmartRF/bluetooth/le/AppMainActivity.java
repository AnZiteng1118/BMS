package com.AmoSmartRF.bluetooth.le;

import com.lin.bluetooth.le.R;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

public class AppMainActivity extends Activity {

	private FragmentManager fragmentManager;

	private SchoolMainFragment schoolMainFragment;
	private HistogramMainFragment histogramMainFragment;
	private CurveMainFragment curveMainFragment;
	private LogbookMainFragment logbookMainFragment;

	private long mExitTime = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initTab();
		fragmentManager = getFragmentManager();
		initTabHost(0);

	}

	private void initTab() {
		RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radio_group);
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				switch (arg1) {
				case R.id.rb_tab_one:
					initTabHost(0);
					break;
				case R.id.rb_tab_two:
					initTabHost(1);
					break;
				case R.id.rb_tab_three:
					initTabHost(2);
					break;
				case R.id.rb_tab_four:
					initTabHost(3);
					break;
				default:
					break;
				}
			}
		});
	}
	
	private void initTabHost(int index) {
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		hideFragments(transaction);
		switch (index) {
		case 0:
			if (schoolMainFragment == null) {
				schoolMainFragment = new SchoolMainFragment();
				transaction.add(R.id.tabcontent, schoolMainFragment);
			} else if (schoolMainFragment.isHidden()) {
				transaction.show(schoolMainFragment);
			}
			break;
		case 1:
			if (histogramMainFragment == null) {
				// 如果ContactsFragment为空，则创建一个并添加到界面上
				histogramMainFragment = new HistogramMainFragment();
				transaction.add(R.id.tabcontent, histogramMainFragment);
			} else if (histogramMainFragment.isHidden()) {
				// 如果ContactsFragment不为空，则直接将它显示出来
				transaction.show(histogramMainFragment);
			}
			break;
		case 2:
			if (curveMainFragment == null) {
				// 如果NewsFragment为空，则创建一个并添加到界面上
				curveMainFragment = new CurveMainFragment();
				transaction.add(R.id.tabcontent, curveMainFragment);
			} else if (curveMainFragment.isHidden()) {
				// 如果NewsFragment不为空，则直接将它显示出来
				transaction.show(curveMainFragment);
			}
			break;
		case 3:
			if (logbookMainFragment == null) {
				// 如果NewsFragment为空，则创建一个并添加到界面上
				logbookMainFragment = new LogbookMainFragment();
				transaction.add(R.id.tabcontent, logbookMainFragment);
			} else if (logbookMainFragment.isHidden()) {
				// 如果NewsFragment不为空，则直接将它显示出来
				transaction.show(logbookMainFragment);
			}
			break;
		default:
			break;
		}
		transaction.commit();
	}


	private void hideFragments(FragmentTransaction transaction) {
		if (schoolMainFragment != null) {
			transaction.hide(schoolMainFragment);
		}
		if (histogramMainFragment != null) {
			transaction.hide(histogramMainFragment);
		}
		if (curveMainFragment != null) {
			transaction.hide(curveMainFragment);
		}
		if (logbookMainFragment != null) {
			transaction.hide(logbookMainFragment);
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
