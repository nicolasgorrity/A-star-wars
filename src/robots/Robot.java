package robots;

import field.Direction;

/**
 * Created by Nicolas on 11/10/2016
 *
 * A Robot object only has one attribute: its location on the matrix.
 * But why? We could have stored its goal location, its current direction... And the code would have been easier to write! But we didn't do that.
 * Still wondering why? Well, read what follows:
 *
 * A list of Robot objects will be stored into each Node of the A* search tree.
 * Consequently, to optimise the quantity of memory we use during the search, and to avoid memory saturation problems for very big levels,
 * we need to store as little data as possible into Robot objects.
 *
 * As, in a Node, the only indispensable information concerning a robot is its location, we only need to store this.
 * Other information like goal locations don't need to be stored and duplicated into Node objects.
 */

public class Robot {

    private short currentLocation;

    public Robot(short currentLocation) {
        this.currentLocation = currentLocation;
    }

    public short getCurrentLocation() {
        return currentLocation;
    }

    private void setCurrentLocation(short currentLocation) {
        this.currentLocation = currentLocation;
    }

    public void move(Direction direction) {
        setCurrentLocation((short) (getCurrentLocation() + direction.getDirectionValue()));
    }

    //As a robot only differentiates itself from the others by its location, we simply use it as its hashcode.
    @Override
    public int hashCode() {
        return currentLocation;
    }

    @Override
    public boolean equals(Object o) {
        //Both Objects are equal if they are the same instance
        if (this == o) return true;
        //Both Objects are different if they belong to different classes.
        if (o == null || getClass() != o.getClass()) return false;
        //Both Objects are equal if they are both Robots and if their location is the same.
        Robot robot = (Robot) o;
        return currentLocation == robot.currentLocation;
    }

    @Override
    public String toString() {
        return "Robot{" +
                "pos=" + currentLocation +
                '}';
    }
}
