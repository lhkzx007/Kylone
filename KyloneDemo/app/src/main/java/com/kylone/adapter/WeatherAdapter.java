package com.kylone.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kylone.base.ComponentContext;
import com.kylone.player.R;
import com.kylone.shcapi.shApiMain;
import com.kylone.utils.AniUtils;
import com.kylone.utils.WeatherUtils;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by Zack on 2018/4/16
 */

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder> {
    private List<shApiMain.WeatherAttribute> mData;
    private int id_layout = R.layout.item_home;
    private int scale_duration = 150;
    private float scale_default = 1.05f;
    private WeakReference<RecyclerView> mWeenRecyclerView;

    public WeatherAdapter(int id_layout, RecyclerView recyclerView) {
        this.id_layout = id_layout;
        setWeenRecyclerView(recyclerView);
    }

    @Override
    public WeatherViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(id_layout, parent, false);
        return new WeatherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WeatherViewHolder holder, int position) {

        shApiMain.WeatherAttribute info = mData.get(position);

        if (holder.bg != null) {
            Glide.with(ComponentContext.getContext()).load(info.get("logo")).into(holder.bg);
        }

        if (holder.code!=null){
            String code = info.get("code");
            holder.code.setImageResource(WeatherUtils.getWeatherIcon(code));
        }


        if (holder.city != null) {
            String city = info.get("name");
            holder.city.setText(city);
        }


        if (holder.info != null) {
            holder.info.setText(info.get("text"));
        }

        if (holder.time != null) {
            holder.time.setText(info.get("pub"));
        }

        if (holder.value != null) {
            StringBuffer buffer = new StringBuffer();
            buffer.append(info.get("celc")).append(" °C\n");
            buffer.append(info.get("chill")).append(" °F\n");
            holder.value.setText(buffer);
        }
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }


    public void setData(List<shApiMain.WeatherAttribute> data) {
        mData = data;
        notifyDataSetChanged();
    }

    public shApiMain.WeatherAttribute getData(int position) {
        if (mData != null) {
            return mData.get(position);
        }
        return null;
    }


    public void setWeenRecyclerView(RecyclerView mWeenRecyclerView) {
        this.mWeenRecyclerView = new WeakReference<>(mWeenRecyclerView);
    }

    /**
     * 设置item变大的倍数
     *
     * @param scale_default
     */
    public void setScaleDefault(float scale_default) {
        this.scale_default = scale_default;
    }

    /**
     * 设置item变大动画的时间
     *
     * @param scale_duration
     */
    public void setScaleDuration(int scale_duration) {
        this.scale_duration = scale_duration;
    }


    public class WeatherViewHolder extends RecyclerView.ViewHolder {
        TextView city, info, value, time;
        ImageView code, bg;

        public WeatherViewHolder(View itemView) {
            super(itemView);
            bg = (ImageView) itemView.findViewById(R.id.weather_bg);
            city = (TextView) itemView.findViewById(R.id.weather_city);
            code = (ImageView) itemView.findViewById(R.id.weather_code);
            info = (TextView) itemView.findViewById(R.id.weather_info);
            value = (TextView) itemView.findViewById(R.id.weather_value);
            time = (TextView) itemView.findViewById(R.id.weather_time);

            itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        AniUtils.aniScale(v, 1f, scale_default, scale_duration);
                    } else {
                        AniUtils.aniScale(v, scale_default, 1f, scale_duration);
                    }
                }
            });

        }


    }
}
