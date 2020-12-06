package com.example.expensestracker.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensestracker.R;
import com.example.expensestracker.activity.ActivityTransactionDetails;
import com.example.expensestracker.model.Transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Transaction> data;

    private SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.getDefault());

    public TransactionAdapter(Context context, ArrayList<Transaction> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public TransactionAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false);

        return new MyViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull TransactionAdapter.MyViewHolder holder, int position) {
        holder.ivBg.setImageResource(R.drawable.shape_circle);
        holder.ivBg.setColorFilter(ContextCompat.getColor(context, R.color.grey_10), android.graphics.PorterDuff.Mode.MULTIPLY);

        String formattedDate = df.format(data.get(position).getDate());
        holder.tvTrcDate.setText(formattedDate);

        holder.tvType.setText(data.get(position).getType());
        holder.tvCategory.setText(data.get(position).getCategory());

        if(data.get(position).getType().equals("Income")) {
            holder.ivIcon.setImageResource(R.drawable.ic_arrow_upward);
            holder.tvAmount.setText(String.format("+ RM %.2f", data.get(position).getAmount()));

            holder.ivIcon.setColorFilter(ContextCompat.getColor(context, R.color.green_800), android.graphics.PorterDuff.Mode.MULTIPLY);
            holder.tvType.setTextColor(context.getResources().getColor(R.color.green_800));
            holder.tvAmount.setTextColor(context.getResources().getColor(R.color.green_800));
        }
        else if(data.get(position).getType().equals("Expenses")) {
            holder.ivIcon.setImageResource(R.drawable.ic_arrow_downward);
            holder.tvAmount.setText(String.format("- RM %.2f", data.get(position).getAmount()));

            holder.ivIcon.setColorFilter(ContextCompat.getColor(context, R.color.red_800), android.graphics.PorterDuff.Mode.MULTIPLY);
            holder.tvType.setTextColor(context.getResources().getColor(R.color.red_800));
            holder.tvAmount.setTextColor(context.getResources().getColor(R.color.red_800));
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView ivBg;
        private ImageView ivIcon;
        private TextView tvTrcDate;
        private TextView tvType;
        private TextView tvCategory;
        private TextView tvAmount;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            ivBg = itemView.findViewById(R.id.ivBg);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            tvTrcDate = itemView.findViewById(R.id.tvTrcDate);
            tvType = itemView.findViewById(R.id.tvType);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvAmount = itemView.findViewById(R.id.tvAmount);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Transaction trc = data.get(getAdapterPosition());

            String formattedDate = df.format(trc.getDate());

            Intent intent = new Intent(context, ActivityTransactionDetails.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("id", trc.getId());
            intent.putExtra("date", formattedDate);
            intent.putExtra("type", trc.getType());
            intent.putExtra("category", trc.getCategory());
            intent.putExtra("description", trc.getDescription());
            intent.putExtra("amount", trc.getAmount());
            context.startActivity(intent);
        }
    }
}
