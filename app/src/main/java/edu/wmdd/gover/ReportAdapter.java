package edu.wmdd.gover;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {

    private Context context;
    private List<Report> list;

    public ReportAdapter(Context context, List<Report> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.report_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Report report = list.get(position);

        holder.textPropertyName.setText(report.getPropertyName());
        if (report.getReportDate() != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String strDate = formatter.format(report.getReportDate());
            holder.textReportDate.setText(strDate);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textPropertyName, textReportDate;

        public ViewHolder(View itemView) {
            super(itemView);

            textPropertyName = itemView.findViewById(R.id.property_name);
            textReportDate = itemView.findViewById(R.id.report_date);
        }
    }

}