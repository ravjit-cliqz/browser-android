package com.cliqz.browser.main;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.appcompat.content.res.AppCompatResources;

import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

import javax.annotation.Nullable;

import timber.log.Timber;

/**
 * @author Khaled Tantawy
 */
public class ReactNativeDrawableManager extends SimpleViewManager<ImageView> {

    private static final String REACT_CLASS = "NativeDrawable";

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    protected ImageView createViewInstance(ThemedReactContext reactContext) {
        return new ImageView(reactContext);
    }

    @ReactProp(name = "color")
    public void setColorFilter(ImageView view, @Nullable String colorFilter) {
        view.setColorFilter(Color.parseColor(colorFilter));
    }

    @ReactProp(name = "source")
    public void setSrc(ImageView view, @Nullable String src) {
        final Context imageContext = view.getContext();
        final Resources resources = imageContext.getResources();
        final int id = resources.getIdentifier(src, "drawable", imageContext.getPackageName());
        if (id > 0) {
            final Drawable drawable = AppCompatResources.getDrawable(imageContext, id);
            view.setImageDrawable(drawable);
        } else {
            Timber.e("Vector drawable " + src  + " doesn't exist");
        }
        view.setScaleType(ImageView.ScaleType.FIT_XY);
    }
}
