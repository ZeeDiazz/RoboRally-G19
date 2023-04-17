package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;

public class CheckPoint extends Subject {
    public final Space space;
    public final int x;
    public final int y;


    public CheckPoint(Space space, int x, int y){
        this.space = space;
        this.x = x;
        this.y = y;
    }

}
