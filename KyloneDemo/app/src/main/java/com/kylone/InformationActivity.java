package com.kylone;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kylone.adapter.CommonAdapter;
import com.kylone.base.BaseActivity;
import com.kylone.biz.CommonInfo;
import com.kylone.player.R;
import com.kylone.utils.ScreenParameter;
import com.kylone.view.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zack on 2018/5/24
 */

public class InformationActivity extends BaseActivity {
    private RecyclerView rv;
    private CommonAdapter adapter;
    private ImageView img;
    private TextView introduce;

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
                outRect.left = ScreenParameter.getFitWidth(7);
                outRect.right = ScreenParameter.getFitWidth(7);
                outRect.top = ScreenParameter.getFitWidth(7);
                outRect.bottom = ScreenParameter.getFitWidth(7);
            }
        });
        GridLayoutManager layoutManage = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        rv.setLayoutManager(layoutManage);
        adapter = new CommonAdapter(R.layout.item_information, rv);
        rv.setAdapter(adapter);


    }

    private void initData() {
        Glide.with(this).fromString().load("http://static.asiawebdirect.com/m/kl/portals/maldives-resorts-net/homepage/pagePropertiesImage/maldives-hotels.jpg").into(img);

        introduce.setText(R.string.maldives);


        List<CommonInfo> testData = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            CommonInfo test = new CommonInfo();
            test.setTitle("food");
            switch (i) {
                case 0:
                    test.setImage(R.mipmap.information_1 + "");
                    break;
                case 1:
                    test.setImage(R.mipmap.information_2 + "");
                    break;
                case 2:
                    test.setImage(R.mipmap.information_3 + "");
                    break;
                case 3:
                    test.setImage(R.mipmap.information_4 + "");
                    break;
                case 4:
                    test.setImage(R.mipmap.information_5 + "");
                    break;
                case 5:
                    test.setImage(R.mipmap.information_6 + "");
                    break;
            }
            test.setAction("kylone.intent.action.Web");
            test.setValue("url", "http://www.baidu.com");
            testData.add(test);
        }
        adapter.setData(testData);
    }
}
