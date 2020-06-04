//
// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license.
//
// Microsoft Cognitive Services (formerly Project Oxford): https://www.microsoft.com/cognitive-services
//
// Microsoft Cognitive Services (formerly Project Oxford) GitHub:
// https://github.com/Microsoft/Cognitive-Face-Android
//
// Copyright (c) Microsoft Corporation
// All rights reserved.
//
// MIT License:
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
//
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED ""AS IS"", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//
package com.smart.classroom.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Defined several functions to manage local storage.
 */
public class StorageHelper {
    private static final String TAG = "StorageHelper";
    public static Set<String> getAllPersonGroupIds(Context context) {
        SharedPreferences personGroupIdSet = context.getSharedPreferences("PersonGroupIdSet", Context.MODE_PRIVATE);
        return personGroupIdSet.getStringSet("PersonGroupIdSet", new HashSet<>());
    }

    public static String getPersonGroupName(String personGroupId, Context context) {
        SharedPreferences personGroupIdNameMap = context.getSharedPreferences("PersonGroupIdNameMap", Context.MODE_PRIVATE);
        return personGroupIdNameMap.getString(personGroupId, "");
    }

    public static void setPersonGroupName(String personGroupIdToAdd, String personGroupName, Context context) {
        SharedPreferences personGroupIdNameMap = context.getSharedPreferences("PersonGroupIdNameMap", Context.MODE_PRIVATE);

        SharedPreferences.Editor personGroupIdNameMapEditor = personGroupIdNameMap.edit();
        personGroupIdNameMapEditor.putString(personGroupIdToAdd, personGroupName);
        personGroupIdNameMapEditor.apply();

        Set<String> personGroupIds = getAllPersonGroupIds(context);

        Set<String> newPersonGroupIds = new HashSet<>(personGroupIds);
        newPersonGroupIds.add(personGroupIdToAdd);
        SharedPreferences personGroupIdSet = context.getSharedPreferences("PersonGroupIdSet", Context.MODE_PRIVATE);
        SharedPreferences.Editor personGroupIdSetEditor = personGroupIdSet.edit();
        personGroupIdSetEditor.putStringSet("PersonGroupIdSet", newPersonGroupIds);
        personGroupIdSetEditor.apply();
    }

    public static void deletePersonGroups(List<String> personGroupIdsToDelete, Context context) {
        SharedPreferences personGroupIdNameMap = context.getSharedPreferences("PersonGroupIdNameMap", Context.MODE_PRIVATE);
        SharedPreferences.Editor personGroupIdNameMapEditor = personGroupIdNameMap.edit();
        for (String personGroupId: personGroupIdsToDelete) {
            personGroupIdNameMapEditor.remove(personGroupId);
        }
        personGroupIdNameMapEditor.apply();

        Set<String> personGroupIds = getAllPersonGroupIds(context);
        Set<String> newPersonGroupIds = new HashSet<>();
        for (String personGroupId: personGroupIds) {
            if (!personGroupIdsToDelete.contains(personGroupId)) {
                newPersonGroupIds.add(personGroupId);
            }
        }
        SharedPreferences personGroupIdSet = context.getSharedPreferences("PersonGroupIdSet", Context.MODE_PRIVATE);
        SharedPreferences.Editor personGroupIdSetEditor = personGroupIdSet.edit();
        personGroupIdSetEditor.putStringSet("PersonGroupIdSet", newPersonGroupIds);
        personGroupIdSetEditor.apply();
    }

    public static Set<String> getAllPersonIds(String personGroupId, Context context) {
        SharedPreferences personIdSet = context.getSharedPreferences(personGroupId + "PersonIdSet", Context.MODE_PRIVATE);
        return personIdSet.getStringSet("PersonIdSet", new HashSet<>());
    }

    public static void markAttendance(String studentId, Context context, boolean increase){
        SharedPreferences studentAttendance = context.getSharedPreferences("StudentAttendance", Context.MODE_PRIVATE);
        SharedPreferences.Editor attendanceEditor = studentAttendance.edit();
        int increasedAttendance = getStudentAttendance(studentId, context) + (increase ? 1 : -1);
        attendanceEditor.putInt(studentId, increasedAttendance);
        attendanceEditor.apply();
    }

    public static int getStudentAttendance(String studentId, Context context) {
        SharedPreferences studentAttendance = context.getSharedPreferences("StudentAttendance", Context.MODE_PRIVATE);
        return studentAttendance.getInt(studentId, 0);
    }

    public static void addFeedback(String personId, String feedback, Context context){
        SharedPreferences studentFeedbackList = context.getSharedPreferences("StudentFeedbackList", Context.MODE_PRIVATE);
        SharedPreferences.Editor feedbackEditor = studentFeedbackList.edit();
        Set<String> feedbackList = getStudentFeedbackList(personId, context);
        int feedbackCount = 0;
        for (String previousFeedback : feedbackList){
            if (previousFeedback.contains(feedback)) {
                feedbackCount ++;
            }
        }
        String countedFeedback = feedback + feedbackCount;
        feedbackList.add(countedFeedback);
        feedbackEditor.putStringSet(personId, feedbackList);
        feedbackEditor.apply();
    }

    public static Set<String> getStudentFeedbackList(String personId, Context context) {
        SharedPreferences studentFeedbackList = context.getSharedPreferences("StudentFeedbackList", Context.MODE_PRIVATE);
        return studentFeedbackList.getStringSet(personId, new HashSet<>());
    }

    public static String getPersonName(String personId, String personGroupId, Context context) {
        SharedPreferences personIdNameMap = context.getSharedPreferences(personGroupId + "PersonIdNameMap", Context.MODE_PRIVATE);
        return personIdNameMap.getString(personId, "");
    }

    public static void setPersonName(String personIdToAdd, String personName, String personGroupId, Context context) {
        SharedPreferences personIdNameMap = context.getSharedPreferences(personGroupId + "PersonIdNameMap", Context.MODE_PRIVATE);

        SharedPreferences.Editor personIdNameMapEditor = personIdNameMap.edit();
        personIdNameMapEditor.putString(personIdToAdd, personName);
        personIdNameMapEditor.apply();

        Set<String> personIds = getAllPersonIds(personGroupId, context);

        Set<String> newPersonIds = new HashSet<>(personIds);
        newPersonIds.add(personIdToAdd);
        SharedPreferences personIdSet = context.getSharedPreferences(personGroupId + "PersonIdSet", Context.MODE_PRIVATE);
        SharedPreferences.Editor personIdSetEditor = personIdSet.edit();
        personIdSetEditor.putStringSet("PersonIdSet", newPersonIds);
        personIdSetEditor.apply();
    }

    public static void deletePersons(List<String> personIdsToDelete, String personGroupId, Context context) {
        SharedPreferences personIdNameMap = context.getSharedPreferences(personGroupId + "PersonIdNameMap", Context.MODE_PRIVATE);
        SharedPreferences.Editor personIdNameMapEditor = personIdNameMap.edit();
        for (String personId: personIdsToDelete) {
            personIdNameMapEditor.remove(personId);
        }
        personIdNameMapEditor.apply();

        Set<String> personIds = getAllPersonIds(personGroupId, context);
        Set<String> newPersonIds = new HashSet<>();
        for (String personId: personIds) {
            if (!personIdsToDelete.contains(personId)) {
                newPersonIds.add(personId);
            }
        }
        SharedPreferences personIdSet = context.getSharedPreferences(personGroupId + "PersonIdSet", Context.MODE_PRIVATE);
        SharedPreferences.Editor personIdSetEditor = personIdSet.edit();
        personIdSetEditor.putStringSet("PersonIdSet", newPersonIds);
        personIdSetEditor.apply();
    }

    public static Set<String> getAllFaceIds(String personId, Context context) {
        SharedPreferences faceIdSet = context.getSharedPreferences(personId + "FaceIdSet", Context.MODE_PRIVATE);
        return faceIdSet.getStringSet("FaceIdSet", new HashSet<>());
    }

    public static String getFaceUri(String faceId, Context context) {
        SharedPreferences faceIdUriMap = context.getSharedPreferences("FaceIdUriMap", Context.MODE_PRIVATE);
        return faceIdUriMap.getString(faceId, "");
    }

    public static void setFaceUri(String faceIdToAdd, String faceUri, String personId, Context context) {
        SharedPreferences faceIdUriMap = context.getSharedPreferences("FaceIdUriMap", Context.MODE_PRIVATE);

        SharedPreferences.Editor faceIdUriMapEditor = faceIdUriMap.edit();
        faceIdUriMapEditor.putString(faceIdToAdd, faceUri);
        faceIdUriMapEditor.apply();

        Set<String> faceIds = getAllFaceIds(personId, context);

        Set<String> newFaceIds = new HashSet<>(faceIds);
        newFaceIds.add(faceIdToAdd);
        SharedPreferences faceIdSet = context.getSharedPreferences(personId + "FaceIdSet", Context.MODE_PRIVATE);
        SharedPreferences.Editor faceIdSetEditor = faceIdSet.edit();
        faceIdSetEditor.putStringSet("FaceIdSet", newFaceIds);
        faceIdSetEditor.apply();
    }

    public static void deleteFaces(List<String> faceIdsToDelete, String personId, Context context) {
        Set<String> faceIds = getAllFaceIds(personId, context);
        Set<String> newFaceIds = new HashSet<>();
        for (String faceId: faceIds) {
            if (!faceIdsToDelete.contains(faceId)) {
                newFaceIds.add(faceId);
            }
        }
        SharedPreferences faceIdSet = context.getSharedPreferences(personId + "FaceIdSet", Context.MODE_PRIVATE);
        SharedPreferences.Editor faceIdSetEditor = faceIdSet.edit();
        faceIdSetEditor.putStringSet("FaceIdSet", newFaceIds);
        faceIdSetEditor.apply();
    }
}
