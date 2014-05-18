package ch.ethz.jcd.server;

import ch.ethz.jcd.main.utils.VDisk;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class VFSServer
{
    private Map<String, String> users = new HashMap<>();
    private Map<String, VDisk> disks = new HashMap<>();

    public VFSServer()
    {
        users.put("marc", "test");
    }

    public boolean login(String username, String password)
    {
        // Validate credentials
        if (!users.containsKey(username) || users.get(username).equals(password))
        {
            return false;
        }
        // Open VDisk file/create new VDisk
        if (!disks.containsKey(username))
        {
            File vdiskFile = new File("/tmp/" + username + ".vdisk");

            // TODO: Create new VDisk if it does not exist yet
            if (!vdiskFile.exists())
            {
                VDisk.format(vdiskFile, 1024 * 1024, false, false);
            }

            try
            {
                disks.put(username, new VDisk(vdiskFile));
            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
}
