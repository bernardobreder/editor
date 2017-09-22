package project.resource;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.WeakHashMap;

import javax.imageio.ImageIO;

public abstract class Resource {
	
	private static final WeakHashMap<String, BufferedImage> IMAGES = new WeakHashMap<String, BufferedImage>();
	
	public static BufferedImage getProject(int width, int height) {
		return image("project.png", width, height);
	}
	
	public static BufferedImage getFileAdd(int width, int height) {
		return image("file_add.png", width, height);
	}
	
	public static BufferedImage getPlay(int width, int height) {
		return image("play.png", width, height);
	}
	
	public static BufferedImage getPause(int width, int height) {
		return image("pause.png", width, height);
	}
	
	public static BufferedImage getStop(int width, int height) {
		return image("stop.png", width, height);
	}
	
	public static BufferedImage getSave(int width, int height) {
		return image("save.png", width, height);
	}

	public static BufferedImage getFolder(int width, int height) {
		return image("folder.png", width, height);
	}
	
	public static BufferedImage getFile(int width, int height) {
		return image("file.png", width, height);
	}

	public static BufferedImage getScript(int width, int height) {
		return image("script.png", width, height);
	}
	
	private static BufferedImage image(String name, int width, int height) {
		String key = name + "@width=" + width + ",height=" + height;
		BufferedImage image = IMAGES.get(key);
		if (image != null) {
			return image;
		}
		try {
			image = ImageIO.read(Resource.class.getResourceAsStream("/project/" + name));
			if (image.getWidth() != width || image.getHeight() != height) {
				BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g = resizedImage.createGraphics();
				g.setComposite(AlphaComposite.Src);
				g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.drawImage(image, 0, 0, width, height, null);
				g.dispose();
				image = resizedImage;
			}
			return image;
		} catch (Exception e) {
			return new BufferedImage(width, width, BufferedImage.TYPE_INT_ARGB);
		} finally {
			IMAGES.put(key, image);
		}
	}
	
}
