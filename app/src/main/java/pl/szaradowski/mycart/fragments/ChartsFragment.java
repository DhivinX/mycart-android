/*
 * Created by Dominik Szaradowski on 23.05.19 10:44
 * Copyright (c) 2019 . All rights reserved.
 * Website: https://www.szaradowski.pl
 */

package pl.szaradowski.mycart.fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.text.HtmlCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.EntryXComparator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import pl.szaradowski.mycart.R;
import pl.szaradowski.mycart.common.Receipt;
import pl.szaradowski.mycart.common.Utils;
import pl.szaradowski.mycart.components.RichButton;
import pl.szaradowski.mycart.components.RichTextView;

public class ChartsFragment extends Fragment {
    RichButton btnFrom, btnTo;
    Calendar from, to;
    RichTextView tvSum, tvSumTitle, tvReceiptTitle;
    HorizontalBarChart chLast6;
    LineChart chReceipt;
    LinearLayout rootView;
    Snackbar chart_info;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_charts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnFrom = view.findViewById(R.id.btnFrom);
        btnTo = view.findViewById(R.id.btnTo);
        tvSum = view.findViewById(R.id.tvSum);
        tvSumTitle = view.findViewById(R.id.tvSumTitle);
        tvReceiptTitle = view.findViewById(R.id.tvReceiptTitle);
        rootView = view.findViewById(R.id.rootView);
        chLast6 = view.findViewById(R.id.chLast6);
        chReceipt = view.findViewById(R.id.chReceipt);

        chLast6.setNoDataText(getString(R.string.no_chart_data));
        chReceipt.setNoDataText(getString(R.string.no_chart_data));

        chart_info = Snackbar.make(rootView, "", Snackbar.LENGTH_SHORT);

        from = Calendar.getInstance();
        from.set(Calendar.DAY_OF_MONTH, 1);

        to = Calendar.getInstance();

        btnFrom.setText(formatDate(from.get(Calendar.YEAR), from.get(Calendar.MONTH), from.get(Calendar.DAY_OF_MONTH)));
        btnTo.setText(formatDate(to.get(Calendar.YEAR), to.get(Calendar.MONTH), to.get(Calendar.DAY_OF_MONTH)));

        btnFrom.setOnClickListener(new RichButton.OnClickListener() {
            @Override
            public void onClick() {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String date = formatDate(year, month, dayOfMonth);
                        from.set(year, month, dayOfMonth, 0, 0, 0);
                        from.set(Calendar.MILLISECOND, 0);

                        btnFrom.setText(date);
                        refresh();
                    }
                }, from.get(Calendar.YEAR), from.get(Calendar.MONTH), from.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.show();
            }
        });

        btnTo.setOnClickListener(new RichButton.OnClickListener() {
            @Override
            public void onClick() {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String date = formatDate(year, month, dayOfMonth);
                        to.set(year, month, dayOfMonth, 23, 59, 59);
                        to.set(Calendar.MILLISECOND, 0);

                        btnTo.setText(date);
                        refresh();
                    }
                }, to.get(Calendar.YEAR), to.get(Calendar.MONTH), to.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.show();
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refresh();
            }
        }, 400);
    }

    private String formatDate(int year, int month, int dayOfMonth){
        return dayOfMonth + " " + getContext().getString(getContext().getResources().getIdentifier("month_sm_" + (month + 1), "string", getContext().getPackageName())) + " " + year;
    }

    private boolean isTimePrevMonth(long time, int month, ArrayList<String> barEntries){
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);

        Calendar c2 = Calendar.getInstance();
        c2.add(Calendar.MONTH, month * -1);

        String m = (c2.get(Calendar.MONTH) + 1) + "";
        if(!barEntries.contains(m)){
            barEntries.add(m);
        }

        return c2.get(Calendar.YEAR) == c.get(Calendar.YEAR) && c2.get(Calendar.MONTH) == c.get(Calendar.MONTH);
    }

    @SuppressLint("SetTextI18n")
    private void refresh(){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -6);
        long max_time = c.getTimeInMillis();

        float sum = 0;
        for(Receipt r : Utils.db.getReceiptsList(from.getTimeInMillis(), to.getTimeInMillis())) {
            sum += r.getVal();
        }

        tvSum.setText(Utils.currency.formatPrice(sum));
        tvSumTitle.setText(
                HtmlCompat.fromHtml(
                        getString(R.string.charts_title_sum, "<b>"+btnFrom.getText().toString()+"</b>", "<b>"+btnTo.getText().toString()+"</b>")
                        , HtmlCompat.FROM_HTML_MODE_LEGACY)
        );

        //SQLiteDatabase db = Utils.db.getReadableDatabase();


        /*
        ArrayList<Entry> receiptEntries = new ArrayList<>();
        ArrayList<BarEntry> barEntries_6 = new ArrayList<>();

        ArrayList<String> barEntries_6_months = new ArrayList<>();
        Map<Integer, Float> map_days = new HashMap<>();

        float sum = 0;

        float sum_m0 = 0;
        float sum_m1 = 0;
        float sum_m2 = 0;
        float sum_m3 = 0;
        float sum_m4 = 0;
        float sum_m5 = 0;

        for(Receipt r : Receipt.getList()){
            float price_all = r.getVal();

            if(isInRange(r.getTime())){
                sum += price_all;

                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(r.getTime());
                c.set(Calendar.HOUR, 0);
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MILLISECOND, 0);

                int t = (int) (c.getTimeInMillis() / 1000);

                try {
                    float v = map_days.get(t);

                    v += price_all;

                    map_days.remove(t);
                    map_days.put(t, v);
                }catch (NullPointerException e){
                    map_days.put(t, price_all);
                }
            }

            if(isTimePrevMonth(r.getTime(), 5, barEntries_6_months)) sum_m0 += price_all;
            if(isTimePrevMonth(r.getTime(), 4, barEntries_6_months)) sum_m1 += price_all;
            if(isTimePrevMonth(r.getTime(), 3, barEntries_6_months)) sum_m2 += price_all;
            if(isTimePrevMonth(r.getTime(), 2, barEntries_6_months)) sum_m3 += price_all;
            if(isTimePrevMonth(r.getTime(), 1, barEntries_6_months)) sum_m4 += price_all;
            if(isTimePrevMonth(r.getTime(), 0, barEntries_6_months)) sum_m5 += price_all;
        }

        if(Receipt.getList().size() > 0) {
            barEntries_6.add(new BarEntry(0, sum_m0));
            barEntries_6.add(new BarEntry(1, sum_m1));
            barEntries_6.add(new BarEntry(2, sum_m2));
            barEntries_6.add(new BarEntry(3, sum_m3));
            barEntries_6.add(new BarEntry(4, sum_m4));
            barEntries_6.add(new BarEntry(5, sum_m5));

            drawChart6(barEntries_6, barEntries_6_months);

            receiptEntries.clear();

            Iterator it = map_days.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();

                Log.e("d", pair.getKey()+" "+pair.getValue());

                receiptEntries.add(new Entry((int) pair.getKey(), (float) pair.getValue()));
                it.remove();
            }

            drawChartReceipt(receiptEntries);
        }

        tvSumTitle.setText(
                HtmlCompat.fromHtml(
                        getString(R.string.charts_title_sum, "<b>"+btnFrom.getText().toString()+"</b>", "<b>"+btnTo.getText().toString()+"</b>")
                , HtmlCompat.FROM_HTML_MODE_LEGACY)
        );

        tvReceiptTitle.setText(
                HtmlCompat.fromHtml(
                        getString(R.string.charts_title_receipt, "<b>"+btnFrom.getText().toString()+"</b>", "<b>"+btnTo.getText().toString()+"</b>")
                        , HtmlCompat.FROM_HTML_MODE_LEGACY)
        );

        tvSum.setText(Utils.currency.formatPrice(sum));
        */
    }

    private void drawChart6(ArrayList<BarEntry> entries, final ArrayList<String> months){
        BarDataSet barSet = new BarDataSet(entries, "");
        barSet.setColors(ColorTemplate.COLORFUL_COLORS);
        barSet.setDrawValues(true);

        barSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });

        BarData barData = new BarData(barSet);

        chLast6.getDescription().setEnabled(false);
        chLast6.setData(barData);
        chLast6.animateY(500);
        chLast6.setPinchZoom(false);
        chLast6.setScaleEnabled(false);
        chLast6.setHighlightPerTapEnabled(true);

        chLast6.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                String m = months.get((int) value);

                return getContext().getString(getContext().getResources().getIdentifier("month_sm_" + m, "string", getContext().getPackageName()));
            }
        });

        chLast6.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if(e.getY() > 0) {
                    chart_info.setText(Utils.currency.formatPrice(e.getY()));
                    chart_info.show();
                }
            }

            @Override
            public void onNothingSelected() {
                if(chart_info != null) chart_info.dismiss();
            }
        });
    }


    private void drawChartReceipt(ArrayList<Entry> entries){
        LineDataSet setDataReceipt = null;
        LineData dataReceipt = null;

        Collections.sort(entries, new EntryXComparator());

        setDataReceipt = new LineDataSet(entries, "");
        setDataReceipt.setColors(ColorTemplate.COLORFUL_COLORS);
        setDataReceipt.setDrawValues(true);

        setDataReceipt.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });

        dataReceipt = new LineData(setDataReceipt);
        chReceipt.setData(dataReceipt);

        chReceipt.animateY(500);
        chReceipt.setPinchZoom(true);
        chReceipt.setScaleEnabled(true);
        chReceipt.setHighlightPerTapEnabled(true);
        chReceipt.getDescription().setEnabled(false);

        chReceipt.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                long t = Math.round(value) * 1000L;

                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(t);

                return c.get(Calendar.DAY_OF_MONTH) + " " + getContext().getString(getContext().getResources().getIdentifier("month_sm_" + (c.get(Calendar.MONTH) + 1), "string", getContext().getPackageName()));
            }
        });

        chReceipt.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if (e.getY() > 0) {
                    long t = Math.round(e.getX()) * 1000L;
                    Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(t);

                    String time = c.get(Calendar.DAY_OF_MONTH) + " " + getContext().getString(getContext().getResources().getIdentifier("month_sm_" + (c.get(Calendar.MONTH) + 1), "string", getContext().getPackageName()));

                    chart_info.setText(time + " --- " + Utils.currency.formatPrice(e.getY()));
                    chart_info.show();
                }
            }

            @Override
            public void onNothingSelected() {
                if (chart_info != null) chart_info.dismiss();
            }
        });

        chReceipt.notifyDataSetChanged();
        chReceipt.invalidate();
    }
}
