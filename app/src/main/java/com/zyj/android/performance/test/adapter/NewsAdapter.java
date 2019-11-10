package com.zyj.android.performance.test.adapter;

import android.net.Uri;
import android.os.Debug;
import android.os.Trace;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zyj.android.performance.test.R;
import com.zyj.android.performance.test.bean.NewsItem;
import com.zyj.android.performance.test.net.NetUtils;
import com.zyj.android.performance.test.utils.LaunchTimer;

import java.util.List;


// Adapter 处理数据集合并负责绑定视图
// Adapter是个控制器对象，从模型层获取数据，然后提供给RecyclerView显示，起到了沟通的桥梁作用，adapter负责：
//        1、创建必要的ViewHolder；
//        2、 绑定ViewHolder至模型层数据
// ViewHolder 持有所有需要绑定数据和操作的view

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
    private List<NewsItem> mItems;
    private boolean mHasRecorded;
    private OnFeedShowCallBack mCallBack;

    public NewsAdapter(List<NewsItem> items) {
        this.mItems = items;
    }

    public void setItems(List<NewsItem> items) {
        this.mItems = items;
        notifyDataSetChanged();
    }

    public void setOnFeedShowCallBack(OnFeedShowCallBack callBack) {
        this.mCallBack = callBack;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item_constrainlayout, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
//        Debug.startMethodTracing();
//        Debug.stopMethodTracing();
        if (position == 0 && !mHasRecorded) {
            mHasRecorded = true;
            // 更为精确的是使用.addOnDrawListener()，但是这个api只在16以上才支持，可以使用addOnPreDrawListener替换
            holder.layout.getViewTreeObserver()
                    .addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            // this指代当前Listener
                            holder.layout.getViewTreeObserver().removeOnPreDrawListener(this);
                            LaunchTimer.endRecord("FeedShow");
                            return true;
                        }
                    });
        }

        NewsItem newsItem = mItems.get(position);

        holder.textView.setText(newsItem.title);
        Uri uri = Uri.parse(newsItem.imgurl);
        holder.imageView.setImageURI(uri);
    }

    @Override
    public int getItemCount() {
        return mItems == null ? 0 : mItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        SimpleDraweeView imageView;
        ConstraintLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_title);
            imageView = itemView.findViewById(R.id.iv_news);
            layout = itemView.findViewById(R.id.ll_out);
        }
    }
}
