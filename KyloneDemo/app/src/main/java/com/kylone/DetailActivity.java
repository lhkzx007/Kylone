package com.kylone;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
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

/**
 * Created by Zack on 2018/5/22
 */

public class DetailActivity extends BaseActivity {
    private ImageView tv_img;
    private TextView tv_doc;
    private TextView btn_play;
    private RecyclerView ry_film;
    private CommonAdapter adapterFilm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        initView();
        initListener();

        initData();
    }

    private void initListener() {
        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DetailActivity.this, PlayActivity.class));
            }
        });
    }

    private void initView() {
        tv_img = (ImageView) findViewById(R.id.detail_img);
        tv_doc = (TextView) findViewById(R.id.detail_doc);
        btn_play = (TextView) findViewById(R.id.detail_play);
        ry_film = (RecyclerView) findViewById(R.id.detail_film);


        ry_film.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                if (parent.getChildAdapterPosition(view) > 0) {
                    outRect.left = ScreenParameter.getFitWidth(14);
                }
            }
        });
        ry_film.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapterFilm = new CommonAdapter(R.layout.item_detail_film, ry_film);
        ry_film.setAdapter(adapterFilm);
    }

    private void initData() {
        Glide.with(this).fromString().load("https://img3.doubanio.com/view/photo/m/public/p1910813120.webp").into(tv_img);
        tv_doc.setText("霸王别姬 (1993)\n\t\t\t\t\t1924年冬天，9岁的小豆子被作妓女的母亲切掉右手上那根畸形的指头后进入关家戏班学戏。戏班里只有师兄小石头同情关照小豆子。十年过去了，在关师父严厉和残酷的训导下，师兄二人演技很快提高，小豆子取艺名程蝶衣（张国荣饰），演旦角；小石头取艺名段小楼（张丰毅饰），演生角。俩人合演的《霸王别姬》誉满京城，师兄二人也红极一时。二人约定合演一辈子《霸王别姬》。");


        String films = "{\"item\":[{\"title\":\"头号玩家\",\"action\":\"kylone.intent.action.Detail\",\"image\":\"https://img1.doubanio.com/view/photo/m/public/p2516578307.webp\",\"key\":\"url|title\",\"value\":\"http://str.kylone.blue:4750/2000/0/base/stream.ts|头号玩家\"},{\"title\":\"湮灭\",\"action\":\"kylone.intent.action.Detail\",\"image\":\"https://img1.doubanio.com/view/photo/m/public/p2516914607.webp\",\"key\":\"url|title\",\"value\":\"http://str.kylone.blue:4750/2007/0/base/stream.ts|湮灭\"},{\"title\":\"起跑线\",\"action\":\"kylone.intent.action.Detail\",\"image\":\"https://img1.doubanio.com/view/photo/s_ratio_poster/public/p2517518428.webp\",\"key\":\"url|title\",\"value\":\"http://cms.kylone.blue/movie/1/doublesniper.mp4|起跑线\"},{\"title\":\"头号玩家\",\"action\":\"kylone.intent.action.Detail\",\"image\":\"https://img1.doubanio.com/view/photo/m/public/p2516578307.webp\",\"key\":\"url|title\",\"value\":\"http://str.kylone.blue:4750/2000/0/base/stream.ts|头号玩家\"},{\"title\":\"头号玩家\",\"action\":\"kylone.intent.action.Detail\",\"image\":\"https://img1.doubanio.com/view/photo/m/public/p2516578307.webp\",\"key\":\"url|title\",\"value\":\"http://str.kylone.blue:4750/2000/0/base/stream.ts|头号玩家\"},{\"title\":\"头号玩家\",\"action\":\"kylone.intent.action.Detail\",\"image\":\"https://img1.doubanio.com/view/photo/m/public/p2516578307.webp\",\"key\":\"url|title\",\"value\":\"http://str.kylone.blue:4750/2000/0/base/stream.ts|头号玩家\"},{\"title\":\"头号玩家\",\"action\":\"kylone.intent.action.Detail\",\"image\":\"https://img1.doubanio.com/view/photo/m/public/p2516578307.webp\",\"key\":\"url|title\",\"value\":\"http://str.kylone.blue:4750/2000/0/base/stream.ts|头号玩家\"},{\"title\":\"头号玩家\",\"action\":\"kylone.intent.action.Detail\",\"image\":\"https://img1.doubanio.com/view/photo/m/public/p2516578307.webp\",\"key\":\"url|title\",\"value\":\"http://str.kylone.blue:4750/2000/0/base/stream.ts|头号玩家\"},{\"title\":\"头号玩家\",\"action\":\"kylone.intent.action.Detail\",\"image\":\"https://img1.doubanio.com/view/photo/m/public/p2516578307.webp\",\"key\":\"url|title\",\"value\":\"http://str.kylone.blue:4750/2000/0/base/stream.ts|头号玩家\"},{\"title\":\"头号玩家\",\"action\":\"kylone.intent.action.Detail\",\"image\":\"https://img1.doubanio.com/view/photo/m/public/p2516578307.webp\",\"key\":\"url|title\",\"value\":\"http://str.kylone.blue:4750/2000/0/base/stream.ts|头号玩家\"},{\"title\":\"头号玩家\",\"action\":\"kylone.intent.action.Detail\",\"image\":\"https://img1.doubanio.com/view/photo/m/public/p2516578307.webp\",\"key\":\"url|title\",\"value\":\"http://str.kylone.blue:4750/2000/0/base/stream.ts|头号玩家\"}]}";
        adapterFilm.setData(CommonInfo.parseInfo(films));
    }
}
