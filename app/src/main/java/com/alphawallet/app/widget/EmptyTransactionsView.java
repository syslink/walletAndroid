package com.alphawallet.app.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.alphawallet.app.R;

import androidx.annotation.NonNull;

public class EmptyTransactionsView extends FrameLayout {

    public EmptyTransactionsView(@NonNull Context context, OnClickListener onClickListener) {
        super(context);

        LayoutInflater.from(getContext())
                .inflate(R.layout.layout_empty_transactions,this,true);
    }
}
