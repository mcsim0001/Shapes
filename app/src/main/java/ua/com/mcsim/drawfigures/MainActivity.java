package ua.com.mcsim.drawfigures;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.amazon.device.ads.Ad;
import com.amazon.device.ads.AdError;
import com.amazon.device.ads.AdLayout;
import com.amazon.device.ads.AdProperties;
import com.amazon.device.ads.AdRegistration;
import com.amazon.device.ads.AdSize;
import com.amazon.device.ads.DefaultAdListener;

import ua.com.mcsim.drawfigures.drawing.PixelPaintView;
import ua.com.mcsim.drawfigures.utils.MultiSlide;


public class MainActivity extends Activity implements View.OnClickListener {


    private PixelPaintView mPaintView;
    private ConstraintLayout deskLayout, sizeLayout;
    private final int HOME_ANIMALS = 1000;
    private final int BRUSH_COLOR = 2000;
    private final int BRUSH_WEIGHT = 150;
    private int brushSize = 30;
    private final float BRUSH_SCALE = 1.3f;
    private ImageButton btnErase, btnPalette, btnRecycler, btnAnimals, btnSize, btnUndo, btnRedo;
    private ImageView animalImage, sizeCircle;
    private SeekBar sizeBar;
    private MultiSlide slideBottomPanel, slideLeftPanel;
    private View bottomPanel, leftPanel, lastView;
    private final String STATUS = "rate_status";
    private final int[] miniature = {
            R.drawable.fig1_150x200,
            R.drawable.fig2_150x200,
            R.drawable.fig3_150x200,
            R.drawable.fig4_150x200,
            R.drawable.fig5_150x200,
            R.drawable.fig6_150x200,
            R.drawable.fig7_150x200,
            R.drawable.fig8_150x200,
            R.drawable.fig9_150x200,
            R.drawable.fig10_150x200,
            R.drawable.fig11_150x200,
            R.drawable.fig12_150x200,
            R.drawable.fig13_150x200};
    private final int[] trafaret = {
            R.drawable.fig1,
            R.drawable.fig2,
            R.drawable.fig3,
            R.drawable.fig4,
            R.drawable.fig5,
            R.drawable.fig6,
            R.drawable.fig7,
            R.drawable.fig8,
            R.drawable.fig9,
            R.drawable.fig10,
            R.drawable.fig11,
            R.drawable.fig12,
            R.drawable.fig13};

    private final int[] brush = {
            R.drawable.br1,
            R.drawable.br2,
            R.drawable.br3,
            R.drawable.br4,
            R.drawable.br5,
            R.drawable.br6,
            R.drawable.br7,
            R.drawable.br8,
            R.drawable.br9,
            R.drawable.br10,
            R.drawable.br11,
            R.drawable.br12,
            R.drawable.br13,
            R.drawable.br14};
    private final int[] color = {
            R.color.brush1,
            R.color.brush2,
            R.color.brush3,
            R.color.brush4,
            R.color.brush5,
            R.color.brush6,
            R.color.brush7,
            R.color.brush8,
            R.color.brush9,
            R.color.brush10,
            R.color.brush11,
            R.color.brush12,
            R.color.brush13,
            R.color.brush14};

    //variables for amazon ads
    private static final String APP_KEY = "d2fa3daeefe94bee91989951cccbd7c1"; // Sample Application Key. Replace this value with your Application Key.
    private static final String LOG_TAG = "FloatingAdSample"; // Tag used to prefix all log messages.
    private AdLayout adView; // The ad that is currently visible to the user.
    private SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("mLog", "onCreate");

        //***********Fullscreen without action bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);


        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            return;

        }

        setContentView(R.layout.drawerdesk);

        //Creating Paint desk with letter
        mPaintView = (PixelPaintView) findViewById(R.id.paintView);

        //Seekbar
        sizeBar = (SeekBar) findViewById(R.id.size_bar);
        sizeBar.setProgress(brushSize);
        sizeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int prog;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                prog=progress+20;
                sizeCircle.getLayoutParams().height = progress+20;
                sizeCircle.getLayoutParams().width = progress+20;
                sizeCircle.requestLayout();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mPaintView.setBrushSize(prog);
                brushSize = prog;
            }
        });

        //Buttons
        btnErase = (ImageButton) findViewById(R.id.btn_erase);
        btnErase.setOnClickListener(this);
        btnPalette = (ImageButton) findViewById(R.id.btn_palette);
        btnPalette.setOnClickListener(this);
        btnRecycler = (ImageButton) findViewById(R.id.btn_recycler);
        btnRecycler.setOnClickListener(this);
        btnAnimals = (ImageButton) findViewById(R.id.btn_figures);
        btnAnimals.setOnClickListener(this);
        btnSize = (ImageButton) findViewById(R.id.btn_size);
        btnSize.setOnClickListener(this);
        btnUndo = (ImageButton) findViewById(R.id.btn_undo);
        btnUndo.setOnClickListener(this);
        btnRedo = (ImageButton) findViewById(R.id.btn_redo);
        btnRedo.setOnClickListener(this);

        //Images
        sizeCircle = (ImageView) findViewById(R.id.size_circle);
        sizeCircle.getLayoutParams().height = brushSize;
        sizeCircle.getLayoutParams().width = brushSize;
        sizeCircle.setImageResource(R.drawable.size_example);

        //Trafarete Image
        animalImage = (ImageView) findViewById(R.id.animalImage);
        animalImage.setImageResource(R.drawable.fig1);

        //Panels
        bottomPanel = findViewById(R.id.bottom_panel);
        leftPanel = findViewById(R.id.left_panel);

        slideBottomPanel = new MultiSlide(bottomPanel, MultiSlide.BOTTOM);
        slideBottomPanel.hideImmediately();

        slideLeftPanel = new MultiSlide(leftPanel,MultiSlide.LEFT);
        slideLeftPanel.hideImmediately();

        //Layouts
        deskLayout = (ConstraintLayout) findViewById(R.id.back_layout);
        sizeLayout = (ConstraintLayout) findViewById(R.id.size_layout);
        sizeLayout.setVisibility(View.GONE);

        //panels
        initializeBottomPanel();
        initializeLeftPanel();

        //initialize ads

        // For debugging purposes enable logging, but disable for production builds.
        //AdRegistration.enableLogging(false);
        // For debugging purposes flag all ad requests as tests, but set to false for production builds.
        //AdRegistration.enableTesting(false);

        /*try {
            AdRegistration.setAppKey(APP_KEY);
        } catch (final IllegalArgumentException e) {
            Log.e(LOG_TAG, "IllegalArgumentException thrown: " + e.toString());
            return;
        }

        loadAd();*/
    }

   /* private void loadAd() {
        if (this.adView == null) { // Create and configure a new ad if the next ad doesn't currently exist.
            this.adView = (AdLayout) findViewById(R.id.ad_layout);

            // Note: The above implementation is for an auto-sized ad in an AdLayout of width MATCH_PARENT and
            // height WRAP_CONTENT. The rendered ad will retain its original device-independent pixel size and
            // will not scale up to fill its container. If you want the ad to fill its container then you can
            // instead specify your size as AdSize.SIZE_AUTO or, equivalently, leave out the AdSize parameter.
            //
            // Alternatively, if you want to prevent automatic size selection and give the ad the same fixed
            // size on all devices, you will need to factor in the phone's scale when setting up the AdLayout
            // dimensions. See the example below for 320x50 dip:
            //
            //    this.nextAdView = new AdLayout(this, AdSize.SIZE_320x50);
            //    final float scale = this.getApplicationContext().getResources().getDisplayMetrics().density;
            //    final LayoutParams layoutParams = new FrameLayout.LayoutParams((int) (320 * scale),
            //             (int) (50 * scale), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);

            // Register our ad handler that will receive callbacks for state changes during the ad lifecycle.
            this.adView.setListener(new SampleAdListener());
        }

        // Load the ad with default ad targeting.
        this.adView.loadAd();

        // Note: You can choose to provide additional targeting information to modify how your ads
        // are targeted to your users. This is done via an AdTargetingOptions parameter that's passed
        // to the loadAd call. See an example below:
        //
        //    final AdTargetingOptions adOptions = new AdTargetingOptions();
        //    adOptions.enableGeoLocation(true);
        //    this.nextAdView.loadAd(adOptions);
    }*/


    private void initializeBottomPanel() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.bottom_panel_layout);
        ImageView menuItem;

        for (int i = 0; i < miniature.length; i++ ) {
            menuItem = new ImageView(this);
            menuItem.setId(miniature[i]+HOME_ANIMALS);
            menuItem.setImageResource(miniature[i]);
            menuItem.setOnClickListener(this);
            linearLayout.addView(menuItem,200,150);
        }
    }

    private void initializeLeftPanel() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.left_panel_layout);
        ImageView menuItem = new ImageView(this);
        linearLayout.addView(menuItem,BRUSH_WEIGHT+20,0);  //

        for (int i = 0; i < brush.length; i++ ) {
            menuItem = new ImageView(this);
            menuItem.setId(brush[i]+BRUSH_COLOR);
            menuItem.setImageResource(brush[i]);
            menuItem.setOnClickListener(this);
            linearLayout.addView(menuItem,BRUSH_WEIGHT,88);
            if (i==0) {
                onClick(menuItem);
            }
        }
    }

    private void eraserButtonSwitch() {

        if (mPaintView.isEraserEnable()) {
            btnErase.setBackgroundResource(R.drawable.brush40x40);
        } else {
            btnErase.setBackgroundResource(R.drawable.clean40x40);
        }
    }
    private void panelSwitch(MultiSlide panel) {
        if (!panel.isVisible()) {

            panel.animateIn();
        } else {
            panel.animateOut();

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }



    @Override
    public void onClick(View view) {

        //Touch Animation
        touchAnimation(view);

        //Drawerscreen Buttons
        switch (view.getId()){
            case R.id.btn_erase:{
                mPaintView.setErase(!mPaintView.isEraserEnable());
                eraserButtonSwitch();
                break;
            }
            /*case R.id.btn_letter:{
                showhideLetter();
                break;
            }*/
            case R.id.btn_palette:{

                panelSwitch(slideLeftPanel);
                break;
            }
            case R.id.btn_recycler:{
                mPaintView.startNew();
                break;
            }
            case R.id.btn_figures:{
                panelSwitch(slideBottomPanel);
                break;
            }
            case R.id.btn_size:{
                if (sizeLayout.getVisibility() == View.VISIBLE) {
                    sizeLayout.setVisibility(View.GONE);
                }else sizeLayout.setVisibility(View.VISIBLE);
                break;
            }
            /*case R.id.btn_undo:{
                mPaintView.performUndo();
                break;
            }
            case R.id.btn_redo:{
                mPaintView.performRedo();
                break;
            }*/
        }
        //Animals
        for (int i = 0; i < miniature.length; i++) {
            if (miniature[i]+HOME_ANIMALS == view.getId()) {
                animalImage.setImageResource(trafaret[i]);
                slideBottomPanel.animateOut();
                mPaintView.startNew();
                break;
            }
        }
        //Brushes + colors
        for (int i = 0; i < brush.length; i++) {
            if (brush[i]+BRUSH_COLOR == view.getId()) {
                view.animate().scaleX(BRUSH_SCALE);
                view.animate().scaleY(BRUSH_SCALE);
                mPaintView.setColor(getResources().getColor(color[i]));
                if (lastView!=null&&lastView!=view) {
                    lastView.animate().scaleX(1.0f);
                    lastView.animate().scaleY(1.0f);
                }
                lastView = view;
                break;
            }
        }
    }

    private void touchAnimation(View view) {
        Animation anim1 = new ScaleAnimation(1.0f,0.9f,1.0f,0.9f);
        anim1.setDuration(100);
        view.startAnimation(anim1);
    }

    /*private void showCurrentAd() {
        final Animation slideUp = AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_up);
        this.adView.startAnimation(slideUp);
    }*/

    /**
     * Shows the ad that is in the next ad view to the user.
     */
    /*private void showNextAd() {
        this.adViewContainer.removeView(this.adView);
        final AdLayout tmp = this.adView;
        this.adView = this.nextAdView;
        this.nextAdView = tmp;
        showCurrentAd();
    }*/

    /**
     * Hides the ad that is in the current ad view, and then displays the ad that is in the next ad view.
     */
    /*private void swapCurrentAd() {
        final Animation slideDown = AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_down);
        slideDown.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(final Animation animation) {
                showCurrentAd();
            }

            public void onAnimationRepeat(final Animation animation) {
            }

            public void onAnimationStart(final Animation animation) {
            }
        });
        this.adView.startAnimation(slideDown);
    }*/
    /*private class SampleAdListener extends DefaultAdListener {
        *//**
         * This event is called once an ad loads successfully.
         *//*
        @Override
        public void onAdLoaded(final Ad ad, final AdProperties adProperties) {
            Log.i(LOG_TAG, adProperties.getAdType().toString() + " ad loaded successfully.");
            // If there is an ad currently being displayed, swap the ad that just loaded with current ad.
            // Otherwise simply display the ad that just loaded.
            if (MainActivity.this.adView != null) {
                swapCurrentAd();
            } else {
                // This is the first time we're loading an ad, so set the
                // current ad view to the ad we just loaded and set the next to null
                // so that we can load a new ad in the background.
                showCurrentAd();
            }
        }

        *//**
         * This event is called if an ad fails to load.
         *//*
        @Override
        public void onAdFailedToLoad(final Ad ad, final AdError error) {
            Log.w(LOG_TAG, "Ad failed to load. Code: " + error.getCode() + ", Message: " + error.getMessage());
        }

        *//**
         * This event is called after a rich media ad expands.
         *//*
        @Override
        public void onAdExpanded(final Ad ad) {
            Log.i(LOG_TAG, "Ad expanded.");
            // You may want to pause your activity here.
        }

        *//**
         * This event is called after a rich media ad has collapsed from an expanded state.
         *//*
        @Override
        public void onAdCollapsed(final Ad ad) {
            Log.i(LOG_TAG, "Ad collapsed.");
            // Resume your activity here, if it was paused in onAdExpanded.
        }
    }*/
    private boolean isNotRatedApp() {

        return !getPreferences(MODE_PRIVATE).getBoolean(STATUS, false);
    }

    private void gotoMarket() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("amzn://apps/android?p=ua.com.olga110183.shapes15")));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.amazon.com/gp/mas/dl/android?p=ua.com.olga110183.shapes15")));
        }
    }

    /*@Override
    public void onDestroy() {
        super.onDestroy();
        if (this.adView != null)
            this.adView.destroy();
    }*/

    @Override
    public void onBackPressed() {
        if (isNotRatedApp()) {
            android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(MainActivity.this);
            dialog.setTitle(getResources().getString(R.string.dialog_title));
            dialog.setPositiveButton(R.string.btn_positive, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    gotoMarket();
                    preferences = getPreferences(MODE_PRIVATE);
                    SharedPreferences.Editor ed = preferences.edit();
                    ed.putBoolean(STATUS, true);
                    ed.commit();
                    finish();
                }
            });
            dialog.setNegativeButton(R.string.btn_negative, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            dialog.show();
        } else finish();
    }

}
