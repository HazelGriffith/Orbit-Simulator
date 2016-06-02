
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

public class LEDCube3d {

  public static void main(final String[] args) {
    new LEDCube3d();
  }

  public LEDCube3d() {
    final TransformGroup group = new TransformGroup();
    { //to enable rotation
      group.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
      group.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    }
    final SimpleUniverse universe = new SimpleUniverse();

    //
    for (float x = 0, xco = 0; x <= .5f; x = x + 0.500f, ++xco) {
      for (float y = 0, yco = 0; y < .1f; y = y + 0.3f, ++yco) {
        for (float z = 0f, zco = 0; z < 0.1; z = z + 0.4f, ++zco) {
          
          final Sphere sphere = new Sphere(0.15f);
          final TransformGroup tg = new TransformGroup();
          final Transform3D transform = new Transform3D();
          final Vector3f vector = new Vector3f(x, y, z);

          transform.setTranslation(vector);
          tg.setTransform(transform);
          tg.addChild(sphere);
          group.addChild(tg);
        }
      }
    }
    // 
    final Transform3D transform = new Transform3D();
    transform.rotX(1);

    final long milis = 5000;
    final Alpha alpha = new Alpha(-1, Alpha.INCREASING_ENABLE, 0, 0, milis, 0, 0, 0, 0, 0);
    final RotationInterpolator ri = new RotationInterpolator(alpha, group, transform, 0.0f, (float) Math.PI * 2.0f);
    ri.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
    group.addChild(ri);
    final BranchGroup bg = new BranchGroup();
    bg.addChild(group);
    //

    final Color3f light1Color = new Color3f(.1f, 1.4f, 1.8f);
    final BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
    final Vector3f light1Direction = new Vector3f(4.0f, -7.0f, -12.0f);
    final DirectionalLight light1 = new DirectionalLight(light1Color, light1Direction);
    light1.setInfluencingBounds(bounds);
    bg.addChild(light1);
    universe.getViewingPlatform().setNominalViewingTransform();
    universe.addBranchGraph(bg);
    System.out.println("constructor");
  
  }
  
}
