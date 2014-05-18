package ch.ethz.jcd.application.commands;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class CommandFactory
{
    public static AbstractVFSCommand create(String[] args)
    {
        try
        {
            Class<?> clazz = Class.forName("ch.ethz.jcd.application.commands.VFS" + args[0]);
            Constructor<?> constructor = clazz.getConstructor(String[].class);
            Object[] params = {args};
            return (AbstractVFSCommand) constructor.newInstance(params);
        } catch (ClassNotFoundException | InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e)
        {
            return new VFSNull(args);
        }
    }
}
