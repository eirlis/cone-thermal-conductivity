package org.bitbucket.eirlis.conetc;

import controlP5.ControlEvent;
import controlP5.ControlP5;
import org.bitbucket.eirlis.conetc.managers.PositionManager;
import processing.core.PApplet;
import controlP5.Textfield;
import processing.core.PFont;
import processing.event.MouseEvent;

/**
 * Created by Elena on 13.03.2016.
 */
public class ConeTC extends PApplet {
    private float scaleX = 1, scaleY = 1;
    private float rotationX = 0f;
    private float rotationZ = 0f;
    private ControlP5 cp5;
    private String textValue = "";

    private int bottomRadius = 100;
    private int topRadius = 100;
    private int coneHeight = 300;
    private int ro;
    private int c;
    private int lambda;

    private PositionManager _positionManager;
    @Override
    public void mouseWheel(MouseEvent event) {
        super.mouseWheel(event);
            final float k = 0.2f;
            _positionManager.changeScale(-event.getCount() * k);
    }

    void cylinder(float bottom, float top, float h, int sides) {
        pushMatrix();

        translate(0,h/2,0);

        float angle;
        float[] x = new float[(sides+1) * 3 / 4 + 2];
        float[] z = new float[(sides+1) * 3 / 4 + 2];
        float[] x2 = new float[(sides+1) * 3 / 4 + 2];
        float[] z2 = new float[(sides+1) * 3 / 4 + 2];

        //get the x and z position on a circle for all the sides
        for(int i=0; i < x.length; i++){
            angle = TWO_PI / (sides) * i;
            x[i] = sin(angle) * bottom;
            z[i] = cos(angle) * bottom;
        }
        x[x.length - 2] = 0;
        z[z.length - 2] = 0;
        x[x.length - 1] = x[0];
        z[z.length - 1] = z[0];

        for(int i=0; i < x.length; i++){
            angle = TWO_PI / (sides) * i;
            x2[i] = sin(angle) * top;
            z2[i] = cos(angle) * top;
        }
        x2[x.length - 2] = 0;
        z2[z.length - 2] = 0;
        x2[x.length - 1] = x2[0];
        z2[z.length - 1] = z2[0];

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
        size(640, 600, "processing.opengl.PGraphics3D");
    }

    @Override
    public void setup() {
        PFont font = createFont("arial",20);

        cp5 = new ControlP5(this);
        _positionManager = new PositionManager(this);
        _positionManager.setOffsetY(0.3f);
        _positionManager.setScale(0.7f);

        cp5.addLabel("Geometric Characteristics: ")
                .setPosition(20, 50)
                .setSize(200, 40)
                .setFont(font)
                .setColor(color(255, 255, 255));
        cp5.addTextfield("Bottom radius")
                .setText("100")
                .setPosition(20,100)
                .setSize(200,40)
                .setFont(font)
                .setFocus(true)
                .setColor(color(255,255,255))
        ;
        cp5.addTextfield("Top radius")
                .setText("100")
                .setPosition(20,170)
                .setSize(200,40)
                .setFont(font)
                .setColor(color(255,255,255))
        ;
        cp5.addTextfield("Height")
                .setText("300")
                .setPosition(20,240)
                .setSize(200,40)
                .setFont(font)
                .setColor(color(255,255,255))
        ;
        cp5.addLabel("Physical Characteristics: ")
                .setPosition(20, 310)
                .setSize(200, 40)
                .setFont(font)
                .setColor(color(255, 255, 255))
        ;
        cp5.addTextfield("Density")
                .setText("300")
                .setPosition(20,360)
                .setSize(200,40)
                .setFont(font)
                .setColor(color(255,255,255))
        ;
        cp5.addTextfield("Specific Heat Capacity")
                .setText("300")
                .setPosition(20,430)
                .setSize(200,40)
                .setFont(font)
                .setColor(color(255,255,255))
        ;
        cp5.addTextfield("Conductivity coefficient")
                .setText("300")
                .setPosition(20,500)
                .setSize(200,40)
                .setFont(font)
                .setColor(color(255,255,255))
        ;

        textFont(font);
    }

    @Override
    public void draw() {

        background(0);
        fill(255);
        try {
            bottomRadius = Integer.parseInt(cp5.get(Textfield.class, "Bottom radius").getText());
            topRadius = Integer.parseInt(cp5.get(Textfield.class, "Top radius").getText());
            coneHeight = Integer.parseInt(cp5.get(Textfield.class, "Height").getText());
            ro = Integer.parseInt(cp5.get(Textfield.class, "Density").getText());
            c = Integer.parseInt(cp5.get(Textfield.class, "Specific Heat Capacity").getText());
            lambda = Integer.parseInt(cp5.get(Textfield.class, "Conductivity coefficient").getText());
        } catch (NumberFormatException e) {

        }
        // text(cp5.get(Textfield.class,"Radius").getText(), 360,130);
        // text(textValue, 360,180);

//        background(0);

        lights();
        noStroke();
        pushMatrix();
        _positionManager.draw();
        //translate(600, height*0.30f, -250);
//        rotateX(rotationX);
//        rotateZ(rotationZ);
        cylinder(bottomRadius, topRadius, coneHeight, 40);
        fill(255, 0, 0);
        cylinder(bottomRadius / 2, topRadius / 2, coneHeight, 40);
        popMatrix();
    }

    public void clear() {
        cp5.get(Textfield.class,"textValue").clear();
    }

    void controlEvent(ControlEvent theEvent) {
        if(theEvent.isAssignableFrom(Textfield.class)) {
            println("controlEvent: accessing a string from controller '"
                    +theEvent.getName()+"': "
                    +theEvent.getStringValue()
            );
        }
    }

    public void input(String theText) {
        // automatically receives results from controller input
        println("a textfield event for controller 'input' : "+theText);
    }

    private static int rgbToInt(int red, int green, int blue) {
        int rgb = red;
        rgb = (rgb << 8) + green;
        rgb = (rgb << 8) + blue;
        return rgb;
    }
    @Override
    public void keyPressed() {
        if (key == CODED) {
            if (keyCode == UP)
                rotationX -= 0.1f;
            if (keyCode == DOWN)
                rotationX += 0.1f;
            if (keyCode == RIGHT)
                rotationZ += 0.1f;
            if (keyCode == LEFT)
                rotationZ -= 0.1f;
        }
    }

    @Override
    public void mouseDragged() {
//        rotationX = rotationX + 0.01f * (mouseX - pmouseX);
//        rotationZ = rotationZ + 0.01f * (mouseY - pmouseY);
            _positionManager.setPositionByMouse();
    }

    public static void main(String[] args) {
        PApplet.main(new String[] { "org.bitbucket.eirlis.conetc.ConeTC" });
        System.out.println("Hello");
    }
}
