package ethz.jcd.visitor;

import ethz.jcd.layer.VDirectory;
import ethz.jcd.layer.VFile;
import ethz.jcd.layer.VType;

/**
 * Created by phgamper on 3/6/14.
 */
public interface VTypeVisitor<R, A>
{
    public R visit(VType type, A arg);

    public R directory(VDirectory vdir, A arg);

    public R file(VFile vfile, A arg);
}
