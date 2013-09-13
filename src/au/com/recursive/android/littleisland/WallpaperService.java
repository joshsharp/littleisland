package au.com.recursive.android.littleisland;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import org.andengine.engine.options.EngineOptions;
import org.andengine.extension.ui.livewallpaper.BaseLiveWallpaperService;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import org.andengine.engine.LimitedFPSEngine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.SmoothCamera;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.Entity;
import org.andengine.entity.primitive.Gradient;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.opengl.texture.Texture;
import org.andengine.opengl.texture.atlas.TextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.EmptyBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.bitmap.source.decorator.LinearGradientFillBitmapTextureAtlasSourceDecorator;
import org.andengine.opengl.texture.atlas.bitmap.source.decorator.LinearGradientFillBitmapTextureAtlasSourceDecorator.LinearGradientDirection;
import org.andengine.opengl.texture.atlas.bitmap.source.decorator.shape.RectangleBitmapTextureAtlasSourceDecoratorShape;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.util.adt.color.Color;
import org.andengine.util.debug.Debug;

public class WallpaperService extends BaseLiveWallpaperService {
    
    private static int CAMERA_WIDTH = 480;
    private static int CAMERA_HEIGHT = 720;
    private Camera camera;
    private Scene scene;
    int TILEHEIGHT = 30;
    int TILEWIDTH = 30;
    private Gradient[][] grid = new Gradient[TILEHEIGHT][TILEWIDTH];
    BitmapTextureAtlas cloudTexture1, cloudTexture2;
    TextureRegion cloudTextureRegion1, cloudTextureRegion2;
    ArrayList clouds = new ArrayList();
    
    //row base colours. 
    int[] nightRed = {
        15, 15, 15, 15, 20, 30, 50, 
        60, 50, 40, 20, 15, 6,  2,   1, 1,
        1,  1,  1,  1,  1,  1,  1,   0, 0, 0,
        0,  0,  0,  0,
        };
    int[] nightGreen = {
        20, 20, 20, 20, 25, 40, 55,
        55, 50, 40, 25, 20, 18, 10,  5,  5,
        5,  5,  5,  4,  4,  4,  4,   0,  0, 0,
        0,  0,  0,  0,
        };
    int[] nightBlue = {
        15, 15, 15, 20, 25, 40, 58,
        90, 80, 70, 50, 45, 40, 30, 20, 20,
        20, 20, 20, 20, 20, 20, 15, 10, 10, 5,
        0,  0,  0,  0,
        };

//    int[] dayRed = {
//        70, 70, 70, 60, 60, 50, 50,
//        90,  60,  50,  40,  30,  20,  20, 20, 10
//    };
//
//    int[] dayGreen = {
//        135, 130, 120, 110, 110, 100, 100, 
//        150, 120, 100,  90,  80,  70,  70, 70, 50
//    };
//
//    int[] dayBlue = {
//        75, 70, 65, 60, 60, 60, 50, 
//        220, 190, 180, 170, 160, 150, 145, 140, 130
//    };
    
    int[] dayRed = {
        70, 100, 100,  90, 80,  70, 30,
        90,  60,  50,  40,  30,  20,  19, 17, 15,
        14,  13,  12,  11,  10,  10,  9,  9, 8,
         8,   5,   5,   5,   5
    };

    int[] dayGreen = {
        60,  90, 100,  90, 90,  90, 70, 
        150, 120, 100,  90,  80,  75,  70, 65, 62,
        60,  55,  52,   50,  48,  45,  42, 40, 37,
        35,  30,  30,   30,  30
    };

    int[] dayBlue = {
        10,  20,  20,  30, 50,  70, 90, 
        220, 190, 180, 170, 160, 150, 145, 140, 138,
        135, 132, 130, 128, 125, 120, 115, 110, 107, 
        105, 100, 100, 100, 100
    };
    
    int[] sunsetRed = {
        15, 15, 15, 20, 30, 50, 65,
        210, 200, 190, 170, 130, 95, 80, 60, 40,
        38,   35,  33,  30,  26, 22, 20, 18, 16, 
        15,   10,  10,  10,  8, 
    };
    
    int[] sunsetGreen = {
        20, 20, 20, 20, 25, 40, 50, 
        160, 180, 190, 180, 150, 110, 90, 70, 50,
        48,   46,  44,  42,  40,  36, 34, 30, 25, 
        20,   15,  15,  15,  10,  
    };
    
    int[] sunsetBlue = {
        15, 15, 15, 15, 20, 30, 35,
        80, 130, 170, 170, 160, 125, 120, 100, 99,
        98,  95,  93,  91,  90,  88,  86,  82, 75, 
        60,  50,  50,  50,  40, 
    };
    
    Boolean night = false;
    Boolean switchDay = false;
    int timeOfDay = 0;
    Entity stars;
    private static int TILESIZE = 40;
    
    @Override
    public org.andengine.engine.Engine onCreateEngine(EngineOptions pEngineOptions) {
        return new LimitedFPSEngine(pEngineOptions, 20);
    }

    @Override
    public EngineOptions onCreateEngineOptions() {

        final DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager)getSystemService(WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        wm.getDefaultDisplay().getRotation();
        CAMERA_WIDTH = displayMetrics.widthPixels;
        CAMERA_HEIGHT = displayMetrics.heightPixels;
        //TILESIZE = CAMERA_WIDTH / 10 + 2;
        this.camera = new SmoothCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT, 300, 300, 20);
        
        EngineOptions opts = new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED,
            new FillResolutionPolicy(), this.camera);
        opts.getRenderOptions().setDithering(true);
        return opts;
    }

    public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws IOException {
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
        cloudTexture1 = new BitmapTextureAtlas(getTextureManager(), 228, 140, TextureOptions.DEFAULT);
        cloudTextureRegion1 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(cloudTexture1, this, "cloud1.png", 0, 0);
        
        cloudTexture2 = new BitmapTextureAtlas(getTextureManager(), 240, 140, TextureOptions.DEFAULT);
        cloudTextureRegion2 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(cloudTexture2, this, "cloud2.png", 0, 0);
        
        cloudTexture1.load();
        cloudTexture2.load();
        
        
        pOnCreateResourcesCallback.onCreateResourcesFinished();
    }

    public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws IOException {
        scene = new Scene(){
            
            public Boolean redraw  = false;
            private long lastRedraw = System.currentTimeMillis();
            private long lastStars = System.currentTimeMillis();

            @Override
            protected void onManagedUpdate(float pSecondsElapsed) {
                
                long now = System.currentTimeMillis();
                
                if (redraw || (now - lastRedraw > 2000)){
                    lastRedraw = System.currentTimeMillis();
                    //Debug.d("redraw!");
                    updateSceneGrid(this);
                    redraw = false;
                }
                if (night){
                    if (now - lastStars > 500){
                        updateStars(this);
                        lastStars = System.currentTimeMillis();
                    }
                }
                updateSprites(this);
                
                super.onManagedUpdate(pSecondsElapsed); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public boolean onSceneTouchEvent(TouchEvent pSceneTouchEvent) {
                //this.redraw = true;
                Debug.d("touched");
                return super.onSceneTouchEvent(pSceneTouchEvent); //To change body of generated methods, choose Tools | Templates.
            }
            
            
        };
        
        stars = new Entity(25 * TILESIZE / 2,CAMERA_HEIGHT / 2, 25 * TILESIZE,CAMERA_HEIGHT);        
        
        this.getEngine().registerUpdateHandler(new FPSLogger());
        pOnCreateSceneCallback.onCreateSceneFinished(scene);
    }

    public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws IOException {
        this.drawSceneGrid(pScene);
        
        
//        for (int i = 0; i < 4; i++){
//            int maxX = TILESIZE * TILEWIDTH;
//            int minY = TILESIZE * 6 + (TILESIZE * i);
//            int maxY = CAMERA_HEIGHT;
//
//            float x = (float)(Math.random() * (maxX / 4)) + (maxX / 4 * i);
//            float y = (float)(Math.random() * (maxY-minY)) + minY;
//            
//            TextureRegion tx = cloudTextureRegion1;
//            
//            if (Math.random() > 0.5f){
//                tx = cloudTextureRegion2;
//            }
//            Sprite cloud = new Sprite(x,y,tx,this.getVertexBufferObjectManager());
//            cloud.setZIndex(10);
//            cloud.setAlpha(0.7f);
//            pScene.attachChild(cloud);
//            clouds.add(cloud);
//        }
        
        pOnPopulateSceneCallback.onPopulateSceneFinished();
    }
    
    private int getTimeOfDay(){
        Calendar c = Calendar.getInstance(); 
        int hour = c.get(Calendar.HOUR_OF_DAY);
        
        return hour;
        //return 18;
    }
    
    private int getMinute(){
        Calendar c = Calendar.getInstance(); 
        int min = c.get(Calendar.MINUTE);
        return min;
    }
    
    private int[] getTimeOfDayRed(){
        int hour = getTimeOfDay();
        
        if (hour == 18){
            return sunsetRed;
        }
        
        if (hour < 7 || hour >= 19){
            //night
            return nightRed;
        }
        return dayRed;
    }
    
    private int[] getTimeOfDayGreen(){
        int hour = getTimeOfDay();
        
        if (hour == 18){
            return sunsetGreen;
        }
        
        if (hour < 7 || hour >= 19){
            //night
            return nightGreen;
        }
        return dayGreen;
    }
    
    private int[] getTimeOfDayBlue(){
        int hour = getTimeOfDay();
        
        if (hour == 18){
            return sunsetBlue;
        }
        
        if (hour < 7 || hour >= 19){
            //night
            return nightBlue;
        }
        return dayBlue;
    }
    
    private Boolean isNight(){
        int hour = getTimeOfDay();
        if (hour < 7 || hour >= 18){
            return true;
        }
        return false;
    }
    
    public void updateSprites(Scene pScene){
        //move the cloud right
//        for (int i = 0; i < clouds.size(); i++){
//            Sprite cloud = (Sprite)clouds.get(i);
//            float newX = cloud.getX();
//            newX += 0.2;
//            
//            if (isNight()){
//                if (newX > 0){
//                    newX = (float)(Math.random() * 1000 * -1) - 200;
//                }
//            }
//            
//            if (newX + 150 > TILESIZE * TILEWIDTH){
//                newX = -150f;
//                int minY = TILESIZE * 7;
//                int maxY = CAMERA_HEIGHT;
//
//                float y = (float)(Math.random() * (maxY-minY)) + minY;
//                cloud.setY(y);
//            }        
//            cloud.setX(newX);
//        }
    }
    
    public void updateSceneGrid(Scene pScene){
        
        if (isNight()){
            if (!night){
                Log.d("update", "switch!");
                switchDay = true;
                
//                for (int i = 0; i < clouds.size(); i++){
//                    Sprite cloud = (Sprite)clouds.get(i);
//                    cloud.setAlpha(0.5f);
//                }
            }
            night = true;
        } else {
            if (night){
                Log.d("update", "switch!");
                switchDay = true;
                
//                for (int i = 0; i < clouds.size(); i++){
//                    Sprite cloud = (Sprite)clouds.get(i);
//                    cloud.setAlpha(0.7f);
//                }
            }
            night = false;
        }
        
        Boolean stillSwitching = false;
        
        for (int rows=0; rows < TILEHEIGHT; rows++){
            for (int cols=0; cols < TILEWIDTH; cols++){
                Gradient g = grid[rows][cols];
                
                float red, green, blue, red2, green2, blue2 = 0f;
                
                red = (float)((Math.random() * 10 + getTimeOfDayRed()[rows]) / 255);
                green = (float)((Math.random() * 10 + getTimeOfDayGreen()[rows]) / 255);
                blue = (float)((Math.random() * 10 + getTimeOfDayBlue()[rows]) / 255);

                red2 = Math.max(red - 0.03f, 0);
                green2 = Math.max(green - 0.03f, 0);
                blue2 = Math.max(blue - 0.03f, 0);
                
                if (switchDay){
                    float curRed, curGreen, curBlue;
                    curRed = g.getFromRed();
                    curGreen = g.getFromGreen();
                    curBlue = g.getFromBlue();
                    
                    float diff = 0;
                    diff += Math.abs(red - curRed);
                    diff += Math.abs(green - curGreen);
                    diff += Math.abs(blue - curBlue);
                    
                    if (diff > 0.3f){
                        Log.d("switch","still switching");
                        stillSwitching = true;
                        if (Math.random() > 0.5){
                            g.setGradientColor((red + curRed) / 2, (red2 + g.getToRed()) / 2, 
                                (green + curGreen) / 2, (green2 + g.getToGreen()) / 2, 
                                (blue + curBlue) / 2, (blue2 + g.getToBlue()) / 2);
                        }
                    }
                } else {
                    //only random squares get an update?
                    if (Math.random() > 0.9){

                        g.setGradientColor(red, red2, green, green2, blue, blue2);

                    }
                }  
            }
        }
        
        if (switchDay && night){
            if (!stars.hasParent()){
                pScene.attachChild(stars);
                stars.setZIndex(8);

                //draw some stars!
                int minY = TILESIZE * 7 - (TILESIZE / 2);
                int maxY = CAMERA_HEIGHT;
                for (int i = 0; i < 150; i++){
                    int y = (int)(Math.random() * (maxY-minY)) + minY;
                    int x = (int)(Math.random() * 25 * TILESIZE);
                    int size = 2;
                    if (Math.random() <= 0.4f){
                        size = 4;
                    }

                    Rectangle r = new Rectangle(x,y,size,size,this.getVertexBufferObjectManager());
                    r.setColor(1.0f, 1.0f, 1.0f, (float)(Math.random()*0.7 + 0.1f));
                    stars.attachChild(r);
                }
            }
        }
        if (switchDay && !night){
            stars.detachChildren();
            pScene.detachChild(stars);
        }
        if (!stillSwitching){
            switchDay = false;
        }
    }
    
    public void updateStars(Scene pScene){
        if (isNight()){
            //animate some stars
            for (int i=0; i < stars.getChildCount(); i++){
                Rectangle r = (Rectangle)stars.getChildByIndex(i);
                float alpha = r.getAlpha();
                
                float twinkle = alpha - 0.2f + (float)(Math.random() * 0.4);
                
                if (getTimeOfDay() == 18){
                    //only a few stars visible
                    if (i % 90 < getMinute()){
                        //you are allowed twinkle
                        r.setAlpha(Math.max(0.5f,Math.min(twinkle,0.7f)));
                    } else {
                        //you are not
                        r.setAlpha(0.0f);
                    }
                } else {
                    r.setAlpha(Math.max(0.5f,Math.min(twinkle,0.7f)));
                }              
                
            }
        }
    }
    
    public void drawSceneGrid(Scene pScene){
        pScene.detachChildren();
        
        //first we initialise the array
        for (int rows=0; rows < TILEHEIGHT; rows++){
            for (int cols=0; cols < TILEWIDTH; cols++){
                
                float red, green, blue, red2, green2, blue2 = 0f;
                
                red = (float)((Math.random() * 10 + getTimeOfDayRed()[rows]) / 255);
                green = (float)((Math.random() * 10 + getTimeOfDayGreen()[rows]) / 255);
                blue = (float)((Math.random() * 10 + getTimeOfDayBlue()[rows]) / 255);

                red2 = Math.max(red - 0.03f, 0);
                green2 = Math.max(green - 0.03f, 0);
                blue2 = Math.max(blue - 0.03f, 0);
                
                //then we apply it        
                Gradient g = new Gradient(cols * TILESIZE - (TILESIZE / 2),rows * TILESIZE - (TILESIZE / 2),TILESIZE,TILESIZE,this.getVertexBufferObjectManager());
                g.setGradientColor(red, red2, 
                        green,green2, 
                        blue, blue2);
                //g.setGradientColor(red, red, green, green, blue, blue);
                g.setGradientAngle(290);
                g.setGradientDitherEnabled(true);
                grid[rows][cols] = g;
                pScene.attachChild(g);
                
            }
        }
        
        
    }

    @Override
    protected void onOffsetsChanged(float pXOffset, float pYOffset, float pXOffsetStep, float pYOffsetStep, int pXPixelOffset, int pYPixelOffset) {
        camera.setCenter((CAMERA_WIDTH - pXPixelOffset)/2, CAMERA_HEIGHT/2);
        super.onOffsetsChanged(pXOffset, pYOffset, pXOffsetStep, pYOffsetStep, pXPixelOffset, pYPixelOffset); //To change body of generated methods, choose Tools | Templates.
    }
   
    
}
