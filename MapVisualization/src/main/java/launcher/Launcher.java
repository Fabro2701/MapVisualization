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
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import loading.Triangulate;
import loading.Triangulate;
import model.Matrix;
import model.Util;
import model.Vector3D;
import model.elements.Mesh;
import model.elements.Triangle;

public class Launcher extends JPanel{
	static Color colors[] = {Color.red, Color.blue, Color.green, Color.cyan, Color.yellow};
	Mesh mesh;
	Matrix projectionMatrix;
	int width=512,height=400;
	float zfar=1000f,znear=0.1f;
	float a=(float)width/(float)height,f=90f,q=zfar/(zfar-znear);
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
				Vector3D vForward = Vector3D.mul(Launcher.this.lookDir, -((float)e.getWheelRotation())*0.3f);
				Launcher.this.camera = Vector3D.add(Launcher.this.camera, vForward);
				repaint();
			}
		});
		
		
		KeyListener keyListener = new KeyListener() {

			@Override
			public void keyPressed(KeyEvent ke) {
				int c = ke.getKeyCode();
				Vector3D vForward = Vector3D.mul(Launcher.this.lookDir, 8f);
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
	    mesh.setTriangles(Triangulate.convert("elevation", 5f));
		//mesh.setTriangles(Util.loadFromObj("axis.obj"));
		
		//projectionMatrix = Util.createProjectionMatrix(zfar, znear, a, f, q, frad);
		projectionMatrix = Matrix.Matrix_MakeProjection(f, a, znear, zfar);
		
		camera = new Vector3D(0f,0f,0f);
		//camera = new Vector3D(0.39764875f,-0.26771787f,4.376751f);
		lookDir = new Vector3D(0f,0f,1f);
		//lookDir = new Vector3D(0.6082864f,0.7925648f,-0.04276373f);
		this.repaint();
	}
	/**
	 * look: (0.6082864,0.7925648,-0.04276373,1.0)
camera: (-0.40235126,-0.26771787,4.376751,1.0)
	 */
	@Override
	public void paintComponent(Graphics g) {
		System.out.println("look: "+lookDir);
		System.out.println("camera: "+camera);
		//afx/z, fy/z, zq-znearq
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(Color.black);
		
		g2.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		g2.setColor(Color.white);
		
		
		//System.out.println(time);
		//time=0;
		Matrix zrotation = Matrix.Matrix_MakeRotationZ(time);
		Matrix xrotation = Matrix.Matrix_MakeRotationX(time);
		
		Matrix matTrans = Matrix.translate(0f, 0f, 5f);
		
		Matrix matWorld = Matrix.identity();
		matWorld = zrotation.multiply(xrotation);
		matWorld = matWorld.multiply(matTrans);
		
		Vector3D vUp = new Vector3D(0f,1f,0f);
		//Vector3D vTarget = camera.add(lookDir);
		Vector3D vTarget = new Vector3D(0f,0f,1f);
		Matrix matCameraRot = Matrix.Matrix_MakeRotationY(fYaw);
		matCameraRot = matCameraRot.multiply(Matrix.Matrix_MakeRotationX(fXaw));
		lookDir = Vector3D.multiplyMatrix(vTarget, matCameraRot);
		//lookDir = new Vector3D(0.6082864f,0.7925648f,-0.04276373f);
		vTarget = Vector3D.add(camera, lookDir);
		
		Matrix matCamera = Matrix.Matrix_PointAt(camera, vTarget, vUp);
		Matrix matView = Matrix.Matrix_QuickInverse(matCamera);

		List<Triangle>trianglesToRaster = new ArrayList<Triangle>();
		
		int ti = 0;
		int cont=0,i=0;
		for(Triangle tri:mesh.getTriangles()) {
			
			

			//System.out.println(tri);
			//System.out.println(" --->");
			ti++;
			

			Triangle triTransformed = new Triangle(Vector3D.multiplyMatrix(tri.points[0], matWorld),
												   Vector3D.multiplyMatrix(tri.points[1], matWorld),
												   Vector3D.multiplyMatrix(tri.points[2], matWorld));
			
			triTransformed.col = tri.col;
			
			Vector3D l1 = Vector3D.sub(triTransformed.points[1], triTransformed.points[0]);
			Vector3D l2 = Vector3D.sub(triTransformed.points[2], triTransformed.points[0]);

			Vector3D normal = Vector3D.crossProduct(l1, l2);
			normal = Vector3D.normal(normal);
			
			Vector3D cameraRay = Vector3D.sub(triTransformed.points[0], camera);
			//if(normal.z>0) continue;
			
			

			if(Vector3D.dotProduct(normal, cameraRay) < 0f) {
				//System.out.println("i: "+ti);
				Triangle triViewed = new Triangle(Vector3D.multiplyMatrix(triTransformed.points[0], matView),
												  Vector3D.multiplyMatrix(triTransformed.points[1], matView),
												  Vector3D.multiplyMatrix(triTransformed.points[2], matView));
				triViewed.col = triTransformed.col;
				
				
				int nClippedTriangles = 0;
				Triangle clipped[] = new Triangle[] {new Triangle(),new Triangle()};
				nClippedTriangles = Triangle.Triangle_ClipAgainstPlane(new Vector3D(0.0f, 0.0f, 0.1f), 
																	   new Vector3D(0.0f, 0.0f, 1.0f), 
																	   triViewed, 
																	   clipped);
				//if(nClippedTriangles>1)System.out.println(nClippedTriangles);
				//System.out.println(nClippedTriangles);
				for (int n = 0; n < nClippedTriangles; n++){
					//System.out.println(clipped[n]);
					//project
					Triangle triProjected = new Triangle(Vector3D.multiplyMatrix(clipped[n].points[0], projectionMatrix),
														 Vector3D.multiplyMatrix(clipped[n].points[1], projectionMatrix),
														 Vector3D.multiplyMatrix(clipped[n].points[2], projectionMatrix));
					triProjected.col = clipped[n].col;
					
					float threshold = 1f;
					if(Math.abs(triProjected.points[0].w) <= threshold ||Math.abs(triProjected.points[1].w) <= threshold ||Math.abs(triProjected.points[2].w) <= threshold) {
						//continue;
					}
					//System.out.println(triProjected.points[0].w+ " "+triProjected.points[1].w+ " "+triProjected.points[2].w);
					triProjected.points[0] = Vector3D.div(triProjected.points[0], triProjected.points[0].w);
					triProjected.points[1] = Vector3D.div(triProjected.points[1], triProjected.points[1].w);
					triProjected.points[2] = Vector3D.div(triProjected.points[2], triProjected.points[2].w);
					
					triProjected.points[0].x *= -1.0f;
					triProjected.points[1].x *= -1.0f;
					triProjected.points[2].x *= -1.0f;
					triProjected.points[0].y *= -1.0f;
					triProjected.points[1].y *= -1.0f;
					triProjected.points[2].y *= -1.0f;
					
					Vector3D offsetView = new Vector3D(1f,1f,0f);
					triProjected.points[0] = Vector3D.add(triProjected.points[0], offsetView);
					triProjected.points[1] = Vector3D.add(triProjected.points[1], offsetView);
					triProjected.points[2] = Vector3D.add(triProjected.points[2], offsetView);
					
					triProjected.points[0].x *= 0.5f * (float)this.getWidth();
					triProjected.points[0].y *= 0.5f * (float)this.getHeight();
					triProjected.points[1].x *= 0.5f * (float)this.getWidth();
					triProjected.points[1].y *= 0.5f * (float)this.getHeight();
					triProjected.points[2].x *= 0.5f * (float)this.getWidth();
					triProjected.points[2].y *= 0.5f * (float)this.getHeight();
					
					
					trianglesToRaster.add(triProjected);
					Triangle t = triProjected;
					//if(i<colors.length)g2.setColor(this.colors[i]);
					//else continue;
					//System.out.println(t);
//					g2.setColor(Color.white);
//					g2.fillPolygon(new int[] {(int) t.points[0].x,(int)t.points[1].x,(int)t.points[2].x}, new int[] {(int)t.points[0].y,(int)t.points[1].y,(int)t.points[2].y}, 3);
//					g2.setColor(Color.black);
//					g2.drawPolygon(new int[] {(int) t.points[0].x,(int)t.points[1].x,(int)t.points[2].x}, new int[] {(int)t.points[0].y,(int)t.points[1].y,(int)t.points[2].y}, 3);
//					i++;
				}
			}
		}
		//if(true)return;
		Collections.sort(trianglesToRaster, new Comparator<Triangle>(){
			@Override
			public int compare(Triangle t1, Triangle t2) {
				float z1 = (t1.points[0].z + t1.points[1].z + t1.points[2].z) / 3.0f;
				float z2 = (t2.points[0].z + t2.points[1].z + t2.points[2].z) / 3.0f;
				return z1<z2?1:z1>z2?-1:0;
			}
		});
		for (Triangle triToRaster : trianglesToRaster)
		{
			
			// Clip triangles against all four screen edges, this could yield
			// a bunch of triangles, so create a queue that we traverse to 
			//  ensure we only test new triangles generated against planes
			Triangle clipped[] = new Triangle[] {new Triangle(),new Triangle()};
			Queue<Triangle> listTriangles = new LinkedList<Triangle>();
			
			// Add initial triangle
			listTriangles.add(triToRaster);
			int nNewTriangles = 1;

			for (int p = 0; p < 4; p++)
			{
				int nTrisToAdd = 0;
				while (nNewTriangles > 0)
				{
					// Take triangle from front of queue
					Triangle test = listTriangles.poll();
					nNewTriangles--;
					// Clip it against a plane. We only need to test each 
					// subsequent plane, against subsequent new triangles
					// as all triangles after a plane clip are guaranteed
					// to lie on the inside of the plane. I like how this
					// comment is almost completely and utterly justified
					switch (p)
					{
					case 0:	nTrisToAdd = Triangle.Triangle_ClipAgainstPlane(new Vector3D(0.0f, 0.0f, 0.0f), new Vector3D(0.0f, 1.0f, 0.0f), test, clipped); break;
					case 1:	nTrisToAdd = Triangle.Triangle_ClipAgainstPlane(new Vector3D(0.0f, (float)this.getHeight() - 1, 0.0f), new Vector3D(0.0f, -1.0f, 0.0f), test, clipped); break;
					case 2:	nTrisToAdd = Triangle.Triangle_ClipAgainstPlane(new Vector3D(0.0f, 0.0f, 0.0f), new Vector3D(1.0f, 0.0f, 0.0f), test, clipped); break;
					case 3:	nTrisToAdd = Triangle.Triangle_ClipAgainstPlane(new Vector3D((float)this.getWidth() - 1, 0.0f, 0.0f), new Vector3D(-1.0f, 0.0f, 0.0f), test, clipped); break;
					}

					// Clipping may yield a variable number of triangles, so
					// add these new ones to the back of the queue for subsequent
					// clipping against next planes
					for (int w = 0; w < nTrisToAdd; w++) {
						listTriangles.add(clipped[w]);
					}
				}
				nNewTriangles = listTriangles.size();
			}
			for(Triangle t:listTriangles) {
				//System.out.println(triToRaster);
				cont++;
				//System.out.println("painting: "+t);
				//System.out.println(t.col);
				g2.setColor(t.col);
				g2.fillPolygon(new int[] {(int) t.points[0].x,(int)t.points[1].x,(int)t.points[2].x}, new int[] {(int)t.points[0].y,(int)t.points[1].y,(int)t.points[2].y}, 3);
				g2.setColor(Color.white);
				g2.drawPolygon(new int[] {(int) t.points[0].x,(int)t.points[1].x,(int)t.points[2].x}, new int[] {(int)t.points[0].y,(int)t.points[1].y,(int)t.points[2].y}, 3);

			}
		}
		//System.out.println(cont);
		
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
