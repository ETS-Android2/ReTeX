package org.scilab.forge.jlatexmath.graphics;

import org.scilab.forge.jlatexmath.font.FontW;
import org.scilab.forge.jlatexmath.platform.font.Font;
import org.scilab.forge.jlatexmath.platform.font.FontRenderContext;
import org.scilab.forge.jlatexmath.platform.geom.Line2D;
import org.scilab.forge.jlatexmath.platform.geom.Rectangle2D;
import org.scilab.forge.jlatexmath.platform.geom.RoundRectangle2D;
import org.scilab.forge.jlatexmath.platform.graphics.Color;
import org.scilab.forge.jlatexmath.platform.graphics.Graphics2DInterface;
import org.scilab.forge.jlatexmath.platform.graphics.Image;
import org.scilab.forge.jlatexmath.platform.graphics.Stroke;
import org.scilab.forge.jlatexmath.platform.graphics.Transform;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.CanvasElement;

public class Graphics2DW implements Graphics2DInterface {

	private Canvas canvas;
	private Context2d context;

	private BasicStrokeW basicStroke;
	private ColorW color;
	private FontW font;

	private TransformW transform;
	private TransformW savedTransform;

	public Graphics2DW(Canvas canvas) {
		this.canvas = canvas;
		context = canvas.getContext2d();
		initBasicStroke();
		initColor();
		initTransform();
		initFont();
	}

	private void initBasicStroke() {
		basicStroke = new BasicStrokeW((float) context.getLineWidth(),
				context.getLineCap(), context.getLineJoin(),
				(float) context.getMiterLimit());
	}

	private void initColor() {
		color = new ColorW(0, 0, 0);
		context.setStrokeStyle(color.getCssColor());
	}

	private void initTransform() {
		transform = new TransformW();
		savedTransform = transform.createClone();
	}

	private void initFont() {
		font = new FontW(context.getFont(), Font.PLAIN, 12);
	}

	@Override
	public void setStroke(Stroke stroke) {
		basicStroke = (BasicStrokeW) stroke;
		context.setLineCap(basicStroke.getJSLineCap());
		context.setLineJoin(basicStroke.getJSLineJoin());
		context.setLineWidth(basicStroke.getWidth());
		context.setMiterLimit(basicStroke.getMiterLimit());
	}

	@Override
	public Stroke getStroke() {
		return basicStroke;
	}

	@Override
	public void setColor(Color color) {
		this.color = (ColorW) color;
		context.setStrokeStyle(this.color.getCssColor());
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public Transform getTransform() {
		return transform.createClone();
	}

	@Override
	public void saveTransformation() {
		context.save();
		savedTransform = transform.createClone();
	}

	@Override
	public void restoreTransformation() {
		context.restore();
		transform = savedTransform.createClone();
	}

	@Override
	public Font getFont() {
		return font;
	}

	@Override
	public void setFont(Font font) {
		this.font = (FontW) font;
		context.setFont(this.font.getCssFontString());
	}

	@Override
	public void fillRect(int x, int y, int width, int height) {
		context.fillRect(x, y, width, height);
	}

	@Override
	public void fill(Rectangle2D rectangle) {
		context.fillRect(rectangle.getX(), rectangle.getY(),
				rectangle.getWidth(), rectangle.getHeight());
	}

	@Override
	public void draw(Rectangle2D rectangle) {
		context.rect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(),
				rectangle.getHeight());
	}

	@Override
	public void draw(RoundRectangle2D rectangle) {
		double x = rectangle.getX();
		double y = rectangle.getY();
		double w = rectangle.getWidth();
		double h = rectangle.getHeight();
		double arcW = rectangle.getArcW();
		double arcH = rectangle.getArcH();
		if (Math.abs(arcW - arcH) < 0.01) {
			// TODO draw using the normal API
		} else {
			// TODO source http://www.spaceroots.org/documents/ellipse/
			throw new UnsupportedOperationException(
					"ArcW and ArcH must be equal.");
		}
	}

	@Override
	public void draw(Line2D line) {
		context.moveTo(line.getX1(), line.getY1());
		context.lineTo(line.getX2(), line.getY2());
	}

	@Override
	public void drawChars(char[] data, int offset, int length, int x, int y) {
		String string = String.valueOf(data, offset, length);
		context.fillText(string, x, y);
	}

	@Override
	public void drawArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {
		doArcPath(x, y, width, height, startAngle, arcAngle);
		context.stroke();
	}

	@Override
	public void fillArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {
		doArcPath(x, y, width, height, startAngle, arcAngle);
		context.fill();
	}

	private void doArcPath(int x, int y, int width, int height, int startAngle,
			int arcAngle) {
		context.save();
		context.beginPath();

		context.translate(x, y);
		context.scale(width, height);
		context.arc(1, 1, 1, startAngle, arcAngle);
		context.restore();
	}

	@Override
	public void translate(double x, double y) {
		context.translate(x, y);
		transform.translate(x, y);
	}

	@Override
	public void scale(double x, double y) {
		context.scale(x, y);
		transform.scale(x, y);
	}

	@Override
	public void rotate(double theta, double x, double y) {
		translate(x, y);
		rotate(theta);
		translate(-x, -y);
	}

	@Override
	public void rotate(double theta) {
		context.rotate(theta);
		transform.rotate(theta);
	}

	@Override
	public void drawImage(Image image, int x, int y) {
		ImageW impl = (ImageW) image;
		Canvas imageCanvas = impl.getCanvas();
		CanvasElement canvasElement = imageCanvas.getCanvasElement();
		context.drawImage(canvasElement, x, y);
	}

	@Override
	public void drawImage(Image image, Transform transform) {
		context.save();

		context.transform(transform.getScaleX(), transform.getShearX(),
				transform.getShearY(), transform.getScaleY(),
				transform.getTranslateX(), transform.getTranslateY());
		drawImage(image, 0, 0);
		
		context.restore();
	}

	@Override
	public FontRenderContext getFontRenderContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setRenderingHint(int key, int value) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getRenderingHint(int key) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void dispose() {
		// NO-OP
	}

}
