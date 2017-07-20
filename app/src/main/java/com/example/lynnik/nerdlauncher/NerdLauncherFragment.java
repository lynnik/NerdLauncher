package com.example.lynnik.nerdlauncher;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NerdLauncherFragment extends Fragment {

  public static final String TAG = "NerdLauncherFragment";

  private RecyclerView mRecyclerView;

  public static NerdLauncherFragment newInstance() {
    return new NerdLauncherFragment();
  }

  @Nullable
  @Override
  public View onCreateView(
      LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View v = inflater
        .inflate(R.layout.fragment_nerd_launcher, container, false);

    mRecyclerView = (RecyclerView)
        v.findViewById(R.id.fragment_nerd_launcher_recycler_view);
    mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    setupAdapter();

    return v;
  }

  private void setupAdapter() {
    Intent startupIntent = new Intent(Intent.ACTION_MAIN);
    startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);

    PackageManager packageManager = getActivity().getPackageManager();
    List<ResolveInfo> activities = packageManager
        .queryIntentActivities(startupIntent, 0);

    Collections.sort(activities, new Comparator<ResolveInfo>() {
      @Override
      public int compare(ResolveInfo a, ResolveInfo b) {
        PackageManager pm = getActivity().getPackageManager();
        return String.CASE_INSENSITIVE_ORDER
            .compare(a.loadLabel(pm).toString(), b.loadLabel(pm).toString());
      }
    });

    Log.i(TAG, "Found " + activities.size() + " activities.");
    mRecyclerView.setAdapter(new ActivityAdapter(activities));
  }

  private class ActivityHolder extends RecyclerView.ViewHolder {

    private ResolveInfo mResolveInfo;
    private TextView mNameTextView;

    public ActivityHolder(View itemView) {
      super(itemView);
      mNameTextView = (TextView) itemView;
    }

    public void bindActivity(ResolveInfo resolveInfo) {
      mResolveInfo = resolveInfo;
      PackageManager pm = getActivity().getPackageManager();
      String appName = mResolveInfo.loadLabel(pm).toString();
      mNameTextView.setText(appName);
    }
  }

  private class ActivityAdapter extends RecyclerView.Adapter<ActivityHolder> {

    private final List<ResolveInfo> mActivities;

    public ActivityAdapter(List<ResolveInfo> activities) {
      mActivities = activities;
    }

    @Override
    public ActivityHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      LayoutInflater inflater = LayoutInflater.from(getActivity());
      View v = inflater
          .inflate(android.R.layout.simple_list_item_1, parent, false);
      return new ActivityHolder(v);
    }

    @Override
    public void onBindViewHolder(ActivityHolder holder, int position) {
      ResolveInfo resolveInfo = mActivities.get(position);
      holder.bindActivity(resolveInfo);
    }

    @Override
    public int getItemCount() {
      return mActivities.size();
    }
  }
}