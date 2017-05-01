package com.example.eduardo.gshell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> _listDataChild;
    public ArrayList<QJob> _jobsList;

    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<String>> listChildData, ArrayList<QJob> jobsList) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        this._jobsList = jobsList;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);

        txtListChild.setText(childText);

        if (childText.equals("job_state = R")) {
            txtListChild.setBackgroundColor(Color.GREEN);
        } else if (childText.equals("job_state = Q")) {
            txtListChild.setBackgroundColor(Color.RED);
        } else {
            txtListChild.setBackgroundColor(Color.WHITE);
        }

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        TextView lblListSubHeader = (TextView) convertView
                .findViewById(R.id.lblListSubHeader);
        CheckBox lblListRunning = (CheckBox) convertView
                .findViewById(R.id.lblListRunning);
        ProgressBar lblListProg = (ProgressBar) convertView
                .findViewById(R.id.lblListProg);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        for (int jobnum = 0; jobnum < _jobsList.size(); jobnum++) {
            if (_jobsList.get(jobnum).jobName == headerTitle) {
                QJob cJob = _jobsList.get(jobnum);

                lblListSubHeader.setText(cJob.jobID);

                String jState = cJob.jobDetails.get("job_state");
                if (jState.equals("R")) {
                    lblListRunning.setChecked(true);
                    String wTime = cJob.jobDetails.get("resources_used.walltime");
                    double wallTime = Double.parseDouble(wTime.split(":")[0]);
                    String aTime = cJob.jobDetails.get("Resource_List.walltime");
                    double allowedTime = Double.parseDouble(aTime.split(":")[0]);
                    int cProgress = (int) (100 * wallTime / allowedTime);
                    lblListProg.setIndeterminate(false);
                    lblListProg.setProgress(cProgress,true);
                } else {
                    lblListRunning.setChecked(false);
                    lblListProg.setIndeterminate(true);
                }

            }
        }

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}