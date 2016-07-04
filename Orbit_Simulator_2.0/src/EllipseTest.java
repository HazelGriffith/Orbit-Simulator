import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.universe.*;
import javax.media.j3d.*;
import javax.vecmath.*;

import com.sun.j3d.utils.geometry.Cone;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.image.TextureLoader;

import javax.swing.Timer;

public class EllipseTest extends Applet implements ActionListener, KeyListener {
	// Determines the amount of randomly generate satellites in orbit
	int satArrSize = 0;

	// Creates the button object
	Button go = new Button("Go");

	// Initializes the TrannsformGroup objects
	TransformGroup objTrans;
	TransformGroup[] Satellites = new TransformGroup[satArrSize];
	TransformGroup central;
	TransformGroup[] SatxAxis = new TransformGroup[20];
	TransformGroup[] SatyAxis = new TransformGroup[20];
	TransformGroup[] SatzAxis = new TransformGroup[20];
	TransformGroup latLong;

	// Initializes the Transform3D affine matrices for manipulating the shapes
	Transform3D[] transArray = new Transform3D[satArrSize];
	Transform3D ManSatTransF = new Transform3D();
	Transform3D rotateV1 = new Transform3D();
	Transform3D rotateV2 = new Transform3D();
	Transform3D rotate = new Transform3D();

	// These regRot transforms are used for moving the cone shaped region
	Transform3D regRot1 = new Transform3D();
	Transform3D regRot2 = new Transform3D();
	Transform3D regRot3 = new Transform3D();

	// initialize is the transform that combines the previous translations and
	// rotations
	// into one affine matrix
	Transform3D initialize = new Transform3D();

	// initializes the Timer object
	Timer timer;

	// Initializes the moving point within the cone and the point of the
	// manually created satellite
	Point3f coneRegion;
	Point3f satLocation = new Point3f(0.0f, 0.0f, 0.0f);

	// Initializes the angle of rotation for each object
	float angleS = 0.0f;
	float[] angleSats = new float[satArrSize];
	float angleE = 0.0f;
	// Geosynchronous orbit

	// float xMult = -1.019168f; // sets greatest distance along x-axis of orbit
	// float yMult = 0.0f; // sets greatest distance along y-axis of orbit float
	// float zMult = 1.019168f; // sets greatest distance along z-axis of orbit

	// custom orbit of manual satellite
	float xMult = 0.5f;
	float yMult = 0.5f;
	float zMult = 0.0f;

	// variable used to convert from degrees to radians
	float convert = (float) Math.PI / 180;

	// Initializes the multipliers of the randomly generate satellites
	float[] xMultArray = new float[satArrSize];
	float[] yMultArray = new float[satArrSize];
	float[] zMultArray = new float[satArrSize];

	// sets the Location of the Cone Region on earth
	float lat = 45.383082f;
	float lon = -75.698312f;

	// Initializes the variables that keep Earth at a focal point of each orbit
	float xTrans = 0.0f;
	float yTrans = 0.0f;
	float zTrans = 0.0f;
	float[] xTransArray = new float[satArrSize];
	float[] yTransArray = new float[satArrSize];
	float[] zTransArray = new float[satArrSize];

	// Initializes the height and radius of the Cone Region
	float height = 0.6f;
	float rad = 0.5f;

	// Initializes the radians per height value of the cone to determine the
	// radius of the cone at any elevation above earth
	float radperHeight = rad / height;

	/*
	 * float xloc = 0.0f; float yloc = 0.0f; float zloc = 0.0f;
	 */

	// Initializes the location of each randomly generated satellite
	float[] xlocArray = new float[satArrSize];
	float[] ylocArray = new float[satArrSize];
	float[] zlocArray = new float[satArrSize];

	// variables used to ultimately determine the speed of each randomly
	// generated satellite
	double[] radiusArray = new double[satArrSize];
	double[] dist = new double[satArrSize];

	// Initializes the vector which sets Earth at the correct focal point in
	// each ellipse
	Vector3f focalLength = new Vector3f();

	// Initializes the vector that adjusts the point coneRegion to be at the
	// same
	// elevation as the manually created satellite
	Vector3f determinesHeight = new Vector3f();
	
	//checks the state change from outside the cone to inside the cone
	Boolean collDetect = false;
	Boolean satRotation = false;

	public BranchGroup createSceneGraph() {

		//THIS FOR LOOP GENERATES THE RANDOM SATELLITES
		for (int i = 0; i < satArrSize; i++) {
			
			//generates each multiplier and makes 50% of them negative
			xMultArray[i] = (float) (Math.random() * 0.65f + 0.25);
			if (Math.random() > 0.5) {
				xMultArray[i] *= -1;
			}
			yMultArray[i] = (float) (Math.random() * 0.65f + 0.25);
			if (Math.random() > 0.5) {
				yMultArray[i] *= -1;
			}
			zMultArray[i] = (float) (Math.random() * 0.65f + 0.25);
			if (Math.random() > 0.5) {
				zMultArray[i] *= -1;
			}

			//these bulky if statements setup multiple cases for rotation to
			//avoid the satellites moving on a linear path
			if (xMultArray[i] == 0) {
				//THIS IS THE COS COS SIN CASE #1
				
				//Calculates the length of the axis when the angle is 0 degrees
				float axisTwo = (float) Math.sqrt(
						Math.pow(Math.cos(angleS) * xMultArray[i], 2) + Math.pow(Math.cos(angleS) * yMultArray[i], 2)
								+ Math.pow(Math.sin(angleS) * zMultArray[i], 2));
				
				//Calculates the length of the axis when the angle is 90 degrees
				float axisOne = (float) Math.sqrt(Math.pow(Math.cos(Math.PI / 2.0f) * xMultArray[i], 2)
						+ Math.pow(Math.cos(Math.PI / 2.0f) * yMultArray[i], 2)
						+ Math.pow(Math.sin(Math.PI / 2.0f) * zMultArray[i], 2));

				//Calculates the distance between the center of the ellipse and 
				//a focal point
				float F = (float) Math.sqrt(Math.abs(Math.pow(axisOne, 2) - Math.pow(axisTwo, 2)));

				//checks that the satellites don't appear to move within the earth
				while (((axisTwo > axisOne)&&(axisTwo - F < 0.17f))^((axisOne > axisTwo)&&(axisOne - F < 0.17f))){
					
					//generates each multiplier and makes 50% of them negative
					xMultArray[i] = (float) (Math.random() * 0.65f + 0.25);
					if (Math.random() > 0.5) {
						xMultArray[i] *= -1;
					}
					yMultArray[i] = (float) (Math.random() * 0.65f + 0.25);
					if (Math.random() > 0.5) {
						yMultArray[i] *= -1;
					}
					zMultArray[i] = (float) (Math.random() * 0.65f + 0.25);
					if (Math.random() > 0.5) {
						zMultArray[i] *= -1;
					}
					
					//Calculates the length of the axis when the angle is 0 degrees
					axisTwo = (float) Math.sqrt(
							Math.pow(Math.cos(angleS) * xMultArray[i], 2) + Math.pow(Math.cos(angleS) * yMultArray[i], 2)
									+ Math.pow(Math.sin(angleS) * zMultArray[i], 2));
					
					//Calculates the length of the axis when the angle is 90 degrees
					axisOne = (float) Math.sqrt(Math.pow(Math.cos(Math.PI / 2.0f) * xMultArray[i], 2)
							+ Math.pow(Math.cos(Math.PI / 2.0f) * yMultArray[i], 2)
							+ Math.pow(Math.sin(Math.PI / 2.0f) * zMultArray[i], 2));

					//Calculates the distance between the center of the ellipse and 
					//a focal point
					F = (float) Math.sqrt(Math.abs(Math.pow(axisOne, 2) - Math.pow(axisTwo, 2)));
					
				}
				
				//Determines which axis is longest to find out on which axis the
				//focal points would be.
				if (axisTwo > axisOne) {
						focalLength = new Vector3f((float) Math.cos(angleS) * xMultArray[i],
								(float) Math.cos(angleS) * yMultArray[i], (float) Math.sin(angleS) * zMultArray[i]);
						focalLength.normalize();
						focalLength.x *= F;
						focalLength.y *= F;
						focalLength.z *= F;
				} else if (axisTwo < axisOne) {
						focalLength = new Vector3f((float) Math.cos(Math.PI / 2.0f) * xMultArray[i],
								(float) Math.cos(Math.PI / 2.0f) * yMultArray[i],
								(float) Math.sin(Math.PI / 2.0f) * zMultArray[i]);
						focalLength.normalize();
						focalLength.x *= F;
						focalLength.y *= F;
						focalLength.z *= F;
				} else {
					focalLength = new Vector3f(0.0f, 0.0f, 0.0f);
				}
				
				xTransArray[i] = -1 * focalLength.x;
				yTransArray[i] = -1 * focalLength.y;
				zTransArray[i] = -1 * focalLength.z;

				xlocArray[i] = (float) Math.cos(angleS) * xMultArray[i] + xTransArray[i];
				ylocArray[i] = (float) Math.cos(angleS) * yMultArray[i] + yTransArray[i];
				zlocArray[i] = (float) Math.sin(angleS) * zMultArray[i] + zTransArray[i];
			} else if ((yMultArray[i] == 0) ^ (zMultArray[i] == 0) ^ ((yMultArray[i] != 0) && (zMultArray[i] != 0))) {
				//THIS IS THE COS SIN SIN CASE #2
				
				float axisTwo = (float) Math.sqrt(
						Math.pow(Math.cos(angleS) * xMultArray[i], 2) + Math.pow(Math.sin(angleS) * yMultArray[i], 2)
								+ Math.pow(Math.sin(angleS) * zMultArray[i], 2));
				float axisOne = (float) Math.sqrt(Math.pow(Math.cos(Math.PI / 2.0f) * xMultArray[i], 2)
						+ Math.pow(Math.sin(Math.PI / 2.0f) * yMultArray[i], 2)
						+ Math.pow(Math.sin(Math.PI / 2.0f) * zMultArray[i], 2));
				float F = (float) Math.sqrt(Math.abs(Math.pow(axisOne, 2) - Math.pow(axisTwo, 2)));
				
				while (((axisTwo > axisOne)&&(axisTwo - F < 0.17f))^((axisOne > axisTwo)&&(axisOne - F < 0.17f))){
					//generates each multiplier and makes 50% of them negative
					xMultArray[i] = (float) (Math.random() * 0.65f + 0.25);
					if (Math.random() > 0.5) {
						xMultArray[i] *= -1;
					}
					yMultArray[i] = (float) (Math.random() * 0.65f + 0.25);
					if (Math.random() > 0.5) {
						yMultArray[i] *= -1;
					}
					zMultArray[i] = (float) (Math.random() * 0.65f + 0.25);
					if (Math.random() > 0.5) {
						zMultArray[i] *= -1;
					}
					
					axisTwo = (float) Math.sqrt(
							Math.pow(Math.cos(angleS) * xMultArray[i], 2) + Math.pow(Math.sin(angleS) * yMultArray[i], 2)
									+ Math.pow(Math.sin(angleS) * zMultArray[i], 2));
					axisOne = (float) Math.sqrt(Math.pow(Math.cos(Math.PI / 2.0f) * xMultArray[i], 2)
							+ Math.pow(Math.sin(Math.PI / 2.0f) * yMultArray[i], 2)
							+ Math.pow(Math.sin(Math.PI / 2.0f) * zMultArray[i], 2));
					F = (float) Math.sqrt(Math.abs(Math.pow(axisOne, 2) - Math.pow(axisTwo, 2)));
					
				}
				
				if (axisTwo > axisOne) {
						
						focalLength = new Vector3f((float) Math.cos(angleS) * xMultArray[i],
								(float) Math.sin(angleS) * yMultArray[i], (float) Math.sin(angleS) * zMultArray[i]);
						
						focalLength.normalize();
						focalLength.x *= F;
						focalLength.y *= F;
						focalLength.z *= F;
				} else if (axisTwo < axisOne) {
						
						focalLength = new Vector3f((float) Math.cos(Math.PI / 2.0f) * xMultArray[i],
								(float) Math.sin(Math.PI / 2.0f) * yMultArray[i],
								(float) Math.sin(Math.PI / 2.0f) * zMultArray[i]);
						
						focalLength.normalize();
						focalLength.x *= F;
						focalLength.y *= F;
						focalLength.z *= F;
				} else {
					focalLength = new Vector3f(0.0f, 0.0f, 0.0f);
				}
				xTransArray[i] = -1 * focalLength.x;
				yTransArray[i] = -1 * focalLength.y;
				zTransArray[i] = -1 * focalLength.z;

				xlocArray[i] = (float) Math.cos(angleS) * xMultArray[i] + xTransArray[i];
				ylocArray[i] = (float) Math.sin(angleS) * yMultArray[i] + yTransArray[i];
				zlocArray[i] = (float) Math.sin(angleS) * zMultArray[i] + zTransArray[i];
			} else {

				System.out.println("You chose incompatible values for xMult, yMult, or zMult");

			}
		}

		// Sets Earth at the focal point of the ellipse of the manual satellite
		if (xMult == 0) {
			float axisTwo = (float) Math.sqrt(Math.pow(Math.cos(angleS) * xMult, 2)
					+ Math.pow(Math.cos(angleS) * yMult, 2) + Math.pow(Math.sin(angleS) * zMult, 2));
			float axisOne = (float) Math.sqrt(Math.pow(Math.cos(Math.PI / 2.0f) * xMult, 2)
					+ Math.pow(Math.cos(Math.PI / 2.0f) * yMult, 2) + Math.pow(Math.sin(Math.PI / 2.0f) * zMult, 2));

			float F = (float) Math.sqrt(Math.abs(Math.pow(axisOne, 2) - Math.pow(axisTwo, 2)));
			if (axisTwo > axisOne) {
				focalLength = new Vector3f((float) Math.cos(angleS) * xMult, (float) Math.cos(angleS) * yMult,
						(float) Math.sin(angleS) * zMult);
				focalLength.normalize();
				focalLength.x *= F;
				focalLength.y *= F;
				focalLength.z *= F;
			} else if (axisTwo < axisOne) {
				focalLength = new Vector3f((float) Math.cos(Math.PI / 2.0f) * xMult,
						(float) Math.cos(Math.PI / 2.0f) * yMult, (float) Math.sin(Math.PI / 2.0f) * zMult);
				focalLength.normalize();
				focalLength.x *= F;
				focalLength.y *= F;
				focalLength.z *= F;
			} else {
				focalLength = new Vector3f(0.0f, 0.0f, 0.0f);
			}
			xTrans = -1 * focalLength.x;
			yTrans = -1 * focalLength.y;
			zTrans = -1 * focalLength.z;

			satLocation.x = (float) Math.cos(angleS) * xMult + xTrans;
			satLocation.y = (float) Math.cos(angleS) * yMult + yTrans;
			satLocation.z = (float) Math.sin(angleS) * zMult + zTrans;
		} else if ((yMult == 0) ^ (zMult == 0) ^ ((yMult != 0) && (zMult != 0))) {
			float axisTwo = (float) Math.sqrt(Math.pow(Math.cos(angleS) * xMult, 2)
					+ Math.pow(Math.sin(angleS) * yMult, 2) + Math.pow(Math.sin(angleS) * zMult, 2));
			float axisOne = (float) Math.sqrt(Math.pow(Math.cos(Math.PI / 2.0f) * xMult, 2)
					+ Math.pow(Math.sin(Math.PI / 2.0f) * yMult, 2) + Math.pow(Math.sin(Math.PI / 2.0f) * zMult, 2));
			float F = (float) Math.sqrt(Math.abs(Math.pow(axisOne, 2) - Math.pow(axisTwo, 2)));
			if (axisTwo > axisOne) {
				focalLength = new Vector3f((float) Math.cos(angleS) * xMult, (float) Math.sin(angleS) * yMult,
						(float) Math.sin(angleS) * zMult);
				focalLength.normalize();
				focalLength.x *= F;
				focalLength.y *= F;
				focalLength.z *= F;
			} else if (axisTwo < axisOne) {
				focalLength = new Vector3f((float) Math.cos(Math.PI / 2.0f) * xMult,
						(float) Math.sin(Math.PI / 2.0f) * yMult, (float) Math.sin(Math.PI / 2.0f) * zMult);
				focalLength.normalize();
				focalLength.x *= F;
				focalLength.y *= F;
				focalLength.z *= F;
			} else {
				focalLength = new Vector3f(0.0f, 0.0f, 0.0f);
			}
			xTrans = -1 * focalLength.x;
			yTrans = -1 * focalLength.y;
			zTrans = -1 * focalLength.z;

			satLocation.x = (float) Math.cos(angleS) * xMult + xTrans;
			satLocation.y = (float) Math.sin(angleS) * yMult + yTrans;
			satLocation.z = (float) Math.sin(angleS) * zMult + zTrans;
		} else {

			System.out.println("You chose incompatible values for xMult, yMult, or zMult");

		}

		// Setup materials for ambient light
		// sets the earth image as the texture of Earth and wraps it around the
		// sphere

		TextureLoader lodGrey = new TextureLoader("C:\\Users\\Ryan\\Pictures\\ProjectPhotos\\grey.jpg", "RGB",
				new Container());
		TextureLoader lodGreen = new TextureLoader("C:\\Users\\Ryan\\Pictures\\ProjectPhotos\\green.jpg", "RGB",
				new Container());
		TextureLoader lodRed = new TextureLoader("C:\\Users\\Ryan\\Pictures\\ProjectPhotos\\red.jpg", "RGB",
				new Container());
		TextureLoader lodBlue = new TextureLoader("C:\\Users\\Ryan\\Pictures\\ProjectPhotos\\blue.jpg", "RGB",
				new Container());
		TextureLoader loader = new TextureLoader("C:\\Users\\Ryan\\Pictures\\Saved Pictures\\earth.jpg", "RGB",
				new Container());
		Texture grey = lodGrey.getTexture();
		Texture green = lodGreen.getTexture();
		Texture blue = lodBlue.getTexture();
		Texture red = lodRed.getTexture();
		Texture texture = loader.getTexture();
		grey.setBoundaryModeS(Texture.WRAP);
		grey.setBoundaryModeT(Texture.WRAP);
		green.setBoundaryModeS(Texture.WRAP);
		green.setBoundaryModeT(Texture.WRAP);
		red.setBoundaryModeS(Texture.WRAP);
		red.setBoundaryModeT(Texture.WRAP);
		blue.setBoundaryModeS(Texture.WRAP);
		blue.setBoundaryModeT(Texture.WRAP);
		texture.setBoundaryModeS(Texture.WRAP);
		texture.setBoundaryModeT(Texture.WRAP);
		grey.setBoundaryColor(new Color4f(0.0f, 1.0f, 0.0f, 0.0f));
		green.setBoundaryColor(new Color4f(0.0f, 1.0f, 0.0f, 0.0f));
		red.setBoundaryColor(new Color4f(0.0f, 1.0f, 0.0f, 0.0f));
		blue.setBoundaryColor(new Color4f(0.0f, 1.0f, 0.0f, 0.0f));
		texture.setBoundaryColor(new Color4f(0.0f, 1.0f, 0.0f, 0.0f));

		TextureAttributes texAttr = new TextureAttributes();
		texAttr.setTextureMode(TextureAttributes.MODULATE);
		Appearance apx = new Appearance();
		Appearance apy = new Appearance();
		Appearance apz = new Appearance();
		Appearance apE = new Appearance();
		Appearance apR = new Appearance();
		Appearance apS = new Appearance();

		apS.setTexture(grey);
		apS.setTextureAttributes(texAttr);

		apR.setTexture(grey);
		apR.setTextureAttributes(texAttr);
		apR.setTransparencyAttributes(new TransparencyAttributes(1, 0.5F));

		apy.setTexture(green);
		apy.setTextureAttributes(texAttr);

		apz.setTexture(red);
		apz.setTextureAttributes(texAttr);

		apx.setTexture(blue);
		apx.setTextureAttributes(texAttr);

		apE.setTexture(texture);
		apE.setTextureAttributes(texAttr);

		int primflags = Primitive.GENERATE_NORMALS + Primitive.GENERATE_TEXTURE_COORDS;

		BranchGroup objRoot = new BranchGroup();
		objTrans = new TransformGroup();
		central = new TransformGroup();
		latLong = new TransformGroup();
		for (int i = 0; i < satArrSize; i++) {
			Satellites[i] = new TransformGroup();
			Satellites[i].setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
			objRoot.addChild(Satellites[i]);
		}
		for (int i = 0; i < 20; i++){
			SatxAxis[i] = new TransformGroup();
			SatyAxis[i] = new TransformGroup();
			SatzAxis[i] = new TransformGroup();
			SatxAxis[i].setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
			SatyAxis[i].setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
			SatzAxis[i].setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
			objRoot.addChild(SatxAxis[i]);
			objRoot.addChild(SatyAxis[i]);
			objRoot.addChild(SatzAxis[i]);
		}
		objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		central.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		latLong.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		objRoot.addChild(objTrans);
		objRoot.addChild(central);
		objRoot.addChild(latLong);

		// X axis
		//They're all made of spheres for simplicity's sake
		
		for (float x = -5.0f; x <= 5.0f; x += 0.1f) {
			Sphere xAxis = new Sphere(0.005f, 100, apx);
			TransformGroup grid = new TransformGroup();
			Transform3D transform = new Transform3D();
			Vector3f vector = new Vector3f(x, .0f, .0f);
			transform.setTranslation(vector);
			grid.setTransform(transform);
			grid.addChild(xAxis);
			objTrans.addChild(grid);
		}

		// Y axis

		for (float y = -5.0f; y <= 5.0f; y += 0.1f) {
			TransformGroup grid = new TransformGroup();
			Transform3D transform = new Transform3D();
			Sphere yAxis = new Sphere(0.005f, 100, apy);
			Vector3f vector = new Vector3f(.0f, y, .0f);
			transform.setTranslation(vector);
			grid.setTransform(transform);
			grid.addChild(yAxis);
			objTrans.addChild(grid);
		}

		// Z axis

		for (float z = -5.0f; z <= 5.0f; z += 0.1f) {
			TransformGroup grid = new TransformGroup();
			Transform3D transform = new Transform3D();
			Sphere zAxis = new Sphere(0.005f, 100, apz);
			Vector3f vector = new Vector3f(.0f, .0f, z);
			transform.setTranslation(vector);
			grid.setTransform(transform);
			grid.addChild(zAxis);
			objTrans.addChild(grid);
		}

		// Creates the Earth, Region and Satellite objects
		Cone[] sats = new Cone[satArrSize];
		for (int i = 0; i < satArrSize; i++) {
			sats[i] = new Cone(0.02f, 0.07f, 1000, apS);
		}
		Cone sat = new Cone(0.02f, 0.07f, 1000, apS);
		Cone region = new Cone(rad, height, 1000, apR);
		Sphere earth = new Sphere(.1539f, primflags, 100, apE);

		// Sets up the required TransformGroup class objects to manage each
		// shape
		objTrans = new TransformGroup();
		central = new TransformGroup();
		latLong = new TransformGroup();
		for (int i = 0; i < satArrSize; i++) {
			Satellites[i] = new TransformGroup();
			Satellites[i].setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		}
		for (int i = 0; i < 20; i++){
			SatxAxis[i] = new TransformGroup();
			SatyAxis[i] = new TransformGroup();
			SatzAxis[i] = new TransformGroup();
			SatxAxis[i].setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
			SatyAxis[i].setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
			SatzAxis[i].setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		}
		objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		central.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		latLong.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

		// Sets up the initial position of each shape before GO is pressed

		// sets up the Region object
		Transform3D pos3 = new Transform3D();
		Transform3D temp = new Transform3D();
		Transform3D temp2 = new Transform3D();
		float angle1 = (float) ((-66.56285 - lat - 23.43715)*convert);
		float angle2 = (float) (lon*convert);
		pos3.rotX(angle1);
		temp.rotY(angle2);
		temp2.rotX(23.43715*convert);
		temp.mul(pos3);
		temp2.mul(temp);
		Matrix3f matrix = new Matrix3f();
		temp2.get(matrix);
		
		Vector3f setupTrans = new Vector3f(0.0f, -1.0f, 0.0f);
		temp2.transform(setupTrans);

		setupTrans.normalize();
		
		setupTrans.x *= (0.1539f + height / 2.0f);
		setupTrans.y *= (0.1539f + height / 2.0f);
		setupTrans.z *= (0.1539f + height / 2.0f);

		Transform3D initialize = new Transform3D(matrix, setupTrans, 1.0f);
		
		// Sets up the Earth and Satellite objects
		Transform3D[] posArray = new Transform3D[satArrSize];
		for (int i = 0; i < satArrSize; i++) {
			posArray[i] = new Transform3D();
			posArray[i].setTranslation(new Vector3f(xlocArray[i], ylocArray[i], zlocArray[i]));
		}
		Transform3D pos1 = new Transform3D();
		pos1.setTranslation(new Vector3f(satLocation.x, satLocation.y, satLocation.z));
		
		float xS = 0.0f;
		for (int i = 0; i < 20; i++){
			Sphere ball = new Sphere(0.005f, 100, apx);
			Transform3D transform = new Transform3D();
			transform.setTranslation(new Vector3f(xS, .0f, .0f));
			transform.mul(pos1);
			SatxAxis[i].setTransform(transform);
			SatxAxis[i].addChild(ball);
			objRoot.addChild(SatxAxis[i]);
			xS += 0.01;
		}
		
		float yS = 0.0f;
		for (int i = 0; i < 20; i++){
			Sphere ball = new Sphere(0.005f, 100, apy);
			Transform3D transform = new Transform3D();
			transform.setTranslation(new Vector3f(.0f, yS, .0f));
			transform.mul(pos1);
			SatyAxis[i].setTransform(transform);
			SatyAxis[i].addChild(ball);
			objRoot.addChild(SatyAxis[i]);
			yS += 0.01;
		}
		
		float zS = 0.0f;
		for (int i = 0; i < 20; i++){
			Sphere ball = new Sphere(0.005f, 100, apz);
			Transform3D transform = new Transform3D();
			transform.setTranslation(new Vector3f(.0f, .0f, zS));
			transform.mul(pos1);
			SatzAxis[i].setTransform(transform);
			SatzAxis[i].addChild(ball);
			objRoot.addChild(SatzAxis[i]);
			zS += 0.01;
		}
		
		Transform3D pos2 = new Transform3D();
		pos2.setTranslation(new Vector3f(0.0f, 0.0f, 0.0f));
		Transform3D axialTilt = new Transform3D();
		axialTilt.rotX(23.43715 * convert);
		axialTilt.mul(pos2);

		// Applies the created Transforms and adds them to the Scene Graph
		for (int i = 0; i < satArrSize; i++) {
			Satellites[i].setTransform(posArray[i]);
			Satellites[i].addChild(sats[i]);
			objRoot.addChild(Satellites[i]);
		}
		
		central.setTransform(axialTilt);
		objTrans.setTransform(pos1);
		latLong.setTransform(initialize);
		central.addChild(earth);
		objTrans.addChild(sat);
		latLong.addChild(region);
		objRoot.addChild(objTrans);
		objRoot.addChild(central);
		objRoot.addChild(latLong);

		// Sets up the ambient lighting, though this code is admittedly glitched
		// yet working. As it seemingly has no effect on the appearance of the
		// simulation.
		/*
		 * final Color3f light1Color = new Color3f(1.0f, 1.0f, 0.0f); final
		 * BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0,
		 * 0.0), 10000000.0); final AmbientLight light1 = new
		 * AmbientLight(light1Color); light1.setInfluencingBounds(bounds);
		 * objRoot.addChild(light1);
		 */

		return objRoot;
	}

	public EllipseTest() {
		setLayout(new BorderLayout());
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		Canvas3D c = new Canvas3D(config);
		add("Center", c);
		c.addKeyListener(this);
		timer = new Timer(10, this);

		// Timer.start()

		Panel p = new Panel();
		p.add(go);
		add("North", p);
		go.addActionListener(this);
		go.addKeyListener(this);

		// Create a simple scene attach it to the virtual universe

		OrbitBehavior orbit = new OrbitBehavior();
		orbit.setSchedulingBounds(new BoundingSphere());

		BranchGroup scene = createSceneGraph();
		SimpleUniverse u = new SimpleUniverse(c);
		u.getViewingPlatform().setViewPlatformBehavior(orbit);
		u.getViewingPlatform().setNominalViewingTransform();
		u.addBranchGraph(scene);
	}

	public void keyPressed(KeyEvent e) {

		// Invoked when a key has been pressed.
	}

	public void keyReleased(KeyEvent e) {

		// Invoked when a key has been released.

	}

	public void keyTpyed(KeyEvent e) {
		// Invoked when a key has been typed.
	}

	public void actionPerformed(ActionEvent e) {

		// start timer when button is pressed

		if (e.getSource() == go) {
			if (!timer.isRunning()) {
				timer.start();
			}
		} else {

			//calculates rotation angles basedon lat and lon variables
			float angle1 = (float) ((-66.56285 - lat - 23.43715) * convert);
			float angle2 = (float) (lon * convert);
			
			//Creates the rotation transforms and multiplies them together
			regRot1.rotX(angle1);
			regRot2.rotY(angle2);
			regRot3.rotX(23.43715 * convert);
			regRot2.mul(regRot1);
			regRot3.mul(regRot2);
			
			//Creates 3x3 matrix that contains the rotation transforms
			Matrix3f matrix = new Matrix3f();
			regRot3.get(matrix);
			
			//Create vector that translates the cone to the surface of the earth
			Vector3f setupTrans = new Vector3f(0.0f, -1.0f, 0.0f);
			
			//rotates the vector with the cone region
			regRot3.transform(setupTrans);

			//normalizes the vector and gives the correct length
			setupTrans.normalize();
			setupTrans.x *= (0.1539f + height / 2.0f);
			setupTrans.y *= (0.1539f + height / 2.0f);
			setupTrans.z *= (0.1539f + height / 2.0f);
			
			//creates a point that has the same coordinates as the centre of the
			//Cone region
			coneRegion = new Point3f(setupTrans.z, setupTrans.y, setupTrans.x);

			//Combines the rotation matrix and translation vector into one 
			//Transform3D object
			initialize = new Transform3D(matrix, setupTrans, 1.0f);

			//Calculates and applies the correct translation of each randomly
			//generated satellite.
			for (int i = 0; i < satArrSize; i++) {
				transArray[i] = new Transform3D();
				radiusArray[i] = (double) (Math
						.sqrt(Math.pow(xlocArray[i], 2.0) + Math.pow(ylocArray[i], 2.0) + Math.pow(zlocArray[i], 2.0))
						* 41371 * 1000);

				double tangential_SpeedSats = (double) (Math
						.sqrt(((6.67 * Math.pow(10.0, -11.0)) * (5.972 * Math.pow(10.0, 24.0))) / radiusArray[i]));

				double angular_speedSats = (double) tangential_SpeedSats / radiusArray[i];

				dist[i] = angular_speedSats * 0.01 * 10000;

				if (xMultArray[i] == 0) {
					xlocArray[i] = (float) Math.cos(angleSats[i]) * xMultArray[i] + xTransArray[i];
					ylocArray[i] = (float) Math.cos(angleSats[i]) * yMultArray[i] + yTransArray[i];
					zlocArray[i] = (float) Math.sin(angleSats[i]) * zMultArray[i] + zTransArray[i];
				} else if ((yMultArray[i] == 0) ^ (zMultArray[i] == 0)
						^ ((yMultArray[i] != 0) && (zMultArray[i] != 0))) {
					xlocArray[i] = (float) Math.cos(angleSats[i]) * xMultArray[i] + xTransArray[i];
					ylocArray[i] = (float) Math.sin(angleSats[i]) * yMultArray[i] + yTransArray[i];
					zlocArray[i] = (float) Math.sin(angleSats[i]) * zMultArray[i] + zTransArray[i];
				}

				angleSats[i] += dist[i];

				transArray[i].setTranslation(new Vector3f(xlocArray[i], ylocArray[i], zlocArray[i]));
				Satellites[i].setTransform(transArray[i]);
			}

			//CALCULATES AND APPLIES THE TRANSLATION OF THE MANUALLY CREATED
			//SATELLITE
			
			// Calculates the radius of the satellite.
			double radius = (double) Math
					.sqrt(Math.pow(satLocation.x, 2.0) + Math.pow(satLocation.y, 2.0) + Math.pow(satLocation.z, 2.0))
					* 41371 * 1000;

			// v = sqrt(G*Me/r)
			double tangential_speedS = (double) (Math
					.sqrt(((6.67 * Math.pow(10.0, -11.0)) * (5.972 * Math.pow(10.0, 24.0))) / radius));

			// w = v / r
			double angular_speedS = tangential_speedS / radius;

			// calculate the radians traveled every hundredth of a second.
			double dist = angular_speedS * 0.01 * 10000;

			//Calculates the correct translation for the manually generated 
			//satellite
			if (xMult == 0) {
				satLocation.x = (float) Math.cos(angleS) * xMult + xTrans;
				satLocation.y = (float) Math.cos(angleS) * yMult + yTrans;
				satLocation.z = (float) Math.sin(angleS) * zMult + zTrans;
			} else if ((yMult == 0) ^ (zMult == 0) ^ ((yMult != 0) && (zMult != 0))) {
				satLocation.x = (float) Math.cos(angleS) * xMult + xTrans;
				satLocation.y = (float) Math.sin(angleS) * yMult + yTrans;
				satLocation.z = (float) Math.sin(angleS) * zMult + zTrans;
			}

			//Calculates the angle of the satellites rotation, starting at 0
			angleS += (float) dist;
			
			//Calculates the angle of the earth's rotation, starting at 0
			angleE += (7.27 * Math.pow(10, -7)) * 10000;
			
			//Creates the Transform3D object that is used to rotate the earth
			Transform3D axialTilt = new Transform3D();
			axialTilt.rotX(23.43715 * convert);
			rotate.rotY(angleE);
			rotate.mul(axialTilt);
			
			//This rotates the cone region at the same rate as earth
			regRot2.rotY(angleE);

			//Thiss combines the initialize transform with the earth rotation
			//transform
			regRot2.mul(initialize);
			
			//This keeps the Point3f object centered in the Cone Region
			regRot2.transform(coneRegion);

			//This creates the vector that is used to translate the point 
			//coneRegion to always be at the same elevation as the manually
			//generated satellite
			determinesHeight = new Vector3f(0.0f, -1.0f, 0.0f);
			regRot2.transform(determinesHeight);
			determinesHeight.normalize();

			//Checks if the main satellite has entered the cone region
			if (radius / 41371 / 1000 < height + 0.1539) {
				
				//calculates the height difference between the coneRegion point 
				//and the satellite
				float heightDiff = (float) (radius / 41371 / 1000) - coneRegion.distance(new Point3f(0.0f, 0.0f, 0.0f));
				
				//changes the vector length to the difference in elevation
				determinesHeight.x *= heightDiff;
				determinesHeight.y *= heightDiff;
				determinesHeight.z *= heightDiff;

				//Translates the coneRegion point to be at the same height as
				//satellite
				coneRegion.x += determinesHeight.x;
				coneRegion.y += determinesHeight.y;
				coneRegion.z += determinesHeight.z;
				
				//This determines if the satellite is within the cone
				if (coneRegion.distance(satLocation) < radperHeight * (coneRegion.distance(new Point3f(0.0f, 0.0f, 0.0f)))-0.1539) {
					
					//This if-statement is used to avoid infinite proximity detections
					//while the satellite is within the cone
					if (collDetect == true){
						//System.out.println("COLLISION");
						satRotation = true;
						collDetect = false;
					}
				} else {
					collDetect = true;
				}
			}
			
			if (satRotation == true){
				Point3f target = new Point3f(0.0f, 0.0f, 0.0f);
				Vector3f checkSign = new Vector3f(0.0f, 1.0f, 0.0f);
				Vector3f pointDirect = new Vector3f(target.x - satLocation.x, target.y - satLocation.y, target.z - satLocation.z);
				float angleOfXrot = pointDirect.angle(checkSign);
				
				if (pointDirect.z < 0 ){
					rotateV1.rotX(angleOfXrot);
				} else if (pointDirect.z >= 0){
					rotateV1.rotX(-1 * angleOfXrot);
				}

				rotateV1.transform(checkSign);
				float angleofYrot = pointDirect.angle(checkSign);
				
				if (checkSign.x > 0){
					if (pointDirect.z > 0){
						rotateV2.rotY(-1 * angleofYrot);
					} else if (pointDirect.z <= 0){
						rotateV2.rotY(angleofYrot);
					}
				} else if (checkSign.x <= 0 ){
					if (pointDirect.z > 0){
						rotateV2.rotY(angleofYrot);
					} else if (pointDirect.z <= 0){
						rotateV2.rotY(-1 * angleofYrot);
					}
				}
				rotateV2.mul(rotateV1);
			}
			
			Vector3f satPosition = new Vector3f(satLocation.x, satLocation.y, satLocation.z);
			Transform3D translate = new Transform3D();
			translate.setTranslation(satPosition);
			
			rotateV2.get(matrix);
			ManSatTransF = new Transform3D(matrix, satPosition, 1.0f);
			
			float xS = 0.0f;
			for (int i = 0; i < 20; i++){
				Transform3D transform = new Transform3D();
				transform.setTranslation(new Vector3f(xS, .0f, .0f));
				rotateV2.mul(transform);
				translate.mul(rotateV2);
				SatxAxis[i].setTransform(translate);
				xS += 0.01;
				rotateV2.set(matrix);
				translate.set(satPosition);
			}
			
			float yS = 0.0f;
			for (int i = 0; i < 20; i++){
				Transform3D transform = new Transform3D();
				transform.setTranslation(new Vector3f(.0f, yS, .0f));
				rotateV2.mul(transform);
				translate.mul(rotateV2);
				SatyAxis[i].setTransform(translate);
				yS += 0.01;
				rotateV2.set(matrix);
				translate.set(satPosition);
			}
			
			float zS = 0.0f;
			for (int i = 0; i < 20; i++){
				Transform3D transform = new Transform3D();
				transform.setTranslation(new Vector3f(.0f, .0f, zS));
				rotateV2.mul(transform);
				translate.mul(rotateV2);
				SatzAxis[i].setTransform(translate);
				zS += 0.01;
				rotateV2.set(matrix);
				translate.set(satPosition);
			}
			
			//These statements apply the transform matrices to the objects
			central.setTransform(rotate);
			objTrans.setTransform(ManSatTransF);
			latLong.setTransform(regRot2);
		}
	}

	public static void main(String[] args) {
		
		System.out.println("Program started");
		EllipseTest bb = new EllipseTest();
		
		//To be honest I have only a vague idea of how keyListener works,
		//and I have no idea what MainFrame does. Though I get the impression 
		//that it creates the window.
		bb.addKeyListener(bb);
		MainFrame mf = new MainFrame(bb, 256, 256);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}
}