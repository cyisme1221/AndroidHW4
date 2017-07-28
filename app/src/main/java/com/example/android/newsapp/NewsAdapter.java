package com.example.android.newsapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.android.newsapp.model.NewsItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by cy on 7/1/17.
 */

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ItemViewHolder>{

    private List<NewsItem> data = new ArrayList<NewsItem>();
    private ItemClickListener listener;
    private Context mContext;

    public NewsAdapter(Context context,ArrayList<NewsItem> data, ItemClickListener listener) {
        this.data.addAll(data);
        this.listener = listener;
        this.mContext = context;
    }

    public void setData(ArrayList<NewsItem> data){
        this.data = data;
        notifyDataSetChanged();
    }


    public interface ItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shoudAttachToParentImmediately = false;

        View view = inflater.inflate(R.layout.news_list_item,  parent, shoudAttachToParentImmediately );
        ItemViewHolder itemViewHolder = new ItemViewHolder(view);

        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if(data == null) {
            return 0;
        }
        else{
            return data.size();
        }
    }


    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView title;
        TextView description;
        TextView date;
        ImageView logo;

        ItemViewHolder(View view){
            super(view);
            logo = (ImageView) view.findViewById(R.id.iv_img);
            title = (TextView) view.findViewById(R.id.title);
            description = (TextView) view.findViewById(R.id.description);
            date = (TextView) view.findViewById(R.id.publish_at);
            view.setOnClickListener(this);
        }

        public void bind(int position) {
            NewsItem newsItem = data.get(position);
            title.setText(newsItem.getTitle());
            description.setText(newsItem.getDescription());
            date.setText(newsItem.getPublishedAt());
            //用Picasso 来 load一个 thumbnail for each news item in the recycler view.
            if(!TextUtils.isEmpty(newsItem.getUrlToImage())){
                Picasso.with(mContext)
                        .load(newsItem.getUrlToImage())
                        .into(logo);
            }
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            listener.onListItemClick(pos);
        }

    }
}