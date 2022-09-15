package launcher;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import model.Matrix;
import model.Util;
import model.Vector3D;
import model.elements.Mesh;
import model.elements.Triangle;

public class Launcher extends JPanel{
	Mesh mesh;
	Matrix projectionMatrix;
	int width=1000,height=500;
	float zfar=1000f,znear=0.1f;
	float a=width/height,f=90f,q=zfar/(zfar-znear);
	float frad = (float) (1f/ Math.tan(f*0.5f/180f*Math.PI));
	float time = 0.0f;
	public Launcher() {
		Dimension size = new Dimension(width, height);
		this.setMinimumSize(size);
		this.setMaximumSize(size);
		this.setPreferredSize(size);
		this.init();
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				advance(0.0005f);
			}
		});
	}
	private void init() {
		mesh = new Mesh();
		mesh.setTriangles(Util.createSquare());
		
		projectionMatrix = Util.createProjectionMatrix(zfar, znear, a, f, q, frad);
		this.repaint();
	}
	@Override
	public void paintComponent(Graphics g) {
		
		//afx/z, fy/z, zq-znearq
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(Color.white);
		
		g2.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		g2.setColor(Color.black);
		for(Triangle t:mesh.getTriangles()) {
			
			Triangle tRotated1 = this.rotate(t, Util.createRotationZMatrix(this.time));
			Triangle tRotated2 = this.rotate(tRotated1, Util.createRotationXMatrix(this.time));
			
			Triangle tTranslated = this.translate(tRotated2);
			
			Triangle tPorjected = this.project(tTranslated);
			
			
			g2.drawPolygon(new int[] {(int) tPorjected.points[0].x,(int)tPorjected.points[1].x,(int)tPorjected.points[2].x}, new int[] {(int)tPorjected.points[0].y,(int)tPorjected.points[1].y,(int)tPorjected.points[2].y}, 3);
		
		}
	}
	private Triangle translate(Triangle t) {
		Triangle tTranslated = new Triangle(t);
		tTranslated.points[0].z +=3f;
		tTranslated.points[1].z +=3f;
		tTranslated.points[2].z +=3f;
		return tTranslated;
	}
	private Triangle project(Triangle t) {
		Triangle tPorjected = new Triangle(projectionMatrix.multiply(t.points[0]),
				   projectionMatrix.multiply(t.points[1]),
				   projectionMatrix.multiply(t.points[2]));

		tPorjected.points[0].x += 1f;tPorjected.points[0].y += 1f;
		tPorjected.points[1].x += 1f;tPorjected.points[1].y += 1f;		
		tPorjected.points[2].x += 1f;tPorjected.points[2].y += 1f;
		
		tPorjected.points[0].x *= 0.5f * this.getWidth();
		tPorjected.points[0].y *= 0.5f * this.getHeight();
		tPorjected.points[1].x *= 0.5f * this.getWidth();
		tPorjected.points[1].y *= 0.5f * this.getHeight();
		tPorjected.points[2].x *= 0.5f * this.getWidth();
		tPorjected.points[2].y *= 0.5f * this.getHeight();
		return tPorjected;
	}
	private Triangle rotate(Triangle t, Matrix rot) {
		Triangle tRotated = new Triangle(rot.multiply(t.points[0]),
										 rot.multiply(t.points[1]),
										 rot.multiply(t.points[2]));
	
		return tRotated;
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
