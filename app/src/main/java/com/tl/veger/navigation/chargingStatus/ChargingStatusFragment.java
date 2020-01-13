package com.tl.veger.navigation.chargingStatus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tl.veger.R;
import com.tl.veger.base.BaseFragment;
import com.tl.veger.base.app.AppApplication;
import com.tl.veger.busbean.BatteryBusBean;
import com.tl.veger.busbean.BluetoothBusBean;
import com.tl.veger.utils.ConmmonUtil;
import com.tl.veger.utils.ConstanceValue;
import com.tl.veger.widge.ShowNumberByPic;
import com.tl.veger.widge.SmartTabBar;
import com.tl.veger.widge.waves.MultiWaveHeader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;

public class ChargingStatusFragment extends BaseFragment {

	@BindView(R.id.tab_navigation_bar)
	SmartTabBar tab_navigation_bar;
	@BindView(R.id.navigation_bar_container)
	LinearLayout navigation_bar_container;
	@BindView(R.id.charge_iv)
	ImageView charge_iv;
	@BindView(R.id.no_charge)
	View no_charge;
	@BindView(R.id.charge_wave)
	MultiWaveHeader charge_wave;
	@BindView(R.id.showNumberByPic)
	ShowNumberByPic showNumberByPic;

	public static final String TAG = "ChargingStatusFragment";
	private List<Integer> batteryList = new ArrayList<>();//剩余电量使用时间
	private List<Integer> percentList = new ArrayList<>();//剩余电量百分比
	private List<Integer> chargeNeedTimeList = new ArrayList<>();//充满电需要的时间
	private List<Integer> chargeTimeList = new ArrayList<>();//充满电的时间
	private batterystate state = batterystate.BATTERY_PERCENTAGE;
	private ArrayList<Integer> blockList = new ArrayList<>();

	private enum batterystate {
		BATTERY_PERCENTAGE, COUNTDOWN_TO_FULLY_CHARGED, BATTERY_TIME_REMAINING, BATTERY_FULL_TIME
	}

	@Override
	protected int getLayoutResId() {
		return R.layout.fragment_charging_status;
	}

	public static ChargingStatusFragment newInstance() {

		Bundle args = new Bundle();

		ChargingStatusFragment fragment = new ChargingStatusFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	protected void init() {
		EventBus.getDefault().register(this);
		initTab();
		setData();
		setBlockList();
	}


	private void setBlockList() {
		blockList.add(0);
		blockList.add(0);
		blockList.add(0);
		blockList.add(0);
	}

	private void initTab() {

		LinearLayout.LayoutParams layoutParams =
				new LinearLayout.LayoutParams(AppApplication.getInstance().getWindowWidth() / 3,
						LinearLayout.LayoutParams.MATCH_PARENT);
		// layoutParams.setMargins(30,0,30,5);

		List<String> tabList = new ArrayList<>();
		List<String> baseTabList = new ArrayList<>();
		baseTabList.add("Battery Percentage");
		baseTabList.add("Countdown To Fully Charged");
		baseTabList.add("Battery Time Remaining");
		baseTabList.add("Battery Full Time");

		for (int i = 0; i < 150; i++) {
			tabList.addAll(baseTabList);
		}

		for (int i = 0; i < tabList.size(); i++) {
			TextView tv = new TextView(getContext());
			tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
			tv.setGravity(Gravity.CENTER);
			tv.setText(tabList.get(i));
			tv.setTextColor(Color.WHITE);
			//tv.setTag(i);
			navigation_bar_container.addView(tv, layoutParams);
		}

		tab_navigation_bar.getChildInfo();
		//默认选中第三个
		tab_navigation_bar.setDefaultPosition(152);
		tab_navigation_bar.setSelecteListener(new SmartTabBar.OnScrollViewSelectListener() {
			@Override
			public void OnSelectListener(TextView tab) {
				tab_navigation_bar.setCurrentTabInMiddle((int) tab.getTag());

				String title = tab.getText().toString();

				switch (title) {
					case "Battery Percentage"://剩余电量百分比
						state = batterystate.BATTERY_PERCENTAGE;
						showNumberByPic.setData(percentList);
						break;

					case "Countdown To Fully Charged"://充满电需要的时间
						state = batterystate.COUNTDOWN_TO_FULLY_CHARGED;
						if (ConstanceValue.IS_CHARGING) {
							showNumberByPic.setData(chargeNeedTimeList);
						} else {
							showNumberByPic.setData(blockList);
						}
						break;

					case "Battery Time Remaining"://剩余电量使用时间
						state = batterystate.BATTERY_TIME_REMAINING;
						if (!ConstanceValue.IS_CHARGING) {
							showNumberByPic.setData(batteryList);
						} else {
							showNumberByPic.setData(blockList);
						}
						break;

					case "Battery Full Time"://充满电的时间
						state = batterystate.BATTERY_FULL_TIME;
						if (ConstanceValue.IS_CHARGING) {
							showNumberByPic.setData(chargeTimeList);
						} else {
							showNumberByPic.setData(blockList);
						}
						break;
				}

			}
		});
	}


	@Subscribe
	public void onEvent(BatteryBusBean bean) {
		setData();
	}

	private List<Integer> TimeProc(int totalMins) {
		int hour = totalMins / 60;
		int min = totalMins % 60;

		List<Integer> dataList = new ArrayList<>();
		dataList.add(hour/10);
		dataList.add(hour%10);
		dataList.add(min/10);
		dataList.add(min%10);

		return dataList;
	}

	private void setData() {
		percentList.clear();
		batteryList.clear();
		chargeNeedTimeList.clear();
		chargeTimeList.clear();
		DecimalFormat df = new DecimalFormat("0.0");
		//更新percentList
		percentList.addAll(ConmmonUtil.getNumberList(ConstanceValue.CURRENT_BATTERY_PERCENT + "",
				ConmmonUtil.INTEGER));

		//更新batteryList
		double canUseTime = ((ConstanceValue.CURRENT_BATTERY_PERCENT/100.0) * 22 * 60);
		batteryList.addAll(TimeProc((int)canUseTime));

		//更新chargeNeedTimeList
		double tmp = (100 - ConstanceValue.CURRENT_BATTERY_PERCENT)/100.0;
		float needTime = (float) (tmp * 3.5) * 60;
		chargeNeedTimeList.addAll( TimeProc((int)needTime) );

		//更新chargeTimeList
		Calendar calendars = Calendar.getInstance();
		int hour = calendars.get(Calendar.HOUR);
		int min = calendars.get(Calendar.MINUTE);

		float chargeTime = hour*60 + min + needTime;
		chargeTimeList.addAll(TimeProc((int)chargeTime));

		//根据当前所选状态更新ui
		switch (state) {
			case BATTERY_PERCENTAGE:
				showNumberByPic.setData(percentList);
				break;
			case BATTERY_FULL_TIME:
				if (ConstanceValue.IS_CHARGING) {
					showNumberByPic.setData(chargeTimeList);
				} else {
					showNumberByPic.setData(blockList);
				}

				break;
			case BATTERY_TIME_REMAINING:
				if (!ConstanceValue.IS_CHARGING) {
					showNumberByPic.setData(batteryList);
				} else {
					showNumberByPic.setData(blockList);
				}
				break;

			case COUNTDOWN_TO_FULLY_CHARGED:
				if (ConstanceValue.IS_CHARGING) {
					showNumberByPic.setData(chargeNeedTimeList);
				} else {
					showNumberByPic.setData(blockList);
				}

				break;
		}
		setChargeState();


		//是否充电
		showWave(ConstanceValue.IS_CHARGING);
	}


	private void showWave(boolean isShow) {
		if (isShow) {
			no_charge.setVisibility(View.GONE);
			charge_wave.setVisibility(View.VISIBLE);
			setChargeState();
		} else {
			no_charge.setVisibility(View.VISIBLE);
			charge_wave.setVisibility(View.GONE);
			charge_iv.setImageResource(R.mipmap.not_charging);
		}
		BluetoothBusBean bluetoothBusBean = new BluetoothBusBean();
		bluetoothBusBean.setNotice(ConstanceValue.SWITCH_BATTERY);
		EventBus.getDefault().post(bluetoothBusBean);
	}


	private void setChargeState() {
		if (ConstanceValue.CURRENT_BATTERY_PERCENT <= 50) {
			charge_iv.setImageResource(R.mipmap.fast_charge);
		} else if (ConstanceValue.CURRENT_BATTERY_PERCENT <= 80) {
			charge_iv.setImageResource(R.mipmap.charge);
		} else {
			charge_iv.setImageResource(R.mipmap.trickle_charge);
		}
	}


	@Override
	public void onDetach() {
		super.onDetach();
		EventBus.getDefault().unregister(this);
	}
}