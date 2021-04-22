package com.alphawallet.app.ui;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.alphawallet.app.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PickDialog extends DialogFragment implements View.OnClickListener{
    private LinearLayoutManager mRecyclerViewLayoutManager;

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_tracking_picker, container, false);
        initViews(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        window.setWindowAnimations(R.style.CommonDialogAnimStyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        DisplayMetrics dm = new DisplayMetrics();
        window.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        wl.x = width;
        wl.y = height;
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = height - 50;
        getDialog().onWindowAttributesChanged(wl);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogFullScreen);
    }

    public void initViews(View view) {
        RecyclerView commandRecycleView = view.findViewById(R.id.rv_command);
        RecyclerView listRecycleView = view.findViewById(R.id.rv_list);
        view.findViewById(R.id.tv_cancel).setOnClickListener(this);
        mRecyclerViewLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerViewLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        commandRecycleView.setLayoutManager(mRecyclerViewLayoutManager);
        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(getContext());
        verticalLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listRecycleView.setLayoutManager(verticalLayoutManager);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add("ETH");
        }

        MyAdapter adapter = new MyAdapter(R.layout.item_layout_v,list);
        commandRecycleView.setAdapter(adapter);

        MyAdapter myAdapter = new MyAdapter(R.layout.item_layout, list);
        listRecycleView.addItemDecoration(new MyItemDecoration());
        listRecycleView.setAdapter(myAdapter);

    }

    @Override
    public void onClick(View v) {
        dismiss();
    }

    public class MyAdapter extends BaseQuickAdapter<String,BaseViewHolder>{


        public MyAdapter(int layoutResId, @org.jetbrains.annotations.Nullable List<String> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(@NotNull BaseViewHolder helper, String s) {
        }
    }

    class MyItemDecoration extends RecyclerView.ItemDecoration {
        /**
         *
         * @param outRect 边界
         * @param view recyclerView ItemView
         * @param parent recyclerView
         * @param state recycler 内部数据管理
         */
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            //设定底部边距为1px
            outRect.set(30, 0, 30, 1);
        }
    }
}
