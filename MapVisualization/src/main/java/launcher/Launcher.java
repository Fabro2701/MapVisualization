package launcher;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import loading.Triangulate;
import model.Matrix;
import model.Util;
import model.Vector3D;
import model.elements.Mesh;
import model.elements.Triangle;

public class Launcher extends JPanel{
	Mesh mesh;
	Matrix projectionMatrix;
	int width=700,height=700;
	float zfar=1000f,znear=0.1f;
	float a=width/height,f=90f,q=zfar/(zfar-znear);
	float frad = (float) (1f/ (float)Math.tan(f*0.5f/180f*3.14159f));
	float offset = 8f;
	float time = 0.0f;
	Vector3D camera;
	Vector3D lookDir;
	float fYaw,fXaw;
	public Launcher() {
		Dimension size = new Dimension(width, height);
		this.setMinimumSize(size);
		this.setMaximumSize(size);
		this.setPreferredSize(size);
		this.init();
		MouseAdapter mouseA = new MouseAdapter() {
			
			boolean pressed = false;
    		Point current = null;
    		@Override
			public void mousePressed(MouseEvent e) {
    			//advance(0.0005f);
    			if(!pressed) {
    				pressed = true;
        			current = e.getPoint();
    			}
    			//
			}
    		@Override
			public void mouseReleased(MouseEvent e) {
    			pressed = false;
    			
			}
			@Override
			public void mouseDragged(MouseEvent e) {
				if(pressed) {
					Point dir = e.getPoint();
					if(dir.equals(current))return;
					int dx = dir.x-current.x;
					int dy = dir.y-current.y;
					
					float decreaseFactor = 10000.0f;
					if(Math.abs(dx) >10)Launcher.this.fYaw +=(float)dx/decreaseFactor;
					if(Math.abs(dy) >10)Launcher.this.fXaw +=(float)dy/decreaseFactor;
							
					repaint();
				}
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				System.out.println("clicked");
			}
		};
		this.addMouseListener(mouseA);
		this.addMouseMotionListener(mouseA);;
		this.addMouseWheelListener(new MouseWheelListener () {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				Vector3D vForward = Launcher.this.lookDir.mul(-((float)e.getWheelRotation())*3f);
				Launcher.this.camera = Launcher.this.camera.add(vForward);
				repaint();
			}
		});
		
		
		KeyListener keyListener = new KeyListener() {

			@Override
			public void keyPressed(KeyEvent ke) {
				int c = ke.getKeyCode();
				Vector3D vForward = Launcher.this.lookDir.mul(8f);
				switch(c) {
				  case KeyEvent.VK_W:
					  Launcher.this.camera.y-=0.8f;
				    break;
				  case KeyEvent.VK_S:
					  Launcher.this.camera.y+=0.8f;
				    break;
				  case KeyEvent.VK_A:
					  Launcher.this.camera.x-=0.8f;
				    break;
				  case KeyEvent.VK_D:
					  Launcher.this.camera.x+=0.8f;
				    break;
				  case KeyEvent.VK_Z:
					  Launcher.this.camera.z-=0.8f;
				    break;
				  case KeyEvent.VK_X:
					  Launcher.this.camera.z+=0.8f;
				    break;

				  default:				    
				}
				repaint();
			}

			@Override
			public void keyReleased(KeyEvent ke) {
				
			}

			@Override
			public void keyTyped(KeyEvent ke) {
			}
			
		};
		this.addKeyListener(keyListener);
		this.setFocusable(true);
        this.requestFocusInWindow();
	}
	private void init() {
		mesh = new Mesh();
		
		//mesh.setTriangles(Util.createSquare());
	    mesh.setTriangles(Triangulate.convert("elevation", 2f));
		//mesh.setTriangles(Util.loadFromObj("axis.obj"));
		
		//projectionMatrix = Util.createProjectionMatrix(zfar, znear, a, f, q, frad);
		projectionMatrix = Matrix.Matrix_MakeProjection(f, a, znear, zfar);
		
		camera = new Vector3D(0f,0f,0f);
		lookDir = new Vector3D(0f,0f,1f);
		this.repaint();
	}
	@Override
	public void paintComponent(Graphics g) {
		//afx/z, fy/z, zq-znearq
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(Color.black);
		
		g2.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		g2.setColor(Color.white);
		
		List<Triangle>trianglesToRaster = new ArrayList<Triangle>();
		
		//System.out.println(time);
		time=0;
		Matrix zrotation = Matrix.Matrix_MakeRotationZ(time);
		Matrix xrotation = Matrix.Matrix_MakeRotationX(time);
		
		Matrix matTrans = Matrix.translate(0f, 0f, 15f);
		
		Matrix matWorld = Matrix.identity();
		matWorld = zrotation.multiply(xrotation);
		matWorld = matWorld.multiply(matTrans);
		
		Vector3D vUp = new Vector3D(0f,1f,0f);
		//Vector3D vTarget = camera.add(lookDir);
		Vector3D vTarget = new Vector3D(0f,0f,1f);
		Matrix matCameraRot = Matrix.Matrix_MakeRotationY(fYaw);
		matCameraRot = matCameraRot.multiply(Matrix.Matrix_MakeRotationX(fXaw));
		lookDir = vTarget.multiplyMatrix(matCameraRot);
		vTarget = camera.add(lookDir);
		
		Matrix matCamera = Matrix.Matrix_PointAt(camera, vTarget, vUp);
		Matrix matView = Matrix.Matrix_QuickInverse(matCamera);
		
		for(Triangle tri:mesh.getTriangles()) {

			Triangle triTransformed = new Triangle(tri.points[0].multiplyMatrix(matWorld),
												   tri.points[1].multiplyMatrix(matWorld),
												   tri.points[2].multiplyMatrix(matWorld));
			

			
			Vector3D l1 = triTransformed.points[1].sub(triTransformed.points[0]);
			Vector3D l2 = triTransformed.points[2].sub(triTransformed.points[0]);

			Vector3D normal = l1.crossProduct(l2).normal();
			
			Vector3D cameraRay = triTransformed.points[0].sub(camera);
			//if(normal.z>0) continue;
			
			if(!(normal.dotProduct(cameraRay) < 0f))continue;
			
			Triangle triViewed = new Triangle(triTransformed.points[0].multiplyMatrix(matView),
					 triTransformed.points[1].multiplyMatrix(matView),
					 triTransformed.points[2].multiplyMatrix(matView));

			
			Triangle triProjected = new Triangle(triViewed.points[0].multiplyMatrix(projectionMatrix),
					triViewed.points[1].multiplyMatrix(projectionMatrix),
					triViewed.points[2].multiplyMatrix(projectionMatrix));
			
			triProjected.points[0] = triProjected.points[0].div(triProjected.points[0].w);
			triProjected.points[1] = triProjected.points[1].div(triProjected.points[1].w);
			triProjected.points[2] = triProjected.points[2].div(triProjected.points[2].w);

			
			Vector3D offsetView = new Vector3D(1f,1f,0f);
			triProjected.points[0] = triProjected.points[0].add(offsetView);
			triProjected.points[1] = triProjected.points[1].add(offsetView);
			triProjected.points[2] = triProjected.points[2].add(offsetView);
			
			triProjected.points[0].x *= 0.5f * (float)this.getWidth();
			triProjected.points[0].y *= 0.5f * (float)this.getHeight();
			triProjected.points[1].x *= 0.5f * (float)this.getWidth();
			triProjected.points[1].y *= 0.5f * (float)this.getHeight();
			triProjected.points[2].x *= 0.5f * (float)this.getWidth();
			triProjected.points[2].y *= 0.5f * (float)this.getHeight();
			
			Triangle t = triProjected;
			g2.drawPolygon(new int[] {(int) t.points[0].x,(int)t.points[1].x,(int)t.points[2].x}, new int[] {(int)t.points[0].y,(int)t.points[1].y,(int)t.points[2].y}, 3);

			trianglesToRaster.add(triProjected);
			
		}
//		Collections.sort(trianglesToRaster, new Comparator<Triangle>(){
//			@Override
//			public int compare(Triangle t1, Triangle t2) {
//				float z1 = (t1.points[0].z + t1.points[1].z + t1.points[2].z) / 3.0f;
//				float z2 = (t2.points[0].z + t2.points[1].z + t2.points[2].z) / 3.0f;
//				return z1<z2?-1:z1>z2?1:0;
//			}
//		});
		for(Triangle t:trianglesToRaster) {
			//g2.drawPolygon(new int[] {(int) t.points[0].x,(int)t.points[1].x,(int)t.points[2].x}, new int[] {(int)t.points[0].y,(int)t.points[1].y,(int)t.points[2].y}, 3);
			//g2.fillPolygon(new int[] {(int) tPorjected.points[0].x,(int)tPorjected.points[1].x,(int)tPorjected.points[2].x}, new int[] {(int)tPorjected.points[0].y,(int)tPorjected.points[1].y,(int)tPorjected.points[2].y}, 3);
		}
		
	}
	public void advance(float t) {
		
		this.time += t;
		this.repaint();
		SwingUtilities.invokeLater(()->{
			advance(t);
		});
	}
	public static void main(String args[]) {
		
		SwingUtilities.invokeLater(()->{JFrame frame = new JFrame();
		Dimension size = new Dimension(500, 500);
		//frame.setMinimumSize(size);
		//frame.setMaximumSize(new Dimension(1000, 1000));
		//frame.setPreferredSize(size);
		frame.setContentPane(new Launcher());
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocation(800, 100);
		frame.setVisible(true);});
	}
}
