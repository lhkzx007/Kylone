package com.kylone.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.kylone.base.ComponentContext;
import com.kylone.biz.CommonInfo;
import com.kylone.player.R;
import com.kylone.utils.AniUtils;
import com.kylone.utils.IntentUtils;
import com.kylone.utils.LogUtil;
import com.kylone.utils.StringUtils;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by Zack on 2018/4/16
 */

public class CommonAdapter  extends RecyclerView.Adapter<CommonAdapter.BaseMViewHolder> {
    private List<CommonInfo> mData;
    protected int id_layout = R.layout.item_home;
    private OnItemListener itemListener;
    private SparseBooleanArray selected_position = new SparseBooleanArray();
    private int scale_duration = 150;
    private float scale_default = 1.05f;
    private int isSelect = 0;  // 0 不选中,1-单选,2-多选
    private WeakReference<RecyclerView> mWeenRecyclerView;

    public CommonAdapter(int id_layout, RecyclerView recyclerView) {
        this.id_layout = id_layout;
        setWeenRecyclerView(recyclerView);
    }

    @NonNull
    @Override
    public BaseMViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(id_layout, parent, false);
        return new BaseMViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseMViewHolder holder, int position) {
        if (isSelect > 0) {
            holder.itemView.setSelected(isSelect(position));
        }
//        holder.itemView.setTag(position);
        CommonInfo info = mData.get(position);
        if (holder.title != null) {
            holder.title.setText(info.getTitle());
        }
        if (holder.image != null && !TextUtils.isEmpty(info.getImage())) {
            RequestListener<String, GlideDrawable> errorListener = new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {

                    LogUtil.e("onException", e.toString() + "  model:" + model + " isFirstResource: " + isFirstResource);
//                    imageView.setImageResource(R.mipmap.ic_launcher);
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                    LogUtil.e("onResourceReady", "isFromMemoryCache:" + isFromMemoryCache + "  model:" + model + " isFirstResource: " + isFirstResource);
                    return false;
                }
            };

            int imageId = StringUtils.parseInt(info.getImage());
            if (imageId > 0) {
                Glide.with(ComponentContext.getContext()).load(imageId).into(holder.image);
            } else {
                Glide.with(ComponentContext.getContext()).load(info.getImage()).listener(errorListener).into(holder.image);
            }
        }
        if (holder.icon != null) {
            Glide.with(ComponentContext.getContext()).load(info.getIcon()).into(holder.icon);
        }
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    public void clearSelect() {
        if (isSelect > 0 && selected_position != null) {
            selected_position.clear();
            if (getItemCount() > 0 && mWeenRecyclerView != null && mWeenRecyclerView.get() != null) {
                for (int i = 0; i < getItemCount(); i++) {
                    RecyclerView.ViewHolder vH = mWeenRecyclerView.get().findViewHolderForAdapterPosition(i);
                    if (vH != null) {
                        vH.itemView.setSelected(false);
                    }
                }
            }
        }
    }

    public int getSelect(int index) {
        return selected_position.keyAt(index);
    }

    public void setData(List<CommonInfo> data) {
        mData = data;
        notifyDataSetChanged();
    }

    public CommonInfo getData(int position) {
        if (mData != null) {
            return mData.get(position);
        }
        return null;
    }

    public void setItemType(int layoutId) {
        id_layout = layoutId;
    }

    public void setOnItemListener(OnItemListener listener) {
        itemListener = listener;
    }

    private void setWeenRecyclerView(RecyclerView mWeenRecyclerView) {
        this.mWeenRecyclerView = new WeakReference<>(mWeenRecyclerView);
    }

//
//    boolean outputPortDragging = false;
//    boolean outputPortScrolling = false;
//
//    public void fff(final int position) {
//        RecyclerView recyclerView = mWeenRecyclerView.get();
//        if (recyclerView != null) {
//            final View itemView;
//            final LinearLayoutManager layoutMgr = (LinearLayoutManager) recyclerView.getLayoutManager();
////            final GridLayoutManager layoutMgr = (GridLayoutManager) recyclerView.getLayoutManager();
//
//
//            int firstPosition = layoutMgr.findFirstVisibleItemPosition();
//            int lastPosition = layoutMgr.findLastVisibleItemPosition();
//            if (position < firstPosition || position > lastPosition) {
//                recyclerView.smoothScrollToPosition(position); //滚动到指定的位置
////这里需要处理滚动监听，请看步骤3
//                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//                    @Override
//                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                        super.onScrollStateChanged(recyclerView, newState);
//                        int firstPosition;
//                        switch (newState) {
//                            case RecyclerView.SCROLL_STATE_SETTLING:
//                                if (!outputPortDragging && !outputPortScrolling) {
//                                    outputPortScrolling = true; //a scrolling occurs
//                                }
//                                break;
//                            case RecyclerView.SCROLL_STATE_DRAGGING:
//                                outputPortDragging = true; //如果是用户主动滑动recyclerview，则不触发位置计算。
//                                break;
//                            case RecyclerView.SCROLL_STATE_IDLE:
//                                if (!outputPortDragging && outputPortScrolling) {
//                                    outputPortDragging = false;
//                                    outputPortScrolling = false;
//                                    firstPosition = layoutMgr.findFirstVisibleItemPosition();
//                                    int lastPos = layoutMgr.findLastVisibleItemPosition();
//                                    //N.B.: firstVisibleItemPosition is not the first child of layoutmanager
//                                    itemView = layoutMgr.getChildAt(position - (int) layoutMgr.getChildAt(0).getTag()));  //由于滚动事件会多次触发IDLE状态，我们只需要在第一次IDLE被触发时获取ItemView。
//                                }
//                                break;
//                        }
//                    }
//                });
//            } else {
//                //直接获取ItemView
//                itemView = layoutMgr.getChildAt(position - (int) layoutMgr.getChildAt(0).getTag())；//通过获取Child(0)的tag得到第一个Child的实际位置
//            }
//
//
//        }
//    }

    /**
     * 设置item变大的倍数
     *
     * @param scale_default 倍数
     */

    public void setScaleDefault(float scale_default) {
        this.scale_default = scale_default;
    }

    /**
     * 设置item变大动画的时间
     *
     * @param scale_duration 动画的时间<单位 毫秒>
     */
    public void setScaleDuration(int scale_duration) {
        this.scale_duration = scale_duration;
    }

    public void openSingleSelect() {
        isSelect = 1;
    }

    public void openMultiSelect() {
        isSelect = 2;
    }

    public boolean isSelect(int position) {
        return selected_position != null && selected_position.get(position);
    }


    public void setSelected(int position) {
        if (selected_position != null) {
//            if (isSelect == 1) {
//                selected_position.clear();
//            }
//            if (mData != null) {
//                if (isSelect == 1 && mWeenRecyclerView != null) {
//
//                }
//            }
//            selected_position.put(position, true);

            if (mData != null && mData.size() > 0 && mWeenRecyclerView != null && mWeenRecyclerView.get() != null) {
                if (mWeenRecyclerView != null && mWeenRecyclerView.get() != null) {
                    RecyclerView recyclerView = mWeenRecyclerView.get();
                    RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(position);
                    if (holder != null && holder instanceof BaseMViewHolder) {
                        setSelected(((BaseMViewHolder) holder), position);
                    }
                }
            } else {
                if (isSelect == 1) {
                    selected_position.clear();
                }
                selected_position.put(position, true);
            }


        }
    }

    public void setSelected(BaseMViewHolder holder, int position) {
        if (isSelect > 0) {
            boolean sel = isSelect(position);
            if (isSelect == 1) {
                if (!sel) { //未选中
                    if (mWeenRecyclerView != null) {
                        RecyclerView view = mWeenRecyclerView.get();
                        if (view != null) {
                            for (int i = 0; i < selected_position.size(); i++) {
                                int key = selected_position.keyAt(i);
                                RecyclerView.ViewHolder viewKey = view.findViewHolderForAdapterPosition(key);
                                if (viewKey != null && viewKey.itemView != null) {
                                    viewKey.itemView.setSelected(false);
                                    if (itemListener != null) {
                                        itemListener.onSelectChange(viewKey, key, false);
                                    }
                                }
                            }

                        }
                    }

                    selected_position.clear();
                    holder.itemView.setSelected(true);
                    selected_position.put(position, true);
                    if (itemListener != null) {
                        itemListener.onSelectChange(holder, position, true);
                    }
                }
            } else {
                if (!sel) {
                    selected_position.delete(position);
                } else {
                    selected_position.put(position, true);
                }
                itemListener.onSelectChange(holder, position, sel);
            }
        }
    }

    public interface OnItemListener {
        void onClick(RecyclerView.ViewHolder v, int position);

        void onFocusChange(RecyclerView.ViewHolder v, int position, boolean hasFocus);

        void onSelectChange(RecyclerView.ViewHolder v, int position, boolean hasSelect);
    }

    public class BaseMViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView icon;
        public ImageView image;
//        OnItemListener itemListener;

        public BaseMViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.item_title);
            icon = (ImageView) itemView.findViewById(R.id.item_icon);
            image = (ImageView) itemView.findViewById(R.id.item_image);

            itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (scale_default != 1){
                        if (hasFocus) {
                            AniUtils.aniScale(v, 1f, scale_default, scale_duration);
                        } else {
                            AniUtils.aniScale(v, scale_default, 1f, scale_duration);
                        }
                    }
                    int position = getAdapterPosition();
                    if (itemListener != null) {
                        itemListener.onFocusChange(BaseMViewHolder.this, position, hasFocus);
                    }
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (itemListener != null) {
                        itemListener.onClick(BaseMViewHolder.this, position);
                    } else {
                        if (mData != null && mData.size() > 0) {
                            CommonInfo info = mData.get(position);
                            if (info == null) return;
                            IntentUtils.startActivityForAction(info.getAction(), info.getValues());
                        }
                    }
                    setSelected(BaseMViewHolder.this, position);
                }
            });
        }


    }
}
