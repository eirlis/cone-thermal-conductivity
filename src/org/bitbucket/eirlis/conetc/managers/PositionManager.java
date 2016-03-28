package org.bitbucket.eirlis.conetc.managers;

import processing.core.PApplet;

public class PositionManager {
    public final static float SPEED_POS = 20.0f;
    public final static float EPS_POS = 0.0001f;

    private float _curRotateX = 0;
    private float _curRotateY = -PApplet.QUARTER_PI;
    private float _goalRotateX = PApplet.QUARTER_PI / 2f;
    private float _goalRotateY = 0;

    private float _goalY = 0;
    private float _curY = 0;

    private float _goalScale = 0;
    private float _curScale = 0;

    private final PApplet mParent;

    public PositionManager(PApplet parent) {
        mParent = parent;
    }

    public PApplet getParent(){
        return mParent;
    }

    public void draw() {
        float diff = _curY - _goalY;
        if (PApplet.abs(diff) > EPS_POS) {
            _curY -= diff / SPEED_POS;
        }

        diff = _curRotateX - _goalRotateX;
        if (PApplet.abs(diff) > EPS_POS) {
            _curRotateX -= diff / SPEED_POS;
        }

        diff = _curRotateY - _goalRotateY;
        if (PApplet.abs(diff) > EPS_POS) {
            _curRotateY -= diff / SPEED_POS;
        }

        diff = _curScale - _goalScale;
        if (PApplet.abs(diff) > EPS_POS) {
            _curScale -= diff / SPEED_POS;
        }

        getParent().translate(getParent().width * 0.7f, _curY, 0.0f);
        getParent().rotateX(_curRotateY);
        getParent().rotateY(_curRotateX);

        getParent().scale(_curScale);
    }

    public void setPositionByMouse() {
        _goalRotateX += (getParent().mouseX - getParent().pmouseX) / (float) (getParent().width) * PApplet.PI;
        _goalRotateY += (getParent().pmouseY - getParent().mouseY) / (float) (getParent().height) * PApplet.PI;

        checkPosition();
    }

    private void checkPosition() {
        if (_goalRotateY > PApplet.QUARTER_PI * 0.5f)
            _goalRotateY = PApplet.QUARTER_PI * 0.5f;
        else if (_goalRotateY < -PApplet.QUARTER_PI)
            _goalRotateY = -PApplet.QUARTER_PI;

        if (_goalRotateX > PApplet.QUARTER_PI)
            _goalRotateX = PApplet.QUARTER_PI;
        else if (_goalRotateX < -PApplet.QUARTER_PI)
            _goalRotateX = -PApplet.QUARTER_PI;
    }

    public void setOffsetY(float k) {
        float offsetY = k >= 0.0f && k <= 1.0f ? k : 0.0f;
        _curY = getParent().height * offsetY;
        _goalY = getParent().width * offsetY;
    }

    public void setScale(float scale) {
        if (scale < 0.5f) {
            _goalScale = 0.5f;
        } else if (scale > 1.0f) {
            _goalScale = 1.0f;
        } else {
            _goalScale = scale;
        }
    }

    public void changeScale(float k) {
        setScale(_goalScale + k);
    }
}