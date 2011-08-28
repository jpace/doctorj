/* Generated By:JJTree: Do not edit this line. ASTVariableDeclaratorId.java */
package net.sourceforge.pmd.ast;

import java.util.List;


public class ASTVariableDeclaratorId extends SimpleJavaNode {

    public ASTVariableDeclaratorId(int id) {
        super(id);
    }

    public ASTVariableDeclaratorId(JavaParser p, int id) {
        super(p, id);
    }

    /**
     * Accept the visitor. *
     */
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    private int arrayDepth;

    public void bumpArrayDepth() {
        arrayDepth++;
    }

    public int getArrayDepth() {
        return arrayDepth;
    }

    public boolean isArray() {
        return arrayDepth > 0;
    }

    public boolean isExceptionBlockParameter() {
        return jjtGetParent().jjtGetParent() instanceof ASTTryStatement;
    }

    public SimpleNode getTypeNameNode() {
        if (jjtGetParent() instanceof ASTFormalParameter) {
            return findTypeNameNode(jjtGetParent());
        } else if (jjtGetParent().jjtGetParent() instanceof ASTLocalVariableDeclaration || jjtGetParent().jjtGetParent() instanceof ASTFieldDeclaration) {
            return findTypeNameNode(jjtGetParent().jjtGetParent());
        }
        throw new RuntimeException("Don't know how to get the type for anything other than ASTLocalVariableDeclaration/ASTFormalParameter/ASTFieldDeclaration");
    }

    public ASTType getTypeNode() {
        if (jjtGetParent() instanceof ASTFormalParameter) {
            return (ASTType) jjtGetParent().jjtGetChild(0);
        } else if (jjtGetParent().jjtGetParent() instanceof ASTLocalVariableDeclaration || jjtGetParent().jjtGetParent() instanceof ASTFieldDeclaration) {
            SimpleNode n = (SimpleNode) jjtGetParent().jjtGetParent();
            return (ASTType) n.getFirstChildOfType(ASTType.class);
        }
        throw new RuntimeException("Don't know how to get the type for anything other than ASTLocalVariableDeclaration/ASTFormalParameter/ASTFieldDeclaration");
    }

    private SimpleNode findTypeNameNode(Node node) {
        if (node.jjtGetChild(0) instanceof ASTAnnotation) {
            ASTType typeNode = (ASTType) node.jjtGetChild(1);
            return (SimpleNode) typeNode.jjtGetChild(0);
        }
        ASTType typeNode = (ASTType) node.jjtGetChild(0);
        return (SimpleNode) typeNode.jjtGetChild(0);
    }

}
