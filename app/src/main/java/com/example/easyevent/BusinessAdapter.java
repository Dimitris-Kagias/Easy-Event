package com.example.easyevent;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BusinessAdapter extends RecyclerView.Adapter<BusinessAdapter.BusinessViewHolder> {

    private Context context;
    private List<GetBusiness> businesses;
    private String eventType;
    private String date;
    private String time;
    private String location;

    public BusinessAdapter(Context context, List<GetBusiness> businesses, String eventType, String date, String time, String location) {
        this.context = context;
        this.businesses = businesses;
        this.eventType = eventType;
        this.date = date;
        this.time = time;
        this.location = location;
    }

    @NonNull
    @Override
    public BusinessViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_business, parent, false);
        return new BusinessViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BusinessViewHolder holder, int position) {
        GetBusiness business = businesses.get(position);
        holder.textViewName.setText(business.getName());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, BusinessInfoActivity.class);
            intent.putExtra("BUSINESS", business);
            intent.putExtra("EVENT_TYPE", eventType);
            intent.putExtra("DATE", date);
            intent.putExtra("TIME", time);
            intent.putExtra("LOCATION", location);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return businesses.size();
    }

    public static class BusinessViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;

        public BusinessViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.text_view_name);
        }
    }
}
