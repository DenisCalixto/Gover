package edu.wmdd.gover;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioGroup;
import android.widget.TextView;
import java.util.ArrayList;

public class InspectionSectionItemListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<InspectionSectionItem> sectionItemsArrayList;

    public InspectionSectionItemListAdapter(Context context, ArrayList<InspectionSectionItem> sectionsArrayList) {

        this.context = context;
        this.sectionItemsArrayList = sectionsArrayList;
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }
    @Override
    public int getItemViewType(int position) {

        return position;
    }

    @Override
    public int getCount() {
        return sectionItemsArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return sectionItemsArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        InspectionSectionItemListAdapter.ViewHolder holder;

        if (convertView == null) {
            holder = new InspectionSectionItemListAdapter.ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.inspection_item_item, null, true);

            holder.txtItemName = (TextView) convertView.findViewById(R.id.itemName);
            holder.txtItemStatus = (TextView) convertView.findViewById(R.id.itemStatus);

            convertView.setTag(holder);
        }else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (InspectionSectionItemListAdapter.ViewHolder)convertView.getTag();
        }
        holder.txtItemName.setText(sectionItemsArrayList.get(position).getName());
        holder.txtItemStatus.setText(sectionItemsArrayList.get(position).getStatus());

        return convertView;
    }

    private class ViewHolder {

        protected TextView txtItemName, txtItemStatus;
    }

}