/*
A linked list representation of a queue to hold
 the nodes we traverse in the Puzzle.solve method.
 */

public class Queue {


    Node head, tail;

    public Queue() {
        this.head = null;
        this.tail = null;

    }

    void enqueue(Node node) {
        Node temp = node;

        //queue is empty case
        if (this.tail == null) {
            this.head = temp;
            this.tail = temp;
            return;
        }

        //otherwise make the new thing the tail
        this.tail.next = temp;
        this.tail = temp;
    }

    Node dequeue() {

        //store head temporarily and change the head so that the old head is "removed".
        Node temp = head;
        head = head.next;
        if (head==null){
            tail =null;
        }
        return temp;

    }
}
