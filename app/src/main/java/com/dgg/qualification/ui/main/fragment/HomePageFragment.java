package com.dgg.qualification.ui.main.fragment;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.bumptech.glide.Glide;
import com.dgg.baselibrary.KtBaseFragment;
import com.dgg.baselibrary.loading.ILoadingHelper;
import com.dgg.baselibrary.network.DefaultSubscriber;
import com.dgg.baselibrary.tools.CommonUtils;
import com.dgg.baselibrary.tools.LogUtils;
import com.dgg.baselibrary.tools.SharedPreferencesUtils;
import com.dgg.baselibrary.tools.ToastUtils;
import com.dgg.baselibrary.widget.nestgridview.FakerGridView;
import com.dgg.baselibrary.widget.nestgridview.VGUtil;
import com.dgg.baselibrary.widget.refresh.interfaces.OnTouchUpListener;
import com.dgg.baselibrary.widget.refresh.layout.SWPullRecyclerLayout;
import com.dgg.qualification.BuildConfig;
import com.dgg.qualification.R;
import com.dgg.baselibrary.network.BaseJson;
import com.dgg.qualification.common.Api;
import com.dgg.qualification.common.CustomLinearLayoutManager;
import com.dgg.qualification.common.UmengAnalytics;
import com.dgg.qualification.ui.main.adapter.DataAdapter;
import com.dgg.qualification.ui.main.adapter.HomeMenuAdapter;
import com.dgg.qualification.ui.main.adapter.NumAdapter;
import com.dgg.qualification.ui.main.contract.HomePageContract;
import com.dgg.qualification.ui.main.presenter.HomePagePresenter;
import com.dgg.qualification.ui.main.server.HomePage;
import com.dgg.qualification.ui.main.server.HomePageItem;
import com.dgg.qualification.ui.main.server.ManPageServer;
import com.dgg.qualification.ui.mine.MessageActivity;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

/*z
 * Created by qiqi on 17/7/4.
 */

public class HomePageFragment extends KtBaseFragment implements HomePageContract.View {
    ArrayList<String> imgs = new ArrayList<>();//banner 图片地址
    ArrayList<String> imgPaths = new ArrayList<>();//banner 调转地址
    private ConvenientBanner convenientBanner;
    private FakerGridView layout_gridview;
    private ImageView message;
    private View view;
    private ILoadingHelper loading;
    private HomePagePresenter presenter;
    private String cache;
    private LRecyclerView list;
    private DataAdapter mDataAdapter;
    private LRecyclerViewAdapter mLRecyclerViewAdapter;

    @NotNull
    @Override
    protected View initView() {
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_home_page, null, false);
        convenientBanner = (ConvenientBanner) view.findViewById(R.id.banner);
        layout_gridview = (FakerGridView) view.findViewById(R.id.layout_gridview);
        message = (ImageView) view.findViewById(R.id.message);
        list = (LRecyclerView) view.findViewById(R.id.list);
        return view;
    }

    @Override
    protected void initData() {
//        Log.d("appLog", "软件获得的状态：" + BuildConfig.DEBUG);
        presenter = new HomePagePresenter(this);
        mDataAdapter = new DataAdapter(getContext());
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(mDataAdapter);
        list.setAdapter(mLRecyclerViewAdapter);
        LinearLayoutManager layoutManager = new CustomLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        layoutManager.setSmoothScrollbarEnabled(true);
        layoutManager.setAutoMeasureEnabled(true);
        list.setLayoutManager(layoutManager);
        list.setHasFixedSize(true);
        list.setRefreshProgressStyle(ProgressStyle.LineSpinFadeLoader);
        list.setLoadingMoreProgressStyle(ProgressStyle.BallSpinFadeLoader);
        list.setFooterViewColor(R.color.bule_tt, R.color.black_tt3, R.color.white);
        list.setFooterViewHint("拼命加载中", "----已经全部为你呈现了----", "网络不给力啊，点击再试一次吧");
        list.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.refreshingNewList();
            }
        });
        list.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                presenter.loadingMoreNewList();
            }
        });
        initCacheData();
        getPageData();
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(), MessageActivity.class));
                UmengAnalytics.umengSend(UmengAnalytics.home_message);
            }
        });
    }

    private void initCacheData() {
        loading = initLoading(list);
        cache = (String) SharedPreferencesUtils.getParam(getActivity(), "CacheData", "");
        if (TextUtils.isEmpty(cache)) {
            loading.showLoading();
            return;
        }
        HomePage data = CommonUtils.getGson().fromJson(cache, HomePage.class);
        initBanner(data.bannerList);
        initMenu(data.icoList);
        initNewList(data.contentList);
    }

    private void getPageData() {
        HashMap<String, Object> map = Api.getCommonData();
        map.put("page", 1);
        map.put("pageSize", 10);//              Common共同    encrypt加密
        ManPageServer.getAllData(getActivity(), CommonUtils.encryptDES(map))
                //subscribe订阅
                .subscribe(new DefaultSubscriber<BaseJson<HomePage>>(DefaultSubscriber.SILENCE) {
                    @Override
                    public void onNext(BaseJson<HomePage> baseJson) {
                        initBanner(baseJson.data.bannerList);
                        initMenu(baseJson.data.icoList);
                        initNewList(baseJson.data.contentList);
                        SharedPreferencesUtils.setParam(getActivity(), "CacheData", new Gson().toJson(baseJson.data));
                        loading.restore();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if (TextUtils.isEmpty(cache))
                            loading.showError();
                    }
                });
    }

    private void initBanner(ArrayList<HomePageItem> bannerList) {
        imgs.clear();
        imgPaths.clear();
        for (HomePageItem ele : bannerList) {
            imgs.add(ele.imgPath);
            imgPaths.add(ele.imgUrl);
        }
        //开始自动翻页
        convenientBanner.startTurning(3000);
//        convenientBanner.setPageTransformer(new AccordionTransformer());
        convenientBanner.setPages(
                new CBViewHolderCreator<NetworkImageHolderView>() {
                    @Override
                    public NetworkImageHolderView createHolder() {
                        return new NetworkImageHolderView();
                    }
                }, imgs)
                .setPageIndicator(new int[]{R.drawable.ic_page_indicator_theme, R.drawable.ic_page_indicator_theme_focus})
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT)
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        if (!TextUtils.isEmpty(imgPaths.get(position)))
                            CommonUtils.previewContent(getActivity(), imgPaths.get(position));
                        UmengAnalytics.umengSend(UmengAnalytics.home_banner);
                    }
                });
    }

    /*初始化菜单*/
    private void initMenu(ArrayList<HomePageItem> icoList) {
        new VGUtil(layout_gridview, new HomeMenuAdapter(getActivity(), icoList, R.layout.item_home_menu)).bind();

    }

    private void initNewList(ArrayList<HomePageItem> contentList) {
        mDataAdapter.setDataList(contentList);
    }

    @Override
    public void reLoadingData() {
        loading.showLoading();
        getPageData();
    }

    @Override
    public void refreshNewList(ArrayList<HomePageItem> items) {
        initNewList(items);
        list.refreshComplete(items.size());
    }

    @Override
    public Context getMyContext() {
        return getContext();
    }

    @Override
    public void showToast(String msg) {
        ToastUtils.showToast(getContext(), msg);
    }

    @Override
    public void loadMoreComplete() {
        list.setNoMore(true);
    }

    @Override
    public void loadListError() {
        list.refreshComplete(0);
    }

    public class NetworkImageHolderView implements Holder<String> {
        private ImageView imageView;

        @Override
        public View createView(Context context) {
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            return imageView;
        }

        @Override
        public void UpdateUI(Context context, int position, String data) {
            Glide.with(getActivity())
                    .load(data)
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.load_fail)
                    .into(imageView);
        }
    }
}
