package com.smart.classroom.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;


import com.smart.classroom.R;
import com.smart.classroom.helper.StorageHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class CheckStatsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        ListView mListView = (ListView) findViewById(R.id.listView);

        //Add the Person objects to an ArrayList
        ArrayList<StudentStats> studentsStatsList = new ArrayList<>();

        // Retrieve all the laboratory groups IDs
        Set<String> groupIds = StorageHelper.getAllPersonGroupIds(this);
        for (String groupId : groupIds){
            String groupName = StorageHelper.getPersonGroupName(groupId, this);
            // Retrieve all the students IDs from a laboratory group
            Set<String> studentsIds = StorageHelper.getAllPersonIds(groupId, this);
            for (String studentId : studentsIds) {
                // Retrieve the name, attendance status and feedback list of a student identified by studentId
                String studentName = StorageHelper.getPersonName(studentId, groupId, this);
                int attendance = StorageHelper.getStudentAttendance(studentId, this);

                // Compute the most frequent feedback from the list
                Set<String> feedbackList = StorageHelper.getStudentFeedbackList(studentId, this);
                String feedback = getDominantFeedbackFrom(feedbackList);

                // Serialize the stats and add them into a list
                StudentStats student = new StudentStats(studentId, studentName, groupName, attendance, feedback);
                studentsStatsList.add(student);
            }
        }

        StudentsListAdapter adapter = new StudentsListAdapter(this, R.layout.student_stats_layout, studentsStatsList);
        mListView.setAdapter(adapter);
    }

    public String getDominantFeedbackFrom(Set<String> feedbackList){
        int happyStatus = 0;
        int neutralStatus = 0;
        int angryStatus = 0;
        for (String feedback : feedbackList){
            if (feedback.contains(getString(R.string.happy))){
                happyStatus ++;
            } else if (feedback.contains(getString(R.string.neutral))){
                neutralStatus ++;
            } else if (feedback.contains(getString(R.string.angry))){
                angryStatus ++;
            }
        }
        int dominantStatus = Math.max(happyStatus, Math.max(neutralStatus, angryStatus));
        if (dominantStatus == happyStatus){
            return getString(R.string.happy);

        } else if (dominantStatus == angryStatus) {
            return getString(R.string.angry);

        } else {
            return getString(R.string.neutral);
        }
    }

    static class StudentStats {
        private final String groupName;
        private final String studentId;
        private final String studentName;
        private final int attendanceStatus;
        private final String feedback;

        StudentStats(String studentId, String studentName, String groupName, int attendance, String feedback) {
            this.studentId = studentId;
            this.studentName = studentName;
            this.groupName = groupName;
            this.attendanceStatus = attendance;
            this.feedback = feedback;
        }

        String getStudentId() { return studentId; }

        String getStudentName() { return studentName; }

        String getGroupName() { return groupName; }

        int getAttendanceStatus(){
            return attendanceStatus;
        }

        String getFeedback(){
            return feedback;
        }
    }
}
