package ch.ethz.jcd.application;

import ch.ethz.jcd.application.commands.VFSQuit;
import ch.ethz.jcd.main.utils.VDisk;

import java.io.*;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;

public class VFSClient implements Observer
{
    private VFSConsole console;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public static void main(String args[]) throws Exception
    {
        new VFSClient(VFSApplicationPreProcessor.prepareDisk(args));
    }

    public VFSClient(VDisk vDisk)
    {
        console = new VFSConsole(vDisk);
        console.addObserver(this);
        try
        {
            socket = new Socket("localhost", 2000);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Observable o, Object arg)
    {
        try
        {
            // send
            //System.out.println("sending command");
            out.writeObject(arg);
            // receive
            //System.out.println("waiting for acknowledge");
            in.readObject();
            //System.out.println("acknowledge received");

            if(arg instanceof VFSQuit)
            {
                socket.close();
            }
        }
        catch (IOException | ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }
}
