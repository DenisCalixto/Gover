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

public class PropertyAdapter extends RecyclerView.Adapter<PropertyAdapter.ViewHolder> {

    private Context context;
    private List<Property> list;

    public PropertyAdapter(Context context, List<Property> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.property_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Property property = list.get(position);

        holder.textDescription.setText(property.getDescription());
        holder.textName.setText(String.valueOf(property.getName()));

        String url = String.valueOf(property.getImage_url());
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
        public TextView textDescription, textName;
        public NetworkImageView imageThumbnail;

        public ViewHolder(View itemView) {
            super(itemView);

            textDescription = itemView.findViewById(R.id.property_description);
            textName = itemView.findViewById(R.id.property_name);
            imageThumbnail = itemView.findViewById(R.id.property_thumbnail);
        }
    }

}