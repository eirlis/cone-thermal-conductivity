package org.bitbucket.eirlis.conetc;

import controlP5.*;
import org.bitbucket.eirlis.conetc.core.ThermalProblemSolver;
import org.bitbucket.eirlis.conetc.managers.PositionManager;
import org.bitbucket.eirlis.conetc.render.FigureRenderer;
import org.bitbucket.eirlis.conetc.render.Gradient;
import org.omg.CORBA.DoubleHolder;
import processing.core.PApplet;
import processing.core.PFont;
import processing.event.MouseEvent;
import sun.font.TextLabel;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Elena on 13.03.2016.
 */
public class ConeTC extends PApplet {
    private float scaleX = 1, scaleY = 1;
    private float rotationX = 0f;
    private float rotationZ = 0f;
    private ControlP5 cp5;
    private String textValue = "";
    private FigureRenderer mFigureRenderer = new FigureRenderer(this);
    private ThermalProblemSolver mThermalProblemSolver = new ThermalProblemSolver();
    private Gradient gradient = new Gradient(this);

    double[] temperatureField;
    private int bottomRadius = 100;
    private int topRadius = 100;
    private int coneHeight = 300;
    private double ro;
    private double c;
    private double lambda;
    private double T0;
    private double Th;
    private double time;

    private PositionManager _positionManager;
    @Override
    public void mouseWheel(MouseEvent event) {
        super.mouseWheel(event);
            final float k = 0.2f;
            _positionManager.changeScale(-event.getCount() * k);
    }

    @Override
    public void settings() {
        size(900, 800, "processing.opengl.PGraphics3D");
    }

    @Override
    public void setup() {
        gradient.addColor(color(102, 0, 102));
        gradient.addColor(color(0, 144, 255));
        gradient.addColor(color(0, 255, 207));
        gradient.addColor(color(51, 204, 102));
        gradient.addColor(color(111, 255, 0));
        gradient.addColor(color(191, 255, 0));
        gradient.addColor(color(255, 240, 0));
        gradient.addColor(color(255, 153, 102));
        gradient.addColor(color(204, 51, 0));
        gradient.addColor(color(153, 0, 0));


        PFont font = createFont("arial",20);

        cp5 = new ControlP5(this);
        _positionManager = new PositionManager(this, 450, 200);
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
                .setText("1500")
                .setPosition(20,360)
                .setSize(200,40)
                .setFont(font)
                .setColor(color(255,255,255))
        ;
        cp5.addTextfield("Specific Heat Capacity")
                .setText("750")
                .setPosition(20,430)
                .setSize(200,40)
                .setFont(font)
                .setColor(color(255,255,255))
        ;
        cp5.addTextfield("Conductivity coefficient")
                .setText("0.7")
                .setPosition(20,500)
                .setSize(200,40)
                .setFont(font)
                .setColor(color(255,255,255))
        ;
        cp5.addTextfield("Initial temperature")
                .setText("20")
                .setPosition(20,570)
                .setSize(200,40)
                .setFont(font)
                .setColor(color(255,255,255))
        ;
        cp5.addTextfield("Border temperature")
                .setText("50")
                .setPosition(20,640)
                .setSize(200,40)
                .setFont(font)
                .setColor(color(255,255,255))
        ;
        cp5.addLabel("Time: ")
                .setPosition(300, 500)
                .setSize(200, 40)
                .setFont(font)
                .setColor(color(255, 255, 255))
        ;
        cp5.addTextfield("Time")
                .setText("30")
                .setPosition(300,570)
                .setSize(200,40)
                .setFont(font)
                .setColor(color(255,255,255))
        ;

        List<Integer> colors = gradient.getColors();
        for (int i = 0; i < colors.size(); i++) {
            cp5.addLabel("Color" + i)
                    .setPosition(700, 40 + i * 40)
                    .setText("")
                    .setSize(200, 40)
                    .setFont(font)
                    .setColor(color(255, 255, 255));
        }

        cp5.addButton("Calculate")
                .setPosition(300, 670)
                .setSize(100, 40)
                .addListener(new ControlListener() {
                    @Override
                    public void controlEvent(ControlEvent controlEvent) {
                        temperatureField = mThermalProblemSolver.currentTemperatureCylinder(
                                 Math.max(bottomRadius, topRadius) / 1000.0,
                                lambda,
                                ro,
                                c,
                                T0,
                                Th,
                                time
                        );
                        updateGradient();
                    }
                });

        textFont(font);
    }

    private void updateGradient() {
        gradient.setMin(Math.min(T0, Th));
        gradient.setMax(Math.max(T0, Th));
        List<Integer> colors = gradient.getColors();
        for (int i = 0; i < colors.size(); i++) {
            Textlabel label = cp5.get(Textlabel.class, "Color" + i);
            label.setText(Integer.toString((int)gradient.getColorValue(i)));
        }
    }

    private void drawLegend() {
        List<Integer> colors = gradient.getColors();
        for (int i = 0; i < colors.size(); i++) {
            fill(colors.get(i));
            Textlabel label = cp5.get(Textlabel.class, "Color" + i);
            float x, y;
            x = label.getPosition()[0];
            y = label.getPosition()[1];
            rect(x - 30, y, label.getHeight(), label.getHeight());
        }
    }

    @Override
    public void draw() {
        background(0);
        fill(255);
        try {
            bottomRadius = Integer.parseInt(cp5.get(Textfield.class, "Bottom radius").getText());
            topRadius = Integer.parseInt(cp5.get(Textfield.class, "Top radius").getText());
            coneHeight = Integer.parseInt(cp5.get(Textfield.class, "Height").getText());
            ro = Double.parseDouble(cp5.get(Textfield.class, "Density").getText());
            c = Double.parseDouble(cp5.get(Textfield.class, "Specific Heat Capacity").getText());
            lambda = Double.parseDouble(cp5.get(Textfield.class, "Conductivity coefficient").getText());
            T0 = Double.parseDouble(cp5.get(Textfield.class, "Initial temperature").getText());
            Th = Double.parseDouble(cp5.get(Textfield.class, "Border temperature").getText());
            time = Double.parseDouble(cp5.get(Textfield.class, "Time").getText());

        } catch (NumberFormatException e) {

        }
        if (temperatureField != null)
            drawLegend();
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
        if (temperatureField == null) {
            gradient.setMax(50);
            gradient.setMin(50);
            fill(gradient.getGradient(25));
            mFigureRenderer.cylinder(bottomRadius, topRadius, coneHeight, 40);
        } else {
            drawTemperatureCylinder();
        }
        popMatrix();
    }
    

    private void drawTemperatureCylinder() {
        double radiusFactor = 1.0 / temperatureField.length;
        for (int i = temperatureField.length - 1; i >= 0; i--) {
            fill(gradient.getGradient(temperatureField[i]));
            mFigureRenderer.tube(
                    (float)(bottomRadius * radiusFactor * i),
                    (float)(bottomRadius * radiusFactor * (i + 1)),
                    (float)(topRadius * radiusFactor * i),
                    (float)(topRadius * radiusFactor * (i + 1)),
                    coneHeight,
                    40
            );
        }
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
