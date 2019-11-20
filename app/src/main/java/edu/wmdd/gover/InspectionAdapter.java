package edu.wmdd.gover;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.text.SimpleDateFormat;
import java.util.List;

public class InspectionAdapter extends RecyclerView.Adapter<InspectionAdapter.ViewHolder> {

    private Context context;
    private List<Inspection> list;

    public InspectionAdapter(Context context, List<Inspection> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.inspection_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Inspection inspection = list.get(position);

        holder.textPropertyName.setText(inspection.getPropertyName());
        if (inspection.getInspectionDate() != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String strDate = formatter.format(inspection.getInspectionDate());
            holder.textInspectionDate.setText(strDate);
        }

        String url = String.valueOf(inspection.getImage_url());
        ImageLoader imageLoader;

        imageLoader = CustomVolleyRequest.getInstance(this.context)
                .getImageLoader();
        imageLoader.get(url, ImageLoader.getImageListener(holder.imageThumbnail,
                R.drawable.loading_shape, android.R.drawable
                        .ic_dialog_alert));
        holder.imageThumbnail.setImageUrl(url, imageLoader);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textPropertyName, textInspectionDate;
        public NetworkImageView imageThumbnail;

        public ViewHolder(View itemView) {
            super(itemView);

            textPropertyName = itemView.findViewById(R.id.property_name);
            textInspectionDate = itemView.findViewById(R.id.inspector_date);
            imageThumbnail = itemView.findViewById(R.id.property_thumbnail);
        }
    }

}