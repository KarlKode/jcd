package ch.ethz.jcd.main.visitor;

import ch.ethz.jcd.main.layer.VDirectory;
import ch.ethz.jcd.main.layer.VFile;
import ch.ethz.jcd.main.layer.VType;

public interface VTypeVisitor<R, A>
{
    public R visit(VType type, A arg);

    public R directory(VDirectory vdir, A arg);

    public R file(VFile vfile, A arg);
}
