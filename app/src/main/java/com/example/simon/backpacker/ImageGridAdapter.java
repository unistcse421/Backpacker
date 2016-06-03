package com.example.simon.backpacker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by simon on 2016-06-03.
 */
public class ImageGridAdapter extends BaseAdapter {
    private Context context = null;
    private ArrayList<MapsActivity.PicInfo> picinfos = null;

    public ImageGridAdapter(Context _context, ArrayList<MapsActivity.PicInfo> _picinfos){
        this.context = _context;
        this.picinfos = _picinfos;
    }

    @Override
    public int getCount() {
        return picinfos.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        ImageView imageView = null;

        if(convertView != null)
            imageView = (ImageView)convertView;
        else{
            Bitmap bitmap = picinfos.get(position).getBitmap();
            bitmap = Bitmap.createScaledBitmap(bitmap,320,240,false);

            imageView = new ImageView(context);
            imageView.setImageBitmap(bitmap);

            View.OnClickListener imageViewClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,ShowPicActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("id",position);
                    intent.putExtra("piclist",picinfos);
                    context.startActivity(intent);
                }
            };
            imageView.setOnClickListener(imageViewClickListener);
        }
        return imageView;
    }
}
