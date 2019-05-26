/*
 * Created by Dominik Szaradowski on 23.05.19 10:44
 * Copyright (c) 2019 . All rights reserved.
 * Website: https://www.szaradowski.pl
 */

package pl.szaradowski.mycart.fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.text.HtmlCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;

import pl.szaradowski.mycart.R;
import pl.szaradowski.mycart.common.Receipt;
import pl.szaradowski.mycart.common.Utils;
import pl.szaradowski.mycart.components.RichButton;
import pl.szaradowski.mycart.components.RichTextView;

public class ChartsFragment extends Fragment {
    RichButton btnFrom, btnTo;
    Calendar from, to;
    RichTextView tvSum, tvSumTitle;
    HorizontalBarChart chLast6;
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
        rootView = view.findViewById(R.id.rootView);
        chLast6 = view.findViewById(R.id.chLast6);

        chart_info = Snackbar.make(rootView, "", Snackbar.LENGTH_SHORT);

        from = Calendar.getInstance();
        //from.add(Calendar.DATE, -30);
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

                        btnTo.setText(date);
                        refresh();
                    }
                }, to.get(Calendar.YEAR), to.get(Calendar.MONTH), to.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.show();
            }
        });

        refresh();
    }

    private String formatDate(int year, int month, int dayOfMonth){
        return dayOfMonth + " " + getContext().getString(getContext().getResources().getIdentifier("month_sm_" + (month + 1), "string", getContext().getPackageName())) + " " + year;
    }

    private boolean isInRange(long time){
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);

        return (c.after(from) && c.before(to)) || c.equals(from) || c.equals(to);
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
        ArrayList<BarEntry> barEntries_6 = new ArrayList<>();
        ArrayList<String> barEntries_6_months = new ArrayList<>();

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
            }

            if(isTimePrevMonth(r.getTime(), 5, barEntries_6_months)) sum_m0 += price_all;
            if(isTimePrevMonth(r.getTime(), 4, barEntries_6_months)) sum_m1 += price_all;
            if(isTimePrevMonth(r.getTime(), 3, barEntries_6_months)) sum_m2 += price_all;
            if(isTimePrevMonth(r.getTime(), 2, barEntries_6_months)) sum_m3 += price_all;
            if(isTimePrevMonth(r.getTime(), 1, barEntries_6_months)) sum_m4 += price_all;
            if(isTimePrevMonth(r.getTime(), 0, barEntries_6_months)) sum_m5 += price_all;
        }

        barEntries_6.add(new BarEntry(0, sum_m0));
        barEntries_6.add(new BarEntry(1, sum_m1));
        barEntries_6.add(new BarEntry(2, sum_m2));
        barEntries_6.add(new BarEntry(3, sum_m3));
        barEntries_6.add(new BarEntry(4, sum_m4));
        barEntries_6.add(new BarEntry(5, sum_m5));

        drawChart6(barEntries_6, barEntries_6_months);

        tvSumTitle.setText(
                HtmlCompat.fromHtml(
                        getString(R.string.charts_title_sum, "<b>"+btnFrom.getText().toString()+"</b>", "<b>"+btnTo.getText().toString()+"</b>")
                , HtmlCompat.FROM_HTML_MODE_LEGACY)
        );

        tvSum.setText(String.format(Utils.locale, "%.2f", sum) + " " + Utils.currency);

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
                    chart_info.setText(String.format(Utils.locale, "%.2f", e.getY()) + " " + Utils.currency);
                    chart_info.show();
                }
            }

            @Override
            public void onNothingSelected() {
                if(chart_info != null) chart_info.dismiss();
            }
        });
    }
}
