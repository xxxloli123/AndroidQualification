package com.dgg.qualification.ui.topic.activity;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dgg.baselibrary.loading.ILoadingHelper;
import com.dgg.baselibrary.network.BaseJson;
import com.dgg.baselibrary.network.DefaultSubscriber;
import com.dgg.baselibrary.tools.CommonUtils;
import com.dgg.baselibrary.tools.SharedPreferencesUtils;
import com.dgg.baselibrary.widget.nestgridview.FakerGridView;
import com.dgg.baselibrary.widget.nestgridview.VGUtil;
import com.dgg.baselibrary.widget.nestgridview.ViewHolder;
import com.dgg.baselibrary.widget.nestgridview.adapter.SingleAdapter;
import com.dgg.baselibrary.widget.sortlist.CharacterParser;
import com.dgg.baselibrary.widget.sortlist.PinyinComparator;
import com.dgg.baselibrary.widget.sortlist.SideBar;
import com.dgg.baselibrary.widget.sortlist.SortAdapter;
import com.dgg.baselibrary.widget.sortlist.SortModel;
import com.dgg.hdforeman.base.KtBaseActivity;
import com.dgg.qualification.R;
import com.dgg.qualification.common.Api;
import com.dgg.qualification.ui.topic.been.Area;
import com.dgg.qualification.ui.topic.server.TopicServer;

import org.simple.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by qiqi on 17/7/5.
 */

public class ReginChooseActivity extends KtBaseActivity implements View.OnClickListener {

    public static final String EVENT_SELECT_AREA = "event_select_area";
    public static final String AREA_ID = "areaId";//储存地区id key

    private ArrayList<Area> hotDatas = new ArrayList<>();
    private FakerGridView layout_gridview;
    private SideBar sidebar;
    private ListView ll_list;
    private CharacterParser characterParser;
    private SortAdapter adapter;
    private TextView tv_dialog, locationText;
    private LinearLayout location, root;
    private ImageView locationAction;
    private Area loctionArea;
    private ILoadingHelper loading;

    @Override
    protected int contentLayoutId() {
        return R.layout.activity_regin_choose;
    }

    @Override
    protected void initData() {
        initTitle("地区");
        layout_gridview = (FakerGridView) findViewById(R.id.layout_gridview);
        sidebar = (SideBar) findViewById(R.id.sidebar);
        ll_list = (ListView) findViewById(R.id.ll_list);
        tv_dialog = (TextView) findViewById(R.id.tv_dialog);
        locationText = (TextView) findViewById(R.id.locationText);
        location = (LinearLayout) findViewById(R.id.location);
        locationAction = (ImageView) findViewById(R.id.locationAction);
        location.setOnClickListener(this);
        location.setClickable(false);
        CommonUtils.setOnTouch(location);
        root = (LinearLayout) findViewById(R.id.root);
        loading = initLoading(root);
        loading.showLoading();
        getData();
    }

    private void getData() {
        TopicServer.getRegin(this, CommonUtils.encryptDES(Api.getCommonData()))
                .subscribe(new DefaultSubscriber<BaseJson<ArrayList<Area>>>(loading) {
                    @Override
                    public void onNext(BaseJson<ArrayList<Area>> baseJson) {
                        initHotArea(baseJson.data);
                        loading.restore();
                    }
                });
    }

    private void initHotArea(ArrayList<Area> data) {
        String BDLocation = (String) SharedPreferencesUtils.getParam(ReginChooseActivity.this, "BDLocation", "");
        for (Area ele : data) {
            if (ele.isHot == 2)
                hotDatas.add(ele);
            if (BDLocation.contains(ele.name.substring(0, 1))) {
                setLocationInfo(ele);
            }
        }
        new VGUtil(layout_gridview, new FollowupImgAttachmentsAdapter(ReginChooseActivity.this, hotDatas, R.layout.item_hot_area)).bind();
        initSortList(data);
    }

    /*设置定位匹配信息*/
    private void setLocationInfo(Area ele) {
        locationText.setText(ele.name);
        loctionArea = ele;
        location.setClickable(true);
        locationAction.setVisibility(View.VISIBLE);
    }

    /*设置 快速检索列表*/
    private void initSortList(ArrayList<Area> data) {
        characterParser = CharacterParser.getInstance();
        PinyinComparator pinyinComparator = new PinyinComparator();
        sidebar.setTextView(tv_dialog);//中间提示dialog
        // 设置右侧触摸监听
        sidebar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                // 该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    ll_list.setSelection(position);
                }
            }
        });
        ll_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO item 的点击监听
                // 这里要利用adapter.getItem(position)来获取当前position所对应的对象
                SortModel itemData = (SortModel) adapter.getItem(position);
                Area area = new Area();
                area.id = itemData.id;
                area.name = itemData.name;
                selcetAreaAction(area);
            }
        });

        List<SortModel> SourceDateList = filledData(data);
        // 根据a-z进行排序源数据
        Collections.sort(SourceDateList, pinyinComparator);
        adapter = new SortAdapter(this, SourceDateList);
        ll_list.setAdapter(adapter);
    }

    /**
     * 为ListView填充数据
     *
     * @param date
     * @return
     */
    private List<SortModel> filledData(ArrayList<Area> date) {
        List<SortModel> mSortList = new ArrayList<SortModel>();

        for (int i = 0; i < date.size(); i++) {
            SortModel sortModel = new SortModel();
            sortModel.name = (date.get(i).name);
            sortModel.id = date.get(i).id;
            // 汉字转换成拼音
            String pinyin = characterParser.getSelling(date.get(i).name);
            String sortString = pinyin.substring(0, 1).toUpperCase();
            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                sortModel.sortLetters = (sortString.toUpperCase());
            } else {
                sortModel.sortLetters = ("#");
            }
            mSortList.add(sortModel);
        }
        return mSortList;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.location:
                selcetAreaAction(loctionArea);
                break;
        }
    }

    /*选择地区过后的操作*/
    private void selcetAreaAction(Area data) {
        EventBus.getDefault().post(data, EVENT_SELECT_AREA);
        onBackPressed();
    }

    @Override
    public void reLoadingData() {
        loading.showLoading();
        getData();
    }

    public class FollowupImgAttachmentsAdapter extends SingleAdapter<Area> {

        public FollowupImgAttachmentsAdapter(Context context, List<Area> datas, int itemLayoutId) {
            super(context, datas, itemLayoutId);
        }

        @Override
        public void onBindViewHolder(ViewGroup parent, ViewHolder holder, final Area data, final int pos) {
            TextView tt = holder.getView(R.id.content);
            tt.setText(data.name);
//            CommonUtils.setOnTouch(tt);
            tt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selcetAreaAction(data);
                }
            });
        }
    }
}
