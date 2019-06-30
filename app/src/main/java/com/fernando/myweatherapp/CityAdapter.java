package com.fernando.myweatherapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.CityViewHolder> {

    private ArrayList<City> cityList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class CityViewHolder extends RecyclerView.ViewHolder {

        public TextView txtName;
        public TextView txtTemperature;
        public TextView txtTime;
        public ImageView imgIcon;
        public RelativeLayout layout;

        public CityViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtName);
            txtTemperature = itemView.findViewById(R.id.txtTemperature);
            txtTime = itemView.findViewById(R.id.txtTime);
            imgIcon = itemView.findViewById(R.id.imgIcon);
            layout = itemView.findViewById(R.id.layout);

            imgIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });
        }
    }

    public CityAdapter(ArrayList<City> cityList) {
        this.cityList = cityList;
    }

    @Override
    public CityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.city_item, parent, false);
        CityViewHolder cityViewholder = new CityViewHolder(v, listener);
        return cityViewholder;
    }

    @Override
    public void onBindViewHolder(CityViewHolder holder, int position) {
        City currentItem = cityList.get(position);

        holder.txtName.setText(currentItem.getName());
        holder.txtTemperature.setText(currentItem.getTemperature());
        holder.txtTime.setText(currentItem.getTimeZone());
        holder.layout.setBackgroundResource(getBackground(currentItem.getTimeZone(), currentItem.getIcon()));
        holder.imgIcon.setImageResource(getIcon(currentItem.getIcon()));
    }

    private int getIcon(String icon) {
        String prefIcon = icon.substring(0, 2);

        switch (prefIcon) {
            case "01":
                return R.mipmap.ic_clear;
            case "02":
                if (icon.substring(2, 3).equals("d")) {
                    return R.mipmap.ic_few_clouds_day;
                } else {
                    return R.mipmap.ic_few_clouds_night;
                }
            case "03":
                return R.mipmap.ic_clouds;
            case "04":
                return R.mipmap.ic_clouds;
            case "09":
                return R.mipmap.ic_rain;
            case "10":
                return R.mipmap.ic_rain;
            case "11":
                return R.mipmap.ic_thunderstorm;
            case "13":
                return R.mipmap.ic_snow;
            case "50":
                return R.mipmap.ic_mist;
            default:
                return 0;
        }
    }

    private int getBackground(String time, String icon) {
        int hour = Integer.parseInt(time.substring(0, 2));

        if (icon.substring(2, 3).equals("d")) {
            if (icon.substring(0, 2).equals("01") || icon.substring(0, 2).equals("02")) {
                if ((hour >= 18)) {
                    return R.mipmap.overlay_4;
                } else {
                    return R.mipmap.overlay_3;
                }
            } else {
                return R.mipmap.overlay_1;
            }
        } else {
            return R.mipmap.overlay_2;
        }
    }

    @Override
    public int getItemCount() {
        return cityList.size();
    }
}