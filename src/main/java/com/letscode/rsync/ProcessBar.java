/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.letscode.rsync;

/**
 *
 * @author kevin
 */
public class ProcessBar {
    private final int width;
    private String barStart = "[";
    private String barEnd = "]";
    private String arrowBody = "=";
    private String arrowEnd = ">";

    public ProcessBar(int width) {
        this.width = width;
    }

    public ProcessBar(int width, String barStart, String barEnd, String arrowBody,
            String arrowEnd) {
        this.barStart = barStart;
        this.barEnd = barEnd;
        this.arrowBody = arrowBody;
        this.arrowEnd = arrowEnd;
        this.width = width;
    }

    public void printProcessBar(int percent) {
        int processWidth = percent * width / 100;
        System.out.print("\r" + barStart);
        for (int i = 0; i < processWidth; i++) {
            System.out.print(arrowBody);
        }
        System.out.print(arrowEnd);
        for (int i = processWidth; i < width; i++) {
            System.out.print(" ");
        }
        System.out.print(barEnd + percent + "%");
    }
}
