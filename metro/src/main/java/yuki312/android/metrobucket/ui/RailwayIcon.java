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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import java.util.HashMap;
import java.util.WeakHashMap;

import yuki312.android.metrobucket.R;
import yuki312.android.metrobucket.content.webapi.MetroConst.RailwayConst;

public class RailwayIcon {
    private static final HashMap<String, Integer> iconResIds = new HashMap<String, Integer>(10);
    private static final WeakHashMap<String, Bitmap> iconBitmaps = new WeakHashMap<String, Bitmap>(10);

    static {
        iconResIds.put(RailwayConst.Chiyoda.sameAs, R.drawable.metro_chiyoda);
        iconResIds.put(RailwayConst.Fukutoshin.sameAs, R.drawable.metro_fukutoshin);
        iconResIds.put(RailwayConst.Ginza.sameAs, R.drawable.metro_ginza);
        iconResIds.put(RailwayConst.Hanzomon.sameAs, R.drawable.metro_hanzomon);
        iconResIds.put(RailwayConst.Hibiya.sameAs, R.drawable.metro_hibiya);
        iconResIds.put(RailwayConst.Marunouchi.sameAs, R.drawable.metro_marunouchi);
        iconResIds.put(RailwayConst.MarunouchiBranch.sameAs, R.drawable.metro_marunouchi);
        iconResIds.put(RailwayConst.Namboku.sameAs, R.drawable.metro_namboku);
        iconResIds.put(RailwayConst.Tozai.sameAs, R.drawable.metro_tozai);
        iconResIds.put(RailwayConst.Yurakucho.sameAs, R.drawable.metro_yurakucho);

        iconBitmaps.put(RailwayConst.Chiyoda.sameAs, null);
        iconBitmaps.put(RailwayConst.Fukutoshin.sameAs, null);
        iconBitmaps.put(RailwayConst.Ginza.sameAs, null);
        iconBitmaps.put(RailwayConst.Hanzomon.sameAs, null);
        iconBitmaps.put(RailwayConst.Hibiya.sameAs, null);
        iconBitmaps.put(RailwayConst.Marunouchi.sameAs, null);
        iconBitmaps.put(RailwayConst.MarunouchiBranch.sameAs, null);
        iconBitmaps.put(RailwayConst.Namboku.sameAs, null);
        iconBitmaps.put(RailwayConst.Tozai.sameAs, null);
        iconBitmaps.put(RailwayConst.Yurakucho.sameAs, null);
    }

    public static int resolveResId(String railwaySameAs) {
        return iconResIds.get(railwaySameAs);
    }

    public static Bitmap from(Context context, String railwaySameAs) {
        Bitmap bitmap = iconBitmaps.get(railwaySameAs);
        if (bitmap != null) {
            return bitmap;
        }

        int resId = iconResIds.containsKey(railwaySameAs) ?
                iconResIds.get(railwaySameAs) : R.drawable.metro_default;
        Bitmap newIcon = createIcon(context, resId);

        iconBitmaps.put(railwaySameAs, newIcon);

        return newIcon;
    }

    private static Bitmap createIcon(Context context, int resId) {
        int w = context.getResources().getDimensionPixelSize(R.dimen.metro_bubble_icon_width);
        int h = context.getResources().getDimensionPixelSize(R.dimen.metro_bubble_icon_height);
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable shape = context.getResources().getDrawable(resId);
        shape.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
        shape.draw(canvas);
        return bitmap;
    }
}
