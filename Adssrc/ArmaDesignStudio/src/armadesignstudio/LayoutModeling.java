package armadesignstudio;

import armadesignstudio.math.*;
import armadesignstudio.object.*;
import armadesignstudio.ui.*;
import buoy.event.*;
import buoy.widget.*;
import java.awt.*;
import java.util.Vector;
import java.io.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import java.nio.channels.FileChannel;

import javax.swing.*; // For JOptionPane

/** LayoutModeling is a utility class for managing laoyut object views, manipulation and saving. */

public class LayoutModeling {

	private static String baseDir = System.getProperty("user.dir") +
        System.getProperty("file.separator") + "layout_settings";


	public void setBaseDir(String dir){
		this.baseDir = dir;
	}

	public String getBaseDir(){
		return baseDir;
	}

    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if(!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        }
        finally {
            if(source != null) {
                source.close();
            }
            if(destination != null) {
                destination.close();
            }
        }
    }

    /**
     * getCoords
     *
     * Description: Called by ObjectInfo if state is tubeLayoutViewModel == true || layoutViewModel == false
     */
	public CoordinateSystem getCoords(ObjectInfo info){
		CoordinateSystem cx = info.getCoords();
		CoordinateSystem c = new CoordinateSystem();
		//cx.copyCoords(c);
        //c.copyCoords(cx);

		if(info.getLayoutView() == false || info.getTubeLayoutView() == true){ // Plate or Tube layout mode

			// Layout file
			String dir = baseDir; // System.getProperty("user.dir") + System.getProperty("file.separator") + "layout_settings";
			File d = new File(dir);
			if(d.exists() == false){
				d.mkdir();
			}
			//
			//String name = this.getName();
			//dir = dir + System.getProperty("file.separator") + name;
			//d = new File(dir);
			//if(d.exists() == false){
			//	d.mkdir();
			//}

            String dir2 = dir;
            dir2 = dir2 + System.getProperty("file.separator") + info.getId();
            File d2 = new File(dir2);


			dir = dir + System.getProperty("file.separator") + info.getName();
			d = new File(dir);

            if(d2.exists() == false && d.exists() == true){ // Migrate old data
                try {
                    copyFile(d, d2);
                } catch(Exception e){
                }
            }

			if(d2.exists() == false){

                //System.out.println("******************************* " + info.getName());
                // Set c to object (info) coordinate system
                //c.copyCoords(cx);
                //c.setOrigin(new Vec3(-3.0, -1.0, 0.0));
                //System.out.println("LayoutModeling.getCoords: * x " +
                //                   cx.getOrigin().x + " y " + cx.getOrigin().y + " z " + cx.getOrigin().z);
				// Save coordSystem to layout coord file for construction.
				//saveLayout(info, cx);
                //saveLayout(info, c);
                
                return null;

			} else {
				// Read from file
				Properties prop = new Properties();
				InputStream input = null;
				try {
					  //InputStream file = new FileInputStream(d);
					  //InputStream buffer = new BufferedInputStream(file);
					  //ObjectInput input = new ObjectInputStream (buffer);

					  //deserialize the List
					  //CoordinateSystem c2 = (CoordinateSystem)input.readObject();
					  //display its data
					  //for(String quark: recoveredQuarks){
						//System.out.println("Recovered Quark: " + quark);
					  //}

					  input = new FileInputStream(d2);

						// load a properties file
						prop.load(input);

					Vec3 origin = new Vec3();
					origin.x =  Double.parseDouble(prop.getProperty("origin.x"));
					origin.y =  Double.parseDouble(prop.getProperty("origin.y"));
					origin.z =  Double.parseDouble(prop.getProperty("origin.z"));
					Vec3 zDir = new Vec3();
					zDir.x =  Double.parseDouble(prop.getProperty("zDir.x"));
					zDir.y =  Double.parseDouble(prop.getProperty("zDir.y"));
					zDir.z =  Double.parseDouble(prop.getProperty("zDir.z"));
					Vec3 upDir = new Vec3();
					upDir.x =  Double.parseDouble(prop.getProperty("upDir.x"));
					upDir.y =  Double.parseDouble(prop.getProperty("upDir.y"));
					upDir.z =  Double.parseDouble(prop.getProperty("upDir.z"));

					c = new CoordinateSystem(origin, zDir, upDir);

					//System.out.println("getCoords " + origin.x + " " + origin.y + " " + origin.z);
				}
				//catch(ClassNotFoundException ex){
					  //fLogger.log(Level.SEVERE, "Cannot perform input. Class not found.", ex);
				//}
				catch(IOException ex){
					  //fLogger.log(Level.SEVERE, "Cannot perform input.", ex);
				} catch(Exception e){
                    
				}
			}
		}
		return c;
	}


    /**
     * saveLayout
     *
     * Description: Save alternate coordicate data for object in seperate file.
     *  Can be used for layout view to temporary reposition.
     */
	public void saveLayout(ObjectInfo info, CoordinateSystem c){
		//CoordinateSystem c = info.getCoords();

		//if(info.getLayoutView() == false || info.getTubeLayoutView() == true){ // Plate or Tube layout mode

			// Layout file
			String dir = baseDir; // System.getProperty("user.dir") + System.getProperty("file.separator") + "layout_settings";
			File d = new File(dir);
			if(d.exists() == false){
				d.mkdir();
			}
			//
			//String name = this.getName();
			//dir = dir + System.getProperty("file.separator") + name;
			//d = new File(dir);
			//if(d.exists() == false){
			//	d.mkdir();
			//}
            String dir2 = dir;
            dir2 = dir2 + System.getProperty("file.separator") + info.getId();
            File d2 = new File(dir2);
            if(d2.exists() == true){
                //d2.delete();
            }
            //System.out.println(" ID: " + info.getId());

			//dir = dir + System.getProperty("file.separator") + info.getName();
			//d = new File(dir);
			//if(d.exists() == true){
			//	d.delete();
			//}

			//if(d.exists() == false){
				//d.mkdir();
				//PrintWriter writer = new PrintWriter(gCodeFile, "UTF-8");
				Properties prop = new Properties();
                InputStream input = null;
				OutputStream output = null;

				try {
				  //OutputStream file = new FileOutputStream(d);
				  //OutputStream buffer = new BufferedOutputStream(file);
				  //ObjectOutput output = new ObjectOutputStream(buffer);
                    
                    if(d2.exists() == true){
                        input = new FileInputStream(d2);
                        
                        // load a properties file
                        if(input != null){
                            prop.load(input);
                        }
                    }
                    

					// set the properties value
					prop.setProperty("origin.x", c.getOrigin().x + "");
					prop.setProperty("origin.y", c.getOrigin().y + "");
					prop.setProperty("origin.z", c.getOrigin().z + "");

					prop.setProperty("zDir.x", c.getZDirection().x + "");
					prop.setProperty("zDir.y", c.getZDirection().y + "");
					prop.setProperty("zDir.z", c.getZDirection().z + "");

					prop.setProperty("upDir.x", c.getUpDirection().x + "");
					prop.setProperty("upDir.y", c.getUpDirection().y + "");
					prop.setProperty("upDir.z", c.getUpDirection().z + "");

                    // save properties to project root folder
                    //output = new FileOutputStream(d);
					//prop.store(output, null);

                    output = new FileOutputStream(d2);
                    prop.store(output, null);

					// Vec3 getOrigin()
					// Vec3 getZDirection()
					// Vec3 getUpDirection()
					// double [] getRotationAngles()


				  //output.writeObject(c);
				}
				catch(IOException ex){
				  //fLogger.log(Level.SEVERE, "Cannot perform output.", ex);
				  System.out.println("Error: " + dir);
				  System.out.println("Error: " + ex);
				}

				//writer.close();
				// DataOutputStream out
			//}
		//}
	}
    
    
    /**
     * deleteLayout
     *
     * Description: Delete layout file. Used to reset object and child positioning.
     */
    public void deleteLayout(ObjectInfo info){
        try {
            String dir = baseDir;
            File d = new File(dir);
            if(d.exists() == true){
                String dir2 = dir + System.getProperty("file.separator") + info.getId();
                File d2 = new File(dir2);
                if(d2.exists() == true){
                    d2.delete();
                }
            }
        } catch(Exception ex){
            //System.out.println("Error: " + dir);
            System.out.println("Error: " + ex);
        }
    }


	public void disableObject(ObjectInfo info){
		// Create disable file
		String dir = baseDir;
		File d = new File(dir);
		if(d.exists() == false){
			d.mkdir();
		}

		dir = dir + System.getProperty("file.separator") + info.getId() + "_disabled";
		d = new File(dir);
		if(d.exists() == true){
			//d.delete();
		} else {
			try {
				PrintWriter writer = new PrintWriter(dir, "UTF-8");
				writer.println("_");
				writer.close();
			} catch (Exception e){
				System.out.println("Error: " + e);
			}
		}
	}

	public void enableObject(ObjectInfo info){
		String dir = baseDir;
		File d = new File(dir);
		if(d.exists() == false){
			d.mkdir();
		}

		dir = dir + System.getProperty("file.separator") + info.getId() + "_disabled";
		d = new File(dir);
		if(d.exists() == true){
			d.delete();
		}
	}

	public boolean isObjectEnabled(ObjectInfo info){
		String dir = baseDir;
		File d = new File(dir);
		if(d.exists() == false){
			d.mkdir();
		}

		dir = dir + System.getProperty("file.separator") + info.getId() + "_disabled";
		d = new File(dir);
		if(d.exists() == true){
			return false;
		}
		return true;
	}

    
    public void setGCodePointOffset(ObjectInfo info){
        
        // Layout file
        String dir = baseDir;
        File d = new File(dir);
        if(d.exists() == false){
            d.mkdir();
        }
        
        String dir2 = dir;
        dir2 = dir2 + System.getProperty("file.separator") + info.getId();
        File d2 = new File(dir2);
        if(d2.exists() == true){
            //d2.delete();
        }
        
        Properties prop = new Properties();
        OutputStream output = null;
        InputStream input = null;
        try {
            input = new FileInputStream(d2);
            prop.load(input);
            
            String objCutOrder = prop.getProperty("point_offset");
            if(objCutOrder == null){
                objCutOrder = "0";
            }
            
            String order = JOptionPane.showInputDialog("Enter point offset for " + info.getName(), objCutOrder);
            if(order != null){
                // set the properties value
                prop.setProperty("point_offset", order);
                
                // save properties to project root folder
                output = new FileOutputStream(d2);
                prop.store(output, null);
            }
        } catch(IOException ex){
            //fLogger.log(Level.SEVERE, "Cannot perform output.", ex);
            System.out.println("Error: " + dir);
            System.out.println("Error: " + ex);
        }
    }
    
    /**
     * getPointOffset
     *
     * Description: get poly attribute value.
     */
    public int getPointOffset(String polyID){
        int offset = 0;
        
        String dir = baseDir;
        File d = new File(dir);
        if(d.exists() == false){
            d.mkdir();
        }
        
        String dir2 = dir;
        dir2 = dir2 + System.getProperty("file.separator") + polyID ;
        File d2 = new File(dir2);
        
        Properties prop = new Properties();
        OutputStream output = null;
        InputStream input = null;
        try {
            input = new FileInputStream(d2);
            prop.load(input);
            
            String objCutOrder = prop.getProperty("point_offset");
            if(objCutOrder == null){
                objCutOrder = "0";
            }
            
            offset = Integer.parseInt(objCutOrder);
            
        } catch(IOException ex){
            //fLogger.log(Level.SEVERE, "Cannot perform output.", ex);
            System.out.println("Error: " + dir);
            System.out.println("Error: " + ex);
        }
        
        return offset;
    }


	public void setGCodePolyOrder(ObjectInfo info){
		// Layout file
		String dir = baseDir; // System.getProperty("user.dir") + System.getProperty("file.separator") + "layout_settings";
		File d = new File(dir);
		if(d.exists() == false){
			d.mkdir();
		}

		String dir2 = dir;
		dir2 = dir2 + System.getProperty("file.separator") + info.getId();
		File d2 = new File(dir2);
		if(d2.exists() == true){
			//d2.delete();
		}
		//System.out.println(" ID: " + info.getId());

		//if(d.exists() == false){
			//d.mkdir();
			Properties prop = new Properties();
			OutputStream output = null;
			InputStream input = null;
			try {
				input = new FileInputStream(d2);
				prop.load(input);

				String objCutOrder = prop.getProperty("cut_order");
				if(objCutOrder == null){
					objCutOrder = "0";
				}

				String order = JOptionPane.showInputDialog(
					"Enter cut depth for " + info.getName(), objCutOrder);
				if(order != null){
					// set the properties value
					prop.setProperty("cut_order", order);

					// save properties to project root folder
					output = new FileOutputStream(d2);
					prop.store(output, null);
				}
			} catch(IOException ex){
			  //fLogger.log(Level.SEVERE, "Cannot perform output.", ex);
			  System.out.println("Error: " + dir);
			  System.out.println("Error: " + ex);
			}
		//}
	}

    
    /**
     * getPolyOrder
     *
     * Description: get poly attribute value.
     */
    public int getPolyOrder(String polyID){
        int polyOrder = 0;
        
        String dir = baseDir;
        File d = new File(dir);
        if(d.exists() == false){
            d.mkdir();
        }
        
        String dir2 = dir;
        dir2 = dir2 + System.getProperty("file.separator") + polyID;
        File d2 = new File(dir2);
        
        Properties prop = new Properties();
        OutputStream output = null;
        InputStream input = null;
        try {
            input = new FileInputStream(d2);
            prop.load(input);
            
            String objReverseOrder = prop.getProperty("cut_order");
            if(objReverseOrder == null){
                objReverseOrder = "0";
            }
            
            polyOrder = Integer.parseInt(objReverseOrder);
            
        } catch(IOException ex){
            //fLogger.log(Level.SEVERE, "Cannot perform output.", ex);
            System.out.println("Error: " + dir);
            System.out.println("Error: " + ex);
        }
        
        return polyOrder;
    }
    
    
    
    
    
    public void setGCodePolyDepth(ObjectInfo info){
		// Layout file
		String dir = baseDir; // System.getProperty("user.dir") + System.getProperty("file.separator") + "layout_settings";
		File d = new File(dir);
		if(d.exists() == false){
			d.mkdir();
		}

		String dir2 = dir;
		dir2 = dir2 + System.getProperty("file.separator") + info.getId();
		File d2 = new File(dir2);
		if(d2.exists() == true){
			//d2.delete();
		}
		//System.out.println(" ID: " + info.getId());

		//if(d.exists() == false){
			//d.mkdir();
			Properties prop = new Properties();
			OutputStream output = null;
			InputStream input = null;
			try {
				input = new FileInputStream(d2);
				prop.load(input);

				String objCutDepth = prop.getProperty("cut_depth");
				if(objCutDepth == null){
					objCutDepth = "0";
				}

				String depth = JOptionPane.showInputDialog("Enter cut depth for " + info.getName(), objCutDepth);
				if(depth != null){
					// set the properties value
					prop.setProperty("cut_depth", depth);

					// save properties to project root folder
					output = new FileOutputStream(d2);
					prop.store(output, null);
				}
			} catch(IOException ex){
			  //fLogger.log(Level.SEVERE, "Cannot perform output.", ex);
			  System.out.println("Error: " + dir);
			  System.out.println("Error: " + ex);
			}
		//}
	}
    
    /**
     * getPolyDepth
     *
     */
    public double getPolyDepth(ObjectInfo info){
        double depth = 0;
        String dir = baseDir;
        File d = new File(dir);
        if(d.exists() == false){
            d.mkdir();
        }
        
        String dir2 = dir;
        dir2 = dir2 + System.getProperty("file.separator") + info.getId();
        File d2 = new File(dir2);
        
        Properties prop = new Properties();
        OutputStream output = null;
        InputStream input = null;
        try {
            input = new FileInputStream(d2);
            prop.load(input);
            
            String value = prop.getProperty("cut_depth");
            if(value == null){
                value = "0";
            }
            
            depth = Double.parseDouble(value);
            
        } catch(IOException ex){
            //fLogger.log(Level.SEVERE, "Cannot perform output.", ex);
            System.out.println("Error: " + dir);
            System.out.println("Error: " + ex);
        }
        return depth;
    }
    
    public void setGCodeReverseOrder(ObjectInfo info){
        // Layout file
        String dir = baseDir; // System.getProperty("user.dir") + System.getProperty("file.separator") + "layout_settings";
        File d = new File(dir);
        if(d.exists() == false){
            d.mkdir();
        }
        
        String dir2 = dir;
        dir2 = dir2 + System.getProperty("file.separator") + info.getId();
        File d2 = new File(dir2);
        if(d2.exists() == true){
            //d2.delete();
        }
        //System.out.println(" ID: " + info.getId());
        
        //if(d.exists() == false){
        //d.mkdir();
        Properties prop = new Properties();
        OutputStream output = null;
        InputStream input = null;
        try {
            input = new FileInputStream(d2);
            prop.load(input);
            
            String objReverseOrder = prop.getProperty("reverse_order");
            if(objReverseOrder == null){
                objReverseOrder = "0";
            }
            
            String reverse = JOptionPane.showInputDialog("Enter 0=default, 1=reverse order of cut for " + info.getName(), objReverseOrder);
            if(reverse != null){
                // set the properties value
                prop.setProperty("reverse_order", reverse);
                
                // save properties to project root folder
                output = new FileOutputStream(d2);
                prop.store(output, null);
            }
        } catch(IOException ex){
            //fLogger.log(Level.SEVERE, "Cannot perform output.", ex);
            System.out.println("Error: " + dir);
            System.out.println("Error: " + ex);
        }
        //}
    }
    
    /**
     * getReverseOrder
     *
     * Description: get poly attribute value.
     */
    public int getReverseOrder(String polyID){
        int offset = 0;
        
        String dir = baseDir;
        File d = new File(dir);
        if(d.exists() == false){
            d.mkdir();
        }
        
        String dir2 = dir;
        dir2 = dir2 + System.getProperty("file.separator") + polyID;
        File d2 = new File(dir2);
        
        Properties prop = new Properties();
        OutputStream output = null;
        InputStream input = null;
        try {
            input = new FileInputStream(d2);
            prop.load(input);
            
            String objReverseOrder = prop.getProperty("reverse_order");
            if(objReverseOrder == null){
                objReverseOrder = "0";
            }
            
            offset = Integer.parseInt(objReverseOrder);
            
        } catch(IOException ex){
            //fLogger.log(Level.SEVERE, "Cannot perform output.", ex);
            System.out.println("Error: " + dir);
            System.out.println("Error: " + ex);
        }
        
        return offset;
    }



	public void saveLayoutFile(  )
	{

	}
    
    
    
    
    
    public void setObjectStructure(ObjectInfo info){
        
        // Layout file
        String dir = baseDir; // System.getProperty("user.dir") + System.getProperty("file.separator") + "layout_settings";
        File d = new File(dir);
        if(d.exists() == false){
            d.mkdir();
        }
        
        String dir2 = dir;
        dir2 = dir2 + System.getProperty("file.separator") + info.getId();
        File d2 = new File(dir2);
        if(d2.exists() == true){
            //d2.delete();
        }
        //System.out.println(" ID: " + info.getId());
        
        //if(d2.exists() == false){
            //d2.mkdir();
        //}
        
        //if (!Files.exists(dir2, LinkOption.NOFOLLOW_LINKS))
        //    Files.createFile(dir2);
        
        if(!d2.exists() && !d2.isDirectory())
        {
            try {
                d2.createNewFile();
            } catch(IOException ex){
                //fLogger.log(Level.SEVERE, "Cannot perform output.", ex);
                
                System.out.println("Error: creating file " + dir2);
            }
        }
        
        Properties prop = new Properties();
        OutputStream output = null;
        InputStream input = null;
        try {
            input = new FileInputStream(d2);
            prop.load(input);
            
            String objCutOrder = prop.getProperty("structure");
            if(objCutOrder == null){
                objCutOrder = "";
            }
            
            String order = JOptionPane.showInputDialog(
                                                       "Enter structure name " + info.getName(), objCutOrder);
            if(order != null){
                // set the properties value
                prop.setProperty("structure", order);
                
                // save properties to project root folder
                output = new FileOutputStream(d2);
                prop.store(output, null);
                
                System.out.println(" write " + d2);
            }
        } catch(IOException ex){
            //fLogger.log(Level.SEVERE, "Cannot perform output.", ex);
            System.out.println("Error: " + dir);
            System.out.println("Error: " + ex);
        }
        //}
    }
    
    
    /**
     * get
     *
     * Description: get poly attribute value.
     */
    public String getObjectStructure(String polyID){
        String result = "";
        
        String dir = baseDir;
        File d = new File(dir);
        if(d.exists() == false){
            d.mkdir();
        }
        
        String dir2 = dir;
        dir2 = dir2 + System.getProperty("file.separator") + polyID;
        File d2 = new File(dir2);
        
        Properties prop = new Properties();
        OutputStream output = null;
        InputStream input = null;
        try {
            input = new FileInputStream(d2);
            prop.load(input);
            
            String value = prop.getProperty("structure");
            if(value == null){
                value = "0";
            }
            
            result = value;
            
        } catch (FileNotFoundException ex){
        } catch(IOException ex){
            //fLogger.log(Level.SEVERE, "Cannot perform output.", ex);
            //System.out.println("Error: " + dir);
            System.out.println("Error: " + ex);
        }
        
        return result;
    }
    
    
    // ?????
    public void generateStructureMesh(){
        System.out.println("Generate Structure Meshes... ");
        
        // For each object read property files.
        
        //objects
        
        //
    }
    
    
    public void setObjectGroup(ObjectInfo info){
        // Layout file
        String dir = baseDir; // System.getProperty("user.dir") + System.getProperty("file.separator") + "layout_settings";
        File d = new File(dir);
        if(d.exists() == false){
            d.mkdir();
        }
        
        String dir2 = dir;
        dir2 = dir2 + System.getProperty("file.separator") + info.getId();
        File d2 = new File(dir2);
        if(d2.exists() == true){
            //d2.delete();
        }
        if(d2.exists() == false){
            try {
            PrintWriter writer = new PrintWriter(dir2, "UTF-8");
            writer.close();
            } catch(Exception ex){}
        }
        //System.out.println(" ID: " + info.getId());
        
        //if(d2.exists() == false){
            //d2.mkdir();
        
        //}
        Properties prop = new Properties();
        OutputStream output = null;
        InputStream input = null;
        try {
            input = new FileInputStream(d2);
            prop.load(input);
            
            String objCutOrder = prop.getProperty("object_group");
            if(objCutOrder == null){
                objCutOrder = "0";
            }
            
            String order = JOptionPane.showInputDialog(
                                                       "Enter group name " + info.getName(), objCutOrder);
            if(order != null){
                // set the properties value
                prop.setProperty("object_group", order);
                
                // save properties to project root folder
                output = new FileOutputStream(d2);
                prop.store(output, null);
            }
        } catch(IOException ex){
            //fLogger.log(Level.SEVERE, "Cannot perform output.", ex);
            System.out.println("Error: " + dir);
            System.out.println("Error: " + ex);
        }
        //}
    }
    
    /**
     * getObjectGroup
     *
     * Description: get poly attribute value.
     */
    public String getObjectGroup(String polyID){
        String objectGroup = "";
        String dir = baseDir;
        File d = new File(dir);
        if(d.exists() == false){
            d.mkdir();
        }
        String dir2 = dir;
        dir2 = dir2 + System.getProperty("file.separator") + polyID;
        File d2 = new File(dir2);
        Properties prop = new Properties();
        OutputStream output = null;
        InputStream input = null;
        try {
            input = new FileInputStream(d2);
            prop.load(input);
            objectGroup = prop.getProperty("object_group");
            if(objectGroup == null){
                objectGroup = "0";
            }
        } catch(IOException ex){
            //fLogger.log(Level.SEVERE, "Cannot perform output.", ex);
            System.out.println("Error: " + dir);
            System.out.println("Error: " + ex);
        }
        return objectGroup;
    }
    
}
