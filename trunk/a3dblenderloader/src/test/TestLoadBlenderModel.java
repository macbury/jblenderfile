package test;

import com.ardor3d.extension.model.blender.BlenderImporter;
import com.ardor3d.extension.model.blender.BlenderStorage;
import com.ardor3d.extension.model.blender.Log;
import com.ardor3d.framework.Canvas;
import com.ardor3d.framework.DisplaySettings;
import com.ardor3d.framework.FrameHandler;
import com.ardor3d.framework.Scene;
import com.ardor3d.framework.Updater;
import com.ardor3d.framework.lwjgl.LwjglCanvas;
import com.ardor3d.framework.lwjgl.LwjglCanvasRenderer;
import com.ardor3d.image.util.AWTImageLoader;
import com.ardor3d.input.Key;
import com.ardor3d.input.PhysicalLayer;
import com.ardor3d.input.control.FirstPersonControl;
import com.ardor3d.input.logical.InputTrigger;
import com.ardor3d.input.logical.KeyPressedCondition;
import com.ardor3d.input.logical.LogicalLayer;
import com.ardor3d.input.logical.TriggerAction;
import com.ardor3d.input.logical.TwoInputStates;
import com.ardor3d.input.lwjgl.LwjglControllerWrapper;
import com.ardor3d.input.lwjgl.LwjglKeyboardWrapper;
import com.ardor3d.input.lwjgl.LwjglMouseManager;
import com.ardor3d.input.lwjgl.LwjglMouseWrapper;
import com.ardor3d.intersection.PickResults;
import com.ardor3d.light.DirectionalLight;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Ray3;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.Renderer;
import com.ardor3d.renderer.TextureRendererFactory;
import com.ardor3d.renderer.lwjgl.LwjglTextureRendererProvider;
import com.ardor3d.renderer.state.LightState;
import com.ardor3d.renderer.state.ZBufferState;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.util.ContextGarbageCollector;
import com.ardor3d.util.GameTaskQueue;
import com.ardor3d.util.GameTaskQueueManager;
import com.ardor3d.util.ReadOnlyTimer;
import com.ardor3d.util.Timer;
import com.ardor3d.util.resource.URLResourceSource;
import java.io.File;
import java.net.MalformedURLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestLoadBlenderModel implements Runnable, Updater, Scene {

    final Timer timer = new Timer();
    final FrameHandler frameHandler = new FrameHandler(timer);
    final DisplaySettings settings;
    final LwjglCanvas canvas;
    final PhysicalLayer physicalLayer;
    final LwjglMouseManager mouseManager;
    Vector3 worldUp = new Vector3(0, 1, 0);
    final Node root = new Node();
    final LogicalLayer logicalLayer = new LogicalLayer();
    boolean exit;
    private final File modelFile;

    public TestLoadBlenderModel(DisplaySettings settings, File modelFile) {
        this.modelFile = modelFile;
        this.settings = settings;
        LwjglCanvasRenderer canvasRenderer = new LwjglCanvasRenderer(this);
        canvas = new LwjglCanvas(canvasRenderer, settings);
        physicalLayer = new PhysicalLayer(new LwjglKeyboardWrapper(), new LwjglMouseWrapper(), new LwjglControllerWrapper(), (LwjglCanvas) canvas);
        mouseManager = new LwjglMouseManager();
        TextureRendererFactory.INSTANCE.setProvider(new LwjglTextureRendererProvider());
        logicalLayer.registerInput(canvas, physicalLayer);
        frameHandler.addUpdater(this);
        frameHandler.addCanvas(canvas);
        canvas.setTitle("TestLoadBlenderModel");
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        Log.enabled = false;
        DisplaySettings settings = new DisplaySettings(1024, 768, 24, 0, 0, 8, 0, 0, false, false);
        TestLoadBlenderModel main = new TestLoadBlenderModel(settings, new File(args[0]));
        main.run();
    }

    public void run() {
        try {
            frameHandler.init();

            while (!exit) {
                frameHandler.updateFrame();
            }
            // grab the graphics context so cleanup will work out.
            canvas.getCanvasRenderer().makeCurrentContext();
            ContextGarbageCollector.doFinalCleanup(canvas.getCanvasRenderer().getRenderer());
            canvas.close();
        } catch (final Throwable t) {
            System.err.println("Throwable caught in MainThread - exiting");
            t.printStackTrace(System.err);
        }
    }

    public void init() {
        registerInputTriggers();

        AWTImageLoader.registerLoader();

        final ZBufferState buf = new ZBufferState();
        buf.setEnabled(true);
        buf.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);
        root.setRenderState(buf);

        LightState lightState = new LightState();
        DirectionalLight point = new DirectionalLight();
        point.setDirection(-1, 0, 0);
        point.setAmbient(ColorRGBA.DARK_GRAY);
        point.setDiffuse(ColorRGBA.GRAY);
        point.setEnabled(true);
        lightState.attach(point);
        lightState.setEnabled(true);
        root.setRenderState(lightState);
        try {
            BlenderStorage storage = new BlenderImporter().load(new URLResourceSource(modelFile.toURI().toURL()));
            List<Node> scenes = storage.getScenes();
            for (Node node : scenes) {
                root.attachChild(node);
            }
            root.updateWorldTransform(true);
            root.updateWorldBound(true);
            root.updateWorldRenderStates(true);
        } catch (MalformedURLException ex) {
            Logger.getLogger(TestLoadBlenderModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void update(ReadOnlyTimer timer) {
        if (canvas.isClosing()) {
            exit = true;
        }
        logicalLayer.checkTriggers(timer.getTimePerFrame());
        GameTaskQueueManager.getManager(canvas.getCanvasRenderer().getRenderContext()).getQueue(GameTaskQueue.UPDATE).execute();
        root.updateGeometricState(timer.getTimePerFrame(), true);
    }

    public PickResults doPick(Ray3 pickRay) {
        return null;
    }

    public boolean renderUnto(Renderer renderer) {
        GameTaskQueueManager.getManager(canvas.getCanvasRenderer().getRenderContext()).getQueue(GameTaskQueue.RENDER).execute(renderer);
        ContextGarbageCollector.doRuntimeCleanup(renderer);
        if (!canvas.isClosing()) {
            renderer.draw(root);
            return true;
        } else {
            return false;
        }
    }

    protected void registerInputTriggers() {
        FirstPersonControl.setupTriggers(logicalLayer, worldUp, true);

        logicalLayer.registerTrigger(new InputTrigger(new KeyPressedCondition(Key.ESCAPE), new TriggerAction() {

            public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
                exit = true;
            }
        }));

    }
}
