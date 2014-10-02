package ttp;

/**
 *
 * @author Zhu Zheng
 */

public class Node {
    //The id of the node.
    private final int id;

    //The position of the node.
    private final double[] position;

    //Node constructor
    public Node(int id, double[] position) {
        super();
        this.id = id;
        this.position = position;
    }

    //Return the id of the node
    public int getID() {
        return id;
    }

    //Return the position of the node
    public double[] getPosition() {
        return position;
    }

}
