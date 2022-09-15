package loading;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import model.Vector3D;
import model.elements.Triangle;

public class Triangulate {
	public static List<Triangle>convert(String filename){
		List<Triangle> tris = new ArrayList<Triangle>();
		BufferedImage img = null;
		
		try {
			img = ImageIO.read(new File("resources/" + filename + ".png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		img = Triangulate.reduceImage(20, img);

		for(int y=0;y<img.getHeight();y++) {
			for(int x=0;x<img.getWidth();x++) {
				if(x<img.getWidth()-1 && y<img.getHeight()-1) {
					//System.out.println(new Color(img.getRGB(x, y)).getGreen());
					Vector3D p0 = new Vector3D(x,y,new Color(img.getRGB(x, y)).getGreen()/255f);
					Vector3D p1 = new Vector3D(x+1,y,new Color(img.getRGB(x+1, y)).getGreen()/255f);
					Vector3D p2 = new Vector3D(x,y+1,new Color(img.getRGB(x, y+1)).getGreen()/255f);
					tris.add(new Triangle(p0,p1,p2));
					
					Vector3D p10 = new Vector3D(x+1,y,new Color(img.getRGB(x+1, y)).getGreen()/255f);
					Vector3D p11 = new Vector3D(x,y+1,new Color(img.getRGB(x, y+1)).getGreen()/255f);
					Vector3D p12 = new Vector3D(x+1,y+1,new Color(img.getRGB(x+1, y+1)).getGreen()/255f);
					tris.add(new Triangle(p10,p11,p12));
					System.out.print(p2.z+" ");
				}
			}System.out.println();
		}
		
		
		return tris;
	}
	private static BufferedImage reduceImage(int size, Image img) {
		Image imgS = img.getScaledInstance(size, size, Image.SCALE_SMOOTH);
		BufferedImage image2 = new BufferedImage(size,size,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D)image2.createGraphics();
		g2d.drawImage(imgS, 0, 0, size, size, null);
		return image2;
	}
	public static void main(String args[]) {
		Triangulate.convert("elevation");
	}
}
