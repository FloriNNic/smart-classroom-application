package com.smart.classroom.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.smart.classroom.R;
import com.smart.classroom.helper.StorageHelper;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class StudentsListAdapter extends ArrayAdapter<CheckStatsActivity.StudentStats> {
    private Context mContext;
    private int mResource;
    private int lastPosition = -1;

    private static class ViewHolder {
        private TextView nameAndGroup;
        private TextView attendance;
        private TextView feedback;
    }

    StudentsListAdapter(Context context, int resource, ArrayList<CheckStatsActivity.StudentStats> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        //create the view result for showing the animation
        final View result;
        ViewHolder viewHolder;

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            viewHolder= new ViewHolder();
            viewHolder.nameAndGroup = (TextView) convertView.findViewById(R.id.nameView);
            viewHolder.attendance = (TextView) convertView.findViewById(R.id.attendanceView);
            viewHolder.feedback = (TextView) convertView.findViewById(R.id.feedbackView);
            Button buttonPlus = (Button) convertView.findViewById(R.id.buttonPlus);
            Button buttonMinus = (Button) convertView.findViewById(R.id.buttonMinus);

            configureViewHolder(viewHolder, position, buttonPlus, buttonMinus);
            result = convertView;
            convertView.setTag(viewHolder);

        } else { result = convertView; }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition)
                                    ? R.anim.load_down_anim : R.anim.load_up_anim);
        result.startAnimation(animation);
        lastPosition = position;
        return convertView;
    }

    private void configureViewHolder(ViewHolder viewHolder, int position, Button buttonPlus, Button buttonMinus){

        //get the student information
        String studentId = Objects.requireNonNull(getItem(position)).getStudentId();
        String studentName = Objects.requireNonNull(getItem(position)).getStudentName();
        String groupName = Objects.requireNonNull(getItem(position)).getGroupName();
        int attendance = Objects.requireNonNull(getItem(position)).getAttendanceStatus();
        String feedback = Objects.requireNonNull(getItem(position)).getFeedback();

        viewHolder.nameAndGroup.setText(String.format("%s\n- %s -", studentName, groupName));
        viewHolder.attendance.setText(String.format(Locale.ENGLISH,"%d", attendance));
        viewHolder.feedback.setText(feedback);


        buttonPlus.setOnClickListener(v -> new AlertDialog.Builder(mContext)
                .setTitle("Change attendance status")
                .setMessage(String.format("Are you sure you want to increase the attendance for %s?", studentName))
                // change the attendance only for YES result
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    StorageHelper.markAttendance(studentId, mContext, true);
                    int actualAttendance = Integer.parseInt(viewHolder.attendance.getText().toString());
                    viewHolder.attendance.setText(String.format(Locale.ENGLISH, "%d", ++actualAttendance));
                })
                // a null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .show());


        buttonMinus.setOnClickListener(v -> new AlertDialog.Builder(mContext)
                .setTitle("Change attendance status")
                .setMessage(String.format("Are you sure you want to decrease the attendance for %s?", studentName))
                // change the attendance only for YES result and attendance > 0
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    if (attendance > 0){
                        StorageHelper.markAttendance(studentId, mContext, false);
                        int actualAttendance = Integer.parseInt(viewHolder.attendance.getText().toString());
                        viewHolder.attendance.setText(String.format(Locale.ENGLISH, "%d", --actualAttendance));
                    }
                })
                // a null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .show());
    }
}