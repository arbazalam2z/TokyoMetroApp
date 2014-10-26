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

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import yuki312.android.metrobucket.R;

/**
 * フラグメントを追加する場合はlayout.xmlから指定すること
 */
public class StaticMapFragment extends Fragment {
    private static final String ARG_LATLNG = "latlng";

    private static final int MAP_WIDTH_PX_LIMIT = 640;
    private static final int MAP_HEIGHT_PX_LIMIT = 640;
    private static final int[] MAP_SCALE_LIMITS = new int[]{1, 2, /* 4 */};
    private static final int MAP_ZOOM = 15;

    private static final String MAP_URI_FORMAT = "http://maps.googleapis.com/maps/api/staticmap?zoom=%d&size=%dx%d&scale=%d&sensor=false&markers=color:orange%%7C%.6f,%.6f";

    private LatLng latlng = null;

    private ImageView staticmap;

    public StaticMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.latlng = (LatLng) getArguments().getParcelable(ARG_LATLNG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        staticmap = (ImageView) inflater.inflate(R.layout.fragment_static_map, container, false);
        refresh();
        return staticmap;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        staticmap = null;
    }

    public void setLatlng(LatLng latlng) {
        this.latlng = latlng;
    }

    public void refresh() {
        if (this.latlng != null) {
            ViewTreeObserver vto = staticmap.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    Picasso.with(getActivity()).load(makeMapsUri()).into(staticmap);
                }
            });
        }
    }

    private String makeMapsUri() {
        int scale = scale();
        ViewGroup.LayoutParams size = measure(scale);
        String uri = String.format(MAP_URI_FORMAT,
                MAP_ZOOM, size.width, size.height, scale, latlng.latitude, latlng.longitude);
        return uri;
    }

    private ViewGroup.LayoutParams measure(int scale) {
        int w = staticmap.getWidth() / scale;
        int h = staticmap.getHeight() / scale;
        w = (w > MAP_WIDTH_PX_LIMIT) ? MAP_WIDTH_PX_LIMIT : w;
        h = (h > MAP_HEIGHT_PX_LIMIT) ? MAP_HEIGHT_PX_LIMIT : h;

        return new ViewGroup.LayoutParams(w, h);
    }

    private int scale() {
        int wScale = ((Double) Math.ceil(staticmap.getWidth() / (double) MAP_WIDTH_PX_LIMIT)).intValue();
        int hScale = ((Double) Math.ceil(staticmap.getHeight() / (double) MAP_HEIGHT_PX_LIMIT)).intValue();
        int larger = (wScale >= hScale) ? wScale : hScale;
        int scale = MAP_SCALE_LIMITS[0];
        for (int s : MAP_SCALE_LIMITS) {
            scale = s;
            if (larger <= scale) {
                break;
            }
        }
        return scale;
    }
}
