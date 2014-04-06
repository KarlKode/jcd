package ch.ethz.jcd.main.utils;

import ch.ethz.jcd.main.exceptions.InvalidBlockAddressException;
import ch.ethz.jcd.main.exceptions.InvalidBlockCountException;
import ch.ethz.jcd.main.exceptions.InvalidSizeException;
import ch.ethz.jcd.main.exceptions.VDiskCreationException;
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
    public static long VDISK_BLOCK_COUNT = 256;
    public final File vdiskFile = new File("data/vdisk.vdisk");

    @Before
    public void setUp()
            throws InvalidBlockAddressException, InvalidSizeException, InvalidBlockCountException, VDiskCreationException, IOException
    {
        VDisk.format(vdiskFile, VUtil.BLOCK_SIZE * VDISK_BLOCK_COUNT);
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
            throws FileNotFoundException
    {
        VDisk vDisk = new VDisk(vdiskFile);
        HashMap<String, VObject> list = vDisk.list();

        assertEquals(0, list.size());

        VDirectory home = vDisk.mkdir("home");
        VDirectory phgamper = vDisk.mkdir(home, "phgamper");
        VFile cache = vDisk.touch(phgamper, ".cache");
        VDirectory etc = vDisk.mkdir("etc");
        VFile foo = vDisk.touch("foo.c");

        list = vDisk.list();

        assertEquals(3, list.size());
        assertEquals(home, list.get("home"));
        assertEquals(etc, list.get("etc"));
        assertEquals(foo, list.get("foo.c"));

        list = vDisk.list(phgamper);
        assertEquals(1, list.size());
        assertEquals(cache, list.get(".cache"));
    }

    @Test
    public void testChdir()
            throws FileNotFoundException
    {
        VDisk vDisk = new VDisk(vdiskFile);
        VDirectory root = vDisk.pwdir();
        VDirectory home = vDisk.mkdir("home");
        VDirectory phgamper = vDisk.mkdir(home, "phgamper");

        vDisk.chdir("/home/phgamper/");
        assertEquals(phgamper, vDisk.pwdir());

        vDisk.chdir();
        assertEquals(root, vDisk.pwdir());

        vDisk.chdir("/");
        assertEquals(root, vDisk.pwdir());

        vDisk.chdir("/not/existing/path/");
        assertEquals(root, vDisk.pwdir());
    }

    @Test
    public void testMkdir()
            throws FileNotFoundException
    {
        VDisk vDisk = new VDisk(vdiskFile);
        VDirectory root = vDisk.pwdir();
        VDirectory home = vDisk.mkdir("home");
        VDirectory phgamper = vDisk.mkdir(home, "phgamper");
        VDirectory etc = vDisk.mkdir("etc");

        HashMap<String, VObject> list = vDisk.list();
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
            throws FileNotFoundException
    {
        VDisk vDisk = new VDisk(vdiskFile);
        VDirectory root = vDisk.pwdir();
        VDirectory home = vDisk.mkdir("home");
        VDirectory phgamper = vDisk.mkdir(home, "phgamper");
        VFile cache = vDisk.touch(phgamper, ".cache");
        VFile xorg = vDisk.touch(phgamper, "xorg.conf");
        VFile bar = vDisk.touch(phgamper, "bar.db");
        VFile foo = vDisk.touch("foo.c");

        HashMap<String, VObject> list = vDisk.list();
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
            throws FileNotFoundException
    {
        VDisk vDisk = new VDisk(vdiskFile);
        VDirectory root = vDisk.pwdir();
        VDirectory home = vDisk.mkdir("home");
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
        list = vDisk.list();
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
            throws FileNotFoundException
    {
        VDisk vDisk = new VDisk(vdiskFile);
        VDirectory root = vDisk.pwdir();
        VDirectory home = vDisk.mkdir("home");
        VDirectory phgamper = vDisk.mkdir(home, "phgamper");
        VDirectory bak = vDisk.mkdir("bak");
        VFile cache = vDisk.touch(phgamper, ".cache");
        VFile xorg = vDisk.touch(phgamper, "xorg.conf");
        VFile bar = vDisk.touch(phgamper, "bar.db");

        HashMap<String, VObject> list = vDisk.list(bak);
        assertEquals(0, list.size());

        vDisk.move(home, bak);
        list = vDisk.list(bak);
        assertEquals(1, list.size());
        assertEquals(home, list.get("home"));
        assertEquals(bak, home.getParent());
        list = vDisk.list(phgamper);
        assertEquals(3, list.size());

        vDisk.move(phgamper, "usr");
        list = vDisk.list(home);
        assertEquals(0, list.size());
        assertEquals(root, phgamper.getParent());
        list = vDisk.list();
        assertFalse(list.containsKey("phgamper"));
        assertTrue(list.containsKey("usr"));

        vDisk.move(phgamper, home, "phgamper");
        list = vDisk.list(home);
        assertEquals(1, list.size());
        assertEquals(home, phgamper.getParent());
        list = vDisk.list(home);
        assertTrue(list.containsKey("phgamper"));


        vDisk.move(bar, "bar.txt");
        list = vDisk.list();
        assertEquals(2, list.size());
        assertTrue(list.containsKey("bar.txt"));
        assertFalse(list.containsKey("bar.db"));
        assertEquals(root, bar.getParent());
    }

    @Test
    public void testCopy()
            throws FileNotFoundException
    {
        VDisk vDisk = new VDisk(vdiskFile);
        VDirectory root = vDisk.pwdir();
        VDirectory etc = vDisk.mkdir("etc");
        VDirectory confd = vDisk.mkdir(etc, "conf.d");
        VDirectory initd = vDisk.mkdir(etc, "init.d");
        VFile foo = vDisk.touch("foo.c");
        VFile barsh = vDisk.touch(initd, "bar.sh");
        VFile net = vDisk.touch(initd, "net");

        HashMap<String, VObject> list = vDisk.list(initd);
        int oldsize = list.size();
        assertFalse(list.containsKey("foo.cpp"));
        VFile foocpp = vDisk.copy(foo, initd, "foo.cpp");
        list = vDisk.list(initd);
        assertEquals(oldsize + 1, list.size());
        assertEquals(foocpp, list.get("foo.cpp"));
        list = vDisk.list();
        assertEquals(foo, list.get("foo.c"));


        list = vDisk.list();
        oldsize = list.size();
        assertFalse(list.containsKey("bar"));
        VFile bar = vDisk.copy(barsh, "bar");
        list = vDisk.list();
        assertEquals(oldsize + 1, list.size());
        assertEquals(bar, list.get("bar"));
        list = vDisk.list(initd);
        assertEquals(barsh, list.get("bar.sh"));


        list = vDisk.list(confd);
        oldsize = list.size();
        assertFalse(list.containsKey("net"));
        VFile netcopy = vDisk.copy(net, confd);
        list = vDisk.list(confd);
        assertEquals(oldsize + 1, list.size());
        assertEquals(netcopy, list.get("net"));

        list = vDisk.list();
        oldsize = list.size();
        assertFalse(list.containsKey("home"));
        VDirectory home = vDisk.copy(initd, "home");
        list = vDisk.list();
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
        VDirectory usrcopy = vDisk.copy(home, initd);
        list = vDisk.list(initd);
        assertEquals(oldsize + 1, list.size());
        assertEquals(usrcopy, list.get("usr"));
    }

    @Test
    public void testDelete( )
            throws FileNotFoundException
    {
        VDisk vDisk = new VDisk(vdiskFile);
        VDirectory bin = vDisk.mkdir("bin");

        HashMap<String, VObject> list = vDisk.list();
        assertTrue(list.containsKey("bin"));
        vDisk.delete(bin);
        list = vDisk.list();
        assertFalse(list.containsKey("bin"));

        VDirectory usr = vDisk.mkdir("usr");
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
        assertNull(vDisk.resolve("/usr/src/linux/arch/x86_64/boot/"));
        assertNull(vDisk.resolve("/usr/src/"));
    }

    @Test
    public void testImportFromHost()
            throws FileNotFoundException
    {
        VDisk vDisk = new VDisk(vdiskFile);
        VDirectory bin = vDisk.mkdir("bin");

        HashMap<String, VObject> list = vDisk.list();
        int oldsize = list.size();
        assertFalse(list.containsKey("simons_cat.jpg"));
        VFile cat = vDisk.importFromHost(new File("data/simons_cat.jpg"));
        list = vDisk.list();
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
            throws FileNotFoundException
    {
        VDisk vDisk = new VDisk(vdiskFile);
        VDirectory bin = vDisk.mkdir("bin");
        VFile foo = vDisk.touch(bin, "foo.c");

        File out = new File("data/foo.c");
        out.delete();
        vDisk.exportToHost(foo, out);
        assertTrue(out.exists());
    }

    @Test
    public void testImprtExport()
            throws IOException
    {
        VDisk vDisk = new VDisk(vdiskFile);
        VFile cat = vDisk.importFromHost(new File("data/simons_cat.jpg"));
        File out = new File("data/cat.jpg");
        out.delete();
        out.createNewFile();
        vDisk.exportToHost(cat, out);
        assertTrue(out.exists());

        VFile helloworld = vDisk.importFromHost(new File("data/helloworld.c"));
        File hello = new File("data/hello.c");
        hello.delete();
        hello.createNewFile();
        vDisk.exportToHost(helloworld, hello);
        assertTrue(hello.exists());


        File inFile = new File("data/import_export.test");
        inFile.delete();
        RandomAccessFile raf = new RandomAccessFile(inFile, "rw");
        raf.writeInt(Integer.MAX_VALUE);
        VFile vFile = vDisk.importFromHost(inFile);
        File outFile = new File("data/import_export.test");
        outFile.delete();
        vDisk.exportToHost(vFile, outFile);
        assertTrue(outFile.exists());
        raf = new RandomAccessFile(outFile, "rw");
        assertEquals(Integer.MAX_VALUE, raf.readInt());
    }


    @Test
    public void testResolve()
            throws FileNotFoundException
    {
        VDisk vDisk = new VDisk(vdiskFile);
        VDirectory etc = vDisk.mkdir("etc");
        VDirectory initd = vDisk.mkdir(etc, "init.d");
        assertEquals(initd, vDisk.resolve("/etc/init.d/"));
    }
}
