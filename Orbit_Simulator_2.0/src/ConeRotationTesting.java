import java.awt.Container;
import java.awt.*;
import java.awt.event.*;

import javax.media.j3d.Appearance;
import javax.media.j3d.Material;
import javax.media.j3d.PositionInterpolator;
import javax.media.j3d.RotPosPathInterpolator;
import javax.media.j3d.Texture;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.Alpha;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.RotationInterpolator;
import javax.media.j3d.RotationPathInterpolator;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.AmbientLight;

import javax.swing.Timer;

import javax.vecmath.Color4f;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import javax.vecmath.Matrix3d;
import javax.vecmath.Quat4f;

import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.geometry.Cone;
import com.sun.j3d.utils.geometry.Cylinder;

public class ConeRotationTesting {
	public static void main(String[] args){
		new ConeRotationTesting();
	}
	public ConeRotationTesting(){
		SimpleUniverse universe = new SimpleUniverse();
		BranchGroup bg = new BranchGroup();
		TransformGroup tg1 = new TransformGroup();
		TransformGroup tg2 = new TransformGroup();
		TransformGroup tgO = new TransformGroup();
		TransformGroup tgR = new TransformGroup();
		TransformGroup tgL = new TransformGroup();
		tgR.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		tg1.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		tg2.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		tgO.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		tgL.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		
		Transform3D translation = new Transform3D();
		Transform3D translationAxis = new Transform3D();
		Transform3D rotation = new Transform3D();
		Transform3D temp = new Transform3D();
		
		
		Color3f light1 = new Color3f(1.0f, 1.0f, 1.0f);
		
		Color3f meshA = new Color3f(1.0f, 1.0f, 1.0f);
		Color3f meshD = new Color3f(1.0f, 1.0f, 1.0f);
		Color3f meshS = new Color3f(1.0f, 1.0f, 1.0f);
		Color3f meshE = new Color3f(1.0f, 1.0f, 1.0f);
		Color3f meshE2 = new Color3f(.0f, .0f, .0f);
		Color3f meshD2 = new Color3f(.0f, .0f, .0f);
		Color3f meshS2 = new Color3f(.0f, .0f, .0f);
		Material mesh = new Material(meshA, meshD, meshS, meshE, 0.0f);
		Material mesh2 = new Material(meshA, meshD2, meshS2, meshE2, 0.0f);
		Appearance ap = new Appearance();
		Appearance ap2 = new Appearance();
		ap2.setMaterial(mesh2);
		ap.setMaterial(mesh);
		
		Sphere sphere = new Sphere(0.1f, ap2);
		Cone cone = new Cone(0.05f, 0.15f, 100, ap);
		
		temp.setTranslation(new Vector3f(0.0f, 0.0f, 0.0f));
		tg2.setTransform(temp);
		tg2.addChild(sphere);
		
		
		translation.setTranslation(new Vector3f(0.5f,0.0f,0.0f));
		rotation.rotX(Math.PI/2.0f);
		translationAxis.rotZ(Math.PI*2.0f);
		tg1.setTransform(translation);
		tg1.addChild(cone);
		tgO.addChild(tg1);
		
		long millis = 5000;
		Alpha alpha1 = new Alpha(-1, Alpha.INCREASING_ENABLE, 0, 0, millis, 0, 0, 0, 0, 0 );
		Alpha alpha2 = new Alpha(-1, Alpha.INCREASING_ENABLE, 0, 0, millis, 2000, 0, millis, 2000, 0 );
		int knotSize = 360;
		float[] knots = new float[knotSize];
		/*for (int i = 0; i < knotSize; i++){
			knots[i] = (float) (i+1) / knotSize;
		}*/
		knots[0] = 0.0f;
		for (int i = 1; i < knotSize - 1; i++){
			knots[i] = i / knotSize;
		}
		knots[359] = 1.00f;
		Quat4f[] quats = new Quat4f[knotSize];
		for (int i = 0; i < knotSize; i++){
			if (i % 2 == 0){
				quats[i] = new Quat4f(1.0f, 0.0f, 0.0f, 0.0f);
			} else {
				quats[i] = new Quat4f(0.0f, 1.0f, 0.0f, 0.0f);
			}
		}
		
		Point3f[] points = new Point3f[knotSize];
		for (int i = 0; i < knotSize; i ++){
			points[i] = new Point3f((float)Math.sin(i*(Math.PI/180)), (float)Math.cos(i *(Math.PI/180)), 0.0f);
		}
		
		//RotationPathInterpolator r1 = new RotationPathInterpolator(alpha1, tgO, rotation, knots, quats);
		RotationInterpolator r1 = new RotationInterpolator(alpha1, tgR, rotation, 0.0f, (float) Math.PI*2.0f);
		//RotationInterpolator r2 = new RotationInterpolator(alpha2, tgO);
		PositionInterpolator p1 = new PositionInterpolator(alpha1, tgO, translationAxis, 0.0f, -1.0f);
		RotPosPathInterpolator t1 = new RotPosPathInterpolator(alpha1, tgO, rotation, knots, quats, points);
		r1.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
		p1.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
		t1.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));

		tgO.addChild(t1);
		
		bg.addChild(tgO);
		bg.addChild(tg2);
		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
		AmbientLight ambient = new AmbientLight(light1);
		ambient.setInfluencingBounds(bounds);
		bg.addChild(ambient);
		
	    OrbitBehavior orbit = new OrbitBehavior();
		orbit.setSchedulingBounds(new BoundingSphere());
		
		universe.getViewingPlatform().setViewPlatformBehavior(orbit);
		universe.getViewingPlatform().setNominalViewingTransform();
		universe.addBranchGraph(bg);
	}
}
