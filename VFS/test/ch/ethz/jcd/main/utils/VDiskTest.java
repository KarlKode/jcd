package ch.ethz.jcd.main.utils;

import ch.ethz.jcd.main.exceptions.*;
import ch.ethz.jcd.main.exceptions.command.ResolveException;
import ch.ethz.jcd.main.layer.VDirectory;
import ch.ethz.jcd.main.layer.VFile;
import ch.ethz.jcd.main.layer.VObject;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

import static org.junit.Assert.*;

public class VDiskTest
{
    public static long VDISK_BLOCK_COUNT = 256000;
    public final File vdiskFile = new File("data/vdisk.vdisk");

    @Before
    public void setUp()
            throws InvalidBlockAddressException, InvalidSizeException, InvalidBlockCountException, VDiskCreationException, IOException
    {
        VDisk.format(vdiskFile, VUtil.BLOCK_SIZE * VDISK_BLOCK_COUNT, false);
    }

    @Test
    public void testDispose()
            throws FileNotFoundException
    {
        VDisk vDisk = new VDisk(vdiskFile);
        vDisk.dispose();
        assertFalse("disposing the VDisk failed", vdiskFile.exists());
    }

    @Test
    public void testList()
            throws Exception
    {
        VDisk vDisk = new VDisk(vdiskFile);
        VDirectory root = (VDirectory) vDisk.resolve("/");
        HashMap<String, VObject> list = vDisk.list(root);

        assertEquals(0, list.size());

        VDirectory home = vDisk.mkdir(root, "home");
        VDirectory phgamper = vDisk.mkdir(home, "phgamper");
        VFile cache = vDisk.touch(phgamper, ".cache");
        VDirectory etc = vDisk.mkdir(root, "etc");
        VFile foo = vDisk.touch(root, "foo.c");

        list = vDisk.list(root);

        assertEquals(3, list.size());
        assertEquals(home, list.get("home"));
        assertEquals(etc, list.get("etc"));
        assertEquals(foo, list.get("foo.c"));

        list = vDisk.list(phgamper);
        assertEquals(1, list.size());
        assertEquals(cache, list.get(".cache"));
    }

    @Test
    public void testMkdir()
            throws Exception
    {
        VDisk vDisk = new VDisk(vdiskFile);
        VDirectory root = (VDirectory) vDisk.resolve("/");
        VDirectory home = vDisk.mkdir(root, "home");
        VDirectory phgamper = vDisk.mkdir(home, "phgamper");
        VDirectory etc = vDisk.mkdir(root, "etc");

        HashMap<String, VObject> list = vDisk.list(root);
        assertEquals(2, list.size());
        assertEquals(home, list.get("home"));
        assertEquals(etc, list.get("etc"));
        assertEquals(root, home.getParent());
        assertEquals(root, etc.getParent());

        list = vDisk.list(home);
        assertEquals(1, list.size());
        assertEquals(phgamper, list.get("phgamper"));
        assertEquals(home, phgamper.getParent());
    }

    @Test
    public void testTouch()
            throws Exception
    {
        VDisk vDisk = new VDisk(vdiskFile);
        VDirectory root = (VDirectory) vDisk.resolve("/");
        VDirectory home = vDisk.mkdir(root, "home");
        VDirectory phgamper = vDisk.mkdir(home, "phgamper");
        VFile cache = vDisk.touch(phgamper, ".cache");
        VFile xorg = vDisk.touch(phgamper, "xorg.conf");
        VFile bar = vDisk.touch(phgamper, "bar.db");
        VFile foo = vDisk.touch(root, "foo.c");

        HashMap<String, VObject> list = vDisk.list(root);
        assertEquals(2, list.size());
        assertEquals(foo, list.get("foo.c"));
        assertEquals(root, foo.getParent());

        list = vDisk.list(phgamper);
        assertEquals(3, list.size());
        assertEquals(cache, list.get(".cache"));
        assertEquals(xorg, list.get("xorg.conf"));
        assertEquals(bar, list.get("bar.db"));
        assertEquals(phgamper, cache.getParent());
        assertEquals(phgamper, xorg.getParent());
        assertEquals(phgamper, bar.getParent());
    }

    @Test
    public void testRename()
            throws Exception
    {
        VDisk vDisk = new VDisk(vdiskFile);
        VDirectory root = (VDirectory) vDisk.resolve("/");
        VDirectory home = vDisk.mkdir(root, "home");
        VDirectory phgamper = vDisk.mkdir(home, "phgamper");
        VFile cache = vDisk.touch(phgamper, ".cache");
        VFile xorg = vDisk.touch(phgamper, "xorg.conf");
        VFile bar = vDisk.touch(phgamper, "bar.db");

        vDisk.rename(bar, "bar.txt");

        HashMap<String, VObject> list = vDisk.list(phgamper);
        assertEquals(3, list.size());
        assertFalse(list.containsKey("bar.db"));
        assertTrue(list.containsKey("bar.txt"));
        assertEquals(phgamper, bar.getParent());

        vDisk.rename(home, "home.bak");
        list = vDisk.list(root);
        assertEquals(1, list.size());
        assertFalse(list.containsKey("home"));
        assertTrue(list.containsKey("home.bak"));
        assertEquals(root, home.getParent());

        list = vDisk.list(phgamper);
        assertEquals(3, list.size());
        assertEquals(cache, list.get(".cache"));
        assertEquals(xorg, list.get("xorg.conf"));
        assertEquals(bar, list.get("bar.db"));
        assertEquals(phgamper, cache.getParent());
        assertEquals(phgamper, xorg.getParent());
        assertEquals(phgamper, bar.getParent());
        assertEquals(home, phgamper.getParent());
    }

    @Test
    public void testMove()
            throws Exception {
        VDisk vDisk = new VDisk(vdiskFile);
        VDirectory root = (VDirectory) vDisk.resolve("/");
        VDirectory home = vDisk.mkdir(root, "home");
        VDirectory phgamper = vDisk.mkdir(home, "phgamper");
        VDirectory bak = vDisk.mkdir(root, "bak");
        VFile cache = vDisk.touch(phgamper, ".cache");
        VFile xorg = vDisk.touch(phgamper, "xorg.conf");
        VFile bar = vDisk.touch(phgamper, "bar.db");

        HashMap<String, VObject> list = vDisk.list(bak);
        assertEquals(0, list.size());

        vDisk.move(home, bak, null);
        list = vDisk.list(bak);
        assertEquals(1, list.size());
        assertEquals(home, list.get("home"));
        assertEquals(bak, home.getParent());
        list = vDisk.list(phgamper);
        assertEquals(3, list.size());

        vDisk.move(phgamper, root, "usr");
        list = vDisk.list(home);
        assertEquals(0, list.size());
        assertEquals(root, phgamper.getParent());
        list = vDisk.list(root);
        assertFalse(list.containsKey("phgamper"));
        assertTrue(list.containsKey("usr"));

        vDisk.move(phgamper, home, "phgamper");
        list = vDisk.list(home);
        assertEquals(1, list.size());
        assertEquals(home, phgamper.getParent());
        list = vDisk.list(home);
        assertTrue(list.containsKey("phgamper"));


        vDisk.move(bar, root, "bar.txt");
        list = vDisk.list(root);
        assertEquals(2, list.size());
        assertTrue(list.containsKey("bar.txt"));
        assertFalse(list.containsKey("bar.db"));
        assertEquals(root, bar.getParent());
    }

    @Test
    public void testCopy()
            throws Exception {
        VDisk vDisk = new VDisk(vdiskFile);
        VDirectory root = (VDirectory) vDisk.resolve("/");
        VDirectory etc = vDisk.mkdir(root, "etc");
        VDirectory confd = vDisk.mkdir(etc, "conf.d");
        VDirectory initd = vDisk.mkdir(etc, "init.d");
        VFile foo = vDisk.touch(root, "foo.c");
        VFile barsh = vDisk.touch(initd, "bar.sh");
        VFile net = vDisk.touch(initd, "net");

        HashMap<String, VObject> list = vDisk.list(initd);
        int oldsize = list.size();
        assertFalse(list.containsKey("foo.cpp"));
        VFile foocpp = vDisk.copy(foo, initd, "foo.cpp");
        list = vDisk.list(initd);
        assertEquals(oldsize + 1, list.size());
        assertEquals(foocpp, list.get("foo.cpp"));
        list = vDisk.list(root);
        assertEquals(foo, list.get("foo.c"));


        list = vDisk.list(root);
        oldsize = list.size();
        assertFalse(list.containsKey("bar"));
        VFile bar = vDisk.copy(barsh, root, "bar");
        list = vDisk.list(root);
        assertEquals(oldsize + 1, list.size());
        assertEquals(bar, list.get("bar"));
        list = vDisk.list(initd);
        assertEquals(barsh, list.get("bar.sh"));


        list = vDisk.list(confd);
        oldsize = list.size();
        assertFalse(list.containsKey("net"));
        VFile netcopy = vDisk.copy(net, confd, null);
        list = vDisk.list(confd);
        assertEquals(oldsize + 1, list.size());
        assertEquals(netcopy, list.get("net"));

        list = vDisk.list(root);
        oldsize = list.size();
        assertFalse(list.containsKey("home"));
        VDirectory home = vDisk.copy(initd, root, "home");
        list = vDisk.list(root);
        assertEquals(oldsize + 1, list.size());
        assertEquals(home, list.get("home"));


        list = vDisk.list(home);
        oldsize = list.size();
        assertFalse(list.containsKey("usr"));
        VDirectory usr = vDisk.copy(etc, home, "usr");
        list = vDisk.list(home);
        assertEquals(oldsize + 1, list.size());
        assertEquals(usr, list.get("usr"));

        list = vDisk.list(initd);
        oldsize = list.size();
        assertFalse(list.containsKey("usr"));
        VDirectory usrcopy = vDisk.copy(home, initd, null);
        list = vDisk.list(initd);
        assertEquals(oldsize + 1, list.size());
        assertEquals(usrcopy, list.get("usr"));
    }

    @Test
    public void testDelete( )
            throws Exception {
        VDisk vDisk = new VDisk(vdiskFile);
        VDirectory root = (VDirectory) vDisk.resolve("/");
        VDirectory bin = vDisk.mkdir(root, "bin");

        HashMap<String, VObject> list = vDisk.list(root);
        assertTrue(list.containsKey("bin"));
        vDisk.delete(bin);
        list = vDisk.list(root);
        assertFalse(list.containsKey("bin"));

        VDirectory usr = vDisk.mkdir(root, "usr");
        VDirectory src = vDisk.mkdir(usr, "src");
        VDirectory linux = vDisk.mkdir(src, "linux");
        VFile config = vDisk.touch(linux, ".config");
        VFile configgz = vDisk.touch(linux, "config.tar.gz");
        VDirectory arch = vDisk.mkdir(linux, "arch");
        VDirectory x86_64 = vDisk.mkdir(arch, "x86_64");
        VDirectory boot = vDisk.mkdir(x86_64, "boot");
        VFile bzimage = vDisk.touch(boot, "bzimage");

        list = vDisk.list(linux);
        assertEquals(3, list.size());
        vDisk.delete(src);
        list = vDisk.list(usr);
        assertTrue(list.isEmpty());
        try
        {
            vDisk.resolve("/usr/src/linux/arch/x86_64/boot/");
            fail("Exception was expected for non existand file/directory.");
        } catch (ResolveException e)
        {
            assertTrue(e.getCause() instanceof NoSuchFileOrDirectoryException);
        }
        try
        {
            assertNull(vDisk.resolve("/usr/src/"));
            fail("Exception was expected for non existand file/directory.");
        } catch (ResolveException e)
        {
            assertTrue(e.getCause() instanceof NoSuchFileOrDirectoryException);
        }
    }

    @Test
    public void testImportFromHost()
            throws Exception
    {
        VDisk vDisk = new VDisk(vdiskFile);
        VDirectory root = (VDirectory) vDisk.resolve("/");
        VDirectory bin = vDisk.mkdir(root, "bin");

        HashMap<String, VObject> list = vDisk.list(root);
        int oldsize = list.size();
        assertFalse(list.containsKey("simons_cat.jpg"));
        VFile cat = vDisk.importFromHost(new File("data/simons_cat.jpg"), root);
        list = vDisk.list(root);
        assertEquals(oldsize + 1, list.size());
        assertTrue(list.containsKey("simons_cat.jpg"));

        list = vDisk.list(bin);
        oldsize = list.size();
        assertFalse(list.containsKey("point.s"));
        VFile point = vDisk.importFromHost(new File("data/point.s"), bin);
        list = vDisk.list(bin);
        assertEquals(oldsize + 1, list.size());
        assertTrue(list.containsKey("point.s"));
    }

    @Test
    public void testExportFromHost()
            throws Exception
    {
        VDisk vDisk = new VDisk(vdiskFile);
        VDirectory root = (VDirectory) vDisk.resolve("/");
        VDirectory bin = vDisk.mkdir(root, "bin");
        VFile foo = vDisk.touch(bin, "foo.c");

        File out = new File("data/foo.c");
        out.delete();
        vDisk.exportToHost(foo, out);
        assertTrue(out.exists());
    }

    @Test
    public void testImprtExport()
            throws Exception
    {
        VDisk vDisk = new VDisk(vdiskFile);
        VDirectory root = (VDirectory) vDisk.resolve("/");
        VFile cat = vDisk.importFromHost(new File("data/simons_cat.jpg"), root);
        File out = new File("data/cat.jpg");
        out.delete();
        out.createNewFile();
        vDisk.exportToHost(cat, out);
        assertTrue(out.exists());

        VFile helloworld = vDisk.importFromHost(new File("data/helloworld.c"), root);
        File hello = new File("data/hello.c");
        hello.delete();
        hello.createNewFile();
        vDisk.exportToHost(helloworld, hello);
        assertTrue(hello.exists());


        File inFile = new File("data/import_export.test");
        inFile.delete();
        RandomAccessFile raf = new RandomAccessFile(inFile, "rw");
        raf.writeInt(Integer.MAX_VALUE);
        VFile vFile = vDisk.importFromHost(inFile, root);
        File outFile = new File("data/import_export.test");
        outFile.delete();
        vDisk.exportToHost(vFile, outFile);
        assertTrue(outFile.exists());
        raf = new RandomAccessFile(outFile, "rw");
        assertEquals(Integer.MAX_VALUE, raf.readInt());
    }


    @Test
    public void testResolve()
            throws Exception
    {
        VDisk vDisk = new VDisk(vdiskFile);
        VDirectory root = (VDirectory) vDisk.resolve("/");
        VDirectory etc = vDisk.mkdir(root, "etc");
        VDirectory initd = vDisk.mkdir(etc, "init.d");
        assertEquals(initd, vDisk.resolve("/etc/init.d/"));
    }
}
