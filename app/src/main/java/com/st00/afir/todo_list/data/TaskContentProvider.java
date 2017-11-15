/*
* Copyright (C) 2016 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.st00.afir.todo_list.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

public class TaskContentProvider extends ContentProvider {

    public static final int TASKS = 100;
    public static final int TASK_WITH_ID = 101;
    public static UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(TaskContract.AUTHORITY,TaskContract.PATH_TASKS,TASKS);

        uriMatcher.addURI(TaskContract.AUTHORITY,TaskContract.PATH_TASKS+"/#",TASK_WITH_ID);

        return uriMatcher;
    }

    TaskDbHelper taskDbHelper;
    /* onCreate() is where you should initialize anything you’ll need to setup
    your underlying data source.
    In this case, you’re working with a SQLite database, so you’ll need to
    initialize a DbHelper to gain access to it.
     */
    @Override
    public boolean onCreate() {
        taskDbHelper = new TaskDbHelper(getContext());

        return true;
    }


    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {

        final SQLiteDatabase database = taskDbHelper.getWritableDatabase();

        //get 100 or 101 (for inserting, we don't have to know wich id we're handling) --> just 100 "TASKS"
        int match = sUriMatcher.match(uri);

        Uri returnedUri;

        switch (match){
            case TASKS:
                long id = database.insert(TaskContract.TaskEntry.TABLE_NAME,null,values);
                if(id > 0){
                    returnedUri = ContentUris.withAppendedId(TaskContract.TaskEntry.CONTENT_URI,id);
                }else {
                    throw new SQLException("Failed to insert row : "+uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: "+uri);
        }
        //notify the resolver that a change has occurred (to update the UI)
        getContext().getContentResolver().notifyChange(uri, null);

        return returnedUri;
    }


    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteDatabase database = taskDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);

        Cursor returnedCursor;

        switch (match){
            case TASKS:
                returnedCursor = database.query(TaskContract.TaskEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: "+uri);
        }

        returnedCursor.setNotificationUri(getContext().getContentResolver(),uri);

        return returnedCursor;
    }


    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public String getType(@NonNull Uri uri) {

        throw new UnsupportedOperationException("Not yet implemented");
    }

}
