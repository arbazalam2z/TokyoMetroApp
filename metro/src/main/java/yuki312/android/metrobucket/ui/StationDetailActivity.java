/*
 * Copyright 2014 yuki312 All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package yuki312.android.metrobucket.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.common.base.Strings;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import yuki312.android.metrobucket.R;
import yuki312.android.metrobucket.content.ContentRepository;
import yuki312.android.metrobucket.content.SyncStationService;
import yuki312.android.metrobucket.content.model.Railway;
import yuki312.android.metrobucket.content.model.Station;
import yuki312.android.metrobucket.content.model.StationTimetable;

import static yuki312.android.metrobucket.util.LogUtils.makeLogTag;


public class StationDetailActivity extends Activity
        implements ObservableScrollView.Callbacks {
    private static final String TAG = makeLogTag(StationInfoFragment.class);

    private String currentStationId = "";
    public static final String BUNDLE_KEY_STATION_SAME_AS = "key_station_id";

    private ArrayList<String> listenStationSameAs = new ArrayList<String>();
    private ArrayList<String> listenRailwaySameAs = new ArrayList<String>();

    private void refreshStationInfo() {
        if (Strings.isNullOrEmpty(currentStationId)) {
            return;
        }

        Station station = ContentRepository.get().findStationOrFetchIfNeeded(currentStationId, true);
        if (station == null) {
            return;
        }

        if (!listenRailwaySameAs.contains(station.railway)) {
            listenRailwaySameAs.add(station.railway);
        }
        headerBarTitle.setText(station.title);

        Railway railway = ContentRepository.get().findRailwayOrFetchIfNeeded(station.railway, true);
        if (railway != null) {
            ActionBar actionBar = this.getActionBar();
            if (actionBar != null) {
                actionBar.setIcon(RailwayIcon.resolveResId(railway.sameAs));
            }
        }

        removeBodyContainer();

        addSection(bodyContainer, "Railway", "Railway");
        ViewGroup subContainer1 = addIndent(bodyContainer);
        addItem(subContainer1, ((railway == null) ? "none" : railway.title));
        addDivider(subContainer1);

        addSection(bodyContainer, "Exit", "Exit");
        ViewGroup subContainer2 = addIndent(bodyContainer);
        addItem(subContainer2, station.facility);
        addDivider(subContainer2);

        addSection(bodyContainer, "ConnectingRailway", "ConnectingRailway");
        ViewGroup subContainer3 = addIndent(bodyContainer);
        String[] connectingRailways = station.connectingRailway;
        if (connectingRailways == null) {
            addItem(subContainer3, "none");
        } else {
            for (String connectingRailway : connectingRailways) {
                if (!listenRailwaySameAs.contains(connectingRailway)) {
                    listenRailwaySameAs.add(connectingRailway);
                }
                Railway connected = ContentRepository.get().findRailwayOrFetchIfNeeded(connectingRailway, false);
                if (connected != null) {
                    addItem(subContainer3, connected.title);
                }
            }
        }
        addDivider(subContainer3);

        StationTimetable[] timetables = ContentRepository.get().findTimetableByStation(station.sameAs, true);
        if (timetables == null) {
            timetables = new StationTimetable[]{}; // NullObject
        }
        for (StationTimetable timetable : timetables) {
            String stationSameAs = timetable.weekdays[0].destinationStation;
            Station destination = ContentRepository.get().findStationOrFetchIfNeeded(stationSameAs, false);
            if (!listenStationSameAs.contains(stationSameAs)) {
                listenStationSameAs.add(stationSameAs);
            }
            addSection(bodyContainer, "TimeTable" + timetable.sameAs, "TimeTable - destination : " + (destination != null ? destination.title : stationSameAs));
            ViewGroup subContainer4 = addIndent(bodyContainer);

            for (StationTimetable.StationTimetableObject times : timetable.weekdays) {
                addItem(subContainer4, times.departureTime);
            }
            addDivider(subContainer4);
        }

        StaticMapFragment staticMap = (StaticMapFragment) getFragmentManager().findFragmentByTag("staticmap");
        staticMap.setLatlng(new LatLng(station.lat, station.lng));
        staticMap.refresh();
    }

    private void removeBodyContainer() {
        bodyContainer.removeAllViews();
    }

    private void addSection(ViewGroup container, String sectionId, String caption) {
        LayoutInflater inflater = this.getLayoutInflater();
        TextView captionView = (TextView) inflater.inflate(R.layout.view_textlist_caption, null, false);
        captionView.setText(caption);
        container.addView(captionView);
    }

    private ViewGroup addIndent(ViewGroup container) {
        LayoutInflater inflater = this.getLayoutInflater();
        ViewGroup indentView = (ViewGroup) inflater.inflate(R.layout.view_textlist_indent, container, false);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) indentView.getLayoutParams();
        int marginLeft = getResources().getDimensionPixelSize(R.dimen.Material_Layout_Keyline2);
        int marginRight = getResources().getDimensionPixelSize(R.dimen.Material_Layout_Keyline3);
        params.setMargins(marginLeft, 0, marginRight, 0);
        container.addView(indentView, params);

        return indentView;
    }

    private void addItem(ViewGroup container, String label) {
        LayoutInflater inflater = this.getLayoutInflater();
        TextView itemView = (TextView) inflater.inflate(R.layout.view_textlist_singleitem, null, false);
        itemView.setText(label);
        container.addView(itemView);
    }

    private void addDivider(ViewGroup container) {
        LayoutInflater inflater = this.getLayoutInflater();
        inflater.inflate(R.layout.view_textlist_divider, container, true);
    }

    public static void startStationDetailActivity(Context context, String stationSameAs) {
        Intent intent = new Intent(context, StationDetailActivity.class);
        intent.putExtra(BUNDLE_KEY_STATION_SAME_AS, stationSameAs);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            currentStationId = savedInstanceState.getString(BUNDLE_KEY_STATION_SAME_AS);
        } else {
            currentStationId = getIntent().getStringExtra(BUNDLE_KEY_STATION_SAME_AS);
        }

        setContentView(R.layout.activity_stationinfo);
        createView();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadHeaderImage();
        registerEvent();
        if (!listenStationSameAs.contains(currentStationId)) {
            listenStationSameAs.add(currentStationId);
        }
        refreshStationInfo();
        SyncStationService.startSyncAt(this, currentStationId);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(BUNDLE_KEY_STATION_SAME_AS, currentStationId);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterEvent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyView();
    }

    public void onEvent(ContentRepository.UpdateStationEvent event) {
        if (listenStationSameAs.contains(event.sameAs)) {
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    refreshStationInfo();
                }
            });
        }
    }

    public void onEvent(ContentRepository.UpdateRailwayEvent event) {
        if (listenRailwaySameAs.contains(event.sameAs)) {
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    refreshStationInfo();
                }
            });
        }
    }

    public void onEvent(ContentRepository.UpdateTimetableEvent event) {
        if (listenStationSameAs.contains(event.stationSameAs)) {
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    refreshStationInfo();
                }
            });
        }
    }

    private void registerEvent() {
        EventBus.getDefault().register(this);
    }

    private void unregisterEvent() {
        EventBus.getDefault().unregister(this);
    }

    /*
     * ImageryHeader -----------------------------------------------------------------
     */

    // GapFillアニメ開始位置の調整. 開始位置に"遊び"を持たせる.
    private static final float GAP_FILL_DISTANCE_MULTIPLIER = 1.5f;

    // ヘッダ画像スクロール時のパララックスエフェクト係数
    private static final float HEADER_IMAGE_BACKGROUND_PARALLAX_EFFECT_MULTIPLIER = 0.5f;

    private static final int[] RES_IDS_ACTION_BAR_SIZE = {android.R.attr.actionBarSize};

    private static final float HEADER_IMAGE_ASPECT_RATIO = 1.7777777f;

    private ObservableScrollView scrollView;

    private View headerImageContainer;

    private ViewGroup bodyContainer;

    private View headerBarContainer;
    private View headerBarContents;
    private TextView headerBarTitle;
    private View headerBarBackground;
    private View headerBarShadow;

    private int headerBarTopClearance;
    private int headerImageHeightPixels;
    private int headerBarContentsHeightPixels;

    private boolean showHeaderImage;
    private boolean gapFillShown;

    private ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener
            = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            recomputeHeaderImageAndScrollingMetrics();
        }
    };

    public void createView() {
        scrollView = (ObservableScrollView) findViewById(R.id.scroll_view);

        headerImageContainer = findViewById(R.id.header_image_container);
        bodyContainer = (ViewGroup) findViewById(R.id.body_container);
        headerBarContainer = findViewById(R.id.header_bar_container);
        headerBarContents = findViewById(R.id.header_bar_contents);
        headerBarTitle = (TextView) findViewById(R.id.header_bar_title);
        headerBarBackground = findViewById(R.id.header_bar_background);
        headerBarShadow = findViewById(R.id.header_bar_shadow);

        StaticMapFragment staticMap = new StaticMapFragment();
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.header_image_container, staticMap, "staticmap");
        transaction.commit();

        setupCustomScrolling();
    }

    @SuppressWarnings("deprecation")
    public void destroyView() {
        if (scrollView == null) {
            return;
        }

        ViewTreeObserver vto = scrollView.getViewTreeObserver();
        if (vto.isAlive()) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                vto.removeGlobalOnLayoutListener(globalLayoutListener); /* deprecated */
            } else {
                vto.removeOnGlobalLayoutListener(globalLayoutListener);
            }
        }
    }

    @Override
    public void onScrollChanged(int deltaX, int deltaY) {
        if (this.isFinishing()) {
            return;
        }

        // Reposition the header bar -- it's normally anchored to the top of the content,
        // but locks to the top of the screen on scroll
        int scrollY = scrollView.getScrollY();

        float newTop = Math.max(headerImageHeightPixels, scrollY + headerBarTopClearance);
        headerBarContainer.setTranslationY(newTop);
        headerBarBackground.setPivotY(headerBarContentsHeightPixels);

        int gapFillDistance = (int) (headerBarTopClearance * GAP_FILL_DISTANCE_MULTIPLIER);
        boolean showGapFill = !showHeaderImage || (scrollY > (headerImageHeightPixels - gapFillDistance));
        float desiredHeaderScaleY = showGapFill ?
                ((headerBarContentsHeightPixels + gapFillDistance + 1) * 1f / headerBarContentsHeightPixels)
                : 1f;
        if (!showHeaderImage) {
            headerBarBackground.setScaleY(desiredHeaderScaleY);
        } else if (gapFillShown != showGapFill) {
            headerBarBackground.animate()
                    .scaleY(desiredHeaderScaleY)
                    .setInterpolator(new DecelerateInterpolator(2f))
                    .setDuration(250)
                    .start();
        }
        gapFillShown = showGapFill;

        // Make a shadow. TODO: Do not need if running on AndroidL
        headerBarShadow.setVisibility(View.VISIBLE);

        if (headerBarTopClearance != 0) {
            // Fill the gap between status bar and header bar with color
            float gapFillProgress = Math.min(Math.max(getProgress(scrollY,
                    headerImageHeightPixels - headerBarTopClearance * 2,
                    headerImageHeightPixels - headerBarTopClearance), 0), 1);
            // TODO: Set elevation properties if running on AndroidL
            headerBarShadow.setAlpha(gapFillProgress);
        }

        // Move background image (parallax effect)
        headerImageContainer.setTranslationY(scrollY * HEADER_IMAGE_BACKGROUND_PARALLAX_EFFECT_MULTIPLIER);
    }

    private void recomputeHeaderImageAndScrollingMetrics() {
        final int actionBarSize = calculateActionBarSize();
        headerBarTopClearance = actionBarSize - headerBarContents.getPaddingTop();
        headerBarContentsHeightPixels = headerBarContents.getHeight();

        headerImageHeightPixels = headerBarTopClearance;
        if (showHeaderImage) {
            // TODO: getwidth
            headerImageHeightPixels = (int) (headerImageContainer.getWidth() / HEADER_IMAGE_ASPECT_RATIO);
            headerImageHeightPixels = Math.min(headerImageHeightPixels, scrollView.getHeight() * 2 / 3);
        }

        ViewGroup.LayoutParams lp;
        lp = headerImageContainer.getLayoutParams();
        if (lp.height != headerImageHeightPixels) {
            lp.height = headerImageHeightPixels;
            headerImageContainer.setLayoutParams(lp);
        }

        lp = headerBarBackground.getLayoutParams();
        if (lp.height != headerBarContentsHeightPixels) {
            lp.height = headerBarContentsHeightPixels;
            headerBarBackground.setLayoutParams(lp);
        }

        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams)
                bodyContainer.getLayoutParams();
        if (mlp.topMargin != headerBarContentsHeightPixels + headerImageHeightPixels) {
            mlp.topMargin = headerBarContentsHeightPixels + headerImageHeightPixels;
            bodyContainer.setLayoutParams(mlp);
        }

        onScrollChanged(0, 0); // trigger scroll handling
    }

    private void setupCustomScrolling() {
        scrollView = (ObservableScrollView) findViewById(R.id.scroll_view);
        scrollView.addCallbacks(this);
        ViewTreeObserver vto = scrollView.getViewTreeObserver();
        if (vto.isAlive()) {
            vto.addOnGlobalLayoutListener(globalLayoutListener);
        }
    }

    protected void loadHeaderImage() {
        showHeaderImage = true;
    }

    private int calculateActionBarSize() {
        Resources.Theme theme = this.getTheme();
        if (theme == null) {
            return 0;
        }

        TypedArray att = theme.obtainStyledAttributes(RES_IDS_ACTION_BAR_SIZE);
        if (att == null) {
            return 0;
        }

        float size = att.getDimension(0, 0);
        att.recycle();
        return (int) size;
    }

    private float getProgress(int value, int min, int max) {
        if (min == max) {
            throw new IllegalArgumentException("Max (" + max + ") cannot equal min (" + min + ")");
        }

        return (value - min) / (float) (max - min);
    }
}
