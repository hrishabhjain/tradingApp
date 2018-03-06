package com.tradingapp;

/**
 * Created by rishabhjain on 3/5/18.
 */
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;
import com.tradingapp.component.ApplicationComponent;
import com.tradingapp.databinding.ActivityHomeBinding;
import com.tradingapp.modules.ActivityModule;
import com.tradingapp.usecase.RestApiUseCase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import javax.inject.Inject;

public class HomeActivity extends AppCompatActivity implements
        OnChartGestureListener, OnChartValueSelectedListener, HomeViewModel.CallBack {

    @Inject
    public HomeViewModel homeViewModel;

    @Inject
    public RestApiUseCase restApiUseCase;

    private LineChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationComponent component = TradingApplication.get(this).getComponent();
        component.getActivitySubComponentBuilder().activityModule(new ActivityModule(this)).build().inject(this);

        ActivityHomeBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        binding.setVariable(BR.model, homeViewModel);
        homeViewModel.setCallBack(this);
        homeViewModel.onLoad(this);

        mChart = binding.chart;
        initChart();

    }

    private void initChart() {
        mChart.setOnChartGestureListener(this);
        mChart.setOnChartValueSelectedListener(this);
        mChart.setDrawGridBackground(false);

        mChart.getDescription().setEnabled(false);
        mChart.setTouchEnabled(true);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setPinchZoom(true);

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
//        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
//        mv.setChartView(mChart); // For bounds control
//        mChart.setMarker(mv); // Set the marker to the chart

        mChart.getAxisRight().setEnabled(false);
        mChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        mChart.getXAxis().setLabelCount(3, true);
        mChart.getXAxis().setValueFormatter(new IAxisValueFormatter() {

            private SimpleDateFormat mFormat = new SimpleDateFormat("dd MMM yy");

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mFormat.format(new Date((long) value));
            }
        });
    }

    @Override
    public void setData(List<OHLC> ohlcList) {
        ArrayList<Entry> values = new ArrayList<Entry>();
        for (int i = ohlcList.size() - 1; i >= 0; i--) {
            OHLC ohlc = ohlcList.get(i);
            float avg = (ohlc.getHigh() + ohlc.getLow())/2;
            values.add(new Entry(ohlc.getTimestamp(), avg, getResources().getDrawable(R.drawable.ic_launcher_background)));
        }

        LineDataSet set1;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet)mChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {

            set1 = new LineDataSet(values, "Price");
            set1.setDrawIcons(false);
            set1.setDrawFilled(true);

            if (Utils.getSDKInt() >= 18) {
                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_red);
                set1.setFillDrawable(drawable);
            } else {
                set1.setFillColor(Color.BLACK);
            }

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1); // add the datasets

            LineData data = new LineData(dataSets);
            mChart.setData(data);
        }
        mChart.animateX(1000);
        Legend l = mChart.getLegend();

        l.setForm(LegendForm.LINE);

        List<ILineDataSet> sets = mChart.getData()
                .getDataSets();

        for (ILineDataSet iSet : sets) {

            LineDataSet set = (LineDataSet) iSet;
            set.setDrawValues(false);
            set.setDrawCircles(false);
        }

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.setAxisMaximum((float) (ohlcList.get(0).getHigh() + (ohlcList.get(0).getHigh() * .35)));
        leftAxis.setAxisMinimum((float) (ohlcList.get(0).getHigh() - (ohlcList.get(0).getHigh() * .25)));
        leftAxis.enableGridDashedLine(0f, 1000f, 0f);
        leftAxis.setDrawZeroLine(false);

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);
        mChart.setExtraRightOffset(32);
        mChart.invalidate();
    }

    @Override
    public void setError(String error) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(error);
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                "Try Again",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        homeViewModel.onLoad(HomeActivity.this);
                        dialog.dismiss();
                    }
                });


        final AlertDialog alert11 = builder1.create();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                alert11.show();
            }
        });
    }

    //region: gestures

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "START, x: " + me.getX() + ", y: " + me.getY());
    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "END, lastGesture: " + lastPerformedGesture);

        // un-highlight values after the gesture is finished and no single-tap
        if(lastPerformedGesture != ChartTouchListener.ChartGesture.SINGLE_TAP)
            mChart.highlightValues(null); // or highlightTouch(null) for callback to onNothingSelected(...)
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {
        Log.i("LongPress", "Chart longpressed.");
    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
        Log.i("DoubleTap", "Chart double-tapped.");
    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        Log.i("SingleTap", "Chart single-tapped.");
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
        Log.i("Fling", "Chart flinged. VeloX: " + velocityX + ", VeloY: " + velocityY);
    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
        Log.i("Scale / Zoom", "ScaleX: " + scaleX + ", ScaleY: " + scaleY);
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
        Log.i("Translate / Move", "dX: " + dX + ", dY: " + dY);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Entry selected", e.toString());
        Log.i("LOWHIGH", "low: " + mChart.getLowestVisibleX() + ", high: " + mChart.getHighestVisibleX());
        Log.i("MIN MAX", "xmin: " + mChart.getXChartMin() + ", xmax: " + mChart.getXChartMax() + ", ymin: " + mChart.getYChartMin() + ", ymax: " + mChart.getYChartMax());
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }

    //endregion
}

