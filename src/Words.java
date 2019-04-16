import java.lang.reflect.Array;
import java.util.Arrays;

public class Words {

    private Node firstNode = null;

    public int findWord(boolean and,String... words){
        Node x = firstNode;
        int[] found = new int[words.length];
        while(x != null){
            for (int i = 0;i < words.length;i++)
                if (x.word.equals(words[i]))
                    found[i] = x.value;
            x = x.nextNode;
        }
        Arrays.sort(found);
        return (and)?found[0]:found[found.length-1];
    }

    public Words trim(int maxWords){
        Node x = firstNode;
        int i = 0;
        while(x != null) {
            if (i > maxWords)
                x.nextNode = null;
            else
                x = x.nextNode;
        }
        return this;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        Node temp = firstNode;
        while(temp != null){
            sb.append("\n").append(temp.value).append(" : '").append(temp.word).append("'");
            temp = temp.nextNode;
        }
        return sb.toString();
    }

    public void addWord(String word,int value){
        if(firstNode == null){
            firstNode = new Node(word,value);
            return;
        }

        boolean done = false;
        Node x = firstNode;
        while(!done && x != null){
            if(x.word.equals(word)){
                x.value += value;
                done = true;
            }
            else
                x = x.nextNode;
        }
        if(done){
            Node temp;
            if(x.prevNode != null && x.prevNode.value < x.value) {
                x.prevNode.nextNode = x.nextNode;
                if(x.nextNode != null)
                    x.nextNode.prevNode = x.prevNode;
                temp = x.prevNode;
            }else return;
            //traverse backwards until find the right place to insert;
            while(temp != null && temp.value < x.value){
                temp = temp.prevNode;
            }
            //sub the first one
            if(temp == null){
                temp = firstNode;
                temp.prevNode = x;
                x.nextNode = temp;
                x.prevNode = null;
                firstNode = x;
            }
            //insert the node
            else{
                x.nextNode = temp.nextNode;
                x.prevNode = temp;
                temp.nextNode.prevNode = x;
                temp.nextNode = x;
            }
        }
        //new node
        else{
            x = new Node(word,value);
            Node temp = firstNode;
            while(temp.value > x.value){
                //if run out of nodes
                if(temp.nextNode == null){
                    temp.nextNode = x;
                    x.prevNode = temp;
                    return;
                } else{
                    temp = temp.nextNode;
                }
            }
            x.prevNode = temp.prevNode;
            x.nextNode = temp;
            if(temp.prevNode != null)
                temp.prevNode.nextNode = x;
            else
                firstNode = x;
            temp.prevNode = x;
        }
    }

    class Node{
        String word;
        int value;
        Node nextNode = null;
        Node prevNode = null;

        Node(String word,int value){
            this.word = word;
            this.value = value;
        }
    }
}
