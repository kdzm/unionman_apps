/*
 * Copyright (C) 2009 The Android Open Source Project
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

package com.um.gallery3d.app;

import java.io.FileNotFoundException;
import java.io.InputStream;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class GifUtils {

    public static void playGif(final Context context, final Uri uri) {
        try {
            Intent intent = new Intent(context, Gif.class);
            intent.setData(uri);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static boolean isGifFile(Context context, Uri uri) {
        GifOpenHelper gHelper = new GifOpenHelper();
        InputStream is = null;

        try {
            is = context.getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        boolean isGif = false;

        try {
            if (is != null && gHelper.STATUS_FORMAT_ERROR != gHelper.read(is)) {
                isGif = true;
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }

        return isGif;
    }
}
