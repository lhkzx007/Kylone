package com.kylone;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kylone.adapter.CommonAdapter;
import com.kylone.base.BaseActivity;
import com.kylone.biz.CommonInfo;
import com.kylone.player.R;
import com.kylone.shcapi.shApiMain;
import com.kylone.utils.ApiUtils;
import com.kylone.utils.HandlerUtils;
import com.kylone.utils.LogUtil;
import com.kylone.base.Density;
import com.kylone.utils.ThreadManager;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by Zack on 2018/5/24
 */

public class InformationActivity extends BaseActivity {
    private RecyclerView rv;
    private CommonAdapter adapter;
    private ImageView img;
    private TextView introduce;
    private List<CommonInfo> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        initView();
        initData();
    }

    private void initView() {
        img = (ImageView) findViewById(R.id.information_img);
        introduce = (TextView) findViewById(R.id.information_introduce);
        rv = (RecyclerView) findViewById(R.id.information_infos);

        rv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                //从第二个条目开始，距离上方Item的距离
                outRect.left = Density.INSTANCE.dp2px(7);
                outRect.right = Density.INSTANCE.dp2px(7);
                outRect.top = Density.INSTANCE.dp2px(7);
                outRect.bottom = Density.INSTANCE.dp2px(7);
            }
        });
        GridLayoutManager layoutManage = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        rv.setLayoutManager(layoutManage);
        adapter = new CommonAdapter(R.layout.item_information, rv);
        rv.setAdapter(adapter);


    }

//    private void initData() {
//        if (introduce != null) {
//            String ptxt=getIntent().getStringExtra("ptxt");
//            introduce.setText(ptxt);
//        }
//
//
//        Glide.with(this).fromString().load("http://static.asiawebdirect.com/m/kl/portals/maldives-resorts-net/homepage/pagePropertiesImage/maldives-hotels.jpg").into(img);
//
//        List<CommonInfo> testData = new ArrayList<>();
//        for (int i = 0; i < 6; i++) {
//            CommonInfo test = new CommonInfo();
//            test.setTitle("food");
//            switch (i) {
//                case 0:
//                    test.setImage(R.mipmap.information_1 + "");
//                    break;
//                case 1:
//                    test.setImage(R.mipmap.information_2 + "");
//                    break;
//                case 2:
//                    test.setImage(R.mipmap.information_3 + "");
//                    break;
//                case 3:
//                    test.setImage(R.mipmap.information_4 + "");
//                    break;
//                case 4:
//                    test.setImage(R.mipmap.information_5 + "");
//                    break;
//                case 5:
//                    test.setImage(R.mipmap.information_6 + "");
//                    break;
//            }
//            test.setAction("kylone.intent.action.Web");
//            test.setValue("url", "http://www.baidu.com");
//            testData.add(test);
//        }
//        adapter.setData(testData);
//
//
//
//    }


    void initData() {
        if (introduce != null) {
            String ptxt = getIntent().getStringExtra("ptxt");
            introduce.setText(ptxt);
        }

        if (img!=null) {
            String prehdbg = ApiUtils.shApi.getConfigVal("premimg");
            Glide.with(getApplicationContext()).load(prehdbg).into(img);
        }

        ThreadManager.execute(new Runnable() {
            @Override
            public void run() {
                try {
//                    prehdbg
                    // cms.kylone.blue/jack/

                    items.clear();
                    if (ApiUtils.shApi.contentlist != null) {
                        shApiMain.ContentItem item = ApiUtils.shApi.contentlist.get("item", "info");
                        if (item == null || item.size() == 0) {
                            LogUtil.i("无数据");
                            return;
                        }
                        SparseArray<CommonInfo> sparseArray = new SparseArray<CommonInfo>();

                        Enumeration<String> keys = item.keys();
                        while (keys.hasMoreElements()) {
                            String key = keys.nextElement();
                            shApiMain.ContentAttribute value = item.get(key);
                            LogUtil.i(key + "  --  " + value);
                            CommonInfo contextInfo = new CommonInfo();
                            StringBuilder keyCommon = new StringBuilder();
                            StringBuilder valueCommon = new StringBuilder();
                            Enumeration<String> vKeys = value.keys();
                            int index = value.size();
                            int i = 0;
                            while (vKeys.hasMoreElements()) {
                                String vvkey = vKeys.nextElement();
                                String vvalue = value.get(vvkey);
                                if (TextUtils.equals("ord", vvkey)) {
                                    i = Integer.valueOf(vvalue);
                                    continue;
                                }

                                keyCommon.append(vvkey);
                                valueCommon.append(vvalue);


                                if (--index != 0) {
                                    keyCommon.append("|");
                                    valueCommon.append("|");
                                }
                                if (TextUtils.equals(vvkey, "logo")) {
                                    contextInfo.setImage(vvalue);
                                } else if (TextUtils.equals(vvkey, "txt")) {
                                    contextInfo.setTitle(vvalue);
                                    if (TextUtils.equals(vvalue, "Weather")) {
                                        contextInfo.setAction("kylone.intent.action.Weather");
                                    } else {
                                        contextInfo.setAction("kylone.intent.action.Web");

                                    }
                                }
//                                LogUtil.i(" --vvalue--  :" + vvalue);
                            }
                            LogUtil.i(" --zack--  :" + keyCommon.toString());
                            LogUtil.i(" --zack--  :" + valueCommon.toString());
                            contextInfo.setValue(keyCommon.toString(), valueCommon.toString());
                            sparseArray.append(i, contextInfo);
                        }
                        for (int j = 0; j < sparseArray.size(); j++) {
                            int i = sparseArray.keyAt(j);
                            items.add(sparseArray.get(i));
                        }

                        HandlerUtils.runUITask(new Runnable() {
                            @Override
                            public void run() {
                                adapter.setData(items);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }
}
