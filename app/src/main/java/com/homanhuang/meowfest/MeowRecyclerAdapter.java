package com.homanhuang.meowfest;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Homan on 3/1/2018.
 */

public class MeowRecyclerAdapter extends RecyclerView.Adapter<MeowRecyclerAdapter.MyViewHolder> {

    private List<Meow> meowList;
    private Context mContext;

    /* Log tag and shortcut */
    final static String TAG = "MYLOG MeowRec";
    public static void ltag(String message) { Log.i(TAG, message); }

    public MeowRecyclerAdapter(Context context, List<Meow> vList) {
        this.meowList = vList;
        this.mContext = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        //layout variables
        TextView dateTV;
        TextView descriptionTV;
        ImageView meowIV;

        public MyViewHolder(View view) {
            super(view);
            dateTV = (TextView) view.findViewById(R.id.dateTV);
            descriptionTV = (TextView) view.findViewById(R.id.descriptionTV);

            meowIV = (ImageView) view.findViewById(R.id.meowIV);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //import the layout
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.meow_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Meow mMeow = meowList.get(position);

        //get flight
        String dateTime = mMeow.getTimestamp();
        String[] parts = dateTime.split("T");
        String date = parts[0];
        String[] parts2 = date.split("-");
        String y = parts2[0];
        String m = parts2[1];
        String d = parts2[2];

        holder.dateTV.setText(m+"/"+d+"/"+y);

        holder.descriptionTV.setText(mMeow.getTitle() + "\n" + mMeow.getDescription());

        int width = 350;
        int height = 200;

        Picasso.with(mContext)
                .load(mMeow.getImage_url())
                .resize(width, height)
                .centerInside()
                .into(holder.meowIV);

        holder.meowIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ltag("mission: "+mMeow.getImage_url());
                toBrowser(mMeow.getImage_url());
            }
        });
    }

    @Override
    public int getItemCount() {
        return meowList.size();
    }

    public void toBrowser(String link) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        mContext.startActivity(browserIntent);
    }
}