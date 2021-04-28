package com.alphawallet.app.ui;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.alphawallet.app.R;
import com.alphawallet.app.viewmodel.DealPageItemBean;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PickDialog extends DialogFragment implements View.OnClickListener{
    private static final String TAG = "PickDialog";
    private LinearLayoutManager mRecyclerViewLayoutManager;
    private List<DealPageItemBean> mHeaderList;
    private List<DealPageItemBean> mComList;
    private String netWortFilter;

    public PickDialog(List<DealPageItemBean> headerList, List<DealPageItemBean> comList, String networkFilter) {
        mHeaderList = headerList;
        mComList = comList;
        this.netWortFilter = networkFilter;
    }

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
        RecyclerView headerRecycleView = view.findViewById(R.id.rv_header_list);
        RecyclerView commonRecycleView = view.findViewById(R.id.rv_common_list);
        view.findViewById(R.id.tv_cancel).setOnClickListener(this);
        mRecyclerViewLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerViewLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        headerRecycleView.setLayoutManager(mRecyclerViewLayoutManager);
        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(getContext());
        verticalLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        commonRecycleView.setLayoutManager(verticalLayoutManager);

        MyAdapter adapter = new MyAdapter(R.layout.item_layout_v,mHeaderList);
        headerRecycleView.setAdapter(adapter);

        MyAdapter myAdapter = new MyAdapter(R.layout.item_layout, mComList);
        commonRecycleView.setAdapter(myAdapter);

    }
    private String getNetWortFilter(){
        return this.netWortFilter;
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }

    public class MyAdapter extends BaseQuickAdapter<DealPageItemBean,BaseViewHolder>{


        public MyAdapter(int layoutResId, @org.jetbrains.annotations.Nullable List<DealPageItemBean> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(@NotNull BaseViewHolder helper, DealPageItemBean item) {
            helper.setText(R.id.tv_token_name, item.getName());
            ImageView view = helper.findView(R.id.src_token_icon);
            String url;
            if (getNetWortFilter().equals("56")) {
                //bsc
                url = ActivityFragment.BSC_ICON_BASE_URL + item.getAddress() + ".png";
            }else {
                //heco
                url = ActivityFragment.HECO_ICON_BASE_URL + item.getAddress() + ".png";
            }
//            Log.d(TAG, "convert: url = " + url);
            Glide.with(getContext()).load(url)
                    .apply(new RequestOptions().circleCrop())
                    .apply(new RequestOptions().placeholder(R.drawable.ic_token_eth))
                    .into(view);
        }

    }
}
