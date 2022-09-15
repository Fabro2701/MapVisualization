package model;

public class Matrix {
	float m[][];
	int columns, rows;
	public Matrix(int columns, int rows) {
		this.columns=columns;
		this.rows=rows;
		m = new float[rows][columns];
	}
	public void setElement(float e, int x, int y) {
		m[y][x]=e;
	}
	public Vector3D multiply(Vector3D l) {
		Vector3D r = new Vector3D();
		r.x = l.x * m[0][0] + l.y * m[1][0] + l.z * m[2][0] + m[3][0];
		r.y = l.x * m[0][1] + l.y * m[1][1] + l.z * m[2][1] + m[3][1];
		r.z = l.x * m[0][2] + l.y * m[1][2] + l.z * m[2][2] + m[3][2];
		float w = l.x * m[0][3] + l.y * m[1][3] + l.z * m[2][3] + m[3][3];

		if (w != 0.0f)
		{
			r.x /= w; r.y /= w; r.z /= w;
		}
		return r;
	}
	@Override
	public String toString() {
		return null;
	}
	public static void main(String args[]) {
	}
}
