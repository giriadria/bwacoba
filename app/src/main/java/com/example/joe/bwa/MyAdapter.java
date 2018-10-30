package com.example.joe.bwa;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;

import java.util.List;



    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        private Context context;
        private List<Upload> uploads;
        public View view;
        public ClipData.Item currentItem;
        private DatabaseReference mDatabase;

        public TextView textLokasi;



        public MyAdapter(Context context, List<Upload> uploads) {
            this.uploads = uploads;
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_images, parent, false);
            ViewHolder viewHolder = new ViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {


            final Upload upload = uploads.get(position);


            holder.textViewUser.setText(upload.getPhone());
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent event = new Intent(context,EventActivity.class);

                    event.putExtra("lokasi",upload.getLokasi());
                    event.putExtra("pushId",upload.getPushId());
                    event.putExtra("tentang",upload.getTentang());
                    event.putExtra("tanggal",upload.getTanggal());
                    event.putExtra("jam",upload.getJam());
                    event.putExtra("url",upload.getUrl());
                    event.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(event);
                }
            });

            holder.textViewEventName.setText(upload.getNama());
            holder.postTime.setText(upload.getPostTime());
            Glide.with(context).load(upload.getUrl()).into(holder.imageView);



        }



        @Override
        public int getItemCount() {
            return uploads.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {


            public ImageView imageView;
            public TextView textDeskripsi;
            public TextView textTanggal;
            public TextView textJam;
            public TextView textViewUser;
            public TextView textLokasi;
            public TextView textViewEventName;
            public TextView postTime;
            public CardView cardView;

            public ViewHolder(View itemView) {
                super(itemView);


                imageView = (ImageView) itemView.findViewById(R.id.imageView);
                textDeskripsi=(TextView)itemView.findViewById(R.id.textDeskripsi);
                textTanggal=(TextView) itemView.findViewById(R.id.textTanggal);
                textJam=(TextView)itemView.findViewById(R.id.textJam);
                textLokasi =(TextView) itemView.findViewById(R.id.textLokasi);
                textViewUser = (TextView) itemView.findViewById(R.id.textViewUser);
                cardView = (CardView) itemView.findViewById(R.id.cardView);
                textViewEventName = (TextView) itemView.findViewById(R.id.textViewEventName);
                postTime = (TextView) itemView.findViewById(R.id.postTime);

            }
        }
    }

