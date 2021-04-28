package com.alphawallet.app.ui;

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
import com.alphawallet.app.repository.SharedPreferenceRepository;
import com.alphawallet.app.repository.entity.RealmAuxData;
import com.alphawallet.app.repository.entity.RealmTransaction;
import com.alphawallet.app.viewmodel.ActivityViewModel;
import com.alphawallet.app.viewmodel.ActivityViewModelFactory;
import com.alphawallet.app.viewmodel.DealPageItemBean;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import org.jetbrains.annotations.NotNull;

import dagger.android.support.AndroidSupportInjection;
import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

import static com.alphawallet.app.repository.TokensRealmSource.EVENT_CARDS;

/**
 * Created by JB on 26/06/2020.
 */
public class ActivityFragment extends BaseFragment implements View.OnClickListener, Callback {
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
    public static final String BSC_TYPE = "56";
    public static final String BSC_ICON_BASE_URL = "https://exchange.pancakeswap.finance/images/coins/";
    public static final String HECO_ICON_BASE_URL = "https://mdex.com/token-icons/heco/";
    //56 -->bsc   128-->heco
    private String mNetworkFilter;
    private List<DealPageItemBean> mHecoHeaderList;
    private List<DealPageItemBean> mHecoComList;
    private List<DealPageItemBean> mBscHeaderList;
    private List<DealPageItemBean> mBscComList;

    private List<DealPageItemBean> mCurrentHeadList;
    private List<DealPageItemBean> mCurrentComList;
    private boolean mIsBsc;
    private TextView mExchangeRateText;

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

    private void setData() {
        //heco取HT和HUSD，bsc取BNB和BUSD
        String leftIconUrl;
        String rightIconUrl;
        DealPageItemBean leftInfoBean;
        DealPageItemBean rightInfoBean;
        if (mIsBsc) {
            leftInfoBean = mBscHeaderList.get(0);
            rightInfoBean = mBscHeaderList.get(1);
            leftIconUrl = BSC_ICON_BASE_URL + leftInfoBean.getAddress() + ".png";
            rightIconUrl = BSC_ICON_BASE_URL + rightInfoBean.getAddress() + ".png";
        } else {
            leftInfoBean = mHecoHeaderList.get(0);
            rightInfoBean = mHecoHeaderList.get(1);
            leftIconUrl = HECO_ICON_BASE_URL + leftInfoBean.getAddress() + ".png";
            rightIconUrl = HECO_ICON_BASE_URL + rightInfoBean.getAddress() + ".png";
        }
//        Log.d(TAG, "setData: leftIconUrl = " + leftIconUrl);
//        Log.d(TAG, "setData: rightIconUrl = " + rightIconUrl);

        Glide.with(getContext()).load(leftIconUrl)
                .apply(new RequestOptions().circleCrop())
                .apply(new RequestOptions().placeholder(R.drawable.ic_token_eth))
                .into(mLeftIcon);

        Glide.with(getContext()).load(rightIconUrl)
                .apply(new RequestOptions().circleCrop())
                .apply(new RequestOptions().placeholder(R.drawable.ic_token_eth))
                .into(mRightIcon);
        mLeftTextView.setText(leftInfoBean.getName().toUpperCase());
        mRightTextView.setText(rightInfoBean.getName().toUpperCase());

        //1 USDT = 0.00039018 ETH
        String rate = "0.00039018";//TODO  待计算
        String exchangeRate = "1 " + leftInfoBean.getName().toUpperCase() +
                " = " + rate + " " + rightInfoBean.getName().toUpperCase();
        mExchangeRateText.setText(exchangeRate);
    }

    private void initDealList() {
        Request request = new Request.Builder()
                .url("https://doulaig.oss-cn-hangzhou.aliyuncs.com/wallet/swapableTokenList.json")
                .build();
        mOkHttpClient.newCall(request).enqueue(this);
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

        for (ActivityMeta am : activityItems) {
            if (am instanceof TransactionMeta && am.getTimeStampSeconds() > lastUpdateTime)
                lastUpdateTime = am.getTimeStampSeconds();
        }

        startTxListener(lastUpdateTime - 60 * 10); //adjust for timestamp delay
    }

    private void startTxListener(long lastUpdateTime) {
        String walletAddress = viewModel.defaultWallet().getValue() != null ? viewModel.defaultWallet().getValue().address : "";
        eventTimeFilter = lastUpdateTime;
        if (realmId == null || !realmId.equalsIgnoreCase(walletAddress)) {
            if (realmUpdates != null) realmUpdates.removeAllChangeListeners();

            realmId = walletAddress;
            realmUpdates = realm.where(RealmTransaction.class).greaterThan("timeStamp", lastUpdateTime).findAllAsync();
            realmUpdates.addChangeListener(realmTransactions -> {
                Log.d(TAG, "startTxListener: ");
                List<TransactionMeta> metas = new ArrayList<>();
                //make list
                if (realmTransactions.size() == 0) return;
                for (RealmTransaction item : realmTransactions) {
                    if (viewModel.getTokensService().getNetworkFilters().contains(item.getChainId())) {
                        TransactionMeta newMeta = new TransactionMeta(item.getHash(), item.getTimeStamp(), item.getTo(), item.getChainId(), item.getBlockNumber());
                        metas.add(newMeta);
                    }
                }

                if (metas.size() > 0) {
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
                for (RealmAuxData item : realmEvents) {
                    if (item.getResultReceivedTime() >= eventTimeFilter && viewModel.getTokensService().getNetworkFilters().contains(item.getChainId())) {
                        EventMeta newMeta = new EventMeta(item.getTransactionHash(), item.getEventName(), item.getFunctionId(), item.getResultTime(), item.getChainId());
                        metas.add(newMeta);
                    }
                }

                eventTimeFilter = System.currentTimeMillis() - DateUtils.SECOND_IN_MILLIS; // allow for async; may receive many event updates

                if (metas.size() > 0) {
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
        mExchangeRateText = view.findViewById(R.id.tv_exchange_rate);
        ImageView settingImage = view.findViewById(R.id.img_setting);
        settingImage.setOnClickListener(this);
        Button exchangeBtn = view.findViewById(R.id.btn_exchange);
        exchangeBtn.setOnClickListener(this);

    }

    private void initData() {
        if (mNetworkFilter.equals("56")) {
            //bsc
            mCurrentHeadList = mBscHeaderList;
            mCurrentComList = mBscComList;
        } else {
            //heco
            mCurrentHeadList = mHecoHeaderList;
            mCurrentComList = mHecoComList;
        }
        setData();
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
        PickDialog dialog = new PickDialog(mCurrentHeadList, mCurrentComList, mNetworkFilter);
        dialog.showNow(getActivity().getSupportFragmentManager(), "test");
    }

    public void resetTokens() {

    }

    public void addedToken(List<ContractLocator> tokenContracts) {

    }

    public void resetTransactions() {

    }

    void onShow() {
        SharedPreferenceRepository repository = new SharedPreferenceRepository(getContext());
        mNetworkFilter = repository.getNetworkFilterList();
        mIsBsc = mNetworkFilter.equals(BSC_TYPE);
        initData();
    }

    @Override
    public void onFailure(@NotNull Call call, @NotNull IOException e) {

    }

    @Override
    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
        String string = response.body().string();
        JSONObject jsonObject = JSONObject.parseObject(string);
        JSONObject hecoObj = JSONObject.parseObject(jsonObject.getString("heco"));
        mHecoHeaderList = JSON.parseArray(hecoObj.getString("headList"), DealPageItemBean.class);
        mHecoComList = JSON.parseArray(hecoObj.getString("commonList"), DealPageItemBean.class);

        JSONObject bscObj = JSONObject.parseObject(jsonObject.getString("bsc"));
        mBscHeaderList = JSON.parseArray(bscObj.getString("headList"), DealPageItemBean.class);
        mBscComList = JSON.parseArray(bscObj.getString("commonList"), DealPageItemBean.class);

    }
}
