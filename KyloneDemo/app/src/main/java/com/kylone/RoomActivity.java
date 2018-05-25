package com.kylone;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.kylone.adapter.CommonAdapter;
import com.kylone.base.BaseActivity;
import com.kylone.biz.CommonInfo;
import com.kylone.player.R;
import com.kylone.utils.ScreenParameter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zack on 2018/5/24
 */

public class RoomActivity extends BaseActivity {
    RecyclerView rv;
    CommonAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_server);
        initView();
        initData();
    }

    private void initView() {
        rv = (RecyclerView) findViewById(R.id.room_items);


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
        GridLayoutManager layoutManage = new GridLayoutManager(this, 5, GridLayoutManager.VERTICAL, false);
        rv.setLayoutManager(layoutManage);
        adapter = new CommonAdapter(R.layout.item_room, rv);
        rv.setAdapter(adapter);

    }

    private void initData() {
        List<CommonInfo> testData = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            CommonInfo test = new CommonInfo();
            test.setTitle("food");
            test.setImage("https://ss0.bdstatic.com/94oJfD_bAAcT8t7mm9GUKT-xh_/timg?image&quality=100&size=b4000_4000&sec=1527094076&di=7afba2a83c1179fb108ceda397de67db&src=http://img.juimg.com/tuku/yulantu/121020/240425-12102020030650.jpg");
            testData.add(test);
        }
        adapter.setData(testData);
    }

}
