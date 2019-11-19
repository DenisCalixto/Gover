package edu.wmdd.gover;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

public class PropertyAdapter extends RecyclerView.Adapter<PropertyAdapter.ViewHolder> {

    private Context context;
    private RecyclerViewClickListener mListener;
    private List<Property> list;

    public PropertyAdapter(Context context, List<Property> list, RecyclerViewClickListener listener) {
        this.context = context;
        this.list = list;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.property_item, parent, false);
        return new ViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Property property = list.get(position);

        holder.textNotes.setText(property.getNotes());
        holder.textName.setText(String.valueOf(property.getAddress()));
        String url = String.valueOf(property.getImage_url());

        if (url != "" && url != null && !url.equals("null")) {
            Log.d("Volley",property.getNotes() + " has image: " + property.getImage_url());
            ImageLoader imageLoader;

            imageLoader = CustomVolleyRequest.getInstance(this.context)
                    .getImageLoader();
            imageLoader.get(url, ImageLoader.getImageListener(holder.imageThumbnail,
                    R.drawable.loading_shape, android.R.drawable
                            .ic_dialog_alert));
            holder.imageThumbnail.setImageUrl(url, imageLoader);
        }
        else {
            Log.d("Volley",property.getNotes() + " has no image: " + property.getImage_url());
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView textNotes, textName;
        public NetworkImageView imageThumbnail;
        private RecyclerViewClickListener mListener;

        public ViewHolder(View itemView, RecyclerViewClickListener listener) {
            super(itemView);

            textNotes = itemView.findViewById(R.id.property_notes);
            textName = itemView.findViewById(R.id.property_name);
            imageThumbnail = itemView.findViewById(R.id.property_thumbnail);

            mListener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onClick(view, getAdapterPosition());
        }
    }

}