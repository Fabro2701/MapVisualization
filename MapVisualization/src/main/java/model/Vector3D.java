package model;

public class Vector3D implements Cloneable{
	public float x,y,z,w=1f;
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
	public Vector3D crossProduct(Vector3D r) {
		return new Vector3D(this.y*r.z-this.z*r.y, this.z*r.x-this.x*r.z, this.x*r.y-this.y*r.x);
	}
	public float dotProduct(Vector3D r) {
		return this.x*r.x + this.y*r.y + this.z*r.z;
	}
	public float length() {
		return (float) Math.sqrt(this.dotProduct(this));
	}
	public Vector3D normal() {
		float aux = this.length();
		return new Vector3D(x/aux,y/aux,z/aux);
	}
	public Vector3D add(Vector3D r) {
		return new Vector3D(x+r.x,y+r.y,z+r.z);
	}
	public Vector3D sub(Vector3D r) {
		return new Vector3D(x-r.x,y-r.y,z-r.z);
	}
	public Vector3D mul(Vector3D r) {
		return new Vector3D(x*r.x,y*r.y,z*r.z);
	}
	public Vector3D div(float r) {
		return new Vector3D(x/r,y/r,z/r);
	}
	public Vector3D mul(float r) {
		return new Vector3D(x*r,y*r,z*r);
	}
	public Vector3D sub(float r) {
		return new Vector3D(x-r,y-r,z-r);
	}
	public Vector3D add(float r) {
		return new Vector3D(x+r,y+r,z+r);
	}
	public Vector3D multiplyMatrix(Matrix m) {
//		System.out.println("mult: ");
//		System.out.println("a1: ");
//		System.out.println(this);
//		System.out.println("a2: ");
//		System.out.println(m);
		Vector3D v = new Vector3D();

		v.x = this.x * m.m[0][0] + this.y * m.m[1][0] + this.z * m.m[2][0] + this.w * m.m[3][0];
		v.y = this.x * m.m[0][1] + this.y * m.m[1][1] + this.z * m.m[2][1] + this.w * m.m[3][1];
		v.z = this.x * m.m[0][2] + this.y * m.m[1][2] + this.z * m.m[2][2] + this.w * m.m[3][2];
		v.w = this.x * m.m[0][3] + this.y * m.m[1][3] + this.z * m.m[2][3] + this.w * m.m[3][3];
		return v;
	}
	@Override
	public String toString() {
		return "("+x+","+y+","+z+")";
	}
}
