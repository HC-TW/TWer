package com.hc.twer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DateChoiceAdapter extends RecyclerView.Adapter<DateChoiceAdapter.DateChoiceViewHolder> {

    private Context context;
    private ArrayList<Date> dates;
    public ArrayList<Integer> dateIndex = new ArrayList<>();

    public DateChoiceAdapter(Context context, ArrayList<Date> dates)
    {
        this.context = context;
        this.dates = dates;
    }

    public static class DateChoiceViewHolder extends RecyclerView.ViewHolder {

        private TextView dateChoice;
        private CheckBox dateCheckBox;

        public DateChoiceViewHolder(View itemView) {
            super(itemView);

            dateChoice = itemView.findViewById(R.id.date_choice);
            dateCheckBox = itemView.findViewById(R.id.date_checkbox);
        }
    }

    @NonNull
    @Override
    public DateChoiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.date_choice_layout, parent, false);

        return new DateChoiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final DateChoiceViewHolder holder, final int position) {

        holder.dateChoice.setText(transformDate(dates.get(position)));
        holder.dateCheckBox.setOnClickListener(v -> {
            Log.d("dateIndex", position + "clicked");
            if (holder.dateCheckBox.isChecked())
            {
                dateIndex.add(position);
            }
            else
            {
                dateIndex.remove(Integer.valueOf(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }

    // get real world date
    private String transformDate(Date date)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd");
        String dateString = simpleDateFormat.format(date);

        return dateString;
    }
}
