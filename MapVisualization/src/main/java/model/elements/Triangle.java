package model.elements;

import model.Vector3D;

public class Triangle {
	public Vector3D points[];
	public Triangle(Vector3D p1, Vector3D p2, Vector3D p3) {
		points = new Vector3D[]{p1,p2,p3};
	}
	public Triangle(Triangle copy) {
		points = new Vector3D[3];
		points[0] = new Vector3D(copy.points[0]);
		points[1] = new Vector3D(copy.points[1]);
		points[2] = new Vector3D(copy.points[2]);
	}
}
