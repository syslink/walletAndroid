package com.alphawallet.app.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alphawallet.app.R;
import com.alphawallet.app.entity.ActivityMeta;
import com.alphawallet.app.entity.ContractLocator;
import com.alphawallet.app.entity.EventMeta;
import com.alphawallet.app.entity.TransactionMeta;
import com.alphawallet.app.entity.Wallet;
import com.alphawallet.app.repository.entity.RealmAuxData;
import com.alphawallet.app.repository.entity.RealmTransaction;
import com.alphawallet.app.viewmodel.ActivityViewModel;
import com.alphawallet.app.viewmodel.ActivityViewModelFactory;
import com.alphawallet.app.viewmodel.DealPageInfo;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import dagger.android.support.AndroidSupportInjection;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.Request;
import okhttp3.Response;

import static com.alphawallet.app.repository.TokensRealmSource.EVENT_CARDS;

/**
 * Created by JB on 26/06/2020.
 */
public class ActivityFragment extends BaseFragment implements View.OnClickListener {
    @Inject
    ActivityViewModelFactory activityViewModelFactory;
    private static final String TAG = "ActivityFragment";
    private RelativeLayout mRootDeal;
    private RelativeLayout mRootNoActivities;
    private ImageView mLeftIcon;
    private TextView mLeftTextView;
    private ImageView mRightIcon;
    private TextView mRightTextView;
    private ActivityViewModel viewModel;
    private Realm realm;
    private long eventTimeFilter;
    private String realmId;
    private RealmResults<RealmTransaction> realmUpdates;
    private RealmResults<RealmAuxData> auxRealmUpdates;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        AndroidSupportInjection.inject(this);
        View view = inflater.inflate(R.layout.fragment_transactions, container, false);
        toolbar(view);
        setToolbarTitle(R.string.activity_label);
        initDealList();
        initViewModel();
        initViews(view);
        return view;
    }

    @SuppressLint("CheckResult")
    private void initDealList() {
        Observable.create(new ObservableOnSubscribe<Response>() {
            @Override
            public void subscribe(ObservableEmitter<Response> emitter) throws Exception {
                Request request = new Request.Builder()
                        .url("https://doulaig.oss-cn-hangzhou.aliyuncs.com/wallet/swapableTokenList.json")
                        .build();
                emitter.onNext(mOkHttpClient.newCall(request).execute());
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Response>() {
                    @Override
                    public void accept(Response response) throws Exception {
                        String string = response.body().string();
                        DealPageInfo dealPageInfo = JSONObject.parseObject(string, DealPageInfo.class);

                    }
                });
    }

    private void initViewModel() {
        if (viewModel == null) {
            viewModel = new ViewModelProvider(this, activityViewModelFactory)
                    .get(ActivityViewModel.class);
            viewModel.defaultWallet().observe(getViewLifecycleOwner(), this::onDefaultWallet);
            viewModel.activityItems().observe(getViewLifecycleOwner(), this::onItemsLoaded);
        }
    }

    private void onDefaultWallet(Wallet wallet) {
        Log.d(TAG, "onDefaultWallet: ");
        //adapter.setDefaultWallet(wallet);
    }

    private void onItemsLoaded(ActivityMeta[] activityItems) {
        Log.d(TAG, "onItemsLoaded: ");
        realm = viewModel.getRealmInstance();
        //adapter.updateActivityItems(buildTransactionList(activityItems).toArray(new ActivityMeta[0]));
        //showEmptyTx();
        long lastUpdateTime = 0;

        for (ActivityMeta am : activityItems)
        {
            if (am instanceof TransactionMeta && am.getTimeStampSeconds() > lastUpdateTime) lastUpdateTime = am.getTimeStampSeconds();
        }

        startTxListener(lastUpdateTime - 60*10); //adjust for timestamp delay
    }

    private void startTxListener(long lastUpdateTime)
    {
        String walletAddress = viewModel.defaultWallet().getValue() != null ? viewModel.defaultWallet().getValue().address : "";
        eventTimeFilter = lastUpdateTime;
        if (realmId == null || !realmId.equalsIgnoreCase(walletAddress))
        {
            if (realmUpdates != null) realmUpdates.removeAllChangeListeners();

            realmId = walletAddress;
            realmUpdates = realm.where(RealmTransaction.class).greaterThan("timeStamp", lastUpdateTime).findAllAsync();
            realmUpdates.addChangeListener(realmTransactions -> {
                Log.d(TAG, "startTxListener: ");
                List<TransactionMeta> metas = new ArrayList<>();
                //make list
                if (realmTransactions.size() == 0) return;
                for (RealmTransaction item : realmTransactions)
                {
                    if (viewModel.getTokensService().getNetworkFilters().contains(item.getChainId()))
                    {
                        TransactionMeta newMeta = new TransactionMeta(item.getHash(), item.getTimeStamp(), item.getTo(), item.getChainId(), item.getBlockNumber());
                        metas.add(newMeta);
                    }
                }

                if (metas.size() > 0)
                {
                    TransactionMeta[] metaArray = metas.toArray(new TransactionMeta[0]);
                    //adapter.updateActivityItems(buildTransactionList(metaArray).toArray(new ActivityMeta[0]));
                    //systemView.hide();
                }
            });

            auxRealmUpdates = realm.where(RealmAuxData.class)
                    .endsWith("instanceKey", EVENT_CARDS)
                    .greaterThan("resultReceivedTime", lastUpdateTime)
                    .findAllAsync();
            auxRealmUpdates.addChangeListener(realmEvents -> {
                List<ActivityMeta> metas = new ArrayList<>();
                if (realmEvents.size() == 0) return;
                for (RealmAuxData item : realmEvents)
                {
                    if (item.getResultReceivedTime() >= eventTimeFilter && viewModel.getTokensService().getNetworkFilters().contains(item.getChainId()))
                    {
                        EventMeta newMeta = new EventMeta(item.getTransactionHash(), item.getEventName(), item.getFunctionId(), item.getResultTime(), item.getChainId());
                        metas.add(newMeta);
                    }
                }

                eventTimeFilter = System.currentTimeMillis() - DateUtils.SECOND_IN_MILLIS; // allow for async; may receive many event updates

                if (metas.size() > 0)
                {
                    //adapter.updateActivityItems(metas.toArray(new ActivityMeta[0]));
                    //systemView.hide();
                }
            });
        }
    }

    private void initViews(View view) {
        mRootDeal = view.findViewById(R.id.rl_root_deal);
        mRootNoActivities = view.findViewById(R.id.rl_root_no_activities);
        view.findViewById(R.id.ll_deal_left).setOnClickListener(this);
        view.findViewById(R.id.ll_deal_right).setOnClickListener(this);
        mLeftIcon = view.findViewById(R.id.img_left_icon);
        mLeftTextView = view.findViewById(R.id.tv_left);
        mRightIcon = view.findViewById(R.id.img_right_icon);
        mRightTextView = view.findViewById(R.id.tv_right);
        ((TextView) view.findViewById(R.id.toolbar_title)).setText(getResources().getString(R.string.tx_label));
        ImageView exchangeImageView = view.findViewById(R.id.img_exchange);
        exchangeImageView.setOnClickListener(this);
        TextView leftOutNumber = view.findViewById(R.id.tv_left_number);
        TextView rightOutNumber = view.findViewById(R.id.tv_right_number);
        EditText leftEditText = view.findViewById(R.id.et_left);
        EditText rightEditText = view.findViewById(R.id.et_right);
        TextView exchangeRateText = view.findViewById(R.id.tv_exchange_rate);
        ImageView settingImage = view.findViewById(R.id.img_setting);
        settingImage.setOnClickListener(this);
        Button exchangeBtn = view.findViewById(R.id.btn_exchange);
        exchangeBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_deal_left:
            case R.id.ll_deal_right:
                showCurrencyListDialog();
                break;
            case R.id.img_exchange:
                onClickExchangeImage();
                break;
            case R.id.img_setting:
                Log.d(TAG, "onClick: setting");
                break;
            case R.id.btn_exchange:
                Log.d(TAG, "onClick: exchange");
                break;
        }
    }

    private void onClickExchangeImage() {
        //mLeftIcon.get
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
