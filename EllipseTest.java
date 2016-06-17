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
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.image.TextureLoader;

import javax.swing.Timer;

public class EllipseTest extends Applet implements ActionListener, KeyListener {
	private Button go = new Button("Go");
	private TransformGroup objTrans;
	private TransformGroup central;
	private Transform3D trans = new Transform3D();
	private Transform3D rotate = new Transform3D();
	private Timer timer;
	private float angleS = 0.0f;
	private float angleE = 0.0f;
	float xMult = 1.5f;
	float yMult = 1.0f;
	float zMult = 2.0f;
	float xTrans = 0.0f;
	float yTrans = 0.0f;
	float zTrans = 0.0f;
	float xloc = 0.0f;
	float yloc = 0.0f;
	float zloc = 0.0f;

	// distance of focal points from center
	// float fociLength = (float) Math.sqrt(Math.pow(axesDist[0], 2) -
	// Math.pow(axesDist[1], 2));

	// Vector3f satTrans = new Vector3f(-1* xloc, -1 * yloc, -1 * zloc);

	// satTrans = satTrans.normalize();

	public BranchGroup createSceneGraph() {

		if (xMult == 0) {
			float axisTwo = (float) Math.sqrt(Math.pow(Math.cos(angleS) * xMult, 2)
					+ Math.pow(Math.cos(angleS) * yMult, 2)
					+ Math.pow(Math.sin(angleS) * zMult, 2));
			float axisOne = (float) Math.sqrt(Math.pow(Math.cos(Math.PI / 2.0f) * xMult, 2)
					+ Math.pow(Math.cos(Math.PI / 2.0f) * yMult, 2)
					+ Math.pow(Math.sin(Math.PI / 2.0f) * zMult, 2));
			
			float F = (float) Math.sqrt(Math.pow(axisOne, 2) - Math.pow(axisTwo, 2));
			Vector3f focalLength;
			if (axisTwo > axisOne){
				focalLength = new Vector3f((float)Math.cos(angleS) * xMult, (float)Math.cos(angleS) * yMult, (float) Math.sin(angleS) * zMult);
				focalLength.normalize();
				focalLength.x *= F;
				focalLength.y *= F;
				focalLength.z *= F;
			} else if (axisTwo < axisOne){
				focalLength = new Vector3f((float)Math.cos(Math.PI/2.0f) * xMult, (float)Math.cos(Math.PI/2.0f) * yMult, (float) Math.sin(Math.PI/2.0f) * zMult);
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
			
			xloc = (float) Math.cos(angleS) * xMult + xTrans;
			yloc = (float) Math.cos(angleS) * yMult + yTrans;
			zloc = (float) Math.sin(angleS) * zMult + zTrans;
		} else if ((yMult == 0) ^ (zMult == 0) ^ ((yMult != 0) && (zMult != 0))) {
			float axisTwo = (float) Math.sqrt(Math.pow(Math.cos(angleS) * xMult, 2)
					+ Math.pow(Math.cos(angleS) * yMult, 2)
					+ Math.pow(Math.sin(angleS) * zMult, 2));
			float axisOne = (float) Math.sqrt(Math.pow(Math.cos(Math.PI / 2.0f) * xMult, 2)
					+ Math.pow(Math.sin(Math.PI / 2.0f) * yMult, 2)
					+ Math.pow(Math.sin(Math.PI / 2.0f) * zMult, 2));
			float F = (float) Math.sqrt(Math.pow(axisOne, 2) - Math.pow(axisTwo, 2));
			Vector3f focalLength;
			if (axisTwo > axisOne){
				focalLength = new Vector3f((float)Math.cos(angleS) * xMult, (float)Math.sin(angleS) * yMult, (float) Math.sin(angleS) * zMult);
				focalLength.normalize();
				focalLength.x *= F;
				focalLength.y *= F;
				focalLength.z *= F;
			} else if (axisTwo < axisOne){
				focalLength = new Vector3f((float)Math.cos(Math.PI/2.0f) * xMult, (float)Math.sin(Math.PI/2.0f) * yMult, (float) Math.sin(Math.PI/2.0f) * zMult);
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
			xloc = (float) Math.cos(angleS) * xMult + xTrans;
			yloc = (float) Math.sin(angleS) * yMult + yTrans;
			zloc = (float) Math.sin(angleS) * zMult + zTrans;
		} else {
			System.out.println("You chose incompatible values for xMult, yMult, or zMult");
		}

		double radius = (double) Math.sqrt(Math.pow(xloc, 2.0) + Math.pow(yloc, 2.0) + Math.pow(zloc, 2.0)) * 46371
				* 1000;

		// Setup materials for ambient light
		// sets the earth image as the texture of Earth and wraps it around the
		// sphere
		TextureLoader loader = new TextureLoader("C:\\Users\\Ryan\\Pictures\\Saved Pictures\\earth.jpg", "RGB",
				new Container());
		Texture texture = loader.getTexture();
		texture.setBoundaryModeS(Texture.WRAP);
		texture.setBoundaryModeT(Texture.WRAP);
		texture.setBoundaryColor(new Color4f(0.0f, 1.0f, 0.0f, 0.0f));

		TextureAttributes texAttr = new TextureAttributes();
		texAttr.setTextureMode(TextureAttributes.MODULATE);

		// Creates Color3f objects to assign the material to different objects.
		Color3f meshCA = new Color3f(.5f, .5f, .0f);
		Color3f meshCD = new Color3f(0.0f, 0.0f, 0.0f);
		Color3f meshCS = new Color3f(0.0f, 0.0f, 0.0f);
		Color3f meshCE = new Color3f(.0f, .5f, .5f);
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
		apE.setTexture(texture);
		apE.setTextureAttributes(texAttr);

		int primflags = Primitive.GENERATE_NORMALS + Primitive.GENERATE_TEXTURE_COORDS;

		BranchGroup objRoot = new BranchGroup();
		objTrans = new TransformGroup();
		central = new TransformGroup();
		objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		central.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		objRoot.addChild(objTrans);
		objRoot.addChild(central);

		// Create a simple shape leaf node. Add it to the scene graph.

		Cone sphere = new Cone(0.02f, 0.07f, 1000, apO);
		Sphere earth = new Sphere(.1374f, primflags, 100, apE);

		objTrans = new TransformGroup();
		central = new TransformGroup();
		objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		central.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

		Transform3D pos1 = new Transform3D();
		pos1.setTranslation(new Vector3f(xloc, yloc, zloc));
		Transform3D pos2 = new Transform3D();
		pos2.setTranslation(new Vector3f(0.0f, 0.0f, 0.0f));
		central.setTransform(pos2);
		objTrans.setTransform(pos1);
		central.addChild(earth);
		objTrans.addChild(sphere);
		objRoot.addChild(objTrans);
		objRoot.addChild(central);

		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
		// Color3f light1Color = new Color3f(1.0f, 0.0f, 0.2f);
		// Vector3f light1Direction = new Vector3f(4.0f, -7.0f, -12.0f);
		AmbientLight light1 = new AmbientLight(new Color3f(1.0f, 1.0f, 1.0f));
		light1.setInfluencingBounds(bounds);
		objRoot.addChild(light1);

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

		// Createa simple scene attach it to the virtual universe

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
			// Calculates the radius of the satellite.
			double radius = (double) Math.sqrt(Math.pow(xloc, 2.0) + Math.pow(yloc, 2.0) + Math.pow(zloc, 2.0)) * 46371
					* 1000;

			// v = sqrt(G*Me/r)
			double tangential_speedS = (double) (Math
					.sqrt(((6.67 * Math.pow(10.0, -11.0)) * (5.972 * Math.pow(10.0, 24.0))) / radius));

			// w = v / r
			double angular_speedS = tangential_speedS / radius;

			// calculate the radians traveled every hundredth of a second.
			double dist = angular_speedS * 0.01 * 10000;

			angleS += (float) dist;

			if (xMult == 0) {
				xloc = (float) Math.cos(angleS) * xMult + xTrans;
				yloc = (float) Math.cos(angleS) * yMult + yTrans;
				zloc = (float) Math.sin(angleS) * zMult + zTrans;
			} else if ((yMult == 0) ^ (zMult == 0) ^ ((yMult != 0) && (zMult != 0))) {
				xloc = (float) Math.cos(angleS) * xMult + xTrans;
				yloc = (float) Math.sin(angleS) * yMult + yTrans;
				zloc = (float) Math.sin(angleS) * zMult + zTrans;
			}

			angleE += (7.27 * Math.pow(10, -7)) * 10000;
			rotate.rotY(angleE);
			
			radius = (double) Math.sqrt(Math.pow(xloc, 2.0) + Math.pow(yloc, 2.0) + Math.pow(zloc, 2.0)) * 46371 * 1000;

			System.out.println(angleS);
			trans.setTranslation(new Vector3f(xloc, yloc, zloc));
			central.setTransform(rotate);
			objTrans.setTransform(trans);
		}
	}

	public static void main(String[] args) {
		System.out.println("Program started");
		EllipseTest bb = new EllipseTest();
		bb.addKeyListener(bb);
		MainFrame mf = new MainFrame(bb, 256, 256);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}
}