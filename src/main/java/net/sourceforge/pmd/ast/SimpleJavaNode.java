package net.sourceforge.pmd.ast;

public class SimpleJavaNode extends SimpleNode implements JavaNode {

    public SimpleJavaNode(JavaParser p, int i) {
        super(p, i);
    }

    public SimpleJavaNode(int i) {
        super(i);
    }

    /**
     * Accept the visitor. *
     */
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    /**
     * Accept the visitor. *
     */
    public Object childrenAccept(JavaParserVisitor visitor, Object data) {
        if (children != null) {
            for (int i = 0; i < children.length; ++i) {
                ((JavaNode) children[i]).jjtAccept(visitor, data);
            }
        }
        return data;
    }
    
    /* You can override these two methods in subclasses of SimpleNode to
    customize the way the node appears when the tree is dumped.  If
    your output uses more than one line you should override
    toString(String), otherwise overriding toString() is probably all
    you need to do. 
   
    Changing this method is dangerous, since it is used by the XPathRule
    for evaluating Element Names !!
  */

    public String toString() {
        return JavaParserTreeConstants.jjtNodeName[id];
    }
}
