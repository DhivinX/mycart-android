/*
 * Created by Dominik Szaradowski on 23.05.19 10:36
 * Copyright (c) 2019 . All rights reserved.
 * Website: https://www.szaradowski.pl
 */

package pl.szaradowski.mycart.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import pl.szaradowski.mycart.R;
import pl.szaradowski.mycart.activities.MainActivity;
import pl.szaradowski.mycart.activities.ReceiptActivity;
import pl.szaradowski.mycart.adapters.ReceiptsAdapter;
import pl.szaradowski.mycart.common.Receipt;
import pl.szaradowski.mycart.common.Utils;
import pl.szaradowski.mycart.components.IconButton;
import pl.szaradowski.mycart.components.RichTextView;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class ReceiptsFragment extends Fragment {
    RecyclerView list;
    ReceiptsAdapter adapter;
    IconButton add;

    IconButton btnDate;
    Calendar date;

    RichTextView title;
    RichTextView tvSubtitle;

    ArrayList<Receipt> receipts = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_receipts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        date = Calendar.getInstance();

        list = view.findViewById(R.id.list);
        add = view.findViewById(R.id.add);
        btnDate = view.findViewById(R.id.btnDate);
        title = view.findViewById(R.id.title);
        tvSubtitle = view.findViewById(R.id.tvSubtitle);

        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ReceiptsAdapter(receipts, getContext());
        list.setAdapter(adapter);

        adapter.setFingerListener(new ReceiptsAdapter.FingerListener() {
            @Override
            public void onClick(int position) {
                Receipt item = receipts.get(position);

                Intent intent = new Intent(getActivity(), ReceiptActivity.class);
                intent.putExtra("id_receipt", item.getId());
                Objects.requireNonNull(getActivity()).startActivity(intent);
            }

            @Override
            public boolean onLongClick(int position) {
                return false;
            }
        });

        add.setOnClickListener(new IconButton.OnClickListener() {
            @Override
            public void onClick() {
                Intent intent = new Intent(getActivity(), ReceiptActivity.class);
                intent.putExtra("id_receipt", -1);
                Objects.requireNonNull(getActivity()).startActivity(intent);
            }
        });

        btnDate.setOnClickListener(new IconButton.OnClickListener() {
            @Override
            public void onClick() {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        date.set(year, month, dayOfMonth, 0, 0, 0);
                        date.set(Calendar.MILLISECOND, 0);

                        load();
                    }
                }, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_NEGATIVE) {
                            date = Calendar.getInstance();
                        }
                    }
                });

                datePickerDialog.show();
            }
        });

        ShowcaseConfig guide_config = new ShowcaseConfig();
        guide_config.setDelay(500);

        MaterialShowcaseSequence guide = new MaterialShowcaseSequence(getActivity(), "guide_"+System.currentTimeMillis());
        guide.setConfig(guide_config);

        guide.addSequenceItem(((MainActivity) getActivity()).appbar, "Witaj", "test", getString(R.string.guide_understand));
        //guide.start();
    }

    public void load(){
        if(adapter == null) return;
        scrollToPosition(0);

        tvSubtitle.setText(getString(R.string.receipt_sub, Utils.db.countAllReceipts()+""));

        receipts.clear();
        receipts.addAll(Utils.db.getReceiptsList());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        load();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        load();
    }

    public void scrollToPosition(final int position) {
        if (list != null) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    list.scrollToPosition(position);

                    if (position == 0) {
                        LinearLayoutManager layoutManager = (LinearLayoutManager) list.getLayoutManager();
                        layoutManager.scrollToPositionWithOffset(0, 0);
                    }
                }
            });
        }
    }
}
