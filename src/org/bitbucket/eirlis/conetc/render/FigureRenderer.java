package org.bitbucket.eirlis.conetc.render;

import processing.core.PApplet;
import processing.core.PImage;

/**
 * Created by Elena on 28.03.2016.
 */
public class FigureRenderer {
    private PApplet mPApplet;

    public FigureRenderer(PApplet mPApplet) {
        this.mPApplet = mPApplet;
    }

     public void cylinder(float bottom, float top, float h, int sides) {
        mPApplet.pushMatrix();

        mPApplet.translate(0,h/2,0);

        float angle;
        float[] x = new float[(sides+1) * 3 / 4 + 2];
        float[] z = new float[(sides+1) * 3 / 4 + 2];
        float[] x2 = new float[(sides+1) * 3 / 4 + 2];
        float[] z2 = new float[(sides+1) * 3 / 4 + 2];

        //get the x and z position on a circle for all the sides
        for(int i=0; i < x.length; i++){
            angle = PApplet.TWO_PI / (sides) * i;
            x[i] = PApplet.sin(angle) * bottom;
            z[i] = PApplet.cos(angle) * bottom;
        }
        x[x.length - 2] = 0;
        z[z.length - 2] = 0;
        x[x.length - 1] = x[0];
        z[z.length - 1] = z[0];

        for(int i=0; i < x.length; i++){
            angle = PApplet.TWO_PI / (sides) * i;
            x2[i] = PApplet.sin(angle) * top;
            z2[i] = PApplet.cos(angle) * top;
        }
        x2[x.length - 2] = 0;
        z2[z.length - 2] = 0;
        x2[x.length - 1] = x2[0];
        z2[z.length - 1] = z2[0];

        //draw the bottom of the cylinder
        mPApplet.beginShape(PApplet.TRIANGLE_FAN);

        mPApplet.vertex(0,   -h/2,    0);

        for(int i = 0; i < x.length; i++){
            mPApplet.vertex(x[i], -h/2, z[i]);
        }

        mPApplet.endShape();

        //draw the center of the cylinder
        mPApplet.beginShape(PApplet.QUAD_STRIP);

        for(int i=0; i < x.length; i++){
            mPApplet.vertex(x[i], -h/2, z[i]);
            mPApplet.vertex(x2[i], h/2, z2[i]);
        }

        mPApplet.endShape();

        //draw the top of the cylinder
        mPApplet.beginShape(PApplet.TRIANGLE_FAN);

        mPApplet.vertex(0,   h/2,    0);

        for(int i=0; i < x.length; i++){
            mPApplet.vertex(x2[i], h/2, z2[i]);
        }

        mPApplet.endShape();

        mPApplet.popMatrix();
    }

    public void tube(
            float bottomInner,
            float bottomOuter,
            float topInner,
            float topOuter,
            float h,
            int sides
    ) {
        mPApplet.pushMatrix();

        mPApplet.translate(0,h/2,0);

        float angle;
        final int vertexCount = (sides + 1) * 3 / 4;
        float[] x = new float[vertexCount * 2 + 1];
        float[] z = new float[vertexCount * 2 + 1];
        float[] x2 = new float[vertexCount * 2 + 1];
        float[] z2 = new float[vertexCount * 2 + 1];

        //get the x and z position on a circle for all the sides
        for(int i = 0; i < vertexCount; i++){
            angle = PApplet.TWO_PI / (sides) * i;
            x[i] = PApplet.sin(angle) * bottomOuter;
            z[i] = PApplet.cos(angle) * bottomOuter;
        }
        for(int i = vertexCount - 1, j = vertexCount; i >= 0; i--, j++){
            angle = PApplet.TWO_PI / (sides) * i;
            x[j] = PApplet.sin(angle) * bottomInner;
            z[j] = PApplet.cos(angle) * bottomInner;
        }

        for(int i=0; i < x.length; i++){
            angle = PApplet.TWO_PI / (sides) * i;
            x2[i] = PApplet.sin(angle) * topOuter;
            z2[i] = PApplet.cos(angle) * topOuter;
        }
        for(int i = vertexCount - 1, j = vertexCount; i >= 0; i--, j++){
            angle = PApplet.TWO_PI / (sides) * i;
            x2[j] = PApplet.sin(angle) * topInner;
            z2[j] = PApplet.cos(angle) * topInner;
        }
        x[x.length - 1] = x[0];
        z[z.length - 1] = z[0];
        x2[x2.length - 1] = x2[0];
        z2[z2.length - 1] = z2[0];


        //draw the bottom of the cylinder
        mPApplet.beginShape(/*PApplet.TRIANGLE_FAN*/);

        mPApplet.vertex(0,   -h/2,    0);

        for (int i = 0; i < x.length; i++) {
            mPApplet.vertex(x[i], -h/2, z[i]);
        }

        mPApplet.endShape();

        //draw the center of the cylinder
        mPApplet.beginShape(PApplet.QUAD_STRIP);

        for (int i=0; i < x.length; i++) {
            mPApplet.vertex(x[i], -h/2, z[i]);
            mPApplet.vertex(x2[i], h/2, z2[i]);
        }

        mPApplet.endShape();

        //draw the top of the cylinder
        mPApplet.beginShape(/*PApplet.TRIANGLE_FAN*/);

        mPApplet.vertex(0,   h/2,    0);

        for(int i=0; i < x.length; i++){
            mPApplet.vertex(x2[i], h/2, z2[i]);
        }

        mPApplet.endShape();

        mPApplet.popMatrix();
    }

    public void textureCylinder(
            float radius,
            float height,
            PImage img,
            int sides
    ) {
        mPApplet.rotateY(180);
        mPApplet.background(0);
        mPApplet.beginShape(PApplet.QUAD_STRIP);
        mPApplet.texture(img);
        float angle = (float) 270.0 / sides;
        float[] tubeX = new float[sides];
        float[] tubeY = new float[sides];
        for (int i = 0; i < sides; i++) {
            tubeX[i] = PApplet.cos(PApplet.radians(i * angle));
            tubeY[i] = PApplet.sin(PApplet.radians(i * angle));
        }

        for (int i = 0; i < sides; i++) {
            float x = tubeX[i] * radius;
            float z = tubeY[i] * radius;
            float u = img.width / sides * i;
            mPApplet.vertex(x, -radius, z, u, 0);
            mPApplet.vertex(x, radius, z, u, img.height);
        }
        mPApplet.endShape();
        mPApplet.beginShape(PApplet.QUADS);
        mPApplet.texture(img);
        mPApplet.vertex(0, -radius, 0, 0, 0);
        mPApplet.vertex(radius, -radius, 0, radius, 0);
        mPApplet.vertex(radius, radius, 0, radius, radius);
        mPApplet.vertex(0, radius, 0, 0, radius);
        mPApplet.endShape();
    }
}
