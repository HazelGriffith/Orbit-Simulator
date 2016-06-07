package default_package;

import javax.media.j3d.Alpha;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.RotationInterpolator;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.universe.SimpleUniverse;

public class Satellites {

  public static void main(final String[] args) {
    new Satellites();
  }

  public Satellites() {
	  
	  //Variables
	  int SatArrSize = 6;
	  float angleOfRotation = (float) (5.0f*Math.PI/6.0f);
	  float[] tempAngleOfRotation = new float[SatArrSize];
	  for (int i = 0; i< SatArrSize; i++){
		  tempAngleOfRotation[i] = (float) (Math.random()*Math.PI);
	  }
	  
	  float[] xArray = new float[SatArrSize];
	  float[] yArray = new float[SatArrSize];
	  float[] zArray = new float[SatArrSize];
	  double[] milisSArray = new double[SatArrSize];
	  
	  float GridRadius = 0.7f;
	  float x1 = (float) Math.cos(angleOfRotation)*GridRadius, y1 = (float) Math.sin(angleOfRotation)*GridRadius, z1 = 0.0f;
	  double multiplier = 0.0001;
	  for (int i = 0; i < SatArrSize; i++){
		  float Temp_radii = (float) Math.random()+(6371/46371);
		  xArray[i] = (float) (Math.cos(tempAngleOfRotation[i])*Temp_radii);
		  yArray[i] = (float) (Math.sin(tempAngleOfRotation[i])*Temp_radii);
		  zArray[i] = 0.0f;
	  }
	  
	  // Calculates the radius of the satellite.
	  double radius = (double) Math.sqrt(Math.pow(x1, 2.0)+Math.pow(y1, 2.0)+Math.pow(z1, 2.0))*46371*1000;

	  // v = sqrt(G*Me/r)
	  double tangential_speedS = (double)(Math.sqrt(((6.67*Math.pow(10.0, -11.0))*(5.972*Math.pow(10.0, 24.0)))/radius));
	    
	  // w = v / r
	  double angular_speedS = tangential_speedS / radius;
	    
	  // s = (w / Math.PI*2.0f)^-1
	  double milisS = (double)(Math.pow((angular_speedS/(Math.PI*2.0f)), -1.0d)*1000*multiplier);
	    
	    
	    
	  for (int i = 0; i < SatArrSize; i++){
	      // Calculates the radius of the satellite.
	      double TempRadius = (double) Math.sqrt(Math.pow(xArray[i], 2.0)+Math.pow(yArray[i], 2.0)+Math.pow(zArray[i], 2.0))*46371*1000;

	      // v = sqrt(G*Me/r)
	      double Temp_tangential_speedS = (double)(Math.sqrt(((6.67*Math.pow(10.0, -11.0))*(5.972*Math.pow(10.0, 24.0)))/TempRadius));
	        
	      // w = v / r
	      double Temp_angular_speedS = Temp_tangential_speedS / TempRadius;
	        
	      // s = (w / Math.PI*2.0f)^-1
	      milisSArray[i] = (double)(Math.pow((Temp_angular_speedS/(Math.PI*2.0f)), -1.0d)*1000*multiplier);
	  }
	    
	  final long milisE =  (long) (86400000*multiplier);
	  
    //Rotation Transform Groups  
    final Transform3D RotateS = new Transform3D();
	Transform3D[] RotateSatelliteArray = new Transform3D[SatArrSize];
	for (int i = 0; i < SatArrSize; i++){
		RotateSatelliteArray[i] = new Transform3D();
	}
	final Transform3D RotateE = new Transform3D();
		
	//transform1.rotX(20);
	//transform2.rotY(20);
	for (int i = 0; i < SatArrSize; i++){
		RotateSatelliteArray[i].rotZ(tempAngleOfRotation[i]);
	}
	RotateS.rotZ(angleOfRotation);
	
	//Creates the overall TransformGroups.
    final TransformGroup satellites = new TransformGroup();
    TransformGroup[] satellitesArray = new TransformGroup[SatArrSize];
    
    for (int i = 0; i < SatArrSize; i++){
    	satellitesArray[i] = new TransformGroup();
    }
    final TransformGroup planet = new TransformGroup();
    
    //to enable rotation
    for (int i = 0; i < SatArrSize; i++){
    	satellitesArray[i].setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    }
    planet.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    satellites.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

    // Creates the Universe in which everything is rendered
    final SimpleUniverse universe = new SimpleUniverse();
    
    //Create the spheres
    final Sphere earth = new Sphere(.1374f);
    final Sphere satellite = new Sphere(0.03f);
    final Sphere[] satelliteArray = new Sphere[SatArrSize]; 
    for (int i = 0; i < SatArrSize; i++){
    	satelliteArray[i] = new Sphere(0.03f);
    }
    
    //Create the Inner TransformGroups
    final TransformGroup tgE = new TransformGroup();
    final TransformGroup tgS = new TransformGroup();
    TransformGroup[] tgSArray = new TransformGroup[SatArrSize];
    for (int i = 0; i < SatArrSize; i++){
    	tgSArray[i] = new TransformGroup();
    }
    
    //Create the Transform3D groups that handle translation
    final Transform3D transformS = new Transform3D();
    final Transform3D transformE = new Transform3D();
    Transform3D[] transformSArray = new Transform3D[SatArrSize];
    for (int i = 0; i < SatArrSize; i++){
    	transformSArray[i] = new Transform3D();
    }
    
    //Create the soon to be applied vector translations;
    final Vector3f vectorS = new Vector3f(x1, y1, z1);
    Vector3f[] vectorSArray = new Vector3f[SatArrSize];
    
    for (int i = 0; i < SatArrSize; i++){
    	vectorSArray[i] = new Vector3f(xArray[i], yArray[i], zArray[i]);
    }
    final Vector3f vectorE = new Vector3f(.0f, .0f, .0f);

    // Apply the translation vectors to set the initial position of each sphere.
    transformE.setTranslation(vectorE);
    transformS.setTranslation(vectorS);
    for (int i = 0; i < SatArrSize; i++){
    	transformSArray[i].setTranslation(vectorSArray[i]);
    }
    
    //Applies the transform3D translations to the inner Translation Groups
    tgE.setTransform(transformE);
    tgS.setTransform(transformS);
    for (int i = 0; i < SatArrSize; i++){
    	tgSArray[i].setTransform(transformSArray[i]);
    }
    
    //Adds objects to the inner translation Groups.
    tgE.addChild(earth);
    tgS.addChild(satellite);
    for (int i = 0; i < SatArrSize; i++){
    	tgSArray[i].addChild(satelliteArray[i]);
    }
    
    //Adds the inner transform Groups to the overall Transform groups
    satellites.addChild(tgS);
    planet.addChild(tgE);
    for (int i = 0; i < SatArrSize; i++){
    	satellitesArray[i].addChild(tgSArray[i]);
    }
    
    final Alpha alpha = new Alpha(-1, Alpha.INCREASING_ENABLE, 0, 0, (long)milisS, 0, 0, 0, 0, 0);
    final Alpha alphaE = new Alpha(-1, Alpha.INCREASING_ENABLE, 0, 0, milisE, 0, 0, 0, 0, 0);
    Alpha[] alphaSArray = new Alpha[SatArrSize];
    for (int i = 0; i < SatArrSize; i++){
    	alphaSArray[i] = new Alpha(-1, Alpha.INCREASING_ENABLE, 0, 0, (long)milisSArray[i], 0, 0, 0, 0, 0);
    }
    final RotationInterpolator r2 = new RotationInterpolator(alphaE, planet, RotateE, 0.0f, (float) Math.PI * 2.0f);
    final RotationInterpolator r1 = new RotationInterpolator(alpha, satellites, RotateS, 0.0f, (float) Math.PI * 2.0f);
    RotationInterpolator[] rSArray = new RotationInterpolator[SatArrSize];
    for (int i = 0; i < SatArrSize; i++){
    	rSArray[i] = new RotationInterpolator(alphaSArray[i], satellitesArray[i], RotateSatelliteArray[i], 0.0f, (float) Math.PI * 2.0f);
    	rSArray[i].setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
    	satellitesArray[i].addChild(rSArray[i]);
    }
    
    r1.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
    r2.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
    
    satellites.addChild(r1);
    planet.addChild(r2);
    final BranchGroup bg = new BranchGroup();
    bg.addChild(satellites);
    bg.addChild(planet);
    for (int i = 0; i < SatArrSize; i++){
    	bg.addChild(satellitesArray[i]);
    }
    //

    final Color3f light1Color = new Color3f(.1f, 1.4f, 1.8f);
    final BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 10000000.0);
    final Vector3f light1Direction = new Vector3f(4.0f, -7.0f, -12.0f);
    final DirectionalLight light1 = new DirectionalLight(light1Color, light1Direction);
    light1.setInfluencingBounds(bounds);
    bg.addChild(light1);
    universe.getViewingPlatform().setNominalViewingTransform();
    universe.addBranchGraph(bg);
    System.out.println("constructor");
  
  }
  
}