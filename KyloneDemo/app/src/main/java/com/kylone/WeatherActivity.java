package com.kylone;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;

import com.kylone.adapter.CommonAdapter;
import com.kylone.adapter.WeatherAdapter;
import com.kylone.base.BaseActivity;
import com.kylone.biz.CommonInfo;
import com.kylone.player.R;
import com.kylone.shcapi.shApiMain;
import com.kylone.utils.ApiUtils;
import com.kylone.utils.HandlerUtils;
import com.kylone.utils.LogUtil;
import com.kylone.utils.ScreenParameter;
import com.kylone.utils.ThreadManager;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by Zack on 2018/5/29
 */

public class WeatherActivity extends BaseActivity {
    private RecyclerView rv;
    private WeatherAdapter adapter;
    private List<shApiMain.WeatherAttribute> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        initView();
        initData();
    }

    private void initView() {
        rv = (RecyclerView) findViewById(R.id.weather_rv);

        rv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                //从第二个条目开始，距离上方Item的距离
                outRect.left = ScreenParameter.getFitWidth(7);
                outRect.right = ScreenParameter.getFitWidth(7);
                outRect.top = ScreenParameter.getFitWidth(7);
                outRect.bottom = ScreenParameter.getFitWidth(7);
            }
        });
        GridLayoutManager layoutManage = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        rv.setLayoutManager(layoutManage);
        adapter = new WeatherAdapter(R.layout.item_weather, rv);
        rv.setAdapter(adapter);
    }

    private void initData() {
        ThreadManager.execute(new Runnable() {
            @Override
            public void run() {
                try {

                    final int fres = ApiUtils.shApi.fetchWeatherData();
                    LogUtil.i(" ----------------fres :  " + fres);
                    if (fres == ApiUtils.shApi.SHC_FAIL) {
                        return;
                    }

                    // cms.kylone.blue/jack/

                    items.clear();
                    if (ApiUtils.shApi.contentlist != null) {
                        shApiMain.ContentItem item = ApiUtils.shApi.contentlist.get("locs", "0");
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
//                            CommonInfo contextInfo = new CommonInfo();
                            String woe = value.get("woe");

                            if (!TextUtils.isEmpty(woe)) {
                                shApiMain.WeatherAttribute weather = ApiUtils.shApi.weatherlist.get(woe);
                                Enumeration<String> keys_ = weather.keys();
                                while (keys_.hasMoreElements()){
                                    String key_ =keys_.nextElement();
                                    LogUtil.i(key_+ " ||||||  "+weather.get(key_));
                                }
                                weather.put("logo",value.get("logo"));

                                items.add(weather);
                            }

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
