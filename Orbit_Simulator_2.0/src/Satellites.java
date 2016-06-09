//NOTE FOR RYAN:


import java.awt.Container;

import javax.media.j3d.Appearance;
import javax.media.j3d.Material;
import javax.media.j3d.Texture;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.Alpha;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.RotationInterpolator;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.AmbientLight;

import javax.vecmath.Color4f;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;
import javax.vecmath.Matrix3d;

import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.geometry.Cone;
import com.sun.j3d.utils.geometry.Cylinder;


public class Satellites {

  public static void main(final String[] args) {
    new Satellites();
  }

  public Satellites() {
	  
	  //Variables
	  int SatArrSize = 100;
	  float angleOfRotation = (float) (0.0*Math.PI/6.0);
	  float[] tempAngleOfRotation = new float[SatArrSize];
	  for (int i = 0; i< SatArrSize; i++){
		  tempAngleOfRotation[i] = (float) (Math.random()*Math.PI);
	  }
	  
	  final BranchGroup bg = new BranchGroup();
	  float[] xArray = new float[SatArrSize];
	  float[] yArray = new float[SatArrSize];
	  float[] zArray = new float[SatArrSize];
	  double[] milisSArray = new double[SatArrSize];
	  
	  float GridRadius = (42164.0f/46371.0f);
      //float GridRadius = 0.3f;
	  
	  //First attempt at rotation around an arbitrary axis
	  float x1 = (float) Math.sin((Math.PI/2.0f)-angleOfRotation)*GridRadius, y1 = (float) Math.cos((Math.PI/2.0f)-angleOfRotation)*GridRadius, z1 = 0.0f;
	  double multiplier = 0.0001;
	  for (int i = 0; i < SatArrSize; i++){
		  float Temp_radii =  (float) (Math.random()+(6571.0/46371.0));
		  xArray[i] = (float) Math.sin((Math.PI/2.0f)-tempAngleOfRotation[i])*Temp_radii;
		  yArray[i] = (float) Math.cos((Math.PI/2.0f)-tempAngleOfRotation[i])*Temp_radii;
		  zArray[i] = 0.0f;
		  //System.out.println(Temp_radii);
	  }
	  
	  //sets the earth image as the texture of Earth and wraps it around the sphere
	  TextureLoader loader = new TextureLoader("C:\\Users\\Ryan\\Pictures\\Saved Pictures\\earth.jpg", "RGB", new Container());
	  Texture texture = loader.getTexture();
	  texture.setBoundaryModeS(Texture.WRAP);
	  texture.setBoundaryModeT(Texture.WRAP);
	  texture.setBoundaryColor(new Color4f(0.0f, 1.0f, 0.0f, 0.0f));
	  
	  TextureAttributes texAttr = new TextureAttributes();
	  texAttr.setTextureMode(TextureAttributes.MODULATE);
	  
	  //Creates Color3f objects to assign the material to different objects.
	  Color3f meshCA = new Color3f(1.0f, 1.0f, 1.0f);
	  Color3f meshCD = new Color3f(1.0f, 1.0f, 1.0f);
	  Color3f meshCS = new Color3f(1.0f, 1.0f, 1.0f);
	  Color3f meshCE = new Color3f(1.0f, 1.0f, 1.0f);
	  Material mesh = new Material(meshCA, meshCE, meshCD, meshCS, 0.0f);
	  Color3f meshCAx = new Color3f(1.0f, 0.0f, 0.0f);
	  Color3f meshCDx = new Color3f(1.0f, 0.0f, 0.0f);
	  Color3f meshCSx = new Color3f(1.0f, 0.0f, 0.0f);
	  Color3f meshCEx = new Color3f(1.0f, 0.0f, 0.0f);
	  Material meshx = new Material(meshCAx, meshCEx, meshCDx, meshCSx, 0.0f);
	  Color3f meshCAy = new Color3f(0.0f, 1.0f, 0.0f);
	  Color3f meshCDy = new Color3f(0.0f, 1.0f, 0.0f);
	  Color3f meshCSy = new Color3f(0.0f, 1.0f, 0.0f);
	  Color3f meshCEy = new Color3f(0.0f, 1.0f, 0.0f);
	  Material meshy = new Material(meshCAy, meshCEy, meshCDy, meshCSy, 0.0f);
	  Color3f meshCAz = new Color3f(0.0f, 0.0f, 1.0f);
	  Color3f meshCDz = new Color3f(0.0f, 0.0f, 1.0f);
	  Color3f meshCSz = new Color3f(0.0f, 0.0f, 1.0f);
	  Color3f meshCEz = new Color3f(0.0f, 0.0f, 1.0f);
	  Material meshz = new Material(meshCAz, meshCEz, meshCDz, meshCSz, 0.0f);
	  Appearance apx = new Appearance();
	  Appearance apy = new Appearance();
	  Appearance apz = new Appearance();
	  Appearance apE = new Appearance();
	  Appearance apO = new Appearance();
	  apx.setMaterial(meshx);
	  apy.setMaterial(meshy);
	  apz.setMaterial(meshz);
	  apO.setMaterial(mesh);
	  apE.setMaterial(mesh);
	  apE.setTexture(texture);
	  apE.setTextureAttributes(texAttr);
	  
	  int primflags = Primitive.GENERATE_NORMALS + Primitive.GENERATE_TEXTURE_COORDS;
	  
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
    Transform3D RotateS = new Transform3D();
	Transform3D[] RotateSatelliteArray = new Transform3D[SatArrSize];
	for (int i = 0; i < SatArrSize; i++){
		RotateSatelliteArray[i] = new Transform3D();
	}
	final Transform3D RotateE = new Transform3D();
	
	//Sets up rotation matrices for generated satellites.
	for (int i = 0; i < SatArrSize; i++){
		Transform3D tempM1 = new Transform3D();
		Transform3D tempM2 = new Transform3D();
		tempM1.rotZ(tempAngleOfRotation[i]);
		tempM2.rotX(tempAngleOfRotation[i]);
		tempM1.mul(tempM2);
		RotateSatelliteArray[i] = tempM1;
	}
	Transform3D m1 = new Transform3D();
	Transform3D m2 = new Transform3D();
	m1.rotZ(angleOfRotation);
	m2.rotX(angleOfRotation);
	m1.mul(m2);
	RotateS = m1;
	
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
    final Sphere earth = new Sphere(.1374f, primflags, apE);
    final Cone satellite = new Cone(0.015f,0.05f, apO);
    final Cone[] satelliteArray = new Cone[SatArrSize]; 
    for (int i = 0; i < SatArrSize; i++){
    	satelliteArray[i] = new Cone(0.015f, 0.05f, apO);
    }
    
	//X axis made of spheres
	
	for (float x = -1.0f; x <= 1.0f; x+= 0.1f){
		Sphere sphere = new Sphere(0.005f, apx);
		TransformGroup grid = new TransformGroup();
		Transform3D transform = new Transform3D();
		Vector3f vector = new Vector3f( x, .0f, .0f);
		transform.setTranslation(vector);
		grid.setTransform(transform);
		grid.addChild(sphere);
		bg.addChild(grid);
	}
	
	// Y axis made of cones
	
	for (float y = -1.0f; y <= 1.0f; y+= 0.1f){
		TransformGroup grid = new TransformGroup();
		Transform3D transform = new Transform3D();
		Cone cone = new Cone(0.005f, 0.1f, apy);
		Vector3f vector = new Vector3f(.0f, y, .0f);
		transform.setTranslation(vector);
		grid.setTransform(transform);
		grid.addChild(cone);
		bg.addChild(grid);
	}
	
	// Z axis made of cylinders
	
	for (float z = -1.0f; z <= 1.0f; z+= 0.1f){
		TransformGroup grid = new TransformGroup();
		Transform3D transform = new Transform3D();
		Cylinder cylinder = new Cylinder(0.005f, 0.01f, apz);
		Vector3f vector = new Vector3f(.0f, .0f, z);
		transform.setTranslation(vector);
		grid.setTransform(transform);
		grid.addChild(cylinder);
		bg.addChild(grid);
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
    
    bg.addChild(satellites);
    bg.addChild(planet);
    for (int i = 0; i < SatArrSize; i++){
    	bg.addChild(satellitesArray[i]);
    }
    //
    
    
    final Color3f light1Color = new Color3f(5.0f, 0.0f, 0.0f);

    
    final BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 10000000.0);
   // final Vector3f light1Direction = new Vector3f(4.0f, -7.0f, -12.0f);
   // final DirectionalLight light1 = new DirectionalLight(light1Color, light1Direction);
    final AmbientLight light1 = new AmbientLight(light1Color);
    light1.setInfluencingBounds(bounds);
    bg.addChild(light1);
    
    OrbitBehavior orbit = new OrbitBehavior();
    orbit.setSchedulingBounds(new BoundingSphere());
    universe.getViewingPlatform().setViewPlatformBehavior(orbit);
    
    universe.getViewingPlatform().setNominalViewingTransform();
    universe.addBranchGraph(bg);
  
  }
  
}