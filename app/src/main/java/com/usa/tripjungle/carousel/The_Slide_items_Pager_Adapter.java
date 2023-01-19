package com.usa.tripjungle.carousel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.usa.tripjungle.R;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class The_Slide_items_Pager_Adapter extends PagerAdapter {

    private Context Mcontext;
    private List<The_Slide_Items_Model_Class> theSlideItemsModelClassList;


    public The_Slide_items_Pager_Adapter(Context Mcontext, List<The_Slide_Items_Model_Class> theSlideItemsModelClassList) {
        this.Mcontext = Mcontext;
        this.theSlideItemsModelClassList = theSlideItemsModelClassList;
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater inflater = (LayoutInflater) Mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View sliderLayout = inflater.inflate(R.layout.the_items_layout,null);

        ImageView featured_image = sliderLayout.findViewById(R.id.my_featured_image);

        URL newurl = null;
        try {
            newurl = new URL(theSlideItemsModelClassList.get(position).getFeatured_image());
            Bitmap mIcon_val = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
            featured_image.setImageBitmap(mIcon_val);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        container.addView(sliderLayout);
        return sliderLayout;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    @Override
    public int getCount() {
        return theSlideItemsModelClassList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }
}
