package au.com.recursive.android.littleisland;

import android.util.DisplayMetrics;
import android.view.WindowManager;
import org.andengine.engine.options.EngineOptions;
import org.andengine.extension.ui.livewallpaper.BaseLiveWallpaperService;

import java.io.IOException;
import java.io.InputStream;
import org.andengine.engine.LimitedFPSEngine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.SmoothCamera;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.tmx.TMXLayer;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.extension.tmx.TMXLoader;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.extension.tmx.util.exception.TMXLoadException;
import org.andengine.opengl.texture.Texture;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.util.debug.Debug;

public class TMXWallpaperService extends BaseLiveWallpaperService {
    
    private static int CAMERA_WIDTH = 480;
    private static int CAMERA_HEIGHT = 720;
    private Camera camera;
    private Scene scene;
    private TiledTextureRegion seaRegion;
    private BuildableBitmapTextureAtlas tileTexture;
    private Sprite bgSprite;
    private TMXTiledMap map;

    private static int TILESIZE = 38;
    
    @Override
    public org.andengine.engine.Engine onCreateEngine(EngineOptions pEngineOptions) {
        return new LimitedFPSEngine(pEngineOptions, 30);
    }

    @Override
    public EngineOptions onCreateEngineOptions() {

        final DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager)getSystemService(WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        wm.getDefaultDisplay().getRotation();
        CAMERA_WIDTH = displayMetrics.widthPixels;
        CAMERA_HEIGHT = displayMetrics.heightPixels;
        this.camera = new SmoothCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT, 300, 300, 20);
        
        return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED,
            new FillResolutionPolicy(), this.camera);
    }

    public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws IOException {
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
        tileTexture = new BuildableBitmapTextureAtlas(getTextureManager(), TILESIZE, TILESIZE, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        tileTexture.addEmptyTextureAtlasSource(0,0, TILESIZE, TILESIZE);
        seaRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(tileTexture, this.getAssets(), "tiles1.png", 2, 1);
        tileTexture.load();    
        
        pOnCreateResourcesCallback.onCreateResourcesFinished();
    }

    public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws IOException {
        scene = new Scene();
        //scene.setBackground(new Background(0.0f, 20.0f, 40.0f)); 
        
        try {
            final TMXLoader tmxLoader = new TMXLoader(this.getAssets(), this.getTextureManager(), TextureOptions.BILINEAR_PREMULTIPLYALPHA, this.getVertexBufferObjectManager());            
            this.map = tmxLoader.loadFromAsset("maps/sea.tmx");

        } catch (final TMXLoadException e) {
            Debug.e(e);
        }

        final TMXLayer tmxLayer = this.map.getTMXLayers().get(0);
        scene.attachChild(tmxLayer);
        
        pOnCreateSceneCallback.onCreateSceneFinished(scene);
    }

    public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws IOException {
        
//        for (int rows=0; rows < 32; rows++){
//            for (int cols=0; cols < 32; cols++){
//                Sprite s = new Sprite(cols * TILESIZE, rows * TILESIZE,this.seaRegion,this.getVertexBufferObjectManager());
//                pScene.attachChild(s);
//            }
//        }
        pOnPopulateSceneCallback.onPopulateSceneFinished();
    }

    @Override
    protected void onOffsetsChanged(float pXOffset, float pYOffset, float pXOffsetStep, float pYOffsetStep, int pXPixelOffset, int pYPixelOffset) {
        camera.setCenter((CAMERA_WIDTH - pXPixelOffset)/2, CAMERA_HEIGHT/2);
        super.onOffsetsChanged(pXOffset, pYOffset, pXOffsetStep, pYOffsetStep, pXPixelOffset, pYPixelOffset); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
