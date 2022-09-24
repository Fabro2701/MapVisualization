package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import model.elements.Triangle;

public class Util {
	public static float cosf(float x) {
		return (float) Math.cos(x);
	}
	public static float sinf(float x) {
		return (float) Math.sin(x);
	}
	public static float tanf(float x) {
		return (float) Math.tan(x);
	}
	public  static List<Triangle> loadFromObj(String filename) {
		List<Triangle> tris = new ArrayList<Triangle>();
		List<Vector3D> verts = new ArrayList<Vector3D>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader("resources/"+filename));
			String line = reader.readLine();
			while(line!=null) {
				String info[] = line.split(" ");
				if(info[0].equals("v")) {
					verts.add(new Vector3D(Float.parseFloat(info[1]),Float.parseFloat(info[2]),Float.parseFloat(info[3])));
				}
				if(info[0].equals("f")) {
					tris.add(new Triangle(verts.get(Integer.parseInt(info[1])-1),verts.get(Integer.parseInt(info[2])-1),verts.get(Integer.parseInt(info[3])-1)));
				}
				line = reader.readLine();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		System.out.println(filename+ " loaded");
		return tris;
	}
	public static List<Triangle> createSquare() {
		List<Triangle> arr = new ArrayList<Triangle>();

		// SOUTH
		arr.add(new Triangle(new Vector3D(0.0f, 0.0f, 0.0f),    new Vector3D(0.0f, 1.0f, 0.0f),    new Vector3D(1.0f, 1.0f, 0.0f)));                    
		arr.add(new Triangle(new Vector3D(0.0f, 0.0f, 0.0f),    new Vector3D(1.0f, 1.0f, 0.0f),    new Vector3D(1.0f, 0.0f, 0.0f)));

		// EAST                                                      
		arr.add(new Triangle(new Vector3D(1.0f, 0.0f, 0.0f),    new Vector3D(1.0f, 1.0f, 0.0f),    new Vector3D(1.0f, 1.0f, 1.0f)));
		arr.add(new Triangle(new Vector3D(1.0f, 0.0f, 0.0f),    new Vector3D(1.0f, 1.0f, 1.0f),    new Vector3D(1.0f, 0.0f, 1.0f)));
		//NORTH                                                     
		arr.add(new Triangle(new Vector3D(1.0f, 0.0f, 1.0f),    new Vector3D(1.0f, 1.0f, 1.0f),    new Vector3D(0.0f, 1.0f, 1.0f)));
		arr.add(new Triangle(new Vector3D(1.0f, 0.0f, 1.0f),    new Vector3D(0.0f, 1.0f, 1.0f),    new Vector3D(0.0f, 0.0f, 1.0f)));
       
		//WEST                                                      
		arr.add(new Triangle(new Vector3D(0.0f, 0.0f, 1.0f),    new Vector3D(0.0f, 1.0f, 1.0f),    new Vector3D(0.0f, 1.0f, 0.0f)));
		arr.add(new Triangle(new Vector3D(0.0f, 0.0f, 1.0f),    new Vector3D(0.0f, 1.0f, 0.0f),    new Vector3D(0.0f, 0.0f, 0.0f)));
        
		//TOP                                                       
		arr.add(new Triangle(new Vector3D(0.0f, 1.0f, 0.0f),    new Vector3D(0.0f, 1.0f, 1.0f),    new Vector3D(1.0f, 1.0f, 1.0f)));
		arr.add(new Triangle(new Vector3D(0.0f, 1.0f, 0.0f),    new Vector3D(1.0f, 1.0f, 1.0f),    new Vector3D(1.0f, 1.0f, 0.0f)));
        
		//BOTTOM                                                    
		arr.add(new Triangle(new Vector3D(1.0f, 0.0f, 1.0f),    new Vector3D(0.0f, 0.0f, 1.0f),    new Vector3D(0.0f, 0.0f, 0.0f)));
		arr.add(new Triangle(new Vector3D(1.0f, 0.0f, 1.0f),    new Vector3D(0.0f, 0.0f, 0.0f),    new Vector3D(1.0f, 0.0f, 0.0f)));
		
		return arr;
	}
	public static Matrix createProjectionMatrix(float zfar, float znear, float a, float f, float q, float frad){
		Matrix m = new Matrix(4,4);
		
		m.setElement(a*frad, 0, 0);
		m.setElement(0, 1, 0);
		m.setElement(0, 2, 0);
		m.setElement(0, 3, 0);
		
		m.setElement(0, 0, 1);
		m.setElement(frad, 1, 1);
		m.setElement(0, 2, 1);
		m.setElement(0, 3, 1);

		m.setElement(0, 0, 2);
		m.setElement(0, 1, 2);
		m.setElement(q, 2, 2);
		m.setElement(1f, 3, 2);
		
		m.setElement(0, 0, 3);
		m.setElement(0, 1, 3);
		m.setElement(-znear*q, 2, 3);
		m.setElement(0, 3, 3);
		return m;
	}
	public static Matrix createRotationZMatrix(float fTheta){
		Matrix m = new Matrix(4,4);
		
		m.setElement((float) Math.cos(fTheta), 0, 0);
		m.setElement((float) Math.sin(fTheta), 1, 0);
		m.setElement(0, 2, 0);
		m.setElement(0, 3, 0);
		
		m.setElement((float) -Math.sin(fTheta), 0, 1);
		m.setElement((float) Math.cos(fTheta), 1, 1);
		m.setElement(0, 2, 1);
		m.setElement(0, 3, 1);

		m.setElement(0, 0, 2);
		m.setElement(0, 1, 2);
		m.setElement(1f, 2, 2);
		m.setElement(0, 3, 2);
		
		m.setElement(0, 0, 3);
		m.setElement(0, 1, 3);
		m.setElement(0, 2, 3);
		m.setElement(1f, 3, 3);
		return m;
	}
	public static Matrix createRotationXMatrix(float fTheta){
		Matrix m = new Matrix(4,4);
		
		m.setElement(1f, 0, 0);
		m.setElement(0, 1, 0);
		m.setElement(0, 2, 0);
		m.setElement(0, 3, 0);
		
		m.setElement(0, 0, 1);
		m.setElement((float) Math.cos(fTheta*0.5f), 1, 1);
		m.setElement((float) Math.sin(fTheta*0.5f), 2, 1);
		m.setElement(0, 3, 1);

		m.setElement(0, 0, 2);
		m.setElement((float) -Math.sin(fTheta*0.5f), 1, 2);
		m.setElement((float) Math.cos(fTheta*0.5f), 2, 2);
		m.setElement(0, 3, 2);
		
		m.setElement(0, 0, 3);
		m.setElement(0, 1, 3);
		m.setElement(0, 2, 3);
		m.setElement(1f, 3, 3);
		return m;
	}
}
