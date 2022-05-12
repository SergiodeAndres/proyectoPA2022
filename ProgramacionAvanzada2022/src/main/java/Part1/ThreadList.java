package Part1;
import java.util.*;
import javax.swing.JTextField;

public class ThreadList {
    private JTextField textField; 
    private ArrayList<String> list; 
    
    //Constructor
    public ThreadList (JTextField paramTextField)
    {
        this.textField = paramTextField; 
        list = new ArrayList<String>(); 
    }
    
    //Add specified string to the list
    public synchronized void push (String name)
    {
        list.add(name);
        print();
    }
    
    //Remove specified string from the list
    public synchronized void pop (String name)
    {
        list.remove(name); 
        print(); 
    }
    
    //Print contents of the list
    public void print()
    {
        String content="";
        for(int i=0; i<list.size(); i++)
        {
            content=content+list.get(i)+" ";
        }
        textField.setText(content);
    }
    
    //Getters and Setters
    public ArrayList<String> getThreadList()
    {
        return list;
    }

    public JTextField getTextField() 
    {
        return textField;
    }

    public void setTextField(JTextField textField) 
    {
        this.textField = textField;
    }

    public void setList(ArrayList<String> list) 
    {
        this.list = list;
    }

}
