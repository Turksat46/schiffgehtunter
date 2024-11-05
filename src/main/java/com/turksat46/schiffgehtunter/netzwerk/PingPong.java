package com.turksat46.schiffgehtunter.netzwerk;

import java.io.BufferedReader;
import java.io.Writer;

public class PingPong {
    private String role;
    private BufferedReader in;
    private Writer out;



    public PingPong(String role){
        this.role = role;

    }
}
