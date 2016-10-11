/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend;

import java.util.LinkedHashMap;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author i01frajos445
 */
public class XMLTest {

    XML toTest;
    String rootname = "Roottag";

    public XMLTest() {
        toTest = new XML(rootname);
    }

    /**
     * Test of hasAttributes method, of class XML.
     */
    @Test
    public void testHasAttributes() {
        String attrName = "testAttribute";
        assertFalse("Shouldnt have Attributes", toTest.hasAttributes());
        toTest.addAttribute(attrName, "1");
        assertTrue("Should have a Attribute", toTest.hasAttributes());
        toTest.deleteAttribute(attrName);
        assertFalse("Shouldnt have Attributes", toTest.hasAttributes());
    }

    /**
     * Test of hasContent method, of class XML.
     */
    @Test
    public void testHasContent() {
        assertFalse("Should have no Content", toTest.hasContent());
        toTest.setContent("Testcontent");
        assertTrue("Should have Content", toTest.hasContent());
        toTest.deleteContent();
        assertFalse("Should have no Content", toTest.hasContent());
    }

    /**
     * Test of hasChildren method, of class XML.
     */
    @Test
    public void testHasChildren() {
        assertFalse("Shoudnt have Children", toTest.hasChildren());
        String childname = "testchildren";
        toTest.addChildren(childname);
        assertTrue("Should have children", toTest.hasChildren());
        toTest.deleteChildren(childname);
        assertFalse("Shoudnt have Children", toTest.hasChildren());
    }

    /**
     * Test of hasParent method, of class XML.
     */
    @Test
    public void testHasParent() {
        toTest.addChildren("child");
        XML child = toTest.getFirstChild();
        assertTrue("Should have children", child.hasParent());
        toTest.deleteChild(child);
        assertFalse("Should have no Parents", toTest.hasParent());
        assertFalse("Cannot restore original XML", toTest.hasChildren());
    }

    /**
     * Test of getName method, of class XML.
     */
    @Test
    public void testGetName() {
        assertEquals("Should have to same name", rootname, toTest.getName());
    }

    /**
     * Test of getAttributes method, of class XML.
     */
    @Test
    public void testGetAttributes() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        String test1[] = {"test1", "1"};
        toTest.addAttribute(test1[0], test1[1]);
        map.put(test1[0], test1[1]);
        String test2[] = {"test2", "2"};
        toTest.addAttribute(test2[0], test2[1]);
        map.put(test2[0], test2[1]);
        assertEquals("Should have the same attributes", map, toTest.getAttributes());
        toTest.deleteAttribute(test1[0]);
        toTest.deleteAttributesByValue(test2[1]);
        assertFalse("Cannot restore original XML", toTest.hasAttributes());
    }

    /**
     * Test of getAttribute method, of class XML.
     */
    @Test
    public void testGetAttribute() {
        String attrName = "testattr";
        String attrValue = "1";
        toTest.addAttribute(attrName, attrValue);
        assertEquals("Should have the same Attribute value", attrValue, toTest.getAttribute(attrName));
        toTest.deleteAttribute(attrName);
        assertFalse("cannot restore original XML", toTest.hasAttributes());

    }

    /**
     * Test of getContent method, of class XML.
     */
    @Test
    public void testGetContent() {
        assertEquals("Shouldnt have content", "", toTest.getContent());
        String content = "testcontent";
        toTest.setContent(content);
        assertEquals("Should habe the same content", content, toTest.getContent());
        toTest.deleteContent();
        assertFalse("Cannot restore original XML", toTest.hasContent());
    }

    /**
     * Test of getFirstChild method, of class XML.
     */
    @Test
    public void testGetFirstChild_0args() {
        toTest.addChildren("B");
        toTest.addChildren("A");
        assertEquals("Should be equal", new XML("B").getName(), toTest.getFirstChild().getName());
        toTest.deleteChildren("A");
        toTest.deleteChildren("B");
        assertFalse("Cannot restore original XML", toTest.hasChildren());
    }

    /**
     * Test of getLastChild method, of class XML.
     */
    @Test
    public void testGetLastChild() {//Sorts the children
        toTest.addChildren("B");
        toTest.addChildren("A");
        assertEquals("Should be equal", new XML("B").getName(), toTest.getFirstChild().getName());
        toTest.deleteChildren("A");
        toTest.deleteChildren("B");
        assertFalse("Cannot restore original XML", toTest.hasChildren());
    }

    /**
     * Test of getChildren method, of class XML.
     */
    @Test
    public void testGetChildren() {
        toTest.addChildren("child");
        assertEquals("Should have the same name", toTest.getChildren().get(0).getName(), "child");
        toTest.deleteChildren("child");
        assertFalse("Should have no children", toTest.hasChildren());
    }

    /**
     * Test of getChild method, of class XML.
     */
    @Test
    public void testGetChild() {
        toTest.addChildren("child");
        assertEquals("Should have the same name", toTest.getChild("child").get(0).getName(), "child");
        toTest.deleteChildren("child");
        assertFalse("Should have no children", toTest.hasChildren());
    }

    /**
     * Test of getFirstChild method, of class XML.
     */
    @Test
    public void testGetFirstChild_String() {
        toTest.addChildren("child");
        assertEquals("Should have the same name", toTest.getFirstChild("child").getName(), "child");
        toTest.deleteChildren("child");
        assertFalse("Should have no children", toTest.hasChildren());
    }

    /**
     * Test of getParent method, of class XML.
     */
    @Test
    public void testGetParent() {
        //assertFalse("Shouldnt have a Parent", toTest.getParent());
        
    }

    /**
     * Test of addAttribute method, of class XML.
     */
    @Test
    public void testAddAttribute() {

    }

    /**
     * Test of setContent method, of class XML.
     */
    @Test
    public void testSetContent() {

    }

    /**
     * Test of addChildren method, of class XML.
     */
    @Test
    public void testAddChildren_XMLArr() {
        
    }

    /**
     * Test of addChildren method, of class XML.
     */
    @Test
    public void testAddChildren_StringArr() {
        assertFalse("Should have no children", toTest.hasChildren());
        toTest.addChildren("child");
        assertTrue("Should have a child", toTest.hasChildren());
        toTest.deleteChildren("child");
        assertFalse("Should have no children", toTest.hasChildren());
    }

    /**
     * Test of deleteAttribute method, of class XML.
     */
    @Test
    public void testDeleteAttribute() {
        assertFalse("Shouldnt have no Attributes", toTest.hasAttributes());
        toTest.addAttribute("test", "test");
        toTest.addAttribute("test2", "test");
        assertTrue("Should have Attributes", toTest.hasAttributes());
        toTest.deleteAttribute("test2");
        toTest.deleteAttribute("test");
        assertFalse("Shouldnt have no Attributes", toTest.hasAttributes());
    }

    /**
     * Test of deleteAttributesByValue method, of class XML.
     */
    @Test
    public void testDeleteAttributesByValue() {
        assertFalse("Shouldnt have no Attributes", toTest.hasAttributes());
        toTest.addAttribute("test", "test");
        toTest.addAttribute("test2", "test");
        assertTrue("Should have Attributes", toTest.hasAttributes());
        toTest.deleteAttributesByValue("test");
        assertFalse("Shouldnt have no Attributes", toTest.hasAttributes());
    }

    /**
     * Test of deleteFirstAttributeByValue method, of class XML.
     */
    @Test
    public void testDeleteFirstAttributeByValue() {
        assertFalse("Shouldnt have no Attributes", toTest.hasAttributes());
        toTest.addAttribute("test", "test");
        toTest.addAttribute("test2", "test");
        assertTrue("Should have Attributes", toTest.hasAttributes());
        toTest.deleteFirstAttributeByValue("test");
        assertTrue("Should have Attributes", toTest.hasAttributes());
        toTest.deleteFirstAttributeByValue("test");
        assertFalse("Shouldnt have no Attributes", toTest.hasAttributes());
    }

    /**
     * Test of clearAttributes method, of class XML.
     */
    @Test
    public void testClearAttributes() {
        assertFalse("Shouldnt have no Attributes", toTest.hasAttributes());
        toTest.addAttribute("test", "test");
        toTest.addAttribute("test2", "bla");
        assertTrue("Should have Attributes", toTest.hasAttributes());
        toTest.clearAttributes();
        assertFalse("Shouldnt have no Attributes", toTest.hasAttributes());
    }

    /**
     * Test of clearContent method, of class XML.
     */
    @Test
    public void testClearContent() {
        toTest.setContent("content");
        assertTrue("Should have a Content", toTest.hasContent());
        toTest.clearContent();
        assertFalse("Should have no Content", toTest.hasContent());
    }

    /**
     * Test of deleteChildren method, of class XML.
     */
    @Test
    public void testDeleteChildren() {
        toTest.addChildren("child");
        toTest.addChildren("child");
        assertTrue("Should have Children", toTest.hasChildren());
        toTest.deleteChildren("child");
        assertFalse("Should have no Children", toTest.hasChildren());
    }

    /**
     * Test of deleteChild method, of class XML.
     */
    @Test
    public void testDeleteChild() {
        toTest.addChildren("child");
        assertTrue("Should have Children", toTest.hasChildren());
        XML child = toTest.getFirstChild();
        toTest.deleteChild(child);
        assertFalse("Should have no Children", toTest.hasChildren());
    }

    /**
     * Test of clearChildren method, of class XML.
     */
    @Test
    public void testClearChildren() {
        toTest.addChildren("child1");
        toTest.addChildren("child2");
        assertTrue("Should have Children", toTest.hasChildren());
        toTest.clearChildren();
        assertFalse("Should have no Children", toTest.hasChildren());
    }

    /**
     * Test of delete method, of class XML.
     */
    @Test
    public void testDelete() {
        toTest.addChildren("child");
        assertTrue("Should have Children", toTest.hasChildren());
        toTest.getChild("child").get(0).delete();
        assertFalse("Should have no Children", toTest.hasChildren());
    }

    /**
     * Test of isLastChild method, of class XML.
     */
    @Test
    public void testIsLastChild() {
        assertTrue("Should be the last child", toTest.isLastChild());
    }

    /**
     * Test of toXMLString method, of class XML.
     */
    @Test
    public void testToXMLString_0args() {
        String expected = "<" + rootname + ">demo</" + rootname + ">";
        toTest.setContent("demo");
        assertEquals(expected, toTest.toXMLString());
        toTest.deleteContent();
        assertFalse("Cannot restore original xml", toTest.hasContent());
    }

    /**
     * Test of toXMLString method, of class XML.
     */
    @Test
    public void testToXMLString_boolean() {
        String expected = "<" + rootname + ">demo</" + rootname + ">";
        toTest.setContent("demo");
        assertEquals(expected, toTest.toXMLString(true));
        toTest.deleteContent();
        assertFalse("Cannot restore original xml", toTest.hasContent());
    }

    /**
     * Test of toString method, of class XML.
     */
    @Test
    public void testToString() {
        toTest.addChildren("demo");
        toTest.setContent("content");
        toTest.getChild("demo").get(0).setContent("content2");
        String temp = toTest.toString();
        assertEquals("should be equal", "Name: \"Roottag\" ; Content: \"content\" ; Children: 1 \n"
                + "├─Name: \"demo\" ; Content: \"content2\" ; Parent: \"Roottag\" ", toTest.toString());
        toTest.deleteChildren("demo");
        toTest.deleteContent();

    }

}
