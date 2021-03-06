package org.bitbucket.eirlis.conetc;

import controlP5.*;
import org.bitbucket.eirlis.conetc.core.TwoDimensionProblemSolver;
import org.bitbucket.eirlis.conetc.managers.PositionManager;
import org.bitbucket.eirlis.conetc.render.FigureRenderer;
import org.bitbucket.eirlis.conetc.render.Gradient;
import org.bitbucket.eirlis.conetc.render.ThermalCylinder;
import processing.core.PApplet;
import processing.core.PFont;
import processing.event.MouseEvent;

import java.util.List;
import java.util.Objects;

/**
 * Created by Elena on 13.03.2016.
 */
public class ConeTC extends PApplet {

    private static int NR = 100, NZ = 100;
    private float scaleX = 1, scaleY = 1;
    private float rotationX = 0f;
    private float rotationZ = 0f;
    private ControlP5 cp5;
    private String textValue = "";
    private FigureRenderer mFigureRenderer = new FigureRenderer(this);
    private TwoDimensionProblemSolver mThermalProblemSolver = new TwoDimensionProblemSolver(NR, NZ);
    private Gradient gradient = new Gradient(this);
    private ThermalCylinder thermalCylinder;

    private double MAX_TEMPERATURE = 500;
    private double MIN_TEMPERATURE = 0;
    private double stepFrequency = 0.1;

    double[][] temperatureField;
    private int steps;
    private int topRadius = 100;
    private int coneHeight = 300;
    private double ro;
    private double c;
    private double lambda;
    private double T0;
    private double Th;
    private double time;

    private double timeStep;
    private double currentTime = 0;
    private double timer = stepFrequency;
    private boolean started = false;

    private PositionManager _positionManager;

    @Override
    public void mouseWheel(MouseEvent event) {
        super.mouseWheel(event);
        final float k = 0.2f;
        _positionManager.changeScale(-event.getCount() * k);
    }

    @Override
    public void settings() {
        size(800, 750, "processing.opengl.PGraphics3D");
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

        PFont font = createFont("arial", 20);

        cp5 = new ControlP5(this);
        _positionManager = new PositionManager(this, 450, 200);
        _positionManager.setOffsetY(0.3f);
        _positionManager.setScale(0.7f);


        //TODO:
        //handle min/max values
        //minus in temperature
        //framerate
        //min max temp for gradient --> bound gradient n temperature fields with min/max values
        //steps counter < 0
        cp5.addLabel("Geometric Characteristics: ")
                .setPosition(20, 20)
                .setSize(200, 20)
                .setFont(font)
                .setColor(color(255, 255, 255));

        cp5.addTextfield("Top radius")
                .setText("100")
                .setPosition(20, 100)
                .setSize(200, 25)
                .setInputFilter(ControlP5.INTEGER)
                .setFont(font)
                .setColor(color(255, 255, 255))
                .onLeave(new CallbackListener() {
                    @Override
                    public void controlEvent(CallbackEvent callbackEvent) {
                        if (Objects.equals(cp5.get(Textfield.class, "Top radius").getText(), "")) {
                            topRadius = 100;
                            cp5.get(Textfield.class, "Top radius").setText("100");
                        }

                        if (Integer.parseInt(cp5.get(Textfield.class, "Top radius").getText()) > 200 ||
                                Integer.parseInt(cp5.get(Textfield.class, "Top radius").getText()) < 50) {
                            topRadius = 100;
                            cp5.get(Textfield.class, "Top radius").setText("100");
                        }
               /* bottomRadius = Integer.parseInt(cp5.get(Textfield.class, "Top radius").getText());
                cp5.get(Textfield.class, "Bottom radius").setText(String.valueOf(bottomRadius));*/
                    }
                })
        ;
        cp5.addTextfield("Height")
                .setText("300")
                .setPosition(20, 145)
                .setSize(200, 25)
                .setInputFilter(ControlP5.INTEGER)
                .setFont(font)
                .setColor(color(255, 255, 255))
                .onLeave(new CallbackListener() {
                    @Override
                    public void controlEvent(CallbackEvent callbackEvent) {
                        if (Objects.equals(cp5.get(Textfield.class, "Height").getText(), "")) {
                            height = 200;
                            cp5.get(Textfield.class, "Height").setText("400");
                        }

                        if (Integer.parseInt(cp5.get(Textfield.class, "Height").getText()) > 400 ||
                                Integer.parseInt(cp5.get(Textfield.class, "Height").getText()) < 50) {
                            height = 200;
                            cp5.get(Textfield.class, "Height").setText("400");
                        }
                    }
                })
        ;
        cp5.addLabel("Physical Characteristics: ")
                .setPosition(20, 190)
                .setSize(200, 25)
                .setFont(font)
                .setColor(color(255, 255, 255))
        ;
        cp5.addTextfield("Density")
                .setText("1500")
                .setPosition(20, 235)
                .setSize(200, 25)
                .setInputFilter(ControlP5.FLOAT)
                .setFont(font)
                .setColor(color(255, 255, 255))
        ;
        cp5.addTextfield("Specific Heat Capacity")
                .setText("750")
                .setPosition(20, 280)
                .setSize(200, 25)
                .setInputFilter(ControlP5.FLOAT)
                .setFont(font)
                .setColor(color(255, 255, 255))
        ;
        cp5.addTextfield("Conductivity coefficient")
                .setText("0.7")
                .setPosition(20, 325)
                .setSize(200, 25)
                .setInputFilter(ControlP5.FLOAT)
                .setFont(font)
                .setColor(color(255, 255, 255))
        ;
        cp5.addTextfield("Initial temperature")
                .setText("20")
                .setPosition(20, 370)
                .setSize(200, 25)
                .setInputFilter(ControlP5.FLOAT)
                .setFont(font)
                .setColor(color(255, 255, 255))
        ;
        cp5.addTextfield("Border temperature")
                .setText("400")
                .setPosition(20, 415)
                .setSize(200, 25)
                .setInputFilter(ControlP5.FLOAT)
                .setFont(font)
                .setColor(color(255, 255, 255))
        ;
        cp5.addLabel("Animation: ")
                .setPosition(20, 470)
                .setSize(200, 25)
                .setFont(font)
                .setColor(color(255, 255, 255))
        ;
        cp5.addTextfield("Time")
                .setText("300")
                .setPosition(20, 515)
                .setSize(150, 25)
                .setInputFilter(ControlP5.INTEGER)
                .setFont(font)
                .setColor(color(255, 255, 255))
                .onLeave(new CallbackListener() {
                    @Override
                    public void controlEvent(CallbackEvent callbackEvent) {
                        if (Objects.equals(cp5.get(Textfield.class, "Time").getText(), "")) {
                            time = 300;
                            cp5.get(Textfield.class, "Time").setText("300");
                        }

                        if (Integer.parseInt(cp5.get(Textfield.class, "Time").getText()) > 99999 ||
                                Integer.parseInt(cp5.get(Textfield.class, "Time").getText()) < 1) {
                            time = 300;
                            cp5.get(Textfield.class, "Time").setText("300");
                        }

                    }
                })
        ;

        cp5.addTextfield("Steps")
                .setText("10")
                .setPosition(175, 515)
                .setSize(45, 25)
                .setFont(font)
                .setInputFilter(ControlP5.INTEGER)
                .setColor(color(255, 255, 255))
                .onLeave(new CallbackListener() {
                    @Override
                    public void controlEvent(CallbackEvent callbackEvent) {
                        if (Objects.equals(cp5.get(Textfield.class, "Steps").getText(), "")) {
                            steps = 50;
                            cp5.get(Textfield.class, "Steps").setText("50");
                        }

                        if (Integer.parseInt(cp5.get(Textfield.class, "Steps").getText()) > 999 ||
                                Integer.parseInt(cp5.get(Textfield.class, "Steps").getText()) < 0) {
                            steps = 50;
                            cp5.get(Textfield.class, "Steps").setText("50");
                        }
                    }
                })
        ;

        cp5.addTextfield("Animation delay")
                .setText("0.5")
                .setPosition(20, 560)
                .setSize(200, 25)
                .setFont(font)
                .setInputFilter(ControlP5.FLOAT)
                .setColor(color(255, 255, 255))
                .onLeave(new CallbackListener() {
                    @Override
                    public void controlEvent(CallbackEvent callbackEvent) {
                        if (Objects.equals(cp5.get(Textfield.class, "Animation delay").getText(), "")) {
                            stepFrequency = 0.5;
                            cp5.get(Textfield.class, "Animation delay").setText("0.5");
                        }

                        if (Double.parseDouble(cp5.get(Textfield.class, "Animation delay").getText()) > 0.99 ||
                                Double.parseDouble(cp5.get(Textfield.class, "Animation delay").getText()) < 0.01) {
                            stepFrequency = 0.5;
                            cp5.get(Textfield.class, "Animation delay").setText("0.5");
                        }
                    }
                })
        ;

        cp5.addTextfield("Min legend temperature")
                .setText("0")
                .setPosition(20, 605)
                .setSize(200, 25)
                .setFont(font)
                .setInputFilter(ControlP5.FLOAT)
                .setColor(color(255, 255, 255))
                .onLeave(new CallbackListener() {
                    @Override
                    public void controlEvent(CallbackEvent callbackEvent) {
                        if (Objects.equals(cp5.get(Textfield.class, "Min legend temperature").getText(), "")) {
                            MIN_TEMPERATURE = 0;
                            cp5.get(Textfield.class, "Min legend temperature").setText("0");
                        }

                        if (Double.parseDouble(cp5.get(Textfield.class, "Min legend temperature").getText()) > 10000 ||
                                Double.parseDouble(cp5.get(Textfield.class, "Min legend temperature").getText()) < 0) {
                            MIN_TEMPERATURE = 0;
                            cp5.get(Textfield.class, "Animation delay").setText("0");
                        }
                    }
                })
        ;

        cp5.addTextfield("Max legend temperature")
                .setText("500")
                .setPosition(20, 650)
                .setSize(200, 25)
                .setFont(font)
                .setInputFilter(ControlP5.FLOAT)
                .setColor(color(255, 255, 255))
                .onLeave(new CallbackListener() {
                    @Override
                    public void controlEvent(CallbackEvent callbackEvent) {
                        if (Objects.equals(cp5.get(Textfield.class, "Max legend temperature").getText(), "")) {
                            MAX_TEMPERATURE = 500;
                            cp5.get(Textfield.class, "Max legend temperature").setText("500");
                        }

                        if (Double.parseDouble(cp5.get(Textfield.class, "Max legend temperature").getText()) > 100000 ||
                                Double.parseDouble(cp5.get(Textfield.class, "Max legend temperature").getText()) < 0) {
                            MAX_TEMPERATURE = 500;
                            cp5.get(Textfield.class, "Animation delay").setText("500");
                        }
                    }
                })
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

        Button btStartAnimation = cp5.addButton("Start animation")
                .setPosition(300, 515)
                .setSize(300, 60)
                .addListener(new ControlListener() {
                    @Override
                    public void controlEvent(ControlEvent controlEvent) {
                        startDraw();
                    }
                });

        Button btReset = cp5.addButton("Reset data")
                .setPosition(300, 600)
                .setSize(300, 30);

        btReset.addCallback(new CallbackListener() {
            @Override
            public void controlEvent(CallbackEvent callbackEvent) {
                if (callbackEvent.getAction() == ControlP5.ACTION_PRESS) {
                    resetToDefaults();

                }
            }
        });

        textFont(font);
    }

    private void resetToDefaults() {
        cp5.get(Textfield.class, "Top radius").setText("100");
        cp5.get(Textfield.class, "Height").setText("300");
        cp5.get(Textfield.class, "Density").setText("1500");

        cp5.get(Textfield.class, "Specific Heat Capacity").setText("750");
        cp5.get(Textfield.class, "Conductivity coefficient").setText("0.7");
        cp5.get(Textfield.class, "Initial temperature").setText("20");
        cp5.get(Textfield.class, "Border temperature").setText("200");
        cp5.get(Textfield.class, "Time").setText("300");
        cp5.get(Textfield.class, "Steps").setText("10");
        cp5.get(Textfield.class, "Animation delay").setText("0.5");
        cp5.get(Textfield.class, "Min legend temperature").setText("0");
        cp5.get(Textfield.class, "Max legend temperature").setText("500");


        if (temperatureField != null) {
            stopDraw();

            cp5.get(Textfield.class, "Initial temperature").unlock();
            temperatureField = null;
        }

    }

    private void updateGradient() {
        gradient.setMax(MAX_TEMPERATURE);
        gradient.setMin(MIN_TEMPERATURE);
//        gradient.setMin(Math.min(T0, Th));
//        gradient.setMax(Math.max(T0, Th));
        List<Integer> colors = gradient.getColors();
        for (int i = 0; i < colors.size(); i++) {
            Textlabel label = cp5.get(Textlabel.class, "Color" + i);
            label.setText(Integer.toString((int) gradient.getColorValue(i)));
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

    private void calculate() {
        if (temperatureField != null) {
            temperatureField = mThermalProblemSolver.calculateTemperatureCylinder(
                    topRadius / 1000.0,
                    height / 1000.0,
                    lambda,
                    ro,
                    c,
                    temperatureField,
                    Th,
                    Th,
                    Th,
                    timeStep
            );
        } else {
            temperatureField = mThermalProblemSolver.calculateTemperatureCylinder(
                    topRadius / 1000.0,
                    height / 1000.0,
                    lambda,
                    ro,
                    c,
                    T0,
                    Th,
                    Th,
                    Th,
                    timeStep
            );
        }
        updateGradient();
        thermalCylinder = new ThermalCylinder(
                topRadius,
                coneHeight,
                temperatureField,
                gradient,
                ConeTC.this,
                50
        );
    }

    private void startDraw() {
        if (steps <= 0)
            return;
        timeStep = time / steps;
        started = true;
        cp5.get(Textfield.class, "Top radius").lock();
        cp5.get(Textfield.class, "Height").lock();
        cp5.get(Textfield.class, "Density").lock();

        cp5.get(Textfield.class, "Specific Heat Capacity").lock();
        cp5.get(Textfield.class, "Conductivity coefficient").lock();
        cp5.get(Textfield.class, "Initial temperature").lock();
        cp5.get(Textfield.class, "Border temperature").lock();
        cp5.get(Textfield.class, "Time").lock();
        cp5.get(Textfield.class, "Steps").lock();
        cp5.get(Textfield.class, "Max legend temperature").lock();
        cp5.get(Textfield.class, "Min legend temperature").lock();
    }

    private void stopDraw() {
        started = false;
        cp5.get(Button.class, "Start animation").setColor(ControlP5.THEME_CP5BLUE);
        cp5.get(Button.class, "Start animation").setLabel("Start animation");
        currentTime = 0;
        timer = stepFrequency;

        cp5.get(Textfield.class, "Top radius").unlock();
        cp5.get(Textfield.class, "Height").unlock();
        cp5.get(Textfield.class, "Density").unlock();

        cp5.get(Textfield.class, "Specific Heat Capacity").unlock();
        cp5.get(Textfield.class, "Conductivity coefficient").unlock();
        cp5.get(Textfield.class, "Border temperature").unlock();
        cp5.get(Textfield.class, "Time").unlock();
        cp5.get(Textfield.class, "Steps").unlock();
        cp5.get(Textfield.class, "Max legend temperature").unlock();
        cp5.get(Textfield.class, "Min legend temperature").unlock();
    }

    @Override
    public void draw() {
        double deltaTime = 1.0 / frameRate;
        if (started) {
            cp5.get(Button.class, "Start animation").setColor(ControlP5.THEME_RED);
            cp5.get(Button.class, "Start animation").setLabel("ANIMATION IN PROCESS");
            timer -= deltaTime;
            if (timer < 0) {
                calculate();
                timer += stepFrequency;
                currentTime += timeStep;
                steps--;
                cp5.get(Textfield.class, "Steps").setText(Integer.toString(steps));
                if (steps <= 0) {
                    stopDraw();
                }
            }
        }
        background(0);
        fill(255);
        try {
            topRadius = Integer.parseInt(cp5.get(Textfield.class, "Top radius").getText());
            coneHeight = Integer.parseInt(cp5.get(Textfield.class, "Height").getText());
            ro = Double.parseDouble(cp5.get(Textfield.class, "Density").getText());
            c = Double.parseDouble(cp5.get(Textfield.class, "Specific Heat Capacity").getText());
            lambda = Double.parseDouble(cp5.get(Textfield.class, "Conductivity coefficient").getText());
            T0 = Double.parseDouble(cp5.get(Textfield.class, "Initial temperature").getText());
            Th = Double.parseDouble(cp5.get(Textfield.class, "Border temperature").getText());
            time = Double.parseDouble(cp5.get(Textfield.class, "Time").getText());
            steps = Integer.parseInt(cp5.get(Textfield.class, "Steps").getText());
            stepFrequency = Double.parseDouble(cp5.get(Textfield.class, "Animation delay").getText());
            MAX_TEMPERATURE = Double.parseDouble(cp5.get(Textfield.class, "Max legend temperature").getText());
            MIN_TEMPERATURE = Double.parseDouble(cp5.get(Textfield.class, "Min legend temperature").getText());

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
            mFigureRenderer.cylinder(topRadius, topRadius, coneHeight, 40);
        } else {
            // mFigureRenderer.textureCylinder(bottomRadius, coneHeight, loadImage("kotik.png"), 40);
            // drawTemperatureCylinder();
            thermalCylinder.draw();
        }
        popMatrix();
    }


    private void drawTemperatureCylinder() {
        double heightInc = (double) coneHeight / NZ;
        double radiusFactor = 1.0 / NR;
        for (int i = NZ - 1; i >= 0; i--) {
            for (int j = NR - 1; j >= 0; j--) {
                fill(gradient.getGradient(temperatureField[j][i]));
                mFigureRenderer.tube(
                        (float) (topRadius * radiusFactor * j),
                        (float) (topRadius * radiusFactor * (j + 1)),
                        (float) (topRadius * radiusFactor * j),
                        (float) (topRadius * radiusFactor * (j + 1)),
                        (float) heightInc,
                        40
                );
            }
            translate(0, (float) heightInc, 0);
        }
    }

    /*
    2D version
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
    */

    public void clear() {
        cp5.get(Textfield.class, "textValue").clear();
    }

    void controlEvent(ControlEvent theEvent) {
        if (theEvent.isAssignableFrom(Textfield.class)) {
            println("controlEvent: accessing a string from controller '"
                    + theEvent.getName() + "': "
                    + theEvent.getStringValue()
            );
        }
    }

    public void input(String theText) {
        // automatically receives results from controller input
        println("a textfield event for controller 'input' : " + theText);
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
            System.out.println("here");
            if (keyCode == UP)
                rotationX -= 0.1f;
            if (keyCode == DOWN)
                rotationX += 0.1f;
            if (keyCode == RIGHT)
                rotationZ += 0.1f;
            if (keyCode == LEFT)
                rotationZ -= 0.1f;
        }
        if (keyCode == 32) {
            temperatureField = null;
            loop();
        }
    }

    @Override
    public void mouseDragged() {
//        rotationX = rotationX + 0.01f * (mouseX - pmouseX);
//        rotationZ = rotationZ + 0.01f * (mouseY - pmouseY);
        _positionManager.setPositionByMouse();
    }

    public static void main(String[] args) {
        PApplet.main(new String[]{"org.bitbucket.eirlis.conetc.ConeTC"});
        System.out.println("Hello");
    }
}
