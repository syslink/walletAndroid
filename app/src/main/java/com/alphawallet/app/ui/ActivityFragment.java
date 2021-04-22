package com.alphawallet.app.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.alphawallet.app.R;
import com.alphawallet.app.entity.ContractLocator;
import com.alphawallet.app.viewmodel.ActivityViewModelFactory;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import dagger.android.support.AndroidSupportInjection;

/**
 * Created by JB on 26/06/2020.
 */
public class ActivityFragment extends BaseFragment implements View.OnClickListener {
    @Inject
    ActivityViewModelFactory activityViewModelFactory;
    private static final String TAG = "ActivityFragment";
    private RelativeLayout mRootDeal;
    private RelativeLayout mRootNoActivities;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        AndroidSupportInjection.inject(this);
        View view = inflater.inflate(R.layout.fragment_transactions, container, false);
        toolbar(view);
        setToolbarTitle(R.string.activity_label);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        mRootDeal = view.findViewById(R.id.rl_root_deal);
        mRootNoActivities = view.findViewById(R.id.rl_root_no_activities);
        view.findViewById(R.id.ll_deal_left).setOnClickListener(this);
        view.findViewById(R.id.ll_deal_right).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_deal_left:
            case R.id.ll_deal_right:
                showCurrencyListDialog();
                break;

        }
    }

    private void showCurrencyListDialog() {
        PickDialog dialog = new PickDialog();
        dialog.showNow(getActivity().getSupportFragmentManager(), "test");
    }

    public void resetTokens() {

    }

    public void addedToken(List<ContractLocator> tokenContracts) {

    }

    public void resetTransactions() {

    }
}
