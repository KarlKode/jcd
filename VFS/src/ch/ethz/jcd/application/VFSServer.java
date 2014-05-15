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
        new VFSServer(VFSApplicationPreProcessor.prepareDisk(args));
    }

    public VFSServer(VDisk vDisk)
    {
        try
        {
            this.socket = new ServerSocket(2000);

            while(true)
            {
                Socket client = this.socket.accept();
                //System.out.println("Socket accepted");
                new VFSCommandExecutor(vDisk, client);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private class VFSCommandExecutor implements AbstractVFSApplication, Runnable
    {
        private Socket client;
        private final VDisk vDisk;
        private final Queue<AbstractVFSCommand> history = new LinkedList<>();
        private VDirectory current;
        private boolean quit = false;

        private ObjectOutputStream out;
        private ObjectInputStream in;


        public VFSCommandExecutor(VDisk vDisk, Socket client)
        {
            this.client = client;
            this.vDisk = vDisk;

            try
            {
                current = (VDirectory) vDisk.resolve(VDisk.PATH_SEPARATOR);
                in = new ObjectInputStream(client.getInputStream());
                out = new ObjectOutputStream(client.getOutputStream());
            }
            catch (IOException | ResolveException e)
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
                    //System.out.println("waiting for command");
                    AbstractVFSCommand cmd = (AbstractVFSCommand) in.readObject();
                    //System.out.println("command received");
                    history.add(cmd);

                    try
                    {
                        //System.out.println("executing command");
                        cmd.execute(this);
                        //System.out.println("sending acknowledge");
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

            try
            {
                client.close();
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
            this.current = dir;
        }

        @Override
        public void quit()
        {
            quit = true;
        }
    }
}
