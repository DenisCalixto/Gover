package edu.wmdd.gover;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.ViewHolder> {

    private Context context;
    private List<Job> list;

    public JobAdapter(Context context, List<Job> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.single_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Job job = list.get(position);

        holder.textSummary.setText(job.getSummary());
        holder.textId.setText(String.valueOf(job.getId()));

        String url = String.valueOf(job.getImage_url());
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
        public TextView textSummary, textRating, textId;
        public NetworkImageView imageThumbnail;

        public ViewHolder(View itemView) {
            super(itemView);

            textSummary = itemView.findViewById(R.id.main_summary);
            textId = itemView.findViewById(R.id.main_id);
            imageThumbnail = itemView.findViewById(R.id.thumbnail);
        }
    }

}