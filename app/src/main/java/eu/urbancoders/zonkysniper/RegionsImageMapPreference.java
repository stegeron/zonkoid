package eu.urbancoders.zonkysniper;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.Toast;

import com.zechassault.zonemap.adapter.NoteImageAdapter;
import com.zechassault.zonemap.listener.ItemClickListener;
import com.zechassault.zonemap.util.BitmapUtils;
import com.zechassault.zonemap.view.ImageMapView;
import com.zechassault.zonemap.view.NoteImageView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eu.urbancoders.zonkysniper.core.ZonkySniperApplication;

/**
 * Mapa kraju
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 27.11.2018
 */
public class RegionsImageMapPreference extends Preference {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RegionsImageMapPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public RegionsImageMapPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RegionsImageMapPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RegionsImageMapPreference(Context context) {
        super(context);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        View v = super.onCreateView(parent);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ZonkySniperApplication.getInstance().getApplicationContext());

        ImageMapView regionsView = (ImageMapView) v.findViewById(R.id.imageMapViewFront);

        List<Region> points = new ArrayList<>();
        points.add(new Region("Praha", new PointF(0.349f, 0.400f),
                BitmapUtils.resAsBitmap(ContextCompat.getDrawable(ZonkySniperApplication.getInstance().getApplicationContext(), R.drawable.regions_map_sel_1)),
                BitmapUtils.resAsBitmap(ContextCompat.getDrawable(ZonkySniperApplication.getInstance().getApplicationContext(), R.drawable.regions_map_1))));
        points.add(new Region("Středočeský", new PointF(0.348f, 0.401f),
                BitmapUtils.resAsBitmap(ContextCompat.getDrawable(ZonkySniperApplication.getInstance().getApplicationContext(), R.drawable.regions_map_sel_2)),
                BitmapUtils.resAsBitmap(ContextCompat.getDrawable(ZonkySniperApplication.getInstance().getApplicationContext(), R.drawable.regions_map_2))));
        points.add(new Region("Jihočeský", new PointF(0.360f, 0.778f),
                BitmapUtils.resAsBitmap(ContextCompat.getDrawable(ZonkySniperApplication.getInstance().getApplicationContext(), R.drawable.regions_map_sel_3)),
                BitmapUtils.resAsBitmap(ContextCompat.getDrawable(ZonkySniperApplication.getInstance().getApplicationContext(), R.drawable.regions_map_3))));
        points.add(new Region("Plzeňský", new PointF(0.151f, 0.603f),
                BitmapUtils.resAsBitmap(ContextCompat.getDrawable(ZonkySniperApplication.getInstance().getApplicationContext(), R.drawable.regions_map_sel_4)),
                BitmapUtils.resAsBitmap(ContextCompat.getDrawable(ZonkySniperApplication.getInstance().getApplicationContext(), R.drawable.regions_map_4))));
        points.add(new Region("Karlovarský", new PointF(0.093f, 0.349f),
                BitmapUtils.resAsBitmap(ContextCompat.getDrawable(ZonkySniperApplication.getInstance().getApplicationContext(), R.drawable.regions_map_sel_5)),
                BitmapUtils.resAsBitmap(ContextCompat.getDrawable(ZonkySniperApplication.getInstance().getApplicationContext(), R.drawable.regions_map_5))));
        points.add(new Region("Ústecký", new PointF(0.247f, 0.203f),
                BitmapUtils.resAsBitmap(ContextCompat.getDrawable(ZonkySniperApplication.getInstance().getApplicationContext(), R.drawable.regions_map_sel_6)),
                BitmapUtils.resAsBitmap(ContextCompat.getDrawable(ZonkySniperApplication.getInstance().getApplicationContext(), R.drawable.regions_map_6))));
        points.add(new Region("Liberecký", new PointF(0.423f, 0.135f),
                BitmapUtils.resAsBitmap(ContextCompat.getDrawable(ZonkySniperApplication.getInstance().getApplicationContext(), R.drawable.regions_map_sel_7)),
                BitmapUtils.resAsBitmap(ContextCompat.getDrawable(ZonkySniperApplication.getInstance().getApplicationContext(), R.drawable.regions_map_7))));
        points.add(new Region("Královéhradecký", new PointF(0.545f, 0.265f),
                BitmapUtils.resAsBitmap(ContextCompat.getDrawable(ZonkySniperApplication.getInstance().getApplicationContext(), R.drawable.regions_map_sel_8)),
                BitmapUtils.resAsBitmap(ContextCompat.getDrawable(ZonkySniperApplication.getInstance().getApplicationContext(), R.drawable.regions_map_8))));
        points.add(new Region("Pardubický", new PointF(0.592f, 0.463f),
                BitmapUtils.resAsBitmap(ContextCompat.getDrawable(ZonkySniperApplication.getInstance().getApplicationContext(), R.drawable.regions_map_sel_9)),
                BitmapUtils.resAsBitmap(ContextCompat.getDrawable(ZonkySniperApplication.getInstance().getApplicationContext(), R.drawable.regions_map_9))));
        points.add(new Region("Vysočina", new PointF(0.521f, 0.661f),
                BitmapUtils.resAsBitmap(ContextCompat.getDrawable(ZonkySniperApplication.getInstance().getApplicationContext(), R.drawable.regions_map_sel_10)),
                BitmapUtils.resAsBitmap(ContextCompat.getDrawable(ZonkySniperApplication.getInstance().getApplicationContext(), R.drawable.regions_map_10))));
        points.add(new Region("Jihomoravský", new PointF(0.664f, 0.757f),
                BitmapUtils.resAsBitmap(ContextCompat.getDrawable(ZonkySniperApplication.getInstance().getApplicationContext(), R.drawable.regions_map_sel_11)),
                BitmapUtils.resAsBitmap(ContextCompat.getDrawable(ZonkySniperApplication.getInstance().getApplicationContext(), R.drawable.regions_map_11))));
        points.add(new Region("Olomoucký", new PointF(0.767f, 0.473f),
                BitmapUtils.resAsBitmap(ContextCompat.getDrawable(ZonkySniperApplication.getInstance().getApplicationContext(), R.drawable.regions_map_sel_12)),
                BitmapUtils.resAsBitmap(ContextCompat.getDrawable(ZonkySniperApplication.getInstance().getApplicationContext(), R.drawable.regions_map_12))));
        points.add(new Region("Moravskoslezský", new PointF(0.866f, 0.470f),
                BitmapUtils.resAsBitmap(ContextCompat.getDrawable(ZonkySniperApplication.getInstance().getApplicationContext(), R.drawable.regions_map_sel_13)),
                BitmapUtils.resAsBitmap(ContextCompat.getDrawable(ZonkySniperApplication.getInstance().getApplicationContext(), R.drawable.regions_map_13))));
        points.add(new Region("Zlínský", new PointF(0.836f, 0.721f),
                BitmapUtils.resAsBitmap(ContextCompat.getDrawable(ZonkySniperApplication.getInstance().getApplicationContext(), R.drawable.regions_map_sel_14)),
                BitmapUtils.resAsBitmap(ContextCompat.getDrawable(ZonkySniperApplication.getInstance().getApplicationContext(), R.drawable.regions_map_14))));


        regionsView.setAdapter(new NoteImageAdapterImpl(points, ZonkySniperApplication.getInstance().getApplicationContext()));
//        TableLayout layout = v.findViewById(R.id.regions_image_map);

//        regionsView.setScaleToBackground(false);

//        regionsView.setScaleToBackground(true);
        regionsView.setAllowTransparent(false);


        return v;
    }

    class Region {

        /**
         * Item coordinate on image
         */
        private PointF coordinate;

        /**
         * Item label
         */
        private String text;

        /**
         * bitmap to show when item is "selected" (clicked previously)
         */
        Bitmap bitmapSelected;

        /**
         * bitmap to show when item not "selected"
         */
        Bitmap bitmapUnselected;


        public Region(String text, PointF point, Bitmap bitmapSelected, Bitmap bitmapUnselected) {
            this.coordinate = point;
            this.text = text;
            this.bitmapSelected = bitmapSelected;
            this.bitmapUnselected = bitmapUnselected;
        }

        public PointF getCoordinate() {
            return coordinate;
        }

        public void setCoordinate(PointF coordinate) {
            this.coordinate = coordinate;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public Bitmap getBitmapSelected() {
            return bitmapSelected;
        }

        public void setBitmapSelected(Bitmap bitmapSelected) {
            this.bitmapSelected = bitmapSelected;
        }

        public Bitmap getBitmapUnselected() {
            return bitmapUnselected;
        }

        public void setBitmapUnselected(Bitmap bitmapUnselected) {
            this.bitmapUnselected = bitmapUnselected;
        }
    }

    class NoteImageAdapterImpl extends NoteImageAdapter<Region> implements ItemClickListener<Region> {
        private final List<Region> items;
        private final Context context;
        private Paint labelPaintUnselected;
        private Paint labelPaintSelected;
        private Set<Region> selected = new HashSet<>();

        public NoteImageAdapterImpl(List<Region> items, Context context) {
            this.context = context;
            this.items = items;

            /*  Define unselected label paint */
            labelPaintUnselected = new Paint();
            labelPaintUnselected.setAntiAlias(true);
            labelPaintUnselected.setStrokeWidth(5);
            labelPaintUnselected.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));
            labelPaintUnselected.setTextSize(50);

            /*Define selected item paint for labels */
            labelPaintSelected = new Paint();
            labelPaintSelected.setAntiAlias(true);
            labelPaintSelected.setFakeBoldText(true);
            labelPaintSelected.setTextSize(50);
            labelPaintSelected.setStrokeWidth(5);
            labelPaintSelected.setColor(ContextCompat.getColor(context, R.color.warningYellow));

            setItemClickListener(this);

        }

        /*
            Tell the adapter how to get an item coordinate
         */
        @Override
        public PointF getItemCoordinates(Region item) {
            return item.getCoordinate();
        }

        /*
            Tell adapter how to retrieve an item based on its position
         */
        @Override
        public Region getItemAtPosition(int position) {
            return items.get(position);
        }

        /*
           Tell the adapter the number of total item
         */
        @Override
        public int getCount() {
            return items.size();
        }

        /*
        here we give the adapter the text to show in the sides label lists
         */
        @Override
        public String getLabel(Region item) {
            return item.getText();
        }

        /**
         * here we give the function to define the way the adapter retrieve an item bitmap
         */
        @Override
        public Bitmap getItemBitmap(Region item) {

            if (selected.contains(item)) {
                return item.getBitmapSelected();
            }
            return item.getBitmapUnselected();
        }

        /**
         * here we define how the paint to use depending on weather an item is "selected" or not
         */
        @Override
        public Paint getLabelPaint(Region item) {
            if (selected.contains(item)) {
                return labelPaintSelected;
            }
            return labelPaintUnselected;
        }

        /**
         * If some item label should not link to ce center of the item location you can use getAnchor
         * to define a custom location for the line to link to.
         * PointF(0,0) will link the label to the top left of an item image on the map
         * PointF(0.5,0.5) will link the label to the center. By default the anchor is  PointF(0.5,0.5)
         * so here we define only those which not link to center
         */
        @Override
        public PointF getAnchor(Region item) {
            switch (item.getText()) {
                case "Arms":
                    return new PointF(0.95f, 0.6f);
                case "Nose":
                    return new PointF(0.25f, 0.7f);
                case "Shoulders":
                    return new PointF(0.33f, 0.5f);
                case "Quadriceps":
                    return new PointF(0.1f, 0.5f);
            }
            return super.getAnchor(item);
        }

        /**
         * Define what happen when an item is clicked here we store taped item,
         * so we can display differently selected items
         */
        @Override
        public void onMapItemClick(Region item) {
            if (selected.contains(item)) {
                selected.remove(item);
            } else {
                selected.add(item);
            }
            notifyDataSetHasChanged();

            Toast.makeText(getContext(), item.getText(), Toast.LENGTH_SHORT).show();

        }
    }
}
