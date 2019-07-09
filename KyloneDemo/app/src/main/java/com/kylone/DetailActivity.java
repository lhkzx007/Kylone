package com.kylone;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kylone.adapter.CommonAdapter;
import com.kylone.base.BaseActivity;
import com.kylone.base.Density;
import com.kylone.base.Util;
import com.kylone.biz.CommonInfo;
import com.kylone.player.R;
import com.kylone.shcapi.shApiMain;
import com.kylone.utils.ApiUtils;
import com.kylone.utils.HandlerUtils;
import com.kylone.utils.IntentUtils;
import com.kylone.utils.LogUtil;
import com.kylone.utils.MediaPerference;
import com.kylone.base.Density;
import com.kylone.widget.PasscodeDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * Created by Zack on 2018/5/22
 */

public class DetailActivity extends BaseActivity implements PasscodeDialog.OnEditableListener {
    private ImageView tv_img;
    private TextView tv_doc;
    private TextView btn_play;
    private RecyclerView ry_film;
    private CommonAdapter adapterFilm;
    private ArrayList<CommonInfo> contents = new ArrayList<CommonInfo>();
    private String txt;
    private String passcode;
    private String pcode;
    private PasscodeDialog pDialog;
    private String url;
    private String uuid;
    private String pmid;
    private String pmvod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        initView();
        initData();
        initListener();
        if (!Util.Debug) {
            initContent();
        }

    }

    private void initListener() {

        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (TextUtils.isEmpty(pcode)) {
//
//                    return;
//                }
                if (Util.Debug){
                    play();
                    return;
                }
                LogUtil.i(" pmvod  =" + pmvod);
                String r = ApiUtils.shApi.shdrmquery(pmvod, "vod", "probe", pmid, uuid, pcode);
                LogUtil.i(" probe =  " + r);
                if (r.equals("reg")) {
                    String r1 = ApiUtils.shApi.shdrmquery(pmvod, "vod", "check", pmid, uuid, pcode);
                    if (r1.equals("auth")) {
                        boolean isAuth = auth(pcode);
                        if (!isAuth) {
                            return;
                        }
                    } else if (!r1.equals("ok")) {
                        String error = ApiUtils.shApi.shdrmquerydescr();
                        LogUtil.i(" auth error cod =  " + error);
                        return;
                    }
                } else if (!r.equals("ok")) {
                    String error = ApiUtils.shApi.shdrmquerydescr();
                    LogUtil.i(" req auth error cod =  " + error);
                    return;
                }
                play();
            }
        });
    }

    @Override
    public boolean onEditableCompletion(String edit) {
        boolean isAuth = auth(edit);
        if (isAuth) {
            MediaPerference.putString("pcode", edit);
            play();
        } else {
            LogUtil.i(" ----- 鉴权失败 ------");
            pDialog.show("Password is error , Please input again");
        }
        return isAuth;
    }

    private void play() {

        String newUrl ;
        if (Util.Debug){
            newUrl =  Uri.fromFile(new File(Environment.getExternalStorageDirectory(),"france2.ts")).toString();
        }else{
            newUrl = shApiMain.shdrmtokenize(url, uuid);
        }

        LogUtil.i(" url -> " + newUrl);
        Intent intent = new Intent(DetailActivity.this, PlayActivity.class);
        intent.putExtra("url", newUrl);
        intent.putExtra("txt", txt);
        startActivity(intent);
    }

    public boolean auth(String pcode) {
        String r2 = ApiUtils.shApi.shdrmquery(pmvod, "vod", "auth", pmid, uuid, pcode);
        LogUtil.i("   auth cod = " + r2);
        if (!r2.equals("ok")) {

            String error = ApiUtils.shApi.shdrmquerydescr();
            LogUtil.i(" auth error cod =  " + error);
            pDialog.show("Input Password");
            return false;
        }
        return true;
    }

    private void initView() {
        pDialog = new PasscodeDialog(this);
        pDialog.setOnEditableListener(this);


        tv_img = (ImageView) findViewById(R.id.detail_img);
        tv_doc = (TextView) findViewById(R.id.detail_doc);
        btn_play = (TextView) findViewById(R.id.detail_play);
        ry_film = (RecyclerView) findViewById(R.id.detail_film);


        ry_film.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                if (parent.getChildAdapterPosition(view) > 0) {
                    outRect.left = Density.INSTANCE.dp2px(14);
                }
            }
        });
        ry_film.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapterFilm = new CommonAdapter(R.layout.item_detail_film, ry_film);
        ry_film.setAdapter(adapterFilm);

        adapterFilm.setOnItemListener(new CommonAdapter.OnItemListener() {
            @Override
            public void onClick(RecyclerView.ViewHolder v, int position) {
                try {
                    if (contents != null) {
                        CommonInfo c = contents.get(position);
                        if (c != null) {
                            if (TextUtils.equals(c.getTitle(), txt)) {
                                return;
                            }
                            IntentUtils.startActivityForAction(c.getAction(), c.getValues());
                            finish();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFocusChange(RecyclerView.ViewHolder v, int position, boolean hasFocus) {

            }

            @Override
            public void onSelectChange(RecyclerView.ViewHolder v, int position, boolean hasSelect) {

            }
        });
    }


    private void initData() {
        pcode = MediaPerference.getString("pcode", "");
        String logo = getIntent().getStringExtra("logo");


        StringBuilder builder = new StringBuilder();


        txt = getIntent().getStringExtra("txt");
        String itxt = getIntent().getStringExtra("itxt");
        String idir = getIntent().getStringExtra("idir");
        String star = getIntent().getStringExtra("star");
        String irate = getIntent().getStringExtra("irate");
        String kind = getIntent().getStringExtra("kind");
        String dur = getIntent().getStringExtra("dur");
        String year = getIntent().getStringExtra("year");
        builder.append(txt).append("    ").append(year).append("    ").append(irate).append("\n");
        builder.append(star).append("\n");
        builder.append(idir).append("\n");
//        builder.append(kind).append("\n");
//        builder.append(dur).append("\n");
        builder.append(itxt).append("\n");


        url = getIntent().getStringExtra("src");
        uuid = getIntent().getStringExtra("uuid");
        pmid = getIntent().getStringExtra("pmid");
        pmvod = ApiUtils.shApi.getConfigVal("pmvod");

        tv_doc.setText(builder);
        Glide.with(this).fromString().load(logo).into(tv_img);

    }


    private void initContent() {
        contents.clear();

        if (ApiUtils.shApi.contentlist != null) {
            shApiMain.ContentCategory movie = ApiUtils.shApi.contentlist.get("movie");
            if (movie == null || movie.size() == 0) {
                LogUtil.i("无数据");
                HandlerUtils.runUITask(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DetailActivity.this, "No data , Exit the page after 3 seconds. ", Toast.LENGTH_LONG).show();
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

//                CommonInfo infoItem = new CommonInfo();
//                infoItem.setTitle(Conver.conver(key));
//                if (TextUtils.equals("0", key)) {
//                    items.add(0, infoItem);
//                } else {
//                    items.add(infoItem);
//                }
//                LogUtil.i(key);


//                ArrayList<CommonInfo> contentInfos = new ArrayList<CommonInfo>();
                Enumeration<String> vKeys = value.keys();

                while (vKeys.hasMoreElements()) {
                    String vKey = vKeys.nextElement();
                    shApiMain.ContentAttribute vvalue = value.get(vKey);

                    CommonInfo contextInfo = new CommonInfo();
                    contextInfo.setTitle(vKey);
                    contextInfo.setAction("kylone.intent.action.Detail");

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
                        }
//                        else if (TextUtils.equals(vvkey, "src")) {
//                            keyCommon.append("|url");
//                            valueCommon.append("|").append(vvvalue);
//                        } else if (TextUtils.equals(vvkey, "txt")) {
//                            keyCommon.append("|title");
//                            valueCommon.append("|").append(vvvalue);
//                        }
                        if (--index != 0) {
                            keyCommon.append("|");
                            valueCommon.append("|");
                        }
                    }
                    LogUtil.i(" --zack--  :" + keyCommon.toString());
                    LogUtil.i(" --zack--  :" + valueCommon.toString());
                    contextInfo.setValue(keyCommon.toString(), valueCommon.toString());
                    contents.add(contextInfo);
                }

                HandlerUtils.runUITask(new Runnable() {
                    @Override
                    public void run() {
                        if (contents != null && contents.size() > 0) {
                            adapterFilm.setData(contents);
                        }
                    }
                });
            }
        }
    }


}
