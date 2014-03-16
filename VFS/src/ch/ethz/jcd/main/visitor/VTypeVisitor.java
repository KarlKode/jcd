package ch.ethz.jcd.main.visitor;

import ch.ethz.jcd.main.layer.VDirectory;
import ch.ethz.jcd.main.layer.VFile;
import ch.ethz.jcd.main.layer.VType;

/**
 * This interface is an implementation of the visitor pattern and provides the
 * necessary tools to visit the VType structure.*
 *
 * @param <R> generic return type
 * @param <A> generic argument type
 */
public interface VTypeVisitor<R, A>
{
    /**
     * This mehtod visits the given VType
     *
     * @param type to visit
     * @param arg to pass
     * @return a generic return value
     */
    public R visit(VType type, A arg);

    /**
     * This method is called after accepting the VDirectory.
     *
     * @param vdir being accepted
     * @param arg passed by the visitor
     * @return a generic return value
     */
    public R directory(VDirectory vdir, A arg);

    /**
     * This method is called after accepting the VFile.
     *
     * @param vfile being accepted
     * @param arg passed by the visitor
     * @return a generic return value
     */
    public R file(VFile vfile, A arg);
}
