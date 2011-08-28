/* Generated By:JJTree: Do not edit this line. ASTConstructorDeclaration.java */

package net.sourceforge.pmd.ast;

public class ASTConstructorDeclaration extends AccessNode {
    public ASTConstructorDeclaration(int id) {
        super(id);
    }

    public ASTConstructorDeclaration(JavaParser p, int id) {
        super(p, id);
    }

    public int getParameterCount() {
        return ((ASTFormalParameters) jjtGetChild(0)).getParameterCount();
    }


    /**
     * Accept the visitor. *
     */
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    private boolean containsComment;

    public boolean containsComment() {
        return this.containsComment;
    }

    public void setContainsComment() {
        this.containsComment = true;
    }

    public void dump(String prefix) {
        System.out.println(collectDumpedModifiers(prefix));
        dumpChildren(prefix);
    }

}
