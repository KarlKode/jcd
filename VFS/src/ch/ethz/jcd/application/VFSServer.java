package ch.ethz.jcd.application;

import ch.ethz.jcd.application.commands.AbstractVFSCommand;
import ch.ethz.jcd.main.exceptions.InvalidBlockAddressException;
import ch.ethz.jcd.main.exceptions.InvalidBlockCountException;
import ch.ethz.jcd.main.exceptions.InvalidSizeException;
import ch.ethz.jcd.main.exceptions.VDiskCreationException;
import ch.ethz.jcd.main.exceptions.command.CommandException;
import ch.ethz.jcd.main.exceptions.command.ResolveException;
import ch.ethz.jcd.main.layer.VDirectory;
import ch.ethz.jcd.main.utils.VDisk;
import ch.ethz.jcd.main.utils.VUtil;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import java.util.Queue;


public class VFSServer
{
    private ServerSocket socket;

    public static void main(String[] args)
    {
        new VFSServer();
    }

    public VFSServer()
    {
        try
        {
            this.socket = new ServerSocket(2000);

            while(true)
            {
                System.out.println("Waiting for clients to accept");
                Socket client = this.socket.accept();
                System.out.println("Socket accepted");

                ObjectInputStream in = new ObjectInputStream(client.getInputStream());
                ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
                try
                {
                    // load the client's VDisk
                    System.out.println("loading VDisk");
                    VDisk vDisk = VFSApplicationPreProcessor.prepareDisk((String[]) in.readObject());
                    System.out.println("VDisk loaded");
                    // sending ACK
                    System.out.println("Sending ACK");
                    out.writeObject(true);
                    System.out.println("Acknowledge sent");
                    System.out.println("running executor");
                    new VFSCommandExecutor(vDisk, client, in, out);
                }
                catch (ClassNotFoundException e)
                {
                    // send NAK
                    System.out.println("Sending NAK");
                    out.writeObject(false);
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private class VFSCommandExecutor implements AbstractVFSApplication, Runnable
    {
        private final Socket client;
        private final ObjectOutputStream out;
        private final ObjectInputStream in;

        private final VDisk vDisk;
        private final Queue<AbstractVFSCommand> history = new LinkedList<>();

        private VDirectory current;
        private boolean quit = false;

        public VFSCommandExecutor(VDisk vDisk, Socket client, ObjectInputStream in, ObjectOutputStream out)
        {
            this.client = client;
            this.vDisk = vDisk;
            this.in = in;
            this.out = out;

            try
            {
                this.current = (VDirectory) vDisk.resolve(VDisk.PATH_SEPARATOR);
            }
            catch (ResolveException e)
            {
                e.printStackTrace();
            }

            new Thread(this).start();
        }

        @Override
        public void run()
        {
            while(!quit)
            {
                try
                {
                    System.out.println("waiting for command");
                    AbstractVFSCommand cmd = (AbstractVFSCommand) in.readObject();
                    System.out.println("command received");
                    history.add(cmd);

                    try
                    {
                        System.out.println("executing command > " + cmd.toString());
                        cmd.execute(this);
                        System.out.println("sending acknowledge");
                        out.writeObject(null);
                    }
                    catch (CommandException e)
                    {
                        out.writeObject(e);
                    }
                }
                catch (IOException | ClassNotFoundException e)
                {
                    e.printStackTrace();
                }
            }

            System.out.println("received quit, closing socket");

            try
            {
                client.close();
                System.out.println("Connection to Client closed");
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        @Override
        public Queue<AbstractVFSCommand> getHistory()
        {
            return history;
        }

        @Override
        public VDisk getVDisk()
        {
            return vDisk;
        }

        @Override
        public VDirectory getCurrent()
        {
            return current;
        }

        @Override
        public void setCurrent(VDirectory dir)
        {
            current = dir;
        }

        @Override
        public void quit()
        {
            quit = true;
        }
    }
}
