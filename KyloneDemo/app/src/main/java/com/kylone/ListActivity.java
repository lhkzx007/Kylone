package com.kylone;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.kylone.adapter.CommonAdapter;
import com.kylone.base.BaseActivity;
import com.kylone.biz.CommonInfo;
import com.kylone.player.R;
import com.kylone.shcapi.shApiMain;
import com.kylone.utils.ApiUtils;
import com.kylone.utils.Conver;
import com.kylone.utils.HandlerUtils;
import com.kylone.utils.LogUtil;
import com.kylone.utils.ScreenParameter;
import com.kylone.utils.ThreadManager;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Created by zack
 */

public class ListActivity extends BaseActivity {

    private RecyclerView itemR, contentR;
    private CommonAdapter adapterItem;
    private CommonAdapter adapterFilm;

    private ArrayList<CommonInfo> items = new ArrayList<>();
    private Hashtable<String, ArrayList<CommonInfo>> contents = new Hashtable<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        initView();
        initAdapter();
        initListener();
        test();
//        initDate();
    }

    private void initView() {
        itemR = (RecyclerView) findViewById(R.id.film_list_item);
        contentR = (RecyclerView) findViewById(R.id.film_list_content);
    }

    private void initAdapter() {
        itemR.setLayoutManager(new LinearLayoutManager(this));
        adapterItem = new CommonAdapter(R.layout.list_item, itemR);
        adapterItem.openSingleSelect();
        adapterItem.setSelected(0);
        itemR.setAdapter(adapterItem);


        contentR.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.left = ScreenParameter.getFitWidth(7);
                outRect.right = ScreenParameter.getFitWidth(7);
                outRect.top = ScreenParameter.getFitHeight(9);
                outRect.bottom = ScreenParameter.getFitHeight(9);
            }
        });
        contentR.setLayoutManager(new GridLayoutManager(this, 5));
        adapterFilm = new CommonAdapter(R.layout.item_film, contentR);
        contentR.setAdapter(adapterFilm);

    }

    void initDate() {
        ThreadManager.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    items.clear();
                    contents.clear();
                    if (ApiUtils.shApi.contentlist != null) {
                        shApiMain.ContentCategory movie = ApiUtils.shApi.contentlist.get("movie");
                        if (movie == null || movie.size() == 0) {
                            LogUtil.i("无数据");
                            HandlerUtils.runUITask(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ListActivity.this, "No data , Exit the page after 3 seconds. ", Toast.LENGTH_LONG).show();
                                    HandlerUtils.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            finish();

                                        }
                                    }, 3000);
                                }
                            });
                            return;
                        }
                        Enumeration<String> keys = movie.keys();
                        while (keys.hasMoreElements()) {
                            String key = keys.nextElement();
                            shApiMain.ContentItem value = movie.get(key);

                            CommonInfo infoItem = new CommonInfo();
                            infoItem.setTitle(Conver.conver(key));
                            if (TextUtils.equals("0", key)) {
                                items.add(0, infoItem);
                            } else {
                                items.add(infoItem);
                            }
                            LogUtil.i(key);


                            ArrayList<CommonInfo> contentInfos = new ArrayList<CommonInfo>();
                            Enumeration<String> vKeys = value.keys();

                            while (vKeys.hasMoreElements()) {
                                String vKey = vKeys.nextElement();
                                shApiMain.ContentAttribute vvalue = value.get(vKey);

                                CommonInfo contextInfo = new CommonInfo();
                                contextInfo.setTitle(vKey);
                                contextInfo.setAction("kylone.intent.action.Player");

//                                LogUtil.i("-" + vKey);


                                StringBuilder keyCommon = new StringBuilder();
                                StringBuilder valueCommon = new StringBuilder();
                                Enumeration<String> vvKeys = vvalue.keys();
                                int index = vvalue.size();
                                while (vvKeys.hasMoreElements()) {
                                    String vvkey = vvKeys.nextElement();
//                                    LogUtil.i("--" + vvkey);
                                    String vvvalue = vvalue.get(vvkey);
//                                    LogUtil.i("---" + vvvalue);
                                    keyCommon.append(vvkey);
                                    valueCommon.append(vvvalue);
//                                    LogUtil.i("--" + index);
                                    if (TextUtils.equals(vvkey, "logo")) {
                                        contextInfo.setImage(vvvalue);
                                    } else if (TextUtils.equals(vvkey, "src")) {
                                        keyCommon.append("|url");
                                        valueCommon.append("|").append(vvvalue);
                                    } else if (TextUtils.equals(vvkey, "txt")) {
                                        keyCommon.append("|title");
                                        valueCommon.append("|").append(vvvalue);
                                    }
                                    if (--index != 0) {
                                        keyCommon.append("|");
                                        valueCommon.append("|");
                                    }
                                }
                                LogUtil.i(" --zack--  :" + keyCommon.toString());
                                contextInfo.setValue(keyCommon.toString(), valueCommon.toString());
                                contentInfos.add(contextInfo);
                            }
                            contents.put(infoItem.getTitle(), contentInfos);
                        }

                        HandlerUtils.runUITask(new Runnable() {
                            @Override
                            public void run() {
                                adapterItem.setData(items);
                                if (items != null && items.size() > 0) {
                                    adapterFilm.setData(contents.get(items.get(0).getTitle()));
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void initListener() {
        adapterItem.setOnItemListener(new CommonAdapter.OnItemListener() {
            @Override
            public void onClick(RecyclerView.ViewHolder v, int position) {
                LogUtil.i("item-----" + position);
            }

            @Override
            public void onFocusChange(RecyclerView.ViewHolder v, int position, boolean hasFocus) {

            }

            @Override
            public void onSelectChange(RecyclerView.ViewHolder v, int position, boolean hasSelect) {

            }
        });
    }


    private void test() {

//        String a = "{\"item\":[{\"title\":\"ALL\"},{\"title\":\"电影\"}]}";
        String a = "{\"item\":[{\"title\":\"ALL\"},{\"title\":\"电影\"},{\"title\":\"电影\"},{\"title\":\"电影\"},{\"title\":\"电影\"},{\"title\":\"电影\"},{\"title\":\"电影\"},{\"title\":\"电影\"},{\"title\":\"电影\"},{\"title\":\"电影\"},{\"title\":\"电影\"},{\"title\":\"电影\"},{\"title\":\"电影\"},{\"title\":\"电影\"},{\"title\":\"电影\"},{\"title\":\"电影\"},{\"title\":\"电影\"},{\"title\":\"电影\"},{\"title\":\"电影\"},{\"title\":\"电影\"},{\"title\":\"电影\"},{\"title\":\"电影\"},{\"title\":\"电影\"},{\"title\":\"电影\"},{\"title\":\"电影\"},{\"title\":\"电影\"},{\"title\":\"电影\"},{\"title\":\"电影\"},{\"title\":\"电影\"},{\"title\":\"电影\"},{\"title\":\"电影\"},{\"title\":\"电影\"},{\"title\":\"电影\"},{\"title\":\"电影\"},{\"title\":\"电影\"},{\"title\":\"电影\"},{\"title\":\"电影\"},{\"title\":\"电影\"},{\"title\":\"电影\"},{\"title\":\"电影\"},{\"title\":\"电影\"},{\"title\":\"电影\"},{\"title\":\"电影\"},{\"title\":\"电影\"},{\"title\":\"电影\"},{\"title\":\"电影\"},{\"title\":\"电影\"},{\"title\":\"电影\"},{\"title\":\"电影\"},{\"title\":\"电影\"},{\"title\":\"电影\"},{\"title\":\"电影\"},{\"title\":\"电影\"},{\"title\":\"电影\"},{\"title\":\"电影\"},{\"title\":\"电影\"},{\"title\":\"电影\"},{\"title\":\"电影\"},{\"title\":\"电影\"},{\"title\":\"电影\"},{\"title\":\"电影\"}]}";
        adapterItem.setData(CommonInfo.parseInfo(a));
//        arge.putString("url","http://str.kylone.blue:4750/2000/0/base/stream.ts");
//        arge.putString("url","http://str.kylone.blue:4750/2007/0/base/stream.ts");

//        http://cms.kylone.blue/movie/1/doublesniper.mp4
//        String films = "{\n" +
//                "  \"item\": [\n" +
//                "    {\n" +
//                "      \"title\": \"头号玩家\",\n" +
//                "      \"action\": \"kylone.intent.action.Detail\",\n" +
//                "      \"image\": \"https://img1.doubanio.com/view/photo/m/public/p2516578307.webp\",\n" +
//                "\"key\":\"url|title\",\n" +
//                "\"value\":\"http://str.kylone.blue:4750/2000/0/base/stream.ts|头号玩家\"" +
//                "    },\n" +
//                "    {\n" +
//                "      \"title\": \"湮灭\",\n" +
//                "      \"action\": \"kylone.intent.action.Player\",\n" +
//                "      \"image\": \"https://img1.doubanio.com/view/photo/m/public/p2516914607.webp\",\n" +
//                "\"key\":\"url|title\",\n" +
//                "\"value\":\"http://str.kylone.blue:4750/2007/0/base/stream.ts|湮灭\"" +
//                "    },\n" +
//                "    {\n" +
//                "      \"title\": \"起跑线\",\n" +
//                "      \"action\": \"kylone.intent.action.Player\",\n" +
//                "      \"image\": \"https://img1.doubanio.com/view/photo/s_ratio_poster/public/p2517518428.webp\",\n" +
//                "\"key\":\"url|title\",\n" +
//                "\"value\":\"http://cms.kylone.blue/movie/1/doublesniper.mp4|起跑线\"" +
//                "    },\n" +
//                "    {\n" +
//                "      \"title\": \"环太平洋：雷霆再起\",\n" +
//                "      \"action\": \"kylone.intent.action.Player\",\n" +
//                "      \"image\": \"https://img3.doubanio.com/view/photo/s_ratio_poster/public/p2512933684.webp\"\n" +
//                "    },\n" +
//                "    {\n" +
//                "      \"title\": \"狂暴巨兽\",\n" +
//                "      \"action\": \"kylone.intent.action.Player\",\n" +
//                "      \"image\": \"https://img3.doubanio.com/view/photo/s_ratio_poster/public/p2516079193.webp\"\n" +
//                "    },\n" +
//                "    {\n" +
//                "      \"title\": \"唐人街探案2\",\n" +
//                "      \"action\": \"kylone.intent.action.Player\",\n" +
//                "      \"image\": \"https://img3.doubanio.com/view/photo/s_ratio_poster/public/p2511355624.webp\"\n" +
//                "    },\n" +
//                "    {\n" +
//                "      \"title\": \"头号玩家\",\n" +
//                "      \"action\": \"kylone.intent.action.Player\",\n" +
//                "      \"image\": \"https://img1.doubanio.com/view/photo/m/public/p2516578307.webp\"\n" +
//                "    },\n" +
//                "    {\n" +
//                "      \"title\": \"湮灭\",\n" +
//                "      \"action\": \"kylone.intent.action.Player\",\n" +
//                "      \"image\": \"https://img1.doubanio.com/view/photo/m/public/p2516914607.webp\"\n" +
//                "    },\n" +
//                "    {\n" +
//                "      \"title\": \"起跑线\",\n" +
//                "      \"action\": \"kylone.intent.action.Player\",\n" +
//                "      \"image\": \"https://img1.doubanio.com/view/photo/s_ratio_poster/public/p2517518428.webp\"\n" +
//                "    },\n" +
//                "    {\n" +
//                "      \"title\": \"环太平洋：雷霆再起\",\n" +
//                "      \"action\": \"kylone.intent.action.Player\",\n" +
//                "      \"image\": \"https://img3.doubanio.com/view/photo/s_ratio_poster/public/p2512933684.webp\"\n" +
//                "    },\n" +
//                "    {\n" +
//                "      \"title\": \"狂暴巨兽\",\n" +
//                "      \"action\": \"kylone.intent.action.Player\",\n" +
//                "      \"image\": \"https://img3.doubanio.com/view/photo/s_ratio_poster/public/p2516079193.webp\"\n" +
//                "    },\n" +
//                "    {\n" +
//                "      \"title\": \"唐人街探案2\",\n" +
//                "      \"action\": \"kylone.intent.action.Player\",\n" +
//                "      \"image\": \"https://img3.doubanio.com/view/photo/s_ratio_poster/public/p2511355624.webp\"\n" +
//                "    },\n" +
//                "    {\n" +
//                "      \"title\": \"头号玩家\",\n" +
//                "      \"action\": \"kylone.intent.action.Player\",\n" +
//                "      \"image\": \"https://img1.doubanio.com/view/photo/m/public/p2516578307.webp\"\n" +
//                "    },\n" +
//                "    {\n" +
//                "      \"title\": \"湮灭\",\n" +
//                "      \"action\": \"kylone.intent.action.Player\",\n" +
//                "      \"image\": \"https://img1.doubanio.com/view/photo/m/public/p2516914607.webp\"\n" +
//                "    },\n" +
//                "    {\n" +
//                "      \"title\": \"起跑线\",\n" +
//                "      \"action\": \"kylone.intent.action.Player\",\n" +
//                "      \"image\": \"https://img1.doubanio.com/view/photo/s_ratio_poster/public/p2517518428.webp\"\n" +
//                "    },\n" +
//                "    {\n" +
//                "      \"title\": \"环太平洋：雷霆再起\",\n" +
//                "      \"action\": \"kylone.intent.action.Player\",\n" +
//                "      \"image\": \"https://img3.doubanio.com/view/photo/s_ratio_poster/public/p2512933684.webp\"\n" +
//                "    },\n" +
//                "    {\n" +
//                "      \"title\": \"狂暴巨兽\",\n" +
//                "      \"action\": \"kylone.intent.action.Player\",\n" +
//                "      \"image\": \"https://img3.doubanio.com/view/photo/s_ratio_poster/public/p2516079193.webp\"\n" +
//                "    },\n" +
//                "    {\n" +
//                "      \"title\": \"唐人街探案2\",\n" +
//                "      \"action\": \"kylone.intent.action.Player\",\n" +
//                "      \"image\": \"https://img3.doubanio.com/view/photo/s_ratio_poster/public/p2511355624.webp\"\n" +
//                "    },\n" +
//                "    {\n" +
//                "      \"title\": \"头号玩家\",\n" +
//                "      \"action\": \"kylone.intent.action.Player\",\n" +
//                "      \"image\": \"https://img1.doubanio.com/view/photo/m/public/p2516578307.webp\"\n" +
//                "    },\n" +
//                "    {\n" +
//                "      \"title\": \"湮灭\",\n" +
//                "      \"action\": \"kylone.intent.action.Player\",\n" +
//                "      \"image\": \"https://img1.doubanio.com/view/photo/m/public/p2516914607.webp\"\n" +
//                "    },\n" +
//                "    {\n" +
//                "      \"title\": \"起跑线\",\n" +
//                "      \"action\": \"kylone.intent.action.Player\",\n" +
//                "      \"image\": \"https://img1.doubanio.com/view/photo/s_ratio_poster/public/p2517518428.webp\"\n" +
//                "    },\n" +
//                "    {\n" +
//                "      \"title\": \"环太平洋：雷霆再起\",\n" +
//                "      \"action\": \"kylone.intent.action.Player\",\n" +
//                "      \"image\": \"https://img3.doubanio.com/view/photo/s_ratio_poster/public/p2512933684.webp\"\n" +
//                "    },\n" +
//                "    {\n" +
//                "      \"title\": \"狂暴巨兽\",\n" +
//                "      \"action\": \"kylone.intent.action.Player\",\n" +
//                "      \"image\": \"https://img3.doubanio.com/view/photo/s_ratio_poster/public/p2516079193.webp\"\n" +
//                "    },\n" +
//                "    {\n" +
//                "      \"title\": \"唐人街探案2\",\n" +
//                "      \"action\": \"kylone.intent.action.Player\",\n" +
//                "      \"image\": \"https://img3.doubanio.com/view/photo/s_ratio_poster/public/p2511355624.webp\"\n" +
//                "    }\n" +
//                "  ]\n" +
//                "}";

        String films = "{\"item\":[{\"title\":\"头号玩家\",\"action\":\"kylone.intent.action.Detail\",\"image\":\"https://img1.doubanio.com/view/photo/m/public/p2516578307.webp\",\"key\":\"url|title\",\"value\":\"http://str.kylone.blue:4750/2000/0/base/stream.ts|头号玩家\"},{\"title\":\"湮灭\",\"action\":\"kylone.intent.action.Detail\",\"image\":\"https://img1.doubanio.com/view/photo/m/public/p2516914607.webp\",\"key\":\"url|title\",\"value\":\"http://str.kylone.blue:4750/2007/0/base/stream.ts|湮灭\"},{\"title\":\"起跑线\",\"action\":\"kylone.intent.action.Detail\",\"image\":\"https://img1.doubanio.com/view/photo/s_ratio_poster/public/p2517518428.webp\",\"key\":\"url|title\",\"value\":\"http://cms.kylone.blue/movie/1/doublesniper.mp4|起跑线\"},{\"title\":\"头号玩家\",\"action\":\"kylone.intent.action.Detail\",\"image\":\"https://img1.doubanio.com/view/photo/m/public/p2516578307.webp\",\"key\":\"url|title\",\"value\":\"http://str.kylone.blue:4750/2000/0/base/stream.ts|头号玩家\"},{\"title\":\"头号玩家\",\"action\":\"kylone.intent.action.Detail\",\"image\":\"https://img1.doubanio.com/view/photo/m/public/p2516578307.webp\",\"key\":\"url|title\",\"value\":\"http://str.kylone.blue:4750/2000/0/base/stream.ts|头号玩家\"},{\"title\":\"头号玩家\",\"action\":\"kylone.intent.action.Detail\",\"image\":\"https://img1.doubanio.com/view/photo/m/public/p2516578307.webp\",\"key\":\"url|title\",\"value\":\"http://str.kylone.blue:4750/2000/0/base/stream.ts|头号玩家\"},{\"title\":\"头号玩家\",\"action\":\"kylone.intent.action.Detail\",\"image\":\"https://img1.doubanio.com/view/photo/m/public/p2516578307.webp\",\"key\":\"url|title\",\"value\":\"http://str.kylone.blue:4750/2000/0/base/stream.ts|头号玩家\"},{\"title\":\"头号玩家\",\"action\":\"kylone.intent.action.Detail\",\"image\":\"https://img1.doubanio.com/view/photo/m/public/p2516578307.webp\",\"key\":\"url|title\",\"value\":\"http://str.kylone.blue:4750/2000/0/base/stream.ts|头号玩家\"},{\"title\":\"头号玩家\",\"action\":\"kylone.intent.action.Detail\",\"image\":\"https://img1.doubanio.com/view/photo/m/public/p2516578307.webp\",\"key\":\"url|title\",\"value\":\"http://str.kylone.blue:4750/2000/0/base/stream.ts|头号玩家\"},{\"title\":\"头号玩家\",\"action\":\"kylone.intent.action.Detail\",\"image\":\"https://img1.doubanio.com/view/photo/m/public/p2516578307.webp\",\"key\":\"url|title\",\"value\":\"http://str.kylone.blue:4750/2000/0/base/stream.ts|头号玩家\"},{\"title\":\"头号玩家\",\"action\":\"kylone.intent.action.Detail\",\"image\":\"https://img1.doubanio.com/view/photo/m/public/p2516578307.webp\",\"key\":\"url|title\",\"value\":\"http://str.kylone.blue:4750/2000/0/base/stream.ts|头号玩家\"}]}";
        adapterFilm.setData(CommonInfo.parseInfo(films));
    }
}
