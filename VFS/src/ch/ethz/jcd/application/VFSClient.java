package ch.ethz.jcd.application;

import ch.ethz.jcd.application.commands.VFSquit;
import ch.ethz.jcd.main.exceptions.command.CommandException;
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
        new VFSClient(VFSApplicationPreProcessor.prepareDisk(args), args);
    }

    public VFSClient(VDisk vDisk, String args[])
    {
        try
        {
            // overwrite args[0] for local tests
            args[0] = "data/server.vdisk";

            System.out.println("Connecting to Server");
            socket = new Socket("localhost", 2000);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            // send the args to load the client's VDisk on server
            System.out.println("Sending arguments");
            out.writeObject(args);
            // wait for receiving ACK
            System.out.println("Waiting for acknowledge");
            if (!((Boolean) in.readObject()))
            {
                System.exit(1);
            }
        }
        catch (IOException | ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        console = new VFSConsole(vDisk);
        console.addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg)
    {
        try
        {
            // send
            System.out.println("sending command");
            out.writeObject(arg);
            // receive
            System.out.println("waiting for acknowledge");
            Object ack = in.readObject();
            if(ack instanceof CommandException)
            {
                System.out.print("NAK received > ");
                System.out.println(((CommandException) ack).getCause());

                // TODO undo last operation
            }
            System.out.println("acknowledge received");

            if(arg instanceof VFSquit)
            {
                System.out.println("Closing connection");
                socket.close();
                System.out.println("Quitting the application");
            }
        }
        catch (IOException | ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }
}
