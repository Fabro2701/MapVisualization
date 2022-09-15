package model;

public class Vector3D implements Cloneable{
	public float x,y,z;
	public Vector3D() {
		
	}
	public Vector3D(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector3D(Vector3D copy) {
		x=copy.x;
		y=copy.y;
		z=copy.z;
	}
}
