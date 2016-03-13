package org.bitbucket.eirlis.conetc;

import processing.core.PApplet;

/**
 * Created by Elena on 13.03.2016.
 */
public class ConeTC extends PApplet {

    void cylinder(float bottom, float top, float h, int sides) {
        pushMatrix();

        translate(0,h/2,0);

        float angle;
        float[] x = new float[sides+1];
        float[] z = new float[sides+1];

        float[] x2 = new float[sides+1];
        float[] z2 = new float[sides+1];

        //get the x and z position on a circle for all the sides
        for(int i=0; i < x.length; i++){
            angle = TWO_PI / (sides) * i;
            x[i] = sin(angle) * bottom;
            z[i] = cos(angle) * bottom;
        }

        for(int i=0; i < x.length; i++){
            angle = TWO_PI / (sides) * i;
            x2[i] = sin(angle) * top;
            z2[i] = cos(angle) * top;
        }

        //draw the bottom of the cylinder
        beginShape(TRIANGLE_FAN);

        vertex(0,   -h/2,    0);

        for(int i=0; i < x.length; i++){
            vertex(x[i], -h/2, z[i]);
        }

        endShape();

        //draw the center of the cylinder
        beginShape(QUAD_STRIP);

        for(int i=0; i < x.length; i++){
            vertex(x[i], -h/2, z[i]);
            vertex(x2[i], h/2, z2[i]);
        }

        endShape();

        //draw the top of the cylinder
        beginShape(TRIANGLE_FAN);

        vertex(0,   h/2,    0);

        for(int i=0; i < x.length; i++){
            vertex(x2[i], h/2, z2[i]);
        }

        endShape();

        popMatrix();
    }

    @Override
    public void settings() {
        size(640, 360, "processing.opengl.PGraphics3D");
    }

    @Override
    public void draw() {
        background(0);
        lights();

        noStroke();
        pushMatrix();
        translate(400, height*0.35f, -200);

        cylinder(10, 80, 300, 200);
        popMatrix();
    }

    public static void main(String[] args) {
        PApplet.main(new String[] { "org.bitbucket.eirlis.conetc.ConeTC" });
    }
}
