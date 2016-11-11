package com.example.user.fyber;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by user on 11/3/2016.
 */
public class AdapterOffers extends BaseAdapter {

    private ArrayList<Offer> items;
    private LayoutInflater inflater;
    private Context ctx;

    public AdapterOffers(Context ctx, ArrayList<Offer> items){
        this.items = items;
        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.ctx = ctx;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Offer getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class ViewHolder {

        private ImageView image;

        private TextView title;
        private TextView payout;
        private TextView teaser;


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.offer_item, parent, false);

            holder.image = (ImageView) convertView.findViewById(R.id.image);

            holder.title = (TextView)convertView.findViewById(R.id.title);
            holder.teaser = (TextView)convertView.findViewById(R.id.teaser);
            holder.payout = (TextView)convertView.findViewById(R.id.payout);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.title.setText(getItem(position).getTitle().toString());
        holder.payout.setText(getItem(position).getPayout().toString());

        holder.teaser.setText(getItem(position).getTeaser().toString());

        Picasso.with(convertView.getContext()).load(getItem(position).getHires()).error(R.drawable.ic_plusone_tall_off_client).placeholder(R.drawable.ic_plusone_tall_off_client).noFade().into(holder.image);



        return convertView;
    }
}
