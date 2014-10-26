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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import de.greenrobot.event.EventBus;
import yuki312.android.metrobucket.R;
import yuki312.android.metrobucket.content.ContentRepository;
import yuki312.android.metrobucket.content.SyncStationService;
import yuki312.android.metrobucket.content.model.Railway;
import yuki312.android.metrobucket.content.model.Station;

public class MapActivity extends FragmentActivity
        implements GoogleMap.OnCameraChangeListener, ClusterManager.OnClusterClickListener<MapActivity.MarkerAdapter>, ClusterManager.OnClusterItemClickListener<MapActivity.MarkerAdapter> {

    private GoogleMap map; // Might be null if Google Play services APK is not available.

    private ClusterManager<MarkerAdapter> clusterManager;
    private HashSet<String> stations = new HashSet<String>();
    private HashMap<String, BitmapDescriptor> iconSet = new HashMap<String, BitmapDescriptor>();

    private Handler clusterHandler;

    private final static float CAMERA_HIGH_ZOOM = 15.0f;

    public class MarkerAdapter implements ClusterItem {
        private String stationSameAs;
        private String railwaySameAs;
        private LatLng latLng;

        public MarkerAdapter(String stationSameAs, String railwaySameAs, LatLng latLng) {
            this.stationSameAs = stationSameAs;
            this.railwaySameAs = railwaySameAs;
            this.latLng = latLng;
        }

        public String getStationSameAs() {
            return this.stationSameAs;
        }

        public String getRailwaySameAs() {
            return this.railwaySameAs;
        }

        @Override
        public LatLng getPosition() {
            return this.latLng;
        }
    }

    private class PersonRenderer extends DefaultClusterRenderer<MarkerAdapter> {

        public PersonRenderer(Context context, GoogleMap map, ClusterManager<MarkerAdapter> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(MarkerAdapter item, MarkerOptions markerOptions) {
            String sameAs = item.railwaySameAs;
            if (!iconSet.containsKey(sameAs)) {
                iconSet.put(sameAs, BitmapDescriptorFactory.fromBitmap(RailwayIcon.from(MapActivity.this, sameAs)));
            }
            markerOptions.icon(iconSet.get(sameAs));
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster<MarkerAdapter> cluster) {
            return cluster.getSize() > 1;
        }
    }

    @Override
    public boolean onClusterClick(Cluster<MarkerAdapter> cluster) {
        float zoom = CAMERA_HIGH_ZOOM;
        if (CAMERA_HIGH_ZOOM <= map.getCameraPosition().zoom) {
            // 既に十分にズームされている.
            CameraUpdate camera = CameraUpdateFactory.newLatLngZoom(cluster.getPosition(), map.getCameraPosition().zoom);
            map.moveCamera(camera);

            String[] bundleSameAs = new String[cluster.getSize()];
            Iterator<MarkerAdapter> it = cluster.getItems().iterator();
            for (int i = 0; it.hasNext(); i++) {
                MarkerAdapter marker = it.next();
                bundleSameAs[i] = marker.stationSameAs;
            }
            ChoiceStationDialog.show(getFragmentManager(), bundleSameAs);

        } else {
            CameraUpdate camera = CameraUpdateFactory.newLatLngZoom(cluster.getPosition(), CAMERA_HIGH_ZOOM);
            map.animateCamera(camera);
        }

        return true;
    }

    @Override
    public boolean onClusterItemClick(MarkerAdapter item) {
        CameraUpdate camera = CameraUpdateFactory.newLatLngZoom(item.latLng, map.getCameraPosition().zoom);
        map.moveCamera(camera);
        StationDetailActivity.startStationDetailActivity(this, item.getStationSameAs());

        return true;
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        clusterHandler.removeMessages(1);
        clusterHandler.sendEmptyMessageDelayed(1, 500);
    }

    private void markAllStation() {
        String[] sameAsSets = ContentRepository.get().stationSameAsSets();
        SyncStationService.startSyncAll(this);

        for (String sameAs : sameAsSets) {
            markStationAt(sameAs);
        }
    }

    private void markStationAt(String sameAs) {
        if (stations.contains(sameAs)) {
            return;
        }

        Station station = ContentRepository.get().findStation(sameAs);
        assert station != null;
        LatLng latlng = new LatLng(station.lat, station.lng);

        clusterManager.addItem(new MarkerAdapter(station.sameAs, station.railway, latlng));
        stations.add(sameAs);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        Toast.makeText(this, "Check update...", Toast.LENGTH_LONG).show();
        markAllStation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        setUpMapIfNeeded();
        clusterHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                CameraPosition cameraPosition = (CameraPosition) msg.obj;
                clusterManager.onCameraChange(cameraPosition);
            }
        };
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);
        stations.clear();
        clusterManager.clearItems();

        super.onPause();
    }

    public void onEvent(final ContentRepository.UpdateStationEvent event) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (map != null) {
                    markStationAt(event.sameAs);
                }
            }
        });
    }

    public static class ChoiceStationDialog extends DialogFragment {
        public static final String BUNDLE_KEY_STATION_SAME_AS_SET = "key_station_ids";

        public static void show(FragmentManager fragmentManager, String[] sameAsSet) {
            DialogFragment newFragment = new ChoiceStationDialog();
            Bundle bundle = new Bundle();
            bundle.putStringArray(BUNDLE_KEY_STATION_SAME_AS_SET, sameAsSet);
            newFragment.setArguments(bundle);
            newFragment.show(fragmentManager, "contact_us");
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final String[] sameAsSet = getArguments().getStringArray(BUNDLE_KEY_STATION_SAME_AS_SET);
            final String[] labels = new String[sameAsSet.length];
            for (int i = 0; i < sameAsSet.length; i++) {
                Station station = ContentRepository.get().findStation(sameAsSet[i]);
                Railway railway = ContentRepository.get().findRailway(station.railway);
                if (station != null) {
                    labels[i] = station.title + " - " + (railway != null ? railway.title : "?");
                } else {
                    labels[i] = "";
                }
            }

            final Activity activity = getActivity();

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setItems(labels, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    CharSequence sameAs = sameAsSet[which];
                    StationDetailActivity.startStationDetailActivity(activity, sameAs.toString());
                }
            });
            return builder.create();
        }
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #map} is not null.
     * <p/>
     * If it isn't installed {@link com.google.android.gms.maps.SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(android.os.Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        if (map == null) {
            map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_gmap)).getMap();
            if (map != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #map} is not null.
     */
    private void setUpMap() {
        LatLng pos = new LatLng(35.673631, 139.741419);
        CameraUpdate camera = CameraUpdateFactory.newLatLngZoom(pos, CAMERA_HIGH_ZOOM);
        map.moveCamera(camera);
        clusterManager = new ClusterManager<MarkerAdapter>(this, map);
        map.setOnCameraChangeListener(this);
        map.setOnMarkerClickListener(clusterManager);
        map.setOnInfoWindowClickListener(clusterManager);
        clusterManager.setOnClusterClickListener(this);
        clusterManager.setOnClusterItemClickListener(this);
        clusterManager.setRenderer(new PersonRenderer(this, map, clusterManager));
    }
}
