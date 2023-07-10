package org.openmuc.j60870.internal.cli;

public class CliConParameter {
    private int messageFragmentParameter;
    private int asduLength;
    private int ioaLength;
    private int t1;
    private int t2;
    private int t3;
    private int kParam;
    private int wParam;
    private int port;

    public CliConParameter() {
        this(5000, 2, 3, 15000, 10000, 20000, 12, 8, 2404);
    }

    public CliConParameter(int messageFragmentParameter, int asduLength, int ioaLength, int t1, int t2, int t3, int kParam, int wParam, int port) {
        this.messageFragmentParameter = messageFragmentParameter;
        this.asduLength = asduLength;
        this.ioaLength = ioaLength;
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
        this.kParam = kParam;
        this.wParam = wParam;
        this.port = port;
    }

    public int getMessageFragmentParameter() {
        return messageFragmentParameter;
    }

    public void setMessageFragmentParameter(int messageFragmentParameter) {
        this.messageFragmentParameter = messageFragmentParameter;
    }

    public int getAsduLength() {
        return asduLength;
    }

    public void setAsduLength(int asduLength) {
        this.asduLength = asduLength;
    }

    public int getIoaLength() {
        return ioaLength;
    }

    public void setIoaLength(int ioaLength) {
        this.ioaLength = ioaLength;
    }

    public int getT1() {
        return t1;
    }

    public void setT1(int t1) {
        this.t1 = t1;
    }

    public int getT2() {
        return t2;
    }

    public void setT2(int t2) {
        this.t2 = t2;
    }

    public int getT3() {
        return t3;
    }

    public void setT3(int t3) {
        this.t3 = t3;
    }

    public int getkParam() {
        return kParam;
    }

    public void setkParam(int kParam) {
        this.kParam = kParam;
    }

    public int getwParam() {
        return wParam;
    }

    public void setwParam(int wParam) {
        this.wParam = wParam;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
