/* Copyright (C) 1999-2013 by Peter Eastman
   Copyright (C) 2020 by Jon Taylor

   This program is free software; you can redistribute it and/or modify it under the
   terms of the GNU General Public License as published by the Free Software
   Foundation; either version 2 of the License, or (at your option) any later version.

   This program is distributed in the hope that it will be useful, but WITHOUT ANY
   WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
   PARTICULAR PURPOSE.  See the GNU General Public License for more details. */

package armadesignstudio;

import armadesignstudio.animation.*;
import armadesignstudio.image.*;
import armadesignstudio.image.filter.*;
import armadesignstudio.material.*;
import armadesignstudio.math.*;
import armadesignstudio.object.*;
import armadesignstudio.procedural.*;
import armadesignstudio.script.*;
import armadesignstudio.texture.*;
import armadesignstudio.ui.*;
import armadesignstudio.keystroke.*;
import armadesignstudio.view.*;
import buoy.widget.*;
import buoy.xml.*;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;
import java.lang.reflect.*;
import javax.swing.*;

/** This is the main class for Art of Illusion.  All of its methods and variables are static,
    so no instance of this class ever gets created.  It starts up the application, and
    maintains global variables. */

public class ArmaDesignStudio
{
  public static final String APP_DIRECTORY, PLUGIN_DIRECTORY;
  public static final String TOOL_SCRIPT_DIRECTORY, OBJECT_SCRIPT_DIRECTORY, STARTUP_SCRIPT_DIRECTORY;
  public static final ImageIcon APP_ICON;
  public static Font defaultFont;
//  private static String currentDirectory;
  public static int standardDialogInsets = 0;
  private static ApplicationPreferences preferences;
  private static ObjectInfo clipboardObject[];
  private static Texture clipboardTexture[];
  private static Material clipboardMaterial[];
  private static ImageMap clipboardImage[];
  private static ArrayList<EditingWindow> windows = new ArrayList<EditingWindow>();
  private static HashMap<String, String> classTranslations = new HashMap<String, String>();
  private static int numNewWindows = 0;

  static
  {
    // A clever trick for getting the location of the jar file, which David Smiley
    // posted to the Apple java-dev mailing list on April 14, 2002.  It works on
    // most, but not all, platforms, so in case of a problem we fall back to using
    // user.dir.

    String dir = System.getProperty("user.dir");
    try
      {
        URL url = ArmaDesignStudio.class.getResource("/armadesignstudio/ArmaDesignStudio.class");
        if (url.toString().startsWith("jar:"))
          {
            String furl = url.getFile();
            furl = furl.substring(0, furl.indexOf('!'));
            dir = new File(new URL(furl).getFile()).getParent();
            if (!new File(dir).exists())
              dir = System.getProperty("user.dir");
          }
      }
      catch (Exception ex)
      {
      }

    // Set up the standard directories.

    APP_DIRECTORY = dir;
    PLUGIN_DIRECTORY = new File(APP_DIRECTORY, "Plugins").getAbsolutePath();
    File scripts = new File(APP_DIRECTORY, "Scripts");
    TOOL_SCRIPT_DIRECTORY = new File(scripts, "Tools").getAbsolutePath();
    OBJECT_SCRIPT_DIRECTORY = new File(scripts, "Objects").getAbsolutePath();
    STARTUP_SCRIPT_DIRECTORY = new File(scripts, "Startup").getAbsolutePath();

    // Load the application's icon.

    ImageIcon icon = new IconResource("armadesignstudio/Icons/appIcon.png");
    APP_ICON = (icon.getIconWidth() == -1 ? null : icon);

    // Build a table of classes which have moved.

    classTranslations.put("armadesignstudio.tools.CSGObject", "armadesignstudio.object.CSGObject");
    classTranslations.put("armadesignstudio.Cube", "armadesignstudio.object.Cube");
    classTranslations.put("armadesignstudio.Curve", "armadesignstudio.object.Curve");
    classTranslations.put("armadesignstudio.Cylinder", "armadesignstudio.object.Cylinder");
    classTranslations.put("armadesignstudio.DirectionalLight", "armadesignstudio.object.DirectionalLight");
    classTranslations.put("armadesignstudio.NullObject", "armadesignstudio.object.NullObject");
    classTranslations.put("armadesignstudio.PointLight", "armadesignstudio.object.PointLight");
    classTranslations.put("armadesignstudio.SceneCamera", "armadesignstudio.object.SceneCamera");
    classTranslations.put("armadesignstudio.Sphere", "armadesignstudio.object.Sphere");
    classTranslations.put("armadesignstudio.SplineMesh", "armadesignstudio.object.SplineMesh");
    classTranslations.put("armadesignstudio.SpotLight", "armadesignstudio.object.SpotLight");
    classTranslations.put("armadesignstudio.TriangleMesh", "armadesignstudio.object.TriangleMesh");
    classTranslations.put("armadesignstudio.Tube", "armadesignstudio.object.Tube");
    classTranslations.put("armadesignstudio.CylindricalMapping", "armadesignstudio.texture.CylindricalMapping");
    classTranslations.put("armadesignstudio.ImageMapTexture", "armadesignstudio.texture.ImageMapTexture");
    classTranslations.put("armadesignstudio.LayeredMapping", "armadesignstudio.texture.LayeredMapping");
    classTranslations.put("armadesignstudio.LayeredTexture", "armadesignstudio.texture.LayeredTexture");
    classTranslations.put("armadesignstudio.LinearMapping3D", "armadesignstudio.texture.LinearMapping3D");
    classTranslations.put("armadesignstudio.procedural.ProceduralTexture2D", "armadesignstudio.texture.ProceduralTexture2D");
    classTranslations.put("armadesignstudio.procedural.ProceduralTexture3D", "armadesignstudio.texture.ProceduralTexture3D");
    classTranslations.put("armadesignstudio.ProjectionMapping", "armadesignstudio.texture.ProjectionMapping");
    classTranslations.put("armadesignstudio.SphericalMapping", "armadesignstudio.texture.SphericalMapping");
    classTranslations.put("armadesignstudio.UniformMapping", "armadesignstudio.texture.UniformMapping");
    classTranslations.put("armadesignstudio.UniformTexture", "armadesignstudio.texture.UniformTexture");
    classTranslations.put("armadesignstudio.LinearMaterialMapping", "armadesignstudio.material.LinearMaterialMapping");
    classTranslations.put("armadesignstudio.procedural.ProceduralMaterial3D", "armadesignstudio.material.ProceduralMaterial3D");
    classTranslations.put("armadesignstudio.UniformMaterial", "armadesignstudio.material.UniformMaterial");
    classTranslations.put("armadesignstudio.UniformMaterialMapping", "armadesignstudio.material.UniformMaterialMapping");
    classTranslations.put("armadesignstudio.tools.tapDesigner.TapDesignerObjectCollection", "armadesignstudio.tapDesigner.TapDesignerObjectCollection");
    classTranslations.put("armadesignstudio.tools.tapDesigner.TapTube", "armadesignstudio.tapDesigner.TapTube");
    classTranslations.put("armadesignstudio.tools.tapDesigner.TapSplineMesh", "armadesignstudio.tapDesigner.TapSplineMesh");
    classTranslations.put("armadesignstudio.tools.tapDesigner.TapObject", "armadesignstudio.tapDesigner.TapObject");
    classTranslations.put("armadesignstudio.tools.tapDesigner.TapLeaf", "armadesignstudio.tapDesigner.TapLeaf");
  }

  public static void main(String args[])
  {
    Translate.setLocale(Locale.getDefault());
    try
    {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch (Exception ex)
    {
    }
    JPopupMenu.setDefaultLightWeightPopupEnabled(false);
    ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);
    try
    {
      // Due to the strange way PopupFactory is implemented, we need to use reflection to make sure
      // we *really* get heavyweight popups from the very start.

      Class popup = PopupFactory.class;
      Field heavyweight = popup.getDeclaredField("HEAVY_WEIGHT_POPUP");
      Method setPopupType = popup.getDeclaredMethod("setPopupType", Integer.TYPE);
      heavyweight.setAccessible(true);
      setPopupType.setAccessible(true);
      setPopupType.invoke(PopupFactory.getSharedInstance(), heavyweight.get(null));
    }
    catch (Exception ex)
    {
      // Don't worry about it.
    }
    TitleWindow title = new TitleWindow();
    PluginRegistry.addCategory(Plugin.class);
    PluginRegistry.addCategory(Renderer.class);
    PluginRegistry.addCategory(Translator.class);
    PluginRegistry.addCategory(ModellingTool.class);
    PluginRegistry.addCategory(Texture.class);
    PluginRegistry.addCategory(Material.class);
    PluginRegistry.addCategory(TextureMapping.class);
    PluginRegistry.addCategory(MaterialMapping.class);
    PluginRegistry.addCategory(ImageFilter.class);
    PluginRegistry.addCategory(armadesignstudio.procedural.Module.class);
    PluginRegistry.registerPlugin(new UniformTexture());
    PluginRegistry.registerPlugin(new ImageMapTexture());
    PluginRegistry.registerPlugin(new ProceduralTexture2D());
    PluginRegistry.registerPlugin(new ProceduralTexture3D());
    PluginRegistry.registerPlugin(new UniformMaterial());
    PluginRegistry.registerPlugin(new ProceduralMaterial3D());
    PluginRegistry.registerPlugin(new UniformMapping(null, null));
    PluginRegistry.registerPlugin(new ProjectionMapping(null, null));
    PluginRegistry.registerPlugin(new CylindricalMapping(null, null));
    PluginRegistry.registerPlugin(new SphericalMapping(null, null));
    PluginRegistry.registerPlugin(new UVMapping(null, null));
    PluginRegistry.registerPlugin(new LinearMapping3D(null, null));
    PluginRegistry.registerPlugin(new LinearMaterialMapping(null, null));
    PluginRegistry.registerPlugin(new BrightnessFilter());
    PluginRegistry.registerPlugin(new SaturationFilter());
    PluginRegistry.registerPlugin(new ExposureFilter());
    PluginRegistry.registerPlugin(new TintFilter());
    PluginRegistry.registerPlugin(new BlurFilter());
    PluginRegistry.registerPlugin(new GlowFilter());
    PluginRegistry.registerPlugin(new OutlineFilter());
    PluginRegistry.registerPlugin(new NoiseReductionFilter());
    PluginRegistry.registerPlugin(new DepthOfFieldFilter());
    PluginRegistry.registerResource("TranslateBundle", "armadesignstudio", ArmaDesignStudio.class.getClassLoader(), "armadesignstudio", null);
    PluginRegistry.registerResource("UITheme", "default", ArmaDesignStudio.class.getClassLoader(), "armadesignstudio/Icons/defaultTheme.xml", null);
    PluginRegistry.scanPlugins();
    ThemeManager.initThemes();
    preferences = new ApplicationPreferences();
    KeystrokeManager.loadRecords();
    ViewerCanvas.addViewerControl(new ViewerOrientationControl());
    ViewerCanvas.addViewerControl(new ViewerPerspectiveControl());
    ViewerCanvas.addViewerControl(new ViewerScaleControl());
      
      ViewerCanvas.addViewerControl(new ViewerFrontButtonControl()); // Only applies to the first position ViewerCanvas->SceneViewer (filtered in ViewerCanvas.buildChoices)
      ViewerCanvas.addViewerControl(new ViewerLeftButtonControl());
      ViewerCanvas.addViewerControl(new ViewerTopButtonControl());
      
      
    List plugins = PluginRegistry.getPlugins(Plugin.class);
    for (int i = 0; i < plugins.size(); i++)
    {
      try
      {
        ((Plugin) plugins.get(i)).processMessage(Plugin.APPLICATION_STARTING, new Object [0]);
      }
      catch (Throwable tx)
      {
        tx.printStackTrace();
        String name = plugins.get(i).getClass().getName();
        name = name.substring(name.lastIndexOf('.')+1);
        new BStandardDialog("", UIUtilities.breakString(Translate.text("pluginInitError", name)), BStandardDialog.ERROR).showMessageDialog(null);
      }
    }
    for (int i = 0; i < args.length; i++)
    {
      try
      {
        newWindow(new Scene(new File(args[i]), true));
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
      }
    }
    runStartupScripts();
    if (numNewWindows == 0)
      newWindow();
    title.dispose();
  }

  /** Get the complete version number of Arma Design Studio. */

  public static String getVersion()
  {
    return getMajorVersion()+".2";
  }

  /** Get the major part of the version number of Arma Design Studio. */

  public static String getMajorVersion()
  {
    return "3.0";
  }

  /** Get the application preferences object. */

  public static ApplicationPreferences getPreferences()
  {
    return preferences;
  }

  /** Create a new Scene, and display it in a window. */

  public static void newWindow()
  {
    Scene theScene = new Scene();
    CoordinateSystem coords = new CoordinateSystem(new Vec3(0.0, 0.0, Camera.DEFAULT_DISTANCE_TO_SCREEN), new Vec3(0.0, 0.0, -1.0), Vec3.vy());
    ObjectInfo info = new ObjectInfo(new SceneCamera(), coords, "Camera 1");
    info.addTrack(new PositionTrack(info), 0);
    info.addTrack(new RotationTrack(info), 1);
    theScene.addObject(info, null);
    info = new ObjectInfo(new DirectionalLight(new RGBColor(1.0f, 1.0f, 1.0f), 0.8f), coords.duplicate(), "Light 1");
    info.addTrack(new PositionTrack(info), 0);
    info.addTrack(new RotationTrack(info), 1);
    theScene.addObject(info, null);
    newWindow(theScene);
  }

  /** Create a new window for editing the specified scene. */

  public static void newWindow(final Scene theScene)
  {
    // New windows should always be created on the event thread.

    numNewWindows++;
    SwingUtilities.invokeLater(new Runnable() {
      public void run()
      {
        LayoutWindow fr = new LayoutWindow(theScene);
        windows.add(fr);
        List plugins = PluginRegistry.getPlugins(Plugin.class);
        for (int i = 0; i < plugins.size(); i++)
        {
          try
          {
            ((Plugin) plugins.get(i)).processMessage(Plugin.SCENE_WINDOW_CREATED, new Object [] {fr});
          }
          catch (Throwable tx)
          {
            tx.printStackTrace();
            String name = plugins.get(i).getClass().getName();
            name = name.substring(name.lastIndexOf('.')+1);
            new BStandardDialog("", UIUtilities.breakString(Translate.text("pluginNotifyError", name)), BStandardDialog.ERROR).showMessageDialog(null);
          }
        }
        fr.setVisible(true);
        fr.arrangeDockableWidgets();

        // If the user opens a file immediately after running the program, close the empty
        // scene window.

        for (int i = windows.size()-2; i >= 0; i--)
          if (windows.get(i) instanceof LayoutWindow)
          {
            LayoutWindow win = (LayoutWindow) windows.get(i);
            if (win.getScene().getName() == null && !win.isModified())
              closeWindow(win);
          }
      }
    });
  }

  /** Add a window to the list of open windows. */

  public static void addWindow(EditingWindow win)
  {
    windows.add(win);
  }

  /** Close a window. */

  public static void closeWindow(EditingWindow win)
  {
    if (win.confirmClose())
      {
        windows.remove(win);
        if (win instanceof LayoutWindow)
        {
          List plugins = PluginRegistry.getPlugins(Plugin.class);
          for (int i = 0; i < plugins.size(); i++)
          {
            try
            {
              ((Plugin) plugins.get(i)).processMessage(Plugin.SCENE_WINDOW_CLOSING, new Object [] {win});
            }
            catch (Throwable tx)
            {
              tx.printStackTrace();
              String name = plugins.get(i).getClass().getName();
              name = name.substring(name.lastIndexOf('.')+1);
              new BStandardDialog("", UIUtilities.breakString(Translate.text("pluginNotifyError", name)), BStandardDialog.ERROR).showMessageDialog(null);
            }
          }
        }
      }
    if (windows.size() ==  0)
      quit();
  }

  /** Get a list of all open windows. */

  public static EditingWindow[] getWindows()
  {
    return windows.toArray(new EditingWindow[windows.size()]);
  }

  /** Quit Art of Illusion. */

  public static void quit()
  {
    for (int i = windows.size()-1; i >= 0; i--)
    {
      EditingWindow win = windows.get(i);
      closeWindow(win);
      if (windows.contains(win))
        return;
    }
    List plugins = PluginRegistry.getPlugins(Plugin.class);
    for (int i = 0; i < plugins.size(); i++)
    {
      try
      {
        ((Plugin) plugins.get(i)).processMessage(Plugin.APPLICATION_STOPPING, new Object [0]);
      }
      catch (Throwable tx)
      {
        tx.printStackTrace();
        String name = plugins.get(i).getClass().getName();
        name = name.substring(name.lastIndexOf('.')+1);
        new BStandardDialog("", UIUtilities.breakString(Translate.text("pluginNotifyError", name)), BStandardDialog.ERROR).showMessageDialog(null);
      }
    }
    System.exit(0);
  }

  /** Execute all startup scripts. */

  private static void runStartupScripts()
  {
    String files[] = new File(STARTUP_SCRIPT_DIRECTORY).list();
    HashMap<String, Object> variables = new HashMap<String, Object>();
    if (files != null)
      for (String file : files)
      {
        try
        {
          String language = ScriptRunner.getLanguageForFilename(file);
          try
          {
            String script = loadFile(new File(STARTUP_SCRIPT_DIRECTORY, file));
            ScriptRunner.executeScript(language, script, variables);
          }
          catch (IOException ex)
          {
            ex.printStackTrace();
          }
        }
        catch (IllegalArgumentException ex)
        {
          // This file isn't a known scripting language.
        }
      }
  }

  /** Get a class specified by name.  This checks both the system classes, and all plugins.
      It also accounts for classes which changed packages in version 1.3. */

  public static Class getClass(String name) throws ClassNotFoundException
  {
    try
    {
      return lookupClass(name);
    }
    catch (ClassNotFoundException ex)
    {
      int i = name.indexOf('$');
      if (i == -1)
      {
        String newName = classTranslations.get(name);
        if (newName == null)
          throw ex;
        return lookupClass(newName);
      }
      String newName = classTranslations.get(name.substring(0, i));
      if (newName == null)
        throw ex;
      return lookupClass(newName+name.substring(i));
    }
  }

  private static Class lookupClass(String name) throws ClassNotFoundException
  { 
    //System.out.println("lookupClass: " + name);
    
    // artofillusion.texture.UniformTexture
    if(name.contains("artofillusion.")){
      name = "armadesignstudio." + name.substring(14);	
    }

    try
    {
      return Class.forName(name);
    }
    catch (ClassNotFoundException ex)
    {
    }
    List pluginLoaders = PluginRegistry.getPluginClassLoaders();
    for (int i = 0; i < pluginLoaders.size(); i++)
    {
      try
      {
        return ((ClassLoader) pluginLoaders.get(i)).loadClass(name);
      }
      catch (ClassNotFoundException ex)
      {
        if (i == pluginLoaders.size()-1)
          throw ex;
      }
    }
    return null;
  }

  /** This is a utility routine which loads a file from disk. */

  public static String loadFile(File f) throws IOException
  {
    BufferedReader in = new BufferedReader(new FileReader(f));
    StringBuffer buf = new StringBuffer();
    int c;
    while ((c = in.read()) != -1)
      buf.append((char) c);
    in.close();
    return buf.toString();
  }

  /** Save a scene to a file.  This method returns true if the scene is successfully saved,
      false if an error occurs. */

  public static boolean saveScene(Scene sc, LayoutWindow fr)
  {
    // Create the file.

    try
    {
      File f = new File(sc.getDirectory(), sc.getName());
      sc.writeToFile(f);
      List plugins = PluginRegistry.getPlugins(Plugin.class);
      for (int i = 0; i < plugins.size(); i++)
      {
          System.out.println("save plugin: " + i + " " );
          // ((Plugin) plugins.get(i))
          
        try
        {
          ((Plugin) plugins.get(i)).processMessage(Plugin.SCENE_SAVED, new Object [] {f, fr});
        }
        catch (Throwable tx)
        {
          tx.printStackTrace();
          String name = plugins.get(i).getClass().getName();
          name = name.substring(name.lastIndexOf('.')+1);
          new BStandardDialog("", UIUtilities.breakString(Translate.text("pluginNotifyError", name)), BStandardDialog.ERROR).showMessageDialog(null);
        }
      }
      RecentFiles.addRecentFile(f);
    }
    catch (IOException ex)
    {
      new BStandardDialog("", new String [] {Translate.text("errorSavingScene"), ex.getMessage() == null ? "" : ex.getMessage()}, BStandardDialog.ERROR).showMessageDialog(fr);
      return false;
    }
    return true;
  }

  /** Prompt the user to select a scene file, then open a new window containing it.  The BFrame is used for
      displaying dialogs. */

  public static void openScene(BFrame fr)
  {
    BFileChooser fc = new BFileChooser(BFileChooser.OPEN_FILE, Translate.text("openScene"));
    if (getCurrentDirectory() != null)
      fc.setDirectory(new File(getCurrentDirectory()));
    if (!fc.showDialog(fr))
      return;
    setCurrentDirectory(fc.getDirectory().getAbsolutePath());
    openScene(fc.getSelectedFile(), fr);
  }

  /** Load a scene from a file, and open a new window containing it.  The BFrame is used for
      displaying dialogs. */

  public static void openScene(File f, BFrame fr)
  {
    // Open the file and read the scene.
    
    //System.out.println("Open file: " + f.getPath() + " " + f.getName());
    //System.out.println("Open file: " + f.getPath());

    try
    {
      Scene sc = new Scene(f, true);
      if (sc.errorsOccurredInLoading())
        new BStandardDialog("", new Object[] {UIUtilities.breakString(Translate.text("errorLoadingScenePart")), new BScrollPane(new BTextArea(sc.getLoadingErrors()))}, BStandardDialog.ERROR).showMessageDialog(fr);
      newWindow(sc);
      RecentFiles.addRecentFile(f);
    }
    catch (InvalidObjectException ex)
    {
      new BStandardDialog("", UIUtilities.breakString(Translate.text("errorLoadingWholeScene")), BStandardDialog.ERROR).showMessageDialog(fr);
    }
    catch (IOException ex)
    {
      new BStandardDialog("", new String [] {Translate.text("errorLoadingFile"), ex.getMessage() == null ? "" : ex.getMessage()}, BStandardDialog.ERROR).showMessageDialog(fr);
    }
  }

  /** Copy a list of objects to the clipboard, so they can be pasted into either the same scene or a
      different one. */

  public static void copyToClipboard(ObjectInfo obj[], Scene scene)
  {
    // First make a list of all textures used by the objects.

    ArrayList<Texture> textures = new ArrayList<Texture>();
    for (int i = 0; i < obj.length; i++)
      {
        Texture tex = obj[i].getObject().getTexture();
        if (tex instanceof LayeredTexture)
          {
            LayeredMapping map = (LayeredMapping) obj[i].getObject().getTextureMapping();
            Texture layer[] = map.getLayers();
            for (int j = 0; j < layer.length; j++)
              {
                Texture dup = layer[j].duplicate();
                dup.setID(layer[j].getID());
                textures.add(dup);
                map.setLayer(j, dup);
                map.setLayerMapping(j, map.getLayerMapping(j).duplicate(obj[i].getObject(), dup));
              }
          }
        else if (tex != null)
          {
            Texture dup = tex.duplicate();
            dup.setID(tex.getID());
            textures.add(dup);
            obj[i].getObject().setTexture(dup, obj[i].getObject().getTextureMapping().duplicate(obj[i].getObject(), dup));
          }
      }

    // Next make a list of all materials used by the objects.

    ArrayList<Material> materials = new ArrayList<Material>();
    for (int i = 0; i < obj.length; i++)
      {
        Material mat = obj[i].getObject().getMaterial();
        if (mat != null)
          {
            Material dup = mat.duplicate();
            dup.setID(mat.getID());
            materials.add(dup);
            obj[i].getObject().setMaterial(dup, obj[i].getObject().getMaterialMapping().duplicate(obj[i].getObject(), dup));
          }
      }

    // Now make a list of all ImageMaps used by any of them.

    ArrayList<ImageMap> images = new ArrayList<ImageMap>();
    for (int i = 0; i < scene.getNumImages(); i++)
      {
        ImageMap map = scene.getImage(i);
        boolean used = false;
        for (int j = 0; j < textures.size() && !used; j++)
          used = textures.get(j).usesImage(map);
        for (int j = 0; j < materials.size() && !used; j++)
          used = materials.get(j).usesImage(map);
        if (used)
          images.add(map);
      }

    // Save all of them to the appropriate arrays.

    clipboardObject = obj;
    clipboardTexture = textures.toArray(new Texture[textures.size()]);
    clipboardMaterial = materials.toArray(new Material[materials.size()]);
    clipboardImage = images.toArray(new ImageMap[images.size()]);
  }

    
  /**
   * pasteClipboard
   *
   * Paste the contents of the clipboard into a window.
   * If a scene object is selected and it's name is not the same as the clipboard object then add the paste object as a child.
   * */
  public static void pasteClipboard(LayoutWindow win, boolean asChild)
  {
    if (clipboardObject == null)
      return;
    Scene scene = win.getScene();
    UndoRecord undo = new UndoRecord(win, false);
    win.setUndoRecord(undo);
    int sel[] = win.getSelectedIndices();

    // First add any new image maps to the scene.

    for (int i = 0; i < clipboardImage.length; i++)
      {
        int j;
        for (j = 0; j < scene.getNumImages() && clipboardImage[i].getID() != scene.getImage(j).getID(); j++);
        if (j == scene.getNumImages())
          scene.addImage(clipboardImage[i]);
      }

    // Now add any new textures.

    for (int i = 0; i < clipboardTexture.length; i++)
      {
        Texture newtex;
        int j;
        for (j = 0; j < scene.getNumTextures() && clipboardTexture[i].getID() != scene.getTexture(j).getID(); j++);
        if (j == scene.getNumTextures())
          {
            newtex = clipboardTexture[i].duplicate();
            newtex.setID(clipboardTexture[i].getID());
            scene.addTexture(newtex);
          }
        else
          newtex = scene.getTexture(j);
        for (j = 0; j < clipboardObject.length; j++)
          {
            Texture current = clipboardObject[j].getObject().getTexture();
            if (current != null)
            {
              ParameterValue oldParamValues[] = clipboardObject[j].getObject().getParameterValues();
              ParameterValue newParamValues[] = new ParameterValue[oldParamValues.length];
              for (int k = 0; k < newParamValues.length; k++)
                newParamValues[k] = oldParamValues[k].duplicate();
              if (current == clipboardTexture[i])
                clipboardObject[j].setTexture(newtex, clipboardObject[j].getObject().getTextureMapping().duplicate(clipboardObject[j].getObject(), newtex));
              else if (current instanceof LayeredTexture)
                {
                  LayeredMapping map = (LayeredMapping) clipboardObject[j].getObject().getTextureMapping();
                  map = (LayeredMapping) map.duplicate();
                  clipboardObject[j].setTexture(new LayeredTexture(map), map);
                  Texture layer[] = map.getLayers();
                  for (int k = 0; k < layer.length; k++)
                    if (layer[k] == clipboardTexture[i])
                      {
                        map.setLayer(k, newtex);
                        map.setLayerMapping(k, map.getLayerMapping(k).duplicate(clipboardObject[j].getObject(), newtex));
                      }
                }
              clipboardObject[j].getObject().setParameterValues(newParamValues);
            }
          }
      }

    // Add any new materials.

    for (int i = 0; i < clipboardMaterial.length; i++)
      {
        Material newmat;
        int j;
        for (j = 0; j < scene.getNumMaterials() && clipboardMaterial[i].getID() != scene.getMaterial(j).getID(); j++);
        if (j == scene.getNumMaterials())
        {
          newmat = clipboardMaterial[i].duplicate();
          newmat.setID(clipboardMaterial[i].getID());
          scene.addMaterial(newmat);
        }
        else
          newmat = scene.getMaterial(j);
        for (j = 0; j < clipboardObject.length; j++)
          {
            Material current = clipboardObject[j].getObject().getMaterial();
            if (current == clipboardMaterial[i])
              clipboardObject[j].setMaterial(newmat, clipboardObject[j].getObject().getMaterialMapping().duplicate(clipboardObject[j].getObject(), newmat));
          }
      }

    // Finally add the objects to the scene.
    // If an object is selected, add it as a child to that selected object.
      // JDT: Bug with children objects.
      
    ObjectInfo obj[] = ObjectInfo.duplicateAll(clipboardObject);
      for (int i = 0; i < obj.length; i++){
          win.addObject(obj[i], undo);
          
          // If an object is selected, set the new paste object as a child.
          if(sel.length > 0){
              ObjectInfo selected = scene.getObject(sel[0]); // target
              
              ObjectInfo[] selChildren = selected.getChildren();
              ObjectInfo[] clipboardChildren = obj[i].getChildren();
              //System.out.println("len: " + selChildren.length + " - " + selected.getName());
              
              if( selected.getName().equals(obj[i].getName()) == false && clipboardChildren.length == 0){ // Don't add as child if the name is the same. (or child)
                  if(asChild){
                      obj[i].setParent(selected);
                      selected.addChild(obj[i], selected.getChildren().length);
                  }
              }
          }
      }
    undo.addCommand(UndoRecord.SET_SCENE_SELECTION, new Object [] {sel});
  }

  /** Get the number of objects on the clipboard. */

  public static int getClipboardSize()
  {
    if (clipboardObject == null)
      return 0;
    return clipboardObject.length;
  }

  /** Get the directory in which the user most recently accessed a file. */

  public static String getCurrentDirectory()
  {
    return ModellingApp.currentDirectory;
  }

  /** Set the directory in which the user most recently accessed a file. */

  public static void setCurrentDirectory(String currentDirectory)
  {
    ModellingApp.currentDirectory = currentDirectory;
  }

}
