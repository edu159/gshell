package com.example.eduardo.gshell;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> _listDataChild;
    public ArrayList<QJob> _jobsList;
    private Server _server;

    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<String>> listChildData, ArrayList<QJob> jobsList, Server server) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        this._jobsList = jobsList;
        this._server = server;
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

        final TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);
        final TextView txtListChildValue = (TextView) convertView
                .findViewById(R.id.lblListItemValue);

        String part1 = childText.substring(0,childText.indexOf(" = "));
        String part2 = childText.substring(childText.indexOf(" = ") + 3,childText.length());

        txtListChild.setText(part1);
        txtListChildValue.setText(part2);


        txtListChild.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                txtListChild.setMaxLines(100 - txtListChild.getMaxLines());
                txtListChildValue.setMaxLines(100 - txtListChild.getMaxLines());
            }
        });

        txtListChildValue.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                txtListChild.setMaxLines(100 - txtListChild.getMaxLines());
                txtListChildValue.setMaxLines(100 - txtListChild.getMaxLines());
            }
        });

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
        TextView lblListSubSubHeader = (TextView) convertView
                .findViewById(R.id.lblListSubSubHeader);
        Button lblListRunning = (Button) convertView
                .findViewById(R.id.lblListRunning);
        ProgressBar lblListProg = (ProgressBar) convertView
                .findViewById(R.id.lblListProg);
        lblListHeader.setText(headerTitle);

        for (int jobnum = 0; jobnum < _jobsList.size(); jobnum++) {
            if (_jobsList.get(jobnum).jobName == headerTitle) {
                final QJob cJob = _jobsList.get(jobnum);

                cJob.button = lblListRunning;
                lblListSubHeader.setText(cJob.jobID);
                String path = cJob.jobDetails.get("Error_Path");
                path = path.substring(0,path.lastIndexOf("/"));
                path = path.substring(path.indexOf("/"),path.length());
                lblListSubSubHeader.setText(path);

                String jState = cJob.jobDetails.get("job_state");
                if (jState.equals("R")) {
                    lblListRunning.setText(" R ");
                    lblListRunning.setTextColor(Color.GREEN);
                    String wTime = cJob.jobDetails.get("resources_used.walltime");
                    double wallTime = Double.parseDouble(wTime.split(":")[0]);
                    String aTime = cJob.jobDetails.get("Resource_List.walltime");
                    double allowedTime = Double.parseDouble(aTime.split(":")[0]);
                    int cProgress = (int) (100 * wallTime / allowedTime);
                    lblListProg.setIndeterminate(false);
                    lblListProg.setProgress(cProgress,true);
                } else if (jState.equals("Q")) {
                    lblListRunning.setText(" Q ");
                    lblListProg.setIndeterminate(true);
                    lblListRunning.setTextColor(Color.parseColor("#EF6C00"));
                } else if (jState.equals("H")) {
                    lblListRunning.setText(" H ");
                    lblListRunning.setTextColor(Color.RED);
                }

                lblListRunning.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        cJob.toggleHold(_server);
                        cJob.tf2.update();
                    }
                });

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