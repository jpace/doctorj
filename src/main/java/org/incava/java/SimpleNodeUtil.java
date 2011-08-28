package org.incava.java;

import java.util.*;
import net.sourceforge.pmd.ast.JavaParserConstants;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;


/**
 * Miscellaneous routines for the SimpleNode.
 */
public class SimpleNodeUtil
{
    /**
     * Returns the token images for the node.
     */
    public static String toString(SimpleNode node)
    {
        Token tk = node.getFirstToken();
        Token last = node.getLastToken();
        String str = tk.image;
        while (tk != last) {
            tk = tk.next;
            str += tk.image;
        }
        return str;
    }

    /**
     * Returns whether the node has any children.
     */
    public static boolean hasChildren(SimpleNode node)
    {
        return node.jjtGetNumChildren() > 0;
    }

    /**
     * Returns the parent node.
     */
    public static SimpleNode getParent(SimpleNode node)
    {
        return (SimpleNode)node.jjtGetParent();
    }

    /**
     * Returns a list of children, both nodes and tokens.
     */
    public static List getChildren(SimpleNode node)
    {
        List list = new ArrayList();
        
        Token t = new Token();
        t.next = node.getFirstToken();
        
        int nChildren = node.jjtGetNumChildren();
        for (int ord = 0; ord < nChildren; ++ord) {
            SimpleNode n = (SimpleNode)node.jjtGetChild(ord);
            while (true) {
                t = t.next;
                if (t == n.getFirstToken()) {
                    break;
                }
                list.add(t);
            }
            list.add(n);
            t = n.getLastToken();
        }

        while (t != node.getLastToken()) {
            t = t.next;
            list.add(t);
        }

        return list;
    }

    /**
     * Returns a list of children, optionally nodes and tokens.
     */
    public static List getChildren(SimpleNode node, boolean getNodes, boolean getTokens) 
    {
        List list = new ArrayList();
        
        Token t = new Token();
        t.next = node.getFirstToken();
        
        int nChildren = node.jjtGetNumChildren();
        for (int ord = 0; ord < nChildren; ++ord) {
            SimpleNode n = (SimpleNode)node.jjtGetChild(ord);
            while (true) {
                t = t.next;
                if (t == n.getFirstToken()) {
                    break;
                }
                if (getTokens) {
                    list.add(t);
                }
            }
            if (getNodes) {
                list.add(n);
            }
            t = n.getLastToken();
        }

        while (t != node.getLastToken()) {
            t = t.next;
            if (getTokens) {
                list.add(t);
            }
        }

        return list;
    }

    public static SimpleNode findChild(SimpleNode parent, Class childType)
    {
        return findChild(parent, childType, 0);
    }

    public static SimpleNode findChild(SimpleNode parent, Class childType, int index)
    {
        if (parent != null) {
            int nChildren = parent.jjtGetNumChildren();
            if (index >= 0 && index < nChildren) {
                int nFound = 0;
                for (int i = 0; i < nChildren; ++i) {
                    SimpleNode child = (SimpleNode)parent.jjtGetChild(i);
                    // tr.Ace.log("considering " + child.getClass() + " as match against " + childType);
                    if (childType == null || child.getClass().equals(childType)) {
                        if (nFound == index) {
                            // tr.Ace.log("got match");
                            return child;
                        }
                        else {
                            ++nFound;
                        }
                    }
                }
            }
            else {
                tr.Ace.stack("WARNING: index " + index + " out of bounds (" + 0 + ", " + nChildren + ")");
            }
            // tr.Ace.log("no match for " + childType);
        }
        return null;
    }

    /**
     * Returns a list of child tokens, non-hierarchically.
     */
    public static List getChildrenSerially(SimpleNode node) 
    {
        return getChildrenSerially(node, null);
    }

    /**
     * Returns a list of child tokens, non-hierarchically.
     */
    public static List getChildrenSerially(SimpleNode node, List list) 
    {
        List children = list;
        if (children == null) {
            children = new ArrayList();
        }
        
        Token t = new Token();
        t.next = node.getFirstToken();
        
        int nChildren = node.jjtGetNumChildren();
        for (int ord = 0; ord < nChildren; ++ord) {
            SimpleNode n = (SimpleNode)node.jjtGetChild(ord);
            while (true) {
                t = t.next;
                if (t == n.getFirstToken()) {
                    break;
                }
                children.add(t);
            }
            getChildrenSerially(n, children);

            t = n.getLastToken();
        }

        while (t != node.getLastToken()) {
            t = t.next;
            children.add(t);
        }

        return children;
    }

    public static SimpleNode[] findChildren(SimpleNode parent, Class childType)
    {
        List kids = new ArrayList();
        int nChildren = parent == null ? 0 : parent.jjtGetNumChildren();
        for (int i = 0; i < nChildren; ++i) {
            SimpleNode child = (SimpleNode)parent.jjtGetChild(i);
            if (childType == null || child.getClass().equals(childType)) {
                kids.add(child);
            }
        }

        if (childType == null) {
            return (SimpleNode[])kids.toArray(new SimpleNode[0]);
        }
        else {
            int size = kids.size();
            SimpleNode[] ary = (SimpleNode[])java.lang.reflect.Array.newInstance(childType, size);
            System.arraycopy(kids.toArray(), 0, ary, 0, size);
            return ary;
        }
    }

    /**
     * Returns all children of the node.
     */
    public static SimpleNode[] findChildren(SimpleNode parent)
    {
        return findChildren(parent, null);
    }

    /**
     * Returns the tokens for a node.
     */
    public static List getTokens(SimpleNode node)
    {
        // tr.Ace.log("node: " + node);

        List tokens = new ArrayList();
        Token tk = new Token();
        tk.next = node.getFirstToken();

        if (tk != null) {
            tokens.add(tk);
            // tr.Ace.log("node.getLastToken(): " + node.getLastToken());
            do {
                tk = tk.next;
                // tr.Ace.log("token: " + tk);
                tokens.add(tk);
            } while (tk != node.getLastToken());
        }
        return tokens;
    }

    public static Token findToken(SimpleNode node, int tokenType)
    {
        List childTokens = getChildren(node, false, true);
        Iterator it = childTokens.iterator();
        while (it.hasNext()) {
            Token tk = (Token)it.next();
            if (tk.kind == tokenType) {
                return tk;
            }
        }
        return null;
    }

    /**
     * Returns whether the node has a matching token, occurring prior to any
     * non-tokens (i.e., before any child nodes).
     */
    public static boolean hasLeadingToken(SimpleNode node, int tokenType)
    {
        return getLeadingToken(node, tokenType) != null;
    }

    /**
     * Returns whether the node has a matching token, occurring prior to any
     * non-tokens (i.e., before any child nodes).
     */
    public static Token getLeadingToken(SimpleNode node, int tokenType)
    {
        if (node.jjtGetNumChildren() > 0) {
            SimpleNode n = (SimpleNode)node.jjtGetChild(0);

            Token t = new Token();
            t.next = node.getFirstToken();
            
            while (true) {
                t = t.next;
                if (t == n.getFirstToken()) {
                    break;
                }
                else if (t.kind == tokenType) {
                    return t;
                }
            }
        }

        return null;
    }

    /**
     * Returns the tokens preceding the first child of the node.
     */
    public static List getLeadingTokens(SimpleNode node)
    {
        List list = new ArrayList();
        
        if (node.jjtGetNumChildren() > 0) {
            SimpleNode n = (SimpleNode)node.jjtGetChild(0);

            Token t = new Token();
            t.next = node.getFirstToken();
            
            while (true) {
                t = t.next;
                if (t == n.getFirstToken()) {
                    break;
                }
                else {
                    list.add(t);
                }
            }
        }

        return list;
    }

    public static void print(SimpleNode node)
    {
        print(node, "");
    }

    public static void print(SimpleNode node, String prefix)
    {
        Token first = node.getFirstToken();
        Token last  = node.getLastToken();

        tr.Ace.log(prefix + "<" + node.toString() + ">" + getLocation(first, last));
    }

    public static void dump(SimpleNode node, String prefix) 
    {
        dump(node, prefix, false);
    }

    public static void dump(SimpleNode node, String prefix, boolean showWhitespace) 
    {
        print(node, prefix);

        List children = getChildren(node);
        Iterator it = children.iterator();
        while (it.hasNext()) {
            Object obj = it.next();
            if (obj instanceof Token) {
                Token tk = (Token)obj;
                
                if (showWhitespace) {
                    Token st = tk.specialToken;
                    if (st != null) {
                        while (st.specialToken != null) {
                            st = st.specialToken;
                        }
                        
                        while (st != null) {
                            String s = st.toString().replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r");
                            tr.Ace.log(prefix + "    s[" + getLocation(st, st) + "] \"" + s + "\"");
                            st = st.next;
                        }
                    }
                }
                
                tr.Ace.log(prefix + "    t" + getLocation(tk, tk) + " \"" + tk + "\" (" + tk.kind + ")");
            }
            else {
                SimpleNode sn = (SimpleNode)obj;
                dump(sn, prefix + "    ", showWhitespace);
            }
        }
    }  

    protected static String getLocation(Token t1, Token t2)
    {
        return "[" + t1.beginLine + ":" + t1.beginColumn + ":" + t2.endLine + ":" + t2.endColumn + "]";
    }

    /**
     * Returns a numeric "level" for the node. Zero is public or abstract, one
     * is protected, two is package, and three is private.
     */
    public static int getLevel(SimpleNode node)
    {
        List tokens = getLeadingTokens(node);
        Iterator tit = tokens.iterator();
        while (tit.hasNext()) {
            Token t = (Token)tit.next();
            switch (t.kind) {
                case JavaParserConstants.PUBLIC:
                    // fallthrough
                case JavaParserConstants.ABSTRACT:
                    return 0;
                case JavaParserConstants.PROTECTED:
                    return 1;
                case JavaParserConstants.PRIVATE:
                    return 3;
            }
        }

        // AKA "package"
        return 2;
    }

}
