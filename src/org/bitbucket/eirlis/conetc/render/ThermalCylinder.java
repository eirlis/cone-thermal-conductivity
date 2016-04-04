package org.bitbucket.eirlis.conetc.render;

import controlP5.Textlabel;
import org.bitbucket.eirlis.conetc.core.TwoDimensionProblemSolver;
import processing.core.PApplet;
import processing.core.PImage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by Elena on 04.04.2016.
 */
public class ThermalCylinder {
    private static final int SIDE_TEXTURE_WIDTH = 400;
    private static final int SIDE_TEXTURE_HEIGHT = 400;
    private PImage sideTexture;
    private PApplet pApplet;
    private float radius, height;
    private int sides;
    float[] x;
    float[] z;
    float[] x2;
    float[] z2;

    public ThermalCylinder(
            float radius,
            float height,
            double[][] tField,
            Gradient gradient,
            PApplet applet,
            int sides
            ) {
        this.sides = sides;
        this.radius = radius;
        this.height = height;
        initShape(sides, radius);
        initSideTexture(tField, gradient);
        pApplet = applet;
    }

    public void draw() {
        drawSide();
    }

    private void drawSide() {
        pApplet.pushMatrix();

        pApplet.translate(0, height / 2, 0);

        pApplet.beginShape(PApplet.QUAD_STRIP);
        pApplet.texture(sideTexture);
        for(int i=0; i < x.length - 2; i++){
            float u = SIDE_TEXTURE_WIDTH / sides * i;
            pApplet.vertex(x[i], - height / 2, z[i], u, 0);
            pApplet.vertex(x2[i], height / 2, z2[i], u, SIDE_TEXTURE_HEIGHT);
        }

        pApplet.endShape();
        pApplet.popMatrix();
    }

    private void initSideTexture(double[][] tField, Gradient gradient) {
        BufferedImage bi = new BufferedImage(
                SIDE_TEXTURE_WIDTH,
                SIDE_TEXTURE_HEIGHT,
                BufferedImage.TYPE_INT_RGB
            );
        int n = tField.length - 1;
        double nodesToPixels = (double) n / SIDE_TEXTURE_HEIGHT;
        for (int i = 0; i < SIDE_TEXTURE_HEIGHT; i++) {
            double temp = i * nodesToPixels;
            int left = (int) (temp);
            int right = (int) Math.ceil(temp);
            double t = tField[n - 1][left] + (tField[n - 1][right] - tField[n - 1][left]) * (temp - left);
            for (int j = 0; j < SIDE_TEXTURE_WIDTH; j++) {
                bi.setRGB(j, i, gradient.getGradient(t));
            }
        }
        sideTexture = new PImage(bi);
    }

    private void initShape(int sides, float radius) {
        x = new float[(sides+1) * 3 / 4 + 2];
        z = new float[(sides+1) * 3 / 4 + 2];
        x2 = new float[(sides+1) * 3 / 4 + 2];
        z2 = new float[(sides+1) * 3 / 4 + 2];
        for(int i=0; i < x.length; i++){
            float angle = PApplet.TWO_PI / (sides) * i;
            x[i] = PApplet.sin(angle) * radius;
            z[i] = PApplet.cos(angle) * radius;
        }
        x[x.length - 2] = 0;
        z[z.length - 2] = 0;
        x[x.length - 1] = x[0];
        z[z.length - 1] = z[0];

        for(int i=0; i < x.length; i++){
            float angle = PApplet.TWO_PI / (sides) * i;
            x2[i] = PApplet.sin(angle) * radius;
            z2[i] = PApplet.cos(angle) * radius;
        }
        x2[x.length - 2] = 0;
        z2[z.length - 2] = 0;
        x2[x.length - 1] = x2[0];
        z2[z.length - 1] = z2[0];
    }

    // rrrrrrrrggggggggbbbbbbbb
    private int rgb(int red, int green, int blue) {
        return (red << 16) + (green << 8) + blue;
    }

    public static void main(String[] args) throws IOException {
        TwoDimensionProblemSolver tdps = new TwoDimensionProblemSolver(50, 50);
        double t[][]  =
                new TwoDimensionProblemSolver(50, 50)
                        .calculateTemperatureCylinder(
                                0.1,
                                0.1,
                                0.7,
                                1500,
                                750,
                                20,
                                50,
                                50,
                                50,
                                60
                        );
        PApplet pApplet = new PApplet();
        Gradient gradient = new Gradient(pApplet);
        gradient.addColor(pApplet.color(102, 0, 102));
        gradient.addColor(pApplet.color(0, 144, 255));
        gradient.addColor(pApplet.color(0, 255, 207));
        gradient.addColor(pApplet.color(51, 204, 102));
        gradient.addColor(pApplet.color(111, 255, 0));
        gradient.addColor(pApplet.color(191, 255, 0));
        gradient.addColor(pApplet.color(255, 240, 0));
        gradient.addColor(pApplet.color(255, 153, 102));
        gradient.addColor(pApplet.color(204, 51, 0));
        gradient.addColor(pApplet.color(153, 0, 0));

        gradient.setMin(Math.min(20, 50));
        gradient.setMax(Math.max(20, 50));

        // ThermalCylinder tc = new ThermalCylinder(0.1f, 0.1f, t, gradient, pApplet);
//        tc.initSideTexture(t, gradient);
//        File file = new File("image.jpg");
//        ImageIO.write(tc.sideTexture, "jpg", file);
    }
}
