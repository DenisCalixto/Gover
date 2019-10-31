package edu.wmdd.gover;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;

public class InspectionSectionListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<InspectionSection> sectionsArrayList;
    private InspectionSectionItemListAdapter sectionItemsListAdapter;

    public InspectionSectionListAdapter(Context context, ArrayList<InspectionSection> sectionsArrayList) {

        this.context = context;
        this.sectionsArrayList = sectionsArrayList;
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
        return sectionsArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return sectionsArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.inspection_section_item, null, true);

            holder.txtName = (TextView) convertView.findViewById(R.id.sectionName);
            holder.itemsList = (ListView) convertView.findViewById(R.id.itemsList);

            convertView.setTag(holder);
        }else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder)convertView.getTag();
        }
        holder.txtName.setText(sectionsArrayList.get(position).getName());

        if (holder.itemsList.getCount() > 0) {
            sectionItemsListAdapter = new InspectionSectionItemListAdapter(context, sectionsArrayList.get(position).getItems());
            holder.itemsList.setAdapter(sectionItemsListAdapter);
        }

        return convertView;
    }

    private class ViewHolder {

        protected TextView txtName;
        protected ListView itemsList;
    }

}