package ch.ethz.jcd.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by leo on 10.05.14.
 */
public abstract class AbstractObservableCommand<T> extends Observable{
    private List<Observer> observers = new ArrayList<Observer>();

    public void preExecution(){
        this.notifyObservers();
    }
    public abstract void execute(T parameter);

    public void postExecution(){
        this.notifyObservers();
    }
}
