package model.elements;

import java.util.List;

public class Mesh {
	List<Triangle>triangles;

	public List<Triangle> getTriangles() {
		return triangles;
	}

	public void setTriangles(List<Triangle> triangles) {
		this.triangles = triangles;
	}
}
