//package edu.wmdd.gover;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import android.content.Context;
//import android.graphics.Typeface;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseExpandableListAdapter;
//import android.widget.ExpandableListView;
//import android.widget.TextView;
//
//public class CustomExpandableListAdapter extends BaseExpandableListAdapter {
//
//    private Context context;
//    private List<InspectionSection> expandableListTitle;
//    private HashMap<InspectionSection, ArrayList<InspectionSectionItem>> expandableListDetail;
//
//    public CustomExpandableListAdapter(Context context, List<InspectionSection> expandableListTitle,
//                                       HashMap<InspectionSection, ArrayList<InspectionSectionItem>> expandableListDetail) {
//        this.context = context;
//        this.expandableListTitle = expandableListTitle;
//        this.expandableListDetail = expandableListDetail;
//    }
//
//    @Override
//    public Object getChild(int listPosition, int expandedListPosition) {
//        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
//                .get(expandedListPosition);
//    }
//
//    @Override
//    public long getChildId(int listPosition, int expandedListPosition) {
//        return expandedListPosition;
//    }
//
//    @Override
//    public View getChildView(int listPosition, final int expandedListPosition,
//                             boolean isLastChild, View convertView, ViewGroup parent) {
//        final InspectionSectionItem expandedListText = (InspectionSectionItem) getChild(listPosition, expandedListPosition);
//        if (convertView == null) {
//            LayoutInflater layoutInflater = (LayoutInflater) this.context
//                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            convertView = layoutInflater.inflate(R.layout.inspection_item_item, null);
//        }
//        TextView itemName = (TextView) convertView
//                .findViewById(R.id.itemName);
//        itemName.setText(expandedListText.getName());
//        return convertView;
//    }
//
//    @Override
//    public int getChildrenCount(int listPosition) {
//        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
//                .size();
//    }
//
//    @Override
//    public Object getGroup(int listPosition) {
//        return this.expandableListTitle.get(listPosition);
//    }
//
//    @Override
//    public int getGroupCount() {
//        return this.expandableListTitle.size();
//    }
//
//    @Override
//    public long getGroupId(int listPosition) {
//        return listPosition;
//    }
//
//    @Override
//    public View getGroupView(int listPosition, boolean isExpanded,
//                             View convertView, ViewGroup parent) {
//        InspectionSection listTitle = (InspectionSection) getGroup(listPosition);
//        if (convertView == null) {
//            LayoutInflater layoutInflater = (LayoutInflater) this.context.
//                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            convertView = layoutInflater.inflate(R.layout.inspection_section_item, null);
//        }
//        TextView sectionName = (TextView) convertView
//                .findViewById(R.id.sectionName);
//        sectionName.setTypeface(null, Typeface.BOLD);
//        sectionName.setText(listTitle.getName());
//
//        return convertView;
//    }
//
//    @Override
//    public boolean hasStableIds() {
//        return false;
//    }
//
//    @Override
//    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
//        return true;
//    }
//}